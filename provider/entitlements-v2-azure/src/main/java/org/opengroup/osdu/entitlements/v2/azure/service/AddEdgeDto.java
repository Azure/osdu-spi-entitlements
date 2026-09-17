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

package org.opengroup.osdu.entitlements.v2.azure.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class AddEdgeDto {
    private String fromNodeId;
    private String toNodeId;
    /**
     * data partition id of a node, from where the edge goes
     */
    private String dpOfFromNodeId;
    /**
     * data partition id of a node, to where the edge goes
     */
    private String dpOfToNodeId;
    private String edgeLabel;
    private Map<String, String> edgeProperties;
}
