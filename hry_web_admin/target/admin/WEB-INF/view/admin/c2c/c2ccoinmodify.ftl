 <!-- Copyright:    -->
 <!-- C2cCoinModify.html     -->
 <!-- @author:      liushilei  -->
 <!-- @version:     V1.0             -->
 <!-- @Date:        2018-06-13 12:06:01      -->

 <#include "/base/base.ftl">
<div class="centerRowBg centerRowBg_admin">
 <div class="row">
 <div class="col-md-12">
     <h3 class="page-header">修改c2c交易参数  <button type="button"  class="btn  btn-info-blue pull-right"  onclick="loadUrl('${ctx}/v.do?u=/admin/c2c/c2ccoinlist')" > <i class="fa fa-mail-forward"></i>返回</button></h3>
 </div>
 </div>


 <form id="form" >
 <input type="hidden" class="form-control" name="id" id="id" value="${model.id}"/>

     <div class="row">
         <div class="col-md-6 column">
             <div class="form-group">
                 <label for="coinCode">交易币种</label>
                 <@HrySelect url="${ctx}//exchange/exproduct/findall" readonly="readonly" itemvalue="coinCode"  itemname="coinCode"  name="coinCode"  id="coinCode" value="${model.coinCode}"  > </@HrySelect>
             </div>
         </div>
     </div>


     <div class="row">
         <div class="col-md-6 column">
             <div class="form-group">
                 <label for="isOpen">是否开启</label>
                 <@HrySelect key="yesno"  name="isOpen"  id="isOpen"  value="${model.isOpen}"   > </@HrySelect>
             </div>
         </div>
     </div>

     <div class="row">
         <div class="col-md-6 column">
             <div class="form-group">
                 <label for="buyPrice">买入价（两位小数）</label>
                 <input type="text" class="form-control" name="buyPrice" value="${model.buyPrice}"  id="buyPrice"/>
             </div>
         </div>



     </div>

     <div class="row">
         <div class="col-md-6 column">
             <div class="form-group">
                 <label for="sellPrice">卖出价（两位小数）</label>
                 <input type="text" class="form-control" name="sellPrice" value="${model.sellPrice}"  id="sellPrice"/>
             </div>
         </div>
     </div>


     <div class="row">
         <div class="col-md-6 column">
             <div class="form-group">
                 <label for="minCount">交易最小数量</label>
                 <input type="text" class="form-control" name="minCount" value="${model.minCount}"  id="minCount"/>
             </div>
         </div>



     </div>

     <div class="row">
         <div class="col-md-6 column">
             <div class="form-group">
                 <label for="maxCount">交易最大数量</label>
                 <input type="text" class="form-control" name="maxCount" value="${model.maxCount}"  id="maxCount"/>
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
 seajs.use(["js/admin/c2c/C2cCoin","js/base/HrySelect"],function(o,HrySelect){
     HrySelect.init();
 	o.modify();
 });
 </script>






