package com.github.spgoding.teleporttitle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 常用工具
 * @author SPGoding
 *
 */
public class Util {
	
	/**
	 * 获取随机整数
	 * @param min 最小值(包含)
	 * @param max 最大值(包含)
	 * @return 随机整数
	 */
	public static int getRandom(int min, int max) {
		return min + (int) (Math.random() * (max - min + 1));
	}
	
	public static World getWorld(String name, CommandSender sender) {
		World world = Bukkit.getWorld(name);
		if (world != null) {
			return world;
		} else {
			sender.sendMessage("§c未找到名为 §6" + name + " §c的世界");
		}
		
		return null;
	}

	/**
	 * 根据各字符串获得位置，位置非法则向执行者警告
	 * @param world 世界名
	 * @param x x坐标
	 * @param y y坐标
	 * @param z z坐标
	 * @param sender 命令执行者
	 * @return 位置，或null
	 */
	public static Location getLocation(World world, String x, String y, String z, CommandSender sender) {
		if (world != null) {
			if (isNumeric(x) && isNumeric(y) && isNumeric(z)) {
				return new Location(world, Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
			} else {
				sender.sendMessage("§6(" + x + ", " + y + ", " + z + ")" + "§c不是一个合法的坐标");
			}
		}
		
		return null;
	}
	
	/**
	 * 判断字符串是否为数字
	 * @param str 字符串
	 * @return 是true，否false
	 */
	public static boolean isNumeric(String str) {  
	   for(int i = str.length(); i >= 0; i--){  
	    	 int chr = str.charAt(i);  
	    	 if (chr < 48 || chr > 57) {	    		 
	    		 return false;  
	    	 }
	   }
	   
	   return true;  
	}   
	
	/**
	 * 将位置转变为可读的字符串
	 * @param loc 指定位置
	 * @return 形如(x, y, z) in world的字符串
	 */
	public static String locationToString(Location loc) {
		return "§7世界 §6" + loc.getWorld().getName() + " §7中的 (§6" + Integer.toString(loc.getBlockX()) + "§7, §6" +
				Integer.toString(loc.getBlockY()) + "§7, §6" +
				Integer.toString(loc.getBlockZ()) + "§7)";
	}
	
	/**
	 * 在玩家屏幕上显示Title
	 * @param p 玩家
	 * @param json 消息Json
	 */
	public static void showTitleOnScreen(Player p, String json) {
		if (p.hasPermission("teleporttitle.showtitle")) {
			Bukkit.getServer().dispatchCommand(
					Bukkit.getConsoleSender(), 
					"title " + p.getName() + " title " + json);
		}
	}

}
