package com.proj.stan.common.file;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @Author: stan
 * @Date: 2021/02/18
 * @Description:
 */
@Slf4j
public class PropertiesFileUtil {

    public static Properties readPropertiesFile(String fileName) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("");
        String filePath = "";
        if (resource != null) {
            filePath = resource.getPath() + fileName;
        }

        Properties pp = null;

        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filePath), StandardCharsets.UTF_8)) {
            pp = new Properties();
            pp.load(reader);
        } catch (IOException e) {
            log.error("读取[{}]配置文件出错", fileName);
        }

        return pp;
    }

}
