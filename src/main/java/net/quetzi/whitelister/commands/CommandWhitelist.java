package net.quetzi.whitelister.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.quetzi.whitelister.Whitelister;
import net.quetzi.whitelister.util.Refs;
import net.quetzi.whitelister.util.WhitelistFetcher;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CommandWhitelist extends CommandBase
{
    private List<String> aliases;

    public CommandWhitelist()
    {
        aliases = new ArrayList<>();
    }

    @Nonnull
    @Override
    public String getName()
    {
        return "whitelister";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender)
    {
        return "Syntax: /wl reload|enable|disable|export|list|maintenance";
    }

    @Nonnull
    @Override
    public List<String> getAliases()
    {
        aliases.add("qwl");
        aliases.add("wl");
        return aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
    {
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("reload"))
            {
                Whitelister.log.info(WhitelistFetcher.updateWhitelist() > 0 ? Refs.RELOAD_SUCCESS : Refs.RELOAD_FAILED);
            }
            else if (args[0].equalsIgnoreCase("enable"))
            {
                Whitelister.isEnabled = true;
                Whitelister.config.get("Settings", "WhitelistEnabled", false).set(true);
                Whitelister.config.save();
                sender.sendMessage(new TextComponentString(Refs.ENABLED));
            }
            else if (args[0].equalsIgnoreCase("disable"))
            {
                Whitelister.isEnabled = false;
                Whitelister.config.get("Settings", "WhitelistEnabled", false).set(false);
                Whitelister.config.save();
                sender.sendMessage(new TextComponentString(Refs.DISABLED));
            }
            else if (args[0].equalsIgnoreCase("export"))
            {
                WhitelistFetcher.writeWhitelist();
                WhitelistFetcher.writeJsonWhitelist();
                sender.sendMessage(new TextComponentString("Remote whitelist saved."));
            }
            else if (args[0].equalsIgnoreCase("list"))
            {
                String                list         = "Users: ";
                Iterator<Set<String>> listIterator = Whitelister.whitelist.values().iterator();
                while (listIterator.hasNext())
                {
                    Iterator<String> playerIterator = listIterator.next().iterator();
                    while (playerIterator.hasNext())
                    {
                        list = list + playerIterator.next();
                        if (playerIterator.hasNext() || listIterator.hasNext())
                        {
                            list = list + ", ";
                        }
                    }
                }
                sender.sendMessage(new TextComponentString(list));
            }
            else if (args[0].equals("maintenance"))
            {
                FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().setWhiteListEnabled(!FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().isWhiteListEnabled());
                sender.sendMessage(new TextComponentString("Maintenance whitelist is now " + (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().isWhiteListEnabled() ? "enabled" : "disabled")));
            }
        }
        else
        {
            sender.sendMessage(new TextComponentString(Refs.WHITELISTCMD_SYNTAX));
        }
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 0)
        {
            return getListOfStringsMatchingLastWord(args, "reload", "enable", "disable", "export", "list", "maintenance");
        }
        return new ArrayList<>();
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 4;
    }
}
