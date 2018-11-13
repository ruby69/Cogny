package io.dymatics.cogny.service;

import java.util.List;

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
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApi {

    @GET("meta")
    Call<Meta> meta();

    @GET("invite/DRIVER/{invitationCode}")
    Call<User.Invitation> checkInvite(@Path("invitationCode") String invitationCode);

    @POST("signin")
    Call<User> signin(@Body String token);

    @POST("signup")
    Call<User> signup(@Body User user);

    @POST("users/check")
    Call<User> checkUser(@Body String token);

    @POST("revoke")
    Call<User> revoke(@Body String token);

    @GET("partners/{code}")
    Call<Partner> partner(@Path("code") String code);

    @POST("mobiles")
    Call<UserMobileDevice> mobiles(@Body UserMobileDevice.Form form);


    ////////////////////////////////////////////////////////////////////////////////////////////////

    @GET("obds/{obdSerial}")
    Call<ObdDevice> obdDevice(@Path("obdSerial") String obdSerial);

    @GET("vehicle/{obdDeviceNo}")
    Call<Vehicle> vehicle(@Path("obdDeviceNo") Long obdDeviceNo);

    @GET("fotas/{type}")
    Call<Fota> fotas(@Path("type") Fota.Type type);


    ////////////////////////////////////////////////////////////////////////////////////////////////


    @POST("drives")
    Call<DriveHistory> drives(@Body DriveHistory driveHistory);

    @POST("drivef")
    Call<Void> drivef(@Body DriveHistory driveHistory);

    @Headers({"Content-Type: application/json;charset=utf-8", "Content-Encoding: gzip"})
    @POST("sensing")
    Call<Void> sensing(@Body Sensings sensings);

    @Headers({"Content-Type: application/json;charset=utf-8", "Content-Encoding: gzip"})
    @POST("activities")
    Call<Void> activityLogs(@Body List<String> activityLogs);

    @GET("messages/{vehicleNo}")
    Call<RepairMsg> messages(@Path("vehicleNo") Long vehicleNo);

    @GET("histories/{vehicleNo}?scale=90")
    Call<History.Page> histories(@Path("vehicleNo") Long vehicleNo, @Query("page") int page);
}
