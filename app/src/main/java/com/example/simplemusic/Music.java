package com.example.simplemusic;

/**
 * 音乐对象类。<br>
 * 一个Java Bean，字段包含音乐标题，文件路径和封面图
 *
 * @author 1lch2
 * @since 2021/04/13
 */
public class Music {

    /** 音乐的标题 */
    private String title;
    /** 音乐文件的路径 */
    private String path;
    /** 音乐对象在列表中的序号 */
    private int index;

    /**
     * 返回音乐标题
     *
     * @return 音乐标题
     */
    public String getTitle () {
        return title;
    }

    /**
     * 设定音乐标题<br>
     * 一般和文件同名，但是不带结尾的扩展名
     *
     * @param title 音乐标题
     */
    public void setTitle (String title) {
        this.title = title;
    }

    /**
     * 返回音乐的路径<br>
     * 存放在assets目录下时路径即为文件名，通过AssetFileDescriptor访问
     *
     * @return 音乐的路径
     */
    public String getPath () {
        return path;
    }

    /**
     * 设定音乐的路径<br>
     * 存放在assets目录下时，路径和文件名相同
     *
     * @param path 音乐的路径
     */
    public void setPath (String path) {
        this.path = path;
    }

    /**
     * 返回音乐的序号<br>
     *
     * @return 音乐在列表中的序号
     */
    public int getIndex () {
        return index;
    }

    /**
     * 设定音乐的序号<br>
     * 此处的序号为遍历assets目录时赋值的，和每一项在列表中的位置相同
     *
     * @param index 音乐在列表中的序号
     */
    public void setIndex (int index) {
        this.index = index;
    }

    @Override
    public String toString () {
        return "Music{" +
                "title='" + title + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
