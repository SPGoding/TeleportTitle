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
	
	public TeleportTitle getPlugin() {
		return plugin;
	}

	public TeleportTitleCommandExecutor(TeleportTitle plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * ��⵽���ִ������
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Util.sendSepratedLine(sender);
		if (label.equalsIgnoreCase("telet") || label.equalsIgnoreCase("teleporttitle")) {
			// �����ʵ�����
			if (args.length > 0) {
				switch (args[0].toLowerCase()) {
				case "add":
					// ��title
					return runAdd(sender, args);
				case "confirm":
					// ȷ��ɾ��
					return runConfirm(sender);
				case "del":
					// ɾtitle
					return runDel(sender, args);
				case "list":
					// ��title
					return runList(sender, args);
				case "reload":
					// ���������ļ�
					return runReload(sender);
				case "save":
					// ���������ļ�
					return runSave(sender);
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
				sender.sendMessage("��cֻ����ҿ���ʹ�ò����������������");
			}
			
			Util.sendSepratedLine(sender);			
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
				sender.sendMessage("��cֻ����ҿ���ʹ�ò����������������");
			}
			
			Util.sendSepratedLine(sender);			
			return true;
		case 6:
			// telet add <����Json�ı�> [x y z] [����]
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
			// �� & ������ʽ����
			json = json.replaceAll("&(?=0|1|2|3|4|5|6|7|8|9|a|b|c|d|e|f|l|m|n|o|k|r)", "\u00A7");
			// ���
			getPlugin().getJsons().add(json);
			getPlugin().getLocations().add(loc);
			
			// ��ʾ��Ϣ
			sender.sendMessage("��b�ѳɹ���ӱ��� ��r" + json + "��b ��λ�� ��6" + 
			Util.locationToString(loc));
			if (sender instanceof Player) {
				Util.showTitleOnScreen(((Player) sender), json);
				sender.sendMessage("��b���������Ļ����ȷ��ʾ���˱������������");
				sender.sendMessage("��b��������������Json����ȷ��");
			}
		}
	}

	/**
	 * ִ��ɾ��title������
	 * @param sender ����ִ����
	 * @return true
	 */
	private boolean runConfirm(CommandSender sender) {
		// ȷ��ɾ��
		Enumeration<CommandSender> e = goingToDels.keys();
	    while(e.hasMoreElements()) {
	    	if (e.nextElement() == sender) {
	    		// ����Ǹ�senderҪɾ��
				if (System.currentTimeMillis() <= goingToDels.get(sender).getTime()) {
					// ʱ�仹Okay������ɾ
					delTitles(goingToDels.get(sender).getLocation(), sender);
				} else {
					sender.sendMessage("��c�Ѿ�����ȷ��ʱ��");
				}
				goingToDels.remove(sender);
				Util.sendSepratedLine(sender);
				return true;
	    	}
	    }
		sender.sendMessage("��c��δʹ�ù� ��6/telet del ��c����");
		Util.sendSepratedLine(sender);
		return true;
	}
	
	/**
	 * ִ��ɾTitle������
	 * @param sender ����ִ����
	 * @param args ����
	 * @return false������ʾusage
	 */
	private boolean runDel(CommandSender sender, String[] args) {
		GoingToDel gtd = new GoingToDel();
		
		switch (args.length) {
		case 1:
			// telet del
			if (sender instanceof Player) {
				gtd.setLocation(((Player) sender).getLocation().getBlock().getLocation());
			} else {
				sender.sendMessage("��cֻ����ҿ���ʹ�ò����������������");
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
				sender.sendMessage("��cֻ����ҿ���ʹ�ò����������������");
				Util.sendSepratedLine(sender);
				return true;
			}
			
			break;			
		case 5:
			// telet del [x y z] [����]
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
		// ѭ���������Sender֮ǰ��һ��Ҫȷ�ϵ�ɾ��û�йܣ����Ǹ�ɾ��ɾ��
		Enumeration<CommandSender> e = goingToDels.keys();
	    while(e.hasMoreElements()) {
	    	if (e.nextElement() == sender) goingToDels.remove(sender);
	    }
		goingToDels.put(sender, gtd);
		sender.sendMessage("��c���� ��630���� ��cִ������ ��6/telet confirm ��cȷ��ɾ��λ��" +
				Util.locationToString(gtd.getLocation()) + " ��c�ġ�l���С�c���ͱ���");
		Util.sendSepratedLine(sender);
		return true;
	}
	
	/**
	 * ɾ��ָ��λ�õı���
	 * @param loc ָ��λ��
	 * @param sender ����ִ����
	 */
	private void delTitles (Location loc, CommandSender sender) {
		if (loc != null) {
			int count = 0;
			for (int i = 0; i < getPlugin().getLocations().size(); i++) {
				Location item = getPlugin().getLocations().get(i);
				if (Util.locationToString(loc).equals(Util.locationToString(item))) {
					// ֱ��ɾ��
					getPlugin().getLocations().remove(i);
					getPlugin().getJsons().remove(i);
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
			// telet list
			listTitles(sender);
			
			Util.sendSepratedLine(sender);			
			return true;
		case 2:
			// telet list [����]
			listTitles(sender, Util.getWorld(args[1], sender));
			
			Util.sendSepratedLine(sender);			
			return true;
		case 5:
			// telet list [����] [x y z]
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
	 * ��ָ��sender�г����д��ͱ���
	 * @param sender ָ��sender
	 */
	private void listTitles(CommandSender sender) {
		int cnt = 0;
		for (int i = 0; i < getPlugin().getLocations().size(); i++) {
			sender.sendMessage("��6" +
					Util.locationToString(getPlugin().getLocations().get(i)) +
					"��7:��r " + getPlugin().getJsons().get(i));
			cnt++;
		}
		sender.sendMessage("��b�������й� ��6" + Integer.toString(cnt) + "�� ��b���ͱ���");
	}
	
	/**
	 * ��ָ��sender�г�ָ������Ĵ��ͱ���
	 * @param sender ָ��sender
	 */
	private void listTitles(CommandSender sender, World world) {
		if (world != null){
			int cnt = 0;
			for (int i = 0; i < getPlugin().getLocations().size(); i++) {
				Location iLoc = getPlugin().getLocations().get(i);
				if (iLoc.getWorld() == world) {
					sender.sendMessage(
							"��6" + Util.locationToString(getPlugin().getLocations().get(i)) +
							"��7: ��r" + getPlugin().getJsons().get(i));
					cnt++;
				}
			}
			sender.sendMessage("��b���� ��6" + world.getName() + " ��b�й� ��6" +
			Integer.toString(cnt) + "�� ��b���ͱ���");
		}
	}
	
	/**
	 * ��ָ��sender�г�ָ��λ�õĴ��ͱ���
	 * @param sender ָ��sender
	 */
	private void listTitles(CommandSender sender, Location loc) {
		if (loc != null) {
			int cnt = 0;
			for (int i = 0; i < getPlugin().getLocations().size(); i++) {
				Location iLoc = getPlugin().getLocations().get(i);
				if (Util.locationToString(loc).equals(Util.locationToString(iLoc))) {
					sender.sendMessage("��6" + Util.locationToString(getPlugin().getLocations().get(i)) +
							"��7: ��r" + getPlugin().getJsons().get(i));
					cnt++;
				}
			}
			sender.sendMessage("��bλ�� ��6" + Util.locationToString(loc) + " ��b�й� ��6" +
			Integer.toString(cnt) + "�� ��b���ͱ���");
		}
	}

	/**
	 * ִ�����������ļ�������
	 * @param sender ����ִ����
	 * @return true
	 */
	private boolean runReload(CommandSender sender) {
		getPlugin().completeFiles();
		getPlugin().reloadConfig();
		getPlugin().readConfig();
		sender.sendMessage("��b�����¶�ȡ�����ļ�");
		Util.sendSepratedLine(sender);
		return true;
	}
	
	/**
	 * ִ�б��������ļ�������
	 * @param sender ����ִ����
	 * @return true
	 */
	private boolean runSave(CommandSender sender) {
		getPlugin().writeConfig();
		getPlugin().saveConfig();
		sender.sendMessage("��b�ѱ��������ļ�");
		Util.sendSepratedLine(sender);
		return true;
	}
}
