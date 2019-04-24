# mc-plugins-ap-java

# Useful Links
[Spigot API Documentation](https://hub.spigotmc.org/javadocs/spigot/overview-summary.html)

[Paper Server Downloads](https://papermc.io/downloads)

[Spigot API Downloads](https://getbukkit.org/download/spigot)

# Installation Instructions
Note that these instructions are for Windows devices only. Minecraft servers cannot be run on macOS.

## Downloading the Main Repository
Download this entire repository by clicking on the green "Clone or Download" button and "Download ZIP". Once it downloads, right click on the zip file and click "Extract All". Extract it to somewhere you'll remember it, we'll be needing its files later.

![Download the Repository](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/cap01.png?raw=true)

## Installing Java SE 11
Visit the [Java SE Downloads Page](https://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase11-5116896.html).

![Download Java SE 11](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/cap02.png?raw=true)

Accept the License Agreement, and then download `jdk-11.02_windows-x64_bin.exe`

You may need to make an Oracle account to download it. I just used my student email.

Once it downloads, run the executable. Just click next through everything, there are no settings you need to change.

## Installing Eclipse
Visit the [Eclipse Downloads Page](https://www.eclipse.org/downloads/packages/)

![Download Eclipse](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/cap03.png?raw=true)

Download the Windows version of the "Eclipse IDE for Java Developers". The file should be called `eclipse-java-20##-##-R-win32-x86_64.zip`

Once it downloads, extract the zip file and move the `eclipse` folder somewhere you want it. This is a standalone application. I put mine on my desktop.

Run the `eclipse.exe` file inside of the folder. Just leave the workspace as the default. Once it boots to the welcome page, you can close the app.

## Installing Minecraft
Visit the [Minecraft Download Page](https://my.minecraft.net/en-us/store/minecraft/) and sign in to your minecraft account.

You should see the Download Minecraft button if you have signed in and purchased the game.

Download and run `MinecraftInstaller.msi`. The default install settings are fine.

Once installed, run the launcher. Currently, Minecraft version 1.14 is the latest version (just released!), but we want version 1.13.2.

To set this up, click the three bars at the top right and switch over to the Launch options tab. Click the plus and add another launch profile, and set the game version to release 1.13.2.

Give it a name, and click Save. Switch back over to the News tab, and click the up arrow next to the Play button, and select the new launch profile you added.

Click the Play button, and the game will install and boot. You can close the game and launcher once you reach the title page.

## Installing Optifine (Optional)
Although this is an optional step, it is **highly recommended** because it will result in better client performance across all computers.

Visit the [Optifine Downloads Page](https://optifine.net/downloads).

![Download Optifine](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/cap04.png?raw=true)

Download the latest version of Optifine for Minecraft 1.13.2. Make sure you use the `(mirror)` button since it avoids the bit.ly ads. Your file should be called `OptiFine_1.13.2_HD_U_E#.jar`.

Chrome may ask you if you want to keep the file, if so, click "Keep".

Run the file, and click install.

## Setting up the Server
Nothing yet.

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
Here's a very simple example from the `GrapplingHook` plugin which cancels fall damage as long as a player is grappling.

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