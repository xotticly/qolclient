package com.example.client.modules;

import com.example.client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Client-side night vision. Note: turning it off also clears a real night vision potion. */
public class Fullbright extends Module {
	public Fullbright() {
		super("Fullbright", "See in the dark", Category.RENDER);
	}

	@Override
	public void onTick(Minecraft mc) {
		if (mc.player == null) return;
		mc.player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
	}

	@Override
	public void onDisable() {
		var player = Minecraft.getInstance().player;
		if (player != null) player.removeEffect(MobEffects.NIGHT_VISION);
	}
}
