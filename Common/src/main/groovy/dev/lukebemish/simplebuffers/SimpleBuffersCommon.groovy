/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.simplebuffers


import groovy.transform.CompileStatic
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import org.groovymc.cgl.reg.RegistrationProvider

@CompileStatic
final class SimpleBuffersCommon {

    static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, Constants.MOD_ID)
    static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(Registries.BLOCK, Constants.MOD_ID)

    private SimpleBuffersCommon() {}

    static void init() {}
}
