package com.example.client.module;

import com.example.client.settings.Setting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

public abstract class Module {
	public enum Category { MOVEMENT, RENDER, MISC }

	private final String name;
	private final String description;
	private final Category category;
	private final List<Setting> settings = new ArrayList<>();
	private boolean enabled;

	protected Module(String name, String description, Category category) {
		this.name = name;
		this.description = description;
		this.category = category;
	}

	protected <T extends Setting> T add(T setting) {
		settings.add(setting);
		return setting;
	}

	public void toggle() { setEnabled(!enabled); }

	public void setEnabled(boolean state) {
		if (state == enabled) return;
		enabled = state;
		if (enabled) onEnable(); else onDisable();
	}

	/** Called every client tick while enabled and a player exists. */
	public void onTick(Minecraft mc) {}
	public void onEnable() {}
	public void onDisable() {}

	public String getName() { return name; }
	public String getDescription() { return description; }
	public Category getCategory() { return category; }
	public boolean isEnabled() { return enabled; }
	public List<Setting> getSettings() { return settings; }
}
