package hry.otc.remote;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import hry.otc.releaseAdvertisement.model.*;
import hry.otc.releaseAdvertisement.service.*;
import hry.otc.remote.model.*;
import hry.util.OssUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.util.StringUtil;

import hry.account.fund.model.AppBankCard;
import hry.account.fund.model.AppTransaction;
import hry.bean.FrontPage;
import hry.bean.JsonResult;
import hry.bean.ObjectUtil;
import hry.customer.person.model.AppPersonInfo;
import hry.customer.user.model.AppCustomer;
import hry.exchange.account.model.ExDigitalmoneyAccount;
import hry.front.redis.model.UserRedis;
import hry.manage.remote.model.User;
import hry.redis.common.dao.RedisUtil;
import hry.redis.common.utils.RedisService;
import hry.trade.redis.model.ExDigitalmoneyAccountRedis;
import hry.util.PageFactory;
import hry.util.QueryFilter;
import hry.web.otc.service.impl.OtcServiceImpl;

public class RemoteAdvertisementServiceImpl implements RemoteAdvertisementService{

	@Resource
	private OtcServiceImpl otcService;
	@Resource
    private ReleaseAdvertisementService releaseAdvertisementService;
    @Resource
    private ExCoinExchangeRateService exCoinExchangeRateService;
    @Resource
    private OtcAppTransactionService otcAppTransactionService;
    @Resource
    private RedisService redisService;
    @Resource
    private TrustShieldService trustShieldService;
    @Resource
    private AppAppealService appAppealService;
    @Resource
    private ExCoinFeeService exCoinFeeService;
    @Resource
    private AppOrderSpeakService appOrderSpeakService;
    @Resource
    private OtcAppLogService otcAppLogService;
	@Resource
	private OtcCompletionRateService otcCompletionRateService;


    public static String transactionNum(String bussType) {
		SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssSSS");
		String time = format.format(new Date(System.currentTimeMillis()));
		String randomStr = RandomStringUtils.random(4, false, true);
		if (null == bussType || "".equals(bussType)) {
			bussType = "00";
		}
		return bussType + time + randomStr;
	}

    @Override
	public ExDigitalmoneyAccountRedis getCoinAccountRedis(Long userId, String coinCode){
    	// ???????????????????????????
 		RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
 		UserRedis userRedis = redisUtil.get(userId.toString());
 		// ?????????????????????????????????id
 		Map<String, Long> map = userRedis.getDmAccountId();
 		Set<String> keySet = map.keySet();
 		
 		ExDigitalmoneyAccountRedis dmAccount = null;
 		for (String key : keySet) {
 			if(key.equals(coinCode)){
 				RedisUtil<ExDigitalmoneyAccountRedis>  a = new RedisUtil<ExDigitalmoneyAccountRedis>(ExDigitalmoneyAccountRedis.class);
 				dmAccount = a.get(userRedis.getDmAccountId(key).toString());
 			}
 		}
 		return dmAccount;
    }
    
    @Override
	public int keepDecimalForCoin(String coinCode){
    	int keepDecimalForCoin = 0;
    	
		String productinfoListall = redisService.get("otc:coinCodeList");
		JSONArray parseArray = JSON.parseArray(productinfoListall);
		for(int k=0;k<parseArray.size();k++){
			JSONObject jo = JSON.parseObject(parseArray.get(k).toString());
			if(jo.getString("coinCode").equals(coinCode)){
				if (jo.getInteger("keepDecimalForCoin")!=null) {
					keepDecimalForCoin = jo.getInteger("keepDecimalForCoin");
				}
			}
		}
		return keepDecimalForCoin;
    }
    
    
    @Override
    public JsonResult getExchangeRateByCode(String baseCoinCode) {
        QueryFilter filter = new QueryFilter(ExCoinExchangeRate.class);
        filter.addFilter("baseCoinCode=", baseCoinCode);
        filter.setOrderby(" modified desc");
        ExCoinExchangeRate exCoinExchangeRate = exCoinExchangeRateService.get(filter);
        if(exCoinExchangeRate != null && exCoinExchangeRate.getExchangeRate() != null){
            return new JsonResult().setSuccess(true).setObj(exCoinExchangeRate.getExchangeRate());
        }
        return new JsonResult().setSuccess(false).setObj(null);
    }
    
	@Override
	public JsonResult getPersonalAsset(Long userId,String type, String isDefault){
		//??????????????????
		List<AppBankCard> list = otcService.getPersonalAsset(userId,type,isDefault);
		if(list != null && list.size() > 0){
			List<AppBankCardRemote> beanList = ObjectUtil.beanList(list, AppBankCardRemote.class);
			return new JsonResult().setSuccess(true).setObj(beanList);
		}
		return new JsonResult().setSuccess(false).setObj(null);
	}
	
	@Override
	public synchronized JsonResult addReleaseAdvertisement(String transactionMode, String coinCode, String coinName,
			String nationality, String isFixed, String tradeMoney, String premium, String paymentTerm,
			String tradeMoneyMix, String tradeMoneyMax, String remark, String isAutomatic, String isSecurity,
			String isBeTrusted, Long userId, String payType, String payTypeRemake, String serviceCharge, BigDecimal coinNumMin, BigDecimal coinNumMax,String hidshichangjiage, String legalCurrency) {
		try {

			System.out.println("?????????");
			coinCode = coinCode.split(",")[0];
			
			String coinPercent = "";// ??????????????????
			int keepDecimalForCoin = 0;//???????????????
			String otcMinPercent = "0";//otc??????????????????????????????
			String otcMaxPercent = "0";//otc??????????????????????????????

			String productinfoListall = redisService.get("otc:coinCodeList");
			JSONArray parseArray = JSON.parseArray(productinfoListall);
			for(int k=0;k<parseArray.size();k++){
				JSONObject jo = JSON.parseObject(parseArray.get(k).toString());
				if(jo.getString("coinCode").equals(coinCode)){
					if(jo.getString("coinPercent")!=null){
						coinPercent = jo.getString("coinPercent");
					}
					if (jo.getInteger("keepDecimalForCoin")!=null) {
						keepDecimalForCoin = jo.getInteger("keepDecimalForCoin");
					}
					if(jo.getString("otcMinPercent")!=null){
						otcMinPercent = jo.getString("otcMinPercent");
					}
					if(jo.getString("otcMaxPercent")!=null){
						otcMaxPercent = jo.getString("otcMaxPercent");
					}

				}
			}

			//???????????????
			BigDecimal yi1 = new BigDecimal("1");// 1
			BigDecimal yibai1 = new BigDecimal("100");// 100
			BigDecimal money=new BigDecimal(tradeMoney);
			BigDecimal shichangjiage=new BigDecimal(hidshichangjiage);
			BigDecimal pre=new BigDecimal("0");
			if( premium != null && ! "".equals(premium)){
				pre=new BigDecimal(premium);
			}
			//??????coinCode??????????????????????????????--????????????????????????
			//??????????????????????????????
			if ("1".equals( isFixed) ){// ???????????????
				//??????????????????
				if( otcMinPercent != null && !"".equals(otcMinPercent)  && !"0".equals(otcMinPercent)){
					BigDecimal minPercent=new BigDecimal(otcMinPercent);
					BigDecimal minPrice=shichangjiage.multiply(yi1.subtract(minPercent.divide(yibai1))).setScale(2, BigDecimal.ROUND_HALF_DOWN);
					if(money.compareTo(minPrice) < 0){
						return new JsonResult().setSuccess(false).setMsg("jiagexiaoyuzuidixiane");
					}
				}
				//??????????????????
				if( otcMaxPercent != null && !"".equals(otcMaxPercent)  && !"0".equals(otcMaxPercent)){
					BigDecimal maxPercent=new BigDecimal(otcMaxPercent);
					BigDecimal maxPrice=shichangjiage.multiply(yi1.add(maxPercent.divide(yibai1))).setScale(2, BigDecimal.ROUND_HALF_DOWN);
					if(money.compareTo(maxPrice) > 0){
						return new JsonResult().setSuccess(false).setMsg("jiagedayuzuigaoxiane");
					}
				}
			}else{
				/*if("1".equals(transactionMode)) { //??????
					//??????????????????
					if( otcMinPercent != null && !"".equals(otcMinPercent) && !"0".equals(otcMinPercent)){
						BigDecimal minPercent=new BigDecimal(otcMinPercent);
						if(pre.compareTo(minPercent) < 0){
							return new JsonResult().setSuccess(false).setMsg("jiagexiaoyuzuidixiane");
						}
					}
				}else if("2".equals(transactionMode)){ //??????
					//??????????????????
					if( otcMaxPercent != null && !"".equals(otcMaxPercent) && !"0".equals(otcMaxPercent)){
						BigDecimal maxPercent=new BigDecimal(otcMaxPercent);
						if(pre.compareTo(maxPercent) > 0){
							return new JsonResult().setSuccess(false).setMsg("jiagedayuzuigaoxiane");
						}
					}
				}*/

			}


			if("1".equals(transactionMode)){ //??????
				//???????????????????????? 0??? 1???
				if( "0".equals( isFixed ) ){
					//??????

				}else{
					//??????

				}
			}else if("2".equals(transactionMode)){//??????
				//???????????????????????? 0??? 1???
				if( "0".equals( isFixed ) ){
					//??????

				}else{
					//??????

				}
			}

			
			//ExProduct exProduct = otcService.getExProductByCoinCode(coinCode);
			if(new BigDecimal(tradeMoneyMix).compareTo(new BigDecimal(tradeMoney).multiply(new BigDecimal(coinPercent).divide(new BigDecimal(100))))<0){
				return new JsonResult().setSuccess(false).setMsg("baifenbihuilv");
			}
			
			//??????????????????????????????????????????????????????
			QueryFilter rafilter = new QueryFilter(ReleaseAdvertisement.class);
			rafilter.addFilter("transactionMode=", transactionMode);
			rafilter.addFilter("coinCode=", coinCode);
			rafilter.addFilter("customerId=", userId);
			rafilter.addFilter("status=", 1);
			rafilter.addFilter("state=", 0);
			List<ReleaseAdvertisement> raList = releaseAdvertisementService.find(rafilter);
			if(raList != null && raList.size() > 0){ //?????????????????????????????????
				String msg = "";
				if("1".equals(transactionMode)){ //????????????
					msg = "yijingfabuguochushou";
				}else if("2".equals(transactionMode)){//????????????
					msg = "yijingfabuguogoumai";
				}else{
					msg = "fabuyichang";
				}
				return new JsonResult().setSuccess(false).setMsg(msg);
			}
			
			//?????????????????????????????????????????????????????????????????????????????????
			QueryFilter qf = new QueryFilter(AppTransaction.class);
			qf.addFilter("coinCode=", coinCode);
			qf.addFilter("status!=", 5); //????????????
			qf.addFilter("status!=", 14); //????????????
			qf.addFilter("status!=", 1); //???????????????????????????????????????????????????????????????
			//??????
			if("1".equals(transactionMode)){ //????????????????????????sellUserId
				qf.addFilter("sellUserId=", userId);
			}else if("2".equals(transactionMode)){ //????????????????????????buyUserId
				qf.addFilter("buyUserId=", userId);
			}else{
				return new JsonResult().setSuccess(false).setMsg("leixingyichang");
			}
			List<OtcAppTransaction> atList = otcAppTransactionService.find(qf);
			if(atList != null && atList.size() > 0){ //????????????????????????
				return new JsonResult().setSuccess(false).setMsg("bunengfabuguanggaoing");
			}
			
			//ExProductParameter exProductParameter = otcService.getExProductParameterByCoinCode(coinCode);
			 
			// ?????? ????????????
			ExDigitalmoneyAccount exDigitalmoneyAccount = otcService.getCoinAccountByIdAndCoinCode(userId, coinCode);
			if (exDigitalmoneyAccount == null) {
				return new JsonResult().setSuccess(false).setMsg("xnzh_no_exist");
			}

			ReleaseAdvertisement releaseAdvertisement = new ReleaseAdvertisement();

			BigDecimal tradeMoneyMaxBD = new BigDecimal(tradeMoneyMax);
			if (transactionMode.equals("1")) { // ?????? ??????

				if (isFixed.equals("1")) { // ???????????????

					BigDecimal price = new BigDecimal(tradeMoney);
					// ????????? ?????? ??????=?????????
					//BigDecimal bs = tradeMoneyMaxBD.divide(price, keepDecimalForCoin, BigDecimal.ROUND_DOWN);
					// ?????????
					//otcService.publish(exDigitalmoneyAccount.getId(), bs, 2, 1, "", 60);
					//otcService.publish(exDigitalmoneyAccount.getId(), bs.multiply(new BigDecimal(-1)), 1, 1, "", 60);

					otcService.publish(exDigitalmoneyAccount.getId(), coinNumMax, 2, 1, "", 60);
					otcService.publish(exDigitalmoneyAccount.getId(), coinNumMax.multiply(new BigDecimal(-1)), 1, 1, "", 60);


				} else {

					BigDecimal yi = new BigDecimal("1");// 1
					BigDecimal yibai = new BigDecimal("100");// 100
					BigDecimal premiumBD = new BigDecimal(premium);
					BigDecimal shichangjiageBD = new BigDecimal(tradeMoney);
					// if (transactionMode.equals("1")) {// ?????? ??????
					BigDecimal price = shichangjiageBD
							.multiply(yi.subtract(premiumBD.divide(yibai, 2, BigDecimal.ROUND_DOWN))); // ????????????*(1-??????????????????)????????????

					// tradeMoney = price.toString();

					// ????????? ?????? ??????=?????????
					//BigDecimal bs = tradeMoneyMaxBD.divide(price, 4, BigDecimal.ROUND_DOWN);
					//otcService.publish(exDigitalmoneyAccount.getId(), bs, 2, 1, "", 60);
					//otcService.publish(exDigitalmoneyAccount.getId(), bs.multiply(new BigDecimal(-1)), 1, 1, "", 60);

					otcService.publish(exDigitalmoneyAccount.getId(), coinNumMax, 2, 1, "", 60);
					otcService.publish(exDigitalmoneyAccount.getId(), coinNumMax.multiply(new BigDecimal(-1)), 1, 1, "", 60);

					// } else {// ?????? ??????
					// BigDecimal price = shichangjiageBD
					// .multiply(yi.add(premiumBD.divide(yibai, 4,
					// BigDecimal.ROUND_HALF_UP)));// ????????????*(1+??????????????????)????????????

					// tradeMoney = price.toString();
					// }
				}
			}

			releaseAdvertisement.setTransactionMode(Integer.valueOf(transactionMode));
			releaseAdvertisement.setCoinCode(coinCode);
			releaseAdvertisement.setCoinName(coinName);
			releaseAdvertisement.setNationality(nationality);
			releaseAdvertisement.setIsFixed(Integer.valueOf(isFixed));
			releaseAdvertisement.setTradeMoney(new BigDecimal(tradeMoney).setScale(3, BigDecimal.ROUND_DOWN));
			if (!premium.isEmpty()) {
				releaseAdvertisement.setPremium(new BigDecimal(premium));
			}
			releaseAdvertisement.setPaymentTerm(paymentTerm);
			releaseAdvertisement.setPayType(payType);
			releaseAdvertisement.setTradeMoneyMix(new BigDecimal(tradeMoneyMix).setScale(2, BigDecimal.ROUND_DOWN));
			releaseAdvertisement.setTradeMoneyMax(new BigDecimal(tradeMoneyMax).setScale(2, BigDecimal.ROUND_DOWN));
			releaseAdvertisement.setRemark(remark);
			//releaseAdvertisement.setIsAutomatic(Integer.valueOf(isAutomatic));
			///releaseAdvertisement.setIsSecurity(Integer.valueOf(isSecurity));
			//releaseAdvertisement.setIsBeTrusted(Integer.valueOf(isBeTrusted));
			releaseAdvertisement.setCustomerId(userId);
			releaseAdvertisement.setModified(new Date());
			releaseAdvertisement.setCreated(new Date());
			/*
			 * releaseAdvertisement.setTradeMoney(new BigDecimal(tradeMoney));
			 * releaseAdvertisement.setPayTypeRemake(payTypeRemake);
			 */
			//????????????
			releaseAdvertisement.setPayTypeRemake(payTypeRemake);
			
			releaseAdvertisement.setStatus(1);
			releaseAdvertisement.setState(0);
			releaseAdvertisement.setAccountId(exDigitalmoneyAccount.getId());
			
			//???????????????????????????????????????
			//?????????????????????????????????
			if(payType != null && !"".equals(payType)){
				AppBankCard appBankCard = null;
				if(payType.contains("1")){//?????????
					appBankCard = otcService.selectByParameter(userId, 1, 1);
					if(appBankCard != null){
						releaseAdvertisement.setBankId(appBankCard.getId());
						//??????????????????
						releaseAdvertisement.setBankNumber(appBankCard.getCardNumber());
					}
				}
				if(payType.contains("2")){//?????????
					appBankCard = otcService.selectByParameter(userId, 2, 1);
					if(appBankCard != null){
						releaseAdvertisement.setAlipayId(appBankCard.getId());
						//?????????????????????
						releaseAdvertisement.setAlipayAccount(appBankCard.getCardNumber());
						//??????????????????
						releaseAdvertisement.setAlipayThingUrl(appBankCard.getThingUrl());
					}				
				}
				if(payType.contains("3")){//??????
					appBankCard = otcService.selectByParameter(userId, 3, 1);
					if(appBankCard != null){
						releaseAdvertisement.setWechatId(appBankCard.getId());
						//??????????????????
						releaseAdvertisement.setWechatAccount(appBankCard.getCardNumber());
						//??????????????????
						releaseAdvertisement.setWechatThingUrl(appBankCard.getThingUrl());
					}
				}
			}
			releaseAdvertisement.setCoinNumMin(coinNumMin);
			releaseAdvertisement.setCoinNumMax(coinNumMax);
			releaseAdvertisement.setInitialCoinNumMin(coinNumMin);//??????????????????????????????
			releaseAdvertisement.setInitialCoinNumMax(coinNumMax);// ??????????????????????????????
			releaseAdvertisement.setLegalCurrency(legalCurrency);
			releaseAdvertisementService.save(releaseAdvertisement);
			return new JsonResult().setSuccess(true).setMsg("fabuchenggong");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JsonResult().setSuccess(false).setMsg("fabushibai");
	}
	
	@Override
	public JsonResult userExCoinParameter(Long userId, String premium, String shichangjiage, String coinCode,
			String tradeMoneyMax, String transactionMode, String isFixeds, String tradeMoney) {
		BigDecimal tradeMoneyMaxBD = new BigDecimal(tradeMoneyMax);
		BigDecimal yi = new BigDecimal("1");// 1
		BigDecimal yibai = new BigDecimal("100");// 100
		
		ExDigitalmoneyAccountRedis dmAccount = getCoinAccountRedis(userId, coinCode);
		
		if (isFixeds.equals("true")) {// ???????????????
			BigDecimal price = new BigDecimal(tradeMoney);
			if (price.compareTo(BigDecimal.ZERO) == 0) {
				return new JsonResult().setSuccess(false).setMsg("jiagebunengxiaoyudengyu0");
			}
			// ????????? ?????? ??????=?????????
			BigDecimal bs = tradeMoneyMaxBD.divide(price, 4, BigDecimal.ROUND_DOWN);
			// ???????????????
			if (transactionMode.equals("1")) {// ??????---?????? ???????????????????????????
				if (dmAccount.getHotMoney().compareTo(bs) < 0) {
					return new JsonResult().setSuccess(false).setMsg("bishuliangbugou");
				} else {
					return new JsonResult().setSuccess(true).setObj("0");// serviceCharge.toString()
				}
			} else {// ??????
				return new JsonResult().setSuccess(true).setObj("0");// serviceCharge.toString()
			}

		} else {// ????????????????????????

			BigDecimal premiumBD = new BigDecimal(premium);
			BigDecimal shichangjiageBD = new BigDecimal(shichangjiage);
			System.out.println(premiumBD.toString());
			System.out.println(shichangjiageBD.toString());
			if (transactionMode.equals("1")) {// ?????? ??????
				BigDecimal price = shichangjiageBD.multiply(yi.subtract(premiumBD.divide(yibai))); // ????????????*(1-??????????????????)????????????
				System.out.println("??????" + price.toString());
				// ????????? ?????? ??????=?????????
				BigDecimal bs = tradeMoneyMaxBD.divide(price, 4, BigDecimal.ROUND_DOWN);

				System.out.println("?????????" + bs.toString());
				if (dmAccount.getHotMoney().compareTo(bs) < 0) {
					return new JsonResult().setSuccess(false).setMsg("bishuliangbugou");
				} else {
					return new JsonResult().setSuccess(true).setObj("0");// serviceCharge.toString()
				}

			} else {// ?????? ??????
				return new JsonResult().setSuccess(true).setObj("0");
			}

		}
	}
	
	@Override
	public FrontPage advertisingHallDetail(String payType,String nationality,String coinCode, String transactionMode, String offset, String limit, String legalCurrency) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("payType", payType);
		params.put("nationality", nationality);
		params.put("coinCode", coinCode);
		params.put("transactionMode", transactionMode);
		params.put("offset", offset);
		params.put("limit", limit);
		params.put("legalCurrency", legalCurrency);
		FrontPage frontPage = releaseAdvertisementService.findPageHall(params);
		List<ReleaseAdvertisement> list = frontPage.getRows();
		if (list.size() > 0) {
			List<ReleaseAdvertisementRemote> beanList = ObjectUtil.beanList(list, ReleaseAdvertisementRemote.class);
			for (int i = 0; i < beanList.size(); i++) {
				AppCustomer appCustomer = otcService.getAppCustomerById(beanList.get(i).getCustomerId());
				if(!StringUtils.isEmpty(appCustomer)){
					beanList.get(i).setUsername(appCustomer.getNickNameOtc());
				}
			}
			frontPage.setRows(beanList);
		}
		return frontPage;
	}
	
	@Override
	public JsonResult adUserInfor(Long id, String coinCode) {
    	try{
			Map<Object, Object> map = new HashMap<Object, Object>();
			// ??????30??????????????????
			BigDecimal adUserBy30 = otcAppTransactionService.adUserBy30(id, coinCode);
			map.put("adUserBy30", adUserBy30 == null ? 0 : adUserBy30);

			Map<String,Object> params = new HashMap<String,Object>();
			params.put("id", id);
			params.put("status", 14);
			Integer tradeNumAll = otcAppTransactionService.allTradeCount(params);
			map.put("tradeNumAll", tradeNumAll == null ? 0 : tradeNumAll);
			// ???????????????
			BigDecimal adUserAll = otcAppTransactionService.adUserAll(id, coinCode);
			map.put("tradeNum", adUserAll == null ? 0 : adUserAll);
			// ????????????
			AppCustomer appCustomer = otcService.getAppCustomerById(id);
			map.put("adUserAll", appCustomer.getTrustNum() == null ? 0 : appCustomer.getTrustNum());

			//???????????????
			map.put("keepDecimalFixPrice", keepDecimalForCoin(coinCode));
			return new JsonResult().setSuccess(true).setObj(map);
		}catch(Exception e){
			e.printStackTrace();
		}
		return new JsonResult().setSuccess(false);
	}
	
	@Override
	public JsonResult selectTrustShield(Long id1, Long id2) {
		Map<String, Object> map = new HashMap<String, Object>();
		int trust = 0, shield = 0;
		QueryFilter qf = new QueryFilter(TrustShield.class);
		qf.addFilter("trust=", id1);
		qf.addFilter("isTrust=", id2);
		List<TrustShield> list = trustShieldService.find(qf);
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getStatus() == 1) {
					trust = 1;
				} else if (list.get(i).getStatus() == 2) {
					shield = 1;
				}
			}
			map.put("trust", trust);
			map.put("shield", shield);
			return new JsonResult().setSuccess(true).setObj(map);
		}
		return new JsonResult().setSuccess(false);
	}
	
	@Override
	public JsonResult getById(Long id) {
		QueryFilter qf = new QueryFilter(ReleaseAdvertisement.class);
		qf.addFilter("id=", id);
		ReleaseAdvertisement r = releaseAdvertisementService.get(id);
		if (r != null) {
			ReleaseAdvertisementRemote bean2bean = ObjectUtil.bean2bean(r, ReleaseAdvertisementRemote.class);
			return new JsonResult().setObj(bean2bean);
		}
		return null;
	}
	
	@Override
	public JsonResult isCanTransaction(String customerId, String transactionMode,String coinCode) {
		//????????????????????????????????????????????????????????????  ?????????????????????????????????????????????????????????
		QueryFilter filter = new QueryFilter(ReleaseAdvertisement.class);
		filter.addFilter("customerId=", customerId);
		if("1".equals(transactionMode)){
			filter.addFilter("transactionMode=", 2);
		}else if("2".equals(transactionMode)){
			filter.addFilter("transactionMode=", 1);
		}else{
			return new JsonResult().setSuccess(false).setMsg("jiaoyileixingyichang");
		}
		filter.addFilter("coinCode=", coinCode);
		filter.addFilter("status=", 1);
		filter.addFilter("state=", 0);
		List<ReleaseAdvertisement> raList = releaseAdvertisementService.find(filter);
		if(raList != null && raList.size() > 0){ //????????????
			return new JsonResult().setSuccess(false).setMsg("yijingyoule");
		}
		
		//??????????????????????????????????????????????????????????????????????????????
		QueryFilter qf = new QueryFilter(AppTransaction.class);
		//qf.addFilter("status_in", "14,5");//?????????????????????
		qf.addFilter("status!=", 14); //??????
		qf.addFilter("status!=", 5); //??????
		qf.addFilter("status!=", 1); //???????????????????????????????????????????????????????????????
		qf.addFilter("status!=", 11); //??????????????????
		qf.addFilter("status!=", 12); //??????????????????
		//qf.addFilter("transactionMode=", transactionMode);
		qf.addFilter("coinCode=", coinCode);
		//??????????????????
		if("1".equals(transactionMode)){ //????????????     ?????????????????????buyUserId
			qf.addFilter("buyUserId=", customerId);
		}else if("2".equals(transactionMode)){ //????????????   ?????????????????????sellUserId
			qf.addFilter("sellUserId=", customerId);
		}else{
			return new JsonResult().setSuccess(false).setMsg("jiaoyileixingyichang");
		}
		List<OtcAppTransaction> atList = otcAppTransactionService.find(qf);
		if(atList != null && atList.size() > 0){
			return new JsonResult().setSuccess(false).setMsg("yiyouzhengzaijiaoyizhong");
		}
		
		return new JsonResult().setSuccess(true).setMsg("keyijinxingjiaoyi");
	}
	
	@Override
	public JsonResult orderAccounting(String tradeNum, Long releaseId) {
		Map<String, Object> map = new HashMap<String, Object>();

		QueryFilter qf = new QueryFilter(AppTransaction.class);
		qf.addFilter("transactionNum=", tradeNum);
		OtcAppTransaction otcAppTransaction = otcAppTransactionService.get(qf);
		if (otcAppTransaction != null) {
			OtcAppTransactionRemote bean2bean = ObjectUtil.bean2bean(otcAppTransaction, OtcAppTransactionRemote.class);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			bean2bean.setCreatedString(sdf.format(bean2bean.getCreated()));
			map.put("app", bean2bean);
			map.put("appCustomerId", otcAppTransaction.getCustomerId());
			
			//?????????????????????????????????????????????
			if(2 == otcAppTransaction.getTransactionMode()){
				//?????????????????????
				if(otcAppTransaction.getBankId() != null){
					AppBankCard appBankCard = otcService.getById(otcAppTransaction.getBankId());
					AppBankCardRemote paRemote = ObjectUtil.bean2bean(appBankCard, AppBankCardRemote.class);
					if(StringUtil.isNotEmpty(paRemote.getThingUrl())){
						paRemote.setThingUrl(OssUtil.getUrl(paRemote.getThingUrl()));
					}
					map.put("bank", paRemote);
				}
				//?????????????????????
				if(otcAppTransaction.getAlipayId() != null){
					AppBankCard appBankCard = otcService.getById(otcAppTransaction.getAlipayId());
					AppBankCardRemote paRemote = ObjectUtil.bean2bean(appBankCard, AppBankCardRemote.class);
					if(StringUtil.isNotEmpty(paRemote.getThingUrl())){
						paRemote.setThingUrl(OssUtil.getUrl(paRemote.getThingUrl()));
					}
					map.put("alipay", paRemote);
				}
				//??????????????????
				if(otcAppTransaction.getWechatId() != null){
					AppBankCard appBankCard = otcService.getById(otcAppTransaction.getWechatId());
					AppBankCardRemote paRemote = ObjectUtil.bean2bean(appBankCard, AppBankCardRemote.class);
					if(StringUtil.isNotEmpty(paRemote.getThingUrl())){
						paRemote.setThingUrl(OssUtil.getUrl(paRemote.getThingUrl()));
					}
					map.put("wechat", paRemote);
				}
			}
		}
		ReleaseAdvertisement releaseAdvertisement = releaseAdvertisementService.get(releaseId);
		if (releaseAdvertisement != null) {
			map.put("payTypeRemake", releaseAdvertisement.getPayTypeRemake());

			List<String> list = new ArrayList<String>();
			String[] split = releaseAdvertisement.getPayType().split(",");
			if (split.length > 0) {
				for (int i = 0; i < split.length; i++) {
					if ("1".equals(split[i])) {
						list.add("yinhangzhuanzhang");
					} else if ("2".equals(split[i])) {
						list.add("zhifubao2");
					} else if ("3".equals(split[i])) {
						list.add("weixinzhifu");
					}
				}
			}
			map.put("list", list);
			map.put("paymentTerm", releaseAdvertisement.getPaymentTerm());
			//?????????????????????
			map.put("releaseRemark", releaseAdvertisement.getRemark());
			// ????????????
			map.put("releasePrice", releaseAdvertisement.getTradeMoney());
			
			//????????????????????????????????????????????????????????????
			if(releaseAdvertisement.getTransactionMode() == 1){
				//?????????????????????
				if(releaseAdvertisement.getBankId() != null){
					AppBankCard appBankCard = otcService.getById(releaseAdvertisement.getBankId());
					AppBankCardRemote paRemote = ObjectUtil.bean2bean(appBankCard, AppBankCardRemote.class);
					if(StringUtil.isNotEmpty(paRemote.getThingUrl())){
						paRemote.setThingUrl(OssUtil.getUrl(paRemote.getThingUrl()));
					}
					map.put("bank", paRemote);
				}
				//?????????????????????
				if(releaseAdvertisement.getAlipayId() != null){
					AppBankCard appBankCard = otcService.getById(releaseAdvertisement.getAlipayId());
					AppBankCardRemote paRemote = ObjectUtil.bean2bean(appBankCard, AppBankCardRemote.class);
					if(StringUtil.isNotEmpty(paRemote.getThingUrl())){
						paRemote.setThingUrl(OssUtil.getUrl(paRemote.getThingUrl()));
					}
					map.put("alipay", paRemote);
				}
				//??????????????????
				if(releaseAdvertisement.getWechatId() != null){
					AppBankCard appBankCard = otcService.getById(releaseAdvertisement.getWechatId());
					AppBankCardRemote paRemote = ObjectUtil.bean2bean(appBankCard, AppBankCardRemote.class);
					if(StringUtil.isNotEmpty(paRemote.getThingUrl())){
						paRemote.setThingUrl(OssUtil.getUrl(paRemote.getThingUrl()));
					}
					map.put("wechat", paRemote);
				}
			}
		}
		return new JsonResult().setObj(map);
	}
	
	@Override
	public JsonResult redisTimeOrder(String tradeNum, String paymentTerm) {
		Double index = redisService.zscore("otc:tradeNum", tradeNum);
		if(index != null){
			QueryFilter qf = new QueryFilter(AppTransaction.class);
			qf.addFilter("transactionNum=", tradeNum);
			OtcAppTransaction otcAppTransaction = otcAppTransactionService.get(qf);

			if (StringUtil.isEmpty(paymentTerm)) {

				if (otcAppTransaction != null) {
					ReleaseAdvertisement r = releaseAdvertisementService.get(otcAppTransaction.getAdvertisementId());
					if (r != null) {
						paymentTerm = r.getPaymentTerm();
					}
				}
			}
			if (otcAppTransaction != null) {
				otcAppTransaction.setStatus(2);// 2???????????????
				otcAppTransactionService.update(otcAppTransaction);
			}

			Double zscore = redisService.zscore("otc:tradeNum", tradeNum);

			return new JsonResult().setSuccess(true).setObj((int)(zscore - System.currentTimeMillis() / 1000));
		}
		return null;
	}
	
	
	@Override
	public JsonResult getExCoinFee(String coinCodeMoney, String coinCode) {
		BigDecimal eatFee = new BigDecimal(0); // ???????????????
		int eatFeeType = 0;// ???????????????????????????
		
		String productinfoListall = redisService.get("otc:coinCodeList");
		JSONArray parseArray = JSON.parseArray(productinfoListall);
		for(int k=0;k<parseArray.size();k++){
			JSONObject jo = JSON.parseObject(parseArray.get(k).toString());
			if(jo.getString("coinCode").equals(coinCode)){
				if (jo.getInteger("eatFee")!=null) {
					eatFee = jo.getBigDecimal("eatFee");
				}
				if(jo.getInteger("eatFeeType")!=null){
					eatFeeType = jo.getInteger("eatFeeType");
				}
			}
		}
		BigDecimal bs = new BigDecimal(coinCodeMoney);

		if (0 == eatFeeType) {// ??????
			return new JsonResult().setSuccess(true).setCode(eatFee.setScale(2, BigDecimal.ROUND_DOWN).toString());

		} else if (1 == eatFeeType) {// ?????????
			eatFee = eatFee.multiply(new BigDecimal(coinCodeMoney))
					.divide(new BigDecimal(100)).setScale(2);
			return new JsonResult().setSuccess(true).setCode(eatFee.setScale(2, BigDecimal.ROUND_DOWN).toString());
		}
		return new JsonResult().setSuccess(true).setCode("0");
	}

	public static void main(String[] args) {
    	Double d1 = new Double(152);
		Double d2 = new Double(150);
		System.out.println((int)(d1 - d2));

		System.out.println(RandomStringUtils.random(8, true, true));
	}
	
	@Override
	public synchronized JsonResult buydetail(Long releaseId, String tradeMoney, String coinCodeMoney, String remark, Long buyId,
			Long sellId, String transactionMode, String coinCode, String payType, Long userid, String editor) {
		try {
			//??????24???????????????????????????3?????????30??????????????????????????????????????????????????????24??????????????????????????????
			if( "1".equals(transactionMode) ){
				String ss = redisService.get("otcCanNotBuy:"+buyId);
				if( ! StringUtils.isEmpty(ss) ){
					return new JsonResult().setSuccess(false).setMsg("24xiaoshibunenggoujiaoyi");
				}
			}


			BigDecimal eatFee = new BigDecimal(0); // ???????????????
			int eatFeeType = 0;// ???????????????????????????
			String coinPercent = "";// ??????????????????
			int keepDecimalForCoin = keepDecimalForCoin(coinCode);// ???????????????
			
			String productinfoListall = redisService.get("otc:coinCodeList");
			JSONArray parseArray = JSON.parseArray(productinfoListall);
			for(int k=0;k<parseArray.size();k++){
				JSONObject jo = JSON.parseObject(parseArray.get(k).toString());
				if(jo.getString("coinCode").equals(coinCode)){
					if (jo.getInteger("eatFee")!=null) {
						eatFee = jo.getBigDecimal("eatFee");
					}
					if(jo.getInteger("eatFeeType")!=null){
						eatFeeType = jo.getInteger("eatFeeType");
					}
					if(jo.getString("coinPercent")!=null){
						coinPercent = jo.getString("coinPercent");
					}
				}
			}
			
			ReleaseAdvertisement releaseAdvertisement = releaseAdvertisementService.get(releaseId);

			//????????????????????????????????????????????????????????????   ????????????????????????????????????
			if( releaseAdvertisement.getStatus() == 0 ||  releaseAdvertisement.getCoinNumMax().compareTo(new BigDecimal(coinCodeMoney)) < 0
					||  releaseAdvertisement.getCoinNumMin().compareTo(new BigDecimal(coinCodeMoney)) > 0){
				return new JsonResult().setSuccess(false).setMsg("ciguanggaoyiguoqi");
			}

			if(StringUtils.isEmpty(coinPercent)){
				return new JsonResult().setSuccess(false).setMsg("qingshezhibaifenbihuilv");
			}else{
				//??????????????????
				/*if(releaseAdvertisement != null && releaseAdvertisement.getPremium() != null && releaseAdvertisement.getPremium().compareTo(new BigDecimal(0))>0){
					BigDecimal price = releaseAdvertisement.getTradeMoney().multiply(new BigDecimal("1").subtract(releaseAdvertisement.getPremium().divide(new BigDecimal("100")))); // ????????????*(1-??????????????????)????????????
					if(releaseAdvertisement.getTradeMoneyMax().subtract(new BigDecimal(tradeMoney)).compareTo(price.multiply(new BigDecimal(coinPercent).divide(new BigDecimal(100))))<0){
						releaseAdvertisement.setState(1);//????????????
					}
				}else{*/
					if(releaseAdvertisement.getTradeMoneyMax().subtract(new BigDecimal(tradeMoney)).compareTo(releaseAdvertisement.getTradeMoney().multiply(new BigDecimal(coinPercent).divide(new BigDecimal(100))))<0){
						releaseAdvertisement.setState(1);//????????????
					}
				//}
			}

			//TODO ?????????????????????????????? 2018.09.29,????????????????????????
			/*if(releaseAdvertisement.getIsBeTrusted()==1){
				QueryFilter trusfilter=new QueryFilter(TrustShield.class);
				trusfilter.addFilter("trust=", userid);
				trusfilter.addFilter("isTrust=", releaseAdvertisement.getCustomerId());
				trusfilter.addFilter("status=", 1);
				List<TrustShield> list=trustShieldService.find(trusfilter);
				if (list.size()<=0) {
					return new JsonResult().setSuccess(false).setMsg("xinrenbenguanggao");
				}
			}*/
			//??????????????????????????????//TODO ?????????????????????????????? 2018.09.29,????????????????????????
			/*if(releaseAdvertisement.getIsSecurity()==1){
				AppCustomer customer=otcService.getAppCustomerById(userid);
				if (customer != null) {
					if( customer.getPhoneState() != 1 ){
						return new JsonResult().setSuccess(false).setMsg("bangdingshoujihoukejinxingjiaoyi");
					}
					if( customer.getHasEmail() != 1 ){
						return new JsonResult().setSuccess(false).setMsg("bangdingyouxianghoukejinxingjiaoyi");
					}
				}
			}*/

			OtcAppTransaction otcAppTransaction = new OtcAppTransaction();
			otcAppTransaction.setTransactionNum(transactionNum(""));

			String randomStr = RandomStringUtils.randomNumeric(4);

			otcAppTransaction.setReferenceNum(randomStr);// ???????????????
			otcAppTransaction.setCustomerId(userid);
			//????????? redis
			ExDigitalmoneyAccountRedis exDigitalmoneyAccountRedis = getCoinAccountRedis(userid, coinCode);
			if (exDigitalmoneyAccountRedis != null) {
				otcAppTransaction.setAccountId(exDigitalmoneyAccountRedis.getId());
			}
			// ??????transactionMode???1??????id???????????????2??????????????????????????????
			AppCustomer appCustomerBuy = otcService.getAppCustomerById(buyId);
			if (appCustomerBuy != null) {
				otcAppTransaction.setBuyUserName(appCustomerBuy.getNickNameOtc());
			}
			AppCustomer appCustomerSell = otcService.getAppCustomerById(sellId);
			if (appCustomerBuy != null) {
				otcAppTransaction.setSellUserName(appCustomerSell.getNickNameOtc());
			}
			otcAppTransaction.setBuyUserId(buyId);
			BigDecimal yi = new BigDecimal("1");// 1
			BigDecimal yibai = new BigDecimal("100");// 100
			if ("1".equals(transactionMode)) {// ??? - ??????

				otcAppTransaction.setStatus(2);
				if (0 == eatFeeType) {// ??????
					otcAppTransaction.setSellfee(eatFee);
				} else if (1 == eatFeeType) {// ?????????
					otcAppTransaction.setSellfee(eatFee.multiply(new BigDecimal(coinCodeMoney))
							.divide(new BigDecimal(100)).setScale(keepDecimalForCoin, BigDecimal.ROUND_DOWN));
				}
				//???????????????
				BigDecimal price;
				//??????coinCode??????????????????????????????--????????????????????????
				if (releaseAdvertisement.getIsFixed() == 1) {// ???????????????
					price=releaseAdvertisement.getTradeMoney();
				}else{
					//????????????????????????
					price = releaseAdvertisement.getTradeMoney().multiply(
							yi.subtract(releaseAdvertisement.getPremium().divide(yibai))).setScale(3, BigDecimal.ROUND_DOWN);
				}
				releaseAdvertisement.setCoinNumMax(releaseAdvertisement.getCoinNumMax().subtract(new BigDecimal(coinCodeMoney)));
				releaseAdvertisement.setTradeMoneyMax(releaseAdvertisement.getCoinNumMax().multiply(price).setScale(3, BigDecimal.ROUND_DOWN));

				//????????????????????????????????????????????????????????????????????????
				if(releaseAdvertisement.getCoinNumMax().compareTo(releaseAdvertisement.getCoinNumMin())<0){
					releaseAdvertisement.setState(1);//????????????
					releaseAdvertisement.setStatus(0);
					ExDigitalmoneyAccountRedis exDigitalmoneyAccountRedisGua = getCoinAccountRedis(releaseAdvertisement.getCustomerId(), coinCode);
					otcService.publish(exDigitalmoneyAccountRedisGua.getId(), releaseAdvertisement.getCoinNumMax().multiply(new BigDecimal(-1)), 2, 1, "", 61);
					otcService.publish(exDigitalmoneyAccountRedisGua.getId(), releaseAdvertisement.getCoinNumMax(), 1, 1, "", 61);
					//???????????????30???,?????????????????????????????????????????????????????????
					int i=60*60*24*30;
					/*try {
						i = Integer.parseInt(releaseAdvertisement.getPaymentTerm())*60;
					}catch (Exception e){}*/
					redisService.save("otc:ReleaseAdvertisementAfterColse:"+otcAppTransaction.getTransactionNum(),releaseAdvertisement.getCoinNumMax().toString(),i);
					//if (releaseAdvertisement.getIsFixed() == 1) {// ???????????????
						/*ExCoinAccount exCoinAccountGua = exCoinAccountService.get(new QueryFilter(ExCoinAccount.class)
								.addFilter("customerId=", releaseAdvertisement.getCustomerId()).addFilter("coinCode=", coinCode));
						exCoinAccountGua.setColdMoney(exCoinAccountGua.getColdMoney().subtract(bs));// ??????
						exCoinAccountGua.setHotMoney(exCoinAccountGua.getHotMoney().add(bs));// ????????????
						exCoinAccountService.update(exCoinAccountGua);*/
					//} else {
						/*
						 * BigDecimal serviceCharge = new BigDecimal("0");
						 * if(null!=exCoinParameter){ if
						 * (exCoinParameter.getEatFeeType() == 0) {// ????????????????????? 0????????????
						 * serviceCharge = exCoinParameter.getEatFee(); } else {//
						 * 1??????????????? serviceCharge =
						 * exCoinParameter.getEatFee().multiply(bs) .divide(new
						 * BigDecimal(100)).setScale(2); } } bs =
						 * bs.add(serviceCharge);// ?????????
						 */
						/*ExCoinAccount exCoinAccountGua = exCoinAccountService.get(new QueryFilter(ExCoinAccount.class)
								.addFilter("customerId=", releaseAdvertisement.getCustomerId()).addFilter("coinCode=", coinCode));
						exCoinAccountGua.setColdMoney(exCoinAccountGua.getColdMoney().subtract(bs));// ??????
						exCoinAccountGua.setHotMoney(exCoinAccountGua.getHotMoney().add(bs));// ????????????
						exCoinAccountService.update(exCoinAccountGua);*/
					//}
					//exCoinAccountService.update(exCoinAccount);// ????????????
				}
				//??????????????????
				//BigDecimal count=new BigDecimal(tradeMoney).divide(releaseAdvertisement.getTradeMoney(), keepDecimalForCoin, BigDecimal.ROUND_HALF_DOWN);
				//releaseAdvertisement.setCoinNumMax(releaseAdvertisement.getCoinNumMax().subtract(count));
				/*int i=15*60;
				try {
					i = Integer.parseInt(releaseAdvertisement.getPaymentTerm())*60;
				}catch (Exception e){}*/
				//????????????,???????????????????????????
				//redisService.save("otcReleaseAdvertisementOrderCoinCount:"+otcAppTransaction.getTransactionNum(),count.toString(),i);

				releaseAdvertisement.setTransactionNum(releaseAdvertisement.getTransactionNum()+1);
				releaseAdvertisementService.update(releaseAdvertisement);
				
				//redisService.save("otc:tradeNum:" + otcAppTransaction.getTransactionNum(), otcAppTransaction.getTransactionNum(),60 * Integer.parseInt(releaseAdvertisement.getPaymentTerm()));
				//??????zset??????
				redisService.zadd("otc:tradeNum", System.currentTimeMillis() / 1000 + Integer.parseInt(releaseAdvertisement.getPaymentTerm()) * 60, otcAppTransaction.getTransactionNum());
			} else if ("2".equals(transactionMode)) {// ??? - ??????
				BigDecimal fee=new BigDecimal("0");
				if (0 == eatFeeType) {// ??????
					fee=eatFee;
				} else if (1 == eatFeeType) {
					fee = eatFee.multiply(new BigDecimal(coinCodeMoney))
							.divide(new BigDecimal(100)).setScale(keepDecimalForCoin, BigDecimal.ROUND_DOWN);
				}
				if (exDigitalmoneyAccountRedis.getHotMoney().compareTo(BigDecimal.ZERO) <=0 || exDigitalmoneyAccountRedis.getHotMoney().compareTo(new BigDecimal(coinCodeMoney).add(fee)) < 0) {
					return new JsonResult().setSuccess(false).setMsg("nindebibuzu");
				}

				// ??????????????? ???????????????
				// ?????????
				BigDecimal bs = new BigDecimal(coinCodeMoney);
				//???????????????

				releaseAdvertisement.setCoinNumMax(releaseAdvertisement.getCoinNumMax().subtract(bs));
				if (0 == eatFeeType) {// ??????

					otcAppTransaction.setSellfee(eatFee);
					
					otcService.publish(exDigitalmoneyAccountRedis.getId(), bs.add(eatFee), 2, 1, "", 62);
					otcService.publish(exDigitalmoneyAccountRedis.getId(), (bs.add(eatFee)).multiply(new BigDecimal(-1)), 1, 1, "", 62);

				} else if (1 == eatFeeType) {// ?????????
					BigDecimal EatFee = eatFee.multiply(new BigDecimal(coinCodeMoney))
							.divide(new BigDecimal(100)).setScale(keepDecimalForCoin, BigDecimal.ROUND_DOWN);

					otcAppTransaction.setSellfee(EatFee);
					
					otcService.publish(exDigitalmoneyAccountRedis.getId(), bs.add(EatFee), 2, 1, "", 62);
					otcService.publish(exDigitalmoneyAccountRedis.getId(), (bs.add(EatFee)).multiply(new BigDecimal(-1)), 1, 1, "", 62);
				} else {
					otcService.publish(exDigitalmoneyAccountRedis.getId(), bs, 2, 1, "", 62);
					otcService.publish(exDigitalmoneyAccountRedis.getId(), bs.multiply(new BigDecimal(-1)), 1, 1, "", 62);
				}
				/*
				 * if (0==exCoinParameter.getPutFeeType()) {
				 * appTransaction.setBuyfee(exCoinParameter.getPutFee()); }
				 * else if (1==exCoinParameter.getEatFeeType()) {
				 * appTransaction.setBuyfee(exCoinParameter.getPutFee().
				 * multiply(new BigDecimal(coinCodeMoney)) .divide(new
				 * BigDecimal(100)).setScale(2)); }
				 */
				// ????????????				
				//releaseAdvertisement.setState(1);
				BigDecimal price;
				//??????coinCode??????????????????????????????--????????????????????????
				if (releaseAdvertisement.getIsFixed() == 1) {// ???????????????
					price=releaseAdvertisement.getTradeMoney();
				}else{
					//????????????????????????
					price = releaseAdvertisement.getTradeMoney().multiply(
							yi.add(releaseAdvertisement.getPremium().divide(yibai))).setScale(2, BigDecimal.ROUND_HALF_DOWN);
				}
				releaseAdvertisement.setTradeMoneyMax(releaseAdvertisement.getCoinNumMax().multiply(price).setScale(2, BigDecimal.ROUND_HALF_DOWN));
				//releaseAdvertisement.setPayType(payType);
				releaseAdvertisement.setPayTypeRemake(editor);
				
				if(releaseAdvertisement.getCoinNumMax().compareTo(releaseAdvertisement.getCoinNumMin())<0){
					releaseAdvertisement.setState(1);//????????????
					releaseAdvertisement.setStatus(0);
				}
				releaseAdvertisement.setTransactionNum(releaseAdvertisement.getTransactionNum()+1);
				releaseAdvertisementService.update(releaseAdvertisement);
				otcAppTransaction.setStatus(2);
				/*redisService.save("otc:tradeNum:" + otcAppTransaction.getTransactionNum(), otcAppTransaction.getTransactionNum(),
						Integer.valueOf(releaseAdvertisement.getPaymentTerm().replaceAll("min","")) * 60);*/
				redisService.zadd("otc:tradeNum", System.currentTimeMillis() / 1000 + Integer.valueOf(releaseAdvertisement.getPaymentTerm()) * 60, otcAppTransaction.getTransactionNum());
			}

			otcAppTransaction.setSellUserId(sellId);
			otcAppTransaction.setCoinCode(coinCode);
			otcAppTransaction.setTransactionMode(Integer.valueOf(transactionMode));
			otcAppTransaction.setTradeNum(new BigDecimal(coinCodeMoney));
			otcAppTransaction.setTradeMoney(new BigDecimal(tradeMoney));

			otcAppTransaction.setCreated(new Date());
			otcAppTransaction.setRemark(remark);
			otcAppTransaction.setAdvertisementId(releaseId);
			otcAppTransaction.setPayType(payType);
			if(releaseAdvertisement.getIsFixed()==0){
				otcAppTransaction.setTransactionType(2);
			}else if(releaseAdvertisement.getIsFixed()==1){//??????
				otcAppTransaction.setTransactionType(1);
			}
			
			//????????????????????????  0
			otcAppTransaction.setSellIsDeleted(0);
			otcAppTransaction.setBuyIsDeleted(0);
			
			//???-???????????????????????????
			if("2".equals(transactionMode)){
				//???????????????????????????????????????
				//?????????????????????????????????
				if(payType != null && !"".equals(payType)){
					AppBankCard appBankCard = null;
					if(payType.contains("1")){//?????????
						appBankCard = otcService.selectByParameter(userid, 1, 1);
						if(appBankCard != null){
							//??????????????????
							otcAppTransaction.setBankId(appBankCard.getId());
							otcAppTransaction.setBankNumber(appBankCard.getCardNumber());
						}
					}
					if(payType.contains("2")){//?????????
						appBankCard = otcService.selectByParameter(userid, 2, 1);
						if(appBankCard != null){
							otcAppTransaction.setAlipayId(appBankCard.getId());
							//?????????????????????
							otcAppTransaction.setAlipayAccount(appBankCard.getCardNumber());
							//??????????????????
							otcAppTransaction.setAlipayThingUrl(appBankCard.getThingUrl());
						}				
					}
					if(payType.contains("3")){//??????
						appBankCard = otcService.selectByParameter(userid, 3, 1);
						if(appBankCard != null){
							otcAppTransaction.setWechatId(appBankCard.getId());
							//??????????????????
							otcAppTransaction.setWechatAccount(appBankCard.getCardNumber());
							//??????????????????
							otcAppTransaction.setWechatThingUrl(appBankCard.getThingUrl());
						}
					}
				}
			}
			String random = RandomStringUtils.random(6, false, true);
			otcAppTransaction.setPaymentCode(random);
			otcAppTransaction.setLegalCurrency(releaseAdvertisement.getLegalCurrency());
			otcAppTransactionService.save(otcAppTransaction);

			Map<String, String> map = new HashMap<String, String>();
			map.put("transactionNum", otcAppTransaction.getTransactionNum());
			map.put("referenceNum", otcAppTransaction.getReferenceNum());
			return new JsonResult().setSuccess(true).setObj(map);
		} catch (Exception e) {
			e.printStackTrace();
			return new JsonResult().setSuccess(false);
		}
	}
	
	@Override
	public List<AppOrderSpeakRemote> selectOrderSpeak(Long orderId) {
    	QueryFilter qf = new QueryFilter(AppOrderSpeak.class).addFilter("orderId=", orderId);
		List<AppOrderSpeak> list = appOrderSpeakService.find(qf);
		List<AppOrderSpeakRemote> beanList = ObjectUtil.beanList(list, AppOrderSpeakRemote.class);
		return beanList;
	}
	
	@Override
	public void addSellOrderSpeak(String orderId, Long buyId, Long sellId, String sellSpeak){
		AppOrderSpeak appOrderSpeak = new AppOrderSpeak();
		appOrderSpeak.setBuyId(buyId);
		appOrderSpeak.setSellId(sellId);
		appOrderSpeak.setSellSpeak(sellSpeak);
		appOrderSpeak.setOrderId(orderId);
		appOrderSpeakService.save(appOrderSpeak);
	}
	
	@Override
	public FrontPage userAppTransaction(Map<String, String> map) {
		Page<AppTransaction> page = PageFactory.getPage(map);

		QueryFilter qf = new QueryFilter(OtcAppTransaction.class);
		if (StringUtil.isNotEmpty(map.get("buyUserId"))) {
			qf.addFilter("buyUserId=", map.get("buyUserId"));
		}
		if (StringUtil.isNotEmpty(map.get("sellUserId"))) {
			qf.addFilter("sellUserId=", map.get("sellUserId"));
		}
		if (StringUtil.isNotEmpty(map.get("transactionNum"))) {
			qf.addFilter("transactionNum_like", "%"+map.get("transactionNum")+"%");
		}
		if (StringUtil.isNotEmpty(map.get("status"))) {
			if ("3".equals(map.get("status"))) {
				qf.addFilter("status_in", "3,6,7");
			} else if ("4".equals(map.get("status"))) {
				qf.addFilter("status_in", "4,9,10,11,12,15,16");
			} else {
				qf.addFilter("status=", map.get("status"));
			}
		}
		//?????????????????????
		if(StringUtil.isNotEmpty(map.get("buyIsDeleted"))){
			qf.addFilter("buyIsDeleted=", 0);
		}
		if(StringUtil.isNotEmpty(map.get("sellIsDeleted"))){
			qf.addFilter("sellIsDeleted=", 0);
		}
		
		qf.setOrderby("created desc");
		List<OtcAppTransaction> list = otcAppTransactionService.find(qf);
		FrontPage userAppTransaction = new FrontPage(list, page.getTotal(), page.getPages(), page.getPageSize());
		// FrontPage userAppTransaction =
		// appTransactionService.userAppTransaction(map);
		List<OtcAppTransaction> list1 = userAppTransaction.getRows();
		List<OtcAppTransactionRemote> beanList = ObjectUtil.beanList(list1, OtcAppTransactionRemote.class);
		if(beanList != null && beanList.size() > 0){
			for(OtcAppTransactionRemote att : beanList){
				if(att != null && att.getTransactionNum() != null){
					//???????????????id
					QueryFilter filter = new QueryFilter(AppAppeal.class);
					filter.addFilter("transactionNum=", att.getTransactionNum());
					AppAppeal appeal = appAppealService.get(filter);
					if(appeal != null && appeal.getUserId() != null){
						att.setAppealId(appeal.getUserId()); //????????????????????????id
					}
				}
			}
		}
		userAppTransaction.setRows(beanList);
		return userAppTransaction;
	}
	
	@Override
	public synchronized JsonResult addAppeal(Long userId, String tradeNum, String appeal, String content, String thingUrl,String transactionMode) {
		try {
			OtcAppTransaction otcAppTransaction = otcAppTransactionService
					.get(new QueryFilter(OtcAppTransaction.class).addFilter("transactionNum=", tradeNum));
			if (otcAppTransaction != null) {
				AppAppeal appAppeal = appAppealService.get(new QueryFilter(AppAppeal.class).addFilter("transactionNum=", tradeNum));
				if(appAppeal!=null){
					userId = appAppeal.getUserId();
					if("1".equals(transactionMode)){//??????????????????
						appAppeal.setAppeal(appeal);
						appAppeal.setContent(content);
						appAppeal.setUserId(userId);
						appAppeal.setTransactionNum(tradeNum);
						appAppeal.setThingUrl(thingUrl);
						appAppealService.update(appAppeal);

						return new JsonResult().setSuccess(true);
					}else{
						appAppeal.setAppealSell(appeal);
						appAppeal.setContentSell(content);
						appAppeal.setUserId(userId);
						appAppeal.setTransactionNum(tradeNum);
						appAppeal.setThingUrlSell(thingUrl);
						appAppealService.update(appAppeal);

						return new JsonResult().setSuccess(true);
					}
				}else{
					if("1".equals(transactionMode)){//??????????????????
						AppAppeal appAppealn = new AppAppeal();
						appAppealn.setAppeal(appeal);
						appAppealn.setContent(content);
						appAppealn.setUserId(userId);
						appAppealn.setTransactionNum(tradeNum);
						appAppealn.setThingUrl(thingUrl);
						appAppealService.save(appAppealn);

						otcAppTransaction.setStatus(15);
						otcAppTransactionService.update(otcAppTransaction);
						return new JsonResult().setSuccess(true);
					}else{
						AppAppeal appAppealn = new AppAppeal();
						appAppealn.setAppealSell(appeal);
						appAppealn.setContentSell(content);
						appAppealn.setUserId(userId);
						appAppealn.setTransactionNum(tradeNum);
						appAppealn.setThingUrlSell(thingUrl);
						appAppealService.save(appAppealn);

						otcAppTransaction.setStatus(16);
						otcAppTransactionService.update(otcAppTransaction);
						return new JsonResult().setSuccess(true);
					}
				}
			}
			return new JsonResult().setSuccess(false).setMsg("dingdanbucunzai");
		} catch (Exception e) {
			return new JsonResult().setSuccess(false).setMsg("dingdanyichang");
		}
	}
	
	@Override
	public JsonResult appealInfor(String tradeNum) {
		Map<String, Object> map = new HashMap<String, Object>();

		OtcAppTransaction otcAppTransaction = otcAppTransactionService
				.get(new QueryFilter(OtcAppTransaction.class).addFilter("transactionNum=", tradeNum));
		if (otcAppTransaction != null) {
			// ????????????
			OtcAppTransactionRemote otcappTransactionRemote = ObjectUtil.bean2bean(otcAppTransaction,
					OtcAppTransactionRemote.class);
			if (otcAppTransaction.getStatus() == 1) {
				otcappTransactionRemote.setStateZHCN("daiquerendingdan");
			} else if (otcAppTransaction.getStatus() == 2) {
				otcappTransactionRemote.setStateZHCN("yiquerendingdan");
			} else if (otcAppTransaction.getStatus() == 3) {
				otcappTransactionRemote.setStateZHCN("yiwanchengzhifu");
			} else if (otcAppTransaction.getStatus() == 4) {
				otcappTransactionRemote.setStateZHCN("shensuzhongdaihuifu");
			} else if (otcAppTransaction.getStatus() == 5) {
				otcappTransactionRemote.setStateZHCN("yiquxiao");
			} else if (otcAppTransaction.getStatus() == 6) {
				otcappTransactionRemote.setStateZHCN("shenqingtuikuanzhong");
			} else if (otcAppTransaction.getStatus() == 7) {
				otcappTransactionRemote.setStateZHCN("tuikuanyibohui");
			} else if (otcAppTransaction.getStatus() == 8) {
				otcappTransactionRemote.setStateZHCN("shensuwancheng");
			} else if (otcAppTransaction.getStatus() == 9) {
				otcappTransactionRemote.setStateZHCN("shensuchenggongdaiqueren");
			} else if (otcAppTransaction.getStatus() == 10) {
				otcappTransactionRemote.setStateZHCN("shensushibaidaiqueren");
			} else if (otcAppTransaction.getStatus() == 11) {
				otcappTransactionRemote.setStateZHCN("pingtaitongguoshensu");
			} else if (otcAppTransaction.getStatus() == 12) {
				otcappTransactionRemote.setStateZHCN("pingtaibohuishensu");
			} else if (otcAppTransaction.getStatus() == 13) {
				otcappTransactionRemote.setStateZHCN("tuikuanchenggong");
			} else if (otcAppTransaction.getStatus() == 14) {
				otcappTransactionRemote.setStateZHCN("yiwancheng");
			} else if (otcAppTransaction.getStatus() == 15) {
				otcappTransactionRemote.setStateZHCN("maijiashensuzhong");
			} else if (otcAppTransaction.getStatus() == 16) {
				otcappTransactionRemote.setStateZHCN("maijiashensuzhong1");
			}
			String str = "";
			String[] split = otcappTransactionRemote.getPayType().split(",");
			if (split.length > 0) {
				for (int i = 0; i < split.length; i++) {
					if ("1".equals(split[i])) {
						str += "yinhangzhuanzhang,";
					} else if ("2".equals(split[i])) {
						str += "zhifubao2,";
					} else if ("3".equals(split[i])) {
						str += "weixinzhifu,";
					}
				}
			}
			if (str.length() > 1) {
				str = str.substring(0, str.length() - 1);
			}
			otcappTransactionRemote.setPayType(str);
			//???????????????????????????????????????
			int keepDecimalForCoin = keepDecimalForCoin(otcappTransactionRemote.getCoinCode());
			
			otcappTransactionRemote.setTradeMoney(otcappTransactionRemote.getTradeMoney().setScale(keepDecimalForCoin, BigDecimal.ROUND_DOWN));
			otcappTransactionRemote.setTradeNum(otcappTransactionRemote.getTradeNum().setScale(keepDecimalForCoin, BigDecimal.ROUND_DOWN));
			
			
			
			map.put("app", otcappTransactionRemote);
			// ????????????
			ReleaseAdvertisement releaseAdvertisement = releaseAdvertisementService
					.get(otcappTransactionRemote.getAdvertisementId());
			map.put("releaseId", otcappTransactionRemote.getAdvertisementId());
			BigDecimal tradeMoney=new BigDecimal("0");
			if (releaseAdvertisement != null) {
				map.put("payTypeRemake", releaseAdvertisement.getPayTypeRemake());
				map.put("isFixed", releaseAdvertisement.getIsFixed());
				map.put("paymentTerm", releaseAdvertisement.getPaymentTerm());
				//??????
				map.put("remark", releaseAdvertisement.getRemark());

				if (releaseAdvertisement.getIsFixed() == 0) {// ????????????
					BigDecimal yi = new BigDecimal("1");// 1
					BigDecimal yibai = new BigDecimal("100");// 100
					BigDecimal premiumBD = releaseAdvertisement.getPremium();
					BigDecimal shichangjiageBD = releaseAdvertisement.getTradeMoney();

					if (releaseAdvertisement.getTransactionMode() == 1) {// ??????
																			// ??????
						BigDecimal price = shichangjiageBD
								.multiply(yi.subtract(premiumBD.divide(yibai, 4, BigDecimal.ROUND_DOWN))); // ????????????*(1-??????????????????)????????????

						//map.put("tradeMoney", price.toString());
						tradeMoney=price;

					} else {// ?????? ??????
						BigDecimal price = shichangjiageBD
								.multiply(yi.add(premiumBD.divide(yibai, 4, BigDecimal.ROUND_DOWN)));// ????????????*(1+??????????????????)????????????

						//map.put("tradeMoney", price.toString());
						tradeMoney=price;
					}
				} else {
					//map.put("tradeMoney", releaseAdvertisement.getTradeMoney().toString());
					tradeMoney=releaseAdvertisement.getTradeMoney();
				}
				tradeMoney=tradeMoney.setScale(keepDecimalForCoin, BigDecimal.ROUND_DOWN);
				map.put("tradeMoney", tradeMoney.toString());
			}
			// ????????????
			AppAppeal appAppeal = appAppealService.get(
					new QueryFilter(AppAppeal.class).addFilter("transactionNum=", otcAppTransaction.getTransactionNum()));
			if (appAppeal != null) {
				AppAppealRemote bean2bean = ObjectUtil.bean2bean(appAppeal, AppAppealRemote.class);
				if (StringUtil.isNotEmpty(bean2bean.getThingUrl())) {
					String[] spl = bean2bean.getThingUrl().split(",");
					List<String> list = new LinkedList<String>();
					for (int i = 0; i < spl.length; i++) {
						list.add(OssUtil.getUrl(spl[i]));

					}
					bean2bean.setImgUrl(list);
				}
				if (StringUtil.isNotEmpty(bean2bean.getThingUrlSell())) {
					String[] spl = bean2bean.getThingUrlSell().split(",");
					List<String> list = new LinkedList<String>();
					for (int i = 0; i < spl.length; i++) {
						list.add(OssUtil.getUrl(spl[i]));

					}
					bean2bean.setImgUrlSell(list);
				}
				map.put("appAppealRemote", bean2bean);
			}
		}
		return new JsonResult().setSuccess(true).setObj(map);
	}
	
	@Override
	public AppAppealRemote getAppAppealByNum(String transactionNum){
		AppAppeal appAppeal = appAppealService.get(
				new QueryFilter(AppAppeal.class).addFilter("transactionNum=", transactionNum));
		if (appAppeal != null) {
			AppAppealRemote bean2bean = ObjectUtil.bean2bean(appAppeal, AppAppealRemote.class);
			if (StringUtil.isNotEmpty(bean2bean.getThingUrl())) {
				String[] spl = bean2bean.getThingUrl().split(",");
				List<String> list = new LinkedList<String>();
				for (int i = 0; i < spl.length; i++) {
					list.add(spl[i]);
				}
				bean2bean.setImgUrl(list);
			}
			if (StringUtil.isNotEmpty(bean2bean.getThingUrlSell())) {
				String[] spl = bean2bean.getThingUrlSell().split(",");
				List<String> list = new LinkedList<String>();
				for (int i = 0; i < spl.length; i++) {
					list.add(spl[i]);
				}
				bean2bean.setImgUrlSell(list);
			}
			return bean2bean;
		}
		return null;
	}
	
	@Override
	public JsonResult cancelAppeal(String transactionNum){
		//???????????????????????????????????????????????????
		QueryFilter filter = new QueryFilter(OtcAppTransaction.class);
		filter.addFilter("transactionNum=", transactionNum);
		OtcAppTransaction otcAppTransaction = otcAppTransactionService.get(filter);
		//????????????
		if(otcAppTransaction != null){
			otcAppTransaction.setStatus(3);//????????????
			otcAppTransactionService.update(otcAppTransaction);
		}
		
		//????????????????????????????????????????????????
		QueryFilter qf = new QueryFilter(AppAppeal.class);
		qf.addFilter("transactionNum=", transactionNum);
		AppAppeal appAppeal = appAppealService.get(qf);
		boolean success = false;
		if(appAppeal != null){
			success = appAppealService.delete(appAppeal.getId());
		}
		return new JsonResult().setSuccess(success);
	}
	
	@Override
	public JsonResult refundAndReject(String tradeNum, String type, String transactionMode, String content,
			Long userid) {
		OtcAppTransaction otcAppTransaction = otcAppTransactionService
				.get(new QueryFilter(OtcAppTransaction.class).addFilter("transactionNum=", tradeNum));
		if (otcAppTransaction != null) {
			if (transactionMode.equals("2")) {// ??????
				if ("6".equals(type)) {// ????????????

					otcAppTransaction.setStatus(Integer.valueOf(type));
					otcAppTransactionService.update(otcAppTransaction);
					// ??????

					ReleaseAdvertisement releaseAdvertisement = releaseAdvertisementService
							.get(otcAppTransaction.getAdvertisementId());

					if (null != releaseAdvertisement) {
						int keepDecimalForCoin = keepDecimalForCoin(releaseAdvertisement.getCoinCode());

						if (releaseAdvertisement.getCustomerId().equals(userid)) {// ?????????
																					// ???????????????
																					// ????????????
																					// ????????????????????????
							ExDigitalmoneyAccountRedis coinAccountRedis = getCoinAccountRedis(releaseAdvertisement.getCustomerId(), releaseAdvertisement.getCoinCode());
							
							if (releaseAdvertisement.getIsFixed() == 1) {// ???????????????
								// ????????? ?????? ??????=?????????
								BigDecimal bs = releaseAdvertisement.getTradeMoneyMax()
										.divide(releaseAdvertisement.getTradeMoney(), keepDecimalForCoin, BigDecimal.ROUND_DOWN);
								
								otcService.publish(coinAccountRedis.getId(), bs.multiply(new BigDecimal(-1)), 2, 1, "", 63);
								otcService.publish(coinAccountRedis.getId(), bs, 1, 1, "", 63);
								
								//exCoinAccount.setColdMoney(exCoinAccount.getColdMoney().subtract(bs));// ??????
								//exCoinAccount.setHotMoney(exCoinAccount.getHotMoney().add(bs));// ????????????
							} else {
								BigDecimal yi = new BigDecimal("1");// 1
								BigDecimal yibai = new BigDecimal("100");// 100
								BigDecimal price = releaseAdvertisement.getTradeMoney()
										.multiply(yi.subtract(releaseAdvertisement.getPremium().divide(yibai))); // ????????????*(1-??????????????????)????????????

								// ????????? ?????? ??????=?????????
								BigDecimal bs = releaseAdvertisement.getTradeMoneyMax().divide(price, keepDecimalForCoin, BigDecimal.ROUND_DOWN);

								otcService.publish(coinAccountRedis.getId(), bs.multiply(new BigDecimal(-1)), 2, 1, "", 63);
								otcService.publish(coinAccountRedis.getId(), bs, 1, 1, "", 63);
								
								//exCoinAccount.setColdMoney(exCoinAccount.getColdMoney().subtract(bs));// ??????
								//exCoinAccount.setHotMoney(exCoinAccount.getHotMoney().add(bs));// ????????????
							}

						} else {// ????????? ????????? ????????? ???????????????
								// ???????????????????????????
							/*ExCoinParameter exCoinParameter = exCoinParameterService
									.get(new QueryFilter(ExCoinParameter.class).addFilter("coinCode=",
											releaseAdvertisement.getCoinCode()));*/
							ExDigitalmoneyAccountRedis coinAccountRedis = getCoinAccountRedis(releaseAdvertisement.getCustomerId(), releaseAdvertisement.getCoinCode());
							
							BigDecimal eatFee = new BigDecimal(0); // ???????????????
							int eatFeeType = 0;// ???????????????????????????
							
							String productinfoListall = redisService.get("otc:coinCodeList");
							JSONArray parseArray = JSON.parseArray(productinfoListall);
							for(int k=0;k<parseArray.size();k++){
								JSONObject jo = JSON.parseObject(parseArray.get(k).toString());
								if(jo.getString("coinCode").equals(releaseAdvertisement.getCoinCode())){
									if (jo.getInteger("eatFee")!=null) {
										eatFee = jo.getBigDecimal("eatFee");
									}
									if(jo.getInteger("eatFeeType")!=null){
										eatFeeType = jo.getInteger("eatFeeType");
									}
								}
							}
							
							if (eatFeeType == 0) {// ???????????????

								// ????????? ?????? ??????=?????????
								BigDecimal bs = otcAppTransaction.getTradeNum();
								
								otcService.publish(coinAccountRedis.getId(), (bs.add(eatFee)).multiply(new BigDecimal(-1)), 2, 1, "", 63);
								otcService.publish(coinAccountRedis.getId(), bs.add(eatFee), 1, 1, "", 63);

								/*exCoinAccount.setColdMoney(
										exCoinAccount.getColdMoney().subtract(bs.add(exCoinParameter.getEatFee())));// ??????
								exCoinAccount.setHotMoney(
										exCoinAccount.getHotMoney().add(bs.add(exCoinParameter.getEatFee())));// ????????????

								exCoinAccountService.update(exCoinAccount);// ????????????*/

							} else {// ??????????????????

								// ????????? ?????? ??????=?????????
								BigDecimal bs = otcAppTransaction.getTradeNum();
								
								otcService.publish(coinAccountRedis.getId(), (bs.add(bs.multiply(eatFee))).multiply(new BigDecimal(-1)), 2, 1, "", 63);
								otcService.publish(coinAccountRedis.getId(), bs.add(bs.multiply(eatFee)), 1, 1, "", 63);

								/*exCoinAccount.setColdMoney(exCoinAccount.getColdMoney()
										.subtract(bs.add(bs.multiply(exCoinParameter.getEatFee()))));// ??????
								exCoinAccount.setHotMoney(exCoinAccount.getHotMoney()
										.add(bs.add(bs.multiply(exCoinParameter.getEatFee()))));// ????????????

								exCoinAccountService.update(exCoinAccount);// ????????????*/
							}
						}
					}
					return new JsonResult().setSuccess(true).setMsg("tuikuanchenggong");
				} else if ("14".equals(type)) {// ???????????? ??????????????????
					otcAppTransaction.setStatus(Integer.valueOf(type));
					otcAppTransactionService.update(otcAppTransaction);

					// ??????
					ExDigitalmoneyAccountRedis coinAccountRedis = getCoinAccountRedis(otcAppTransaction.getSellUserId(), otcAppTransaction.getCoinCode());
					
					otcService.publish(coinAccountRedis.getId(), (otcAppTransaction.getTradeNum().add(otcAppTransaction.getSellfee())).multiply(new BigDecimal(-1)), 2, 1, "", 63);
					otcService.publish(coinAccountRedis.getId(), otcAppTransaction.getTradeNum().add(otcAppTransaction.getSellfee()), 1, 1, "", 63);
					// ???????????? //????????????
					/*sell.setColdMoney(sell.getColdMoney()
							.subtract(appTransaction.getTradeNum().add(appTransaction.getSellfee())));
					sell.setHotMoney(
							sell.getHotMoney().add(appTransaction.getTradeNum().add(appTransaction.getSellfee())));

					exCoinAccountService.update(sell);*/

					return new JsonResult().setSuccess(true).setMsg("shensuchenggongdaiqueren");
				} else if ("10".equals(type)) {
					return new JsonResult().setSuccess(true).setMsg("shensushibaidaiqueren");
				}
			} else if (transactionMode.equals("1")) {// ??????
				if ("6".equals(type)) {// ????????????
					AppAppeal appAppeal = new AppAppeal();
					appAppeal.setUserId(userid);
					appAppeal.setContent(content);
					appAppeal.setTransactionNum(tradeNum);
					appAppealService.save(appAppeal);
					otcAppTransaction.setStatus(Integer.valueOf(type));
					otcAppTransactionService.update(otcAppTransaction);
					return new JsonResult().setSuccess(true).setMsg("shenqingchenggongdaiqueren");
				}
			}
		}
		return new JsonResult().setSuccess(false).setMsg("dingdanbucunzai");
	}
	
	@Override
	public synchronized JsonResult cancleOrder(String tradeNum) {
		JsonResult j = new JsonResult();
		
		Long setnx = redisService.setnx("orderNo:" + tradeNum, "1");
		if(setnx!=null && setnx==1L){
			redisService.expire("orderNo:" + tradeNum, 5);

			QueryFilter qf = new QueryFilter(OtcAppTransaction.class);
			qf.addFilter("transactionNum=", tradeNum);
			OtcAppTransaction otcAppTransaction = otcAppTransactionService.get(qf);
			if (otcAppTransaction != null && (otcAppTransaction.getStatus() == 2 || otcAppTransaction.getStatus() == 3)) {
				otcAppTransaction.setStatus(5);
				otcAppTransactionService.update(otcAppTransaction);

				RedisModel rm = new RedisModel();
				rm.setUserId(otcAppTransaction.getCustomerId());
				rm.setCoinCode(otcAppTransaction.getCoinCode());
				rm.setTransactionMode(otcAppTransaction.getTransactionMode());
				rm.setTradeNum(otcAppTransaction.getTransactionNum());

				//????????????
				redisService.rpush("otc:queue", JSON.toJSONString(rm));
				//????????????
				redisService.publish("otcCompletionRate", JSON.toJSONString(rm));

				// ?????????coin???????????????
				String coinPercent = "";// ??????????????????
				
				String productinfoListall = redisService.get("otc:coinCodeList");
				JSONArray parseArray = JSON.parseArray(productinfoListall);
				for(int k=0;k<parseArray.size();k++){
					JSONObject jo = JSON.parseObject(parseArray.get(k).toString());
					if(jo.getString("coinCode").equals(otcAppTransaction.getCoinCode())){
						if(jo.getString("coinPercent")!=null){
							coinPercent = jo.getString("coinPercent");
						}
					}
				}



				// ????????????
				ReleaseAdvertisement releaseAdvertisement = releaseAdvertisementService
						.get(otcAppTransaction.getAdvertisementId());
				if (releaseAdvertisement != null) {
					/*if(releaseAdvertisement.getStatus()==0){//???????????????????????????????????????????????????
						
					}else{
						releaseAdvertisement.setState(0);
						releaseAdvertisement.setTradeMoneyMax(releaseAdvertisement.getTradeMoneyMax().add(appTransaction.getTradeMoney()));
						releaseAdvertisementService.update(releaseAdvertisement);
					}*/
					//???????????????
					BigDecimal yi = new BigDecimal("1");// 1
					BigDecimal yibai = new BigDecimal("100");// 100
					BigDecimal price=new BigDecimal("0");
					//??????coinCode??????????????????????????????--????????????????????????
					if (releaseAdvertisement.getIsFixed() == 1) {// ???????????????
						price=releaseAdvertisement.getTradeMoney();
					}else{
						//???????????????????????? //????????????(1????????????,2????????????,3????????????)
						if(releaseAdvertisement.getTransactionMode() == 2){
							price = releaseAdvertisement.getTradeMoney().multiply(
									yi.add(releaseAdvertisement.getPremium().divide(yibai))).setScale(2, BigDecimal.ROUND_HALF_DOWN);
						}else if(releaseAdvertisement.getTransactionMode() == 1){

							price = releaseAdvertisement.getTradeMoney().multiply(
									yi.subtract(releaseAdvertisement.getPremium().divide(yibai))).setScale(2, BigDecimal.ROUND_HALF_DOWN);
						}
					}
					if(otcAppTransaction.getTradeMoney().compareTo(releaseAdvertisement.getTradeMoney().multiply(new BigDecimal(coinPercent).divide(new BigDecimal(100))))>=0){
						//?????????????????????????????????
						releaseAdvertisement.setCoinNumMax(releaseAdvertisement.getCoinNumMax().add( otcAppTransaction.getTradeNum() ));
						releaseAdvertisement.setTradeMoneyMax(releaseAdvertisement.getCoinNumMax().multiply(price).setScale(2, BigDecimal.ROUND_HALF_DOWN));
						//???????????????
						releaseAdvertisement.setTransactionNum( releaseAdvertisement.getTransactionNum() - 1);
						//???????????????????????????????????????????????????????????????????????????
						if( releaseAdvertisement.getCoinNumMax().compareTo( releaseAdvertisement.getCoinNumMin() ) >= 0 && releaseAdvertisement.getStatus() == 0 ){
							releaseAdvertisement.setStatus(1);
							releaseAdvertisement.setState(0);
							//????????? ?????????????????? ????????????
							ExDigitalmoneyAccountRedis coinAccountRedis = getCoinAccountRedis(releaseAdvertisement.getCustomerId(), releaseAdvertisement.getCoinCode());
							String s = redisService.get("otc:ReleaseAdvertisementAfterColse:" + tradeNum);
							if(! StringUtils.isEmpty(s) ){
								BigDecimal bs = new BigDecimal(s);
								otcService.publish(coinAccountRedis.getId(), bs, 2, 1, "", 64);
								otcService.publish(coinAccountRedis.getId(), bs.multiply(new BigDecimal(-1)), 1, 1, "", 64);
								redisService.delete("otc:ReleaseAdvertisementAfterColse:" + tradeNum);
							}
						}
						releaseAdvertisementService.update(releaseAdvertisement);

					}else{
						//????????? ?????????????????? ????????????
						ExDigitalmoneyAccountRedis coinAccountRedis = getCoinAccountRedis(releaseAdvertisement.getCustomerId(), releaseAdvertisement.getCoinCode());
						
						otcService.publish(coinAccountRedis.getId(), otcAppTransaction.getTradeNum().multiply(new BigDecimal(-1)), 2, 1, "", 64);
						otcService.publish(coinAccountRedis.getId(), otcAppTransaction.getTradeNum(), 1, 1, "", 64);
						
						/*ExCoinAccount exCoinAccount = exCoinAccountService.get(releaseAdvertisement.getAccountId());
						exCoinAccount.setColdMoney(exCoinAccount.getColdMoney().subtract(appTransaction.getTradeNum()));
						exCoinAccount.setHotMoney(exCoinAccount.getHotMoney().add(appTransaction.getTradeNum()));
						exCoinAccountService.update(exCoinAccount);*/
					}
				}
				//????????????????????????????????????????????????
				if(otcAppTransaction.getTransactionMode() == 2){//????????????
					//??????????????????
					BigDecimal all = otcAppTransaction.getSellfee().add(otcAppTransaction.getTradeNum());
					
					ExDigitalmoneyAccountRedis coinAccountRedis = getCoinAccountRedis(otcAppTransaction.getSellUserId(), otcAppTransaction.getCoinCode());
					
					otcService.publish(coinAccountRedis.getId(), all.multiply(new BigDecimal(-1)), 2, 1, "", 64);
					otcService.publish(coinAccountRedis.getId(), all, 1, 1, "", 64);
					
					/*ExCoinAccount exCoinAccount = exCoinAccountService.get(new QueryFilter(ExCoinAccount.class).addFilter("customerId=", appTransaction.getSellUserId()).addFilter("coinCode=", appTransaction.getCoinCode()));
					exCoinAccount.setColdMoney(exCoinAccount.getColdMoney().subtract(all));
					exCoinAccount.setHotMoney(exCoinAccount.getHotMoney().add(all));
					exCoinAccountService.update(exCoinAccount);*/
				}
			}
			redisService.delete("orderNo:" + tradeNum);

			try{
				//??????24???????????????????????????3?????????30??????????????????????????????????????????????????????24??????????????????????????????
				if( otcAppTransaction.getTransactionMode() == 1 ){
					//24?????????????????????5??????
					// int i=60*60*24;
					int i=60*5;
					String ss = redisService.get("otc:cancleOrder:"+otcAppTransaction.getBuyUserId());
					if( StringUtils.isEmpty(ss)){
						//???????????????24??????
						redisService.save("otc:cancleOrder:"+otcAppTransaction.getBuyUserId(),"1",i);
					}else{
						int parseInt = Integer.parseInt(ss)+1;
						if( parseInt > 2){
							redisService.save("otcCanNotBuy:"+otcAppTransaction.getBuyUserId(),"0",i);
						}
						//??????????????????
						Long time=redisService.getKeyTime("otc:cancleOrder:"+otcAppTransaction.getBuyUserId());
						if( time == null ){
							redisService.save("otc:cancleOrder:"+otcAppTransaction.getBuyUserId(),parseInt+"",i);
						}else{
							redisService.save("otc:cancleOrder:"+otcAppTransaction.getBuyUserId(),parseInt+"",time.intValue());
						}
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}

			
			return j.setSuccess(true);
		}
		return j.setSuccess(false).setMsg("qingbuyaochongfuquxiao");
	}
	
	
	@Override
	public JsonResult orderCompleted(String tradeNum, String paymentTerm) {
		QueryFilter qf = new QueryFilter(OtcAppTransaction.class);
		qf.addFilter("transactionNum=", tradeNum);
		OtcAppTransaction otcAppTransaction = otcAppTransactionService.get(qf);
		
		//????????????????????????
		if(otcAppTransaction != null && otcAppTransaction.getStatus() == 5){//?????????????????????
			return new JsonResult().setSuccess(false).setMsg("dingdanyishixiao");
		}
		
		if (otcAppTransaction != null) {
                otcAppTransaction.setStatus(14);// ????????????
                otcAppTransaction.setPayTime(new Date());
                otcAppTransactionService.update(otcAppTransaction);

				RedisModel rm = new RedisModel();
				rm.setUserId(otcAppTransaction.getCustomerId());
				rm.setCoinCode(otcAppTransaction.getCoinCode());
				rm.setTransactionMode(otcAppTransaction.getTransactionMode());
			rm.setTradeNum(otcAppTransaction.getTransactionNum());

                //????????????
				redisService.rpush("otc:queue", JSON.toJSONString(rm));
				//????????????
				redisService.publish("otcCompletionRate", JSON.toJSONString(rm));
			try{
                //???????????????????????????????????????????????????redis?????????
				redisService.delete("otc:ReleaseAdvertisementAfterColse:" + tradeNum);
			}catch (Exception e){}
			// ??????????????????????????????????????????????????????????????????????????????????????????
			// ????????? ????????????????????? ????????????
			// ??????
			// ex_coin_account
			ExDigitalmoneyAccountRedis coinAccountRedisSell = getCoinAccountRedis(otcAppTransaction.getSellUserId(), otcAppTransaction.getCoinCode());
			// ??????????????? ?????????????????????

			ReleaseAdvertisement r = releaseAdvertisementService.get(otcAppTransaction.getAdvertisementId());
			if (r.getCustomerId().equals(otcAppTransaction.getSellUserId())) {// ?????????????????????
				if (r.getIsFixed() == 1) {// ???????????????
					//?????????????????????
					otcService.publish(coinAccountRedisSell.getId(), otcAppTransaction.getTradeNum().multiply(new BigDecimal(-1)), 2, 1, "", 65);
				} else {
					BigDecimal yi = new BigDecimal("1");// 1
					BigDecimal yibai = new BigDecimal("100");// 100
					BigDecimal price = r.getTradeMoney()
							.multiply(yi.subtract(r.getPremium().divide(yibai, 4, BigDecimal.ROUND_DOWN))); // ????????????*(1-??????????????????)????????????

					//?????????????????????
					otcService.publish(coinAccountRedisSell.getId(), otcAppTransaction.getTradeNum().multiply(new BigDecimal(-1)), 2, 1, "", 65);
				}

			} else {
				//??????????????????????????????????????????
				//???????????????????????????????????????
				otcService.publish(coinAccountRedisSell.getId(), (otcAppTransaction.getTradeNum().add(otcAppTransaction.getSellfee())).multiply(new BigDecimal(-1)), 2, 1, "", 65);
				//????????????????????????????????????????????????
				otcService.publish(coinAccountRedisSell.getId(), otcAppTransaction.getSellfee(), 1, 1, "", 65);
			}

			// ????????????????????????
			//sell.setHotMoney(sell.getHotMoney().subtract(appTransaction.getTradeNum()));

			// ????????? ?????????????????????
			// ??????
			ExDigitalmoneyAccountRedis coinAccountRedisBuy = getCoinAccountRedis(otcAppTransaction.getBuyUserId(), otcAppTransaction.getCoinCode());
			// ???????????? //????????????
			otcService.publish(coinAccountRedisBuy.getId(), otcAppTransaction.getTradeNum(), 1, 1, "", 65);

			// ?????????appTransaction.getCustomerId()??????????????????
			
			ExDigitalmoneyAccountRedis coinAccountRedisCustomer = getCoinAccountRedis(otcAppTransaction.getCustomerId(), otcAppTransaction.getCoinCode());
			// ?????????????????????
			otcService.publish(coinAccountRedisCustomer.getId(), otcAppTransaction.getSellfee().multiply(new BigDecimal(-1)), 1, 1, "", 65);

			// ???????????????
			// ex_coin_fee
			ExCoinFee exCoinFee = new ExCoinFee();
			exCoinFee.setCoinCode(otcAppTransaction.getCoinCode());
			exCoinFee.setCoinName(otcAppTransaction.getCoinCode());
			exCoinFee.setCustomerId(otcAppTransaction.getCustomerId());
			AppCustomer appCustomer = otcService.getAppCustomerById(otcAppTransaction.getCustomerId());
			if (appCustomer != null) {
				exCoinFee.setUserName(appCustomer.getUserName());
			}
			exCoinFee.setFee(otcAppTransaction.getSellfee());
			
			int eatFeeType = 0;// ???????????????????????????
			
			String productinfoListall = redisService.get("otc:coinCodeList");
			JSONArray parseArray = JSON.parseArray(productinfoListall);
			for(int k=0;k<parseArray.size();k++){
				JSONObject jo = JSON.parseObject(parseArray.get(k).toString());
				if(jo.getString("name").equals(otcAppTransaction.getCoinCode())){
					if(jo.getInteger("eatFeeType")!=null){
						eatFeeType = jo.getInteger("eatFeeType");
					}
				}
			}
			
			exCoinFee.setFeeType(eatFeeType);
			exCoinFee.setVolume(otcAppTransaction.getTradeNum());
			exCoinFee.setStatus(2);
			exCoinFeeService.save(exCoinFee);
			
			//?????????????????????
			//r.setStatus(0);
			releaseAdvertisementService.update(r);

			return new JsonResult().setSuccess(true);
		}
		return new JsonResult().setSuccess(false).setMsg("dingdanbucunzai");
	}
	
	@Override
	public JsonResult orderPayment(String tradeNum, String paymentTerm) {
		//????????????????????????
    	Double index = redisService.zscore("otc:tradeNum", tradeNum);
		if(index == null){//???????????????
			return new JsonResult().setSuccess(false).setMsg("dingdanyishixiao");
		}

		QueryFilter qf = new QueryFilter(OtcAppTransaction.class);
		qf.addFilter("transactionNum=", tradeNum);
		OtcAppTransaction otcAppTransaction = otcAppTransactionService.get(qf);
		if (otcAppTransaction != null) {
			otcAppTransaction.setStatus(3);// ????????????
			otcAppTransaction.setPayTime(new Date());
			otcAppTransactionService.update(otcAppTransaction);
			redisService.zrem("otc:tradeNum", tradeNum);
			return new JsonResult().setSuccess(true).setMsg("caozuochenggong");
		}
		return new JsonResult().setSuccess(false).setMsg("dingdanbucunzai");
	}
	
	@Override
	public JsonResult updateIsDeleted(String transactionNum, String type) {
		QueryFilter filter = new QueryFilter(OtcAppTransaction.class);
		filter.addFilter("transactionNum=", transactionNum);
		OtcAppTransaction otcAppTransaction = otcAppTransactionService.get(filter);
		if(otcAppTransaction != null){
			if("1".equals(type)){
				otcAppTransaction.setBuyIsDeleted(1);
			}else if("2".equals(type)){
				otcAppTransaction.setSellIsDeleted(1);
			}
			otcAppTransactionService.update(otcAppTransaction);
			return new JsonResult().setSuccess(true);
		}
		return new JsonResult().setSuccess(false);
	}
	
	@Override
	public synchronized JsonResult closeReleaseAdvertisement(Long releaseId) {
		Long setnx = redisService.setnx("Advertisement:" + releaseId, "1");
		if(setnx!=null && setnx==1L){
			redisService.expire("Advertisement:" + releaseId, 5);
			
			ReleaseAdvertisement releaseAdvertisement = releaseAdvertisementService.get(releaseId);

			if( releaseAdvertisement.getStatus() == 0){
                return new JsonResult().setSuccess(false).setMsg("ciguanggaoyiguanbi");
            }
			if (null != releaseAdvertisement) {
				int keepDecimalForCoin = keepDecimalForCoin(releaseAdvertisement.getCoinCode());
				
				ExDigitalmoneyAccountRedis coinAccountRedis = getCoinAccountRedis(releaseAdvertisement.getCustomerId(), releaseAdvertisement.getCoinCode());
				
				if (releaseAdvertisement.getTransactionMode() == 1 ) {// ?????? ??????

					if (releaseAdvertisement.getIsFixed() == 1) {// ???????????????
						// ????????? ?????? ??????=?????????
						/*BigDecimal bs = releaseAdvertisement.getTradeMoneyMax()
								.divide(releaseAdvertisement.getTradeMoney(), keepDecimalForCoin, BigDecimal.ROUND_DOWN);*/
						
						otcService.publish(coinAccountRedis.getId(), releaseAdvertisement.getCoinNumMax().multiply(new BigDecimal(-1)), 2, 1, "", 66);
						otcService.publish(coinAccountRedis.getId(), releaseAdvertisement.getCoinNumMax(), 1, 1, "", 66);
					} else {
						/*//??????coinCode??????????????????????????????--????????????????????????
						BigDecimal yi = new BigDecimal("1");// 1
						BigDecimal yibai = new BigDecimal("100");// 100
						BigDecimal price = releaseAdvertisement.getTradeMoney()
								.multiply(yi.subtract(releaseAdvertisement.getPremium().divide(yibai))); // ????????????*(1-??????????????????)????????????

						// ????????? ?????? ??????=?????????
						BigDecimal bs = releaseAdvertisement.getTradeMoneyMax().divide(price, keepDecimalForCoin, BigDecimal.ROUND_DOWN);
	*/
						otcService.publish(coinAccountRedis.getId(), releaseAdvertisement.getCoinNumMax().multiply(new BigDecimal(-1)), 2, 1, "", 66);
						otcService.publish(coinAccountRedis.getId(), releaseAdvertisement.getCoinNumMax(), 1, 1, "", 66);
					}
				}
				releaseAdvertisement.setTradeMoneyMax(new BigDecimal(0));
				releaseAdvertisement.setState(1);
				releaseAdvertisement.setStatus(0);
				releaseAdvertisementService.update(releaseAdvertisement);// ????????????
				
				redisService.delete("Advertisement:" + releaseId);
				
				return new JsonResult().setSuccess(true).setMsg("chuliwancheng");
			} else {
				return new JsonResult().setSuccess(false).setMsg("ciguanggaoyiguoqi");
			}
		}
		return new JsonResult().setSuccess(false).setMsg("qingbuyaochongfuquxiao");
	}
	
	@Override
	public FrontPage queryReleaseAdvertisement(Map<String, String> params) {
		FrontPage userAppTransaction = releaseAdvertisementService.findPageBySql(params);
		List<ReleaseAdvertisement> list = userAppTransaction.getRows();
		if(list != null && list.size() > 0){
			for(ReleaseAdvertisement  ad : list){
				if(ad != null && ad.getIsFixed() != null && ad.getIsFixed() == 0){ //???????????? ????????????
					BigDecimal yi = new BigDecimal("1");// 1
					BigDecimal yibai = new BigDecimal("100");// 100
					BigDecimal premiumBD = ad.getPremium();
					BigDecimal shichangjiageBD = ad.getTradeMoney();
					if (ad.getTransactionMode()==1) {// ?????? ??????
						BigDecimal price = shichangjiageBD.multiply(yi.subtract(premiumBD.divide(yibai,4,BigDecimal.ROUND_DOWN))).setScale( 2,BigDecimal.ROUND_HALF_DOWN); // ????????????*(1-??????????????????)????????????
						ad.setTradeMoney(price);
					} else {// ?????? ??????
						BigDecimal price = shichangjiageBD.multiply(yi.add(premiumBD.divide(yibai,4,BigDecimal.ROUND_DOWN))).setScale( 2,BigDecimal.ROUND_HALF_DOWN);// ????????????*(1+??????????????????)????????????
						ad.setTradeMoney(price);
					}
				}
				//???????????????????????????????????????????????????
				BigDecimal zero = new BigDecimal("0");
				if( ad.getInitialCoinNumMax() != null ){
					if( ad.getStatus() == 0){
						if( ad.getInitialCoinNumMax().compareTo( ad.getCoinNumMax() ) == 0){
							//?????????
							ad.setAdvStatus(3);
						}else if( ad.getCoinNumMax().compareTo( zero ) == 0){
							//????????????
							ad.setAdvStatus(1);
						}else{
							//????????????
							ad.setAdvStatus(2);
						}
					}
				}else{
					//???????????????????????????
					ad.setAdvStatus(3);
				}

			}
		}
		List<ReleaseAdvertisementRemote> beanList = ObjectUtil.beanList(list, ReleaseAdvertisementRemote.class);
		
		//??????created???????????????
		if(beanList != null && beanList.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(int i=0;i<beanList.size();i++){
				beanList.get(i).setCreated(sdf.format(new Date(Long.valueOf(beanList.get(i).getCreated()))));
			}
		}
		userAppTransaction.setRows(beanList);
		return userAppTransaction;
	}
	
	/**
	 * ???????????????id???????????????????????????
	 */
	@Override
	public JsonResult batchCloseAd(Long customerId,String transactionMode){
		QueryFilter filter = new QueryFilter(ReleaseAdvertisement.class);
		filter.addFilter("customerId=", customerId);
		filter.addFilter("transactionMode=", transactionMode);
		List<ReleaseAdvertisement> list = releaseAdvertisementService.find(filter);
		boolean flag = true;
		if(list != null){
			for(ReleaseAdvertisement ad : list){
				if(ad != null){
					//????????????????????????????????????
					if(ad.getStatus() == 1){//??????????????????
						JsonResult jsonResult = this.closeReleaseAdvertisement(ad.getId());
						flag = flag || jsonResult.getSuccess();
					}
				}
			}
		}
		if(flag){
			return new JsonResult().setSuccess(true);
		}else{
			return new JsonResult().setSuccess(false);
		}
	}
	
	@Override
	public void addPersonalAsset(String type, String bankName, String account,
			String surname,String truename, String subBranch, String thingUrl, String customerId, String bankAddress, String bankProvince, String cardBank) {
		
		//??????????????????
		Map<String,String> params = new HashMap<String,String>();
		params.put("customerId", customerId);
		params.put("type", type);
		otcService.updateIsDefault(params);
		
		//????????????????????????
		AppBankCard appBankCard = new AppBankCard();
		appBankCard.setType(Integer.valueOf(type));
		appBankCard.setCardNumber(account);
		appBankCard.setCardBank(cardBank);
		appBankCard.setBankProvince(bankProvince);
		appBankCard.setBankAddress(bankAddress);
		appBankCard.setCustomerId(Long.valueOf(customerId));
		appBankCard.setSurname(surname);
		appBankCard.setTrueName(truename);
		appBankCard.setSubBank(subBranch);
		appBankCard.setThingUrl(thingUrl);
		appBankCard.setIsDefault(1); //????????????
		otcService.save(appBankCard);
	}
	
	@Override
	public JsonResult deletePersonalAsset(Long id, Long userId) {
		List<ReleaseAdvertisement> list = releaseAdvertisementService.find(new QueryFilter(ReleaseAdvertisement.class).addFilter("customerId=", userId));
		if(list.size()>0){
			for(int i=0; i<list.size(); i++){
				if(list.get(i).getStatus()==1 || list.get(i).getState() == 0){
					return new JsonResult().setSuccess(false).setMsg("yinhangkbunengshanchu");
				}
			}
		}
		otcService.updateIsDeleteById(id);
		return new JsonResult().setSuccess(true).setMsg("shanchuchenggong");
	}
	
	/**
	 * ???????????????????????????
	 */
	@Override
	public JsonResult setDefault(Long id, String type, Long userId) {
		//??????????????????
		Map<String,String> params = new HashMap<String,String>();
		params.put("customerId", userId.toString());
		params.put("type", type);
		otcService.updateIsDefault(params);
		
		//??????id????????????
		AppBankCard appBankCard = otcService.getById(id);
		if(appBankCard != null){
			//????????????????????????????????????????????????
			appBankCard.setIsDefault(1);
			otcService.update(appBankCard);
			return new JsonResult().setSuccess(true);
		}
		return new JsonResult().setSuccess(false);
	}

	@Override
	public void updateNickName(String nName, Long id){
		otcService.updateNickName(nName, id);
	}
	
	@Override
	public FrontPage gettransaction(Map<String, String> params) {
		FrontPage wFrontPage = new FrontPage(null,0,0,0);
		List<OtcAppTransaction> list = otcAppTransactionService.find(new QueryFilter(AppTransaction.class).addFilter("status_in", "1,2"));
		List<OtcAppTransactionRemote> beanList = ObjectUtil.beanList(list, OtcAppTransactionRemote.class);
		wFrontPage.setRows(beanList);
		return wFrontPage;
	}
	
	@Override
	public JsonResult recoveryReleaseAdvertisement(String tradeNum, Long releaseId) {
		try{
			cancleOrder(tradeNum);
			return new JsonResult().setSuccess(true);
		}catch (Exception e){
			e.printStackTrace();
			return new JsonResult().setSuccess(false);
		}
		/*QueryFilter apptfilter = new QueryFilter(AppTransaction.class);
		apptfilter.addFilter("transactionNum=", tradeNum);
		OtcAppTransaction otcAppTransaction = otcAppTransactionService.get(apptfilter);

		ReleaseAdvertisement releaseAdvertisement = releaseAdvertisementService.get(otcAppTransaction.getAdvertisementId());
		if (null != releaseAdvertisement) {
			BigDecimal eatFee = new BigDecimal(0); // ???????????????

			String productinfoListall = redisService.get("cn:productinfoListall");
			JSONArray parseArray = JSON.parseArray(productinfoListall);
			for(int k=0;k<parseArray.size();k++){
				JSONObject jo = JSON.parseObject(parseArray.get(k).toString());
				if(jo.getString("coinCode").equals(releaseAdvertisement.getCoinCode())){
					if (jo.getInteger("eatFee")!=null) {
						eatFee = jo.getBigDecimal("eatFee");
					}
				}
			}

			//????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
			if (releaseAdvertisement.getTransactionMode()==1) {// ?????? ??????
				ExDigitalmoneyAccountRedis exDigitalmoneyAccountRedis = getCoinAccountRedis(releaseAdvertisement.getCustomerId(), releaseAdvertisement.getCoinCode());
				if (exDigitalmoneyAccountRedis == null) {
					return new JsonResult().setSuccess(false).setMsg("??????????????????");
				}
			} else {// ????????????  ???????????????????????????????????????  ????????????????????????????????????????????????
				ExDigitalmoneyAccountRedis exDigitalmoneyAccountRedis = getCoinAccountRedis(otcAppTransaction.getCustomerId(), releaseAdvertisement.getCoinCode());
				if (exDigitalmoneyAccountRedis == null) {
					return new JsonResult().setSuccess(false).setMsg("??????????????????");
				}
				BigDecimal bs = otcAppTransaction.getTradeNum().add(otcAppTransaction.getSellfee());

				otcService.publish(exDigitalmoneyAccountRedis.getId(), bs.multiply(new BigDecimal(-1)), 2, 1, "", 670);
				otcService.publish(exDigitalmoneyAccountRedis.getId(), bs, 1, 1, "", 670);
			}
			//???????????????
			BigDecimal yi = new BigDecimal("1");// 1
			BigDecimal yibai = new BigDecimal("100");// 100
			BigDecimal price=new BigDecimal("0");
			//??????coinCode??????????????????????????????--????????????????????????
			if (releaseAdvertisement.getIsFixed() == 1) {// ???????????????
				price=releaseAdvertisement.getTradeMoney();
			}else{
				//????????????????????????
				if(releaseAdvertisement.getTransactionMode() == 2){//???????????????
					price = releaseAdvertisement.getTradeMoney().multiply(
							yi.add(releaseAdvertisement.getPremium().divide(yibai))).setScale(2, BigDecimal.ROUND_HALF_DOWN);
				}else if(releaseAdvertisement.getTransactionMode() == 1){//???????????????

					price = releaseAdvertisement.getTradeMoney().multiply(
							yi.subtract(releaseAdvertisement.getPremium().divide(yibai))).setScale(2, BigDecimal.ROUND_HALF_DOWN);
				}
			}
			releaseAdvertisement.setCoinNumMax(releaseAdvertisement.getCoinNumMax().add(otcAppTransaction.getTradeNum()));
			releaseAdvertisement.setTradeMoneyMax(releaseAdvertisement.getCoinNumMax().multiply(price).setScale(2, BigDecimal.ROUND_HALF_DOWN));
			//???????????????
			//releaseAdvertisement.setTransactionNum( releaseAdvertisement.getTransactionNum() - 1);
			//???????????????????????????????????????????????????????????????????????????
			if( releaseAdvertisement.getCoinNumMax().compareTo( releaseAdvertisement.getCoinNumMin() ) >= 0){
				releaseAdvertisement.setStatus(1);
				releaseAdvertisement.setState(0);
				//????????? ?????????????????? ????????????
				ExDigitalmoneyAccountRedis coinAccountRedis = getCoinAccountRedis(releaseAdvertisement.getCustomerId(), releaseAdvertisement.getCoinCode());
				String s = redisService.get("otcReleaseAdvertisementAfterColse:" + tradeNum);
				if(! StringUtils.isEmpty(s) ){
					BigDecimal bs = new BigDecimal(s);
					otcService.publish(coinAccountRedis.getId(), bs, 2, 1, "", 64);
					otcService.publish(coinAccountRedis.getId(), bs.multiply(new BigDecimal(-1)), 1, 1, "", 64);
					redisService.delete("otcReleaseAdvertisementAfterColse:" + tradeNum);
				}
			}
			releaseAdvertisementService.update(releaseAdvertisement);// ????????????

			// ?????????????????????
			if (null != otcAppTransaction) {
				otcAppTransaction.setStatus(5);
				otcAppTransactionService.update(otcAppTransaction);
			}
			return new JsonResult().setSuccess(true).setCode("????????????");
		} else {
			// ?????????????????????
			if (null != otcAppTransaction) {
				otcAppTransaction.setStatus(5);
				otcAppTransactionService.update(otcAppTransaction );
			}
			return new JsonResult().setSuccess(false).setCode("?????????????????????");
		}*/
	}
	
	@Override
	public void addOrderSpeak(String orderId, Long buyId, Long sellId, String buySpeak){
		AppOrderSpeak appOrderSpeak = new AppOrderSpeak();
		appOrderSpeak.setOrderId(orderId);
		appOrderSpeak.setBuyId(buyId);
		appOrderSpeak.setSellId(sellId);
		appOrderSpeak.setBuySpeak(buySpeak);
		appOrderSpeakService.save(appOrderSpeak);
	}
	
	@Override
	public JsonResult isBankCard(String type, Long id){
		AppBankCard bankCard = otcService.isBankCard(type, id);
		if(bankCard != null){
			return new JsonResult().setSuccess(true);
		}
		return new JsonResult().setSuccess(false);
	}
	
	@Override
	public JsonResult trustShield(Long trustId, Long ShieldId, String type) {
		Map<String, Object> map = new HashMap<String, Object>();

		QueryFilter qf = new QueryFilter(TrustShield.class);
		qf.addFilter("trust=", trustId);
		qf.addFilter("isTrust=", ShieldId);
		List<TrustShield> list = trustShieldService.find(qf);
		if (list.size() == 0) {
			AppCustomer appCustomer = otcService.getAppCustomerById(ShieldId);
			if (appCustomer != null) {
				TrustShield trustShield1 = new TrustShield();
				trustShield1.setTrust(trustId);
				trustShield1.setIsTrust(ShieldId);
				if ("1".equals(type)) {// ??????
					appCustomer.setTrustNum(appCustomer.getTrustNum() + 1);
					trustShield1.setStatus(1);
				} else if ("2".equals(type)) {// ??????
					appCustomer.setShieldNum(appCustomer.getShieldNum() + 1);
					trustShield1.setStatus(2);
				}
				trustShieldService.save(trustShield1);
				otcService.updateAppCustomer(appCustomer);
				map.put("trustNum", appCustomer.getTrustNum());
				map.put("isflag", "1");
				map.put("type", type);
			}
		} else {
			AppCustomer appCustomer = otcService.getAppCustomerById(ShieldId);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getStatus().toString().equals(type)) {
					if ("1".equals(type)) {// ??????
						appCustomer.setTrustNum(appCustomer.getTrustNum() - 1 < 0 ? 0 : appCustomer.getTrustNum() - 1);
					} else if ("2".equals(type)) {// ??????
						appCustomer
								.setShieldNum(appCustomer.getShieldNum() - 1 < 0 ? 0 : appCustomer.getShieldNum() - 1);
					}
					trustShieldService.delete(list.get(i).getId());
					otcService.updateAppCustomer(appCustomer);
					map.put("trustNum", appCustomer.getTrustNum());
					map.put("isflag", "2");
					map.put("type", type);
				} else {
					TrustShield trustShield1 = new TrustShield();
					trustShield1.setTrust(trustId);
					trustShield1.setIsTrust(ShieldId);
					if ("1".equals(type)) {// ??????
						appCustomer.setTrustNum(appCustomer.getTrustNum() + 1);
						trustShield1.setStatus(1);
					} else if ("2".equals(type)) {// ??????
						appCustomer.setShieldNum(appCustomer.getShieldNum() + 1);
						trustShield1.setStatus(2);
					}
					trustShieldService.save(trustShield1);
					otcService.updateAppCustomer(appCustomer);
					map.put("trustNum", appCustomer.getTrustNum());
					map.put("isflag", "1");
					map.put("type", type);
				}
			}
		}
		return new JsonResult().setSuccess(true).setObj(map);
	}
	
	@Override
	public JsonResult getUserById(Long id){
		Map<String, Object> map = new HashMap<String, Object>();
		AppCustomer appCustomer = otcService.getAppCustomerById(id);
		if(appCustomer!=null){
			User user = ObjectUtil.bean2bean(appCustomer, User.class);
			user.setNickNameOtc(appCustomer.getNickNameOtc());
			user.setCustomerId(appCustomer.getId());
			
			AppPersonInfo appPersonInfo = otcService.getAppPersonInfoByUserId(id);
			if(appPersonInfo!=null){
				user.setMobile(appPersonInfo.getMobilePhone());
				user.setEmail(appPersonInfo.getEmail());
			}
			map.put("user", user);
			
			QueryFilter qf = new QueryFilter(OtcAppLog.class);
			qf.addFilter("userId=", id);
			OtcAppLog otcAppLog = otcAppLogService.get(qf);
			if(otcAppLog!=null){
				OtcAppLogRemote bean2bean = ObjectUtil.bean2bean(otcAppLog, OtcAppLogRemote.class);
				map.put("logoutTime", bean2bean.getLogoutTime());
				map.put("tradeTime", bean2bean.getTradeTime());
			}
		}
		return new JsonResult().setSuccess(true).setObj(map);
	}
	
	@Override
	public String avgTime(Long customerId) {
		String avgTime = releaseAdvertisementService.avgTime(customerId);
		if (avgTime == null) {
			avgTime = "0";
		}
		return avgTime;
	}
	
	@Override
	public JsonResult isLogLogin(Long userId){
		QueryFilter qf = new QueryFilter(OtcAppLog.class);
		qf.addFilter("userId=", userId);
		OtcAppLog appLog = otcAppLogService.get(qf);
		if(appLog!=null){
			OtcAppLogRemote appLogRemote = ObjectUtil.bean2bean(appLog, OtcAppLogRemote.class);
			return new JsonResult().setObj(appLogRemote);
		}
		return new JsonResult().setObj(null);
	}
	
	@Override
	public void updateByPrimaryKeySelective(OtcAppLogRemote appLogRemote){
		OtcAppLog appLog = ObjectUtil.bean2bean(appLogRemote, OtcAppLog.class);
		otcAppLogService.updateNull(appLog);
	}
	
	@Override
	public void insert(OtcAppLogRemote appLogRemote){
		OtcAppLog appLog = ObjectUtil.bean2bean(appLogRemote, OtcAppLog.class);
		otcAppLogService.save(appLog);
	}

	@Override
	public JsonResult aduserTable(Long id, String state) {
		QueryFilter qf = new QueryFilter(ReleaseAdvertisement.class);
		qf.addFilter("customerId=", id);
		qf.addFilter("transactionMode=", state);
		qf.addFilter("state=", 0);
		qf.addFilter("status=", 1);
		List<ReleaseAdvertisement> list = releaseAdvertisementService.find(qf);
		if (list != null && list.size() > 0) {
			//??????
			for(ReleaseAdvertisement  ad : list){
				if(ad != null && ad.getIsFixed() != null && ad.getIsFixed() == 0){ //???????????? ????????????
					BigDecimal yi = new BigDecimal("1");// 1
					BigDecimal yibai = new BigDecimal("100");// 100
					BigDecimal premiumBD = ad.getPremium();
					BigDecimal shichangjiageBD = ad.getTradeMoney();
					if (ad.getTransactionMode()==1) {// ?????? ??????
						BigDecimal price = shichangjiageBD.multiply(yi.subtract(premiumBD.divide(yibai,4,BigDecimal.ROUND_DOWN))); // ????????????*(1-??????????????????)????????????
						ad.setTradeMoney(price);
					} else {// ?????? ??????
						BigDecimal price = shichangjiageBD.multiply(yi.add(premiumBD.divide(yibai,4,BigDecimal.ROUND_DOWN)));// ????????????*(1+??????????????????)????????????
						ad.setTradeMoney(price);
					}
				}
			}
			
			List<ReleaseAdvertisementRemote> beanList = ObjectUtil.beanList(list, ReleaseAdvertisementRemote.class);
			return new JsonResult().setSuccess(true).setObj(beanList);
		}
		return new JsonResult().setSuccess(false);
	}

    /**
     * ??????????????????????????????????????????
     * @param releaseId
     * @return
     */
    @Override
    public Boolean findOrderByStatus (Long releaseId) {
        ReleaseAdvertisement releaseAdvertisement = releaseAdvertisementService.get(releaseId);
        if( releaseAdvertisement != null){
            QueryFilter filter = new QueryFilter(OtcAppTransaction.class);
			//????????????(1????????????,2????????????,3????????????)
            if( releaseAdvertisement.getTransactionMode() == 2  ){
            	filter.addFilter("buyUserId=",releaseAdvertisement.getCustomerId());
			}else if( releaseAdvertisement.getTransactionMode() == 1  ){
				filter.addFilter("sellUserId=",releaseAdvertisement.getCustomerId());
			}
            filter.addFilter("status _in ","(1,2,3,4,6,7,8,9,10,11,12,13,15,16)");
            filter.addFilter("advertisementId =",releaseAdvertisement.getId());
            Long count = otcAppTransactionService.count(filter);
            if( count > 0L){
                return false;
            }
        }
        return true;
    }

	@Override
	public void closeAllReleaseAdvertisement () {
		//???????????????????????????????????????
		QueryFilter filter = new QueryFilter(ReleaseAdvertisement.class);
		filter.addFilter("isFixed=",1);
		filter.addFilter("status=",1);
		filter.addFilter("tradeMoney_LET","5.5");
		List<ReleaseAdvertisement> releaseAdvertisements = releaseAdvertisementService.find(filter);
		if( releaseAdvertisements != null && releaseAdvertisements.size() > 0){
			for (ReleaseAdvertisement releaseAdvertisement : releaseAdvertisements) {
				QueryFilter f1= new QueryFilter(OtcAppTransaction.class);
				f1.addFilter("advertisementId=",releaseAdvertisement.getId());
				f1.addFilter("status_in","1,2,3,4,6,7,8,9,10,11,12,13,15,16");
				Long count = otcAppTransactionService.count(f1);
				if( count  == 0L ){
					//????????????5.5?????????????????????????????????????????????????????????????????????
					closeReleaseAdvertisement(releaseAdvertisement.getId());
					System.out.println("????????????,??????id??????"+releaseAdvertisement.getId()+",????????????id??????"+releaseAdvertisement.getCustomerId());
				}
			}
		}

		//??????????????????
		QueryFilter filter1 = new QueryFilter(ReleaseAdvertisement.class);
		filter1.addFilter("isFixed=",0);
		filter1.addFilter("status=",1);
		BigDecimal money = new BigDecimal("5.5");
		List<ReleaseAdvertisement> releaseAdvertisementList = releaseAdvertisementService.find(filter1);
		if( releaseAdvertisementList != null && releaseAdvertisementList.size() > 0){
			for (ReleaseAdvertisement releaseAdvertisement : releaseAdvertisementList) {
				QueryFilter f1= new QueryFilter(OtcAppTransaction.class);
				f1.addFilter("advertisementId=",releaseAdvertisement.getId());
				f1.addFilter("status_in","1,2,3,4,6,7,8,9,10,11,12,13,15,16");
				Long count = otcAppTransactionService.count(f1);
				if( count  == 0L ){
					//????????????????????????????????????5.5
					BigDecimal price=new BigDecimal("0");
					if( releaseAdvertisement.getTransactionMode() == 1 ) {
						price = releaseAdvertisement.getTradeMoney().multiply(new BigDecimal("1").subtract(releaseAdvertisement.getPremium().divide(new BigDecimal("100")))).setScale(2,BigDecimal.ROUND_HALF_DOWN); // ????????????*(1-??????????????????)????????????
					}else{
						price = releaseAdvertisement.getTradeMoney().multiply(new BigDecimal("1").subtract(releaseAdvertisement.getPremium().divide(new BigDecimal("100")))).setScale(2,BigDecimal.ROUND_HALF_DOWN); // ????????????*(1-??????????????????)????????????
					}
					if( money.compareTo(price) >= 0){
						//????????????5.5?????????????????????????????????????????????????????????????????????
						closeReleaseAdvertisement(releaseAdvertisement.getId());
						System.out.println("????????????,??????id??????"+releaseAdvertisement.getId()+",????????????id??????"+releaseAdvertisement.getCustomerId());
					}
				}
			}
		}
	}

	public JsonResult findOtcOrderById(String tradeNum){
		QueryFilter qf = new QueryFilter(OtcAppTransaction.class);
		qf.addFilter("transactionNum =", tradeNum);
		OtcAppTransaction app = otcAppTransactionService.get(qf);
		if(app != null){
			return new JsonResult().setSuccess(true).setObj(app.getStatus());
		}
		return new JsonResult().setSuccess(false);
	}

	public BigDecimal getCompletionRate(Long userId, String coinCode){
    	QueryFilter qf = new QueryFilter(OtcCompletionRate.class);
    	qf.addFilter("customerId=", userId);
    	qf.addFilter("coinCode=", coinCode);
		OtcCompletionRate otcCompletionRate = otcCompletionRateService.get(qf);
		if(otcCompletionRate == null){
			return new BigDecimal(100);
		}
		return otcCompletionRate.getCompletionRate();
	}
}
