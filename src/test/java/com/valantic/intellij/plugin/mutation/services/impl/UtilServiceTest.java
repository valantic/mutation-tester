/*
 * Copyright [2022] [valantic CEC Schweiz AG]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Written by Fabian Hüsig <fabian.huesig@cec.valantic.com>, February, 2022
 */
package com.valantic.intellij.plugin.mutation.services.impl;

import com.intellij.execution.ExecutionBundle;
import com.intellij.util.SlowOperations;
import com.intellij.util.ThrowableRunnable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * created by fabian.huesig on 2022-02-01
 */
@RunWith(MockitoJUnitRunner.class)
public class UtilServiceTest {

    @InjectMocks
    private UtilService underTest;

    private MockedStatic<SlowOperations> slowOperationsMockedStatic;
    private MockedStatic<ExecutionBundle> executionBundleMockedStatic;

    @Before
    public void setUp() {
        slowOperationsMockedStatic = Mockito.mockStatic(SlowOperations.class);
        executionBundleMockedStatic = Mockito.mockStatic(ExecutionBundle.class);
    }

    @Test
    public void testAllowSlowOperations() {
        final ThrowableRunnable throwableRunnable = mock(ThrowableRunnable.class);
        slowOperationsMockedStatic.when(() -> SlowOperations.allowSlowOperations(throwableRunnable)).thenAnswer((Answer<Void>) invocation -> null);

        underTest.allowSlowOperations(throwableRunnable);

        slowOperationsMockedStatic.verify(() -> SlowOperations.allowSlowOperations(throwableRunnable));
    }

    @Test
    public void testExecutionMessage() {
        final String messageKey = "messageKey";

        executionBundleMockedStatic.when(() -> ExecutionBundle.message(messageKey)).thenReturn("messageValue");

        final String result = underTest.executionMessage(messageKey);

        executionBundleMockedStatic.verify(() -> ExecutionBundle.message(messageKey));
        assertEquals("messageValue", result);
    }


    @After
    public void tearDown() {
        slowOperationsMockedStatic.close();
        executionBundleMockedStatic.close();
    }

}
