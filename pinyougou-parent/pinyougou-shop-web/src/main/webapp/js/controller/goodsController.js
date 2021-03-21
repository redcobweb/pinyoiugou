 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,itemCatService,typeTemplateService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id = $location.search()['id'];
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				/*商品介绍信息需要转换*/
				editor.html($scope.entity.goodsDesc.introduction);

				/*商品图片信息转换为JSON字符串*/
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);

				/*扩展属性*/
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);

				/*规格属性*/
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);

				/*转换SKU列表中的规格对象*/
                for (var i=0;i<$scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
                }
			}
		);
	}
	
	//保存 
	$scope.save=function(){

        /*将富文本编辑器中的数据提取出来添加到entity.goodsDesc中*/
        $scope.entity.goodsDesc.introduction=editor.html();

		var serviceObject;//服务层对象
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
                    alert("保存商品成功");
                   location.href='goods.html';
				}else{
					alert(response.message);
				}
			}		
		);				
	}

	/*新增*/
	$scope.add=function(){
		/*将富文本编辑器中的数据提取出来添加到entity.goodsDesc中*/
		$scope.entity.goodsDesc.introduction=editor.html();

		goodsService.add( $scope.entity  ).success(
			function(response){
				if(response.success){
					alert("新增商品成功");
					$scope.entity={};/*清空页面*/

					/*清空富文本编辑器*/
					editor.html("");
				}else{
					alert(response.message);
				}
			}
		);
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	/*上传文件*/
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(
			function (response) {
				if (response.success){
					$scope.image_entity.url=response.message;
				}else {
					alert(response.message);
				}
			}
		)
	};

	/*设置当前页面中$scope.entity的结构*/
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};

	/*将当前上传的图片加入图片列表itemImages中*/
	$scope.add_image_entity=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity)
	}

	/*从当前图片列表中移除此图片*/
	$scope.remove_image_entity=function (index) {
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}

	/*查询一级分类列表*/
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(
			function (response) {
				$scope.itemCat1List=response;
			}
		)
	};

	/*变量监控,变化时查询二级分类*/
	$scope.$watch('entity.goods.category1Id',function (newValue, oldValue) {
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.itemCat2List=response;
			}
		)
	});

	$scope.$watch('entity.goods.category2Id',function (newValue, oldValue) {
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.itemCat3List=response;
			}
		)
	});

	/*读取模板ID*/
	$scope.$watch('entity.goods.category3Id',function (newValue, oldValue) {
		itemCatService.findOne(newValue).success(
			function (response) {
				$scope.entity.goods.typeTemplateId=response.typeId;
			}
		)
	});

	/*监控模板ID，变化后查询对应模板，并将其品牌列表属性转化为JSON数据,将其规格选项列表*/
	$scope.$watch('entity.goods.typeTemplateId',function (newValue, oldValue) {
		typeTemplateService.findOne(newValue).success(
			function (response) {
				$scope.typeTemplate=response;/*模板对象*/
				/*品牌列表类型转化*/
				$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
				/*扩展属性*/
				if ($location.search()['id']==null){
					//新增时执行此语句,使用模板生成对应的扩展属性,修改时不执行,避免模板覆盖原有数据
					$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
				}
			}
		);

		/*读取规格*/
		typeTemplateService.findSpecList(newValue).success(
			function (response) {
				$scope.specList=response;
			}
		)
	});

	/*更新规格选项*/
	$scope.updateSpecAttribute=function ($event,name,value) {
		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		if (object!=null){
			/*勾选*/
			if ($event.target.checked){
				object.attributeValue.push(value);
			}else {
				/*取消勾选*/
				object.attributeValue.splice(object.attributeValue.indexOf(value,1));/*移除选项*/
				/*选项全部取消后移除此条记录*/
				if (object.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}

		}else {
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
	};

	/*创建SKU列表*/
    $scope.createItemList=function () {
	    /*网络制式,颜色等属性封装成一个spec对象,其他属性无论如何都存在所以单独列*/
        $scope.entity.itemList=[{spec:{},price:0,num:0,status:'0',isDefault:'0'}];/*定义存储SKU(库存单位)数据的变量并初始化*/

        /*获取用户选择的规格选项列表*/
        var items = $scope.entity.goodsDesc.specificationItems;
        //alert(JSON.stringify(items));

        for(var i=0;i<items.length;i++){
            $scope.entity.itemList = addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
        }
    };

    /*根据参数生成新的SKU列表*/
    addColumn=function (list,columnName,columnValues) {

        //alert(columnValues);
        /*新的SKU列表*/
        var newList=[];

        for (var i=0;i<list.length;i++){
            var oldRow = list[i];
            for (var j=0;j<columnValues.length;j++){
                /*深克隆原SKU的Row*/
                var newRow =JSON.parse(JSON.stringify(oldRow));
                newRow.spec[columnName]=columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }
    /*设置变量存储商品状态,用于在前端调用,构建良好的用户体验*/
    $scope.status=['未审核','已审核','审核未通过','已关闭'];

    //商品分类列表
    $scope.itemCatList=[];
    /*查询商品分类列表*/
    $scope.findItemCatList=function () {
		itemCatService.findAll().success(
			function (response) {
				for (var i=0;i<response.length;i++){
					$scope.itemCatList[response[i].id]=response[i].name;
				}
			}
		)
	};

	/**
	 *判断某规格与规格选项是否应该被勾选
	 * @param specName 规格名
	 * @param optionName 选项名
	 * @returns {boolean} 是否选择
	 */
    $scope.checkAttributeValue=function (specName,optionName) {
		var items = $scope.entity.goodsDesc.specificationItems;
		//调用baseController中的方法
		var object= $scope.searchObjectByKey(items,'attributeName',specName);
		if (object!=null){
			if (object.attributeValue.indexOf(optionName)>=0){
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
});	
