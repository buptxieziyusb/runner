package cn.bupt.runningapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;

import cn.bupt.runningapp.runnerlib.AvatarManagement;
import cn.bupt.runningapp.alert.AlertMessage;
import cn.bupt.runningapp.alert.AlertableAppCompatActivity;
import cn.bupt.runningapp.appserver.asyn.AppServerAsynHandler;
import cn.bupt.runningapp.appserver.asyn.RunnerAsyn;
import cn.bupt.runningapp.appserver.model.UserDetail;
import cn.bupt.runningapp.struct.ServerSessionInfo;
import retrofit2.Call;
import retrofit2.Response;

public class UserInfoActivity extends AlertableAppCompatActivity {
    private UserInfoActivity self = this;
    private static final int GET_PIC_FROM_PHOTOS = 1;
    private static final int CROP_PHOTO = 2;
    private SimpleDraweeView avatarView;
    private Bitmap avatar;

    private OnTabSelectListener onTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelected(@IdRes int tabId) {
            if (tabId == R.id.tab_run) {
                startActivity(new Intent(self, StatisticActivity.class));
                finish();
                overridePendingTransition(0, 0);
            } else if (tabId == R.id.tab_history) {
                startActivity(new Intent(self, HistoryListActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_train_body:
                    startActivity(new Intent(self, BodyDataActivity.class));
                    break;
                case R.id.layout_gift:
                    startActivity(new Intent(self, GiftListActivity.class));
                    break;
                case R.id.avatar_view:
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, GET_PIC_FROM_PHOTOS);
                    break;
                case R.id.logout_button:
                    startActivity(new Intent(self, LoginActivity.class));
                    ServerSessionInfo.clean(getApplication());
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        avatarView = (SimpleDraweeView) findViewById(R.id.avatar_view);
        ((LinearLayout) findViewById(R.id.layout_train_body)).setOnClickListener(onClickListener);
        ((LinearLayout) findViewById(R.id.layout_gift)).setOnClickListener(onClickListener);
        ((Button) findViewById(R.id.logout_button)).setOnClickListener(onClickListener);
        avatarView.setOnClickListener(onClickListener);
        ((BottomBar) findViewById(R.id.bottomBar)).setDefaultTab(R.id.tab_person);
        ((BottomBar) findViewById(R.id.bottomBar)).setOnTabSelectListener(onTabSelectListener);

        updateUserInfo();
    }

    private void updateUserInfo() {
        Call<UserDetail> myInfoCall = RunnerAsyn.RunnerService.myInfo();
        RunnerAsyn.AppServerAsyn(myInfoCall, self, new AppServerAsynHandler<UserDetail>() {
                    @Override
                    public void handler(Response<UserDetail> response) throws Exception {
                        UserDetail userDetail = response.body();
                        ((TextView) findViewById(R.id.user_name_txt)).setText(userDetail.getName());
                        AvatarManagement.printAvatarByURL((String) userDetail.getAvatar(), ServerSessionInfo.AppToken, self, (SimpleDraweeView) findViewById(R.id.avatar_view));
                    }
                },
                new AlertMessage("系统错误", "请重启app"),
                new AlertMessage("网络错误", "请检查网络连接")
        );
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GET_PIC_FROM_PHOTOS:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());//裁剪图片
                }
                break;
            case CROP_PHOTO:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    avatar = extras.getParcelable("data");
                    try {
                        avatar = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/temp.jpg");
                    } catch (Exception e) {
                        avatar = null;
                    }
                    if (avatar != null) {
                        AvatarManagement.setAvatar(new File(Environment.getExternalStorageDirectory().getPath() + "/temp.jpg"), ServerSessionInfo.AppToken, this);
                        avatarView.setImageURI("file:///" + Environment.getExternalStorageDirectory().getPath() + "/temp.jpg");
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// aspectX aspectY 是宽高的比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 323);// outputX outputY 是裁剪图片宽高
        intent.putExtra("outputY", 323);
        Uri uritempFile = Uri.parse("file:///" + Environment.getExternalStorageDirectory().getPath() + "/temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_PHOTO);
    }
}
