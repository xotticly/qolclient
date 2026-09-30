package com.example.client.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BoolSetting extends Setting {
	private boolean value;

	public BoolSetting(String name, boolean defaultValue) {
		super(name);
		this.value = defaultValue;
	}

	public boolean get() { return value; }
	public void set(boolean v) { value = v; }
	public void toggle() { value = !value; }

	@Override
	public JsonElement serialize() { return new JsonPrimitive(value); }

	@Override
	public void deserialize(JsonElement e) {
		if (e != null && e.isJsonPrimitive()) value = e.getAsBoolean();
	}
}
