package net.quetzi.whitelister.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;
import net.quetzi.whitelister.Whitelister;
import net.quetzi.whitelister.util.Refs;
import net.quetzi.whitelister.util.WhitelistFetcher;

public class CommandWhitelist implements ICommand {

    private List<String> aliases;

    public CommandWhitelist() {

        aliases = new ArrayList<String>();
    }

    @Override
    public int compareTo(Object arg0) {

        return 0;
    }

    @Override
    public String getCommandName() {

        return "wl";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {

        return "Syntax: /wl reload, /wl enable, /wl disable, /wl export, /wl list";
    }

    @Override
    public List getCommandAliases() {

        return null;
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                Whitelister.log.info(WhitelistFetcher.updateWhitelist() ? Refs.RELOAD_SUCCESS : Refs.RELOAD_FAILED);
            } else if (args[0].equalsIgnoreCase("enable")) {
                Whitelister.isEnabled = true;
                Whitelister.config.get("Settings", "WhitelistEnabled", false).set(true);
                Whitelister.config.save();
                commandSender.sendChatToPlayer(ChatMessageComponent.createFromText(Refs.ENABLED));
            } else if (args[0].equalsIgnoreCase("disable")) {
                Whitelister.isEnabled = false;
                Whitelister.config.get("Settings", "WhitelistEnabled", false).set(false);
                Whitelister.config.save();
                commandSender.sendChatToPlayer(ChatMessageComponent.createFromText(Refs.DISABLED));
            } else if (args[0].equalsIgnoreCase("export")) {
                WhitelistFetcher.writeWhitelist();
                commandSender.sendChatToPlayer(ChatMessageComponent.createFromText("Remote whitelist written to whitelist-export.txt."));
            } else if (args[0].equalsIgnoreCase("list")) {
                String list = "Users: ";
                Iterator<String> ite = Whitelister.whitelist.iterator();
                while (ite.hasNext()) {
                    list = list + ite.next() + (ite.hasNext() ? ", " : "");
                }
                commandSender.sendChatToPlayer(ChatMessageComponent.createFromText(list));
            }
        } else commandSender.sendChatToPlayer(ChatMessageComponent.createFromText(Refs.WHITELISTCMD_SYNTAX));

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender var1) {

        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender var1, String[] var2) {

        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] var1, int var2) {

        return false;
    }

    public int getRequiredPermissionLevel() {

        return 3;
    }
}
