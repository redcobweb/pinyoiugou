package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojogroup.Specification;
import com.pinyougou.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	@Override
	public void add(Specification specification) {
		/*从组合实体类中获取规格实体*/
		TbSpecification tbSpecification = specification.getSpecification();
		/*新增规格实体*/
		specificationMapper.insert(tbSpecification);

		/*从组合实体类中获取规格选项实体*/
		List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
		/*循环存入*/
		for (TbSpecificationOption option:specificationOptionList){
			option.setSpecId(tbSpecification.getId());//设置规格选项所属规格ID
			Long specId = option.getSpecId();
			System.out.println(specId);
			specificationOptionMapper.insert(option);//新增规格选项
		}
	}


	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){

		/*从组合实体类中获取规格实体*/
		TbSpecification tbSpecification = specification.getSpecification();
		/*更新规格实体*/
		specificationMapper.updateByPrimaryKey(tbSpecification);

		/*删除条件,规格选项specId等于id*/
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(tbSpecification.getId());
		/*删除规格对应的规格选项*/
		specificationOptionMapper.deleteByExample(example);

		/*从组合实体类中获取规格选项实体*/
		List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
		/*重新循环存入*/
		for (TbSpecificationOption option:specificationOptionList){
			option.setSpecId(tbSpecification.getId());//设置规格选项所属规格ID
			specificationOptionMapper.insert(option);//新增规格选项
		}
	}
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
        Specification specification = new Specification();
        /*查询规格实体并存入组合实体类*/
        specification.setSpecification(specificationMapper.selectByPrimaryKey(id));

        /*添加条件,规格选项specId等于id*/
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        /*根据条件查询规格选项列表,并存入组合实体类*/
        specification.setSpecificationOptionList(specificationOptionMapper.selectByExample(example));

        /*返回组合实体类*/
        return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			/*删除规格表数据*/
			specificationMapper.deleteByPrimaryKey(id);

			/*删除规格选项列表*/
			/*删除条件:规格选项specId等于id*/
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(example);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		return specificationMapper.selectOptionList();
	}

}
