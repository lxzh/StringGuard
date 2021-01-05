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

/**
 * StringGuard extension.
 * <p>
 * <code>
 * stringguard {
 *     key 'Hello World'
 *     enable true
 * }
 * </code>
 *
 * @author Megatron King
 * @since 2017/3/7 17:44
 */

class StringGuardExtension {

    boolean useKey = true
    String key
    boolean enable = true
    boolean debug = false
    String[] includePackages = []
    String[] excludePackages = []
    String[] includeClasses = []
    String[] excludeClasses = []
    String implementation

    @Override
    public String toString() {
        return "StringGuardExtension{" +
                "useKey=" + useKey + ", " +
                "key=" + key + ", " +
                "enable=" + enable + ", " +
                "debug=" + debug + ", " +
                "includePackages=" + includePackages + ", " +
                "excludePackages=" + excludePackages + ", " +
                "includeClasses=" + includeClasses + ", " +
                "excludeClasses=" + excludeClasses + ", " +
                "implementation=" + implementation + "}";
    }
}
