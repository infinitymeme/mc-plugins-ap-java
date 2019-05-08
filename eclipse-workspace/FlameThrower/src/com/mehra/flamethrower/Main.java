package com.mehra.flamethrower;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;


public class Main extends JavaPlugin implements Listener{
		
	private LinkedList<Player> firecooldown;
	private LinkedList<SmallFireball> fireballs = new LinkedList <SmallFireball>();
	
	private ItemStack flamethrower;// for making recipe
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		loadFlameThrower();
		firecooldown= new LinkedList<Player>();
	}
	
	@Override
	public void onDisable() {
		
	}

	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.getHand().equals(EquipmentSlot.HAND)) {//checks if the event is being triggered from an interaction from the main hand
			String action = e.getAction().name();
			if ((action.equals("RIGHT_CLICK_AIR"))||(action.equals("RIGHT_CLICK_BLOCK"))) {
				Player p = e.getPlayer();
				ItemStack hand = p.getInventory().getItemInMainHand();// checks if right item is in hand
				if ((hand != null)&&(hand.equals(flamethrower))) {
					e.setCancelled(true);// overrides previous action done with item/ no longer places torch
					if ((!firecooldown.contains(p))) {// keeps player from spamming with flamethrower
						firecooldown.add(p);//adds them to firecooldown list
						
						timedBlasts(p,20);
					}
				}
				
			}
		}
	}
		
	
	private void timedBlasts(Player p, int ticks) {
			if (ticks > 0) {
				SmallFireball a = (SmallFireball) p.getWorld().spawnEntity(p.getEyeLocation().add(p.getLocation().getDirection()), EntityType.SMALL_FIREBALL);
				a.setShooter(p);
				a.setGravity(true);
				a.setVelocity(p.getLocation().getDirection().add(Vector.getRandom().multiply(.1)));
				Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
					timedBlasts(p,ticks-1); //recursive step
				}},1); //static delay of 1s between iterations
				
			} else { //exit condition
				firecooldown.remove(p);
			}
	}
	
	public void loadFlameThrower() {
		NamespacedKey self = new NamespacedKey(this, "flamethrower");
		ItemStack item = new ItemStack(Material.TORCH, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET +""+ ChatColor.RED + "Flamethrower");
		LinkedList<String> lore = new LinkedList<String>();
		lore.add(ChatColor.RESET +""+ ChatColor.GRAY +"[Right Mouse] to fire");
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		item.setItemMeta(meta);
		
		this.flamethrower = item;
		
		ShapedRecipe recipe = new ShapedRecipe(self, item);
		recipe.shape("bb","aa");
		recipe.setIngredient('a', Material.STICK);
		recipe.setIngredient('b', Material.CHARCOAL);
		this.getServer().addRecipe(recipe);
	}
	
}
