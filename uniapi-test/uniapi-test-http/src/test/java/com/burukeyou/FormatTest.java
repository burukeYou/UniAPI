package com.burukeyou;

import com.burukeyou.demo.DemoApplication;
import com.burukeyou.demo.api.FormatServiceApi;
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
    public void test() {
        UserXmlDTO api02 = api.get02();
        System.out.println(api02);
    }

}
