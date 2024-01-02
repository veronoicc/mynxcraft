/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SpruceUI.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.spruceui.option;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.util.Nameable;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

/**
 * Represents an option.
 *
 * @author LambdAurora
 * @version 3.3.0
 * @since 1.0.3
 */
public abstract class SpruceOption implements Nameable {
	public final String key;
	private Optional<Component> tooltip = Optional.empty();

	public SpruceOption(String key) {
		Objects.requireNonNull(key, "Cannot create an option without a key.");
		this.key = key;
	}

	@Override
	public String getName() {
		return I18n.get(this.key);
	}

	public Optional<Component> getOptionTooltip() {
		return this.tooltip;
	}

	public void setTooltip(@Nullable Component tooltip) {
		this.tooltip = Optional.ofNullable(tooltip);
	}

	/**
	 * Returns the display prefix text.
	 *
	 * @return the display prefix
	 */
	public Component getPrefix() {
		return Component.translatable(this.key);
	}

	/**
	 * Returns the display text.
	 *
	 * @param value the value
	 * @return the display text
	 */
	public Component getDisplayText(Component value) {
		return Component.translatable("spruceui.options.generic", this.getPrefix(), value);
	}

	public abstract SpruceWidget createWidget(Position position, int width);
}
