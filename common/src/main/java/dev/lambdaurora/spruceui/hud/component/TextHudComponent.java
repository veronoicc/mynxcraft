/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SpruceUI.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.spruceui.hud.component;

import dev.lambdaurora.spruceui.hud.HudComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Represents a text HUD component.
 *
 * @author LambdAurora
 * @version 5.0.0
 * @since 1.3.5
 */
public class TextHudComponent extends HudComponent {
	protected Minecraft client;
	protected Component text;
	protected int color;

	public TextHudComponent(ResourceLocation identifier, int x, int y, Component text) {
		this(identifier, x, y, text, 0xffffffff);
	}

	public TextHudComponent(ResourceLocation identifier, int x, int y, Component text, int color) {
		super(identifier, x, y);
		this.client = Minecraft.getInstance();
		this.text = text;
		this.color = color;
	}

	/**
	 * Gets this component's text.
	 *
	 * @return the component's text
	 */
	public Component getText() {
		return this.text;
	}

	/**
	 * Sets this component's text.
	 *
	 * @param text the text
	 */
	public void setText(Component text) {
		this.text = text;
	}

	/**
	 * Gets this component's text color.
	 *
	 * @return the text color
	 */
	public int getColor() {
		return this.color;
	}

	/**
	 * Sets this component's text color.
	 *
	 * @param color the text color
	 */
	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public void render(GuiGraphics graphics, float tickDelta) {
		graphics.drawString(this.client.font, this.text, this.x, this.y, this.color);
	}
}
