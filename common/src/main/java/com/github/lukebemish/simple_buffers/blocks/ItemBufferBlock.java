package com.github.lukebemish.simple_buffers.blocks;

import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import com.github.lukebemish.simple_buffers.PlatformExtensionUtil;
import com.github.lukebemish.simple_buffers.SimpleBuffersItems;
import com.github.lukebemish.simple_buffers.blocks.entities.ItemBufferBlockEntity;

public class ItemBufferBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final BooleanProperty ENABLED;

    public ItemBufferBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(FACING, Direction.SOUTH).setValue(ENABLED, true));
    }

    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite()).setValue(ENABLED, true);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ENABLED);
    }

    static {
        ENABLED = BlockStateProperties.ENABLED;
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        boolean disable = !level.hasNeighborSignal(blockPos);
        if (disable != (Boolean)blockState.getValue(ENABLED)) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(ENABLED, disable), 4);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return PlatformExtensionUtil.getItemBufferBE(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof ItemBufferBlockEntity be) {
                be.tickServer(lvl, pos, blockState);
            }
        };
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof ItemBufferBlockEntity be) {
            if (!level.isClientSide && !player.isCreative()) {
                ItemStack itemStack;
                ItemEntity itemEntity;
                switch (be.getTransferRank()) {
                    case 2:
                        itemStack = new ItemStack(SimpleBuffersItems.SPEED_UPGRADE_2.get(),1);
                        itemEntity = new ItemEntity(level, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, itemStack);
                        itemEntity.setDefaultPickUpDelay();
                        level.addFreshEntity(itemEntity);
                    case 1:
                        itemStack = new ItemStack(SimpleBuffersItems.SPEED_UPGRADE_1.get(),1);
                        itemEntity = new ItemEntity(level, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, itemStack);
                        itemEntity.setDefaultPickUpDelay();
                        level.addFreshEntity(itemEntity);
                }
            }
        }

        super.playerWillDestroy(level, blockPos, blockState, player);
    }

    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof  ItemBufferBlockEntity buffer) {
                ItemStack handItem = player.getItemInHand(interactionHand);
                if (buffer.getTransferRank() == 0 && handItem.is(SimpleBuffersItems.SPEED_UPGRADE_1.get())) {
                    handItem.shrink(1);
                    buffer.setTransferRank(1);
                } else if (buffer.getTransferRank() == 1 && handItem.is(SimpleBuffersItems.SPEED_UPGRADE_2.get())) {
                    handItem.shrink(1);
                    buffer.setTransferRank(2);
                } else if (handItem.is(SimpleBuffersItems.SPEC_SHEET.get())) {
                    CompoundTag tag = handItem.getTag();
                    ItemStack newItem = new ItemStack(SimpleBuffersItems.SPEC_SHEET_STORED.get());
                    newItem.setTag(tag);
                    player.getItemInHand(interactionHand).shrink(1);
                    newItem.addTagElement("stored_config", buffer.getSpecSheetTag());
                    player.addItem(newItem);
                } else if (handItem.is(SimpleBuffersItems.SPEC_SHEET_STORED.get())) {
                    buffer.setSpecSheetTag(handItem.getTagElement("stored_config"));
                } else if (handItem.is(SimpleBuffersItems.SMALL_SPEC_SHEET.get())) {
                    Direction direction = blockHitResult.getDirection();
                    CompoundTag tag = handItem.getTag();
                    ItemStack newItem = new ItemStack(SimpleBuffersItems.SMALL_SPEC_SHEET_STORED.get());
                    newItem.setTag(tag);
                    newItem.addTagElement("stored_config", buffer.getSpecSheetSidedTag(direction));
                    player.getItemInHand(interactionHand).shrink(1);
                    player.addItem(newItem);
                } else if (handItem.is(SimpleBuffersItems.SMALL_SPEC_SHEET_STORED.get())) {
                    Direction direction = blockHitResult.getDirection();
                    buffer.setSpecSheetSidedTag(handItem.getTagElement("stored_config"), direction);
                } else if (player instanceof ServerPlayer sPlayer) {
                    MenuRegistry.openExtendedMenu(sPlayer, buffer);
                }
            }

            return InteractionResult.CONSUME;
        }
    }

    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!blockState.is(blockState2.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof ItemBufferBlockEntity) {
                Containers.dropContents(level, blockPos, (ItemBufferBlockEntity)blockEntity);
                level.updateNeighbourForOutputSignal(blockPos, this);
            }

            super.onRemove(blockState, level, blockPos, blockState2, bl);
        }
    }

    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(blockPos));
    }
}
