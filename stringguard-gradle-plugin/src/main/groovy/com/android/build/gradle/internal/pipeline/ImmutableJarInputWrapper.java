package com.android.build.gradle.internal.pipeline;

import com.android.annotations.NonNull;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Status;

import java.io.File;
import java.util.Set;

public class ImmutableJarInputWrapper {
    public static ImmutableJarInput newJarInput(@NonNull String name,
                                                @NonNull File file,
                                                @NonNull Status status,
                                                @NonNull Set<QualifiedContent.ContentType> contentTypes,
                                                @NonNull Set<? super QualifiedContent.Scope> scopes) {
        return new ImmutableJarInput(name, file, status, contentTypes, scopes);
    }
}
