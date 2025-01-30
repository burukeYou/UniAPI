package com.burukeyou.retry;

import com.burukeyou.api.RetryServiceAPI;
import com.burukeyou.demo.DemoApplication;
import com.burukeyou.demo.entity.BaseRsp;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RetryTest {

    @Autowired
    private RetryServiceAPI retryServiceAPI;

    @Test
    public void test01(){

        BaseRsp<String> a = retryServiceAPI.get1(UUID.randomUUID().toString());
        System.out.println();
    }

    @Test
    public void test02(){
        BaseRsp<String> a = retryServiceAPI.get2(UUID.randomUUID().toString());
        System.out.println(a);
    }

    @Test
    public void test03(){
        BaseRsp<String> a = retryServiceAPI.get3(UUID.randomUUID().toString());
        System.out.println(a);
    }

    // 异步重试
//    @Test
//    public void test04() throws InterruptedException {
//        CompletableFuture<BaseRsp<String>> a = retryServiceAPI.get4(UUID.randomUUID().toString(), Objects::isNull);
//
//        a.whenComplete((data,ex) -> {
//            System.out.println("异步重试结果: ");
//            if (ex != null){
//                ex.printStackTrace();
//                return;
//            }
//            System.out.println(data);
//        });
//
//        System.out.println("等待结果");
//        Thread.currentThread().join();
//    }

    @Test
    public void test040() throws InterruptedException {
        CompletableFuture<BaseRsp<String>> a = retryServiceAPI.get40(UUID.randomUUID().toString());
        a.whenComplete((data,ex) -> {
            System.out.println("异步重试结果: ");
            if (ex != null){
                ex.printStackTrace();
                return;
            }
            System.out.println(data);
        });

        System.out.println("等待结果");
        Thread.currentThread().join();
    }

    @Test
    public void test041() throws InterruptedException {
        CompletableFuture<BaseRsp<String>> a = retryServiceAPI.get41(UUID.randomUUID().toString());
        a.whenComplete((data,ex) -> {
            System.out.println("异步重试结果: ");
            if (ex != null){
                ex.printStackTrace();
                return;
            }
            System.out.println(data);
        });

        System.out.println("等待结果");
        Thread.currentThread().join();
    }

    @Test
    public void test042() throws InterruptedException {
        CompletableFuture<BaseRsp<String>> a = retryServiceAPI.get42(UUID.randomUUID().toString());
        a.whenComplete((data,ex) -> {
            System.out.println("异步重试结果: ");
            if (ex != null){
                ex.printStackTrace();
                return;
            }
            System.out.println(data);
        });

        System.out.println("等待结果");
        Thread.currentThread().join();
    }

   /* @Test
    public void test043() throws InterruptedException {
        CompletableFuture<BaseRsp<String>> a = retryServiceAPI.get43(UUID.randomUUID().toString(),result -> result.getCode() > 10);
        a.whenComplete((data,ex) -> {
            System.out.println("异步重试结果: ");
            if (ex != null){
                ex.printStackTrace();
                return;
            }
            System.out.println(data);
        });

        System.out.println("等待结果");
        Thread.currentThread().join();
    }

    @Test
    public void test044() throws InterruptedException {
        CompletableFuture<BaseRsp<String>> a = retryServiceAPI.get44(UUID.randomUUID().toString(), invocation -> invocation.getBodyResult().getCode() > 10) ;
        a.whenComplete((data,ex) -> {
            System.out.println("异步重试结果: ");
            if (ex != null){
                ex.printStackTrace();
                return;
            }
            System.out.println(data);
        });

        System.out.println("等待结果");
        Thread.currentThread().join();
    }


    @Test
    public void test045() throws InterruptedException {
        CompletableFuture<BaseRsp<String>> a = retryServiceAPI.get45(UUID.randomUUID().toString(), new HttpRetryInterceptorPolicy<BaseRsp<String>>() {
            @Override
            public boolean beforeExecute(UniHttpRequest uniHttpRequest, HttpRetryInvocation invocation) throws Exception {
                log.info("before curCount:{}",invocation.getCurExecuteCount());
                HttpFastRetry httpFastRetry = invocation.getHttpFastRetry();
                return HttpRetryInterceptorPolicy.super.beforeExecute(uniHttpRequest, invocation);
            }

            @Override
            public boolean afterExecuteFail(Exception exception, UniHttpRequest uniHttpRequest, HttpRetryInvocation invocation) throws Exception {
                log.info("after fail curCount:{}",invocation.getCurExecuteCount());
                return HttpRetryInterceptorPolicy.super.afterExecuteFail(exception, uniHttpRequest, invocation);
            }

            @Override
            public boolean afterExecuteSuccess(BaseRsp<String> bodyResult, UniHttpRequest uniHttpRequest, UniHttpResponse uniHttpResponse, HttpRetryInvocation invocation) {
                log.info("after success: {} curCount:{}", JSON.toJSONString(bodyResult),invocation.getCurExecuteCount());
                log.info("aaaa {}",uniHttpResponse.getBodyToString());
                return ((BaseRsp)bodyResult).getCode() > 10;
            }
        }) ;
        a.whenComplete((data,ex) -> {
            System.out.println("异步重试结果: ");
            if (ex != null){
                ex.printStackTrace();
                return;
            }
            System.out.println(data);
        });

        System.out.println("等待结果");
        Thread.currentThread().join();
    }*/


    @Test
    public void test051() throws InterruptedException {
        BaseRsp<String> a = retryServiceAPI.get4Sync(UUID.randomUUID().toString());
        System.out.println(a);
    }

    @Test
    public void test052() throws InterruptedException {
        BaseRsp<String> a = retryServiceAPI.get4SyncFast(UUID.randomUUID().toString());
        System.out.println(a);
    }




    /** 普通重试测试
     *
     * 线程池数量: new ThreadPoolExecutor(8, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));
     * 耗时： 600多秒。
     *
     *
     * 线程池数量: new ThreadPoolExecutor(0, Integer.MAX_VALUE,60L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>())
     * 普通重试测试总耗时  任务数:300 耗时:9.1610913
     *
     *  如果是有容量队列：  并发任务量 = 核心线程数数量。IO型任务需要额外调大核心线程数, 一旦并发量超过核心线程数，性能指数型下降，
     */
    @Test
    public  void test04Manny() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int taskSize = 50;
        List<CompletableFuture<BaseRsp<String>>> futures = new ArrayList<>();
        for (int i = 0; i < taskSize; i++) {
            CompletableFuture<BaseRsp<String>> future = retryServiceAPI.get4Many(i + "=="+UUID.randomUUID());
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        System.out.println("所有任务完成");
        for (CompletableFuture<BaseRsp<String>> future : futures) {
            log.info("轮询结束  result:{}",future.get());
        }

        stopWatch.stop();
        log.info("普通重试测试总耗时  任务数:{} 耗时:{}",taskSize,stopWatch.getTotalTimeSeconds());
    }

    /**
     * 线程池数量: 8
     * fast重试测试总耗时  任务数:300 耗时:8.6568239
     *
     * 与线程池数量无关， 无论多大并发任务量都能处理
     */
    @Test
    public  void test04MannyFast() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int taskSize = 50;
        List<CompletableFuture<BaseRsp<String>>> futures = new ArrayList<>();
        for (int i = 0; i < taskSize; i++) {
            CompletableFuture<BaseRsp<String>> future = retryServiceAPI.get4ManyFast(i + "=="+UUID.randomUUID());
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        System.out.println("所有任务完成");
        for (CompletableFuture<BaseRsp<String>> future : futures) {
            log.info("轮询结束  result:{}",future.get());
        }

        stopWatch.stop();
        log.info("fast重试测试总耗时  任务数:{} 耗时:{}",taskSize,stopWatch.getTotalTimeSeconds());
    }
}
