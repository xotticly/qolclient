package com.example.client.config;

import com.example.client.QolClient;
import com.example.client.module.Module;
import com.example.client.module.ModuleManager;
import com.example.client.settings.Setting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve("qolclient.json");
	}

	public static void save(ModuleManager manager) {
		JsonObject root = new JsonObject();
		for (Module m : manager.getModules()) {
			JsonObject obj = new JsonObject();
			obj.addProperty("enabled", m.isEnabled());
			JsonObject settings = new JsonObject();
			for (Setting s : m.getSettings()) settings.add(s.getName(), s.serialize());
			obj.add("settings", settings);
			root.add(m.getName(), obj);
		}
		JsonArray servers = new JsonArray();
		for (String ip : AllowedServers.all()) servers.add(ip);
		root.add("allowedServers", servers);
		try {
			Files.writeString(file(), GSON.toJson(root));
		} catch (IOException e) {
			QolClient.LOGGER.error("Could not save config", e);
		}
	}

	public static void load(ModuleManager manager) {
		Path path = file();
		if (!Files.exists(path)) {
			save(manager);
			return;
		}
		try {
			JsonObject root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
			if (root.has("allowedServers")) {
				for (var el : root.getAsJsonArray("allowedServers")) AllowedServers.add(el.getAsString());
			}
			for (Module m : manager.getModules()) {
				if (!root.has(m.getName())) continue;
				JsonObject obj = root.getAsJsonObject(m.getName());
				if (obj.has("settings")) {
					JsonObject settings = obj.getAsJsonObject("settings");
					for (Setting s : m.getSettings()) {
						if (settings.has(s.getName())) s.deserialize(settings.get(s.getName()));
					}
				}
				if (obj.has("enabled")) m.setEnabled(obj.get("enabled").getAsBoolean());
			}
		} catch (Exception e) {
			QolClient.LOGGER.error("Could not read config, using defaults", e);
		}
	}
}
