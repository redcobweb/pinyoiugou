package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional //添加事务支持,避免出现向多表插入数据时,插入不全之类的异常,在类上加注解,类中所有的方法都支持事务
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		/*向三张表分别添加数据*/

		/*goods表*/
		goods.getGoods().setAuditStatus("0");/*添加状态:未审核*/
		goodsMapper.insert(goods.getGoods());/*添加基本信息*/

		//事务测试
		int i  = 1/0;

		/*goods_desc表*/
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());/*将商品基本表的id给商品扩展表*/
		goodsDescMapper.insert(goods.getGoodsDesc());/*插入商品扩展表数据*/

		/*item表*/
		saveItemList(goods);
	}


	/*新增和修改都会涉及到item表的插入操作,对于item表的插入操作过于繁琐,故将其提取到一个私有方法中,实现复用*/
	private void saveItemList(Goods goods){
		/*Item表*/
		if ("1".equals(goods.getGoods().getIsEnableSpec())){

			for (TbItem item:goods.getItemList()){
				/*构建标题:SKU名称+规格选项值*/
				String title = goods.getGoods().getGoodsName();/*SKU名称*/
				/*获取item表中的spec属性(包含规格名与规格值)存入Map集合*/
				Map<String,Object> map = JSON.parseObject(item.getSpec());
				/*遍历Map集合,将值添加到title上*/
				for (String key:map.keySet()){
					title+=""+map.get(key);
				}
				item.setTitle(title);

				setItemValues(item,goods);

				itemMapper.insert(item);
			}
		}else {
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());;
			item.setPrice(goods.getGoods().getPrice());
			item.setNum(0);
			item.setStatus("1");
			item.setIsDefault("1");

			item.setSpec("{}");//规格

			setItemValues(item,goods);
			itemMapper.insert(item);
		}
	}


	private void setItemValues(TbItem item,Goods goods){
		/*商品分类*/
		item.setCategoryid(goods.getGoods().getCategory3Id());/*三级分类存入*/

		/*创建事件,更新时间*/
		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());

		/*商品ID*/
		item.setGoodsId(goods.getGoods().getId());
		/*商家ID*/
		item.setSellerId(goods.getGoods().getSellerId());
		/*分类名称*/
		/*工具分类ID在分类表中查询对应的itemCat对象*/
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		/*获取对应itemCat对象的分类名,并存入item表*/
		item.setCategory(itemCat.getName());
		/*品牌名称*/
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		/*商家店铺名称*/
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());
		/*图片*/
		List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if (imageList.size()>0){
			item.setImage((String) imageList.get(0).get("url"));
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		/*更新基本表数据*/
		goodsMapper.updateByPrimaryKey(goods.getGoods());

		/*更新扩展表*/
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());

		/*在原有数据的基础上更新SKU数据太过麻烦,修改SKU列表数据,我们可以删除原有的SKU数据,再将修改后的数据存入数据库*/
		/*构建条件*/
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		/*删除原有数据*/
		itemMapper.deleteByExample(example);
		/*插入修改后的item表数据*/
		saveItemList(goods);

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		/*商品基本表*/
		Goods goods = new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		/*商品扩展表*/
		goods.setGoods(tbGoods);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);

		/*读取SKU列表*/
		/*SKU列表与商品的ID关联,首先构建条件,goodsId等于对应id值*/
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		/*根据条件查询*/
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goods.setItemList(itemList);

		return goods;
	}

	/**
	 * 批量删除
	 * 删除一般不意味着物理删除,而是逻辑删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");//1表示逻辑删除
			goodsMapper.updateByPrimaryKey(goods);
		}
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		//被逻辑删除的数据永远无法被搜索到
			criteria.andIsDeleteIsNull();
		if(goods!=null){

			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				/*模糊查询无法达到预期,应该使精确查询*/
//				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for (Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(goods);
		}
	}
	
}
