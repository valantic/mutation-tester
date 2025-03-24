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
package com.valantic.ide.plugin.pit.configuration;

import com.intellij.openapi.project.Project;
import com.valantic.ide.plugin.pit.configuration.option.MutationConfigurationOptions;
import com.valantic.ide.plugin.pit.services.Services;
import com.valantic.ide.plugin.pit.services.impl.ModuleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * created by fabian.huesig on 2022-02-01
 */
@ExtendWith(MockitoExtension.class)
class MutationConfigurationFactoryTest {

    private MutationConfigurationFactory underTest;

    private MockedStatic<Services> servicesMockedStatic;

    @BeforeEach
    void setUp() {
        final MutationConfigurationType mutationConfigurationType = mock(MutationConfigurationType.class);
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ModuleService.class)).thenReturn(mock(ModuleService.class));
        underTest = new MutationConfigurationFactory(mutationConfigurationType);
    }

    @Test
    void testGetId() {
        assertEquals("MutationConfiguration", underTest.getId());
    }

    @Test
    void testCreateTemplateConfiguration() {
        final Project project = mock(Project.class);
        assertNotNull(underTest.createTemplateConfiguration(project));
    }

    @Test
    void testGetOptionClass() {
        assertSame(MutationConfigurationOptions.class, underTest.getOptionsClass());
    }

    @AfterEach
    void tearDown() {
        servicesMockedStatic.close();
    }

}
