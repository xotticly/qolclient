package com.example.client.module;

import com.example.client.config.ConfigManager;
import com.example.client.gui.ModuleScreen;
import com.example.client.SinglePlayer;
import com.example.client.modules.AimAssist;
import com.example.client.modules.AutoEat;
import com.example.client.modules.AutoRespawn;
import com.example.client.modules.AutoTool;
import com.example.client.modules.Flight;
import com.example.client.modules.Fullbright;
import com.example.client.modules.Hud;
import com.example.client.modules.NoFall;
import com.example.client.modules.Speed;
import com.example.client.modules.Sprint;
import com.example.client.modules.TriggerBot;
import com.example.client.modules.Zoom;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ModuleManager {
	private final List<Module> modules = new ArrayList<>();
	private final Map<Module, KeyMapping> keys = new LinkedHashMap<>();
	private KeyMapping guiKey;
	private boolean wasActive = false;

	public void init() {
		register(new Sprint());
		register(new Fullbright());
		register(new AutoRespawn());
		register(new AutoEat());
		register(new AutoTool());
		register(new Zoom());
		register(new Flight());
		register(new Speed());
		register(new NoFall());
		register(new TriggerBot());
		register(new AimAssist());
		register(new Hud());

		// Each module gets a rebindable key in Options > Controls > Key Binds (unbound by default).
		for (Module m : modules) {
			KeyMapping key = new KeyMapping(
					"key.qolclient." + m.getName().toLowerCase(),
					InputConstants.Type.KEYSYM,
					GLFW.GLFW_KEY_UNKNOWN,
					KeyMapping.Category.MISC);
			keys.put(m, KeyBindingHelper.registerKeyBinding(key));
		}

		guiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.qolclient.gui",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_RIGHT_SHIFT,
				KeyMapping.Category.MISC));

		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
	}

	private void register(Module m) { modules.add(m); }

	private void tick(Minecraft mc) {
		while (guiKey.consumeClick()) {
			if (mc.screen == null) mc.setScreen(new ModuleScreen(null));
		}
		for (Map.Entry<Module, KeyMapping> e : keys.entrySet()) {
			boolean toggled = false;
			while (e.getValue().consumeClick()) {
				e.getKey().toggle();
				toggled = true;
			}
			if (toggled) ConfigManager.save(this);
		}
		// Modules only run in singleplayer. Entering/leaving a singleplayer world
		// fires onEnable/onDisable for every enabled module.
		boolean active = isActive();
		if (active != wasActive) {
			wasActive = active;
			for (Module m : modules) {
				if (!m.isEnabled()) continue;
				if (active) m.onEnable(); else m.onDisable();
			}
		}
		if (!active || mc.player == null) return;
		for (Module m : modules) {
			if (m.isEnabled()) m.onTick(mc);
		}
	}

	/** True only while playing in a singleplayer world. */
	public boolean isActive() { return SinglePlayer.isActive(); }

	public List<Module> getModules() { return modules; }

	public List<Module> getEnabled() {
		List<Module> out = new ArrayList<>();
		for (Module m : modules) if (m.isEnabled()) out.add(m);
		return out;
	}
}
