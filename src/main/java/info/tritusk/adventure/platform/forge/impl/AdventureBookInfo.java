package info.tritusk.adventure.platform.forge.impl;

import net.kyori.adventure.inventory.Book;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class AdventureBookInfo implements ReadBookScreen.IBookInfo {

    private final Book book;
    private final List<ITextComponent> pages;

    public AdventureBookInfo(Book book) {
        this.book = book;
        this.pages = book.pages().stream().map(ComponentWrapper::new).collect(Collectors.toList());
    }

    @Override
    public int getPageCount() {
        return this.book.pages().size();
    }

    @Override
    public @NonNull ITextProperties func_230456_a_(int index) {
        return this.pages.get(index);
    }
}
