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
import com.intellij.psi.impl.search.AllClassesSearchExecutor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * created by fabian.huesig on 2022-02-01
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassNameServiceTest {

    private ClassNameService underTest;

    private MockedStatic<AllClassesSearchExecutor> allClassesSearchExecutorMockedStatic;

    @Before
    public void setUp() {
        allClassesSearchExecutorMockedStatic = mockStatic(AllClassesSearchExecutor.class);
        underTest = new ClassNameService();
    }

    @Test
    public void testProcessClassName_shouldBeTrue() {
        final Project project = mock(Project.class);
        final GlobalSearchScope globalSearchScope = mock(GlobalSearchScope.class);
        final Processor<String> processor = mock(Processor.class);

        allClassesSearchExecutorMockedStatic.when(() -> AllClassesSearchExecutor.processClassNames(project, globalSearchScope, processor)).thenReturn(true);

        boolean result = underTest.processClassNames(project, globalSearchScope, processor);

        allClassesSearchExecutorMockedStatic.verify(() -> AllClassesSearchExecutor.processClassNames(project, globalSearchScope, processor));
        assertTrue(result);
    }

    @Test
    public void testProcessClassName_shouldBeFalse() {
        final Project project = mock(Project.class);
        final GlobalSearchScope globalSearchScope = mock(GlobalSearchScope.class);
        final Processor<String> processor = mock(Processor.class);

        allClassesSearchExecutorMockedStatic.when(() -> AllClassesSearchExecutor.processClassNames(project, globalSearchScope, processor)).thenReturn(false);

        boolean result = underTest.processClassNames(project, globalSearchScope, processor);

        allClassesSearchExecutorMockedStatic.verify(() -> AllClassesSearchExecutor.processClassNames(project, globalSearchScope, processor));
        assertFalse(result);
    }

    @After
    public void tearDown() {
        allClassesSearchExecutorMockedStatic.close();
    }
}
