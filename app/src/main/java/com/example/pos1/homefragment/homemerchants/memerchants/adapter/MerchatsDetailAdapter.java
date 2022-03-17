package com.example.pos1.homefragment.homemerchants.memerchants.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.pos1.homefragment.homemerchants.memerchants.bean.MerchantsDetailBean;

import java.util.List;

/**
 * 创建：  qgl
 * 时间：
 * 描述： 商戶详情adapter
 */
public class MerchatsDetailAdapter extends BaseQuickAdapter<MerchantsDetailBean, BaseViewHolder>{


    public MerchatsDetailAdapter(int layoutResId, @Nullable List<MerchantsDetailBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MerchantsDetailBean item) {

    }
}
