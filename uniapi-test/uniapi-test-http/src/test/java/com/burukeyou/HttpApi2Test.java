package com.burukeyou;

import com.burukeyou.demo.DemoApplication;
import com.burukeyou.demo.api.UserService2Api;
import com.burukeyou.demo.api.UserServiceApi;
import com.burukeyou.demo.entity.BaseRsp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class HttpApi2Test {

    @Autowired
    private UserServiceApi userApi;

    @Autowired
    private UserService2Api userApi2;

    @Test
    public void test() {
        BaseRsp<String> add = userApi2.del06("aa");
        System.out.println();
    }

    @Test
    public void testdel01() throws InterruptedException {
        userApi2.del07().whenComplete((rsp, throwable) -> {
            if (throwable != null){
                throwable.printStackTrace();
            }
            System.out.println("222");
            System.out.println(rsp);
        });
        System.out.println("1");
        Thread.sleep(3000000);
    }
}
