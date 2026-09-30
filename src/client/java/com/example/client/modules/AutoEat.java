package com.example.client.modules;

import com.example.client.module.Module;
import com.example.client.settings.BoolSetting;
import com.example.client.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Eats a hotbar food item when hungry, then switches back to your previous slot. */
public class AutoEat extends Module {
	private final NumberSetting hunger = add(new NumberSetting("Hunger Threshold", 14, 1, 19, 1));
	private final BoolSetting pauseNearMobs = add(new BoolSetting("Pause Near Mobs", true));

	private boolean eating = false;
	private int previousSlot = -1;

	public AutoEat() {
		super("AutoEat", "Eats automatically when hungry", Category.MISC);
	}

	private static boolean isGoodFood(ItemStack stack) {
		if (stack.isEmpty() || !stack.has(DataComponents.FOOD)) return false;
		Item item = stack.getItem();
		return item != Items.ROTTEN_FLESH
				&& item != Items.SPIDER_EYE
				&& item != Items.PUFFERFISH
				&& item != Items.POISONOUS_POTATO
				&& item != Items.CHORUS_FRUIT
				&& item != Items.GOLDEN_APPLE
				&& item != Items.ENCHANTED_GOLDEN_APPLE;
	}

	private boolean mobsNearby(Minecraft mc) {
		if (mc.level == null || mc.player == null) return false;
		return !mc.level.getEntitiesOfClass(Monster.class, mc.player.getBoundingBox().inflate(6.0)).isEmpty();
	}

	@Override
	public void onTick(Minecraft mc) {
		var player = mc.player;
		if (player == null) return;
		if (mc.screen != null) {
			if (eating) stopEating(mc);
			return;
		}

		int food = player.getFoodData().getFoodLevel();
		boolean danger = pauseNearMobs.get() && mobsNearby(mc);

		if (eating) {
			if (food >= 20 || danger || !isGoodFood(player.getMainHandItem())) {
				stopEating(mc);
			} else {
				mc.options.keyUse.setDown(true);
			}
			return;
		}

		if (food > hunger.getInt() || danger) return;

		var inv = player.getInventory();
		for (int i = 0; i < 9; i++) {
			if (isGoodFood(inv.getItem(i))) {
				previousSlot = inv.getSelectedSlot();
				inv.setSelectedSlot(i);
				eating = true;
				mc.options.keyUse.setDown(true);
				return;
			}
		}
	}

	private void stopEating(Minecraft mc) {
		mc.options.keyUse.setDown(false);
		if (mc.player != null && previousSlot != -1) {
			mc.player.getInventory().setSelectedSlot(previousSlot);
		}
		previousSlot = -1;
		eating = false;
	}

	@Override
	public void onDisable() {
		if (eating) stopEating(Minecraft.getInstance());
	}
}
