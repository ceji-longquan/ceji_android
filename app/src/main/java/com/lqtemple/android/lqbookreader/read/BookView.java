package com.lqtemple.android.lqbookreader.read;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lqtemple.android.lqbookreader.Configuration;
import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.Singleton;
import com.lqtemple.android.lqbookreader.dto.HighLight;
import com.lqtemple.android.lqbookreader.model.Book;
import com.lqtemple.android.lqbookreader.model.Content;
import com.lqtemple.android.lqbookreader.model.Spine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jedi.functional.Command;
import jedi.option.Option;

import static java.util.Arrays.asList;
import static jedi.functional.FunctionalPrimitives.forEach;
import static jedi.functional.FunctionalPrimitives.isEmpty;
import static jedi.option.Options.none;
import static jedi.option.Options.option;
import static jedi.option.Options.some;

/**
 * Created by sundxing on 16/12/4.
 */
public class BookView extends ScrollView{

    public static final String TAG = "BookView";
    private static final Logger LOG = LoggerFactory.getLogger("BookView");

    private int storedIndex;
    private String storedAnchor;

    private InnerView childView;

    private Set<BookViewListener> listeners;


    private Spine spine;

    private String fileName;
    private Book book;

    private int prevIndex = -1;
    private int prevPos = -1;

    private PageChangeStrategy strategy;

    private int horizontalMargin = 0;
    private int verticalMargin = 0;
    private int lineSpacing = 0;

    private Handler scrollHandler;
    private TextLoader textLoader;
    private Configuration configuration;
    private PageChangeStrategy scrollingStrategy = new ScrollingStrategy();
    private PageChangeStrategy fixedPagesStrategy = new FixedPagesStrategy();

    public BookView(Context context, AttributeSet attributes) {
        super(context, attributes);
        this.scrollHandler = new Handler();
        this.textLoader = Singleton.getInstance(TextLoader.class);
        this.configuration = Configuration.getInstance();

    }

    public void init() {
        this.listeners = new HashSet<>();

        this.childView = (InnerView) this.findViewById(R.id.innerView);
        this.childView.setBookView(this);
        this.childView.setLineSpacing(lineSpacing, configuration.getLineSpacingMult());

        childView.setCursorVisible(false);
        childView.setLongClickable(true);
        this.setVerticalFadingEdgeEnabled(false);
        childView.setFocusable(true);
        childView.setLinksClickable(true);

        if (Build.VERSION.SDK_INT >= Configuration.TEXT_SELECTION_PLATFORM_VERSION ) {
            childView.setTextIsSelectable(true);
        }

        this.setSmoothScrollingEnabled(false);

    }

    public void update() {
        strategy.updateGUI();

    }
    /**
     * Loads the text and saves the restored position.
     */
    public void restore() {
        strategy.clearText();
        loadText();
    }
    /**
     * Blocks the inner-view from creating action-modes for a given amount of time.
     *
     * @param time
     */
    public void blockFor( long time ) {
        this.childView.setBlockUntil( System.currentTimeMillis() + time );
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
        this.childView.setOnTouchListener(l);
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPosition(int pos) {
        this.strategy.setPosition(pos);
    }

    public void setIndex(int index) {
        this.storedIndex = index;
    }

    public void setTextSize(float textSize) {
        this.childView.setTextSize(textSize);

    }

    public void setTextColor(int color) {
        if (this.childView != null) {
            this.childView.setTextColor(color);
        }
    }
    public void setLinkColor(int color) {
        this.childView.setLinkTextColor(color);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);

        if (this.childView != null) {
            this.childView.setBackgroundColor(color);
        }
    }

    public void setLineSpacing(int lineSpacing) {
        if (lineSpacing != this.lineSpacing) {
            this.lineSpacing = lineSpacing;
            this.childView.setLineSpacing(lineSpacing, configuration.getLineSpacingMult());

            if (strategy != null) {
                strategy.updatePosition();
            }
        }
    }

    public void setHorizontalMargin(int horizontalMargin) {

        if (horizontalMargin != this.horizontalMargin) {
            this.horizontalMargin = horizontalMargin;
            setPadding(this.horizontalMargin, this.verticalMargin,
                    this.horizontalMargin, this.verticalMargin);
            if (strategy != null) {
                strategy.updatePosition();
            }
        }
    }
    public void setVerticalMargin(int verticalMargin) {
        if (verticalMargin != this.verticalMargin) {
            this.verticalMargin = verticalMargin;
            setPadding(this.horizontalMargin, this.verticalMargin,
                    this.horizontalMargin, this.verticalMargin);
            if (strategy != null) {
                strategy.updatePosition();
            }
        }
    }
    public int getVerticalMargin() {
        return verticalMargin;
    }

    public int getSelectionStart() {
        return childView.getSelectionStart();
    }

    public int getSelectionEnd() {
        return childView.getSelectionEnd();
    }

    public Option<String> getSelectedText() {

        int start = getSelectionStart();
        int end = getSelectionEnd();

        if (start > 0 && end > 0 && end > start) {
            return some(childView.getText()
                    .subSequence(getSelectionStart(), getSelectionEnd()).toString());
        } else {
            return none();
        }
    }

    /**
     * this f
     * @param percentage
     */
    public void navigateToPercentage(int percentage) {

        if (spine == null) {
            return;
        }

        int index = 0;

        if ( percentage > 0 ) {

            double targetPoint = (double) percentage / 100d;
            List<Double> percentages = this.spine.getRelativeSizes();

            if (percentages == null || percentages.isEmpty()) {
                return;
            }

            double total = 0;

            for (; total < targetPoint && index < percentages.size(); index++) {
                total = total + percentages.get(index);
            }

            index--;

            // Work-around for when we get multiple events.
            if (index < 0 || index >= percentages.size()) {
                return;
            }

            double partBefore = total - percentages.get(index);
            double progressInPart = (targetPoint - partBefore)
                    / percentages.get(index);

            this.strategy.setRelativePosition(progressInPart);
        } else {

            //Simply jump to titlepage
            this.strategy.setPosition(0);
        }

        this.prevPos = this.getProgressPosition();
        doNavigation(index);
    }

    private void doNavigation(int index) {

        // Check if we're already in the right part of the book
        if (index == this.getIndex()) {
            restorePosition();
            progressUpdate();
            return;
        }

        this.prevIndex = this.getIndex();

        this.storedIndex = index;
        this.strategy.clearText();
        this.spine.navigateByIndex(index);

        loadText();
    }

    void loadText() {

        if (spine == null) {
            try {
                Book book = initBookAndSpine();

                if (book != null) {
                    bookOpened(book);
                }
                strategy.initBookPages();

            } catch (IOException io) {
                errorOnBookOpening(io.getMessage());
                return;
            } catch (OutOfMemoryError e) {
                errorOnBookOpening(getContext().getString(R.string.out_of_memory));
                return;
            }
        }
    }

    private Book initBookAndSpine() throws IOException {

        book = textLoader.initBook(fileName);

        this.spine = new Spine(book);

        this.spine.navigateByIndex(BookView.this.storedIndex);

        if (configuration.isShowPageNumbers()) {

            Option<List<List<Integer>>> offsets = configuration
                    .getPageOffsets(fileName);

            offsets.filter( o -> o.size() > 0 ).forEach(
                    (Command<? super List<List<Integer>>>) o -> spine.setPageOffsets( o ));
        }

        return book;
    }

    public void addListener(BookViewListener listener) {
        this.listeners.add(listener);
    }

    private void bookOpened(Book book) {
        for (BookViewListener listener : this.listeners) {
            listener.bookOpened(book);
        }
    }

    private void errorOnBookOpening(String errorMessage) {
        for (BookViewListener listener : this.listeners) {
            listener.errorOnBookOpening(errorMessage);
        }
    }
    private void parseEntryStart(int entry) {
        for (BookViewListener listener : this.listeners) {
            listener.parseEntryStart(entry);
        }
    }

    private void parseEntryComplete( String name) {
        for (BookViewListener listener : this.listeners) {
            listener.parseEntryComplete(name);
        }
    }

    private void fireOpenFile() {
        for (BookViewListener listener : this.listeners) {
            listener.readingFile();
        }
    }

    private void fireRenderingText() {
        for (BookViewListener listener : this.listeners) {
            listener.renderingText();
        }
    }

    /**
     * This call before page number change!
     */
    private void progressUpdate() {

        if (this.strategy == null) {
            return;
        }
        int progress = getCurrentProgress();
        // Start at 0
        int pageNumber = getCurrentPageNum() + 1;
        for (BookViewListener listener : this.listeners) {
            listener.progressUpdate(progress, pageNumber,
                    getTotalPageNum());
        }

    }

    private int getTotalPageNum() {
        if (strategy instanceof FixedPagesStrategy) {
            return ((FixedPagesStrategy) strategy).getTotalPageNum();
        } {
            return 0;
        }
    }

    private int getCurrentProgress() {
        if (strategy instanceof FixedPagesStrategy) {
            return ((FixedPagesStrategy) strategy).getCurrentProgressPercent();
        } {
            return 0;
        }
    }

    private int getCurrentPageNum() {
        if (strategy instanceof FixedPagesStrategy) {
            return ((FixedPagesStrategy) strategy).getCurrentPage();
        } {
            return 0;
        }
    }

    public int getTotalNumberOfPages() {
        if ( spine != null ) {
            return spine.getTotalNumberOfPages();
        }

        return -1;
    }

    public int getPercentageFor( int index, int offset ) {
        if ( spine != null ) {
            return spine.getProgressPercentage(index, offset);
        }

        return -1;
    }

    public int getPageNumberFor(Content content) {
        int offset = content.getTotalOffset();
        PageCounter pageCounter = strategy.getPageCounter();
        int number =  pageCounter.getPageNumberFor(offset);

        Log.d(TAG, " Get Content[" + content.getIndex() + ", offset = " + offset +"] ----> number : " + number);
        return number;
    }

    @Deprecated
    public int getPageNumberFor( int index, int position ) {

        if ( spine == null ) {
            return -1;
        }

        LOG.debug( "Looking for pageNumber for index=" + index + ", position=" + position );

        int pageNum = -1;

        List<List<Integer>> pageOffsets = spine.getPageOffsets();

        if ( pageOffsets == null || index >= pageOffsets.size() ) {
            return -1;
        }

        for ( int i=0; i < index; i++ ) {

            int pages = pageOffsets.get(i).size();

            pageNum += pages;

            LOG.debug("Index " + i + ": pages=" + pages);
        }

        List<Integer> offsets = pageOffsets.get(index);

        LOG.debug("Pages before this index: " + pageNum );


        for ( int i=0; i < offsets.size() && offsets.get(i) <= position; i++ ) {
            pageNum++;
        }

        LOG.debug( "Calculated pageNumber=" + pageNum );
        return pageNum;
    }

    public String getFirstLine() {
        int topLeft = strategy.getTopLeftPosition();

        String plainText = "";

        if ( getStrategy().getText() != null ) {
            plainText = getStrategy().getText().toString();
        }

        if ( plainText.length() == 0 ) {
            return plainText;
        }

        plainText = plainText.substring( topLeft, plainText.length() ).trim();

        int firstNewLine = plainText.indexOf( '\n' );

        if ( firstNewLine == -1 ) {
            return plainText;
        }

        return plainText.substring(0, firstNewLine);
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    // TODO rename index
    public int getIndex() {
        if (this.spine == null) {
            return storedIndex;
        }

        return this.spine.getPosition();
    }

    public boolean hasPrevPosition() {
        return this.prevIndex != -1 && this.prevPos != -1;
    }

    public int getProgressPosition() {
        return strategy.getProgressPosition();
    }

    public String getFileName() {
        return fileName;
    }


    public PageChangeStrategy getStrategy() {
        return this.strategy;
    }

    public int getStartOfCurrentPage() {
        return strategy.getTopLeftPosition();
    }

    /**
     * Scrolls to a previously stored point.
     *
     * Call this after setPosition() to actually go there.
     */
    private void restorePosition() {

        if (this.storedAnchor != null  ) {
            spine.getCurrentHref().forEach((Command<? super String>) href -> {
                Option<Integer> anchorValue = this.textLoader.getAnchor(
                        href, storedAnchor );

                if ( !isEmpty(anchorValue) ) {
                    strategy.setPosition(anchorValue.getOrElse(0));
                    this.storedAnchor = null;
                }
            });
        }

        this.strategy.updatePosition();
    }

    void onInnerViewResize() {
        restorePosition();
    }
    /**
     * Returns if we're at the start of the book, i.e. displaying the title
     * page.
     *
     * @return
     */
    public boolean isAtStart() {

        if (spine == null) {
            return true;
        }

        return spine.getPosition() == 0 && strategy.isAtStart();
    }

    public boolean isAtEnd() {
        if (spine == null) {
            return false;
        }

        return spine.getPosition() >= spine.size() - 1 && strategy.isAtEnd();
    }


    private static String asString( List<Integer> offsets ) {

        StringBuilder stringBuilder = new StringBuilder("[ ");

        forEach( offsets, o -> stringBuilder.append( o + " ") );

        stringBuilder.append(" ]");

        return stringBuilder.toString();
    }


    /**
     * Returns the full word containing the character at the selected location.
     *
     * @param x
     * @param y
     * @return
     */
    public Option<SelectedWord> getWordAt(float x, float y) {

        if (childView == null) {
            return none();
        }

        CharSequence text = this.childView.getText();

        if (text.length() == 0) {
            return none();
        }

        Option<Integer> offsetOption = findOffsetForPosition(x, y);

        if ( isEmpty( offsetOption )) {
            return none();
        }

        int offset = offsetOption.unsafeGet();

        if (offset < 0 || offset > text.length() - 1 || isBoundaryCharacter(text.charAt(offset))) {
            return none();
        }

        int left = Math.max(0, offset - 1);
        int right = Math.min(text.length(), offset);

        CharSequence word = text.subSequence(left, right);
        while (left > 0 && !isBoundaryCharacter(word.charAt(0))) {
            left--;
            word = text.subSequence(left, right);
        }

        if (word.length() == 0) {
            return none();
        }

        while (right < text.length()
                && !isBoundaryCharacter(word.charAt(word.length() - 1))) {
            right++;
            word = text.subSequence(left, right);
        }

        int start = 0;
        int end = word.length();

        if (isBoundaryCharacter(word.charAt(0))) {
            start = 1;
            left++;
        }

        if (isBoundaryCharacter(word.charAt(word.length() - 1))) {
            end = word.length() - 1;
            right--;
        }

        if (start > 0 && start < word.length() && end < word.length()) {
            word = word.subSequence(start, end);

            return some(new SelectedWord( left, right, word ));
        }

        return none();
    }

    private static boolean isBoundaryCharacter(char c) {
        char[] boundaryChars = { ' ', '.', ',', '\"', '\'', '\n', '\t', ':',
                '!', '\'' };

        for (int i = 0; i < boundaryChars.length; i++) {
            if (boundaryChars[i] == c) {
                return true;
            }
        }

        return false;
    }
    // --------GET Span by target coordinate ---------
    public List<HighlightSpan> getHighlightsAt( float x, float y ) {
        return getSpansAt(x, y, HighlightSpan.class );
    }

    public List<ClickableSpan> getLinkAt(float x, float y) {
        return getSpansAt(x, y, ClickableSpan.class );
    }

    /**
     * Returns all the spans of a specific class at a specific location.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param spanClass the class of span to filter for
     * @param <A>
     * @return a List of spans of type A, may be empty.
     */
    private <A> List<A> getSpansAt( float x, float y, Class<A> spanClass) {

        Option<Integer> offsetOption = findOffsetForPosition(x, y);

        CharSequence text = childView.getText();

        if ( isEmpty(offsetOption) || ! (text instanceof Spanned) ) {
            return new ArrayList<>();
        }

        int offset = offsetOption.getOrElse(0);

        return asList( ((Spanned) text).getSpans(offset, offset, spanClass));
    }

    private Option<Integer> findOffsetForPosition(float x, float y) {

        if (childView == null || childView.getLayout() == null) {
            return none();
        }

        Layout layout = this.childView.getLayout();
        int line = layout.getLineForVertical((int) y);

        return option(layout.getOffsetForHorizontal(line, x));
    }

    private boolean needsPageNumberCalculation() {
        if ( ! configuration.isShowPageNumbers() ) {
            return false;
        }

        Option<List<List<Integer>>> offsets = configuration
                .getPageOffsets(fileName);

        return isEmpty( offsets ) ||
                offsets.unsafeGet().size() == 0;
    }

    //-------------------

    public void pageDown() {
        strategy.pageDown();
        progressUpdate();
    }

    public void pageUp() {
        strategy.pageUp();
        progressUpdate();
    }

    public void highlightClicked(HighLight highLight) {
        // TODO
    }
    public Book getBook() {
        return book;
    }



    TextView getInnerView() {
        return childView;
    }

    public Spine getSpine() {
        return this.spine;
    }

    public void setEnableScrolling(boolean enableScrolling) {

        if (this.strategy == null
                || this.strategy.isScrolling() != enableScrolling) {

            int pos = -1;
            boolean wasNull = true;

            Spanned text = null;

            if (this.strategy != null) {
                pos = this.strategy.getTopLeftPosition();
                text = this.strategy.getText().unsafeGet();
                this.strategy.clearText();
                wasNull = false;
            }

            if (enableScrolling) {
                this.strategy = scrollingStrategy;
            } else {
                this.strategy = fixedPagesStrategy;
            }

            strategy.setBookView(this);

            if (!wasNull) {
                this.strategy.setPosition(pos);
            }

            if (text != null && text.length() > 0) {
                this.strategy.loadText(text);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (strategy.isScrolling()) {
            return super.onTouchEvent(ev);
        } else {
            return childView.onTouchEvent(ev);
        }
    }

    // TODO media play --> TextView update
    public void navigateTo(int index, int currentDuration) {

    }

    public void goBackInHistory() {

        if (this.prevIndex == this.getIndex()) {
            strategy.setPosition(prevPos);

            this.storedAnchor = null;
            this.prevIndex = -1;
            this.prevPos = -1;

            restorePosition();

        } else {
            this.strategy.clearText();
            this.spine.navigateByIndex(this.prevIndex);
            strategy.setPosition(this.prevPos);

            this.storedAnchor = null;
            this.prevIndex = -1;
            this.prevPos = -1;

            loadText();
        }
    }

    public void clear() {
        this.childView.setText("");
        this.storedAnchor = null;
        this.storedIndex = -1;
        this.book = null;
        this.fileName = null;

        this.strategy.reset();
    }

    /**
     *  Params is string index : like '1-2-0';
     */
    private class LoadTextTask extends
            AsyncTask<String, BookReadPhase, Option<Spanned>> {

        private String name;

        private String searchTerm = null;

        public LoadTextTask() {

        }

        LoadTextTask(String searchTerm) {
            this.searchTerm = searchTerm;
        }

        public Option<Spanned> doInBackground(String... resources) {

            publishProgress(BookReadPhase.START);

            try {

                this.name = spine.getCurrentTitle().getOrElse("");


                publishProgress(BookReadPhase.PARSE_TEXT);

                Spannable result = textLoader.getText(resources[0]);

                //Clear any old highlighting spans

                SearchResultSpan[] spans = result.getSpans(0, result.length(), SearchResultSpan.class);
                for ( BackgroundColorSpan span: spans ) {
                    result.removeSpan(span);
                }

                // Highlight search results (if any)
                if ( searchTerm != null ) {
                    Pattern pattern = Pattern.compile(Pattern.quote((searchTerm)),Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(result);

                    while ( matcher.find() ) {
                        result.setSpan(new SearchResultSpan(),
                                matcher.start(), matcher.end(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }

                //If the view isn't ready yet, wait a bit.
//                while ( getInnerView().getWidth() == 0 ) {
//                    Thread.sleep(100);
//                }

                strategy.loadText(result);

                return option(result);
            } catch (Exception | OutOfMemoryError io ) {
                LOG.error( "Error loading text", io );

                //FIXME: actually use this error
                //this.error = String.format( getContext().getString(R.string.could_not_load),
                //      io.getMessage());

                //this.error = getContext().getString(R.string.out_of_memory);
            }

            return none();
        }

        public void doOnProgressUpdate(BookReadPhase... values) {

            BookReadPhase phase = values[0];

            switch (phase) {
                case START:
                    parseEntryStart(getIndex());
                    break;
                case PARSE_TEXT:
                    fireRenderingText();
                    break;
                case DONE:
                    parseEntryComplete(this.name);
                    break;
            }
        }

        @Override
        protected void onProgressUpdate(BookReadPhase... values) {
           doOnProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Option<Spanned> spanneds) {
            doOnPostExecute(spanneds);
        }

        public void doOnPostExecute(Option<Spanned> result) {

            restorePosition();
            strategy.updateGUI();
            progressUpdate();

            onProgressUpdate(BookReadPhase.DONE);

            /**
             * This is a hack for scrolling not updating to the right position
             * on Android 4+
             */
            if ( strategy.isScrolling() ) {
                scrollHandler.postDelayed( BookView.this::restorePosition, 100 );
            }
        }
    }


    private static enum BookReadPhase {
        START, OPEN_FILE, PARSE_TEXT, DONE
    }

    @SuppressLint("ParcelCreator")
    private static class SearchResultSpan extends BackgroundColorSpan {
        public SearchResultSpan() {
            super( Color.YELLOW );
        }
    }


}
