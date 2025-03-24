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
package com.valantic.ide.plugin.pit.services;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.valantic.ide.plugin.pit.services.impl.ProjectService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;


/**
 * created by fabian.huesig on 2022-02-01
 */
@ExtendWith(MockitoExtension.class)
class ServicesTest {
    private MockedStatic<ApplicationManager> applicationManagerMockedStatic;

    @BeforeEach
    void setUp() {
        applicationManagerMockedStatic = mockStatic(ApplicationManager.class);
    }

    @Test
    void testGetService() {
        final Application application = mock(Application.class);
        final ProjectService projectService = mock(ProjectService.class);
        applicationManagerMockedStatic.when(ApplicationManager::getApplication).thenReturn(application);
        when(application.getService(ProjectService.class)).thenReturn(projectService);

        assertSame(projectService, Services.getService(ProjectService.class));
    }

    @AfterEach
    void tearDown() {
        applicationManagerMockedStatic.close();
    }
}


