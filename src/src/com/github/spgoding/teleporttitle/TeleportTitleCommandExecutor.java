package com.github.spgoding.teleporttitle;

import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportTitleCommandExecutor implements CommandExecutor {
	private final TeleportTitle plugin;
	private Hashtable<CommandSender, GoingToDel> goingToDels = new Hashtable<>();
	
	public TeleportTitleCommandExecutor(TeleportTitle plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * 监测到玩家执行命令
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Util.sendSepratedLine(sender);
		if (label.equalsIgnoreCase("telet") || label.equalsIgnoreCase("teleporttitle")) {
			// 是劳资的命令
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("add")) {
					// 劳资要加title
					return runAdd(sender, args);
				} else if (args[0].equalsIgnoreCase("del")) {
					// 删title
					return runDel(sender, args);
				} else if (args[0].equalsIgnoreCase("list")) {
					// 列title
					return runList(sender, args);
				} else if (args[0].equalsIgnoreCase("confirm")) {
					// 确认删除
					Enumeration<CommandSender> e = goingToDels.keys();
				    while(e.hasMoreElements()) {
				    	if (e.nextElement() == sender) {
				    		// 这个是该sender要删的
							if (System.currentTimeMillis() <= goingToDels.get(sender).getTime()) {
								// 时间还Okay，可以删
								delTitles(goingToDels.get(sender).getLocation(), sender);
							} else {
								sender.sendMessage("§c已经超过确认时间");
							}
							goingToDels.remove(sender);
							Util.sendSepratedLine(sender);
							return true;
				    	}
				    }
					sender.sendMessage("§c还未使用过 §6/telet del §c命令");
					Util.sendSepratedLine(sender);
					return true;
				}
			}
		}
		
		return false;	
	}

	/**
	 * 执行加Title的命令
	 * @param sender 命令执行者
	 * @param args 参数
	 * @return false将会显示usage
	 */
	private boolean runAdd(CommandSender sender, String[] args) {
		switch (args.length) {
		case 2:
			// telet add <标题Json文本>
			if (sender instanceof Player) {
				addTitle(args[1], (Player) sender);
			} else {
				sender.sendMessage("§c只有玩家可以使用不带具体坐标的命令");
			}
			
			Util.sendSepratedLine(sender);			
			return true;
		case 5:
			// telet add <标题Json文本> [x y z]
			if (sender instanceof Player) {
				addTitle(args[1], 
						Util.getLocation(
								((Player) sender).getWorld(), 
								args[2], args[3], args[4], sender
								),
						sender);
			} else {
				sender.sendMessage("§c只有玩家可以使用不带具体世界的命令");
			}
			
			Util.sendSepratedLine(sender);			
			return true;
		case 6:
			// telet add <标题Json文本> [x y z] [世界]
			addTitle(args[1], 
					Util.getLocation(
							Util.getWorld(args[5], sender), 
							args[2], args[3], args[4], sender
							),
					sender);
			
			Util.sendSepratedLine(sender);			
			return true;
		}
		
		Util.sendSepratedLine(sender);		
		return false;
	}
	
	/**
	 * 根据玩家添加标题
	 * @param json 标题信息
	 * @param p 玩家
	 */
	private void addTitle(String json, Player p) {
		Location loc = p.getLocation().getBlock().getLocation();
		addTitle(json, loc, p);
	}
	
	/**
	 * 根据位置添加标题
	 * @param json 标题信息
	 * @param loc 位置
	 * @param sender 命令的执行者
	 */
	private void addTitle(String json, Location loc, CommandSender sender) {
		if (loc != null) {			
			// 添加
			plugin.getJsons().add(json);
			plugin.getLocations().add(loc);
			
			// 显示消息
			sender.sendMessage("§b已成功添加标题 §6" + json + "§b 到位置 §6" + 
			Util.locationToString(loc));
			if (sender instanceof Player) {
				Util.showTitleOnScreen(((Player) sender), json);
				sender.sendMessage("§c如果您的屏幕上正确显示出了标题则表明无误！");
				sender.sendMessage("§c否则请您检查标题Json的正确性");
			}
		}
	}

	/**
	 * 执行删Title的命令
	 * @param sender 命令执行者
	 * @param args 参数
	 * @return false将会显示usage
	 */
	private boolean runDel(CommandSender sender, String[] args) {
		GoingToDel gtd = new GoingToDel();
		
		switch (args.length) {
		case 1:
			// telet del
			if (sender instanceof Player) {
				gtd.setLocation(((Player) sender).getLocation().getBlock().getLocation());
			} else {
				sender.sendMessage("§c只有玩家可以使用不带具体坐标的命令");
				Util.sendSepratedLine(sender);
				return true;
			}
			
			break;			
		case 4:
			// telet del [x y z]
			if (sender instanceof Player) {
				gtd.setLocation(
						Util.getLocation(
							((Player) sender).getWorld(),
							args[1], args[2], args[3], sender
						)
					);
			} else {
				sender.sendMessage("§c只有玩家可以使用不带具体世界的命令");
				Util.sendSepratedLine(sender);
				return true;
			}
			
			break;			
		case 5:
			// telet del [x y z] [世界]
			gtd.setLocation(
					Util.getLocation(
							Util.getWorld(args[4], sender),
							args[1], args[2], args[3], sender
						)
					);
			
			break;
		default:
			return false;
		}
		
		gtd.setTime(System.currentTimeMillis() + 30000);
		// 循环，如果该Sender之前有一个要确认的删除没有管，把那个删除删掉
		Enumeration<CommandSender> e = goingToDels.keys();
	    while(e.hasMoreElements()) {
	    	if (e.nextElement() == sender) goingToDels.remove(sender);
	    }
		goingToDels.put(sender, gtd);
		sender.sendMessage("§c请在 §630秒内 §c执行命令 §6/telet confirm §c确认删除位于" +
				Util.locationToString(gtd.getLocation()) + " §c的§4§l所有§c传送标题");
		Util.sendSepratedLine(sender);
		return true;
	}
	
	/**
	 * 删除指定位置的标题
	 * @param loc 指定位置
	 * @param sender 命令执行者
	 */
	private void delTitles (Location loc, CommandSender sender) {
		if (loc != null) {
			int count = 0;
			for (int i = 0; i < plugin.getLocations().size(); i++) {
				Location item = plugin.getLocations().get(i);
				if (item.equals(loc)) {
					// 直接删除
					plugin.getLocations().remove(i);
					plugin.getJsons().remove(i);
					i -= 1;
					count++;
				}
			}
			if (count > 0) {
				sender.sendMessage("§b已成功删除位于 §6" + Util.locationToString(loc) +
						" §b的 §6" + Integer.toString(count) + " §b个传送标题");
			} else {
				sender.sendMessage("§c没有在 §6" + Util.locationToString(loc) +
						" §c找到任何传送标题");
			}
		}
	}
	
	/**
	 * 执行列出所有title的命令
	 * @param sender 命令执行者
	 * @param args 参数
	 * @return false将会显示usage
	 */
	private boolean runList(CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			// telet list
			listTitles(sender);
			
			Util.sendSepratedLine(sender);			
			return true;
		case 2:
			// telet list [世界]
			listTitles(sender, Util.getWorld(args[1], sender));
			
			Util.sendSepratedLine(sender);			
			return true;
		case 5:
			// telet list [世界] [x y z]
			listTitles(sender, 
					Util.getLocation(
							Util.getWorld(args[1], sender),
							args[2], args[3], args[4], sender)
					);
			
			Util.sendSepratedLine(sender);			
			return true;
		}
		
		Util.sendSepratedLine(sender);
		return false;
	}
	
	/**
	 * 对指定sender列出所有传送标题
	 * @param sender 指定sender
	 */
	private void listTitles(CommandSender sender) {
		int cnt = 0;
		for (int i = 0; i < plugin.getLocations().size(); i++) {
			sender.sendMessage("§6" +
					Util.locationToString(plugin.getLocations().get(i)) +
					"§7:§b " + plugin.getJsons().get(i));
			cnt++;
		}
		sender.sendMessage("§9服务器中共 §b" + Integer.toString(cnt) + "个 §9传送标题");
	}
	
	/**
	 * 对指定sender列出指定世界的传送标题
	 * @param sender 指定sender
	 */
	private void listTitles(CommandSender sender, World world) {
		if (world != null){
			int cnt = 0;
			for (int i = 0; i < plugin.getLocations().size(); i++) {
				Location iLoc = plugin.getLocations().get(i);
				if (iLoc.getWorld() == world) {
					sender.sendMessage(
							"§6" + Util.locationToString(plugin.getLocations().get(i)) +
							"§7:§b " + plugin.getJsons().get(i));
					cnt++;
				}
			}
			sender.sendMessage("§b世界 §6" + world.getName() + " §9中共 §b" +
			Integer.toString(cnt) + "个 §9传送标题");
		}
	}
	
	/**
	 * 对指定sender列出指定位置的传送标题
	 * @param sender 指定sender
	 */
	private void listTitles(CommandSender sender, Location loc) {
		if (loc != null) {
			int cnt = 0;
			for (int i = 0; i < plugin.getLocations().size(); i++) {
				Location iLoc = plugin.getLocations().get(i);
				if (loc.equals(iLoc)) {
					sender.sendMessage("§6" + Util.locationToString(plugin.getLocations().get(i)) +
							"§7:§b " + plugin.getJsons().get(i));
					cnt++;
				}
			}
			sender.sendMessage("§b位置 §6" + Util.locationToString(loc) + " §9中共 §b" +
			Integer.toString(cnt) + "个 §9传送标题");
		}
	}

}
