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

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.ide.browsers.OpenUrlHyperlinkInfo;
import com.intellij.util.PathUtil;
import com.intellij.util.PathsList;
import com.valantic.intellij.plugin.mutation.action.MutationAction;
import com.valantic.intellij.plugin.mutation.configuration.MutationConfiguration;
import com.valantic.intellij.plugin.mutation.configuration.option.MutationConfigurationOptions;
import com.valantic.intellij.plugin.mutation.enums.MutationConstants;
import com.valantic.intellij.plugin.mutation.localization.Messages;
import com.valantic.intellij.plugin.mutation.services.Services;
import com.valantic.intellij.plugin.mutation.services.impl.ModuleService;
import com.valantic.intellij.plugin.mutation.services.impl.PsiService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.junit.runners.JUnit4;
import org.pitest.boot.HotSwapAgent;
import org.pitest.mutationtest.commandline.MutationCoverageReport;
import org.pitest.mutationtest.config.PluginServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;


/**
 * created by fabian.huesig on 2022-02-01
 */
public class MutationCommandLineState extends JavaCommandLineState {

    private static final String DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss";
    private static final String INDEX_FILE = "index.html";
    private static final String MAIN_CLASS = "org.pitest.mutationtest.commandline.MutationCoverageReport";

    private String creationTime;
    private JavaRunConfigurationModule module;
    private MutationConfigurationOptions options;

    private PsiService psiService = Services.getService(PsiService.class);
    private ModuleService moduleService = Services.getService(ModuleService.class);

    public MutationCommandLineState(final ExecutionEnvironment environment) {
        super(environment);
        Optional.of(environment)
                .map(ExecutionEnvironment::getRunProfile)
                .filter(MutationConfiguration.class::isInstance)
                .map(MutationConfiguration.class::cast)
                .ifPresent(mutationConfiguration -> {
                    this.creationTime = new SimpleDateFormat(DATE_FORMAT).format(new Date());
                    this.module = moduleService.getOrCreateRunConfigurationModule(mutationConfiguration);
                    this.options = mutationConfiguration.getMutationConfigurationOptions();
                    psiService.updateModule(mutationConfiguration.getProject(), this.options.getTargetTests(), this.module);
                });
    }

    @NotNull
    @Override
    public ExecutionResult execute(@NotNull final Executor executor, @NotNull final ProgramRunner runner) throws ExecutionException {
        final ConsoleView consoleView = createConsole(executor);
        final ProcessHandler processHandler = startProcess();
        consoleView.attachToProcess(processHandler);
        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processWillTerminate(final ProcessEvent event, final boolean willBeDestroyed) {
                Optional.of(options)
                        .map(MutationConfigurationOptions::getReportDir)
                        .map(MutationCommandLineState.this::getReport)
                        .map(reportPath -> {
                            if (Boolean.parseBoolean(options.getTimestampedReports())) {
                                return reportPath;
                            }
                            return reportPath + MutationConstants.PATH_SEPARATOR.getValue() + INDEX_FILE;
                        })
                        .map(OpenUrlHyperlinkInfo::new)
                        .ifPresent(openUrlHyperlinkInfo -> consoleView.printHyperlink(Messages.getMessage("report.hyperlink.text"), openUrlHyperlinkInfo));
            }
        });
        return new DefaultExecutionResult(consoleView, processHandler, MutationAction.getSingletonActions());
    }

    /**
     * Path of report. Uses configured reportDir and creates a subdirectory based on timestamp.
     *
     * @return reportDir + timestampFolder
     */
    protected String getReport(final String reportDir) {
        return Optional.ofNullable(reportDir)
                .map(path -> path.replaceFirst(MutationConstants.TRAILING_SLASH_REGEX.getValue(), StringUtils.EMPTY))
                .map(StringBuilder::new)
                .map(stringBuilder -> stringBuilder.append(MutationConstants.PATH_SEPARATOR.getValue()))
                .map(stringBuilder -> stringBuilder.append(creationTime))
                .map(StringBuilder::toString)
                .orElse(null);
    }

    /**
     * Setting JavaParameters to run org.pitest.mutationtest.commandline.MutationCoverageReport.
     * Parameters will be set from @see {@link MutationConfigurationOptions}
     *
     * @return
     * @throws ExecutionException
     */
    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        final JavaParameters javaParameters = new JavaParameters();
        javaParameters.setMainClass(MAIN_CLASS);
        JavaParametersUtil.configureModule(module, javaParameters, JavaParameters.JDK_AND_CLASSES_AND_TESTS, null);
        Optional.of(javaParameters)
                .map(JavaParameters::getProgramParametersList)
                .ifPresent(this::populateParameterList);
        Optional.ofNullable(javaParameters)
                .map(JavaParameters::getClassPath)
                .ifPresent(this::populatePathList);
        return javaParameters;
    }

    /**
     * populates path list with pitest class path entries.
     *
     * @param pathsList
     */
    protected void populatePathList(final PathsList pathsList) {
        pathsList.addFirst(PathUtil.getJarPathForClass(HotSwapAgent.class));
        pathsList.addFirst(PathUtil.getJarPathForClass(MutationCoverageReport.class));
        pathsList.addFirst(PathUtil.getJarPathForClass(PluginServices.class));
        pathsList.addFirst(PathUtil.getJarPathForClass(JUnit4.class));
    }

    /**
     * populates parameter list with values from mutationConfigurationOptions.
     *
     * @param parametersList
     */
    protected void populateParameterList(final ParametersList parametersList) {
        parametersList.add("--targetClasses", options.getTargetClasses());
        parametersList.add("--targetTests", options.getTargetTests());
        parametersList.add("--reportDir", getReport(options.getReportDir()));
        parametersList.add("--sourceDirs", options.getSourceDirs());
        parametersList.add("--mutators", options.getMutators());
        parametersList.add("--timeoutConst", options.getTimeoutConst());
        parametersList.add("--outputFormats", options.getOutputFormats());

        parametersList.add(String.format("--timestampedReports=%s", options.getTimestampedReports()));
        parametersList.add(String.format("--includeLaunchClasspath=%s", options.getIncludeLaunchClasspath()));
        parametersList.add(String.format("--verbose=%s", options.getVerbose()));
        parametersList.add(String.format("--failWhenNoMutations=%s", options.getFailWhenNoMutations()));

        addParameterIfExists(parametersList, "--dependencyDistance", options.getDependencyDistance());
        addParameterIfExists(parametersList, "--threads", options.getThreads());
        addParameterIfExists(parametersList, "--excludedMethods", options.getExcludedMethods());
        addParameterIfExists(parametersList, "--excludedClasses", options.getExcludedClasses());
        addParameterIfExists(parametersList, "--excludedTests", options.getExcludedTests());
        addParameterIfExists(parametersList, "--avoidCallsTo", options.getAvoidCallsTo());
        addParameterIfExists(parametersList, "--timeoutFactor", options.getTimeoutFactor());
        addParameterIfExists(parametersList, "--maxMutationsPerClass", options.getMaxMutationsPerClass());
        addParameterIfExists(parametersList, "--jvmArgs", options.getJvmArgs());
        addParameterIfExists(parametersList, "--jvmPath", options.getJvmPath());
        addParameterIfExists(parametersList, "--classPath", options.getClassPath());
        addParameterIfExists(parametersList, "--mutableCodePaths", options.getMutableCodePaths());
        addParameterIfExists(parametersList, "--testPlugin", options.getTestPlugin());
        addParameterIfExists(parametersList, "--includedGroups", options.getIncludedGroups());
        addParameterIfExists(parametersList, "--excludedGroups", options.getExcludedGroups());
        addParameterIfExists(parametersList, "--detectInlinedCode", options.getDetectInlinedCode());
        addParameterIfExists(parametersList, "--mutationThreshold", options.getMutationThreshold());
        addParameterIfExists(parametersList, "--coverageThreshold", options.getCoverageThreshold());
        addParameterIfExists(parametersList, "--historyInputLocation", options.getHistoryInputLocation());
        addParameterIfExists(parametersList, "--historyOutputLocation", options.getHistoryOutputLocation());
    }

    /**
     * adds the named parameter to the parameterList if the value is not empty.
     *
     * @param parametersList
     * @param parameterName
     * @param parameterValue
     */
    private void addParameterIfExists(final ParametersList parametersList, final String parameterName, final String parameterValue) {
        Optional.ofNullable(parameterValue)
                .filter(StringUtils::isNotEmpty)
                .ifPresent(value -> parametersList.add(parameterName, value));
    }

    /**
     * @TestOnly method for spying on super protected method in different packages.
     * normal processes will start super process directly.
     */
    @TestOnly
    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {
        return super.startProcess();
    }
}
