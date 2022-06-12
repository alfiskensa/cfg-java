package com.ubl.cfg.engine;
import soot.Scene;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.util.*;

/**
 * Copyright (c) 2018, Zhijun Yin. All rights reserved.
 */
public class PermissionFlow {

    private boolean callGraphInitialsed;
    private static final String androidJar = "C:\\Users\\alfir\\AppData\\Local\\Android\\Sdk\\platforms";
    CallGraph callGraph;
    
    InfoflowCFG infoflowCFG;

    public PermissionFlow() {
        callGraphInitialsed = false;
    }

    public void initCallGraph(String apkName) {
    	String baseFolder = "D:\\materi kuliah s2\\Semester 2\\Sistem Operasi\\tugas alfi\\SO Project\\";
        String apkFileLocation = baseFolder + apkName;
        String sourceSinks = baseFolder + "SourcesAndSinks.txt";
        
        InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
		config.getAnalysisFileConfig().setTargetAPKFile(apkFileLocation);
		config.getAnalysisFileConfig().setAndroidPlatformDir(androidJar);
		config.getAnalysisFileConfig().setSourceSinkFile(sourceSinks);
        
//        SetupApplication analyzer = new SetupApplication(androidJar, apkFileLocation);
		SetupApplication analyzer = new SetupApplication(config);

        analyzer.constructCallgraph();
        infoflowCFG = new InfoflowCFG();
        callGraph = Scene.v().getCallGraph();
        callGraphInitialsed = true;
    }

//    public static void main(String[] args) {
//        PermissionFlow analyzer = new PermissionFlow();
//        analyzer.initCallGraph("app-debug.apk");
//    }
    
    public void initCfg(String apkFileLocation) {
    	InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
		config.getAnalysisFileConfig().setTargetAPKFile(apkFileLocation);
		config.getAnalysisFileConfig().setAndroidPlatformDir(androidJar);
		
		SetupApplication analyzer = new SetupApplication(config);

        analyzer.constructCallgraph();
        infoflowCFG = new InfoflowCFG();
        callGraph = Scene.v().getCallGraph();
        callGraphInitialsed = true;
    }
}
