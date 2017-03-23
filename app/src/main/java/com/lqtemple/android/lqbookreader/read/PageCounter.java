package com.lqtemple.android.lqbookreader.read;

import android.content.Context;
import android.graphics.Canvas;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;

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
    private final List<PageIndex> mPageIndices = new ArrayList<>();
    private StaticLayoutFactory mStaticLayoutFactory;
    private BookView mBookView;
    private SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();


    public Spanned getSpannedText() {
        return spannableStringBuilder;
    }

    private List<Spanned> mSpannedChapterText = new ArrayList<>();

    public PageCounter(Context context) {
        mContext = context.getApplicationContext();
        mStaticLayoutFactory = new StaticLayoutFactory();
    }

    public void setBookView(BookView bookView) {
        mBookView = bookView;
    }

    public List<PageIndex> caculPageNumber() {

        // TODO Paragraph style
        //TODO offset 全文本offset
        // TODO 当文字变更是重新计算
        if (!mPageIndices.isEmpty()) {
            return mPageIndices;
        }
        int contentIndex = 0;
        int paraIndex = 0;

        spannableStringBuilder.clear();
        for (List<Content> chapter : mBook.getChapters() ) {

            for (Content content : chapter) {

                if (content.isChapterStart()) {

                    paraIndex++;
                }

                int start = spannableStringBuilder.length();
                spannableStringBuilder.append(content.getText());
                int end = spannableStringBuilder.length();
                spannableStringBuilder.setSpan(new AbsoluteSizeSpan(getTextSize(content)),
                        start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            cacuPageOffsetOneChapter(chapter);
            mSpannedChapterText.add(new SpannableString(spannableStringBuilder));
            spannableStringBuilder.clear();
        }

        for (Spanned spanned : mSpannedChapterText) {
            spannableStringBuilder.append(spanned);
        }
        return mPageIndices;

    }

    private void cacuPageOffsetOneChapter(List<Content> chapter) {

        int boundedWidth = (int) PagetSizeConfig.getPageWidth(mBookView.getContext());
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
                    Log.d(TAG, "add pageIndex :" + pageIndex.getParaIndex());

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
                pageIndex.setParaIndex(content.getIndex());
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
}
