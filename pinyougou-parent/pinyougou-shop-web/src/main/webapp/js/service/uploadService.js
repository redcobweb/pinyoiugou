app.service('uploadService',function ($http) {
    /*上传文件*/
    this.uploadFile=function () {
        var formData = new FormData();/*html5指定类,上传文件时用*/
        formData.append('file',file.files[0]);/*file:文件上传框的name,既取页面name为file的第一个元素,添加到formData中*/

        /*向后端发请求的方式有两种
        * 1.一般请求
        * $http.get('请求路径');
        *
        * 2.文件上传请求
        * $http({
        *   url:路径
        *   method:get
        *   ...
        * });
        * */

        return $http({
            url:'../upload.do',/*请求路径*/
            method:'post',/*请求方式*/
            data:formData,/*传递的数据*/

            /*文件上传时配置*/
            headers:{'Content-Type':undefined},/*数据的格式,默认情况下是JSON数据,上传文件时须指定为undefined*/
            transformRequest:angular.identity/*通过angularJS,对表单进行二进制序列化*/
        });
    }
})