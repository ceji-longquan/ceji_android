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

import android.text.Spannable;
import android.text.SpannableString;

import com.lqtemple.android.lqbookreader.Configuration;
import com.lqtemple.android.lqbookreader.model.RawBook;
import com.lqtemple.android.lqbookreader.view.FastBitmapDrawable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jedi.option.Option;

import static android.R.attr.resource;
import static jedi.functional.FunctionalPrimitives.isEmpty;
import static jedi.option.Options.none;
import static jedi.option.Options.option;

/**
 * Singleton storage for opened book and rendered text.
 *
 * Optimization in case of rotation of the screen.
 */
public class TextLoader{

    /**
     * We start clearing the cache if memory usage exceeds 75%.
     */
    private static final double CACHE_CLEAR_THRESHOLD = 0.75;

    private String currentFile;
    private RawBook currentBook;
    private Map<String, Spannable> renderedText = new HashMap<>();

    private Map<String, FastBitmapDrawable> imageCache = new HashMap<>();


    private Map<String, Map<String, Integer>> anchors = new HashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger("TextLoader");


    public void invalidateCachedText() {
        this.renderedText.clear();
    }


    public boolean hasCachedBook( String fileName ) {
        return fileName != null && fileName.equals( currentFile );
    }


    public RawBook initBook(String fileName) throws IOException {

        if (fileName == null) {
            throw new IOException("No file-name specified.");
        }

        if ( hasCachedBook( fileName ) ) {
            LOG.debug("Returning cached Book for fileName " + currentFile );
            return currentBook;
        }

        closeCurrentBook();

        this.anchors = new HashMap<>();

        RawBook newBook = BookLoader.load(fileName);

        this.currentBook = newBook;
        this.currentFile = fileName;

        return newBook;

    }

    public Option<Integer> getAnchor(String href, String anchor ) {
        if ( this.anchors.containsKey(href) ) {
            Map<String, Integer> nestedMap = this.anchors.get( href );
            return option(nestedMap.get(anchor));
        }

        return none();
    }

    public RawBook getCurrentBook() {
        return this.currentBook;
    }

    public FastBitmapDrawable getCachedImage( String href ) {
        return imageCache.get( href );
    }

    public boolean hasCachedImage( String href ) {
        return imageCache.containsKey(href);
    }

    public void storeImageInChache(String href, FastBitmapDrawable drawable ) {
        this.imageCache.put(href, drawable);
    }

    private void registerNewAnchor(String href, String anchor, int position ) {
        if ( ! anchors.containsKey(href)) {
            anchors.put(href, new HashMap<>());
        }

        anchors.get(href).put(anchor, position);
    }

    public Option<Spannable> getCachedTextForResource(String ref) {

        LOG.debug( "Checking for cached resource: " + resource );

        return option(renderedText.get(ref));
    }

    public Spannable getText(final String index) throws IOException {

        Option<Spannable> cached = getCachedTextForResource( index );

        if ( ! isEmpty(cached) ) {
            return cached.unsafeGet();
        }


        double memoryUsage = Configuration.getMemoryUsage();
        double bitmapUsage = Configuration.getBitmapMemoryUsage();

        LOG.debug("Current memory usage is " +  (int) (memoryUsage * 100) + "%" );
        LOG.debug("Current bitmap memory usage is " +  (int) (bitmapUsage * 100) + "%" );

        //If memory usage gets over the threshold, try to free up memory
        if ( memoryUsage > CACHE_CLEAR_THRESHOLD || bitmapUsage > CACHE_CLEAR_THRESHOLD) {

            LOG.debug("Clearing cached resources.");

            clearCachedText();
//            closeLazyLoadedResources();
        }

        boolean shouldClose = false;

        //If it's already in memory, use that. If not, create a copy
        //that we can safely close after using it

        Spannable result = new SpannableString("");
        try {
            result = TextSpanner.from(index);
            renderedText.put(index, result);
        } catch (Exception e) {
            LOG.error("Caught exception while rendering text", e);
            result = new SpannableString( e.getClass().getSimpleName() + ": " + e.getMessage() );
        }
        finally {
            if ( shouldClose ) {
                //We have the rendered version, so it's safe to close the resource
            }
        }

        return result;
    }

//    private void closeLazyLoadedResources() {
//        if ( currentBook != null ) {
//            for ( Resource res: currentBook.getResources().getAll() ) {
//                res.close();
//            }
//        }
//    }

    public void clearCachedText() {
        clearImageCache();
        anchors.clear();

        renderedText.clear();
    }

    public void closeCurrentBook() {

        if ( currentBook != null ) {
//            for ( Resource res: currentBook.getResources().getAll() ) {
//                res.setData(null); //Release the byte[] data.
//            }
        }

        currentBook = null;
        currentFile = null;
        renderedText.clear();
        clearImageCache();
        anchors.clear();
    }

    public void clearImageCache() {
        for (Map.Entry<String, FastBitmapDrawable> draw : imageCache.entrySet()) {
            draw.getValue().destroy();
        }

        imageCache.clear();
    }




}
