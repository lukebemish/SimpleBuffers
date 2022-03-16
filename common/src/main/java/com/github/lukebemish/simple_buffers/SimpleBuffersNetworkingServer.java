package com.github.lukebemish.simple_buffers;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class SimpleBuffersNetworkingServer {
    @ExpectPlatform
    public static void init() {
        throw new AssertionError();
    }

    public static class BlockConfigUpdateMsg {
        public final int[] ints;
        public final BlockPos pos;
        public BlockConfigUpdateMsg(int[] ints, BlockPos pos) {
            this.ints = ints;
            this.pos = pos;
        }
        public void encode(FriendlyByteBuf buf) {
            buf.writeBlockPos(pos);
            buf.writeInt(ints.length);
            for (int i : ints) {
                buf.writeInt(i);
            }
        }
        public static BlockConfigUpdateMsg decode(FriendlyByteBuf buf) {
            BlockPos pos = buf.readBlockPos();
            int len = buf.readInt();
            int[] ints = new int[len];
            for (int i = 0; i < len; i++) {
                ints[i] = buf.readInt();
            }
            return new BlockConfigUpdateMsg(ints, pos);
        }
    }
}
