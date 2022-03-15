package com.example.pos1.homefragment.homeInvitepartners;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.pos1.R;
import com.example.pos1.base.BaseActivity;
import com.example.pos1.datafragment.databillbean.BillBean;
import com.example.pos1.homefragment.homequoteactivity.bean.MerchTypeBean1;
import com.example.pos1.net.HttpRequest;
import com.example.pos1.net.OkHttpException;
import com.example.pos1.net.RequestParams;
import com.example.pos1.net.ResponseCallback;
import com.example.pos1.views.SwitchButtonView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: qgl
 * 创建日期：2021/8/27
 * 描述:填写生成二维码
 */
public class HomeFillActivity extends BaseActivity implements View.OnClickListener {
    //信用卡结算
    private RelativeLayout js_flt1_relative;
    private TextView js_flt1_tv;
    private String type1 = "";
    //扫码结算
    private RelativeLayout js_flt0_relative;
    private TextView js_flt0_tv;
    private String type2 = "";
    //提交按钮
    private TextView submit_btn;
    //套餐ID
    private String accountId = "";
    //选择商户类型弹出控件
    private OptionsPickerView reasonPicker1;
    private OptionsPickerView reasonPicker2;
    //界面状态、1 新增 2 修改
    private String Status = "1";
    //返回键
    private LinearLayout iv_back;

    private TextView serverThreeSixTv;
    private TextView serverFourEightTv;
    private TextView serverNineNineTv;
    private TextView serverTwoNineNineTv;
    private TextView flowThreeSixTv;
    private TextView flowNineNineTv;
    private TextView flowFourEightTv;

    private EditText serverThreeSixEdit;
    private EditText serverFourEightEdit;
    private EditText serverNineNineEdit;
    private EditText serverTwoNineNineEdit;
    private EditText flowThreeSixEdit;
    private EditText flowNineNineEdit;
    private EditText flowFourEightEdit;
//    private SwitchButtonView mBtnSwitch;

    private int num0 = 0;
    private int num1 = 0;
    private int num2 = 0;
    private int num3 = 0;
    private int num4 = 0;
    private int num5 = 0;
    private int num6 = 0;
    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color, false).init();
        return R.layout.homefill_activity;
    }

    @Override
    protected void initView() {
        iv_back = findViewById(R.id.iv_back);
        js_flt1_relative = findViewById(R.id.js_flt1_relative);
        js_flt1_tv = findViewById(R.id.js_flt1_tv);
        js_flt0_relative = findViewById(R.id.js_flt0_relative);
        js_flt0_tv = findViewById(R.id.js_flt0_tv);
        submit_btn = findViewById(R.id.submit_btn);
        serverThreeSixTv = findViewById(R.id.serverThreeSixTv);
        serverFourEightTv = findViewById(R.id.serverFourEightTv);
        serverNineNineTv = findViewById(R.id.serverNineNineTv);
        serverTwoNineNineTv = findViewById(R.id.serverTwoNineNineTv);
        flowThreeSixTv = findViewById(R.id.flowThreeSixTv);
        flowNineNineTv = findViewById(R.id.flowNineNineTv);
        flowFourEightTv = findViewById(R.id.flowFourEightTv);

        serverThreeSixEdit = findViewById(R.id.serverThreeSixEdit);
        serverFourEightEdit = findViewById(R.id.serverFourEightEdit);
        serverNineNineEdit = findViewById(R.id.serverNineNineEdit);
        serverTwoNineNineEdit = findViewById(R.id.serverTwoNineNineEdit);
        flowThreeSixEdit = findViewById(R.id.flowThreeSixEdit);
        flowNineNineEdit = findViewById(R.id.flowNineNineEdit);
        flowFourEightEdit = findViewById(R.id.flowFourEightEdit);
//        mBtnSwitch = findViewById(R.id.swith_btn);


        posData();
        newPosData();
    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        js_flt1_relative.setOnClickListener(this);
        js_flt0_relative.setOnClickListener(this);
        submit_btn.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        Status = getIntent().getStringExtra("type");
        if (Status.equals("2")) {
            accountId = getIntent().getStringExtra("accoundId");
            posDept();
        }
    }

    //获取后台类别数据
    private void posData() {
        RequestParams params = new RequestParams();
        params.put("userId", getUserId());
        HttpRequest.getBizTerminalRateList(params, "", new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    JSONObject data = new JSONObject(result.getJSONObject("data").toString());
                    List<FillBean> rateT1list = gson.fromJson(data.getJSONArray("rateT0list").toString(),
                            new TypeToken<List<FillBean>>() {
                            }.getType());
                    List<FillBean> rateT0list = gson.fromJson(data.getJSONArray("settlementQrT0list").toString(),
                            new TypeToken<List<FillBean>>() {
                            }.getType());
                    //结算费率T1
                    initReason1(rateT1list, js_flt1_tv);
                    //结算费率T0
                    initReason2(rateT0list, js_flt0_tv);
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

    private void newPosData(){
        RequestParams params = new RequestParams();
        params.put("userId", getUserId());
        HttpRequest.getEchoServer(params, "", new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    List<HomeNewFilBean> homeNewFilBeans = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<HomeNewFilBean>>() {
                            }.getType());
                    serverThreeSixTv.setText(homeNewFilBeans.get(0).getServerName());
                    num0 = Integer.parseInt(homeNewFilBeans.get(0).getServerMoney());

                    serverFourEightTv.setText(homeNewFilBeans.get(1).getServerName());
                    num1 = Integer.parseInt(homeNewFilBeans.get(1).getServerMoney());

                    serverNineNineTv.setText(homeNewFilBeans.get(2).getServerName());
                    num2 = Integer.parseInt(homeNewFilBeans.get(2).getServerMoney());

                    serverTwoNineNineTv.setText(homeNewFilBeans.get(3).getServerName());
                    num3 = Integer.parseInt(homeNewFilBeans.get(3).getServerMoney());

                    flowThreeSixTv.setText(homeNewFilBeans.get(4).getServerName());
                    num4 = Integer.parseInt(homeNewFilBeans.get(4).getServerMoney());

                    flowFourEightTv.setText(homeNewFilBeans.get(5).getServerName());
                    num5 = Integer.parseInt(homeNewFilBeans.get(5).getServerMoney());

                    flowNineNineTv.setText(homeNewFilBeans.get(6).getServerName());
                    num6 = Integer.parseInt(homeNewFilBeans.get(6).getServerMoney());
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



    //获取已填写的类别数据
    private void posDept() {
        RequestParams params = new RequestParams();
        params.put("accountId", accountId);
        HttpRequest.getBizTerminalRateLists(params, "", new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    JSONObject data = new JSONObject(result.getJSONObject("data").toString());
                    js_flt1_tv.setText(data.getJSONObject("rateT0").getString("name"));
                    type1 = data.getJSONObject("rateT0").getString("id");
                    js_flt0_tv.setText(data.getJSONObject("qrsettleRate").getString("name"));
                    type2 = data.getJSONObject("qrsettleRate").getString("id");
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

    //提交数据
    private void subMit() {
        RequestParams params = new RequestParams();
        params.put("rateT0", type1);
        params.put("qrsettleRate", type2);
        params.put("serverThirtySix", serverThreeSixEdit.getText().toString().trim());
        params.put("serverFortyEight", serverFourEightEdit.getText().toString().trim());
        params.put("serverNineNineEdit", serverThreeSixEdit.getText().toString().trim());
        params.put("serverTwoNineNineEdit", serverThreeSixEdit.getText().toString().trim());
        params.put("flowThreeSixEdit", serverThreeSixEdit.getText().toString().trim());
        params.put("flowNineNineEdit", serverThreeSixEdit.getText().toString().trim());
        params.put("flowFourEightEdit", serverThreeSixEdit.getText().toString().trim());
        HttpRequest.postOpenAccount(params, "", new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    if (result.getString("code").equals("200")) {
                        showToast(3, "添加成功");
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("id", result.getString("data"));
                        setResult(5, resultIntent);
                        finish();
                    } else {
                        showToast(3, "添加失败");
                    }
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

    //修改数据
    private void EditData() {
        RequestParams params = new RequestParams();
        params.put("accountId", accountId);
        params.put("rateT0", type1);
        params.put("qrsettleRate", type2);
        HttpRequest.putOpenAccount(params, "", new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    if (result.getString("code").equals("200")) {
                        showToast(3, "修改成功");
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("id", result.getString("accountId"));
                        setResult(5, resultIntent);
                        finish();
                    } else {
                        showToast(3, "修改失败");
                    }
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

    //结算费率T1
    private void initReason1(List<FillBean> mList, TextView tv) {
        reasonPicker1 = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //兑换对象赋值
                tv.setText(mList.get(options1).getName());
                type1 = mList.get(options1).getId();
            }
        }).setTitleText("请选择商户类型").setContentTextSize(17).setTitleSize(17).setSubCalSize(16).build();
        reasonPicker1.setPicker(mList);
    }

    //结算费率T0
    private void initReason2(List<FillBean> mList, TextView tv) {
        reasonPicker2 = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //兑换对象赋值
                tv.setText(mList.get(options1).getName());
                type2 = mList.get(options1).getId();
            }
        }).setTitleText("请选择商户类型").setContentTextSize(17).setTitleSize(17).setSubCalSize(16).build();
        reasonPicker2.setPicker(mList);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.js_flt1_relative:
                reasonPicker1.show();
                break;
            case R.id.js_flt0_relative:
                reasonPicker2.show();
                break;
            case R.id.submit_btn:
                if (TextUtils.isEmpty(type1)) {
                    showToast(3, "选择信用卡结算");
                    return;
                }
                if (TextUtils.isEmpty(type2)) {
                    showToast(3, "选择扫码结算");
                    return;
                }
                if (num0 < Integer.parseInt(serverThreeSixEdit.getText().toString().trim())){
                    showToast(3, "不能大于基本值");
                    return;
                }
                if (num1 < Integer.parseInt(serverFourEightEdit.getText().toString().trim())){
                    showToast(3, "不能大于基本值");
                    return;
                }
                if (num2 < Integer.parseInt(serverNineNineEdit.getText().toString().trim())){
                    showToast(3, "不能大于基本值");
                    return;
                }
                if (num3 < Integer.parseInt(serverTwoNineNineEdit.getText().toString().trim())){
                    showToast(3, "不能大于基本值");
                    return;
                }
                if (num4 < Integer.parseInt(flowThreeSixEdit.getText().toString().trim())){
                    showToast(3, "不能大于基本值");
                    return;
                }
                if (num5 < Integer.parseInt(flowNineNineEdit.getText().toString().trim())){
                    showToast(3, "不能大于基本值");
                    return;
                }
                if (num6 < Integer.parseInt(flowFourEightEdit.getText().toString().trim())){
                    showToast(3, "不能大于基本值");
                    return;
                }
                if (Status.equals("1")) {
                    subMit();
                } else {
                    EditData();
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }


}
