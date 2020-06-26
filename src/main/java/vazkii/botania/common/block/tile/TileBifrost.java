/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

import vazkii.botania.common.lib.LibBlockNames;
import vazkii.botania.common.lib.LibMisc;

import javax.annotation.Nonnull;

public class TileBifrost extends TileMod implements ITickableTileEntity {
	private static final String TAG_TICKS = "ticks";

	public int ticks = 0;

	public TileBifrost() {
		super(ModTiles.BIFROST);
	}

	@Override
	public void tick() {
		if (!world.isRemote) {
			if (ticks <= 0) {
				world.removeBlock(pos, false);
			} else {
				ticks--;
			}
		}
	}

	@Nonnull
	@Override
	public CompoundNBT write(CompoundNBT tag) {
		CompoundNBT ret = super.write(tag);
		ret.putInt(TAG_TICKS, ticks);
		return ret;
	}

	@Override
	public void func_230337_a_(BlockState state, CompoundNBT tag) {
		super.func_230337_a_(state, tag);
		ticks = tag.getInt(TAG_TICKS);
	}

}
