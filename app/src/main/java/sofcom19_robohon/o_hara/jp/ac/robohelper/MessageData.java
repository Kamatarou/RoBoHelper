package sofcom19_robohon.o_hara.jp.ac.robohelper;

public class MessageData {
    public String device;
    public String message;
    public String firebasekey;
    public boolean isSpeech;

    public MessageData(String key,String dev, String msg){
        this.device = dev;
        this.message = msg;
        this.firebasekey  = key;
        this.isSpeech = true;
    }

    public MessageData(){
        //noFunction
    }

    public String getfirebasekey(){
        return firebasekey;
    }

    public void setfirebasekey(String firebasekey){
        this.firebasekey = firebasekey;
    }

    public String getDevice(){
        return device;
    }

    public void setDevice(String device){
        this.device = device;
    }

    public String getmessage(){
        return message;
    }

    public void setmessages(String message){
        this.message = message;
    }

    public boolean getisSpeech(){
        return isSpeech;
    }

    public void setisSpeech(boolean isSpeech){
        this.isSpeech = isSpeech;
    }

}
