package com.example.pos1.mefragment.setup;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
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


import com.example.pos1.R;
import com.example.pos1.base.BaseActivity;
import com.example.pos1.bean.IdCardInfo;
import com.example.pos1.cos.CosServiceFactory;
import com.example.pos1.homefragment.homemerchants.homenewmerchants.merchantstype.bean.SmallinformationBean;
import com.example.pos1.net.HttpRequest;
import com.example.pos1.net.OkHttpException;
import com.example.pos1.net.RequestParams;
import com.example.pos1.net.ResponseCallback;
import com.example.pos1.utils.ImageConvertUtil;
import com.example.pos1.utils.TimeUtils;
import com.example.pos1.utils.Utility;
import com.example.pos1.views.MyDialog1;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;
import com.tencent.ocr.sdk.common.ISDKKitResultListener;
import com.tencent.ocr.sdk.common.OcrModeType;
import com.tencent.ocr.sdk.common.OcrSDKConfig;
import com.tencent.ocr.sdk.common.OcrSDKKit;
import com.tencent.ocr.sdk.common.OcrType;
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
 * 创建日期：2022/3/31
 * 描述:签约界面
 */
public class MeSigningActivity extends BaseActivity implements View.OnClickListener {
    private Button signing_btn;
    private LinearLayout iv_back;

    @Override
    protected int getLayoutId() {
        // 设置状态栏颜色
        statusBarConfig(R.color.new_theme_color, false).init();
        return R.layout.mesigning_layout;
    }

    @Override
    protected void initView() {
        signing_btn = findViewById(R.id.signing_btn);
        iv_back = findViewById(R.id.iv_back);
    }

    @Override
    protected void initListener() {
        signing_btn.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signing_btn:

                showDialog("您是否签约？");
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    //设置支付密码提示Dialog
    private void showDialog(String value) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_content, null);
        TextView textView = view.findViewById(R.id.dialog_tv1);
        TextView dialog_cancel = view.findViewById(R.id.dialog_cancel);
        TextView dialog_determine = view.findViewById(R.id.dialog_determine);
        textView.setText(value);
        Dialog dialog = new MyDialog1(MeSigningActivity.this, true, true, (float) 0.7).setNewView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                posData();

            }
        });
    }

    private void posData() {
        loadDialog.show();
        RequestParams params = new RequestParams();
        params.put("userId", getUserId());
        HttpRequest.postAddReceiverNew(params, "", new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                if (loadDialog.isShowing()) {
                    loadDialog.dismiss();
                }
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    JSONObject data = result.getJSONObject("data");
                    Intent intent = new Intent(MeSigningActivity.this, SiginWebActivity.class);
                    intent.putExtra("url",data.getString("url"));
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(OkHttpException failuer) {
                if (loadDialog.isShowing()) {
                    loadDialog.dismiss();
                }
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }



}