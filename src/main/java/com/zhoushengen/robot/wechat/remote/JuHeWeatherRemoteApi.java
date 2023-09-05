package com.zhoushengen.robot.wechat.remote;

import com.alibaba.fastjson.JSONObject;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

@RetrofitClient(baseUrl = "http://apis.juhe.cn/")
public interface JuHeWeatherRemoteApi {

    /**
     * 气象预警
     *
     * @param provinceCode
     * @param cityCode
     * @param date
     * @param key
     * @return
     */
    @GET("fapig/alarm/queryV2")
    Response<JSONObject> fapig(@Query("province_code") String provinceCode, @Query("city_code") String cityCode,
        @Query("date") String date, @Query("key") String key);

    /**
     * 天气预报
     *
     * @param city
     * @param key
     * @return
     */
    @GET("simpleWeather/query")
    Response<JSONObject> simpleWeather(@Query("city") String city, @Query("key") String key);
}
