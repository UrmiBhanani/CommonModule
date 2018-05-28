package com.androidcommonlibrary.http;

/**
 * Created by admin1 on 3/8/17.
 */

public interface HttpRequestHandlerListener {

    void onSuccess(ResponseBean responseBean);

    void onFailed(ResponseBean responseBean);


}
