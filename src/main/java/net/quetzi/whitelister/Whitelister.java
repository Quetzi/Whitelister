package net.quetzi.whitelister;

import net.minecraftforge.common.config.Configuration;
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

/**
 * Created by Quetzi on 24/09/14.
 */

@Mod(modid = Refs.MODID,
        name = Refs.NAME,
        version = Refs.VERSION,
        dependencies = "required-after:forge@[13.19.0.2130,);",
        acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.11,1.12)"
)
public class Whitelister
{
    public static Logger log = LogManager.getLogger("Whitelister");
    public static Configuration config;
    public static boolean       isEnabled;
    public static boolean       headcrumbsCompat;
    public static String[]      urlList;
    public static String[]      jsonList;
    public static int           checkInterval;
    public static HashMap<String, Set<String>> whitelist = new HashMap<String, Set<String>>();
    public static String kickMessage;
    public static String[] defaultUrls     = {"http://example.com/whitelist.txt", "http://example.com/whitelist2.txt"};
    public static String[] defaultJsonUrls = {"http://example.com/whitelist.json", "http://example.com/whitelist2.json"};

    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void PreInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();
        config = new Configuration(event.getSuggestedConfigurationFile());

        config.load();
        isEnabled = config.getBoolean("isEnabled", Refs.CFGGENERAL, false, "Enable the whitelist");
        urlList = config.getStringList("urlList", Refs.CFGGENERAL, defaultUrls, "Comma separated url List of plain text files");
        jsonList = config.getStringList("jsonList", Refs.CFGGENERAL, defaultJsonUrls, "Comma separated url List of json files");
        checkInterval = config.getInt("checkInterval", Refs.CFGGENERAL, 10, 1, 32000, "Time between checks in minutes");
        kickMessage = config.getString("kickMessage", Refs.CFGGENERAL, "You are not on the whitelist", "Kick message");
        headcrumbsCompat = config.getBoolean("headcrumbsCompat", Refs.CFGGENERAL, false, "Add all whitelisted players to Headcrumbs player list (Feature currently DISABLED)");

        if (config.hasChanged()) config.save();
        if (isEnabled && urlList.length > 0)
        {
            new Thread(new WhitelistFetcher()).start();
        }
    }

    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void PostInit(FMLPostInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(new WhitelistEventHandler());
    }

    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void serverLoad(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandWhitelist());
    }
}
