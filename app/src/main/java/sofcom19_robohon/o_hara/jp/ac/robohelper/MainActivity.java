package sofcom19_robohon.o_hara.jp.ac.robohelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.sharp.android.rb.addressbook.AddressBookCommonUtils;
import jp.co.sharp.android.rb.addressbook.AddressBookManager;
import jp.co.sharp.android.rb.addressbook.AddressBookVariable.AddressBookData;
import jp.co.sharp.android.rb.addressbook.AddressBookVariable.OwnerProfileData;
import sofcom19_robohon.o_hara.jp.ac.robohelper.customize.ScenarioDefinitions;
import sofcom19_robohon.o_hara.jp.ac.robohelper.util.VoiceUIManagerUtil;
import sofcom19_robohon.o_hara.jp.ac.robohelper.util.VoiceUIVariableUtil;
import sofcom19_robohon.o_hara.jp.ac.robohelper.util.VoiceUIVariableUtil.VoiceUIVariableListHelper;
import jp.co.sharp.android.rb.projectormanager.ProjectorManagerServiceUtil;
import jp.co.sharp.android.voiceui.VoiceUIManager;
import jp.co.sharp.android.voiceui.VoiceUIVariable;
import jp.co.sharp.android.rb.camera.FaceDetectionUtil;
import jp.co.sharp.android.rb.camera.ShootMediaUtil;
import jp.co.sharp.android.rb.rbdance.DanceUtil;
import jp.co.sharp.android.rb.messaging.MessagingUtil;
import jp.co.sharp.android.rb.action.ActionUtil;
import jp.co.sharp.android.rb.song.SongUtil;


public class MainActivity extends Activity implements MainActivityVoiceUIListener.MainActivityScenarioCallback {
    public static final String TAG = MainActivity.class.getSimpleName();
    /**
     * 顔認識結果通知Action定義.
     */
    public static final String ACTION_RESULT_FACE_DETECTION = "sofcom19_robohon.o_hara.jp.ac.robohelper.action.RESULT_FACE_DETECTION";
    /**
     * 写真/動画撮影結果通知Action定義.
     */
    public static final String ACTION_RESULT_TAKE_PICTURE = "sofcom19_robohon.o_hara.jp.ac.robohelper.action.RESULT_TAKE_PICTURE";
    /**
     * 動画撮影結果通知Action定義.
     */
    public static final String ACTION_RESULT_REC_MOVIE = "sofcom19_robohon.o_hara.jp.ac.robohelper.action.RESULT_REC_MOVIE";
    /**
     * ダンス実行結果通知用Action定義.
     */
    public static final String ACTION_RESULT_DANCE = "sofcom19_robohon.o_hara.jp.ac.robohelper.action.RESULT_DANCE";
    /**
     * メッセージ送信結果通知用Action定義.
     */
    public static final String ACTION_RESULT_MESSAGE = "sofcom19_robohon.o_hara.jp.ac.robohelper.action.RESULT_MESSAGE";
    /**
     * アクション実行結果通知用Action定義.
     */
    public static final String ACTION_RESULT_ACTION = "sofcom19_robohon.o_hara.jp.ac.robohelper.action.RESULT_ACTION";
    /**
     * 歌実行結果通知用Action定義.
     */
    public static final String ACTION_RESULT_SONG = "sofcom19_robohon.o_hara.jp.ac.robohelper.action.RESULT_SONG";
    /**
     * 音声UI制御.
     */
    private VoiceUIManager mVoiceUIManager = null;
    /**
     * 音声UIイベントリスナー.
     */
    private MainActivityVoiceUIListener mMainActivityVoiceUIListener = null;
    /**
     * 音声UIの再起動イベント検知.
     */
    private VoiceUIStartReceiver mVoiceUIStartReceiver = null;
    /**
     * ホームボタンイベント検知.
     */
    private HomeEventReceiver mHomeEventReceiver;
    /**
     * プロジェクター状態変化イベント検知.
     */
    private ProjectorEventReceiver mProjectorEventReceiver;
    /**
     * プロジェクタ照射中のWakelock.
     */
    private android.os.PowerManager.WakeLock mWakelock;
    /**
     * 排他制御用.
     */
    private Object mLock = new Object();
    /**
     * プロジェクタ照射状態.
     */
    private boolean isProjected = false;
    /**
     * カメラ結果取得用.
     */
    private CameraResultReceiver mCameraResultReceiver;
    /**
     * ダンス実行結果取得用.
     */
    private DanceResultReceiver mDanceResultReceiver;
    /**
     * メッセージ送信結果取得用.
     */
    private MessageResultReceiver mMessageResultReceiver;
    /**
     * アクション実行結果取得用.
     */
    private ActionResultReceiver mActionResultReceiver;
    /**
     * 歌実行結果取得用.
     */
    private SongResultReceiver mSongResultReceiver;
    //firebaseリファレンス⇒https://firebase.google.com/docs/database/android/read-and-write?hl=ja
    /**
    * FirebaseReferenceを取得する
    */
    private DatabaseReference mDatabase;
    /**
    * FirebaseRealTimedatabseへの階層指定につかう
    */
    private String Fkey;
    /**
     * セリフを格納する
     */
    private String mSpeechTxt = "";
    /**
     * 会話相手の管理を行う
     */
    private boolean stat;
    /**
     * 会話の切り返しフラグを設定
     **/
    private boolean isEnd_Conversation = false;
    /**
     * 伝言板の内容を保存するHashMap
     */
    private HashMap<String, Object> MsgMap;
    /**
     * 伝言板の発話の管理をする
     */
    private boolean isBoard;
    /**
    * 伝言板の対象人物名を格納する
    */
    private String mBoardPerson = "";
    /**
     * 普段の呼びかけの管理をする
     */
    private boolean isUsually;
    /**
     * 認識した顔の人物名を格納
     */
    private String mFaceName = "";
    /**
    * タイマークラス
    */
    private RoboTimer roboTimer;
    private Timer timer;
    /**
     * インターバルタイマー定数
     */
    final private long INTERVAL_TIMER = 1000 * 60 * 20;
    /**
     * ANDROID_IDの格納を行う
     */
    public String UUID;
    /**
     * 初回起動の状態管理諸々
     */
    private int dataInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        //タイトルバー設定.
        setupTitleBar();

        //ホームボタンの検知登録.
        mHomeEventReceiver = new HomeEventReceiver();
        IntentFilter filterHome = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeEventReceiver, filterHome);

        //VoiceUI再起動の検知登録.
        mVoiceUIStartReceiver = new VoiceUIStartReceiver();
        IntentFilter filter = new IntentFilter(VoiceUIManager.ACTION_VOICEUI_SERVICE_STARTED);
        registerReceiver(mVoiceUIStartReceiver, filter);

        //TODO プロジェクタイベントの検知登録(プロジェクター利用時のみ).
        //setProjectorEventReceiver();

        //カメラ連携起動結果取得用レシーバー登録(カメラ利用時のみ).
        mCameraResultReceiver = new CameraResultReceiver();
        IntentFilter filterCamera = new IntentFilter(ACTION_RESULT_TAKE_PICTURE);
        filterCamera.addAction(ACTION_RESULT_REC_MOVIE);
        filterCamera.addAction(ACTION_RESULT_FACE_DETECTION);
        registerReceiver(mCameraResultReceiver, filterCamera);

        //TODO ダンス連携起動結果取得用レシーバー登録(ダンス利用時のみ).
        //mDanceResultReceiver = new DanceResultReceiver();
        //IntentFilter filterDance = new IntentFilter(ACTION_RESULT_DANCE);
        //registerReceiver(mDanceResultReceiver, filterDance);

        //TODO メッセージ連携起動結果取得用レシーバー登録(メッセージ利用時のみ).
        //mMessageResultReceiver = new MessageResultReceiver();
        //IntentFilter filterMessage = new IntentFilter(ACTION_RESULT_MESSAGE);
        //registerReceiver(mMessageResultReceiver, filterMessage);

        //TODO アクション連携起動結果取得用レシーバー登録(アクション利用時のみ).
        //mActionResultReceiver = new ActionResultReceiver();
        //IntentFilter filterAction = new IntentFilter(ACTION_RESULT_ACTION);
        //registerReceiver(mActionResultReceiver, filterAction);

        //TODO 歌連携起動結果取得用レシーバー登録(歌利用時のみ).
        //mSongResultReceiver = new SongResultReceiver();
        //IntentFilter filterSong = new IntentFilter(ACTION_RESULT_SONG);
        //registerReceiver(mSongResultReceiver, filterSong);


        //Firebase Database用のレシーバ登録（？）
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // AndroidIDの代入
        UUID =  android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        //初回起動判定諸々
        /*
        SharedPreferences sharedPreferences = getSharedPreferences("MainSetting", MODE_PRIVATE);
        dataInt = sharedPreferences.getInt("dataIPT", 0);
        dataInt++;
        SharedPreferences.Editor edit = sharedPreferences.edit();
        if(dataInt == 1){
            Log.d("Log0","初回起動時です");
            //ここに初回時の動作を記述
            edit.putInt(dataIntPreTag,dataInt).apply();
        }else{
            Log.d("Log0",dataInt+"回目の起動です");
            edit.putInt(dataIntPreTag,dataInt).apply();
        }*/

        //伝言板用のHashMapの初期化
        MsgMap = new HashMap<>();
        //伝言板の初期値代入
        isBoard = false;
        //普段の会話の初期値代入
        isUsually = false;
        //タイマー初期化
        timer = new Timer();
        roboTimer = new RoboTimer(this);
        //ディレイ20分後に初発火、その後20分ごとに発火
        timer.scheduleAtFixedRate(roboTimer, INTERVAL_TIMER , INTERVAL_TIMER);
        //デバッグ用
        //timer.scheduleAtFixedRate(roboTimer, 1000 * 10 , 1000 * 30);

        //発話ボタンの実装.
        Button Button = (Button) findViewById(R.id.accost);
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVoiceUIManager != null) {
                    VoiceUIVariableUtil.VoiceUIVariableListHelper helper = new VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_HELLO);
                    VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
                }
            }
        });

        Button btn2 = findViewById(R.id.btn1);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //basicWrite("こんにちは！");
                //sendMyAPI("こんにちは");
                //Toast.makeText(getApplicationContext(),"UUID:" + UUID, Toast.LENGTH_LONG).show();
                //speakUsually();
                sendBroadcast(getIntentForFaceDetection("TRUE"));
                //speakMessageBoard();
                //Log.d(TAG, "onClick: SwitchTalkFlg->" + isEnd_Conversation);
                //sendMyAPI("てすととうるふ","Toto Wolf");
            }
        });



    }

    public void onStart(){
        super.onStart();
        Log.v("WP", "onStatr()");

        //サーバーが更新されたら呼び出されるリスナーを用意
        mDatabase.child("chat").addValueEventListener(new ValueEventListener() {
            String FastVal ="";
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String , Object> Array_Index = new HashMap<>();
                int Index = 0;
                Long length;
                Array_Index.clear();

                length = dataSnapshot.getChildrenCount() - 1;
                Log.d(TAG, "onData length: " + length);
                //TestTemplate testTemplate = dataSnapshot.getValue(TestTemplate.class);
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Array_Index.put(String.valueOf(Index), ds.getValue());
                    Index++;
                }

                try {
                    JSONObject jsonObject = new JSONObject(Array_Index);
                    for(Long L=length; L >= 0; L--) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject(L.toString());
                        Log.d(TAG, "onDataChange: "+ jsonObject1);
                        Fkey = jsonObject1.getString("firebasekey");
                        if (!(jsonObject1.getString("device").equals("えーた")) ) {
                            Log.d(TAG, "onData isspeech->: " + jsonObject1.getString("isSpeech").equals("true"));
                            if(jsonObject1.getString("isSpeech").equals("true")) {
                                //デバイスがAndroid以外から発信したもので発話フラグが消えてないものを発話する
                                FastVal = jsonObject1.getString("message");
                            }else{
                                FastVal = "";
                            }
                            //既に発話した(発話フラグが消えてる）ものはスルーする
                            break;
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                //発話する文字を代入
                mSpeechTxt = FastVal;
                Log.d(TAG, "onData mSpeechTxt: " + mSpeechTxt);
                if(!mSpeechTxt.equals("")) {
                    basicSpeech();
                }

                //Log.d(TAG, "post is :"+ FastVal);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value :"+ databaseError.toException());
            }
        });

        //会話をボットで行うかチャットで行うかのフラグ管理に変更がある場合
        mDatabase.child("stats").child("isBot").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    stat = dataSnapshot.getValue(boolean.class);
                    Log.d(TAG, "onData talking to: " + stat);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value :"+ databaseError.toException());
            }
        });

        //伝言板の内容を取得
        mDatabase.child("msgboard").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int Index = 0;
                Long length;
                MsgMap.clear();

                length = dataSnapshot.getChildrenCount() - 1;
                Log.d(TAG, "onData length: " + length);
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    MsgMap.put(String.valueOf(Index), ds.getValue());
                    Index++;
                    for(DataSnapshot dss : ds.getChildren()){
                        //Log.d(TAG, "onDataChange: msgPair-> " + dss.getKey());
                        //Log.d(TAG, "onDataChange: msgBoard-> " + dss.getValue());
                        MsgMap.put(dss.getKey(),dss.getValue());
                    }
                }

                //未発話の場合、喋る。ただし、初回起動時は喋らないように設定
                Log.d(TAG, "speak -> "+MsgMap.get("isListen").toString().equals("false")+" : "+isBoard);
                if(MsgMap.get("isListen").toString().equals("false")  && isBoard){
                    speakMessageBoard();
                    isUsually = false;
                }
                else if(isUsually){
                    speakUsually();
                }
                else {
                    //初回起動の処理、起動以降の伝言板の取得を行う
                    Log.d(TAG, "onDataChange: Rejected SpeechBoard");
                    //isBoard = false;

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value :"+ databaseError.toException());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");

        //VoiceUIManagerのインスタンス取得.
        if (mVoiceUIManager == null) {
            mVoiceUIManager = VoiceUIManager.getService(getApplicationContext());
        }
        //MainActivityVoiceUIListener生成.
        if (mMainActivityVoiceUIListener == null) {
            mMainActivityVoiceUIListener = new MainActivityVoiceUIListener(this);
        }
        //VoiceUIListenerの登録.
        VoiceUIManagerUtil.registerVoiceUIListener(mVoiceUIManager, mMainActivityVoiceUIListener);

        //Scene有効化.
        VoiceUIManagerUtil.enableScene(mVoiceUIManager, ScenarioDefinitions.SCENE_COMMON);
        VoiceUIManagerUtil.enableScene(mVoiceUIManager, ScenarioDefinitions.SCENE01);



    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");

        //バックに回ったら発話を中止する.
        VoiceUIManagerUtil.stopSpeech();

        //VoiceUIListenerの解除.
        VoiceUIManagerUtil.unregisterVoiceUIListener(mVoiceUIManager, mMainActivityVoiceUIListener);

        //Scene無効化.
        VoiceUIManagerUtil.disableScene(mVoiceUIManager, ScenarioDefinitions.SCENE_COMMON);
        VoiceUIManagerUtil.disableScene(mVoiceUIManager, ScenarioDefinitions.SCENE01);

        //デフォルトの言語設定に戻す
        Locale locale = Locale.getDefault();
        VoiceUIManagerUtil.setAsr(mVoiceUIManager, locale);
        VoiceUIManagerUtil.setTts(mVoiceUIManager, locale);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");

        //ホームボタンの検知破棄.
        this.unregisterReceiver(mHomeEventReceiver);

        //VoiceUI再起動の検知破棄.
        this.unregisterReceiver(mVoiceUIStartReceiver);

        //タイマー設定を破棄
        roboTimer.cancel();

        //TODO プロジェクタイベントの検知破棄(プロジェクター利用時のみ).
        //this.unregisterReceiver(mProjectorEventReceiver);

        //TODO カメラ連携起動結果取得用レシーバー破棄(カメラ利用時のみ).
        this.unregisterReceiver(mCameraResultReceiver);

        //TODO ダンス結果用レシーバーの破棄(ダンス利用時のみ).
        //this.unregisterReceiver(mDanceResultReceiver);

        //TODO メッセージ送信結果用レシーバーの破棄(メッセージ利用時のみ).
        //this.unregisterReceiver(mMessageResultReceiver);

        //TODO アクション結果用レシーバーの破棄(アクション利用時のみ).
        //this.unregisterReceiver(mActionResultReceiver);

        //TODO 歌結果用レシーバーの破棄(歌利用時のみ).
        //this.unregisterReceiver(mSongResultReceiver);

        //インスタンスのごみ掃除.
        mVoiceUIManager = null;
        mMainActivityVoiceUIListener = null;
        mProjectorEventReceiver = null;
    }

    /**
     * VoiceUIListenerクラスからのコールバックを実装する.
     */
    @Override
    public void onExecCommand(String command, List<VoiceUIVariable> variables) {
        Log.v(TAG, "onExecCommand() : " + command);
        switch (command) {
            case ScenarioDefinitions.FUNC_END_APP:
                finish();
                break;
            case ScenarioDefinitions.FUNC_START_PROJECTOR:
                //TODO プロジェクタマネージャの開始(プロジェクター利用時のみ).
                //if(!isProjected) {
                //    startService(getIntentForProjector());
                //}
                break;
            case ScenarioDefinitions.COMMAND_RESOLVE_VARIABLE:
                for (VoiceUIVariable variable : variables) {
                    String key = variable.getName();
                    Log.d(TAG, "onVoiceUIResolveVariable: " + key + ":" + variable.getStringValue());
                    if (ScenarioDefinitions.RESOLVE_SPEECHTALK_RESULT.equals(key)) {
                        Log.d(TAG, "onExecCommand: mspeechtxt:" + mSpeechTxt);
                        variable.setStringValue(mSpeechTxt);
                        //発話後はリセットする
                        mSpeechTxt = "";
                    }
                    if(ScenarioDefinitions.RESOLVE_BOARD_PERSON.equals(key)){
                        variable.setStringValue(mBoardPerson);
                    }
                    if(ScenarioDefinitions.RESOLVE_FACE_DITECT.equals(key)){
                        Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        Log.d(TAG, "speakMessageBoard: time" + hour);
                        //if(mFaceName.isEmpty()){
                            if(hour >= 4 && hour <= 11){
                                Log.d(TAG, "speakMessageBoard: isMorning");
                                mFaceName = "おはよ！";
                            }
                            else if(hour >= 12 && hour <= 16){
                                Log.d(TAG, "speakMessageBoard: isNoon");
                                mFaceName = "こんにちは！";
                            }
                            else if(hour >= 17 && hour <= 23){
                                Log.d(TAG, "speakMessageBoard: isNight");
                                mFaceName = "こんばんは!";
                            }
                        //}
                        variable.setStringValue(mFaceName);
                        mFaceName = "";
                    }


                }
                break;
            case ScenarioDefinitions.FUNC_SPEECH_AFTER:
                //発話完了後、発話フラグを折る
                if(!stat){
                    mDatabase.child("chat").child(Fkey).child("isSpeech").setValue(false);
                }
                if(!isEnd_Conversation && stat){
                    Log.d(TAG, "onExecCommand: Switchtalk");
                    VoiceUIVariableUtil.VoiceUIVariableListHelper helper = new VoiceUIVariableUtil.VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_SWITCH);
                    VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
                }
                break;
            case ScenarioDefinitions.FUNC_BOARD_AFTER:
                isBoard = false;
                mBoardPerson = "";
                final String person = VoiceUIVariableUtil.getVariableData(variables, "value_board");
                final String return_sentence = VoiceUIVariableUtil.getVariableData(variables, "value_return_board");
                Log.d(TAG, "onExecCommand FaceName: " + person);
                mDatabase.child("msgboard").child(person).child("isListen").setValue(true);
                /*if(return_sentence.isEmpty()) {
                    mDatabase.child("msgboard").child(person).child("isListen").setValue(true);
                }
                else{
                    Log.d(TAG, "onExecCommand BoardReturn: " + return_sentence);
                    mDatabase.child("msgboard").child(person).child("return_msg").setValue(return_sentence);
                }*/

                break;
            case ScenarioDefinitions.FUNC_USUALLY_AFTER:
                isBoard = false;
                isUsually = false;
                break;
            case ScenarioDefinitions.FUNC_LISTEN:
                String key = "listen_kata";
                final String word = VoiceUIVariableUtil.getVariableData(variables, ScenarioDefinitions.KEY_LISTEN_VALUE);
                final String kana = VoiceUIVariableUtil.getVariableData(variables, key);
                Log.d(TAG, "onExec Kana: "+ kana);
                    if(stat){
                        sendMyAPI(word,mFaceName);
                    }
                    else {
                        basicWrite(word);
                    }
                break;

            default:
                break;
        }
    }

    /**
     * 首振り顔認識を行う変数
     */
    // TODO:認識後の処理をする
    public void FaceDitect_SwingHead(){
        Log.i(TAG, "FaceDitect_SwingHead: ");
        sendBroadcast(getIntentForFaceDetection("TRUE"));
    }

    /**
     * タイトルバーを設定する.
     */
    private void setupTitleBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
    }

    /**
     * プロジェクターマネージャーの開始/停止用のIntentを設定する.
     */
    private Intent getIntentForProjector() {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(
                ProjectorManagerServiceUtil.PACKAGE_NAME,
                ProjectorManagerServiceUtil.CLASS_NAME);
        //逆方向で照射する
        intent.putExtra(ProjectorManagerServiceUtil.EXTRA_PROJECTOR_OUTPUT, ProjectorManagerServiceUtil.EXTRA_PROJECTOR_OUTPUT_VAL_REVERSE);
        //足元に照射する
        intent.putExtra(ProjectorManagerServiceUtil.EXTRA_PROJECTOR_DIRECTION, ProjectorManagerServiceUtil.EXTRA_PROJECTOR_DIRECTION_VAL_UNDER);
        intent.setComponent(componentName);
        return intent;
    }

    /**
     * プロジェクターの状態変化イベントを受け取るためのレシーバーをセットする.
     */
    private void setProjectorEventReceiver() {
        Log.v(TAG, "setProjectorEventReceiver()");
        if (mProjectorEventReceiver == null) {
            mProjectorEventReceiver = new ProjectorEventReceiver();
        } else {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ProjectorManagerServiceUtil.ACTION_PROJECTOR_PREPARE);
        intentFilter.addAction(ProjectorManagerServiceUtil.ACTION_PROJECTOR_START);
        intentFilter.addAction(ProjectorManagerServiceUtil.ACTION_PROJECTOR_PAUSE);
        intentFilter.addAction(ProjectorManagerServiceUtil.ACTION_PROJECTOR_RESUME);
        intentFilter.addAction(ProjectorManagerServiceUtil.ACTION_PROJECTOR_END);
        intentFilter.addAction(ProjectorManagerServiceUtil.ACTION_PROJECTOR_END_ERROR);
        intentFilter.addAction(ProjectorManagerServiceUtil.ACTION_PROJECTOR_END_FATAL_ERROR);
        intentFilter.addAction(ProjectorManagerServiceUtil.ACTION_PROJECTOR_TERMINATE);
        registerReceiver(mProjectorEventReceiver, intentFilter);
    }

    /**
     * WakeLockを取得する.
     */
    private void acquireWakeLock() {
        Log.v(TAG, "acquireWakeLock()");
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        synchronized (mLock) {
            if (mWakelock == null || !mWakelock.isHeld()) {
                mWakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, MainActivity.class.getName());
                mWakelock.acquire();
            }
        }
    }

    /**
     * WakeLockを開放する.
     */
    private void releaseWakeLock() {
        Log.v(TAG, "releaseWakeLock()");
        synchronized (mLock) {
            if (mWakelock != null && mWakelock.isHeld()) {
                mWakelock.release();
                mWakelock = null;
            }
        }
    }

    /**
     * 顔検出実行用インテント取得関数
     *
     * @param swing String型でTRUE or FALSE
     * @return 顔検出実行用intent
     */
    private Intent getIntentForFaceDetection(String swing) {
        Intent intent = new Intent(FaceDetectionUtil.ACTION_FACE_DETECTION_MODE);
        intent.setPackage(FaceDetectionUtil.PACKAGE);
        intent.putExtra(FaceDetectionUtil.EXTRA_REPLYTO_ACTION, ACTION_RESULT_FACE_DETECTION);
        intent.putExtra(FaceDetectionUtil.EXTRA_REPLYTO_PKG, getPackageName());
        intent.putExtra(FaceDetectionUtil.EXTRA_FACE_DETECTION_LENGTH, FaceDetectionUtil.EXTRA_FACE_DETECTION_LENGTH_NORMAL);
        intent.putExtra(FaceDetectionUtil.EXTRA_MOVE_HEAD, swing);
        return intent;
    }

    /**
     * 写真撮影実行用インテント取得関数
     *
     * @param facedetect boolean型
     * @return 写真撮影実行用intent
     */
    private Intent getIntentForPhoto(boolean facedetect) {
        Intent intent = new Intent(ShootMediaUtil.ACTION_SHOOT_IMAGE);
        intent.setPackage(ShootMediaUtil.PACKAGE);
        intent.putExtra(ShootMediaUtil.EXTRA_FACE_DETECTION, facedetect);
        intent.putExtra(ShootMediaUtil.EXTRA_REPLYTO_ACTION, ACTION_RESULT_TAKE_PICTURE);
        intent.putExtra(ShootMediaUtil.EXTRA_REPLYTO_PKG, getPackageName());
        //TODO 撮影対象指定する場合はContactIDを指定
        //intent.putExtra(ShootMediaUtil.EXTRA_CONTACTID, ShootMediaUtil.EXTRA_CONTACTID_OWNER);
        return intent;
    }

    /**
     * 動画撮影実行用インテント取得関数
     *
     * @param time int型(sec)
     * @return 動画撮影実行用intent
     */
    private Intent getIntentForVideo(int time) {
        Intent intent = new Intent(ShootMediaUtil.ACTION_SHOOT_MOVIE);
        intent.setPackage(ShootMediaUtil.PACKAGE);
        intent.putExtra(ShootMediaUtil.EXTRA_MOVIE_LENGTH, time);
        intent.putExtra(ShootMediaUtil.EXTRA_REPLYTO_ACTION, ACTION_RESULT_REC_MOVIE);
        intent.putExtra(ShootMediaUtil.EXTRA_REPLYTO_PKG, getPackageName());
        return intent;
    }

    /**
     * ダンス開始用のIntentを設定する.
     */
    private Intent getIntentForDance(String type) {
        Intent intent = new Intent(DanceUtil.ACTION_REQUEST_DANCE);
        intent.putExtra(DanceUtil.EXTRA_REPLYTO_ACTION, ACTION_RESULT_DANCE);
        intent.putExtra(DanceUtil.EXTRA_REPLYTO_PKG, getPackageName());
        intent.putExtra(DanceUtil.EXTRA_TYPE, type);
        if (type.equals(DanceUtil.EXTRA_TYPE_ASSIGN)) {
            intent.putExtra(DanceUtil.EXTRA_REQUEST_ID, 1);
        }
        intent.putExtra(DanceUtil.EXTRA_SKIP_COMMENT, false);
        return intent;
    }

    /**
     * メッセージ送信用のIntentを設定する.
     */
    private Intent getIntentForMessage() {
        String mail_addr = "";
        String body = "";
        String attachment_path = "";
        boolean skip_confirm = false;
        boolean background = false;

        Intent intent = new Intent(MessagingUtil.ACTION_SEND_MESSAGE);
        intent.putExtra(MessagingUtil.EXTRA_EMAIL, mail_addr);
        intent.putExtra(MessagingUtil.EXTRA_SUBJECT, (String) null);
        intent.putExtra(MessagingUtil.EXTRA_TEXT, body);
        intent.putExtra(MessagingUtil.EXTRA_ATTACHMENT_PATH, attachment_path);
        intent.putExtra(MessagingUtil.EXTRA_SKIP_CONFIRM, skip_confirm);
        intent.putExtra(MessagingUtil.EXTRA_BACKGROUND, background);
        intent.putExtra(MessagingUtil.EXTRA_REPLYTO_ACTION, ACTION_RESULT_MESSAGE);
        intent.putExtra(MessagingUtil.EXTRA_REPLYTO_PKG, getPackageName());

        return intent;
    }

    /**
     * アクション開始用のIntentを設定する.
     */
    private Intent getIntentForAction(int id) {
        Intent intent = new Intent(ActionUtil.ACTION_REQUEST_ACTION);
        intent.putExtra(ActionUtil.EXTRA_REPLYTO_ACTION, ACTION_RESULT_ACTION);
        intent.putExtra(ActionUtil.EXTRA_REPLYTO_PKG, getPackageName());
        intent.putExtra(ActionUtil.EXTRA_REQUEST_ID, id);
        return intent;
    }

    /**
     * 歌開始用のIntentを設定する.
     */
    private Intent getIntentForSong(String type) {
        Intent intent = new Intent(SongUtil.ACTION_REQUEST_SONG);
        intent.putExtra(SongUtil.EXTRA_REPLYTO_ACTION, ACTION_RESULT_SONG);
        intent.putExtra(SongUtil.EXTRA_REPLYTO_PKG, getPackageName());
        intent.putExtra(SongUtil.EXTRA_TYPE, type);
        if (type.equals(SongUtil.EXTRA_TYPE_ASSIGN)) {
            intent.putExtra(SongUtil.EXTRA_REQUEST_ID, 1);
        }
        return intent;
    }

    /**
     * ホームボタンの押下イベントを受け取るためのBroadcastレシーバークラス.<br>
     * <p/>
     * アプリは必ずホームボタンで終了する..
     */
    private class HomeEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "Receive Home button pressed");
            // ホームボタン押下でアプリ終了する.
            finish();
        }
    }

    /**
     * 音声UI再起動イベントを受け取るためのBroadcastレシーバークラス.<br>
     * <p/>
     * 稀に音声UIのServiceが再起動することがあり、その場合アプリはVoiceUIの再取得とListenerの再登録をする.
     */
    private class VoiceUIStartReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (VoiceUIManager.ACTION_VOICEUI_SERVICE_STARTED.equals(action)) {
                Log.d(TAG, "VoiceUIStartReceiver#onReceive():VOICEUI_SERVICE_STARTED");
                //VoiceUIManagerのインスタンス取得.
                mVoiceUIManager = VoiceUIManager.getService(getApplicationContext());
                if (mMainActivityVoiceUIListener == null) {
                    mMainActivityVoiceUIListener = new MainActivityVoiceUIListener(getApplicationContext());
                }
                //VoiceUIListenerの登録.
                VoiceUIManagerUtil.registerVoiceUIListener(mVoiceUIManager, mMainActivityVoiceUIListener);
            }
        }
    }

    /**
     * プロジェクターの状態変化時のイベントを受け取るためのBroadcastレシーバークラス.<br>
     * <p/>
     * 照射開始時にはWakeLockの取得、終了時にはWakeLockの開放する.<br>
     * アプリ仕様に応じて必要な処理があれば実装すること.
     */
    private class ProjectorEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "ProjectorEventReceiver#onReceive():" + intent.getAction());
            switch (intent.getAction()) {
                case ProjectorManagerServiceUtil.ACTION_PROJECTOR_PREPARE:
                case ProjectorManagerServiceUtil.ACTION_PROJECTOR_PAUSE:
                case ProjectorManagerServiceUtil.ACTION_PROJECTOR_RESUME:
                    break;
                case ProjectorManagerServiceUtil.ACTION_PROJECTOR_START:
                    acquireWakeLock();
                    isProjected = true;
                    break;
                case ProjectorManagerServiceUtil.ACTION_PROJECTOR_END:
                case ProjectorManagerServiceUtil.ACTION_PROJECTOR_END_FATAL_ERROR:
                case ProjectorManagerServiceUtil.ACTION_PROJECTOR_END_ERROR:
                case ProjectorManagerServiceUtil.ACTION_PROJECTOR_TERMINATE:
                    releaseWakeLock();
                    isProjected = false;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * カメラ連携の結果を受け取るためのBroadcastレシーバー クラス.<br>
     * <p/>
     * それぞれの結果毎に処理を行う.
     */
    private class CameraResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "CameraResultReceiver#onReceive() : " + action);
            switch (action) {
                case ACTION_RESULT_FACE_DETECTION:
                    boolean ExistFlg = false;
                    int result = intent.getIntExtra(FaceDetectionUtil.EXTRA_RESULT_CODE, FaceDetectionUtil.RESULT_CANCELED);
                    if(result == FaceDetectionUtil.RESULT_OK){
                        HashMap<String,String> hashMapFace =
                                (HashMap<String,String>)intent.getSerializableExtra(FaceDetectionUtil.EXTRA_MAP_FACE_DETECTION);

                        //電話帳よりContact IDを参照する
                        AddressBookManager addressMng = AddressBookManager.getService(getApplicationContext());
                        List<String> nameList = new ArrayList<String>();

                        for (String key : hashMapFace.keySet()) {
                            int contactId = Integer.valueOf(hashMapFace.get(key));
                            Log.d(TAG, "contact id = " + contactId);
                            if(contactId == AddressBookCommonUtils.CONTACT_ID_OWNER){
                                //オーナーを検出した場合
                                nameList.add(getOwnerName(addressMng));
                                ExistFlg = true;
                            }else if(contactId == -1){
                                //電話帳登録されていない人を検出した場合
                            }else {
                                //電話帳登録されている人を検出した場合
                                nameList.add(getNameByContactId(addressMng, contactId));
                                ExistFlg = true;
                            }
                        }

                        //発話用のテキスト作成
                        if(ExistFlg) {
                            isBoard = true;
                            isUsually = true;
                            if (nameList.isEmpty()) {
                                Log.d(TAG, "onReceive: ditect face-> No name");
                                mFaceName = "";
                            }else {
                                for (String name : nameList) {
                                    mFaceName += name;
                                    mFaceName += "、";
                                }
                                Log.d(TAG, "onReceive: ditect face -> " + mFaceName);
                            }
                        }else{
                            isUsually = false;
                            Log.d(TAG, "onReceive: No Ditect Face");
                        }
                    }
                    break;
                case ACTION_RESULT_TAKE_PICTURE:
                    result = intent.getIntExtra(ShootMediaUtil.EXTRA_RESULT_CODE, ShootMediaUtil.RESULT_CANCELED);
                    break;
                case ACTION_RESULT_REC_MOVIE:
                    result = intent.getIntExtra(ShootMediaUtil.EXTRA_RESULT_CODE, ShootMediaUtil.RESULT_CANCELED);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * オーナー名取得関数
     * @param addressMng AddressBookManager
     * @return オーナーの呼び方
     */
    private String getOwnerName(AddressBookManager addressMng) {
        String ret = "";
        try {
            OwnerProfileData ownerdata = addressMng.getOwnerProfileData();
            String nickName = ownerdata.getNickname();
            String firstName = ownerdata.getFirstname();
            String lastName = ownerdata.getLastname();

            //オーナーの呼び方の優先順位「ニックネーム」→「名(ファーストネーム)＋さん」→「姓(ラストネーム)＋さん」
            if(nickName != null && !("".equals(nickName)) ) {
                ret = nickName;
            }else if(firstName != null && !("".equals(firstName)) ) {
                firstName = firstName + "さん";
                ret = firstName;
            }else if(lastName != null && !("".equals(lastName)) ) {
                lastName = lastName + "さん";
                ret = lastName;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException occur : " + e.getMessage());
        }
        return ret;
    }

    /**
     * 電話帳登録されている人の名前取得関数
     * @param addressMng AddressBookManager
     * @param id Contact ID
     * @return 友達の呼び方
     */
    private String getNameByContactId(AddressBookManager addressMng, int id) {
        String ret = "";
        try {
            AddressBookData address = addressMng.getAddressBookData(id);
            String nickName = address.getNickname();
            String firstName = address.getFirstname();
            String lastName = address.getLastname();
            //オーナー以外の呼び方の優先順位「ニックネーム」→「姓(ラストネーム)＋さん」→「名(ファーストネーム)＋さん」
            if (nickName != null && !("".equals(nickName))) {
                ret = nickName;
            } else if (lastName != null && !("".equals(lastName))) {
                ret = lastName + "さん";
            } else if (firstName != null && !("".equals(firstName))) {
                ret = firstName + "さん";
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException occur : " + e.getMessage());
        }
        return ret;
    }

    /**
     * ダンス実行結果を受け取るためのBroadcastレシーバー クラス.<br>
     * <p/>
     */
    private class DanceResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getIntExtra(DanceUtil.EXTRA_RESULT_CODE, DanceUtil.RESULT_CANCELED);
            Log.d(TAG, "DanceResultReceiver#onReceive() : " + result);
            if (result == DanceUtil.RESULT_OK) {
                // 正常に完了した場合.
                int id = intent.getIntExtra(DanceUtil.EXTRA_RESULT_ID, -1);
                String name = intent.getStringExtra(DanceUtil.EXTRA_RESULT_NAME);
            }
        }
    }

    /**
     * メッセージ送信結果を受け取るためのBroadcastレシーバー クラス.<br>
     * <p/>
     */
    private class MessageResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getIntExtra(MessagingUtil.EXTRA_RESULT_CODE, MessagingUtil.RESULT_CANCELED);
            Log.d(TAG, "MessageResultReceiver#onReceive() : " + result);
            if (result == MessagingUtil.RESULT_OK) {
                // 正常に完了した場合.
            }
        }
    }

    /**
     * アクション実行結果を受け取るためのBroadcastレシーバー クラス.<br>
     * <p/>
     */
    private class ActionResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getIntExtra(ActionUtil.EXTRA_RESULT_CODE, ActionUtil.RESULT_CANCELED);
            Log.d(TAG, "ActionResultReceiver#onReceive() : " + result);
            if (result == ActionUtil.RESULT_OK) {
                // 正常に完了した場合.
                int id = intent.getIntExtra(ActionUtil.EXTRA_RESULT_ID, -1);
                String name = intent.getStringExtra(ActionUtil.EXTRA_RESULT_NAME);
            }
        }
    }

    /**
     * 歌実行結果を受け取るためのBroadcastレシーバー クラス.<br>
     * <p/>
     */
    private class SongResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getIntExtra(SongUtil.EXTRA_RESULT_CODE, SongUtil.RESULT_CANCELED);
            Log.d(TAG, "SongResultReceiver#onReceive() : " + result);
            if (result == SongUtil.RESULT_OK) {
                // 正常に完了した場合.
                int id = intent.getIntExtra(SongUtil.EXTRA_RESULT_ID, -1);
                String name = intent.getStringExtra(SongUtil.EXTRA_RESULT_NAME);
            }
        }
    }

    /**
     * データベースへ書き込む
     */
    private void basicWrite(String str){
        Log.v("WP", "basicWrinte(String)");
        //サーバーへ書き込む

        String key = mDatabase.push().getKey();
        Log.i(TAG, "basicWrite: " + mDatabase.push());
        MessageData messageData = new MessageData(key,"えーた",str);

        Task task = mDatabase.child("chat").child(key).setValue(messageData);

        //書き込んだ結果の通知（+エラーハンドリング）
        task.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.i("TAG", "Success Send");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Failed Send");
            }
        });

    }

    /**
    * データベースから拾ったものをしゃべらせる
    */
    private void basicSpeech(){
        Log.i(TAG, "basicSpeech() ");
        if(mVoiceUIManager != null && !mSpeechTxt.equals("") ){
            Log.d(TAG, "onResume: "+ScenarioDefinitions.ACC_SPEECH);
            Log.d(TAG, "onClick: " + mSpeechTxt);
            VoiceUIVariableUtil.VoiceUIVariableListHelper helper = new VoiceUIVariableUtil.VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_SPEECH);
            VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
        }
    }

    /**
    *  受け取った発話文字をDialogflowへ飛ばす
    */
    private void sendMyAPI(String word,String F_name){
        Log.d(TAG, "sendMyAPI: word :" + word);
        AsyncDFTask dfTask = new AsyncDFTask();
        dfTask.execute(word,F_name);

        //非同期処理後処理
        dfTask.setmCallBack(new AsyncDFTask.CallBackTask(){
            public void CallBack(ArrayList result){
                mSpeechTxt = (String)result.get(0);
                isEnd_Conversation = (boolean)result.get(1);
                Log.d(TAG, "sendMyAPI: "+ mSpeechTxt);
                Log.d(TAG, "sendMyAPI: isEnd->" + isEnd_Conversation);

                VoiceUIVariableUtil.VoiceUIVariableListHelper helper = new VoiceUIVariableUtil.VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_SPEECH);
                VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
            }
        });
    }

    /**
     * メッセージボードの処理
     */
    private void speakMessageBoard(){
        Log.d(TAG, "speakMessageBoard: " + MsgMap.get("message"));
        Log.d(TAG, "speakMessageBoard: " + MsgMap.get("name"));
        Log.d(TAG, "speakMessageBoard: " + MsgMap.get("time"));

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        Log.d(TAG, "speakMessageBoard: time" + hour);
        if(MsgMap.get("time").equals("morning") && (hour >= 4 && hour <= 11)){
            Log.d(TAG, "speakMessageBoard: isMorning");
        }
        else if(MsgMap.get("time").equals("noon") && (hour >= 12 && hour <= 16)){
            Log.d(TAG, "speakMessageBoard: isNoon");
        }
        else if(MsgMap.get("time").equals("night") && (hour >= 17 && hour <= 23)){
            Log.d(TAG, "speakMessageBoard: isNight");
        }
        else{
            isBoard = false;
            speakUsually();
            return;
        }

        mSpeechTxt = MsgMap.get("message").toString();
        mBoardPerson = MsgMap.get("name").toString();
        if(mVoiceUIManager != null && !mSpeechTxt.equals("") ) {
            Log.d(TAG, "speakMessageBoard: StartSpeach->" + mSpeechTxt);
            VoiceUIVariableUtil.VoiceUIVariableListHelper helper = new VoiceUIVariableUtil.VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_BOARD);
            VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
        }
    }

    /**
     * 普段の会話周りの処理
     */
    private void speakUsually(){
        Log.d(TAG, "speakUsually() ");

        VoiceUIVariableUtil.VoiceUIVariableListHelper helper = new VoiceUIVariableUtil.VoiceUIVariableListHelper().addAccost(ScenarioDefinitions.ACC_USUALLY);
        VoiceUIManagerUtil.updateAppInfo(mVoiceUIManager, helper.getVariableList(), true);
    }
}
