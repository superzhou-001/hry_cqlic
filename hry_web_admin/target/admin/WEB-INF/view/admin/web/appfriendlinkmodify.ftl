 <!-- Copyright:    -->
 <!-- AppFriendLinkModify.html     -->
 <!-- @author:      liushilei  -->
 <!-- @version:     V1.0             -->
 <!-- @Date:        2018-06-20 11:56:58      -->

 <#include "/base/base.ftl">
  <div class="centerRowBg centerRowBg_admin">
 <div class="row">
 <div class="col-md-12">
     <h3 class="page-header">修改友情连接  <button type="button"  class="btn  btn-info-blue pull-right"  onclick="loadUrl('${ctx}/v.do?u=/admin/web/appfriendlinklist')" > <i class="fa fa-mail-forward"></i>返回</button></h3>
 </div>
 </div>


 <form id="form" >
 <input type="hidden" class="form-control" name="id" id="id" value="${model.id}"/>
     <div class="row">
         <div class="col-md-4 column">
             <div class="form-group">
                 <label for="name">友情链接名称</label>
                 <input type="text" class="form-control" name="name" id="name" value="${model.name}" />
             </div>
             <div class="form-group">
                 <label for="linkUrl">友情链接地址</label>
                 <input type="text" class="form-control" name="linkUrl" id="linkUrl"  value="${model.linkUrl}"/>
             </div>
             <div class="form-group">
                 <label for="status">状态</label>
			 <@HrySelect key="articlestatus"  name="status"  id="status"  value="${model.status}" > </@HrySelect>
             </div>
             <div class="form-group">
                 <label for="picturePath">上传图标</label>
                 <div class="hryUpload" style="width: 100%">
                     <div class="hry_inputBox">
                         <input class="form-control hryUpload_filePath " type="hidden" name="picturePath" value="${model.picturePath}" >
                         <button type="button" class="btn btn-info btn-block">上传图片</button>
                     </div>
                     <div class="hry_imgBox">

                     </div>
                 </div>
             </div>
         </div>
     </div>


 <div class="row">
	 <div class="col-md-2 column">
	 	<button type="button" id="modifySubmit" class="btn  btn-info-blue btn-warning" >提交</button>
	 </div>
 </div>
 </form>
  </div>

 <script type="text/javascript">
	 seajs.use(["js/admin/web/AppFriendLink","js/base/HryUpload"],function(o,HryUpload){
         HryUpload.upload();
 	o.modify();
 });
 </script>






