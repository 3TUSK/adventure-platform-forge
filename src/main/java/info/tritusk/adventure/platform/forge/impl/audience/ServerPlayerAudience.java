package info.tritusk.adventure.platform.forge.impl.audience;

import info.tritusk.adventure.platform.forge.impl.ComponentWrapper;
import info.tritusk.adventure.platform.forge.impl.KeyMapper;
import info.tritusk.adventure.platform.forge.impl.SoundMapper;
import info.tritusk.adventure.platform.forge.impl.TextComponentMapper;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.title.Title;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SPlayerListHeaderFooterPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SStopSoundPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerBossInfo;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.function.Function;

/**
 * An {@link Audience} that represents a particular online player.
 */
public class ServerPlayerAudience implements Audience {

    private final WeakReference<ServerPlayerEntity> player;
    private final Function<BossBar, ServerBossInfo> bossBarMapper;

    public ServerPlayerAudience(ServerPlayerEntity p, Function<BossBar, ServerBossInfo> bossBarMapper) {
        this.player = new WeakReference<>(p);
        this.bossBarMapper = bossBarMapper;
    }

    @Override
    public void sendMessage(@NonNull Identity source, @NonNull Component message, @NonNull MessageType type) {
        final ITextComponent realMsg = new ComponentWrapper(message);
        final ChatType chatType = type == MessageType.SYSTEM ? ChatType.SYSTEM : ChatType.CHAT;
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.func_241151_a_(realMsg, chatType, source.uuid());
        }
    }

    @Override
    public void sendActionBar(@NonNull Component message) {
        final ITextComponent realMsg = new ComponentWrapper(message);
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.sendStatusMessage(realMsg, true);
        }
    }

    @Override
    public void sendPlayerListHeader(@NonNull Component header) {
        // TODO We might want to preserve the old footer
        this.sendPlayerListHeaderAndFooter(header, Component.empty());
    }

    @Override
    public void sendPlayerListFooter(@NonNull Component footer) {
        // TODO We might want to preserve the old header
        this.sendPlayerListHeaderAndFooter(Component.empty(), footer);
    }

    @Override
    public void sendPlayerListHeaderAndFooter(@NonNull Component header, @NonNull Component footer) {
        final ITextComponent realHeader = new ComponentWrapper(header);
        final ITextComponent realFooter = new ComponentWrapper(footer);
        final SPlayerListHeaderFooterPacket packet = new SPlayerListHeaderFooterPacket();
        final PacketBuffer bridge = new PacketBuffer(Unpooled.buffer())
                .writeTextComponent(realHeader)
                .writeTextComponent(realFooter);
        try {
            // TODO Stop it, this is !@*#!@%#(!&@#)(! go use access transformer
            bridge.resetReaderIndex();
            packet.readPacketData(bridge);
        } catch (IOException e) {
            throw new RuntimeException("YELL AT THE AUTHOR TO ACTUALLY USE ACCESS TRANSFORMER", e);
        } finally {
            bridge.release();
        }
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.connection.sendPacket(packet);
        }
    }

    @Override
    public void showTitle(@NonNull Title title) {
        Title.Times titleTimes = title.times();
        if (titleTimes == null) {
            // How are you suppose to handle this case anyway?
            this.clearTitle();
        } else {
            final ServerPlayerEntity p = this.player.get();
            if (p != null) {
                p.connection.sendPacket(new STitlePacket(STitlePacket.Type.TITLE, TextComponentMapper.toNative(title.title())));
                p.connection.sendPacket(new STitlePacket(STitlePacket.Type.SUBTITLE, TextComponentMapper.toNative(title.subtitle())));
                p.connection.sendPacket(new STitlePacket((int)titleTimes.fadeIn().toMillis() / 50, (int)titleTimes.stay().toMillis() / 50, (int)titleTimes.fadeOut().toMillis() / 50));
            }
        }
    }

    @Override
    public void clearTitle() {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.connection.sendPacket(new STitlePacket(STitlePacket.Type.CLEAR, StringTextComponent.EMPTY));
        }
    }

    @Override
    public void resetTitle() {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.connection.sendPacket(new STitlePacket(STitlePacket.Type.RESET, StringTextComponent.EMPTY));
        }
    }

    @Override
    public void showBossBar(@NonNull BossBar bar) {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            final ServerBossInfo nativeBossInfo = this.bossBarMapper.apply(bar);
            nativeBossInfo.addPlayer(p);
        }
    }

    @Override
    public void hideBossBar(@NonNull BossBar bar) {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            final ServerBossInfo nativeBossInfo = this.bossBarMapper.apply(bar);
            nativeBossInfo.removePlayer(p);
        }
    }

    @Override
    public void playSound(@NonNull Sound sound) {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.connection.sendPacket(new SPlaySoundPacket(KeyMapper.toNative(sound.name()), SoundMapper.toNative(sound.source()),
                    p.getPositionVec(), sound.volume(), sound.pitch()));
        }
    }

    @Override
    public void playSound(@NonNull Sound sound, double x, double y, double z) {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.connection.sendPacket(new SPlaySoundPacket(KeyMapper.toNative(sound.name()), SoundMapper.toNative(sound.source()),
                    new Vector3d(x, y, z), sound.volume(), sound.pitch()));
        }
    }

    @Override
    public void stopSound(@NonNull SoundStop stop) {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            p.connection.sendPacket(new SStopSoundPacket(KeyMapper.toNative(stop.sound()), SoundMapper.toNative(stop.source())));
        }
    }

    @Override
    public void openBook(@NonNull Book book) {
        final ServerPlayerEntity p = this.player.get();
        if (p != null) {
            final ItemStack fakeBookItem = new ItemStack(Items.WRITTEN_BOOK);
            final CompoundNBT data = fakeBookItem.getOrCreateTag();
            data.putString("title", PlainComponentSerializer.plain().serialize(book.title()));
            data.putString("author", PlainComponentSerializer.plain().serialize(book.author()));
            final ListNBT pages = new ListNBT();
            for (Component page : book.pages()) {
                pages.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(TextComponentMapper.toNative(page))));
            }
            data.put("pages", pages);
            data.putBoolean("resolved", true);

            // Hack: swap out the item on main hand to trick Minecraft to open the book, then swap back
            final ItemStack previous = p.getHeldItemMainhand();
            p.connection.sendPacket(new SSetSlotPacket(-2, p.inventory.currentItem, fakeBookItem));
            p.openBook(fakeBookItem, Hand.MAIN_HAND);
            p.connection.sendPacket(new SSetSlotPacket(-2, p.inventory.currentItem, previous));
        }
    }
}
