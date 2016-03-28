package net.quetzi.whitelister.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.quetzi.whitelister.Whitelister;
import net.quetzi.whitelister.util.Refs;
import net.quetzi.whitelister.util.WhitelistFetcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CommandWhitelist extends CommandBase {

    private List<String> aliases;

    public CommandWhitelist() {

        aliases = new ArrayList<String>();
    }

    @Override
    public String getCommandName() {

        return "whitelister";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {

        return "Syntax: /wl reload, /wl enable, /wl disable, /wl export, /wl list, /wl maintenance";
    }

    @Override
    public List<String> getCommandAliases() {

        aliases.add("qwl");
        aliases.add("wl");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                Whitelister.log.info(WhitelistFetcher.updateWhitelist() > 0 ? Refs.RELOAD_SUCCESS : Refs.RELOAD_FAILED);
            } else if (args[0].equalsIgnoreCase("enable")) {
                Whitelister.isEnabled = true;
                Whitelister.config.get("Settings", "WhitelistEnabled", false).set(true);
                Whitelister.config.save();
                commandSender.addChatMessage(new TextComponentString(Refs.ENABLED));
            } else if (args[0].equalsIgnoreCase("disable")) {
                Whitelister.isEnabled = false;
                Whitelister.config.get("Settings", "WhitelistEnabled", false).set(false);
                Whitelister.config.save();
                commandSender.addChatMessage(new TextComponentString(Refs.DISABLED));
            } else if (args[0].equalsIgnoreCase("export")) {
                WhitelistFetcher.writeWhitelist();
                WhitelistFetcher.writeJsonWhitelist();
                commandSender.addChatMessage(new TextComponentString("Remote whitelist saved."));
            } else if (args[0].equalsIgnoreCase("list")) {
                String list = "Users: ";
                Iterator<Set<String>> listIterator = Whitelister.whitelist.values().iterator();
                while (listIterator.hasNext()) {
                    Iterator<String> playerIterator = listIterator.next().iterator();
                    while (playerIterator.hasNext()) {
                        list = list + playerIterator.next();
                        if (playerIterator.hasNext() || listIterator.hasNext()) {
                            list = list + ", ";
                        }
                    }
                }
                commandSender.addChatMessage(new TextComponentString(list));
            } else if (args[0].equals("maintenance")) {
                FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().setWhiteListEnabled(!FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().isWhiteListEnabled());
                commandSender.addChatMessage(new TextComponentString("Maintenance whitelist is now " + (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().isWhiteListEnabled() ? "enabled" : "disabled")));
            }
        } else commandSender.addChatMessage(new TextComponentString(Refs.WHITELISTCMD_SYNTAX));

    }

    @Override
    public int getRequiredPermissionLevel() {

        return 4;
    }
}
