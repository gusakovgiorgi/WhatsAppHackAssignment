package com.whatsapphack.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tapjoy.TJConnectListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;
import com.whatsapphack.MaskParsingException;

import com.whatsapphack.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloScreen extends AppCompatActivity {

    EditText numberEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_screen);

        numberEdt = (EditText) findViewById(R.id.victimsNumberId);

        //not more one special symbols between *
        numberEdt.addTextChangedListener(new MaskedWatcher("*(***)*** ** **"));

        connectToTapjoy();

        Button btn = (Button) findViewById(R.id.startButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = numberEdt.getText().toString().replace("*","").replace("(","").replace(")","").replace(" ","");
                Log.i("MyApp","numbers text="+number);
                Pattern ptrn = Pattern.compile("^\\d+$");
                Matcher matcher = ptrn.matcher(number);
                if (matcher.find()) {
                    startActivity(new Intent(HelloScreen.this, Loader.class));
                } else {
                    showMessage("Enter only numbers");
                }
            }
        });
    }

    private void connectToTapjoy() {
        // OPTIONAL: For custom startup flags.
        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");

        // If you are not using Tapjoy Managed currency, you would set your own user ID here.
        //	connectFlags.put(TapjoyConnectFlag.USER_ID, "A_UNIQUE_USER_ID");

        // Connect with the Tapjoy server.  Call this when the application first starts.
        // REPLACE THE SDK KEY WITH YOUR TAPJOY SDK Key.
        String tapjoySDKKey = "x7IcjjzlRauc8f9UoJqqOwECAFzBGw8Q7amSEDHkAN9Qqpx38oJvSqrXdfE1";

//        Tapjoy.setGcmSender("34027022155");

        // NOTE: This is the only step required if you're an advertiser.
        Tapjoy.connect(this, tapjoySDKKey, connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                HelloScreen.this.onConnectSuccess();
            }

            @Override
            public void onConnectFailure() {
                HelloScreen.this.onConnectFail();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Tapjoy.onActivityStart(this);
    }

    @Override
    protected void onStop() {
        Tapjoy.onActivityStop(this);
        super.onStop();
    }


    private void showMessage(String msg) {
        Toast.makeText(HelloScreen.this, msg, Toast.LENGTH_SHORT).show();
    }


    private void onConnectFail() {
        Toast.makeText(this, "connect fail", Toast.LENGTH_SHORT).show();
    }

    private void onConnectSuccess() {
//        Toast.makeText(this,"connect success",Toast.LENGTH_SHORT).show();
        //The SDK is now connected.TJPlacementListener placementListener = new TapJoyPlacementListener();
//        TJPlacement placement = new TJPlacement(this, "AppLaunch", new TapJoyPlacementListener());
//        if(Tapjoy.isConnected())
//            placement.requestContent();
//        else
//            Log.d("MyApp", "Tapjoy SDK must finish connecting before requesting content.");

    }


    class MaskedWatcher implements TextWatcher {

        private boolean mTextWatcherEnabled = true;
        private final static char REPLACEMENT_SYMBOL = '*';
        private int position;
        private boolean specialAdd = false;
        private boolean defaultAdd = false;
        private boolean defaultDelete = false;
        private boolean specialDelete = false;
        private boolean ignoreNumbers = false;
        private ArrayList<Integer> specialSignsPositions;
        private char restoreChar;
        private String mMask;

        public MaskedWatcher(String mask) {
            mMask = mask;
            numberEdt.setText(mMask);
            parsing(mMask);
        }

        private void parsing(CharSequence mask) {
            specialSignsPositions = new ArrayList<>();
            int previousPos = -1;
            for (int i = 0; i < mask.length(); i++) {
                if (mask.charAt(i) != REPLACEMENT_SYMBOL) {
                    if ((i - 1) == previousPos) {
                        throw new MaskParsingException("not more one special symbols between");
                    }
                    specialSignsPositions.add(i);
                    previousPos = i;
                }
            }
        }

        private boolean isScpesialAdd(int pos) {
            //special sign ishould be after pos, so increase pos
            pos++;
            for (int savedPos : specialSignsPositions) {
                if (savedPos == pos) {
                    return true;
                }
            }
            return false;
        }

        private boolean isSpecialDelete(int pos) {
            for (int savedPos : specialSignsPositions) {
                if (savedPos == pos) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            Log.v("textWatcher", "s=" + s + ", from " + start + " " + count + " symbols will be replaced by " + after + " symbols");
            if (mTextWatcherEnabled) {
                // numbers add and delete by one
                boolean addCorrectConditions = (count == 0 && after == 1);
                boolean deleteCorrectConditions = (count == 1 && after == 0);
                //add numbers
                if (isScpesialAdd(start) && addCorrectConditions) {
                    position = start;
                    specialAdd = true;
                } else if (start < mMask.length() && addCorrectConditions) {
                    position = start;
                    defaultAdd = true;
                } else if (isSpecialDelete(start) && deleteCorrectConditions) {
                    position = start;
                    restoreChar = s.charAt(start);
                    specialDelete = true;
                } else if (deleteCorrectConditions) {
                    position = start;
                    defaultDelete = true;
                } else {
                    position = start;
                    ignoreNumbers = true;
                }

                //enter numbers in bracets


            }


        }


        @Override
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mTextWatcherEnabled) {
                if (specialAdd) {
                    removeCharAt(position + 1);
                    numberEdt.setSelection(position + 2);
                    specialAdd = false;
                } else if (defaultAdd) {
                    removeCharAt(position + 1);
                    defaultAdd = false;
                } else if (ignoreNumbers) {
                    removeCharAt(position);
                    ignoreNumbers = false;
                } else if (specialDelete) {
                    addStringAt(position - 1, "*" + restoreChar);
                    numberEdt.setSelection(position - 1);
                    specialDelete = false;
                } else if (defaultDelete) {
                    addCharAt(position, "*");
                    numberEdt.setSelection(position);
                    defaultDelete = false;
                }
            }
        }


        private void removeCharAt(int pos) {
            mTextWatcherEnabled = false;
            numberEdt.getText().delete(pos, pos + 1);
            mTextWatcherEnabled = true;
        }

        private void addCharAt(int pos, CharSequence text) {
            mTextWatcherEnabled = false;
            numberEdt.getText().replace(pos, pos, text);
            mTextWatcherEnabled = true;
        }

        private void addStringAt(int pos, CharSequence text) {
            mTextWatcherEnabled = false;
            numberEdt.getText().replace(pos, pos + 1, text);
            mTextWatcherEnabled = true;
        }

    }
}
