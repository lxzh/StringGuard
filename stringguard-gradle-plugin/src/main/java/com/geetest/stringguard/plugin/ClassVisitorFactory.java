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
package com.geetest.stringguard.plugin;

import com.geetest.stringguard.IStringGuard;
import com.geetest.stringguard.plugin.utils.Log;
import com.geetest.stringguard.plugin.utils.TextUtils;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * A factory creates {@link ClassVisitor}.
 *
 * @author Megatron King
 * @since 2017/3/7 19:56
 */

public final class ClassVisitorFactory {

    private ClassVisitorFactory() {
    }

    public static ClassVisitor create(IStringGuard sgImpl, StringGuardMappingPrinter mappingPrinter, Config config, String guardClassName,
                                      String className, ClassWriter cw) {
        Log.v("ClassVisitorFactory create className: " + className);
        if (WhiteLists.inWhiteList(className) || isInExcludePackages(config.getExcludePackages(), className) || isInExcludeClasses(config.getExcludeClasses(), className)) {
            Log.v("StringGuard ignore: " + className);
            return createEmpty(cw);
        }
        if (isInIncludePackages(config.getIncludePackages(), className) || isInIncludeClasses(config.getIncludeClasses(), className)) {
            Log.v("StringGuard include: " + className);
            return new StringGuardClassVisitor(sgImpl, mappingPrinter, guardClassName, config.isUseKey(), config.getKey(), cw);
        }
        Log.v("StringGuard execute: " + className);
        return createEmpty(cw);
    }

    private static ClassVisitor createEmpty(ClassWriter cw) {
        return new ClassVisitor(Opcodes.ASM5, cw) {
        };
    }

    private static boolean isInIncludePackages(String[] packages, String className) {
        if (TextUtils.isEmpty(className)) {
            return false;
        }
        if (packages == null || packages.length == 0) {
            // default we fog all packages.
            return true;
        }
        for (String pkg : packages) {
            if (className.replace('/', '.').startsWith(pkg + ".")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInExcludePackages(String[] packages, String className) {
        if (TextUtils.isEmpty(className)) {
            return false;
        }
        if (packages == null || packages.length == 0) {
            // default we fog all packages.
            return false;
        }
        for (String pkg : packages) {
            if (className.replace('/', '.').startsWith(pkg + ".")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInIncludeClasses(String[] classes, String className) {
        if (TextUtils.isEmpty(className)) {
            return false;
        }
        if (classes == null || classes.length == 0) {
            // default we fog all classes.
            return true;
        }
        for (String clazz : classes) {
            if (className.replace('/', '.').equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInExcludeClasses(String[] classes, String className) {
        if (TextUtils.isEmpty(className)) {
            return false;
        }
        if (classes == null || classes.length == 0) {
            // default we fog all classes.
            return false;
        }
        for (String clazz : classes) {
            if (className.replace('/', '.').equals(clazz)) {
                return true;
            }
        }
        return false;
    }
}
