package com.example.client.modules;

import com.example.client.SinglePlayer;
import com.example.client.module.Module;
import com.example.client.settings.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Multiplies your walking speed. Keep it modest: very high values can rubber-band. Singleplayer only. */
public class Speed extends Module {
	private static final double BASE = 0.1;
	private final NumberSetting multiplier = add(new NumberSetting("Multiplier", 1.5, 1.0, 3.0, 0.1));

	public Speed() {
		super("Speed", "Move faster", Category.MOVEMENT);
	}

	private static void apply(LivingEntity entity, double value) {
		var attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
		if (attr != null && attr.getBaseValue() != value) attr.setBaseValue(value);
	}

	@Override
	public void onTick(Minecraft mc) {
		if (mc.player == null) return;
		double value = BASE * multiplier.get();
		apply(mc.player, value);
		SinglePlayer.runOnServer(mc, sp -> apply(sp, value));
	}

	@Override
	public void onDisable() {
		var mc = Minecraft.getInstance();
		if (mc.player == null) return;
		apply(mc.player, BASE);
		SinglePlayer.runOnServer(mc, sp -> apply(sp, BASE));
	}
}
