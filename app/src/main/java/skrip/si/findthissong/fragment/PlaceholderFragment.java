package skrip.si.findthissong.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import skrip.si.findthissong.R;
import skrip.si.findthissong.helper.Constant;
import skrip.si.findthissong.activity.LyricActivity;
import skrip.si.findthissong.adapter.LyricsAdapter;
import skrip.si.findthissong.model.LyricModel;
import skrip.si.findthissong.model.LyricModel.LyricHolder;

/**
 * Display list on a fragment
 * Created by Fajar on 7/16/2017.
 */

public class PlaceholderFragment extends Fragment implements LyricsAdapter.LyricsAdapterListener {

    ArrayList<String> mTitleList = new ArrayList<>();
    ArrayList<String> mLyricList = new ArrayList<>();
    ArrayList<String> mTitleListResult = new ArrayList<>();
    ArrayList<String> mLyricListResult = new ArrayList<>();
    ArrayList<ArrayList<Integer>> mIndexesList = new ArrayList<>();
    Double mRunningTime;
    int mPatternLength = 0;
    int mFirebaseListSize = 0;
    int mCurrentItemSize = 0;

    TextView timeText;
    TextView itemCountText;
    RecyclerView recyclerView;

    RelativeLayout patternLayout;
    TextView patternText;

    LyricsAdapter mAdapter;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<LyricModel> options;

    Boolean firstLaunch = true;

    public PlaceholderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lyric_list, container, false);

        timeText = rootView.findViewById(R.id.text_search_time);
        itemCountText = rootView.findViewById(R.id.text_item_count);
        recyclerView = rootView.findViewById(R.id.recycler);

        patternLayout = rootView.findViewById(R.id.layout_pattern);
        patternText = rootView.findViewById(R.id.text_pattern);

        patternLayout.setVisibility(View.GONE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

//        Get query data
        Query queryLyrics = FirebaseDatabase.getInstance().getReference("Lyrics").orderByChild("artist");
        options = new FirebaseRecyclerOptions.Builder<LyricModel>()
                .setQuery(queryLyrics, LyricModel.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<LyricModel, LyricHolder> (options) {
            @Override
            public LyricHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_lyric, parent, false);

                LyricHolder lyricHolder = new LyricHolder(view);
                lyricHolder.setOnClickListener(new LyricHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        onLyricRowClicked(position);
                    }
                });

                return lyricHolder;
            }

            @Override
            protected void onBindViewHolder(LyricHolder holder, int position, LyricModel lyricModel) {
                String title = lyricModel.getTitleFormatted();
                String lyric = lyricModel.getLyric();

                holder.setTitle(title);
                holder.setLyric(lyric);
            }

            @Override
            public void onDataChanged() {
                mTitleList.clear();
                mLyricList.clear();

                mFirebaseListSize = getItemCount();
                for (int i=0; i < mFirebaseListSize; i++){
                    LyricModel selectedItem = getSnapshots().get(i);
                    mTitleList.add(selectedItem.getTitleFormatted());
                    mLyricList.add(selectedItem.getLyric());
                }

                if (firstLaunch) {
                    setDefaultAdapter();
                    firstLaunch = false;
                }
            }
        };

        setDefaultAdapter();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public void setDefaultAdapter() {
        mTitleListResult = mTitleList;
        mLyricListResult = mLyricList;
        mPatternLength = 0;
        mIndexesList = getIndexes(mFirebaseListSize);

        patternLayout.setVisibility(View.GONE);

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        timeText.setText(getString(R.string.time_format, 0.00));
        mCurrentItemSize = mFirebaseListSize;
        updateItemCount();
    }

    private void updateItemCount() {
        itemCountText.setText(getString(R.string.item_count_format, mCurrentItemSize));
    }

    public void updateData(ArrayList<Integer> indexResult, ArrayList<ArrayList<Integer>> indexes,
                           String pattern, Double runningTime) {

        ArrayList<String> titleListTemp = new ArrayList<>();
        ArrayList<String> lyricListTemp = new ArrayList<>();

        for (Integer position :
                indexResult) {
            titleListTemp.add(mTitleList.get(position));
            lyricListTemp.add(mLyricList.get(position));
        }

        mTitleListResult = titleListTemp;
        mLyricListResult = lyricListTemp;

        mIndexesList = indexes;
        mPatternLength = pattern.length();
        mRunningTime = runningTime;

        patternText.setText(pattern);
        patternLayout.setVisibility(View.VISIBLE);

        mAdapter = new LyricsAdapter(mTitleListResult, mLyricListResult, this);
        recyclerView.setAdapter(mAdapter);

        timeText.setText(getString(R.string.time_format, mRunningTime));
        mCurrentItemSize = mTitleListResult.size();
        updateItemCount();
    }

    @Override
    public void onLyricRowClicked(int position) {
        Bundle lyricData = new Bundle();
        lyricData.putString(Constant.ARG_TITLE, mTitleListResult.get(position));
        lyricData.putString(Constant.ARG_LYRIC, mLyricListResult.get(position));
        lyricData.putInt(Constant.ARG_PATTERN_LENGTH, mPatternLength);
        lyricData.putIntegerArrayList(Constant.ARG_INDEXES, mIndexesList.get(position));

        Intent lyricIntent = new Intent(getContext(), LyricActivity.class);
        lyricIntent.putExtras(lyricData);
        startActivity(lyricIntent);
    }


    ArrayList<ArrayList<Integer>> getIndexes(int count) {
        ArrayList<Integer> tempIndexes = new ArrayList<>();
        tempIndexes.add(0);

        ArrayList<ArrayList<Integer>> indexData = new ArrayList<>();
        for (int i=0; i < count ; i++)
            indexData.add(tempIndexes);
        return indexData;
    }
}
