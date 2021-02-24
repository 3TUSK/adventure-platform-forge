package info.tritusk.adventure.platform.forge.impl;

import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.world.BossInfo;
import net.minecraft.world.server.ServerBossInfo;

public class BossBarMapper {

    public static ServerBossInfo toNative(BossBar bar) {
        final ServerBossInfo nativeBossBar = new ServerBossInfo(TextComponentMapper.toNative(bar.name()), BossBarMapper.toNative(bar.color()), BossBarMapper.toNative(bar.overlay()));
        nativeBossBar.setPercent(bar.progress());
        nativeBossBar.setCreateFog(bar.hasFlag(BossBar.Flag.CREATE_WORLD_FOG))
                .setDarkenSky(bar.hasFlag(BossBar.Flag.DARKEN_SCREEN))
                .setPlayEndBossMusic(bar.hasFlag(BossBar.Flag.PLAY_BOSS_MUSIC));
        return nativeBossBar;
    }

    public static BossInfo.Color toNative(BossBar.Color color) {
        if (color == null) {
            return null;
        }
        switch (color) {
            case RED: return BossInfo.Color.RED;
            case BLUE: return BossInfo.Color.BLUE;
            case PINK: return BossInfo.Color.PINK;
            case GREEN: return BossInfo.Color.GREEN;
            case WHITE: return BossInfo.Color.WHITE;
            case PURPLE: return BossInfo.Color.PURPLE;
            case YELLOW: return BossInfo.Color.YELLOW;
            default: return null;
        }
    }

    public static BossInfo.Overlay toNative(BossBar.Overlay original) {
        if (original == null) {
            return null;
        }
        switch (original) {
            case PROGRESS: return BossInfo.Overlay.PROGRESS;
            case NOTCHED_6: return BossInfo.Overlay.NOTCHED_6;
            case NOTCHED_10: return BossInfo.Overlay.NOTCHED_10;
            case NOTCHED_12: return BossInfo.Overlay.NOTCHED_12;
            case NOTCHED_20: return BossInfo.Overlay.NOTCHED_20;
            default: return null;
        }
    }
}
