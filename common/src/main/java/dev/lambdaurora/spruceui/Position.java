/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SpruceUI.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.spruceui;

import dev.lambdaurora.spruceui.widget.SpruceWidget;

import java.util.Objects;

/**
 * Represents a position.
 *
 * @author LambdAurora
 * @version 3.0.0
 * @since 1.4.0
 */
public final class Position implements SprucePositioned {
	private SprucePositioned anchor;
	private int x = 0;
	private int y = 0;

	protected Position(SprucePositioned anchor) {
		this.anchor = anchor;
	}

	public static Position of(SprucePositioned anchor, int x, int y) {
		return new Position(anchor).move(x, y);
	}

	public static Position of(int x, int y) {
		return of(origin(), x, y);
	}

	public static Position center(SpruceWidget parent, int y) {
		return center(parent, parent.getWidth(), y);
	}

	public static Position center(SprucePositioned anchor, int width, int y) {
		return of(anchor, width / 2, y);
	}

	public static Position center(int width, int y) {
		return of(width / 2, y);
	}

	/**
	 * Returns the origin position.
	 *
	 * @return the origin position
	 */
	public static Position origin() {
		return new Position(new SprucePositioned() {
		});
	}

	/**
	 * Returns the anchor.
	 *
	 * @return the anchor
	 */
	public SprucePositioned getAnchor() {
		return this.anchor;
	}

	/**
	 * Sets the anchor.
	 *
	 * @param anchor the anchor
	 */
	public void setAnchor(SprucePositioned anchor) {
		this.anchor = anchor;
	}

	@Override
	public int getX() {
		return this.anchor.getX() + this.x;
	}

	@Override
	public int getY() {
		return this.anchor.getY() + this.y;
	}

	public Position move(int x, int y) {
		this.setRelativeX(x);
		this.setRelativeY(y);
		return this;
	}

	/**
	 * Gets the relative X of this position.
	 *
	 * @return the relative X
	 */
	public int getRelativeX() {
		return this.x;
	}

	/**
	 * Sets the relative X of this position.
	 *
	 * @param x the relative X
	 */
	public void setRelativeX(int x) {
		this.x = x;
	}

	/**
	 * Gets the relative Y of this position.
	 *
	 * @return the relative Y
	 */
	public int getRelativeY() {
		return this.y;
	}

	/**
	 * Sets the relative Y of this position.
	 *
	 * @param y the relative Y
	 */
	public void setRelativeY(int y) {
		this.y = y;
	}

	/**
	 * Copies the position into a new position.
	 *
	 * @return the copied position
	 */
	public Position copy() {
		return of(this.anchor, this.x, this.y);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		var position = (Position) o;
		return this.getX() == position.getX() && this.getY() == position.getY();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.anchor, this.x, this.y);
	}

	@Override
	public String toString() {
		return "Position{" +
				"anchor=" + this.anchor +
				", x=" + this.x +
				", y=" + this.y +
				'}';
	}
}
