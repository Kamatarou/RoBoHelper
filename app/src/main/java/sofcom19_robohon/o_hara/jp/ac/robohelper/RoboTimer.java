package sofcom19_robohon.o_hara.jp.ac.robohelper;

import android.content.Context;

import java.util.TimerTask;
import android.os.Handler;

public class RoboTimer  extends TimerTask{
    private Handler handler;
    private Context context;

    RoboTimer(Context context_C){
        handler = new Handler();
        context = context_C;
    }

    public void run(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)context).FaceDitect_SwingHead();
            }
        });
    }

}
