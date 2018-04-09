package cn.bupt.runningapp.appserver.asyn;

import cn.bupt.runningapp.alert.AlertMessage;
import cn.bupt.runningapp.alert.AlertableAppCompatActivity;
import cn.bupt.runningapp.appserver.service.LoginService;
import cn.bupt.runningapp.appserver.service.RunnerService;
import cn.bupt.runningapp.appserver.service.ServiceGenerator;
import cn.bupt.runningapp.struct.ServerSessionInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yisic on 2017/7/8.
 */

public class RunnerAsyn {
    public static RunnerService RunnerService = null;
    public static LoginService LoginService = null;

    public static void initLoginService() {
        LoginService = ServiceGenerator.createService(LoginService.class);
    }

    public static void initRunnerService(String appToken) {
        RunnerService = ServiceGenerator.createService(RunnerService.class, "Token " + appToken);
    }

    public static <S> void AppServerAsyn(Call<S> call, final AlertableAppCompatActivity alertHandler, final AppServerAsynHandler asynHandler, final AlertMessage responseErrMsg, final AlertMessage failMsg) {
        call.enqueue(new Callback<S>() {
            @Override
            public void onResponse(Call<S> call, Response<S> response) {
                try {
                    asynHandler.handler(response);
                } catch (Exception e) {
                    alertHandler.alert(responseErrMsg);
                }
            }

            @Override
            public void onFailure(Call<S> call, Throwable t) {
                alertHandler.alert(failMsg);
            }
        });
    }

}
