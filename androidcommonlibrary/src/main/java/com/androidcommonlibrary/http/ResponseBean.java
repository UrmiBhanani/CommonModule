package com.androidcommonlibrary.http;

import okhttp3.Response;

/**
 * Created by admin1 on 3/8/17.
 */

public class ResponseBean {

    //flag used for checking if any exception occurred.
    private boolean isAnyExceptionOccurred = false;
    //Hold response content from server.
    private String responseContent = null;
    //Hold the exception which occurred;
    private Exception exception = null;
    //Hold the response code which will come into API response.
    private int response_code=0;
    //Response failed at that time hold failing type from "RequestFailedType class"
    private RequestFailedType requestFailedType=null;


    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    //this okhttp response object which consist all requried information like status code etc...by this you can handle particular status code.
    private Response response=null;

    public RequestFailedType getRequestFailedType() {
        return requestFailedType;
    }

    public void setRequestFailedType(RequestFailedType requestFailedType) {
        this.requestFailedType = requestFailedType;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }



    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public boolean isAnyExceptionOccurred() {
        return isAnyExceptionOccurred;
    }

    public void setAnyExceptionOccurred(boolean anyExceptionOccurred) {
        isAnyExceptionOccurred = anyExceptionOccurred;
    }


}
