package com.example.pos1.newprojectview.editMerchats;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pos1.R;
import com.example.pos1.adapter.NewMerchantsGridViewAdapter;
import com.example.pos1.base.BaseActivity;
import com.example.pos1.net.HttpRequest;
import com.example.pos1.net.OkHttpException;
import com.example.pos1.net.RequestParams;
import com.example.pos1.net.ResponseCallback;
import com.example.pos1.newprojectview.bean.NewRateBean;
import com.example.pos1.views.MyDialog;
import com.example.pos1.views.MyGridView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: qgl
 * 创建日期：2023/3/11
 * 描述:修改费率
 */
public class EditRateActivity extends BaseActivity implements View.OnClickListener {
    private TextView feilv_tv;
    private LinearLayout iv_back;
    private String merchantCode;
    private List<NewRateBean> rateBeans = new ArrayList<>();
    private String rateId;
    private Button submit_bt;
    //类型适配器Adapter
    private NewMerchantsGridViewAdapter madapter;
    @Override
    protected int getLayoutId() {
        statusBarConfig(R.color.new_theme_color, false).init();
        return R.layout.edit_rate_activity;
    }

    @Override
    protected void initView() {
        feilv_tv = findViewById(R.id.feilv_tv);
        iv_back = findViewById(R.id.iv_back);
        submit_bt = findViewById(R.id.submit_bt);
        posRate();
    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        feilv_tv.setOnClickListener(this);
        submit_bt.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        merchantCode = getIntent().getStringExtra("id");
        posMerchantInfo(merchantCode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.feilv_tv:
                showDialog(rateBeans);
                break;
            case R.id.submit_bt:
                if (TextUtils.isEmpty(rateId)) {
                    showToast(3, "请选择费率！");
                    return;
                }
                posData();
                break;
        }
    }

    //获取费率
    private void posRate() {
        RequestParams params = new RequestParams();
        HttpRequest.posEchoFeeId(params, getToken(),new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    rateBeans = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<NewRateBean>>() {
                            }.getType());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }

    /**
     * 获取详情信息
     * @param merchantCode
     */
    private void posMerchantInfo(String merchantCode){
        RequestParams params = new RequestParams();
        params.put("merchantCode", merchantCode);
        HttpRequest.posMerchantInfo(params, getToken(), new ResponseCallback() {
            //成功回调
            @Override
            public void onSuccess(Object responseObj) {
                try {
                    JSONObject result = new JSONObject(responseObj.toString()).getJSONObject("data");
                    setValue(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //失败回调
            @Override
            public void onFailure(OkHttpException failuer) {
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }

    /**
     * 回显数据
     * @param result
     */
    private void setValue(JSONObject result) {
        try {
            rateId = result.getString("feeId");
            feilv_tv.setText(result.getString("feeValue"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     * 选择类型
     */
    public void showDialog(List<NewRateBean> mList) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_quote_type_dialog, null);
        Button data_bill_dialog_btn = view.findViewById(R.id.data_bill_dialog_btn);
        MyGridView data_bill_dialog_grid = view.findViewById(R.id.data_bill_dialog_grid);
        madapter = new NewMerchantsGridViewAdapter(EditRateActivity.this, mList);
        data_bill_dialog_grid.setAdapter(madapter);
        data_bill_dialog_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //把点击的position传递到adapter里面去
                madapter.changeState(i);
                feilv_tv.setText(mList.get(i).getFeeValue());
                rateId = mList.get(i).getFeeId();
            }
        });
        Dialog dialog = new MyDialog(EditRateActivity.this, true, true, (float) 1).setNewView(view);
        dialog.show();
        data_bill_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    //发送修改数据
    private void posData() {
        loadDialog.show();
        RequestParams params = new RequestParams();
        //商户号
        params.put("merchantCode", merchantCode);
        //费率ID
        params.put("feeId",rateId);
        HttpRequest.posUpdateMerchantFee(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                if (loadDialog.isShowing()){
                    loadDialog.dismiss();
                }
                showToast(3,"修改成功，等待审核！");
                finish();
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                loadDialog.dismiss();
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }
}