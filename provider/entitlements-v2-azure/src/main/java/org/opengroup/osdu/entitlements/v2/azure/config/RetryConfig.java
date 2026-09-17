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

package org.opengroup.osdu.entitlements.v2.azure.config;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class RetryConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${cache.retry.max}")
    private int maxRetry;

    @Value("${cache.retry.interval}")
    private int retryInterval;

    @Value("${cache.retry.random.factor}")
    private float randomFactor;

    /**
    Retry client with internal of retryInterval +/- random(randomFactor*retryInterval) for maximum maxRetry times.
    This client is used for the concurrent cache rebuild thread which do not acquire the lock to wait and get the rebuilt cache result
     */
    @Bean
    public Retry getRetryClient() {
        return Retry.of(applicationName, io.github.resilience4j.retry.RetryConfig.custom()
                .maxAttempts(maxRetry)
                .intervalFunction(IntervalFunction.ofRandomized(retryInterval, randomFactor))
                .retryOnResult(Objects::isNull)
                .build());
    }
}
