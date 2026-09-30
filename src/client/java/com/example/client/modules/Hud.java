package com.example.client.modules;

import com.example.client.QolClient;
import com.example.client.module.Module;
import com.example.client.settings.BoolSetting;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class Hud extends Module {
	private static final int WHITE = 0xFFFFFFFF;
	private static final int PURPLE = 0xFF8C00FF;

	private final BoolSetting watermark = new BoolSetting("Watermark", true);
	private final BoolSetting arrayList = new BoolSetting("Module List", true);
	private final BoolSetting coords = new BoolSetting("Coordinates", true);

	public Hud() {
		super("HUD", "Watermark, module list and coordinates", Category.RENDER);
		add(watermark);
		add(arrayList);
		add(coords);
		setEnabled(true);

		HudRenderCallback.EVENT.register((graphics, tickCounter) -> {
			if (!isEnabled()) return;
			Minecraft mc = Minecraft.getInstance();
			if (mc.player == null || mc.options.hideGui || !QolClient.MODULES.isActive()) return;

			int y = 4;
			if (watermark.get()) {
				graphics.drawString(mc.font, QolClient.NAME, 4, y, PURPLE, true);
				y += 12;
			}
			if (arrayList.get()) {
				List<String> names = new ArrayList<>();
				for (Module m : QolClient.MODULES.getEnabled()) {
					if (m != this) names.add(m.getName());
				}
				names.sort((a, b) -> Integer.compare(mc.font.width(b), mc.font.width(a)));
				for (String n : names) {
					graphics.drawString(mc.font, n, 4, y, WHITE, true);
					y += 10;
				}
			}
			if (coords.get()) {
				String text = String.format("XYZ %.1f / %.1f / %.1f",
						mc.player.getX(), mc.player.getY(), mc.player.getZ());
				int h = mc.getWindow().getGuiScaledHeight();
				graphics.drawString(mc.font, text, 4, h - 12, WHITE, true);
			}
		});
	}
}
