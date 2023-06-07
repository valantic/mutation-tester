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

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.valantic.intellij.plugin.mutation.configuration.MutationConfiguration;
import com.valantic.intellij.plugin.mutation.configuration.MutationConfigurationFactory;
import com.valantic.intellij.plugin.mutation.configuration.option.MutationConfigurationOptions;
import com.valantic.intellij.plugin.mutation.exception.MutationConfigurationException;
import com.valantic.intellij.plugin.mutation.services.Services;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2022-02-01
 */
@ExtendWith(MockitoExtension.class)
class ConfigurationServiceTest {

    private ConfigurationService underTest;

    @Mock
    private PsiService psiService;

    private MockedStatic<Services> servicesMockedStatic;
    private MockedStatic<RunManager> runManagerMockedStatic;

    @BeforeEach
    void setUp() {
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(PsiService.class)).thenReturn(psiService);
        runManagerMockedStatic = mockStatic(RunManager.class);
        underTest = Mockito.spy(ConfigurationService.class);
    }

    @Test
    void testGetOrCreateMutationConfiguration_foundMatchingConfiguration() {
        final String targetTests = "targetTests";
        final Project project = mock(Project.class);
        final RunManager runManager = mock(RunManager.class);
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final MutationConfigurationOptions mutationConfigurationOptions = mock(MutationConfigurationOptions.class);

        when(psiService.getClassName("targetTests")).thenReturn("targetTestClassName");
        runManagerMockedStatic.when(() -> RunManager.getInstance(project)).thenReturn(runManager);
        when(runManager.getAllConfigurationsList()).thenReturn(Arrays.asList(mutationConfiguration));
        when(mutationConfiguration.getMutationConfigurationOptions()).thenReturn(mutationConfigurationOptions);
        when(mutationConfigurationOptions.getTargetTests()).thenReturn("targetTestClassName");

        final MutationConfiguration result = underTest.getOrCreateMutationConfiguration(project, targetTests);

        assertNotNull(result);
        verify(psiService).getClassName("targetTests");
        assertEquals(mutationConfiguration, result);
    }

    @Test
    void testGetOrCreateMutationConfiguration_existingConfigurationFactory_createNewConfiguration() {
        final String targetTests = "targetTests";
        final Project project = mock(Project.class);
        final RunManager runManager = mock(RunManager.class);
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final MutationConfigurationOptions mutationConfigurationOptions = mock(MutationConfigurationOptions.class);
        final MutationConfigurationFactory mutationConfigurationFactory = mock(MutationConfigurationFactory.class);
        final RunnerAndConfigurationSettings runnerAndConfigurationSettings = mock(RunnerAndConfigurationSettings.class);

        when(psiService.getClassName("targetTests")).thenReturn("targetTestClassName");
        runManagerMockedStatic.when(() -> RunManager.getInstance(project)).thenReturn(runManager);
        when(runManager.getAllConfigurationsList()).thenReturn(Arrays.asList(mutationConfiguration));
        when(mutationConfiguration.getMutationConfigurationOptions()).thenReturn(mutationConfigurationOptions);
        when(mutationConfigurationOptions.getTargetTests()).thenReturn("AnotherTargetTestClassName");
        when(mutationConfiguration.getFactory()).thenReturn(mutationConfigurationFactory);
        when(runManager.createConfiguration(any(MutationConfiguration.class), eq(mutationConfigurationFactory))).thenReturn(runnerAndConfigurationSettings);
        doReturn(mutationConfiguration).when(underTest).createNewMutationConfiguration(project, mutationConfigurationFactory, "targetTestClassName");

        final MutationConfiguration result = underTest.getOrCreateMutationConfiguration(project, targetTests);

        assertNotNull(result);
        verify(psiService, times(2)).getClassName("targetTests");
        verify(mutationConfiguration).getFactory();
        verify(runManager).addConfiguration(runnerAndConfigurationSettings);
        assertEquals(mutationConfiguration, result);
    }

    @Test
    void testGetOrCreateMutationConfiguration_existingConfigurationFactory_createNewConfigurationFromPackageName() {
        final String targetTests = "targetpackage.*";
        final Project project = mock(Project.class);
        final RunManager runManager = mock(RunManager.class);
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final MutationConfigurationOptions mutationConfigurationOptions = mock(MutationConfigurationOptions.class);
        final MutationConfigurationFactory mutationConfigurationFactory = mock(MutationConfigurationFactory.class);
        final RunnerAndConfigurationSettings runnerAndConfigurationSettings = mock(RunnerAndConfigurationSettings.class);

        when(psiService.getClassName("targetpackage.*")).thenReturn("targetTestClassName");
        runManagerMockedStatic.when(() -> RunManager.getInstance(project)).thenReturn(runManager);
        when(runManager.getAllConfigurationsList()).thenReturn(Arrays.asList(mutationConfiguration));
        when(mutationConfiguration.getMutationConfigurationOptions()).thenReturn(mutationConfigurationOptions);
        when(mutationConfigurationOptions.getTargetTests()).thenReturn("AnotherTargetTestClassName");
        when(mutationConfiguration.getFactory()).thenReturn(mutationConfigurationFactory);
        when(runManager.createConfiguration(any(MutationConfiguration.class), eq(mutationConfigurationFactory))).thenReturn(runnerAndConfigurationSettings);
        doReturn(mutationConfiguration).when(underTest).createNewMutationConfiguration(project, mutationConfigurationFactory, "targetpackage");

        final MutationConfiguration result = underTest.getOrCreateMutationConfiguration(project, targetTests);

        assertNotNull(result);
        verify(psiService).getClassName("targetpackage.*");
        verify(mutationConfiguration).getFactory();
        verify(runManager).addConfiguration(runnerAndConfigurationSettings);
        assertEquals(mutationConfiguration, result);
    }

    @Test
    void testGetOrCreateMutationConfiguration_newConfigurationFactory_createNewConfiguration() {
        final String targetTests = "targetTests";
        final Project project = mock(Project.class);
        final RunManager runManager = mock(RunManager.class);
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final MutationConfigurationOptions mutationConfigurationOptions = mock(MutationConfigurationOptions.class);
        final RunnerAndConfigurationSettings runnerAndConfigurationSettings = mock(RunnerAndConfigurationSettings.class);

        when(psiService.getClassName("targetTests")).thenReturn("targetTestClassName");
        runManagerMockedStatic.when(() -> RunManager.getInstance(project)).thenReturn(runManager);
        when(runManager.getAllConfigurationsList()).thenReturn(Arrays.asList(mutationConfiguration));
        when(mutationConfiguration.getMutationConfigurationOptions()).thenReturn(mutationConfigurationOptions);
        when(mutationConfigurationOptions.getTargetTests()).thenReturn("AnotherTargetTestClassName");
        when(mutationConfiguration.getFactory()).thenReturn(null);

        when(runManager.createConfiguration(any(MutationConfiguration.class), any(MutationConfigurationFactory.class))).thenReturn(runnerAndConfigurationSettings);
        doReturn(mutationConfiguration).when(underTest).createNewMutationConfiguration(eq(project), any(MutationConfigurationFactory.class), eq("targetTestClassName"));

        final MutationConfiguration result = underTest.getOrCreateMutationConfiguration(project, targetTests);

        assertNotNull(result);
        verify(psiService, times(2)).getClassName("targetTests");
        verify(mutationConfiguration).getFactory();
        verify(runManager).addConfiguration(runnerAndConfigurationSettings);
        assertEquals(mutationConfiguration, result);
    }

    @Test
    void testCreateNewMutationConfiguration() {
        final Project project = mock(Project.class);
        final RunManager runManager = mock(RunManager.class);
        final MutationConfigurationFactory factory = mock(MutationConfigurationFactory.class);
        final RunnerAndConfigurationSettings runnerAndConfigurationSettings = mock(RunnerAndConfigurationSettings.class);
        final RunConfiguration runConfiguration = mock(RunConfiguration.class);
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);

        runManagerMockedStatic.when(() -> RunManager.getInstance(project)).thenReturn(runManager);
        when(runManager.getConfigurationTemplate(factory)).thenReturn(runnerAndConfigurationSettings);
        when(runnerAndConfigurationSettings.getConfiguration()).thenReturn(runConfiguration);
        when(factory.createConfiguration("configName", runConfiguration)).thenReturn(mutationConfiguration);

        final MutationConfiguration result = underTest.createNewMutationConfiguration(project, factory, "configName");

        assertSame(mutationConfiguration, result);
    }

    @Test
    void testCreateNewMutationConfiguration_hasError() {
        final Project project = mock(Project.class);
        final RunManager runManager = mock(RunManager.class);
        final MutationConfigurationFactory factory = mock(MutationConfigurationFactory.class);
        final RunnerAndConfigurationSettings runnerAndConfigurationSettings = mock(RunnerAndConfigurationSettings.class);
        final RunConfiguration runConfiguration = mock(RunConfiguration.class);

        runManagerMockedStatic.when(() -> RunManager.getInstance(project)).thenReturn(runManager);
        when(runManager.getConfigurationTemplate(factory)).thenReturn(runnerAndConfigurationSettings);
        when(runnerAndConfigurationSettings.getConfiguration()).thenReturn(runConfiguration);
        when(factory.createConfiguration("configName", runConfiguration)).thenReturn(runConfiguration);

        assertThrows(MutationConfigurationException.class, () -> {
            underTest.createNewMutationConfiguration(project, factory, "configName");
        });
    }

    @AfterEach
    void tearDown() {
        servicesMockedStatic.close();
        runManagerMockedStatic.close();
    }
}
