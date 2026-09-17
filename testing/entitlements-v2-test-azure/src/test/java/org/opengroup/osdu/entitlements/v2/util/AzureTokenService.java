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

package org.opengroup.osdu.entitlements.v2.util;

import com.google.common.base.Strings;
import org.opengroup.osdu.azure.util.AzureServicePrincipal;
import org.opengroup.osdu.entitlements.v2.acceptance.model.Token;
import org.opengroup.osdu.entitlements.v2.acceptance.util.TokenService;

public class AzureTokenService implements TokenService {
    private static final String CLIENT_ID =
            System.getProperty("INTEGRATION_TESTER", System.getenv("INTEGRATION_TESTER"));
    private static final String CLIENT_SECRET =
            System.getProperty("AZURE_TESTER_SERVICEPRINCIPAL_SECRET",
                    System.getenv("AZURE_TESTER_SERVICEPRINCIPAL_SECRET"));
    private static final String TENANT_ID =
            System.getProperty("AZURE_AD_TENANT_ID", System.getenv("AZURE_AD_TENANT_ID"));
    private static final String APP_RESOURCE_ID =
            System.getProperty("AZURE_AD_APP_RESOURCE_ID", System.getenv("AZURE_AD_APP_RESOURCE_ID"));
    private static String TOKEN;

    /**
     * Returns token of a service principal
     */
    @Override
    public synchronized Token getToken() {
        if (Strings.isNullOrEmpty(TOKEN)) {
            TOKEN = retrieveToken();
        }
        return Token.builder()
                .value(TOKEN)
                .userId(CLIENT_ID)
                .build();
    }

    private String retrieveToken() {
        try {
            return new AzureServicePrincipal().getIdToken(CLIENT_ID, CLIENT_SECRET, TENANT_ID, APP_RESOURCE_ID);
        } catch (Exception e) {
            throw new RuntimeException("Cannot retrieve token", e);
        }
    }
}
