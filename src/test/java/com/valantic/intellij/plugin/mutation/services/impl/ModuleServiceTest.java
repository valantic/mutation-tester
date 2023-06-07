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
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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
class ModuleServiceTest {

    private ModuleService underTest;

    @Mock
    private ProjectService projectService;
    @Mock
    private PsiService psiService;

    private MockedStatic<Services> servicesMockedStatic;
    private MockedStatic<ModuleUtilCore> moduleUtilCoreMockedStatic;
    private MockedStatic<ModuleManager> moduleManagerMockedStatic;

    @BeforeEach
    void setUp() {
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ProjectService.class)).thenReturn(projectService);
        servicesMockedStatic.when(() -> Services.getService(PsiService.class)).thenReturn(psiService);
        moduleUtilCoreMockedStatic = mockStatic(ModuleUtilCore.class);
        moduleManagerMockedStatic = mockStatic(ModuleManager.class);
        underTest = spy(new ModuleService());
    }

    @Test
    void testGetModules() {
        final Project project = mock(Project.class);
        final ModuleManager moduleManager = mock(ModuleManager.class);
        final Module module = mock(Module.class);

        moduleManagerMockedStatic.when(() -> ModuleManager.getInstance(project)).thenReturn(moduleManager);
        when(moduleManager.getModules()).thenReturn(new Module[]{module});

        final Collection<Module> result = underTest.getModules(project);

        moduleManagerMockedStatic.verify(() -> ModuleManager.getInstance(project));
        assertEquals(1, result.size());
        assertSame(module, result.iterator().next());
    }

    @Test
    void testFindModule() {
        final PsiFile psiFile = mock(PsiFile.class);
        final Module module = mock(Module.class);

        moduleUtilCoreMockedStatic.when(() -> ModuleUtil.findModuleForFile(psiFile)).thenReturn(module);

        final Module result = underTest.findModule(psiFile);

        moduleUtilCoreMockedStatic.verify(() -> ModuleUtil.findModuleForFile(psiFile));
        assertSame(module, result);
    }

    @Test
    void testFindModule_fallbackModule() {
        final Project project = mock(Project.class);
        final Module module1 = mock(Module.class);
        final Module module2 = mock(Module.class);

        when(psiService.getPsiFile("randomName")).thenReturn(null);
        when(projectService.getCurrentProject()).thenReturn(project);
        doReturn(Arrays.asList(module1, module2)).when(underTest).getModules(project);
        when(module1.getName()).thenReturn(StringUtils.EMPTY);
        when(module2.getName()).thenReturn("foundFallbackModule");

        final Module result = underTest.findModule("randomName");

        assertSame(module2, result);
    }

    @Test
    void testGetModuleManager() {
        final Project project = mock(Project.class);
        final ModuleManager moduleManager = mock(ModuleManager.class);

        moduleManagerMockedStatic.when(() -> ModuleManager.getInstance(project)).thenReturn(moduleManager);
        final ModuleManager result = underTest.getModuleManager(project);

        moduleManagerMockedStatic.verify(() -> ModuleManager.getInstance(project));
        assertSame(moduleManager, result);
    }

    @Test
    void testGetOrCreateRunConfigurationModule_get() {
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final JavaRunConfigurationModule javaRunConfigurationModule = mock(JavaRunConfigurationModule.class);
        final String targetTests = "targetTests";
        final Project project = mock(Project.class);
        final Module module = mock(Module.class);

        when(mutationConfiguration.getConfigurationModule()).thenReturn(javaRunConfigurationModule);
        when(projectService.getCurrentProject()).thenReturn(project);
        when(module.getName()).thenReturn("moduleName");
        doReturn(Arrays.asList(module)).when(underTest).getModules(project);

        final JavaRunConfigurationModule result = underTest.getOrCreateRunConfigurationModule(mutationConfiguration, targetTests);

        verify(projectService).getCurrentProject();
        assertSame(javaRunConfigurationModule, result);
    }

    @Test
    void testGetOrCreateRunConfigurationModule_create() {
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final JavaRunConfigurationModule javaRunConfigurationModule = mock(JavaRunConfigurationModule.class);
        final Project project = mock(Project.class);
        final String targetTests = "targetTests";
        final Module module = mock(Module.class);

        when(mutationConfiguration.getConfigurationModule()).thenReturn(null);
        when(projectService.getCurrentProject()).thenReturn(project);
        when(module.getName()).thenReturn("moduleName");
        doReturn(Arrays.asList(module)).when(underTest).getModules(project);
        doReturn(javaRunConfigurationModule).when(underTest).createJavaRunConfigurationModule();

        final JavaRunConfigurationModule result = underTest.getOrCreateRunConfigurationModule(mutationConfiguration, targetTests);

        verify(projectService).getCurrentProject();
        assertNotNull(result);
        assertSame(javaRunConfigurationModule, result);
        verify(javaRunConfigurationModule).setModule(module);
        verify(javaRunConfigurationModule).setModuleName("moduleName");
    }

    @Test
    void testCreateJavaRunConfigurationModule() {
        final Project project = mock(Project.class);

        when(projectService.getCurrentProject()).thenReturn(project);

        final JavaRunConfigurationModule result = underTest.createJavaRunConfigurationModule();

        assertNotNull(result);
        assertSame(project, result.getProject());
    }

    @AfterEach
    void tearDown() {
        servicesMockedStatic.close();
        moduleUtilCoreMockedStatic.close();
        moduleManagerMockedStatic.close();
    }

}
