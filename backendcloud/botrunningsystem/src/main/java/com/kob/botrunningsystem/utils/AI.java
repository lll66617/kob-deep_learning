package com.kob.botrunningsystem.utils;

import ai.onnxruntime.*;
import java.util.*;

public class AI {
    public int getResult(float[] input) throws Exception{
        // 创建 ONNX Runtime 环境
        OrtEnvironment env = OrtEnvironment.getEnvironment();
        // 加载 ONNX 模型
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        OrtSession session = env.createSession("C:\\Users\\86181\\Desktop\\model.onnx",options);

        // 创建输入数据
        //boolean[] inputData = {true, false, false, true, false, false, false,true}; // 根据模型的输入要求提供输入数据
        //float[] inputData = {0.f, 0.f, 0.f, 0.f, 1.f, 0.f, 0.f, 0.f};
        //System.out.println(inputData[0]);

        // 将输入数据封装为 ONNX Tensor
        OnnxTensor inputTensor = OnnxTensor.createTensor(env, input);
        //System.out.println(inputTensor);

        // 执行推理
        OrtSession.Result result = session.run(Collections.singletonMap("onnx::MatMul_0",inputTensor));

        // 解析输出数据
        float[] a=(float[])result.get(0).getValue();

        int maxIndex=-1;
        float maxNum=Float.MIN_VALUE;
        for(int i=0;i<3;i++){
            if(a[i]>maxNum){
                maxIndex=i;
                maxNum=a[i];
            }
        }
        System.out.println(a[0]+" "+a[1]+" "+a[2]);
        return maxIndex;
    }
}
