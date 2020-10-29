package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;
    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        /*使用mybatis分页插件PageHelper*/
        PageHelper.startPage(pageNum,pageSize);//分页
        /*查询完成强转为分页插件的Page类型对象*/
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
        /*返回PageResult对象*/
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(TbBrand brand) {
        brandMapper.insertSelective(brand);
    }

    @Override
    public TbBrand findOne(long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id:ids){
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult search(TbBrand brand, int pageNum, int pageSize) {

        /*使用mybatis分页插件PageHelper*/
        PageHelper.startPage(pageNum,pageSize);//分页

        /*创建筛选条件对象*/
        TbBrandExample example = new TbBrandExample();
        /*在brand不为null时,添加查询条件*/
        if (brand!=null){
            /*筛选条件对象调用产生标准方法,返回其内部静态类Criteria对象*/
            TbBrandExample.Criteria criteria = example.createCriteria();
            if (brand.getName()!=null&&brand.getName().length()>0){
                /*Criteria对象添加条件*/
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (brand.getFirstChar()!=null&&brand.getFirstChar().length()>0){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
        }
        /*查询完成强转为分页插件的Page类型对象*/
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
        /*返回PageResult对象*/
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
