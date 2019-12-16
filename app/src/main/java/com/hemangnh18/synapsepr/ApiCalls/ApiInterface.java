package com.hemangnh18.synapsepr.ApiCalls;


import com.hemangnh18.synapsepr.Models.PostRes;
import com.hemangnh18.synapsepr.Models.PostResponce;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("exec")
    @FormUrlEncoded
    Call<PostResponce> sendPost(@Field("action")  String action,@Field("name")  String fullname, @Field("number")  String number, @Field("alt_number") String alt_number , @Field("email") String email, @Field("institute") String institute ,@Field("city") String city,@Field("event") String event,@Field("team") String team, @Field("total_members") String total_members,@Field("registered_by") String registered_by);


    @POST("exec")
    @FormUrlEncoded
    Call<PostRes> sendAttendence(@Field("action")  String action, @Field("code")  String code, @Field("event")  String event, @Field("attended_by") String attended_by);

}
