package io.dymatics.cognyreport.service;

import io.dymatics.cognyreport.domain.model.Diagnosis;
import io.dymatics.cognyreport.domain.model.DtcReport;
import io.dymatics.cognyreport.domain.model.Meta;
import io.dymatics.cognyreport.domain.model.Partner;
import io.dymatics.cognyreport.domain.model.PerformHistory;
import io.dymatics.cognyreport.domain.model.RepairForm;
import io.dymatics.cognyreport.domain.model.RepairMsg;
import io.dymatics.cognyreport.domain.model.ReportHistory;
import io.dymatics.cognyreport.domain.model.User;
import io.dymatics.cognyreport.domain.model.UserMobileDevice;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApi {

    @GET("meta")
    Call<Meta> meta();

    @GET("invite/PARTNER_MECHANIC/{invitationCode}")
    Call<User.Invitation> checkInvite(@Path("invitationCode") String invitationCode);

    @POST("signin")
    Call<User> signin(@Body String token);

    @POST("signup")
    Call<User> signup(@Body User user);

    @POST("users/check")
    Call<User> checkUser(@Body String token);

    @POST("revoke")
    Call<User> revoke(@Body String token);

    @GET("partners")
    Call<Partner> partner();

    @POST("mobiles")
    Call<UserMobileDevice> mobiles(@Body UserMobileDevice.Form form);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @GET("report/list")
    Call<DtcReport.Groups> dtcReports();

    @GET("report/diagcate")
    Call<Diagnosis.Categories> diagnosisCategories();

    @GET("report/performs/{vehicleNo}?scale=90")
    Call<PerformHistory.Page> performs(@Path("vehicleNo") Long vehicleNo, @Query("page") int page);

    @POST("report/repair/noti")
    Call<RepairMsg> repairNoti(@Body RepairMsg.Form form);

    @POST("report/repairs")
    Call<Void> repairs(@Body RepairForm form);


}
