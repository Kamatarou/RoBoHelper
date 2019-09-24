package sofcom19_robohon.o_hara.jp.ac.robohelper;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncDFTask extends AsyncTask<String, Void, String> {

    final String TAG = "AsyncDialogflowTask";
    final int CONNECT_TIMEOUT = 30 * 1000;
    final int READ_TIMEOUT = 30 * 1000;
    private URL Url;

    /**
     * 非同期処理で自作APIからDialogflowのインテントを取得
     * */
    protected String doInBackground(String... params){
        //結果
        String RstStr = "";

        try{
            //URLの設定
            String burl = "https://us-central1-chat001-16c14.cloudfunctions.net/app";
            String add1 = "/api/v1/hubapi";
            String add2 = "?s=" + params[0];



            //コネクションの確立
            Url = new URL(burl + add1 + add2);
            HttpURLConnection connection = (HttpURLConnection)Url.openConnection();

            //接続設定
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setDoInput(true);

            //リダイレクトを自動許可しない
            connection.setInstanceFollowRedirects(false);

            //ヘッダー設定
            connection.setRequestProperty("Accept-Language", "jp");

            //接続
            connection.connect();

            //結果取得
            int rst = connection.getResponseCode();
            String rstStr = connection.getResponseMessage();
            //Log.d(TAG, "doInBackground: " + rstStr);

            switch(rst){
                case HttpURLConnection.HTTP_OK:
                    Log.d(TAG, "200 : OK");
                    Log.d(TAG, "doInBackground: " + rstStr);
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String data = bufferedReader.readLine();
                    Log.d(TAG, "doInBackground: " + data);
                    JSONObject json = new JSONObject(data);
                    String str = json.get("Response").toString();
                    Log.d(TAG, "doInBackground: getword->" + str);

                    RstStr = str;
                    bufferedReader.close();
                    inputStream.close();
                    bufferedReader.close();
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    Log.d(TAG, "500 : Internal Server Error");
                    break;
                default:
                    Log.d(TAG, "No function");
                    break;
            }


        }
        catch(Exception e){
            e.printStackTrace();
        }

        return RstStr;
    }


    /**
     * 非同期が終わった後処理
     * @param result 取得したインテント
     * */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);


        Log.d(TAG, "onPostExecute: " + result);
    }
}
