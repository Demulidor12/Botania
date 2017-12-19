/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [May 12, 2015, 5:56:45 PM (GMT)]
 */
package vazkii.botania.common.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.client.core.handler.ModelHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.lib.LibItemNames;
import vazkii.botania.common.lib.LibMisc;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemTemperanceStone extends ItemMod {
	private static final String TAG_ACTIVE = "active";

	public ItemTemperanceStone() {
		super(LibItemNames.TEMPERANCE_STONE);
		setMaxStackSize(1);
		addPropertyOverride(new ResourceLocation(LibMisc.MOD_ID, "active"),
				(stack, worldIn, entityIn) -> ItemNBTHelper.getBoolean(stack, TAG_ACTIVE, false) ? 1 : 0);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		ItemNBTHelper.setBoolean(stack, TAG_ACTIVE, !ItemNBTHelper.getBoolean(stack, TAG_ACTIVE, false));
		world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 0.3F, 0.1F);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> stacks, ITooltipFlag flags) {
		if(ItemNBTHelper.getBoolean(stack, TAG_ACTIVE, false))
			addStringToTooltip(I18n.format("botaniamisc.active"), stacks);
		else addStringToTooltip(I18n.format("botaniamisc.inactive"), stacks);
	}

	private void addStringToTooltip(String s, List<String> tooltip) {
		tooltip.add(s.replaceAll("&", "\u00a7"));
	}

	public static boolean hasTemperanceActive(EntityPlayer player) {
		IInventory inv = player.inventory;
		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty() && stack.getItem() == ModItems.temperanceStone && ItemNBTHelper.getBoolean(stack, TAG_ACTIVE, false))
				return true;
		}

		return false;
	}

}
