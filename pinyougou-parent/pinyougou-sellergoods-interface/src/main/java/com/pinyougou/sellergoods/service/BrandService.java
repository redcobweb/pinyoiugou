package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/*
* 品牌接口
* */
public interface BrandService {
    /**
     * 查询所有记录
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 品牌记录分页
     * @param pageNum 当前页面
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(int pageNum,int pageSize);

    /**
     *新增品牌
     * @param brand 品牌对象
     */
    public void add(TbBrand brand);

    /**
     *根据id查询品牌
     * @param id 品牌id
     * @return 品牌对象
     */
    public TbBrand findOne(long id);

    /**
     * 更新品牌
     * @param brand 品牌对象
     */
    public void update(TbBrand brand);

    /**
     *
     * @param ids id数组
     */
    public void delete(Long[] ids);

    /**
     * 根据要求查询品牌记录并分页
     * @param brand 将查询条件存入实体类
     * @param pageNum 当前页面
     * @param pageSize 每页记录数
     * @return 查询并分页后的数据
     */
    public PageResult search(TbBrand brand, int pageNum,int pageSize);

    /**
     *
     * @return 返回封装为map的品牌列表
     */
    public List<Map> selectOptionList();


}
