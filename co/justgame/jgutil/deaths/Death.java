package co.justgame.jgutil.deaths;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import co.justgame.jgutil.main.JGutils;

abstract class Death implements Runnable{
    
    final Player p;
    boolean playerDied;
    Listener l;
    
    public Death(Player p){
        this.p = p;
    }

    @Override
    public void run(){
        this.emptyPlayerInven();
        this.startDeathClock();
        
        l = new Listener(){
            @EventHandler(priority = EventPriority.NORMAL)
            public void deathEvent(final PlayerDeathEvent e){
                if(e.getEntity().equals(p)){
                    playerDied = true;
                    HandlerList.unregisterAll(l);
                }
            }
            @EventHandler(priority = EventPriority.NORMAL)
            public void itemEvent(final PlayerPickupItemEvent e){
                if(e.getPlayer().equals(p)) e.setCancelled(true);
            }
        };
        JGutils.getInstance().getServer().getPluginManager().registerEvents(l, JGutils.getInstance());
    }
    
    protected void emptyPlayerInven(){
        JGutils.getInstance().getServer().getPluginManager().callEvent(new PlayerDeathEvent(p, 
               new ArrayList<ItemStack>(Arrays.asList(p.getInventory().getContents())), p.getExpToLevel(), null));
        
        for(ItemStack it: p.getInventory().getContents()){
            if(it != null) p.getLocation().getWorld().dropItemNaturally(p.getLocation(), it);
        }
        for(ItemStack it: p.getInventory().getArmorContents()){
            if(it.getType() != Material.AIR) p.getLocation().getWorld().dropItemNaturally(p.getLocation(), it);
        }
            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[4]);
    }
    
    protected void startDeathClock(){
        Bukkit.getScheduler().scheduleSyncDelayedTask(JGutils.getInstance(), new Runnable(){
            @Override
            public void run(){
                if(!playerDied) p.setHealth(0.0);
            }
        }, 100);
    }
}
