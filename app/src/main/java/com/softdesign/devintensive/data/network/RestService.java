package com.softdesign.devintensive.data.network;

import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by qwsa on 12.07.2016.
 */
public interface RestService {

    @POST("login")
    Call<UserModelRes> loginUser (@Body UserLoginReq req);

    //new
    /*@Multipart
    @POST("user/{userId}/publicValues/profilePhoto")
    Call<UserModelRes> uploadPhoto(@Path("userId") String userId,
                                   @Part MultipartBody.Part file);*/

    @GET("user/list?orderBy=rating")
    Call<UserListRes> getUserList();



}
