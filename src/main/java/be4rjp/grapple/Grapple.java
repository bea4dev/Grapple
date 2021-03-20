package be4rjp.grapple;

import be4rjp.grapple.data.DataStore;
import be4rjp.grapple.data.PlayerData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Grapple extends JavaPlugin {
    
    private static Grapple plugin;
    
    private DataStore dataStore;
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
    
        getLogger().info("Registering listeners...");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EventListener(), this);
    
    
        //Create DataStore instance.
        dataStore = new DataStore();
    
        //For restart
        getServer().getOnlinePlayers().stream().forEach(player -> {
            PlayerData playerData = new PlayerData(player);
            getDataStore().setPlayerData(player, playerData);
        });
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
    
    public DataStore getDataStore() {return dataStore;}
    
    
    public static Grapple getPlugin(){
        return plugin;
    }
}
