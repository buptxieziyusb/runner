package cn.bupt.runningapp.appserver.service;

import cn.bupt.runningapp.appserver.model.AccessToken;
import cn.bupt.runningapp.appserver.model.User;
import cn.bupt.runningapp.appserver.model.UserDetail;
import cn.bupt.runningapp.appserver.model.UserDetailPlusAccessToken;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Mojo on 2017/4/11.
 */

public interface LoginService {

    @FormUrlEncoded
    @POST("o/token/")
    Call<AccessToken> getAccessToken(
            @Field("username") String username,
            @Field("password") String password,
            @Field("grant_type") String grantType);

    @FormUrlEncoded
    @POST("o/token/")
    Call<AccessToken> refressAccessToken(
            @Field("refresh_token") String refresh_token,
            @Field("grant_type") String grantType);


    @POST("register/")
    Call<UserDetail> register(
            @Body User user);

    @FormUrlEncoded
    @POST("social-login/")
    Call<UserDetailPlusAccessToken> socialLogin(
            @Field("uid") String uid,
            @Field("access_token") String accessToken,
            @Field("type") String type,
            @Field("refresh_token") String refreshToken
    );

}
