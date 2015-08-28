package net.quetzi.whitelister;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.config.Configuration;
import net.quetzi.whitelister.commands.CommandWhitelist;
import net.quetzi.whitelister.handlers.WhitelistEventHandler;
import net.quetzi.whitelister.util.Refs;
import net.quetzi.whitelister.util.WhitelistFetcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Quetzi on 24/09/14.
 */

@Mod(modid = Refs.MODID, name = Refs.NAME, version = Refs.VERSION + "-" + Refs.BUILD, acceptableRemoteVersions = "*")
public class Whitelister {

    public static Logger log = LogManager.getLogger("Whitelister");
    public static Configuration config;
    public static boolean isEnabled;
    public static String[] urlList;
    public static String[] jsonList;
    public static int checkInterval;
    public static HashMap<String, Set<String>> whitelist = new HashMap<String, Set<String>>();
    public static String kickMessage;
    public static String[] defaultUrls = { "http://example.com/whitelist.txt", "http://example.com/whitelist2.txt" };
    public static String[] defaultJsonUrls = { "http://example.com/whitelist.json", "http://example.com/whitelist2.json" };

    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void PreInit(FMLPreInitializationEvent event) {

        log = event.getModLog();
        config = new Configuration(event.getSuggestedConfigurationFile());

        config.load();
        isEnabled = config.getBoolean("isEnabled", Refs.CFGGENERAL, false, "Enable the whitelist");
        urlList = config.getStringList("urlList", Refs.CFGGENERAL, defaultUrls, "Comma separated url List of plain text files");
        jsonList = config.getStringList("jsonList", Refs.CFGGENERAL, defaultJsonUrls, "Comma separated url List of json files");
        checkInterval = config.getInt("checkInterval", Refs.CFGGENERAL, 10, 1, 32000, "Time between checks in minutes");
        kickMessage = config.getString("kickMessage", Refs.CFGGENERAL, "You are not on the whitelist", "Kick message");

        if(config.hasChanged()) config.save();
        if (isEnabled && urlList.length > 0) {
            new Thread(new WhitelistFetcher()).start();
        }
    }

    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void PostInit(FMLPostInitializationEvent event) {

        FMLCommonHandler.instance().bus().register(new WhitelistEventHandler());
    }

    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void serverLoad(FMLServerStartingEvent event) {

        event.registerServerCommand(new CommandWhitelist());
    }
}
