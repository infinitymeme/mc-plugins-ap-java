# mc-plugins-ap-java
This project is an in-depth walkthrough on the creation of Minecraft plugins in Java. Includes software installation, jar setup, and programming techniques.

# Useful Links
[Spigot API Documentation](https://hub.spigotmc.org/javadocs/spigot/overview-summary.html)

[Paper Server Downloads](https://papermc.io/downloads)

[Spigot API Downloads](https://getbukkit.org/download/spigot)

# Installation Instructions
Note that these instructions are for Windows devices only. Minecraft servers cannot be run on macOS.

## Downloading the Main Repository
Download this entire repository by clicking on the green "Clone or Download" button and "Download ZIP".

![Download the Repository](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/cap01.png?raw=true)

 Once it downloads, right click on the zip file and click "Extract All". Extract it to somewhere you'll remember it, we'll be needing its files later.

## Installing Java SE 11
Visit the [Java SE Downloads Page](https://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase11-5116896.html).

![Download Java SE 11](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/cap02.png?raw=true)

Accept the License Agreement, and then download `jdk-11.02_windows-x64_bin.exe`

You may need to make an Oracle account to download it. I just used my student email.

Once it downloads, run the executable. Just click next through everything, there are no settings you need to change.

## Installing Eclipse

Visit the [Eclipse Downloads Page](https://www.eclipse.org/downloads/packages/).

![Download Eclipse](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/cap03.png?raw=true)

Download the latest Windows version of the "Eclipse IDE for Java Developers". The file should be called `eclipse-java-20##-##-R-win32-x86_64.zip`

Once it downloads, extract the zip file and move the `eclipse` folder somewhere you want it. This is a standalone application. I put mine on my desktop.

Run the `eclipse.exe` file inside of the folder. Just leave the workspace as the default. Once it boots to the welcome page, you can close the app.

## Installing Minecraft
Visit the [Minecraft Download Page](https://my.minecraft.net/en-us/download/),

Download and run `MinecraftInstaller.msi`. The default install settings are fine.

Once installed, run the launcher and sign in.

Currently, Minecraft version 1.14 is the latest version (just released!), but we want version 1.13.2.

To set this up, click the three bars at the top right and switch over to the Launch options tab. Click the plus and add another launch profile, and set the game version to release 1.13.2.

Give it a name, and click Save. Switch back over to the News tab, and click the up arrow next to the Play button, and select the new launch profile you added.

Click the Play button, and the game will perform a first-boot. You can close the game and launcher once you reach the title page.

## Installing Optifine (Optional)
Although this is an optional step, it is **highly recommended** because it will result in better client performance across all computers.

Visit the [Optifine Downloads Page](https://optifine.net/downloads).

![Download Optifine](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/cap04.png?raw=true)

Download the latest version of Optifine for Minecraft 1.13.2. Make sure you use the `(mirror)` button since it avoids the bit.ly ads. Your file should be called `OptiFine_1.13.2_HD_U_E#.jar`.

Chrome may ask you if you want to keep the file, if so, click "Keep".

Run the file, and click install.

This will set up another launch profile in the Minecraft launcher.

![Launch Profiles](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/cap05.png?raw=true)

Make sure that when you boot the game next time you use the Optifine launch profile.

## Setting up the Server
Navigate to the location where you downloaded this repository. Extract the `Startup Files.zip` file inside it to the location you want your server kept in.

Rename the folder it extracted to from `Startup Files` to `Minecraft Server` or whatever you want your server folder to be called.

Navigate into the `server` folder and run `start.bat`. This will open a console window, which is the server running. It will eventully stop with an error.

![Server Error](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/cap06.png?raw=true)

Like the error says, you haven't agreed to the EULA. Minimize (not close!) the console and there will be a `eula.txt` in the same directory where the `start.bat` file was.

To agree to the EULA, you just have to change `eula=false` on the last line of the text file to `eula=true`. Make sure you save the file.

Once you have agreed to the EULA, switch back over to the console window and type "y" to restart. The server is now running.

Launch minecraft, and click multiplayer on the title screen. Click direct connect, and type in `localhost` as the Server Address.

Once you join the server, switch over to the console and type `op yourusernamehere`. You can now execute any commands you want in-game, like `/gamemode creative`.

**Optional: Offline Server Mode**

If you find yourself being unable to connect to the server because you could not authenticate with minecraft.net (because it was blocked on school wi-fi, or because you aren't connected to a network), you can turn on offline mode in the server settings. 

Note that this only works if you have signed into your minecraft account in the launcher before.

Navigate to where the `start.bat` file is and find a file called `server.properties` within the directory. Open it and find the line toward the bottom that says `online-mode=true`. Change it to `online-mode=false` and save the file.

Switch over to the console and type `stop`. It will stop and then promp you to restart, so type "y". Once it has restarted, try connecting to it again.

Note: If you enable this mode, the server will identify your username as a new player, so you will have to re-op yourself. Your inventory will also reset.

# Setting Up Eclipse

1. Start eclipse.
1. Uncheck the `Always show Welcome at start up` box in the bottom right
1. Close the welcome window.

![Setup Image 1](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/esetup01.png?raw=true)

### Getting rid of extra windows

Close all windows except for the `Project Explorer` and `Navigator` windows, which I prefer to leave on the left side.

![Setup Image 2](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/esetup02.png?raw=true)

If one of these windows is missing, go under `Window`>`Show View`> and select the missing one.

![Setup Image 3](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/esetup03.png?raw=true)

### Importing this repository's demo projects (Optional)
Close eclipse and navigate to your eclipse workspace. This will likely be at `C:\Users\YourLoginAccount\eclipse-workspace\`. Copy the contents of this repository's `eclipse-workspace` folder to your `eclipse-workspace` folder.

In Eclipse, go to `File`>`Open Projects from File System` and paste your eclipse workspace directory (most likely `C:\Users\YourLoginAccount\eclipse-workspace\`) in the `Import Source:` field. Press `Finish`.

### Placing your API jar in a convenient location (Optional)
If you are the administrator on your computer, you may want to place your `spigot-1.13.2.jar` file, which is required to export your plugins, in a convenient location. Head to your `C:\` directory and make a new folder called `Eclipse-APIs` and drop the `spigot-1.13.2.jar` file here.

By doing this, the demo plugins will work **out of the box** without any build path configuration. Otherwise, you will have to right click the project, go to `Build Path`>`Configure Build Path`>Double Click `spigot-1.13.2.jar`>Find and select your `spigot-1.13.2.jar` file. A copy is provided in the `Startup Files.zip` file from this repository.

# Creating Projects

### Create a new project
1. Right click the blank space in the `Project Explorer` window and select `New`>`Project...`. This will open the `Select a wizard` dialogue.
1. Expand the `Java` folder and select `Java Project`
1. Name your project. I recommend using TitleCase. For example purposes, we'll call ours `DemoProject`. Press `Finish`.
1. If you used TitleCase, a popup will ask you to change the module name to fit conventions, so change it to all lowercase. For example purposes, ours will be `demoproject`.
1. Eclipse will open the `module-info.java` file. Add `requires spigot;` to the module section. Press `ctrl+s` to save the file. It will look something like this:
```java
/**
 * 
 */
/**
 * @author infinitymeme
 *
 */
module demoproject {
	requires spigot;
}
```
Don't worry that spigot is underlined in red. We're about to fix that.
### Configure the build path
1. Right click the project and go to `Build Path`>`Configure Build Path`.
1. Switch to the `Libraries` tab.
1. Highlight `Modulepath` and press `Add External JARs`.
1. Navigate to and select your `spigot-1.13.2.jar` file.
1. Expand the dropdown next to your jar file. Double click on `Javadoc location` and set `Javadoc location path:` to `https://hub.spigotmc.org/javadocs/spigot/overview-summary.html`, then press `Finish`.
1. Ensure that the `JRE System Library` is set to `[JavaSE-11]`. If this is not the case, double click on the field and choose `JavaSE-11 (jdk-11.0.2)` from the `Execution environment` dropdown. 

<details>
  <summary>If you do not have JavaSE-11 (jdk-11.0.2) as an option, expand this dropdown and follow the steps.</summary>
  
  1. Press `Installed JREs...`.
  1. Press `Add...`.
  1. Select `Standard VM` and press `Next >`.
  1. Copy and paste `C:\Program Files\Java\jdk-11.0.2` to the `JRE Home:` field.
  1. Press `Finish` then `Apply and Close`.
  
</details>


Press `Finish`.
Your build path should end up looking like this:
![Setup Image 5](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/esetup05.png?raw=true)

### Create a new package and Main class
1. Right click on `src` and select `New`>`Package`.
1. Name the package following this name convention: `com.authorname.modulename`. These should be the same values in the `module-info.java` file.
![Setup Image 4](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/esetup04.png?raw=true)
1. Press `Finish`.
1. Right click the package and select `New`>`Class`. Name it `Main` and press `Finish`.
1. Add `extends JavaPlugin` to the class definition. Eclipse will underline `JavaPlugin`, so hover over it and select `Import JavaPlugin (org.bukkit.plugin.java)`.
1. Add `onEnable()` and `onDisable()` methods with `@Override` headers. Your `Main.java` file should now look like this:
```java
package com.infinitymeme.demoproject;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}
	
}
```

### Create a plugin.yml
1. Right click the project file (`DemoProject` in this case) and select `New`>`File`.
1. Name the file `plugin.yml`
1. Fill the file with the following information:
```
version: 1.0
api-version: 1.13
name: ProjectName
main: com.author.projectname.Main
author: author
description: description
```
For this example, it looks like this:
```
version: 1.0
api-version: 1.13
name: DemoProject
main: com.infinitymeme.demoproject.Main
author: infinitymeme
description: it's a demo
```

# Useful Techniques
Making plugins all comes down to creativity, both in the function of the plugin and in the design of its code. This section gives templates of techniques to get what you want done quickly and efficiently.

## Event Handling
In order to handle events, you'll need to implement Listener in your class

```java
public class Main extends JavaPlugin implements Listener {
```

and register your events through Bukkit when the plugin enables.

```java
@Override
public void onEnable() {
	Bukkit.getPluginManager().registerEvents(this, this);
}
```


All event handler methods look something like this:

```java
@EventHandler
public void whateverYouWantToCallIt(EventObject e) {
```

The event object has all of the properties you might need to interact with the event.
Here's a very simple example from the `LazerTag` plugin which cancels fall damage as long as a player is playing. This lets the map be more vertically-oriented.

```java
@EventHandler
public void onDamage(EntityDamageEvent e) { //fall damage canceler for those playing
	if ((e.getEntity() instanceof Player)&&(e.getCause().equals(DamageCause.FALL))&&teamValue((Player)e.getEntity())!=0) {
		e.setCancelled(true);
	}
}
```

Notice that unlike a PlayerInteractEvent, this can trigger for any entity, so we check that `e.getEntity() instanceof Player` before casting it to the player type to make other checks. This also immediately rules out doing these extra checks for non-players.

## Delay and Looping

Minecraft runs at 20 ticks per second, and waits for your code to execute before proceeding to the next tick. This creates a problem if you happen to create an infinite loop. Instead, if you wish to create any events that take course over a period of time, such as a constant homing effect, you must use a loop with a delay.

### Delay Function
Queues the code within to be executed after a delay, then proceeds to the next lines of code.
```java
Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
	//your code here
}},DELAY_IN_TICKS);
```
Though it appears simple, this code is more finicky than you would expect because Java. We're going to use this function for debugging to print the current world time in ticks.
```java
public String getTime() {
	return "["+Bukkit.getServer().getWorlds().get(0).getTime()+"] ";
}
```

### Simple use of the Delay Function

Using a for loop with increasing delay, you can make a simple timer from multiple delayed events scheduled in advance, but it's hard to do much with it. The `runTaskLater` method *schedules* the task to be run, then continues. That's why this for loop must schedule events for `i*20` delay. It loads all of the `*tick*` messages in advance. 

```java
//5 second countdown
Bukkit.broadcastMessage(getTime()+"Timer started!");
for (int i=1; i<=5; i++) {
	Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
		Bukkit.broadcastMessage(getTime()+"*tick*");
	}},20*i); //x second * 20 ticks/second
}
Bukkit.broadcastMessage(getTime()+"Done!");
```
Here's the output:

![Countdown Results](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/delay01.png?raw=true)

Please note that you cannot use `i` inside the delayed calls since Java doesn't know whether or not `i` will have been garbage collected due to the method completing.

But, what if you have a loop that could take effect for an infinite amount of time? We'll hit a stack overflow error on `i` eventually.

Or what about an event that occurs after it finishes? What if we want an alarm noise on our timer? 

Or what about a while loop with an end condition? We can't check end conditions every tick because all the future ticks are scheduled instantly, so what do we do?

[Recursion Time](https://www.google.com/search?q=recursion)

### Recursive use of the Delay Function

Here's a full-fledged timer class example. Since we don't really want to have to initialize a timer object, we'll just make it an entirely static class. That means that in our main class we can just do `Timer.startTimer(5);` to use it.

```java
public class Timer() {

	public static void startTimer(int seconds) {
		Bukkit.broadcastMessage(getTime()+"Timer started!");
		timerloop(seconds,seconds);
	}
		
	private static String getTime() {
		return "["+Bukkit.getServer().getWorlds().get(0).getTime()+"] ";
	}

	private void timerloop(int s, int start) {
		if (s > 0) {
			Bukkit.broadcastMessage(getTime()+"*tick* ("+(start-s)+"s)");
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
				timerloop(s-1,start); //recursive step
			}},20); //static delay of 1s between iterations
		} else { //exit condition
			Bukkit.broadcastMessage(getTime()+"*DING*");
		}
	}
}
```

Here's the output:

![Countdown Results](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/delay02.png?raw=true)

Pretty clean right? We even can say how many seconds it has been and do a special message when the timer ends. We couldn't do that before with the for loop.

Notice that any static data (in this case the initial amount of starting seconds) must be carried through local variables alongside the dynamic data (in this case the remaining seconds).

You can find examples of this across all of the provided plugins, but most are too complex to explain concisely here.

## Homing Vector
Generates a vector from location 1 to location 2. This function is useful for setting the velocity (vector) of entities, making them home to a particular location. You might find other uses for it too.
```java
public Vector homingvector(Location l1, Location l2) {
	Vector v = l2.toVector().subtract(l1.toVector());
	v = v.multiply(1/(l1.toVector().distance(l2.toVector())));
	return v;
}
```
Example to make an arrow fly at a player's head:
```java
Arrow a = //some arrow from somewhere;
Player p = //some player from somewhere;
//set arrow's velocity to 3x the vector FROM arrow TO player eyes
a.setVelocity(homingvector(a.getLocation(), p.getEyeLocation()).multiply(3));
```

## Action Bar Message
This function is useful for sending a message to the player through the action bar. It supports ampersand color codes such as `&6` (gold). See the reference chart below for those.
```java
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
```
![Color Codes Chart](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/colorcodes.png?raw=true)

Note that the function replaces the ampersand `&` with the section symbol `§` for convenience. You will need to modify it if you wish to use the symbol normally.

## Custom Items / Recipes
For custom recipes, I recommend adding a load function for each, and having the constructor (generally `onEnable()` since recipes are often in main) call it. Here's a template.
```java
public void loadYOURITEM() {
	NamespacedKey self = new NamespacedKey(this, "YOURITEM");
	ItemStack item = new ItemStack(Material.RESULTING_ITEM, RESULTING_ITEM_COUNT);
	ShapedRecipe recipe = new ShapedRecipe(self, item);
	recipe.shape("aaa","bbb","ccc");
	recipe.setIngredient('a', Material.ITEM_ONE);
	recipe.setIngredient('b', Material.ITEM_TWO);
	recipe.setIngredient('c', Material.ITEM_THREE);
	this.getServer().addRecipe(recipe);
}
```

For custom items, they are often differentiated by a custom name or lore, and obtained through a custom recipe. In order to compare the custom item to the item in events, you should store it to an `ItemStack` instance variable. Let's make a hot potato item as an example.
```java
public class Main extends JavaPlugin {

	private ItemStack hotpotato;
	
	@Override
	public void onEnable() {
		loadHotPotato();
	}
	
	public void loadHotPotato() {
		NamespacedKey self = new NamespacedKey(this, "hotpotato");
		ItemStack item = new ItemStack(Material.BAKED_POTATO, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET +""+ ChatColor.RED +""+ ChatColor.BOLD+"Hot Potato");
		LinkedList<String> lore = new LinkedList<String>();
		lore.add(ChatColor.RESET +""+ ChatColor.RED +""+ ChatColor.ITALIC+"WATCH OUT! IT'S HOT!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		this.hotpotato = item;
		
		ShapedRecipe recipe = new ShapedRecipe(self, item);
		recipe.shape("xxx","xox","xxx");
		recipe.setIngredient('x', Material.LAVA_BUCKET);
		recipe.setIngredient('o', Material.BAKED_POTATO);
		this.getServer().addRecipe(recipe);
	}

}
```
Note that when you compare to an ItemStack from an event, you may have to remove additional information from the event's ItemStack to make it match. For example, if you wanted to check if a player is holding the lazer rifle regardless of its enchantments and quantity, you might do the following:
```java
public ItemStack ignoreCount(ItemStack item) {
	ItemStack i = item.clone(); //clone the item so we can set its unimportant properties to be correct without modifying the original itemstack
	i.setAmount(1); //we don't care about count, so set it to the original recipe count
	return i;
}

//so then to compare we do...
public static void onEvent (Event e) {
	ItemStack eventitem = e.getItem(); //item from some event, could be 64 hot potatoes.
	if (ignoreCount(eventitem).equals(hotpotato)) doHotPotatoExplosion(e.getLocation());
}
```

## Commands
There's a lot you can do with commands, but here's the basic rundown.


### Create the command
This function is your template for creating any command.
```java
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
}
```
First you will need to check the command name.
```java
if (cmd.getName.equals("YOURCOMMAND")) {
}
```
For some commands, you may wish that only players or only the console be able to execute it.
```java
if (sender instanceof Player) {
	//players only
} else {
	//console only
}
```

Arguments are passed in through the `String[] args` parameter. Arguments are separated by a space, so the command `/setname billy bob` will call the function with `cmd.getName()` resolving to `setname` and `args` containing `{"billy", "bob"}`. Ensure that you validate all argument parameters.

Finally, this function returns a boolean. Return `false` if the command parameters are determined to be invalid. (For example, if a number is given instead of a player username). This will display the Usage message (see below) to the sender. Otherwise, return `true` once the command completes.

If something went wrong but the command syntax was correct, like if your command teleports the player to the target block but they weren't looking at any block, return `true` and use `sender.sendMessage()` to let them know what went wrong.

### Modify plugin.yml
Once you write your command, you will need to add it to the `plugin.yml` file. This is where you can add aliases (other names / shortcuts for the command), and specific permissions if you have a permissions manager. This is also where you specify the Usage message to be displayed if the command arguments are incorrect. Here's the template from earlier with commands added:
```
version: 1.0
api-version: 1.13
name: ProjectName
main: com.author.projectname.Main
author: author
description: description
commands:
    command_one:
        aliases: [cmd_one, uno]
        description: this is a command. you need permission to use it.
        permission: projectname.permissionname
        usage: "Usage: /command_one [player] [number]"
    command_two:
        aliases: [cmd_two, dos]
        description: anyone can use me
        usage: "Usage: /command_two [number]"

```

## More Techniques
There are certainly more things you can do in plugins, but this is where I stop holding your hand. Google is your best friend, and you have a few example plugins that you can dig through for reference. The code should be *relatively* well-written.

# Exporting Projects

1. Right click on the project folder (for example, `DemoProject`)
1. Select `Export`
1. Expand the `Java` folder and select `JAR file`
1. Press `Next >`.
1. Press `Browse` to select the export destination and jar file name. This will most likely be in your `server/plugins/` directory.
1. Press `Finish`.

## Running and Testing Your Plugins

While you can export the plugin jar files to any location, if you want your plugins to load into your server, you must place them in the `plugins` folder within the server directory.

You can export and overwrite plugin jars while the server is running, then use the command `/reload confirm` in Minecraft to reload all plugins on the server.

While you can use `System.out.println()` to send messages to the console, it is often more useful to use `Bukkit.broadcastMessage()` to send them to the chat so you don't have to tab back to the console.
