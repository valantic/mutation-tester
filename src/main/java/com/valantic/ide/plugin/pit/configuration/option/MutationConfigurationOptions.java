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
package com.valantic.ide.plugin.pit.configuration.option;

import com.intellij.execution.configurations.ModuleBasedConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;
import org.apache.commons.lang3.StringUtils;


/**
 * created by fabian.huesig on 2022-02-01
 * <p>
 * Pit mutation run configuration options.
 * Based on @see <a href="https://pitest.org/quickstart/commandline/">Pit Commandline Quick Start</a>
 */
public class MutationConfigurationOptions extends ModuleBasedConfigurationOptions {
    private static final String DEFAULT_VALUE_TIMEOUT_CONST = "4000";
    private static final String DEFAULT_VALUE_OUTPUT_FORMATS = "HTML";
    private static final String DEFAULT_VALUE_MUTATORS = "DEFAULTS";
    private static final String DEFAULT_VALUE_TIMESTAMPED_REPORTS = "false";
    private static final String DEFAULT_REPORT_DIR = System.getProperty("java.io.tmpdir");
    private static final String DEFAULT_VALUE_INCLUDE_LAUNCH_CLASSPATH = "true";
    private static final String DEFAULT_VALUE_VERBOSE = "false";
    private static final String DEFAULT_VALUE_TIMEOUT_FACTOR = "1.25";
    private static final String DEFAULT_VALUE_MAX_MUTATIONS_PER_CLASS = "0";
    private static final String DEFAULT_VALUE_FAIL_WHEN_NO_MUTATIONS = "true";
    private static final String DEFAULT_VALUE_EXCLUDED_CLASSES = "*Test*";
    private static final String DEFAULT_VALUE_SKIP_FAILING_TESTS = "false";
    private static final String DEFAULT_VALUE_USE_CLASSPATH_JAR = "true";
    private static final String DEFAULT_VALUE_DELETE_CP_FILE = "true";
    private static final String DEFAULT_VALUE_JVM_ARGS = "--add-opens,java.base/java.io=ALL-UNNAMED";
    private static final String DEFAULT_VALUE_REPO_URL = "https://repo1.maven.org/maven2/";
    private static final String DEFAULT_VALUE_PITEST_VERSION = "1.18.0";
    private static final String DEFAULT_VALUE_PITEST_JUNIT5_VERSION = "1.2.1";
    private static final String DEFAULT_VALUE_JUNIT_JUPITER_VERSION = "5.11.4";

    /**
     * The classes to be mutated. This is expressed as a comma separated list of globs.
     * <p>
     * For example
     * «com.mycompany.*» or «com.mycompany.package.*, com.mycompany.packageB.Foo, com.partner.*»
     */
    private final StoredProperty<String> targetClasses = string(StringUtils.EMPTY).provideDelegate(this, "targetClasses");

    /**
     * A comma separated list of globs can be supplied to this parameter to limit the tests available to be run.
     * If this parameter is not supplied then any test fixture that matched targetClasses may be used, it is
     * however recommended that this parameter is always explicitly set.
     * <p>
     * This parameter can be used to point PIT to a top level suite or suites. Custom suites such as
     *
     * @see <a href="https://github.com/takari/takari-cpsuite">ClassPathSuite</a> are supported. Tests found via these suites can also be limited by the distance filter.
     */
    private final StoredProperty<String> targetTests = string(StringUtils.EMPTY).provideDelegate(this, "targetTests");

    /**
     * Output directory for the reports
     */
    private final StoredProperty<String> reportDir = string(DEFAULT_REPORT_DIR).provideDelegate(this, "reportDir");

    /**
     * Source directories
     */
    private final StoredProperty<String> sourceDirs = string(StringUtils.EMPTY).provideDelegate(this, "sourceDirs");

    /**
     * List of mutations as group or comma separated list of.
     *
     * @see <a href="https://pitest.org/quickstart/mutators">mutators</a>
     */
    private final StoredProperty<String> mutators = string(DEFAULT_VALUE_MUTATORS).provideDelegate(this, "mutators");

    /**
     * Constant amount of additional time to allow a test to run for (after the application of the timeoutFactor) before considering it to be stuck in an infinite loop.
     * Defaults to 4000
     */
    private final StoredProperty<String> timeoutConst = string(DEFAULT_VALUE_TIMEOUT_CONST).provideDelegate(this, "timeoutConst");

    /**
     * Comma separated list of formats in which to write mutation results as the mutations are analysed. Supported formats are HTML, XML, CSV.
     * Defaults to HTML.
     */
    private final StoredProperty<String> outputFormats = string(DEFAULT_VALUE_OUTPUT_FORMATS).provideDelegate(this, "outputFormats");

    /**
     * PIT will create a date and time stamped folder for its output each time it is run.
     * This can can make automation difficult, so the behaviour is by default suppressed by passing --timestampedReports=false.
     */
    private final StoredProperty<String> timestampedReports = string(DEFAULT_VALUE_TIMESTAMPED_REPORTS).provideDelegate(this, "timestampedReports");

    /**
     * Indicates if the PIT should try to mutate classes on the classpath with which it was launched. If not supplied this flag defaults to true.
     * If set to false only classes found on the paths specified by the –classPath option will be considered.
     */
    private final StoredProperty<String> includeLaunchClasspath = string(DEFAULT_VALUE_INCLUDE_LAUNCH_CLASSPATH).provideDelegate(this, "includeLaunchClasspath");

    /**
     * PIT can optionally apply an additional filter to the supplied tests, such that only tests a certain distance from a mutated class will be considered for running. e.g A test that directly calls
     * a method on a mutated class has a distance of 0 , a test that calls a method on a class that uses the mutee as an implementation detail has a distance of 1 etc.
     * This filter will not work for tests that utilise classes via interfaces, reflection or other methods where the dependencies between classes cannot be determined from the byte code.
     * The distance filter is particularly useful when performing a targeted mutation test of a subset of classes within a large project as it avoids the overheads of calculating the times and
     * coverage of tests that cannot exercise the mutees.
     */
    private final StoredProperty<String> dependencyDistance = string(StringUtils.EMPTY).provideDelegate(this, "dependencyDistance");

    /**
     * The number of threads to use when mutation testing.
     */
    private final StoredProperty<String> threads = string(StringUtils.EMPTY).provideDelegate(this, "threads");

    /**
     * List of globs to match against method names. Methods matching the globs will be excluded from mutation.
     */
    private final StoredProperty<String> excludedMethods = string(StringUtils.EMPTY).provideDelegate(this, "excludedMethods");

    /**
     * List of globs to match against class names. Matching classes will be excluded from mutation.
     * Prior to release 1.3.0 tests matching this filter were also excluded from being run.
     * From 1.3.0 onwards tests are excluded with the excludedTests parameter.
     */
    private final StoredProperty<String> excludedClasses = string(DEFAULT_VALUE_EXCLUDED_CLASSES).provideDelegate(this, "excludedClasses");

    /**
     * List of globs to match against test class names.
     * Matching tests will not be run (note if a test suite includes an excluded class, then it will “leak” back in).
     */
    private final StoredProperty<String> excludedTests = string(StringUtils.EMPTY).provideDelegate(this, "excludedTests");

    /**
     * List of packages and classes which are to be considered outside the scope of mutation. Any lines of code containing calls to these classes will not be mutated.
     * If a list is not explicitly supplied then PIT will default to a list of common logging packages as follows:
     * - java.util.logging
     * - org.apache.log4j
     * - org.slf4j
     * - org.apache.commons.logging
     * If the feature FLOGCALL is disabled, this parameter is ignored and logging calls are also mutated.
     */
    private final StoredProperty<String> avoidCallsTo = string(StringUtils.EMPTY).provideDelegate(this, "avoidCallsTo");

    /**
     * Output verbose logging. Defaults to off/false.
     */
    private final StoredProperty<String> verbose = string(DEFAULT_VALUE_VERBOSE).provideDelegate(this, "verbose");
    /**
     * A factor to apply to the normal runtime of a test when considering if it is stuck in an infinite loop.
     * Defaults to 1.25
     */
    private final StoredProperty<String> timeoutFactor = string(DEFAULT_VALUE_TIMEOUT_FACTOR).provideDelegate(this, "timeoutFactor");

    /**
     * The maximum number of mutations to create per class. Use 0 or -ve number to set no limit.
     */
    private final StoredProperty<String> maxMutationsPerClass = string(DEFAULT_VALUE_MAX_MUTATIONS_PER_CLASS).provideDelegate(this, "maxMutationsPerClass");

    /**
     * Argument string to use when PIT launches child processes.
     * This is most commonly used to increase the amount of memory available to the process, but may be used to pass any valid JVM argument.
     */
    private final StoredProperty<String> jvmArgs = string(DEFAULT_VALUE_JVM_ARGS).provideDelegate(this, "jvmArgs");

    /**
     * The path to tha java executable to be used to launch test with.
     * If none is supplied defaults to the one pointed to by JAVA_HOME.
     */
    private final StoredProperty<String> jvmPath = string(StringUtils.EMPTY).provideDelegate(this, "jvmPath");

    /**
     * Whether to throw an error when no mutations found.
     * Defaults to true
     */
    private final StoredProperty<String> failWhenNoMutations = string(DEFAULT_VALUE_FAIL_WHEN_NO_MUTATIONS).provideDelegate(this, "failWhenNoMutations");

    /**
     * List of classpaths which should be considered to contain mutable code.
     * If your build maintains separate output directories for tests and production classes this parameter should be set to your code output directory in order to avoid mutating test helper classes
     * etc.
     */
    private final StoredProperty<String> mutableCodePaths = string(StringUtils.EMPTY).provideDelegate(this, "mutableCodePaths");

    /**
     * Comma separated list of TestNG groups/JUnit categories to include in mutation analysis. Note that only class level categories are supported.
     */
    private final StoredProperty<String> includedGroups = string(StringUtils.EMPTY).provideDelegate(this, "includedGroups");

    /**
     * Comma separated list of TestNG groups/JUnit categories to exclude from mutation analysis. Note that only class level categories are supported.
     */
    private final StoredProperty<String> excludedGroups = string(StringUtils.EMPTY).provideDelegate(this, "excludedGroups");

    /**
     * Enabled by default since 0.29.
     * Flag to indicate if PIT should attempt to detect the inlined code generated by the java compiler in order to implement finally blocks. Each copy of the inlined code would normally be mutated
     * separately, resulting in multiple identical looking mutations. When inlined code detection is enabled PIT will attempt to spot inlined code and create only a single mutation that mutates all
     * affected instructions simultaneously.
     * The algorithm cannot easily distinguish between inlined copies of code, and genuine duplicate instructions on the same line within a finally block.
     * In the case of any doubt PIT will act cautiously and assume that the code is not inlined.
     * This will be detected as two separate inlined instructions:
     * <code>
     * finally {
     * int++;
     * int++;
     * }
     * </code>
     * But this will look confusing so PIT will assume no in-lining is taking place.
     * <code>
     * finally {
     * int++; int++;
     * }
     * </code>
     * This sort of pattern might not be common with integer addition, but things like string concatenation are likely to produce multiple similar instructions on the same line.
     */
    private final StoredProperty<String> detectInlinedCode = string(StringUtils.EMPTY).provideDelegate(this, "detectInlinedCode");

    /**
     * Mutation score threshold below which the build will fail. This is an integer percent (0-100) that represents the fraction of killed mutations out of all mutations.
     * Please bear in mind that your build may contain equivalent mutations. Careful thought must therefore be given when selecting a threshold.
     */
    private final StoredProperty<String> mutationThreshold = string(StringUtils.EMPTY).provideDelegate(this, "mutationThreshold");

    /**
     * Line coverage threshold below which the build will fail. This is an integer percent (0-100) that represents the fraction of the project covered by the tests.
     */
    private final StoredProperty<String> coverageThreshold = string(StringUtils.EMPTY).provideDelegate(this, "coverageThreshold");

    /**
     * Line coverage threshold below which the build will fail. This is an integer percent (0-100) that represents the fraction of the project covered by the tests.
     */
    private final StoredProperty<String> historyInputLocation = string(StringUtils.EMPTY).provideDelegate(this, "historyInputLocation");

    /**
     * Path to write history information for incremental analysis. May be the same as historyInputLocation.
     */
    private final StoredProperty<String> historyOutputLocation = string(StringUtils.EMPTY).provideDelegate(this, "historyOutputLocation");


    /**
     * Determines if failling tests should be skipped or not. Default value is false.
     */
    private final StoredProperty<String> skipFailingTests = string(DEFAULT_VALUE_SKIP_FAILING_TESTS).provideDelegate(this, "skipFailingTests");

    /**
     * A generated classpath file for all modules of the existing project.
     * Currently this value can not be changed.
     */
    private final StoredProperty<String> classPathFile = string("// WILL BE GENERATED. THIS VALUE IS NOT USED").provideDelegate(this, "classPathFile");

    /**
     * Determines if the minion process should create a classpath jar. Default value is true.
     * Currently this value can not be changed.
     */
    private final StoredProperty<String> useClasspathJar = string(DEFAULT_VALUE_USE_CLASSPATH_JAR).provideDelegate(this, "useClasspathJar");

    /**
     * A classpath file will be generated. Determine if the file should be deleted after running the tests.
     * Default is set to true.
     */
    private final StoredProperty<String> deleteCpFile = string(DEFAULT_VALUE_DELETE_CP_FILE).provideDelegate(this, "deleteCpFile");

    private final StoredProperty<String> repoUrl = string(DEFAULT_VALUE_REPO_URL).provideDelegate(this, "repoUrl");
    private final StoredProperty<String> pitestVersion = string(DEFAULT_VALUE_PITEST_VERSION).provideDelegate(this, "pitestVersion");
    private final StoredProperty<String> pitestJunit5Version = string(DEFAULT_VALUE_PITEST_JUNIT5_VERSION).provideDelegate(this, "pitestJunit5Version");
    private final StoredProperty<String> junitJupiterVersion = string(DEFAULT_VALUE_JUNIT_JUPITER_VERSION).provideDelegate(this, "junitJupiterVersion");


    // getter & setter
    public String getTargetClasses() {
        return targetClasses.getValue(this);
    }

    public void setTargetClasses(String targetClasses) {
        this.targetClasses.setValue(this, targetClasses);
    }

    public String getTargetTests() {
        return targetTests.getValue(this);
    }

    public void setTargetTests(String targetTests) {
        this.targetTests.setValue(this, targetTests);
    }

    public String getReportDir() {
        return reportDir.getValue(this);
    }

    public void setReportDir(String reportDir) {
        this.reportDir.setValue(this, reportDir);
    }

    public String getSourceDirs() {
        return sourceDirs.getValue(this);
    }

    public void setSourceDirs(String sourceDirs) {
        this.sourceDirs.setValue(this, sourceDirs);
    }

    public String getMutators() {
        return mutators.getValue(this);
    }

    public void setMutators(String mutators) {
        this.mutators.setValue(this, mutators);
    }

    public String getTimeoutConst() {
        return String.valueOf(timeoutConst.getValue(this));
    }

    public void setTimeoutConst(String timeoutConst) {
        this.timeoutConst.setValue(this, timeoutConst);
    }

    public String getOutputFormats() {
        return outputFormats.getValue(this);
    }

    public void setOutputFormats(String outputFormats) {
        this.outputFormats.setValue(this, outputFormats);
    }

    public String getTimestampedReports() {
        return String.valueOf(timestampedReports.getValue(this));
    }

    public void setTimestampedReports(String timestampedReports) {
        this.timestampedReports.setValue(this, timestampedReports);
    }

    public String getIncludeLaunchClasspath() {
        return includeLaunchClasspath.getValue(this);
    }

    public void setIncludeLaunchClasspath(String includeLaunchClasspath) {
        this.includeLaunchClasspath.setValue(this, includeLaunchClasspath);
    }

    public String getDependencyDistance() {
        return dependencyDistance.getValue(this);
    }

    public void setDependencyDistance(String dependencyDistance) {
        this.dependencyDistance.setValue(this, dependencyDistance);
    }

    public String getThreads() {
        return threads.getValue(this);
    }

    public void setThreads(String threads) {
        this.threads.setValue(this, threads);
    }

    public String getExcludedMethods() {
        return excludedMethods.getValue(this);
    }

    public void setExcludedMethods(String excludedMethods) {
        this.excludedMethods.setValue(this, excludedMethods);
    }

    public String getExcludedClasses() {
        return excludedClasses.getValue(this);
    }

    public void setExcludedClasses(String excludedClasses) {
        this.excludedClasses.setValue(this, excludedClasses);
    }

    public String getExcludedTests() {
        return excludedTests.getValue(this);
    }

    public void setExcludedTests(String excludedTests) {
        this.excludedTests.setValue(this, excludedTests);
    }

    public String getAvoidCallsTo() {
        return avoidCallsTo.getValue(this);
    }

    public void setAvoidCallsTo(String avoidCallsTo) {
        this.avoidCallsTo.setValue(this, avoidCallsTo);
    }

    public String getVerbose() {
        return verbose.getValue(this);
    }

    public void setVerbose(String verbose) {
        this.verbose.setValue(this, verbose);
    }

    public String getTimeoutFactor() {
        return timeoutFactor.getValue(this);
    }

    public void setTimeoutFactor(String timeoutFactor) {
        this.timeoutFactor.setValue(this, timeoutFactor);
    }

    public String getMaxMutationsPerClass() {
        return maxMutationsPerClass.getValue(this);
    }

    public void setMaxMutationsPerClass(String maxMutationsPerClass) {
        this.maxMutationsPerClass.setValue(this, maxMutationsPerClass);
    }

    public String getJvmArgs() {
        return jvmArgs.getValue(this);
    }

    public void setJvmArgs(String jvmArgs) {
        this.jvmArgs.setValue(this, jvmArgs);
    }

    public String getJvmPath() {
        return jvmPath.getValue(this);
    }

    public void setJvmPath(String jvmPath) {
        this.jvmPath.setValue(this, jvmPath);
    }

    public String getFailWhenNoMutations() {
        return failWhenNoMutations.getValue(this);
    }

    public void setFailWhenNoMutations(String failWhenNoMutations) {
        this.failWhenNoMutations.setValue(this, failWhenNoMutations);
    }

    public String getMutableCodePaths() {
        return mutableCodePaths.getValue(this);
    }

    public void setMutableCodePaths(String mutableCodePaths) {
        this.mutableCodePaths.setValue(this, mutableCodePaths);
    }

    public String getIncludedGroups() {
        return includedGroups.getValue(this);
    }

    public void setIncludedGroups(String includedGroups) {
        this.includedGroups.setValue(this, includedGroups);
    }

    public String getExcludedGroups() {
        return excludedGroups.getValue(this);
    }

    public void setExcludedGroups(String excludedGroups) {
        this.excludedGroups.setValue(this, excludedGroups);
    }

    public String getDetectInlinedCode() {
        return detectInlinedCode.getValue(this);
    }

    public void setDetectInlinedCode(String detectInlinedCode) {
        this.detectInlinedCode.setValue(this, detectInlinedCode);
    }

    public String getMutationThreshold() {
        return mutationThreshold.getValue(this);
    }

    public void setMutationThreshold(String mutationThreshold) {
        this.mutationThreshold.setValue(this, mutationThreshold);
    }

    public String getCoverageThreshold() {
        return coverageThreshold.getValue(this);
    }

    public void setCoverageThreshold(String coverageThreshold) {
        this.coverageThreshold.setValue(this, coverageThreshold);
    }

    public String getHistoryInputLocation() {
        return historyInputLocation.getValue(this);
    }

    public void setHistoryInputLocation(String historyInputLocation) {
        this.historyInputLocation.setValue(this, historyInputLocation);
    }

    public String getHistoryOutputLocation() {
        return historyOutputLocation.getValue(this);
    }

    public void setHistoryOutputLocation(String historyOutputLocation) {
        this.historyOutputLocation.setValue(this, historyOutputLocation);
    }

    public String getSkipFailingTests() {
        return skipFailingTests.getValue(this);
    }

    public void setSkipFailingTests(String skipFailingTests) {
        this.skipFailingTests.setValue(this, skipFailingTests);
    }

    public String getClasspathFile() {
        return classPathFile.getValue(this);
    }

    public void setClassPathFile(String classPathFile) {
        this.classPathFile.setValue(this, classPathFile);
    }

    public String getUseClasspathJar() {
        return useClasspathJar.getValue(this);
    }

    public void setUseClasspathJar(String useClasspathJar) {
        this.useClasspathJar.setValue(this, useClasspathJar);
    }

    public String getDeleteCpFile() {
        return deleteCpFile.getValue(this);
    }

    public void setDeleteCpFile(String deleteCpFile) {
        this.deleteCpFile.setValue(this, deleteCpFile);
    }

    public String getRepoUrl() {
        return repoUrl.getValue(this);
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl.setValue(this, repoUrl);
    }

    public String getPitestVersion() {
        return pitestVersion.getValue(this);
    }

    public void setPitestVersion(String pitestVersion) {
        this.pitestVersion.setValue(this, pitestVersion);
    }

    public String getPitestJunit5Version() {
        return pitestJunit5Version.getValue(this);
    }

    public void setPitestJunit5Version(String pitestJunit5Version) {
        this.pitestJunit5Version.setValue(this, pitestJunit5Version);
    }

    public String getJunitJupiterVersion() {
        return junitJupiterVersion.getValue(this);
    }

    public void setJunitJupiterVersion(String junitJupiterVersion) {
        this.junitJupiterVersion.setValue(this, junitJupiterVersion);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
