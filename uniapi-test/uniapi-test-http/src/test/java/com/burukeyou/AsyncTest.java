package com.burukeyou;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.burukeyou.demo.DemoApplication;
import com.burukeyou.demo.api.UserAsyncServiceApi;
import com.burukeyou.demo.api.UserServiceApi;
import com.burukeyou.demo.entity.BaseRsp;
import com.burukeyou.uniapi.http.core.response.HttpResponse;
import com.burukeyou.uniapi.http.support.HttpFuture;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Slf4j
@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class AsyncTest {

    @Autowired
    private UserServiceApi userApi;

    @Autowired
    private UserAsyncServiceApi userApi2;

    @Test
    public void test() {
        BaseRsp<String> add = userApi2.del06("aa");
        System.out.println();
    }

    @Test
    public void testdel04() throws InterruptedException, ExecutionException {
        //String s = userApi2.del041();
        String stringHttpResponse = userApi2.del04().get();
        System.out.println();
    }

    @Test
    public void testdel07() throws InterruptedException {
        userApi2.del07("s999").whenComplete((rsp, throwable) -> {
            if (throwable != null){
                throwable.printStackTrace();
            }
            System.out.println("222");
            System.out.println(rsp);
        });
        System.out.println("1");
        Thread.sleep(3000000);
    }

    @Test
    public void test042() throws ExecutionException, InterruptedException {
        CompletableFuture<String> aa = userApi2.del042();
        String s = aa.get();
        System.out.println();
    }

    @Test
    public void test043() throws ExecutionException, InterruptedException {
        Future<String> aa = userApi2.del043();
        String s = aa.get();
        System.out.println();
    }

    @Test
    public void test044() throws InterruptedException {
        userApi2.del044();
        System.out.println("11111111111");
        Thread.currentThread().join();
    }

    @Test
    public void test045() throws InterruptedException {
        HttpFuture<BaseRsp<String>> future = userApi2.del045();

        future.whenComplete((rsp, ex) -> {
            System.out.println("333333333333333");
            if (ex != null) {
                ex.printStackTrace();
            }
            System.out.println("22222====str==>" + rsp);
        });

//        future.whenCompleteResponse((rsp, ex) -> {
//            System.out.println("333333333333333");
//            if (ex != null) {
//                ex.printStackTrace();
//            }
//
//            String bodyToString = rsp.getBodyToString();
//            BaseRsp<String> bodyResult = rsp.getBodyResult();
//            System.out.println("22222====str==>" + bodyToString);
//            System.out.println("2222===result===> " + bodyResult);
//        });

        //HttpResponse<BaseRsp<String>> httpResponse = future.getHttpResponse();

        //BaseRsp<String> stringBaseRsp = future.get();
        //System.out.println(stringBaseRsp);

        System.out.println("11111111111");
        Thread.currentThread().join();
    }

    @Test
    public void test046() throws ExecutionException, InterruptedException {
        CompletableFuture<HttpResponse<BaseRsp<String>>> future = userApi2.del046();

        future.whenComplete((rsp, ex) -> {
            if (ex != null){
                ex.printStackTrace();
            }
            try {
                System.out.println("2222222");
                String bodyToString = rsp.getBodyToString();
                BaseRsp<String> bodyResult = rsp.getBodyResult();
                System.out.println("33333333333 => " + bodyToString);
                System.out.println("44444444444 => " + bodyResult);
            } catch (Exception e) {
              e.printStackTrace();
            }
        });

        System.out.println("111111111111");
        Thread.currentThread().join();
    }

    @Test
    public void test047() throws ExecutionException, InterruptedException {
        HttpFuture<HttpResponse<BaseRsp<String>>> future = userApi2.del047();

        future.whenComplete((rsp, ex) -> {
            System.out.println("2222222");
            String bodyToString = rsp.getBodyToString();
            BaseRsp<String> bodyResult = rsp.getBodyResult();
            System.out.println("33333333333 => " + bodyToString);
            System.out.println("44444444444 => " + bodyResult);
        });


        System.out.println("111111111111");
        Thread.currentThread().join();
    }

    @Test
    public void test071() throws InterruptedException {
        HttpFuture<BaseRsp<String>> future = userApi2.del071("牛");

        future.whenCompleteResponse((rsp, ex) -> {
            System.out.println("333333333333333");
            if (ex != null) {
                log.error("处理异常: ",ex);
               return;
            }

            String bodyToString = rsp.getBodyToString();
            BaseRsp<String> bodyResult = rsp.getBodyResult();
            System.out.println("22222====str==>" + bodyToString);
            System.out.println("2222===result===> " + bodyResult);
        });

        //HttpResponse<BaseRsp<String>> httpResponse = future.getHttpResponse();

        //BaseRsp<String> stringBaseRsp = future.get();
        //System.out.println(stringBaseRsp);

        System.out.println("11111111111");
        Thread.currentThread().join();
    }

}
