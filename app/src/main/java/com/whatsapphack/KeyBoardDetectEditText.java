package com.whatsapphack;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

/**
 * Created by hasana on 12/21/2016.
 */

public class KeyBoardDetectEditText extends EditText {
    public KeyBoardDetectEditText(Context context) {
        super(context);
    }

    public KeyBoardDetectEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyBoardDetectEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event)
    {
        Log.v("helloScreen","back pressed");
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            clearFocus();
        }
        return super.onKeyPreIme(keyCode, event);
    }

}
