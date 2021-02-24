package info.tritusk.adventure.platform.forge.impl;

import net.kyori.adventure.sound.Sound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

import java.util.EnumMap;

public class SoundMapper {

    private static final EnumMap<Sound.Source, SoundCategory> ADVENTURE_TYPE_TO_NATIVE = new EnumMap<>(Sound.Source.class);

    static {
        ADVENTURE_TYPE_TO_NATIVE.put(Sound.Source.MASTER, SoundCategory.MASTER);
        ADVENTURE_TYPE_TO_NATIVE.put(Sound.Source.MUSIC, SoundCategory.MUSIC);
        ADVENTURE_TYPE_TO_NATIVE.put(Sound.Source.RECORD, SoundCategory.RECORDS);
        ADVENTURE_TYPE_TO_NATIVE.put(Sound.Source.WEATHER, SoundCategory.WEATHER);
        ADVENTURE_TYPE_TO_NATIVE.put(Sound.Source.BLOCK, SoundCategory.BLOCKS);
        ADVENTURE_TYPE_TO_NATIVE.put(Sound.Source.HOSTILE, SoundCategory.HOSTILE);
        ADVENTURE_TYPE_TO_NATIVE.put(Sound.Source.NEUTRAL, SoundCategory.NEUTRAL);
        ADVENTURE_TYPE_TO_NATIVE.put(Sound.Source.PLAYER, SoundCategory.PLAYERS);
        ADVENTURE_TYPE_TO_NATIVE.put(Sound.Source.AMBIENT, SoundCategory.AMBIENT);
        ADVENTURE_TYPE_TO_NATIVE.put(Sound.Source.VOICE, SoundCategory.VOICE);
    }

    public static SoundCategory toNative(Sound.Source source) {
        // Note that EnumMap.get(null) works, even though null key is forbidden for an EnumMap.
        // In case of EnumMap.get(null), null is returned.
        return ADVENTURE_TYPE_TO_NATIVE.get(source);
    }

    public static ISound toNative(Sound sound) {
        return toNative(sound, 0D, 0D, 0D, false);
    }

    public static ISound toNative(Sound sound, double x, double y, double z, boolean global) {
        final ResourceLocation id = KeyMapper.toNative(sound.name());
        final SoundCategory category = toNative(sound.source());
        final float pitch = sound.pitch(), volume = sound.volume();
        return new SimpleSound(id, category, pitch, volume, false, 0, ISound.AttenuationType.NONE, x, y, z, global);
    }

}
