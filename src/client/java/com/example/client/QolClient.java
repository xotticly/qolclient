package com.example.client;

import com.example.client.config.ConfigManager;
import com.example.client.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QolClient implements ClientModInitializer {
	public static final String MOD_ID = "qolclient";
	public static final String NAME = "QoL Client";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ModuleManager MODULES = new ModuleManager();

	@Override
	public void onInitializeClient() {
		MODULES.init();
		ConfigManager.load(MODULES);
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save(MODULES));
		LOGGER.info("{} loaded with {} modules", NAME, MODULES.getModules().size());
	}
}
