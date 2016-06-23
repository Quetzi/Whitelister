package net.quetzi.whitelister.handlers;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.quetzi.whitelister.Whitelister;

import java.util.Set;

/**
 * Created by Quetzi on 24/09/14.
 */
public class WhitelistEventHandler
{
    @SubscribeEvent
    public void PlayerLoggedInHandler(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (!Whitelister.isEnabled) return;
        for (String player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOppedPlayerNames())
        {
            if (player.toLowerCase().equals(event.player.getName().toLowerCase()))
            {
                Whitelister.log.info("Allowing exempt " + event.player.getGameProfile().getName());
                return;
            }
        }

        if (!isWhitelisted(event.player.getGameProfile().getName().toLowerCase()))
        {
            Whitelister.log.info(event.player.getGameProfile().getName() + " not on whitelist.");
            Whitelister.log.info("Blocking " + event.player.getGameProfile().getName());
            ((EntityPlayerMP) event.player).connection.kickPlayerFromServer(Whitelister.kickMessage);
        }
        else
        {
            Whitelister.log.info("Allowing " + event.player.getGameProfile().getName());
        }
        if (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().isWhiteListEnabled())
        {
            event.player.addChatMessage(new TextComponentString("WHITELIST IS CURRENTLY IN MAINTENANCE MODE"));
        }
    }

    private boolean isWhitelisted(String username)
    {
        for (Set<String> list : Whitelister.whitelist.values())
        {
            if (list.contains(username))
            {
                return true;
            }
        }
        return false;
    }
}
