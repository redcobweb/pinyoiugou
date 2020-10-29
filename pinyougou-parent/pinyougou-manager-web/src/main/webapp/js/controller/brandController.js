/*品牌管理控制层*/
app.controller('brandController',function ($scope,$controller,brandService) {
    /*继承baseController.js*/
    /*{$scope:$scope}:伪继承,让当前scope等于父类scope实现伪继承*/
    $controller('baseController',{$scope:$scope});

    /*查询全部数据*/
    $scope.findAll=function() {
        brandService.findAll().success(
            function (response) {
                $scope.list=response;
            }
        );
    }

    /*分页方法*/
    $scope.findPage=function (page,size) {
        brandService.findPage(page,size).success(
            function (response) {
                $scope.list=response.rows;//显示当前页记录
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }
    /*保存方法*/
    $scope.save=function () {
        // var methodName = 'add';//方法名
        // if ($scope.entity.id!=null){
        // 	methodName='update'
        // }
        var object=null;
        if ($scope.entity.id!=null){
            object=brandService.update($scope.entity);
        }else {
            object=brandService.add($scope.entity);
        }
        object.success(
            function (response) {
                if (response.success){
                    $scope.reloadList();//刷新
                }else {
                    alert(response.message);
                }
            }
        );
    }
    /*根据id查询*/
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        );
    }
    /*根据id数组删除*/
    $scope.deleteBrands=function () {
        brandService.delete($scope.selectIds).success(
            function (response) {
                if (response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message);
                }
            }
        );
    }
    /*初始化查询条件*/
    $scope.searchEntity={};
    /*条件查询*/
    $scope.search=function (page,size) {
        brandService.search(page,size,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;//显示当前页记录
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        )
    }

});