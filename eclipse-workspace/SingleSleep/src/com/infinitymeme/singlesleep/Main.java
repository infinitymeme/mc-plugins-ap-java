package com.infinitymeme.singlesleep;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.Listener;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {

	protected static final int TICKS_BETWEEN = 1;
	protected static final int FF_SPEED = 20;
	
	private int sleeping = 0;
	private int thread_cancel = 0;


	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		sleeping = 0;
		thread_cancel = 0;
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public void sleeptick(World w) {
		new BukkitRunnable() {
	        
            @Override
            public void run() {
//            	Bukkit.broadcastMessage("tick");
            	if (thread_cancel > 0) {
//            		Bukkit.broadcastMessage("canceled");
        			thread_cancel--;
        		} else {
	        		if (sleeping > 0) {
	            		w.setTime(w.getTime()+(FF_SPEED*TICKS_BETWEEN));
	            		sleeptick(w);
	            	}
        		}
            }
                
            
        }.runTaskLater(this, TICKS_BETWEEN);
		
	}
	
	@EventHandler
	public void onEnterBed(PlayerBedEnterEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (e.getPlayer().isSleeping()) {
					if (e.getPlayer().getWorld().hasStorm()) e.getPlayer().getWorld().setStorm(false);
					long time = e.getPlayer().getWorld().getTime();
					if ((time >= 12541)&&(time <= 23458)) {
						Bukkit.broadcastMessage(ChatColor.YELLOW + e.getPlayer().getDisplayName() + ChatColor.YELLOW + " is sleeping");
						if (sleeping==0) {
							sleeping++;
							sleeptick(e.getPlayer().getWorld());
						} else {
							sleeping++;
							
							
						}
					}
				}
			}
		}.runTaskLater(this, 1);
	}
	
	@EventHandler
	public void onLeaveBed(PlayerBedLeaveEvent e) {
		if (sleeping > 0) {
			sleeping--;
//			Bukkit.broadcastMessage("nevermind (remaining "+Integer.toString(sleeping)+")");
			if (sleeping == 0) {
//				Bukkit.broadcastMessage("total nevermind");
				long time = e.getPlayer().getWorld().getTime();
				if (!((time >= 12541)&&(time <= 23458))) {
					Bukkit.broadcastMessage(ChatColor.YELLOW + "Rise and shine!");
					thread_cancel++;
				}
			}
		}
	}

	
	
	
}
