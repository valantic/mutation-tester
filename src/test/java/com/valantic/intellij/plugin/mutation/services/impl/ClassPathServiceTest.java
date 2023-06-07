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
package com.valantic.intellij.plugin.mutation.services.impl;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.OrderRootsEnumerator;
import com.intellij.util.PathsList;
import com.valantic.intellij.plugin.mutation.services.Services;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2023-05-01
 */
@ExtendWith(MockitoExtension.class)
class ClassPathServiceTest {

    private ClassPathService underTest;

    private MockedStatic<Services> servicesMockedStatic;
    private MockedStatic<ModuleRootManager> moduleRootManagerMockedStatic;
    private MockedStatic<ModuleManager> moduleManagerMockedStatic;

    @Mock
    private ProjectService projectService;
    @Mock
    private DependencyService dependencyService;

    @BeforeEach
    void setUp() {
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ProjectService.class)).thenReturn(projectService);
        servicesMockedStatic.when(() -> Services.getService(DependencyService.class)).thenReturn(dependencyService);
        moduleRootManagerMockedStatic = mockStatic(ModuleRootManager.class);
        moduleManagerMockedStatic = mockStatic(ModuleManager.class);
        underTest = Mockito.spy(ClassPathService.class);
    }

    @Test
    void testGetClassPathForModule() {
        final Module module = mock(Module.class);
        final ModuleRootManager moduleRootManager = mock(ModuleRootManager.class);
        final OrderEnumerator orderEnumerator = mock(OrderEnumerator.class);
        final OrderRootsEnumerator orderRootsEnumerator = mock(OrderRootsEnumerator.class);
        final PathsList pathsList = mock(PathsList.class);
        final List<String> entries = Arrays.asList("entry1/classes", "entry2/resources", "entry3/test.jar", "entry4/eclipsebin");

        moduleRootManagerMockedStatic.when(() -> ModuleRootManager.getInstance(module)).thenReturn(moduleRootManager);
        when(moduleRootManager.orderEntries()).thenReturn(orderEnumerator);
        when(orderEnumerator.classes()).thenReturn(orderRootsEnumerator);
        when(orderRootsEnumerator.getPathsList()).thenReturn(pathsList);
        when(pathsList.getPathList()).thenReturn(entries);

        final List<String> result = underTest.getClassPathForModule(module);

        verify(moduleRootManager).orderEntries();
        verify(orderEnumerator).classes();
        verify(orderRootsEnumerator).getPathsList();
        verify(pathsList).getPathList();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.contains("entry1/classes"));
        assertTrue(result.contains("entry2/resources"));
        assertTrue(result.contains("entry3/test.jar"));
        assertTrue(result.contains("entry4/classes"));
    }

    @Test
    void testGetClassPathForModules() {
        final Project project = mock(Project.class);
        final ModuleManager moduleManager = mock(ModuleManager.class);
        final Module module1 = mock(Module.class);
        final Module module2 = mock(Module.class);
        final Module module3 = mock(Module.class);
        final Module[] modules = new Module[]{module1, module2, module3};
        final File file = mock(File.class);

        when(projectService.getCurrentProject()).thenReturn(project);
        moduleManagerMockedStatic.when(() -> ModuleManager.getInstance(project)).thenReturn(moduleManager);
        when(moduleManager.getModules()).thenReturn(modules);
        doReturn(Arrays.asList("external/test.jar", "module1/test.jar", "module1/classes")).when(underTest).getClassPathForModule(module1);
        doReturn(Arrays.asList("module2/resources", "module2/classes")).when(underTest).getClassPathForModule(module2);
        doReturn(Arrays.asList("external/test.jar", "module3/another.jar", "module3/classes")).when(underTest).getClassPathForModule(module3);
        when(dependencyService.getThirdPartyDependency("pitest\\-junit5\\-plugin\\-\\d.*")).thenReturn(file);
        when(file.getAbsolutePath()).thenReturn("pitest-junit5.jar");

        final List<String> result = underTest.getClassPathForModules();

        verify(projectService).getCurrentProject();
        verify(dependencyService).getThirdPartyDependency("pitest\\-junit5\\-plugin\\-\\d.*");
        verify(moduleManager).getModules();
        assertNotNull(result);
        assertEquals(8, result.size());
        assertTrue(result.contains("external/test.jar"));
        assertTrue(result.contains("module1/test.jar"));
        assertTrue(result.contains("module1/classes"));
        assertTrue(result.contains("module2/resources"));
        assertTrue(result.contains("module2/classes"));
        assertTrue(result.contains("module3/another.jar"));
        assertTrue(result.contains("module3/classes"));
        assertTrue(result.contains("pitest-junit5.jar"));
    }

    @AfterEach
    void tearDown() {
        servicesMockedStatic.close();
        moduleRootManagerMockedStatic.close();
        moduleManagerMockedStatic.close();
    }
}