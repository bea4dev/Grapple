package be4rjp.grapple;
import be4rjp.grapple.nms.NMSUtil;
import org.bukkit.*;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class GrappleFirst extends BukkitRunnable {
    
    private final Player player;
    private final Vector direction;
    
    private int tick = 0;
    
    private RayTrace rayTrace;
    private ArrayList<Vector> positions;
    
    public GrappleFirst(Player player){
        this.player = player;
        this.direction = player.getEyeLocation().getDirection();
        
        this.rayTrace = new RayTrace(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
        this.positions = rayTrace.traverse(40.0, 3);
        
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1F, 1F);
    
        Grapple.getPlugin().getDataStore().getPlayerData(player).setGrappling(true);
    }
    
    @Override
    public void run(){
        
        if(!player.isOnline()){
            stop();
            return;
        }
        
        if(tick < positions.size()){
            Location location = positions.get(tick).toLocation(player.getWorld());
            location.getWorld().spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
            
            
            RayTrace rayTrace2 = new RayTrace(location.toVector(), direction);
            ArrayList<Vector> positions2 = rayTrace2.traverse(3.0, 0.5);
            
            for(int i = 0; i < positions2.size(); i++) {
                
                Location position = positions2.get(i).toLocation(player.getWorld());
                
                if(!position.getBlock().getType().toString().endsWith("AIR")){
                    position.getWorld().playSound(position, Sound.ITEM_CROSSBOW_HIT, 1F, 1F);
    
                    try {
                        Object silverfish = NMSUtil.createEntitySilverfish(player.getWorld());
                        NMSUtil.setEntityPositionRotation(silverfish, position.getX(), position.getY() - 1.0, position.getZ(), 0, 0);
                        
                        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                            if (p.getWorld() == player.getWorld()) {
                                NMSUtil.sendSpawnEntityLivingPacket(p, silverfish);
                                NMSUtil.sendEntityMetadataPacket(p, silverfish);
                                NMSUtil.sendSpawnEntityAttachPacket(p, silverfish, NMSUtil.getNMSPlayer(player));
                            }
                        }
    
                        GrappleSecond grappleSecond = new GrappleSecond(player, position, silverfish);
                        grappleSecond.runTaskTimer(Grapple.getPlugin(), 0, 1);
                    }catch (Exception e){e.printStackTrace();}
                    
                    stop();
                    return;
                }
            }
        }else{
            stop();
        }
    
        tick++;
    }
    
    public void stop(){
        this.cancel();
        Grapple.getPlugin().getDataStore().getPlayerData(player).setGrappling(false);
    }
}
