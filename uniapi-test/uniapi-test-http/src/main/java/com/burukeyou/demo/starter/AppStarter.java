package com.burukeyou.demo.starter;

import com.burukeyou.demo.api.UserServiceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppStarter implements CommandLineRunner {

    @Autowired
    private UserServiceApi userServiceApi;

    @Override
    public void run(String... args) throws Exception {
//
//        new Thread(() -> {
//            while (true) {
//                log.info("架子啊xxxx {}", userServiceApi.getClass().getName());
//                BaseRsp<String> add = userServiceApi.add("11");
//                System.out.println(add);
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

    }
}
