package de.whitescan.ipwhitelist;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.ChatPaginator;

import lombok.AllArgsConstructor;

/**
 * 
 * @author Whitescan
 *
 */
@AllArgsConstructor
public class IpWhitelistCommand implements CommandExecutor, TabExecutor {

	private IpWhitelist ipWhitelist;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("ipwhitelist.setup")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to perform this action.");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(ipWhitelist.getTag() + ChatColor.AQUA + "Commands : ");
			sender.sendMessage(ChatColor.AQUA + "/ipwhitelist list [page] - List whitelisted IPs");
			sender.sendMessage(ChatColor.AQUA + "/ipwhitelist addip <ip> - Add IP to whitelist");
			sender.sendMessage(ChatColor.AQUA + "/ipwhitelist remip <ip> - Removes IP to whitelist");
			sender.sendMessage(ChatColor.AQUA + "/ipwhitelist reload - Reload whitelist");
			return true;
		}
		if (args[0].equalsIgnoreCase("list")) {
			ChatPaginator.ChatPage page;
			sender.sendMessage(ipWhitelist.getTag() + ChatColor.AQUA + "Whitelisted IPs :");
			StringBuilder iplistbuff = new StringBuilder();
			for (String ip : ipWhitelist.getBungeeIps()) {
				iplistbuff.append(ChatColor.AQUA + ip + "\n");
			}
			for (String ip : ipWhitelist.getConfig().getStringList("whitelist")) {
				iplistbuff.append(ChatColor.AQUA + ip + "\n");
			}
			if (iplistbuff.length() > 0) {
				iplistbuff.deleteCharAt(iplistbuff.length() - 1);
			}
			String iplist = iplistbuff.toString();
			if (args.length > 1) {
				page = ChatPaginator.paginate(iplist, Integer.parseInt(args[1]), 55, 8);
				sender.sendMessage(page.getLines());
			} else {
				page = ChatPaginator.paginate(iplist, 1, 55, 8);
				sender.sendMessage(page.getLines());
			}
			sender.sendMessage(ChatColor.AQUA + "Page " + page.getPageNumber() + "/" + page.getTotalPages() + ".");
			return true;
		}
		if (args[0].equalsIgnoreCase("addip")) {
			if (args.length < 2) {
				sender.sendMessage(ipWhitelist.getTag() + ChatColor.AQUA + "Command usage : ");
				sender.sendMessage(ChatColor.AQUA + "/ipwhitelist addip <ip>");
				return true;
			}
			if (!ipWhitelist.getBungeeIps().contains(args[1])
					&& !ipWhitelist.getConfig().getStringList("whitelist").contains(args[1])) {
				List<String> whitelist = ipWhitelist.getConfig().getStringList("whitelist");
				whitelist.add(args[1]);
				ipWhitelist.getConfig().set("whitelist", whitelist);
				ipWhitelist.getConfig().set("setup", false);
				ipWhitelist.saveConfig();
				sender.sendMessage(
						ipWhitelist.getTag() + ChatColor.AQUA + "Successfully whitelisted IP " + args[1] + "!");
				return true;
			}
			sender.sendMessage(ipWhitelist.getTag() + ChatColor.AQUA + "IP " + args[1] + " was already whitelisted!");
			return true;
		}
		if (args[0].equalsIgnoreCase("remip")) {
			if (args.length < 2) {
				sender.sendMessage(ipWhitelist.getTag() + ChatColor.AQUA + "Command usage : ");
				sender.sendMessage(ChatColor.AQUA + "/ipwhitelist remip <ip>");
				return true;
			}
			List<String> whitelist = ipWhitelist.getConfig().getStringList("whitelist");
			if (whitelist.remove(args[1])) {
				ipWhitelist.getConfig().set("whitelist", whitelist);
				ipWhitelist.saveConfig();
				sender.sendMessage(
						ipWhitelist.getTag() + ChatColor.AQUA + "Successfully unwhitelisted IP " + args[1] + "!");
				return true;
			}
			if (ipWhitelist.getBungeeIps().contains(args[1])) {
				sender.sendMessage(ipWhitelist.getTag() + ChatColor.AQUA + "IP " + args[1]
						+ " is in your bukkit.yml or spigot.yml bungee-proxies. Remove it there!");
				return true;
			}
			sender.sendMessage(ipWhitelist.getTag() + ChatColor.AQUA + "IP " + args[1] + " was not whitelisted!");
			return true;
		}
		if (args[0].equalsIgnoreCase("reload")) {
			ipWhitelist.reloadConfig();
			ipWhitelist.reloadBukkitConfig();
			sender.sendMessage(ipWhitelist.getTag() + ChatColor.AQUA + "Successfully reloaded config!");
			return true;
		}
		if (args[0].equalsIgnoreCase("debug")) {
			ipWhitelist.getConfig().set("debug", (!ipWhitelist.getConfig().getBoolean("debug", false)));
			ipWhitelist.saveConfig();
			sender.sendMessage(ipWhitelist.getTag() + ChatColor.AQUA + "Debug mode : " + ChatColor.RED
					+ ipWhitelist.getConfig().getBoolean("debug"));
			return true;
		}
		if (args[0].equalsIgnoreCase("setup")) {
			if (!(ipWhitelist.getConfig().getBoolean("setup", false) || ipWhitelist.getBungeeIps().isEmpty()
					&& ipWhitelist.getConfig().getStringList("whitelist").isEmpty())) {
				sender.sendMessage(ipWhitelist.getTag() + ChatColor.RED
						+ "Cannot enable setup mode, some IPs are already whitelisted");
				return true;
			}
			ipWhitelist.getConfig().set("setup", (!ipWhitelist.getConfig().getBoolean("setup", false)));
			ipWhitelist.saveConfig();
			sender.sendMessage(ipWhitelist.getTag() + ChatColor.AQUA + "Setup mode : " + ChatColor.RED
					+ ipWhitelist.getConfig().getBoolean("setup"));
			return true;
		}
		sender.sendMessage(ipWhitelist.getTag() + ChatColor.AQUA + "Commands : ");
		sender.sendMessage(ChatColor.AQUA + "/ipwhitelist list [page] - List whitelisted IPs");
		sender.sendMessage(ChatColor.AQUA + "/ipwhitelist addip <ip> - Add IP to whitelist");
		sender.sendMessage(ChatColor.AQUA + "/ipwhitelist remip <ip> - Removes IP to whitelist");
		sender.sendMessage(ChatColor.AQUA + "/ipwhitelist reload - Reload whitelist");
		sender.sendMessage(ChatColor.AQUA + "/ipwhtelist debug - Toggles debug state");
		sender.sendMessage(ChatColor.AQUA + "/ipwhitelist setup - Turn setup mode on");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

		List<String> options = new ArrayList<>();

		if (sender.hasPermission("ipwhitelist.setup")) {

			if (args.length == 1) {
				options.add("list");
				options.add("addip");
				options.add("remip");
				options.add("reload");
				options.add("debug");
				options.add("setup");
			}

		}

		return options;
	}
}
