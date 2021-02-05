/*
 * Copyright (C) 2017, Megatron King
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.geetest.stringguard.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.pipeline.ImmutableJarInputWrapper
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.geetest.stringguard.plugin.utils.Log
import com.geetest.stringguard.plugin.utils.MD5
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Sets
import com.google.common.io.Files
import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.artifacts.result.ResolutionResult
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.logging.LogLevel

abstract class StringGuardTransform extends Transform {

    public static final String GUARD_CLASS_NAME = 'StringGuard'
    private static final String TRANSFORM_NAME = 'StringGuard'

    private Project mProject;
    protected DomainObjectSet<BaseVariant> mVariants;
    protected StringGuardClassInjector mInjector
    protected StringGuardMappingPrinter mMappingPrinter

    protected StringGuardExtension mExtension;

    StringGuardTransform(Project project, DomainObjectSet<BaseVariant> variants) {
        this.mProject = project
        project.afterEvaluate {
            mExtension = project.stringguard
            Log.v("StringGuardTransform " + mExtension)
            boolean useKey = mExtension.getGuardMode()==0
            String key = mExtension.getKey()
            String implementation = mExtension.getImplementation()
            if (useKey) {
                if (key == null || key.length() == 0) {
                    throw new IllegalArgumentException("Missing stringguard key config")
                }
            }

            if (implementation == null || implementation.length() == 0) {
                throw new IllegalArgumentException("Missing stringguard implementation config")
            }
            Log.v("StringGuardTransform afterEvaluate variants:" + variants.toString())
            variants.all { variant->
                Log.v("StringGuardTransform afterEvaluate variant:" + variant.name)
            }

            if (mExtension.enable) {
                def applicationId = variants.first().applicationId
                def manifestFile = project.file("src/main/AndroidManifest.xml")
                if (manifestFile.exists()) {
                    def parsedManifest = new XmlParser().parse(
                            new InputStreamReader(new FileInputStream(manifestFile), "utf-8"))
                    if (parsedManifest != null) {
                        def packageName = parsedManifest.attribute("package")
                        if (packageName != null) {
                            applicationId = packageName
                        }
                    }
                }
                createGuardClass(project, mExtension, variants, applicationId)
            } else {
                mMappingPrinter = null
                mInjector = null
            }
        }
    }

    void createGuardClass(def project, StringGuardExtension extConfig,
                          DomainObjectSet<BaseVariant> variants, def applicationId) {
        variants.all { variant ->
            def variantName = variant.name.toUpperCase()[0] + variant.name.substring(1)
            Task generateTask = project.tasks.findByName(variantName)
            if (generateTask == null) {
                generateTask = project.tasks.create("generate${variantName}StringGuard", DefaultTask)

                def sgDir = new File(project.buildDir, "generated" +
                        File.separatorChar + "source" + File.separatorChar + "stringguard" + File.separatorChar + variant.name)
                def sgFile = new File(sgDir, applicationId.replace((char) '.', File.separatorChar) + File.separator + "StringGuard.java")
                variant.registerJavaGeneratingTask(generateTask, sgDir)

                generateTask.doLast {
                    mMappingPrinter = new StringGuardMappingPrinter(
                            new File(project.buildDir, "outputs/mapping/${variant.name.toLowerCase()}/stringguard.txt"))
                    // Create class injector
                    mInjector = new StringGuardClassInjector(
                            extConfig.enable,
                            extConfig.guardMode,
                            extConfig.key,
                            extConfig.debug,
                            extConfig.includePackages, extConfig.excludePackages,
                            extConfig.includeClasses, extConfig.excludeClasses,
                            extConfig.implementation,
                            applicationId + "." + GUARD_CLASS_NAME, mMappingPrinter)

                    WhiteLists.printWhiteList("createGuardClass");

                    // Generate StringGuard.java
                    StringGuardClassGenerator.generate(sgFile, applicationId, GUARD_CLASS_NAME,
                            extConfig.implementation)
                }
            }
        }
    }

    @Override
    String getName() {
        return TRANSFORM_NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return ImmutableSet.of(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return null
    }

    @Override
    boolean isIncremental() {
        return true
    }

    private List<String> getArtifactsForConfiguration(Configuration configuration) {
        List<String> relativeLibs = new ArrayList<>();
        if (configuration.isCanBeResolved()) {
            ResolvableDependencies incoming = configuration.getIncoming();
            ResolutionResult resolutionResult = incoming.getResolutionResult();
            Set<ResolvedComponentResult> components = resolutionResult.getAllComponents();
            for (ResolvedComponentResult result : components) {
                ModuleVersionIdentifier identifier = result.getModuleVersion();
                if (identifier != null) {
                    relativeLibs.add(String.join(":", identifier.getGroup(), identifier.getName(), identifier.getVersion()));
                } else {
                    relativeLibs.add("" + identifier);
                }
            }
        }
        return relativeLibs;
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        Log.v("StringGuardTransform " + mExtension)

        def dirInputs = new HashSet<>()
        def jarInputs = new HashSet<>()

        if (!transformInvocation.isIncremental()) {
            transformInvocation.getOutputProvider().deleteAll()
        }

        // 配置日志等级
        transformInvocation.getContext().logging.level = LogLevel.DEBUG;

        def variantName = transformInvocation.getContext().variantName;

        Log.v("transform start")
        Log.v("transform variantName:" + variantName)
        Log.v("transform transformInvocation:" + transformInvocation)
        Log.v("transform transformInvocation Inputs:" + transformInvocation.getInputs())
        // Collecting inputs.
        transformInvocation.inputs.each { input ->
            Log.v("transform input:" + input)
            input.directoryInputs.each { dirInput ->
                Log.v("transform dirInput:" + dirInput)
                dirInputs.add(dirInput)
            }
            input.jarInputs.each { jarInput ->
                Log.v("transform jarInput:" + jarInput)
                jarInputs.add(jarInput)
            }
        }


        if (mMappingPrinter != null) {
            mMappingPrinter.startMappingOutput()
            mMappingPrinter.ouputInfo(mExtension.getKey(), mExtension.getImplementation())
        }

        Configuration stringguarded
        try {
            stringguarded = mProject.configurations.getByName("stringguarded")
        } catch(Exception e) {
        }
        if (stringguarded != null) {
            stringguarded.getIncoming().getFiles().each { file ->
                Log.v("incoming file it:" + file)
                if (file.name.endsWith(".jar")) {
                    String jarHash = getUniqueHashName(file)
                    String fileName = file.getName()
                    String jarIDName = "android.local.jars:jetified-" + fileName + ":" + jarHash.substring(jarHash.indexOf("_") + 1)
                    Log.v("UniqueHashName:" + jarHash)
                    Object obj = ImmutableJarInputWrapper.newJarInput(jarIDName, file, Status.NOTCHANGED, TransformManager.CONTENT_CLASS,
                            Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT, QualifiedContent.Scope.SUB_PROJECTS))
                    Log.v("additional added input:" + obj)
                    jarInputs.add(obj)
                }
            }
        }

        Log.v("transform mExtension:" + mExtension)
        WhiteLists.addWhiteList(WhiteLists.shortClassName(mExtension.getImplementation()))
        WhiteLists.printWhiteList("transform");

        if (!dirInputs.isEmpty() || !jarInputs.isEmpty()) {
            File dirOutput = transformInvocation.outputProvider.getContentLocation(
                    "classes", getOutputTypes(), getScopes(), Format.DIRECTORY)
            FileUtils.mkdirs(dirOutput)
            if (!dirInputs.isEmpty()) {
                dirInputs.each { dirInput ->
                    if (transformInvocation.incremental) {
                        dirInput.changedFiles.each { entry ->
                            File fileInput = entry.getKey()
                            File fileOutput = new File(fileInput.getAbsolutePath().replace(
                                    dirInput.file.getAbsolutePath(), dirOutput.getAbsolutePath()))
                            FileUtils.mkdirs(fileOutput.parentFile)
                            Status fileStatus = entry.getValue()
                            switch (fileStatus) {
                                case Status.ADDED:
                                case Status.CHANGED:
                                    if (fileInput.isDirectory()) {
                                        return // continue.
                                    }
                                    if (mInjector != null && fileInput.getName().endsWith('.class')) {
                                        mInjector.doFog2Class(fileInput, fileOutput)
                                    } else {
                                        Files.copy(fileInput, fileOutput)
                                    }
                                    break
                                case Status.REMOVED:
                                    if (fileOutput.exists()) {
                                        if (fileOutput.isDirectory()) {
                                            fileOutput.deleteDir()
                                        } else {
                                            fileOutput.delete()
                                        }
                                    }
                                    break
                            }
                        }
                    } else {
                        dirInput.file.traverse(type: FileType.FILES) { fileInput ->
                            File fileOutput = new File(fileInput.getAbsolutePath().replace(dirInput.file.getAbsolutePath(), dirOutput.getAbsolutePath()))
                            FileUtils.mkdirs(fileOutput.parentFile)
                            if (mInjector != null && fileInput.getName().endsWith('.class')) {
                                mInjector.doFog2Class(fileInput, fileOutput)
                            } else {
                                Files.copy(fileInput, fileOutput)
                            }
                        }
                    }
                }
            }

            if (!jarInputs.isEmpty()) {
                jarInputs.each { jarInput ->
                    File jarInputFile = jarInput.file
                    File jarOutputFile = transformInvocation.outputProvider.getContentLocation(
                            getUniqueHashName(jarInputFile), getOutputTypes(), getScopes(), Format.JAR
                    )
                    Log.v("transform jarInput:" + jarInput + " jarOutputFile:" + jarOutputFile)

                    FileUtils.mkdirs(jarOutputFile.parentFile)

                    switch (jarInput.status) {
                        case Status.NOTCHANGED:
                            if (transformInvocation.incremental) {
                                break
                            }
                        case Status.ADDED:
                        case Status.CHANGED:
                            if (mInjector != null) {
                                mInjector.doFog2Jar(jarInputFile, jarOutputFile)
                            } else {
                                Files.copy(jarInputFile, jarOutputFile)
                            }
                            break
                        case Status.REMOVED:
                            if (jarOutputFile.exists()) {
                                jarOutputFile.delete()
                            }
                            break
                    }
                }
            }
        }

        if (mMappingPrinter != null) {
            mMappingPrinter.endMappingOutput()
        }
    }

    String getUniqueHashName(File fileInput) {
        final String fileInputName = fileInput.getName()
        if (fileInput.isDirectory()) {
            return fileInputName
        }
        final String parentDirPath = fileInput.getParentFile().getAbsolutePath()
        final String pathMD5 = MD5.getMessageDigest(parentDirPath.getBytes())
        final int extSepPos = fileInputName.lastIndexOf('.')
        final String fileInputNamePrefix =
                (extSepPos >= 0 ? fileInputName.substring(0, extSepPos) : fileInputName)
        return fileInputNamePrefix + '_' + pathMD5
    }

}