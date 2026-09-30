package com.example.client.modules;

import com.example.client.module.Module;
import com.example.client.settings.BoolSetting;
import com.example.client.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

/** Smoothly turns your view toward the nearest mob in front of you. Mobs only, singleplayer only. */
public class AimAssist extends Module {
	private final NumberSetting range = add(new NumberSetting("Range", 4.5, 3.0, 8.0, 0.5));
	private final NumberSetting fov = add(new NumberSetting("FOV", 60, 10, 180, 10));
	private final NumberSetting smooth = add(new NumberSetting("Strength", 0.3, 0.05, 1.0, 0.05));
	private final BoolSetting hostileOnly = add(new BoolSetting("Hostile Only", true));
	private final BoolSetting onlyAttacking = add(new BoolSetting("Only While Attacking", true));

	public AimAssist() {
		super("AimAssist", "Nudges your aim toward nearby mobs", Category.COMBAT);
	}

	@Override
	public void onTick(Minecraft mc) {
		var player = mc.player;
		if (player == null || mc.level == null || mc.screen != null) return;
		if (onlyAttacking.get() && !mc.options.keyAttack.isDown()) return;

		var eye = player.getEyePosition();
		LivingEntity best = null;
		float bestAngle = (float) fov.get() / 2f;

		var candidates = mc.level.getEntitiesOfClass(LivingEntity.class,
				player.getBoundingBox().inflate(range.get()),
				e -> e != player && !(e instanceof Player) && e.isAlive()
						&& (!hostileOnly.get() || e instanceof Monster));

		for (LivingEntity e : candidates) {
			if (player.distanceTo(e) > range.get() || !player.hasLineOfSight(e)) continue;
			var d = e.getBoundingBox().getCenter().subtract(eye);
			float yaw = (float) (Math.toDegrees(Math.atan2(d.z, d.x)) - 90.0);
			float diff = Math.abs(Mth.wrapDegrees(yaw - player.getYRot()));
			if (diff < bestAngle) {
				bestAngle = diff;
				best = e;
			}
		}
		if (best == null) return;

		var d = best.getBoundingBox().getCenter().subtract(eye);
		float targetYaw = (float) (Math.toDegrees(Math.atan2(d.z, d.x)) - 90.0);
		float targetPitch = (float) -Math.toDegrees(Math.atan2(d.y, Math.sqrt(d.x * d.x + d.z * d.z)));

		float k = (float) smooth.get();
		float newYaw = player.getYRot() + Mth.wrapDegrees(targetYaw - player.getYRot()) * k;
		float newPitch = player.getXRot() + (targetPitch - player.getXRot()) * k;
		player.setYRot(newYaw);
		player.setXRot(Mth.clamp(newPitch, -90f, 90f));
	}
}
