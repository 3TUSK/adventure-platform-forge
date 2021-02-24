package info.tritusk.adventure.platform.forge.impl;

import info.tritusk.adventure.platform.forge.ForgeServerAudiences;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

@Mod("adventure-platform-forge")
@Mod.EventBusSubscriber(modid = "adventure-platform-forge")
public class ForgePlatform {

    public static ForgeServerAudiences serverAudienceProvider;

    @SubscribeEvent
    public static void onServerStart(FMLServerAboutToStartEvent event) {
        serverAudienceProvider = new ForgeServerAudiences(event.getServer());
    }

    @SubscribeEvent
    public static void onServerSTop(FMLServerStoppingEvent event) {
        serverAudienceProvider.close();
        serverAudienceProvider = null;
    }
}
