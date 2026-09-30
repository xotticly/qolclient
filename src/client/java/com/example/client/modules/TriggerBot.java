package com.example.client.modules;

import com.example.client.module.Module;
import com.example.client.settings.BoolSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

/** Attacks the mob under your crosshair once your weapon is fully charged. Mobs only, singleplayer only. */
public class TriggerBot extends Module {
	private final BoolSetting hostileOnly = add(new BoolSetting("Hostile Only", true));

	public TriggerBot() {
		super("TriggerBot", "Auto-attack the mob you're aiming at", Category.COMBAT);
	}

	@Override
	public void onTick(Minecraft mc) {
		var player = mc.player;
		if (player == null || mc.gameMode == null || mc.screen != null) return;
		if (!(mc.hitResult instanceof EntityHitResult hit)) return;

		Entity target = hit.getEntity();
		if (!(target instanceof LivingEntity living) || target instanceof Player) return;
		if (!living.isAlive()) return;
		if (hostileOnly.get() && !(target instanceof Monster)) return;
		if (player.getAttackStrengthScale(0.5f) < 1.0f) return;

		mc.gameMode.attack(player, target);
		player.swing(InteractionHand.MAIN_HAND);
	}
}
