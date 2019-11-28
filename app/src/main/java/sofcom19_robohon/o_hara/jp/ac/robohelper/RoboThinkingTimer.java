package sofcom19_robohon.o_hara.jp.ac.robohelper;

import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;

public class RoboThinkingTimer extends TimerTask {
    private Handler handler;
    private Context context;

    RoboThinkingTimer(Context context_C){
        handler = new Handler();
        context = context_C;
    }

    @Override
    public void run() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)context).speakThinking();
            }
        });
    }
}
