package com.social.englishclass;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class AudioServiceInterface {
    private ServiceConnection mServiceConnection;
    private AudioService mService;

    public AudioServiceInterface(Context context) {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((AudioService.AudioServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceConnection = null;
                mService = null;
            }
        };
        context.bindService(new Intent(context, AudioService.class)
                .setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void setPlayList(ArrayList<Long> audioIds) {
        if (mService != null) {
            mService.setPlayList(audioIds);
        }
    }
    public void clearPlayList() {
        if (mService != null) {
            mService.clearPlayList();
        }
    }

    public void play(int position) {
        if (mService != null) {
            mService.play(position);

        }
    }

    public void play2(float a) {
        if (mService != null) {
            mService.play2(a);
        }
    }
    public void pcmplay() {
        if (mService != null) {
            mService.pcmplay();
        }
    }

    public void pause() {
        if (mService != null) {
            mService.play();
        }
    }

    public void stop() {
        if (mService != null) {
            mService.stop();
            mService.clearPlayList();
        }
    }

    public void forward() {
        if (mService != null) {
            mService.forward();
        }
    }

    public void rewind() {
        if (mService != null) {
            mService.rewind();
        }
    }

    public void togglePlay(float a) {
        if (isPlaying()) {
            mService.pause();
        } else {

            mService.play2(a);
        }
    }
    public void record3gp() {
        if (mService != null) {
            mService.record3gp();
        }
    }
    public void record() {
        if (mService != null) {
            mService.record();
        }
    }
    public void recrecordstop() {
        if (mService != null) {
            mService.recrecordstop();
        }
    }
    public void recordstop() {
        if (mService != null) {
            mService.recordstop();
        }
    }

    public void recordplay(String fname) {
        if (mService != null) {
            mService.recordplay(fname);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordpause() {
        if (mService != null) {
            mService.recordpause();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordresume() {
        if (mService != null) {
            mService.recordresume();
        }
    }

    public void recordstopplay() {
        if (mService != null) {
            mService.recordstopplay();
        }
    }

    public  void deletedialog(int position){
        mService.deletedialog(position);
    }

    public boolean isPlaying() {
        if (mService != null) {
            return mService.isPlaying();
        }
        return false;
    }

    public AudioAdapter.AudioItem getAudioItem() {
        if (mService != null) {
            return mService.getAudioItem();
        }
        return null;
    }


}
