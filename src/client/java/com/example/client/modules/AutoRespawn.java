package com.example.client.modules;

import com.example.client.module.Module;
import net.minecraft.client.Minecraft;

public class AutoRespawn extends Module {
	public AutoRespawn() {
		super("AutoRespawn", "Respawn instantly on death", Category.MISC);
	}

	@Override
	public void onTick(Minecraft mc) {
		if (mc.player != null && mc.player.isDeadOrDying()) {
			mc.player.respawn();
		}
	}
}
