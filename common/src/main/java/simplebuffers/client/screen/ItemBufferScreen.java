package simplebuffers.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import simplebuffers.SimpleBuffers;
import simplebuffers.SimpleBuffersNetworkingClient;
import simplebuffers.SimpleBuffersNetworkingServer;
import simplebuffers.blocks.entities.SidedFilterContainer;
import simplebuffers.menu.FilterSlot;
import simplebuffers.menu.ItemBufferMenu;
import simplebuffers.menu.ToggleableSlot;
import simplebuffers.util.*;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class ItemBufferScreen extends AbstractContainerScreen<ItemBufferMenu> {
    private final ResourceLocation GUI = new ResourceLocation(SimpleBuffers.MOD_ID, "textures/gui/item_buffer_gui.png");
    private final ResourceLocation GUI_SIDE_INFO = new ResourceLocation(SimpleBuffers.MOD_ID, "textures/gui/item_buffer_side_info.png");

    private BufferScreenState shownState = BufferScreenState.ITEMS;
    private ItemBufferMenu bufferMenu;
    protected int imageWidth = 176+76*2;

    private boolean showingLimitOutput = false;
    private boolean showingLimitInput = false;

    public ItemBufferScreen(ItemBufferMenu menu, Inventory inv, Component name) {
        super(menu, inv, name);
        this.bufferMenu = menu;
        onRenderStateChange();
    }

    public void sendUpdatePacket() {
        int count = this.bufferMenu.containerData.getCount();
        int[] vals = new int[count];
        for (int i = 0; i < count; i++) {
            vals[i] = this.bufferMenu.containerData.get(i);
        }
        SimpleBuffersNetworkingServer.BlockConfigUpdateMsg msg = new SimpleBuffersNetworkingServer.BlockConfigUpdateMsg(vals, this.bufferMenu.pos);
        SimpleBuffersNetworkingClient.send(SimpleBuffers.SERVERBOUND_BUFFER_CONFIG_UPDATE_ID, msg);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, mouseX, mouseY);
        int relX = (this.width - this.imageWidth+76*2) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        int relMouseX = mouseX - relX;
        int relMouseY = mouseY - relY;
        if (this.hoveredSlot == null) {
            if (shownState != BufferScreenState.ITEMS && !showingLimitInput && !showingLimitOutput) {
                IOState ioState = IOState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())));
                if (7 < relMouseX && relMouseX < 24 && 34 < relMouseY && relMouseY < 51) {
                    Component stateText = TextComponent.EMPTY;
                    switch (ioState) {
                        case IN -> stateText = new TranslatableComponent("gui_text.simple_buffers.input");
                        case OUT -> stateText = new TranslatableComponent("gui_text.simple_buffers.output");
                        case INOUT -> stateText = new TranslatableComponent("gui_text.simple_buffers.inout");
                        case NONE -> stateText = new TranslatableComponent("gui_text.simple_buffers.no_inout");
                    }
                    this.renderTooltip(matrixStack, stateText, mouseX, mouseY);
                }
                //filter in
                if (7+18 < relMouseX && relMouseX < 24+18 && 34 < relMouseY && relMouseY < 51-8 && ioState.isIn()) {
                    FilterState filterState = FilterState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+6));
                    Component stateText = TextComponent.EMPTY;
                    switch (filterState) {
                        case WHITELIST -> stateText = new TranslatableComponent("gui_text.simple_buffers.whitelist");
                        case BLACKLIST -> stateText = new TranslatableComponent("gui_text.simple_buffers.blacklist");
                        case RR -> stateText = new TranslatableComponent("gui_text.simple_buffers.rr");
                    }
                    this.renderTooltip(matrixStack, stateText, mouseX, mouseY);
                }
                //filter out
                if (7+18 < relMouseX && relMouseX < 24+18 && 34+8 < relMouseY && relMouseY < 51 && ioState.isOut()) {
                    FilterState filterState = FilterState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+12));
                    Component stateText = TextComponent.EMPTY;
                    switch (filterState) {
                        case WHITELIST -> stateText = new TranslatableComponent("gui_text.simple_buffers.whitelist");
                        case BLACKLIST -> stateText = new TranslatableComponent("gui_text.simple_buffers.blacklist");
                        case RR -> stateText = new TranslatableComponent("gui_text.simple_buffers.rr");
                    }
                    this.renderTooltip(matrixStack, stateText, mouseX, mouseY);
                }
                //pull in
                if (7+18+18 < relMouseX && relMouseX < 24+18+18 && 34 < relMouseY && relMouseY < 51-8 && ioState.isIn()) {
                    ToggleState pullState = ToggleState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+18));
                    Component stateText = TextComponent.EMPTY;
                    switch (pullState) {
                        case ON -> stateText = new TranslatableComponent("gui_text.simple_buffers.pull_on");
                        case OFF -> stateText = new TranslatableComponent("gui_text.simple_buffers.pull_off");
                    }
                    this.renderTooltip(matrixStack, stateText, mouseX, mouseY);
                }
                //push out
                if (7+18+18 < relMouseX && relMouseX < 24+18+18 && 34+8 < relMouseY && relMouseY < 51 && ioState.isOut()) {
                    ToggleState pushState = ToggleState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+24));
                    Component stateText = TextComponent.EMPTY;
                    switch (pushState) {
                        case ON -> stateText = new TranslatableComponent("gui_text.simple_buffers.push_on");
                        case OFF -> stateText = new TranslatableComponent("gui_text.simple_buffers.push_off");
                    }
                    this.renderTooltip(matrixStack, stateText, mouseX, mouseY);
                }
                //redstone in
                if (7+18*3 < relMouseX && relMouseX < 24+18*3 && 34 < relMouseY && relMouseY < 51-8 && ioState.isIn()) {
                    RedstoneState rState = RedstoneState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+30));
                    Component stateText = TextComponent.EMPTY;
                    switch (rState) {
                        case DISABLED -> stateText = new TranslatableComponent("gui_text.simple_buffers.redstone_disabled");
                        case HIGH -> stateText = new TranslatableComponent("gui_text.simple_buffers.redstone_high");
                        case LOW -> stateText = new TranslatableComponent("gui_text.simple_buffers.redstone_low");
                    }
                    this.renderTooltip(matrixStack, stateText, mouseX, mouseY);
                }
                //redstone out
                if (7+18*3 < relMouseX && relMouseX < 24+18*3 && 34+8 < relMouseY && relMouseY < 51 && ioState.isOut()) {
                    RedstoneState rState = RedstoneState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+36));
                    Component stateText = TextComponent.EMPTY;
                    switch (rState) {
                        case DISABLED -> stateText = new TranslatableComponent("gui_text.simple_buffers.redstone_disabled");
                        case HIGH -> stateText = new TranslatableComponent("gui_text.simple_buffers.redstone_high");
                        case LOW -> stateText = new TranslatableComponent("gui_text.simple_buffers.redstone_low");
                    }
                    this.renderTooltip(matrixStack, stateText, mouseX, mouseY);
                }
                //limit in
                if (7+18*4 < relMouseX && relMouseX < 24+18*4 && 34 < relMouseY && relMouseY < 51-8 && ioState.isOut()) {
                    Component stateText = new TranslatableComponent("gui_text.simple_buffers.limit");
                    this.renderTooltip(matrixStack, stateText, mouseX, mouseY);
                }
                //limit out
                if (7+18*4 < relMouseX && relMouseX < 24+18*4 && 34+8 < relMouseY && relMouseY < 51 && ioState.isOut()) {
                    Component stateText = new TranslatableComponent("gui_text.simple_buffers.limit");
                    this.renderTooltip(matrixStack, stateText, mouseX, mouseY);
                }
            }
        }
    }

    protected void renderItemWOSlot(FilterSlot slot) {
        int i = slot.x+this.leftPos;
        int j = slot.y+this.topPos;
        ItemStack itemStack = slot.getItem();
        this.itemRenderer.renderAndDecorateItem(this.minecraft.player, itemStack, i, j, slot.x + slot.y * this.imageWidth);
        this.itemRenderer.renderGuiItemDecorations(this.font, itemStack, i, j, null);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth+76*2) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        int relMouseX = mouseX - relX;
        int relMouseY = mouseY - relY;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth-76, this.imageHeight);
        //speed rank
        if (this.menu.speedRank == 1) {
            this.blit(matrixStack, relX+174, relY, 108, 166, 71, 74);
        } else if (this.menu.speedRank == 2) {
            this.blit(matrixStack, relX+174, relY, 179, 166, 71, 74);
        }
        //current state
        //items
        switch (shownState) {
            case ITEMS -> this.blit(matrixStack, relX + 183, relY + 10, 55, 167, 16, 16);
            case LEFT -> this.blit(matrixStack, relX + 183, relY + 28, 73, 167, 16, 16);
            case RIGHT -> this.blit(matrixStack, relX + 219, relY + 28, 73, 167, 16, 16);
            case DOWN -> this.blit(matrixStack, relX + 201, relY + 46, 73, 167, 16, 16);
            case UP -> this.blit(matrixStack, relX + 201, relY + 10, 73, 167, 16, 16);
            case BACK -> this.blit(matrixStack, relX + 219, relY + 46, 73, 167, 16, 16);
            case FRONT -> this.blit(matrixStack, relX + 201, relY + 28, 91, 167, 16, 16);
        }
        //hover highlight
        //items
        if (182 < relMouseX && relMouseX < 199 && 9 < relMouseY && relMouseY < 26) {
            this.blit(matrixStack, relX + 183, relY + 10, 1, 167, 16, 16);
        }
        //left
        if (182 < relMouseX && relMouseX < 199 && 27 < relMouseY && relMouseY < 44) {
            this.blit(matrixStack, relX + 183, relY + 28, 19, 167, 16, 16);
        }
        //right
        if (218 < relMouseX && relMouseX < 235 && 27 < relMouseY && relMouseY < 44) {
            this.blit(matrixStack, relX + 219, relY + 28, 19, 167, 16, 16);
        }
        //down
        if (200 < relMouseX && relMouseX < 217 && 45 < relMouseY && relMouseY < 62) {
            this.blit(matrixStack, relX + 201, relY + 46, 19, 167, 16, 16);
        }
        //up
        if (200 < relMouseX && relMouseX < 217 && 9 < relMouseY && relMouseY < 26) {
            this.blit(matrixStack, relX + 201, relY + 10, 19, 167, 16, 16);
        }
        //back
        if (218 < relMouseX && relMouseX < 235 && 45 < relMouseY && relMouseY < 62) {
            this.blit(matrixStack, relX + 219, relY + 46, 19, 167, 16, 16);
        }
        //front
        if (200 < relMouseX && relMouseX < 217 && 27 < relMouseY && relMouseY < 44) {
            this.blit(matrixStack, relX + 201, relY + 28, 37, 167, 16, 16);
        }

        //sided info
        if (shownState != BufferScreenState.ITEMS) {
            RenderSystem.setShaderTexture(0, GUI_SIDE_INFO);
            this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth-76*2, this.imageHeight);
            if (7 < relMouseX && relMouseX < 24 && 34 < relMouseY && relMouseY < 51) {
                this.blit(matrixStack, relX + 8, relY + 35, 1, 185, 16, 16);
            }
            IOState ioState = IOState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())));
            switch (ioState) {
                case INOUT -> this.blit(matrixStack, relX + 8, relY + 35, 1, 167, 16, 16);
                case OUT -> this.blit(matrixStack, relX + 8, relY + 35, 19, 167, 16, 16);
                case IN -> this.blit(matrixStack, relX + 8, relY + 35, 37, 167, 16, 16);
                case NONE -> this.blit(matrixStack, relX + 8, relY + 35, 55, 167, 16, 16);
            }
            if (!ioState.isIn()) {
                this.blit(matrixStack, relX+7, relY+16, 0, 202, 162, 18);
                this.blit(matrixStack, relX+8+18, relY+35, 37, 185, 16, 8);
                this.blit(matrixStack, relX+8+18+18, relY+35, 37, 185, 16, 8);
                this.blit(matrixStack, relX+8+18*3, relY+35, 37, 185, 16, 8);
                this.blit(matrixStack, relX+8+18*4, relY+35, 37, 185, 16, 8);
            }
            if (!ioState.isOut()) {
                this.blit(matrixStack, relX+7, relY+52, 0, 202, 162, 18);
                this.blit(matrixStack, relX+8+18, relY+35+8, 37, 185, 16, 8);
                this.blit(matrixStack, relX+8+18+18, relY+35+8, 37, 185, 16, 8);
                this.blit(matrixStack, relX+8+18*3, relY+35+8, 37, 185, 16, 8);
                this.blit(matrixStack, relX+8+18*4, relY+35+8, 37, 185, 16, 8);
            }
            if (ioState.isIn()) {
                //filter
                FilterState filterState = FilterState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+6));
                if (7+18 < relMouseX && relMouseX < 24+18 && 34 < relMouseY && relMouseY < 51-8) {
                    this.blit(matrixStack, relX+8+18, relY+35, 19, 185, 16, 8);
                }
                switch (filterState) {
                    case WHITELIST -> this.blit(matrixStack, relX + 8+18, relY + 35, 55+18, 167, 16, 8);
                    case BLACKLIST -> this.blit(matrixStack, relX + 8+18, relY + 35, 55+18+18, 167, 16, 8);
                    case RR -> this.blit(matrixStack, relX + 8+18, relY + 35, 55+18+18+18, 167, 16, 8);
                }
                //pull
                ToggleState pullState = ToggleState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+18));
                if (pullState == ToggleState.ON) {
                    this.blit(matrixStack, relX+8+18+18, relY+35, 19+18+18, 185, 16, 8);
                }
                if (7+18+18 < relMouseX && relMouseX < 24+18+18 && 34 < relMouseY && relMouseY < 51-8) {
                    this.blit(matrixStack, relX+8+18+18, relY+35, 19, 185, 16, 8);
                }
                this.blit(matrixStack, relX+8+18+18, relY+35, 127, 167, 16, 8);
                //redstone
                RedstoneState rState = RedstoneState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+30));
                switch (rState) {
                    case HIGH -> this.blit(matrixStack, relX+8+18*3, relY+35, 19+18*4, 185, 16, 8);
                    case LOW -> this.blit(matrixStack, relX+8+18*3, relY+35, 19+18*3, 185, 16, 8);
                }
                if (7+18*3 < relMouseX && relMouseX < 24+18*3 && 34 < relMouseY && relMouseY < 51-8) {
                    this.blit(matrixStack, relX+8+18*3, relY+35, 19, 185, 16, 8);
                }
                //limit
                if (7+18*4 < relMouseX && relMouseX < 24+18*4 && 34 < relMouseY && relMouseY < 51-8) {
                    this.blit(matrixStack, relX+8+18*4, relY+35, 19, 185, 16, 8);
                }
                if (showingLimitInput) {
                    for (int m = 0; m < (this.menu).slots.size(); ++m) {
                        Slot slot = (this.menu).slots.get(m);
                        if (slot instanceof FilterSlot fs) {
                            if (fs.isVis()) {
                                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                                this.renderItemWOSlot(fs);
                            }
                        }
                    }
                    this.setBlitOffset(250);
                    RenderSystem.setShaderTexture(0, GUI_SIDE_INFO);
                    this.blit(matrixStack, relX+68, relY+24, 176, 0, 40, 19);
                    if (99 <= relMouseX && 29 <= relMouseY && relMouseX < 104 && relMouseY < 33) {
                        this.blit(matrixStack, relX+99, relY+47-19, 207, 23, 5, 5);
                    }
                    if (99 <= relMouseX && 34 <= relMouseY && relMouseX < 104 && relMouseY < 39) {
                        this.blit(matrixStack, relX+99, relY+53-19, 207, 29, 5, 5);
                    }
                    this.setBlitOffset(0);
                }
            }
            if (ioState.isOut()) {
                //filter
                FilterState filterState = FilterState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+12));
                if (7+18 < relMouseX && relMouseX < 24+18 && 34+8 < relMouseY && relMouseY < 51) {
                    this.blit(matrixStack, relX+8+18, relY+35+8, 19, 185, 16, 8);
                }
                switch (filterState) {
                    case WHITELIST -> this.blit(matrixStack, relX + 8+18, relY + 35+8, 55+18, 167+8, 16, 8);
                    case BLACKLIST -> this.blit(matrixStack, relX + 8+18, relY + 35+8, 55+18+18, 167+8, 16, 8);
                    case RR -> this.blit(matrixStack, relX + 8+18, relY + 35+8, 55+18+18+18, 167+8, 16, 8);
                }
                //push
                ToggleState pushState = ToggleState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+24));
                if (pushState == ToggleState.ON) {
                    this.blit(matrixStack, relX+8+18+18, relY+35+8, 19+18+18, 185, 16, 8);
                }
                if (7+18+18 < relMouseX && relMouseX < 24+18+18 && 34+8 < relMouseY && relMouseY < 51) {
                    this.blit(matrixStack, relX+8+18+18, relY+35+8, 19, 185, 16, 8);
                }
                this.blit(matrixStack, relX+8+18+18, relY+35+8, 127, 167+8, 16, 8);
                //redstone
                RedstoneState rState = RedstoneState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+36));
                switch (rState) {
                    case HIGH -> this.blit(matrixStack, relX+8+18*3, relY+35+8, 19+18*4, 185, 16, 8);
                    case LOW -> this.blit(matrixStack, relX+8+18*3, relY+35+8, 19+18*3, 185, 16, 8);
                }
                if (7+18*3 < relMouseX && relMouseX < 24+18*3 && 34+8 < relMouseY && relMouseY < 51) {
                    this.blit(matrixStack, relX+8+18*3, relY+35+8, 19, 185, 16, 8);
                }
                //limit
                if (7+18*4 < relMouseX && relMouseX < 24+18*4 && 34+8 < relMouseY && relMouseY < 51) {
                    this.blit(matrixStack, relX+8+18*4, relY+35+8, 19, 185, 16, 8);
                }
                if (showingLimitOutput) {
                    for (int m = 0; m < (this.menu).slots.size(); ++m) {
                        Slot slot = (this.menu).slots.get(m);
                        if (slot instanceof FilterSlot fs) {
                            if (fs.isVis()) {
                                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                                this.renderItemWOSlot(fs);
                            }
                        }
                    }
                    this.setBlitOffset(250);
                    RenderSystem.setShaderTexture(0, GUI_SIDE_INFO);
                    this.blit(matrixStack, relX+68, relY+43, 176, 0, 40, 19);
                    if (99 <= relMouseX && 29+19 <= relMouseY && relMouseX < 104 && relMouseY < 33+19) {
                        this.blit(matrixStack, relX+99, relY+47, 207, 23, 5, 5);
                    }
                    if (99 <= relMouseX && 34+19 <= relMouseY && relMouseX < 104 && relMouseY < 39+19) {
                        this.blit(matrixStack, relX+99, relY+53, 207, 29, 5, 5);
                    }
                    this.setBlitOffset(0);
                }
            }
        }
    }

    @Override
    public void renderLabels(PoseStack stack, int i, int j) {
        int relX = (this.width - this.imageWidth+76*2) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        super.renderLabels(stack,i,j);
        //current state text
        Component stateText = TextComponent.EMPTY;
        switch (this.shownState) {
            case ITEMS -> stateText = new TranslatableComponent("gui_text.simple_buffers.items");
            case LEFT -> stateText = new TranslatableComponent("gui_text.simple_buffers.left");
            case RIGHT -> stateText = new TranslatableComponent("gui_text.simple_buffers.right");
            case FRONT -> stateText = new TranslatableComponent("gui_text.simple_buffers.front");
            case BACK -> stateText = new TranslatableComponent("gui_text.simple_buffers.back");
            case UP -> stateText = new TranslatableComponent("gui_text.simple_buffers.up");
            case DOWN -> stateText = new TranslatableComponent("gui_text.simple_buffers.down");
        }
        int textLen = this.font.width(stateText);
        this.font.draw(stack, stateText, (float)this.titleLabelX+this.imageWidth-76*2 - textLen - 15, (float)this.titleLabelY, 4210752);
        if (this.shownState!=BufferScreenState.ITEMS) {
            if (showingLimitInput) {
                int limit = this.menu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+42);
                Component component = new TextComponent(Integer.toString(limit));
                this.setBlitOffset(255);
                stack.translate(0,0,this.getBlitOffset());
                this.font.draw(stack, component, 74, 37-7, 16777215);
                this.setBlitOffset(0);
                stack.translate(0,0,this.getBlitOffset());
            }
            if (showingLimitOutput) {
                int limit = this.menu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())+48);
                Component component = new TextComponent(Integer.toString(limit));
                this.setBlitOffset(255);
                stack.translate(0,0,this.getBlitOffset());
                this.font.draw(stack, component, 74, 56-7, 16777215);
                this.setBlitOffset(0);
                stack.translate(0,0,this.getBlitOffset());
            }
        }
    }

    public void onRenderStateChange() {
        for (FilterSlot s : this.bufferMenu.filterSlots) {
            s.turnOff();
            s.setVis(false);
        }
        if (shownState != BufferScreenState.ITEMS) {
            for (ToggleableSlot s : this.bufferMenu.itemSlots) {
                s.turnOff();
            }
            IOState ioState = IOState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())));
            if (ioState.isIn()) {
                for (int i = 0; i < 9; i++) {
                    int filterNum = SidedFilterContainer.getIOSlotNum(true, i, shownState.toSide());
                    this.bufferMenu.filterSlots.get(filterNum).turnOn();
                    this.bufferMenu.filterSlots.get(filterNum).setVis(true);
                }
            }
            if (ioState.isOut()) {
                for (int i = 0; i < 9; i++) {
                    int filterNum = SidedFilterContainer.getIOSlotNum(false, i, shownState.toSide());
                    this.bufferMenu.filterSlots.get(filterNum).turnOn();
                    this.bufferMenu.filterSlots.get(filterNum).setVis(true);
                }
            }
            if (showingLimitOutput || showingLimitInput) {
                for (ToggleableSlot s : this.bufferMenu.filterSlots) {
                    s.turnOff();
                }
            }
        } else {
            showingLimitOutput = false;
            showingLimitInput = false;
            for (ToggleableSlot s : this.bufferMenu.itemSlots) {
                s.turnOn();
            }
        }
    }

    public void onImportantStateChange() {
        sendUpdatePacket();
        onRenderStateChange();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int i) {
        int relX = (this.width - this.imageWidth+76*2) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        double relMouseX = mouseX - relX;
        double relMouseY = mouseY - relY;
        if (showingLimitInput) {
            if (68 <= relMouseX && 24 <= relMouseY && relMouseX < 108 && relMouseY < 43) {
                if (99 <= relMouseX && 29 <= relMouseY && relMouseX < 104 && relMouseY < 33) {
                    menu.progressLimit(this.shownState.toSide(), true, true);
                    this.onImportantStateChange();
                }
                if (99 <= relMouseX && 34 <= relMouseY && relMouseX < 104 && relMouseY < 39) {
                    menu.progressLimit(this.shownState.toSide(), true, false);
                    this.onImportantStateChange();
                }
            } else {
                showingLimitInput = false;
                this.onRenderStateChange();
            }
            return true;
        }
        if (showingLimitOutput) {
            if (68 <= relMouseX && 24+19 <= relMouseY && relMouseX < 108 && relMouseY < 43+19) {
                if (99 <= relMouseX && 29+19 <= relMouseY && relMouseX < 104 && relMouseY < 33+19) {
                    menu.progressLimit(this.shownState.toSide(), false, true);
                    this.onImportantStateChange();
                }
                if (99 <= relMouseX && 34+19 <= relMouseY && relMouseX < 104 && relMouseY < 39+19) {
                    menu.progressLimit(this.shownState.toSide(), false, false);
                    this.onImportantStateChange();
                }
            } else {
                showingLimitOutput = false;
                this.onRenderStateChange();
            }
            return true;
        }
        //items
        if (183 <= relMouseX && relMouseX < 199 && 10 <= relMouseY && relMouseY < 26) {
            this.shownState = BufferScreenState.ITEMS;
            this.onRenderStateChange();
            return true;
        }
        //left
        if (183 <= relMouseX && relMouseX < 199 && 28 <= relMouseY && relMouseY < 44) {
            this.shownState = BufferScreenState.LEFT;
            this.onRenderStateChange();
            return true;
        }
        //right
        if (219 <= relMouseX && relMouseX < 235 && 28 <= relMouseY && relMouseY < 44) {
            this.shownState = BufferScreenState.RIGHT;
            this.onRenderStateChange();
            return true;
        }
        //down
        if (201 <= relMouseX && relMouseX < 217 && 46 <= relMouseY && relMouseY < 62) {
            this.shownState = BufferScreenState.DOWN;
            this.onRenderStateChange();
            return true;
        }
        //up
        if (201 <= relMouseX && relMouseX < 217 && 10 <= relMouseY && relMouseY < 26) {
            this.shownState = BufferScreenState.UP;
            this.onRenderStateChange();
            return true;
        }
        //back
        if (219 <= relMouseX && relMouseX < 235 && 46 <= relMouseY && relMouseY < 62) {
            this.shownState = BufferScreenState.BACK;
            this.onRenderStateChange();
            return true;
        }
        //front
        if (201 <= relMouseX && relMouseX < 217 && 28 <= relMouseY && relMouseY < 44) {
            this.shownState = BufferScreenState.FRONT;
            this.onRenderStateChange();
            return true;
        }

        //progress io mode
        if (shownState != BufferScreenState.ITEMS) {
            if (8 <= relMouseX && relMouseX < 24 && 35 <= relMouseY && relMouseY < 51) {
                this.bufferMenu.progressIOState(shownState.toSide());
                this.onImportantStateChange();
                return true;
            }
            //filter state change
            IOState ioState = IOState.fromValStatic(bufferMenu.containerData.get(RelativeSide.ORDERED_SIDES.indexOf(shownState.toSide())));
            if (ioState.isIn()) {
                if (26 <= relMouseX && relMouseX < 42 && 35 <= relMouseY && relMouseY < 43) {
                    this.bufferMenu.progressFilterState(shownState.toSide(), true);
                    this.onImportantStateChange();
                    return true;
                }
                if (26+18 <= relMouseX && relMouseX < 42+18 && 35 <= relMouseY && relMouseY < 43) {
                    this.bufferMenu.progressPushPullState(shownState.toSide(), true);
                    this.onImportantStateChange();
                    return true;
                }
                if (26+18*2 <= relMouseX && relMouseX < 42+18*2 && 35 <= relMouseY && relMouseY < 43) {
                    this.bufferMenu.progressRedstoneState(shownState.toSide(), true);
                    this.onImportantStateChange();
                    return true;
                }
                //level selector
                if (26+18*3 <= relMouseX && relMouseX < 42+18*3 && 35 <= relMouseY && relMouseY < 43) {
                    this.showingLimitInput = true;
                    this.onRenderStateChange();
                    return true;
                }
            }
            if (ioState.isOut()) {
                if (26 <= relMouseX && relMouseX < 42 && 43 <= relMouseY && relMouseY < 51) {
                    this.bufferMenu.progressFilterState(shownState.toSide(), false);
                    this.onImportantStateChange();
                    return true;
                }
                if (26+18 <= relMouseX && relMouseX < 42+18 && 43 <= relMouseY && relMouseY < 51) {
                    this.bufferMenu.progressPushPullState(shownState.toSide(), false);
                    this.onImportantStateChange();
                    return true;
                }
                if (26+18*2 <= relMouseX && relMouseX < 42+18*2 && 43 <= relMouseY && relMouseY < 51) {
                    this.bufferMenu.progressRedstoneState(shownState.toSide(), false);
                    this.onImportantStateChange();
                    return true;
                }
                //level selector
                if (26+18*3 <= relMouseX && relMouseX < 42+18*3 && 43 <= relMouseY && relMouseY < 51) {
                    this.showingLimitOutput = true;
                    this.onRenderStateChange();
                    return true;
                }
            }
        }


        return super.mouseClicked(mouseX,mouseY,i);
    }
}
