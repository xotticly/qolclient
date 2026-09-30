package com.example.client.settings;

import com.google.gson.JsonElement;

public abstract class Setting {
	private final String name;

	protected Setting(String name) { this.name = name; }

	public String getName() { return name; }

	public abstract JsonElement serialize();

	public abstract void deserialize(JsonElement element);
}
