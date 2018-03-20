package com.bupt.run.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * 当前DEMO的播报方式是队列模式。其原理就是依次将需要播报的语音放入链表中，播报过程是从头开始依次往后播报。
 * <p>
 * 导航SDK原则上是不提供语音播报模块的，如果您觉得此种播报方式不能满足你的需求，请自行优化或改进。
 */
public class TTSController implements ICallBack {


    @Override
    public void onCompleted(int code) {
        if (handler != null) {
            handler.obtainMessage(CHECK_TTS_PLAY).sendToTarget();
        }
    }

    public static enum TTSType {
        /**
         * 讯飞语音
         */
        IFLYTTS,
        /**
         * 系统语音
         */
        SYSTEMTTS;
    }

    public static TTSController ttsManager;
    private Context mContext;
    private TTS tts = null;

    private IFlyTTS iflyTTS = null;
    private LinkedList<String> wordList = new LinkedList<String>();
    private final int TTS_PLAY = 1;
    private final int CHECK_TTS_PLAY = 2;
    private boolean allowSpeak = true;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TTS_PLAY:
                    if (tts != null && wordList.size() > 0 && allowSpeak) {
                        tts.playText(wordList.removeFirst());
                    }
                    break;
                case CHECK_TTS_PLAY:
                    if (!tts.isPlaying()) {
                        handler.obtainMessage(1).sendToTarget();
                    }
                    break;
            }

        }
    };

    public void setTTSType(TTSType type) {
       // if (type == TTSType.SYSTEMTTS) {
     //       tts = systemTTS;
      //  } else {
            tts = iflyTTS;
    //    }
        tts.setCallback(this);
    }

    private TTSController(Context context) {
        mContext = context.getApplicationContext();
     //   systemTTS = SystemTTS.getInstance(mContext);
        iflyTTS = IFlyTTS.getInstance(mContext);
        tts = iflyTTS;
        init();
    }

    public void init() {
      /*  if (systemTTS != null) {
            systemTTS.init();
        }*/
        if (iflyTTS != null) {
            iflyTTS.init();
        }
        tts.setCallback(this);
    }

    public static TTSController getInstance(Context context) {
        if (ttsManager == null) {
            ttsManager = new TTSController(context);
        }
        return ttsManager;
    }

    public void stopSpeaking() {
       /* if (systemTTS != null) {
            systemTTS.stopSpeak();
        }*/
        if (iflyTTS != null) {
            iflyTTS.stopSpeak();
        }
        wordList.clear();
    }

    public void pauseSpeaking() {
        if (iflyTTS != null) {
            iflyTTS.pauseSpeak();
        }
    }

    public void resumeSpeaking() {
        if (iflyTTS != null) {
            iflyTTS.resumeSpeak();
        }
    }

    public void destroy() {
    /*    if (systemTTS != null) {
            systemTTS.destroy();
        }*/
        if (iflyTTS != null) {
            iflyTTS.destroy();
        }
        ttsManager = null;
    }

    public void say(final List<String> whatToSay, long waitTime) {
        for (String playText : whatToSay) {
            iflyTTS.playText(playText);
        }
    }

    public void say(final String playText) {
        if (wordList != null){
            wordList.addLast(playText);
        };
        handler.obtainMessage(CHECK_TTS_PLAY).sendToTarget();
        //iflyTTS.playText(playText);
    }

    public void saveText(String text) {
        wordList.add(text);
    }

    public void noMoreTalk() {
        if (iflyTTS.isPlaying()) {
            iflyTTS.stopSpeak();
        }
        wordList.clear();
    }

    public boolean isAllowSpeak() {
        return allowSpeak;
    }

    public void setAllowSpeak(boolean allowSpeak) {
        this.allowSpeak = allowSpeak;
    }
}
