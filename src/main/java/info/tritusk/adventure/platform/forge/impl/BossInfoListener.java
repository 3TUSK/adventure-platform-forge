package info.tritusk.adventure.platform.forge.impl;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.minecraft.world.BossInfo;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;
import java.util.function.Function;

public class BossInfoListener implements BossBar.Listener {

    private final Function<BossBar, ? extends BossInfo> mapper;

    public BossInfoListener(Function<BossBar, ? extends BossInfo> mapper) {
        this.mapper = mapper;
    }

    @Override
    public void bossBarNameChanged(@NonNull BossBar bar, @NonNull Component oldName, @NonNull Component newName) {
        if (oldName != newName) {
            this.mapper.apply(bar).setName(TextComponentMapper.toNative(newName));
        }
    }

    @Override
    public void bossBarProgressChanged(@NonNull BossBar bar, float oldProgress, float newProgress) {
        if (oldProgress != newProgress) {
            this.mapper.apply(bar).setPercent(newProgress);
        }
    }

    @Override
    public void bossBarColorChanged(@NonNull BossBar bar, BossBar.@NonNull Color oldColor, BossBar.@NonNull Color newColor) {
        if (oldColor != newColor) {
            this.mapper.apply(bar).setColor(BossBarMapper.toNative(newColor));
        }
    }

    @Override
    public void bossBarOverlayChanged(@NonNull BossBar bar, BossBar.@NonNull Overlay oldOverlay, BossBar.@NonNull Overlay newOverlay) {
        if (oldOverlay != newOverlay) {
            this.mapper.apply(bar).setOverlay(BossBarMapper.toNative(newOverlay));
        }
    }

    @Override
    public void bossBarFlagsChanged(@NonNull BossBar bar, @NonNull Set<BossBar.Flag> flagsAdded, @NonNull Set<BossBar.Flag> flagsRemoved) {
        final BossInfo bossInfo = this.mapper.apply(bar);
        if (flagsAdded.contains(BossBar.Flag.CREATE_WORLD_FOG)) {
            bossInfo.setCreateFog(true);
        }
        if (flagsAdded.contains(BossBar.Flag.DARKEN_SCREEN)) {
            bossInfo.setDarkenSky(true);
        }
        if (flagsAdded.contains(BossBar.Flag.PLAY_BOSS_MUSIC)) {
            bossInfo.setPlayEndBossMusic(true);
        }
        if (flagsRemoved.contains(BossBar.Flag.CREATE_WORLD_FOG)) {
            bossInfo.setCreateFog(false);
        }
        if (flagsRemoved.contains(BossBar.Flag.DARKEN_SCREEN)) {
            bossInfo.setDarkenSky(false);
        }
        if (flagsRemoved.contains(BossBar.Flag.PLAY_BOSS_MUSIC)) {
            bossInfo.setPlayEndBossMusic(false);
        }
    }

}
