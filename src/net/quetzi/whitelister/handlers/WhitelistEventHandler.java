package net.quetzi.whitelister.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.quetzi.whitelister.Whitelister;

/**
 * Created by Quetzi on 24/09/14.
 */
public class WhitelistEventHandler {

    @SubscribeEvent
    public void PlayerLoggedInHandler(PlayerEvent.PlayerLoggedInEvent event) {

        if(!Whitelister.isEnabled) return;
    }
}
