package com.proj.stan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
@AllArgsConstructor
@Getter
@ToString
public enum SerializeTypeEnum {


    KRYO((byte) 0x01, "kryo"),

    ;


    private final byte code;
    private final String desc;


    public static String getName(byte code) {
        for (SerializeTypeEnum anEnum : values()) {
            if (code == anEnum.getCode()) {
                return anEnum.getDesc();
            }
        }

        return "";
    }
}
