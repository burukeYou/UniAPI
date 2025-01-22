package com.burukeyou.uniapi.http.core.retry;

import java.util.concurrent.Callable;

import com.burukeyou.uniapi.http.core.request.UniHttpRequest;
import com.burukeyou.uniapi.http.support.UniHttpResponseParseInfo;

public interface RetryExecutor {


    Object execute(UniHttpRequest requestMetadata, Callable<UniHttpResponseParseInfo> callable) throws Throwable;


}
