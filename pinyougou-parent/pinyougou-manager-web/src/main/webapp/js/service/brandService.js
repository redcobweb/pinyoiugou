/*品牌管理服务层*/
app.service("brandService",function ($http) {
    /*查询全部数据*/
    this.findAll=function () {
        return $http.get('../brand/findAll.do');
    }
    /*分页查询数据*/
    this.findPage=function (page,size) {
        return $http.get('../brand/findPage.do?page='+page+'&size='+size);
    }
    /*新增数据*/
    this.add=function (entity) {
        return $http.post('../brand/add.do',entity);
    }
    /*更新数据*/
    this.update=function (entity) {
        return $http.post('../brand/update.do',entity);
    }
    /*查询单个数据*/
    this.findOne=function (id) {
        return $http.get('../brand/findOne.do?id='+id);
    }
    /*根据id数组删除数据*/
    this.delete=function (selectIds) {
        return $http.get('../brand/delete.do?ids='+selectIds);
    }
    /*条件查询数据*/
    this.search=function (page,size,searchEntity) {
        return $http.post('../brand/search.do?page='+page+'&size='+size,searchEntity);
    }
    
    /*根据下拉列表要求查询*/
    this.selectOptionList=function () {
        return $http.get('../brand/selectOptionList.do');
    }

});