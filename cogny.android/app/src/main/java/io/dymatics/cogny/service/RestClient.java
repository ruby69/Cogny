package io.dymatics.cogny.service;

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
import java.util.List;

import io.dymatics.cogny.BuildConfig;
import io.dymatics.cogny.Constants;
import io.dymatics.cogny.On;
import io.dymatics.cogny.R;
import io.dymatics.cogny.domain.model.DriveHistory;
import io.dymatics.cogny.domain.model.History;
import io.dymatics.cogny.domain.model.Fota;
import io.dymatics.cogny.domain.model.Meta;
import io.dymatics.cogny.domain.model.ObdDevice;
import io.dymatics.cogny.domain.model.Partner;
import io.dymatics.cogny.domain.model.RepairMsg;
import io.dymatics.cogny.domain.model.Sensings;
import io.dymatics.cogny.domain.model.User;
import io.dymatics.cogny.domain.model.UserMobileDevice;
import io.dymatics.cogny.domain.model.Vehicle;
import io.dymatics.cogny.support.PrefsService_;
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
                        .addInterceptor(new GzipRequestInterceptor())
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
        on.ready();
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
                        toast(call.request().url().toString() + " -> " + response.message());
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

    public void fetchPartner(String code, On<Partner> on) {
        request(restApi.partner(code), on);
    }

    public void fetchFota(Fota.Type type, On<Fota> on) {
        request(restApi.fotas(type), on);
    }

    public void postUserMobiles(UserMobileDevice.Form form, On<UserMobileDevice> on) {
        request(restApi.mobiles(form), on);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void fetchObdDevice(String obdSerial, On<ObdDevice> on) {
        request(restApi.obdDevice(obdSerial), on);
    }

    public void fetchVehicle(Long obdDeviceNo, On<Vehicle> on) {
        request(restApi.vehicle(obdDeviceNo), on);
    }

    public void postDrives(DriveHistory driveHistory, On<DriveHistory> on) {
        request(restApi.drives(driveHistory), on);
    }

    public void postDrivef(DriveHistory driveHistory, On<Void> on) {
        request(restApi.drivef(driveHistory), on);
    }

    public void postSensing(Sensings sensings, On<Void> on) {
        request(restApi.sensing(sensings), on);
    }

    public void postActivityLogs(List<String> activityLogs, On<Void> on) {
        request(restApi.activityLogs(activityLogs), on);
    }

    public void fetchRepairMsg(Long vehicleNo, On<RepairMsg> on) {
        request(restApi.messages(vehicleNo), on);
    }

    public void fetchHistories(Long vehicleNo, int page, On<History.Page> on) {
        request(restApi.histories(vehicleNo, page), on);
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
