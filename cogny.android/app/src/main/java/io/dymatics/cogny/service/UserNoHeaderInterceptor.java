package io.dymatics.cogny.service;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserNoHeaderInterceptor implements Interceptor {
    private CognyBean cognyBean;

    public UserNoHeaderInterceptor(CognyBean cognyBean) {
        this.cognyBean = cognyBean;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("userNo", String.valueOf(cognyBean.getUserNo()));
        return chain.proceed(builder.build());
    }
}