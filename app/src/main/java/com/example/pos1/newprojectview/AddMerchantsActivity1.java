package com.example.pos1.newprojectview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.pos1.R;
import com.example.pos1.adapter.NewMerchantsGridViewAdapter;
import com.example.pos1.base.BaseActivity;
import com.example.pos1.net.HttpRequest;
import com.example.pos1.net.OkHttpException;
import com.example.pos1.net.RequestParams;
import com.example.pos1.net.ResponseCallback;
import com.example.pos1.newprojectview.adapter.NewAreaAdapter;
import com.example.pos1.newprojectview.adapter.NewCityAdapter;
import com.example.pos1.newprojectview.adapter.NewProvinceAdapter;
import com.example.pos1.newprojectview.bean.EditPosPBean;
import com.example.pos1.newprojectview.bean.NewCityBean;
import com.example.pos1.newprojectview.bean.NewDistrictBean;
import com.example.pos1.newprojectview.bean.NewProvinceBean;
import com.example.pos1.newprojectview.bean.NewRateBean;
import com.example.pos1.newprojectview.model.EditPosPProvinceView;
import com.example.pos1.newprojectview.model.GetAddressView;
import com.example.pos1.utils.Utils;
import com.example.pos1.views.MyDialog;
import com.example.pos1.views.MyDialog1;
import com.example.pos1.views.MyGridView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.pos1.utils.Utils.isLocServiceEnable;

/**
 * 作者: qgl
 * 创建日期：2021/8/17
 * 描述: posP商户报件1
 */
public class AddMerchantsActivity1 extends BaseActivity implements View.OnClickListener, EditPosPProvinceView.ProvinceListener {
    //选择商户注册省市区
    private RelativeLayout province_relative;
    //选择商户注册省市区、TextView
    private TextView province_tv;
    //联系人
    private EditText quote_contact_name;
    //商户简写
    private EditText quote_posCode;
    //详细地址
    private EditText quote_address;
    //申请人联系电话
    private EditText quote_phone;
    //提交按钮
    private Button submit_bt;
    //返回键
    private LinearLayout iv_back;
    private List<NewRateBean> rateBeans = new ArrayList<>();
    //类型适配器Adapter
    private NewMerchantsGridViewAdapter madapter;
    //费率ID
    private RelativeLayout feilv_relative;
    private TextView feilv_tv;
    private String rateId = "";
    private String Longitude = ""; //经
    private String Latitude = "";//纬度
    private String loctionEorr = "";
    //省code
    private String provinceNo;
    //市code
    private String cityNo;
    //区code
    private String areaNo;
    private String provinceName;  // 省名称
    private String cityName;  // 市名称
    private String areaName;  // 区名称

    //需要关闭
    public static AddMerchantsActivity1 instance = null;
    //定位方法引入
    private GetAddressView addressView;
    //获取省市区方法
    private EditPosPProvinceView provinceView;
    //填写数据实体
    private EditPosPBean beans = new EditPosPBean();
    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color, false).init();
        return R.layout.addmerchants_activity_01;
    }

    @Override
    protected void initView() {
        instance = this;
        //初始化定位
        addressView = new GetAddressView(this,locationListener);
        //初始化选择省市区
        provinceView = new EditPosPProvinceView(this, this);
        if (isLocServiceEnable(this)) {
            addressView.startLocation();
        } else {
            isDialog("请您设置位置权限在进行报件！");
        }
        iv_back = findViewById(R.id.iv_back);
        province_relative = findViewById(R.id.province_relative);
        province_tv = findViewById(R.id.province_tv);
        quote_phone = findViewById(R.id.quote_phone);
        submit_bt = findViewById(R.id.submit_bt);
        quote_contact_name = findViewById(R.id.quote_contact_name);
        quote_posCode = findViewById(R.id.quote_posCode);
        quote_address = findViewById(R.id.quote_address);
        feilv_relative = findViewById(R.id.feilv_relative);
        feilv_tv = findViewById(R.id.feilv_tv);
        posRate();
    }

    @Override
    protected void initData() {


    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        province_relative.setOnClickListener(this);
        submit_bt.setOnClickListener(this);
        feilv_relative.setOnClickListener(this);
    }

    //定位回调
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                //关闭等待框
                loadDialog.dismiss();
                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    Longitude = location.getLongitude() + "";
                    Latitude = location.getLatitude() + "";
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");
                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    shouLog("地址信息1：", sb.toString());
                    quote_address.setText(location.getAddress());
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                    loctionEorr = sb.toString();
                    shouLog("---》", sb.toString());
                }
                addressView.stopLocation();
            } else {
                shouLog("---》", "失败了");
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        addressView.destroyLocation();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.province_relative:
                provinceView.showCityPicker();
                break;
            case R.id.submit_bt:
                if (TextUtils.isEmpty(quote_posCode.getText().toString().trim())) {
                    showToast(3, "请输入SN");
                    return;
                }
                if (TextUtils.isEmpty(rateId)) {
                    showToast(3, "请选择费率");
                    return;
                }
                if (TextUtils.isEmpty(quote_contact_name.getText().toString().trim())) {
                    showToast(3, "请输入联系人");
                    return;
                }

                if (TextUtils.isEmpty(quote_phone.getText().toString().trim())) {
                    showToast(3, "请输入联系电话");
                    return;
                }

                if (TextUtils.isEmpty(provinceNo.trim())) {
                    showToast(3, "请选择商户省市区");
                    return;
                }
                if (TextUtils.isEmpty(quote_address.getText().toString().trim())) {
                    showToast(3, "请输入详细地址");
                    return;
                }
                if (Longitude.equals("") && Longitude == "") {
                    showToast(3, loctionEorr);
                    return;
                }
                getMerchantInfoRecord(quote_posCode.getText().toString().trim());
                break;
            case R.id.feilv_relative:
                showDialog(rateBeans);
                break;
        }
    }

    //获取费率
    private void posRate() {
        RequestParams params = new RequestParams();
        HttpRequest.posEchoFeeId(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    rateBeans = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<NewRateBean>>() {
                            }.getType());
                    if (rateBeans.size() > 0) {
                        rateId = rateBeans.get(0).getFeeId();
                        feilv_tv.setText(rateBeans.get(0).getFeeValue());
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
    public void showDialog(List<NewRateBean> mList) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_quote_type_dialog, null);
        Button data_bill_dialog_btn = view.findViewById(R.id.data_bill_dialog_btn);
        MyGridView data_bill_dialog_grid = view.findViewById(R.id.data_bill_dialog_grid);
        madapter = new NewMerchantsGridViewAdapter(com.example.pos1.newprojectview.AddMerchantsActivity1.this, mList);
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
        Dialog dialog = new MyDialog(AddMerchantsActivity1.this, true, true, (float) 1).setNewView(view);
        dialog.show();
        data_bill_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 如果没有权限、弹框
     *
     * @param title
     */
    private void isDialog(String title) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.is_dialog_layout, null);
        TextView textView = view.findViewById(R.id.dialog_tv1);
        TextView dialog_determine = view.findViewById(R.id.dialog_determine);
        textView.setText(title);
        Dialog dialog = new MyDialog1(AddMerchantsActivity1.this, true, true, (float) 0.7).setNewView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交
                dialog.dismiss();
                finish();
            }
        });
    }

    /**
     * 判断sn是否存在
     *
     * @param snCode
     */
    private void getMerchantInfoRecord(String snCode) {
        RequestParams params = new RequestParams();
        params.put("posCode", snCode);
        HttpRequest.posMerchantInfoRecord(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                onSuccTest(responseObj);
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }

    /**
     * 接口返回处理
     *
     * @param responseObj
     */
    private void onSuccTest(Object responseObj) {

        try {
            JSONObject result = new JSONObject(responseObj.toString());
            if (result.getString("data").equals("1")) {
                showToast(3, "此SN已占用");
            } else {
                Intent intent = new Intent(AddMerchantsActivity1.this, AddMerchantsActivity2.class);
                beans.setPosCode(quote_posCode.getText().toString().trim());
                beans.setFeeId(rateId);
                beans.setApplicant(quote_contact_name.getText().toString().trim());
                beans.setMerchantShortHand(quote_contact_name.getText().toString().trim());
                beans.setContactPhoneNo(quote_phone.getText().toString().trim());
                beans.setClientServicePhoneNo(quote_phone.getText().toString().trim());
                beans.setAddress(quote_address.getText().toString().trim());
                beans.setProvinceNo(provinceNo);
                beans.setProvinceName(provinceName);
                beans.setCityNo(cityNo);
                beans.setCityName(cityName);
                beans.setAreaNo(areaNo);
                beans.setAreaName(areaName);
                beans.setActiveLongitude(Longitude);
                beans.setActiveLatitude(Latitude);
                Bundle bundle = new Bundle();
                bundle.putSerializable("editBean", (Serializable) beans);
                intent.putExtras(bundle);
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求省市区
     *
     * @param areaLevel  级别 1、2、3
     * @param parentCode 父类的code
     * @param type       请求的界别 1、2、3
     */
    public void postProvince(String areaLevel, String parentCode, int type) {
        RequestParams params = new RequestParams();
        params.put("areaLevel", areaLevel);
        params.put("parentCode", parentCode);
        HttpRequest.getArea(params,getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                //需要转化为实体对象
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    if (type == 0) {
                        List<NewProvinceBean> titleList = gson.fromJson(result.getJSONArray("data").toString(),
                                new TypeToken<List<NewProvinceBean>>() {
                                }.getType());
                        provinceView.setTitleData(titleList);
                    } else if (type == 1) {
                        List<NewCityBean> titleList = gson.fromJson(result.getJSONArray("data").toString(),
                                new TypeToken<List<NewCityBean>>() {
                                }.getType());
                        provinceView.setTwoLabelData(titleList);
                    }else if (type == 2){
                        List<NewDistrictBean> titleList = gson.fromJson(result.getJSONArray("data").toString(),
                                new TypeToken<List<NewDistrictBean>>() {
                                }.getType());
                        provinceView.setAreaLabelData(titleList);
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

    @Override
    public void setSelectAdrCallback(String province,String city,String area,String provinceCode, String cityCode,String areaCode) {
        province_tv.setText(province+"-"+city+"-"+area);
        provinceNo = provinceCode;
        provinceName = province;
        cityNo = cityCode;
        cityName = city;
        areaNo = areaCode;
        areaName = area;
    }
}
