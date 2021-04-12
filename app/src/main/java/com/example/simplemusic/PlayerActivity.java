/**
*
* @author lichenghao02
* @since 2021/04/12
*/
package com.example.simplemusic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

public class PlayerActivity extends AppCompatActivity {

    // 初始化所有的固定按钮的引用
    private ImageView smallAlbumCover = (ImageView) findViewById(R.id.album_cover);
    private Button playButton = (Button) findViewById(R.id.button_play_player);
    private Button previousButton = (Button) findViewById(R.id.button_previous_player);
    private Button nextButton = (Button) findViewById(R.id.button_next_player);
    private SeekBar progressSeekBar = (SeekBar) findViewById(R.id.progressbar);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
    }
}