package com.example.boundservice23032021.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import com.example.boundservice23032021.R;
import com.example.boundservice23032021.adapter.SongAdapter;
import com.example.boundservice23032021.interfaces.OnItemClickSong;
import com.example.boundservice23032021.interfaces.OnListenCurrentTimeSong;
import com.example.boundservice23032021.model.Song;
import com.example.boundservice23032021.service.MyService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRcvSong;
    List<Song> mListSong;
    SongAdapter mSongAdapter;
    String[] arrNameSong = {"chieuthuhoabongnang", "codocvuong", "hoahaiduong", "hoyeuaimatroi", "tetdongday"};
    MediaMetadataRetriever mMetaRetriever;
    Boolean mBound;
    TextView mTvCurrentTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRcvSong = findViewById(R.id.recyclerViewSong);
        mTvCurrentTime = findViewById(R.id.textviewtimesong);
        mListSong = new ArrayList<>();

        mListSong.add(new Song("Chiều thu họa bóng nàng", R.raw.chieuthuhoabongnang, 0));
        mListSong.add(new Song("Cô độc vương", R.raw.codocvuong, 0));
        mListSong.add(new Song("Hoa hải đường", R.raw.hoahaiduong, 0));
        mListSong.add(new Song("Họ yêu ai mất rồi", R.raw.hoyeuaimatroi, 0));
        mListSong.add(new Song("Tết đong đầy", R.raw.tetdongday, 0));

        for (int i = 0; i < arrNameSong.length; i++) {
            Uri mediaPath = Uri.parse("android.resource://com.example.boundservice23032021/raw/" + arrNameSong[i]);
            mMetaRetriever = new MediaMetadataRetriever();
            mMetaRetriever.setDataSource(this, mediaPath);
            long duration = Long.parseLong(mMetaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            mListSong.get(i).setDuration(duration);
        }

        mSongAdapter = new SongAdapter(mListSong);


        mRcvSong.setAdapter(mSongAdapter);

        mSongAdapter.setOnItemClickSong(new OnItemClickSong() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(MainActivity.this, MyService.class);
                intent.putExtra("objectsong",mListSong.get(position));
                startService(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this,MyService.class);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBound = true;
            MyService.MyBinder myBinder = (MyService.MyBinder) iBinder;
            myBinder.getService().setOnListCurrentTimeSong(new OnListenCurrentTimeSong() {
                @Override
                public void onCurrentTime(long time) {
                    long minus = (time / 60000) ;
                    long second = (time % 60000) / 1000;
                    mTvCurrentTime.setText("0" +minus + " : " + (second >= 10 ? second : "0" +second));
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }
}