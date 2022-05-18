package de.whitescan.ipwhitelist;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

/**
 * 
 * @author Whitescan
 *
 */
public class IpWhitelist extends JavaPlugin {

	@Getter
	private List<String> bungeeIps = new ArrayList<>();

	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		reloadBukkitConfig();
		if (!(!getConfig().getBoolean("setup", false)
				|| getBungeeIps().isEmpty() && getConfig().getStringList("whitelist").isEmpty())) {
			getConfig().set("setup", false);
			saveConfig();
		}

		getCommand("ipwhitelist").setExecutor(new IpWhitelistCommand(this));

	}

	public void reloadBukkitConfig() {
		bungeeIps.clear();
		File spigotyml = new File(getDataFolder().getParentFile().getParentFile(), "spigot.yml");
		File bukkityml = new File(getDataFolder().getParentFile().getParentFile(), "bukkit.yml");
		if (spigotyml.exists()) {
			YamlConfiguration spigotcfg = YamlConfiguration.loadConfiguration(spigotyml);
			if (spigotcfg.getBoolean("settings.bungeecord")) {
				bungeeIps.addAll(spigotcfg.getStringList("settings.bungeecord-addresses"));
			}
		} else if (bukkityml.exists()) {
			YamlConfiguration bukkitcfg = YamlConfiguration
					.loadConfiguration(new File(getDataFolder().getParentFile().getParentFile(), "bukkit.yml"));
			bungeeIps.addAll(bukkitcfg.getStringList("settings.bungee-proxies"));
		}
	}

	public String getTag() {
		return ChatColor.ITALIC.toString() + ChatColor.GREEN + "[" + ChatColor.AQUA + getName() + ChatColor.GREEN + "] "
				+ ChatColor.RESET;
	}

	public boolean allow(String ip) {
		return bungeeIps.contains(ip) || getConfig().getStringList("whitelist").contains(ip);
	}

	public boolean allow(InetSocketAddress addr) {
		return allow(addr.getAddress().getHostAddress());
	}

	public boolean allow(InetAddress addr) {
		return allow(addr.getHostAddress());
	}

	public void whitelist(InetSocketAddress ip) {
		whitelist(ip.getAddress().getHostAddress());
	}

	public void whitelist(String ip) {
		getConfig().getStringList("whitelist").add(ip);
		saveConfig();
	}

	public void debug(String s) {
		if (getConfig().getBoolean("debug", false)) {
			getLogger().log(Level.INFO, s);
		}
	}
}
