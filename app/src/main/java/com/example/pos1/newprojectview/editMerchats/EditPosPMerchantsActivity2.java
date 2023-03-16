package com.example.pos1.newprojectview.editMerchats;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.pos1.R;
import com.example.pos1.base.BaseActivity;
import com.example.pos1.newprojectview.AddMerchantsActivity2;
import com.example.pos1.newprojectview.AddMerchantsActivity3;
import com.example.pos1.newprojectview.bean.EditPosPBean;
import com.example.pos1.newprojectview.model.UploadImageView;
import com.example.pos1.utils.ImageConvertUtil;
import com.example.pos1.utils.TimeUtils;
import com.example.pos1.utils.Utility;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.ocr.sdk.common.ISdkOcrEntityResultListener;
import com.tencent.ocr.sdk.common.OcrModeType;
import com.tencent.ocr.sdk.common.OcrSDKConfig;
import com.tencent.ocr.sdk.common.OcrSDKKit;
import com.tencent.ocr.sdk.common.OcrType;
import com.tencent.ocr.sdk.entity.IdCardOcrResult;
import com.tencent.ocr.sdk.entity.OcrProcessResult;
import com.wildma.pictureselector.PictureBean;
import com.wildma.pictureselector.PictureSelector;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * 作者: qgl
 * 创建日期：2023/3/8
 * 描述:posP修改报件2
 */
public class EditPosPMerchantsActivity2 extends BaseActivity implements View.OnClickListener, UploadImageView.onResultListener {
    private EditPosPBean editBean = new EditPosPBean();
    private EditText name_ed;
    private EditText card_number_ed;
    private SimpleDraweeView id_card_is;
    private SimpleDraweeView id_card_the;
    private SimpleDraweeView id_card_pay;
    private TextView home_quote_start_time;
    private TextView home_quote_un_time;
    private String url1 = "";
    private String url2 = "";
    private String url3 = "";
    private LinearLayout iv_back;
    final private int IdCardIn = 1004;
    final private int IdCardOn = 1005;
    final private int IdCardPay = 1006;

    private PopupWindow popWindow;
    private View popView;
    // 身份证名字
    private String IdName;
    // 身份证号码
    private String IdNumber;
    // 身份证有效期
    private String IdValidDate;
    //有效期开始时间
    private String st1 = "";
    private String s = "";
    //有效期结束时间
    private String st2 = "";
    private String t = "";

    private Button submit_bt;
    private UploadImageView uploadImageView;

    //需要关闭
    public static EditPosPMerchantsActivity2 instance = null;
    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color, false).init();
        return R.layout.editpospmerchants_activity_02;
    }

    @Override
    protected void initView() {
        instance = this;
        uploadImageView = new UploadImageView(this, getSecretId(), getSecretKey(), getBucketName(), this);
        name_ed = findViewById(R.id.name_ed);
        card_number_ed = findViewById(R.id.card_number_ed);
        id_card_is = findViewById(R.id.id_card_is);
        id_card_the = findViewById(R.id.id_card_the);
        id_card_pay = findViewById(R.id.id_card_pay);
        home_quote_start_time = findViewById(R.id.home_quote_start_time);
        home_quote_un_time = findViewById(R.id.home_quote_un_time);
        iv_back = findViewById(R.id.iv_back);
        submit_bt = findViewById(R.id.submit_bt);
    }

    @Override
    protected void initListener() {
        home_quote_start_time.setOnClickListener(this);
        home_quote_un_time.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        id_card_is.setOnClickListener(this);
        id_card_the.setOnClickListener(this);
        id_card_pay.setOnClickListener(this);
        submit_bt.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        editBean = (EditPosPBean) getIntent().getSerializableExtra("editBean");
        name_ed.setText(editBean.getLegalPersonName());
        card_number_ed.setText(editBean.getCertificateNo());
        id_card_is.setImageURI(editBean.getCertificatesFrontPic());
        id_card_the.setImageURI(editBean.getCertificatesBackPic());
        id_card_pay.setImageURI(editBean.getIdcardHandPic());
        home_quote_start_time.setText(editBean.getCertificateStartDate());
        home_quote_un_time.setText(editBean.getCertificateEndDate());
        s = editBean.getCertificateStartDate();
        t = editBean.getCertificateEndDate();
        url1 = editBean.getCertificatesFrontPic();
        url2 = editBean.getCertificatesBackPic();
        url3 = editBean.getIdcardHandPic();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.id_card_is:
                showEditPhotoWindow(IdCardIn);
                break;
            case R.id.id_card_the:
                showEditPhotoWindow(IdCardOn);
                break;
            case R.id.id_card_pay:
                PictureSelector
                        .create(this, PictureSelector.SELECT_REQUEST_CODE)
                        .selectPicture(false);
                break;
            case R.id.home_quote_start_time:
                selectTime(home_quote_start_time, 1);
                break;
            case R.id.home_quote_un_time:
                selectTime(home_quote_un_time, 2);
                break;
            case R.id.submit_bt:
                if (TextUtils.isEmpty(url1)) {
                    showToast(3, "选择身份证正面照");
                    return;
                }
                if (TextUtils.isEmpty(url2)) {
                    showToast(3, "选择身份证背面照");
                    return;
                }
                if (TextUtils.isEmpty(url3)) {
                    showToast(3, "选择手持身份证照");
                    return;
                }
                if (TextUtils.isEmpty(name_ed.getText().toString().trim())) {
                    showToast(3, "法人姓名");
                    return;
                }
                if (TextUtils.isEmpty(card_number_ed.getText().toString().trim())) {
                    showToast(3, "法人身份证号码");
                    return;
                }
                if (TextUtils.isEmpty(s)) {
                    showToast(3, "有效期开始时间");
                    return;
                }
                if (TextUtils.isEmpty(t)) {
                    showToast(3, "有效期结束时间");
                    return;
                }
                Intent intent = new Intent(this, EditPosPMerchantsActivity3.class);
                editBean.setCertificatesFrontPic(url1);
                editBean.setCertificatesBackPic(url2);
                editBean.setIdcardHandPic(url3);
                editBean.setLegalPersonName(name_ed.getText().toString().trim());
                editBean.setCertificateNo(card_number_ed.getText().toString().trim());
                editBean.setCertificateStartDate(s);
                editBean.setCertificateEndDate(t);
                Bundle bundle = new Bundle();
                bundle.putSerializable("editBean", (Serializable) editBean);
                intent.putExtras(bundle);
                startActivity(intent);
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
                if (value == IdCardIn) {
                    getIDCardIn();
                } else {
                    getIDCardOn();
                }
            }
        });
        tv_pai_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popWindow.isShowing()) {
                    popWindow.dismiss();
                }
                PictureSelector
                        .create(EditPosPMerchantsActivity2.this, value)
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

    //身份证正面识别
    private void getIDCardIn() {
        initSdk(getSecretId(), getSecretKey());
        //弹出界面
        OcrSDKKit.getInstance().startProcessOcrResultEntity(this, OcrType.IDCardOCR_FRONT, null, IdCardOcrResult.class,
                new ISdkOcrEntityResultListener<IdCardOcrResult>() {
                    @Override
                    public void onProcessSucceed(IdCardOcrResult idCardOcrResult, OcrProcessResult ocrProcessResult) {
                        Log.e("response", idCardOcrResult.toString());
                        IdName = idCardOcrResult.getName();
                        IdNumber = idCardOcrResult.getIdNum();
                        Bitmap bitmap = ImageConvertUtil.base64ToBitmap(ocrProcessResult.imageBase64Str);
                        try {
                            if (bitmap != null) {
                                id_card_is.setImageBitmap(bitmap);
                                String folderName = "baojian/Android/" + editBean.getPosCode() + "/" + TimeUtils.getNowTime("day");
                                uploadImageView.upload(IdCardIn, ImageConvertUtil.getFile(bitmap).getCanonicalPath(), folderName);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setResultListData();
                    }

                    @Override
                    public void onProcessFailed(String errorCode, String message, OcrProcessResult ocrProcessResult) {
                        popTip(errorCode, message);
                        Log.e("requestId", ocrProcessResult.toString());
                        IdName = "";
                        IdNumber = "";
                    }

                });
    }

    /**
     * 腾讯卡片识别初始化
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

    // 配置识别出来的数据
    private void setResultListData() {
        if (IdName != null && !IdName.isEmpty()) {
            name_ed.setText(IdName);
            card_number_ed.setText(IdNumber);
        }
        if (IdValidDate != null && !IdValidDate.isEmpty()) {
            st1 = IdValidDate.substring(0, IdValidDate.indexOf("-"));
            st2 = IdValidDate.substring(st1.length() + 1);
            s = st1.replace(".", "");
            t = st2.replace(".", "");
            home_quote_start_time.setText(s);
            home_quote_un_time.setText(t);
        }
    }

    //身份证反面
    private void getIDCardOn() {
        initSdk(getSecretId(), getSecretKey());
        //身份证反面
        OcrSDKKit.getInstance().startProcessOcrResultEntity(this, OcrType.IDCardOCR_BACK, null, IdCardOcrResult.class,
                new ISdkOcrEntityResultListener<IdCardOcrResult>() {
                    @Override
                    public void onProcessSucceed(IdCardOcrResult idCardOcrResult, OcrProcessResult ocrProcessResult) {
                        Log.e("response", idCardOcrResult.toString());
                        Bitmap bitmap = ImageConvertUtil.base64ToBitmap(ocrProcessResult.imageBase64Str);
                        try {
                            if (bitmap != null) {
                                id_card_the.setImageBitmap(bitmap);
                                String folderName = "baojian/Android/" + editBean.getPosCode() + "/" + TimeUtils.getNowTime("day");
                                uploadImageView.upload(IdCardOn, ImageConvertUtil.getFile(bitmap).getCanonicalPath(), folderName);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        IdValidDate = idCardOcrResult.getValidDate();
                        setResultListData();
                    }

                    @Override
                    public void onProcessFailed(String errorCode, String message, OcrProcessResult ocrProcessResult) {
                        popTip(errorCode, message);
                        Log.e("11111requestId", ocrProcessResult.toString());
                        IdValidDate = "";
                    }


                });
    }

    /************************************** 选取照片开始 ***********************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
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
                                id_card_pay.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                                String folderName = "baojian/Android/" + editBean.getPosCode() + "/" + TimeUtils.getNowTime("day");
                                uploadImageView.upload(IdCardPay, file.getPath(), folderName);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        }).setRenameListener(new OnRenameListener() {
                    @Override
                    public String rename(String filePath) {
                        String result = Calendar.getInstance().getTimeInMillis() + ".jpg";
                        return result;
                    }
                }).launch();
            }
        } else if (requestCode == IdCardIn) {
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
                                uploadImageView.upload(IdCardIn, file.getPath(), folderName);

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        }).launch();
            }
        } else if (requestCode == IdCardOn) {
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
                                id_card_the.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                                String folderName = "baojian/Android/" + editBean.getPosCode() + "/" + TimeUtils.getNowTime("day");
                                uploadImageView.upload(IdCardOn, file.getPath(), folderName);

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        }).launch();
            }
        }

    }

    /************************************** 选取照片结束 ***********************************************************************/
    /**
     * 选择时间
     */
    private void selectTime(TextView textView, int type) {
        TimePickerView pvTime = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                //开始时间
                if (type == 1) {
                    s = TimeUtils.getNewTimes(date);
                }
                //结束时间
                else {
                    t = TimeUtils.getNewTimes(date);
                }
                textView.setText(TimeUtils.getNewTimes(date));
            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .build();
        pvTime.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OcrSDKKit.getInstance().release();
    }


    /**
     * 存储图片
     *
     * @param type 图片类型
     * @param url  地址
     */
    @Override
    public void onResult(int type, String url) {
        if (type == IdCardIn) {
            url1 = url;
            shouLog("身份证正面--->", url);
        } else if (type == IdCardOn) {
            url2 = url;
            shouLog("身份证反面--->", url);
        } else if (type == IdCardPay) {
            shouLog("身份证手持--->", url);
            url3 = url;
        }
    }
}