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

import com.intellij.codeInsight.TestFrameworks;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.valantic.intellij.plugin.mutation.enums.MutationConstants;
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
        classNameService.processClassNames(project, projectService.getSearchScope(project), name -> {
            if (getClassName(fullyQualifiedClassName).equals(name)) {
                exists[0] = true;
                return false;
            }
            return true;

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
                .filter(className -> className.contains(MutationConstants.PACKAGE_SEPARATOR.getValue()))
                .map(className -> StringUtils.substringAfterLast(className, MutationConstants.PACKAGE_SEPARATOR.getValue()))
                .orElse(fullyQualifiedClassName);
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
                .map(testClassName -> StringUtils.removeEnd(testClassName, MutationConstants.TEST_CLASS_SUFFIX.getValue()))
                .map(testClassName -> StringUtils.removeStart(testClassName, MutationConstants.TEST_CLASS_SUFFIX.getValue()))
                .filter(this::doesClassExists)
                .orElseGet(() -> Optional.of(psiClass)
                        .map(PsiClass::getContainingFile)
                        .filter(PsiJavaFile.class::isInstance)
                        .map(PsiJavaFile.class::cast)
                        .map(PsiJavaFile::getPackageName)
                        .map(packageName -> packageName + MutationConstants.PACKAGE_WILDCARD_SUFFIX.getValue())
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
        if (StringUtils.isNotEmpty(classPackageName) && !classPackageName.endsWith(MutationConstants.PACKAGE_SEPARATOR.getValue() + dirName)) {
            String basePackageName = classPackageName.split(MutationConstants.PACKAGE_SEPARATOR.getValue() + dirName + MutationConstants.PACKAGE_SEPARATOR.getValue())[0];
            return basePackageName + MutationConstants.PACKAGE_SEPARATOR.getValue() + dirName;
        }
        return classPackageName;
    }

    /**
     * get PsiFile for qualified name in the current project.
     *
     * @param qualifiedName
     * @return PsiClass
     */
    public PsiFile getPsiFile(final String qualifiedName) {
        final Project project = projectService.getCurrentProject();
        PsiClass psiClass = null;
        if (qualifiedName.endsWith(MutationConstants.PACKAGE_SEPARATOR.getValue() + MutationConstants.WILDCARD_SUFFIX.getValue())) {
            final String packageName = qualifiedName.split(MutationConstants.WILDCARD_SUFFIX_REGEX.getValue())[0];
            PsiPackage psiPackage = getJavaPsiFacade().findPackage(packageName);
            if (psiPackage != null) {
                psiClass = findPsiClassInPackage(psiPackage);
            }
        }
        if (psiClass == null) {
            psiClass = getJavaPsiFacade().findClass(qualifiedName, new ProjectJavaFileSearchScope(project));
        }
        return psiClass != null ? psiClass.getContainingFile() : null;
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
     * recursive call to find first class of psiPackage.getClasses.
     * If empty make recursive call with subpackages.
     *
     * @param psiPackage
     * @return
     */
    private PsiClass findPsiClassInPackage(final PsiPackage psiPackage) {
        return Arrays.stream(psiPackage.getClasses())
                .findFirst()
                .orElseGet(() -> Arrays.stream(psiPackage.getSubPackages())
                        .map(this::findPsiClassInPackage)
                        .findFirst()
                        .orElse(null));

    }

    /**
     * get JavaPsiFacade
     *
     * @return
     */
    private JavaPsiFacade getJavaPsiFacade() {
        return JavaPsiFacade.getInstance(projectService.getCurrentProject());
    }

}
