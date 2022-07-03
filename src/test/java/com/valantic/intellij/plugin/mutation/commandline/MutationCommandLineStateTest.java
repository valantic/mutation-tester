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
package com.valantic.intellij.plugin.mutation.commandline;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.ide.browsers.OpenUrlHyperlinkInfo;
import com.intellij.psi.search.ExecutionSearchScopes;
import com.intellij.psi.search.GlobalSearchScope;
import com.valantic.intellij.plugin.mutation.action.MutationAction;
import com.valantic.intellij.plugin.mutation.configuration.MutationConfiguration;
import com.valantic.intellij.plugin.mutation.configuration.option.MutationConfigurationOptions;
import com.valantic.intellij.plugin.mutation.localization.Messages;
import com.valantic.intellij.plugin.mutation.services.Services;
import com.valantic.intellij.plugin.mutation.services.impl.ModuleService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2022-02-01
 */
@RunWith(MockitoJUnitRunner.class)
public class MutationCommandLineStateTest {

    private MutationCommandLineState underTest;

    @Mock
    private ModuleService moduleService;

    @Mock
    private ExecutionEnvironment environment;
    @Mock
    private MutationConfigurationOptions mutationConfigurationOptions;
    @Mock
    private JavaRunConfigurationModule javaRunConfigurationModule;


    private MockedStatic<Services> servicesMockedStatic;
    private MockedStatic<TextConsoleBuilderFactory> textConsoleBuilderFactoryMockedStatic;
    private MockedStatic<Messages> messagesMockedStatic;
    private MockedStatic<JavaParametersUtil> javaParametersUtilMockedStatic;
    private MockedStatic<ExecutionSearchScopes> executionSearchScopesMockedStatic;

    @Before
    public void setUp() {
        final MutationConfiguration mutationConfiguration = mock(MutationConfiguration.class);
        final GlobalSearchScope searchScope = mock(GlobalSearchScope.class);
        final TextConsoleBuilderFactory textConsoleBuilderFactory = mock(TextConsoleBuilderFactory.class);

        javaParametersUtilMockedStatic = mockStatic(JavaParametersUtil.class);
        messagesMockedStatic = mockStatic(Messages.class);
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ModuleService.class)).thenReturn(moduleService);
        executionSearchScopesMockedStatic = mockStatic(ExecutionSearchScopes.class);
        executionSearchScopesMockedStatic.when(() -> ExecutionSearchScopes.executionScope(any(), any())).thenReturn(searchScope);
        textConsoleBuilderFactoryMockedStatic = mockStatic(TextConsoleBuilderFactory.class);
        textConsoleBuilderFactoryMockedStatic.when(() -> TextConsoleBuilderFactory.getInstance()).thenReturn(textConsoleBuilderFactory);
        when(mutationConfiguration.getMutationConfigurationOptions()).thenReturn(mutationConfigurationOptions);
        when(mutationConfigurationOptions.getTargetTests()).thenReturn("targetTests");
        when(environment.getRunProfile()).thenReturn(mutationConfiguration);
        when(moduleService.getOrCreateRunConfigurationModule(mutationConfiguration, "targetTests")).thenReturn(javaRunConfigurationModule);

        underTest = spy(new MutationCommandLineState(environment));

        verify(moduleService).getOrCreateRunConfigurationModule(mutationConfiguration, "targetTests");
    }


    @Test
    public void testExecute_noTimestamps() throws ExecutionException {
        final Executor executor = mock(Executor.class);
        final ProgramRunner programRunner = mock(ProgramRunner.class);
        final TextConsoleBuilder textConsoleBuilder = mock(TextConsoleBuilder.class);
        final ConsoleView consoleView = mock(ConsoleView.class);
        final OSProcessHandler processHandler = mock(OSProcessHandler.class);
        final ArgumentCaptor<ProcessAdapter> processAdapterArgumentCaptor = ArgumentCaptor.forClass(ProcessAdapter.class);
        final ProcessEvent processEvent = mock(ProcessEvent.class);

        doReturn(textConsoleBuilder).when(underTest).getConsoleBuilder();
        when(textConsoleBuilder.getConsole()).thenReturn(consoleView);
        doReturn(processHandler).when(underTest).startProcess();

        ExecutionResult result = underTest.execute(executor, programRunner);
        verify(processHandler).addProcessListener(processAdapterArgumentCaptor.capture());
        final ProcessAdapter processAdapter = processAdapterArgumentCaptor.getValue();
        when(mutationConfigurationOptions.getReportDir()).thenReturn("reportDir");
        when(mutationConfigurationOptions.getTimestampedReports()).thenReturn("false");
        doReturn("/var/temp").when(underTest).getReport("reportDir");
        messagesMockedStatic.when(() -> Messages.getMessage("report.hyperlink.text")).thenReturn("Open in browser");

        processAdapter.processWillTerminate(processEvent, false);


        verify(consoleView).attachToProcess(processHandler);
        verify(consoleView).printHyperlink(eq("Open in browser"), any(OpenUrlHyperlinkInfo.class));
        assertNotNull(result);
        assertEquals(1, result.getActions().length);
        assertEquals(MutationAction.class, result.getActions()[0].getClass());
        assertSame(consoleView, result.getExecutionConsole());
        assertSame(processHandler, result.getProcessHandler());
    }

    @Test
    public void testExecute_withTimestamps() throws ExecutionException {
        final Executor executor = mock(Executor.class);
        final ProgramRunner programRunner = mock(ProgramRunner.class);
        final TextConsoleBuilder textConsoleBuilder = mock(TextConsoleBuilder.class);
        final ConsoleView consoleView = mock(ConsoleView.class);
        final OSProcessHandler processHandler = mock(OSProcessHandler.class);
        final ArgumentCaptor<ProcessAdapter> processAdapterArgumentCaptor = ArgumentCaptor.forClass(ProcessAdapter.class);
        final ProcessEvent processEvent = mock(ProcessEvent.class);

        doReturn(textConsoleBuilder).when(underTest).getConsoleBuilder();
        when(textConsoleBuilder.getConsole()).thenReturn(consoleView);
        doReturn(processHandler).when(underTest).startProcess();

        underTest.execute(executor, programRunner);
        verify(processHandler).addProcessListener(processAdapterArgumentCaptor.capture());
        final ProcessAdapter processAdapter = processAdapterArgumentCaptor.getValue();
        when(mutationConfigurationOptions.getReportDir()).thenReturn("reportDir");
        when(mutationConfigurationOptions.getTimestampedReports()).thenReturn("true");
        doReturn("/var/temp").when(underTest).getReport("reportDir");
        messagesMockedStatic.when(() -> Messages.getMessage("report.hyperlink.text")).thenReturn("Open in browser");

        processAdapter.processWillTerminate(processEvent, false);


        verify(consoleView).attachToProcess(processHandler);
        verify(consoleView).printHyperlink(eq("Open in browser"), any(OpenUrlHyperlinkInfo.class));
    }

    @Test
    public void testGetReport_withString() {
        final String reportDir = "var/temp/reportDir/";

        final String result = underTest.getReport(reportDir);

        assertTrue(result.matches("^var\\/temp\\/reportDir\\/\\d\\d\\d\\d-\\d\\d-\\d\\d-\\d\\d-\\d\\d-\\d\\d$"));
    }

    @Test
    public void testGetReport_null() {
        assertNull(underTest.getReport(null));
    }

    @Test
    public void testCreateJavaParameters() throws ExecutionException {
        when(mutationConfigurationOptions.getTargetClasses()).thenReturn("targetClasses");
        when(mutationConfigurationOptions.getTargetTests()).thenReturn("targetTests");
        when(mutationConfigurationOptions.getReportDir()).thenReturn("reportDir");
        when(mutationConfigurationOptions.getSourceDirs()).thenReturn("sourceDirs");
        when(mutationConfigurationOptions.getMutators()).thenReturn("mutators");
        when(mutationConfigurationOptions.getTimeoutConst()).thenReturn("timeoutConst");
        when(mutationConfigurationOptions.getOutputFormats()).thenReturn("outputFormats");

        when(mutationConfigurationOptions.getTimestampedReports()).thenReturn("true");
        when(mutationConfigurationOptions.getIncludeLaunchClasspath()).thenReturn("true");
        when(mutationConfigurationOptions.getVerbose()).thenReturn("verbose");
        when(mutationConfigurationOptions.getFailWhenNoMutations()).thenReturn("true");

        when(mutationConfigurationOptions.getDependencyDistance()).thenReturn("dependencyDistance");
        when(mutationConfigurationOptions.getThreads()).thenReturn("threads");
        when(mutationConfigurationOptions.getExcludedMethods()).thenReturn("excludedMethods");
        when(mutationConfigurationOptions.getExcludedClasses()).thenReturn("excludedClasses");
        when(mutationConfigurationOptions.getExcludedTests()).thenReturn("excludedTests");
        when(mutationConfigurationOptions.getAvoidCallsTo()).thenReturn("avoidCallsTo");
        when(mutationConfigurationOptions.getTimeoutFactor()).thenReturn("timeoutFactor");
        when(mutationConfigurationOptions.getMaxMutationsPerClass()).thenReturn("maxMutationsPerClass");
        when(mutationConfigurationOptions.getJvmArgs()).thenReturn("jvmArgs");
        when(mutationConfigurationOptions.getJvmPath()).thenReturn("jvmPath");
        when(mutationConfigurationOptions.getClassPath()).thenReturn("classPath");
        when(mutationConfigurationOptions.getMutableCodePaths()).thenReturn("mutableCodePaths");
        when(mutationConfigurationOptions.getTestPlugin()).thenReturn("testPlugin");
        when(mutationConfigurationOptions.getIncludedGroups()).thenReturn("includedGroups");
        when(mutationConfigurationOptions.getExcludedGroups()).thenReturn("excludedGroups");
        when(mutationConfigurationOptions.getDetectInlinedCode()).thenReturn("detectInlinedCode");
        when(mutationConfigurationOptions.getMutationThreshold()).thenReturn("mutationThreshold");
        when(mutationConfigurationOptions.getCoverageThreshold()).thenReturn("coverageThreshold");
        when(mutationConfigurationOptions.getHistoryInputLocation()).thenReturn("historyInputLocation");
        when(mutationConfigurationOptions.getHistoryOutputLocation()).thenReturn("historyOutputLocation");

        JavaParameters result = underTest.createJavaParameters();

        assertEquals("org.pitest.mutationtest.commandline.MutationCoverageReport", result.getMainClass());
        javaParametersUtilMockedStatic.verify(() ->
                JavaParametersUtil.configureModule(eq(javaRunConfigurationModule), any(JavaParameters.class), eq(JavaParameters.JDK_AND_CLASSES_AND_TESTS), eq(null)));
        assertTrue(result.getProgramParametersList().toString().matches("\\[--targetClasses, targetClasses, --targetTests, targetTests, --reportDir, reportDir\\/\\d\\d\\d\\d-\\d\\d-\\d\\d-\\d\\d-\\d\\d-\\d\\d, --sourceDirs, sourceDirs, --mutators, mutators, --timeoutConst, timeoutConst, --outputFormats, outputFormats, --timestampedReports=true, --includeLaunchClasspath=true, --verbose=verbose, --failWhenNoMutations=true, --dependencyDistance, dependencyDistance, --threads, threads, --excludedMethods, excludedMethods, --excludedClasses, excludedClasses, --excludedTests, excludedTests, --avoidCallsTo, avoidCallsTo, --timeoutFactor, timeoutFactor, --maxMutationsPerClass, maxMutationsPerClass, --jvmArgs, jvmArgs, --jvmPath, jvmPath, --classPath, classPath, --mutableCodePaths, mutableCodePaths, --testPlugin, testPlugin, --includedGroups, includedGroups, --excludedGroups, excludedGroups, --detectInlinedCode, detectInlinedCode, --mutationThreshold, mutationThreshold, --coverageThreshold, coverageThreshold, --historyInputLocation, historyInputLocation, --historyOutputLocation, historyOutputLocation]"));

        assertTrue(result.getClassPath().getPathList().stream()
                .filter(pathListEntry -> pathListEntry.endsWith("/junit4.jar")).findFirst().isPresent());
        assertTrue(result.getClassPath().getPathList().stream()
                .filter(pathListEntry -> pathListEntry.contains("/pitest-entry-")).findFirst().isPresent());
        assertTrue(result.getClassPath().getPathList().stream()
                .filter(pathListEntry -> pathListEntry.contains("/pitest-command-line-")).findFirst().isPresent());
        assertTrue(result.getClassPath().getPathList().stream()
                .filter(pathListEntry -> pathListEntry.contains("/pitest-")).findFirst().isPresent());
    }

    @After
    public void tearDown() {
        messagesMockedStatic.close();
        servicesMockedStatic.close();
        textConsoleBuilderFactoryMockedStatic.close();
        javaParametersUtilMockedStatic.close();
        executionSearchScopesMockedStatic.close();
    }
}
