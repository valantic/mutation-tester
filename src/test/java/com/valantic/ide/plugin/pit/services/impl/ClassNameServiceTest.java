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
package com.valantic.ide.plugin.pit.services.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.impl.search.AllClassesSearchExecutor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * created by fabian.huesig on 2022-02-01
 */
@ExtendWith(MockitoExtension.class)
class ClassNameServiceTest {

    private ClassNameService underTest;

    private MockedStatic<AllClassesSearchExecutor> allClassesSearchExecutorMockedStatic;

    @BeforeEach
    void setUp() {
        allClassesSearchExecutorMockedStatic = mockStatic(AllClassesSearchExecutor.class);
        underTest = new ClassNameService();
    }

    @Test
    void testProcessClassName_shouldBeTrue() {
        final Project project = mock(Project.class);
        final GlobalSearchScope globalSearchScope = mock(GlobalSearchScope.class);
        final Processor<String> processor = mock(Processor.class);

        allClassesSearchExecutorMockedStatic.when(() -> AllClassesSearchExecutor.processClassNames(project, globalSearchScope, processor)).thenReturn(true);

        final boolean result = underTest.processClassNames(project, globalSearchScope, processor);

        allClassesSearchExecutorMockedStatic.verify(() -> AllClassesSearchExecutor.processClassNames(project, globalSearchScope, processor));
        assertTrue(result);
    }

    @Test
    void testProcessClassName_shouldBeFalse() {
        final Project project = mock(Project.class);
        final GlobalSearchScope globalSearchScope = mock(GlobalSearchScope.class);
        final Processor<String> processor = mock(Processor.class);

        allClassesSearchExecutorMockedStatic.when(() -> AllClassesSearchExecutor.processClassNames(project, globalSearchScope, processor)).thenReturn(false);

        final boolean result = underTest.processClassNames(project, globalSearchScope, processor);

        allClassesSearchExecutorMockedStatic.verify(() -> AllClassesSearchExecutor.processClassNames(project, globalSearchScope, processor));
        assertFalse(result);
    }

    @AfterEach
    void tearDown() {
        allClassesSearchExecutorMockedStatic.close();
    }
}
