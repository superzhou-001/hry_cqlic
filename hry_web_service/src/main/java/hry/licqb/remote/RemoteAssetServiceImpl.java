package hry.licqb.remote;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import hry.bean.FrontPage;
import hry.bean.JsonResult;
import hry.core.thread.ThreadPool;
import hry.exchange.product.model.ExProduct;
import hry.exchange.product.service.ExProductService;
import hry.front.redis.model.UserRedis;
import hry.licqb.award.RulesConfig;
import hry.licqb.award.model.OutInfo;
import hry.licqb.award.service.OutInfoService;
import hry.licqb.record.model.CustomerFreeze;
import hry.licqb.record.model.DealRecord;
import hry.licqb.record.service.CustomerFreezeService;
import hry.licqb.record.service.DealRecordService;
import hry.licqb.thread.AgainPutIntoRunnable;
import hry.licqb.thread.ChargeRecordRunnable;
import hry.licqb.util.AccountUtil;
import hry.licqb.util.DealEnum;
import hry.licqb.util.RedisStaticUtil;
import hry.mq.producer.service.MessageProducer;
import hry.redis.common.dao.RedisUtil;
import hry.redis.common.utils.RedisService;
import hry.trade.redis.model.Accountadd;
import hry.trade.redis.model.ExDigitalmoneyAccountRedis;
import hry.util.PageFactory;
import hry.util.QueryFilter;
import hry.util.date.DateUtil;
import hry.util.idgenerate.IdGenerate;
import hry.util.idgenerate.NumConstant;
import org.springframework.util.StringUtils;
import util.UserRedisUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhouming
 * @date 2019/8/30 14:20
 * @Version 1.0
 */
public class RemoteAssetServiceImpl implements RemoteAssetService {

    @Resource
    private MessageProducer messageProducer;
    @Resource
    private DealRecordService dealRecordService;
    @Resource
    private OutInfoService outInfoService;
    @Resource
    private RedisService redisService;
    @Resource
    private ExProductService exProductService;
    @Resource
    private CustomerFreezeService customerFreezeService;

    @Override
    public JsonResult getPlatformConfig() {
        JSONObject obj = new JSONObject();
        // ???????????????
        String coinName = RedisStaticUtil.getAppConfigValue(RulesConfig.RulesCoinKey, "coinName");
        // ?????????code
        String coinCode = RedisStaticUtil.getAppConfigValue(RulesConfig.RulesCoinKey, "coinCode");
        // ???????????????
        String issuePrice = RedisStaticUtil.getAppConfigValue(RulesConfig.RulesCoinKey, "issuePrice");
        // ??????????????????
        String extractStartTime = RedisStaticUtil.getAppConfigValue(RulesConfig.ExtractTimeKey, "extractStartTime");
        // ??????????????????
        String extractEndTime = RedisStaticUtil.getAppConfigValue(RulesConfig.ExtractTimeKey, "extractEndTime");
        // ????????????
        String extractNum = RedisStaticUtil.getAppConfigValue(RulesConfig.ExtractTimeKey, "extractNum");
        // ??????????????????
        String minInvest = RedisStaticUtil.getAppConfigValue(RulesConfig.InvestRangeKey, "minInvest");
        // ??????????????????
        String maxInvest = RedisStaticUtil.getAppConfigValue(RulesConfig.InvestRangeKey, "maxInvest");
        obj.put("coinName",coinName);
        obj.put("coinCode",coinCode);
        obj.put("issuePrice",issuePrice);
        obj.put("extractStartTime",extractStartTime);
        obj.put("extractEndTime",extractEndTime);
        obj.put("extractNum",extractNum);
        obj.put("minInvest",minInvest);
        obj.put("maxInvest",maxInvest);
        return new JsonResult(true).setObj(obj);
    }

    @Override
    public JsonResult getAccountByCoinCode(Long customerId, String coinCode) {
        JSONObject obj = new JSONObject();
        // ???????????????
        ExDigitalmoneyAccountRedis exaccount = this.selectAccount(customerId, coinCode);
        if(exaccount != null){
            obj.put("hotMoney", exaccount.getHotMoney().compareTo(new BigDecimal(0))==0?new BigDecimal(0):exaccount.getHotMoney());
            obj.put("coldMoney", exaccount.getColdMoney().compareTo(new BigDecimal(0))==0?new BigDecimal(0):exaccount.getColdMoney());
        }
        return new JsonResult(true).setObj(obj);
    }

    @Override
    public JsonResult myAccount(Map<String, String> paramMap) {
        JSONObject obj = new JSONObject();
        // ??????id
        Long customerId = Long.valueOf(paramMap.get("customerId"));
        // ?????????code
        String coinCode = RedisStaticUtil.getAppConfigValue(RulesConfig.RulesCoinKey, "coinCode");
        // ??????????????? ??????USDT (????????????????????????????????????)
        String issuePrice = RedisStaticUtil.getAppConfigValue(RulesConfig.RulesCoinKey, "issuePrice");
        // ??????????????????????????????
        ExDigitalmoneyAccountRedis platExaccount = this.selectAccount(customerId, coinCode);
        // ????????????????????????
        BigDecimal platHotMoney = platExaccount.getHotMoney();
        // ????????????????????????
        BigDecimal platColdMoney = platExaccount.getColdMoney();
        // ???????????????
        BigDecimal platSum = platHotMoney.add(platColdMoney);

        //??????USDT???????????????
        ExDigitalmoneyAccountRedis usdtExaccount = this.selectAccount(customerId, "USDT");
        // ??????USDT?????????
        BigDecimal usdtHotMoney = usdtExaccount.getHotMoney();
        // ??????USDT?????????
        BigDecimal usdtColdMoney = usdtExaccount.getColdMoney();
        // USDT??????
        BigDecimal usdtSum = usdtHotMoney.add(usdtColdMoney);

        // ?????????????????????
        BigDecimal totalAssets = usdtSum.add(platSum.multiply(new BigDecimal(issuePrice)));
        if(totalAssets == null){
            totalAssets = new BigDecimal(0);
        }
        obj.put("totalAssets",totalAssets);
        return new JsonResult(true).setObj(obj);
    }

    @Override
    public JsonResult againInvest(Map<String, String> paramMap) {
        // ??????????????????
        // ??????????????????
        String investStartTime = RedisStaticUtil.getAppConfigValue(RulesConfig.InvestTimeKey, "investStartTime");
        // ??????????????????
        String investEndTime = RedisStaticUtil.getAppConfigValue(RulesConfig.InvestTimeKey, "investEndTime");
        // ????????????
        String nowDate = DateUtil.dateToString(new Date()).split(" ")[1];
        Boolean time = hourMinuteBetween(nowDate, investStartTime, investEndTime);
        if (!time) {
            return new JsonResult(false).setMsg("qingzaizhidignshijianlicai");
        }
        String customerId = paramMap.get("customerId");
        boolean flag = redisService.lock("AGAININVEST:"+customerId);
        if (!flag) {
            redisService.unLock("AGAININVEST:"+customerId);
            return new JsonResult(false).setMsg("req_error"); //????????????
        }
        // ??????????????????????????????
        CustomerFreeze freeze = customerFreezeService.getCustomerFreeze(Long.parseLong(customerId));
        if (freeze != null) {
            redisService.unLock("AGAININVEST:"+customerId);
            // ????????????
            ThreadPool.exe(new AgainPutIntoRunnable(Long.parseLong(customerId)));
            return new JsonResult(false).setMsg("zhanghuzijinyichang"); // ??????????????????
        }
        // ????????????
        String investNum = paramMap.get("investNum");
        if (new BigDecimal(investNum).compareTo(BigDecimal.ZERO) == -1) {
            redisService.unLock("AGAININVEST:"+customerId);
            return new JsonResult(false).setMsg("qingshuruzhengquetouzijine");
        }
        // ?????????????????????????????????USDT???
        ExDigitalmoneyAccountRedis usdtAccount = this.selectAccount(Long.parseLong(customerId),"USDT");
        if (new BigDecimal(investNum).compareTo(usdtAccount.getHotMoney()) == 1) {
            redisService.unLock("AGAININVEST:"+customerId);
            return new JsonResult(false).setMsg("zhanghuyuebuzu");
        }

        // ????????????????????????
        String minManageMoney = RedisStaticUtil.getAppConfigValue(RulesConfig.InvestRangeKey, "minManageMoney");
        if (new BigDecimal(investNum).compareTo(new BigDecimal(minManageMoney)) == -1) {
            redisService.unLock("AGAININVEST:"+customerId);
            return new JsonResult(false).setMsg("jinediyuzuixiaolicaishu");
        }
        // ?????????????????????????????????
        BigDecimal multiple = new BigDecimal(investNum).divide(new BigDecimal(minManageMoney),8,BigDecimal.ROUND_HALF_UP);
        if (new BigDecimal(multiple.intValue()).compareTo(multiple) != 0 ) {
            redisService.unLock("AGAININVEST:"+customerId);
            return new JsonResult(false).setMsg("licaibeishu");
        }

        QueryFilter filter = new QueryFilter(OutInfo.class);
        filter.addFilter("customerId=", customerId);
        OutInfo outInfo = outInfoService.get(filter);
        // ?????????????????????
        String maxInvest = RedisStaticUtil.getAppConfigValue(RulesConfig.InvestRangeKey, "maxInvest");
        if (outInfo != null) {
            if (new BigDecimal(investNum).add(outInfo.getBaseMoney()).compareTo(new BigDecimal(maxInvest)) == 1) {
                redisService.unLock("AGAININVEST:"+customerId);
                return new JsonResult(false).setMsg("touzijinechaoxian");
            }
        }
        // ??????USDT (?????????????????????USDT)
        String transactionNum = NumConstant.Ex_Dm_Transaction;
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("customerId", Long.valueOf(customerId));
        maps.put("accountId", usdtAccount.getId());
        maps.put("coinCode", "USDT");
        maps.put("money", investNum);
        maps.put("transactionNum", IdGenerate.transactionNum(transactionNum));
        maps.put("dealType", DealEnum.TYPE13.getIndex());
        maps.put("dealRemark", DealEnum.SITE13.getName());
        outInfoService.putIntoStorageMoney(maps);
        redisService.unLock("AGAININVEST:"+customerId);
        return new JsonResult(true).setCode("8888").setMsg("futouchenggong");
    }

    /**
     * ?????????????????????
     *
     * @param customerId
     * @param coinCode
     * @return
     */
    private ExDigitalmoneyAccountRedis selectAccount(Long customerId, String coinCode) {
        // ???redis??????
        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(customerId.toString());
        // ???????????????
        ExDigitalmoneyAccountRedis exaccount = UserRedisUtils.getAccount(userRedis.getDmAccountId(coinCode).toString(),
                coinCode);
        return exaccount;
    }

    @Override
    public JsonResult findexchangeData(Long customerId) {
        JSONObject obj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        // ???????????????
        String platCode = RedisStaticUtil.getAppConfigValue(RulesConfig.RulesCoinKey, "coinCode");
        // ????????????????????????
        QueryFilter queryFilter = new QueryFilter(ExProduct.class);
        queryFilter.addFilter("issueState=","1");
        queryFilter.setOrderby("coinCode asc");
        List<ExProduct> productList = exProductService.find(queryFilter);
        for (ExProduct exProduct : productList) {
            JSONObject object = new JSONObject();
            String price = redisService.hget("hry:coinPrice", exProduct.getCoinCode());
            object.put("coinCode",exProduct.getCoinCode());
            object.put("price", price);
            jsonArray.add(object);
        }
        // ???-???????????????
        obj.put("codeList", jsonArray);
        // ????????????
        ExDigitalmoneyAccountRedis platAccount = this.selectAccount(customerId, platCode);
        obj.put("hotMoney", platAccount.getHotMoney());
        return new JsonResult(true).setObj(obj);
    }

    @Override
    public JsonResult exchangeCode(Map<String, String> paramMap) {
        // ??????????????????
        // ??????????????????
        String exchangeStartTime = RedisStaticUtil.getAppConfigValue(RulesConfig.ExchangeTimeKey, "exchangeStartTime");
        // ??????????????????
        String exchangeEndTime = RedisStaticUtil.getAppConfigValue(RulesConfig.ExchangeTimeKey, "exchangeEndTime");
        // ????????????
        String nowDate = DateUtil.dateToString(new Date()).split(" ")[1];
        Boolean flag = hourMinuteBetween(nowDate, exchangeStartTime, exchangeEndTime);
        if (!flag) {
            return new JsonResult(false).setMsg("qingzaizhidignshijianduihuan");
        }
        // ???????????????
        String code = paramMap.get("buyCode");
        if (!"USDT".equals(code)) {
            return new JsonResult(false).setMsg("?????????????????????USDT");
        }
        Boolean lock = redisService.lock("exchangeCode:"+paramMap.get("customerId"));
        if (lock) {
            // ?????????
            String sellCode = paramMap.get("sellCode");
            // ???????????????
            String buyCode = paramMap.get("buyCode");
            // ????????????
            BigDecimal exchangeNum = new BigDecimal(paramMap.get("exchangeNum"));
            // ??????Id
            Long customerId = Long.parseLong(paramMap.get("customerId"));
            // ?????????????????????(????????????????????????????????????)??????USDT --- ????????????????????????????????????????????? ?????????????????????
            String issuePrice = RedisStaticUtil.getAppConfigValue(RulesConfig.RulesCoinKey, "issuePrice");
            // ?????????????????????????????????(?????????)
            ExDigitalmoneyAccountRedis sellAccount = this.selectAccount(customerId, sellCode);
            // ??????????????????---?????????
            BigDecimal maxExchangeNum = sellAccount.getHotMoney();
            if (exchangeNum.compareTo(BigDecimal.ZERO) == -1) {
                redisService.unLock("exchangeCode:"+paramMap.get("customerId"));
                return new JsonResult(false).setMsg("duihuanshuliangyouwu").setCode("1001");
            }
            if (exchangeNum.compareTo(maxExchangeNum) == 1) {
                redisService.unLock("exchangeCode:"+paramMap.get("customerId"));
                return new JsonResult(false).setMsg("chaoguozuidaduihuanshuliang").setCode("1002");
            }
            // ?????????????????????
            if (exchangeNum.compareTo(sellAccount.getHotMoney()) == 1) {
                redisService.unLock("exchangeCode:"+paramMap.get("customerId"));
                return new JsonResult(false).setMsg("duihuanshuliangyouwu").setCode("1003");
            }
            // ?????????????????????
            BigDecimal buyNum = exchangeNum.multiply(new BigDecimal(issuePrice));
            // ????????????????????????????????????
            ExDigitalmoneyAccountRedis buyAccount = this.selectAccount(customerId, buyCode);
            List<Accountadd> list = new ArrayList<Accountadd>();
            // ????????????????????????---?????????(sellCode)
            String sellNum = IdGenerate.transactionNum(DealEnum.SITE12.getIndex());
            list.add(AccountUtil.getAccountAdd(sellAccount.getId(), new BigDecimal("-" + exchangeNum), 1, 1, 55,
                    sellNum));
            // ????????????????????????---buyCode
            String buyNumber = IdGenerate.transactionNum(DealEnum.SITE7.getIndex());
            list.add(AccountUtil.getAccountAdd(buyAccount.getId(), new BigDecimal("+" + buyNum), 1, 1, 55,
                    buyNumber));
            messageProducer.toAccount(JSON.toJSONString(list));

            // ???????????????????????????
            DealRecord sellRecord = new DealRecord();
            sellRecord.setCustomerId(customerId);
            sellRecord.setAccountId(sellAccount.getId());
            sellRecord.setCodeValue(new BigDecimal(issuePrice));
            sellRecord.setDealType(Integer.parseInt(DealEnum.TYPE12.getIndex()));
            sellRecord.setDealMoney(exchangeNum);
            sellRecord.setTransactionNum(sellNum);
            sellRecord.setCoinCode(sellCode);
            sellRecord.setRemark(DealEnum.SITE12.getName());
            dealRecordService.save(sellRecord);

            // ???????????????????????????
            DealRecord buyRecord = new DealRecord();
            buyRecord.setCustomerId(customerId);
            buyRecord.setAccountId(sellAccount.getId());
            buyRecord.setCodeValue(new BigDecimal(issuePrice));
            buyRecord.setDealType(Integer.parseInt(DealEnum.TYPE7.getIndex()));
            buyRecord.setDealMoney(buyNum);
            buyRecord.setTransactionNum(buyNumber);
            buyRecord.setCoinCode(buyCode);
            buyRecord.setRemark(DealEnum.SITE7.getName());
            dealRecordService.save(buyRecord);
            redisService.unLock("exchangeCode:"+paramMap.get("customerId"));
        }

        return new JsonResult(true).setMsg("duihuanchenggong");
    }

    /**
     *  ????????????
     * */
    private Boolean hourMinuteBetween (String nowDate, String startDate, String endDate) {
        Boolean flag = false;
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date now = format.parse(nowDate);
            Date start = format.parse(startDate);
            Date end = format.parse(endDate);
            long nowTime = now.getTime();
            long startTime = start.getTime();
            long endTime = end.getTime();
            flag = nowTime >= startTime && nowTime <= endTime;
        } catch (Exception e) {
            e.getLocalizedMessage();
            flag = false;
        }
        return flag;
    }

    @Override
    public FrontPage findDealDetails(Map<String, String> paramMap) {
        Page<DealRecord> page = PageFactory.getPage(paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("customerId",paramMap.get("customerId"));
        map.put("coinCode",paramMap.get("coinCode"));
        String dealType = paramMap.get("dealType");
        if(!StringUtils.isEmpty(dealType)){
            map.put("dealType", dealType);
        } else {
            // ?????????code
            String coinCode = RedisStaticUtil.getAppConfigValue(RulesConfig.RulesCoinKey, "coinCode");
            if (coinCode.equals(paramMap.get("coinCode"))) {
                int[] dealTypes = {1,2,3,4,12,9,10,11};
                map.put("dealTypes", dealTypes);
            } else {
                int[] dealTypes = {6,7,8,13,14,15};
                map.put("dealTypes", dealTypes);
            }
        }
        List<DealRecord> recordList = dealRecordService.findPageBySql(map);
        return new FrontPage(recordList, page.getTotal(), page.getPages(), page.getPageSize());
    }

    @Override
    public JsonResult findUserAssetNum(Long customerId) {
        JSONObject obj = new JSONObject();
        // ??????????????????
        String minInvest = RedisStaticUtil.getAppConfigValue(RulesConfig.InvestRangeKey, "minInvest");
        // ??????????????????
        String maxInvest = RedisStaticUtil.getAppConfigValue(RulesConfig.InvestRangeKey, "maxInvest");
        BigDecimal min = new BigDecimal(minInvest);
        BigDecimal max = new BigDecimal(maxInvest);
        QueryFilter filter = new QueryFilter(OutInfo.class);
        filter.addFilter("customerId=",customerId);
        filter.addFilter("status=",0);
        OutInfo outInfo = outInfoService.get(filter);
        if (outInfo != null) {
            BigDecimal baseMoney = outInfo.getBaseMoney();
            BigDecimal maxMoney = new BigDecimal(maxInvest);
            max = new BigDecimal(maxInvest).subtract(baseMoney);
        } else {
            ExDigitalmoneyAccountRedis account = this.selectAccount(customerId, "USDT");
            max = new BigDecimal(maxInvest).subtract(account.getColdMoney());
        }
        obj.put("minInvest",min);
        obj.put("maxInvest",max);
        return new JsonResult(true).setObj(obj);
    }
}
