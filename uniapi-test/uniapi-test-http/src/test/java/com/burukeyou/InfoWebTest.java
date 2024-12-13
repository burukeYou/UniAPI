package com.burukeyou;

import com.alibaba.fastjson.JSON;
import com.burukeyou.demo.DemoApplication;
import com.burukeyou.demo.api.SSLServiceApi;
import com.burukeyou.demo.entity.BaseRsp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class InfoWebTest {

    @Autowired
    private SSLServiceApi infoServiceApi;

    @Test
    public void test() {
        BaseRsp<String> aaa = infoServiceApi.get01("aaa");
        System.out.println(JSON.toJSONString(aaa));
    }

}
