package com.github.spgoding.teleporttitle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * ���ù���
 * @author SPGoding
 *
 */
public class Util {
	
	/**
	 * ���ͷָ���
	 * @param sender ����ִ����
	 */
	public static void sendSepratedLine(CommandSender sender) {
		sender.sendMessage("��7==============================");
	}
	
	/**
	 * ��ȡ�������
	 * @param min ��Сֵ(����)
	 * @param max ���ֵ(����)
	 * @return �������
	 */
	public static int getRandom(int min, int max) {
		return min + (int) (Math.random() * (max - min + 1));
	}
	
	public static World getWorld(String name, CommandSender sender) {
		World world = Bukkit.getWorld(name);
		if (world != null) {
			return world;
		} else {
			sender.sendMessage("��cδ�ҵ���Ϊ ��6" + name + " ��c������");
		}
		
		return null;
	}

	/**
	 * ���ݸ��ַ������λ�ã�λ�÷Ƿ�����ִ���߾���
	 * @param world ������
	 * @param x x����
	 * @param y y����
	 * @param z z����
	 * @param sender ����ִ����
	 * @return λ�ã���null
	 */
	public static Location getLocation(World world, String x, String y, String z, CommandSender sender) {
		if (world != null) {
			if (isNumeric(x) && isNumeric(y) && isNumeric(z)) {
				return new Location(world, Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
			} else {
				sender.sendMessage("��7(��6" + x + "��7, ��6" + y + "��7, ��6" + z + "��7) ��c����һ���Ϸ�������");
			}
		}
		
		return null;
	}
	
	/**
	 * �ж��ַ����Ƿ�Ϊ����
	 * @param str �ַ���
	 * @return ��true����false
	 */
	public static boolean isNumeric(String str) {  
	   for(int i = 0; i < str.length(); i++){  
	    	 int chr = str.charAt(i);  
	    	 if (chr < 48 || chr > 57) {	    		 
	    		 return false;  
	    	 }
	   }
	   
	   return true;  
	}   
	
	/**
	 * ��λ��ת��Ϊ�ɶ����ַ���
	 * @param loc ָ��λ��
	 * @return ����(x, y, z) in world���ַ���
	 */
	public static String locationToString(Location loc) {
		if (loc != null) {
			return "��7(����: ��6" + loc.getWorld().getName() + "��7, x: ��6" + Integer.toString(loc.getBlockX()) + "��7, y: ��6" +
				Integer.toString(loc.getBlockY()) + "��7, z: ��6" +
				Integer.toString(loc.getBlockZ()) + "��7)";
		} else {
			return "��cû�����λ��";
		}
	}
	
	/**
	 * �������Ļ����ʾTitle
	 * @param p ���
	 * @param json ��ϢJson
	 */
	public static void showTitleOnScreen(Player p, String json) {
		if (p.hasPermission("teleporttitle.showtitle")) {
			Bukkit.getServer().dispatchCommand(
					Bukkit.getConsoleSender(), 
					"title " + p.getName() + " title " + 
					json.replaceAll("%player%", p.getName()));
		}
	}

}
