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
package com.valantic.intellij.plugin.mutation.configuration.option;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * created by fabian.huesig on 2022-02-01
 */
@RunWith(MockitoJUnitRunner.class)
public class MutationConfigurationOptionsTest {

    @InjectMocks
    private MutationConfigurationOptions underTest;

    @Test
    public void testSetterAndGetter() {
        underTest.setTargetClasses("targetClasses");
        assertEquals("targetClasses", underTest.getTargetClasses());

        underTest.setTargetTests("targetTests");
        assertEquals("targetTests", underTest.getTargetTests());

        underTest.setReportDir("reportDir");
        assertEquals("reportDir", underTest.getReportDir());

        underTest.setSourceDirs("sourceDirs");
        assertEquals("sourceDirs", underTest.getSourceDirs());

        underTest.setMutators("ALL");
        assertEquals("ALL", underTest.getMutators());

        underTest.setTimeoutConst("100");
        assertEquals("100", underTest.getTimeoutConst());

        underTest.setOutputFormats("HTML");
        assertEquals("HTML", underTest.getOutputFormats());

        underTest.setTimestampedReports("false");
        assertEquals("false", underTest.getTimestampedReports());

        underTest.setIncludeLaunchClasspath("includeLaunchClasspath");
        assertEquals("includeLaunchClasspath", underTest.getIncludeLaunchClasspath());

        underTest.setDependencyDistance("dependencyDistance");
        assertEquals("dependencyDistance", underTest.getDependencyDistance());

        underTest.setThreads("threads");
        assertEquals("threads", underTest.getThreads());

        underTest.setExcludedMethods("excludedMethods");
        assertEquals("excludedMethods", underTest.getExcludedMethods());

        underTest.setExcludedClasses("excludedClasses");
        assertEquals("excludedClasses", underTest.getExcludedClasses());

        underTest.setExcludedTests("excludedTests");
        assertEquals("excludedTests", underTest.getExcludedTests());

        underTest.setAvoidCallsTo("avoidCallsTo");
        assertEquals("avoidCallsTo", underTest.getAvoidCallsTo());

        underTest.setVerbose("verbose");
        assertEquals("verbose", underTest.getVerbose());

        underTest.setTimeoutFactor("timeoutFactor");
        assertEquals("timeoutFactor", underTest.getTimeoutFactor());

        underTest.setMaxMutationsPerClass("maxMutationsPerClass");
        assertEquals("maxMutationsPerClass", underTest.getMaxMutationsPerClass());

        underTest.setJvmArgs("jvmArgs");
        assertEquals("jvmArgs", underTest.getJvmArgs());

        underTest.setJvmPath("jvmPath");
        assertEquals("jvmPath", underTest.getJvmPath());

        underTest.setFailWhenNoMutations("failWhenNoMutations");
        assertEquals("failWhenNoMutations", underTest.getFailWhenNoMutations());

        underTest.setClassPath("classPath");
        assertEquals("classPath", underTest.getClassPath());

        underTest.setMutableCodePaths("mutableCodePaths");
        assertEquals("mutableCodePaths", underTest.getMutableCodePaths());

        underTest.setTestPlugin("testPlugin");
        assertEquals("testPlugin", underTest.getTestPlugin());

        underTest.setIncludedGroups("includedGroups");
        assertEquals("includedGroups", underTest.getIncludedGroups());

        underTest.setExcludedGroups("excludedGroups");
        assertEquals("excludedGroups", underTest.getExcludedGroups());

        underTest.setDetectInlinedCode("detectInlinedCode");
        assertEquals("detectInlinedCode", underTest.getDetectInlinedCode());

        underTest.setMutationThreshold("mutationThreshold");
        assertEquals("mutationThreshold", underTest.getMutationThreshold());

        underTest.setCoverageThreshold("coverageThreshold");
        assertEquals("coverageThreshold", underTest.getCoverageThreshold());

        underTest.setHistoryInputLocation("historyInputLocation");
        assertEquals("historyInputLocation", underTest.getHistoryInputLocation());

        underTest.setHistoryOutputLocation("historyOutputLocation");
        assertEquals("historyOutputLocation", underTest.getHistoryOutputLocation());
    }

}
