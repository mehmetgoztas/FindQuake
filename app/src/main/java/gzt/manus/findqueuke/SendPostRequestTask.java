package gzt.manus.findqueuke;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SendPostRequestTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        String urlStr = params[0];
        JSONObject postData = new JSONObject();
        try {
            postData.put("email", params[1]);
            postData.put("location", params[2]);
            postData.put("name", params[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String response = "";

        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("email", params[1]); // Add email header
            connection.setRequestProperty("location", params[2]); // Add location header
            connection.setRequestProperty("name", params[3]); // Add name header
            connection.setDoOutput(true);

            // Veriyi yaz
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(postData.toString().getBytes());
            outputStream.flush();
            outputStream.close();
            connection.setConnectTimeout(30000); // 10 saniye
            connection.setReadTimeout(30000); // 10 saniye
            if (connection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                httpsConnection.setHostnameVerifier((hostname, session) -> true);
            }

            // Yanıtı oku
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            response = stringBuilder.toString();

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }


    @Override
    protected void onPostExecute(String result) {
        int statusCode = 200;
        boolean isBase64Encoded = false;
        Map<String, String> headers = new HashMap<>();
        String body = result;

        // APIResponse nesnesini oluşturun
        APIResponse apiResponse = new APIResponse(statusCode, isBase64Encoded, headers, body);

        // APIResponse nesnesini JSON formatına dönüştürün
        JSONObject responseJson = new JSONObject();
        try {
            responseJson.put("statusCode", apiResponse.getStatusCode());
            responseJson.put("isBase64Encoded", apiResponse.isBase64Encoded());
            responseJson.put("headers", new JSONObject(apiResponse.getHeaders()));
            responseJson.put("body", apiResponse.getBody());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // Doğru formatlı yanıtı gönderin
        String response = responseJson.toString();

        Log.d("API Response", response);

        // ...
    }


    public class APIResponse {
        private int statusCode;
        private boolean isBase64Encoded;
        private Map<String, String> headers;
        private String body;

        public APIResponse(int statusCode, boolean isBase64Encoded, Map<String, String> headers, String body) {
            this.statusCode = statusCode;
            this.isBase64Encoded = isBase64Encoded;
            this.headers = headers;
            this.body = body;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public boolean isBase64Encoded() {
            return isBase64Encoded;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public String getBody() {
            return body;
        }
    }

}
