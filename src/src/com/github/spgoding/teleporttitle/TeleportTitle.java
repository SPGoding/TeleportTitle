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
	 * ע��Listeners
	 */
	private void registerListenrs() {
		getServer().getPluginManager().registerEvents(this, this);
	}

	/**
	 * ��ȫĿ¼
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
	 * ���������ļ�
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
						// ����Location��Message
						Location loc = new Location(
								Bukkit.getWorld(locs[0]), 
								Double.parseDouble(locs[1]), 
								Double.parseDouble(locs[2]), 
								Double.parseDouble(locs[3]));
						locations.add(loc);
						// ɾ�����˵�һ������ģ�����ԭ Json �������|����
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
	 * �����������¼�����ʾtitle
	 * @param e �¼�
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
				// ���������ļ�
				
				// �滻����
				String msg = messages.get(i)
						.replaceAll("%PLAYER%", p.getName())
						.replaceAll("%WORLD%", loc.getWorld().getName());
				
				// ����title
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
	 * ע��Listeners
	 */
	private void unregisterEvents() {
		HandlerList.unregisterAll();
	}
}
