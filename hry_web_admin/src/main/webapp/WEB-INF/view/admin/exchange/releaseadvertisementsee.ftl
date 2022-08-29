 <!-- Copyright:    -->
 <!-- ReleaseAdvertisementSee.html     -->
 <!-- @author:      denghf  -->
 <!-- @version:     V1.0             -->
 <!-- @Date:        2018-10-29 13:36:05      -->

 <#include "/base/base.ftl">
  <div class="centerRowBg centerRowBg_admin">
 <div class="row">
 <div class="col-md-12">
     <h3 class="page-header">ReleaseAdvertisement--See  <button type="button"  class="btn btn-info-blue pull-right"  onclick="loadUrl('${ctx}/v.do?u=/admin/exchange/releaseadvertisementlist')" > <i class="fa fa-mail-forward"></i>返回</button></h3>
 </div>
 </div>


 <form id="form" >
 <div class="row">
 	<div class="col-md-4 column">
		<div class="form-group">
			 <label for="customerId">用户id</label>
			 <input type="text" class="form-control" name="customerId" id="customerId" value="${model.customerId}" disabled/>
		</div>
		<div class="form-group">
			 <label for="accountId">虚拟账户id</label>
			 <input type="text" class="form-control" name="accountId" id="accountId" value="${model.accountId}" disabled/>
		</div>
		<div class="form-group">
			 <label for="coinName">币种名称</label>
			 <input type="text" class="form-control" name="coinName" id="coinName" value="${model.coinName}" disabled/>
		</div>
		<div class="form-group">
			 <label for="coinCode">币种代码</label>
			 <input type="text" class="form-control" name="coinCode" id="coinCode" value="${model.coinCode}" disabled/>
		</div>
		<div class="form-group">
			 <label for="transactionMode">交易方式(1在线购买,2在线出售,3本地购买)</label>
			 <input type="text" class="form-control" name="transactionMode" id="transactionMode" value="${model.transactionMode}" disabled/>
		</div>
		<div class="form-group">
			 <label for="nationality">国籍</label>
			 <input type="text" class="form-control" name="nationality" id="nationality" value="${model.nationality}" disabled/>
		</div>
		<div class="form-group">
			 <label for="isFixed">固定价格是否开启 0否 1是</label>
			 <input type="text" class="form-control" name="isFixed" id="isFixed" value="${model.isFixed}" disabled/>
		</div>
		<div class="form-group">
			 <label for="tradeMoney">交易金额</label>
			 <input type="text" class="form-control" name="tradeMoney" id="tradeMoney" value="${model.tradeMoney}" disabled/>
		</div>
		<div class="form-group">
			 <label for="premium">溢价</label>
			 <input type="text" class="form-control" name="premium" id="premium" value="${model.premium}" disabled/>
		</div>
		<div class="form-group">
			 <label for="paymentTerm">付款期限</label>
			 <input type="text" class="form-control" name="paymentTerm" id="paymentTerm" value="${model.paymentTerm}" disabled/>
		</div>
		<div class="form-group">
			 <label for="payType">交易类型(1银行转账,2支付宝,3微信支付)</label>
			 <input type="text" class="form-control" name="payType" id="payType" value="${model.payType}" disabled/>
		</div>
		<div class="form-group">
			 <label for="tradeMoneyMix">最低交易金额</label>
			 <input type="text" class="form-control" name="tradeMoneyMix" id="tradeMoneyMix" value="${model.tradeMoneyMix}" disabled/>
		</div>
		<div class="form-group">
			 <label for="tradeMoneyMax">最高交易金额</label>
			 <input type="text" class="form-control" name="tradeMoneyMax" id="tradeMoneyMax" value="${model.tradeMoneyMax}" disabled/>
		</div>
		<div class="form-group">
			 <label for="remark">备注</label>
			 <input type="text" class="form-control" name="remark" id="remark" value="${model.remark}" disabled/>
		</div>
		<div class="form-group">
			 <label for="isAutomatic">固定价格是否开启 0否 1是</label>
			 <input type="text" class="form-control" name="isAutomatic" id="isAutomatic" value="${model.isAutomatic}" disabled/>
		</div>
		<div class="form-group">
			 <label for="automaticReply">自动回复内容</label>
			 <input type="text" class="form-control" name="automaticReply" id="automaticReply" value="${model.automaticReply}" disabled/>
		</div>
		<div class="form-group">
			 <label for="isSecurity">是否启用安全选项 0否 1是</label>
			 <input type="text" class="form-control" name="isSecurity" id="isSecurity" value="${model.isSecurity}" disabled/>
		</div>
		<div class="form-group">
			 <label for="isBeTrusted">是否启用仅限受信任的交易者 0否 1是</label>
			 <input type="text" class="form-control" name="isBeTrusted" id="isBeTrusted" value="${model.isBeTrusted}" disabled/>
		</div>
		<div class="form-group">
			 <label for="transactionNum">交易次数</label>
			 <input type="text" class="form-control" name="transactionNum" id="transactionNum" value="${model.transactionNum}" disabled/>
		</div>
		<div class="form-group">
			 <label for="saasId">saasId</label>
			 <input type="text" class="form-control" name="saasId" id="saasId" value="${model.saasId}" disabled/>
		</div>
		<div class="form-group">
			 <label for="payTypeRemake">上传的资料</label>
			 <input type="text" class="form-control" name="payTypeRemake" id="payTypeRemake" value="${model.payTypeRemake}" disabled/>
		</div>
		<div class="form-group">
			 <label for="status">广告状态 0关闭 1开启</label>
			 <input type="text" class="form-control" name="status" id="status" value="${model.status}" disabled/>
		</div>
		<div class="form-group">
			 <label for="state">广告状态为0有效，1作废</label>
			 <input type="text" class="form-control" name="state" id="state" value="${model.state}" disabled/>
		</div>
		<div class="form-group">
			 <label for="bankId">支付信息记录id</label>
			 <input type="text" class="form-control" name="bankId" id="bankId" value="${model.bankId}" disabled/>
		</div>
		<div class="form-group">
			 <label for="bankNumber">银行卡号</label>
			 <input type="text" class="form-control" name="bankNumber" id="bankNumber" value="${model.bankNumber}" disabled/>
		</div>
		<div class="form-group">
			 <label for="alipayId">支付信息记录id</label>
			 <input type="text" class="form-control" name="alipayId" id="alipayId" value="${model.alipayId}" disabled/>
		</div>
		<div class="form-group">
			 <label for="alipayAccount">支付宝账号</label>
			 <input type="text" class="form-control" name="alipayAccount" id="alipayAccount" value="${model.alipayAccount}" disabled/>
		</div>
		<div class="form-group">
			 <label for="alipayThingUrl">支付宝二维码</label>
			 <input type="text" class="form-control" name="alipayThingUrl" id="alipayThingUrl" value="${model.alipayThingUrl}" disabled/>
		</div>
		<div class="form-group">
			 <label for="wechatId">支付信息记录id</label>
			 <input type="text" class="form-control" name="wechatId" id="wechatId" value="${model.wechatId}" disabled/>
		</div>
		<div class="form-group">
			 <label for="wechatAccount">微信账号</label>
			 <input type="text" class="form-control" name="wechatAccount" id="wechatAccount" value="${model.wechatAccount}" disabled/>
		</div>
		<div class="form-group">
			 <label for="wechatThingUrl">微信二维码</label>
			 <input type="text" class="form-control" name="wechatThingUrl" id="wechatThingUrl" value="${model.wechatThingUrl}" disabled/>
		</div>
		<div class="form-group">
			 <label for="coinNumMin">数量min</label>
			 <input type="text" class="form-control" name="coinNumMin" id="coinNumMin" value="${model.coinNumMin}" disabled/>
		</div>
		<div class="form-group">
			 <label for="coinNumMax">数量max</label>
			 <input type="text" class="form-control" name="coinNumMax" id="coinNumMax" value="${model.coinNumMax}" disabled/>
		</div>
	</div>
 </div>

 </form>
 
 </div>

 <script type="text/javascript">
 seajs.use("js/admin/exchange/ReleaseAdvertisement",function(o){
 	o.see();
 });
 </script>




