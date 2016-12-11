package com.lqtemple.android.lqbookreader.read;

import android.annotation.SuppressLint;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;

import com.lqtemple.android.lqbookreader.dto.HighLight;

/**
 * TODO dismiss lint
 */
@SuppressLint("ParcelCreator")
public class HighlightSpan extends BackgroundColorSpan {

    private HighLight highLight;

    public HighlightSpan(HighLight highLight ) {
        super( highLight.getColor() );
        this.highLight = highLight;
    }

    public HighLight getHighLight() {
        return this.highLight;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        ds.setUnderlineText(this.highLight.getTextNote() != null && this.highLight.getTextNote().trim().length() > 0 );
    }
}
