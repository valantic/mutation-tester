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

import com.valantic.intellij.plugin.mutation.search.ProjectJavaFileSearchScope;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

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
        return Optional.of(ProjectManager.getInstance())
                .map(ProjectManager::getOpenProjects)
                .map(Arrays::stream)
                .flatMap(Stream::findFirst)
                .orElse(null);
    }

    public ProjectRootManager getProjectRootManager(final Project project) {
        return ProjectRootManager.getInstance(project);
    }

    public GlobalSearchScope getJavaFileProjectSearchScope(final Project project) {
        return ProjectJavaFileSearchScope.projectScope(project);
    }

}
