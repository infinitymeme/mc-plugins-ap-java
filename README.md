# mc-plugins-ap-java

# Useful Links
[Spigot API Documentation](https://hub.spigotmc.org/javadocs/spigot/overview-summary.html)

[Paper Server Downloads](https://papermc.io/downloads)

[Spigot API Downloads](https://getbukkit.org/download/spigot)

# Installation Instructions
Note that these instructions are for Windows devices only. Minecraft servers cannot be run on macOS.
## Downloading the Main Repository
Download this entire repository by clicking on the green "Clone or Download" button and "Download ZIP".
![Download the Repository](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/cap01.png?raw=true)
## Installing Java SE 11
Visit the [Java SE Downloads Page](https://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase11-5116896.html).
![Download Java SE 11](https://github.com/ferisril000/mc-plugins-ap-java/blob/images/cap02.png?raw=true)
Accept the License Agreement, and then download ```jdk-11.02_windows-x64_bin.exe```
You may need to make an Oracle account to download it. I just used my student email.
## Installing Eclipse
Nothing yet.
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
Here's a very simple example from the ```GrapplingHook``` plugin which cancels fall damage as long as a player is grappling.

```java
@EventHandler
public void onDamage(EntityDamageEvent e) {
  if ((e.getEntity() instanceof Player)&&(e.getCause().equals(DamageCause.FALL))&&(grappling.contains((Player)e.getEntity()))) {
    e.setCancelled(true);
  }
}
```

Notice that unlike a PlayerInteractEvent, this can trigger for any entity, so we check that ```e.getEntity() instanceof Player```, which avoids making extra checks, and must be done anyways to cast it to a Player type to check if ```grappling``` contains it. Every event is different, so make sure to read up on the docs page.

## Delay and Looping

Minecraft runs at 20 ticks per second, and waits for your code to execute before proceeding to the next tick. This creates a problem if you happen to create an infinite loop. Instead, if you wish to create any events that take course over a period of time, such as a constant homing effect, you must use a loop with a delay.

### Delay Function
Waits the specified amount of time, executes the code within, then proceeds to the next lines of code.
```java
Bukkit.getScheduler().runTaskLater(this, new Runnable() {public void run() {
  //your code here
}},DELAY_IN_TICKS);
```
Note, this does not run correctly within a typical for loop.
