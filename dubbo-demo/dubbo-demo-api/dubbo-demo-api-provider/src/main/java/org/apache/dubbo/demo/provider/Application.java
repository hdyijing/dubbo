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
package org.apache.dubbo.demo.provider;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.*;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.demo.DemoService;

import java.util.concurrent.CountDownLatch;

public class Application {
    public static void main(String[] args) throws Exception {
        args = new String[]{"classic"};
        if (isClassic(args)) {
            startWithExport();
        } else {
            startWithBootstrap();
        }
    }

    private static boolean isClassic(String[] args) {
        return args.length > 0 && "classic".equalsIgnoreCase(args[0]);
    }

    private static void startWithBootstrap() {
        // 服务配置,即暴露的接口
        ServiceConfig<DemoServiceImpl> service = new ServiceConfig<>();
        service.setInterface(DemoService.class);
        service.setRef(new DemoServiceImpl());

        // dubbo引导程序
        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        bootstrap.application(new ApplicationConfig("dubbo-demo-api-provider"))
            // 注册中心,此处使用zk
            .registry(new RegistryConfig("zookeeper://192.168.31.116:2181"))
            // 使用dubbo协议
            .protocol(new ProtocolConfig(CommonConstants.DUBBO, -1))
            // 暴露的service服务
            .service(service)
            // 启动
            .start()
            // 阻塞等待
            .await();
    }

    private static void startWithExport() throws InterruptedException {
        // 服务配置,即暴露的接口
        ServiceConfig<DemoServiceImpl> service = new ServiceConfig<>();
        service.setInterface(DemoService.class);
        service.setRef(new DemoServiceImpl());
        // 服务名
        service.setApplication(new ApplicationConfig("dubbo-demo-api-provider"));
        // 注册中心地址
        service.setRegistry(new RegistryConfig("zookeeper://192.168.31.116:2181"));
        // 元数据配置地址
        service.setMetadataReportConfig(new MetadataReportConfig("zookeeper://192.168.31.116:2181"));
        // 发布服务
        service.export();

        System.out.println("dubbo service started");
        new CountDownLatch(1).await();
    }
}
