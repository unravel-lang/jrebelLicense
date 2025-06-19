package com.example.demo.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.security.Security;

public class BouncyCastleFeature implements Feature {
    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {


        // Register other classes as needed
    }

    @Override
    public void afterRegistration(AfterRegistrationAccess access) {

        // // TODO Auto-generated method stub

        // Feature.super.afterRegistration(access);

        System.out.println("start registration");
        RuntimeClassInitialization.initializeAtBuildTime("org.bouncycastle");
        RuntimeClassInitialization.initializeAtRunTime("org.bouncycastle.jcajce.provider.drbg.DRBG$Default");
        RuntimeClassInitialization.initializeAtRunTime("org.bouncycastle.jcajce.provider.drbg.DRBG$NonceAndIV");
        Security.addProvider(new BouncyCastleProvider());

    }
}