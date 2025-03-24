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
package com.valantic.ide.plugin.pit.services.impl;

import com.intellij.codeInsight.TestFrameworks;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.ThrowableRunnable;
import com.valantic.ide.plugin.pit.search.ProjectJavaFileSearchScope;
import com.valantic.ide.plugin.pit.services.Services;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * created by fabian.huesig on 2022-02-01
 */
@ExtendWith(MockitoExtension.class)
class PsiServiceTest {

    private PsiService underTest;

    @Mock
    private ClassNameService classNameService;
    @Mock
    private ModuleService moduleService;
    @Mock
    private ProjectService projectService;

    private MockedStatic<Services> servicesMockedStatic;
    private MockedStatic<TestFrameworks> testFrameworksMockedStatic;
    private MockedStatic<JavaPsiFacade> javaPsiFacadeMockedStatic;

    @BeforeEach
    void setUp() {
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(ClassNameService.class)).thenReturn(classNameService);
        servicesMockedStatic.when(() -> Services.getService(ModuleService.class)).thenReturn(moduleService);
        servicesMockedStatic.when(() -> Services.getService(ProjectService.class)).thenReturn(projectService);
        testFrameworksMockedStatic = mockStatic(TestFrameworks.class);
        javaPsiFacadeMockedStatic = mockStatic(JavaPsiFacade.class);
        underTest = spy(PsiService.class);
    }


    @Test
    void testDoesClassExists_processClassNames_equalsClassName() {
        final String fullyQualifiedClassName = "fullyQualifiedClassName";
        final Project project = mock(Project.class);
        final GlobalSearchScope searchScope = mock(GlobalSearchScope.class);
        final ArgumentCaptor<Processor<String>> processorArgumentCaptor = ArgumentCaptor.forClass(Processor.class);

        when(projectService.getCurrentProject()).thenReturn(project);
        when(projectService.getSearchScope(project)).thenReturn(searchScope);

        final boolean result = underTest.doesClassExists(fullyQualifiedClassName);

        verify(classNameService).processClassNames(eq(project), eq(searchScope), processorArgumentCaptor.capture());
        assertFalse(processorArgumentCaptor.getValue().process("fullyQualifiedClassName"));
        assertTrue(processorArgumentCaptor.getValue().process("anyOtherGivenName"));
        assertFalse(result);
    }

    @Test
    void testDoesClassExists_processClassNames_notEqualsClassName() {
        final String fullyQualifiedClassName = "package.notAFittingClassName";
        final Project project = mock(Project.class);
        final GlobalSearchScope searchScope = mock(GlobalSearchScope.class);
        final ArgumentCaptor<Processor<String>> processorArgumentCaptor = ArgumentCaptor.forClass(Processor.class);

        when(projectService.getCurrentProject()).thenReturn(project);
        when(projectService.getSearchScope(project)).thenReturn(searchScope);
        doReturn("notTheExpectedClassName").when(underTest).getClassName(fullyQualifiedClassName);

        final boolean result = underTest.doesClassExists(fullyQualifiedClassName);

        verify(classNameService).processClassNames(eq(project), eq(searchScope), processorArgumentCaptor.capture());
        assertTrue(processorArgumentCaptor.getValue().process("notAFittingClassName"));
        assertTrue(processorArgumentCaptor.getValue().process("anyOtherGivenName"));
        assertFalse(result);
    }

    @Test
    void testIsTestClass_shouldBeTrue() {
        final PsiClass psiClass = mock(PsiClass.class);
        final TestFrameworks testFrameworks = mock(TestFrameworks.class);

        testFrameworksMockedStatic.when(() -> TestFrameworks.getInstance()).thenReturn(testFrameworks);
        when(testFrameworks.isTestClass(psiClass)).thenReturn(true);

        assertTrue(underTest.isTestClass(psiClass));
        testFrameworksMockedStatic.verify(() -> TestFrameworks.getInstance());
    }

    @Test
    void testIsTestClass_shouldBeFalse() {
        final PsiClass psiClass = mock(PsiClass.class);
        final TestFrameworks testFrameworks = mock(TestFrameworks.class);

        testFrameworksMockedStatic.when(() -> TestFrameworks.getInstance()).thenReturn(testFrameworks);
        when(testFrameworks.isTestClass(psiClass)).thenReturn(false);

        assertFalse(underTest.isTestClass(psiClass));
        testFrameworksMockedStatic.verify(() -> TestFrameworks.getInstance());
    }

    @Test
    void testGetClassName_isClassName() {
        final String fullyQualifiedClassName = "fullyQualifiedClassName";
        assertEquals("fullyQualifiedClassName", underTest.getClassName(fullyQualifiedClassName));
    }

    @Test
    void testGetClassName_isPackage() {
        final String fullyQualifiedClassName = "fully.qualified.ClassName";
        assertEquals("ClassName", underTest.getClassName(fullyQualifiedClassName));
    }


    @Test
    void testgetPsiFile_className() {
        final Project project = mock(Project.class);
        final String qualifiedName = "ClassName";
        final JavaPsiFacade javaPsiFacade = mock(JavaPsiFacade.class);
        final PsiClass psiClass = mock(PsiClass.class);
        final PsiFile psiFile = mock(PsiFile.class);

        when(projectService.getCurrentProject()).thenReturn(project);
        javaPsiFacadeMockedStatic.when(() -> JavaPsiFacade.getInstance(project)).thenReturn(javaPsiFacade);
        when(javaPsiFacade.findClass(eq("ClassName"), any(ProjectJavaFileSearchScope.class))).thenReturn(psiClass);
        when(psiClass.getContainingFile()).thenReturn(psiFile);

        final PsiFile result = underTest.getPsiFile(qualifiedName);

        assertSame(psiFile, result);
    }

    @Test
    void testUpdateModule_wildcardPackage_packageFound() throws Throwable {
        final Project project = mock(Project.class);
        final String qualifiedName = "var.package.*";
        final JavaPsiFacade javaPsiFacade = mock(JavaPsiFacade.class);
        final PsiPackage psiPackage = mock(PsiPackage.class);
        final PsiClass psiClass = mock(PsiClass.class);
        final PsiFile psiFile = mock(PsiFile.class);

        when(projectService.getCurrentProject()).thenReturn(project);
        javaPsiFacadeMockedStatic.when(() -> JavaPsiFacade.getInstance(project)).thenReturn(javaPsiFacade);
        when(javaPsiFacade.findPackage("var.package")).thenReturn(psiPackage);
        when(psiPackage.getClasses()).thenReturn(new PsiClass[]{psiClass});
        when(psiClass.getContainingFile()).thenReturn(psiFile);

        final PsiFile result = underTest.getPsiFile(qualifiedName);

        assertSame(psiFile, result);
    }

    @Test
    void testUpdateModule_wildcardPackage_foundInSubpackages() throws Throwable {
        final Project project = mock(Project.class);
        final String qualifiedName = "var.package.*";
        final JavaPsiFacade javaPsiFacade = mock(JavaPsiFacade.class);
        final PsiPackage psiPackage = mock(PsiPackage.class);
        final PsiPackage subPackage = mock(PsiPackage.class);
        final PsiClass psiClass = mock(PsiClass.class);
        final PsiFile psiFile = mock(PsiFile.class);

        when(projectService.getCurrentProject()).thenReturn(project);
        javaPsiFacadeMockedStatic.when(() -> JavaPsiFacade.getInstance(project)).thenReturn(javaPsiFacade);
        when(javaPsiFacade.findPackage("var.package")).thenReturn(psiPackage);
        when(psiPackage.getClasses()).thenReturn(new PsiClass[]{});
        when(psiPackage.getSubPackages()).thenReturn(new PsiPackage[]{subPackage});
        when(subPackage.getClasses()).thenReturn(new PsiClass[]{psiClass});
        when(psiClass.getContainingFile()).thenReturn(psiFile);

        final PsiFile result = underTest.getPsiFile(qualifiedName);

        assertSame(psiFile, result);
    }

    @Test
    void testUpdateModule_wildcardPackage_packageDoesNotExists() throws Throwable {
        final Project project = mock(Project.class);
        final String qualifiedName = "var.package.not.existing.*";
        final JavaRunConfigurationModule configurationModule = mock(JavaRunConfigurationModule.class);
        final ArgumentCaptor<ThrowableRunnable> throwableRunnableArgumentCaptor = ArgumentCaptor.forClass(ThrowableRunnable.class);
        final JavaPsiFacade javaPsiFacade = mock(JavaPsiFacade.class);

        when(projectService.getCurrentProject()).thenReturn(project);
        javaPsiFacadeMockedStatic.when(() -> JavaPsiFacade.getInstance(project)).thenReturn(javaPsiFacade);
        when(javaPsiFacade.findPackage("var.package.not.existing")).thenReturn(null);

        final PsiFile result = underTest.getPsiFile(qualifiedName);

        assertSame(null, result);
    }

    @Test
    void testDetermineTargetTest_classExists() {
        final PsiClass psiClass = mock(PsiClass.class);

        when(psiClass.getQualifiedName()).thenReturn("qualifiedName");
        doReturn(true).when(underTest).doesClassExists("qualifiedName");

        assertEquals("qualifiedName", underTest.determineTargetTest(psiClass));
    }

    @Test
    void testDetermineTargetTest_classDoesNotExistsExists() {
        final PsiClass psiClass = mock(PsiClass.class);

        when(psiClass.getQualifiedName()).thenReturn("qualifiedName");
        doReturn(false).when(underTest).doesClassExists("qualifiedName");

        assertEquals(StringUtils.EMPTY, underTest.determineTargetTest(psiClass));
    }

    @Test
    void testDetermineTargetClass_classExists() {
        final String targetTest = "targetClassTest";
        final PsiClass psiClass = mock(PsiClass.class);

        doReturn(true).when(underTest).doesClassExists("targetClass");

        assertEquals("targetClass", underTest.determineTargetClass(targetTest, psiClass));

        final String targetTestWithPrefix = "TestTargetClass";

        doReturn(true).when(underTest).doesClassExists("TargetClass");

        assertEquals("TargetClass", underTest.determineTargetClass(targetTestWithPrefix, psiClass));
    }

    @Test
    void testDetermineTargetClass_classDoesNotExists_usePackage() {
        final String targetTest = "targetClassTest";
        final PsiClass psiClass = mock(PsiClass.class);
        final PsiJavaFile psiJavaFile = mock(PsiJavaFile.class);

        doReturn(false).when(underTest).doesClassExists("targetClass");
        when(psiClass.getContainingFile()).thenReturn(psiJavaFile);
        when(psiJavaFile.getPackageName()).thenReturn("packageName");

        assertEquals("packageName.*", underTest.determineTargetClass(targetTest, psiClass));
    }

    @Test
    void testDetermineTargetClass_classDoesNotExists_notAJavaDir() {
        final String targetTest = "targetClassTest";
        final PsiClass psiClass = mock(PsiClass.class);
        final PsiFile psiFile = mock(PsiFile.class);


        doReturn(false).when(underTest).doesClassExists("targetClass");
        when(psiClass.getContainingFile()).thenReturn(psiFile);

        assertEquals(StringUtils.EMPTY, underTest.determineTargetClass(targetTest, psiClass));
    }

    @Test
    void testResolvePackageNameForDir_ChildIsClass() {
        final PsiJavaDirectoryImpl dir = mock(PsiJavaDirectoryImpl.class);
        final PsiClass psiClass = mock(PsiClass.class);
        final PsiJavaFile psiJavaFile = mock(PsiJavaFile.class);

        when(dir.getName()).thenReturn("dirName");
        when(dir.getChildren()).thenReturn(new PsiElement[]{psiClass});
        when(psiClass.getContainingFile()).thenReturn(psiJavaFile);
        when(psiJavaFile.getPackageName()).thenReturn("var.package.dirName");

        assertEquals("var.package.dirName", underTest.resolvePackageNameForDir(dir));
    }

    @Test
    void testResolvePackageNameForDir_recursiveChildIsClass() {
        final PsiJavaDirectoryImpl dir = mock(PsiJavaDirectoryImpl.class);
        final PsiJavaDirectoryImpl subDir = mock(PsiJavaDirectoryImpl.class);
        final PsiClass psiClass = mock(PsiClass.class);
        final PsiJavaFile psiJavaFile = mock(PsiJavaFile.class);

        when(dir.getName()).thenReturn("dirName");
        when(dir.getChildren()).thenReturn(new PsiElement[]{subDir});
        when(subDir.getChildren()).thenReturn(new PsiElement[]{psiClass});
        when(psiClass.getContainingFile()).thenReturn(psiJavaFile);
        when(psiJavaFile.getPackageName()).thenReturn("var.package.dirName.subDir");

        assertEquals("var.package.dirName", underTest.resolvePackageNameForDir(dir));
    }

    @Test
    void testResolvePackageNameForDir_notAJavaFile_shouldReturnEmptyString() {
        final PsiJavaDirectoryImpl dir = mock(PsiJavaDirectoryImpl.class);
        final PsiJavaDirectoryImpl subDir = mock(PsiJavaDirectoryImpl.class);
        final PsiClass psiClass = mock(PsiClass.class);
        final PsiFile psiFile = mock(PsiFile.class);

        when(dir.getName()).thenReturn("dirName");
        when(dir.getChildren()).thenReturn(new PsiElement[]{subDir});
        when(subDir.getChildren()).thenReturn(new PsiElement[]{psiClass});
        when(psiClass.getContainingFile()).thenReturn(psiFile);

        assertEquals(StringUtils.EMPTY, underTest.resolvePackageNameForDir(dir));
    }

    @AfterEach
    void tearDown() {
        servicesMockedStatic.close();
        testFrameworksMockedStatic.close();
        javaPsiFacadeMockedStatic.close();
    }

}
