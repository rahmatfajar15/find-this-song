package skrip.si.findthissong.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.widget.TextView;

import java.util.ArrayList;

import skrip.si.findthissong.R;
import skrip.si.findthissong.helper.Constant;

public class LyricActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric);

        TextView titleText = findViewById(R.id.text_show_title);
        TextView lyricText = findViewById(R.id.text_show_lyric);

        Bundle getExtras = getIntent().getExtras();

        assert null != getExtras;
        String title = getExtras.getString(Constant.ARG_TITLE);
        String lyric = getExtras.getString(Constant.ARG_LYRIC);
        int m = getExtras.getInt(Constant.ARG_PATTERN_LENGTH);
        ArrayList<Integer> indexes = getExtras.getIntegerArrayList(Constant.ARG_INDEXES);

        SpannableString string = new SpannableString(lyric);

        assert indexes != null;
        for (int index :
                    indexes) {
                string.setSpan(
                        new BackgroundColorSpan(Color.YELLOW),
                        index,
                        (index + m > lyric.length() ? lyric.length() - 1 : index + m),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        titleText.setText(title);
        lyricText.setText(string);
    }
}
