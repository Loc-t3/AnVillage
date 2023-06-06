package com.mc.common.utils;

import lombok.Getter;

/**
 * @作者：XMC
 * @邮箱：1309478453@qq.com
 * @创建时间： 2023-04-15 16:21
 * @类说明：填写类说明
 * @修改记录：
 */
@Getter
public enum VillageOrAppellationEnum {

    VILLAGENAME1("1", "富强"),
    VILLAGENAME2("2","民主"),
    VILLAGENAME3("3","文明"),
    VILLAGENAME4("4","和谐"),
    VILLAGENAME5("5","自由"),
    VILLAGENAME6("6","平等"),
    VILLAGENAME7("7","公正"),
    VILLAGENAME8("8","法治"),
    VILLAGENAME9("9","爱国"),
    VILLAGENAME10("10","敬业"),
    VILLAGENAME11("11","诚信"),
    VILLAGENAME12("12","友善");

    private final String code;
    private final String name;


    VillageOrAppellationEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static VillageOrAppellationEnum getByCode(String code){
        for (VillageOrAppellationEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;

    }



    //村庄名称
//   社会主义核心价值观: 富强、民主、文明、和谐、自由、平等、公正、法治、爱国、敬业、诚信、友善
   /* public static final String VILLAGE_NAME_1 ="富强";
    public static final String VILLAGE_NAME_2 ="民主";
    public static final String VILLAGE_NAME_3 ="文明";
    public static final String VILLAGE_NAME_4 ="和谐";
    public static final String VILLAGE_NAME_5 ="自由";
    public static final String VILLAGE_NAME_6 ="平等";
    public static final String VILLAGE_NAME_7 ="公正";
    public static final String VILLAGE_NAME_8 ="法治";
    public static final String VILLAGE_NAME_9 ="爱国";
    public static final String VILLAGE_NAME_10 ="敬业";
    public static final String VILLAGE_NAME_11 ="诚信";
    public static final String VILLAGE_NAME_12 ="友善";

    //称谓
    public static final String APPELATION_NAME_1 ="无名小卒";
    public static final String APPELATION_NAME_2 ="默默无闻";
    public static final String APPELATION_NAME_3 ="平民英雄";
    public static final String APPELATION_NAME_4 ="万人迷";
    public static final String APPELATION_NAME_5 ="超级巨星";
    public static final String APPELATION_NAME_6 ="传奇人物";

    //称谓描述
    public static final String APPELATION_DESC_1 ="欢迎程度较低，没有太多人知道";
    public static final String APPELATION_DESC_2 ="受欢迎程度有所提升，但是还没有广泛的知名度";
    public static final String APPELATION_DESC_3 ="受欢迎程度已经开始有所突破，有一定的粉丝群体";
    public static final String APPELATION_DESC_4 ="欢迎程度已经很高，有大量的粉丝群体";
    public static final String APPELATION_DESC_5 ="受欢迎程度已经达到顶峰，无数人关注";
    public static final String APPELATION_DESC_6 ="受欢迎程度已经超越了时代，成为经典的代表";*/


}
