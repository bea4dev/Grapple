package be4rjp.grapple;

import be4rjp.grapple.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {
    
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event){
        
        if(event.getEntity().getType() != EntityType.PLAYER) return;
        
        Player player = (Player)event.getEntity();
        
        if(player.getInventory().getItemInMainHand() == null) return;
        
        if(player.getInventory().getItemInMainHand().getType() != Material.CROSSBOW) return;
        
        if(!player.getInventory().getItemInMainHand().hasItemMeta()) return;
        
        if(!player.getInventory().getItemInMainHand().getItemMeta().hasCustomModelData()) return;
        
        if(player.getInventory().getItemInMainHand().getItemMeta().getCustomModelData() == 1) {
            GrappleFirst grappleEffect = new GrappleFirst(player);
            grappleEffect.runTaskTimer(Grapple.getPlugin(), 0, 1);
            event.setCancelled(true);
        }
    }
    
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        PlayerData playerData = new PlayerData(player);
        Grapple.getPlugin().getDataStore().setPlayerData(player, playerData);
    }
}
