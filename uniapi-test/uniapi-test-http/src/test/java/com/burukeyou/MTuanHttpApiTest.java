package com.burukeyou;

import com.burukeyou.demo.DemoApplication;
import com.burukeyou.demo.api.WeatherServiceApi;
import com.burukeyou.demo.entity.BaseRsp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MTuanHttpApiTest {

    @Autowired
    private WeatherServiceApi weatherServiceApi;

    @Test
    public void test1(){
        BaseRsp<String> rsp = weatherServiceApi.getCityWeather("beiJing");
        System.out.println(rsp);
    }
}
