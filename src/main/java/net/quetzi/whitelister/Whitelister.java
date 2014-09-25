package net.quetzi.whitelister;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.quetzi.whitelister.commands.CommandWhitelist;
import net.quetzi.whitelister.handlers.WhitelistPlayerTracker;
import net.quetzi.whitelister.util.Refs;
import net.quetzi.whitelister.util.WhitelistFetcher;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Quetzi on 24/09/14.
 */

@Mod(modid = Refs.MODID, name = Refs.NAME, version = Refs.VERSION + "-" + Refs.BUILD)
@NetworkMod (
		clientSideRequired = false,
		serverSideRequired = true
	)
public class Whitelister {

    public static Logger log = Logger.getLogger("Whitelister");
    public static Configuration config;
    public static boolean isEnabled;
    public static String[] urlList;
    public static int checkInterval;
    public static Set<String> whitelist = new HashSet<String>();
    public static String kickMessage;

    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void PreInit(FMLPreInitializationEvent event) {

        String[] defaultUrls = { "http://example.com/whitelist.txt", "http://example.com/whitelist2.txt" };
        log = event.getModLog();
        config = new Configuration(event.getSuggestedConfigurationFile());

        config.load();
        isEnabled = config.get(Refs.CFGGENERAL, "isEnabled", false, "Enable the whitelist").getBoolean(false);
        urlList = config.get(Refs.CFGGENERAL, "urlList", defaultUrls, "Comma separated url List").getStringList();
        checkInterval = config.get(Refs.CFGGENERAL, "checkInterval", 10,  "Time between checks in minutes").getInt(10);
        kickMessage = config.get(Refs.CFGGENERAL, "kickMessage", "You are not on the whitelist", "Kick message").getString();

        if(config.hasChanged()) config.save();
        if (isEnabled && urlList.length > 0) {
            new Thread(new WhitelistFetcher()).start();
        }
    }

    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void PostInit(FMLPostInitializationEvent event) {

    	GameRegistry.registerPlayerTracker(new WhitelistPlayerTracker());
    }

    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void serverLoad(FMLServerStartingEvent event) {

        event.registerServerCommand(new CommandWhitelist());
    }
}
