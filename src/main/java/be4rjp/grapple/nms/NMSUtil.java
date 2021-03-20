package be4rjp.grapple.nms;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NMSUtil {
    public static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = "net.minecraft.server." + version + nmsClassString;
        Class<?> nmsClass = Class.forName(name);
        return nmsClass;
    }
    
    
    public static Object getConnection(Player player) throws SecurityException, NoSuchMethodException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        
        Method getHandle = player.getClass().getMethod("getHandle");
        Object nmsPlayer = getHandle.invoke(player);
        Field conField = nmsPlayer.getClass().getField("playerConnection");
        Object con = conField.get(nmsPlayer);
        return con;
    }
    
    
    public static Object getNMSPlayer(Player player) throws SecurityException, NoSuchMethodException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        
        Method getHandle = player.getClass().getMethod("getHandle");
        Object nmsPlayer = getHandle.invoke(player);
        return nmsPlayer;
    }
    
    
    public static Object getNMSWorld(World world) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        
        Method getHandle = world.getClass().getMethod("getHandle");
        Object nmsWorld = getHandle.invoke(world);
        return nmsWorld;
    }
    
    
    public static int getEntityID(Object entity)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> Entity = getNMSClass("Entity");
        Method getBukkitEntity = Entity.getMethod("getBukkitEntity");
        Object bukkitEntity = getBukkitEntity.invoke(entity);
        
        return ((org.bukkit.entity.Entity)bukkitEntity).getEntityId();
    }
    
    
    public static Object createEntitySilverfish(World world)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> EntitySilverfish = getNMSClass("EntitySilverfish");
        Class<?> NMSWorld = getNMSClass("World");
        Class<?> EntityTypes = getNMSClass("EntityTypes");
        
        Object SILVERFISH = EntityTypes.getField("SILVERFISH").get(null);
        
        Object entitySilverfish = EntitySilverfish.getConstructor(EntityTypes, NMSWorld).newInstance(SILVERFISH, getNMSWorld(world));
        
        Method setFlag = EntitySilverfish.getMethod("setFlag", int.class, boolean.class);
        setFlag.invoke(entitySilverfish, 5, true);
    
        Method setSilent = EntitySilverfish.getMethod("setSilent", boolean.class);
        setSilent.invoke(entitySilverfish, true);
        
        return entitySilverfish;
    }
    
    
    public static void setEntityPositionRotation(Object entity, double x, double y, double z, float yaw, float pitch)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> Entity = getNMSClass("Entity");
        Method setPositionRotation = Entity.getMethod("setPositionRotation", double.class, double.class, double.class, float.class, float.class);
        setPositionRotation.invoke(entity, x, y, z, yaw, pitch);
    }
    
    
    public static void sendEntityDestroyPacket(Player player, Object entity)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> packetClass = getNMSClass("PacketPlayOutEntityDestroy");
        Class<?> Entity = getNMSClass("Entity");
        
        Method getBukkitEntity = Entity.getMethod("getBukkitEntity");
        Object bukkitEntity = getBukkitEntity.invoke(entity);
        
        Constructor<?> packetConstructor = packetClass.getConstructor(int[].class);
        int[] ints = {((org.bukkit.entity.Entity)bukkitEntity).getEntityId()};
        Object packet = packetConstructor.newInstance(ints);
        Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
        sendPacket.invoke(getConnection(player), packet);
    }
    
    
    public static void sendSpawnEntityLivingPacket(Player player, Object entity)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> packetClass = getNMSClass("PacketPlayOutSpawnEntityLiving");
        Class<?> LivingEntity = getNMSClass("EntityLiving");
        Constructor<?> packetConstructor = packetClass.getConstructor(LivingEntity);
        Object packet = packetConstructor.newInstance(entity);
        Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
        sendPacket.invoke(getConnection(player), packet);
    }
    
    
    public static void sendEntityMetadataPacket(Player player, Object entity)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> packetClass = getNMSClass("PacketPlayOutEntityMetadata");
        Class<?> Entity = getNMSClass("Entity");
        Class<?> DataWatcher = getNMSClass("DataWatcher");
        
        Method getDataWatcher = Entity.getMethod("getDataWatcher");
        Object dataWatcher = getDataWatcher.invoke(entity);
        
        Method getBukkitEntity = Entity.getMethod("getBukkitEntity");
        Object bukkitEntity = getBukkitEntity.invoke(entity);
        
        Constructor<?> packetConstructor = packetClass.getConstructor(int.class, DataWatcher, boolean.class);
        Object packet = packetConstructor.newInstance(((org.bukkit.entity.Entity)bukkitEntity).getEntityId(), dataWatcher, true);
        Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
        sendPacket.invoke(getConnection(player), packet);
    }
    
    
    public static void sendSpawnEntityAttachPacket(Player player, Object attachedEntity, Object holdEntity)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        
        Class<?> packetClass = getNMSClass("PacketPlayOutAttachEntity");
        Class<?> Entity = getNMSClass("Entity");
        Constructor<?> packetConstructor = packetClass.getConstructor(Entity, Entity);
        Object packet = packetConstructor.newInstance(attachedEntity, holdEntity);
        Method sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
        sendPacket.invoke(getConnection(player), packet);
    }
}
