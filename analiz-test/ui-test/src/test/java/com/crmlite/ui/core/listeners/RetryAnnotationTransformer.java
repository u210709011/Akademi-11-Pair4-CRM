package com.crmlite.ui.core.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * {@link RetryAnalyzer}'i her test metoduna otomatik baglar; boylece her sinifta
 * tek tek {@code @Test(retryAnalyzer = ...)} yazmaya gerek kalmaz.
 */
public class RetryAnnotationTransformer implements IAnnotationTransformer {

    // NOT: TestNG'nin IAnnotationTransformer arayuzu ham (raw) tipler kullanir;
    // Class<?>/Constructor<?> yazilirsa imza cakismasi olusur ve derlenmez.
    @Override
    @SuppressWarnings("rawtypes")
    public void transform(ITestAnnotation annotation,
                          Class testClass,
                          Constructor testConstructor,
                          Method testMethod) {
        if (annotation.getRetryAnalyzerClass() == null
                || annotation.getRetryAnalyzerClass() == org.testng.internal.annotations.DisabledRetryAnalyzer.class) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}
