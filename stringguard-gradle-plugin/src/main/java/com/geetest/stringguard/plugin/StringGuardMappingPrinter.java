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

import com.geetest.stringguard.plugin.utils.Log;
import com.geetest.stringguard.plugin.utils.TextUtils;

import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Output StringGuard mapping to file.
 *
 * @author Megatron King
 * @since 2018/9/21 11:07
 */
/* package */ final class StringGuardMappingPrinter {

    private File mMappingFile;
    private BufferedWriter mWriter;

    private String mCurrentClassName;

    /* package */ StringGuardMappingPrinter(File mappingFile) {
        this.mMappingFile = mappingFile;
    }

    /* package */ void startMappingOutput() {
        try {
            if (mMappingFile.exists() && !mMappingFile.delete()) {
                throw new IOException();
            }
            File dir = mMappingFile.getParentFile();
            if (dir.exists() || dir.mkdirs()) {
                mWriter = new BufferedWriter(new FileWriter(mMappingFile));
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            Log.e("Create stringguard mapping file failed.");
        }
    }

    /* package */ void ouputInfo(String key, String implementation) {
        try {
            mWriter.write("stringguard key : " + key);
            mWriter.newLine();
            mWriter.write("stringguard impl: " + implementation);
            mWriter.newLine();
        } catch (IOException e) {
            // Ignore
        }
    }

    /* package */ void output(String className, String originValue, String encryptValue) {
        if (TextUtils.isEmpty(className)) {
            return;
        }
        try {
            if (!className.equals(mCurrentClassName)) {
                mWriter.newLine();
                mWriter.write("[" + className + "]");
                mWriter.newLine();
                mCurrentClassName = className;
            }
            mWriter.write(originValue + " -> " + encryptValue);
            mWriter.newLine();
        } catch (IOException e) {
            // Ignore
        }
    }

    /* package */ void endMappingOutput() {
        if (mWriter != null) {
            IOUtils.closeQuietly(mWriter);
        }
    }

}
