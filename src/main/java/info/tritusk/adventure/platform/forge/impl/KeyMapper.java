package info.tritusk.adventure.platform.forge.impl;

import net.kyori.adventure.key.Key;
import net.minecraft.util.ResourceLocation;

public class KeyMapper {

    public static ResourceLocation toNative(Key key) {
        return key == null ? null : new ResourceLocation(key.namespace(), key.value());
    }

    public static Key toAdventure(ResourceLocation id) {
        return Key.key(id.getNamespace(), id.getPath());
    }
}
