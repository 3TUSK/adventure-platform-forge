package info.tritusk.adventure.platform.forge.impl;

import net.kyori.adventure.text.format.TextColor;
import net.minecraft.util.text.Color;

public class ColorMapper {

    public static Color toNative(TextColor color) {
        return Color.fromInt(color.value());
    }

    public static TextColor toAdventure(Color color) {
        return TextColor.color(color.getColor());
    }

}
