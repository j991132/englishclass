package com.social.englishclass;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

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

    public void pause() {
        if (mService != null) {
            mService.play();
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

    public void record() {
        if (mService != null) {
            mService.record();
        }
    }

    public void recordstop() {
        if (mService != null) {
            mService.recordstop();
        }
    }

    public void recordplay() {
        if (mService != null) {
            mService.recordplay();
        }
    }

    public void recordstopplay() {
        if (mService != null) {
            mService.recordstopplay();
        }
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
