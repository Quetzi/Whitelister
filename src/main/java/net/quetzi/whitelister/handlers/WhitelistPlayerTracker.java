package net.quetzi.whitelister.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.quetzi.whitelister.Whitelister;
import cpw.mods.fml.common.IPlayerTracker;

/**
 * Created by Quetzi on 24/09/14.
 */
public class WhitelistPlayerTracker implements IPlayerTracker {

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		if(!Whitelister.isEnabled) return;
		//Yes, username, because there may be another mod that changes the displayname
        if (MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(player.username)) {
            Whitelister.log.info("Allowing exempt " + player.username);
            return;
        }

        if (!Whitelister.whitelist.contains(player.username.toLowerCase())) {
            Whitelister.log.info(player.username + " not on whitelist.");
            Whitelister.log.info("Blocking " + player.username);
            ((EntityPlayerMP) player).playerNetServerHandler.kickPlayerFromServer(Whitelister.kickMessage);
        } else {
            Whitelister.log.info("Allowing " + player.username);
        }
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}
}
