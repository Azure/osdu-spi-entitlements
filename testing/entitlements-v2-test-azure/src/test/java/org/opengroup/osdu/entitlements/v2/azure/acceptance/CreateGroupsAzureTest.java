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

package org.opengroup.osdu.entitlements.v2.azure.acceptance;

import org.opengroup.osdu.entitlements.v2.acceptance.CreateGroupTest;
import org.opengroup.osdu.entitlements.v2.acceptance.model.request.RequestData;
import org.opengroup.osdu.entitlements.v2.util.AzureConfigurationService;
import org.opengroup.osdu.entitlements.v2.util.AzureTokenService;

public class CreateGroupsAzureTest extends CreateGroupTest {

    public CreateGroupsAzureTest() {
        super(new AzureConfigurationService(), new AzureTokenService());
    }

    // Upstream cleanup deletes only groupName-<ts>; also remove the data group, which would otherwise
    // keep users.data.root as a member and exhaust app.quota.users.data.root.
    @Override
    protected void cleanup() throws Exception {
        try {
            super.cleanup();
        } finally {
            RequestData requestData = RequestData.builder()
                    .method("DELETE")
                    .relativePath("groups/" + configurationService.getIdOfGroup("data.groupName-" + currentTime))
                    .dataPartitionId(configurationService.getTenantId())
                    .token(tokenService.getToken().getValue())
                    .build();
            httpClientService.send(requestData).close();
        }
    }
}
