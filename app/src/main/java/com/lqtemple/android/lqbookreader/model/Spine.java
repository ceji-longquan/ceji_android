package com.lqtemple.android.lqbookreader.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jedi.option.Option;

import static jedi.option.Options.none;
import static jedi.option.Options.option;

/**
 * Created by sundxing on 16/12/4.
 */

public class Spine implements Iterable<Spine.SpineEntry>{
    private List<SpineEntry> entries;

    private List<List<Integer>> pageOffsets = new ArrayList<>();

    private int position;

    public static final String COVER_HREF = "PageTurnerCover";

    /** How long should a cover page be to be included **/
    private static final int COVER_PAGE_THRESHOLD = 1024;

    private String tocHref;


    public Spine() {
        this.entries = new ArrayList<>();
        this.position = 0;

    }

    public Spine(Book book) {
        this();
        // 生成目录
        for (JContent content : book.getContent()) {
            String index = content.getIndex();
            if (index.endsWith("0") && content.getType() == Type.Title.ordinal()) {
                // Is title
                SpineEntry entry = new SpineEntry();
                entry.href = index;
                entry.title = content.getText();
                entries.add(entry);
            }
        }
    }

    public void setPageOffsets(List<List<Integer>> pageOffsets) {
        if ( pageOffsets != null ) {
            this.pageOffsets = pageOffsets;
        } else {
            this.pageOffsets = new ArrayList<>();
        }
    }

    public int getTotalNumberOfPages() {
        int total = 0;
        for ( List<Integer> pagesPerSection: pageOffsets ) {
            total += pagesPerSection.size();
        }

        return Math.max(0, total - 1);
    }

    @Override
    public Iterator<SpineEntry> iterator() {
        return this.entries.iterator();
    }

    public List<List<Integer>> getPageOffsets() {
        return pageOffsets;
    }


    /**
     * Returns the number of entries in this spine.
     * This includes the generated cover.
     *
     * @return
     */
    public int size() {
        return this.entries.size();
    }

    /**
     * Navigates one entry forward.
     *
     * @return false if we're already at the end.
     */
    public boolean navigateForward() {

        if ( this.position == size() -1 ) {
            return false;
        }

        this.position++;
        return true;
    }

    /**
     * Navigates one entry back.
     *
     * @return false if we're already at the start
     */
    public boolean navigateBack() {
        if ( this.position == 0 ) {
            return false;
        }

        this.position--;
        return true;
    }

    /**
     * Checks if the current entry is the cover page.
     *
     * @return
     */
    public boolean isCover() {
        return this.position == 0;
    }

    /**
     * Returns the title of the current entry,
     * or null if it could not be determined.
     *
     * @return
     */
    public Option<String> getCurrentTitle() {
        if ( entries.size() > 0 ) {
            return option(entries.get(position).title);
        } else {
            return none();
        }
    }

    /**
     * Returns the href of the current resource.
     * @return
     */
    public Option<String> getCurrentHref() {
        if ( entries.size() > 0 ) {
            return option(entries.get(position).href);
        } else {
            return none();
        }
    }

    /**
     * Navigates to a specific point in the spine.
     *
     * @param index
     * @return false if the point did not exist.
     */
    public boolean navigateByIndex( int index ) {
        if ( index < 0 || index >= size() ) {
            return false;
        }

        this.position = index;
        return true;
    }

    /**
     * Returns the current position in the spine.
     *
     * @return
     */
    public int getPosition() {
        return position;
    }

    /**
     * Navigates to the point with the given href.
     *
     * @param href
     * @return false if that point did not exist.
     */
    public boolean navigateByHref( String href ) {

        String encodedHref = encode(href);

        for ( int i=0; i < size(); i++ ) {
            String entryHref = encode(entries.get(i).href);
            if ( entryHref.equals(encodedHref)){
                this.position = i;
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a percentage, which indicates how
     * far the given point in the current entry is
     * compared to the whole book.
     *
     * @param progressInPart
     * @return
     */
    public int getProgressPercentage(double progressInPart) {
        return getProgressPercentage(getPosition(), progressInPart);
    }

    private int getProgressPercentage(int index, double progressInPart) {

        if ( this.entries == null ) {
            return -1;
        }

        double uptoHere = 0;

        List<Double> percentages = getRelativeSizes();

        for ( int i=0; i < percentages.size() && i < index; i++ ) {
            uptoHere += percentages.get( i );
        }

        double thisPart = percentages.get(index);

        double progress = uptoHere + (progressInPart * thisPart);

        return (int) (progress * 100);
    }

    /**
     * Returns the progress percentage for the given text position
     * in the given index.
     *
     * @param index
     * @param position
     * @return
     */
    public int getProgressPercentage(int index, int position) {
        if ( this.entries == null || index >= entries.size() ) {
            return -1;
        }

        double progressInPart = ( (double)position / (double) entries.get(index).size);
        return getProgressPercentage(index, progressInPart);
    }



    /**
     * Returns a list of doubles representing the relative size of each spine index.
     * @return
     */
    public List<Double> getRelativeSizes() {
        int total = 0;
        List<Integer> sizes = new ArrayList<>();

        for ( int i=0; i < entries.size(); i++ ) {
            int size = entries.get(i).size;
            sizes.add(size);
            total += size;
        }

        List<Double> result = new ArrayList<>();
        for ( int i=0; i < sizes.size(); i++ ) {
            double part = (double) sizes.get(i) / (double) total;
            result.add( part );
        }

        return result;
    }



    private static String encode(String input) {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if ( ch == '\\' ) { //Some books use \ as a separator... invalid, but we'll try to fix it
                resultStr.append('/');
            } else if (isUnsafe(ch)) {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            } else {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }

    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    /**
     * This is slightly unsafe: it lets / and % pass, making
     * multiple encodes safe.
     * @param ch
     * @return
     */
    private static boolean isUnsafe(char ch) {
        if (ch > 128 || ch < 0)
            return true;
        return " %$&+,:;=?@<>#[]".indexOf(ch) >= 0;
    }

    public class SpineEntry {

        private String title;
        private String href;

        private int size;

        public String getTitle() {
            return title;
        }

        public int getSize() {
            return size;
        }

        public String getHref() {
            return href;
        }
    }
}
