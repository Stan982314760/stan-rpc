package com.proj.stan.connect.compress;

import com.proj.stan.common.extension.SPI;

/**
 * @Author: stan
 * @Date: 2021/02/19
 * @Description:
 */
@SPI
public interface Compress {

    byte[] compress(byte[] beforeCompress);

    byte[] decompress(byte[] afterCompress);
}
