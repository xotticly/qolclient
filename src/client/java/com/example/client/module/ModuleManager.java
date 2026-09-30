package com.example.client.module;

import com.example.client.config.ConfigManager;
import com.example.client.modules.AutoRespawn;
import com.example.client.modules.Fullbright;
import com.example.client.modules.Hud;
import com.example.client.modules.Sprint;
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

	public void init() {
		register(new Sprint());
		register(new Fullbright());
		register(new AutoRespawn());
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

		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
	}

	private void register(Module m) { modules.add(m); }

	private void tick(Minecraft mc) {
		for (Map.Entry<Module, KeyMapping> e : keys.entrySet()) {
			boolean toggled = false;
			while (e.getValue().consumeClick()) {
				e.getKey().toggle();
				toggled = true;
			}
			if (toggled) ConfigManager.save(this);
		}
		if (mc.player == null) return;
		for (Module m : modules) {
			if (m.isEnabled()) m.onTick(mc);
		}
	}

	public List<Module> getModules() { return modules; }

	public List<Module> getEnabled() {
		List<Module> out = new ArrayList<>();
		for (Module m : modules) if (m.isEnabled()) out.add(m);
		return out;
	}
}
