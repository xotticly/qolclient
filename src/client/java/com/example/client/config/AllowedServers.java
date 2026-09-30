package com.example.client.config;

import java.util.LinkedHashSet;
import java.util.Set;

/** Multiplayer servers you've explicitly allowed the mod to run on (e.g. a private server with friends). */
public final class AllowedServers {
	private static final Set<String> SERVERS = new LinkedHashSet<>();

	private AllowedServers() {}

	private static String norm(String ip) { return ip == null ? "" : ip.trim().toLowerCase(); }

	public static boolean contains(String ip) { return SERVERS.contains(norm(ip)); }

	public static void add(String ip) { if (!norm(ip).isEmpty()) SERVERS.add(norm(ip)); }

	public static boolean toggle(String ip) {
		String key = norm(ip);
		if (key.isEmpty()) return false;
		if (!SERVERS.remove(key)) {
			SERVERS.add(key);
			return true;
		}
		return false;
	}

	public static Set<String> all() { return SERVERS; }
}
