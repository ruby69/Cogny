package io.dymatics.cogny.service;

import java.io.IOException;

import io.dymatics.cogny.support.PrefsService_;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookiesInterceptor implements Interceptor {
    protected PrefsService_ prefs;

    public AddCookiesInterceptor(PrefsService_ prefs) {
        this.prefs = prefs;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
//        for (String cookie : prefs.cookies().getOr(new HashSet<String>())) {
//            builder.addHeader("Cookie", cookie);
//            if (BuildConfig.DEBUG) {
//                Log.i("## AddCookies", cookie);
//            }
//        }
        return chain.proceed(builder.build());
    }
}