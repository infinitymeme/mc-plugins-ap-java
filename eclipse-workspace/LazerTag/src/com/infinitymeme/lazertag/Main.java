package com.infinitymeme.lazertag;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {
	
	//BEGIN CONFIGURATION
	private static final double HITBOX_SIZE = 0.5;
	private static final int MAX_DISTANCE = 100;
	private static final int FIRE_COOLDOWN = 10;
	private static final int RESPAWN_TIME = 5*20;
	private static final int[] SLOW_THRESHOLD = {6, 8, 10, 12, 14, 16};
	//END CONFIGURATION
	
	
	private Location redspawn;
	private Location bluespawn;
	
	private LinkedList<Player> redteam;
	private LinkedList<Player> blueteam;
	
	private LinkedList<Player> firecooldown;
	
	private LinkedHashMap<Player, Integer> streak;
	
	private ItemStack lazerrifle;
	
	private ItemStack bluehelmet;
	private ItemStack bluechestplate;
	private ItemStack blueleggings;
	private ItemStack blueboots;
	
	private ItemStack redhelmet;
	private ItemStack redchestplate;
	private ItemStack redleggings;
	private ItemStack redboots;
	
	
	
	//TEAM CONSTANTS
	private static final int TEAM_BLUE = -1;
	private static final int TEAM_RED = 1;
	private static final int TEAM_NONE = 0;
	

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		redspawn = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
		bluespawn = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
		
		redteam = new LinkedList<Player>();
		blueteam = new LinkedList<Player>();
		
		firecooldown = new LinkedList<Player>();
		
		streak = new LinkedHashMap<Player, Integer>();
		
		loadArmor();
		loadLazerRifle();
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if ((e.getHand()!=null)&&(e.getHand().equals(EquipmentSlot.HAND))) {
			Player p = e.getPlayer();
			ItemStack hand = p.getInventory().getItemInMainHand();
			if ((hand != null)&&(hand.equals(lazerrifle))) {
				e.setCancelled(true);
				String action = e.getAction().name();
				if ((action.equals("RIGHT_CLICK_AIR"))||(action.equals("RIGHT_CLICK_BLOCK"))) {
					if ((!firecooldown.contains(p))) {
						firecooldown.add(p);
						Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
							firecooldown.remove(p);
						}},FIRE_COOLDOWN);
						for (Player hit : fireLazer(p)) {
							tag(hit, p, 0, p.getLocation());
						}
					}
					
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) { //damage canceler for those playing
		if ((e.getEntity() instanceof Player)&&(teamValue((Player)e.getEntity())!=0)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) { //drop canceler for those playing
		if (teamValue(e.getPlayer())!=0) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInvMove(InventoryClickEvent e) { //inv edit canceler for those playing
		if (teamValue((Player)e.getWhoClicked())!=0) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onSwapHands(PlayerSwapHandItemsEvent e) { //hand swap canceler for those playing
		if (teamValue(e.getPlayer())!=0) {
			e.setCancelled(true);
		}
	}
	
	public int teamDiff(Player p, Player pp) {//1 same team, -1 diff team, 0 no team
		return teamValue(p)*teamValue(pp);
	}
	
	public int teamValue(Player p) { //use team value constants
		if (blueteam.contains(p)) return -1;
		else if (redteam.contains(p)) return 1;
		return 0;
	}
	
	public Vector homingvector(Location l1, Location l2) {
		Vector v = l2.toVector().subtract(l1.toVector());
		v = v.multiply(1/(l1.toVector().distance(l2.toVector())));
		return v;
	}
	
	public LinkedList<Player> fireLazer(Player p) {
		LinkedList<Player> hit = new LinkedList<Player>();
		Location l = p.getEyeLocation();
		Vector v = l.getDirection();
		l.add(v.multiply(0.5));
		int distance=0;
		while ((l.getBlock().getType().equals(Material.AIR)||(l.getBlock().getType().equals(Material.CAVE_AIR))||(l.getBlock().getType().equals(glassColor(teamValue(p)))))&&(distance<MAX_DISTANCE)) {
			l.getWorld().spawnParticle(particleColor(teamValue(p)), l, 1);
			Player np = null;
			double nearest = HITBOX_SIZE+1;
			for (Entity ent:l.getWorld().getNearbyEntities(l, HITBOX_SIZE, HITBOX_SIZE, HITBOX_SIZE)) {
				if (ent instanceof Player) {
					Player entp = (Player) ent;
					if ((!entp.equals(p))&&(entp.getGameMode().equals(GameMode.ADVENTURE))) {
						if ((entp.getLocation().distance(l) < nearest)||(entp.getEyeLocation().distance(l) < nearest)) np = (Player) ent;
					}
				}
			}
			if ((np != null)&&(teamDiff(p, np) <= TEAM_NONE)&&(!hit.contains(np))) { //enemy team or no team
				hit.add(np);
			}
			l.add(v);
			distance++;
		}
		if (distance < MAX_DISTANCE) {
			p.getWorld().playEffect(l, Effect.STEP_SOUND, l.getBlock().getType(), 100);
		}

		return hit;
	}
	
	public void tag(Player p, Player tagger, int tick, Location l) {
		if (tick == 0) {
			p.playEffect(EntityEffect.HURT);
			
			Firework fw = (Firework) p.getWorld().spawnEntity(p.getEyeLocation(), EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            FireworkEffect efx1 = FireworkEffect.builder()
            		.flicker(true)
            		.withColor(fireworkColor(teamValue(p)))
            		.withFade(Color.WHITE)
            		.with(Type.BURST)
            		.trail(false)
            		.build();
            fwm.addEffect(efx1);
            fwm.setPower(0);
            fw.setFireworkMeta(fwm);
            fw.setSilent(true);
            
			p.setGameMode(GameMode.SPECTATOR);
			p.removePotionEffect(PotionEffectType.SPEED);
			p.sendTitle("", ChatColor.GOLD+"Eliminated by "+chatColor(teamValue(tagger))+tagger.getName(), 5, RESPAWN_TIME-5, 5);
			Bukkit.broadcastMessage(chatColor(teamValue(tagger))+tagger.getName()+ChatColor.GOLD+" eliminated "+chatColor(teamValue(p))+p.getName());
			
			if (!streak.containsKey(tagger)) streak.put(tagger,0);
			int s = streak.get(tagger);
			s++;
			streak.put(tagger,s); 
			switch (s) {
			case 5:
				Bukkit.broadcastMessage(chatColor(teamValue(tagger))+tagger.getName()+ChatColor.GOLD+" is dominating! (5)");
				break;
			case 10:
				Bukkit.broadcastMessage(chatColor(teamValue(tagger))+tagger.getName()+ChatColor.GOLD+" is unstoppable! (10)");
				break;
			case 15:
				Bukkit.broadcastMessage(chatColor(teamValue(tagger))+tagger.getName()+ChatColor.GOLD+" is godlike! (15)");
				break;
			case 20:
				Bukkit.broadcastMessage(chatColor(teamValue(tagger))+tagger.getName()+ChatColor.GOLD+" is an Epic Gamer! (20)");
				break;
			}
			if (streak.containsKey(p)) {
				int ss = streak.get(p);
				if (ss >= 5) {
					Bukkit.broadcastMessage(chatColor(teamValue(tagger))+tagger.getName()+ChatColor.GOLD+" shutdown "+chatColor(teamValue(p))+p.getName()+ChatColor.GOLD+"'s streak of "+ss+"!");
				}
			}
			final Location nextl = p.getLocation();
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
				tag(p, tagger, tick+1, nextl);
				fw.detonate();
			}},1);
		} else if (tick < RESPAWN_TIME) {
			try { //homingvector errors if same location, which can only really happen if p spectates their tagger. this escapes that.
				p.teleport(l.setDirection(homingvector(p.getEyeLocation(), tagger.getEyeLocation())));
			} catch (Exception ex) {
				p.setSpectatorTarget(null);
			}
			
			for (int i=SLOW_THRESHOLD.length-1; i>=0; i--) {
				if (p.getLocation().distance(tagger.getLocation()) >= SLOW_THRESHOLD[i]) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, i, false, false, false), true);
					break;
				}
			}
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
				tag(p, tagger, tick+1, l);
			}},1);
		} else {
			p.removePotionEffect(PotionEffectType.SLOW);
			resetPlayer(p);
		}
	}
	
	public Particle particleColor(int teamvalue) { //color you fire with
		Particle[] p = {Particle.DRIP_WATER, Particle.TOWN_AURA, Particle.DRIP_LAVA};
		return p[teamvalue+1];
	}
	
	public Color fireworkColor(int teamvalue) { //color you explode in
		Color[] p = {Color.RED, Color.GRAY, Color.BLUE};
		return p[teamvalue+1];
	}
	
	public ChatColor chatColor(int teamvalue) { //color of your name
		ChatColor[] p = {ChatColor.BLUE, ChatColor.GRAY, ChatColor.RED};
		return p[teamvalue+1];
	}
	
	public Material glassColor(int teamvalue) { //glass you can shoot through
		Material[] p = {Material.BLUE_STAINED_GLASS, Material.GRAY_STAINED_GLASS, Material.RED_STAINED_GLASS};
		return p[teamvalue+1];
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (cmd.getName().equals("ltspawn")) {
				if (args.length >= 1) {
					if (args[0].equals("red")) {
						redspawn = ((Player) sender).getLocation();
						return true;
					} else if (args[0].equals("blue")) {
						bluespawn = ((Player) sender).getLocation();
						return true;
					}
				}
			}
		}
		if (cmd.getName().equals("team")) {
			if (args.length >= 1) {
				Player p;
				if ((args.length >= 2)&&((sender.hasPermission("lazertag.team.others")||(!(sender instanceof Player))))) p = Bukkit.getPlayer(args[1]);
				else p = (Player) sender;
				
				if ((sender.hasPermission("lazertag.admin"))||(teamValue(p)==TEAM_NONE)) {
					if (args[0].equals("red")) {
						if (blueteam.contains(p)) blueteam.remove(p);
						redteam.add(p);
						return true;
					} else if (args[0].equals("blue")) {
						if (redteam.contains(p)) redteam.remove(p);
						blueteam.add(p);
						return true;
					} else if (args[0].equals("none")) {
						if (redteam.contains(p)) redteam.remove(p);
						if (blueteam.contains(p)) blueteam.remove(p);
						if (streak.containsKey(p)) streak.remove(p);
						return true;
					}
				} else {
					return true;
				}
				
			}
		} else if (cmd.getName().equals("respawnall")) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				resetPlayer(p);
			}
			firecooldown = new LinkedList<Player>();
			return true;
		} else if (cmd.getName().equals("respawn")) {
			if ((args.length > 0)&&(sender.hasPermission("lazertag.admin"))) {
				Player p = Bukkit.getPlayer(args[0]);
				if (p!=null) {
					resetPlayer(p);
					return true;
				} else {
					return false;
				}
			} else {
				resetPlayer((Player) sender);
			}
		}
	return false;
	}
	
	public void resetPlayer(Player p) {
		p.getInventory().setHeldItemSlot(0);
		p.getInventory().setItemInMainHand(lazerrifle);
		if (teamValue(p) == TEAM_BLUE) {
			p.teleport(bluespawn);
			PlayerInventory i = p.getInventory();
			i.setHelmet(bluehelmet);
			i.setChestplate(bluechestplate);
			i.setLeggings(blueleggings);
			i.setBoots(blueboots);
			
		}
		else if (teamValue(p) == TEAM_RED) {
			p.teleport(redspawn);
			PlayerInventory i = p.getInventory();
			i.setHelmet(redhelmet);
			i.setChestplate(redchestplate);
			i.setLeggings(redleggings);
			i.setBoots(redboots);
		}
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 2, false, false, false), true);
		p.setGameMode(GameMode.ADVENTURE);
		streak.put(p,0);
		
	}
	
	public void loadArmor() {
		bluehelmet = new ItemStack(Material.LEATHER_HELMET, 1);
		LeatherArmorMeta bhm = ((LeatherArmorMeta) bluehelmet.getItemMeta());
		bhm.setColor(Color.BLUE);
		bluehelmet.setItemMeta(bhm);
		
		bluechestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		LeatherArmorMeta bcm = ((LeatherArmorMeta) bluechestplate.getItemMeta());
		bcm.setColor(Color.BLUE);
		bluechestplate.setItemMeta(bhm);
		
		blueleggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		LeatherArmorMeta blm = ((LeatherArmorMeta) blueleggings.getItemMeta());
		blm.setColor(Color.BLUE);
		blueleggings.setItemMeta(bhm);
		
		blueboots = new ItemStack(Material.LEATHER_BOOTS, 1);
		LeatherArmorMeta bbm = ((LeatherArmorMeta) blueboots.getItemMeta());
		bbm.setColor(Color.BLUE);
		blueboots.setItemMeta(bbm);
		
		
		redhelmet = new ItemStack(Material.LEATHER_HELMET, 1);
		LeatherArmorMeta rhm = ((LeatherArmorMeta) redhelmet.getItemMeta());
		rhm.setColor(Color.RED);
		redhelmet.setItemMeta(rhm);
		
		redchestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		LeatherArmorMeta rcm = ((LeatherArmorMeta) redchestplate.getItemMeta());
		rcm.setColor(Color.RED);
		redchestplate.setItemMeta(rcm);
		
		redleggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		LeatherArmorMeta rlm = ((LeatherArmorMeta) redleggings.getItemMeta());
		rlm.setColor(Color.RED);
		redleggings.setItemMeta(rlm);
		
		redboots = new ItemStack(Material.LEATHER_BOOTS, 1);
		LeatherArmorMeta rbm = ((LeatherArmorMeta) redboots.getItemMeta());
		rbm.setColor(Color.RED);
		redboots.setItemMeta(rbm);
	}

	
	public void loadLazerRifle() {
		NamespacedKey self = new NamespacedKey(this, "lazerrifle");
		ItemStack item = new ItemStack(Material.GOLDEN_AXE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET +""+ ChatColor.AQUA + "Lazer Rifle");
		LinkedList<String> lore = new LinkedList<String>();
		lore.add(ChatColor.RESET +""+ ChatColor.GRAY +"[Right Mouse] to fire");
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
		item.setItemMeta(meta);
		
		this.lazerrifle = item;
		
		ShapedRecipe recipe = new ShapedRecipe(self, item);
		recipe.shape(" x"," x");
		recipe.setIngredient('x', Material.BEDROCK);
		this.getServer().addRecipe(recipe);
	}
	
}
