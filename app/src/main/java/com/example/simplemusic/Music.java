/**
* @author lichenghao02
* @since 2021/04/13
*/
package com.example.simplemusic;

import android.graphics.Bitmap;

/**
 * 音乐对象类
 * 字段包含音乐标题，文件路径和封面图
 */
public class Music {

    private String title;
    private String path;
    private Bitmap cover;

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getPath () {
        return path;
    }

    public void setPath (String path) {
        this.path = path;
    }

    public Bitmap getCover () {
        return cover;
    }

    public void setCover (Bitmap cover) {
        this.cover = cover;
    }

    @Override
    public String toString () {
        return "Music{" +
                "title='" + title + '\'' +
                ", path='" + path + '\'' +
                ", cover=" + cover +
                '}';
    }
}
