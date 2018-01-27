package com.github.spgoding.teleporttitle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TeleportTitle extends JavaPlugin implements Listener {
	private List<Location> locations = new ArrayList<>();
	private List<String> messages = new ArrayList<>();
	
	@Override
	public void onEnable() {
		registerListenrs();
		completeFiles();
		reloadConfig();
		loadConfig();
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
	 * 加载配置文件
	 */
	private void loadConfig() {
		messages.clear();
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
						messages.add(str.replaceFirst(strs[0] + "\\|", ""));
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
		for (int i = 0; i < locations.size(); i++) {
			Location loc = locations.get(i);
			if (loc.getBlockX() == toLoc.getBlockX() &&
					loc.getBlockY() == toLoc.getBlockY() &&
					loc.getBlockZ() == toLoc.getBlockZ() &&
					loc.getWorld() == toLoc.getWorld()){
				// 符合配置文件
				
				// 替换变量
				String msg = messages.get(i)
						.replaceAll("%PLAYER%", p.getName())
						.replaceAll("%WORLD%", loc.getWorld().getName());
				
				// 发送title
				Bukkit.getServer().dispatchCommand(
						Bukkit.getConsoleSender(), 
						"title " + p.getName() + " title " + msg);
				
				break;
			}
		}
	}
	
	@Override
	public void onDisable() {
		unregisterEvents();
	}

	/**
	 * 注销Listeners
	 */
	private void unregisterEvents() {
		HandlerList.unregisterAll();
	}
}
