# mc-plugins-ap-java

# Useful Links
[Spigot API Documentation](https://hub.spigotmc.org/javadocs/spigot/overview-summary.html)

[Paper Server Downloads](https://papermc.io/downloads)

[Spigot API Downloads](https://getbukkit.org/download/spigot)


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

