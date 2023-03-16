package com.example.pos1.newprojectview.editMerchats;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.pos1.newprojectview.AddMerchantsActivity1;
import com.example.pos1.newprojectview.AddMerchantsActivity2;
import com.example.pos1.newprojectview.AddMerchantsActivity3;
import com.example.pos1.newprojectview.adapter.NewAreaAdapter;
import com.example.pos1.newprojectview.bean.EditPosPBean;
import com.example.pos1.newprojectview.bean.NewCityBean;
import com.example.pos1.newprojectview.bean.NewDistrictBean;
import com.example.pos1.newprojectview.bean.NewProvinceBean;
import com.example.pos1.newprojectview.bean.NewRateBean;
import com.example.pos1.newprojectview.model.EditPosPProvinceView;
import com.example.pos1.newprojectview.model.ProvinceView;
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
 * 创建日期：2023/3/8
 * 描述:posP修改报件1
 */
public class EditPosPMerchantsActivity1 extends BaseActivity implements View.OnClickListener, EditPosPProvinceView.ProvinceListener {
    private EditPosPBean beans = new EditPosPBean();
    //SN号
    private EditText quote_posCode;
    //返回键
    private LinearLayout iv_back;
    //费率ID
    private String rateId;
    //费率显示
    private TextView feilv_tv;
    //联系人
    private TextView quote_contact_name;
    //联系电话
    private TextView quote_phone;
    //注册省市区
    private TextView province_tv;
    //省code
    private String provinceNo;
    //市code
    private String cityNo;
    //区code
    private String areaNo;
    private String provinceName;  // 省名称
    private String cityName;  // 市名称
    private String areaName;  // 区名称
    //选择商户注册省市区
    private RelativeLayout province_relative;
    //详细地址
    private EditText quote_address;
    /**************  地图定位  ****************/
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private String Longitude = ""; //经
    private String Latitude = "";//纬度
    private String loctionEorr = "";
    /**************  地图定位  ****************/
    //费率adapter
    private NewMerchantsGridViewAdapter madapter;
    //费率按钮
    private RelativeLayout feilv_relative;
    private List<NewRateBean> rateBeans = new ArrayList<>();
    private EditPosPProvinceView provinceView;
    private Button submit_bt;
    //报件ID
    private String merchantCode;
    //需要关闭
    public static EditPosPMerchantsActivity1 instance = null;
    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color, false).init();
        return R.layout.editpospmerchants_activity_01;
    }

    @Override
    protected void initView() {
        instance = this;
        iv_back = findViewById(R.id.iv_back);
        quote_posCode = findViewById(R.id.quote_posCode);
        feilv_tv = findViewById(R.id.feilv_tv);
        quote_contact_name = findViewById(R.id.quote_contact_name);
        quote_phone = findViewById(R.id.quote_phone);
        province_tv = findViewById(R.id.province_tv);
        quote_address = findViewById(R.id.quote_address);
        feilv_relative = findViewById(R.id.feilv_relative);
        province_relative = findViewById(R.id.province_relative);
        submit_bt = findViewById(R.id.submit_bt);
        //初始化选择省市区
        provinceView = new EditPosPProvinceView(this, this);
        if (isLocServiceEnable(this)) {
            //初始化定位
            initLocation();
            //开启定位
            startLocation();
        } else {
            isDialog("请您设置位置权限在进行报件！");
        }
        posRate();

    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        feilv_relative.setOnClickListener(this);
        province_relative.setOnClickListener(this);
        submit_bt.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        merchantCode = getIntent().getStringExtra("sid");
        posMerchantInfo(merchantCode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.feilv_relative:
                showDialog(rateBeans);
                break;
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
                    showToast(3, "联系人");
                    return;
                }

                if (TextUtils.isEmpty(quote_phone.getText().toString().trim())) {
                    showToast(3, "联系电话");
                    return;
                }

                if (TextUtils.isEmpty(provinceNo.trim())) {
                    showToast(3, "商户省市区");
                    return;
                }
                if (TextUtils.isEmpty(quote_address.getText().toString().trim())) {
                    showToast(3, "详细地址");
                    return;
                }
                if (Longitude.equals("") && Longitude == "") {
                    showToast(3, loctionEorr);
                    return;
                }
                Intent intent = new Intent(EditPosPMerchantsActivity1.this, EditPosPMerchantsActivity2.class);
                beans.setMerchantCode(merchantCode);
                beans.setPosCode(quote_posCode.getText().toString().trim());
                beans.setFeeId(rateId);
                beans.setApplicant(quote_contact_name.getText().toString().trim());
                beans.setMerchantShortHand(quote_contact_name.getText().toString().trim());
                beans.setApplicant(quote_phone.getText().toString().trim());
                beans.setContactPhoneNo(quote_phone.getText().toString().trim());
                beans.setAddress(quote_address.getText().toString().trim());
                beans.setProvinceNo(provinceNo);
                beans.setProvinceName(provinceName);
                beans.setCityNo(cityNo);
                beans.setCityName(cityName);
                beans.setAreaNo(areaNo);
                beans.setAreaName(areaName);
                beans.setRegisterLongitude(Longitude);
                beans.setRegisterLatitude(Latitude);
                Bundle bundle = new Bundle();
                bundle.putSerializable("editBean", (Serializable) beans);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
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
        Dialog dialog = new MyDialog1(EditPosPMerchantsActivity1.this, true, true, (float) 0.7).setNewView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        ;
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
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        try {
            //等待框提示词
            loadDialog.setTitleStr("获取定位中...");
            //开启等待框
            loadDialog.show();
            // 设置定位参数
            locationClient.setLocationOption(locationOption);
            // 启动定位
            locationClient.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);
        //初始化client
        try {
            locationClient = new AMapLocationClient(this.getApplicationContext());
            locationOption = getDefaultOption();
            //设置定位参数
            locationClient.setLocationOption(locationOption);
            // 设置定位监听
            locationClient.setLocationListener(locationListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定位监听
     */
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
                //停止定位
                stopLocation();
            } else {
                shouLog("---》", "失败了");
            }
        }
    };


    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        try {
            // 停止定位
            locationClient.stopLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    /**
     * 获取详情信息
     *
     * @param merchantCode
     */
    private void posMerchantInfo(String merchantCode) {
        RequestParams params = new RequestParams();
        params.put("merchantCode", merchantCode);
        HttpRequest.posMerchantInfo(params, getToken(), new ResponseCallback() {
            //成功回调
            @Override
            public void onSuccess(Object responseObj) {
                //需要转化为实体对象
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString()).getJSONObject("data");
                    beans = gson.fromJson(result.toString(),
                            new TypeToken<EditPosPBean>() {
                            }.getType());
                    setDisplayView(beans);
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
     * @param beans
     */
    private void setDisplayView(EditPosPBean beans) {
        merchantCode = beans.getMerchantCode();
        quote_posCode.setText(beans.getPosCode());
        feilv_tv.setText(beans.getFeeValue());
        rateId = beans.getFeeId();
        quote_contact_name.setText(beans.getApplicant());
        quote_phone.setText(beans.getContactPhoneNo());
        province_tv.setText(beans.getProvinceName()+"-"+beans.getCityName()+"-"+beans.getAreaName());
        provinceNo = beans.getProvinceNo();
        provinceName = beans.getProvinceName();
        cityNo = beans.getCityNo();
        cityName = beans.getCityName();
        areaNo = beans.getAreaNo();
        areaName = beans.getAreaName();
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
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    /***
     * 选择类型
     */
    public void showDialog(List<NewRateBean> mList) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_quote_type_dialog, null);
        Button data_bill_dialog_btn = view.findViewById(R.id.data_bill_dialog_btn);
        MyGridView data_bill_dialog_grid = view.findViewById(R.id.data_bill_dialog_grid);
        madapter = new NewMerchantsGridViewAdapter(EditPosPMerchantsActivity1.this, mList);
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
        Dialog dialog = new MyDialog(EditPosPMerchantsActivity1.this, true, true, (float) 1).setNewView(view);
        dialog.show();
        data_bill_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
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

    /**
     * 地址结果回调
     * @param province
     * @param city
     * @param area
     * @param provinceCode
     * @param cityCode
     * @param areaCode
     */
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