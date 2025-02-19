package me.reecepbcups.core;

import me.reecepbcups.tools.Main;
import me.reecepbcups.utiltools.Util;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Top implements CommandExecutor{//,TabCompleter,Listener {

	String Section, Permission;
	private final Main plugin;
	public Top(Main instance) {
		plugin = instance;
		
		
		Section = "Core.Top";        

		// https://essinfo.xeya.me/permissions.html
		if(plugin.enabledInConfig(Section+".Enabled")) {
			plugin.getCommand("top").setExecutor(this);
			Permission = plugin.getConfig().getString(Section+".Permission");
		}
		
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!sender.hasPermission(Permission)) {
			sender.sendMessage(Util.color("&cYou do not have access to &n/" +label+"&c."));
			return true;
		} 

		Player p = (Player)sender;
		Block block = p.getWorld().getHighestBlockAt(p.getLocation());
	    p.teleport(block.getLocation().add(0, 1, 0));
		Util.coloredMessage(p, Main.lang("TOP_TELEPORT").replace("%block%", block.getY()+""));
		
		return true;
	}
}
