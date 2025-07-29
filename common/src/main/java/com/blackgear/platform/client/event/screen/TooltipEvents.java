package com.blackgear.platform.client.event.screen;

import com.blackgear.platform.core.util.event.Event;
import com.blackgear.platform.core.util.event.CancellableResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface TooltipEvents {
    Event<Item> ITEM_SETUP = Event.create(Item.class);
    Event<RenderTooltip> RENDER_TOOLTIP = Event.cancellable(RenderTooltip.class);

    interface Item {
        void registerTooltip(ItemStack stack, List<Component> lines, TooltipFlag flag);
    }

    interface RenderTooltip {
        CancellableResult onRendering(GuiGraphics graphics, List<? extends ClientTooltipComponent> texts, int x, int y);
    }
}