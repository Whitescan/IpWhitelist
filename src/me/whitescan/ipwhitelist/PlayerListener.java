package me.whitescan.ipwhitelist;

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
	private Map<UUID, InetAddress> addresses = new HashMap<UUID, InetAddress>();

	public PlayerListener(IpWhitelist plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerChannelRegistered(PlayerRegisterChannelEvent ev) {
		if (this.plugin.getConfig().getBoolean("setup", false) && ev.getChannel().equals("BungeeCord")) {
			this.plugin.getConfig().set("whitelist", Lists.newArrayList(
					(Object[]) new String[] { this.addresses.get(ev.getPlayer().getUniqueId()).getHostAddress() }));
			this.plugin.getConfig().set("setup", false);
			this.plugin.saveConfig();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerLogin(final PlayerLoginEvent ev) {
		final InetAddress addr = ev.getRealAddress();
		this.plugin.debug("Player " + ev.getPlayer().getName() + " is connecting with IP : " + addr);
		if (this.plugin.getConfig().getBoolean("setup", false)) {
			this.addresses.put(ev.getPlayer().getUniqueId(), addr);
			this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {

				@Override
				public void run() {
					if (PlayerListener.this.plugin.getConfig().getBoolean("setup", false)) {
						ev.getPlayer().kickPlayer("Server is in setup mode");
					} else if (!PlayerListener.this.plugin.allow(addr)) {
						ev.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&',
								PlayerListener.this.plugin.getConfig().getString("playerKickMessage")));
					}
				}
			}, 20L);
		} else if (!this.plugin.allow(addr)) {
			ev.setKickMessage(ChatColor.translateAlternateColorCodes('&',
					this.plugin.getConfig().getString("playerKickMessage")));
			ev.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
		}
	}

}
