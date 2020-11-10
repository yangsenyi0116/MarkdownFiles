# OkHttp的Callback回调封装

```java
package com.maibangbangbusiness.app.http.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.maibangbangbusiness.app.http.ContentBody;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
/**
 * @Author: Malen(qzmmdsl@sina.cn).
 * @Detail: Call
 * @Motto: Never Give Up ！！！
 */

public abstract class Call implements Callback {

    private Handler handler = null;

    private Looper looper = null;

    protected static final int SUCCESS_MESSAGE = 0;

    protected static final int FAILURE_MESSAGE = 1;

    public abstract void onFinish();

    public abstract void onFailure(Request request, IOException e);

    public abstract void onResponse(Response response) throws IOException;

    public Call() {
        this(null);
    }
    
    public Call(Looper looper) {
        this.looper = looper == null ? Looper.myLooper() : looper;
        handler = new ResponderHandler(this, looper);
    }

    @Override
    public void onResponse(okhttp3.Call call, Response response) throws IOException {
        if (!Thread.currentThread().isInterrupted()) {
            if (call.request().body() instanceof ContentBody) {//重置contentbody 的成员变量
                ContentBody contentBody = (ContentBody) call.request().body();
                contentBody.contents = null;
            }
            sendSuccessMessage(call, response);
        }
    }

    @Override
    public void onFailure(okhttp3.Call call, IOException e) {
        if (!Thread.currentThread().isInterrupted()) {
            if (call.request().body() instanceof ContentBody) {//重置contentbody 的成员变量
                ContentBody contentBody = (ContentBody) call.request().body();
                contentBody.contents = null;
            }
            sendFailureMessage(call, e);
        }
    }

    final public void sendSuccessMessage(okhttp3.Call call, Response response) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{call, response}));
    }

    final public void sendFailureMessage(okhttp3.Call call, Throwable throwable) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{call, throwable}));
    }

    protected void handleMessage(Message msg) {
        Object[] response;
        try {
            switch (msg.what) {
                case SUCCESS_MESSAGE:
                    response = (Object[]) msg.obj;
                    if (response != null && response.length >= 2) {
                        onResponse((Response) response[1]);
                        onFinish();
                    }
                    break;
                case FAILURE_MESSAGE:
                    response = (Object[]) msg.obj;
                    if (response != null && response.length >= 2) {
                        onFailure((Request) response[0], (IOException) response[1]);
                        onFinish();
                    }
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected void sendMessage(Message msg) {
        if (handler == null) {
            handleMessage(msg);
        } else if (!Thread.currentThread().isInterrupted()) { // do not send messages if request has been cancelled
            handler.sendMessage(msg);
        }
    }

    protected Message obtainMessage(int responseMessageId, Object responseMessageData) {
        return Message.obtain(handler, responseMessageId, responseMessageData);
    }

    private static class ResponderHandler extends Handler {
        private final Call mResponder;

        ResponderHandler(Call mResponder, Looper looper) {
            super(looper);
            this.mResponder = mResponder;
        }

        @Override
        public void handleMessage(Message msg) {
            mResponder.handleMessage(msg);
        }
    }
}/**
 * @Author: Malen(qzmmdsl@sina.cn).
 * @Detail: JsonCall
 * @Motto: Never Give Up ！！！
 */

public abstract class JsonResponseCall extends Call {

    public abstract void onSuccess(T body);

    @Override
    public void onFinish() {
        try {
            if (L.isDebug()) {
                L.d("请求结束");
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
    }

    @Override
    public void onResponse(Response response) throws IOException {
        BufferedReader reader = null;
        try {
            if (response.isSuccessful()) {
                Class<?> c = getClass();
                Type type = c.getGenericSuperclass();
                type = ((ParameterizedType) type).getActualTypeArguments()[0];
                reader = new BufferedReader(
                        new InputStreamReader(
                                response.body().byteStream(), "UTF-8"));
                T body = AppUtils.fromJson(reader.readLine(), type);
                this.onSuccess(body);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                reader.close();
                reader = null;
            }
        }
    }
}
```

