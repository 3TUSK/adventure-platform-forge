package info.tritusk.adventure.platform.forge.impl;

import info.tritusk.adventure.platform.forge.impl.visitor.ToNativeConverter;
import net.kyori.adventure.text.Component;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Optional;

public class ComponentWrapper implements ITextComponent {

    private final Component wrapped;
    private final ITextComponent deepConverted;

    public ComponentWrapper(Component wrapped) {
        this.wrapped = wrapped;
        final ToNativeConverter converter = new ToNativeConverter();
        converter.accept(wrapped);
        this.deepConverted = converter.getNative();
    }

    public Component getWrapped() {
        return this.wrapped;
    }

    @Override
    public @NonNull Style getStyle() {
        return this.deepConverted.getStyle();
    }

    @Override
    public @NonNull String getUnformattedComponentText() {
        return this.deepConverted.getUnformattedComponentText();
    }

    @Override
    public @NonNull List<ITextComponent> getSiblings() {
        return this.deepConverted.getSiblings();
    }

    @Override
    public @NonNull IFormattableTextComponent copyRaw() {
        return this.deepConverted.copyRaw();
    }

    @Override
    public @NonNull IFormattableTextComponent deepCopy() {
        return this.deepConverted.deepCopy();
    }

    @Override
    public @NonNull IReorderingProcessor func_241878_f() {
        return this.deepConverted.func_241878_f();
    }

    @Override
    public @NonNull String getString() {
        return deepConverted.getString();
    }

    @Override
    public @NonNull String getStringTruncated(int maxLen) {
        return deepConverted.getStringTruncated(maxLen);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public <T> @NonNull Optional<T> getComponentWithStyle(@NonNull IStyledTextAcceptor<T> acceptor, @NonNull Style styleIn) {
        return deepConverted.getComponentWithStyle(acceptor, styleIn);
    }

    @Override
    public <T> @NonNull Optional<T> getComponent(@NonNull ITextAcceptor<T> acceptor) {
        return deepConverted.getComponent(acceptor);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public <T> @NonNull Optional<T> func_230534_b_(@NonNull IStyledTextAcceptor<T> acceptor, @NonNull Style style) {
        return deepConverted.func_230534_b_(acceptor, style);
    }

    @Override
    public <T> @NonNull Optional<T> func_230533_b_(@NonNull ITextAcceptor<T> acceptor) {
        return deepConverted.func_230533_b_(acceptor);
    }

}
