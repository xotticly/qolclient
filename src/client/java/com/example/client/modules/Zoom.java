package com.example.client.modules;

import com.example.client.module.Module;
import com.example.client.settings.NumberSetting;
import net.minecraft.client.Minecraft;

/** Toggle-style zoom: lowers your FOV while enabled, restores it when disabled. Bind it to a key like C. */
public class Zoom extends Module {
	private final NumberSetting fov = add(new NumberSetting("Zoom FOV", 30, 30, 90, 5));
	private int previousFov = -1;

	public Zoom() {
		super("Zoom", "Zoom in (toggle)", Category.RENDER);
	}

	@Override
	public void onEnable() {
		var mc = Minecraft.getInstance();
		if (mc.options == null) return;
		previousFov = mc.options.fov().get();
		mc.options.fov().set(fov.getInt());
	}

	@Override
	public void onTick(Minecraft mc) {
		// Follow the setting if it changes while zoomed.
		if (previousFov != -1 && mc.options.fov().get() != fov.getInt()) {
			mc.options.fov().set(fov.getInt());
		}
	}

	@Override
	public void onDisable() {
		var mc = Minecraft.getInstance();
		if (mc.options != null && previousFov != -1) {
			mc.options.fov().set(previousFov);
		}
		previousFov = -1;
	}
}
