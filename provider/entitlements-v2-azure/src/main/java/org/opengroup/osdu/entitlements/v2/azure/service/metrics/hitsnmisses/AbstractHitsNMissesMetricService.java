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
import com.microsoft.applicationinsights.telemetry.MetricTelemetry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractHitsNMissesMetricService implements HitsNMissesMetricService {
    private static final int DEFAULT_METRIC_VALUE = 1;
    private final TelemetryClient telemetryClient;

    /**
     * This value will be used to send the 'hits' metric to application insights.
     * Based on this name, it will be possible to filter metrics in the Metrics Explorer.
     *
     * @return The name of the 'hits' metric.
     */
    protected abstract String hitsMetricName();

    /**
     * This value will be used to send the 'misses' metric to application insights.
     * Based on this name, it will be possible to filter metrics in the Metrics Explorer.
     *
     * @return The name of the 'misses' metric.
     */
    protected abstract String missesMetricName();

    @Override
    public void sendHitsMetric() {
        sendMetric(hitsMetricName());
    }

    @Override
    public void sendMissesMetric() {
        sendMetric(missesMetricName());
    }

    private void sendMetric(String name) {
        MetricTelemetry metric = new MetricTelemetry();
        metric.setName(name);
        metric.setValue(DEFAULT_METRIC_VALUE);
        telemetryClient.trackMetric(metric);
        telemetryClient.flush();
    }
}