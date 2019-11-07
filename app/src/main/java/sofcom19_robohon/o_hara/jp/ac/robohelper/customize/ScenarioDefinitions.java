package sofcom19_robohon.o_hara.jp.ac.robohelper.customize;

/**
 * シナリオファイルで使用する定数の定義クラス.<br>
 * <p>
 * <p>
 * controlタグのtargetにはPackage名を設定すること<br>
 * scene、memory_p(長期記憶の変数名)、resolve variable(アプリ変数解決の変数名)、accostのwordはPackage名を含むこと<br>
 * </p>
 */
public class ScenarioDefinitions {

    /**
     * sceneタグを指定する文字列
     */
    public static final String TAG_SCENE = "scene";
    /**
     * accostタグを指定する文字列
     */
    public static final String TAG_ACCOST = "accost";
    /**
     * target属性を指定する文字列
     */
    public static final String ATTR_TARGET = "target";
    /**
     * function属性を指定する文字列
     */
    public static final String ATTR_FUNCTION = "function";
    /**
     * memory_pを指定するタグ
     */
    public static final String TAG_MEMORY_PERMANENT = "memory_p:";
    /**
     * function：アプリ終了を通知する.
     */
    public static final String FUNC_END_APP = "end_app";
    /**
     * function：プロジェクタ起動を通知する.
     */
    public static final String FUNC_START_PROJECTOR = "start_projector";
    /**
     * function : 喋ったことをサーバーに送るための処理を通知.
     */
    public static final String FUNC_LISTEN = "func_listen";
    /**
     * function : 喋る内容を受け取り、喋った後の処理
     */
    public static final String FUNC_SPEECH_AFTER = "func_speech_after";
    /**
     * function : 掲示板の中身を喋り、喋った後の処理
     */
    public static final String FUNC_BOARD_AFTER ="func_board_after";
    /**
     * function : 掲示板の中身を喋り、喋った後の処理
     */
    public static final String FUNC_USUALLY_AFTER ="func_usually_after";
    /**
     * Package名.
     */
    protected static final String PACKAGE = "sofcom19_robohon.o_hara.jp.ac.robohelper";
    /**
     * シナリオ共通: controlタグで指定するターゲット名.
     */
    public static final String TARGET = PACKAGE;
    /**
     * scene名: アプリ共通シーン
     */
    public static final String SCENE_COMMON = PACKAGE + ".scene_common";
    /**
     * scene名: 特定シーン
     */
    public static final String SCENE01 = PACKAGE + ".scene01";
    /**
     * data key：トースト表示内容
     * */
    public static final String KEY_LISTEN_VALUE = "listen_value";
    /**
     * accost名：こんにちは発話実行.
     */
    public static final String ACC_HELLO = ScenarioDefinitions.PACKAGE + ".hello.say";
    /**
     * accost名：アプリ終了発話実行.
     */
    public static final String ACC_END_APP = ScenarioDefinitions.PACKAGE + ".app_end.execute";
    /**
     * accost名：accostテスト発話実行.
     */
    public static final String ACC_SPEECH =  ScenarioDefinitions.PACKAGE + ".acc_speech.t1";
    /**
     * accost名：メッセージボード発話
     */
    public static final String ACC_BOARD =  ScenarioDefinitions.PACKAGE + ".acc_spcBoard.t1";
    /**
     * accost名：普段の声掛け発話
     */
    public static final String ACC_USUALLY =  ScenarioDefinitions.PACKAGE + ".acc_usually.t1";
    /**
     * accost名：切り返し会話用
     */
    public static final String ACC_SWITCH =  ScenarioDefinitions.PACKAGE + ".acc_switch.t3";
    /**
     * 音声UIコールバック用定義.
     */
    public static final String COMMAND_RESOLVE_VARIABLE = "onVoiceUIResolveVariable";
    /**
     * resolve variable：アプリで変数解決する値.
     */
    public static final String RESOLVE_SPEECHTALK_RESULT = ScenarioDefinitions.PACKAGE + ":my_speech_talk_result";
    /*
    * resolve variable : 伝言板の対象の値
    */
    public static final String RESOLVE_BOARD_PERSON = ScenarioDefinitions.PACKAGE + ":my_board_person";
    /*
     * resolve variable : 認識対象の名前
     */
    public static final String RESOLVE_FACE_DITECT = ScenarioDefinitions.PACKAGE + ":my_face_ditect";
    /**
     * static クラスとして使用する.
     */
    private ScenarioDefinitions() {
    }
}
