package com.bupt.run.runningapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bupt.run.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import com.bupt.run.runningapp.alert.AlertMessage;
import com.bupt.run.runningapp.alert.AlertableAppCompatActivity;
import com.bupt.run.runningapp.appserver.asyn.AppServerAsynHandler;
import com.bupt.run.runningapp.appserver.asyn.RunnerAsyn;
import com.bupt.run.runningapp.appserver.model.UserDetailPlusAccessToken;
import com.bupt.run.runningapp.appserver.service.LoginService;
import com.bupt.run.runningapp.appserver.service.ServiceGenerator;
import com.bupt.run.runningapp.social.weibo.WeiboConstants;
import com.bupt.run.runningapp.struct.ServerSessionInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AlertableAppCompatActivity {
    LoginActivity self = this;
    SsoHandler ssoHandler;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.activity_login_weibo_login_button:
                    weiboLogin();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //login service init
        RunnerAsyn.initLoginService();

        //weibo sdk init
        WbSdk.install(self, new AuthInfo(self, WeiboConstants.APP_KEY, WeiboConstants.REDIRECT_URL, WeiboConstants.SCOPE));
        ssoHandler = new SsoHandler(self);

        //fresco init
        Fresco.initialize(getApplication());

        //login activity init
        ((Button) findViewById(R.id.activity_login_weibo_login_button)).setOnClickListener(onClickListener);

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg);
        drawable.setColorFilter(Color.GRAY,PorterDuff.Mode.MULTIPLY);
        ((LinearLayout) findViewById(R.id.login_bg_layout)).setBackground(drawable);
//        ((LinearLayout) findViewById(R.id.login_bg_layout)).setBackgroundDrawable(drawable);

        if (ServerSessionInfo.haveSavedLoginInfo(getApplication())) {
            ServerSessionInfo.load(getApplication());
            (findViewById(R.id.activity_login_weibo_login_button)).setVisibility(View.INVISIBLE);
            RunnerAsyn.initRunnerService(ServerSessionInfo.AppToken);
            startActivity(new Intent(LoginActivity.this, StatisticActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            default:
                if (ssoHandler != null) {
                    ssoHandler.authorizeCallBack(requestCode, resultCode, data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void weiboLogin() {
        ssoHandler.authorize(new WbAuthListener() {
            @Override
            public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
                appServerLogin(oauth2AccessToken.getUid(), oauth2AccessToken.getToken(), "weibo", oauth2AccessToken.getRefreshToken());
            }

            @Override
            public void cancel() {
                alert(new AlertMessage("weibo login was canceled", "please try again"));
            }

            @Override
            public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
                alert(new AlertMessage("weibo login fail", "please try again"));
            }
        });
    }

    private void appServerLogin(final String uid, final String socialAccessToken, final String socialType, final String socialRefreshToken) {
        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        Call<UserDetailPlusAccessToken> appLoginCall = loginService.socialLogin(uid, socialAccessToken, socialType, socialRefreshToken);

        RunnerAsyn.AppServerAsyn(appLoginCall, self,
                new AppServerAsynHandler<UserDetailPlusAccessToken>() {
                    @Override
                    public void handler(Response<UserDetailPlusAccessToken> response) throws Exception {
                        ServerSessionInfo.init(
                                uid,
                                socialAccessToken,
                                socialRefreshToken,
                                socialType,
                                response.body().getAccessToken(),
                                getApplication()
                        );
                        RunnerAsyn.initRunnerService(response.body().getAccessToken());
                        startActivity(new Intent(self, StatisticActivity.class));
                        finish();
                    }
                }
                , new AlertMessage("登录错误", "请输入正确的账号密码")
                , new AlertMessage("网络错误", "请检查网络连接")
        );
    }
}
