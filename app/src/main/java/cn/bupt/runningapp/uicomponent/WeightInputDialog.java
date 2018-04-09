package cn.bupt.runningapp.uicomponent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import cn.bupt.runningapp.BodyDataActivity;
import cn.bupt.runningapp.R;
import cn.bupt.runningapp.alert.AlertMessage;
import cn.bupt.runningapp.appserver.asyn.AppServerAsynHandler;
import cn.bupt.runningapp.appserver.asyn.RunnerAsyn;
import cn.bupt.runningapp.appserver.model.PhysicalStatus;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sunli on 2017/7/5.
 */

public class WeightInputDialog extends BaseDialog {
    private ImageView img_close_input;
    private EditText text_input_data;
    private Button save_button;
    private BodyDataActivity bodydataActivity;

    public WeightInputDialog(Context context, BodyDataActivity bodydataActivity) {
        super(context, 800, 900);
        this.bodydataActivity = bodydataActivity;
    }


    @Override
    protected View getView() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_input_weight, null);
        //得到各种view
        text_input_data = (EditText) view.findViewById(R.id.text_input_record_data);
        ((Button) view.findViewById(R.id.btn_save_record_data)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weight = text_input_data.getText().toString();
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("weight", weight)
                        .build();
                Call<PhysicalStatus> postNewWeightCall = RunnerAsyn.RunnerService.postNewWeight(requestBody);
                RunnerAsyn.AppServerAsyn(postNewWeightCall, bodydataActivity, new AppServerAsynHandler() {
                            @Override
                            public void handler(Response response) throws Exception {
                                bodydataActivity.updateLineChart();
                            }
                        },
                        new AlertMessage("系统错误", "请重启app"),
                        new AlertMessage("网络错误", "请检查网络连接")
                );
                dismiss();
            }
        });
        ((ImageView) view.findViewById(R.id.img_close_input_record_dialog)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}
