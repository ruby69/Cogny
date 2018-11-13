package io.dymatics.cognyreport.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import io.dymatics.cognyreport.Constants;
import io.dymatics.cognyreport.support.PrefsService_;
import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {
    protected PrefsService_ prefs;

    public ReceivedCookiesInterceptor(PrefsService_ prefs) {
        this.prefs = prefs;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            Set<String> cookies = new HashSet<>();
            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
                Constants.log("received cookies : " + header);
            }
//            prefs.cookies().put(cookies);
        }
        return originalResponse;
    }
}
