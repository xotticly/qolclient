package com.example.client.modules;

import com.example.client.SinglePlayer;
import com.example.client.module.Module;
import net.minecraft.client.Minecraft;

/** Cancels fall damage by resetting fall distance on your singleplayer server. */
public class NoFall extends Module {
	public NoFall() {
		super("NoFall", "No fall damage", Category.MOVEMENT);
	}

	@Override
	public void onTick(Minecraft mc) {
		if (mc.player == null) return;
		mc.player.resetFallDistance();
		SinglePlayer.runOnServer(mc, sp -> sp.resetFallDistance());
	}
}
