package com.whatsapphack.listeners;

import android.util.Log;

import com.tapjoy.TJActionRequest;
import com.tapjoy.TJError;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;

/**
 * Created by hasana on 12/23/2016.
 */

public class TapJoyPlacementListener implements TJPlacementListener {
    private TJGetCurrencyBalanceListener mBalanceListener;
    private boolean showWhenAvailable=false;

    public void setBlanceListener(TJGetCurrencyBalanceListener listener){
        mBalanceListener=listener;
    }
    public void showContentWhenAvailable(TJPlacement tjPlacement){
        showWhenAvailable=true;
        tjPlacement.requestContent();
    }
    @Override
    public void onRequestSuccess(TJPlacement tjPlacement) {
        Log.i("TapJoy","onRequestSuccess()");

    }

    @Override
    public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {
        Log.i("TapJoy","onRequestFailure()");
    }

    @Override
    public void onContentReady(TJPlacement tjPlacement) {
        Log.i("TapJoy","onContentReady()");
        if(showWhenAvailable){
            showWhenAvailable=false;
            tjPlacement.showContent();
        }
    }

    @Override
    public void onContentShow(TJPlacement tjPlacement) {
        Log.i("TapJoy","onContentShow()");
    }

    @Override
    public void onContentDismiss(TJPlacement tjPlacement) {
        Log.i("TapJoy","onContentDismiss()");
        if(mBalanceListener!=null) {
            Tapjoy.getCurrencyBalance(mBalanceListener);
        }
    }

    @Override
    public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {
        Log.i("TapJoy","onPurchaseRequest()");
    }

    @Override
    public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {
        Log.i("TapJoy","onRewardRequest()");
    }


}
