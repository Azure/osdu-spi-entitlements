//  Copyright © Microsoft Corporation
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.opengroup.osdu.entitlements.v2.azure.spi.gremlin.connection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opengroup.osdu.azure.util.AzureServicePrincipal;
import org.opengroup.osdu.entitlements.v2.azure.AzureAppProperties;
import org.opengroup.osdu.entitlements.v2.azure.config.GremlinTaskScheduler;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GremlinConnectionManager}.
 */
@ExtendWith(MockitoExtension.class)
class GremlinConnectionManagerTest {

    private static final String COSMOS_DB_ENDPOINT = "https://cosmos.azure.com/.default";

    @Mock
    private AzureServicePrincipal azureServicePrincipal;

    @Mock
    private AzureAppProperties config;

    @Mock
    private GremlinTaskScheduler gremlinTaskScheduler;

    @Mock
    private ScheduledFuture mockScheduledFuture;

    private GremlinConnectionManager connectionManager;

    @BeforeEach
    void setUp() {
        connectionManager = new GremlinConnectionManager(azureServicePrincipal, config, gremlinTaskScheduler);
    }

    @Test
    void refreshConnections_whenManagedIdentityFails_shouldScheduleRetry() {
        RuntimeException msiFailure = new RuntimeException("MSI token service unavailable");
        when(azureServicePrincipal.getMSIToken(COSMOS_DB_ENDPOINT)).thenThrow(msiFailure);
        when(gremlinTaskScheduler.schedule(any(Runnable.class), any(Instant.class))).thenReturn(mockScheduledFuture);

        connectionManager.refreshConnections();

        verify(azureServicePrincipal).getMSIToken(COSMOS_DB_ENDPOINT);
        verify(gremlinTaskScheduler).schedule(any(Runnable.class), any(Instant.class));
    }
}
