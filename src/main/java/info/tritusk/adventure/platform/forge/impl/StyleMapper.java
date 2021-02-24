package info.tritusk.adventure.platform.forge.impl;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.util.text.Style;

public class StyleMapper {

    private static Boolean toNullableBoolean(TextDecoration.State state) {
        switch (state) {
            case TRUE: return Boolean.TRUE;
            case FALSE: return Boolean.FALSE;
            default: return null; // Implied case NOT_SET
        }
    }

    public static Style toNative(final net.kyori.adventure.text.format.Style style) {
        Style nativeStyle = Style.EMPTY;
        if (style == null) {
            return nativeStyle;
        }
        nativeStyle = nativeStyle.setBold(toNullableBoolean(style.decoration(TextDecoration.BOLD)))
                .setItalic(toNullableBoolean(style.decoration(TextDecoration.ITALIC)))
                .setObfuscated(toNullableBoolean(style.decoration(TextDecoration.OBFUSCATED)))
                .setStrikethrough(toNullableBoolean(style.decoration(TextDecoration.STRIKETHROUGH)))
                .setUnderlined(toNullableBoolean(style.decoration(TextDecoration.UNDERLINED)))
                .setInsertion(style.insertion());
        TextColor color = style.color();
        if (color != null) {
            nativeStyle = nativeStyle.setColor(ColorMapper.toNative(color));
        }
        Key fontId = style.font();
        if (fontId != null) {
            nativeStyle = nativeStyle.setFontId(KeyMapper.toNative(fontId));
        }
        ClickEvent clickEvent = style.clickEvent();
        if (clickEvent != null) {
            nativeStyle = nativeStyle.setClickEvent(ClickEventMapper.toNative(clickEvent));
        }
        HoverEvent<?> hoverEvent = style.hoverEvent();
        if (hoverEvent != null) {
            nativeStyle = nativeStyle.setHoverEvent(HoverEventMapper.toNative(hoverEvent));
        }
        return nativeStyle;
    }
}
