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
 * Written by Fabian Hüsig, February, 2022
 */
package com.valantic.intellij.plugin.mutation.linemarker;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;
import com.valantic.intellij.plugin.mutation.action.MutationAction;
import com.valantic.intellij.plugin.mutation.icons.Icons;
import com.valantic.intellij.plugin.mutation.services.Services;
import com.valantic.intellij.plugin.mutation.services.impl.MessageService;
import com.valantic.intellij.plugin.mutation.services.impl.PsiService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MutationRunLineMarkerContributorTest {

    private MutationRunLineMarkerContributor underTest;

    @Mock
    private PsiService psiService;
    @Mock
    private MessageService messageService;

    private MockedStatic<Services> servicesMockedStatic;
    private MockedStatic<MutationAction> mutationActionMockedStatic;

    @BeforeEach
    void setUp() throws Exception {
        servicesMockedStatic = mockStatic(Services.class);
        mutationActionMockedStatic = mockStatic(MutationAction.class);
        servicesMockedStatic.when(() -> Services.getService(PsiService.class)).thenReturn(psiService);
        servicesMockedStatic.when(() -> Services.getService(MessageService.class)).thenReturn(messageService);
        underTest = spy(new MutationRunLineMarkerContributor());
    }


    @Test
    void testGetInfo() {
        final PsiIdentifier psiIdentifier = mock(PsiIdentifier.class);
        final PsiClass psiClass = mock(PsiClass.class);
        final MutationAction[] mutationActions = new MutationAction[]{mock(MutationAction.class)};

        when(psiIdentifier.getParent()).thenReturn(psiClass);
        when(psiService.isTestClass(psiClass)).thenReturn(true);
        when(psiService.determineTargetTest(psiClass)).thenReturn("targetTest");
        when(psiService.determineTargetClass("targetTest", psiClass)).thenReturn("targetClass");
        when(messageService.executionMessage("run.text")).thenReturn("Run Text");
        mutationActionMockedStatic.when(() -> MutationAction.getSingletonActions("targetClass", "targetTest")).thenReturn(mutationActions);

        final RunLineMarkerContributor.Info result = underTest.getInfo(psiIdentifier);

        assertNotNull(result);
        assertNotNull(result.tooltipProvider);
        assertEquals(Icons.MUTATIONx12, result.icon);
        assertArrayEquals(mutationActions, result.actions);
        assertEquals("Run Text", result.tooltipProvider.apply(null));
        verify(psiService).determineTargetTest(psiClass);
        verify(psiService).determineTargetClass("targetTest", psiClass);
        verify(messageService).executionMessage("run.text");
        mutationActionMockedStatic.verify(() -> MutationAction.getSingletonActions("targetClass", "targetTest"));

        when(psiService.determineTargetTest(psiClass)).thenReturn("TestTarget");
        when(psiService.determineTargetClass("TestTarget", psiClass)).thenReturn("TargetClass");
        when(messageService.executionMessage("run.text")).thenReturn("Run Text");
        mutationActionMockedStatic.when(() -> MutationAction.getSingletonActions("TargetClass", "TestTarget")).thenReturn(mutationActions);

        final RunLineMarkerContributor.Info result2 = underTest.getInfo(psiIdentifier);

        assertNotNull(result2);
        assertNotNull(result2.tooltipProvider);
        assertEquals(Icons.MUTATIONx12, result2.icon);
        assertArrayEquals(mutationActions, result2.actions);
        assertEquals("Run Text", result2.tooltipProvider.apply(null));
        verify(psiService, Mockito.times(2)).determineTargetTest(psiClass);
        verify(psiService).determineTargetClass("TestTarget", psiClass);
        verify(messageService, Mockito.times(2)).executionMessage("run.text");
        mutationActionMockedStatic.verify(() -> MutationAction.getSingletonActions("TargetClass", "TestTarget"));
    }

    @AfterEach
    void tearDown() throws Exception {
        servicesMockedStatic.close();
        mutationActionMockedStatic.close();
    }
}
