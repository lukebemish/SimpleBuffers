/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.simplebuffers.forge

import dev.lukebemish.simplebuffers.Constants
import dev.lukebemish.simplebuffers.SimpleBuffersCommon
import groovy.transform.CompileStatic
import org.groovymc.gml.GMod

@GMod(Constants.MOD_ID)
@CompileStatic
class SimpleBuffersForge {
    SimpleBuffersForge() {
        SimpleBuffersCommon.init()
    }
}
