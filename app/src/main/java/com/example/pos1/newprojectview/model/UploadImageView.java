package com.example.pos1.newprojectview.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.pos1.cos.CosServiceFactory;
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

import java.io.File;


/**
 * 作者: qgl
 * 创建日期：2023/3/9
 * 描述:图片上传
 */
public class UploadImageView {
    //存储桶地区
    private String region = "ap-beijing";
    private CosXmlService cosXmlService;
    private TransferManager transferManager;
    private COSXMLUploadTask cosxmlTask;
    private Context mContext;
    private String BucketName;
    private onResultListener onResultListener;


    public UploadImageView(Context context, String secretId, String secretKey, String BucketName,onResultListener listener) {
        this.mContext = context;
        this.BucketName = BucketName;
        this.onResultListener = listener;
        // 初始化腾讯存储桶
        cosXmlService = CosServiceFactory.getCosXmlService(context, region, secretId, secretKey, true);
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        transferManager = new TransferManager(cosXmlService, transferConfig);
    }


    /**
     * 上传图片
     * @param type  当前上传图片标识
     * @param fileUrl
     * @param folderName
     */
    public void  upload(int type, String fileUrl,String folderName) {
        if (TextUtils.isEmpty(fileUrl)) {
            return;
        }
        if (cosxmlTask == null) {
            File file = new File(fileUrl);
            String cosPath;
            if (TextUtils.isEmpty(folderName)) {
                cosPath = file.getName();
            } else {
                cosPath = folderName + File.separator + file.getName();
            }
            cosxmlTask = transferManager.upload(BucketName, cosPath, fileUrl, null);
            Log.e("参数-------》", BucketName + "----" + cosPath + "---" + fileUrl);

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
                    Log.e("1111", "成功");
                    onResultListener.onResult(type,cOSXMLUploadTaskResult.accessUrl);
                }

                @Override
                public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                    if (cosxmlTask.getTaskState() != TransferState.PAUSED) {
                        cosxmlTask = null;
                        Toast.makeText(mContext, "图片上传失败！请重新选择图片", Toast.LENGTH_LONG).show();
                    }
                    exception.printStackTrace();
                    serviceException.printStackTrace();
                }
            });

        }

    }

    public interface onResultListener{
        void onResult(int type,String url);
    }
}