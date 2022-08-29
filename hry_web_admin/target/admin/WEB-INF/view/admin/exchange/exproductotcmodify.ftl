<!-- Copyright:    -->
 <!-- ExProductAdd.html     -->
 <!-- @author:      liushilei  -->
 <!-- @version:     V1.0             -->
 <!-- @Date:        2018-06-12 15:44:37      -->

<#include "/base/base.ftl">
<div class="centerRowBg centerRowBg_admin">
<div class="row">
    <div class="col-md-12">
        <h3 class="page-header">修改OTC币种
            <button type="button" class="btn  btn-info-blue pull-right"
                    onclick="loadUrl('${ctx}/v.do?u=/admin/exchange/exproductotclist')"><i class="fa fa-mail-forward"></i>返回
            </button>
        </h3>
    </div>
</div>


<form id="form">
	<div class="row">
        <div class="col-md-4 column">
            <div class="form-group">
                <label for="name">币种</label>
                <input type="text" class="form-control" id="coinCode" name="coinCode" value="${model.coinCode}" readonly/>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-4 column">
            <div class="form-group">
                <label for="eatFee">吃单交易手续费</label>
                <input type="text" class="form-control" name="eatFee" value="${model.eatFee}" id="name" />
            </div>
        </div>
        <div class="col-md-4 column">
            <input type="hidden" id="eatFeeTypeModel" value="${model.eatFeeType}"/>
            <div class="form-group">
                <label for="eatFeeType">吃单交易手续费类型</label>
                <select id="eatFeeType" name="eatFeeType">
                	<option value="0">固定费率</option>
                	<option value="1">百分比汇率</option>
                </select>
            </div>
        </div>
    </div>
	<div class="row">
        <div class="col-md-4 column">
            <div class="form-group">
                <label for="coinPercent">币价格百分比</label>
                <input type="text" class="form-control" name="coinPercent" value="${model.coinPercent}" id="name" />
            </div>
        </div>
        <div class="col-md-4 column">
            <input type="hidden" id="otcStateModel" value="${model.otcState}"/>
            <div class="form-group">
                <label for="otcState">是否开启</label>
                    <select id="otcState" name="otcState">
                        <option value="0">否</option>
                        <option value="1">是</option>
                    </select>
                </select>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-4 column">
            <div class="form-group">
                <label for="otcMinPercent">otc限价最低百分比</label>
                <input type="text" class="form-control" name="otcMinPercent" value="${model.otcMinPercent}" id="name" />
            </div>
        </div>
        <div class="col-md-4 column">

            <div class="form-group">
                <label for="otcMaxPercent">otc限价最高百分比</label>
                <input type="text" class="form-control" name="otcMaxPercent" value="${model.otcMaxPercent}"id="coinCode" />
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-2 column">
            <button type="button" id="modifySubmit" class="btn btn-blue btn-block">提交</button>
        </div>
    </div>

</form>
</div>
<script type="text/javascript">
    seajs.use(["js/admin/exchange/ExProduct","js/base/HryUpload","js/base/HryDate","js/base/HrySelect"], function (o,HryUpload,HryDate,HrySelect) {
        HryUpload.upload();
        HryDate.init();
        HrySelect.init();
        o.otcModify();
    });
</script>




