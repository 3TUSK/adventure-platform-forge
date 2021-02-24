package info.tritusk.adventure.platform.forge.impl.audience;

import info.tritusk.adventure.platform.forge.impl.AdventureBookInfo;
import info.tritusk.adventure.platform.forge.impl.ComponentWrapper;
import info.tritusk.adventure.platform.forge.impl.KeyMapper;
import info.tritusk.adventure.platform.forge.impl.SoundMapper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

public class ClientAudience implements Audience {

    private final Function<BossBar, ? extends BossInfo> bossBarMapper;

    public ClientAudience(Function<BossBar, ? extends BossInfo> bossBarMapper) {
        this.bossBarMapper = bossBarMapper;
    }

    @Override
    public void sendMessage(final @NonNull Identity source, final @NonNull Component message, final @NonNull MessageType type) {
        final ClientPlayerEntity p = Minecraft.getInstance().player;
        if (p != null) {
            p.sendMessage(new TranslationTextComponent("chat.type.text", source.examinableName(), message), source.uuid());
        }
    }

    @Override
    public void sendActionBar(@NonNull Component message) {
        Minecraft.getInstance().ingameGUI.setOverlayMessage(new ComponentWrapper(message), false);
    }

    @Override
    public void sendPlayerListHeader(@NonNull Component header) {
        Minecraft.getInstance().ingameGUI.getTabList().setHeader(new ComponentWrapper(header));
    }

    @Override
    public void sendPlayerListFooter(@NonNull Component footer) {
        Minecraft.getInstance().ingameGUI.getTabList().setFooter(new ComponentWrapper(footer));
    }

    @Override
    public void sendPlayerListHeaderAndFooter(@NonNull Component header, @NonNull Component footer) {
        this.sendPlayerListHeader(header);
        this.sendPlayerListFooter(footer);
    }

    @Override
    public void showTitle(@NonNull Title title) {
        Title.Times titleTimes = title.times();
        if (titleTimes == null) {
            // How are you suppose to handle this case anyway?
            this.clearTitle();
        } else {
            Minecraft.getInstance().ingameGUI.func_238452_a_(new ComponentWrapper(title.title()),
                    new ComponentWrapper(title.subtitle()),
                    (int)(titleTimes.fadeIn().toMillis() / Ticks.SINGLE_TICK_DURATION_MS),
                    (int)(titleTimes.stay().toMillis()/ Ticks.SINGLE_TICK_DURATION_MS),
                    (int)(titleTimes.fadeOut().toMillis() / Ticks.SINGLE_TICK_DURATION_MS));
        }
    }

    @Override
    public void clearTitle() {
        Minecraft.getInstance().ingameGUI.func_238452_a_(null, null, 0, 0, 0);
    }

    @Override
    public void resetTitle() {
        Minecraft.getInstance().ingameGUI.setDefaultTitlesTimes();
    }

    @Override
    public void showBossBar(@NonNull BossBar bar) {
        final BossInfo nativeBossBar = this.bossBarMapper.apply(bar);
        final SUpdateBossInfoPacket fakePacket = new SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation.ADD, nativeBossBar);
        Minecraft.getInstance().ingameGUI.getBossOverlay().read(fakePacket); // Trick Minecraft to add a new boss bar wtf
    }

    @Override
    public void hideBossBar(@NonNull BossBar bar) {
        final BossInfo nativeBossBar = this.bossBarMapper.apply(bar);
        final SUpdateBossInfoPacket fakePacket = new SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation.REMOVE, nativeBossBar);
        Minecraft.getInstance().ingameGUI.getBossOverlay().read(fakePacket); // Trick Minecraft to remove a new boss bar wtf
    }

    @Override
    public void playSound(@NonNull Sound sound) {
        final ClientPlayerEntity p = Minecraft.getInstance().player;
        if (p == null) {
            Minecraft.getInstance().getSoundHandler().play(SoundMapper.toNative(sound));
        } else {
            this.playSound(sound, p.getPosX(), p.getPosY(), p.getPosZ());
        }
    }

    @Override
    public void playSound(@NonNull Sound sound, double x, double y, double z) {
        Minecraft.getInstance().getSoundHandler().play(SoundMapper.toNative(sound, x, y, z, true));
    }

    @Override
    public void stopSound(@NonNull SoundStop stop) {
        Minecraft.getInstance().getSoundHandler().stop(KeyMapper.toNative(stop.sound()), SoundMapper.toNative(stop.source()));
    }

    @Override
    public void openBook(@NonNull Book book) {
        Minecraft.getInstance().displayGuiScreen(new ReadBookScreen(new AdventureBookInfo(book)));
    }
}
