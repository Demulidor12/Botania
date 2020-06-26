/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.state.enums;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum LuminizerVariant implements IStringSerializable {
	DEFAULT,
	DETECTOR,
	FORK,
	TOGGLE;

	@Override
	public String func_176610_l() {
		return name().toLowerCase(Locale.ROOT);
	}

}
