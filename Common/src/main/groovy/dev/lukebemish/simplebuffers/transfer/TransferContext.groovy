package dev.lukebemish.simplebuffers.transfer

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level

@CompileStatic
@TupleConstructor
class TransferContext {
    final Level level
    final BlockPos blockPos
    final Direction side
}
