/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.simplebuffers.quilt

import dev.lukebemish.simplebuffers.SimpleBuffersCommon
import groovy.transform.CompileStatic
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer

@CompileStatic
class SimpleBuffersQuilt implements ModInitializer {
    @Override
    void onInitialize(ModContainer mod) {
        SimpleBuffersCommon.init()
    }
}
