package com.lqtemple.android.lqbookreader;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sundxing on 16/12/18.
 */

public class Singleton {

    private static Map<Class, Object> mSet = new ConcurrentHashMap<>();

    private Singleton() {
    }

    public static  <T> T getInstance(Class<T> clazz) {
        T instance = null;
        Object object = mSet.get(clazz);
        if (clazz.isInstance(object)) {
            instance = (T) object;
        }

        if (instance == null ) {
            synchronized (mSet) {
                try {
                    instance = clazz.newInstance();
                    mSet.put(clazz, instance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
    }
}
