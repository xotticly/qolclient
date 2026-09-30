package com.example.client.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting {
	private final List<String> modes;
	private int index;

	public ModeSetting(String name, String defaultMode, String... options) {
		super(name);
		this.modes = Arrays.asList(options);
		this.index = Math.max(0, modes.indexOf(defaultMode));
	}

	public String get() { return modes.get(index); }
	public List<String> getModes() { return modes; }
	public void cycle() { index = (index + 1) % modes.size(); }

	public void set(String mode) {
		int i = modes.indexOf(mode);
		if (i != -1) index = i;
	}

	@Override
	public JsonElement serialize() { return new JsonPrimitive(get()); }

	@Override
	public void deserialize(JsonElement e) {
		if (e != null && e.isJsonPrimitive()) set(e.getAsString());
	}
}
