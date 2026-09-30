package com.example.client.modules;

import com.example.client.module.Module;
import com.example.client.settings.BoolSetting;
import net.minecraft.client.Minecraft;

public class Sprint extends Module {
	private final BoolSetting keepWhenBlocked = new BoolSetting("Ignore Collisions", false);

	public Sprint() {
		super("Sprint", "Always sprint while moving forward", Category.MOVEMENT);
		add(keepWhenBlocked);
	}

	@Override
	public void onTick(Minecraft mc) {
		var player = mc.player;
		if (player == null) return;
		if (!mc.options.keyUp.isDown()) return;
		if (player.isShiftKeyDown() || player.isUsingItem()) return;
		if (player.getFoodData().getFoodLevel() <= 6) return;
		if (player.horizontalCollision && !keepWhenBlocked.get()) return;
		player.setSprinting(true);
	}
}
