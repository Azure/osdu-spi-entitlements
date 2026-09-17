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

package org.opengroup.osdu.entitlements.v2.azure.service.metrics.hitsnmisses;

public interface HitsNMissesMetricService {
    /**
     * Sends one 'hits' metric to application insights,
     * to send several such metrics, you need to call this method exactly as many times as metrics you expect to send.
     * <p>
     * 'hits' refers to the number of times an action has reached the destination.
     */
    void sendHitsMetric();
    /**
     * Sends one 'misses' metric to application insights,
     * to send several such metrics, you need to call this method exactly as many times as metrics you expect to send.
     * <p>
     * 'misses' refers to the number of times an action has NOT reached the destination.
     */
    void sendMissesMetric();
}