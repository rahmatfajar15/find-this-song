package skrip.si.findthissong.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import skrip.si.findthissong.R;

/**
 * Show Lyrics Adapter
 * Created by Fajar on 6/29/2017.
 */

public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.ViewHolder>{
    private ArrayList<String> mLyricDataSet;
    private ArrayList<String> mTitleDataSet;
    private LyricsAdapterListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView lyricText, titleText;
        LinearLayout rowContainer;

        ViewHolder(View v) {
            super(v);
            rowContainer = v.findViewById(R.id.layout);
            lyricText = v.findViewById(R.id.text_item_lyric);
            titleText = v.findViewById(R.id.text_item_title);
        }
    }

    public LyricsAdapter(ArrayList<String> titleDataSet, ArrayList<String> lyricDataSet, LyricsAdapterListener listener) {
        this.mTitleDataSet = titleDataSet;
        this.mLyricDataSet = lyricDataSet;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_lyric, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleText.setText(mTitleDataSet.get(position));
        holder.lyricText.setText(mLyricDataSet.get(position));
        applyClick(holder, position);
    }

    private void applyClick(ViewHolder holder, final int position){
        holder.rowContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLyricRowClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLyricDataSet.size();
    }

    public interface LyricsAdapterListener {
        void onLyricRowClicked(int position);
    }
}
