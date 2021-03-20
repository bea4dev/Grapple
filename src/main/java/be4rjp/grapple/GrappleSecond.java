package be4rjp.grapple;
import be4rjp.grapple.nms.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class GrappleSecond extends BukkitRunnable {
    
    private final Player player;
    private final Location to;
    private final Object silverfish;
    
    private int i = 1;
    
    public GrappleSecond(Player player, Location to, Object silverfish){
        this.player = player;
        this.to = to;
        this.silverfish = silverfish;
        
        player.setFallDistance(0);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 5F, 2F);
    }
    
    @Override
    public void run() {
        if(!player.isOnline()) stop();
        
        if(player.isSneaking()) stop();
        
        Location playerLoc = player.getEyeLocation();
        
        double distance = playerLoc.distance(to);
        
        if(distance < 3.0 || distance > 60.0) stop();
        
        Vector toVector = new Vector(to.getX() - playerLoc.getX(), to.getY() - playerLoc.getY(), to.getZ() - playerLoc.getZ()).normalize().multiply(0.3);
        
        RayTrace rayTrace = new RayTrace(playerLoc.toVector(), toVector);
        ArrayList<Vector> positions = rayTrace.traverse(playerLoc.distance(to), 1);
        for(int i = 0; i < positions.size(); i++) {
            Location position = positions.get(i).toLocation(player.getWorld());
            if(!position.getBlock().getType().toString().endsWith("AIR")){
                stop();
                return;
            }
        }
        
        Vector direction = player.getEyeLocation().getDirection().multiply(1.3);
        
        if(toVector.angle(direction) > Math.toRadians(90)) stop();
        
        double speedRate = (0.4 * ((double)i / 20.0));
        Vector flyVector = player.getVelocity().multiply(1.3).add(toVector).add(direction).multiply(speedRate > 1.2 ? 1.2 : speedRate);
        
        if(flyVector.lengthSquared() > 30.0) stop();
        
        player.setVelocity(flyVector);
        player.getWorld().playSound(playerLoc, Sound.BLOCK_NOTE_BLOCK_HAT, 0.8F, 1F);
        
        i++;
    }
    
    
    public void stop(){
        this.cancel();
        player.setFallDistance(0);
        Grapple.getPlugin().getDataStore().getPlayerData(player).setGrappling(false);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 5F, 2F);
        try{
            for(Player p : Bukkit.getServer().getOnlinePlayers()){
                if(p.getWorld() == player.getWorld()) {
                    NMSUtil.sendEntityDestroyPacket(p, silverfish);
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }
}