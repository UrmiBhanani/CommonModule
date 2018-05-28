package com.androidcommonlibrary.http;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.androidcommonlibrary.R;
import com.androidcommonlibrary.http.customexceptions.ResponseFailedException;
import com.androidcommonlibrary.http.customexceptions.UnauthorizationAccessException;
import com.androidcommonlibrary.http.httpannotations.Get;
import com.androidcommonlibrary.http.httpannotations.Multipart;
import com.androidcommonlibrary.http.httpannotations.RequestKey;
import com.androidcommonlibrary.http.httpannotations.Url;
import com.androidcommonlibrary.util.Log;
import com.androidcommonlibrary.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by admin1 on 3/8/17.
 */
//import okhttp3.OkHttpClient;

public class HttpRequestNew extends AsyncTask<Void, Void, ResponseBean> {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    Context mContext;
    HttpRequestHandlerListener httpRequestHandlerListener;
    HashMap<String, String> stringStringHashMap = new HashMap<>();
    Dialog dialog;
    String imgParam;
    String methodName;
    private OkHttpClient client = new OkHttpClient();
    private String urlString = "", request = "";
    private boolean mIsShow;
    private boolean isMultipartReq = false;
    private File file = null;
    private RequestBody mRequestBody = null;
    private ResponseBean responseBean_globle = new ResponseBean();
    private Object[] values;
    private Pattern pattern = Pattern.compile(HTML_PATTERN);
    private boolean isPostRequest = false;


    /**
     * @param methodName:                must be provide method name which you are calling.
     * @param _context                   : Pass current context
     * @param mIsShow                    : boolean to show progressbar
     * @param values                     : Api values must be follow orders of which you passed key
     * @param httpRequestHandlerListener : interface object to get response.
     */

    public HttpRequestNew(String methodName, Context _context, boolean mIsShow, HttpRequestHandlerListener httpRequestHandlerListener, Object... values) {
        try {
            mContext = _context;
            this.mIsShow = mIsShow;
            this.httpRequestHandlerListener = httpRequestHandlerListener;
            this.values = values;
            this.methodName = methodName;

            if (Utils.isOnline(mContext)) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                    cancel(true);
                }
            } else {
                hideProgressBar();
                responseBean_globle.setRequestFailedType(RequestFailedType.ON_INTERNET_FAILED);
                this.httpRequestHandlerListener.onFailed(responseBean_globle);
                cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            if (mIsShow) {
                showProgressBar();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected ResponseBean doInBackground(Void... params) {
        ResponseBean responseBean = null;

        try {
            responseBean = setupRequestBodyFromAnnotation(methodName, values);


            if (!isMultipartReq) {
                if (!isPostRequest)
                    return getRequest();
                else
                    return postRequest();
            } else {
                return uploadMultipartData();
            }
        } catch (Exception e) {
            if(responseBean==null)
            {
                responseBean=new ResponseBean();
                responseBean.setException(e);
                responseBean.setAnyExceptionOccurred(true);
                responseBean.setRequestFailedType(RequestFailedType.ON_EXCEPTION);
            }


        }
        return responseBean;
    }

    @Override
    protected void onPostExecute(ResponseBean responseBean) {
        super.onPostExecute(responseBean);
        if (mIsShow) {
            hideProgressBar();
        }

        if (responseBean != null) {
            if (responseBean.isAnyExceptionOccurred()) {
                handleException(responseBean.getException(), responseBean);
            } else {

                if (Utils.isNotNull(responseBean.getResponseContent())) {

                    try {
                        new JSONObject(responseBean.getResponseContent());
                        httpRequestHandlerListener.onSuccess(responseBean);
                        Log.print("Response:" + responseBean.getResponseContent());
                    } catch (JSONException e) {
                        e.printStackTrace();

                        if (hasHTMLTags(responseBean.getResponseContent())) {
                            responseBean.setRequestFailedType(RequestFailedType.HTML_ARRIVED_FROM_RESPONSE);
                            responseBean.setException(e);
                            httpRequestHandlerListener.onFailed(responseBean);
                        } else {
                            responseBean.setRequestFailedType(RequestFailedType.ON_EXCEPTION);
                            responseBean.setException(e);
                            httpRequestHandlerListener.onFailed(responseBean);
                        }

                    }
                } else {
                    responseBean.setRequestFailedType(RequestFailedType.EMPTY_RESPONSE_FROM_SERVER);
                    httpRequestHandlerListener.onFailed(responseBean);
                }

            }


        }

    }

    public boolean hasHTMLTags(String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    private void handleException(Exception e, ResponseBean responseBean) {


        if (e instanceof UnknownHostException) {
            responseBean.setRequestFailedType(RequestFailedType.ON_INTERNET_FAILED);
            httpRequestHandlerListener.onFailed(responseBean);
        } else if (e instanceof IOException) {
            responseBean.setException(e);
            responseBean.setRequestFailedType(RequestFailedType.ON_INTERNET_FAILED);
            httpRequestHandlerListener.onFailed(responseBean);
        } else if (e instanceof NetworkErrorException) {

        } else {
            responseBean.setRequestFailedType(RequestFailedType.ON_EXCEPTION);
            httpRequestHandlerListener.onFailed(responseBean);
        }


    }


    public ResponseBean postRequest() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {

            Log.print("URL:" + urlString);
            Log.print("Request:" + request);

            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            FormBody.Builder builder = null;
            builder = new FormBody.Builder();
            if (stringStringHashMap.size() != 0) {
                if (stringStringHashMap.size() != 0) {
                    for (Map.Entry<String, String> item : stringStringHashMap.entrySet()) {
                        builder.add(item.getKey(), String.valueOf(item.getValue()));
                    }
                    mRequestBody = builder.build();

                }
            }

            /*if API needs to authenticate than add credential in header and replace username/password with your own credentials*/
            Request request = null;
            if (mRequestBody == null) {
                String credential = Credentials.basic("username", "password");
                request = new Request.Builder()
                        .url(urlString)
                        .header("Authorization", credential)
                        .build();
            } else {
                String credential = Credentials.basic("username", "password");
                request = new Request.Builder()
                        .url(urlString)
                        .header("Authorization", credential)
                        .post(mRequestBody)
                        .build();
            }

            Response response = client.newCall(request).execute();


            if (response.isSuccessful()) {
                String content = response.body().string();
                responseBean_globle.setResponse(response);
                responseBean_globle.setResponseContent(content);

            } else if (response.code() == 401) {
                Exception exception = new UnauthorizationAccessException("Unauthorized access");
                responseBean_globle.setException(exception);
                responseBean_globle.setResponse(response);
                responseBean_globle.setAnyExceptionOccurred(true);
            } else {
                Exception exception = new ResponseFailedException("Response Failed code is  " + response.code());
                responseBean_globle.setException(exception);
                responseBean_globle.setResponse(response);
                responseBean_globle.setAnyExceptionOccurred(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseBean_globle.setAnyExceptionOccurred(true);
            responseBean_globle.setException(e);
        }
        return responseBean_globle;
    }

    public ResponseBean getRequest() {

        Log.print("URL:" + urlString);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            /*if API needs to authenticate than add credential in header and replace username/password with your own credentials*/
            String credential = Credentials.basic("username", "password");
            Request request = new Request.Builder()
                    .header("Authorization", credential)
                    .url(urlString)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String content = response.body().string();
                responseBean_globle.setResponse(response);
                responseBean_globle.setResponseContent(content);

            } else if (response.code() == 401) {
                Exception exception = new UnauthorizationAccessException("Unauthorized access");
                responseBean_globle.setException(exception);
                responseBean_globle.setResponse(response);
                responseBean_globle.setAnyExceptionOccurred(true);
            } else {
                Exception exception = new ResponseFailedException("Response Failed code is  " + response.code());
                responseBean_globle.setException(exception);
                responseBean_globle.setResponse(response);
                responseBean_globle.setAnyExceptionOccurred(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseBean_globle.setAnyExceptionOccurred(true);
            responseBean_globle.setException(e);
        }
        return responseBean_globle;
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

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewChild = inflater.inflate(R.layout.loader, null);

         dialog.setContentView(viewChild);

        Runtime.getRuntime().gc();
        System.gc();

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

    public ResponseBean setupRequestBodyFromAnnotation(String methodName, Object... values) {
        Class<?> ac = mContext.getClass();
        ResponseBean responseBean = new ResponseBean();

       try {


            Method mGetter = null;
            Method[] allMethods = ac.getDeclaredMethods();
            for (Method m : allMethods) {
                if (m.getName().contains(methodName)) {
                    mGetter = m;

                    break;
                }
            }

            if (mGetter != null) {
                if (mGetter.isAnnotationPresent(Url.class)) {
                    Url mXY = mGetter.getAnnotation(Url.class);
                    if (mXY != null) {
                        urlString = mXY.url();
                    }

                    if (!Utils.isNotNull(urlString)) {
                        throw new IllegalArgumentException("URL must not be empty.");
                    }


                    isPostRequest = !mGetter.isAnnotationPresent(Get.class);


                    if (isPostRequest) {


                        if (mGetter.isAnnotationPresent(Multipart.class)) {
                            isMultipartReq = true;
                            Multipart multipart = mGetter.getAnnotation(Multipart.class);
                            this.imgParam = multipart.imagekey();
                            if (TextUtils.isEmpty(this.imgParam)) {
                                throw new IllegalArgumentException("Provide to keyword for image send.");

                            } else {
                                if (values.length > 0) {
                                    List<Object> stringList = new ArrayList<Object>(Arrays.asList(values)); //new ArrayList is only needed if you absolutely need an ArrayList
                                    List<Object> newlist = new ArrayList<>();

                                    for (Object o :
                                            stringList) {
                                        if (o instanceof File) {
                                            this.file = (File) o;
                                        } else {
                                            if (o != null) {
                                                newlist.add(o);

                                            }
                                        }

                                    }

                                    values = null;
                                    int index = 0;

                                    values = new Object[newlist.size()];
                                    for (Object o : newlist) {
                                        values[index] = o;
                                        index++;

                                    }


                                }


                            }


                        }

                        RequestKey requestKey = mGetter.getAnnotation(RequestKey.class);

                        if (values.length != 0) {

                            if (mGetter.isAnnotationPresent(RequestKey.class)) {

                                if (requestKey.keys().length == values.length) {


                                    for (int i = 0; i < values.length; i++) {
                                        stringStringHashMap.put(requestKey.keys()[i], String.valueOf(values[i]));
                                    }


                                } else {


                                    throw new IllegalArgumentException("Method arguments and passed keys count should be same.");
                                }

                            } else {
                                throw new IllegalArgumentException("Method arguments and passed keys count should be same.");

                            }


                        } else {


                            if (mGetter.isAnnotationPresent(RequestKey.class)) {

                                if (requestKey.keys().length > 0) {
                                    throw new IllegalArgumentException("Method arguments and passed keys count should be same.");

                                }

                            } else {
                                if (isMultipartReq) {

                                } else {
                                    throw new IllegalArgumentException("Method arguments and passed keys count should be same.");

                                }

                            }

                        }


                    } else {

                        if (mGetter.isAnnotationPresent(RequestKey.class)) {
                            RequestKey requestKey = mGetter.getAnnotation(RequestKey.class);

                            if (requestKey.keys().length == values.length) {

                                for (int i = 0; i < requestKey.keys().length; i++) {
                                    stringStringHashMap.put(requestKey.keys()[i], String.valueOf(values[i]));

                                }


                                Uri.Builder builderget = Uri.parse(urlString).buildUpon();
                                if (stringStringHashMap.size() != 0) {


                                    for (Map.Entry<String, String> item : stringStringHashMap.entrySet()) {
                                        builderget.appendQueryParameter(item.getKey(), String.valueOf(item.getValue()));
                                    }
                                    urlString = null;
                                    try {
                                        urlString = builderget.build().toString();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }


                            } else {


                                throw new IllegalArgumentException("Method arguments and passed keys count should be same.");
                            }

                        }


                    }


                } else {
                    throw new IllegalArgumentException("URL annotation not found for call to request.");

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            responseBean.setException(e);
            responseBean.setRequestFailedType(RequestFailedType.ON_EXCEPTION);
            throw new IllegalArgumentException("Something wrong." + e.getMessage());

        }


        return responseBean;
    }

    public ResponseBean uploadMultipartData() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {

            Log.print("URL:" + urlString);
            Log.print("Request:" + mRequestBody);

            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

//            RequestBody body = RequestBody.create(JSON, mRequestBody);
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);

//                    .addHeader("Content-Type","application/json")
//                    .addPart(Headers.of("Content-Type", "application/json", "authorization", "Bearer "+token), body )


            if (stringStringHashMap.size() != 0) {


                for (Map.Entry<String, String> item : stringStringHashMap.entrySet()) {
                    multipartBuilder.addFormDataPart(item.getKey(), String.valueOf(item.getValue()));
                }

            }


            if (file != null) {

                if (file.length() != 0) {
                    multipartBuilder.addFormDataPart(imgParam, file.getName(),
                            RequestBody.create(MediaType.parse("image/png"), file));

                }


            }


            RequestBody requestBody = multipartBuilder
                    .build();


            Request request = new Request.Builder()
                    .url(urlString)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String content = response.body().string();
                responseBean_globle.setResponse(response);
                responseBean_globle.setResponseContent(content);

            } else if (response.code() == 401) {
                Exception exception = new UnauthorizationAccessException("Unauthorized access");
                responseBean_globle.setException(exception);
                responseBean_globle.setResponse(response);
                responseBean_globle.setAnyExceptionOccurred(true);
            } else {
                Exception exception = new ResponseFailedException("Response Failed code is  " + response.code());
                responseBean_globle.setException(exception);
                responseBean_globle.setResponse(response);
                responseBean_globle.setAnyExceptionOccurred(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseBean_globle.setAnyExceptionOccurred(true);
            responseBean_globle.setException(e);
        }
        return responseBean_globle;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        try {
            hideProgressBar();
        } catch (Exception e) {
            e.printStackTrace();
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