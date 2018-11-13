package io.dymatics.cognyreport.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.Nullable;
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.dymatics.cognyreport.BuildConfig;
import io.dymatics.cognyreport.Constants;
import io.dymatics.cognyreport.On;
import io.dymatics.cognyreport.R;
import io.dymatics.cognyreport.domain.model.Diagnosis;
import io.dymatics.cognyreport.domain.model.DtcReport;
import io.dymatics.cognyreport.domain.model.Meta;
import io.dymatics.cognyreport.domain.model.Partner;
import io.dymatics.cognyreport.domain.model.PerformHistory;
import io.dymatics.cognyreport.domain.model.RepairForm;
import io.dymatics.cognyreport.domain.model.RepairMsg;
import io.dymatics.cognyreport.domain.model.User;
import io.dymatics.cognyreport.domain.model.UserMobileDevice;
import io.dymatics.cognyreport.support.PrefsService_;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@EBean(scope = EBean.Scope.Singleton)
public class RestClient {
    @RootContext Context context;
    @Bean CognyBean cognyBean;
    @Pref PrefsService_ prefs;
    @SystemService ConnectivityManager connectivityManager;

    @Setter private boolean openedMain;
    private RestApi restApi;

    class NullOnEmptyConverterFactory extends Converter.Factory {
        @Nullable
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return (Converter<ResponseBody, Object>) body -> {
                if (body.contentLength() == 0) return null;
                return delegate.convert(body);
            };
        }
    }

    @AfterInject
    void afterInject() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getResources().getString(BuildConfig.DEBUG ? R.string.base_url_dev : R.string.base_url))
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new UserNoHeaderInterceptor(cognyBean))
                        .addInterceptor(new AddCookiesInterceptor(prefs))
                        .addInterceptor(new ReceivedCookiesInterceptor(prefs))
                        .build())
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        restApi = retrofit.create(RestApi.class);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void log(final Call call, Throwable e) {
        Constants.log(call.request().url().toString() + " -> response is fail \n" + e.getMessage());
    }

    private <T> void request(final Call<T> call, final On<T> on) {
        if (isConnected()) {
            call.enqueue(new retrofit2.Callback<T>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {
                    T body = null;
                    if (response.isSuccessful()) {
                        body = response.body();
                        on.success(body);
                        Constants.log(call.request().url().toString() + " -> response is successful" + (body != null ? " \n" + body.toString() : ""));
                    } else {
                        toast(response.message());
                        on.failure(new RuntimeException(response.message()));
                        Constants.log(call.request().url().toString() + " -> response is fail \n" + response.message());
                    }
                    on.complete(body);
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {
                    log(call, t);
                    on.failure(t);
                    on.complete(null);
                    if (openedMain) {
                        toast(R.string.message_common_net_error);
                    }
                }
            });
        } else {
            Throwable t = new RuntimeException("not connected");
            log(call, t);
            on.failure(t);
            on.complete(null);
            if (openedMain) {
                toast(R.string.message_common_net_state_check);
            }
        }
    }

    private boolean isConnected() {
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE);
        } else {
            return false;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void fetchMeta(On<Meta> on) {
        request(restApi.meta(), on);
    }

    public void checkInvite(String invitationCode, On<User.Invitation> on) {
        request(restApi.checkInvite(invitationCode), on);
    }

    public void signin(String token, On<User> on) {
        request(restApi.signin(token), on);
    }

    public void signup(User user, On<User> on) {
        request(restApi.signup(user), on);
    }

    public void checkUser(String token, On<User> on) {
        request(restApi.checkUser(token), on);
    }

    public void revoke(String token, On<User> on) {
        request(restApi.revoke(token), on);
    }

    public void fetchPartner(On<Partner> on) {
        request(restApi.partner(), on);
    }

    public void postUserMobiles(UserMobileDevice.Form form, On<UserMobileDevice> on) {
        request(restApi.mobiles(form), on);
    }

    public void fetchDtcReports(On<DtcReport.Groups> on) {
        request(restApi.dtcReports(), on);
    }

    public void fetchDiagnosisCategories(On<Diagnosis.Categories> on) {
        request(restApi.diagnosisCategories(), on);
    }

    public void fetchPerforms(Long vehicleNo, int page, On<PerformHistory.Page> on) {
        request(restApi.performs(vehicleNo, page), on);
    }

    public void postRepairNoti(RepairMsg.Form form, On<RepairMsg> on) {
        request(restApi.repairNoti(form), on);
    }

    public void postRepairs(RepairForm form, On<Void> on) {
        request(restApi.repairs(form), on);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @UiThread
    void toast(int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    @UiThread
    void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
