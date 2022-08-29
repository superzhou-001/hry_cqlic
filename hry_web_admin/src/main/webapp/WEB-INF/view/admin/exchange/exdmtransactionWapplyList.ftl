 <!-- Copyright:    -->
 <!-- ExDmTransactionList.html     -->
 <!-- @author:      liushilei  -->
 <!-- @version:     V1.0             -->
 <!-- @Date:        2018-06-13 13:59:41      -->

<#include "/base/base.ftl">
<div class="centerRowBg centerRowBg_admin">
 <div class="row">
     <div class="col-lg-12">
         <h3 class="page-header">提币申请审核</h3>
     </div>
 </div>
 <div class="row">
     <div class="col-md-12">
         <div class="row">
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
                            <label for="surName">用户姓氏</label>
                            <input type="text" class="form-control" tablesearch name="surname"/>
                        </div>
                    </div>
                    <div class="col-md-2 column">
                        <div class="form-group">
                            <label for="trueName">用户名字</label>
                            <input type="text" class="form-control" tablesearch name="trueName"/>
                        </div>
                    </div>

                    <div class="col-md-2 column">
                        <div class="form-group">
                            <label for="inAddress">转入钱包地址</label>
                            <input type="text" class="form-control" tablesearch name="inAddress"/>
                        </div>
                    </div>
                    <div class="col-md-2 column">
                        <div class="form-group">
                            <label for="transactionNum">交易订单号</label>
                            <input type="text" class="form-control" tablesearch name="transactionNum"/>
                        </div>
                    </div>

                    <div class="col-md-2 column">
                        <div class="form-group">
                            <label for="surName">币种类型</label>
                        <@HrySelect url="${ctx}/exchange/exproduct/findall"  itemvalue="coinCode"  itemname="coinCode"  name="coinCode"  id="coinCode"   > </@HrySelect>
                        </div>
                    </div>
                    <div class="col-md-2 column">
                        <div class="form-group">
                            <label for="inAddress">开始时间</label>
                            <input type="text" class="form-control" name="created_GT" id="created_GT" readonly tablesearch  data-date-format="yyyy-mm-dd" data-min-view="month" value="${(info.birthday?string("yyyy-MM-dd"))!}">
                        </div>
                    </div>
                    <div class="col-md-2 column">
                        <div class="form-group">
                            <label for="sellEntrustNum">结束时间</label>
                            <input type="text" class="form-control" name="created_LT" id="created_LT" readonly tablesearch  data-date-format="yyyy-mm-dd" data-min-view="month" value="${(info.birthday?string("yyyy-MM-dd"))!}">
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
         </div>
         <div id="toolbar">
             <#--<button id="post" class="btn btn-info-blue" >
                 <i class="glyphicon glyphicon-share"></i>通过
             </button>
             <button id="stop" class="btn btn-info-blue" >
                 <i class="glyphicon glyphicon-share"></i>驳回
             </button>
             <button id="allpost" class="btn btn-info-blue" >
                 <i class="glyphicon glyphicon-share"></i>批量通过
             </button>
             <button id="allstop" class="btn btn-info-blue" >
                 <i class="glyphicon glyphicon-share"></i>批量驳回
             </button>-->
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
<!-- 提币确认弹窗 -->
    <div class="hide" id="postDiv">
        <input type="hidden" id="orderId">
        <div class="row" id="postcontent" style="margin-left: 60px;">
        </div>
        <div class="row">
            <div class="col s10 ">
                <div class="row input-field" style="margin-left: 60px;">
                    <input type="text" name="google_code" id="google_code"  /> <label for="google_code">谷歌验证码</label>

                </div>
                <div class="row" style="margin-left: 60px;">
                    <button id="confirmpost" class="btn btn-info-blue" >
                        <i class="glyphicon glyphicon-share"></i>通过
                    </button>
                </div>
            </div>
        </div>

    </div>

 <!-- 提币单个驳回弹窗 -->
    <div class="hide" id="stopDiv">
        <input type="hidden" id="stopid">
        <div class="row" id="stopcontent" style="margin-left: 60px;">
        </div>
        <div class="row">
            <div class="col s10 ">

                <div class="row" style="margin-left: 60px;">
                    <button id="confirmstop" class="btn btn-info-blue" >
                        <i class="glyphicon glyphicon-share"></i>驳回
                    </button>
                </div>
            </div>
        </div>

    </div>

 <!-- 提币单个驳回弹窗 -->
    <div class="hide" id="stopallDiv">
        <input type="hidden" id="stopallid">
        <div class="row" id="stopallcontent" style="margin-left: 60px;">
        </div>
        <div class="row">
            <div class="col s10 ">

                <div class="row" style="margin-left: 60px;">
                    <button id="confirmstopall" class="btn btn-info-blue" >
                        <i class="glyphicon glyphicon-share"></i>驳回
                    </button>
                </div>
            </div>
        </div>

    </div>

 <script type="text/javascript">
 seajs.use(["js/admin/exchange/ExDmTransaction","js/base/HrySelect","js/base/HryDate"],function(o,HrySelect,HryDate){
 	o.wapplylist();
     HryDate.init();
     HrySelect.init();
 });

 </script>

