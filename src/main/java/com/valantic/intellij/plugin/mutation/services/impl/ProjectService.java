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
package com.valantic.intellij.plugin.mutation.services.impl;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;

import java.util.concurrent.TimeUnit;

/**
 * created by fabian.huesig on 2022-02-01
 */
@Service
public final class ProjectService {

    /**
     * gets current project
     *
     * @return
     */
    public Project getCurrentProject() {
        try {
            final DataContext[] dataContext = createDataContext();
            ReadAction.run(() -> dataContext[0] = DataManager.getInstance().getDataContextFromFocusAsync().blockingGet(5, TimeUnit.SECONDS));
            return PlatformDataKeys.PROJECT.getData(dataContext[0]);
        } catch (Exception e) {
            return ProjectManager.getInstance().getOpenProjects()[0];
        }
    }

    public ProjectRootManager getProjectRootManager(final Project project) {
        return ProjectRootManager.getInstance(project);
    }

    public GlobalSearchScope getSearchScope(final Project project) {
        return ProjectScope.getProjectScope(project);
    }

    protected DataContext[] createDataContext() {
        return new DataContext[1];
    }
}
