package com.example.pos1.homefragment.hometeam;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.pos1.R;
import com.example.pos1.adapter.HomeQuoteGridViewAdapter;
import com.example.pos1.base.BaseActivity;
import com.example.pos1.fragment.MeFragment;
import com.example.pos1.homefragment.homeInvitepartners.FillBean;
import com.example.pos1.homefragment.homeInvitepartners.HomeNewFilBean;
import com.example.pos1.homefragment.homequoteactivity.HomeQuoteActivity1;
import com.example.pos1.homefragment.homequoteactivity.bean.MerchTypeBean3;
import com.example.pos1.homefragment.hometeam.adapter.HomeTeamFillGridViewAdapter;
import com.example.pos1.net.HttpRequest;
import com.example.pos1.net.OkHttpException;
import com.example.pos1.net.RequestParams;
import com.example.pos1.net.ResponseCallback;
import com.example.pos1.views.MyDialog;
import com.example.pos1.views.MyGridView;
import com.example.pos1.views.SwitchButtonView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 作者: qgl
 * 创建日期：2021/10/27
 * 描述:修改伙伴费率
 */
public class HomeTeamRateActivity extends BaseActivity implements View.OnClickListener {
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
    //返回键
    private LinearLayout iv_back;
    private String parnterId;
    private List<FillBean> fillBeanList1 = new ArrayList<>();
    private List<FillBean> fillBeanList2 = new ArrayList<>();
    private HomeTeamFillGridViewAdapter madapter;


    private TextView serverThreeSixTv;
    private TextView serverFourEightTv;
    private TextView serverNineNineTv;
    private TextView serverTwoNineNineTv;
    private TextView flowTwoFourTv;
    private TextView flowThreeSixTv;
    private TextView flowNineNineTv;
    private TextView flowFourEightTv;
    private TextView sixtyTv;

    private TextView serverOneNinetyNineTv;
    private TextView serverFortyNineTv;
    private TextView flowSixtyTv;

    private TextView threeninenineTv;

    private TextView serverThreeSixTv_mine;
    private TextView serverFourEightTv_mine;
    private TextView serverNineNineTv_mine;
    private TextView serverTwoNineNineTv_mine;
    private TextView flowTwoFourTv_mine;
    private TextView flowThreeSixTv_mine;
    private TextView flowNineNineTv_mine;
    private TextView flowFourEightTv_mine;
    private TextView sixty_mine;

    private TextView serverOneNinetyNine_mine;
    private TextView serverFortyNine_mine;
    private TextView flowSixty_mine;
    private TextView threeninenineTv_mine;


    private EditText serverThreeSixEdit;
    private EditText serverFourEightEdit;
    private EditText serverNineNineEdit;
    private EditText serverTwoNineNineEdit;
    private EditText flowTwoFourEdit;
    private EditText flowThreeSixEdit;
    private EditText flowNineNineEdit;
    private EditText flowFourEightEdit;
    private EditText sixtyEdit;

    private EditText serverOneNinetyNineEdit;
    private EditText serverFortyNineEdit;
    private EditText flowSixtyEdit;

    private EditText threeninenineTvEdit;

    private SwitchButtonView mBtnSwitch;

    private int num0 = 0;
    private int num1 = 0;
    private int num2 = 0;
    private int num3 = 0;
    private int num4 = 0;
    private int num5 = 0;
    private int num6 = 0;
    private int num7 = 0;
    private int num8 = 0;

    private int num9 = 0;
    private int num10 = 0;
    private int num11 = 0;
    private int num12 = 0;

    private String serverSwitch = "0";   //0 关 1开

    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color, false).init();
        return R.layout.home_team_rate_activity;
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
        flowTwoFourTv = findViewById(R.id.flowTwoFourTv);
        flowThreeSixTv = findViewById(R.id.flowThreeSixTv);
        flowNineNineTv = findViewById(R.id.flowNineNineTv);
        flowFourEightTv = findViewById(R.id.flowFourEightTv);
        sixtyTv = findViewById(R.id.sixtyTv);

        serverOneNinetyNineTv = findViewById(R.id.serverOneNinetyNineTv);
        serverFortyNineTv = findViewById(R.id.serverFortyNineTv);
        flowSixtyTv = findViewById(R.id.flowSixtyTv);
        threeninenineTv = findViewById(R.id.threeninenineTv);



        serverThreeSixTv_mine = findViewById(R.id.serverThreeSixTv_mine);
        serverFourEightTv_mine = findViewById(R.id.serverFourEightTv_mine);
        serverNineNineTv_mine = findViewById(R.id.serverNineNineTv_mine);
        serverTwoNineNineTv_mine = findViewById(R.id.serverTwoNineNineTv_mine);
        flowTwoFourTv_mine = findViewById(R.id.flowTwoFour_mine);
        flowThreeSixTv_mine = findViewById(R.id.flowThreeSixTv_mine);
        flowNineNineTv_mine = findViewById(R.id.flowNineNineTv_mine);
        flowFourEightTv_mine = findViewById(R.id.flowFourEightTv_mine);
        sixty_mine = findViewById(R.id.sixty_mine);

        serverOneNinetyNine_mine = findViewById(R.id.serverOneNinetyNine_mine);
        serverFortyNine_mine = findViewById(R.id.serverFortyNine_mine);
        flowSixty_mine = findViewById(R.id.flowSixty_mine);

        threeninenineTv_mine = findViewById(R.id.threeninenineTv_mine);


        serverThreeSixEdit = findViewById(R.id.serverThreeSixEdit);
        serverFourEightEdit = findViewById(R.id.serverFourEightEdit);
        serverNineNineEdit = findViewById(R.id.serverNineNineEdit);
        serverTwoNineNineEdit = findViewById(R.id.serverTwoNineNineEdit);
        flowTwoFourEdit = findViewById(R.id.flowTwoFourEdit);
        flowThreeSixEdit = findViewById(R.id.flowThreeSixEdit);
        flowNineNineEdit = findViewById(R.id.flowNineNineEdit);
        flowFourEightEdit = findViewById(R.id.flowFourEightEdit);
        sixtyEdit = findViewById(R.id.sixtyEdit);

        serverOneNinetyNineEdit = findViewById(R.id.serverOneNinetyNineEdit);
        serverFortyNineEdit = findViewById(R.id.serverFortyNineEdit);
        flowSixtyEdit = findViewById(R.id.flowSixtyEdit);

        threeninenineTvEdit = findViewById(R.id.threeninenineTvEdit);

        mBtnSwitch = findViewById(R.id.swith_btn);

        posData();
    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        js_flt1_relative.setOnClickListener(this);
        js_flt0_relative.setOnClickListener(this);
        submit_btn.setOnClickListener(this);
        mBtnSwitch.setmOnCheckedChangeListener(new SwitchButtonView.OnCheckedChangeListener() {
            @Override
            public void OnCheckedChanged(boolean isChecked) {
                if (isChecked) {
                    serverSwitch = "1";
                } else {
                    serverSwitch = "0";
                }
            }
        });
    }

    @Override
    protected void initData() {
        parnterId = getIntent().getStringExtra("parnterId");
        js_flt1_tv.setText(getIntent().getStringExtra("xyk"));
        js_flt0_tv.setText(getIntent().getStringExtra("sm"));
        newPosData();
        newPosEData();
    }

    //获取后台类别数据
    private void posData() {
        RequestParams params = new RequestParams();
        params.put("userId", getUserId());
        HttpRequest.getBizTerminalRateList(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    JSONObject data = new JSONObject(result.getJSONObject("data").toString());
                    fillBeanList1 = gson.fromJson(data.getJSONArray("rateT0list").toString(),
                            new TypeToken<List<FillBean>>() {
                            }.getType());
                    fillBeanList2 = gson.fromJson(data.getJSONArray("settlementQrT0list").toString(),
                            new TypeToken<List<FillBean>>() {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.js_flt1_relative:
                showDialog(fillBeanList1, "请选择信用卡结算", js_flt1_tv, 0);
                break;
            case R.id.js_flt0_relative:
                showDialog(fillBeanList2, "请选择扫码结算", js_flt0_tv, 1);
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
                if (num0 < Integer.parseInt(serverThreeSixEdit.getText().toString().trim())) {
                    shouLog("111", "----------------");
                    serverThreeSixEdit.setError("不能大于基本值");
                    return;
                }
                if (num1 < Integer.parseInt(serverFourEightEdit.getText().toString().trim())) {
                    shouLog("222", "----------------");
                    serverFourEightEdit.setError("不能大于基本值");
                    return;
                }
                if (num2 < Integer.parseInt(serverNineNineEdit.getText().toString().trim())) {
                    shouLog("333", "----------------");
                    serverNineNineEdit.setError("不能大于基本值");
                    return;
                }
                if (num3 < Integer.parseInt(serverTwoNineNineEdit.getText().toString().trim())) {
                    shouLog("444", "----------------");
                    serverTwoNineNineEdit.setError("不能大于基本值");
                    return;
                }

                if (num4 < Integer.parseInt(flowTwoFourEdit.getText().toString().trim())) {
                    shouLog("555", "----------------");
                    flowTwoFourEdit.setError("不能大于基本值");
                    return;
                }

                if (num5 < Integer.parseInt(flowThreeSixEdit.getText().toString().trim())) {
                    shouLog("666", "----------------");
                    flowThreeSixEdit.setError("不能大于基本值");
                    return;

                }
                if (num6 < Integer.parseInt(flowFourEightEdit.getText().toString().trim())) {
                    shouLog("777", "----------------");
                    flowFourEightEdit.setError("不能大于基本值");
                    return;
                }
                if (num7 < Integer.parseInt(flowNineNineEdit.getText().toString().trim())) {
                    shouLog("888", "----------------");
                    flowNineNineEdit.setError("不能大于基本值");
                    return;
                }

                if (num8 < Integer.parseInt(sixtyEdit.getText().toString().trim())) {
                    shouLog("999", "----------------");
                    sixtyEdit.setError("不能大于基本值");
                    return;
                }

                if (num9 < Integer.parseInt(serverOneNinetyNineEdit.getText().toString().trim())){
                    shouLog("888","----------------");
                    serverOneNinetyNineEdit.setError("不能大于基本值");
                    return;
                }

                if (num10 < Integer.parseInt(serverFortyNineEdit.getText().toString().trim())){
                    shouLog("888","----------------");
                    serverFortyNineEdit.setError("不能大于基本值");
                    return;
                }

                if (num11 < Integer.parseInt(flowSixtyEdit.getText().toString().trim())){
                    shouLog("888","----------------");
                    flowSixtyEdit.setError("不能大于基本值");
                    return;
                }

                if (num12 < Integer.parseInt(threeninenineTvEdit.getText().toString().trim())) {
                    shouLog("888", "----------------");
                    threeninenineTvEdit.setError("不能大于基本值");
                    return;
                }

                EditData();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    //获取类目
    private void newPosData() {
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
                    serverThreeSixTv_mine.setText("(0~" + homeNewFilBeans.get(0).getServerMoney() + ")");
                    num0 = Integer.parseInt(homeNewFilBeans.get(0).getServerMoney());

                    serverFourEightTv.setText(homeNewFilBeans.get(1).getServerName());
                    serverFourEightTv_mine.setText("(0~" + homeNewFilBeans.get(1).getServerMoney() + ")");
                    num1 = Integer.parseInt(homeNewFilBeans.get(1).getServerMoney());

                    num2 = Integer.parseInt(homeNewFilBeans.get(2).getServerMoney());
                    serverNineNineTv.setText(homeNewFilBeans.get(2).getServerName());
                    serverNineNineTv_mine.setText("(0~" + homeNewFilBeans.get(2).getServerMoney() + ")");


                    serverTwoNineNineTv.setText(homeNewFilBeans.get(3).getServerName());
                    serverTwoNineNineTv_mine.setText("(0~" + homeNewFilBeans.get(3).getServerMoney() + ")");
                    num3 = Integer.parseInt(homeNewFilBeans.get(3).getServerMoney());


                    flowTwoFourTv.setText(homeNewFilBeans.get(4).getServerName());
                    flowTwoFourTv_mine.setText("(0~" + homeNewFilBeans.get(4).getServerMoney() + ")");
                    num4 = Integer.parseInt(homeNewFilBeans.get(4).getServerMoney());


                    flowThreeSixTv.setText(homeNewFilBeans.get(5).getServerName());
                    flowThreeSixTv_mine.setText("(0~" + homeNewFilBeans.get(5).getServerMoney() + ")");
                    num5 = Integer.parseInt(homeNewFilBeans.get(5).getServerMoney());


                    flowFourEightTv.setText(homeNewFilBeans.get(6).getServerName());
                    flowFourEightTv_mine.setText("(0~" + homeNewFilBeans.get(6).getServerMoney() + ")");
                    num6 = Integer.parseInt(homeNewFilBeans.get(6).getServerMoney());

                    flowNineNineTv.setText(homeNewFilBeans.get(7).getServerName());
                    flowNineNineTv_mine.setText("(0~" + homeNewFilBeans.get(7).getServerMoney() + ")");
                    num7 = Integer.parseInt(homeNewFilBeans.get(7).getServerMoney());


                    sixtyTv.setText(homeNewFilBeans.get(8).getServerName());
                    sixty_mine.setText("(0~" + homeNewFilBeans.get(8).getServerMoney() + ")");
                    num8 = Integer.parseInt(homeNewFilBeans.get(8).getServerMoney());

                    serverOneNinetyNineTv.setText(homeNewFilBeans.get(9).getServerName());
                    serverOneNinetyNine_mine.setText("(0~"+homeNewFilBeans.get(9).getServerMoney()+")");
                    num9 = Integer.parseInt(homeNewFilBeans.get(9).getServerMoney());

                    serverFortyNineTv.setText(homeNewFilBeans.get(10).getServerName());
                    serverFortyNine_mine.setText("(0~"+homeNewFilBeans.get(10).getServerMoney()+")");
                    num10 = Integer.parseInt(homeNewFilBeans.get(10).getServerMoney());

                    flowSixtyTv.setText(homeNewFilBeans.get(11).getServerName());
                    flowSixty_mine.setText("(0~"+homeNewFilBeans.get(11).getServerMoney()+")");
                    num11 = Integer.parseInt(homeNewFilBeans.get(11).getServerMoney());

                    threeninenineTv.setText(homeNewFilBeans.get(12).getServerName());
                    threeninenineTv_mine.setText("(0~" + homeNewFilBeans.get(12).getServerMoney() + ")");
                    num12 = Integer.parseInt(homeNewFilBeans.get(12).getServerMoney());
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

    private void newPosEData() {
        RequestParams params = new RequestParams();
        params.put("userId", parnterId);
        HttpRequest.getParntEchoServer(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    JSONObject data = new JSONObject(result.getJSONObject("data").toString());
                    serverThreeSixEdit.setText(data.getString("serverThirtySix"));
                    serverFourEightEdit.setText(data.getString("serverFortyEight"));
                    serverNineNineEdit.setText(data.getString("serverNinetyNine"));
                    serverTwoNineNineEdit.setText(data.getString("serverTwoNinetyNine"));
                    flowTwoFourEdit.setText(data.getString("flowTwoFour"));
                    flowThreeSixEdit.setText(data.getString("flowThirtySix"));
                    flowFourEightEdit.setText(data.getString("flowFortyEight"));
                    flowNineNineEdit.setText(data.getString("flowNinetyNine"));
                    sixtyEdit.setText(data.getString("serverSixty"));
                    serverSwitch = data.getString("serverSwitch");

                    serverOneNinetyNineEdit.setText(data.getString("serverOneNinetyNine"));
                    serverFortyNineEdit.setText(data.getString("serverFortyNine"));
                    flowSixtyEdit.setText(data.getString("flowSixty"));

                    threeninenineTvEdit.setText(data.getString("serverThreeNinetyNine"));



                    if (data.getString("serverSwitch").equals("0")) {
                        mBtnSwitch.setChecked(false);

                    } else {
                        mBtnSwitch.setChecked(true);
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

    private void EditData() {
        RequestParams params = new RequestParams();
        params.put("userId", parnterId);
        params.put("rateT0", type1);
        params.put("qrsettleRate", type2);
        params.put("serverThirtySix", serverThreeSixEdit.getText().toString().trim());
        params.put("serverFortyEight", serverFourEightEdit.getText().toString().trim());
        params.put("serverNinetyNine", serverNineNineEdit.getText().toString().trim());
        params.put("serverTwoNinetyNine", serverTwoNineNineEdit.getText().toString().trim());
        params.put("flowTwoFour", flowTwoFourEdit.getText().toString().trim());
        params.put("flowThirtySix", flowThreeSixEdit.getText().toString().trim());
        params.put("flowFortyEight", flowFourEightEdit.getText().toString().trim());
        params.put("flowNinetyNine", flowNineNineEdit.getText().toString().trim());
        params.put("serverSixty", sixtyEdit.getText().toString().trim());

        params.put("serverOneNinetyNine", serverOneNinetyNineEdit.getText().toString().trim());
        params.put("serverFortyNine", serverFortyNineEdit.getText().toString().trim());
        params.put("flowSixty", flowSixtyEdit.getText().toString().trim());
        params.put("serverThreeNinetyNine", threeninenineTvEdit.getText().toString().trim());

        params.put("serverSwitch", serverSwitch);
        HttpRequest.putModifyRate(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    if (result.getString("code").equals("200")) {
                        showToast(3, "修改成功");
                        // 成功,通知我的，界面更新头像
                        EventBus.getDefault().post(new HomeTeamActivity());

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


    /***
     * 选择类型
     */
    public void showDialog(List<FillBean> mList, String title, TextView tv, int value) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_quote_type_dialog, null);
        TextView title_tv = view.findViewById(R.id.title_tv);
        Button data_bill_dialog_btn = view.findViewById(R.id.data_bill_dialog_btn);
        MyGridView data_bill_dialog_grid = view.findViewById(R.id.data_bill_dialog_grid);
        title_tv.setText(title);
        madapter = new HomeTeamFillGridViewAdapter(HomeTeamRateActivity.this, mList);
        data_bill_dialog_grid.setAdapter(madapter);
        data_bill_dialog_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //把点击的position传递到adapter里面去
                madapter.changeState(i);
                tv.setText(mList.get(i).getName());
                if (value == 0) {
                    type1 = mList.get(i).getId();
                } else {
                    type2 = mList.get(i).getId();
                }
            }
        });
        Dialog dialog = new MyDialog(HomeTeamRateActivity.this, true, true, (float) 1).setNewView(view);
        dialog.show();
        data_bill_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}