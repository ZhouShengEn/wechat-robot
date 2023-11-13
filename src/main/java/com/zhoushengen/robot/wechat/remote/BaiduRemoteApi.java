package com.zhoushengen.robot.wechat.remote;

import com.alibaba.fastjson.JSONObject;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/9/7 12:45
 **/
@RetrofitClient(baseUrl = "https://api.map.baidu.com/")
public interface BaiduRemoteApi {

    /**
     * 经纬度编码
     * @param ak
     * @param output
     * @param coordtype
     * @param location
     * @return
     */
    @GET("reverse_geocoding/v3/")
    Response<JSONObject> locationTransform(@Query("ak") String ak, @Query("output") String output,  @Query("coordtype") String coordtype,  @Query("location") String location);
}
