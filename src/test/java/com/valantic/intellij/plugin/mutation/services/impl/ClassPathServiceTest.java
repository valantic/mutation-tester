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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2023-05-01
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassPathServiceTest {

    private ClassPathService underTest;

    private MockedStatic<Services> servicesMockedStatic;
    private MockedStatic<ModuleRootManager> moduleRootManagerMockedStatic;
    private MockedStatic<ModuleManager> moduleManagerMockedStatic;

    @Mock
    private ProjectService projectService;

    @Before
    public void setUp() {
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ProjectService.class)).thenReturn(projectService);
        moduleRootManagerMockedStatic = mockStatic(ModuleRootManager.class);
        moduleManagerMockedStatic = mockStatic(ModuleManager.class);
        underTest = Mockito.spy(ClassPathService.class);
    }

    @Test
    public void testGetClassPathForModule() {
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
        assertEquals("entry1/classes", result.get(0));
        assertEquals("entry2/resources", result.get(1));
        assertEquals("entry3/test.jar", result.get(2));
        assertEquals("entry4/classes", result.get(3));
    }

    @Test
    public void testGetClassPathForModules() {
        final Project project = mock(Project.class);
        final ModuleManager moduleManager = mock(ModuleManager.class);
        final Module module1 = mock(Module.class);
        final Module module2 = mock(Module.class);
        final Module module3 = mock(Module.class);
        final Module[] modules = new Module[]{module1, module2, module3};

        when(projectService.getCurrentProject()).thenReturn(project);
        moduleManagerMockedStatic.when(() -> ModuleManager.getInstance(project)).thenReturn(moduleManager);
        when(moduleManager.getModules()).thenReturn(modules);
        doReturn(Arrays.asList("external/test.jar", "module1/test.jar", "module1/classes")).when(underTest).getClassPathForModule(module1);
        doReturn(Arrays.asList("module2/resources", "module2/classes")).when(underTest).getClassPathForModule(module2);
        doReturn(Arrays.asList("external/test.jar", "module3/another.jar", "module3/classes")).when(underTest).getClassPathForModule(module3);

        final List<String> result = underTest.getClassPathForModules();

        verify(projectService).getCurrentProject();
        verify(moduleManager).getModules();
        assertNotNull(result);
        assertEquals(7, result.size());
        assertEquals("external/test.jar", result.get(0));
        assertEquals("module1/test.jar", result.get(1));
        assertEquals("module1/classes", result.get(2));
        assertEquals("module2/resources", result.get(3));
        assertEquals("module2/classes", result.get(4));
        assertEquals("module3/another.jar", result.get(5));
        assertEquals("module3/classes", result.get(6));
    }

    @After
    public void tearDown() {
        servicesMockedStatic.close();
        moduleRootManagerMockedStatic.close();
        moduleManagerMockedStatic.close();
    }
}