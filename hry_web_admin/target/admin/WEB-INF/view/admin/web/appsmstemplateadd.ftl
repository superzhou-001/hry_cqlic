 <!-- Copyright:    -->
 <!-- AppSmsTemplateAdd.html     -->
 <!-- @author:      zhouming  -->
 <!-- @version:     V1.0             -->
 <!-- @Date:        2018-10-25 17:56:24      -->

<#include "/base/base.ftl">
 <div class="centerRowBg centerRowBg_admin">
<div class="row">
    <div class="col-md-12">
        <h3 class="page-header">添加短信模板  <button type="button"  class="btn btn-info-blue pull-right"  onclick="loadUrl('${ctx}/language.do?u=/admin/web/appsmstemplatelist')" > <i class="fa fa-mail-forward"></i>返回</button></h3>
    </div>
</div>


<form id="form" >

<div class="row">
    <div class="col-md-10 column">
        <div class="form-group">
            <label for="messageCategory">模板语种</label>
		  	<@HrySelect key="sysLanguage" name="messageCategory" id="messageCategory" ></@HrySelect>
        </div>
		<div class="form-group">
			 <label for="mkey">模板KEY</label>
			<#-- <input type="text" class="form-control" name="mkey" id="mkey" />-->
			<@HrySelect key="smsTemplateKey" name="mkey" id="mkey" ></@HrySelect>
		</div>
		<div class="form-group">
			 <label for="name">模板名称</label>
			 <input type="text" class="form-control" name="name" id="name" />
		</div>
        <div class="form-group">
            <label for="describes">模板内容</label>
            <div ueditorid="ueditor_content" class="appconfig ueditorparent">
                <textarea id="ueditor_content" name="describes" class="ueditor" style="margin: 0px; width: 900px; height: 120px;"></textarea>
            </div>
        </div>
		<div class="form-group">
			 <label for="remark">模板备注</label>
			 <input type="text" class="form-control" name="remark" id="remark" />
		</div>
	</div>
</div>

<div class="row">
	<div class="col-md-2 column">
		<button type="button" id="addSubmit" class="btn btn-info-blue btn-block" >提交</button>
	</div>
</div>

</form>

</div>
<script type="text/javascript">
seajs.use("js/admin/web/AppSmsTemplate",function(o){
	o.add();
});
</script>




