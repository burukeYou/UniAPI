package com.burukeyou;

import com.burukeyou.demo.DemoApplication;
import com.burukeyou.demo.api.FormatServiceApi;
import com.burukeyou.demo.entity.json.StuDTO;
import com.burukeyou.demo.entity.json.StuReq;
import com.burukeyou.demo.entity.xml.UserXmlDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Slf4j
@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class FormatTest {

    @Autowired
    private FormatServiceApi api;

    @Autowired
    private Environment environment;

    @Test
    public void test02() {
        String property = environment.getProperty("bbbq.name");
        UserXmlDTO api02 = api.get02();
        System.out.println(api02);
    }

    @Test
    public void test03() {
        api.get03(new UserXmlDTO("zhangsan",99));
    }

    @Test
    public void test04() {
        StuDTO api04 = api.get04();
        System.out.println();
    }

    @Test
    public void test05() {
        StuDTO api04 = api.get05();
        System.out.println();
    }

    @Test
    public void test06() {
        StuReq req = new StuReq();
        req.setId("123789");
        req.setName("张三");
        req.setSonId(3);
        req.setCount("count99");
        req.setArr1(new int[]{4,5,6});
        req.setDetail(new StuReq.Detail("a",4));
        req.setBbq(new StuReq.BBQ("b",887));
        api.get06(req);
        System.out.println();
    }
}
