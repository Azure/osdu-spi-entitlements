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

package org.opengroup.osdu.entitlements.v2.azure.filters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.slf4j.MDC;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class AppIdFilterTest {

    @InjectMocks
    private AppIdFilter appIdFilter;

    @Mock
    private DpsHeaders dpsHeaders;

    @Test
    public void shouldPopulateAppIdSuccessfully() throws IOException, ServletException {

        try (MockedStatic<MDC> mock = Mockito.mockStatic(MDC.class)) {
            mock.when(() -> MDC.put("x-app-id", "x-app-id-value")).thenAnswer(Answers.RETURNS_DEFAULTS);

            HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
            HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
            FilterChain filterChain = Mockito.mock(FilterChain.class);
            Mockito.when(dpsHeaders.getAppId()).thenReturn("x-app-id-value");
            appIdFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

            mock.verify(() -> MDC.put("x-app-id", "x-app-id-value"));
        }
    }
}
