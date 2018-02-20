package skrip.si.findthissong.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import skrip.si.findthissong.R;
import skrip.si.findthissong.algorithm.RaitaAlgorithm;
import skrip.si.findthissong.algorithm.ReverseColussiAlgorithm;
import skrip.si.findthissong.dialog.SpeechSearchDialog;
import skrip.si.findthissong.Constant;
import skrip.si.findthissong.dialog.TextSearchDialog;
import skrip.si.findthissong.fragment.PlaceholderFragment;
import skrip.si.findthissong.model.LyricModel;

import static java.lang.Math.min;

public class MainActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private static final int MAX_PATTERN = 40;

    FloatingActionMenu mFam;
    FloatingActionButton mFabSpeechSearch;

    PlaceholderFragment mRaita, mReverseColussi;

    ArrayList<String> mTitleList = new ArrayList<>();
    ArrayList<String> mLyricList = new ArrayList<>();

    ArrayList<Integer> raitaIndexResult = new ArrayList<>();
    ArrayList<Integer> reverseColussiIndexResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(this);

        ViewPager mViewPager = findViewById(R.id.container);
        TabLayout mTabLayout = findViewById(R.id.tabs);

        Query firebaseLyricsRef = FirebaseDatabase.getInstance().getReference("Lyrics");
        firebaseLyricsRef.orderByChild("artist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLyricList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    LyricModel lyric = postSnapshot.getValue(LyricModel.class);

                    assert lyric != null;
                    mTitleList.add(lyric.getTitleFormatted());
                    mLyricList.add(lyric.getLyric());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Data List", databaseError.getDetails());
            }
        });

        //setup RaitaAlgorithm Fragment
        mRaita = new PlaceholderFragment();
        mReverseColussi = new PlaceholderFragment();

        // Setup ViewPager Adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mRaita, "Raita Algorithm");
        adapter.addFragment(mReverseColussi, "Reverse Colussi Algorithm");
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        // Setup Floating Action Menu
        mFam = findViewById(R.id.fam);
        animateFam(mFam);

        FloatingActionButton fabTextSearch = findViewById(R.id.fab_text_search);
        mFabSpeechSearch = findViewById(R.id.fab_speech_search);

        fabTextSearch.setOnClickListener(this);
        mFabSpeechSearch.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_search:
                resetList();
                return true;
            case R.id.about_menu:
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        if (offset == 0){
            mFam.showMenu(true);
        } else {
            mFam.hideMenu(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_speech_search:
                if (!micIsGranted()) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            Constant.PERMISSIONS_REQUEST_RECORD_AUDIO);
                } else {
                    Intent speechIntent = new Intent(getApplicationContext(), SpeechSearchDialog.class);
                    startActivityForResult(speechIntent, Constant.RC_INTENT_SPEECH_SEARCH);
                }

                mFam.close(true);
                break;


            case R.id.fab_text_search:
                Intent textIntent = new Intent(getApplicationContext(), TextSearchDialog.class);
                startActivityForResult(textIntent, Constant.RC_INTENT_TEXT_SEARCH);

                mFam.close(true);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Voice Search
        if (requestCode == Constant.RC_INTENT_SPEECH_SEARCH && resultCode == Activity.RESULT_OK && data != null) {
            String patternFromVoice = data.getStringExtra(Constant.ARG_PATTERN);
            if (patternFromVoice.isEmpty()){
                Toast.makeText(getApplicationContext(), "Pattern Kosong", Toast.LENGTH_SHORT).show();
            } else {
                patternFromVoice = cropPattern(patternFromVoice, getApplicationContext());
                search(Constant.ALGORITHM_RAITA, patternFromVoice);
                search(Constant.ALGORITHM_REVERSE_COLUSSI, patternFromVoice);
            }
        }

        // Text Search
        if (requestCode == Constant.RC_INTENT_TEXT_SEARCH && resultCode == Activity.RESULT_OK && data != null) {
            String patternFromText = data.getStringExtra(Constant.ARG_PATTERN);
            if (patternFromText.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Pattern Kosong", Toast.LENGTH_SHORT).show();
            } else {
                patternFromText = cropPattern(patternFromText, getApplicationContext());
                search(Constant.ALGORITHM_RAITA, patternFromText);
                search(Constant.ALGORITHM_REVERSE_COLUSSI, patternFromText);
            }
            showDifferenceResult();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(),
                            "Please, allow Microphone Permission\nSo apps can run properly"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Intent speechIntent = new Intent(getApplicationContext(), SpeechSearchDialog.class);
                    startActivityForResult(speechIntent, Constant.RC_INTENT_SPEECH_SEARCH);
                }

                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void animateFam(final FloatingActionMenu fam) {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(fam.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(fam.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(fam.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(fam.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                fam.getMenuIconView().setImageResource(fam.isOpened()
                        ? R.drawable.ic_search_white_24dp : R.drawable.ic_close_white_24dp);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        fam.setIconToggleAnimatorSet(set);

        fam.setMenuButtonHideAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_to_bottom));
        fam.setMenuButtonShowAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_from_bottom));

        fam.setClosedOnTouchOutside(true);
    }

    void search(String algorithm, String pattern) {
        ArrayList<ArrayList<Integer>> wordIndexesResult = new ArrayList<>();
        ArrayList<Integer> indexResult = new ArrayList<>();
        ArrayList<String> convertedLyric = new ArrayList<>();
        int index = 0;
        Double tDelta = 0.0;
        Long tStart, tEnd;

//        final String regex = "[^\\x20-\\x7E]";
        final String regexNonAlphabet = "[^\\x20-\\x7E]";
        final String regexMultipleSpace = "( ){2,99}";
        final String replacement = " ";

        pattern = pattern.replaceAll(regexNonAlphabet, replacement);
        pattern = pattern.replaceAll(regexMultipleSpace, "");
        pattern = pattern.toLowerCase();

        for (String item :
                mLyricList) {
            item = item.replaceAll(regexNonAlphabet, replacement);
            item = item.replaceAll(regexMultipleSpace, "");
            item = item.trim().toLowerCase();
            convertedLyric.add(item);
        }

        switch (algorithm) {
            case Constant.ALGORITHM_RAITA:
                // Search RaitaAlgorithm
                tStart = System.nanoTime();
                for (String item :
                        convertedLyric) {
                    RaitaAlgorithm raitaSearch = new RaitaAlgorithm();
                    if (raitaSearch.search(pattern, item)) {
                        indexResult.add(index);
                        wordIndexesResult.add(raitaSearch.indexes());
                    }
                    index++;
                }
                tEnd = System.nanoTime();
                tDelta = (tEnd - tStart) / 1E9;  // 1E9 = 10^9 = 1 000 000 000 (get value as seconds)

                raitaIndexResult = indexResult;
                break;

            case Constant.ALGORITHM_REVERSE_COLUSSI:
                tStart = System.nanoTime();
                for (String item :
                        convertedLyric) {
                    ReverseColussiAlgorithm reverseColussiSearch = new ReverseColussiAlgorithm();
                    if (reverseColussiSearch.search(pattern, item)) {
                        indexResult.add(index);
                        wordIndexesResult.add(reverseColussiSearch.indexes());
                    }
                    index++;
                }
                tEnd = System.nanoTime();
                tDelta = (tEnd - tStart) / 1E9;  // 1E9 = 10^9 = 1 000 000 000 (get value as seconds)

                reverseColussiIndexResult = indexResult;
                break;

            default:
                break;
        }

        switch (algorithm) {
            case Constant.ALGORITHM_RAITA:
                mRaita.updateData(indexResult, wordIndexesResult,
                        pattern, tDelta);
                break;

            case Constant.ALGORITHM_REVERSE_COLUSSI:
                mReverseColussi.updateData(indexResult, wordIndexesResult,
                        pattern, tDelta);
                break;

            default:
                break;
        }

        if (indexResult.size() == 0){
            Toast.makeText(getApplicationContext(), "'" + pattern + "' - Tidak ditemukan " + algorithm, Toast.LENGTH_SHORT).show();
        }
    }

    String cropPattern(String text, Context context) {
        if (countWords(text) > MAX_PATTERN){
//            https://stackoverflow.com/a/16854719/4917020
            Pattern pattern = Pattern.compile("([\\S]+\\s*){1,"+MAX_PATTERN+"}");
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) text = matcher.group();

            Toast.makeText(context, "Pattern > "+MAX_PATTERN+".\nPattern= '"+text+"'", Toast.LENGTH_LONG).show();
        }

        return text;
    }

    void resetList() {
        mRaita.setDefaultAdapter();
        mReverseColussi.setDefaultAdapter();
        Toast.makeText(getApplicationContext(), "Reset List", Toast.LENGTH_SHORT).show();
    }

    void showDifferenceResult() {
        reverseColussiIndexResult.removeAll(raitaIndexResult);
        if (!reverseColussiIndexResult.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int index : reverseColussiIndexResult) {
                sb.append(mTitleList.get(index)).append(", ");
            }
            Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_LONG).show();
        }
    }

    boolean micIsGranted() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED);
    }

//    https://stackoverflow.com/a/5864184/4917020
    static int countWords(String s) {
        int wordCount = 0;

        boolean word = false;
        int endOfLine = s.length() - 1;

        for (int i = 0; i < s.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
