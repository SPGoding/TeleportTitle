package com.github.spgoding.teleporttitle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportTitleCommandExecutor implements CommandExecutor {
	private final TeleportTitle plugin;
	
	public TeleportTitleCommandExecutor(TeleportTitle plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * ��⵽���ִ������
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("telet") || label.equalsIgnoreCase("teleporttitle")) {
			// �����ʵ�����
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("add")) {
					// ����Ҫ��title
					return runAdd(sender, args);
				} else if (args[0].equalsIgnoreCase("del")) {
					// ɾtitle
					return runDel(sender, args);
				} else if (args[0].equalsIgnoreCase("list")) {
					// ��title
					return runList(sender, args);
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
				plugin.getLogger().info("��cֻ����ҿ���ʹ�ò����������������");
			}
			
			return true;
		case 5:
			// telet add <����Json�ı�> [x y z]
			if (sender instanceof Player) {
				addTitle(args[1], 
						Util.getLocation(
								((Player) sender).getWorld(), 
								args[2], args[3], args[4], sender
								),
						sender);
			} else {
				plugin.getLogger().info("��cֻ����ҿ���ʹ�ò����������������");
			}
			
			return true;
		case 6:
			// telet add <����Json�ı�> [x y z] [����]
			World w = Bukkit.getWorld(args[5]);
			if (w != null) {
				addTitle(args[1], 
						Util.getLocation(
								Util.getWorld(args[5], sender), 
								args[2], args[3], args[4], sender
								),
						sender);
			} else {
				plugin.getLogger().info("��cδ�ҵ���Ϊ��6" + args[5] + "��c������" );
			}
				
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
		Location loc = p.getLocation().getBlock().getLocation();
		addTitle(json, loc, p);
	}
	
	/**
	 * ����λ����ӱ���
	 * @param json ������Ϣ
	 * @param loc λ��
	 * @param sender �����ִ����
	 */
	private void addTitle(String json, Location loc, CommandSender sender) {
		if (loc != null) {			
			// ���
			plugin.getJsons().add(json);
			plugin.getLocations().add(loc);
			
			// ��ʾ��Ϣ
			sender.sendMessage("��b�ѳɹ���ӱ��� ��6" + json + "��b ��λ�� ��6" + 
			Util.locationToString(loc));
			if (sender instanceof Player) {
				Util.showTitleOnScreen(((Player) sender), json);
				sender.sendMessage("��c���������Ļ����ȷ��ʾ���˱������������");
				sender.sendMessage("��c��������������Json����ȷ��");
			}
		}
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
				delTitles((Player) sender);
			} else {
				plugin.getLogger().info("ֻ����ҿ���ʹ�ò����������������");
			}
			return true;
		case 4:
			// telet del [x y z]
			if (sender instanceof Player) {
				delTitles(Util.getLocation(((Player) sender).getWorld(),
						args[1], args[2], args[3], sender),
						sender);
			} else {
				plugin.getLogger().info("ֻ����ҿ���ʹ�ò����������������");
			}
			return true;
		case 5:
			// telet del [x y z] [����]
				delTitles(
						Util.getLocation(
						Util.getWorld(args[4], sender),
						args[1], args[2], args[3], sender
						),
						sender
						);
			return true;
		}
		
		return false;
	}
	
	/**
	 * ɾ���������λ�õı���
	 * @param p ָ�����
	 */
	private void delTitles(Player p) {
		delTitles(p.getLocation().getBlock().getLocation(), p);
	}
	
	/**
	 * ɾ��ָ��λ�õı���
	 * @param loc ָ��λ��
	 * @param sender ����ִ����
	 */
	private void delTitles (Location loc, CommandSender sender) {
		if (loc != null) {
			int count = 0;
			for (int i = 0; i < plugin.getLocations().size(); i++) {
				Location item = plugin.getLocations().get(i);
				if (item.equals(loc)) {
					// ֱ��ɾ��
					plugin.getLocations().remove(i);
					plugin.getJsons().remove(i);
					i -= 1;
					count++;
				}
			}
			if (count > 0) {
				sender.sendMessage("��b�ѳɹ�ɾ��λ�� ��6" + Util.locationToString(loc) +
						" ��b�� ��6" + Integer.toString(count) + " ��b�����ͱ���");
			} else {
				sender.sendMessage("��cû���� ��6" + Util.locationToString(loc) +
						" ��c�ҵ��κδ��ͱ���");
			}
		}
	}
	
	/**
	 * ִ���г�����title������
	 * @param sender ����ִ����
	 * @param args ����
	 * @return false������ʾusage
	 */
	private boolean runList(CommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			sender.sendMessage("��7==============================");
			// telet list
			listTitles(sender);
			sender.sendMessage("��7==============================");
			return true;
		case 2:
			sender.sendMessage("��7==============================");
			// telet list [����]
			listTitles(sender, Util.getWorld(args[1], sender));
			sender.sendMessage("��7==============================");
			return true;
		case 5:
			sender.sendMessage("��7==============================");
			// telet list [����] [x y z]
			listTitles(sender, 
					Util.getLocation(
							Util.getWorld(args[1], sender),
							args[2], args[3], args[4], sender)
					);
			sender.sendMessage("��7==============================");
			return true;
		}		
		return false;
	}
	
	/**
	 * ��ָ��sender�г����д��ͱ���
	 * @param sender ָ��sender
	 */
	private void listTitles(CommandSender sender) {
		int cnt = 0;
		for (int i = 0; i < plugin.getLocations().size(); i++) {
			sender.sendMessage("��6" +
					Util.locationToString(plugin.getLocations().get(i)) +
					"��7:��b " + plugin.getJsons().get(i));
			cnt++;
		}
		sender.sendMessage("��9�������й� ��6" + Integer.toString(cnt) + "�� ��9���ͱ���");
	}
	
	/**
	 * ��ָ��sender�г�ָ������Ĵ��ͱ���
	 * @param sender ָ��sender
	 */
	private void listTitles(CommandSender sender, World world) {
		if (world != null){
			int cnt = 0;
			for (int i = 0; i < plugin.getLocations().size(); i++) {
				Location iLoc = plugin.getLocations().get(i);
				if (iLoc.getWorld() == world) {
					sender.sendMessage(
							"��6" + Util.locationToString(plugin.getLocations().get(i)) +
							"��7:��b " + plugin.getJsons().get(i));
					cnt++;
				}
			}
			sender.sendMessage("��9���� ��6" + world.getName() + " ��9�й� ��6" +
			Integer.toString(cnt) + "�� ��9���ͱ���");
		}
	}
	
	/**
	 * ��ָ��sender�г�ָ��λ�õĴ��ͱ���
	 * @param sender ָ��sender
	 */
	private void listTitles(CommandSender sender, Location loc) {
		if (loc != null) {
			int cnt = 0;
			for (int i = 0; i < plugin.getLocations().size(); i++) {
				Location iLoc = plugin.getLocations().get(i);
				if (loc.equals(iLoc)) {
					sender.sendMessage("��6" + Util.locationToString(plugin.getLocations().get(i)) +
							"��7:��b " + plugin.getJsons().get(i));
					cnt++;
				}
			}
			sender.sendMessage("��9λ�� ��6" + Util.locationToString(loc) + " ��9�й� ��6" +
			Integer.toString(cnt) + "�� ��9���ͱ���");
		}
	}

}
