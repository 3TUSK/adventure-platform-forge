package info.tritusk.adventure.platform.forge.impl;

import net.kyori.adventure.util.Codec;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import org.checkerframework.checker.nullness.qual.NonNull;

public class JsonLikeNBTCodec implements Codec<CompoundNBT, String, Exception, Exception> {

    public static final JsonLikeNBTCodec INSTANCE = new JsonLikeNBTCodec();

    @Override
    public @NonNull CompoundNBT decode(@NonNull String encoded) throws Exception {
        return JsonToNBT.getTagFromJson(encoded);
    }

    @Override
    public @NonNull String encode(@NonNull CompoundNBT decoded) {
        return decoded.toString();
    }
}
