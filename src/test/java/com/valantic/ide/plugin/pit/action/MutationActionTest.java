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
package com.valantic.ide.plugin.pit.action;

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ThrowableRunnable;
import com.valantic.ide.plugin.pit.configuration.MutationConfiguration;
import com.valantic.ide.plugin.pit.configuration.option.MutationConfigurationOptions;
import com.valantic.ide.plugin.pit.services.Services;
import com.valantic.ide.plugin.pit.services.impl.ConfigurationService;
import com.valantic.ide.plugin.pit.services.impl.PsiService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2022-02-01
 */
@ExtendWith(MockitoExtension.class)
class MutationActionTest {

    private MutationAction underTest;

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private PsiService psiService;

    private MockedStatic<Services> servicesMockedStatic;
    private MockedStatic<PsiTreeUtil> psiTreeUtilMockedStatic;
    private MockedStatic<DefaultRunExecutor> defaultRunExecutorMockedStatic;
    private MockedStatic<ExecutionEnvironmentBuilder> executionEnvironmentBuilderMockedStatic;
    private MockedStatic<ExecutionManager> executionManagerMockedStatic;
    private MockedStatic<ReadAction> readActionMockedStatic;


    @BeforeEach
    void setUp() {
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ConfigurationService.class)).thenReturn(configurationService);
        servicesMockedStatic.when(() -> Services.getService(PsiService.class)).thenReturn(psiService);
        psiTreeUtilMockedStatic = mockStatic(PsiTreeUtil.class);
        defaultRunExecutorMockedStatic = mockStatic(DefaultRunExecutor.class);
        executionEnvironmentBuilderMockedStatic = mockStatic(ExecutionEnvironmentBuilder.class);
        executionManagerMockedStatic = mockStatic(ExecutionManager.class);
        readActionMockedStatic = mockStatic(ReadAction.class);
        underTest = new MutationAction();
    }

    @Test
    void testSingletonActionConstructor() {
        final MutationAction[] mutationAction = MutationAction.getSingletonActions();

        assertNotNull(mutationAction);
        assertEquals(1, mutationAction.length);
        assertNull(mutationAction[0].getTargetTest());
        assertNull(mutationAction[0].getTargetClass());
    }

    @Test
    void testSingletonActionConstructor_withParameters() {
        final MutationAction[] mutationAction = MutationAction.getSingletonActions("targetClass", "targetTest");

        assertNotNull(mutationAction);
        assertEquals(1, mutationAction.length);
        assertEquals("targetTest", mutationAction[0].getTargetTest());
        assertEquals("targetClass", mutationAction[0].getTargetClass());
    }

    @Test
    void testUpdate_isNotEnabled() throws Throwable {
        final AnActionEvent anActionEvent = mock(AnActionEvent.class);
        final Presentation presentation = mock(Presentation.class);
        final ArgumentCaptor<ThrowableRunnable> throwableRunnableArgumentCaptor = ArgumentCaptor.forClass(ThrowableRunnable.class);

        when(anActionEvent.getPlace()).thenReturn("ProjectViewPopup");
        when(anActionEvent.getPresentation()).thenReturn(presentation);

        underTest.update(anActionEvent);

        readActionMockedStatic.verify(() -> ReadAction.run(throwableRunnableArgumentCaptor.capture()));
        throwableRunnableArgumentCaptor.getValue().run();
        verify(presentation).setEnabled(false);
        assertFalse(presentation.isEnabled());
        verify(psiService, times(0)).isTestClass(any(PsiClass.class));
    }

    @Test
    void testUpdate_isPsiJavaDirectoryImpl_isEnabled() throws Throwable {
        final AnActionEvent anActionEvent = mock(AnActionEvent.class);
        final Presentation presentation = mock(Presentation.class);
        final PsiElement psiElement = mock(PsiJavaDirectoryImpl.class);
        final PsiClass psiClass = mock(PsiClass.class);
        final ArgumentCaptor<ThrowableRunnable> throwableRunnableArgumentCaptor = ArgumentCaptor.forClass(ThrowableRunnable.class);

        when(anActionEvent.getPlace()).thenReturn("ProjectViewPopup");
        when(anActionEvent.getPresentation()).thenReturn(presentation);
        when(anActionEvent.getData(LangDataKeys.PSI_ELEMENT)).thenReturn(psiElement);
        servicesMockedStatic.when(() -> PsiTreeUtil.findChildOfType(psiElement, PsiClass.class)).thenReturn(psiClass);
        when(psiService.isTestClass(psiClass)).thenReturn(true);

        underTest.update(anActionEvent);

        readActionMockedStatic.verify(() -> ReadAction.run(throwableRunnableArgumentCaptor.capture()));
        throwableRunnableArgumentCaptor.getValue().run();

        verify(presentation).setEnabled(true);
        verify(psiService).isTestClass(psiClass);
    }

    @Test
    void testUpdate_isPsiJavaDirectoryImplWithNoChild_isDisabled() throws Throwable {
        final AnActionEvent anActionEvent = mock(AnActionEvent.class);
        final Presentation presentation = mock(Presentation.class);
        final PsiElement psiElement = mock(PsiElement.class);
        final ArgumentCaptor<ThrowableRunnable> throwableRunnerArgumentCaptor = ArgumentCaptor.forClass(ThrowableRunnable.class);

        when(anActionEvent.getPlace()).thenReturn("ProjectViewPopup");
        when(anActionEvent.getPresentation()).thenReturn(presentation);
        when(anActionEvent.getData(LangDataKeys.PSI_ELEMENT)).thenReturn(psiElement);

        underTest.update(anActionEvent);

        readActionMockedStatic.verify(() -> ReadAction.run(throwableRunnerArgumentCaptor.capture()));
        throwableRunnerArgumentCaptor.getValue().run();

        verify(presentation).setEnabled(false);
        verify(psiService, times(0)).isTestClass(any());
    }

    @Test
    void testActionPerformed_selectedDir() {
        final AnActionEvent anActionEvent = mock(AnActionEvent.class);
        final Project project = mock(Project.class);
        final DataContext dataContext = mock(DataContext.class);
        final PsiJavaDirectoryImpl psiJavaDirectoryImpl = mock(PsiJavaDirectoryImpl.class);
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final MutationConfigurationOptions mutationConfigurationOptions = mock(MutationConfigurationOptions.class);
        final Executor executor = mock(Executor.class);
        final ExecutionEnvironmentBuilder builder = mock(ExecutionEnvironmentBuilder.class);
        final ExecutionManager executionManager = mock(ExecutionManager.class);
        final ExecutionEnvironment executionEnvironment = mock(ExecutionEnvironment.class);

        when(anActionEvent.getProject()).thenReturn(project);
        when(anActionEvent.getDataContext()).thenReturn(dataContext);
        when(dataContext.getData(DataKey.create("psi.Element.array"))).thenReturn(new PsiElement[]{psiJavaDirectoryImpl});
        when(psiService.resolvePackageNameForDir(psiJavaDirectoryImpl)).thenReturn("packageName");
        when(configurationService.getOrCreateMutationConfiguration(project, "packageName.*")).thenReturn(mutationConfiguration);
        when(mutationConfiguration.getMutationConfigurationOptions()).thenReturn(mutationConfigurationOptions);
        when(project.getBasePath()).thenReturn("projectBasePath");
        defaultRunExecutorMockedStatic.when(() -> DefaultRunExecutor.getRunExecutorInstance()).thenReturn(executor);
        executionEnvironmentBuilderMockedStatic.when(() -> ExecutionEnvironmentBuilder.createOrNull(executor, mutationConfiguration)).thenReturn(builder);
        executionManagerMockedStatic.when(() -> ExecutionManager.getInstance(project)).thenReturn(executionManager);
        when(builder.build()).thenReturn(executionEnvironment);

        underTest.actionPerformed(anActionEvent);

        verify(mutationConfigurationOptions).setSourceDirs("projectBasePath");
        verify(mutationConfigurationOptions).setTargetTests("packageName.*");
        verify(mutationConfigurationOptions).setTargetClasses("packageName.*");
        assertEquals("packageName.*", underTest.getTargetTest());
        assertEquals("packageName.*", underTest.getTargetClass());
        verify(executionManager).restartRunProfile(executionEnvironment);
    }

    @Test
    void testActionPerformed_selectedFile_withTestSuffix() {
        final AnActionEvent anActionEvent = mock(AnActionEvent.class);
        final Project project = mock(Project.class);
        final DataContext dataContext = mock(DataContext.class);
        final PsiElement psiElement = mock(PsiElement.class);
        final PsiJavaFile psiJavaFile = mock(PsiJavaFile.class);
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final MutationConfigurationOptions mutationConfigurationOptions = mock(MutationConfigurationOptions.class);
        final Executor executor = mock(Executor.class);
        final ExecutionEnvironmentBuilder builder = mock(ExecutionEnvironmentBuilder.class);
        final ExecutionManager executionManager = mock(ExecutionManager.class);
        final ExecutionEnvironment executionEnvironment = mock(ExecutionEnvironment.class);

        when(anActionEvent.getProject()).thenReturn(project);
        when(anActionEvent.getDataContext()).thenReturn(dataContext);
        when(dataContext.getData(DataKey.create("psi.Element.array"))).thenReturn(new PsiElement[]{psiElement});
        when(dataContext.getData(DataKey.create("psi.File"))).thenReturn(psiJavaFile);
        when(psiJavaFile.getPackageName()).thenReturn("packageName");
        when(psiJavaFile.getName()).thenReturn("classNameTest.java");
        when(psiService.doesClassExists("packageName.className")).thenReturn(true);
        when(configurationService.getOrCreateMutationConfiguration(project, "packageName.classNameTest")).thenReturn(mutationConfiguration);
        when(mutationConfiguration.getMutationConfigurationOptions()).thenReturn(mutationConfigurationOptions);
        when(project.getBasePath()).thenReturn("projectBasePath");
        defaultRunExecutorMockedStatic.when(() -> DefaultRunExecutor.getRunExecutorInstance()).thenReturn(executor);
        executionEnvironmentBuilderMockedStatic.when(() -> ExecutionEnvironmentBuilder.createOrNull(executor, mutationConfiguration)).thenReturn(builder);
        executionManagerMockedStatic.when(() -> ExecutionManager.getInstance(project)).thenReturn(executionManager);
        when(builder.build()).thenReturn(executionEnvironment);

        underTest.actionPerformed(anActionEvent);

        verify(mutationConfigurationOptions).setSourceDirs("projectBasePath");
        verify(mutationConfigurationOptions).setTargetTests("packageName.classNameTest");
        verify(mutationConfigurationOptions).setTargetClasses("packageName.className");
        assertEquals("packageName.classNameTest", underTest.getTargetTest());
        assertEquals("packageName.className", underTest.getTargetClass());
        verify(executionManager).restartRunProfile(executionEnvironment);
    }

    @Test
    void testActionPerformed_selectedFile_withTestPrefix() {
        final AnActionEvent anActionEvent = mock(AnActionEvent.class);
        final Project project = mock(Project.class);
        final DataContext dataContext = mock(DataContext.class);
        final PsiElement psiElement = mock(PsiElement.class);
        final PsiJavaFile psiJavaFile = mock(PsiJavaFile.class);
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final MutationConfigurationOptions mutationConfigurationOptions = mock(MutationConfigurationOptions.class);
        final Executor executor = mock(Executor.class);
        final ExecutionEnvironmentBuilder builder = mock(ExecutionEnvironmentBuilder.class);
        final ExecutionManager executionManager = mock(ExecutionManager.class);
        final ExecutionEnvironment executionEnvironment = mock(ExecutionEnvironment.class);

        when(anActionEvent.getProject()).thenReturn(project);
        when(anActionEvent.getDataContext()).thenReturn(dataContext);
        when(dataContext.getData(DataKey.create("psi.Element.array"))).thenReturn(new PsiElement[]{psiElement});
        when(dataContext.getData(DataKey.create("psi.File"))).thenReturn(psiJavaFile);
        when(psiJavaFile.getPackageName()).thenReturn("packageName");
        when(psiJavaFile.getName()).thenReturn("TestClassName.java");
        when(psiService.doesClassExists("packageName.ClassName")).thenReturn(true);
        when(psiService.doesClassExists("packageName.")).thenReturn(false);
        when(configurationService.getOrCreateMutationConfiguration(project, "packageName.TestClassName")).thenReturn(mutationConfiguration);
        when(mutationConfiguration.getMutationConfigurationOptions()).thenReturn(mutationConfigurationOptions);
        when(project.getBasePath()).thenReturn("projectBasePath");
        defaultRunExecutorMockedStatic.when(() -> DefaultRunExecutor.getRunExecutorInstance()).thenReturn(executor);
        executionEnvironmentBuilderMockedStatic.when(() -> ExecutionEnvironmentBuilder.createOrNull(executor, mutationConfiguration)).thenReturn(builder);
        executionManagerMockedStatic.when(() -> ExecutionManager.getInstance(project)).thenReturn(executionManager);
        when(builder.build()).thenReturn(executionEnvironment);

        underTest.actionPerformed(anActionEvent);

        verify(mutationConfigurationOptions).setSourceDirs("projectBasePath");
        verify(mutationConfigurationOptions).setTargetTests("packageName.TestClassName");
        verify(mutationConfigurationOptions).setTargetClasses("packageName.ClassName");
        assertEquals("packageName.TestClassName", underTest.getTargetTest());
        assertEquals("packageName.ClassName", underTest.getTargetClass());
        verify(executionManager).restartRunProfile(executionEnvironment);
    }

    @AfterEach
    void tearDown() {
        servicesMockedStatic.close();
        psiTreeUtilMockedStatic.close();
        defaultRunExecutorMockedStatic.close();
        executionEnvironmentBuilderMockedStatic.close();
        executionManagerMockedStatic.close();
        readActionMockedStatic.close();
    }
}
