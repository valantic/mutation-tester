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
 * Written by Fabian Hüsig <fabian.huesig@cec.valantic.com>, February, 2022
 */
package com.valantic.intellij.plugin.mutation.configuration;

import com.intellij.openapi.project.Project;
import com.valantic.intellij.plugin.mutation.configuration.option.MutationConfigurationOptions;
import com.valantic.intellij.plugin.mutation.services.Services;
import com.valantic.intellij.plugin.mutation.services.impl.ModuleService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * created by fabian.huesig on 2022-02-01
 */
@RunWith(MockitoJUnitRunner.class)
public class MutationConfigurationFactoryTest {

    private MutationConfigurationFactory underTest;

    private MockedStatic<Services> servicesMockedStatic;

    @Before
    public void setUp() {
        final MutationConfigurationType mutationConfigurationType = mock(MutationConfigurationType.class);
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ModuleService.class)).thenReturn(mock(ModuleService.class));
        underTest = new MutationConfigurationFactory(mutationConfigurationType);
    }

    @Test
    public void testGetId() {
        assertEquals("MutationConfiguration", underTest.getId());
    }

    @Test
    public void testCreateTemplateConfiguration() {
        final Project project = mock(Project.class);
        assertNotNull(underTest.createTemplateConfiguration(project));
    }

    @Test
    public void testGetOptionClass() {
        assertSame(MutationConfigurationOptions.class, underTest.getOptionsClass());
    }

    @After
    public void tearDown() {
        servicesMockedStatic.close();
    }

}
