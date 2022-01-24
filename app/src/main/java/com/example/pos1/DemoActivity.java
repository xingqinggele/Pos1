package com.example.pos1;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pos1.base.BaseActivity;
import com.example.pos1.cos.CosServiceFactory;
import com.example.pos1.homefragment.homequoteactivity.HomeQuoteActivity2;
import com.example.pos1.utils.Utility;
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
import com.wildma.pictureselector.FileUtils;
import com.wildma.pictureselector.PictureBean;
import com.wildma.pictureselector.PictureSelector;

import java.io.File;
import java.math.BigDecimal;

/**
 * 作者: qgl
 * 创建日期：2021/11/11
 * 描述:
 */
public class DemoActivity extends BaseActivity implements View.OnClickListener {
    private Button img_btn;
    private ImageView img;

    private COSXMLUploadTask cosxmlTask;
    private String folderName = "ceshi";
    private CosXmlService cosXmlService;
    private TransferManager transferManager;
    private Button edbtn;
    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity;
    }

    @Override
    protected void initView() {
        //初始化存储桶控件
        cosXmlService = CosServiceFactory.getCosXmlService(this, "ap-beijing", getSecretId(), getSecretKey(), true);
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        transferManager = new TransferManager(cosXmlService, transferConfig);
        img_btn = findViewById(R.id.img_btn);
        img = findViewById(R.id.img);
        EditText edit1 = findViewById(R.id.edit1);
        TextView etxtd = findViewById(R.id.etxtd);
         edbtn = findViewById(R.id.edbtn);
        edbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shouLog("--------------->===",new BigDecimal(edit1.getText().toString().trim()).toString());
                etxtd.setText(new BigDecimal(edit1.getText().toString().trim()).toString());
            }
        });

    }

    @Override
    protected void initListener() {
        img_btn.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        PictureSelector
                .create(DemoActivity.this, PictureSelector.SELECT_REQUEST_CODE)
                .selectPicture(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
            if (data != null) {
                PictureBean pictureBean = data.getParcelableExtra(com.wildma.pictureselector.PictureSelector.PICTURE_RESULT);
                img.setImageBitmap(BitmapFactory.decodeFile(pictureBean.getPath()));

            }
        }

    }


    //上传图片
    private void upload(String file_url) {
        if (TextUtils.isEmpty(file_url)) {
            return;
        }
        if (cosxmlTask == null) {
            File file = new File(file_url);
            String cosPath;
            if (TextUtils.isEmpty(folderName)) {
                cosPath = file.getName();
            } else {
                cosPath = folderName + File.separator + file.getName();
            }
            cosxmlTask = transferManager.upload(getBucketName(), cosPath, file_url, null);
            Log.e("参数-------》", getBucketName() + "----" + cosPath + "---" + file_url);
            cosxmlTask.setTransferStateListener(new TransferStateListener() {
                @Override
                public void onStateChanged(final TransferState state) {
                    // refreshUploadState(state);
                }
            });
            cosxmlTask.setCosXmlProgressListener(new CosXmlProgressListener() {
                @Override
                public void onProgress(final long complete, final long target) {
                    // refreshUploadProgress(complete, target);
                }
            });

            cosxmlTask.setCosXmlResultListener(new CosXmlResultListener() {
                @Override
                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                    COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                    cosxmlTask = null;
                    setResult(RESULT_OK);
                    shouLog("啊啊啊啊", cOSXMLUploadTaskResult.accessUrl);
                }

                @Override
                public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                    if (cosxmlTask.getTaskState() != TransferState.PAUSED) {
                        cosxmlTask = null;
                        Log.e("1111", "上传失败");
                    }
                    exception.printStackTrace();
                    serviceException.printStackTrace();
                }
            });

        }
    }
}