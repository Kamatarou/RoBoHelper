# RoBoHelper
ロボホンが良い感じに悩みを根掘り葉掘り聞いてスッキリしたりするやつ
⇒アンドロイド側のリポジトリ<br>
RoBoHelper/app/src/main/java/sofcom19_robohon/o_hara/jp/ac/robohelper/<br>
|---/costomize<br>
            |---ScenarioDefintions.java ->MainActivity内で使う別名ここで一括定義<br>
|---MainActivity.java ->本体、Firebaseの送受信や発話制御を行う<br>
|---MessageData.java ->送信するデータを整形するのに使用<br>
<br>
RoBoHelper/app/src/main/assets/hvml/other/<br>
|---sofcom19_robohon_o_hara_jp_ac_robohelper_listeningtalk.hvml ->ここでユー　ザーが発話した内容を受け取る<br>
|---sofcom19_robohon_o_hara_jp_ac_robohelper_speechtalk.hvml->ここでサーバーから受け取った文字を変数を利用して発話。<br>
