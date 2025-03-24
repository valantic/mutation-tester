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
package com.valantic.ide.plugin.pit.search;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.valantic.ide.plugin.pit.services.Services;
import com.valantic.ide.plugin.pit.services.impl.ProjectService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2022-02-01
 */
@ExtendWith(MockitoExtension.class)
class ProjectJavaFileSearchScopeTest {

    private ProjectJavaFileSearchScope underTest;

    @Mock
    private ProjectService projectService;
    @Mock
    private ProjectFileIndex projectFileIndex;

    private MockedStatic<Services> servicesMockedStatic;

    @BeforeEach
    void setUp() {
        final Project project = mock(Project.class);
        final ProjectRootManager projectRootManager = mock(ProjectRootManager.class);

        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ProjectService.class)).thenReturn(projectService);
        when(projectService.getProjectRootManager(project)).thenReturn(projectRootManager);
        when(projectRootManager.getFileIndex()).thenReturn(projectFileIndex);

        underTest = new ProjectJavaFileSearchScope(project);
    }

    @Test
    void testIsSearchInModuleContent() {
        final Module module = mock(Module.class);

        final boolean result = underTest.isSearchInModuleContent(module);

        assertFalse(result);
    }

    @Test
    void testIsSearchInLibraries() {
        final boolean result = underTest.isSearchInLibraries();

        assertFalse(result);
    }

    @Test
    void testContains_shouldBeTrue() {
        final VirtualFile virtualFile = mock(VirtualFile.class);

        when(projectFileIndex.isInSourceContent(virtualFile)).thenReturn(true);
        final boolean result = underTest.contains(virtualFile);

        assertTrue(result);
    }

    @Test
    void testContains_shouldBeFalse() {
        final VirtualFile virtualFile = mock(VirtualFile.class);
        when(projectFileIndex.isInSourceContent(virtualFile)).thenReturn(false);

        final boolean result = underTest.contains(virtualFile);

        assertFalse(result);
    }

    @AfterEach
    void tearDown() {
        servicesMockedStatic.close();
    }
}
