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
import org.opengroup.osdu.entitlements.v2.acceptance.util.ConfigurationService;

/**
 * This class is getting the values from environment variables
 * to be used in integration tests
 */
public class AzureConfigurationService implements ConfigurationService {
    private static String SERVICE_URL;

    @Override
    public String getTenantId() {
        String tenantId = System.getProperty("TENANT_NAME", System.getenv("TENANT_NAME"));
        if (Strings.isNullOrEmpty(tenantId)) {
            tenantId = "opendes";
        }
        return tenantId;
    }

    @Override
    public synchronized String getServiceUrl() {
        if (Strings.isNullOrEmpty(SERVICE_URL)) {
            String serviceUrl = System.getProperty("ENTITLEMENT_V2_URL", System.getenv("ENTITLEMENT_V2_URL"));
            if (serviceUrl == null || serviceUrl.contains("-null")) {
                serviceUrl = "http://localhost:8080/api/entitlements/v2";
            }
            SERVICE_URL = serviceUrl;
        }
        return SERVICE_URL;
    }

    @Override
    public String getDomain() {
        String domain = System.getProperty("DOMAIN", System.getenv("DOMAIN"));
        if (Strings.isNullOrEmpty(domain)) {
            domain = "contoso.com";
        }
        return domain;
    }

    @Override
    public String getIdOfGroup(String groupName) {
        return groupName.toLowerCase() + "@" + getTenantId() + "." + getDomain();
    }

    public String getMemberMailId() {
        String APP_USER_OID1 = System.getProperty("AZURE_AD_VALID_OID_USER1", System.getenv("AZURE_AD_VALID_OID_USER1"));
        if (Strings.isNullOrEmpty(APP_USER_OID1)) {
            return "testMember@test.com";
        }
        return APP_USER_OID1;
    }

    public String getOwnerMailId() {
        String APP_USER_OID2 = System.getProperty("AZURE_AD_VALID_OID_USER2", System.getenv("AZURE_AD_VALID_OID_USER2"));
        if (Strings.isNullOrEmpty(APP_USER_OID2)) {
            return "testmMemberOwner@test.com";
        }
        return APP_USER_OID2;
    }

    public String getMemberMailId_toBeDeleted(long timestamp) {
        String APP_USER_OID1 = System.getProperty("AZURE_AD_VALID_OID_USER1", System.getenv("AZURE_AD_VALID_OID_USER1"));
        if (Strings.isNullOrEmpty(APP_USER_OID1)) {
            return String.format("testMember-%s@test.com", timestamp);
        }
        return APP_USER_OID1;
    }
}
