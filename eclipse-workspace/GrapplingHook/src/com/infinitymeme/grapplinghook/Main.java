package com.infinitymeme.grapplinghook;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.TripwireHook;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {

	private ItemStack ghook;
	private LinkedList<Player> grappling;
	
	public static final int GRAPPLE_RANGE = 64;
	
	
	@Override
	public void onEnable() {
		loadGhook();
		grappling = new LinkedList<Player>();
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		String c = e.getAction().name();
		if ((e.getHand()!=null)&&(e.getHand().equals(EquipmentSlot.HAND))&&((c.equals("RIGHT_CLICK_BLOCK"))||(c.equals("RIGHT_CLICK_AIR")))) {
			Player p = e.getPlayer();
			ItemStack hand = p.getInventory().getItemInMainHand();
			if ((hand != null)&&(hand.equals(ghook))) {
				e.setCancelled(true);
				Block b = p.getTargetBlockExact(GRAPPLE_RANGE);
				if ((b!=null)&&(b.getType().equals(Material.TRIPWIRE_HOOK))&&(!(grappling.contains(p)))) {
					TripwireHook th = (TripwireHook) (b.getState().getData());
					Arrow a = (Arrow) p.getWorld().spawnEntity(p.getEyeLocation().add(p.getLocation().getDirection()), EntityType.ARROW);
					a.setDamage(0);
					a.setKnockbackStrength(2);
					a.setGravity(false);
					a.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
					a.setShooter(p);
					a.setCritical(true);
					a.setSilent(true);
					Vector v = homingvector(a.getLocation(), b.getLocation().add(0.5,0.5,0.5).add((th.getAttachedFace().getDirection().toLocation(p.getWorld()).multiply(0.5))));
					a.getLocation().setDirection(v);
					v = v.multiply(3);
					a.setVelocity(v);
					grappling.add(p);
					arrowflightloop(a,p,v);
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FISHING_BOBBER_THROW, 1, 0.5f);
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if ((e.getEntity() instanceof Player)&&(e.getCause().equals(DamageCause.FALL))&&(grappling.contains((Player)e.getEntity()))) {
			e.setCancelled(true);
		}
	}
	
	public void arrowflightloop(Arrow a, Player p, Vector v) {
		if (a.isInBlock()) { //arrow impacts
			if (a.getLocation().getBlock().getType().equals(Material.TRIPWIRE_HOOK)) { //has hook
				playergrappleloop(a, p, v, homingvector(p.getLocation(), a.getLocation()));
				p.getWorld().playSound(a.getLocation(), Sound.ENTITY_ARMOR_STAND_HIT, 2f, 1.5f);
				if (a.getLocation().distance(p.getLocation()) > 20) p.playSound(p.getLocation(), Sound.ENTITY_ARMOR_STAND_HIT, 0.5f, 1.5f);
			} else { //no hook
				a.setTicksLived(99999);
				grappling.remove(p);
			}
		} else if (p.getLocation().distance(a.getLocation()) <= GRAPPLE_RANGE){ //still in grapple range
			if (a.isDead()) { //arrow hits entity
				a.setTicksLived(99999);
				grappling.remove(p);
			} else { //normal case
				a.setVelocity(v);
				a.setTicksLived(1);
				Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
					arrowflightloop(a,p,v);
				}},1);
			}
		} else { //not in grapple range
			a.setTicksLived(99999);
			grappling.remove(p);
		}
	}
	
	public void playergrappleloop(Arrow a, Player p, Vector v, Vector pv) {
		if ((a.isInBlock())&&(!a.isDead())&&(p.getLocation().distance(a.getLocation()) <= GRAPPLE_RANGE)) {
			if (p.getLocation().distance(a.getLocation()) > 2) {
				if (p.isSneaking()) { //sneaks
					a.setTicksLived(99999);
					grappling.remove(p);
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 2f, 1.5f);
				} else { //normal case
					drawline(p,a);
					p.setVelocity(pv);
					a.setTicksLived(1);
					Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
						playergrappleloop(a, p, v, homingvector(p.getLocation(), a.getLocation()));
					}},1);
				}
			} else if (!p.isSneaking()) { //close and not sneaking
				p.setVelocity(pv);
				a.setTicksLived(1);
				Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
					playergrappleloop(a, p, v, homingvector(p.getLocation(), a.getLocation()).multiply(0.1));
				}},1);
			} else { //close and sneaking
				a.setTicksLived(99999);
				grappling.remove(p);
				p.setFallDistance(0);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 2f, 1.5f);
			}
			
		} else { //arrow disconnects
			a.setTicksLived(99999);
			a.setGravity(true);
			grappling.remove(p);
		}
	}
	
	public Vector homingvector(Location l1, Location l2) {
		Vector v = l2.toVector().subtract(l1.toVector());
		v = v.multiply(1/(l1.toVector().distance(l2.toVector())));
		return v;
	}
	
	public void drawline(Player p, Arrow a) {
		Location pl = p.getEyeLocation();
		pl.add(homingvector(pl, a.getLocation()).multiply(6));
		while (pl.distance(a.getLocation()) > 1) {
			p.getWorld().spawnParticle(Particle.CRIT, pl, 1, 0, 0, 0, 0);
			pl.add(homingvector(pl, a.getLocation()));
		}
	}
	
	public void loadGhook() {
		NamespacedKey self = new NamespacedKey(this, "grapplinghook");
		ItemStack item = new ItemStack(Material.FISHING_ROD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET +""+ ChatColor.AQUA + "Grappling Hook");
		LinkedList<String> lore = new LinkedList<String>();
		lore.add(ChatColor.RESET +""+ ChatColor.GRAY +"[Right Mouse] to fire at grapple point");
		lore.add(ChatColor.RESET +""+ ChatColor.GRAY +"[Shift] to disconnect from a grapple point");
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		item.setItemMeta(meta);
		this.ghook = item;
		ShapedRecipe recipe = new ShapedRecipe(self, item);
		recipe.shape("abc","deb","ffa");
		recipe.setIngredient('a', Material.QUARTZ);
		recipe.setIngredient('b', Material.IRON_INGOT);
		recipe.setIngredient('c', Material.SLIME_BALL);
		recipe.setIngredient('d', Material.REDSTONE_TORCH);
		recipe.setIngredient('e', Material.FISHING_ROD);
		recipe.setIngredient('f', Material.STRING);
		this.getServer().addRecipe(recipe);
	}
}
