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
package com.valantic.ide.plugin.pit.search;

import com.valantic.ide.plugin.pit.services.Services;
import com.valantic.ide.plugin.pit.services.impl.ProjectService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


/**
 * created by fabian.huesig on 2022-02-01
 */
public class ProjectJavaFileSearchScope extends GlobalSearchScope {
    private ProjectFileIndex index;
    private ProjectService projectService = Services.getService(ProjectService.class);

    public ProjectJavaFileSearchScope(@Nullable Project project) {
        super(project);
        Optional.of(project)
                .map(projectService::getProjectRootManager)
                .map(ProjectRootManager::getFileIndex)
                .ifPresent(fileIndex -> this.index = fileIndex);
    }

    @Override
    public boolean isSearchInModuleContent(@NotNull Module module) {
        return false;
    }

    @Override
    public boolean isSearchInLibraries() {
        return false;
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        return this.index.isInSourceContent(file);
    }
}
