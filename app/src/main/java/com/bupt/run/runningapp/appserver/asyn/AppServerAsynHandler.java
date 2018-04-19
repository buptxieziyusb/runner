package com.bupt.run.runningapp.appserver.asyn;

import retrofit2.Response;

/**
 * Created by yisic on 2017/7/8.
 */

public abstract class AppServerAsynHandler<S> {
    public abstract void handler(Response<S> response) throws Exception;
}
