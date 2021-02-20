package com.proj.stan.connect.serialize;

import com.proj.stan.common.extension.SPI;

/**
 * @Author: stan
 * @Date: 2021/02/19
 * @Description:
 */
@SPI
public interface Serializer {

    byte[] serialize(Object obj);

    <T> T deserialize(byte[] data, Class<T> clazz);
}
