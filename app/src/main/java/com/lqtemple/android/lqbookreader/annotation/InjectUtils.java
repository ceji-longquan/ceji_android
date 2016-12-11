package com.lqtemple.android.lqbookreader.annotation;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by sundxing on 16/12/11.
 */
public class InjectUtils {
    public static void initBindView(Object curClass, View sourceView){
        // 通过反射获取到全部属性，反射的字段可能是一个类（静态）字段或实例字段
        Field[] fields = curClass.getClass().getDeclaredFields();
        if(fields != null && fields.length > 0){
            InjectView bindView = null;
            for(Field field : fields){
                //获取自定义的注解类标志
                bindView = field.getAnnotation(InjectView.class);
                if(bindView != null){
                    int viewId = bindView.value();
                    boolean clickBoolean = bindView.click();
                    try {
                        //反射访问私有成员，必须加上这句
                        field.setAccessible(true);
                        if (clickBoolean) {
                            sourceView.findViewById(viewId).setOnClickListener(
                                    (View.OnClickListener) curClass);
                        }
                        //将currentClass的field赋值为sourceView.findViewById(viewId)
                        //给我们要找的字段设置值
                        field.set(curClass, sourceView.findViewById(viewId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 必须在setContentView之后调用
     *
     * @param aty
     */
    public static void bind(Activity aty) {
        initBindView(aty, aty.getWindow().getDecorView());
    }

    /**
     * 必须在onCreateView()之后调用
     *
     * @param aty
     */
    public static void bind(Fragment aty) {
        initBindView(aty, aty.getView());
    }

    public static void bind(View v) {
        initBindView(v, v);
    }
}
