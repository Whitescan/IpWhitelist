package de.whitescan.ipwhitelist;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

import com.google.common.collect.Lists;

/**
 * 
 * @author Whitescan
 *
 */
public class PlayerListener implements Listener {

	private IpWhitelist plugin;

	private Map<UUID, InetAddress> addresses = new HashMap<>();

	public PlayerListener(IpWhitelist plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerChannelRegistered(PlayerRegisterChannelEvent ev) {
		if (plugin.getConfig().getBoolean("setup", false) && ev.getChannel().equals("BungeeCord")) {
			plugin.getConfig().set("whitelist", Lists.newArrayList(
					(Object[]) new String[] { addresses.get(ev.getPlayer().getUniqueId()).getHostAddress() }));
			plugin.getConfig().set("setup", false);
			plugin.saveConfig();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLogin(final PlayerLoginEvent ev) {
		final InetAddress addr = ev.getRealAddress();
		plugin.debug("Player " + ev.getPlayer().getName() + " is connecting with IP : " + addr);
		if (plugin.getConfig().getBoolean("setup", false)) {
			addresses.put(ev.getPlayer().getUniqueId(), addr);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				@Override
				public void run() {
					if (plugin.getConfig().getBoolean("setup", false)) {
						ev.getPlayer().kickPlayer("Server is in setup mode");
					} else if (!plugin.allow(addr)) {
						ev.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&',
								plugin.getConfig().getString("playerKickMessage")));
					}
				}
			}, 20L);
		} else if (!plugin.allow(addr)) {
			ev.setKickMessage(
					ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("playerKickMessage")));
			ev.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
		}
	}

}
