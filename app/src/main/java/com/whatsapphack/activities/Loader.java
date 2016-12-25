package com.whatsapphack.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.tapjoy.TJEarnedCurrencyListener;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJSpendCurrencyListener;
import com.tapjoy.Tapjoy;
import com.whatsapphack.PieProgressDrawable;
import com.whatsapphack.listeners.TapJoyPlacementListener;

import com.whatsapphack.R;

public class Loader extends AppCompatActivity {

    private ImageView imageView;
    private TJPlacement placement;
    private static final String PLACEMENT_NAME = "Offerwall";
    private PieProgressDrawable pieProgressDrawable;
    private boolean showDialogs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        UISetUp();


        startTimer(7000);
    }

    private void startTimer(long milliseconds) {
        new CountDownTimer(milliseconds+1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("time", "int=" + (int) ((8000 - millisUntilFinished) / 1000) + 1);
                updateTime((int) ((8000 - millisUntilFinished) / 1000) + 1);
            }

            @Override
            public void onFinish() {
                showAlertDialog();
            }
        }.start();
    }

    private void UISetUp() {
        pieProgressDrawable = new PieProgressDrawable();
        pieProgressDrawable.setPieColor(ContextCompat.getColor(this, R.color.colorWhite));
        pieProgressDrawable.setCircleColor(ContextCompat.getColor(this, R.color.colorGreen));
        pieProgressDrawable.setMaxProgress(7);
        pieProgressDrawable.setOuterCircleWidth(30);
        pieProgressDrawable.setInnerCircleWidth(10);
        imageView = (ImageView) findViewById(R.id.time_progress);
        imageView.setImageDrawable(pieProgressDrawable);

        TapJoyPlacementListener placementListener = new TapJoyPlacementListener();
        placementListener.setBlanceListener(mBalanceListener);
        placement = new TJPlacement(this, PLACEMENT_NAME, placementListener);
        if (Tapjoy.isConnected()) {
            placement.requestContent();
        }
    }


    @Override
    protected void onStart() {
        showDialogs=true;
        Tapjoy.onActivityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        showDialogs=false;
        Tapjoy.onActivityStop(this);
        super.onStop();
    }

    public void updateTime(int progress) {
        pieProgressDrawable.setLevel(progress);
        imageView.invalidate();
    }

    TJEarnedCurrencyListener mEarnedCurrencyListener = new TJEarnedCurrencyListener() {
        @Override
        public void onEarnedCurrency(String s, int i) {
            Log.i("mApp", "earnCurrencyBalance returned " + s + ":" + i);
            if (i > 0) {
                showSuccesDialog();
            }
        }
    };

    TJGetCurrencyBalanceListener mBalanceListener = new TJGetCurrencyBalanceListener() {
        @Override
        public void onGetCurrencyBalanceResponse(String s, int i) {
            Log.i("Tapjoy", "getCurrencyBalance returned " + s + ":" + i);
            if (i > 0) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSuccesDialog();

                    }
                });
                Tapjoy.spendCurrency(i,mSpendCurrencyListener);

            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialog();
                    }
                });
            }
        }

        @Override
        public void onGetCurrencyBalanceResponseFailure(String s) {
            Log.i("Tapjoy", "getCurrencyBalance error: " + s);
        }
    };

    TJSpendCurrencyListener mSpendCurrencyListener=new TJSpendCurrencyListener() {
        @Override
        public void onSpendCurrencyResponse(String s, int i) {
            Log.i("Tapjoy", s + ": " + i);
        }

        @Override
        public void onSpendCurrencyResponseFailure(String s) {
            Log.i("Tapjoy", "spendCurrency error: " + s);
        }
    };


    private void showSuccesDialog() {
        if(showDialogs) {
            Log.i("Tapjoy", "succesDialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(Loader.this);
            builder.setTitle(getString(R.string.success_dialog_name))
                    .setMessage(getString(R.string.success_dialog_text))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.success_dialog_positive_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    private void showAlertDialog() {
        if(showDialogs) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Loader.this);
            builder.setTitle(getString(R.string.alert_dialog_name))
                    .setMessage(getString(R.string.alert_dialog_text))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.alert_dialog_positive_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (placement.isContentReady())
                                placement.showContent();
                            else {
                                Log.d("MyApp", "placement is not ready.");
                                ((TapJoyPlacementListener)placement.getListener()).showContentWhenAvailable(placement);
                            }

                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


}
