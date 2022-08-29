 <!-- Copyright:    -->
 <!-- ExLendConfigList.html     -->
 <!-- @author:      HeC  -->
 <!-- @version:     V1.0             -->
 <!-- @Date:        2018-10-18 14:47:26      -->

<#include "/base/base.ftl">
 <div class="centerRowBg centerRowBg_admin">
 <div class="row">
     <div class="col-lg-12">
         <h3 class="page-header">杠杆配置</h3>
     </div>
 </div>

 <div class="row">
 	<div class="col-md-12">
 		<form id="table_query_form">
        <div class="row">
            <div class="col-md-2 column">
                <div class="form-group">
                    <label for="coinCode">交易区</label>
                		    <@HrySelect url="${ctx}/lend/exlendconfig/getPriCoin"  itemvalue="coin"  itemname="coin"  name="coin"  id="coin"></@HrySelect>
                </div>
            </div>
            <div class="col-md-2 column">
                <div style="margin-top: 26px;">
                    <button type="button" id="table_query" class="btn  btn-primary"><i
                            class="glyphicon glyphicon-search"></i>查询
                    </button>
                    <button type="button" id="table_reset" class="btn  btn-primary"><i
                            class="glyphicon glyphicon-refresh"></i>重置
                    </button>
                </div>
            </div>
        </div>
        </form>
 	    <div id="toolbar">
             <button id="add" class="btn btn-info-blue" onclick="loadUrl('${ctx}/v.do?u=/admin/lend/exlendconfigadd')" >
 	            <i class="glyphicon glyphicon-plus"></i>添加
 	        </button>
             <button id="modify" class="btn btn-info-blue" >
 	            <i class="glyphicon glyphicon-edit"></i>编辑
 	        </button>
             <button id="remove" class="btn btn-info-blue" >
 	            <i class="glyphicon glyphicon-remove"></i>删除
 	        </button>
 	    </div>
 	    <table id="table"
 	           data-toolbar="#toolbar"
 	           data-show-refresh="true"
 	           data-show-columns="true"
 	           data-show-export="true"
 	           data-search="true"
 	           data-detail-view="false"
 	           data-minimum-count-columns="2"
 	           data-pagination="true"
 	           data-id-field="id"
 	           data-page-list="[10, 25, 50, 100, ALL]"
 	           data-show-footer="false"  
 	           data-side-pagination="server"
 	           >
 	    </table>
     </div>
 </div>


 </div>

 <script type="text/javascript">
 seajs.use(["js/admin/lend/ExLendConfig", "js/base/HrySelect", "js/base/HryDate"],function(o, HrySelect, HryDate){
     HrySelect.init();
     HryDate.init();
 	o.list();
 });

 </script>

