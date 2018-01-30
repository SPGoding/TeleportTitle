package com.github.spgoding.teleporttitle;

import org.bukkit.Location;

/**
 * 用于储存即将删除的位置信息
 * @author SPGoding
 *
 */
public class GoingToDel {
	private Location location;
	private long time;
	
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	
}