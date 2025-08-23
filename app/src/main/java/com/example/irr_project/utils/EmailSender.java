package com.example.irr_project.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmailSender {
    private static final String TAG = "EmailSender";

    // Replace with your Mailjet API keys
    private static final String API_KEY = "b55e998d30a5f94bd444af98a43b3598";
    private static final String API_SECRET = "488b0469880c9f1b54e7047a23e0a39a";

    // Replace with your sender email
    private static final String SENDER_EMAIL = "tkekla123@gmail.com";
    private static final String SENDER_NAME = "Internship App";

    public interface EmailCallback {
        void onSuccess();
        void onError(String error);
    }

    public static void sendVerificationEmail(String toEmail, String verificationCode, EmailCallback callback) {
        new SendEmailTask(toEmail, verificationCode, callback).execute();
    }

    private static class SendEmailTask extends AsyncTask<Void, Void, Boolean> {
        private final String toEmail;
        private final String verificationCode;
        private final EmailCallback callback;
        private String errorMessage;

        SendEmailTask(String toEmail, String verificationCode, EmailCallback callback) {
            this.toEmail = toEmail;
            this.verificationCode = verificationCode;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");

            try {
                // Prepare email content
                JSONObject emailContent = new JSONObject()
                        .put("Messages", new JSONArray()
                                .put(new JSONObject()
                                        .put("From", new JSONObject()
                                                .put("Email", SENDER_EMAIL)
                                                .put("Name", SENDER_NAME))
                                        .put("To", new JSONArray()
                                                .put(new JSONObject()
                                                        .put("Email", toEmail)))
                                        .put("Subject", "Your Password Reset Code")
                                        .put("TextPart", "Your verification code is: " + verificationCode)
                                        .put("HTMLPart", "<h3>Password Reset</h3><p>Your verification code is: <strong>" + verificationCode + "</strong></p>")));

                RequestBody body = RequestBody.create(emailContent.toString(), mediaType);

                // Mailjet API endpoint
                Request request = new Request.Builder()
                        .url("https://api.mailjet.com/v3.1/send")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Basic " + android.util.Base64.encodeToString(
                                (API_KEY + ":" + API_SECRET).getBytes(), android.util.Base64.NO_WRAP))
                        .build();

                Response response = client.newCall(request).execute();
                boolean success = response.isSuccessful();

                if (!success) {
                    errorMessage = "Error: " + response.code() + " - " + response.message();
                    Log.e(TAG, errorMessage);
                }

                return success;
            } catch (JSONException | IOException e) {
                errorMessage = "Exception: " + e.getMessage();
                Log.e(TAG, "Error sending email", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                callback.onSuccess();
            } else {
                callback.onError(errorMessage);
            }
        }
    }
}