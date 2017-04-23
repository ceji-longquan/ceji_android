package com.lqtemple.android.lqbookreader.read;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;

import com.lqtemple.android.lqbookreader.Configuration;
import com.lqtemple.android.lqbookreader.DensityUtil;
import com.lqtemple.android.lqbookreader.StaticLayoutFactory;
import com.lqtemple.android.lqbookreader.model.Book;
import com.lqtemple.android.lqbookreader.model.Content;
import com.lqtemple.android.lqbookreader.model.PageIndex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sundxing on 16/12/25.
 */

public class PageCounter {
    private static final String TAG = PageCounter.class.getSimpleName();
    private final Context mContext;
    private Book mBook;
    private BookView mBookView;

    private final List<PageIndex> mPageIndices = new ArrayList<>();
    private SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
    private Bitmap mFakeBitmapForMeasure;

    /**
     * Leading margin of paragraph.
     */
    private int mLeadingMargin;

    private float mWidth;

    public Spanned getSpannedText() {
        return spannableStringBuilder;
    }

    private List<Spanned> mSpannedChapterText = new ArrayList<>();

    public PageCounter(Context context) {
        mContext = context.getApplicationContext();
        mWidth = PagetSizeConfig.getPageWidth(mContext);
        mFakeBitmapForMeasure = Bitmap.createBitmap((int)mWidth, (int)(mWidth *  0.66), Bitmap.Config.RGB_565);
    }

    public void setBookView(BookView bookView) {
        mBookView = bookView;
    }

    public List<PageIndex> calcuPageNumber() {

        preCalcu();
        // TODO Paragraph style
        //TODO offset 全文本offset
        // TODO 当文字变更是重新计算
        if (!mPageIndices.isEmpty()) {
            return mPageIndices;
        }

        spannableStringBuilder.clear();
        for (List<Content> chapter : mBook.getChapters() ) {

            for (Content content : chapter) {
                int start = spannableStringBuilder.length();
                spannableStringBuilder.append(content.getText());
                int end = spannableStringBuilder.length();

                List<String> urls = content.getImageUrl();
                int[] imageStartOffsets = content.getImageStartOffsets();
                int[] imageEndOffsets = content.getImageEndOffsets();
                for (int i = 0; i < urls.size(); i++) {
                    Log.d(TAG, "image add :" + imageStartOffsets[i]);
                    spannableStringBuilder.setSpan(new ClickableImageSpan(mContext, urls.get(i), mFakeBitmapForMeasure),
                            start + imageStartOffsets[i], start + imageEndOffsets[i], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                spannableStringBuilder.setSpan(new AbsoluteSizeSpan(getTextSize(content)),
                        start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            calcuPageOffsetOneChapter(chapter);
            mSpannedChapterText.add(new SpannableString(spannableStringBuilder));
            spannableStringBuilder.clear();
        }

        for (Spanned spanned : mSpannedChapterText) {
            spannableStringBuilder.append(spanned);
        }
        return mPageIndices;

    }

    private void preCalcu() {
        mLeadingMargin = (int) PagetSizeConfig.getPaint().measureText(Configuration.getInstance().getLeadingMarginText());
        Log.d(TAG, "Leading margin : " + mLeadingMargin);
    }

    private void calcuPageOffsetOneChapter(List<Content> chapter) {

        int boundedWidth = (int) mWidth;
        StaticLayout layout = StaticLayoutFactory.create(spannableStringBuilder,
                PagetSizeConfig.getPaint(), boundedWidth, PagetSizeConfig.getLineSpacing());
        layout.draw(new Canvas());

        int pageHeight = (int) PagetSizeConfig.getPageHeight(mBookView.getContext());
        Log.d(TAG, "Calcu page width = " + boundedWidth + ", height = " + pageHeight);


        int totalLines = layout.getLineCount();
        int topLineNextPage = -1;
        int pageStartOffset = 0;

        while (topLineNextPage < totalLines - 1) {

            Log.d(TAG, "Processing line " + topLineNextPage + " / " + totalLines);

            int topLine = layout.getLineForOffset(pageStartOffset);
            topLineNextPage = layout.getLineForVertical(layout.getLineTop(topLine) + pageHeight);

            Log.d(TAG, "topLine " + topLine + " / " + topLineNextPage);
            if (topLineNextPage == topLine) {
                //If lines are bigger than can fit on a page
                topLineNextPage = topLine + 1;
            }

            int pageEnd = layout.getLineEnd(topLineNextPage - 1);

            Log.d(TAG, "pageStartOffset=" + pageStartOffset + ", pageEnd=" + pageEnd);

            if (pageEnd > pageStartOffset) {
                if (spannableStringBuilder.subSequence(pageStartOffset, pageEnd).toString().trim().length() > 0) {
                    PageIndex pageIndex = findPageIndexByOffset(chapter, pageStartOffset);
                    Log.d(TAG, "add pageIndex :" + pageIndex.getIndex());

                    mPageIndices.add(pageIndex);
                }
                pageStartOffset = layout.getLineStart(topLineNextPage);
            }
        }

    }

    private PageIndex findPageIndexByOffset(List<Content> chapter, int pageStartOffset) {
        for (Content content : chapter) {
            int contentOffset = content.getOffset();
            if (pageStartOffset < contentOffset) continue;
            if (pageStartOffset - contentOffset < content.getText().length()) {
                PageIndex pageIndex = new PageIndex();
                pageIndex.setIndex(content.getIndex());
                pageIndex.setOffset(pageStartOffset - contentOffset);
                int totalOffset = pageStartOffset - contentOffset + content.getTotalOffset();
                pageIndex.setTotalOffset(totalOffset);
                pageIndex.setSpanedTextOffset(pageStartOffset);
                return pageIndex;
            }
        }

        throw new IllegalStateException("can not find page in chapter!");
    }


    private int getTextSize(Content content) {
        // 不同层级的标题大小样式会不同
        int normalSize = DensityUtil.sp2px(mContext, 14f);
        switch (content.getTypeEnum()) {
            case Title:
                int depth = getContentDepth(content);
                int extraSize = DensityUtil.sp2px(mContext, 20f) / Math.max(1, depth - 1);
                normalSize = normalSize + extraSize;
                break;
            case Content:
            case Desc:
                break;
        }
        return normalSize;
    }

    private int getContentDepth(Content content) {
        return content.getIndex().split("-").length;
    }

    public void setBook(Book book) {
        this.mBook = book;
    }

    public int getPageNumberFor(int offset) {
        int total = mPageIndices.size() - 1;
        for (int i = 0; i < total; i++) {
            if (mPageIndices.get(i).getTotalOffset() <= offset && mPageIndices.get(i + 1).getTotalOffset() >= offset) {
                return i + 1;
            }
        }
        return -1;
    }


    private class PlaceHolderDrawable extends Drawable {

        @Override
        public int getIntrinsicHeight() {
            return (int) PagetSizeConfig.getPageWidth(mContext);
        }

        @Override
        public int getIntrinsicWidth() {
            return (int) PagetSizeConfig.getPageWidth(mContext);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {

        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
