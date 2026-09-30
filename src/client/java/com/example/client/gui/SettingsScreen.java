package com.example.client.gui;

import com.example.client.QolClient;
import com.example.client.config.ConfigManager;
import com.example.client.module.Module;
import com.example.client.settings.BoolSetting;
import com.example.client.settings.ModeSetting;
import com.example.client.settings.NumberSetting;
import com.example.client.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Settings for one module: toggles, mode cycling, and -/+ buttons for numbers. */
public class SettingsScreen extends Screen {
	private final Screen parent;
	private final Module module;

	public SettingsScreen(Screen parent, Module module) {
		super(Component.literal(module.getName() + " Settings"));
		this.parent = parent;
		this.module = module;
	}

	private static String fmt(double v) {
		return v == Math.rint(v) ? String.valueOf((long) v) : String.valueOf(v);
	}

	private static Component boolLabel(BoolSetting s) {
		return Component.literal(s.getName() + ": " + (s.get() ? "ON" : "OFF"));
	}

	private static Component modeLabel(ModeSetting s) {
		return Component.literal(s.getName() + ": " + s.get());
	}

	private static Component numLabel(NumberSetting s) {
		return Component.literal(s.getName() + ": " + fmt(s.get()));
	}

	@Override
	protected void init() {
		int cx = this.width / 2;
		int y = 36;

		for (Setting setting : module.getSettings()) {
			if (setting instanceof BoolSetting s) {
				this.addRenderableWidget(Button.builder(boolLabel(s), b -> {
					s.toggle();
					b.setMessage(boolLabel(s));
					ConfigManager.save(QolClient.MODULES);
				}).bounds(cx - 100, y, 200, 20).build());
			} else if (setting instanceof ModeSetting s) {
				this.addRenderableWidget(Button.builder(modeLabel(s), b -> {
					s.cycle();
					b.setMessage(modeLabel(s));
					ConfigManager.save(QolClient.MODULES);
				}).bounds(cx - 100, y, 200, 20).build());
			} else if (setting instanceof NumberSetting s) {
				Button label = Button.builder(numLabel(s), b -> {})
						.bounds(cx - 100, y, 154, 20).build();
				label.active = false;
				this.addRenderableWidget(label);

				this.addRenderableWidget(Button.builder(Component.literal("-"), b -> {
					s.set(s.get() - s.getStep());
					label.setMessage(numLabel(s));
					ConfigManager.save(QolClient.MODULES);
				}).bounds(cx + 58, y, 20, 20).build());

				this.addRenderableWidget(Button.builder(Component.literal("+"), b -> {
					s.set(s.get() + s.getStep());
					label.setMessage(numLabel(s));
					ConfigManager.save(QolClient.MODULES);
				}).bounds(cx + 80, y, 20, 20).build());
			}
			y += 24;
		}

		this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> this.onClose())
				.bounds(cx - 50, this.height - 28, 100, 20).build());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.render(graphics, mouseX, mouseY, delta);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFFFF);
	}

	@Override
	public void onClose() {
		ConfigManager.save(QolClient.MODULES);
		Minecraft.getInstance().setScreen(parent);
	}
}
