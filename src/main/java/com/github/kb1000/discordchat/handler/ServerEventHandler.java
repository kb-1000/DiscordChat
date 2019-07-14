package com.github.kb1000.discordchat.handler;

import com.github.kb1000.discordchat.discord.API;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;

@SideOnly(Side.SERVER)
public class ServerEventHandler {
    @ForgeSubscribe
    public void chat(ServerChatEvent e) {
        if (!API.message(e.player.username, e.message)) {
            if (e.isCancelable()) {
                e.setCanceled(true);
            }
        }
    }
}
