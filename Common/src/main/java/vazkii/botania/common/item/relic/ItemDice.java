/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.relic;

import com.google.common.base.Suppliers;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import vazkii.botania.api.item.IRelic;
import vazkii.botania.client.gui.TooltipHandler;
import vazkii.botania.common.handler.ModSounds;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ResourceLocationHelper;
import vazkii.botania.xplat.IXplatAbstractions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemDice extends ItemRelic {
	public ItemDice(Properties props) {
		super(props);
	}

	public static final Supplier<List<ItemStack>> RELIC_STACKS = Suppliers.memoize(() -> List.of(
			new ItemStack(ModItems.infiniteFruit),
			new ItemStack(ModItems.kingKey),
			new ItemStack(ModItems.flugelEye),
			new ItemStack(ModItems.thorRing),
			new ItemStack(ModItems.odinRing),
			new ItemStack(ModItems.lokiRing)
	)
	);

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		var relic = IXplatAbstractions.INSTANCE.findRelic(stack);

		if (relic != null && relic.isRightPlayer(player)) {
			if (world.isClientSide) {
				return InteractionResultHolder.success(stack);
			}

			world.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.diceOfFate, SoundSource.PLAYERS, 1F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

			List<Integer> possible = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				if (!hasRelicAlready(player, i)) {
					possible.add(i);
				}
			}

			if (!possible.isEmpty()) {
				int relicIdx = possible.get(world.random.nextInt(possible.size()));
				player.sendMessage(new TranslatableComponent("botaniamisc.diceRoll", relicIdx + 1).withStyle(ChatFormatting.DARK_GREEN), Util.NIL_UUID);
				var toGive = RELIC_STACKS.get().get(relicIdx).copy();
				return InteractionResultHolder.success(toGive);
			} else {
				int roll = world.random.nextInt(6) + 1;
				ResourceLocation tableId = ResourceLocationHelper.prefix("dice/roll_" + roll);
				LootTable table = world.getServer().getLootTables().get(tableId);
				LootContext context = new LootContext.Builder(((ServerLevel) world))
						.withParameter(LootContextParams.THIS_ENTITY, player)
						.withParameter(LootContextParams.ORIGIN, player.position())
						.withLuck(player.getLuck())
						.create(LootContextParamSets.GIFT);

				List<ItemStack> generated = table.getRandomItems(context);
				for (ItemStack drop : generated) {
					if (!player.getInventory().add(drop)) {
						player.drop(drop, false);
					}
				}
				String langKey = generated.isEmpty() ? "botaniamisc.dudDiceRoll" : "botaniamisc.diceRoll";
				player.sendMessage(new TranslatableComponent(langKey, roll).withStyle(ChatFormatting.DARK_GREEN), Util.NIL_UUID);

				stack.shrink(1);
				return InteractionResultHolder.success(stack);
			}
		}

		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flags) {
		super.appendHoverText(stack, world, tooltip, flags);
		tooltip.add(new TextComponent(""));
		TooltipHandler.addOnShift(tooltip, () -> {
			String name = stack.getDescriptionId() + ".poem";
			for (int i = 0; i < 4; i++) {
				tooltip.add(new TranslatableComponent(name + i).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
			}
		});
	}

	public static IRelic makeRelic(ItemStack stack) {
		return new RelicImpl(stack, null) {
			@Override
			public boolean shouldDamageWrongPlayer() {
				return false;
			}
		};
	}

	private boolean hasRelicAlready(Player player, int relic) {
		if (relic < 0 || relic > 6 || !(player instanceof ServerPlayer mpPlayer)) {
			return true;
		}

		var stack = RELIC_STACKS.get().get(relic);
		ResourceLocation advId = IXplatAbstractions.INSTANCE.findRelic(stack).getAdvancement();

		if (advId != null) {
			Advancement adv = player.level.getServer().getAdvancements().getAdvancement(advId);
			return adv != null && mpPlayer.getAdvancements().getOrStartProgress(adv).isDone();
		}

		return false;
	}

}
