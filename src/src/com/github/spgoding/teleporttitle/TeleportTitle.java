/* 列限制100
 * Google规范？管它的
 */
package com.github.spgoding.teleporttitle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Teleport Title的主类
 */
public class TeleportTitle extends JavaPlugin implements Listener {
	private List<Location> locations = new ArrayList<>();
	private List<String> jsons = new ArrayList<>();
	
	/**
	 * 插件加载时
	 */
	@Override
	public void onEnable() {
		registerListenrs();
		completeFiles();
		reloadConfig();
		readConfig();
	}

	/**
	 * 注册Listeners
	 */
	private void registerListenrs() {
		getServer().getPluginManager().registerEvents(this, this);
	}

	/**
	 * 补全目录
	 */
	private void completeFiles() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		File config = new File(getDataFolder(), "config.yml");
		if (!config.exists()) {
			saveDefaultConfig();
		}
	}
	
	/**
	 * 读取配置文件
	 */
	private void readConfig() {
		jsons.clear();
		locations.clear();
		if (getConfig().contains("messages")){
			for (String str: getConfig().getStringList("messages")) {
				String[] strs = str.split("\\|");
				if (strs.length >= 2){
					String[] locs = strs[0].split(",");
					if (locs.length == 4){
						// 加载Location和Message
						Location loc = new Location(
								Bukkit.getWorld(locs[0]), 
								Double.parseDouble(locs[1]), 
								Double.parseDouble(locs[2]), 
								Double.parseDouble(locs[3]));
						locations.add(loc);
						// 删掉除了第一个以外的，这样原 Json 里可以有|符号
						jsons.add(str.replaceFirst(strs[0] + "\\|", ""));
					} else {
						getLogger().info("[CONFIG]More than 4 to description a location.");
					}
				} else {
					getLogger().info("[CONFIG]More than 2 to description a message.");
				}
			}
		}
	}
	
	/**
	 * 监听到传送事件，显示title
	 * @param e 事件
	 */
	@EventHandler
	public void sendTitle(PlayerTeleportEvent e){
		Player p = e.getPlayer();
		Location toLoc = e.getTo();
		List<String> msgs = new ArrayList<>();
		for (int i = 0; i < locations.size(); i++) {
			Location loc = locations.get(i);
			if (isLocationsEqual(loc, toLoc)){
				// 传送后坐标符合该条配置
				
				// 替换变量
				String msg = jsons.get(i)
						.replaceAll("%PLAYER%", p.getName())
						.replaceAll("%WORLD%", loc.getWorld().getName());
				
				// 将可发送的msg添加至msg
				msgs.add(msg);
			}
		}
		
		// 从msgs里随机抽取标题并发送
		int index = getRandom(0, msgs.size() - 1);
		showTitleOnScreen(p, msgs.get(index));
	}	

	private int getRandom(int min, int max) {
		return min + (int) (Math.random() * (max - min + 1));
	}
	
	/**
	 * 检测两个位置是否相等
	 * @param loc1 位置1
	 * @param loc2 位置2
	 * @return true即为相等
	 */
	private boolean isLocationsEqual(Location loc1, Location loc2){
		return loc1.getBlockX() == loc2.getBlockX() &&
				loc1.getBlockY() == loc2.getBlockY() &&
				loc1.getBlockZ() == loc2.getBlockZ() &&
				loc1.getWorld() == loc2.getWorld();
	}
	
	/**
	 * 监测到玩家执行命令
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (label.equalsIgnoreCase("telet") || label.equalsIgnoreCase("teleporttitle")) {
			// 是劳资的命令
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("add")) {
					// 劳资要加title
					return runAdd(sender, args);
				} else if (args[0].equalsIgnoreCase("del")) {
					// 劳资要删title
					return runDel(sender, args);
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
				getLogger().info("只有玩家可以使用不带具体坐标的命令");
			}
			return true;
		case 5:
			// telet add <标题Json文本> [x y z]
			if (sender instanceof Player) {
				addTitle(args[1],new Location(((Player) sender).getWorld(),
						Double.parseDouble(args[2]),
						Double.parseDouble(args[3]),
						Double.parseDouble(args[4])),
						sender);
			} else {
				getLogger().info("只有玩家可以使用不带具体世界的命令");
			}
			return true;
		case 6:
			// telet add <标题Json文本> [x y z] [世界]
				addTitle(args[1], new Location(Bukkit.getWorld(args[5]),
						Double.parseDouble(args[2]),
						Double.parseDouble(args[3]),
						Double.parseDouble(args[4])),
						sender);
			return true;
		}
		
		return false;
	}
	
	/**
	 * 根据玩家添加标题
	 * @param json 标题信息
	 * @param p 玩家
	 */
	private void addTitle(String json, Player p) {
		Location loc = new Location(
				p.getWorld(), 
				p.getLocation().getBlockX(), 
				p.getLocation().getBlockY(),
				p.getLocation().getBlockZ());
		addTitle(json, loc, p);
	}
	
	/**
	 * 根据位置添加标题
	 * @param json 标题信息
	 * @param loc 位置
	 * @param sender 命令的执行者
	 */
	private void addTitle(String json, Location loc, CommandSender sender) {
		// 添加
		jsons.add(json);
		locations.add(loc);
		
		// 显示消息
		sender.sendMessage("§b已成功添加标题 §6" + json + "§b 到位置 §6" + locationToString(loc));
		if (sender instanceof Player) {
			showTitleOnScreen(((Player) sender), json);
			sender.sendMessage("§c如果您的屏幕上正确显示出了标题则表明无误！");
			sender.sendMessage("§c否则请您检查标题Json的正确性");
		}
	}

	/**
	 * 将位置转变为可读的字符串
	 * @param loc 指定位置
	 * @return 形如(x, y, z) in world的字符串
	 */
	private String locationToString(Location loc) {
		return "(" + Integer.toString(loc.getBlockX()) + ", " +
				Integer.toString(loc.getBlockY()) + ", " +
				Integer.toString(loc.getBlockZ()) + ") in " +
				loc.getWorld().getName();
	}
	
	/**
	 * 执行删Title的命令
	 * @param sender 命令执行者
	 * @param args 参数
	 * @return false将会显示usage
	 */
	private boolean runDel(CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			// telet del
			if (sender instanceof Player) {
				delTitle((Player) sender);
			} else {
				getLogger().info("只有玩家可以使用不带具体坐标的命令");
			}
			return true;
		case 4:
			// telet del [x y z]
			if (sender instanceof Player) {
				delTitle(new Location(((Player) sender).getWorld(),
						Double.parseDouble(args[1]),
						Double.parseDouble(args[2]),
						Double.parseDouble(args[3])),
						sender);
			} else {
				getLogger().info("只有玩家可以使用不带具体世界的命令");
			}
			return true;
		case 5:
			// telet del [x y z] [世界]
				delTitle(new Location(Bukkit.getWorld(args[4]),
						Double.parseDouble(args[1]),
						Double.parseDouble(args[2]),
						Double.parseDouble(args[3])),
						sender);
			return true;
		}
		
		return false;
	}
	
	/**
	 * 删除玩家所在位置的标题
	 * @param p 指定玩家
	 */
	private void delTitle(Player p) {
		delTitle(new Location(
				p.getWorld(), 
				p.getLocation().getBlockX(), 
				p.getLocation().getBlockY(), 
				p.getLocation().getBlockZ()), p);
	}
	
	/**
	 * 删除指定位置的标题
	 * @param loc 指定位置
	 * @param sender 命令执行者
	 */
	private void delTitle(Location loc, CommandSender sender) {
		int count = 0;
		for (int i = 0; i < locations.size(); i++) {
			Location item = locations.get(i);
			if (isLocationsEqual(item, loc)) {
				// 直接删除
				locations.remove(i);
				jsons.remove(i);
				count++;
			}
		}
		if (count > 0) {
			sender.sendMessage("§b已成功删除位于 §6" + locationToString(loc) +
					" §b的 §6" + Integer.toString(count) + " §b个传送标题");
		} else {
			sender.sendMessage("§c没有在 §6" + locationToString(loc) +
					" §c找到任何传送标题");
		}
	}
	
	/**
	 * 在玩家屏幕上显示Title
	 * @param p 玩家
	 * @param json 消息Json
	 */
	private void showTitleOnScreen(Player p, String json) {
		if (p.hasPermission("teleporttitle.showtitle")) {
			Bukkit.getServer().dispatchCommand(
					Bukkit.getConsoleSender(), 
					"title " + p.getName() + " title " + json);
		}
	}
	
	/**
	 * 插件卸载时
	 */
	@Override
	public void onDisable() {
		unregisterListenrs();
		writeConfig();
		saveConfig();
	}

	/**
	 * 注销Listeners
	 */
	private void unregisterListenrs() {
		HandlerList.unregisterAll();
	}

	/**
	 * 写入配置文件
	 */
	private void writeConfig() {
		List<String> messages = new ArrayList<>();
		for (int i = 0; i < locations.size(); i++) {
			// 把所有坐标和Json再次连起来并写入配置文件
			Location loc = locations.get(i);
			String item = loc.getWorld().getName() + "," +
					Integer.toString(loc.getBlockX()) + "," +
					Integer.toString(loc.getBlockY()) + "," +
					Integer.toString(loc.getBlockZ()) + "|" +
					jsons.get(i);
			
			messages.add(item);
		}
		getConfig().set("messages", messages);
	}
}
