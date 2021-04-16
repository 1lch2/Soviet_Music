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
 * 填充音乐列表的Adapter类<br>
 * 为存放音乐列表的RecyclerView提供数据，每个列表项内容来自Music对象
 *
 * @author lichenghao02
 * @since 2021/04/14
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    /** 音乐列表 */
    private List<Music> mMusicList;

    /** 自定义的RecyclerView项目点击接口 */
    private ItemClickInterface mItemClickInterface;

    /**
     * 用于持有View对象的ViewHolder类
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /** 列表项中ImageView组件 */
        ImageView musicCover;
        /** 列表项中的TextView组件 */
        TextView musicTitle;
        /** 整个列表项对应的View组件 */
        View musicView;

        /**
         * ViewHolder类的构造器
         *
         * @param view 每个列表项对应的View对象
         */
        public ViewHolder (View view) {
            super(view);
            musicCover = view.findViewById(R.id.music_cover);
            musicTitle = view.findViewById(R.id.music_title);
            musicView = view;
        }
    }

    /**
     * 为RecyclerView提供数据的Adapter
     *
     * @param musicList 填充数据来源，即音乐列表
     */
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
     * 在Activity中重写以实现代理
     */
    public interface ItemClickInterface {

        /**
         * 点击RecyclerView的列表项时调用的方法<br>
         * 在Activity中为点击事件绑定监听器并重写此方法，可以实现代理模式，以在Activity中调用Service的方法
         *
         * @param view  被点击项的View组件
         * @param music 被点击项对应的Music对象
         */
        void onItemClick (View view, Music music);
    }

    /**
     * 实现代理的方法
     *
     * @param itemClickInterface 在Activity中被重写的接口方法
     */
    public void realItemClick (ItemClickInterface itemClickInterface) {
        this.mItemClickInterface = itemClickInterface;
    }
}
