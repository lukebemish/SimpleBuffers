package simplebuffers;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import simplebuffers.blocks.entities.ItemBufferBlockEntity;
import simplebuffers.util.RelativeSide;

public class PlatformExtensionUtil {
    @ExpectPlatform
    public static ItemBufferBlockEntity getItemBufferBE(BlockPos blockPos, BlockState blockState) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean bufferMoveItems(ItemBufferBlockEntity buffer, RelativeSide side, Level lvl) {
        throw new AssertionError();
    }
}