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

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.geetest.stringguard.plugin.utils.Log
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException
/**
 * The plugin defines some tasks.
 *
 * @author Megatron King
 * @since 2017/3/6 19:43
 */

class StringGuardPlugin implements Plugin<Project> {

    private static final String PLUGIN_NAME = 'stringguard'

    @Override
    void apply(Project project) {
        project.extensions.create(PLUGIN_NAME, StringGuardExtension)

        project.configurations {
            stringguarded
        }

        project.dependencies {
            compileOnly project.configurations.stringguarded
        }

        def android = project.extensions.android
        Log.v("StringGuardPlugin apply project:" + project + " class:" + project.getClass().toString())
        Log.v("StringGuardPlugin apply extensions:" + project.extensions)
        Log.v("StringGuardPlugin apply android:" + android)
        project.configurations.implementation.setCanBeResolved(true)
        project.configurations.each {
            Log.v("StringGuardPlugin apply configurations:" + it.name)
        }

        if (!project.stringguard.enable) {
            return
        }
        if (android instanceof AppExtension) {
            applyApplication(project, android)
        }
        if (android instanceof LibraryExtension) {
            applyLibrary(project, android)
        }

//
//        project.dependencies {
//            complieOnly project.configurations.stringguarded
//        }

        project.afterEvaluate {
            Log.setDebug(project.stringguard.debug)
        }
    }

    void applyApplication(Project project, def android) {
        android.registerTransform(new StringGuardTransformForApplication(project, android.applicationVariants))
        // throw an exception in instant run mode
        android.applicationVariants.all { variant ->
            def variantName = variant.name.capitalize()
            try {
                def instantRunTask = project.tasks.getByName("transformClassesWithInstantRunFor${variantName}")
                if (instantRunTask) {
                    throw new GradleException(
                            "StringGuard does not support instant run mode, please trigger build"
                                    + " by assemble${variantName} or disable instant run"
                                    + " in 'File->Settings...'."
                    )
                }
            } catch (UnknownTaskException e) {
                // Not in instant run mode, continue.
            }
        }
    }

    void applyLibrary(Project project, def android) {
        android.registerTransform(new StringGuardTransformForLibrary(project, android.libraryVariants))
        android.getTransformsDependencies().each{ dependencies->
            Log.v("StringGuardPlugin applyLibrary dependencies:" + dependencies)
            dependencies.each { obj->
                Log.v("StringGuardPlugin applyLibrary dependencies->obj:" + obj)
            }
        }
    }

}
