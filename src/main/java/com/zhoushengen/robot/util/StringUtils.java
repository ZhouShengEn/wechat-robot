package com.zhoushengen.robot.util;

/**
 * @Author: zhoushengen
 * @Description: TODO
 * @DateTime: 2023/11/7 10:09
 **/
public class StringUtils {

    /**
     * 字符串切割
     * @param s
     * @param chunkSize
     * @return
     */
    public static String[] splitByNumber(String s, int chunkSize){
        int chunkCount = (s.length() / chunkSize) + (s.length() % chunkSize == 0 ? 0 : 1);
        String[] returnVal = new String[chunkCount];
        for(int i=0;i<chunkCount;i++){
            returnVal[i] = s.substring(i*chunkSize, Math.min((i+1)*chunkSize, s.length()));
        }
        return returnVal;
    }
}
