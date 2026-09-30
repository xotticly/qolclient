package com.example.client.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class NumberSetting extends Setting {
	private double value;
	private final double min, max, step;

	public NumberSetting(String name, double defaultValue, double min, double max, double step) {
		super(name);
		this.min = min;
		this.max = max;
		this.step = step;
		set(defaultValue);
	}

	public double get() { return value; }
	public int getInt() { return (int) value; }
	public double getMin() { return min; }
	public double getMax() { return max; }
	public double getStep() { return step; }

	public void set(double v) {
		v = Math.max(min, Math.min(max, v));
		v = Math.round(v / step) * step;
		value = Math.round(v * 1000.0) / 1000.0;
	}

	@Override
	public JsonElement serialize() { return new JsonPrimitive(value); }

	@Override
	public void deserialize(JsonElement e) {
		if (e != null && e.isJsonPrimitive()) set(e.getAsDouble());
	}
}
