package com.lqtemple.android.lqbookreader;

import android.graphics.Typeface;

public class FontFamily {
    private Typeface defaultTypeface;
    private Typeface boldTypeface;
    private Typeface italicTypeface;
    private Typeface boldItalicTypeface;
    private String name;

    public FontFamily(String name, Typeface defaultTypeFace) {
        this.name = name;
        this.defaultTypeface = defaultTypeFace;
    }

    public String getName() {
        return this.name;
    }

    public void setBoldItalicTypeface(Typeface boldItalicTypeface) {
        this.boldItalicTypeface = boldItalicTypeface;
    }

    public void setBoldTypeface(Typeface boldTypeface) {
        this.boldTypeface = boldTypeface;
    }

    public void setDefaultTypeface(Typeface defaultTypeface) {
        this.defaultTypeface = defaultTypeface;
    }

    public void setItalicTypeface(Typeface italicTypeface) {
        this.italicTypeface = italicTypeface;
    }

    public Typeface getBoldItalicTypeface() {
        return this.boldItalicTypeface;
    }

    public Typeface getBoldTypeface() {
        return this.boldTypeface;
    }

    public Typeface getDefaultTypeface() {
        return this.defaultTypeface;
    }

    public Typeface getItalicTypeface() {
        return this.italicTypeface;
    }

    public boolean isFakeBold() {
        return this.boldTypeface == null;
    }

    public boolean isFakeItalic() {
        return this.italicTypeface == null;
    }

    public String toString() {
        return this.name;
    }
}
