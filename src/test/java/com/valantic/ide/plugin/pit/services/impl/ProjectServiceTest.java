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

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import org.jetbrains.concurrency.Promise;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2022-02-01
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private ProjectService underTest;

    private MockedStatic<ProjectManager> projectManagerMockedStatic;
    private MockedStatic<ProjectRootManager> projectRootManagerMockedStatic;
    private MockedStatic<ProjectScope> projectScopeMockedStatic;
    private MockedStatic<ReadAction> readActionMockedStatic;
    private MockedStatic<DataManager> dataManagerMockedStatic;
    private MockedStatic<CommonDataKeys> commonDataKeysMockedStatic;


    @BeforeEach
    void setUp() {
        projectManagerMockedStatic = mockStatic(ProjectManager.class);
        projectRootManagerMockedStatic = mockStatic(ProjectRootManager.class);
        projectScopeMockedStatic = mockStatic(ProjectScope.class);
        readActionMockedStatic = mockStatic(ReadAction.class);
        dataManagerMockedStatic = mockStatic(DataManager.class);
        commonDataKeysMockedStatic = mockStatic(CommonDataKeys.class);
        underTest = spy(new ProjectService());
    }

    @Test
    void testGetCurrentProject_default() {
        final Project project = mock(Project.class);
        final ProjectManager projectManager = mock(ProjectManager.class);

        readActionMockedStatic.when(() -> ReadAction.run(any())).thenThrow(new Exception("random exception"));
        projectManagerMockedStatic.when(() -> ProjectManager.getInstance()).thenReturn(projectManager);
        when(projectManager.getOpenProjects()).thenReturn(new Project[]{project});

        final Project result = underTest.getCurrentProject();

        verify(projectManager).getOpenProjects();
        assertNotNull(result);
        assertSame(project, result);
    }

    @Test
    void testGetCurrentProject_foundInDataContext() throws ExecutionException, TimeoutException {
        final Project project = mock(Project.class);
        final DataManager dataManager = mock(DataManager.class);
        final Promise<DataContext> promise = mock(Promise.class);
        final DataContext dataContext = mock(DataContext.class);
        final DataContext[] resultContext = new DataContext[1];

        dataManagerMockedStatic.when(() -> DataManager.getInstance()).thenReturn(dataManager);
        when(dataManager.getDataContextFromFocusAsync()).thenReturn(promise);
        when(promise.blockingGet(5, TimeUnit.SECONDS)).thenReturn(dataContext);
        readActionMockedStatic.when(() -> ReadAction.run(any())).thenAnswer(invocation -> {
            resultContext[0] = DataManager.getInstance().getDataContextFromFocusAsync().blockingGet(5, TimeUnit.SECONDS);
            return null;
        });
        commonDataKeysMockedStatic.when(() -> CommonDataKeys.PROJECT.getData(dataContext)).thenReturn(project);
        doReturn(resultContext).when(underTest).createDataContext();

        final Project result = underTest.getCurrentProject();

        assertNotNull(result);
        assertSame(project, result);
    }

    @Test
    void testGetCurrentProject_notFound() {
        final ProjectManager projectManager = mock(ProjectManager.class);

        projectManagerMockedStatic.when(() -> ProjectManager.getInstance()).thenReturn(projectManager);
        when(projectManager.getOpenProjects()).thenReturn(new Project[]{});

        assertThrows(IndexOutOfBoundsException.class, () -> underTest.getCurrentProject());
    }

    @Test
    void testGetProjectRootManager() {
        final Project project = mock(Project.class);
        final ProjectRootManager projectRootManager = mock(ProjectRootManager.class);

        projectRootManagerMockedStatic.when(() -> ProjectRootManager.getInstance(project)).thenReturn(projectRootManager);

        final ProjectRootManager result = underTest.getProjectRootManager(project);

        assertSame(projectRootManager, result);
        projectRootManagerMockedStatic.verify(() -> ProjectRootManager.getInstance(project));
    }

    @Test
    void testGetJavaFileProjectSearchScope() {
        final Project project = mock(Project.class);
        final GlobalSearchScope globalSearchScope = mock(GlobalSearchScope.class);
        projectScopeMockedStatic.when(() -> ProjectScope.getProjectScope(project)).thenReturn(globalSearchScope);

        final GlobalSearchScope result = underTest.getSearchScope(project);

        assertSame(globalSearchScope, result);
        projectScopeMockedStatic.verify(() -> ProjectScope.getProjectScope(project));
    }

    @Test
    void testCreateDataContext() {
        final DataContext[] result = underTest.createDataContext();
        assertNotNull(result);
        assertSame(1, result.length);
        assertNull(result[0]);
    }

    @AfterEach
    void tearDown() {
        projectManagerMockedStatic.close();
        projectRootManagerMockedStatic.close();
        projectScopeMockedStatic.close();
        readActionMockedStatic.close();
        dataManagerMockedStatic.close();
        commonDataKeysMockedStatic.close();
    }
}
