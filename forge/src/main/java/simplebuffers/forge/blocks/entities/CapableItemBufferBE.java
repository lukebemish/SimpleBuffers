package simplebuffers.forge.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;
import simplebuffers.blocks.entities.ItemBufferBlockEntity;
import simplebuffers.util.DirectedHolder;

import javax.annotation.Nonnull;

public class CapableItemBufferBE extends ItemBufferBlockEntity {
    private final DirectedHolder<ItemBufferHandlerWrapper> itemHandlerHolder = new DirectedHolder<ItemBufferHandlerWrapper>();
    private final DirectedHolder<LazyOptional<ItemBufferHandlerWrapper>> handlerHolder = new DirectedHolder<LazyOptional<ItemBufferHandlerWrapper>>();

    public CapableItemBufferBE(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        for (Direction dir : DirectedHolder.DIRECTIONS) {
            itemHandlerHolder.setHeld(dir, new ItemBufferHandlerWrapper(getSelf(), dir));
            handlerHolder.setHeld(dir, LazyOptional.of(() -> itemHandlerHolder.getHeld(dir)));
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        for (Direction dir : DirectedHolder.DIRECTIONS) {
            handlerHolder.getHeld(dir).invalidate();
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handlerHolder.getHeld(side).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (Direction dir : DirectedHolder.DIRECTIONS) {
            handlerHolder.getHeld(dir).invalidate();
        }
    }
}
