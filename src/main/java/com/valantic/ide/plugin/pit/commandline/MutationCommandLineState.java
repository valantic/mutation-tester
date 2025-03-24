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
package com.valantic.ide.plugin.pit.commandline;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
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
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.util.PathsList;
import com.valantic.ide.plugin.pit.action.MutationAction;
import com.valantic.ide.plugin.pit.configuration.MutationConfiguration;
import com.valantic.ide.plugin.pit.configuration.option.MutationConfigurationOptions;
import com.valantic.ide.plugin.pit.enums.MutationConstants;
import com.valantic.ide.plugin.pit.exception.MutationClasspathException;
import com.valantic.ide.plugin.pit.exception.MutationConfigurationException;
import com.valantic.ide.plugin.pit.localization.Messages;
import com.valantic.ide.plugin.pit.services.Services;
import com.valantic.ide.plugin.pit.services.impl.ClassPathService;
import com.valantic.ide.plugin.pit.services.impl.DependencyService;
import com.valantic.ide.plugin.pit.services.impl.ProjectService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * created by fabian.huesig on 2022-02-01
 */
public class MutationCommandLineState extends JavaCommandLineState {

    private static final String DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss";
    private static final String INDEX_FILE = "index.html";
    private static final String MAIN_CLASS = "org.pitest.mutationtest.commandline.MutationCoverageReport";
    private static final String CP_FILE_NAME = "pitcp";
    private static final String CP_FILE_SUFFIX = ".txt";

    private ProjectService projectService = Services.getService(ProjectService.class);
    private ClassPathService classPathService = Services.getService(ClassPathService.class);
    private DependencyService dependencyService = Services.getService(DependencyService.class);

    private String creationTime;
    private MutationConfigurationOptions options;

    public MutationCommandLineState(final ExecutionEnvironment environment) {
        super(environment);
        Optional.of(environment)
                .map(ExecutionEnvironment::getRunProfile)
                .filter(MutationConfiguration.class::isInstance)
                .map(MutationConfiguration.class::cast)
                .ifPresent(mutationConfiguration -> {
                    this.creationTime = new SimpleDateFormat(DATE_FORMAT).format(new Date());
                    this.options = mutationConfiguration.getMutationConfigurationOptions();
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
                .orElseThrow(() -> new MutationConfigurationException("Reportdir can not be null"));
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
        javaParameters.setJdk(JavaParametersUtil.createProjectJdk(projectService.getCurrentProject(), null));
        Optional.of(javaParameters)
                .map(JavaParameters::getProgramParametersList)
                .ifPresent(this::populateParameterList);
        Optional.ofNullable(javaParameters)
                .map(JavaParameters::getClassPath)
                .ifPresent(this::addCommandLineClasspath);
        return javaParameters;
    }

    protected void addCommandLineClasspath(final PathsList pathsList) {
        final String pitestJar = dependencyService.getArtifact(options.getRepoUrl(), String.format("org/pitest/pitest/%1$s/pitest-%1$s.jar", options.getPitestVersion()));
        if (StringUtils.isNotEmpty(pitestJar)) {
            pathsList.add(pitestJar);
        }
        final String pitestEntryJar = dependencyService.getArtifact(options.getRepoUrl(), String.format("org/pitest/pitest-entry/%1$s/pitest-entry-%1$s.jar", options.getPitestVersion()));
        if (StringUtils.isNotEmpty(pitestEntryJar)) {
            pathsList.add(pitestEntryJar);
        }
        final String pitestCommandlineJar = dependencyService.getArtifact(options.getRepoUrl(), String.format("org/pitest/pitest-command-line/%1$s/pitest-command-line-%1$s.jar", options.getPitestVersion()));
        if (StringUtils.isNotEmpty(pitestCommandlineJar)) {
            pathsList.add(pitestCommandlineJar);
        }
        final String pitestJUnit5Jar = dependencyService.getArtifact(options.getRepoUrl(), String.format("org/pitest/pitest-junit5-plugin/%1$s/pitest-junit5-plugin-%1$s.jar", options.getPitestJunit5Version()));
        if (StringUtils.isNotEmpty(pitestJUnit5Jar)) {
            pathsList.add(pitestJUnit5Jar);
        }
        final String jUnit5JupiterJar = dependencyService.getArtifact(options.getRepoUrl(), String.format("org/junit/jupiter/junit-jupiter/%1$s/junit-jupiter-%1$s.jar", options.getJunitJupiterVersion()));
        if (StringUtils.isNotEmpty(jUnit5JupiterJar)) {
            pathsList.add(jUnit5JupiterJar);
        }
    }


    /**
     * populates parameter list with values from mutationConfigurationOptions.
     *
     * @param parametersList
     */
    protected void populateParameterList(final ParametersList parametersList) {
        addParameterIfExists(parametersList, "--targetClasses", options.getTargetClasses());
        addParameterIfExists(parametersList, "--targetTests", options.getTargetTests());
        addParameterIfExists(parametersList, "--reportDir", getReport(options.getReportDir()));
        addParameterIfExists(parametersList, "--sourceDirs", options.getSourceDirs());
        addParameterIfExists(parametersList, "--mutators", options.getMutators());
        addParameterIfExists(parametersList, "--timeoutConst", options.getTimeoutConst());
        addParameterIfExists(parametersList, "--outputFormats", options.getOutputFormats());

        addParameterIfExists(parametersList, "--dependencyDistance", options.getDependencyDistance());
        addParameterIfExists(parametersList, "--threads", options.getThreads());
        addParameterIfExists(parametersList, "--excludedMethods", options.getExcludedMethods());
        addParameterIfExists(parametersList, "--excludedClasses", options.getExcludedClasses());
        addParameterIfExists(parametersList, "--excludedTestClasses", options.getExcludedTests());
        addParameterIfExists(parametersList, "--avoidCallsTo", options.getAvoidCallsTo());
        addParameterIfExists(parametersList, "--timeoutFactor", options.getTimeoutFactor());
        addParameterIfExists(parametersList, "--maxMutationsPerClass", options.getMaxMutationsPerClass());
        addParameterIfExists(parametersList, "--jvmArgs", options.getJvmArgs());
        addParameterIfExists(parametersList, "--jvmPath", options.getJvmPath());
        addParameterIfExists(parametersList, "--mutableCodePaths", options.getMutableCodePaths());
        addParameterIfExists(parametersList, "--includedGroups", options.getIncludedGroups());
        addParameterIfExists(parametersList, "--excludedGroups", options.getExcludedGroups());
        addParameterIfExists(parametersList, "--detectInlinedCode", options.getDetectInlinedCode());
        addParameterIfExists(parametersList, "--mutationThreshold", options.getMutationThreshold());
        addParameterIfExists(parametersList, "--coverageThreshold", options.getCoverageThreshold());
        addParameterIfExists(parametersList, "--historyInputLocation", options.getHistoryInputLocation());
        addParameterIfExists(parametersList, "--historyOutputLocation", options.getHistoryOutputLocation());
        addParameterIfExists(parametersList, "--useClasspathJar", options.getUseClasspathJar());
        addParameterIfExists(parametersList, "--skipFailingTests", options.getSkipFailingTests());
        addParameterIfExists(parametersList, "--classPathFile", createClassPathFile());

        // these parameters can be empty but not null
        if (options.getTimestampedReports() != null) {
            parametersList.add(String.format("--timestampedReports=%s", options.getTimestampedReports()));
        }
        if (options.getIncludeLaunchClasspath() != null) {
            parametersList.add(String.format("--includeLaunchClasspath=%s", options.getIncludeLaunchClasspath()));
        }
        if (options.getVerbose() != null) {
            parametersList.add(String.format("--verbose=%s", options.getVerbose()));
        }
        if (options.getFailWhenNoMutations() != null) {
            parametersList.add(String.format("--failWhenNoMutations=%s", options.getFailWhenNoMutations()));
        }
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
     * creates a classpath file for all modules in the project
     *
     * @return classpath file in txt format
     */
    private String createClassPathFile() {
        try {
            final File file = FileUtilRt.createTempFile(CP_FILE_NAME, CP_FILE_SUFFIX, Boolean.parseBoolean(options.getDeleteCpFile()));
            final FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(String.join(System.getProperty("line.separator"),
                    classPathService.getClassPathForModules()));
            fileWriter.close();
            return file.getPath();
        } catch (IOException e) {
            throw new MutationClasspathException("Could not create classpath file", e);
        }
    }

    @Override
    protected GeneralCommandLine createCommandLine() throws ExecutionException {
        GeneralCommandLine commandLine = super.createCommandLine();
        final List<String> validParams = commandLine.getParametersList().getParameters().stream().filter(param -> !param.startsWith("-javaagent:")).collect(Collectors.toList());
        commandLine.getParametersList().clearAll();
        commandLine.getParametersList().addAll(validParams);
        return commandLine;
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
