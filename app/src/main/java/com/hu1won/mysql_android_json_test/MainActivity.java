package com.hu1won.mysql_android_json_test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "PHP서버주소";
    private static String TAG = "그룹명";
    private static String filename = "getjson.php";
    private String mJsonString;
    private TextView tvString;

    MainActivity.GetData task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvString = (TextView) findViewById(R.id.tvString);

        //처음 시작될 때 AsyncTask 시작
        task = new GetData();
        task.execute("http://" + IP_ADDRESS + "/" + filename, "");
    } //PHP주소로부터 데이터를 받아오는 함수

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;
        private int LastInt;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
            mJsonString = result;
            showResult();
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = params[1];
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);
                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();
                return null;
            }
        }

        private void showResult() {
            String TAG_JSON = "그룹명";
            String TAG_INDEX_NO = "field1";
            String TAG_S_DATE = "field2";
            String TAG_STIME = "field3";
            String TAG_ETIME = "field4";
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                LastInt = jsonArray.length();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    mJsonString = item.getString(TAG_INDEX_NO) + ", " + item.getString(TAG_S_DATE) + ", " + item.getString(TAG_STIME) + ", " + item.getString(TAG_ETIME);
                }
                tvString.setText(mJsonString);
            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }
        }
    }
}