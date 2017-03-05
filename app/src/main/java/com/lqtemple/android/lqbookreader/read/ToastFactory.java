package com.lqtemple.android.lqbookreader.read;

import android.widget.Toast;

import com.lqtemple.android.lqbookreader.MyApplication;
import com.lqtemple.android.lqbookreader.R;

/**
 *  Toast factory
 */
public class ToastFactory {
    public static void showPageEnd() {
        Toast.makeText(MyApplication.getsContext(), R.string.book_reach_end_toast, Toast.LENGTH_SHORT).show();
    }

    public static void showPageStart() {
        Toast.makeText(MyApplication.getsContext(), R.string.book_reach_start_toast, Toast.LENGTH_SHORT).show();
    }
}
