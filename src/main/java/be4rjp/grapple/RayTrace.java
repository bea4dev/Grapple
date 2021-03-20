package be4rjp.grapple;

import org.bukkit.util.Vector;
import java.util.ArrayList;

public class RayTrace {
    
    public Vector origin, direction;
    
    public RayTrace(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }
    
    public Vector getPosition(double blocksAway) {
        return origin.clone().add(direction.clone().multiply(blocksAway));
    }
    
    public ArrayList<Vector> traverse(double blocksAway, double accuracy) {
        ArrayList<Vector> positions = new ArrayList<>();
        for (double d = 0; d <= blocksAway; d += accuracy) {
            positions.add(getPosition(d));
        }
        return positions;
    }
}
