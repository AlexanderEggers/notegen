package com.acando.todohelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UtilNetwork {

    public static String websiteToString(String url) throws IOException, OutOfMemoryError {
        HttpsURLConnection connection = null;
        InputStream input = null;
        String respond = null;

        Log.i(UtilNetwork.class.getName(), "DATA URL: " + url);

        try {
            connection = (HttpsURLConnection) (new URL(url).openConnection());
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.setConnectTimeout(4000);
            connection.setReadTimeout(4000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            StringBuilder stringBuilder = new StringBuilder();
            input = connection.getInputStream();
            if (input != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                input.close();
                connection.disconnect();
                respond = stringBuilder.toString();
            }
        } finally {
            if (input != null) input.close();
            if (connection != null) connection.disconnect();
        }
        return respond;
    }

    public static Bitmap getImage(String url) throws IOException, OutOfMemoryError {
        InputStream input = null;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;

        try {
            connection = (HttpURLConnection) (new URL(url).openConnection());
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            input = connection.getInputStream();
            if (input != null) {
                bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();
            }
        } finally {
            if (input != null) input.close();
            if (connection != null) connection.disconnect();
        }
        return bitmap;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
