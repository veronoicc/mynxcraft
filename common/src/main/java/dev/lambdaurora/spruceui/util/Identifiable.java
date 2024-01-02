/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SpruceUI.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.spruceui.util;

import net.minecraft.resources.ResourceLocation;

/**
 * Represents something that can be identified.
 *
 * @author LambdAurora
 * @version 3.2.0
 * @since 3.2.0
 */
public interface Identifiable extends Nameable {
	/**
	 * Gets the identifier of this object.
	 *
	 * @return the identifier of this object
	 */
	ResourceLocation getIdentifier();

	@Override
	default String getName() {
		return this.getIdentifier().getPath();
	}
}
