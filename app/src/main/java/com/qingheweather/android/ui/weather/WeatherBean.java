package com.qingheweather.android.ui.weather;

public class WeatherBean {

    public static final String SUN = "晴";
    public static final String SUN_NIGHT = "晴晚";
    public static final String SUN_CLOUD = "多云";
    public static final String SUN_CLOUD_NIGHT = "多云晚";
    public static final String CLOUDY ="阴";
    public static final String LIGHT_RAIN = "小雨";
    public static final String MODERATE_RAIN = "中雨";
    public static final String HEAVY_RAIN = "大雨";
    public static final String STORM_RAIN = "暴雨";
    public static final String THUNDER_RAIN = "雷阵雨";
    public static final String THUNDER = "雷";
    public static final String LIGHT_SNOW = "小雪";
    public static final String MODERATE_SNOW = "中雪";
    public static final String HEAVY_SNOW = "大雪";
    public static final String STORM_SNOW = "暴雪";
    public static final String SLEET = "雨夹雪";
    public static final String HAIL = "冰雹";
    public static final String LIGHT_HAZE = "轻度雾霾";
    public static final String MODERATE_HAZE = "中度雾霾";
    public static final String HEAVY_HAZE = "重度雾霾";
    public static final String FOG = "雾";


    public String weather;  //天气，取值为上面6种
    public int temperature; //温度值
    public String temperatureStr; //温度的描述值
    public String time; //时间值

    public WeatherBean(String weather, int temperature,String time) {
        this.weather = weather;
        this.temperature = temperature;
        this.time = time;
        this.temperatureStr = temperature + "°";
    }

    public WeatherBean(String weather, int temperature, String temperatureStr, String time) {
        this.weather = weather;
        this.temperature = temperature;
        this.temperatureStr = temperatureStr;
        this.time = time;
    }

    public static String[] getAllWeathers(){
        String[] str = {SUN,SUN_NIGHT,SUN_CLOUD,SUN_CLOUD_NIGHT,CLOUDY,LIGHT_RAIN,MODERATE_RAIN,
                HEAVY_RAIN,STORM_RAIN,THUNDER_RAIN,THUNDER,LIGHT_SNOW,MODERATE_SNOW,HEAVY_SNOW,
                STORM_SNOW,SLEET,HAIL,LIGHT_HAZE,MODERATE_HAZE,HEAVY_HAZE,FOG};
        return str;
    }

}