package info.tritusk.adventure.platform.forge;

import info.tritusk.adventure.platform.forge.impl.BossBarMapper;
import info.tritusk.adventure.platform.forge.impl.BossInfoListener;
import info.tritusk.adventure.platform.forge.impl.audience.ClientAudience;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;

import java.util.IdentityHashMap;

public class ForgeClientAudiences {
    public static final ForgeClientAudiences INSTANCE = new ForgeClientAudiences();

    private final IdentityHashMap<BossBar, ClientBossInfo> bossBars = new IdentityHashMap<>();
    private final BossInfoListener listener = new BossInfoListener(this::getOrCreateFrom);

    public Audience audience() {
        return new ClientAudience(this::getOrCreateFrom);
    }

    private ClientBossInfo getOrCreateFrom(BossBar original) {
        return this.bossBars.computeIfAbsent(original, bar -> {
            bar.addListener(this.listener);
            return new ClientBossInfo(new SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation.ADD, BossBarMapper.toNative(bar)));
        });
    }
}
