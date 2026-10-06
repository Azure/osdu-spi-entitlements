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

package org.opengroup.osdu.entitlements.v2.azure;

import com.azure.security.keyvault.secrets.SecretClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class AzureAppPropertiesTest {

    @InjectMocks
    private AzureAppProperties azureAppProperties;

    @Mock
    private SecretClient secretClient;


    @Test
    public void shouldGetGroupsOfInitialUsers() {
        List<String> groupsOfInitialUsers = azureAppProperties.getGroupsOfInitialUsers();
        assertNotNull(groupsOfInitialUsers);
    }

}