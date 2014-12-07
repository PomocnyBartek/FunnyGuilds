package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.system.protection.ProtectionSystem;

import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class ExplosionPrime implements Listener {
	
	@EventHandler
    public void onExplode(ExplosionPrimeEvent event) {
		if(event.getEntity() instanceof EnderCrystal){
			Location loc = ProtectionSystem.endercrystal((EnderCrystal) event.getEntity());
			if(loc != null) event.setCancelled(true);
		}
	}
}
