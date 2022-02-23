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
package com.valantic.intellij.plugin.mutation.action;

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ThrowableRunnable;
import com.valantic.intellij.plugin.mutation.configuration.MutationConfiguration;
import com.valantic.intellij.plugin.mutation.configuration.option.MutationConfigurationOptions;
import com.valantic.intellij.plugin.mutation.services.Services;
import com.valantic.intellij.plugin.mutation.services.impl.ConfigurationService;
import com.valantic.intellij.plugin.mutation.services.impl.PsiService;
import com.valantic.intellij.plugin.mutation.services.impl.UtilService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2022-02-01
 */
@RunWith(MockitoJUnitRunner.class)
public class MutationActionTest {

    private MutationAction underTest;

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private PsiService psiService;
    @Mock
    private UtilService utilService;

    private MockedStatic<Services> servicesMockedStatic;
    private MockedStatic<PsiTreeUtil> psiTreeUtilMockedStatic;
    private MockedStatic<DefaultRunExecutor> defaultRunExecutorMockedStatic;
    private MockedStatic<ExecutionEnvironmentBuilder> executionEnvironmentBuilderMockedStatic;
    private MockedStatic<ExecutionManager> executionManagerMockedStatic;


    @Before
    public void setUp() {
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ConfigurationService.class)).thenReturn(configurationService);
        servicesMockedStatic.when(() -> Services.getService(PsiService.class)).thenReturn(psiService);
        servicesMockedStatic.when(() -> Services.getService(UtilService.class)).thenReturn(utilService);
        psiTreeUtilMockedStatic = mockStatic(PsiTreeUtil.class);
        defaultRunExecutorMockedStatic = mockStatic(DefaultRunExecutor.class);
        executionEnvironmentBuilderMockedStatic = mockStatic(ExecutionEnvironmentBuilder.class);
        executionManagerMockedStatic = mockStatic(ExecutionManager.class);
        underTest = new MutationAction();
    }

    @Test
    public void testSingletonActionConstructor() {
        final MutationAction[] mutationAction = MutationAction.getSingletonActions();

        assertNotNull(mutationAction);
        assertEquals(1, mutationAction.length);
        assertNull(mutationAction[0].getTargetTest());
        assertNull(mutationAction[0].getTargetClass());
    }

    @Test
    public void testSingletonActionConstructor_withParameters() {
        final MutationAction[] mutationAction = MutationAction.getSingletonActions("targetClass", "targetTest");

        assertNotNull(mutationAction);
        assertEquals(1, mutationAction.length);
        assertEquals("targetTest", mutationAction[0].getTargetTest());
        assertEquals("targetClass", mutationAction[0].getTargetClass());
    }

    @Test
    public void testUpdate_isNotEnabled() throws Throwable {
        final AnActionEvent anActionEvent = mock(AnActionEvent.class);
        final Presentation presentation = mock(Presentation.class);
        final ArgumentCaptor<ThrowableRunnable> throwableRunnerArgumentCaptor = ArgumentCaptor.forClass(ThrowableRunnable.class);

        when(anActionEvent.getPlace()).thenReturn("ProjectViewPopup");
        when(anActionEvent.getPresentation()).thenReturn(presentation);

        underTest.update(anActionEvent);

        verify(utilService).allowSlowOperations(throwableRunnerArgumentCaptor.capture());
        throwableRunnerArgumentCaptor.getValue().run();

        // call a second time to verify throwableRunnerArgumentCaptor did run
        underTest.update(anActionEvent);

        verify(presentation).setEnabled(false);
        assertFalse(presentation.isEnabled());
    }

    @Test
    public void testUpdate_isPsiJavaDirectoryImpl_isEnabled() throws Throwable {
        final AnActionEvent anActionEvent = mock(AnActionEvent.class);
        final Presentation presentation = mock(Presentation.class);
        final PsiElement psiElement = mock(PsiJavaDirectoryImpl.class);
        final PsiClass psiClass = mock(PsiClass.class);
        final ArgumentCaptor<ThrowableRunnable> throwableRunnerArgumentCaptor = ArgumentCaptor.forClass(ThrowableRunnable.class);

        when(anActionEvent.getPlace()).thenReturn("ProjectViewPopup");
        when(anActionEvent.getPresentation()).thenReturn(presentation);
        when(anActionEvent.getData(LangDataKeys.PSI_ELEMENT)).thenReturn(psiElement);
        servicesMockedStatic.when(() -> PsiTreeUtil.findChildOfType(psiElement, PsiClass.class)).thenReturn(psiClass);
        when(psiService.isTestClass(psiClass)).thenReturn(true);
        underTest.update(anActionEvent);

        verify(utilService).allowSlowOperations(throwableRunnerArgumentCaptor.capture());
        throwableRunnerArgumentCaptor.getValue().run();

        // call a second time to verify throwableRunnerArgumentCaptor did run
        underTest.update(anActionEvent);

        verify(presentation).setEnabled(true);
        verify(psiService).isTestClass(psiClass);
    }

    @Test
    public void testUpdate_isPsiJavaDirectoryImplWithNoChild_isDisabled() throws Throwable {
        final AnActionEvent anActionEvent = mock(AnActionEvent.class);
        final Presentation presentation = mock(Presentation.class);
        final PsiElement psiElement = mock(PsiElement.class);
        final ArgumentCaptor<ThrowableRunnable> throwableRunnerArgumentCaptor = ArgumentCaptor.forClass(ThrowableRunnable.class);

        when(anActionEvent.getPlace()).thenReturn("ProjectViewPopup");
        when(anActionEvent.getPresentation()).thenReturn(presentation);
        when(anActionEvent.getData(LangDataKeys.PSI_ELEMENT)).thenReturn(psiElement);
        underTest.update(anActionEvent);

        verify(utilService).allowSlowOperations(throwableRunnerArgumentCaptor.capture());
        throwableRunnerArgumentCaptor.getValue().run();

        // call a second time to verify throwableRunnerArgumentCaptor did run
        underTest.update(anActionEvent);

        verify(presentation).setEnabled(false);
        verify(psiService, times(0)).isTestClass(any());
    }

    @Test
    public void testActionPerformed_selectedDir() {
        final AnActionEvent anActionEvent = mock(AnActionEvent.class);
        final Project project = mock(Project.class);
        final DataContext dataContext = mock(DataContext.class);
        final PsiJavaDirectoryImpl psiJavaDirectoryImpl = mock(PsiJavaDirectoryImpl.class);
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final MutationConfigurationOptions mutationConfigurationOptions = mock(MutationConfigurationOptions.class);
        final JavaRunConfigurationModule javaRunConfigurationModule = mock(JavaRunConfigurationModule.class);
        final Executor executor = mock(Executor.class);
        final ExecutionEnvironmentBuilder builder = mock(ExecutionEnvironmentBuilder.class);
        final ExecutionManager executionManager = mock(ExecutionManager.class);
        final ExecutionEnvironment executionEnvironment = mock(ExecutionEnvironment.class);

        when(anActionEvent.getProject()).thenReturn(project);
        when(anActionEvent.getDataContext()).thenReturn(dataContext);
        when(dataContext.getData("psi.Element.array")).thenReturn(new PsiElement[]{psiJavaDirectoryImpl});
        when(psiService.resolvePackageNameForDir(psiJavaDirectoryImpl)).thenReturn("packageName");
        when(configurationService.getOrCreateMutationConfiguration(project, "packageName.*")).thenReturn(mutationConfiguration);
        when(mutationConfiguration.getMutationConfigurationOptions()).thenReturn(mutationConfigurationOptions);
        when(project.getBasePath()).thenReturn("projectBasePath");
        when(mutationConfiguration.getConfigurationModule()).thenReturn(javaRunConfigurationModule);
        when(mutationConfigurationOptions.getTargetTests()).thenReturn("packageName.*");
        defaultRunExecutorMockedStatic.when(() -> DefaultRunExecutor.getRunExecutorInstance()).thenReturn(executor);
        executionEnvironmentBuilderMockedStatic.when(() -> ExecutionEnvironmentBuilder.createOrNull(executor, mutationConfiguration)).thenReturn(builder);
        executionManagerMockedStatic.when(() -> ExecutionManager.getInstance(project)).thenReturn(executionManager);
        when(builder.build()).thenReturn(executionEnvironment);

        underTest.actionPerformed(anActionEvent);

        verify(mutationConfigurationOptions).setSourceDirs("projectBasePath");
        verify(mutationConfigurationOptions).setTargetTests("packageName.*");
        verify(mutationConfigurationOptions).setTargetClasses("packageName.*");
        verify(psiService).updateModule(project, "packageName.*", javaRunConfigurationModule);
        assertEquals(underTest.getTargetTest(), "packageName.*");
        assertEquals(underTest.getTargetClass(), "packageName.*");
        verify(executionManager).restartRunProfile(executionEnvironment);
    }

    @Test
    public void testActionPerformed_selectedFile() {
        final AnActionEvent anActionEvent = mock(AnActionEvent.class);
        final Project project = mock(Project.class);
        final DataContext dataContext = mock(DataContext.class);
        final PsiElement psiElement = mock(PsiElement.class);
        final PsiJavaFile psiJavaFile = mock(PsiJavaFile.class);
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final MutationConfigurationOptions mutationConfigurationOptions = mock(MutationConfigurationOptions.class);
        final JavaRunConfigurationModule javaRunConfigurationModule = mock(JavaRunConfigurationModule.class);
        final Executor executor = mock(Executor.class);
        final ExecutionEnvironmentBuilder builder = mock(ExecutionEnvironmentBuilder.class);
        final ExecutionManager executionManager = mock(ExecutionManager.class);
        final ExecutionEnvironment executionEnvironment = mock(ExecutionEnvironment.class);

        when(anActionEvent.getProject()).thenReturn(project);
        when(anActionEvent.getDataContext()).thenReturn(dataContext);
        when(dataContext.getData("psi.Element.array")).thenReturn(new PsiElement[]{psiElement});
        when(dataContext.getData("psi.File")).thenReturn(psiJavaFile);
        when(psiJavaFile.getPackageName()).thenReturn("packageName");
        when(psiJavaFile.getName()).thenReturn("className.java");
        when(configurationService.getOrCreateMutationConfiguration(project, "packageName.className")).thenReturn(mutationConfiguration);
        when(mutationConfiguration.getMutationConfigurationOptions()).thenReturn(mutationConfigurationOptions);
        when(project.getBasePath()).thenReturn("projectBasePath");
        when(mutationConfiguration.getConfigurationModule()).thenReturn(javaRunConfigurationModule);
        when(mutationConfigurationOptions.getTargetTests()).thenReturn("packageName.className");
        defaultRunExecutorMockedStatic.when(() -> DefaultRunExecutor.getRunExecutorInstance()).thenReturn(executor);
        executionEnvironmentBuilderMockedStatic.when(() -> ExecutionEnvironmentBuilder.createOrNull(executor, mutationConfiguration)).thenReturn(builder);
        executionManagerMockedStatic.when(() -> ExecutionManager.getInstance(project)).thenReturn(executionManager);
        when(builder.build()).thenReturn(executionEnvironment);

        underTest.actionPerformed(anActionEvent);

        verify(mutationConfigurationOptions).setSourceDirs("projectBasePath");
        verify(mutationConfigurationOptions).setTargetTests("packageName.className");
        verify(mutationConfigurationOptions, times(0)).setTargetClasses(anyString());
        verify(psiService).updateModule(project, "packageName.className", javaRunConfigurationModule);
        assertEquals(underTest.getTargetTest(), "packageName.className");
        verify(executionManager).restartRunProfile(executionEnvironment);
    }

    @After
    public void tearDown() {
        servicesMockedStatic.close();
        psiTreeUtilMockedStatic.close();
        defaultRunExecutorMockedStatic.close();
        executionEnvironmentBuilderMockedStatic.close();
        executionManagerMockedStatic.close();
    }
}
