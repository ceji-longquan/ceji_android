package com.lqtemple.android.lqbookreader.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lqtemple.android.lqbookreader.util.LogHelper;

/**
 * @author Administrator
 *         Created by Administrator on 2017/3/16 0016.
 * @version $Rev$
 * @updateDes ${TODO}
 */

public class BaseActivity extends AppCompatActivity {
/*    Typeface font_Libian;
    Typeface font_SourceHanSansCN;
    Typeface font_STFANGSO;*/
    //加载字体
    private static final String TAG = LogHelper.makeLogTag(BaseActivity.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*font_Libian= Typeface.createFromAsset(getAssets(),"fonts/Libian-SC-Regular.ttf");
        font_SourceHanSansCN= Typeface.createFromAsset(getAssets(),"fonts/SourceHanSansCN-Light.otf");
        font_STFANGSO= Typeface.createFromAsset(getAssets(),"fonts/STFANGSO.TTF");*/

    }


    @Override
    protected void onStart() {
        super.onStart();
        LogHelper.d(TAG, "Activity onStart");

    }


    public void onReturnLast(View v){
        finish();
    }
}
