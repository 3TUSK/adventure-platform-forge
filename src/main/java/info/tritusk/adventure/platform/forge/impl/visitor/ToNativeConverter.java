package info.tritusk.adventure.platform.forge.impl.visitor;

import info.tritusk.adventure.platform.forge.impl.KeyMapper;
import info.tritusk.adventure.platform.forge.impl.StyleMapper;
import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.EntityNBTComponent;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.KeybindTextComponent;
import net.minecraft.util.text.NBTTextComponent;
import net.minecraft.util.text.ScoreTextComponent;
import net.minecraft.util.text.SelectorTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * A {@link ComponentVisitor} implementation that converts a adventure
 * {@link Component} instance to a native {@link ITextComponent}.
 */
public class ToNativeConverter implements ComponentVisitor {

    private IFormattableTextComponent nativeComponent;

    public IFormattableTextComponent getNative() {
        return this.nativeComponent;
    }

    private static IFormattableTextComponent handleStyleAndChildren(Component original, IFormattableTextComponent converted) {
        converted.setStyle(StyleMapper.toNative(original.style()));
        for (Component child : original.children()) {
            final ToNativeConverter converter = new ToNativeConverter();
            converter.accept(child);
            converted.append(converter.nativeComponent);
        }
        return converted;
    }

    @Override
    public void accept(BlockNBTComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new NBTTextComponent.Block(c.nbtPath(), c.interpret(), c.pos().asString()));
    }

    @Override
    public void accept(EntityNBTComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new NBTTextComponent.Entity(c.nbtPath(), c.interpret(), c.selector()));
    }

    @Override
    public void accept(KeybindComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new KeybindTextComponent(c.keybind()));
    }

    @Override
    public void accept(ScoreComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new ScoreTextComponent(c.name(), c.objective()));
    }

    @Override
    public void accept(SelectorComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new SelectorTextComponent(c.pattern()));
    }

    @Override
    public void accept(StorageNBTComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new NBTTextComponent.Storage(c.nbtPath(), c.interpret(), KeyMapper.toNative(c.storage())));
    }

    @Override
    public void accept(TextComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new StringTextComponent(c.content()));
    }

    @Override
    public void accept(TranslatableComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new TranslationTextComponent(c.key(), c.args().stream().map(arg -> {
            final ToNativeConverter converter = new ToNativeConverter();
            converter.accept(arg);
            return converter.nativeComponent;
        }).toArray()));
    }
}
