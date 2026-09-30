package com.example.client;

import com.example.client.config.AllowedServers;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;

/** Helpers for the integrated (singleplayer) server. Everything here is a no-op on multiplayer. */
public final class SinglePlayer {
	private SinglePlayer() {}

	/** True in a private singleplayer world (not opened to LAN), or on a multiplayer server you've allowed. */
	public static boolean isActive() {
		Minecraft mc = Minecraft.getInstance();
		var server = mc.getSingleplayerServer();
		if (server != null) return !server.isPublished();
		var data = mc.getCurrentServer();
		return data != null && AllowedServers.contains(data.ip);
	}

	/** Runs an action on your ServerPlayer, on the server thread. Does nothing outside singleplayer. */
	public static void runOnServer(Minecraft mc, Consumer<ServerPlayer> action) {
		var server = mc.getSingleplayerServer();
		if (server == null || mc.player == null) return;
		UUID id = mc.player.getUUID();
		server.execute(() -> {
			ServerPlayer sp = server.getPlayerList().getPlayer(id);
			if (sp != null) action.accept(sp);
		});
	}
}
