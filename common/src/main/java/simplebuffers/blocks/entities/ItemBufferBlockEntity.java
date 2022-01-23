package simplebuffers.blocks.entities;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import simplebuffers.PlatformExtensionUtil;
import simplebuffers.SimpleBuffersBlocks;
import simplebuffers.blocks.ItemBufferBlock;
import simplebuffers.menu.ItemBufferMenu;
import simplebuffers.util.*;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ItemBufferBlockEntity extends BlockEntity implements WorldlyContainer, ExtendedMenuProvider {

    private NonNullList<ItemStack> buffer;
    public SidedStateHolder<IOState> ioStates;
    public SidedStateHolder<FilterState> inputFilterStates;
    public SidedStateHolder<FilterState> outputFilterStates;
    public SidedStateHolder<ToggleState> inputPullStates;
    public SidedStateHolder<ToggleState> outputPushStates;
    public SidedStateHolder<RedstoneState> inputRedstoneState;
    public SidedStateHolder<RedstoneState> outputRedstoneState;
    public SidedIntegers inputLimit = new SidedIntegers();
    public SidedIntegers outputLimit = new SidedIntegers();

    public final ContainerData dataAccess;
    public final SidedFilterContainer filterContainer;

    private int rr_pending_face;
    private boolean rr_pending = false;

    private int counter = 0;

    private int[] rr_remaining_output = new int[6];
    private int[] rr_index_output = new int[6];
    private int[] rr_remaining_input = new int[6];
    private int[] rr_index_input = new int[6];

    private int transferRank = 0;

    public int getTransferRank() {
        return this.transferRank;
    }

    public void setTransferRank(int rank) {
        this.transferRank = rank;
    }

    public NonNullList<ItemStack> getBuffer() {
        return buffer;
    }

    public ItemBufferBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SimpleBuffersBlocks.ITEM_BUFFER_BLOCK_ENTITY.get(), blockPos, blockState);
        this.buffer = NonNullList.withSize(9, ItemStack.EMPTY);
        this.ioStates = new SidedStateHolder<IOState>(IOState.NONE);
        this.inputFilterStates = new SidedStateHolder<FilterState>(FilterState.WHITELIST);
        this.outputFilterStates = new SidedStateHolder<FilterState>(FilterState.WHITELIST);
        this.inputPullStates = new SidedStateHolder<ToggleState>(ToggleState.OFF);
        this.outputPushStates = new SidedStateHolder<ToggleState>(ToggleState.OFF);
        this.inputRedstoneState = new SidedStateHolder<RedstoneState>(RedstoneState.DISABLED);
        this.outputRedstoneState = new SidedStateHolder<RedstoneState>(RedstoneState.DISABLED);
        ItemBufferBlockEntity cont = this;
        this.dataAccess = new ContainerData() {
            public int get(int i) {
                if (i<6) {
                    return ioStates.getIOState(RelativeSide.ORDERED_SIDES.get(i)).getVal();
                } else if (i<12) {
                    return inputFilterStates.getIOState(RelativeSide.ORDERED_SIDES.get(i-6)).getVal();
                } else if (i<18) {
                    return outputFilterStates.getIOState(RelativeSide.ORDERED_SIDES.get(i-12)).getVal();
                } else if (i<24) {
                    return inputPullStates.getIOState(RelativeSide.ORDERED_SIDES.get(i-18)).getVal();
                } else if (i<30) {
                    return outputPushStates.getIOState(RelativeSide.ORDERED_SIDES.get(i-24)).getVal();
                } else if (i<36) {
                    return inputRedstoneState.getIOState(RelativeSide.ORDERED_SIDES.get(i-30)).getVal();
                } else if (i<42) {
                    return outputRedstoneState.getIOState(RelativeSide.ORDERED_SIDES.get(i-36)).getVal();
                } else if (i<48) {
                    return inputLimit.getHeld(RelativeSide.ORDERED_SIDES.get(i-42));
                } else if (i<54) {
                    return outputLimit.getHeld(RelativeSide.ORDERED_SIDES.get(i-48));
                }
                return 0;
            }

            public void set(int i, int j) {
                if (i<6) {
                    ioStates.setIOState(RelativeSide.ORDERED_SIDES.get(i), IOState.fromValStatic(j));
                    cont.setChanged();
                } else if (i<12) {
                    inputFilterStates.setIOState(RelativeSide.ORDERED_SIDES.get(i-6), FilterState.fromValStatic(j));
                    cont.setChanged();
                } else if (i<18) {
                    outputFilterStates.setIOState(RelativeSide.ORDERED_SIDES.get(i-12), FilterState.fromValStatic(j));
                    cont.setChanged();
                } else if (i<24) {
                    inputPullStates.setIOState(RelativeSide.ORDERED_SIDES.get(i-18), ToggleState.fromValStatic(j));
                    cont.setChanged();
                } else if (i<30) {
                    outputPushStates.setIOState(RelativeSide.ORDERED_SIDES.get(i-24), ToggleState.fromValStatic(j));
                    cont.setChanged();
                } else if (i<36) {
                    inputRedstoneState.setIOState(RelativeSide.ORDERED_SIDES.get(i-30), RedstoneState.fromValStatic(j));
                    cont.setChanged();
                } else if (i<42) {
                    outputRedstoneState.setIOState(RelativeSide.ORDERED_SIDES.get(i-36), RedstoneState.fromValStatic(j));
                    cont.setChanged();
                } else if (i<48) {
                    inputLimit.setHeld(RelativeSide.ORDERED_SIDES.get(i-42), j);
                    cont.setChanged();
                } else if (i<54) {
                    outputLimit.setHeld(RelativeSide.ORDERED_SIDES.get(i-48), j);
                    cont.setChanged();
                }
            }

            public int getCount() {
                return 6*9;
            }
        };
        this.filterContainer = new SidedFilterContainer(this);
    }

    public int getTransferModulus() {
        return 8;
    }

    public int getTransferMaxSize() {
        switch (transferRank) {
            case 0:
                return 1;
            case 1:
                return 8;
            case 2:
                return 64;
        }
        return 1;
    }

    public int getInternalFilterCount(RelativeSide side) {
        ArrayList<ItemStack> filterList = filterContainer.getFilterList(true, side);
        int count = 0;
        for (ItemStack itemStack : buffer) {
            boolean matches1 = inputFilterStates.getIOState(side) == FilterState.BLACKLIST;
            boolean toSet = inputFilterStates.getIOState(side) == FilterState.WHITELIST || inputFilterStates.getIOState(side) == FilterState.RR;
            for (ItemStack is : filterList) {
                ItemStack is1 = is.copy();
                is1.setCount(1);
                ItemStack is2 = itemStack.copy();
                is2.setCount(1);
                if (ItemStack.matches(is1, is2)) {
                    matches1 = toSet;
                }
            }
            if (matches1) {
                count += itemStack.getCount();
            }
        }
        return count;
    }

    public int getExternalFilterCount(RelativeSide side, Function<Integer, ItemStack> provider, int numSlots) {
        ArrayList<ItemStack> filterList = filterContainer.getFilterList(false, side);
        int count = 0;
        for (int i = 0; i < numSlots; i++) {
            ItemStack itemStack = provider.apply(i);
            boolean matches1 = outputFilterStates.getIOState(side) == FilterState.BLACKLIST;
            boolean toSet = outputFilterStates.getIOState(side) == FilterState.WHITELIST || outputFilterStates.getIOState(side) == FilterState.RR;
            for (ItemStack is : filterList) {
                ItemStack is1 = is.copy();
                is1.setCount(1);
                ItemStack is2 = itemStack.copy();
                is2.setCount(1);
                if (ItemStack.matches(is1, is2)) {
                    matches1 = toSet;
                }
            }
            if (matches1) {
                count += itemStack.getCount();
            }
        }
        return count;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("buffer")) {
            ContainerHelper.loadAllItems(tag.getCompound("buffer"), buffer);
        }
        if (tag.contains("filter")) {
            ContainerHelper.loadAllItems(tag.getCompound("filter"), filterContainer.buffer);
        }
        //sided states
        if (tag.contains("iostates")) {
            ioStates.fromTag(tag.getCompound("iostates"));
        }
        if (tag.contains("inputfilterstates")) {
            inputFilterStates.fromTag(tag.getCompound("inputfilterstates"));
        }
        if (tag.contains("outputfilterstates")) {
            outputFilterStates.fromTag(tag.getCompound("outputfilterstates"));
        }
        if (tag.contains("inputpullstates")) {
            inputPullStates.fromTag(tag.getCompound("inputpullstates"));
        }
        if (tag.contains("outputpushstates")) {
            outputPushStates.fromTag(tag.getCompound("outputpushstates"));
        }
        if (tag.contains("inputredstonestates")) {
            inputRedstoneState.fromTag(tag.getCompound("inputredstonestates"));
        }
        if (tag.contains("outputredstonestates")) {
            outputRedstoneState.fromTag(tag.getCompound("outputredstonestates"));
        }
        if (tag.contains("inputlimit")) {
            inputLimit.fromTag(tag.getCompound("inputlimit"));
        }
        if (tag.contains("outputlimit")) {
            outputLimit.fromTag(tag.getCompound("outputlimit"));
        }

        //rr information
        if (tag.contains("rr_remaining_output")) {
            this.rr_remaining_output = tag.getIntArray("rr_remaining_output");
        }
        if (tag.contains("rr_index_output")) {
            this.rr_index_output = tag.getIntArray("rr_index_output");
        }
        if (tag.contains("rr_remaining_input")) {
            this.rr_remaining_input = tag.getIntArray("rr_remaining_input");
        }
        if (tag.contains("rr_index_input")) {
            this.rr_index_input = tag.getIntArray("rr_index_input");
        }
        if (tag.contains("speed_rank")) {
            this.transferRank = tag.getInt("speed_rank");
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag bufferTag = new CompoundTag();
        CompoundTag filterTag = new CompoundTag();
        ContainerHelper.saveAllItems(bufferTag, buffer);
        ContainerHelper.saveAllItems(filterTag, filterContainer.buffer);
        tag.put("buffer", bufferTag);
        tag.put("filter", filterTag);
        //sided states
        CompoundTag ioTag = ioStates.toTag();
        tag.put("iostates",ioTag);
        CompoundTag inputFilterTag = inputFilterStates.toTag();
        tag.put("inputfilterstates",inputFilterTag);
        CompoundTag outputFilterTag = outputFilterStates.toTag();
        tag.put("outputfilterstates",outputFilterTag);
        CompoundTag inputPullTag = inputPullStates.toTag();
        tag.put("inputpullstates",inputPullTag);
        CompoundTag outputPushTag = outputPushStates.toTag();
        tag.put("outputpushstates",outputPushTag);
        CompoundTag inputRedstoneTag = inputRedstoneState.toTag();
        tag.put("inputredstonestates",inputRedstoneTag);
        CompoundTag outputRedstoneTag = outputRedstoneState.toTag();
        tag.put("outputredstonestates",outputRedstoneTag);
        CompoundTag inputLimitTag = inputLimit.toTag();
        tag.put("inputlimit",inputLimitTag);
        CompoundTag outputLimitTag = outputLimit.toTag();
        tag.put("outputlimit",outputLimitTag);
        //rr stuff
        tag.putIntArray("rr_remaining_output", rr_remaining_output);
        tag.putIntArray("rr_index_output", rr_index_output);
        tag.putIntArray("rr_remaining_input", rr_remaining_input);
        tag.putIntArray("rr_index_input", rr_index_input);
        tag.putInt("speed_rank", transferRank);
    }

    public int getContainerSize() {
        return this.buffer.size();
    }

    @Override
    public boolean isEmpty() {
        return this.buffer.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int i) {
        return this.buffer.get(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return removeItem(i,j,false, rr_pending_face);
    }

    public ItemStack removeItem(int i, int j, boolean simulate, Direction dir) {
        RelativeSide side = RelativeSide.fromDirections(dir, this.getBlockState().getValue(ItemBufferBlock.FACING));
        return this.removeItem(i,j,simulate,RelativeSide.getListPlace(side));
    }

    public int getRROutRemaining(RelativeSide side) {
        return this.rr_remaining_output[RelativeSide.getListPlace(side)];
    }

    public ItemStack getRROutItem(RelativeSide side) {
        ArrayList<ItemStack> filterList = filterContainer.getFilterList(false, side);
        return filterList.get(this.rr_index_output[RelativeSide.getListPlace(side)]).copy();
    }

    public boolean checkWhitelist(RelativeSide side, ItemStack itemStack) {
        //TODO: use this everywhere
        ArrayList<ItemStack> filterList = filterContainer.getFilterList(false, side);
        for (ItemStack is : filterList) {
            if (ItemUtils.countlessMatches(is, itemStack)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkBlacklist(RelativeSide side, ItemStack itemStack) {
        //TODO: use this everywhere
        ArrayList<ItemStack> filterList = filterContainer.getFilterList(false, side);
        for (ItemStack is : filterList) {
            if (ItemUtils.countlessMatches(is, itemStack)) {
                return false;
            }
        }
        return true;
    }

    public ItemStack removeItem(int i, int j, boolean simulate, int face) {
        ItemStack itemStack = ContainerExtension.removeItem(this.buffer, i, j, simulate);
        if (!itemStack.isEmpty()) {
            if (this.rr_pending) {
                int count = itemStack.getCount();
                if (rr_remaining_output[face] >= count && !simulate){
                    rr_remaining_output[face] -= count;
                } else {
                    int newCount = rr_remaining_output[face];
                    itemStack.setCount(newCount);
                    if (!simulate) {
                        rr_remaining_output[face] = 0;
                        this.buffer.get(i).grow(count - newCount);
                    }
                }
                if (!simulate) {
                    rrUpdateBuffer(RelativeSide.ORDERED_SIDES.get(face), 0, false);
                    rr_pending = false;
                }
            }
            if (!simulate) {
                this.setChanged();
            }
        }
        if (!simulate) {
            this.flattenItemsLeft();
        }
        return itemStack;
    }

    public boolean canTransferRedstone(RelativeSide side, boolean isInput) {
        RedstoneState rState = isInput ? inputRedstoneState.getIOState(side) : outputRedstoneState.getIOState(side);
        boolean worldState = (Boolean)this.getBlockState().getValue(ItemBufferBlock.ENABLED);
        switch (rState) {
            case DISABLED:
                return true;
            case HIGH:
                return !worldState;
            case LOW:
                return worldState;
        }
        return false;
    }

    public void rrUpdateBuffer(RelativeSide side, int times, boolean isInput) {
        int sideNum = RelativeSide.getListPlace(side);
        if (isInput) {
            if (rr_remaining_input[sideNum] <= 0) {
                rr_index_input[sideNum] += 1;
                if (rr_index_input[sideNum] >= 9) {
                    rr_index_input[sideNum] -= 9;
                }
                ArrayList<ItemStack> filterList = filterContainer.getFilterList(true, side);
                rr_remaining_input[sideNum] = filterList.get(rr_index_input[sideNum]).getCount();
                if (rr_remaining_input[sideNum] <= 0) {
                    if (times < 9) {
                        rrUpdateBuffer(side, times + 1, true);
                    }
                }
            }
        } else {
            if (rr_remaining_output[sideNum] <= 0) {
                rr_index_output[sideNum] += 1;
                if (rr_index_output[sideNum] >= 9) {
                    rr_index_output[sideNum] -= 9;
                }
                ArrayList<ItemStack> filterList = filterContainer.getFilterList(false, side);
                rr_remaining_output[sideNum] = filterList.get(rr_index_output[sideNum]).getCount();
                if (rr_remaining_output[sideNum] <= 0) {
                    if (times < 9) {
                        rrUpdateBuffer(side, times + 1, false);
                    }
                }
            }
        }
        this.setChanged();
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ItemStack.EMPTY;
    }

    public ItemBufferBlockEntity getSelf() {
        return this;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        this.setItem(i,itemStack,rr_pending_face);
    }

    public void setItem(int i, ItemStack itemStack, Direction dir) {
        RelativeSide side = RelativeSide.fromDirections(dir, this.getBlockState().getValue(ItemBufferBlock.FACING));
        this.setItem(i,itemStack,RelativeSide.getListPlace(side));
    }

    public void setItem(int i, ItemStack itemStack, int face) {
        this.buffer.set(i, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
        if (rr_pending && !itemStack.isEmpty()) {
            rr_remaining_input[face] -= itemStack.getCount();
        }
        rrUpdateBuffer(RelativeSide.ORDERED_SIDES.get(face), 0, true);

        flattenItemsLeft();
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level != null && this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public void clearContent() {
        this.buffer.clear();
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return IntStream.range(0,this.getContainerSize()).toArray();
    }

    public void flattenItemsLeft() {
        ArrayList<ItemStack> is = new ArrayList<ItemStack>();
        ItemStack last = ItemStack.EMPTY;
        for (ItemStack item : buffer) {
            if (!item.isEmpty()) {
                if (!last.isEmpty()) {
                    if (ItemUtils.countlessMatches(last, item)) {
                        int lastCount = last.getCount();
                        int maxCount = Math.min(last.getMaxStackSize(), this.getMaxStackSize());
                        if (lastCount >= maxCount) {
                            is.add(item);
                        } else {
                            int itemCount = item.getCount();
                            if (itemCount+lastCount > maxCount) {
                                last.setCount(maxCount);
                                item.setCount(itemCount+lastCount-maxCount);
                                if (itemCount+lastCount-maxCount <= 0) {
                                    item = last;
                                } else {
                                    is.add(item);
                                }
                            } else {
                                last.setCount(itemCount+lastCount);
                                item = last;
                            }
                        }
                    } else {
                        is.add(item);
                    }
                } else {
                    is.add(item);
                }
                last = item;
            }
        }
        clearContent();
        for (int i = 0; i < is.size(); i++) {
            buffer.set(i, is.get(i));
        }
        this.setChanged();
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        ItemStack tryPlace = tryPlaceItem(i, itemStack, direction);
        if (tryPlace.isEmpty()) {
            return true;
        }
        return false;
    }

    public ItemStack tryPlaceItem(int i, ItemStack itemStack, @Nullable Direction direction) {
        //Don't let it in if the last spot is non-empty
        int size = getContainerSize();
        if (!buffer.get(size-1).isEmpty()) {
            return itemStack.copy();
        }
        //Only let it fill the last slot
        if (i != size-1) {
            return itemStack.copy();
        }
        RelativeSide side = RelativeSide.fromDirections(direction, this.getBlockState().getValue(ItemBufferBlock.FACING));
        int alreadyHas = getInternalFilterCount(side);
        int maxAmount = inputLimit.getHeld(side);
        int countAdd = 0;
        ItemStack itemStack2 = itemStack.copy();
        if (maxAmount != 0) {
            if (alreadyHas >= maxAmount) {
                return itemStack.copy();
            }
            int diff = maxAmount - alreadyHas;
            countAdd = itemStack.getCount() - Math.min(itemStack.getCount(), diff);
            itemStack2.setCount(Math.min(itemStack.getCount(), diff));
        }
        if (!canTransferRedstone(side, true)) {
            return itemStack.copy();
        }
        rr_pending = false;
        if (direction != null) {
            IOState ioState = ioStates.getIOState(side);
            if (!ioState.isIn()) {
                return itemStack.copy();
            }
            ArrayList<ItemStack> filterList = filterContainer.getFilterList(true, side);
            if (inputFilterStates.getIOState(side) == FilterState.RR) {
                this.rrUpdateBuffer(side, 0, true);
                ItemStack is1 = filterList.get(rr_index_input[RelativeSide.getListPlace(side)]).copy();
                int rr_count = is1.getCount();
                ItemStack is2 = itemStack2.copy();
                is1.setCount(1);
                is2.setCount(1);
                if (ItemStack.matches(is1,is2)) {
                    ItemStack itemOut = itemStack2.copy();
                    if (rr_count <= itemStack2.getCount()) {
                        itemOut.setCount(itemStack2.getCount()-rr_count);
                    } else {
                        itemOut = ItemStack.EMPTY;
                    }
                    rr_pending_face = RelativeSide.getListPlace(side);
                    rr_pending = true;
                    int oldCount = itemOut.getCount();
                    itemOut = itemStack2.copy();
                    itemOut.setCount(oldCount+countAdd);
                    return itemOut;
                }
                return itemStack.copy();
            } else {
                boolean matches1 = inputFilterStates.getIOState(side) == FilterState.BLACKLIST;
                boolean toSet = inputFilterStates.getIOState(side) == FilterState.WHITELIST;
                for (ItemStack is : filterList) {
                    ItemStack is1 = is.copy();
                    is1.setCount(1);
                    ItemStack is2 = itemStack.copy();
                    is2.setCount(1);
                    if (ItemStack.matches(is1, is2)) {
                        matches1 = toSet;
                    }
                }
                ItemStack altStack = itemStack.copy();
                altStack.setCount(countAdd);
                return matches1 ? altStack: itemStack.copy();
            }
        }
        return itemStack.copy();
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        ItemStack attempt = tryTakeItem(i, itemStack, direction);
        if (!attempt.isEmpty() && attempt.getCount()==itemStack.getCount()) {
            return true;
        }
        return false;
    }

    public ItemStack tryTakeItem(int i, ItemStack itemStack, Direction direction) {
        RelativeSide side = RelativeSide.fromDirections(direction, this.getBlockState().getValue(ItemBufferBlock.FACING));
        if (!canTransferRedstone(side, false)) {
            return ItemStack.EMPTY;
        }
        rr_pending = false;
        if (direction != null) {
            IOState ioState = ioStates.getIOState(side);
            if (!ioState.isOut()) {
                return ItemStack.EMPTY;
            }
            ArrayList<ItemStack> filterList = filterContainer.getFilterList(false, side);
            if (outputFilterStates.getIOState(side) == FilterState.RR) {
                this.rrUpdateBuffer(side, 0, false);
                ItemStack is1 = filterList.get(rr_index_output[RelativeSide.getListPlace(side)]).copy();
                int rr_count = is1.getCount();
                ItemStack is2 = itemStack.copy();
                is1.setCount(1);
                is2.setCount(1);
                if (ItemStack.matches(is1,is2)) {
                    ItemStack itemOut = itemStack.copy();
                    if (rr_count <= itemStack.getCount()) {
                        itemOut.setCount(rr_count);
                    }
                    rr_pending_face = RelativeSide.getListPlace(side);
                    rr_pending = true;
                    return itemOut;
                }
                return ItemStack.EMPTY;
            } else {
                boolean matches1 = outputFilterStates.getIOState(side) == FilterState.BLACKLIST;
                boolean toSet = outputFilterStates.getIOState(side) == FilterState.WHITELIST;
                for (ItemStack is : filterList) {
                    ItemStack is1 = is.copy();
                    is1.setCount(1);
                    ItemStack is2 = itemStack.copy();
                    is2.setCount(1);
                    if (ItemStack.matches(is1, is2)) {
                        matches1 = toSet;
                    }
                }
                return matches1 ? itemStack.copy() : ItemStack.EMPTY;
            }
        }
        return itemStack.copy();
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container.simple_buffers.item_buffer");
    }

    public boolean canOpen(Player player) {
        return (!player.isSpectator());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        if (this.canOpen(player)) {
            return new ItemBufferMenu(i, inventory, this.getBlockPos(), this, this.filterContainer, this.dataAccess, this.transferRank);
        } else {
            return null;
        }
    }

    @Override
    public void saveExtraData(FriendlyByteBuf buf) {
        buf.writeBlockPos(getBlockPos());
        buf.writeInt(transferRank);
    }

    public void tickServer(Level lvl, BlockPos pos, BlockState blockState) {
        if (counter >= this.getTransferModulus()) {
            counter = 0;
        }
        if (counter == 0) {
            // push/pull items
            // this is not going to be fun. I'm trying to do this the same way that Vanilla does it, but it won't be pretty.
            for (RelativeSide side : RelativeSide.ORDERED_SIDES) {
                for (int j = 0; j < this.getTransferMaxSize(); j++) {
                    boolean foundOne = PlatformExtensionUtil.bufferMoveItems(this, side, lvl);
                    if (!foundOne) {
                        break;
                    }
                }
                // this hopefully has attempted to, if appropriate, push and pull one item each.
            }
            this.setChanged();
        }
        counter ++;
    }

    public CompoundTag getSpecSheetTag() {
        CompoundTag tag = new CompoundTag();

        CompoundTag filterTag = new CompoundTag();
        ContainerHelper.saveAllItems(filterTag, filterContainer.buffer);
        tag.put("filter", filterTag);
        //sided states
        CompoundTag ioTag = ioStates.toTag();
        tag.put("iostates",ioTag);
        CompoundTag inputFilterTag = inputFilterStates.toTag();
        tag.put("inputfilterstates",inputFilterTag);
        CompoundTag outputFilterTag = outputFilterStates.toTag();
        tag.put("outputfilterstates",outputFilterTag);
        CompoundTag inputPullTag = inputPullStates.toTag();
        tag.put("inputpullstates",inputPullTag);
        CompoundTag outputPushTag = outputPushStates.toTag();
        tag.put("outputpushstates",outputPushTag);
        CompoundTag inputRedstoneTag = inputRedstoneState.toTag();
        tag.put("inputredstonestates",inputRedstoneTag);
        CompoundTag outputRedstoneTag = outputRedstoneState.toTag();
        tag.put("outputredstonestates",outputRedstoneTag);
        CompoundTag inputLimitTag = inputLimit.toTag();
        tag.put("inputlimit",inputLimitTag);
        CompoundTag outputLimitTag = outputLimit.toTag();
        tag.put("outputlimit",outputLimitTag);

        return tag;
    }

    public CompoundTag getSpecSheetSidedTag(Direction direction) {
        RelativeSide side = RelativeSide.fromDirections(direction, this.getBlockState().getValue(ItemBufferBlock.FACING));
        CompoundTag tag = new CompoundTag();

        CompoundTag filterTag = new CompoundTag();
        NonNullList<ItemStack> items = NonNullList.withSize(18, ItemStack.EMPTY);
        filterContainer.getSidedSublist(side, items);
        ContainerHelper.saveAllItems(filterTag, items);
        tag.put("filter", filterTag);
        //sided states
        tag.putInt("iostates",ioStates.getIOState(side).getVal());
        tag.putInt("inputfilterstates",inputFilterStates.getIOState(side).getVal());
        tag.putInt("outputfilterstates",outputFilterStates.getIOState(side).getVal());
        tag.putInt("inputpullstates",inputPullStates.getIOState(side).getVal());
        tag.putInt("outputpushstates",outputPushStates.getIOState(side).getVal());
        tag.putInt("inputredstonestates",inputRedstoneState.getIOState(side).getVal());
        tag.putInt("outputredstonestates",outputRedstoneState.getIOState(side).getVal());
        tag.putInt("inputlimit",inputLimit.getHeld(side));
        tag.putInt("outputlimit",outputLimit.getHeld(side));

        return tag;
    }

    public void setSpecSheetTag(CompoundTag tag) {
        if (tag == null) {
            return;
        }
        if (tag.contains("filter")) {
            ContainerHelper.loadAllItems(tag.getCompound("filter"), filterContainer.buffer);
        }
        //sided states
        if (tag.contains("iostates")) {
            ioStates.fromTag(tag.getCompound("iostates"));
        }
        if (tag.contains("inputfilterstates")) {
            inputFilterStates.fromTag(tag.getCompound("inputfilterstates"));
        }
        if (tag.contains("outputfilterstates")) {
            outputFilterStates.fromTag(tag.getCompound("outputfilterstates"));
        }
        if (tag.contains("inputpullstates")) {
            inputPullStates.fromTag(tag.getCompound("inputpullstates"));
        }
        if (tag.contains("outputpushstates")) {
            outputPushStates.fromTag(tag.getCompound("outputpushstates"));
        }
        if (tag.contains("inputredstonestates")) {
            inputRedstoneState.fromTag(tag.getCompound("inputredstonestates"));
        }
        if (tag.contains("outputredstonestates")) {
            outputRedstoneState.fromTag(tag.getCompound("outputredstonestates"));
        }
        if (tag.contains("inputlimit")) {
            inputLimit.fromTag(tag.getCompound("inputlimit"));
        }
        if (tag.contains("outputlimit")) {
            outputLimit.fromTag(tag.getCompound("outputlimit"));
        }
    }

    public void setSpecSheetSidedTag(CompoundTag tag, Direction direction) {
        RelativeSide side = RelativeSide.fromDirections(direction, this.getBlockState().getValue(ItemBufferBlock.FACING));
        if (tag == null) {
            return;
        }
        if (tag.contains("filter")) {
            NonNullList<ItemStack> items = NonNullList.withSize(18, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag.getCompound("filter"), items);
            filterContainer.loadSidedSublist(side, items);
        }
        //sided states
        if (tag.contains("iostates")) {
            ioStates.setIOState(side, tag.getInt("iostates"));
        }
        if (tag.contains("inputfilterstates")) {
            inputFilterStates.setIOState(side, tag.getInt("inputfilterstates"));
        }
        if (tag.contains("outputfilterstates")) {
            outputFilterStates.setIOState(side, tag.getInt("outputfilterstates"));
        }
        if (tag.contains("inputpullstates")) {
            inputPullStates.setIOState(side, tag.getInt("inputpullstates"));
        }
        if (tag.contains("outputpushstates")) {
            outputPushStates.setIOState(side, tag.getInt("outputpushstates"));
        }
        if (tag.contains("inputredstonestates")) {
            inputRedstoneState.setIOState(side, tag.getInt("inputredstonestates"));
        }
        if (tag.contains("outputredstonestates")) {
            outputRedstoneState.setIOState(side, tag.getInt("outputredstonestates"));
        }
        if (tag.contains("inputlimit")) {
            inputLimit.setHeld(side, tag.getInt("inputlimit"));
        }
        if (tag.contains("outputlimit")) {
            outputLimit.setHeld(side, tag.getInt("outputlimit"));
        }
    }
}
