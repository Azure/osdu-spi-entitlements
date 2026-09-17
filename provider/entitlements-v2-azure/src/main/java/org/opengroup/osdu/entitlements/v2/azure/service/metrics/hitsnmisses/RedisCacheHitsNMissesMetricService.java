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
import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.stereotype.Service;

@Service
public class RedisCacheHitsNMissesMetricService extends AbstractHitsNMissesMetricService {
    private static final String HITS_METRIC_NAME = "[Entitlements service] Redis cache HITS";
    private static final String MISSES_METRIC_NAME = "[Entitlements service] Redis cache MISSES";

    public RedisCacheHitsNMissesMetricService(TelemetryClient telemetryClient) {
        super(telemetryClient);
    }

    @Override
    protected String hitsMetricName() {
        return HITS_METRIC_NAME;
    }

    @Override
    protected String missesMetricName() {
        return MISSES_METRIC_NAME;
    }
}
