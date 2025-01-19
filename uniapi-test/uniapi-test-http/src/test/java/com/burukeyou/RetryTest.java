package com.burukeyou;

import com.burukeyou.demo.DemoApplication;
import com.burukeyou.demo.api.RetryServiceAPI;
import com.burukeyou.demo.entity.BaseRsp;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

@Slf4j
@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RetryTest {

    @Autowired
    private RetryServiceAPI retryServiceAPI;

    @Test
    public void test01(){
        BaseRsp<String> a = retryServiceAPI.rto1(UUID.randomUUID().toString());
        System.out.println();
    }

    @Test
    public void test02(){
        BaseRsp<String> a = retryServiceAPI.rto2(UUID.randomUUID().toString());
        System.out.println(a);
    }

    @Test
    public void test03(){
        BaseRsp<String> a = retryServiceAPI.rto3(UUID.randomUUID().toString());
        System.out.println(a);
    }
}
