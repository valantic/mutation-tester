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
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.valantic.intellij.plugin.mutation.search.ProjectJavaFileSearchScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2022-02-01
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {

    private ProjectService underTest;

    private MockedStatic<ProjectManager> projectManagerMockedStatic;
    private MockedStatic<ProjectRootManager> projectRootManagerMockedStatic;
    private MockedStatic<GlobalSearchScope> projectJavaFileSearchScopeMockedStatic;


    @Before
    public void setUp() {
        projectManagerMockedStatic = mockStatic(ProjectManager.class);
        projectRootManagerMockedStatic = mockStatic(ProjectRootManager.class);
        projectJavaFileSearchScopeMockedStatic = mockStatic(GlobalSearchScope.class);
        underTest = new ProjectService();
    }

    @Test
    public void testGetCurrentProject_found() {
        Project project = mock(Project.class);
        ProjectManager projectManager = mock(ProjectManager.class);

        projectManagerMockedStatic.when(() -> ProjectManager.getInstance()).thenReturn(projectManager);
        when(projectManager.getOpenProjects()).thenReturn(new Project[]{project});

        Project result = underTest.getCurrentProject();

        verify(projectManager).getOpenProjects();
        assertNotNull(result);
        assertSame(project, result);
    }

    @Test
    public void testGetCurrentProject_notFound() {
        Project project = mock(Project.class);
        ProjectManager projectManager = mock(ProjectManager.class);

        projectManagerMockedStatic.when(() -> ProjectManager.getInstance()).thenReturn(projectManager);
        when(projectManager.getOpenProjects()).thenReturn(new Project[]{});

        Project result = underTest.getCurrentProject();

        verify(projectManager).getOpenProjects();
        assertNull(result);
    }

    @Test
    public void testGetProjectRootManager() {
        Project project = mock(Project.class);
        ProjectRootManager projectRootManager = mock(ProjectRootManager.class);

        projectRootManagerMockedStatic.when(() -> ProjectRootManager.getInstance(project)).thenReturn(projectRootManager);

        ProjectRootManager result = underTest.getProjectRootManager(project);

        assertSame(projectRootManager, result);
        projectRootManagerMockedStatic.verify(() -> ProjectRootManager.getInstance(project));
    }

    @Test
    public void testGetJavaFileProjectSearchScope() {
        Project project = mock(Project.class);
        GlobalSearchScope globalSearchScope = mock(GlobalSearchScope.class);
        projectJavaFileSearchScopeMockedStatic.when(() -> ProjectJavaFileSearchScope.projectScope(project)).thenReturn(globalSearchScope);

        GlobalSearchScope result = underTest.getJavaFileProjectSearchScope(project);

        assertSame(globalSearchScope, result);
        projectJavaFileSearchScopeMockedStatic.verify(() -> ProjectJavaFileSearchScope.projectScope(project));
    }

    @After
    public void tearDown() {
        projectManagerMockedStatic.close();
        projectRootManagerMockedStatic.close();
        projectJavaFileSearchScopeMockedStatic.close();
    }
}
