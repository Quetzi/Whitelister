package net.quetzi.whitelister;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.quetzi.whitelister.commands.CommandWhitelist;
import net.quetzi.whitelister.handlers.WhitelistEventHandler;
import net.quetzi.whitelister.util.Refs;
import net.quetzi.whitelister.util.WhitelistFetcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;

/**
 * Created by Quetzi on 24/09/14.
 */

@Mod(modid = Refs.MODID, name = Refs.NAME, version = Refs.VERSION, dependencies = "required-after:Forge@[11.14.0.1239,);", acceptableRemoteVersions = "*")
public class Whitelister {

    public static Logger log = LogManager.getLogger("Whitelister");
    public static Configuration config;
    public static boolean isEnabled;
    public static String[] urlList;
    public static int checkInterval;
    public static HashMap<String, Set<String>> whitelist = new HashMap<String, Set<String>>();
    public static String kickMessage;

    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void PreInit(FMLPreInitializationEvent event) {

        String[] defaultUrls = { "http://example.com/whitelist.txt", "http://example.com/whitelist2.txt" };
        log = event.getModLog();
        config = new Configuration(event.getSuggestedConfigurationFile());

        config.load();
        isEnabled = config.getBoolean("isEnabled", Refs.CFGGENERAL, false, "Enable the whitelist");
        urlList = config.getStringList("urlList", Refs.CFGGENERAL, defaultUrls, "Comma separated url List");
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
