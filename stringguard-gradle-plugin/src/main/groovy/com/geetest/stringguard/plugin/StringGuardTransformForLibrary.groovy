package com.geetest.stringguard.plugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.gradle.api.BaseVariant
import com.geetest.stringguard.plugin.utils.Log
import com.google.common.collect.ImmutableSet
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
/**
 * StringGuard transform used in library.
 *
 * @author Megatron King
 * @since 17/7/28 12:28
 */

class StringGuardTransformForLibrary extends StringGuardTransform {

    StringGuardTransformForLibrary(Project project, DomainObjectSet<BaseVariant> variants) {
        super(project, variants)
        mVariants = variants;
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return ImmutableSet.of(
                QualifiedContent.Scope.PROJECT
        )
    }

}
