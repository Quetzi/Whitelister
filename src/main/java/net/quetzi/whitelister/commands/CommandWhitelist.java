package net.quetzi.whitelister.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
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
    public int compareTo(Object arg0) {

        return 0;
    }

    @Override
    public String getName() {

        return "whitelister";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {

        return "Syntax: /wl reload, /wl enable, /wl disable, /wl export, /wl list, /wl maintenance";
    }

    @Override
    public List getAliases() {

        aliases.add("qwl");
        aliases.add("wl");
        return aliases;
    }

    @Override
    public void execute(ICommandSender commandSender, String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                Whitelister.log.info(WhitelistFetcher.updateWhitelist() > 0 ? Refs.RELOAD_SUCCESS : Refs.RELOAD_FAILED);
            } else if (args[0].equalsIgnoreCase("enable")) {
                Whitelister.isEnabled = true;
                Whitelister.config.get("Settings", "WhitelistEnabled", false).set(true);
                Whitelister.config.save();
                commandSender.addChatMessage(new ChatComponentText(Refs.ENABLED));
            } else if (args[0].equalsIgnoreCase("disable")) {
                Whitelister.isEnabled = false;
                Whitelister.config.get("Settings", "WhitelistEnabled", false).set(false);
                Whitelister.config.save();
                commandSender.addChatMessage(new ChatComponentText(Refs.DISABLED));
            } else if (args[0].equalsIgnoreCase("export")) {
                if (WhitelistFetcher.writeWhitelist()) {
                    commandSender.addChatMessage(new ChatComponentText("Remote whitelist written to whitelist-export.txt."));
                } else {
                    commandSender.addChatMessage(new ChatComponentText("Whitelist export failed."));
                }
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
                commandSender.addChatMessage(new ChatComponentText(list));
            } else if (args[0].equals("maintenance")) {
                MinecraftServer.getServer().getConfigurationManager().setWhiteListEnabled(!MinecraftServer.getServer().getConfigurationManager().isWhiteListEnabled());
                commandSender.addChatMessage(new ChatComponentText("Maintenance whitelist is now " + (MinecraftServer.getServer().getConfigurationManager().isWhiteListEnabled() ? "enabled" : "disabled")));
            }
        } else commandSender.addChatMessage(new ChatComponentText(Refs.WHITELISTCMD_SYNTAX));

    }

    public int getRequiredPermissionLevel() {

        return 3;
    }
}
