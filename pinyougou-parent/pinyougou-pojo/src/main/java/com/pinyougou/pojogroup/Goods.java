package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

/**
 * <h3>pinyougou</h3>
 * <p>商品组合实体类</p>
 *
 * @author : 沈疴
 * @date : 2020-10-17 10:18
 **/
public class Goods implements Serializable {
    /*商品SPU基本信息*/
    private TbGoods goods;
    /*商品SPU扩展信息*/
    private TbGoodsDesc goodsDesc;
    /*SKU列表*/
    private List<TbItem> itemList;

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
