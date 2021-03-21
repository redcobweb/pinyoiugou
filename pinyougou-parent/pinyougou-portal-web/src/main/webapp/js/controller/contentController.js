app.controller('contentController',function ($scope, contentService) {

    //所有广告
    $scope.contentList=[];

    /*根据分类ID查询出对应广告,存入广告列表数组对应的元素中*/
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId]=response;
            }
        )
    };


});