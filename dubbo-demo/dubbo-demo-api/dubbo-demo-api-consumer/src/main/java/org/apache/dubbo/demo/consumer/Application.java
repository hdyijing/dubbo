/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.demo.consumer;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.demo.DemoService;
import org.apache.dubbo.rpc.service.GenericService;

public class Application {
    public static void main(String[] args) {
        if (isClassic(args)) {
            runWithRefer();
        } else {
            runWithBootstrap();
        }
    }

    private static boolean isClassic(String[] args) {
        return args.length > 0 && "classic".equalsIgnoreCase(args[0]);
    }

    private static void runWithBootstrap() {
        // 调用方接口配置
        ReferenceConfig<DemoService> reference = new ReferenceConfig<>();
        // 调用的接口
        reference.setInterface(DemoService.class);
        // 通用序列化
        reference.setGeneric("true");

        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        // 应用名
        bootstrap.application(new ApplicationConfig("dubbo-demo-api-consumer"))
            // 注册中心地址
            .registry(new RegistryConfig("zookeeper://192.168.31.116:2181"))
            // 协议
            .protocol(new ProtocolConfig(CommonConstants.DUBBO, -1))
            // 添加调用配置
            .reference(reference)
            // 启动
            .start();

        DemoService demoService = bootstrap.getCache().get(reference);
        String message = demoService.sayHello("dubbo");
        System.out.println(message);

        // generic invoke 通用调用
        GenericService genericService = (GenericService) demoService;
        Object genericInvokeResult = genericService.$invoke("sayHello", new String[]{String.class.getName()},
            new Object[]{"dubbo generic invoke"});
        System.out.println(genericInvokeResult);
    }

    private static void runWithRefer() {
        // 调用方配置
        ReferenceConfig<DemoService> reference = new ReferenceConfig<>();
        // 应用名
        reference.setApplication(new ApplicationConfig("dubbo-demo-api-consumer"));
        // 注册中心地址
        reference.setRegistry(new RegistryConfig("zookeeper://192.168.31.116:2181"));
        // 元数据配置地址
        reference.setMetadataReportConfig(new MetadataReportConfig("zookeeper://192.168.31.116:2181"));
        // 设置调用的接口
        reference.setInterface(DemoService.class);
        // 获取接口代理类
        DemoService service = reference.get();
        // 调用接口
        String message = service.sayHello("dubbo");
        System.out.println(message);
    }
}
