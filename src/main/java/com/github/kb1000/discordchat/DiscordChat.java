package com.github.kb1000.discordchat;

import com.github.kb1000.discordchat.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

@Mod(modid="discordchat", name="DiscordChat", version="1.0")
public final class DiscordChat {
    @SidedProxy(modId="discordchat", clientSide="com.github.kb1000.discordchat.proxy.CommonProxy", serverSide="com.github.kb1000.discordchat.proxy.ServerProxy")
    public static CommonProxy proxy;
    @Mod.Init
    public void init(FMLInitializationEvent e) {
        proxy.start();
    }

    @Mod.ServerStopping
    public void serverStopping(FMLServerStoppingEvent e) {
        proxy.stop();
    }
}
