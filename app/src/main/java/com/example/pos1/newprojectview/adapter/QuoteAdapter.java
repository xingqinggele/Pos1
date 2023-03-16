package com.example.pos1.newprojectview.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.pos1.R;
import com.example.pos1.newprojectview.bean.QuoteBean;

import java.math.BigDecimal;
import java.util.List;

/**
 * 作者: qgl
 * 创建日期：2022/8/22
 * 描述:新报件Adapter
 */
public class QuoteAdapter extends BaseQuickAdapter<QuoteBean, BaseViewHolder> {
    //绑定回调方法
    private BindCallback callback;
    private EditCallback editCallback;
    private EditRateCallback editRateCallback;
    private FallDialog fallDialog;
    public QuoteAdapter(int layoutResId, @Nullable List<QuoteBean> data, BindCallback callback, EditCallback editCallback,EditRateCallback editRateCallback,FallDialog fallDialog) {
        super(layoutResId, data);
        this.callback = callback;
        this.editCallback = editCallback;
        this.editRateCallback = editRateCallback;
        this.fallDialog = fallDialog;
    }

    @SuppressLint("Range")
    @Override
    protected void convert(BaseViewHolder helper, QuoteBean item) {
        helper.setText(R.id.me_merchants_name, "商户姓名: " + item.getMerchantName());
        helper.setText(R.id.me_merchants_number, "商户编号: " + item.getMerchCode());
        helper.setText(R.id.me_merchants_price, new BigDecimal(item.getTransAmount()).toString());
        //修改
        boolean repair = true;
        //绑定
        boolean bind = true;
        //修改文字
        String edit_test = "";
        //绑定文字
        String bind_test = "";
        //状态颜色
        String color = "#3CA0FF";
        //状态文字
        String title = "";

        //费率是否显示
        boolean rate = false;
        //posp修改状态
        boolean editEnl = false;
        //报件失败原因
        boolean failureMsg = false;
        //修改状态颜色
        String eColor = "#3CA0FF";
        //修改报件状态
        String editStaticTV = "";
        //修改失败原因显示
        boolean editFailureMsg = false;
        if (item.getType().equals("1")){
            editEnl = false;

            bind = false;
            if (item.getIsAudit().equals("1")){
                title = "报件失败";
                color = "#DC143C";
                repair = true;
                edit_test = "重新报件";
                failureMsg = true;

            }else if (item.getIsAudit().equals("2")){
                title = "进行中";
                color = "#3CA0FF";
                repair = false;
                failureMsg = false;
            }else if (item.getIsAudit().equals("3")){
                title = "报件成功";
                edit_test = "修改";
                color = "#29D385";
                repair = true;
                failureMsg = false;
            }

        }else {

            if (item.getActivateStatus().equals("0")){
                bind_test = "未绑定";

            }else {
                bind_test = "已绑定";

            }
            if (item.getIsAudit().equals("1") ){
                title = "报件失败";
                color = "#DC143C";
                repair = true;
                bind = false;
                edit_test = "修改报件";
                rate = false;

                editEnl = false;
                failureMsg = true;
            }else if (item.getIsAudit().equals("2") || item.getIsAudit().equals("0")){
                title = "进行中";
                color = "#3CA0FF";
                repair = false;
                bind = false;

                editEnl = false;
                failureMsg = false;

            }else if (item.getIsAudit().equals("3")){
                title = "报件成功";
                edit_test = "修改";
                color = "#29D385";
                bind = true;
                rate = true;
                editEnl = true;
                failureMsg = false;
                //posP修改
                switch (item.getSettleAccountStatus()){
                    case "1":
                        editStaticTV = "修改失败";
                        repair = true;
                        eColor = "#DC143C";
                        editFailureMsg = true;
                        break;
                    case "2":
                        editStaticTV = "修改中";
                        repair = false;
                        eColor = "#3CA0FF";
                        editFailureMsg = false;
                        break;
                    case "3":
                        editStaticTV = "修改成功";
                        repair = true;
                        eColor = "#29D385";
                        editFailureMsg = false;
                        break;
                }
            }
        }
        helper.setVisible(R.id.edit_merchants, repair);
        helper.setVisible(R.id.bind_merchants, bind);
        helper.setText(R.id.tv_status, "审核状态："+title);
        helper.setTextColor(R.id.tv_status, Color.parseColor(color));
        helper.setText(R.id.edit_merchants, edit_test);
        helper.setText(R.id.bind_merchants, bind_test);
        //修改状态
        helper.setVisible(R.id.m_rela2,editEnl);
        //修改进件状态
        helper.setText(R.id.edit_static,"修改状态："+editStaticTV);
        helper.setTextColor(R.id.edit_static, Color.parseColor(eColor));
        helper.setText(R.id.edit_fall_tv,"("+item.getAuditMsg()+")");
        helper.setVisible(R.id.edit_fall_tv,editFailureMsg);



        //报件失败原因
        helper.setText(R.id.b_fail_msg_tv,"("+item.getAuditMsg()+")");
        //报件失败原因是否显示
        helper.setVisible(R.id.b_fail_msg_tv,failureMsg);
        //费率
        helper.setVisible(R.id.rate_edit, rate);

        //绑定
        helper.setOnClickListener(R.id.bind_merchants, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("sn",item.getId());
                if (callback == null) return;
                if (!item.getActivateStatus().equals("0"))return;
                    callback.addBind(item.getMerchCode(),item.getId());

            }
        });
        //修改报件
        helper.setOnClickListener(R.id.edit_merchants, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editCallback == null) return;
                editCallback.edit(item.getMerchCode(),item.getIsAudit(),item.getType());
            }
        });
        //修改费率
        helper.setOnClickListener(R.id.rate_edit, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editRateCallback.edit(item.getMerchCode());
            }
        });

        helper.setOnClickListener(R.id.b_fail_msg_tv, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    fallDialog.dialog(item.getAuditMsg());
            }
        });

        helper.setOnClickListener(R.id.edit_fall_tv, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fallDialog.dialog(item.getAuditMsg());
            }
        });

    }

    //绑定接口
    public interface BindCallback {
        void addBind(String id, String a);
    }

    //修改接口
    public interface EditCallback{
        void edit(String id,String type,String isSta);
    }

    //修改费率
    public interface EditRateCallback{
        void edit(String id);
    }

    //失败原因
    public interface FallDialog{
        void dialog(String content);
    }
}