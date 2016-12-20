package org.llama.llama.chat;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Felix on 12.12.2016.
 */

public class SendMessageAsyncTask extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = "SendMessageAsyncTask";

    @Override
    protected Integer doInBackground(String... params) {
        // TODO get type, etc. from chat layout

        // TODO read url settings, etc. from config file
        String msg = params[0];
        String messageLanguage = params[1];
        String userId = params[2];
        String chatId = params[3];

        URL serverUrl;
        try {
            serverUrl = new URL("http://woernsn.net:8888");
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            JSONObject postObject = new JSONObject();
            postObject.put("type", "text");
            postObject.put("from", userId);
            postObject.put("to", chatId);
            postObject.put("message", msg);
            postObject.put("message_language", messageLanguage);

            OutputStreamWriter osWriter = new OutputStreamWriter(conn.getOutputStream());
            osWriter.write(postObject.toString());
            osWriter.close();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED){ // LLAMA_CREATED
                Log.d(TAG, conn.getResponseMessage());
            }

            conn.disconnect();

            Log.d(TAG, "message " + msg + " sent");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "couldn't send message", e);
        }
        return 0;
    }
}
