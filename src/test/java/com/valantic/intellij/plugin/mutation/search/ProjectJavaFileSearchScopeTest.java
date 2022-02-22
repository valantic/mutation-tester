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
package com.valantic.intellij.plugin.mutation.search;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.valantic.intellij.plugin.mutation.services.Services;
import com.valantic.intellij.plugin.mutation.services.impl.ProjectService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2022-02-01
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectJavaFileSearchScopeTest {

    private ProjectJavaFileSearchScope underTest;

    @Mock
    private ProjectService projectService;
    @Mock
    private ProjectFileIndex projectFileIndex;

    private MockedStatic<Services> servicesMockedStatic;

    @Before
    public void setUp() {
        final Project project = mock(Project.class);
        final ProjectRootManager projectRootManager = mock(ProjectRootManager.class);

        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ProjectService.class)).thenReturn(projectService);
        when(projectService.getProjectRootManager(project)).thenReturn(projectRootManager);
        when(projectRootManager.getFileIndex()).thenReturn(projectFileIndex);

        underTest = new ProjectJavaFileSearchScope(project);
    }

    @Test
    public void testIsSearchInModuleContent() {
        final Module module = mock(Module.class);

        boolean result = underTest.isSearchInModuleContent(module);

        assertFalse(result);
    }

    @Test
    public void testIsSearchInLibraries() {
        boolean result = underTest.isSearchInLibraries();

        assertFalse(result);
    }

    @Test
    public void testContains_shouldBeTrue() {
        final VirtualFile virtualFile = mock(VirtualFile.class);

        when(projectFileIndex.isInSourceContent(virtualFile)).thenReturn(true);
        boolean result = underTest.contains(virtualFile);

        assertTrue(result);
    }

    @Test
    public void testContains_shouldBeFalse() {
        final VirtualFile virtualFile = mock(VirtualFile.class);

        when(projectFileIndex.isInSourceContent(virtualFile)).thenReturn(false);
        boolean result = underTest.contains(virtualFile);

        assertFalse(result);
    }

    @After
    public void tearDown() {
        servicesMockedStatic.close();
    }
}
