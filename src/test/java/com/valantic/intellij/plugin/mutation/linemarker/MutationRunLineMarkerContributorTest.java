package com.valantic.intellij.plugin.mutation.linemarker;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;
import com.valantic.intellij.plugin.mutation.services.Services;
import com.valantic.intellij.plugin.mutation.services.impl.PsiService;
import com.valantic.intellij.plugin.mutation.services.impl.UtilService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.function.Function;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MutationRunLineMarkerContributorTest {

    private MutationRunLineMarkerContributor underTest;

    @Mock
    private UtilService utilService;
    @Mock
    private PsiService psiService;

    private MockedStatic<Services> servicesMockedStatic;

    @Before
    public void setUp() throws Exception {
        servicesMockedStatic = mockStatic(Services.class);
        servicesMockedStatic.when(() -> Services.getService(UtilService.class)).thenReturn(utilService);
        servicesMockedStatic.when(() -> Services.getService(PsiService.class)).thenReturn(psiService);
        underTest = spy(new MutationRunLineMarkerContributor());
    }

    @Test
    public void testGetInfo() {
        PsiIdentifier psiIdentifier = mock(PsiIdentifier.class);
        PsiClass psiClass = mock(PsiClass.class);
        ArgumentCaptor<Function> functionArgumentCaptor = ArgumentCaptor.forClass(Function.class);

        when(psiIdentifier.getParent()).thenReturn(psiClass);
        when(psiService.isTestClass(psiClass)).thenReturn(true);
        when(psiService.determineTargetTest(psiClass)).thenReturn("targetTest");
        when(psiService.determineTargetClass("targetTest", psiClass)).thenReturn("targetClass");

        assertNotNull(underTest.getInfo(psiIdentifier));

        verify(psiService).determineTargetTest(psiClass);
        verify(psiService).determineTargetClass("targetTest", psiClass);
        verify(underTest).getInfo(functionArgumentCaptor.capture());

        functionArgumentCaptor.getValue().apply(null);
        verify(utilService).executionMessage("run.text");
    }

    @After
    public void tearDown() throws Exception {
        servicesMockedStatic.close();
    }
}
