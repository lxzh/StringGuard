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

package com.geetest.stringguard;

/**
 * A wrapper for the real implementation of fogs.
 *
 * @author Megatron King
 * @since 2018/9/20 16:14
 */
public final class StringGuardWrapper implements IStringGuard {

    private IStringGuard mSGImpl;

    public StringGuardWrapper(String impl) {
        try {
            mSGImpl = (IStringGuard) Class.forName(impl).newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("StringGuard implementation class not found: " + impl);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("StringGuard implementation class new instance failed: "
                    + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("StringGuard implementation class access failed: "
                    + e.getMessage());
        }
    }

    @Override
    public String encrypt(String... param) {
        return mSGImpl == null ? param[0] : mSGImpl.encrypt(param);
    }

    @Override
    public String decrypt(String... param) {
        return mSGImpl == null ? param[0] : mSGImpl.decrypt(param);
    }

    @Override
    public boolean overflow(String param) {
        return mSGImpl != null && mSGImpl.overflow(param);
    }

    @Override
    public String key() {
        return mSGImpl == null ? "" : mSGImpl.key();
    }
}