package com.example.client.gui;

import com.example.client.QolClient;
import com.example.client.config.AllowedServers;
import com.example.client.config.ConfigManager;
import com.example.client.module.Module;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Main menu: one toggle button per module plus a "..." button for its settings. */
public class ModuleScreen extends Screen {
	private final Screen parent;

	public ModuleScreen(Screen parent) {
		super(Component.literal("QoL Client"));
		this.parent = parent;
	}

	static Component label(Module m) {
		Component state = m.isEnabled()
				? Component.literal("ON").withStyle(ChatFormatting.GREEN)
				: Component.literal("OFF").withStyle(ChatFormatting.RED);
		return Component.literal(m.getName() + ": ").append(state);
	}

	private static Component allowLabel(String ip) {
		return Component.literal("Allow this server: " + (AllowedServers.contains(ip) ? "ON" : "OFF"));
	}

	@Override
	protected void init() {
		List<Module> mods = QolClient.MODULES.getModules();
		int colW = 180;
		int rowH = 24;
		int top = 36;
		int perCol = Math.max(1, (this.height - top - 80) / rowH);
		int cols = Math.max(1, (mods.size() + perCol - 1) / perCol);
		int startX = this.width / 2 - (cols * colW) / 2;

		for (int i = 0; i < mods.size(); i++) {
			Module m = mods.get(i);
			int x = startX + (i / perCol) * colW;
			int y = top + (i % perCol) * rowH;

			Button toggle = Button.builder(label(m), b -> {
				m.toggle();
				b.setMessage(label(m));
				ConfigManager.save(QolClient.MODULES);
			}).bounds(x, y, 130, 20).build();
			this.addRenderableWidget(toggle);

			Button cfg = Button.builder(Component.literal("..."), b ->
					Minecraft.getInstance().setScreen(new SettingsScreen(this, m)))
					.bounds(x + 134, y, 24, 20).build();
			cfg.active = !m.getSettings().isEmpty();
			this.addRenderableWidget(cfg);
		}

		var serverData = Minecraft.getInstance().getCurrentServer();
		if (serverData != null && Minecraft.getInstance().getSingleplayerServer() == null) {
			String ip = serverData.ip;
			this.addRenderableWidget(Button.builder(allowLabel(ip), b -> {
				AllowedServers.toggle(ip);
				b.setMessage(allowLabel(ip));
				ConfigManager.save(QolClient.MODULES);
			}).bounds(this.width / 2 - 100, this.height - 52, 200, 20).build());
		}

		this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> this.onClose())
				.bounds(this.width / 2 - 50, this.height - 28, 100, 20).build());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.render(graphics, mouseX, mouseY, delta);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFFFF);
		if (!QolClient.MODULES.isActive()) {
			graphics.drawCenteredString(this.font,
					Component.literal(Minecraft.getInstance().getCurrentServer() != null
							? "Modules are paused - this server isn't on your allowed list"
							: "Not in singleplayer - modules are paused"),
					this.width / 2, this.height - 70, 0xFFFF5555);
		}
	}

	@Override
	public void onClose() {
		ConfigManager.save(QolClient.MODULES);
		Minecraft.getInstance().setScreen(parent);
	}
}
