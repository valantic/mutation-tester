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

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.valantic.intellij.plugin.mutation.services.Services;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2022-02-01
 */
@RunWith(MockitoJUnitRunner.class)
public class PsiServiceTest {

    private PsiService underTest;

    @Mock
    private ClassNameService classNameService;
    @Mock
    private ModuleService moduleService;
    @Mock
    private UtilService utilService;
    @Mock
    private ProjectService projectService;

    private MockedStatic<Services> servicesMockedStatic;

    @Before
    public void setUp() {
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ClassNameService.class)).thenReturn(classNameService);
        servicesMockedStatic.when(() -> Services.getService(ModuleService.class)).thenReturn(moduleService);
        servicesMockedStatic.when(() -> Services.getService(UtilService.class)).thenReturn(utilService);
        servicesMockedStatic.when(() -> Services.getService(ProjectService.class)).thenReturn(projectService);
        underTest = new PsiService();
    }


    @Test
    public void testDoesClassExists() {
        final String fullyQualifiedClassName = "fullyQualifiedClassName";
        final Project project = mock(Project.class);
        final GlobalSearchScope searchScope = mock(GlobalSearchScope.class);
        final ArgumentCaptor<Processor<String>> processorArgumentCaptor = ArgumentCaptor.forClass(Processor.class);

        when(projectService.getCurrentProject()).thenReturn(project);
        when(projectService.getJavaFileProjectSearchScope(project)).thenReturn(searchScope);

        underTest.doesClassExists(fullyQualifiedClassName);

        verify(classNameService).processClassNames(eq(project), eq(searchScope), processorArgumentCaptor.capture());
        assertFalse(processorArgumentCaptor.getValue().process("fullyQualifiedClassName"));
        assertTrue(processorArgumentCaptor.getValue().process("anyOtherGivenName"));
    }

    @After
    public void tearDown() {
        servicesMockedStatic.close();
    }

}
