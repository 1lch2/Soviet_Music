package com.example.simplemusic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author lichenghao02
 * @since 2021/04/14
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private List<Music> mMusicList;
    private ItemClickInterface mItemClickInterface;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView musicCover;
        TextView musicTitle;
        View musicView;

        public ViewHolder(View view) {
            super(view);
            musicCover = (ImageView) view.findViewById(R.id.music_cover);
            musicTitle = (TextView) view.findViewById(R.id.music_title);
            musicView = view;
        }

        public TextView getMusicTitle () {
            return musicTitle;
        }
    }

    public MusicAdapter (List<Music> musicList) {
        mMusicList = musicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        // 为每个列表项添加点击监听器，点击后播放对应音乐
        viewHolder.musicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                int position = viewHolder.getAdapterPosition();
                Music music = mMusicList.get(position);

                mItemClickInterface.onItemClick(v, music);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder (@NonNull ViewHolder holder, int position) {
        Music music = mMusicList.get(position);
        holder.musicCover.setImageResource(R.drawable.cover);
        holder.musicTitle.setText(music.getTitle());
    }

    @Override
    public int getItemCount () {
        return mMusicList.size();
    }

    /**
     * 自定义的点击事件接口
     * 在
     */
    public interface ItemClickInterface {
        public void onItemClick(View view, Music music);
    }

    public void realItemClick(ItemClickInterface itemClickInterface) {
        this.mItemClickInterface = itemClickInterface;
    }

}
