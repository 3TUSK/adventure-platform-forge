package info.tritusk.adventure.platform.forge;

import info.tritusk.adventure.platform.forge.impl.BossBarMapper;
import info.tritusk.adventure.platform.forge.impl.BossInfoListener;
import info.tritusk.adventure.platform.forge.impl.ForgePlatform;
import info.tritusk.adventure.platform.forge.impl.KeyMapper;
import info.tritusk.adventure.platform.forge.impl.audience.AllServerPlayerAudience;
import info.tritusk.adventure.platform.forge.impl.audience.FilteredServerPlayerAudience;
import info.tritusk.adventure.platform.forge.impl.audience.ServerAudience;
import info.tritusk.adventure.platform.forge.impl.audience.ServerPlayerAudience;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.AudienceProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.server.permission.PermissionAPI;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.IdentityHashMap;
import java.util.UUID;

public class ForgeServerAudiences implements AudienceProvider {

    public static ForgeServerAudiences of() {
        return ForgePlatform.serverAudienceProvider; // TODO Throw exception when it is not ready
    }

    private final MinecraftServer server;
    private final ServerAudience theServerAudience;
    private final IdentityHashMap<BossBar, ServerBossInfo> trackedBossBars = new IdentityHashMap<>();
    private final BossInfoListener listener = new BossInfoListener(this::getOrCreateFrom);

    public ForgeServerAudiences(MinecraftServer server) {
        this.theServerAudience = new ServerAudience(this.server = server);
    }

    public ServerBossInfo getOrCreateFrom(BossBar bossBar) {
        return this.trackedBossBars.computeIfAbsent(bossBar, bar -> {
            bar.addListener(this.listener);
            return BossBarMapper.toNative(bar);
        });
    }

    @Override
    public @NonNull Audience all() {
        return Audience.audience(this.players(), this.console());
    }

    @Override
    public @NonNull Audience console() {
        return this.theServerAudience;
    }

    @Override
    public @NonNull Audience players() {
        return new AllServerPlayerAudience(this.server.getPlayerList(), this::getOrCreateFrom);
    }

    @Override
    public @NonNull Audience player(@NonNull UUID playerId) {
        final ServerPlayerEntity p = this.server.getPlayerList().getPlayerByUUID(playerId);
        return p == null ? Audience.empty() : new ServerPlayerAudience(p, this::getOrCreateFrom);
    }

    @Override
    public @NonNull Audience permission(@NonNull String permission) {
        return new FilteredServerPlayerAudience(this.server.getPlayerList(), this::getOrCreateFrom, p -> PermissionAPI.hasPermission(p, permission));
    }

    @Override
    public @NonNull Audience world(@NonNull Key world) {
        final RegistryKey<World> worldKey = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, KeyMapper.toNative(world));
        return this.server.getWorld(worldKey) == null ? Audience.empty() : new FilteredServerPlayerAudience(this.server.getPlayerList(), this::getOrCreateFrom, p -> p.world.getDimensionKey() == worldKey);
    }

    @Override
    public @NonNull Audience server(@NonNull String serverName) {
        return this.all();
    }

    @Override
    public void close() {
        // TODO What kind of clean up do we need here?
    }
}
