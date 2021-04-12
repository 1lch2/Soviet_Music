/**
*
* @author lichenghao02
* @since 2021/04/12
*/
package com.example.simplemusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    // 初始化所有的固定按钮的引用
    private ImageView smallAlbumCover = (ImageView) findViewById(R.id.album_cover_small);
    private Button playButton = (Button) findViewById(R.id.button_play);
    private Button previousButton = (Button) findViewById(R.id.button_previous);
    private Button nextButton = (Button) findViewById(R.id.button_next);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 点击歌曲封面小图打开播放器界面
        smallAlbumCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainPlayer(v);
            }
        });
    }

    /**
     * 打开播放器界面
     * @param view 被点击的View
     */
    private void openMainPlayer(View view) {
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        startActivity(intent);
    }
}