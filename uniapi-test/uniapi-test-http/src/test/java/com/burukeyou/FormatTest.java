package com.burukeyou;

import com.burukeyou.demo.DemoApplication;
import com.burukeyou.demo.api.FormatServiceApi;
import com.burukeyou.demo.entity.json.StuDTO;
import com.burukeyou.demo.entity.xml.UserXmlDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Slf4j
@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class FormatTest {

    @Autowired
    private FormatServiceApi api;

    @Test
    public void test02() {
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
}
