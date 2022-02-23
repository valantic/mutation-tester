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

import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.valantic.intellij.plugin.mutation.configuration.MutationConfiguration;
import com.valantic.intellij.plugin.mutation.services.Services;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2022-02-01
 */
@RunWith(MockitoJUnitRunner.class)
public class ModuleServiceTest {

    private ModuleService underTest;

    @Mock
    private ProjectService projectService;

    private MockedStatic<Services> servicesMockedStatic;
    private MockedStatic<ModuleUtilCore> moduleUtilMockedStatic;
    private MockedStatic<ModuleManager> moduleManagerMockedStatic;

    @Before
    public void setUp() {
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ProjectService.class)).thenReturn(projectService);
        moduleUtilMockedStatic = mockStatic(ModuleUtilCore.class);
        moduleManagerMockedStatic = mockStatic(ModuleManager.class);
        underTest = new ModuleService();
    }

    @Test
    public void testGetModules() {
        Project project = mock(Project.class);
        ModuleManager moduleManager = mock(ModuleManager.class);
        Module module = mock(Module.class);

        moduleManagerMockedStatic.when(() -> ModuleManager.getInstance(project)).thenReturn(moduleManager);
        when(moduleManager.getModules()).thenReturn(new Module[]{module});
        Collection<Module> result = underTest.getModules(project);

        moduleManagerMockedStatic.verify(() -> ModuleManager.getInstance(project));
        assertEquals(1, result.size());
        assertSame(module, result.iterator().next());
    }

    @Test
    public void testFindModule() {
        PsiFile psiFile = mock(PsiFile.class);
        Module module = mock(Module.class);

        moduleUtilMockedStatic.when(() -> ModuleUtil.findModuleForFile(psiFile)).thenReturn(module);

        Module result = underTest.findModule(psiFile);

        moduleUtilMockedStatic.verify(() -> ModuleUtil.findModuleForFile(psiFile));
        assertSame(module, result);
    }

    @Test
    public void testGetModuleManager() {
        Project project = mock(Project.class);
        ModuleManager moduleManager = mock(ModuleManager.class);

        moduleManagerMockedStatic.when(() -> ModuleManager.getInstance(project)).thenReturn(moduleManager);
        ModuleManager result = underTest.getModuleManager(project);

        moduleManagerMockedStatic.verify(() -> ModuleManager.getInstance(project));
        assertSame(moduleManager, result);
    }

    @Test
    public void testGetOrCreateRunConfigurationModule_get() {
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final JavaRunConfigurationModule javaRunConfigurationModule = mock(JavaRunConfigurationModule.class);

        when(mutationConfiguration.getConfigurationModule()).thenReturn(javaRunConfigurationModule);

        JavaRunConfigurationModule result = underTest.getOrCreateRunConfigurationModule(mutationConfiguration);

        verify(projectService, times(0)).getCurrentProject();
        assertSame(javaRunConfigurationModule, result);
    }

    @Test
    public void testGetOrCreateRunConfigurationModule_create() {
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final Project project = mock(Project.class);

        when(mutationConfiguration.getConfigurationModule()).thenReturn(null);
        when(projectService.getCurrentProject()).thenReturn(project);

        JavaRunConfigurationModule result = underTest.getOrCreateRunConfigurationModule(mutationConfiguration);

        verify(projectService).getCurrentProject();
        assertNotNull(result);
        assertSame(JavaRunConfigurationModule.class, result.getClass());
    }

    @After
    public void tearDown() {
        servicesMockedStatic.close();
        moduleUtilMockedStatic.close();
        moduleManagerMockedStatic.close();
    }

}
