/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.omegaki113r.pksoftwaresolutions.shalintha.translator;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.emiliadev.android.speech.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.facebook.ads.*;

public class Main2Activity extends AppCompatActivity implements MessageDialogFragment.Listener {

    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";

    private static final String STATE_RESULTS = "results";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

    private SpeechService mSpeechService;

    private VoiceRecorder mVoiceRecorder;


    private AdView adView;

    EditText top;
    TextView bot;
    ImageButton btnEnglish;
    boolean grantedPermission = true;

    Button Clear_Btn, btnTranslate;

    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            showStatus(true);
            if (mSpeechService != null) {
                mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (mSpeechService != null) {
                mSpeechService.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            showStatus(false);
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }

    };



    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        top = (EditText) findViewById(R.id.etTop);
        btnEnglish = (ImageButton) findViewById(R.id.btnSpeechEnglish);
        btnTranslate = (Button) findViewById(R.id.btnTranslate);
        bot = (TextView) findViewById(R.id.tvBot);

        Clear_Btn = (Button)findViewById(R.id.clearbtn);

        Clear_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                top.setText("");
                bot.setText("");
            }
        });

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecorder();
                //Toast.makeText(getApplicationContext(),"VOICE STARTED",Toast.LENGTH_LONG).show();
            }
        });

        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectLanguage(top.getText().toString());
            }
        });


/*
        top.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("IN ADD TEXT CHANGE","BEFORE CHANGED");
                detectLanguage(charSequence.toString());
                Log.e("IN ADD TEXT CHANGE","CHANGED");

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
*/

        adView = new AdView(this, "346610935945311_346630652610006", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(Main2Activity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // Request an ad
        adView.loadAd();



    }

    public void translateToSpanish(CharSequence charSequence) {                         // AIzaSyAhEV2ux6cXO2mpOc1fCYB8sgi9fwX48tg
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "";
        url = "https://translation.googleapis.com/language/translate/v2?q=" + charSequence.toString() + "&target=es&source=en&key=AIzaSyAhEV2ux6cXO2mpOc1fCYB8sgi9fwX48tg";
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject object = (JSONObject) response.get("data");
                    JSONArray x = (JSONArray) object.get("translations");
                    JSONObject y = x.getJSONObject(0);
                    String translatedString = y.getString("translatedText");
                    bot.setText(translatedString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void translateToEnglish(CharSequence charSequence) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "";
        url = "https://translation.googleapis.com/language/translate/v2?q=" + charSequence.toString() + "&target=en&source=es&key=AIzaSyAhEV2ux6cXO2mpOc1fCYB8sgi9fwX48tg";
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject object = (JSONObject) response.get("data");
                    JSONArray x = (JSONArray) object.get("translations");
                    JSONObject y = x.getJSONObject(0);
                    String translatedString = y.getString("translatedText");
                    bot.setText(translatedString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Prepare Cloud Speech API
        bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

        // Start listening to voices
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            //startVoiceRecorder();
            grantedPermission = true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            showPermissionMessageDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @Override
    protected void onStop() {
        // Stop listening to voice
        stopVoiceRecorder();

        // Stop Cloud Speech API
        mSpeechService.removeListener(mSpeechServiceListener);
        unbindService(mServiceConnection);
        mSpeechService = null;

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (permissions.length == 1 && grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                grantedPermission = true;
                //startVoiceRecorder();
            } else {
                showPermissionMessageDialog();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_file:
                mSpeechService.recognizeInputStream(getResources().openRawResource(R.raw.audio));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
        //Toast.makeText(getApplicationContext(),"INSIDE START VOICE RECORDER",Toast.LENGTH_LONG).show();
    }

    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }

    private void showPermissionMessageDialog() {
        MessageDialogFragment
                .newInstance(getString(R.string.permission_message))
                .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
    }

    private void showStatus(final boolean hearingVoice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mStatus.setTextColor(hearingVoice ? mColorHearing : mColorNotHearing);
            }
        });
    }

    @Override
    public void onMessageDialogDismissed() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }

    void detectLanguage(String s){
        final String text = s.replace(" ","%20");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "";
        url = "https://translation.googleapis.com/language/translate/v2/detect?q="+text+"&key=AIzaSyAhEV2ux6cXO2mpOc1fCYB8sgi9fwX48tg";
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("DETECTED LANGUAGE RESP",response.toString());
                    JSONObject object = (JSONObject) response.get("data");
                    Log.e("DETECTED LANGUAGE DATA",object.toString());
                    JSONArray x = (JSONArray) object.get("detections");
                    Log.e("DETECTED LANGUAGE DETEC",x.toString());
                    JSONArray y = x.getJSONArray(0);
                    Log.e("DETECTED LANGUAGE y",y.toString());
                    JSONObject z = y.getJSONObject(0);
                    String translatedString = z.getString("language");
                    Log.e("DETECTED LANGUAGE",translatedString);
                    if(translatedString.equals("en")){
                        translateToSpanish(text);
                    }else if(translatedString.equals("es")){
                        translateToEnglish(text);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    Log.e("SPEECH  DETECTED",text);
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                top.setText(text);
                            }
                        });

                        detectLanguage(text);
                    }
                }
            };





}
