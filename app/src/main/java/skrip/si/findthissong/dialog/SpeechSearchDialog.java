package skrip.si.findthissong.dialog;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.ui.SpeechProgressView;

import java.util.List;
import java.util.Locale;

import skrip.si.findthissong.R;
import skrip.si.findthissong.Constant;

public class SpeechSearchDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_speech_search_dialog);

        final SpeechProgressView progressView = findViewById(R.id.progress);
        final TextView resultText = findViewById(R.id.text_result);

//        Logger.setLogLevel(Logger.LogLevel.DEBUG);

        try {
            // https://gotev.github.io/android-speech/
            Speech.init(this, getPackageName());
            Speech.getInstance().setLocale(new Locale("id"));
            Speech.getInstance().setStopListeningAfterInactivity(6000);
            Speech.getInstance().startListening(progressView, new SpeechDelegate() {
                @Override
                public void onStartOfSpeech() {
                    Log.i("speech", "speech recognition is now active");
                }

                @Override
                public void onSpeechRmsChanged(float v) {
//                    Log.d("speech", "rms is now: " + v);
                }

                @Override
                public void onSpeechPartialResults(List<String> results) {
                    StringBuilder str = new StringBuilder();
                    for (String res : results) {
                        str.append(res).append(" ");
                    }
                    Log.i("speech", "partial result: " + str.toString().trim());

                    resultText.setText(str.toString().trim());
                }

                @Override
                public void onSpeechResult(String result) {
                    Log.i("speech", "result: " + result);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constant.ARG_PATTERN, result);
                    setResult(Activity.RESULT_OK, returnIntent);

                    finish();
                }
            });
        } catch (SpeechRecognitionNotAvailable exc) {
            Log.e("Speech", "Speech recognition is not available on this device!");
        } catch (GoogleVoiceTypingDisabledException exc) {
            Log.e("Speech", "Google voice typing must be enabled!");
        } catch (Exception e) {
            Log.e("Speech", e+"");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Speech.getInstance().shutdown();
    }
}
