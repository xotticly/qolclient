package com.example.client.modules;

import com.example.client.SinglePlayer;
import com.example.client.module.Module;
import com.example.client.settings.NumberSetting;
import net.minecraft.client.Minecraft;

/** Gives you creative-style flight in survival (double-tap jump to fly). Singleplayer only. */
public class Flight extends Module {
	private final NumberSetting speed = add(new NumberSetting("Speed", 1.0, 0.5, 5.0, 0.5));

	public Flight() {
		super("Flight", "Creative-style flying in survival", Category.MOVEMENT);
	}

	@Override
	public void onTick(Minecraft mc) {
		var player = mc.player;
		if (player == null) return;
		player.getAbilities().mayfly = true;
		player.getAbilities().setFlyingSpeed(0.05f * (float) speed.get());

		SinglePlayer.runOnServer(mc, sp -> {
			if (!sp.getAbilities().mayfly) {
				sp.getAbilities().mayfly = true;
				sp.onUpdateAbilities();
			}
		});
	}

	@Override
	public void onDisable() {
		var mc = Minecraft.getInstance();
		var player = mc.player;
		if (player == null || player.isCreative() || player.isSpectator()) return;
		player.getAbilities().mayfly = false;
		player.getAbilities().flying = false;
		player.getAbilities().setFlyingSpeed(0.05f);
		SinglePlayer.runOnServer(mc, sp -> {
			sp.getAbilities().mayfly = false;
			sp.getAbilities().flying = false;
			sp.onUpdateAbilities();
		});
	}
}
