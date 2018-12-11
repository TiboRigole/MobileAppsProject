package com.example.tibo.myrides.HelperPackage;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

public class MyService extends Service {

    PlayMP3 mp3= new PlayMP3();
    private final MediaPlayer player= new MediaPlayer();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!player.isPlaying()) {
            mp3.execute();
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private class PlayMP3 extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                AssetFileDescriptor afd =getAssets().openFd("bensound-downtown.mp3");

                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                player.prepare();
                player.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }
}
