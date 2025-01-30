package com.burukeyou.uniapi.http.support;

import com.burukeyou.uniapi.http.core.response.HttpResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

/**
 * Http Future
 * @author  caizhihao
 * @param <T>
 */
public class HttpFuture<T> extends CompletableFuture<T>  {

    private  CompletableFuture<Object> asyncFuture;

    private HttpResponse<T> response;

    public HttpFuture() {
    }

    public HttpFuture(T value, HttpResponse<T> response){
        this.response = response;
        this.complete(value);
    }


    /**
     * Blocking to get body result
     * @return          the object after the request body is desequenced
     */
    @Override
    public T get() {
        try {
            return super.get();
        } catch (Throwable e) {
            throw exceptionUnPack(e);
        }
    }


    /**
     * Timeout Blocking to get body result
     * @param timeout                the maximum time to wait
     * @param unit                   the time unit of the timeout argument
     * @return                       the object after the request body is desequenced
     * @throws TimeoutException
     */
    @Override
    public T get(long timeout, TimeUnit unit) throws TimeoutException {
        try {
            return super.get(timeout,unit);
        } catch (TimeoutException timeoutException){
            throw timeoutException;
        }catch (Throwable e) {
           throw exceptionUnPack(e);
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (this.asyncFuture != null) {
            this.asyncFuture.cancel(mayInterruptIfRunning);
        }
        return super.cancel(mayInterruptIfRunning);
    }


    /**
     * When the request ends, if the exception message of the successful callback is null, HttpResponse is not null
     * @param action
     * @return
     */
    public HttpFuture<T> whenCompleteResponse(BiConsumer<? super HttpResponse<T>, ? super Throwable> action) {
        super.whenComplete((info,ex) -> action.accept(response,ex));
        return this;
    }

    // ==============================================

    /**
     * Blocking to get HTTP response results
     * @return              HTTP response
     */
    public HttpResponse<T> getHttpResponse(){
        if (!isDone()){
            get();
        }
        return response;
    }

    /**
     * Timeout blocking to get HTTP response results
     * @param timeout                the maximum time to wait
     * @param unit                   the time unit of the timeout argument
     * @return
     * @throws TimeoutException
     */
    public HttpResponse<T> getHttpResponse(long timeout, TimeUnit unit) throws TimeoutException {
        if (!isDone()){
            get(timeout,unit);
        }
        return response;
    }

    private RuntimeException exceptionUnPack(Throwable e){
        if (RuntimeException.class.isAssignableFrom(e.getClass())) {
            throw (RuntimeException) e;
        }else if (e instanceof ExecutionException){
            Throwable cause = e.getCause();
            if (cause == null){
                throw new RuntimeException(e);
            }
            // 异常打印，先最外层再到最内层
            RuntimeException runtimeException = null;
            if (RuntimeException.class.isAssignableFrom(cause.getClass())){
                runtimeException = (RuntimeException) cause;
                runtimeException.setStackTrace(combineStackTrace(e.getStackTrace(),runtimeException.getStackTrace()));
            }else {
                runtimeException = new RuntimeException(cause);
                runtimeException.setStackTrace(e.getStackTrace());
            }
            throw runtimeException;
        }else {
            throw new RuntimeException(e);
        }
    }

    private StackTraceElement[] combineStackTrace(StackTraceElement[] a, StackTraceElement[] b) {
        if (a == null){
            a = new StackTraceElement[0];
        }
        if (b == null){
            b = new StackTraceElement[0];
        }
        StackTraceElement[] stackTraceElements = new StackTraceElement[a.length + b.length];
        System.arraycopy(a, 0, stackTraceElements, 0, a.length);
        System.arraycopy(b, 0, stackTraceElements, a.length, b.length);
        return stackTraceElements;
    }

    public void setResponse(HttpResponse<T> response) {
        this.response = response;
    }
}
