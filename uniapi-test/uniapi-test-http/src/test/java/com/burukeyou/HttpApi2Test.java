package com.burukeyou;

import com.burukeyou.demo.DemoApplication;
import com.burukeyou.demo.api.UserService2Api;
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
    private UserService2Api userApi;

    @Test
    public void test() {
        BaseRsp<String> add = userApi.del06("aa");
        System.out.println();
    }

}
