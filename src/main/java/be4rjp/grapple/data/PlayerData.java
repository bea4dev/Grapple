package be4rjp.grapple.data;

import org.bukkit.entity.Player;

public class PlayerData {
    
    private final String uuid;
    
    private boolean isGrappling;
    
    public PlayerData(String uuid){
        this.uuid = uuid;
    }
    
    public PlayerData(Player player){
        this.uuid = player.getUniqueId().toString();
    }
    
    public boolean isGrappling() {
        return isGrappling;
    }
    
    public void setGrappling(boolean grappling) {
        isGrappling = grappling;
    }
}
