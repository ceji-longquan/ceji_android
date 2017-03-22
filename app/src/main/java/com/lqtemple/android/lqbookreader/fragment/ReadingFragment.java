/*
 * Copyright (C) 2011 Alex Kuiper
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

package com.lqtemple.android.lqbookreader.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.lqtemple.android.lqbookreader.BookProgress;
import com.lqtemple.android.lqbookreader.Configuration;
import com.lqtemple.android.lqbookreader.R;
import com.lqtemple.android.lqbookreader.Singleton;
import com.lqtemple.android.lqbookreader.TextUtil;
import com.lqtemple.android.lqbookreader.activity.ReadingActivity;
import com.lqtemple.android.lqbookreader.animation.Animations;
import com.lqtemple.android.lqbookreader.animation.Animator;
import com.lqtemple.android.lqbookreader.animation.PageCurlAnimator;
import com.lqtemple.android.lqbookreader.animation.PageTimer;
import com.lqtemple.android.lqbookreader.animation.RollingBlindAnimator;
import com.lqtemple.android.lqbookreader.annotation.InjectUtils;
import com.lqtemple.android.lqbookreader.annotation.InjectView;
import com.lqtemple.android.lqbookreader.bookmark.Bookmark;
import com.lqtemple.android.lqbookreader.bookmark.BookmarkDatabaseHelper;
import com.lqtemple.android.lqbookreader.dto.HighLight;
import com.lqtemple.android.lqbookreader.model.Book;
import com.lqtemple.android.lqbookreader.read.BookView;
import com.lqtemple.android.lqbookreader.read.BookViewListener;
import com.lqtemple.android.lqbookreader.read.HighlightManager;
import com.lqtemple.android.lqbookreader.read.SelectedWord;
import com.lqtemple.android.lqbookreader.read.TextLoader;
import com.lqtemple.android.lqbookreader.read.TextSelectionCallback;
import com.lqtemple.android.lqbookreader.read.ToastFactory;
import com.lqtemple.android.lqbookreader.view.AnimatedImageView;
import com.lqtemple.android.lqbookreader.view.NavGestureDetector;
import com.lqtemple.android.lqbookreader.view.NavigationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jedi.functional.Command;
import jedi.option.Option;

import static com.lqtemple.android.lqbookreader.Configuration.AnimationStyle;
import static com.lqtemple.android.lqbookreader.Configuration.ReadingDirection;
import static com.lqtemple.android.lqbookreader.Configuration.ScrollStyle;
import static com.lqtemple.android.lqbookreader.PlatformUtil.isIntentAvailable;
import static jedi.functional.FunctionalPrimitives.isEmpty;
import static jedi.option.Options.none;
import static jedi.option.Options.option;
public class ReadingFragment extends Fragment implements
        BookViewListener, TextSelectionCallback {

    private static final String POS_KEY = "offset:";
    private static final String IDX_KEY = "index:";

    protected static final int REQUEST_CODE_GET_CONTENT = 2;

    public static final String PICK_RESULT_ACTION = "colordict.intent.action.PICK_RESULT";
    public static final String EXTRA_QUERY = "EXTRA_QUERY";
    public static final String EXTRA_FULLSCREEN = "EXTRA_FULLSCREEN";
    public static final String EXTRA_HEIGHT = "EXTRA_HEIGHT";
    public static final String EXTRA_GRAVITY = "EXTRA_GRAVITY";
    public static final String EXTRA_MARGIN_LEFT = "EXTRA_MARGIN_LEFT";

    private static final Logger LOG = LoggerFactory
            .getLogger("ReadingFragment");

    private Context context;

    @InjectView(R.id.mainContainer)
    private ViewSwitcher viewSwitcher;

    @InjectView(R.id.bookView)
    private BookView bookView;

    @InjectView(R.id.myTitleBarTextView)
    private TextView titleBar;

    @InjectView(R.id.myTitleBarLayout)
    private RelativeLayout titleBarLayout;

    @InjectView(R.id.mediaPlayerLayout)
    private LinearLayout mediaLayout;

    @InjectView(R.id.titleProgress)
    private SeekBar progressBar;

    @InjectView(R.id.percentageField)
    private TextView percentageField;

    @InjectView(R.id.authorField)
    private TextView authorField;

    @InjectView(R.id.dummyView)
    private AnimatedImageView dummyView;

    @InjectView(R.id.mediaProgress)
    private SeekBar mediaProgressBar;

    @InjectView(R.id.pageNumberView)
    private TextView pageNumberView;

    @InjectView(R.id.playPauseButton)
    private ImageButton playPauseButton;

    @InjectView(R.id.stopButton)
    private ImageButton stopButton;

    @InjectView(R.id.nextButton)
    private ImageButton nextButton;

    @InjectView(R.id.prevButton)
    private ImageButton prevButton;

    @InjectView(R.id.wordView)
    private TextView wordView;

    // TODO init those members
    private TelephonyManager telephonyManager;


    private PowerManager powerManager;

    private AudioManager audioManager;
    private NotificationManager notificationManager;

    private Configuration config;
    private TextLoader textLoader;
    private HighlightManager highlightManager;
    private BookmarkDatabaseHelper bookmarkDatabaseHelper;

    private ProgressDialog waitDialog;


    private boolean ttsAvailable = false;

    private String bookTitle;
    private String titleBase;

    private String fileName;
    private int progressPercentage;

    private String language = "en";

    private int currentPageNumber = -1;

    private SelectedWord selectedWord;


    private static enum Orientation {
        HORIZONTAL, VERTICAL
    }

    private static class SavedConfigState {
        private boolean brightness;
        private boolean stripWhiteSpace;
        private String fontName;
        private String serifFontName;
        private String sansSerifFontName;
        private boolean usePageNum;
        private boolean fullscreen;
        private int vMargin;
        private int hMargin;
        private int textSize;
        private boolean scrolling;
        private boolean allowStyling;
        private boolean allowColoursFromCSS;
    }

    private SavedConfigState savedConfigState = new SavedConfigState();

    private Handler uiHandler;
    private Handler backgroundHandler;

    private Toast brightnessToast;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getContext();
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        config = Configuration.getInstance();
        textLoader = Singleton.getInstance(TextLoader.class);
        highlightManager = Singleton.getInstance(HighlightManager.class);
        highlightManager.setConfig(config);

        bookmarkDatabaseHelper  = new BookmarkDatabaseHelper(context);
        super.onCreate(savedInstanceState);

        // Restore preferences
        this.uiHandler = new Handler();
        HandlerThread bgThread = new HandlerThread("background");
        bgThread.start();
        this.backgroundHandler = new Handler(bgThread.getLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&  config.isFullScreenEnabled() ) {
            return inflater.inflate(R.layout.fragment_reading_fs, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_reading, container, false);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InjectUtils.bind(this);
        setHasOptionsMenu(true);

        this.bookView.init();

        this.progressBar.setFocusable(true);
        this.progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private int seekValue;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bookView.navigateToPercentage(this.seekValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar,
                                          int progress, boolean fromUser) {
                if (fromUser) {
                    seekValue = progress;
                    percentageField.setText(progress + "% ");
                }
            }
        });

        this.mediaProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar,
                                          int progress, boolean fromUser) {
                if (fromUser) {
//                    seekToPointInPlayback(progress);progress
                }
            }
        });

        this.bookView.addListener(this);
//        this.bookView.setTextSelectionCallback(this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        displayPageNumber(-1); // Initializes the pagenumber view properly

        final GestureDetector gestureDetector = new GestureDetector(context,
                new NavGestureDetector(bookView, this, metrics));

        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };


        this.viewSwitcher.setOnTouchListener(gestureListener);
        this.bookView.setOnTouchListener(gestureListener);
        this.dummyView.setOnTouchListener(gestureListener);

        registerForContextMenu(bookView);
        saveConfigState();

        Intent intent = getActivity().getIntent();
        String file = null;

        if ( intent.getData() != null) {
            file = intent.getData().getPath();
        }

        if (file == null) {
            file = "";
            file = config.getLastOpenedFile();
        }

        updateFromPrefs();
        updateFileName(savedInstanceState, file);

        if ("".equals(fileName) || ! new File(fileName).exists() ) {

            LOG.info( "Requested to open file " + fileName + ", which doesn't seem to exist. " +
                    "Switching back to the library.");

           Toast.makeText(getContext(), "File not exits!", Toast.LENGTH_SHORT).show();
            return;

        } else {

            if (savedInstanceState == null && config.isSyncEnabled()) {
//                new DownloadProgressTask().execute();
            } else {
                bookView.restore();
            }
        }

    }

    public void saveConfigState() {
        // Cache old settings to check if we'll need a restart later
        savedConfigState.brightness = config.isBrightnessControlEnabled();
        savedConfigState.stripWhiteSpace = config.isStripWhiteSpaceEnabled();

        savedConfigState.usePageNum = config.isShowPageNumbers();
        savedConfigState.fullscreen = config.isFullScreenEnabled();

        savedConfigState.hMargin = config.getHorizontalMargin();
        savedConfigState.vMargin = config.getVerticalMargin();

        savedConfigState.textSize = config.getTextSize();
        savedConfigState.fontName = config.getDefaultFontFamily().getName();
        savedConfigState.serifFontName = config.getSerifFontFamily().getName();
        savedConfigState.sansSerifFontName = config.getSansSerifFontFamily().getName();

        savedConfigState.scrolling = config.isScrollingEnabled();
        savedConfigState.allowStyling = config.isAllowStyling();
        savedConfigState.allowColoursFromCSS = config.isUseColoursFromCSS();

    }

    /*
     * @see roboguice.activity.RoboActivity#onPause()
     */
    @Override
    public void onPause() {
        LOG.debug("onPause() called.");

        saveReadingPosition();
        super.onPause();
    }

    private void printScreenAndCallState(String calledFrom) {
        boolean isScreenOn = powerManager.isScreenOn();

        if (!isScreenOn) {
            LOG.debug(calledFrom + ": Screen is off");
        } else {
            LOG.debug(calledFrom + ": Screen is on");
        }

        int phoneState = telephonyManager.getCallState();

        if ( phoneState == TelephonyManager.CALL_STATE_RINGING || phoneState == TelephonyManager.CALL_STATE_OFFHOOK ) {
            LOG.debug(calledFrom + ": Detected call activity");
        } else {
            LOG.debug(calledFrom + ": No active call.");
        }
    }

    private void playBeep( boolean error ) {

        if ( ! isAdded() ) {
            return;
        }

        try {
            MediaPlayer beepPlayer = new MediaPlayer();

            String file = "beep.mp3";

            if ( error ) {
                file = "error.mp3";
            }

            AssetFileDescriptor descriptor = context.getAssets().openFd(file);
            beepPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            beepPlayer.prepare();

            beepPlayer.start();
        } catch (Exception io) {
            //We'll manage without the beep :)
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shutdown();
        this.closeWaitDialog();
    }

    private void shutdown() {
        // TODO release Player and other resources
    }


    private void updateFileName(Bundle savedInstanceState, String fileName) {

        this.fileName = fileName;

        int lastPos = config.getLastPosition(fileName);
        int lastIndex = config.getLastIndex(fileName);

        if (savedInstanceState != null) {
            lastPos = savedInstanceState.getInt(POS_KEY, lastPos);
            lastIndex = savedInstanceState.getInt(IDX_KEY, lastIndex);
        }

        this.bookView.setFileName(fileName);
        this.bookView.setPosition(lastPos);
        this.bookView.setIndex(lastIndex);

        config.setLastOpenedFile(fileName);
    }

    @Override
    public void progressUpdate(int progressPercentage, int pageNumber,
                               int totalPages) {

        if ( ! isAdded() || getActivity() == null ) {
            return;
        }

        this.currentPageNumber = pageNumber;

        // Work-around for calculation errors and weird values.
        if (progressPercentage < 0 || progressPercentage > 100) {
            return;
        }

        this.progressPercentage = progressPercentage;

        if (config.isShowPageNumbers() && pageNumber > 0) {
            percentageField.setText("" + progressPercentage + "%  "
                    + pageNumber + " / " + totalPages);
            displayPageNumber(pageNumber);

        } else {
            percentageField.setText("" + progressPercentage + "%");
        }

        this.progressBar.setProgress(progressPercentage);
        this.progressBar.setMax(100);
    }

    private void displayPageNumber(int pageNumber) {

        String pageString;

        if ( !config.isScrollingEnabled() && pageNumber > 0) {
            pageString = Integer.toString(pageNumber) + "\n";
        } else {
            pageString = "\n";
        }

        pageNumberView.setTextColor(config.getTextColor());
        pageNumberView.setTextSize(config.getTextSize());

//        pageNumberView.setTypeface(config.getDefaultFontFamily().getDefaultTypeface());

        pageNumberView.setText(pageString);
        pageNumberView.setGravity(Gravity.CENTER);
        pageNumberView.invalidate();
    }

    private void updateFromPrefs() {

        FragmentActivity activity = getActivity();

        if ( activity == null ) {
            return;
        }

        bookView.setTextSize(config.getTextSize());

        int marginH = config.getHorizontalMargin();
        int marginV = config.getVerticalMargin();

//        this.textLoader.setFontFamily(config.getDefaultFontFamily());
//        this.bookView.setFontFamily(config.getDefaultFontFamily());
//        this.textLoader.setSansSerifFontFamily(config.getSansSerifFontFamily());
//        this.textLoader.setSerifFontFamily(config.getSerifFontFamily());

        bookView.setHorizontalMargin(marginH);
        bookView.setVerticalMargin(marginV);

        if (!isAnimating()) {
            bookView.setEnableScrolling(config.isScrollingEnabled());
        }


        bookView.setLineSpacing(config.getLineSpacing());

        if (config.isFullScreenEnabled()) {
            //TODO
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        } else {
            activity.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ) {
            if ( config.isFullScreenEnabled() ) {
                bookView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            }
            if ( config.isDimSystemUI() ) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            }
        }

        if (config.isKeepScreenOn()) {
            activity.getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        restoreColorProfile();

        // Check if we need a restart
        if (config.isFullScreenEnabled() != savedConfigState.fullscreen
                || config.isShowPageNumbers() != savedConfigState.usePageNum
                || config.isBrightnessControlEnabled() != savedConfigState.brightness
                || config.isStripWhiteSpaceEnabled() != savedConfigState.stripWhiteSpace
                || !config.getDefaultFontFamily().getName().equalsIgnoreCase(savedConfigState.fontName)
                || !config.getSerifFontFamily().getName().equalsIgnoreCase(savedConfigState.serifFontName)
                || !config.getSansSerifFontFamily().getName().equalsIgnoreCase(savedConfigState.sansSerifFontName)
                || config.getHorizontalMargin() != savedConfigState.hMargin
                || config.getVerticalMargin() != savedConfigState.vMargin
                || config.getTextSize() != savedConfigState.textSize
                || config.isScrollingEnabled() != savedConfigState.scrolling
                || config.isAllowStyling() != savedConfigState.allowStyling
                || config.isUseColoursFromCSS() != savedConfigState.allowColoursFromCSS ) {

            textLoader.invalidateCachedText();
            restartActivity();
        }

        Configuration.OrientationLock orientation = config
                .getScreenOrientation();

        switch (orientation) {
            case PORTRAIT:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case LANDSCAPE:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case REVERSE_LANDSCAPE:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); // Android 2.3+ value
                break;
            case REVERSE_PORTRAIT:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT); // Android 2.3+ value
                break;
            default:
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }


    private void restartActivity() {

        onStop();

        //Clear any cached text.
        textLoader.closeCurrentBook();
        Intent intent = new Intent(context, ReadingActivity.class);
        intent.setData(Uri.parse(this.fileName));
        startActivity(intent);
//        this.libraryService.close();

        Activity activity = getActivity();

        if ( activity != null ) {
            activity.finish();
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            hideTitleBar();
            updateFromPrefs();
        } else {
            getActivity().getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        return bookView.onTouchEvent(event);
    }

    @Override
    public void bookOpened(final Book book) {

        Activity activity = getActivity();

        if ( activity == null ) {
            return;
        }

        this.language = this.bookView.getBook().getLanguage();
        LOG.debug("Got language for book: " + language );

        this.bookTitle = book.getTitle();

        this.config.setLastReadTitle(this.bookTitle);

        this.titleBase = this.bookTitle;
        activity.setTitle(titleBase);
        this.titleBar.setText(titleBase);

        this.authorField.setText(book.getAuthorName());

        backgroundHandler.post( () -> {
            try {
//                libraryService.storeBook(fileName, book, true,
//                        config.getCopyToLibraryOnScan());
            } catch (Exception io) {
                LOG.error("Copy to library failed.", io);
            }
        });

        updateFromPrefs();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

    }

    @Override
    public void highLight(int from, int to, String selectedText) {

        int pageStart = bookView.getStartOfCurrentPage();

        String text = TextUtil.shortenText( selectedText );

        this.highlightManager.registerHighlight(fileName, text, bookView.getIndex(),
                pageStart + from, pageStart + to);

        bookView.update();
    }

    private void showHighlightEditDialog( final HighLight highLight ) {
        final AlertDialog.Builder editalert = new AlertDialog.Builder( context );

        editalert.setTitle(R.string.text_note);
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);
        editalert.setView(input);
        input.setText( highLight.getTextNote() );

        editalert.setPositiveButton(R.string.save_note, (dialog, which) -> {
            highLight.setTextNote(input.getText().toString());
            bookView.update();
            highlightManager.saveHighLights();
        });

        editalert.setNegativeButton(android.R.string.cancel, (dialog,which) -> {} );

        editalert.setNeutralButton(R.string.clear_note, (dialog, which) -> {
            highLight.setTextNote(null);
            bookView.update();
            highlightManager.saveHighLights();
        });

        editalert.show();
    }

    private void showHighlightColourDialog( final HighLight highLight ) {

//        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(
//                context, highLight.getColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
//            @Override
//            public void onCancel(AmbilWarnaDialog dialog) {
//                //do nothing.
//            }
//
//            @Override
//            public void onOk(AmbilWarnaDialog dialog, int color) {
//                highLight.setColor( color );
//                bookView.update();
//                highlightManager.saveHighLights();
//            }
//        });
//
//        ambilWarnaDialog.show();
    }


    @Override
    public boolean isDictionaryAvailable() {
        return isIntentAvailable(context, getDictionaryIntent());
    }

    @Override
    public void lookupDictionary(String text) {
        Intent intent = getDictionaryIntent();
        intent.putExtra(EXTRA_QUERY, text); // Search Query
        startActivityForResult(intent, 5);
    }

    private String getLanguageCode() {
        if ( this.language == null || this.language.equals("") || this.language.equalsIgnoreCase("und") ) {
            return Locale.getDefault().getLanguage();
        }

        return this.language;
    }

    @Override
    public void lookupWikipedia(String text) {

        openBrowser("http://" + getLanguageCode() + ".wikipedia.org/wiki/Special:Search?search="
                + URLEncoder.encode(text));
    }

    public void lookupWiktionary(String text) {
        openBrowser("http://" + getLanguageCode() + ".wiktionary.org/w/index.php?title=Special%3ASearch&search="
                + URLEncoder.encode(text));

    }

    @Override
    public void lookupGoogle(String text) {
        openBrowser("http://www.google.com/search?q=" + URLEncoder.encode(text));
    }

    private Intent getDictionaryIntent() {
        final Intent intent = new Intent(PICK_RESULT_ACTION);

        intent.putExtra(EXTRA_FULLSCREEN, false); //
        intent.putExtra(EXTRA_HEIGHT, 400); // 400pixel, if you don't specify,
        // fill_parent"
        intent.putExtra(EXTRA_GRAVITY, Gravity.BOTTOM);
        intent.putExtra(EXTRA_MARGIN_LEFT, 100);

        return intent;
    }

    private void openBrowser(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void restoreColorProfile() {

        this.bookView.setBackgroundColor(config.getBackgroundColor());
        this.viewSwitcher.setBackgroundColor(config.getBackgroundColor());

        this.bookView.setTextColor(config.getTextColor());
        this.bookView.setLinkColor(config.getLinkColor());

        int brightness = config.getBrightNess();

        if (config.isBrightnessControlEnabled()) {
            setScreenBrightnessLevel(brightness);
        }
    }

    private void setScreenBrightnessLevel(int level) {

        Activity activity = getActivity();

        if ( activity != null ) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.screenBrightness = (float) level / 100f;
            activity.getWindow().setAttributes(lp);
        }
    }

    @Override
    public void errorOnBookOpening(String errorMessage) {

        LOG.error(errorMessage);

        closeWaitDialog();

        ReadingActivity readingActivity = (ReadingActivity) getActivity();

        if ( readingActivity != null ) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(readingActivity);
            builder.setContentTitle(getString(R.string.app_name))
                    .setContentText(errorMessage)
                    .setSmallIcon(android.R.drawable.ic_menu_close_clear_cancel)
                    .setAutoCancel( true );

            builder.setTicker(errorMessage);
            PendingIntent pendingIntent = PendingIntent.getActivity( readingActivity, 0, new Intent(), 0);
            builder.setContentIntent( pendingIntent );

            notificationManager.notify( errorMessage.hashCode(), builder.build() );

        }
    }

    private ProgressDialog getWaitDialog() {

        if ( this.waitDialog == null ) {
            this.waitDialog = new ProgressDialog(context);
            this.waitDialog.setOwnerActivity(getActivity());
        }

        // This just consumes all key events and does nothing.
        this.waitDialog.setOnKeyListener( (dialog,keyCode,event) -> true );

        return this.waitDialog;
    }

    private void closeWaitDialog() {

        if ( waitDialog != null ) {
            this.waitDialog.dismiss();
            this.waitDialog = null;
        }
    }

    @Override
    public void parseEntryComplete( String name) {

        if (name != null && !name.equals(this.bookTitle)) {
            this.titleBase = this.bookTitle + " - " + name;
        } else {
            this.titleBase = this.bookTitle;
        }

        Activity activity = getActivity();

        if ( activity != null ) {

            activity.setTitle(this.titleBase);

            closeWaitDialog();
        }

    }

    @Override
    public void parseEntryStart(int entry) {

        if ( ! isAdded() || getActivity() == null ) {
            return;
        }


        this.viewSwitcher.clearAnimation();
        this.viewSwitcher.setBackgroundDrawable(null);
        restoreColorProfile();
        displayPageNumber(-1); //Clear page number

        ProgressDialog progressDialog = getWaitDialog();
        progressDialog.setMessage(getString( R.string.loading_wait));

        progressDialog.show();
    }

    @Override
    public void readingFile() {
        if ( isAdded() ) {
            this.getWaitDialog().setMessage(getString(R.string.opening_file));
        }
    }

    @Override
    public void renderingText() {
        if ( isAdded() ) {
            this.getWaitDialog().setMessage(getString(R.string.loading_text));
        }
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private boolean handleVolumeButtonEvent(KeyEvent event) {

        //Disable volume button handling during TTS
        if (!config.isVolumeKeyNavEnabled()) {
            return false;
        }

        Activity activity = getActivity();

        if ( activity == null ) {
            return false;
        }

        boolean invert = false;

        int rotation = Surface.ROTATION_0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            rotation = display.getRotation();
        }

        switch (rotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_90:
                invert = false;
                break;
            case Surface.ROTATION_180:
            case Surface.ROTATION_270:
                invert = true;
                break;
        }

        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return true;
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            if (invert) {
                pageDown(Orientation.HORIZONTAL);
            }
            else {
                pageUp(Orientation.HORIZONTAL);
            }
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (invert) {
                pageUp(Orientation.HORIZONTAL);
            } else {
                pageDown(Orientation.HORIZONTAL);
            }
        }

        return true;
    }

    public boolean dispatchMediaKeyEvent(KeyEvent event) {

        int action = event.getAction();
        int keyCode = event.getKeyCode();

        if ( audioManager.isMusicActive() ) {
            return false;
        }

        switch (keyCode) {

            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                return simulateButtonPress(action, R.id.playPauseButton, playPauseButton);

            case KeyEvent.KEYCODE_MEDIA_STOP:
                return simulateButtonPress(action, R.id.stopButton, stopButton );

            case KeyEvent.KEYCODE_MEDIA_NEXT:
                return simulateButtonPress(action, R.id.nextButton, nextButton );

            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                return simulateButtonPress(action, R.id.prevButton, prevButton );
        }

        return false;
    }

    private boolean simulateButtonPress(int action, int idToSend, ImageButton buttonToClick ) {
        if ( action == KeyEvent.ACTION_DOWN ) {
            buttonToClick.setPressed(true);
        } else {
            buttonToClick.setPressed(false);
        }

        buttonToClick.invalidate();
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {

        int action = event.getAction();
        int keyCode = event.getKeyCode();

        LOG.debug("Got key event: " + keyCode + " with action " + action);


        final int KEYCODE_NOOK_TOUCH_BUTTON_LEFT_TOP = 92;
        final int KEYCODE_NOOK_TOUCH_BUTTON_LEFT_BOTTOM = 93;
        final int KEYCODE_NOOK_TOUCH_BUTTON_RIGHT_TOP = 94;
        final int KEYCODE_NOOK_TOUCH_BUTTON_RIGHT_BOTTOM = 95;

        boolean nook_touch_up_press = false;

        if (isAnimating() && action == KeyEvent.ACTION_DOWN) {
            stopAnimating();
            return true;
        }

		/*
		 * Tricky bit of code here: if we are NOT running TTS,
		 * we want to be able to start it using the play/pause button.
		 *
		 * When we ARE running TTS, we'll get every media event twice:
		 * once through the receiver and once here if focused.
		 *
		 * So, we only try to read media events here if tts is running.
		 */
        if (dispatchMediaKeyEvent(event)) {
            return true;
        }

        LOG.debug("Key event is NOT a media key event.");

        switch (keyCode) {

            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                return handleVolumeButtonEvent(event);

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (action == KeyEvent.ACTION_DOWN) {
                    pageDown(Orientation.HORIZONTAL);
                }

                return true;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (action == KeyEvent.ACTION_DOWN) {
                    pageUp(Orientation.HORIZONTAL);
                }

                return true;

            case KeyEvent.KEYCODE_BACK:
                if (action == KeyEvent.ACTION_DOWN) {

                    if (titleBarLayout.getVisibility() == View.VISIBLE) {
                        hideTitleBar();
                        updateFromPrefs();
                        return true;
                    } else if (bookView.hasPrevPosition()) {
                        bookView.goBackInHistory();
                        return true;
                    }
                }

                return false;


            case KEYCODE_NOOK_TOUCH_BUTTON_LEFT_TOP:
            case KEYCODE_NOOK_TOUCH_BUTTON_RIGHT_TOP:
                nook_touch_up_press = true;
            case KEYCODE_NOOK_TOUCH_BUTTON_LEFT_BOTTOM:
            case KEYCODE_NOOK_TOUCH_BUTTON_RIGHT_BOTTOM:
                if(action == KeyEvent.ACTION_UP)
                    return false;
                if(nook_touch_up_press == config.isNookUpButtonForward())
                    pageDown(Orientation.HORIZONTAL);
                else
                    pageUp(Orientation.HORIZONTAL);
                return true;
        }


        LOG.debug("Not handling key event: returning false.");
        return false;
    }

    private boolean isAnimating() {
        Animator anim = dummyView.getAnimator();
        return anim != null && !anim.isFinished();
    }

    private void startAutoScroll() {

        if (viewSwitcher.getCurrentView() == this.dummyView) {
            viewSwitcher.showNext();
        }

        this.viewSwitcher.setInAnimation(null);
        this.viewSwitcher.setOutAnimation(null);

        bookView.setKeepScreenOn(true);

        ScrollStyle style = config.getAutoScrollStyle();

        try {
            if (style == ScrollStyle.ROLLING_BLIND) {
                prepareRollingBlind();
            } else {
                preparePageTimer();
            }

            viewSwitcher.showNext();

            uiHandler.post(this::doAutoScroll);
        } catch ( IllegalStateException is ) {
            LOG.error( "Failed to start autoscroll", is );
        }
    }

    private void doAutoScroll() {

        if (dummyView.getAnimator() == null) {
            LOG.debug("BookView no longer has an animator. Aborting rolling blind.");
            stopAnimating();
        } else {

            Animator anim = dummyView.getAnimator();

            if (anim.isFinished()) {
                startAutoScroll();
            } else {
                anim.advanceOneFrame();
                dummyView.invalidate();

                uiHandler.postDelayed(this::doAutoScroll, anim.getAnimationSpeed() * 2);
            }
        }
    }

    private void prepareRollingBlind() {

        Option<Bitmap> before = getBookViewSnapshot();

        bookView.pageDown();
        Option<Bitmap> after = getBookViewSnapshot();

        if ( isEmpty(before) || isEmpty( after ) ) {
            throw new IllegalStateException("Could not initialize images");
        }

        RollingBlindAnimator anim = new RollingBlindAnimator();
        anim.setAnimationSpeed(config.getScrollSpeed());

        before.forEach((Command<? super Bitmap>) anim::setBackgroundBitmap);
        after.forEach((Command<? super Bitmap>) anim::setForegroundBitmap);

        dummyView.setAnimator(anim);
    }

    private void preparePageTimer() {
        bookView.pageDown();
        Option<Bitmap> after = getBookViewSnapshot();

        if ( isEmpty( after ) ) {
            throw new IllegalStateException( "Could not initialize view" );
        }

        after.forEach((Command<? super Bitmap>) img -> {
            PageTimer timer = new PageTimer(img, pageNumberView.getHeight());

            timer.setSpeed(config.getScrollSpeed());

            dummyView.setAnimator(timer);
        });
    }

    private void doPageCurl(boolean flipRight, boolean pageDown) {

        if (isAnimating() || bookView == null ) {
            return;
        }

        this.viewSwitcher.setInAnimation(null);
        this.viewSwitcher.setOutAnimation(null);

        if (viewSwitcher.getCurrentView() == this.dummyView) {
            viewSwitcher.showNext();
        }

        Option<Bitmap> before = getBookViewSnapshot();

        this.pageNumberView.setVisibility(View.GONE);

        PageCurlAnimator animator = new PageCurlAnimator(flipRight);

        // Pagecurls should only take a few frames. When the screen gets
        // bigger, so do the frames.
        animator.SetCurlSpeed(bookView.getWidth() / 8);

        animator.setBackgroundColor(config.getBackgroundColor());

        if (pageDown) {
            bookView.pageDown();
        } else {
            bookView.pageUp();
        }

        Option<Bitmap> after = getBookViewSnapshot();

        //The animator knows how to handle nulls, so
        //we can use unsafeGet() here.
        if ( flipRight ) {
            animator.setBackgroundBitmap(after.unsafeGet());
            animator.setForegroundBitmap(before.unsafeGet());
        } else {
            animator.setBackgroundBitmap(before.unsafeGet());
            animator.setForegroundBitmap(after.unsafeGet());
        }

        dummyView.setAnimator(animator);
        this.viewSwitcher.showNext();

        uiHandler.post(() -> doPageCurl(animator) );

        dummyView.invalidate();

    }

    /**
     * Does the actual page-curl animation.
     *
     * This method advances the animator by 1 frame,
     * and then places itself back on the background
     * queue, passing along the same animator.
     *
     * That was the animator is moved along until it's done.
     *
     * Should be called from a background thread.
     *
     * @param animator
     */
    private void doPageCurl( PageCurlAnimator animator ) {

        if (animator.isFinished()) {

            if (viewSwitcher.getCurrentView() == dummyView) {
                viewSwitcher.showNext();
            }

            dummyView.setAnimator(null);
            pageNumberView.setVisibility(View.VISIBLE);

        } else {
            animator.advanceOneFrame();
            dummyView.invalidate();

            int delay = 1000 / animator.getAnimationSpeed();

            uiHandler.postDelayed(() -> doPageCurl(animator), delay);
        }
    }

    private void stopAnimating() {

        if (dummyView.getAnimator() != null) {
            dummyView.getAnimator().stop();
            this.dummyView.setAnimator(null);
        }

        if (viewSwitcher.getCurrentView() == this.dummyView) {
            viewSwitcher.showNext();
        }

        this.pageNumberView.setVisibility(View.VISIBLE);
        bookView.setKeepScreenOn(false);
    }

    private Option<Bitmap> getBookViewSnapshot() {

        try {
            Bitmap bitmap = Bitmap.createBitmap(viewSwitcher.getWidth(),
                    viewSwitcher.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            bookView.layout(0, 0, viewSwitcher.getWidth(),
                    viewSwitcher.getHeight());

            bookView.draw(canvas);

            if (config.isShowPageNumbers()) {

                /**
                 * FIXME: creating an intermediate bitmap here because I can't
                 * figure out how to draw the pageNumberView directly on the
                 * canvas and have it show up in the right place.
                 */

                Bitmap pageNumberBitmap = Bitmap.createBitmap(
                        pageNumberView.getWidth(), pageNumberView.getHeight(),
                        Config.ARGB_8888);
                Canvas pageNumberCanvas = new Canvas(pageNumberBitmap);

                pageNumberView.layout(0, 0, pageNumberView.getWidth(),
                        pageNumberView.getHeight());
                pageNumberView.draw(pageNumberCanvas);

                canvas.drawBitmap(pageNumberBitmap, 0, viewSwitcher.getHeight()
                        - pageNumberView.getHeight(), new Paint());

                pageNumberBitmap.recycle();

            }

            return option(bitmap);
        } catch (OutOfMemoryError out) {
            viewSwitcher.setBackgroundColor(config.getBackgroundColor());
        }

        return none();
    }

    private void prepareSlide(Animation inAnim, Animation outAnim) {

        Option<Bitmap> bitmap = getBookViewSnapshot();
		/*
		TODO: is this OK?
        We don't set anything when we get None instead of Some.
        */
        bitmap.forEach((Command<? super Bitmap>) dummyView::setImageBitmap);

        this.pageNumberView.setVisibility(View.GONE);

        inAnim.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {}

            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                onSlideFinished();
            }
        });

        viewSwitcher.layout(0, 0, viewSwitcher.getWidth(),
                viewSwitcher.getHeight());
        dummyView.layout(0, 0, viewSwitcher.getWidth(),
                viewSwitcher.getHeight());

        this.viewSwitcher.showNext();

        this.viewSwitcher.setInAnimation(inAnim);
        this.viewSwitcher.setOutAnimation(outAnim);
    }

    private void onSlideFinished() {
        if ( currentPageNumber > 0 ) {
            this.pageNumberView.setVisibility(View.VISIBLE);
        }
    }

    private void pageDown(Orientation o) {

        if (bookView.isAtEnd()) {
            ToastFactory.showPageEnd();
            return;
        }

        stopAnimating();

        if (o == Orientation.HORIZONTAL) {

            AnimationStyle animH = config.getHorizontalAnim();
            ReadingDirection direction = config.getReadingDirection();

            if (animH == AnimationStyle.CURL) {
                doPageCurl(direction == ReadingDirection.LEFT_TO_RIGHT, true);
            } else if (animH == AnimationStyle.SLIDE) {

                if ( direction == ReadingDirection.LEFT_TO_RIGHT ) {
                    prepareSlide(Animations.inFromRightAnimation(),
                            Animations.outToLeftAnimation());
                } else {
                    prepareSlide(Animations.inFromLeftAnimation(),
                            Animations.outToRightAnimation());
                }

                viewSwitcher.showNext();
                bookView.pageDown();
            } else {
                bookView.pageDown();
            }

        } else {
            if (config.getVerticalAnim() == AnimationStyle.SLIDE) {
                prepareSlide(Animations.inFromBottomAnimation(),
                        Animations.outToTopAnimation());
                viewSwitcher.showNext();
            }

            bookView.pageDown();
        }

    }

    private void pageUp(Orientation o) {

        if (bookView.isAtStart()) {
            ToastFactory.showPageStart();
            return;
        }

        stopAnimating();

        if (o == Orientation.HORIZONTAL) {

            AnimationStyle animH = config.getHorizontalAnim();
            ReadingDirection direction = config.getReadingDirection();

            if (animH == AnimationStyle.CURL) {
                doPageCurl(direction == ReadingDirection.RIGHT_TO_LEFT, false);
            } else if (animH == AnimationStyle.SLIDE) {
                if ( direction == ReadingDirection.LEFT_TO_RIGHT ) {
                    prepareSlide(Animations.inFromLeftAnimation(),
                            Animations.outToRightAnimation());
                } else {
                    prepareSlide(Animations.inFromRightAnimation(),
                            Animations.outToLeftAnimation());
                }
                viewSwitcher.showNext();
                bookView.pageUp();
            } else {
                bookView.pageUp();
            }

        } else {

            if (config.getVerticalAnim() == AnimationStyle.SLIDE) {
                prepareSlide(Animations.inFromTopAnimation(),
                        Animations.outToBottomAnimation());
                viewSwitcher.showNext();
            }

            bookView.pageUp();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        Activity activity = getActivity();

        if ( activity == null ) {
            return;
        }
//
//        MenuItem nightMode = menu.findItem(R.id.profile_night);
//        MenuItem dayMode = menu.findItem(R.id.profile_day);
//
//        MenuItem tts = menu.findItem( R.id.text_to_speech );
//        tts.setEnabled(ttsAvailable);
//
//
//        if (config.getColourProfile() == ColourProfile.DAY) {
//            dayMode.setVisible(false);
//            nightMode.setVisible(true);
//        } else {
//            dayMode.setVisible(true);
//            nightMode.setVisible(false);
//        }
//
//        // Only show open file item if we have a file manager installed
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("file/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        if (!isIntentAvailable(context, intent)) {
//            menu.findItem(R.id.open_file).setVisible(false);
//        }
//
        activity.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void hideTitleBar() {
        titleBarLayout.setVisibility(View.GONE);
    }

    /**
     * This is called after the file manager finished.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            // obtain the filename
            Uri fileUri = data.getData();
            if (fileUri != null) {
                String filePath = fileUri.getPath();
                if (filePath != null) {
                    loadNewBook(filePath);
                }
            }
        }
    }

    private void loadNewBook(String fileName) {

        Log.d("ReadingFragment", "load book:" + fileName);
        Activity activity = getActivity();

        if ( activity != null ) {

            activity.setTitle(R.string.app_name);
            this.bookTitle = null;
            this.titleBase = null;

            bookView.clear();

            updateFileName(null, fileName);
//            new DownloadProgressTask().execute();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        LOG.debug("onStop() called.");
        printScreenAndCallState("onStop()");

        closeWaitDialog();
    }

    public BookView getBookView() {
        return this.bookView;
    }

    public void saveReadingPosition() {
        if (this.bookView != null) {

            int index = this.bookView.getIndex();
            int position = this.bookView.getProgressPosition();

            if ( index != -1 && position != -1 ) {
                config.setLastPosition(this.fileName, position);
                config.setLastIndex(this.fileName, index);

//                sendProgressUpdateToServer(index, position);
            }
        }

    }

    public void share(int from, int to, String selectedText) {

        int pageStart = bookView.getStartOfCurrentPage();

        String text = bookTitle + ", " + authorField.getText() + "\n";

        int offset = pageStart + from;

        int pageNumber = bookView.getPageNumberFor( bookView.getIndex(), offset );
        int totalPages = bookView.getTotalNumberOfPages();

        if ( pageNumber != -1 ) {
            text = text + String.format( getString(R.string.page_number_of),
                    pageNumber, totalPages ) + " (" + progressPercentage + "%)\n\n";

        } else {
            text += "" + progressPercentage + "%\n\n";
        }

        text += selectedText;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        sendIntent.putExtra(Intent.EXTRA_TEXT, text );
        sendIntent.setType("text/plain");

        startActivity(Intent.createChooser(sendIntent, getString(R.string.add_bookmark)));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.reading_menu, menu);
//
//        this.searchMenuItem = menu.findItem(R.id.search_text);
//
//        if (this.searchMenuItem != null) {
//            final com.actionbarsherlock.widget.SearchView searchView =
//                    (com.actionbarsherlock.widget.SearchView) searchMenuItem.getActionView();
//
//            if (searchView != null) {
//
//                searchView.setSubmitButtonEnabled(true);
//                searchView.setOnQueryTextListener(new com.actionbarsherlock.widget.SearchView.OnQueryTextListener() {
//
//                    //This is a work-around, since we get the onQuerySubmit() event twice
//                    //when the user hits enter
//                    private String lastQuery = "";
//
//                    @Override
//                    public boolean onQueryTextSubmit(String query) {
//
//                        if ( query.equals(lastQuery) && searchResults != null ) {
//                            showSearchResultDialog(searchResults);
//                        } else if ( ! query.equals(lastQuery) ) {
//                            searchResults = null;
//                            lastQuery = query;
//                            performSearch(query);
//                        }
//
//                        return true;
//                    }
//
//                    @Override
//                    public boolean onQueryTextChange(String newText) {
//                        return false;
//                    }
//                } );
//            }
//        }
    }

    @Override
    public void onOptionsMenuClosed(android.view.Menu menu) {
        updateFromPrefs();
        hideTitleBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        hideTitleBar();
        return false;

//        // Handle item selection
//        switch (item.getItemId()) {
//
//            case R.id.profile_night:
//                config.setColourProfile(ColourProfile.NIGHT);
//                this.restartActivity();
//                return true;
//
//            case R.id.profile_day:
//                config.setColourProfile(ColourProfile.DAY);
//                this.restartActivity();
//                return true;
//
//            case R.id.manual_sync:
//                if (config.isSyncEnabled()) {
//                    new ManualProgressSync().execute();
//                } else {
//                    Toast.makeText(context, R.string.enter_email, Toast.LENGTH_LONG)
//                            .show();
//                }
//                return true;
//
//            case R.id.search_text:
//                onSearchRequested();
//                return true;
//
//            case R.id.open_file:
//                launchFileManager();
//                return true;
//
//            case R.id.rolling_blind:
//                startAutoScroll();
//                return true;
//
//            case R.id.text_to_speech:
//                startTextToSpeech();
//                return true;
//
//            case R.id.about:
//                dialogFactory.buildAboutDialog().show();
//                return true;
//
//            case R.id.add_bookmark:
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.addToBackStack(null);
//                AddBookmarkFragment fragment = new AddBookmarkFragment();
//
//                fragment.setFilename(this.fileName);
//                fragment.setBookmarkDatabaseHelper(bookmarkDatabaseHelper);
//                fragment.setBookIndex(this.bookView.getIndex());
//                fragment.setBookPosition(this.bookView.getProgressPosition());
//
//                String firstLine = this.bookView.getFirstLine();
//
//                if ( firstLine.length() > 20 ) {
//                    firstLine = firstLine.substring(0, 20) + "…";
//                }
//
//                fragment.setInitialText( firstLine );
//
//                fragment.show(ft, "dialog");
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
    }

    @Override
    public boolean onSwipeDown() {

        if (config.isVerticalSwipeEnabled()) {
            pageDown(Orientation.VERTICAL);
            return true;
        }

        return false;
    }

    @Override
    public boolean onSwipeUp() {

        if (config.isVerticalSwipeEnabled()) {
            pageUp(Orientation.VERTICAL);
            return true;
        }

        return false;
    }

    @Override
    public void onScreenTap() {

        Activity activity = getActivity();

        if ( activity == null ) {
            return;
        }

        stopAnimating();

        if (this.titleBarLayout.getVisibility() == View.VISIBLE) {
            titleBarLayout.setVisibility(View.GONE);

            updateFromPrefs();
        } else {
            titleBarLayout.setVisibility(View.VISIBLE);

//            getSherlockActivity().getSupportActionBar().show();
            activity.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public boolean onSwipeLeft() {

        if (config.isHorizontalSwipeEnabled()) {

            if ( config.getReadingDirection() == ReadingDirection.LEFT_TO_RIGHT ) {
                pageDown(Orientation.HORIZONTAL);
            } else {
                pageUp(Orientation.HORIZONTAL);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onSwipeRight() {

        if (config.isHorizontalSwipeEnabled()) {

            if ( config.getReadingDirection() == ReadingDirection.LEFT_TO_RIGHT ) {
                pageUp(Orientation.HORIZONTAL);
            } else {
                pageDown(Orientation.HORIZONTAL);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onTapLeftEdge() {
        if (config.isHorizontalTappingEnabled()) {
            if ( config.getReadingDirection() == ReadingDirection.LEFT_TO_RIGHT ) {
                pageUp(Orientation.HORIZONTAL);
            } else {
                pageDown(Orientation.HORIZONTAL);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onTapRightEdge() {
        if (config.isHorizontalTappingEnabled()) {

            if ( config.getReadingDirection() == ReadingDirection.LEFT_TO_RIGHT ) {
                pageDown(Orientation.HORIZONTAL);
            } else {
                pageUp(Orientation.HORIZONTAL);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onTapTopEdge() {
        if (config.isVerticalTappingEnabled()) {
            pageUp(Orientation.VERTICAL);
            return true;
        }

        return false;
    }

    @Override
    public boolean onTapBottomEdge() {
        if (config.isVerticalTappingEnabled()) {
            pageDown(Orientation.VERTICAL);
            return true;
        }

        return false;
    }

    @Override
    public boolean onLeftEdgeSlide(int value) {

        if (config.isBrightnessControlEnabled() && value != 0) {
            int baseBrightness = config.getBrightNess();

            int brightnessLevel = Math.min(99, value + baseBrightness);
            brightnessLevel = Math.max(1, brightnessLevel);

            final int level = brightnessLevel;

            String brightness = getString(R.string.brightness);
            setScreenBrightnessLevel(brightnessLevel);

            if (brightnessToast == null) {
                brightnessToast = Toast
                        .makeText(context, brightness + ": "
                                + brightnessLevel, Toast.LENGTH_SHORT);
            } else {
                brightnessToast.setText(brightness + ": " + brightnessLevel);
            }

            brightnessToast.show();
            backgroundHandler.post( () -> config.setBrightness(level) );

            return true;
        }

        return false;
    }

    @Override
    public boolean onRightEdgeSlide(int value) {
        return false;
    }


    @Override
    public void onWordLongPressed(int startOffset, int endOffset, CharSequence word) {

        this.selectedWord = new SelectedWord( startOffset, endOffset, word );

        Activity activity = getActivity();

        if ( activity != null ) {
            activity.openContextMenu(bookView);
        }
    }

    @Override
    public void onHighLightClick(HighLight highLight) {

    }

    private void launchFileManager() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(intent, REQUEST_CODE_GET_CONTENT);
        } catch (ActivityNotFoundException e) {
            // No compatible file manager was found.
            Toast.makeText(context, getString(R.string.install_oi),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showPickProgressDialog(final List<BookProgress> results) {

//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle(getString(R.string.cloud_bm));
//
//                c adapter = new ProgressListAdapter(context, bookView, results);
//        builder.setAdapter(adapter, adapter);
//
//        AlertDialog dialog = builder.create();
//        dialog.setOwnerActivity(getActivity());
//        dialog.show();
    }


    @Override
    public void onSaveInstanceState(final Bundle outState) {
        if (this.bookView != null) {
            outState.putInt(POS_KEY, this.bookView.getProgressPosition());
            outState.putInt(IDX_KEY, this.bookView.getIndex());
        }
    }

    private void setSupportProgressBarIndeterminateVisibility(boolean enable) {
//        SherlockFragmentActivity activity = getSherlockActivity();
//        if ( activity != null) {
//            LOG.debug("Setting progress bar to " + enable );
//            activity.setSupportProgressBarIndeterminateVisibility(enable);
//        } else {
//            LOG.debug("Got null activity.");
//        }
    }

    @Override
    public void onCalculatePageNumbersComplete() {
        setSupportProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onStartCalculatePageNumbers() {
        setSupportProgressBarIndeterminateVisibility(true);
    }

    public void onSearchRequested() {
//        if ( this.searchMenuItem != null && searchMenuItem.getActionView() != null ) {
//            getSherlockActivity().getSupportActionBar().show();
//            this.searchMenuItem.expandActionView();
//            this.searchMenuItem.getActionView().requestFocus();
//        } else {
//            dialogFactory.showSearchDialog(R.string.search_text, R.string.enter_query, this::performSearch);
//        }
    }

    //Hack to prevent showing the dialog twice
    private boolean isSearchResultsDialogShowing = false;


    public boolean hasHighlights() {

        List<HighLight> highLights = this.highlightManager.getHighLights(bookView.getFileName());

        return highLights != null && ! highLights.isEmpty();
    }

    public boolean hasBookmarks() {

        List<Bookmark> bookmarks = this.bookmarkDatabaseHelper.getBookmarksForFile(bookView.getFileName());

        return bookmarks != null && ! bookmarks.isEmpty();
    }

    private String getHighlightLabel(int index, int position, String text ) {

        final int totalNumberOfPages = bookView.getTotalNumberOfPages();

        int percentage = bookView.getPercentageFor( index, position );
        int pageNumber = bookView.getPageNumberFor(index, position);

        String result = percentage + "%";

        if ( pageNumber != -1 ) {
            result = String.format( context.getString(R.string.page_number_of),
                    pageNumber, totalNumberOfPages )
                    + " (" + percentage + "%)";
        }

        if ( text != null && text.trim().length() > 0 ) {
            result += ": " + TextUtil.shortenText( text );
        }

        return result;
    }

    public List<NavigationCallback> getBookmarks() {

        List<Bookmark> bookmarks = this.bookmarkDatabaseHelper.getBookmarksForFile(bookView.getFileName());

        List<NavigationCallback> result = new ArrayList<>();

        for ( final Bookmark bookmark: bookmarks ) {

            final String finalText = getHighlightLabel( bookmark.getIndex(),
                    bookmark.getPosition(), null );

            NavigationCallback callback = new NavigationCallback( bookmark.getName(), finalText )
                    .setOnClick( () -> bookView.navigateTo( bookmark.getIndex(), bookmark.getPosition() ))
                    .setOnLongClick( () -> onBookmarkLongClick(bookmark) );

            result.add( callback );
        }

        return result;
    }

    private void onBookmarkLongClick(Bookmark bookmark) {
        //TODO bookmark
    }


    public List<NavigationCallback> getHighlights() {

        List<HighLight> highLights = this.highlightManager.getHighLights(bookView.getFileName());

        List<NavigationCallback> result = new ArrayList<>();

        for ( final HighLight highLight: highLights ) {

            final String finalText = getHighlightLabel( highLight.getIndex(), highLight.getStart(),
                    highLight.getTextNote() );

            NavigationCallback callback = new NavigationCallback( highLight.getDisplayText(), finalText )
                    .setOnClick( () -> bookView.navigateTo( highLight.getIndex(), highLight.getStart() ))
                    .setOnLongClick( () -> onHighLightClick(highLight) );

            result.add( callback );

        }

        return result;
    }


}
