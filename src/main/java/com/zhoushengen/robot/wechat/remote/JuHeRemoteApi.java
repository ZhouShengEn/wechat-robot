package com.zhoushengen.robot.wechat.remote;

import com.alibaba.fastjson.JSONObject;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

@RetrofitClient(baseUrl = "http://v.juhe.cn/")
public interface JuHeRemoteApi {

    /**
     * 历史上的今天
     *
     * @param date 日期 月/日
     * @param key
     * @return
     */
    @GET("todayOnhistory/queryEvent.php")
    Response<JSONObject> todayOnhistory(@Query("date") String date, @Query("key") String key);

    /**
     * 新闻头条
     *
     * @param type     支持类型top(推荐,默认)guonei(国内)guoji(国际)yule(娱乐)tiyu(体育)junshi(军事)keji(科技)caijing(财经)youxi(游戏)qiche(汽车)jiankang(健康)
     * @param page     当前页数, 默认1, 最大50
     * @param pageSize 当前页数, 默认1, 最大50
     * @param isFilter 当前页数, 默认1, 最大50
     * @param key
     * @return
     */
    @GET("toutiao/index")
    Response<JSONObject> toutiao(@Query("type") String type, @Query("page") int page, @Query("page_size") int pageSize,
        @Query("is_filter") int isFilter, @Query("key") String key);


    /**
     * 万年历
     *
     * @param date
     * @param key
     * @return
     */
    @GET("calendar/day")
    Response<JSONObject> calendar(@Query("date") String date, @Query("key") String key);

}
