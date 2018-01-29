/* ������100
 * Google�淶��������
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
 * Teleport Title������
 */
public class TeleportTitle extends JavaPlugin implements Listener {
	private List<Location> locations = new ArrayList<>();
	private List<String> jsons = new ArrayList<>();
	
	/**
	 * �������ʱ
	 */
	@Override
	public void onEnable() {
		registerListenrs();
		completeFiles();
		reloadConfig();
		readConfig();
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
	 * ��ȡ�����ļ�
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
						// ����Location��Message
						Location loc = new Location(
								Bukkit.getWorld(locs[0]), 
								Double.parseDouble(locs[1]), 
								Double.parseDouble(locs[2]), 
								Double.parseDouble(locs[3]));
						locations.add(loc);
						// ɾ�����˵�һ������ģ�����ԭ Json �������|����
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
	 * �����������¼�����ʾtitle
	 * @param e �¼�
	 */
	@EventHandler
	public void sendTitle(PlayerTeleportEvent e){
		Player p = e.getPlayer();
		Location toLoc = e.getTo();
		List<String> msgs = new ArrayList<>();
		for (int i = 0; i < locations.size(); i++) {
			Location loc = locations.get(i);
			if (isLocationsEqual(loc, toLoc)){
				// ���ͺ�������ϸ�������
				
				// �滻����
				String msg = jsons.get(i)
						.replaceAll("%PLAYER%", p.getName())
						.replaceAll("%WORLD%", loc.getWorld().getName());
				
				// ���ɷ��͵�msg�����msg
				msgs.add(msg);
			}
		}
		
		// ��msgs�������ȡ���Ⲣ����
		int index = getRandom(0, msgs.size() - 1);
		showTitleOnScreen(p, msgs.get(index));
	}	

	private int getRandom(int min, int max) {
		return min + (int) (Math.random() * (max - min + 1));
	}
	
	/**
	 * �������λ���Ƿ����
	 * @param loc1 λ��1
	 * @param loc2 λ��2
	 * @return true��Ϊ���
	 */
	private boolean isLocationsEqual(Location loc1, Location loc2){
		return loc1.getBlockX() == loc2.getBlockX() &&
				loc1.getBlockY() == loc2.getBlockY() &&
				loc1.getBlockZ() == loc2.getBlockZ() &&
				loc1.getWorld() == loc2.getWorld();
	}
	
	/**
	 * ��⵽���ִ������
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (label.equalsIgnoreCase("telet") || label.equalsIgnoreCase("teleporttitle")) {
			// �����ʵ�����
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("add")) {
					// ����Ҫ��title
					return runAdd(sender, args);
				} else if (args[0].equalsIgnoreCase("del")) {
					// ����Ҫɾtitle
					return runDel(sender, args);
				}
			}
		}
		return false;	
	}
	
	/**
	 * ִ�м�Title������
	 * @param sender ����ִ����
	 * @param args ����
	 * @return false������ʾusage
	 */
	private boolean runAdd(CommandSender sender, String[] args) {
		switch (args.length) {
		case 2:
			// telet add <����Json�ı�>
			if (sender instanceof Player) {
				addTitle(args[1], (Player) sender);
			} else {
				getLogger().info("ֻ����ҿ���ʹ�ò����������������");
			}
			return true;
		case 5:
			// telet add <����Json�ı�> [x y z]
			if (sender instanceof Player) {
				addTitle(args[1],new Location(((Player) sender).getWorld(),
						Double.parseDouble(args[2]),
						Double.parseDouble(args[3]),
						Double.parseDouble(args[4])),
						sender);
			} else {
				getLogger().info("ֻ����ҿ���ʹ�ò����������������");
			}
			return true;
		case 6:
			// telet add <����Json�ı�> [x y z] [����]
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
	 * ���������ӱ���
	 * @param json ������Ϣ
	 * @param p ���
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
	 * ����λ����ӱ���
	 * @param json ������Ϣ
	 * @param loc λ��
	 * @param sender �����ִ����
	 */
	private void addTitle(String json, Location loc, CommandSender sender) {
		// ���
		jsons.add(json);
		locations.add(loc);
		
		// ��ʾ��Ϣ
		sender.sendMessage("��b�ѳɹ���ӱ��� ��6" + json + "��b ��λ�� ��6" + locationToString(loc));
		if (sender instanceof Player) {
			showTitleOnScreen(((Player) sender), json);
			sender.sendMessage("��c���������Ļ����ȷ��ʾ���˱������������");
			sender.sendMessage("��c��������������Json����ȷ��");
		}
	}

	/**
	 * ��λ��ת��Ϊ�ɶ����ַ���
	 * @param loc ָ��λ��
	 * @return ����(x, y, z) in world���ַ���
	 */
	private String locationToString(Location loc) {
		return "(" + Integer.toString(loc.getBlockX()) + ", " +
				Integer.toString(loc.getBlockY()) + ", " +
				Integer.toString(loc.getBlockZ()) + ") in " +
				loc.getWorld().getName();
	}
	
	/**
	 * ִ��ɾTitle������
	 * @param sender ����ִ����
	 * @param args ����
	 * @return false������ʾusage
	 */
	private boolean runDel(CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			// telet del
			if (sender instanceof Player) {
				delTitle((Player) sender);
			} else {
				getLogger().info("ֻ����ҿ���ʹ�ò����������������");
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
				getLogger().info("ֻ����ҿ���ʹ�ò����������������");
			}
			return true;
		case 5:
			// telet del [x y z] [����]
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
	 * ɾ���������λ�õı���
	 * @param p ָ�����
	 */
	private void delTitle(Player p) {
		delTitle(new Location(
				p.getWorld(), 
				p.getLocation().getBlockX(), 
				p.getLocation().getBlockY(), 
				p.getLocation().getBlockZ()), p);
	}
	
	/**
	 * ɾ��ָ��λ�õı���
	 * @param loc ָ��λ��
	 * @param sender ����ִ����
	 */
	private void delTitle(Location loc, CommandSender sender) {
		int count = 0;
		for (int i = 0; i < locations.size(); i++) {
			Location item = locations.get(i);
			if (isLocationsEqual(item, loc)) {
				// ֱ��ɾ��
				locations.remove(i);
				jsons.remove(i);
				count++;
			}
		}
		if (count > 0) {
			sender.sendMessage("��b�ѳɹ�ɾ��λ�� ��6" + locationToString(loc) +
					" ��b�� ��6" + Integer.toString(count) + " ��b�����ͱ���");
		} else {
			sender.sendMessage("��cû���� ��6" + locationToString(loc) +
					" ��c�ҵ��κδ��ͱ���");
		}
	}
	
	/**
	 * �������Ļ����ʾTitle
	 * @param p ���
	 * @param json ��ϢJson
	 */
	private void showTitleOnScreen(Player p, String json) {
		if (p.hasPermission("teleporttitle.showtitle")) {
			Bukkit.getServer().dispatchCommand(
					Bukkit.getConsoleSender(), 
					"title " + p.getName() + " title " + json);
		}
	}
	
	/**
	 * ���ж��ʱ
	 */
	@Override
	public void onDisable() {
		unregisterListenrs();
		writeConfig();
		saveConfig();
	}

	/**
	 * ע��Listeners
	 */
	private void unregisterListenrs() {
		HandlerList.unregisterAll();
	}

	/**
	 * д�������ļ�
	 */
	private void writeConfig() {
		List<String> messages = new ArrayList<>();
		for (int i = 0; i < locations.size(); i++) {
			// �����������Json�ٴ���������д�������ļ�
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
