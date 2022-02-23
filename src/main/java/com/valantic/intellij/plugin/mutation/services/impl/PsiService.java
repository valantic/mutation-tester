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
package com.valantic.intellij.plugin.mutation.services.impl;

import com.intellij.codeInsight.TestFrameworks;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.valantic.intellij.plugin.mutation.constants.MutationConstants;
import com.valantic.intellij.plugin.mutation.search.ProjectJavaFileSearchScope;
import com.valantic.intellij.plugin.mutation.services.Services;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;


/**
 * created by fabian.huesig on 2022-02-01
 */
@Service
public final class PsiService {

    private ClassNameService classNameService = Services.getService(ClassNameService.class);
    private ModuleService moduleService = Services.getService(ModuleService.class);
    private UtilService utilService = Services.getService(UtilService.class);
    private ProjectService projectService = Services.getService(ProjectService.class);

    /**
     * checks if class exists with the provided class name.
     *
     * @param fullyQualifiedClassName
     * @return does class exists
     */
    public boolean doesClassExists(final String fullyQualifiedClassName) {
        final boolean[] exists = new boolean[1];
        final Project project = projectService.getCurrentProject();
        classNameService.processClassNames(project, projectService.getJavaFileProjectSearchScope(project), name -> {
            if (getClassName(fullyQualifiedClassName).equals(name)) {
                exists[0] = Boolean.TRUE;
                return Boolean.FALSE;
            }
            return Boolean.TRUE;

        });
        return exists[0];
    }

    /**
     * check if provided psiClass is a test class
     *
     * @param psiClass
     * @return is test class
     */
    public boolean isTestClass(PsiClass psiClass) {
        return TestFrameworks.getInstance().isTestClass(psiClass);
    }

    /**
     * get class name and removes packages from name
     *
     * @return class name
     */
    public String getClassName(final String fullyQualifiedClassName) {
        return Optional.of(fullyQualifiedClassName)
                .filter(className -> className.contains(MutationConstants.PACKAGE_SEPARATOR))
                .map(className -> StringUtils.substringAfterLast(className, MutationConstants.PACKAGE_SEPARATOR))
                .orElse(fullyQualifiedClassName);
    }

    /**
     * updates the module needed for java command line state.
     * This can change in a multi module project depending of the used module.
     * Determines the correct moule based by package of the test
     *
     * @param project
     */
    public void updateModule(final Project project, final String qualifiedName, final JavaRunConfigurationModule configurationModule) {
        utilService.allowSlowOperations(() -> Optional.of(JavaPsiFacade.getInstance(project))
                .map(javaPsiFacade -> getPsiClass(javaPsiFacade, qualifiedName, project))
                .map(PsiClass::getContainingFile)
                .map(moduleService::findModule)
                .ifPresent(module -> configurationModule.setModule(module)));
    }

    /**
     * either it is a list of classes, a wildcard package or a single test class,
     * this method will return the base package of the primary used test.
     *
     * @param psiClass
     * @return
     */
    public String determineTargetTest(final PsiClass psiClass) {
        return Optional.of(psiClass)
                .map(PsiClass::getQualifiedName)
                .filter(this::doesClassExists)
                .orElse(StringUtils.EMPTY);
    }

    /**
     * get target class name based in provided string targetTest.
     * If class does not exists, package will be returned based on psiClass.
     *
     * @param targetTest
     * @param psiClass
     * @return targetclass with fully package or package blob
     */
    public String determineTargetClass(final String targetTest, final PsiClass psiClass) {
        return Optional.ofNullable(targetTest)
                .map(testClassName -> StringUtils.removeEnd(testClassName, MutationConstants.TEST_CLASS_SUFFIX))
                .filter(this::doesClassExists)
                .orElseGet(() -> Optional.of(psiClass)
                        .map(PsiClass::getContainingFile)
                        .filter(PsiJavaFile.class::isInstance)
                        .map(PsiJavaFile.class::cast)
                        .map(PsiJavaFile::getPackageName)
                        .map(packageName -> packageName + MutationConstants.PACKAGE_WILDCARD_SUFFIX)
                        .orElse(StringUtils.EMPTY));
    }

    /**
     * resolve package name for given PsiJavaDirectory
     *
     * @param dir
     * @return
     */
    public String resolvePackageNameForDir(PsiJavaDirectoryImpl dir) {
        final String dirName = dir.getName();
        String classPackageName = Optional.of(dir.getChildren())
                .map(this::getAnyChildPsiClass)
                .map(PsiElement::getContainingFile)
                .filter(PsiJavaFile.class::isInstance)
                .map(PsiJavaFile.class::cast)
                .map(PsiJavaFile::getPackageName)
                .orElse(StringUtils.EMPTY);
        if (StringUtils.isNotEmpty(classPackageName) && !classPackageName.endsWith(MutationConstants.PACKAGE_SEPARATOR + dirName)) {
            String basePackageName = classPackageName.split(MutationConstants.PACKAGE_SEPARATOR + dirName + MutationConstants.PACKAGE_SEPARATOR)[0];
            return basePackageName + MutationConstants.PACKAGE_SEPARATOR + dirName;
        }
        return classPackageName;
    }

    /**
     * get recurisvly first child which is a PsiClass
     *
     * @param children
     * @return
     */
    private PsiClass getAnyChildPsiClass(PsiElement[] children) {
        final PsiClass[] psiClass = new PsiClass[1];
        Optional<PsiClass> optionalPsiClass = Arrays.stream(children)
                .filter(PsiClass.class::isInstance)
                .map(PsiClass.class::cast)
                .findFirst();
        if (!optionalPsiClass.isPresent()) {
            Arrays.stream(children).forEach(child -> {
                if (ArrayUtils.isNotEmpty(child.getChildren()))
                    psiClass[0] = getAnyChildPsiClass(child.getChildren());
                if (psiClass[0] != null) {
                    return;
                }
            });
        } else {
            psiClass[0] = optionalPsiClass.get();
        }
        return psiClass[0];
    }

    /**
     * get PsiClass for qualified name in the given project.
     *
     * @param javaPsiFacade
     * @param qualifiedName
     * @param project
     * @return PsiClass
     */
    private PsiClass getPsiClass(final JavaPsiFacade javaPsiFacade, final String qualifiedName, final Project project) {
        if (qualifiedName.endsWith(MutationConstants.PACKAGE_SEPARATOR + MutationConstants.WILDCARD_SUFFIX)) {
            PsiPackage psiPackage = Optional.of(qualifiedName.split(MutationConstants.WILDCARD_SUFFIX_REGEX)[0])
                    .map(javaPsiFacade::findPackage)
                    .orElse(null);
            if (psiPackage != null) {
                return Arrays.stream(psiPackage.getClasses()).findAny().orElse(null);
            }
        }
        return javaPsiFacade.findClass(qualifiedName, new ProjectJavaFileSearchScope(project));
    }

}
