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
import lombok.Getter;
import org.opengroup.osdu.azure.KeyVaultFacade;
import org.opengroup.osdu.entitlements.v2.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class AzureAppProperties extends AppProperties {

    @Autowired
    private SecretClient secretClient;
    @Value("${app.graph.db.port}")
    private int graphDbPort;
    @Value("${app.graph.db.username}")
    private String graphDbUsername;
    @Value("${app.graph.db.sslEnabled}")
    private boolean graphDbSslEnabled;
    @Value("${tenantInfo.container.name}")
    private String tenantInfoContainerName;
    @Value("${azure.cosmosdb.database}")
    private String cosmosDbName;
    @Value("${app.gremlin.token.refresh.buffer.seconds:90}")
    private long gremlinTokenRefreshBufferSeconds;

    public String getGraphDbEndpoint() {
        return KeyVaultFacade.getSecretWithValidation(secretClient, "graph-db-endpoint");
    }

    @Override
    public List<String> getInitialGroups() {
        List<String> initialGroups = new ArrayList<>();
        initialGroups.add("/provisioning/groups/datalake_user_groups.json");
        initialGroups.add("/provisioning/groups/datalake_service_groups.json");
        initialGroups.add("/provisioning/groups/data_groups.json");
        return initialGroups;
    }

    @Override
    public List<String> getGroupsOfInitialUsers() {
        List<String> groupsOfInitialUsers = new ArrayList<>();
        groupsOfInitialUsers.add(getGroupsOfServicePrincipal());
        return groupsOfInitialUsers;
    }

    @Override
    public String getGroupsOfServicePrincipal() {
        return "/provisioning/accounts/groups_of_service_principal.json";
    }

    @Override
    public List<String> getProtectedMembers() {
        List<String> filePaths = new ArrayList<>();
        filePaths.add("/provisioning/groups/data_groups.json");
        filePaths.add("/provisioning/groups/datalake_service_groups.json");
        return filePaths;
    }
}
