/* 列限制100
 * Google规范？管它的
 */
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

/**
 * TeleportTitle主类
 * @author SPGoding
 *
 */
public class TeleportTitle extends JavaPlugin implements Listener {
	private List<Location> locations = new ArrayList<>();
	private List<String> jsons = new ArrayList<>();

	public List<Location> getLocations() {
		return locations;
	}
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}	
	public List<String> getJsons() {
		return jsons;
	}
	public void setJsons(List<String> jsons) {
		this.jsons = jsons;
	}

	/**
	 * 插件加载时
	 */
	@Override
	public void onEnable() {
		registerListenrs();
		registerCommandExecutors();
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

	private void registerCommandExecutors() {
		this.getCommand("telet").setExecutor(new TeleportTitleCommandExecutor(this));
	}
	
	/**
	 * 补全目录
	 */
	void completeFiles() {
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
	void readConfig() {
		getJsons().clear();
		getLocations().clear();
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
						getLocations().add(loc);
						// 删掉除了第一个以外的，这样原 Json 里可以有|符号
						getJsons().add(str.replaceFirst(strs[0] + "\\|", ""));
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
	public void sendTitle(PlayerTeleportEvent e) {
		Player p = e.getPlayer();
		Location toLoc = e.getTo();
		List<String> msgs = new ArrayList<>();
		for (int i = 0; i < getLocations().size(); i++) {
			Location loc = getLocations().get(i);
			if (Util.locationToString(loc).equals(Util.locationToString(toLoc))) {
				// 传送后坐标符合该条配置
				
				// 将可发送的msg添加至msg
				msgs.add(getJsons().get(i));

			}
		}
		
		// 从msgs里随机抽取标题并发送
		if (msgs.size() > 0) {
			int index = Util.getRandom(0, msgs.size() - 1);
			Util.showTitleOnScreen(p, msgs.get(index));			
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
	void writeConfig() {
		List<String> messages = new ArrayList<>();
		for (int i = 0; i < getLocations().size(); i++) {
			// 把所有坐标和Json再次连起来并写入配置文件
			Location loc = getLocations().get(i);
			String item = loc.getWorld().getName() + "," +
					Integer.toString(loc.getBlockX()) + "," +
					Integer.toString(loc.getBlockY()) + "," +
					Integer.toString(loc.getBlockZ()) + "|" +
					getJsons().get(i);
			
			messages.add(item);
		}
		getConfig().set("messages", messages);
	}
}
