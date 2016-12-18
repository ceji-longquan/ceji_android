package com.lqtemple.android.lqbookreader.read;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lqtemple.android.lqbookreader.FileUtils;
import com.lqtemple.android.lqbookreader.model.Book;
import com.lqtemple.android.lqbookreader.model.RawBook;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sundxing on 16/12/11.
 */
public class BookLoader {
    private static Map<String, Book> cacheBooks = new ConcurrentHashMap<>(1);
    // TODO background
    public static Book load(String fileName) {
        //
        Book book = cacheBooks.get(fileName);
        if (book == null) {
            try {
                String result = FileUtils.readFile(fileName, "utf-8").toString();
                RawBook rawBook = JSON.parseObject(result, RawBook.class);
                book = Book.from(rawBook);
                cacheBooks.put(fileName, book);
                Log.d("", "book load:" + rawBook.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return book;
    }
}
