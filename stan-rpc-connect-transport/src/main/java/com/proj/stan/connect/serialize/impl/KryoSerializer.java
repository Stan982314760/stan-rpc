package com.proj.stan.connect.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.proj.stan.common.exception.SerializeException;
import com.proj.stan.connect.remoting.dto.RpcRequest;
import com.proj.stan.connect.remoting.dto.RpcResponse;
import com.proj.stan.connect.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Author: stan
 * @Date: 2021/02/19
 * @Description:
 */
public class KryoSerializer implements Serializer {

    private final ThreadLocal<Kryo> threadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });


    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Output output = new Output(outputStream)) {
            Kryo kryo = threadLocal.get();
            kryo.writeObject(output, obj);
            threadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("Serialization failed");
        }

    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             Input input = new Input(inputStream)) {
            Kryo kryo = threadLocal.get();
            T t = kryo.readObject(input, clazz);
            threadLocal.remove();
            return t;
        } catch (Exception e) {
            throw new SerializeException("Deserialization failed");
        }
    }
}
