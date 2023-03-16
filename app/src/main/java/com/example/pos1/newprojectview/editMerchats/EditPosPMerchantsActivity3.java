package com.example.pos1.newprojectview.editMerchats;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pos1.R;
import com.example.pos1.base.BaseActivity;
import com.example.pos1.net.HttpRequest;
import com.example.pos1.net.OkHttpException;
import com.example.pos1.net.RequestParams;
import com.example.pos1.net.ResponseCallback;
import com.example.pos1.newprojectview.AddMerchantsActivity1;
import com.example.pos1.newprojectview.AddMerchantsActivity2;
import com.example.pos1.newprojectview.AddMerchantsActivity3;
import com.example.pos1.newprojectview.adapter.BankNameAdapter;
import com.example.pos1.newprojectview.adapter.ZhiBankNameAdapter;
import com.example.pos1.newprojectview.bean.BankNameBean;
import com.example.pos1.newprojectview.bean.EditPosPBean;
import com.example.pos1.newprojectview.bean.NewCityBean;
import com.example.pos1.newprojectview.bean.NewProvinceBean;
import com.example.pos1.newprojectview.model.PosPAddProvinceView;
import com.example.pos1.newprojectview.model.ProvinceView;
import com.example.pos1.newprojectview.model.UploadImageView;
import com.example.pos1.utils.CustomConfigUtil;
import com.example.pos1.utils.ImageConvertUtil;
import com.example.pos1.utils.TimeUtils;
import com.example.pos1.utils.Utility;
import com.example.pos1.views.MyListView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tencent.ocr.sdk.common.ISdkOcrEntityResultListener;
import com.tencent.ocr.sdk.common.OcrModeType;
import com.tencent.ocr.sdk.common.OcrSDKConfig;
import com.tencent.ocr.sdk.common.OcrSDKKit;
import com.tencent.ocr.sdk.common.OcrType;
import com.tencent.ocr.sdk.entity.BankCardOcrResult;
import com.tencent.ocr.sdk.entity.OcrProcessResult;
import com.wildma.pictureselector.PictureBean;
import com.wildma.pictureselector.PictureSelector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 作者: qgl
 * 创建日期：2023/3/8
 * 描述:posP修改报件3
 */
public class EditPosPMerchantsActivity3 extends BaseActivity implements View.OnClickListener, UploadImageView.onResultListener, AdapterView.OnItemClickListener {
    private EditPosPBean editBean = new EditPosPBean();
    private SimpleDraweeView id_card_is;
    private EditText b_number;
    private EditText quote_xy_card_name;
    private EditText quote_xy_phone;
    private String bUrl = "";
    private int BankCardIn = 1007;
    private PopupWindow popWindow;
    private View popView;
    //银行卡正面图片
    private Bitmap retBitmap1;
    private UploadImageView uploadImageView;
    private RelativeLayout province_relative;
    private PosPAddProvinceView provinceView;
    private TextView province_tv;
    //开户省
    private String BankProvince = "";
    //开户市
    private String BankCity = "";
    private EditText bank_code_ed;
    private EditText bankSite_tv;
    private boolean isBank = true;
    private boolean isBankSite = true;
    private List<BankNameBean> bankNameBeanList = new ArrayList<>();
    private List<BankNameBean> zhiBankList = new ArrayList<>();
    private MyListView zhListView;

    private BankNameAdapter bankNameAdapter;
    private MyListView addListView;
    //银行编码
    private String bankCode = "";
    private ZhiBankNameAdapter zhiBankNameAdapter;
    //支行名称
    private String bankSite = "";
    //开户支行联行号
    private String bankSiteCode = "";
    private Button submit_bt;
    private LinearLayout iv_back;

    @Override
    protected int getLayoutId() {
        statusBarConfig(R.color.new_theme_color, false).init();
        return R.layout.editpospmerchants_activity_03;
    }

    @Override
    protected void initView() {
        uploadImageView = new UploadImageView(this, getSecretId(), getSecretKey(), getBucketName(), this);
        //初始化选择省市区
        provinceView = new PosPAddProvinceView(this, EditPosPMerchantsActivity3.this);
        id_card_is = findViewById(R.id.id_card_is);
        b_number = findViewById(R.id.b_number);
        quote_xy_card_name = findViewById(R.id.quote_xy_card_name);
        quote_xy_phone = findViewById(R.id.quote_xy_phone);
        province_relative = findViewById(R.id.province_relative);
        province_tv = findViewById(R.id.province_tv);
        bank_code_ed = findViewById(R.id.bank_code_ed);
        addListView = findViewById(R.id.addListView);
        bankSite_tv = findViewById(R.id.bankSite_tv);
        zhListView = findViewById(R.id.zhListView);
        submit_bt = findViewById(R.id.submit_bt);
        iv_back = findViewById(R.id.iv_back);

    }

    @Override
    protected void initListener() {
        id_card_is.setOnClickListener(this);
        province_relative.setOnClickListener(this);
        submit_bt.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        bank_code_ed.addTextChangedListener(textWatcher);
        bankSite_tv.addTextChangedListener(zhiTextWatcher);
        addListView.setOnItemClickListener(this);
        zhListView.setOnItemClickListener(this);

    }

    @Override
    protected void initData() {
        editBean = (EditPosPBean) getIntent().getSerializableExtra("editBean");
        id_card_is.setImageURI(editBean.getBankCardPic());
        bUrl = editBean.getBankCardPic();
        b_number.setText(editBean.getBankCardAccount());
        quote_xy_card_name.setText(editBean.getBankCardHolder());
        quote_xy_phone.setText(editBean.getBankCardPhone());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_card_is:
                showEditPhotoWindow(BankCardIn);
                break;
            case R.id.province_relative:
                provinceView.showCityPicker();
                break;
            case R.id.submit_bt:
                if (TextUtils.isEmpty(bUrl)) {
                    showToast(3, "银行卡正面照片");
                    return;
                }
                if (TextUtils.isEmpty(b_number.getText().toString().trim())) {
                    showToast(3, "银行卡号");
                    return;
                }
                if (TextUtils.isEmpty(quote_xy_card_name.getText().toString().trim())) {
                    showToast(3, "开户名");
                    return;
                }
                if (TextUtils.isEmpty(quote_xy_phone.getText().toString().trim())) {
                    showToast(3, "开户手机号");
                    return;
                }
                if (TextUtils.isEmpty(BankProvince)) {
                    showToast(3, "请选择省");
                    return;
                }
                if (TextUtils.isEmpty(BankCity)) {
                    showToast(3, "请选择市");
                    return;
                }
                if (TextUtils.isEmpty(bankCode)) {
                    showToast(3, "请选择正确银行编码");
                    return;
                }

                if (TextUtils.isEmpty(bankSite)) {
                    showToast(3, "请选择正确开户支行网点");
                    return;
                }
                posData();
                break;
            case R.id.iv_back:
                finish();
                break;

        }
    }

    /**
     * 弹出框
     *
     * @param value
     */
    private void showEditPhotoWindow(int value) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        popView = layoutInflater.inflate(R.layout.popwindow_main, (ViewGroup) null);
        popWindow = new PopupWindow(popView, -1, -2);
        popWindow.setAnimationStyle(R.style.AnimBottom);
        popWindow.setBackgroundDrawable(new ColorDrawable());
        popWindow.showAtLocation(popView, 80, 0, 0);
        popWindow.setTouchable(true);
        popWindow.setOutsideTouchable(false);
        popWindow.setFocusable(true);
        TextView tv_select_pic = popView.findViewById(R.id.tv_photo);
        TextView tv_pai_pic = popView.findViewById(R.id.tv_photograph);
        TextView tv_cancl = popView.findViewById(R.id.tv_cancle);
        LinearLayout layout = popView.findViewById(R.id.dialog_ll);
        tv_select_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popWindow.isShowing()) {
                    popWindow.dismiss();
                }
                initSdk(getSecretId(), getSecretKey());
                OcrSDKKit.getInstance().startProcessOcrResultEntity(EditPosPMerchantsActivity3.this, OcrType.BankCardOCR,
                        CustomConfigUtil.getInstance().getCustomConfigUi(), BankCardOcrResult.class, new ISdkOcrEntityResultListener<BankCardOcrResult>() {
                            @Override
                            public void onProcessSucceed(BankCardOcrResult bankCardOcrResult, OcrProcessResult ocrProcessResult) {
                                //回显银行卡信息
                                getBank_information(bankCardOcrResult, ocrProcessResult.imageBase64Str);
                            }

                            @Override
                            public void onProcessFailed(String errorCode, String message, OcrProcessResult ocrProcessResult) {
                                popTip(errorCode, message);
                                Log.e("requestId", ocrProcessResult.toString());
                            }


                        });
            }
        });
        tv_pai_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popWindow.isShowing()) {
                    popWindow.dismiss();
                }
                PictureSelector
                        .create(EditPosPMerchantsActivity3.this, value)
                        .selectPicture(false);
            }
        });
        tv_cancl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popWindow.isShowing()) {
                    popWindow.dismiss();
                }
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popWindow.isShowing()) {
                    popWindow.dismiss();
                }
            }
        });

    }

    /**
     * 腾讯银行卡识别初始化
     */
    private void initSdk(String secretId, String secretKey) {
        // 启动参数配置
        OcrType ocrType = OcrType.BankCardOCR; // 设置默认的业务识别，银行卡
        OcrSDKConfig configBuilder = OcrSDKConfig.newBuilder(secretId, secretKey, null)
                .setOcrType(ocrType)
                .setModeType(OcrModeType.OCR_DETECT_MANUAL)
                .build();
        // 初始化SDK
        OcrSDKKit.getInstance().initWithConfig(this.getApplicationContext(), configBuilder);
    }

    /**
     * 获取银行卡信息
     *
     * @param response
     * @param srcBase64Image
     */
    public void getBank_information(BankCardOcrResult response, String srcBase64Image) {
        try {
            if (!srcBase64Image.isEmpty()) {
                retBitmap1 = ImageConvertUtil.base64ToBitmap(srcBase64Image);
            }
            if (retBitmap1 != null) {
                id_card_is.setImageBitmap(retBitmap1);
                String folderName = "baojian/Android/" + editBean.getPosCode() + "/" + TimeUtils.getNowTime("day");
                uploadImageView.upload(BankCardIn, ImageConvertUtil.getFile(retBitmap1).getCanonicalPath(), folderName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!response.getCardNo().isEmpty()) {
            b_number.setText(response.getCardNo());
        }
    }

    @Override
    public void onResult(int type, String url) {
        bUrl = url;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BankCardIn) {
            if (data != null) {
                PictureBean pictureBean = data.getParcelableExtra(PictureSelector.PICTURE_RESULT);
                Luban.with(this)
                        .load(pictureBean.getPath())
                        .ignoreBy(100)
                        .setTargetDir(Utility.getPath())
                        .filter(new CompressionPredicate() {
                            @Override
                            public boolean apply(String path) {
                                return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                            }
                        })
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(File file) {
                                id_card_is.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                                String folderName = "baojian/Android/" + editBean.getPosCode() + "/" + TimeUtils.getNowTime("day");
                                uploadImageView.upload(BankCardIn, file.getPath(), folderName);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        }).launch();
            }
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
        HttpRequest.getArea(params, getToken(), new ResponseCallback() {
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
     * 省市区回调接口
     *
     * @param provinceTv   选择的省市区text
     * @param provinceCode
     * @param cityCode
     */
    public void setSelectAdrCallback(String provinceTv, String provinceCode, String cityCode) {
        province_tv.setText(provinceTv);
        BankProvince = provinceCode;
        BankCity = cityCode;
    }

    //银行编码监听
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!TextUtils.isEmpty(bank_code_ed.getText().toString().trim()) && isBank)
                getBanks(bank_code_ed.getText().toString().trim());
        }
    };

    private void getBanks(String bankName) {
        if (TextUtils.isEmpty(bankName)) {
            return;
        }
        RequestParams params = new RequestParams();
        params.put("page", "1");
        params.put("pageSize", "100");
        params.put("bankName", bankName);
        HttpRequest.getBanks(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                //需要转化为实体对象
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    JSONObject data = result.getJSONObject("data");

                    int totalCount = result.getJSONObject("data").getInt("totalCount");
                    if (totalCount > 0) {
                        bankNameBeanList = gson.fromJson(data.getJSONArray("list").toString(),
                                new TypeToken<List<BankNameBean>>() {
                                }.getType());
                    }

                    bankNameAdapter = new BankNameAdapter(EditPosPMerchantsActivity3.this, bankNameBeanList);
                    bankNameAdapter.notifyDataSetChanged();
                    addListView.setAdapter(bankNameAdapter);

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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.addListView:
                isBank = false;
                bank_code_ed.setText(bankNameBeanList.get(i).getName());
                bankCode = bankNameBeanList.get(i).getCode();
                bankNameAdapter.clear();
                isBank = true;
                break;
            case R.id.zhListView:
                isBankSite = false;
                bankSite_tv.setText(zhiBankList.get(i).getName());
                bankSite = zhiBankList.get(i).getName();
                bankSiteCode = zhiBankList.get(i).getCode();
                zhiBankNameAdapter.clear();
                isBankSite = true;
                break;
        }
    }

    //银行支行
    TextWatcher zhiTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!TextUtils.isEmpty(bankSite_tv.getText().toString().trim()) && isBankSite)
                getBranchs(bankSite_tv.getText().toString().trim());
        }
    };


    private void getBranchs(String bankSiteName) {
        RequestParams params = new RequestParams();
        params.put("page", "1");
        params.put("pageSize", "100");
        params.put("provinceCode", BankProvince);
        params.put("cityCode", BankCity);
        params.put("bankCode", bankCode);
        params.put("bankSiteName", bankSiteName);
        HttpRequest.getBranchs(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                //需要转化为实体对象
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    JSONObject data = result.getJSONObject("data");
                    int totalCount = result.getJSONObject("data").getInt("totalCount");
                    if (totalCount > 0) {
                        zhiBankList = gson.fromJson(data.getJSONArray("list").toString(),
                                new TypeToken<List<BankNameBean>>() {
                                }.getType());
                    }
                    zhiBankNameAdapter = new ZhiBankNameAdapter(EditPosPMerchantsActivity3.this, zhiBankList);
                    zhiBankNameAdapter.notifyDataSetChanged();
                    zhListView.setAdapter(zhiBankNameAdapter);

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
     * 修改
     */
    private void posData() {
        loadDialog.show();
        RequestParams params = new RequestParams();
        //商户号
        params.put("merchantCode", editBean.getMerchantCode());
        //联系人
        params.put("applicant", editBean.getApplicant());
        //联系电话
        params.put("contactPhoneNo", editBean.getContactPhoneNo());
        //客服电话
        params.put("clientServicePhoneNo", editBean.getClientServicePhoneNo());
        //费率ID
        params.put("feeId", editBean.getFeeId());
        //SN
        params.put("posCode", editBean.getPosCode());
        //商户简写
        params.put("merchantShortHand", editBean.getMerchantShortHand());
        //省份代码
        params.put("provinceNo", editBean.getProvinceNo());
        //城市编号
        params.put("cityNo", editBean.getCityNo());
        //区县代码
        params.put("areaNo", editBean.getAreaNo());
        //详细地址
        params.put("address", editBean.getAddress());
        //证件有效期截止日期（例：1949-10-01）;
        params.put("certificateEndDate", editBean.getCertificateEndDate());
        //法人证件号码
        params.put("certificateNo", editBean.getCertificateNo());
        //证件有效期开始日期（例：1949-10-01）
        params.put("certificateStartDate", editBean.getCertificateStartDate());
        //证件国徽面
        params.put("certificatesBackPic", editBean.getCertificatesBackPic());
        //证件人脸面
        params.put("certificatesFrontPic",editBean.getCertificatesFrontPic());
        //手持身份证照片
        params.put("idcardHandPic", editBean.getIdcardHandPic());
        //法人姓名
        params.put("legalPersonName", editBean.getLegalPersonName());
        //银行账号
        params.put("bankCardAccount", b_number.getText().toString().trim());
        //开户名
        params.put("bankCardHolder", quote_xy_card_name.getText().toString().trim());
        //银行卡预留手机号
        params.put("bankCardPhone", quote_xy_phone.getText().toString().trim());
        //银行卡照片
        params.put("bankCardPic", bUrl);
        //开户银行省份
        params.put("bankProvince", BankProvince);
        //开户银行城市
        params.put("bankCity", BankCity);
        //银行编码
        params.put("bankCode", bankCode);
        //开户行网点
        params.put("bankSite", bankSite);
        //开户支行联行号
        params.put("unionpayCode", bankSiteCode);
        //手机号
        params.put("phone", getUserName());
        //商户活体检测地址
        params.put("activeAddress", editBean.getActiveAddress());
        //商户活体检测地址纬度
        params.put("activeLatitude", editBean.getActiveLatitude());
        //商户活体检测地址经度
        params.put("activeLongitude", editBean.getActiveLongitude());
        //省名称
        params.put("provinceName", editBean.getProvinceName());
        //市名称
        params.put("cityName", editBean.getCityName());
        //区名称
        params.put("areaName", editBean.getAreaName());
        shouLog("提交的值-------------->", params.toString());
        HttpRequest.getPosPEdit(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                loadDialog.dismiss();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    showToast(2, result.getString("msg") + "");
                    EditPosPMerchantsActivity1.instance.finish();
                    EditPosPMerchantsActivity2.instance.finish();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(OkHttpException failuer) {
                loadDialog.dismiss();
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }
}