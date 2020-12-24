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
        if (WhiteLists.inWhiteList(className) || isInExcludePackages(config.getExcludePackages(), className)) {
            Log.v("StringGuard ignore: " + className);
            return createEmpty(cw);
        }
        if (isInIncludePackages(config.getIncludePackages(), className)) {
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

    private static boolean isInIncludePackages(String[] includePackages, String className) {
        if (TextUtils.isEmpty(className)) {
            return false;
        }
        if (includePackages == null || includePackages.length == 0) {
            // default we fog all packages.
            return true;
        }
        for (String pkg : includePackages) {
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
        for (String fogPackage : packages) {
            if (className.replace('/', '.').startsWith(fogPackage + ".")) {
                return true;
            }
        }
        return false;
    }
}
