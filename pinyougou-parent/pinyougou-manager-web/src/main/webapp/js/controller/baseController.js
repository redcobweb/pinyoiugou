app.controller('baseController',function ($scope) {
    /*分页组件配置
* currentPage:当前页 totalItems:总记录数 itemsPerPage:每页记录数 perPageOption:分页选项 onChange:当页码变更后自动触发的方法*/
    $scope.paginationConf={
        currentPage:1,
        totalItems:10,
        itemsPerPage:10,
        perPageOptions:[10,20,30,40,50],
        onChange:function () {
            $scope.reloadList();
        }
    };
    /*刷新列表*/
    $scope.reloadList=function(){
        // $scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);

    }
    /*用户勾选id集合*/
    $scope.selectIds=[];
    /*更新勾选id集合*/
    $scope.updateSelectIds=function($event,id){
        if($event.target.checked){
            $scope.selectIds.push(id);//push方法向集合添加元素
        }else{
            var index=$scope.selectIds.indexOf(id);//查找值的位置
            $scope.selectIds.splice(index,1);
        }
    }

    $scope.jsonToString=function (jsonString, key) {
        var jsonString=JSON.parse(jsonString);
        var value=jsonString[0][key];
        for (var i=1;i<jsonString.length;i++){
            value=value+','+jsonString[i][key];

        }
        return value;
    }
})