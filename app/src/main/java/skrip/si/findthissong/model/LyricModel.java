package skrip.si.findthissong.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.IgnoreExtraProperties;

import skrip.si.findthissong.R;

/**
 * Model Class for getting data from Firebase
 * Created by Fajar on 8/16/2017.
 */

public class LyricModel {

    private String lyric;
    private String artist;
    private String title;

    public LyricModel(){}

    public LyricModel(String title, String artist, String lyric){
        this.title = title;
        this.artist = artist;
        this.lyric = lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
    public String getLyric(){
        return lyric;
    }


    public String getTitleFormatted() {
        return getArtist() + " - " + getTitle();
    }


    public static class LyricHolder extends RecyclerView.ViewHolder {
        private final TextView mTextTitle;
        private final TextView mTextLyric;

        public LyricHolder(View itemView) {
            super(itemView);
            mTextTitle = itemView.findViewById(R.id.text_item_title);
            mTextLyric = itemView.findViewById(R.id.text_item_lyric);

//                https://stackoverflow.com/a/41629505/4917020
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }

        public interface ClickListener {
            void onItemClick(View view, int position);
        }

        private LyricHolder.ClickListener mClickListener;

        public void setOnClickListener(LyricHolder.ClickListener clickListener) {
            mClickListener = clickListener;
        }


        public void setTitle(String title) {
            mTextTitle.setText(title);
        }

        public void setLyric(String lyric) {
            mTextLyric.setText(lyric);
        }

    }
}

