package com.yl.serializer.mySerializer.Impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.yl.Exception.SerializeException;
import com.yl.serializer.mySerializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author yl
 * @date 2025-05-08 11:29
 */
public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            HessianOutput hessianOutput = new HessianOutput(bos);
            hessianOutput.writeObject(obj);
            return bos.toByteArray();
        } catch (Exception e){
            throw  new SerializeException("Serialization failed");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        try(ByteArrayInputStream bis = new ByteArrayInputStream(bytes)){
            HessianInput hessianInput = new HessianInput(bis);
            return hessianInput.readObject();
        }catch (IOException e){
            throw new SerializeException("Deserialization failed");
        }
    }

    @Override
    public int getType() {
        return 3;
    }
}
