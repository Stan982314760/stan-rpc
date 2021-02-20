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
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip"),

    ;


    private final byte code;
    private final String desc;


    public static String getName(byte code) {
        for (CompressTypeEnum anEnum : values()) {
            if (code == anEnum.getCode()) {
                return anEnum.getDesc();
            }
        }

        return "";
    }

}
