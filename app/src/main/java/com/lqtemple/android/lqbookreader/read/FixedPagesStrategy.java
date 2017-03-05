/*
 * Copyright (C) 2013 Alex Kuiper
 *
 * This file is part of PageTurner
 *
 * PageTurner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PageTurner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PageTurner.  If not, see <http://www.gnu.org/licenses/>.*
 */

package com.lqtemple.android.lqbookreader.read;

import android.graphics.Canvas;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.TextView;
import android.widget.Toast;

import com.lqtemple.android.lqbookreader.Configuration;
import com.lqtemple.android.lqbookreader.MyApplication;
import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.Singleton;
import com.lqtemple.android.lqbookreader.StaticLayoutFactory;
import com.lqtemple.android.lqbookreader.dto.HighLight;
import com.lqtemple.android.lqbookreader.model.PageIndex;
import com.lqtemple.android.lqbookreader.model.Spine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import jedi.option.Option;

import static android.media.CamcorderProfile.get;
import static java.util.Collections.emptyList;
import static jedi.option.Options.none;
import static jedi.option.Options.option;
import static jedi.option.Options.some;


public class FixedPagesStrategy implements PageChangeStrategy {

    private Configuration config;

    private StaticLayoutFactory layoutFactory;

    private HighlightManager highlightManager;

    private static final Logger LOG = LoggerFactory.getLogger("FixedPagesStrategy");

	private Spanned text;
	
	private int pageNum;
	
	
	
	private BookView bookView;
	private TextView childView;

	private int storedPosition = -1;
	private PageCounter pageCounter;
	private List<PageIndex> pageIndices;

	@Override
    public void setBookView(BookView bookView) {
        this.bookView = bookView;
        this.childView = bookView.getInnerView();

		config = Configuration.getInstance();
		highlightManager = Singleton.getInstance(HighlightManager.class);
		layoutFactory = new StaticLayoutFactory();
		pageCounter = new PageCounter(bookView.getContext());
		pageCounter.setBookView(bookView);

	}

	@Override
	public void initBookPages() {
		pageCounter.setBook(bookView.getBook());

		pageIndices = pageCounter.caculPageNumber();
		text = pageCounter.getSpannedText();
	}

    public void setHighlightManager( HighlightManager highlightManager ) {
        this.highlightManager = highlightManager;
    }

    public void setLayoutFactory(StaticLayoutFactory layoutFactory) {
        this.layoutFactory = layoutFactory;
    }

    public void setConfig( Configuration config ) {
        this.config = config;
    }

    @Override
	public void clearStoredPosition() {
		this.pageNum = 0;
		this.storedPosition = 0;
	}
	
	@Override
	public void clearText() {
		this.text = new SpannableStringBuilder("");
		this.childView.setText(text);
		this.pageIndices = new ArrayList<>();
	}
	
	/**
	 * Returns the current page INSIDE THE SECTION.
	 * 
	 * @return
	 */
	public int getCurrentPage() {
		return this.pageNum;
	}
	
	@Override
	public void reset() {
		clearStoredPosition();
		this.pageIndices.clear();
		clearText();
	}
	
	private void updatePageNumber() {
		for ( int i=0; i < this.pageIndices.size(); i++ ) {
			if ( this.pageIndices.get(i).getTotalOffset() > this.storedPosition ) {
				this.pageNum = i -1;
				return;
			}
		}

		this.pageNum = this.pageIndices.size() - 1;
	}
	
	@Override
	public void updatePosition() {

		if ( pageIndices.isEmpty() || text.length() == 0 || this.pageNum == -1) {
			return;
		}
		
		if ( storedPosition != -1 ) {
			updatePageNumber();
		}

        CharSequence sequence = getTextForPage(this.pageNum).getOrElse( "" );

        if ( sequence.length() > 0 ) {

            // #555 Remove \n at the end of sequence which get InnerView size changed
            int endIndex = sequence.length();
            while (sequence.charAt(endIndex - 1) == '\n') {
                endIndex--;
            }

            sequence = sequence.subSequence(0, endIndex);
        }

        try {
		    this.childView.setText( sequence );

            //If we get an error setting the formatted text,
            //strip formatting and try again.

        } catch ( IndexOutOfBoundsException ie ) {
            this.childView.setText( sequence.toString() );
        }
	}
	
	private Option<CharSequence> getTextForPage(int page ) {
		
		if ( pageIndices.size() < 1 || page < 0 ) {
			return none();
		} else if ( page >= pageIndices.size() -1 ) {
            int startOffset = pageIndices.get(pageIndices.size() -1).getTotalOffset();

            if ( startOffset >= 0 && startOffset <= text.length() -1 ) {
			    return some(applySpans(this.text.subSequence(startOffset, text.length()), startOffset));
            } else {
                return some(applySpans(text, 0));
            }
		} else {
			int start = this.pageIndices.get(page).getTotalOffset();
			int end = this.pageIndices.get(page +1 ).getTotalOffset();
			return some(applySpans( this.text.subSequence(start, end), start ));
		}	
	}

    private CharSequence applySpans(CharSequence text, int offset) {
        List<HighLight> highLights = highlightManager.getHighLights( bookView.getFileName() );
        int end = offset + text.length() -1;

        for ( final HighLight highLight: highLights ) {
            if ( highLight.getIndex() == bookView.getIndex() &&
                    highLight.getStart() >= offset && highLight.getStart() < end ) {

                LOG.debug("Got highlight from " + highLight.getStart() + " to " + highLight.getEnd() + " with offset " + offset );

                int highLightEnd = Math.min(end, highLight.getEnd() );

                ( (Spannable) text).setSpan(new HighlightSpan(highLight),
                        highLight.getStart() - offset, highLightEnd - offset,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
        }

        return text;
    }
	
	@Override
	public void setPosition(int pos) {
		this.storedPosition = pos;		
	}
	
	@Override
	public void setRelativePosition(double position) {
		
		int intPosition = (int) (this.text.length() * position);
		setPosition(intPosition);
		
	}

    public int getTopLeftPosition() {

        if ( pageIndices.isEmpty() ) {
            return 0;
        }

        if ( this.pageNum >= this.pageIndices.size() ) {
            return this.pageIndices.get( this.pageIndices.size() -1 ).getTotalOffset();
        }

        return this.pageIndices.get(this.pageNum).getTotalOffset();
    }
	
	public int getProgressPosition() {

        if ( storedPosition > 0 || this.pageIndices.isEmpty() ||  this.pageNum == -1 ) {
            return this.storedPosition;
        }

		return getTopLeftPosition();
	}
	
	public Option<Spanned> getText() {
		return option(text);
	}
	
	public boolean isAtEnd() {
		return pageNum == this.pageIndices.size() - 1;
	}
	
	public boolean isAtStart() {
		return this.pageNum == 0;
	}
	
	public boolean isScrolling() {
		return false;
	}
	
	@Override
	public Option<CharSequence> getNextPageText() {

        if ( isAtEnd() ) {
			return none();
		}
		
		return getTextForPage( this.pageNum + 1);
	}
	
	@Override
	public Option<CharSequence> getPreviousPageText() {
		if ( isAtStart() ) {
			return none();
		}
		
		return getTextForPage( this.pageNum - 1);
	}
	
	@Override
	public void pageDown() {

        this.storedPosition = -1;

		// TODO
		if ( isAtEnd() ) {
			Spine spine = bookView.getSpine();
		
			if ( spine == null || ! spine.navigateForward() ) {
				return;
			}
			
			this.pageNum = 0;
			updatePosition();
		} else {
			this.pageNum = Math.min(pageNum +1, this.pageIndices.size() -1 );
			updatePosition();
		}
	}
	
	@Override
	public void pageUp() {

        this.storedPosition = -1;
	
		if ( isAtStart() ) {
			Spine spine = bookView.getSpine();
		
			if ( spine == null || ! spine.navigateBack() ) {
				return;
			}
			
			this.storedPosition = Integer.MAX_VALUE;
			updatePosition();
		} else {
			this.pageNum = Math.max(pageNum -1, 0);
			updatePosition();
		}
	}
	
	@Override
	public void loadText(Spanned text) {
		this.text = text;
		this.pageNum = 0;
		this.pageIndices = pageCounter.caculPageNumber();
	}

    @Override
    public void updateGUI() {
        updatePosition();   
	}
    
}
