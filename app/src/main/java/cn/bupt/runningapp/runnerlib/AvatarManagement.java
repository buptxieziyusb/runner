package cn.bupt.runningapp.runnerlib;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

import cn.bupt.runningapp.alert.AlertMessage;
import cn.bupt.runningapp.alert.AlertableAppCompatActivity;
import cn.bupt.runningapp.appserver.asyn.AppServerAsynHandler;
import cn.bupt.runningapp.appserver.asyn.RunnerAsyn;
import cn.bupt.runningapp.appserver.service.ServiceGenerator;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by yisic on 2017/7/4.
 */

public class AvatarManagement {
    private static void setAvatarToServerStorage(File file, String appToken, AlertableAppCompatActivity activity) {
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("avatar", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                .build();
        Call<ResponseBody> uploadAvatarCall = RunnerAsyn.RunnerService.uploadAvatar(requestBody);
        RunnerAsyn.AppServerAsyn(uploadAvatarCall, activity, new AppServerAsynHandler() {
                    @Override
                    public void handler(Response response) throws Exception {
                        //success, do nothing
                    }
                },
                new AlertMessage("设置头像出错", "系统错误"),
                new AlertMessage("设置头像出错", "网络错误")
        );
    }

    public static void printAvatarByURL(String serverPath, String token, AlertableAppCompatActivity activity, SimpleDraweeView avatarView) {
        avatarView.setImageURI(ServiceGenerator.API_BASE_URL.substring(0, ServiceGenerator.API_BASE_URL.length() - 5) + serverPath);
    }

    public static void setAvatar(File avatarFile, String token, AlertableAppCompatActivity activity) {
        setAvatarToServerStorage(avatarFile, token, activity);
    }

}
