package com.infinitymeme.customrecipes;


import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_13_R2.PacketPlayOutTitle;
import net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_13_R2.PacketPlayOutTitle.EnumTitleAction;

public class Main extends JavaPlugin implements Listener {
		
	public static final int FASTBUILD_RADIUS = 2;
	public static final int MAX_BUILDRANGE = 32;
	public static final Material[] UNIMPORTANT = {
			Material.AIR,
			Material.CAVE_AIR,
			Material.WATER
	};
	
	private int recursivecooldown = 0;
		
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		loadNametag();
		loadFlint();
		loadSmoothstone();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent e) {
		System.out.println("[Ping] from "+e.getAddress().toString());
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		ItemStack drop = e.getItemDrop().getItemStack();
		ItemStack hand = e.getPlayer().getInventory().getItemInMainHand();
		if (hand.getType().equals(Material.AIR)) {
			ItemMeta meta = drop.getItemMeta();
			if (meta != null) {
				if (meta.getDisplayName().equals(ChatColor.GOLD+"Fastbuild Wand")) {
					meta.setDisplayName(ChatColor.GOLD+"Fastbuild Wand "+ChatColor.GRAY+"(Recursive)");
					drop.setItemMeta(meta);
					e.setCancelled(true);
				} else if (meta.getDisplayName().equals(ChatColor.GOLD+"Fastbuild Wand "+ChatColor.GRAY+"(Recursive)")) {
					meta.setDisplayName(ChatColor.GOLD+"Fastbuild Wand");
					drop.setItemMeta(meta);
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if ((p.getGameMode().equals(GameMode.CREATIVE))&&(e.getHand()!=null)&&(e.getHand().equals(EquipmentSlot.HAND))) {
			ItemStack hand = p.getInventory().getItemInOffHand();
			ItemMeta meta = hand.getItemMeta();
			if ((hand != null)&&(meta!=null)) {
				if (hand.getItemMeta().getDisplayName().equals(ChatColor.GOLD+"Fastbuild Wand")) {
					String action = e.getAction().name();
					if (action.equals("RIGHT_CLICK_BLOCK")&&(e.isBlockInHand())) {
						World w = p.getWorld();
						LinkedList<int[]> blocks = new LinkedList<int[]>();
						Block b = e.getClickedBlock();
						BlockFace bf = e.getBlockFace();
						int modx = bf.getModX();
						int mody = bf.getModY();
						int modz = bf.getModZ();
						int gox = (bf.getModX()==0)? 1 : 0;
						int goy = (bf.getModY()==0)? 1 : 0;
						int goz = (bf.getModZ()==0)? 1 : 0;
						for (int x=(-FASTBUILD_RADIUS)*gox; x<=(FASTBUILD_RADIUS)*gox; x++) {
							for (int y=(-FASTBUILD_RADIUS)*goy; y<=(FASTBUILD_RADIUS)*goy; y++) {
								for (int z=(-FASTBUILD_RADIUS)*goz; z<=(FASTBUILD_RADIUS)*goz; z++) {
									int xx = x+b.getX();
									int yy = y+b.getY();
									int zz = z+b.getZ();
									if ((w.getBlockAt(xx,yy,zz).getType().equals(e.getMaterial()))&&(unimportantBlock(w.getBlockAt(xx+modx,yy+mody,zz+modz).getType()))) {
										int[] tmp = {xx+modx,yy+mody,zz+modz};
										blocks.add(tmp);
									}
									
								}
							}
						}
						Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
							BlockData bd = w.getBlockAt(e.getClickedBlock().getLocation().add(e.getBlockFace().getDirection())).getBlockData();
							if (bd.getMaterial().equals(e.getMaterial())) {
								for (int[] bset : blocks) {
									w.playEffect(new Location(w, bset[0],bset[1],bset[2]), Effect.STEP_SOUND, bd.getMaterial(), 50);
									w.getBlockAt(bset[0],bset[1],bset[2]).setBlockData(bd);
								}
							}
						}},1);
					} else if (action.equals("LEFT_CLICK_BLOCK")) {
						World w = p.getWorld();
						LinkedList<int[]> blocks = new LinkedList<int[]>();
						Block b = e.getClickedBlock();
						BlockFace bf = e.getBlockFace();
						int modx = bf.getModX();
						int mody = bf.getModY();
						int modz = bf.getModZ();
						int gox = (bf.getModX()==0)? 1 : 0;
						int goy = (bf.getModY()==0)? 1 : 0;
						int goz = (bf.getModZ()==0)? 1 : 0;
						for (int x=(-FASTBUILD_RADIUS)*gox; x<=(FASTBUILD_RADIUS)*gox; x++) {
							for (int y=(-FASTBUILD_RADIUS)*goy; y<=(FASTBUILD_RADIUS)*goy; y++) {
								for (int z=(-FASTBUILD_RADIUS)*goz; z<=(FASTBUILD_RADIUS)*goz; z++) {
									int xx = x+b.getX();
									int yy = y+b.getY();
									int zz = z+b.getZ();
									if ((w.getBlockAt(xx,yy,zz).getType().equals(b.getType()))&&((unimportantBlock(w.getBlockAt(xx+modx,yy+mody,zz+modz).getType())))) {
										int[] tmp = {xx,yy,zz};
										blocks.add(tmp);
									}
									
								}
							}
						}
						Material m = b.getType();
						Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
							if (unimportantBlock(b.getType())) {
								for (int[] bset : blocks) {
									w.playEffect(new Location(w, bset[0],bset[1],bset[2]), Effect.STEP_SOUND, m, 50);
									w.getBlockAt(bset[0],bset[1],bset[2]).setType(Material.AIR);
								}
							}
						}},1);
					}
				} else if (hand.getItemMeta().getDisplayName().equals(ChatColor.GOLD+"Fastbuild Wand "+ChatColor.GRAY+"(Recursive)")) {
					if (recursivecooldown > 0) {
						actionBar("&cPlease wait, recursive event in progress.",p);
						e.setCancelled(true);
					} else {
						recursivecooldown = 5;
						recursiveCooldown(p);
						//TODO recursive
						String action = e.getAction().name();
						if (action.equals("RIGHT_CLICK_BLOCK")&&(e.isBlockInHand())) {
							BlockFace bf = e.getBlockFace();
							int gox = (bf.getModX()==0)? 1 : 0;
							int goy = (bf.getModY()==0)? 1 : 0;
							int goz = (bf.getModZ()==0)? 1 : 0;
							Block b = e.getClickedBlock().getWorld().getBlockAt(e.getClickedBlock().getLocation().add(bf.getDirection()));
							Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
								BlockData bd = b.getBlockData();
								if (bd.getMaterial().equals(e.getMaterial())) {
									b.setType(Material.AIR);
									recursiveFill(p, bd, b, bf.getDirection(), gox, goy, goz);
								}
							}},1);
						} else if (action.equals("LEFT_CLICK_BLOCK")) {
							BlockFace bf = e.getBlockFace();
							int gox = (bf.getModX()==0)? 1 : 0;
							int goy = (bf.getModY()==0)? 1 : 0;
							int goz = (bf.getModZ()==0)? 1 : 0;
							Block b = e.getClickedBlock();
							BlockData bd = b.getBlockData();
							e.setCancelled(true);
							Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
								recursiveBreak(p, bd, b, bf.getDirection(), gox, goy, goz);
							}},1);
						}
					}
				}
			}
		}
	}
	
	public void actionBar(String msg, Player p) { 
		msg = msg.replace('&', '§');
		PacketPlayOutTitle packet = new PacketPlayOutTitle(
				EnumTitleAction.ACTIONBAR,
				ChatSerializer.a("{\"text\":\"" +msg+ "\"}"),
				10,
				1,
				10
				);
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
	}
	
	public boolean unimportantBlock(Material m) {
		for (Material ma : UNIMPORTANT) {
			if (m.equals(ma)) return true;
		}
		return false;
	}
	
	public void recursiveCooldown(Player p) {
		if (recursivecooldown > 1) {
			recursivecooldown -= Math.ceil(recursivecooldown/4.0);
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
				recursiveCooldown(p);
			}},1);
		} else if (recursivecooldown == 1) {
			recursivecooldown = 0;
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
				recursiveCooldown(p);
			}},1);
		} else {
			actionBar("&aDone!", p);
			p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 1);
		}
	}
	
	public void recursiveFill(Player p, BlockData bd, Block b, Vector v, int gox, int goy, int goz) {//TODO
		if (p.getLocation().distance(b.getLocation()) <= MAX_BUILDRANGE) {
			ItemStack hand = p.getInventory().getItemInOffHand();
			if (hand != null) {
				ItemMeta meta = hand.getItemMeta();
				if ((meta!=null)&&(meta.getDisplayName().equals(ChatColor.GOLD+"Fastbuild Wand "+ChatColor.GRAY+"(Recursive)"))) {
					if ((unimportantBlock(b.getType()))&&(unimportantBlock(b.getWorld().getBlockAt(b.getLocation().add(v)).getType()))) {
						recursivecooldown++;
						b.setBlockData(bd);
						b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, bd.getMaterial(), 50);
						if (gox==1) {
							Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
								recursiveFill(p,bd,b.getWorld().getBlockAt(b.getX()+1,b.getY(),b.getZ()),v,gox,goy,goz);
								recursiveFill(p,bd,b.getWorld().getBlockAt(b.getX()-1,b.getY(),b.getZ()),v,gox,goy,goz);
							}},1);
						}
						if (goy==1) {
							Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
								recursiveFill(p,bd,b.getWorld().getBlockAt(b.getX(),b.getY()+1,b.getZ()),v,gox,goy,goz);
								recursiveFill(p,bd,b.getWorld().getBlockAt(b.getX(),b.getY()-1,b.getZ()),v,gox,goy,goz);
							}},1);
						}
						if (goz==1) {
							Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
								recursiveFill(p,bd,b.getWorld().getBlockAt(b.getX(),b.getY(),b.getZ()+1),v,gox,goy,goz);
								recursiveFill(p,bd,b.getWorld().getBlockAt(b.getX(),b.getY(),b.getZ()-1),v,gox,goy,goz);
							}},1);
						}
					}
				}
			}
		}
		
	}
	
	public void recursiveBreak(Player p, BlockData bd, Block b, Vector v, int gox, int goy, int goz) {//TODO
		if (p.getLocation().distance(b.getLocation()) <= MAX_BUILDRANGE) {
			ItemStack hand = p.getInventory().getItemInOffHand();
			if (hand != null) {
				ItemMeta meta = hand.getItemMeta();
				if ((meta!=null)&&(meta.getDisplayName().equals(ChatColor.GOLD+"Fastbuild Wand "+ChatColor.GRAY+"(Recursive)"))) {
					if ((b.getType().equals(bd.getMaterial()))&&(unimportantBlock(b.getWorld().getBlockAt(b.getLocation().add(v)).getType()))) {
						recursivecooldown++;
						b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, bd.getMaterial(), 50);
						b.setType(Material.AIR);
						if (gox==1) {
							Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
								recursiveBreak(p,bd,b.getWorld().getBlockAt(b.getX()+1,b.getY(),b.getZ()),v,gox,goy,goz);
								recursiveBreak(p,bd,b.getWorld().getBlockAt(b.getX()-1,b.getY(),b.getZ()),v,gox,goy,goz);
							}},1);
						}
						if (goy==1) {
							Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
								recursiveBreak(p,bd,b.getWorld().getBlockAt(b.getX(),b.getY()+1,b.getZ()),v,gox,goy,goz);
								recursiveBreak(p,bd,b.getWorld().getBlockAt(b.getX(),b.getY()-1,b.getZ()),v,gox,goy,goz);
							}},1);
						}
						if (goz==1) {
							Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
								recursiveBreak(p,bd,b.getWorld().getBlockAt(b.getX(),b.getY(),b.getZ()+1),v,gox,goy,goz);
								recursiveBreak(p,bd,b.getWorld().getBlockAt(b.getX(),b.getY(),b.getZ()-1),v,gox,goy,goz);
							}},1);
						}
					}
				}
			}
		}
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("setname")) {
			if ((sender instanceof Player)) {
				if (args.length >= 1) {
					try {
						Player p = (Player) sender;
						ItemStack hand = p.getInventory().getItemInMainHand();
						if (hand != null) {
							String s = "";
							for (String a : args) s+=a+" ";
							s = s.substring(0, s.length()-1);
							s = s.replace('&', '§');
							
							ItemMeta meta = hand.getItemMeta();
							meta.setDisplayName(s);
							hand.setItemMeta(meta);
							p.getInventory().setItemInMainHand(hand);
							return true;
						} else {
							p.sendMessage(ChatColor.RED+"No item in main hand.");
							return true;
						}
						
					} catch (Exception ex) {return false;}
					
				}
			} else {
				System.out.println("You cannot use this command through the console!");
			}
			
		}
		
		return false;
	}
	
	public void loadNametag() {
		NamespacedKey self = new NamespacedKey(this, "nametag");
		ItemStack item = new ItemStack(Material.NAME_TAG, 1);
		ShapedRecipe recipe = new ShapedRecipe(self, item);
		recipe.shape("  o"," i ","x  ");
		recipe.setIngredient('o', Material.SLIME_BALL);
		recipe.setIngredient('i', Material.STRING);
		recipe.setIngredient('x', Material.PAPER);
		this.getServer().addRecipe(recipe);
	}
	
	public void loadFlint() {
		NamespacedKey self = new NamespacedKey(this, "graveltoflint");
		ItemStack item = new ItemStack(Material.FLINT, 8);
		ShapedRecipe recipe = new ShapedRecipe(self, item);
		recipe.shape("xxx","xox","xxx");
		recipe.setIngredient('x', Material.GRAVEL);
		recipe.setIngredient('o', Material.COBBLESTONE);
		this.getServer().addRecipe(recipe);
	}
	
	public void loadSmoothstone() {
		NamespacedKey self = new NamespacedKey(this, "smoothstone");
		ItemStack item = new ItemStack(Material.SMOOTH_STONE, 1);
		ShapedRecipe recipe = new ShapedRecipe(self, item);
		recipe.shape(" x"," x");
		recipe.setIngredient('x', Material.STONE_SLAB);
		this.getServer().addRecipe(recipe);
	}
		
}
