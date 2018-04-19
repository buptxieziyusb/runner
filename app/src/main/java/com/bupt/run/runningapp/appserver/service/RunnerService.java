package com.bupt.run.runningapp.appserver.service;

import java.util.List;

import com.bupt.run.runningapp.appserver.model.GiftTicket;
import com.bupt.run.runningapp.appserver.model.Merchant;
import com.bupt.run.runningapp.appserver.model.PhysicalStatus;
import com.bupt.run.runningapp.appserver.model.ReadRoute;
import com.bupt.run.runningapp.appserver.model.UserDetail;
import com.bupt.run.runningapp.appserver.model.WriteRoute;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Mojo on 2017/4/11.
 */

public interface RunnerService {

    @GET("my/")
    Call<UserDetail> myInfo();

    @PATCH("my/")
    Call<UserDetail> editMyInfo(@Body UserDetail userDetail);

    @GET("Routes/")
    Call<List<ReadRoute>> getRouteHistory();

    @POST("Routes/")
    Call<ReadRoute> postFinishedRoute(@Body WriteRoute writeRoute);

    @PATCH("my/")
    Call<ResponseBody> uploadAvatar(@Body RequestBody requestBody);

    @GET("PhysicalStatus/")
    Call<List<PhysicalStatus>> getPhysicalStatus();

    @POST("PhysicalStatus/")
    Call<PhysicalStatus> postNewWeight(@Body RequestBody requestBody);

    @GET("Merchants/")
    Call<List<Merchant>> searchMerchants(
            @Query("longitude") Double longitude,
            @Query("latitude") Double latitude,
            @Query("max") Integer max,
            @Query("min") Integer min
    );

    @GET("Gifts/")
    Call<List<GiftTicket>> obtainedGifts();

    @POST("Gifts/")
    Call<GiftTicket> giftTicketRequire(@Body Merchant merchant);
}
