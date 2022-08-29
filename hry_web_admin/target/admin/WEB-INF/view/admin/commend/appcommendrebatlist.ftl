 <!-- Copyright:    -->
 <!-- AppCommendRebatList.html     -->
 <!-- @author:      tianpengyu  -->
 <!-- @version:     V1.0             -->
 <!-- @Date:        2018-06-25 19:50:03      -->

<#include "/base/base.ftl">
  <div class="centerRowBg centerRowBg_admin">
 <div class="row">
     <div class="col-lg-12">
         <h3 class="page-header">返佣派发记录</h3>
     </div>
 </div>

 <div class="row">
 	<div class="col-md-12">
 		<div class="_params">
            <form id="table_query_form">
                <div class="col-md-2 column">
                    <div class="form-group">
                        <label for="email">邮箱</label>
                        <input type="text" class="form-control" tablesearch name="email"/>
                    </div>
                </div>
                <div class="col-md-2 column">
                    <div class="form-group">
                        <label for="mobilePhone">手机号</label>
                        <input type="text" class="form-control" tablesearch name="mobilePhone"/>
                    </div>
                </div>

                <div class="col-md-2 column">
                    <div class="form-group">
                        <label for="email">返佣币种</label>
                        <input type="text" class="form-control" tablesearch name="coinCode"/>
                    </div>
                </div>
                <div class="col-md-2 column">
                    <div class="form-group">
                        <label for="email">返佣时间之前</label>
                        <input type="text" class="form-control" name="created_LT" id="entrustTime_LT" readonly tablesearch
                               data-date-format="yyyy-mm-dd" data-min-view="month"
                               value="${(info.birthday?string("yyyy-MM-dd"))!}">
                    </div>
                </div>


                <div class="col-md-2 column">
                    <div style="margin-top: 26px;">
                        <button type="button" id="table_query" class="btn  btn-primary"><i class="glyphicon glyphicon-search"></i>查询</button>
                        <button type="button" id="table_reset" class="btn  btn-primary"><i class="glyphicon glyphicon-refresh"></i>重置</button>
                    </div>
                </div>
            </form>
 			
 		</div>
 	    <div id="toolbar">

 	    </div>
 	    <table id="table"
 	           data-toolbar="#toolbar"
 	           data-show-refresh="true"
 	           data-show-columns="false"
 	           data-show-export="false"
 	           data-search="false"
 	           data-detail-view="false"
 	           data-minimum-count-columns="2"
 	           data-pagination="true"
 	           data-id-field="id"
 	           data-page-list="[10, 25, 50, 100]"
 	           data-show-footer="false"  
 	           data-side-pagination="server"
 	           >
 	    </table>
     </div>
 </div>


 </div>

 <script type="text/javascript">
 seajs.use(["js/admin/commend/AppCommendRebat","js/base/HryDate"],function(o,HryDate){
     HryDate.init();
 	o.list();
 });

 </script>

