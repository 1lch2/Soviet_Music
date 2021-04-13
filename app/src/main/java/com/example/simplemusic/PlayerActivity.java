/**
 * @author lichenghao02
 * @since 2021/04/12
 */
package com.example.simplemusic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // 初始化所有的固定按钮的引用
        ImageView smallAlbumCover = (ImageView) findViewById(R.id.album_cover);
        Button playButton = (Button) findViewById(R.id.button_play_player);
        Button previousButton = (Button) findViewById(R.id.button_previous_player);
        Button nextButton = (Button) findViewById(R.id.button_next_player);
        SeekBar progressSeekBar = (SeekBar) findViewById(R.id.progressbar);
    }
}