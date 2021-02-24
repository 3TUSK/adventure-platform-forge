package info.tritusk.adventure.platform.forge.impl;

import info.tritusk.adventure.platform.forge.impl.visitor.ToNativeConverter;
import net.kyori.adventure.text.Component;
import net.minecraft.util.text.IFormattableTextComponent;

public class TextComponentMapper {

    public static IFormattableTextComponent toNative(Component component) {
        final ToNativeConverter converter = new ToNativeConverter();
        converter.accept(component);
        return converter.getNative();
    }
}
