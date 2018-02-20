package skrip.si.findthissong.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import skrip.si.findthissong.R;
import skrip.si.findthissong.Constant;

public class TextSearchDialog extends AppCompatActivity {

    EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_text_search);

        searchText = findViewById(R.id.edit_text_search);
        Button searchBtn = findViewById(R.id.button_search_text);

//        Auto show keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constant.ARG_PATTERN, searchText.getText().toString());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

}
