package hry.admin.exchange.service.impl;

import com.alibaba.fastjson.JSONObject;
import hry.admin.exchange.model.ExRobot;
import hry.admin.exchange.service.ConmonApiService;
import hry.admin.web.model.AppConfig;
import hry.bean.JsonResult;
import hry.listener.ServerManagement;
import hry.redis.common.utils.RedisService;
import hry.util.RSAUtil;
import hry.util.SpringUtil;
import hry.util.UUIDUtil;
import hry.util.httpRequest.HttpConnectionUtil;
import hry.util.rsa.FXHParam;
import hry.util.sys.ContextUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


public class HryCurrentPriceByApiRunable implements Callable<JsonResult> {
	private static Logger logger = Logger.getLogger(HryCurrentPriceByApiRunable.class);
	
	private String coinCode;
	private String fixPriceCoinCode;


	public HryCurrentPriceByApiRunable(String coinCode,String fixPriceCoinCode){
		this.coinCode=coinCode;
		this.fixPriceCoinCode= fixPriceCoinCode;
	}


	@Override
	public JsonResult call() throws Exception {
		RedisService redisService = SpringUtil.getBean("redisService");
		JsonResult jsonResult = new JsonResult();

		FXHParam fxhParam = new FXHParam();
		fxhParam.setCoinCode(coinCode);
		fxhParam.setFixCoin(fixPriceCoinCode);

		JSONObject.parseArray(redisService.get("configCache:klinedataconfig")).forEach(app -> {
			AppConfig appConfig = JSONObject.parseObject(app.toString(),AppConfig.class);
			switch (appConfig.getConfigkey()){
				case "klinedataurl" : fxhParam.setPayUrl(appConfig.getValue() ); break;
				case "businessNumber" : fxhParam.setCompanyCode(appConfig.getValue()); break;
				case "publickey" : fxhParam.setPublicKey(appConfig.getValue()); break;
				case "privatekey" : fxhParam.setPrivateKey(appConfig.getValue()); break;
			}
		});

		Map<String, Object> map = new HashMap<>(16);
		map.put("ordernumber", UUIDUtil.getUUID());
		map.put("belonged", "?????????6.0");
		map.put("frontWord", fxhParam.getCoinCode());
		map.put("afterWord", fxhParam.getFixCoin());

		try {
			String sign = RSAUtil.encryptByPrivateKey(JSONObject.toJSONString(map), fxhParam.getPrivateKey());
			Map<String, String> paramMap = new HashMap<>(16);
			paramMap.put("companyCode", fxhParam.getCompanyCode());
			paramMap.put("sign", sign);
			//paramMap.put("sign", JSONObject.toJSONString(map));

			logger.error("????????????:"+paramMap);
			String returnMsg = HttpConnectionUtil.doPostQuery(fxhParam.getPayUrl(), paramMap);
			logger.error("????????????"+returnMsg);
			if(StringUtils.isEmpty(returnMsg)){
				jsonResult.setMsg("????????????");
				jsonResult.setSuccess(false);
			}else {
				JSONObject jsonObject = JSONObject.parseObject(returnMsg);
				if("8888".equals(jsonObject.getString("resultCode"))){
					String price = RSAUtil.decryptByPublicKey(jsonObject.getString("data"), fxhParam.getPublicKey());
					//String price = jsonObject.getString("data");
					logger.error("???????????????"+price);

					jsonResult.setSuccess(true);
					jsonResult.setObj(price);
					jsonResult.setMsg("????????????");
				}else {
					jsonResult.setSuccess(false);
					jsonResult.setMsg("??????????????????????????????:"+jsonObject.getString("reason"));
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonResult;
	}
}
