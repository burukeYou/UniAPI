package com.burukeyou;

import com.alibaba.fastjson2.JSON;
import com.burukeyou.demo.DemoApplication;
import com.burukeyou.demo.api.SSLServiceApi;
import com.burukeyou.demo.entity.BaseRsp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;

@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SslWebTest {

    @Autowired
    private SSLServiceApi infoServiceApi;

    @Test
    public void test() {
        BaseRsp<String> aaa = infoServiceApi.get01("aaa");
        System.out.println(JSON.toJSONString(aaa));
    }

    public static void main(String[] args) throws Exception {
        String file = "classpath:ssl/server.crt";
        String file2 = "classpath:ssl/server01.p12";
        String base64 = getBase64(file2);
        System.out.println(base64);
    }

    private static String getBase64(String file) throws IOException {
        URL url = ResourceUtils.getURL(file);
        byte[] bytes = FileCopyUtils.copyToByteArray(url.openStream());
        return Base64.getEncoder().encodeToString(bytes);
    }

}
