package com.androidcommonlibrary.http;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.androidcommonlibrary.R;
import com.androidcommonlibrary.util.Log;
import com.androidcommonlibrary.util.Pref;

import org.apache.http.NameValuePair;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import butterknife.ButterKnife;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
//import okhttp3.OkHttpClient;


public class HttpRequest extends AsyncTask<Void, Void, String> {
    Context mContext;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    AsyncTaskCompleteListener mAsyncTaskCompleteListener;
    Dialog dialog;

    private String urlString = "", request = "";
    private boolean mIsShow;
    private boolean isMultipartReq = false;
    private File file = null;

    InputStream inputStream = null;
    int statusCode;

    String twoHyphens = "--";
    String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
    String lineEnd = "\r\n";


    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;

    HttpURLConnection connection = null;
    DataOutputStream outputStream = null;
    private List<NameValuePair> objValuePair = null;
    String imgParam;
    private boolean authFlag;

    public HttpRequest(Context _context, String url, String req, boolean mIsShow,boolean authFlag, AsyncTaskCompleteListener _asyncTaskCompleteListener) {
        mContext = _context;
        mAsyncTaskCompleteListener = _asyncTaskCompleteListener;
        urlString = url;
        request = req;
        this.authFlag = authFlag;
        this.mIsShow = mIsShow;
        this.isMultipartReq = false;
        this.file = null;
    }

    public HttpRequest(Context _context, String url, List<NameValuePair> _objvaluepair, boolean mIsShow, boolean _isMultipartReq, File _file,
                       String _imgParam, AsyncTaskCompleteListener _asyncTaskCompleteListener) {
        mContext = _context;
        mAsyncTaskCompleteListener = _asyncTaskCompleteListener;
        urlString = url;
        objValuePair = _objvaluepair;
        this.mIsShow = mIsShow;
        this.isMultipartReq = _isMultipartReq;
        this.file = _file;
        this.imgParam = _imgParam;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mIsShow) {
            showProgressBar();
        }
    }


    @Override
    protected String doInBackground(Void... params) {
        if (!isMultipartReq) {
            if (request.trim().length() == 0)
                return getRequest();
            else
                return postRequest();
        } else {
            return submitPost(mContext, request, urlString, file, imgParam);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.print("Response:" + result);
        if (mIsShow) {
            hideProgressBar();
        }

        mAsyncTaskCompleteListener.asyncTaskComplted(result);
    }

    public String submitPost(Context mContext, String req, String urlString, File file, String imgParam) {
        try {
            FileInputStream fileInputStream = null;
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            String[] q = new String[0];
            int idx = 0;
            if (file != null) {
                q = file.getAbsoluteFile().toString().split("/");
                idx = q.length - 1;
                fileInputStream = new FileInputStream(file);
            }


            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            if (file != null)
                connection.setRequestProperty("\""+imgParam+"\"", q[idx] );
            else
                connection.setRequestProperty("\""+imgParam+"\"", "" );

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            if (file != null) {
                outputStream.writeBytes("Content-Disposition: form-data;  name=\"" + imgParam + "\";filename=\"" + q[idx] + "\"" + lineEnd);
            } else {
                String blank ="";
                outputStream.writeBytes("Content-Disposition: form-data;  name=\"" + imgParam + "\";filename=\"" + blank+ "\"" + lineEnd);
            }
            outputStream.writeBytes("Content-Type: " + "image/jpg" + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            if (file != null) {
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);
            }

            // Upload POST Data
            for (int i = 0; i < objValuePair.size(); i++) {
                NameValuePair objPair = objValuePair.get(i);
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + objPair.getName() + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(objPair.getValue());
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            statusCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            InputStream is = new BufferedInputStream(connection.getInputStream());
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            Log.debug("Is", "IS:" + sb.toString());
            Log.debug("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + statusCode);

            if(file != null)
                fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String postRequest() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {

            Log.print("URL:" + urlString);
            Log.print("Request:" + request);

            client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();

            RequestBody body = RequestBody.create(JSON, request);
            /*if API needs to authenticate than add credential in header and replace username/password with your own credentials*/
            String token = Pref.getValue(mContext,Pref.KEY_Auth_Token,"");
            Log.print("Auth_Token:" + token);
            if(authFlag){
            Request request = new Request.Builder()
                    .url(urlString)
                    .header("X-AUTH-TOKEN", token)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
            }
            else {
                Request request = new Request.Builder()
                        .url(urlString)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getRequest() {
        Log.print("URL:" + urlString);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.print("URL:" + urlString);
        Log.print("Request:" + request);

        client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();

        try {
            /*if API needs to authenticate than add credential in header and replace username/password with your own credentials*/
            String credential = Credentials.basic("username", "password");
            Request request = new Request.Builder()
                    .header("Authorization", credential)
                    .url(urlString)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public interface AsyncTaskCompleteListener {
        void asyncTaskComplted(String response);

    }

    /**
     * Show Dialog During web service calling
     */
    public void showProgressBar() {
        dialog = new Dialog(mContext, android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dialog.setCancelable(false);

       // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                /*setFlags(WindowManager.LayoutParams.Flage,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        //dialog.setCancelable(false);

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewChild = inflater.inflate(R.layout.loader, null);

        dialog.setContentView(viewChild);
        ButterKnife.bind(this, viewChild);

//		GifMovieView view = new GifMovieView(mContext);

        Runtime.getRuntime().gc();
        System.gc();

//		mRelativeLayout.addView(view);

        try {
            dialog.show();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    /**
     * Hide progress dialog
     */
    public void hideProgressBar() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private class NullHostNameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            Log.debug("RestUtilImpl", "Approving certificate for " + hostname);
            return true;
        }

    }

}