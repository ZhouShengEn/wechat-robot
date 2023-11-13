package com.zhoushengen.robot.job.task;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhoushengen.robot.wechat.dto.BaseWeChatResDTO;
import com.zhoushengen.robot.wechat.dto.QueryRoomsResDTO;
import com.zhoushengen.robot.wechat.dto.SendTextMsgReqDTO;
import com.zhoushengen.robot.wechat.entity.PushMessage;
import com.zhoushengen.robot.wechat.mapper.PushMessageMapper;
import com.zhoushengen.robot.wechat.remote.JuHeRemoteApi;
import com.zhoushengen.robot.wechat.remote.JuHeWeatherRemoteApi;
import com.zhoushengen.robot.wechat.remote.WeChatRemoteApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhoushengen
 * @version 1.0
 * @date 2023/9/2 19:37
 */
@Component
@Slf4j
public class SendNewsJob extends QuartzJobBean {

    private final String[] TITLE_NUM = new String[]{"①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧"};

    @Autowired
    private PushMessageMapper pushMessageMapper;

    @Autowired
    private WeChatRemoteApi weChatRemoteApi;

    @Autowired
    private JuHeRemoteApi juHeRemoteApi;

    @Autowired
    private JuHeWeatherRemoteApi juHeWeatherRemoteApi;

    @Value("${ak.juHe.fapig}")
    private String fapigKey;

    @Value("${ak.juHe.todayOnhistory}")
    private String todayOnhistoryKey;

    @Value("${ak.juHe.calendar}")
    private String calendarKey;

    @Value("${ak.juHe.simpleWeather}")
    private String simpleWeatherKey;

    @Value("${wechat.register}")
    private String registerWxid;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        try {
            Date date = new Date();
            Calendar ca = Calendar.getInstance();
            ca.setTime(date);
            String year = String.valueOf(ca.get(Calendar.YEAR));
            String month = String.valueOf(ca.get(Calendar.MONTH)+1);
            String day = String.valueOf(ca.get(Calendar.DAY_OF_MONTH));

            // 1-万年历
            StringBuilder wannianliMessage = new StringBuilder();
            wannianliMessage.append("\n👉[發]今日万年历[發]👈\n");
            Response<JSONObject> wanninaliRes =
                juHeRemoteApi.calendar(year.concat("-").concat(month).concat("-").concat(day), calendarKey);
            LinkedHashMap result = wanninaliRes.body().getObject("result", LinkedHashMap.class);
            LinkedHashMap data = (LinkedHashMap)result.get("data");
            String animalsYear = String.valueOf(data.get("animalsYear"));
            String weekday = String.valueOf(data.get("weekday"));
            String lunarYear = String.valueOf(data.get("lunarYear"));
            String lunar = String.valueOf(data.get("lunar"));
            String suit = String.valueOf(data.get("suit"));
            String avoid = String.valueOf(data.get("avoid"));
            wannianliMessage.append(animalsYear).append(",").append(weekday).append(",").append(lunarYear).append(",农历").append(lunar)
                    .append("\n[OK][适宜]：").append(suit).append("\n[NO][不宜]：").append(avoid);


            // 2-历史上的今天
            Response<JSONObject> historyRes = juHeRemoteApi.todayOnhistory(month.concat("/").concat(day),
                todayOnhistoryKey);
            ArrayList historyResult = historyRes.body().getObject("result", ArrayList.class);
            StringBuilder historyMessage = new StringBuilder();
            if (historyResult.size() > 0) {
                int item = Math.min(historyResult.size(), 8);
                historyMessage.append("👉[咖啡]历史上的今天[咖啡]👈\n");
                for (int i = 0; i < item; i++) {

                    LinkedHashMap history = (LinkedHashMap) historyResult.get(i);
                   historyMessage.append(TITLE_NUM[i]).append(history.get("date")).append(",").append(history.get("title")).append("\n");
                }
            }
            historyMessage.append("......");

            // 5-新闻
//            StringBuilder newsMessage = new StringBuilder("👉[666]国际新闻推送[666]👈");
//            Response<JSONObject> newsRes = juHeRemoteApi.toutiao("guoji", 1, 5, 0, "59c605d44b2929ec74a96f14b8fcb13e");
//            LinkedHashMap newsResult = newsRes.body().getObject("result", LinkedHashMap.class);
//            ArrayList newsData = (ArrayList)newsResult.get("data");
//            newsData.forEach(dataN -> {
//                LinkedHashMap newsDataN = (LinkedHashMap) dataN;
//                newsMessage.append("\n").append(newsDataN.get("title")).append(newsDataN.get("url"));
//            });
            HashMap<String, String> atmosphereMessageMap = new HashMap<>();
            HashMap<String, String> weatherMessageMap = new HashMap<>();
            BaseWeChatResDTO<List<QueryRoomsResDTO>> roomRes = weChatRemoteApi.queryRooms(registerWxid);

            QueryWrapper<PushMessage> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("REGISTER_WXID", registerWxid);
            List<String> roomWxids = pushMessageMapper.selectList(queryWrapper).stream().map(PushMessage::getRoomWxid).collect(Collectors.toList());
            roomRes.getResult().forEach(room -> {
                if (!roomWxids.contains(room.getWxid())) {

                    String remark = String.valueOf(room.getRemark());
                    // 3-气象预警
                    StringBuilder atmosphereMessage = new StringBuilder();
                    // 4-天气预报
                    StringBuilder weatherMessage = new StringBuilder();
                    if (StringUtils.isNotEmpty(remark)) {
                        if (remark.contains("320100")) {
                            // 南京市
                            assembleAtmosphereMessage(atmosphereMessageMap, atmosphereMessage, "320100");
                            assembleWeatherMessage(weatherMessageMap, weatherMessage, "南京");
                        } else if (remark.contains("340100")) {
                            // 合肥市
                            assembleAtmosphereMessage(atmosphereMessageMap, atmosphereMessage, "340100");
                            assembleWeatherMessage(weatherMessageMap, weatherMessage, "合肥");
                        } else if (remark.contains("150400")) {
                            // 赤峰市
                            assembleAtmosphereMessage(atmosphereMessageMap, atmosphereMessage, "150400");
                            assembleWeatherMessage(weatherMessageMap, weatherMessage, "赤峰");
                        }
                    }

//                    String message = "【每日准点推送】" + wannianliMessage.toString() + atmosphereMessage.toString()
//                            + weatherMessage.toString() + historyMessage.toString();
                    String wxid = String.valueOf(room.getWxid());
                    weChatRemoteApi.sendTextMsg(registerWxid, new SendTextMsgReqDTO(wxid, "【每日准点推送】" + wannianliMessage.toString()));
                    weChatRemoteApi.sendTextMsg(registerWxid, new SendTextMsgReqDTO(wxid, atmosphereMessage.toString()
                            + weatherMessage.toString()));
                    weChatRemoteApi.sendTextMsg(registerWxid, new SendTextMsgReqDTO(wxid, historyMessage.toString()));
//                    weChatRemoteApi.sendTextMsg(registerWxid, new SendTextMsgReqDTO(wxid, newsMessage.toString()));
                }
            });


        }catch (Exception e) {
            log.error("定时任务执行失败,跳过此次任务执行。", e);
        }
    }

    private void assembleAtmosphereMessage(HashMap<String, String> atmosphereMessageMap, StringBuilder atmosphereMessage, String city) {
        String existMsg = atmosphereMessageMap.get(city);
        if (null != existMsg) {
            atmosphereMessage.append(existMsg);
        } else {
            Response<JSONObject> atmosphereRes = juHeWeatherRemoteApi.fapig(null, city, null, fapigKey);
            if (null != atmosphereRes.body().get("result")) {
                atmosphereMessage.append("\n👉[炸弹]气象预警[炸弹]👈\n");
                ArrayList atmosphereResult = atmosphereRes.body().getObject("result", ArrayList.class);
                atmosphereResult.forEach(atoItem -> {
                    LinkedHashMap atoItemObject = (LinkedHashMap) atoItem;
                    atmosphereMessage.append("\n").append(atoItemObject.get("atoItemObject")).append("\n").append(atoItemObject.get("content")).append("\n");
                });
            }
            atmosphereMessageMap.put(city, atmosphereMessage.append("\n").toString());
        }
    }

    private void assembleWeatherMessage(HashMap<String, String> weatherMessageMap, StringBuilder weatherMessage, String city) {
        String existMsg = weatherMessageMap.get(city);
        if (null != existMsg) {
            weatherMessage.append(existMsg);
        } else {
            Response<JSONObject> atmosphereRes = juHeWeatherRemoteApi.simpleWeather(city, simpleWeatherKey);
            if (null != atmosphereRes.body().get("result")) {
                weatherMessage.append("👉[太阳]【天气预报】[太阳]👈\n");
                LinkedHashMap atmosphereResult = atmosphereRes.body().getObject("result", LinkedHashMap.class);
                LinkedHashMap realtime = (LinkedHashMap)atmosphereResult.get("realtime");
                ArrayList futures = (ArrayList)atmosphereResult.get("future");
                LinkedHashMap future = (LinkedHashMap) futures.get(0);
                weatherMessage.append("【").append(city).append("市】").append("\n[转圈][当前天气]：").append(realtime.get("temperature")).append("度, ")
                        .append(realtime.get("info")).append(", ").append(realtime.get("direct"))
                        .append(" ").append(realtime.get("power")).append("\n[生病][空气质量指数]：").append(realtime.get("aqi"))
                        .append("\n[跳跳][今日天气]：").append(String.valueOf(future.get("temperature")).replace("/", "到"))
                        .append(", ").append(future.get("weather")).append(", ").append(future.get("direct"));
            }
            System.out.println("调用了一次");
            weatherMessageMap.put(city, weatherMessage.append("\n").toString());
        }
    }
}
