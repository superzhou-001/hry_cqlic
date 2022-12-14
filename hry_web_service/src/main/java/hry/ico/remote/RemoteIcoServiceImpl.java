package hry.ico.remote;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import hry.bean.FrontPage;
import hry.bean.JsonResult;
import hry.bean.ObjectUtil;
import hry.bean.PageResult;
import hry.calculate.util.DateUtil;
import hry.customer.commend.model.AppCommendUser;
import hry.customer.commend.service.AppCommendUserService;
import hry.customer.person.model.AppPersonInfo;
import hry.customer.person.service.AppPersonInfoService;
import hry.customer.user.model.AppCustomer;
import hry.customer.user.service.AppCustomerService;
import hry.exchange.account.model.ExDigitalmoneyAccount;
import hry.exchange.account.service.ExDigitalmoneyAccountService;
import hry.front.redis.model.UserRedis;
import hry.ico.dao.IcoAccountDao;
import hry.ico.model.*;
import hry.ico.model.util.IcoAwardPo;
import hry.ico.remote.model.*;
import hry.ico.service.*;
import hry.manage.remote.RemoteManageService;
import hry.manage.remote.model.AppConfig;
import hry.manage.remote.model.CoinAccount;
import hry.manage.remote.model.RemoteResult;
import hry.message.model.MessageType;
import hry.message.model.RemoteMessage;
import hry.mq.producer.service.MessageProducer;
import hry.redis.common.dao.RedisUtil;
import hry.redis.common.utils.RedisService;
import hry.trade.redis.model.Accountadd;
import hry.trade.redis.model.ExDigitalmoneyAccountRedis;
import hry.util.PageFactory;
import hry.util.QueryFilter;
import hry.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.util.DigestUtils;
import util.BigDecimalUtil;
import util.ToAccountUtil;
import util.UserRedisUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *lzy
 */
public class RemoteIcoServiceImpl implements RemoteIcoService {
    private final Logger logger = Logger.getLogger(RemoteIcoServiceImpl.class);
    //้ไปๆฏ็ 40%
    private final double RATIO=0.4;
    private final static String configCache="configCache:";
    private static String payCoinCode="ETH";
    private static Object buyPlatform = new Object();
    @Resource
    private RedisService redisService;
    @Resource
    private IcoUpgradeConfigService upgradeConfigService;
    @Resource
    private IcoAccountService icoAccountService;
    @Resource
    private IcoTransactionRecordService icoTransactionRecordService;
    @Resource
    private IcoBuyOrderService icoBuyOrderService;
    @Resource
    private IcoLockRecordService icoLockRecordService;
    @Resource
    private RemoteManageService remoteManageService;
    @Resource
    private IcoTransferAccountsService icoTransferAccountsService;
    @Resource
    private AppCommendUserService appCommendUserService;
    @Resource
    private ExDigitalmoneyAccountService exDigitalmoneyAccountService;
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private IcoCustomerLevelService icoCustomerLevelService;
    @Resource
    private AppPersonInfoService appPersonInfoService;
    @Resource
    private IcoExperienceService icoExperienceService;
    @Resource
    private IcoDividendRecordService icoDividendRecordService;
    @Resource
    private IDelayedOrderService iDelayedOrder;
    @Resource
    private IcoLockRewardService icoLockRewardService;
    @Resource
    private IcoAwardRecordService icoAwardRecordService;
    @Resource
    private AppCustomerService appCustomerService;
    @Resource
    private IcoBillDetailsService icoBillDetailsService;
    @Resource
    private CoinCodeKeepDigitService coinCodeKeepDigitService;
    @Resource
    private  IcoLockAppendRecordService appendRecordService;

    //่ทๅ็ณป็ป่งๅ้็ฝฎ
    @Override
    public JsonResult getPlatformRule(String rulekeyStr) {
        IcoRulesConfig rulesConfig=new IcoRulesConfig();
        Map<String,String> cfMap=redisService.getMap(RulesConfig.RulesCoinLikeKey);
        rulesConfig=ObjectUtil.bean2bean(cfMap,IcoRulesConfig.class);
        return new JsonResult().setObj(rulesConfig).setSuccess(true);
    }

    @Override
    public JsonResult getPlatformCurrencyInfo() {
        RemotePlatformCurrencyInfo currencyInfo=null;
        JsonResult jsonResult= getPlatformRule(RulesConfig.RulesCoinKey);
        if(jsonResult.getSuccess()){
            currencyInfo=ObjectUtil.bean2bean(jsonResult.getObj(),RemotePlatformCurrencyInfo.class);
        }
        String PLATFORM_NUMBER=redisService.get(RulesConfig.PLATFORM_NUMBER);//ๅนณๅฐๅธๅฏๅฎๆฐ้
        if(PLATFORM_NUMBER==null||PLATFORM_NUMBER.equals("")){
            PLATFORM_NUMBER="0";
        }
        currencyInfo.setSaleNum(PLATFORM_NUMBER);
        return new JsonResult().setObj(currencyInfo).setSuccess(true);
    }

    /**
     * ๅฝๅๆฏๅฆico้ถๆฎต
     * @return
     */
    @Override
    public JsonResult isCheckICOTime() {
        JsonResult jsonResult=getPlatformRule(RulesConfig.RulesICOKey);//Ico่ฎพ็ฝฎ
        if(jsonResult.getSuccess()){
            IcoRulesConfig rulesConfig= ObjectUtil.bean2bean(jsonResult.getObj(),IcoRulesConfig.class);
            if("0".equals( rulesConfig.getIsLock())){//ๆฏๅฆ่ชๅจ้ 0ๆฏ1ๅฆ
                String startTime= rulesConfig.getIcoLockStartTime();//ๅผๅงๆถ้ด
                String endTime= rulesConfig.getIcoLockEndTime();//็ปๆๆถ้ด
                boolean fl= DateUtil.isDateInterval(startTime,endTime);
                if(fl){
                    return jsonResult;
                }
            }
        }
        return new JsonResult().setSuccess(false);
    }

    /**
     * ่ทๅ็ญ็บง้็ฝฎ
     * @return
     */
    @Override
    public JsonResult getUpgradeConfigList() {
        JsonResult result=new JsonResult();
        List<RemoteIcoUpgradeConfig> resList=new ArrayList<>();
        List<IcoUpgradeConfig> list= upgradeConfigService.findAll();
        if(list!=null&&list.size()>0){
            resList=ObjectUtil.beanList(list,RemoteIcoUpgradeConfig.class);
        }
        result.setSuccess(true);
        result.setObj(resList);
        result.setMsg("success");//ๆๅ
        return result;
    }
    /**
     * ่ฎก็ฎ่ดญไนฐๅนณๅฐๅธ
     * @param hashMap
     * @return
     */
    @Override
    public JsonResult calculatePaymentAmount(HashMap<String, String> hashMap) {
        RemotePaymentAmount remotePaymentAmount=new RemotePaymentAmount();
          String number=hashMap.get("number");//่ดญไนฐๆฐ
          String coinCode=payCoinCode;//"ETH";//hashMap.get("coinCode");//ๆฏไปๅธ็งETH
         JsonResult ruleResult=getPlatformRule(RulesConfig.RulesCoinKey);//
         if(!ruleResult.getSuccess()){
             return new JsonResult().setSuccess(false).setMsg("pingtaiguizehuoqucuowu");//ๅนณๅฐ่งๅ่ทๅ้่ฏฏ
         }
          IcoRulesConfig rulesConfig=ObjectUtil.bean2bean(ruleResult.getObj(),IcoRulesConfig.class);
          remotePaymentAmount.setPayCoinCode(coinCode);
          remotePaymentAmount.setBuyCoinCode(rulesConfig.getCoinCode());//ๅนณๅฐ่งๅ่ทๅๅนณๅฐๅธๅ็งฐ
          BigDecimal num=getPayNumber(new BigDecimal(number).multiply(new BigDecimal(rulesConfig.getIssuePrice())));
          remotePaymentAmount.setBuyPrice(rulesConfig.getIssuePrice());
          remotePaymentAmount.setPayNumber(BigDecimalUtil.bigDecimalToString(num));
       return new JsonResult().setSuccess(true).setMsg("success").setObj(remotePaymentAmount);
    }

    /**
     * ่ฎก็ฎๅบๆๅๆฐ้ ETH
     * @param number  ๆปไปทๅผ้ฉๅธ๏ผๆฐ้*่ดงๅธไปทๆ?ผ๏ผ
     * @return
     */
    private BigDecimal getPayNumber(BigDecimal number){
       String buy_price=redisService.getMap(RulesConfig.RedisMarketKey,"buy_price");//่ดญไนฐไปทๆ?ผ
        if(buy_price==null){
            throw  new RuntimeException();
        }
        return  number.divide(new BigDecimal(buy_price),8,BigDecimal.ROUND_DOWN);
    }

    /**
     *่ดญไนฐๅนณๅฐๅธ
     */
    @Override
    public JsonResult purchasePlatformAccount(HashMap<String, String> hashMap) {
        String customerId=hashMap.get("customerId");//็จๆทId
        String coinCode="ETH";//hashMap.get("coinCode");//ๅธ็ง
        BigDecimal number=new BigDecimal(hashMap.get("number"));//่ดญไนฐๆฐ
        JsonResult jsonResult=calculatePaymentAmount(hashMap);
        if(!jsonResult.getSuccess()){
            return jsonResult;
        }
            RemotePaymentAmount rePay=ObjectUtil.bean2bean(jsonResult.getObj(),RemotePaymentAmount.class);
            BigDecimal payNumber=new BigDecimal(rePay.getPayNumber());//ๆฏไปๆฐ้ETH
            String buyCoinCode=rePay.getBuyCoinCode();//่ดญไนฐๅนณๅฐๅธๅธ็งๅ
            String buyPrice=rePay.getBuyPrice();//่ดญไนฐๅนณๅฐๅธๅฎไปท
            RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
            UserRedis userRedis = redisUtil.get(customerId);
            List<Accountadd>listLock=new ArrayList<>();
            Long orderById=null;
        synchronized (buyPlatform){
             String PLATFORM_NUMBER=redisService.get(RulesConfig.PLATFORM_NUMBER);//ๅนณๅฐๅธๅฉไฝๆฐ้
            if(PLATFORM_NUMBER==null||PLATFORM_NUMBER.equals("")){
                PLATFORM_NUMBER = "0";
            }
            if(new BigDecimal(PLATFORM_NUMBER).compareTo(number)==-1){
                return new JsonResult().setSuccess(false).setMsg("pingtaibibuzu");//ๅนณๅฐๅธไธ่ถณ
            }
            // ่ทๅ่ต้่ดฆๆท๏ผๅคๆญ่ต้่ดฆๆทไฝ้ข
            ExDigitalmoneyAccountRedis exAccount = UserRedisUtils.getAccount(userRedis.getDmAccountId(coinCode).toString(),coinCode);
            if(exAccount.getHotMoney().compareTo(payNumber)==-1){
                return new JsonResult().setSuccess(false).setMsg("zijinbuzu");//่ต้ไธ่ถณ
            }
            Long payAccountId = userRedis.getDmAccountId(exAccount.getCoinCode());
            Accountadd payAccountadd=ToAccountUtil.expenditureHotAssets(payAccountId,payNumber);
            exAccount.setHotMoney(exAccount.getHotMoney().subtract(payNumber));

            listLock.add(payAccountadd);//ๆฃ้ค่ดญไนฐๅนณๅฐๅธๆฏไป็ETH
            // ่ทๅ่ต้่ดฆๆท๏ผๅคๆญ่ต้่ดฆๆทไฝ้ข
            ExDigitalmoneyAccountRedis exAccount1 = UserRedisUtils.getAccount(userRedis.getDmAccountId(buyCoinCode).toString(),buyCoinCode);
            /*//ico่ดฆๆท
            IcoAccount icoAccount= icoAccountService.get(new QueryFilter(IcoAccount.class).addFilter("customerId=",customerId));
           */
            Long buyAccountId = userRedis.getDmAccountId(exAccount1.getCoinCode());
            Accountadd buyAccountadd=ToAccountUtil.ncomeAssets(buyAccountId,number);
            exAccount1.setHotMoney(exAccount1.getHotMoney().add(number));
            listLock.add(buyAccountadd);//่ดญไนฐๅนณๅฐๅธๆทปๅ?ITX
            PLATFORM_NUMBER=BigDecimalUtil.bigDecimalToString(new BigDecimal(PLATFORM_NUMBER).subtract(number));
            String orderNumber=String.valueOf(System.currentTimeMillis());//ๆตๆฐดๅท
            IcoBuyOrder icoBuyOrder=new IcoBuyOrder();//่ดญไนฐๅนณๅฐ่ฎฐๅฝ
            icoBuyOrder.setOrderNumber(orderNumber);////ๆตๆฐดๅท
            icoBuyOrder.setCustomerId(Long.valueOf(customerId));
            icoBuyOrder.setBuyCoinCode(buyCoinCode);//่ดญไนฐๅธ็ง
            icoBuyOrder.setPayCoinCode(coinCode);//ๆฏไปๅธ็ง
            icoBuyOrder.setBuyPrice(new BigDecimal(buyPrice));//ๅฎไปท
            icoBuyOrder.setPayNumber(payNumber);//ๆฏไปETHๆฐ้
            icoBuyOrder.setBuyNumber(number);//่ดญไนฐITXๆฐ้

            //ไบคๆๆตๆฐด
            IcoTransactionRecord transactionRecord=new IcoTransactionRecord();
            transactionRecord.setCustomerId(Long.valueOf(customerId));
            transactionRecord.setProjectNumber(orderNumber);//ๆตๆฐดๅท
            transactionRecord.setCoinCode(exAccount.getCoinCode());
            transactionRecord.setColdMoney(exAccount.getColdMoney());
            transactionRecord.setHotMoney(exAccount.getHotMoney());
            transactionRecord.setType(51);// 11.้ไป12.้ๆพ21่ฝฌ่ดฆ31ๅ็บข32ๆจ่ๅฅๅฑ 41.ๅๅธ42.ๆๅธ51.ไนฐๅฅ52.ๅๅบ
            transactionRecord.setState(201);//201.ๆฏๅบ202.ๆถๅฅ
            transactionRecord.setTransactionCount(payNumber);
            transactionRecord.setRemark("่ดญไนฐๅนณๅฐๅธๆฏไป");
            transactionRecord.setIsShow(1);
            IcoTransactionRecord transactionRecord2=new IcoTransactionRecord();
            transactionRecord2.setCustomerId(Long.valueOf(customerId));
            transactionRecord2.setProjectNumber(orderNumber);//ๆตๆฐดๅท
            transactionRecord2.setCoinCode(exAccount1.getCoinCode());
            transactionRecord2.setColdMoney(exAccount1.getColdMoney());
            transactionRecord2.setHotMoney(exAccount1.getHotMoney());
            transactionRecord2.setType(51);// 11.้ไป12.้ๆพ21่ฝฌ่ดฆ31ๅ็บข32ๆจ่ๅฅๅฑ 41.ๅๅธ42.ๆๅธ51.ไนฐๅฅ52.ๅๅบ
            transactionRecord2.setState(202);//201.ๆฏๅบ202.ๆถๅฅ
            transactionRecord2.setTransactionCount(number);
            transactionRecord2.setRemark("่ดญไนฐๅนณๅฐๅธ่ทๅพ");
            transactionRecord2.setIsShow(1);
            icoBuyOrderService.save(icoBuyOrder);
            orderById=icoBuyOrder.getId();//่ฎขๅID
            transactionRecord2.setForeignKey(icoBuyOrder.getId());//ไธๅกๅค้ฎId
            transactionRecord.setForeignKey(icoBuyOrder.getId());//ไธๅกๅค้ฎId
            icoTransactionRecordService.save(transactionRecord2);
            icoTransactionRecordService.save(transactionRecord);
           /* icoAccount.setHotMoney(icoAccount.getHotMoney().add(number));//่ดญไนฐITXๆฐ้
            icoAccountService.update(icoAccount);*/
            messageProducer.toPlatformCurrency(number.multiply(new BigDecimal(-1)).stripTrailingZeros().toPlainString());
            //redisService.save(RulesConfig.PLATFORM_NUMBER,PLATFORM_NUMBER);
            messageProducer.toAccount(JSON.toJSONString(listLock));
        }
        return new JsonResult().setSuccess(true).setMsg("success").setObj(orderById);//ๆๅ
    }

    /**
     * ๆๆฐ่ดญไนฐๅนณๅฐ่ฎฐๅฝ
     * @param hashMap
     * @return
     */
    @Override
    public FrontPage getIcoBuyOrderRecord(HashMap<String, String> hashMap) {
       // String customerId=hashMap.get("customerId");
        return icoBuyOrderService.findPageBySql(hashMap);
    }

    /**
     * ๆ็ๆจ่ๅ่กจ
     * @param hashMap
     * @return
     */
    @Override
    public FrontPage getMyRecommendationList(HashMap<String, String> hashMap) {
        String coinCode= redisService.getMap(RulesConfig.RulesCoinLikeKey,"coinCode");
        int keepDigit=coinCodeKeepDigitService.getCoinCodeKeepDigit(coinCode);//ๅฐๆฐไฝ
        hashMap.put("keepDigit",String.valueOf(keepDigit));
        FrontPage frontPage=icoBuyOrderService.finRecommendBySql(hashMap);
        return frontPage;
    }

    /**
     * ๆ็ไบคๆๆตๆฐด
     * @param hashMap
     * @return
     */
    @Override
    public FrontPage getMyTransactionflow(HashMap<String, String> hashMap) {
        String customerId=hashMap.get("customerId");
        String type=hashMap.get("type");//็ฑปๅ
        String coinCode=hashMap.get("coinCode");//ๅธ็งCode
        String page=hashMap.get("page")==null?"0":hashMap.get("page");
        String pageSize=hashMap.get("pageSize")==null?"10":hashMap.get("pageSize");
        String starTime=hashMap.get("starTime");//ๅผๅงๆถ้ด
        String endTime=hashMap.get("endTime");//็ปๆๆถ้ด
        String maxNum=hashMap.get("maxNum");//ๆๅคงๅผ
        String minNum=hashMap.get("minNum");//ๆๅฐๅผ
        if(Integer.parseInt(pageSize)>100){
            pageSize="10";
        }
        QueryFilter queryFilter=new QueryFilter(IcoTransactionRecord.class);
        queryFilter.addFilter("customerId=",customerId);
        if(StringUtil.isNull(type)){
            queryFilter.addFilter("type=",type);
        }
        if(StringUtil.isNull(starTime)){
            queryFilter.addFilter("created>=",starTime);
        }
        if(StringUtil.isNull(endTime)){
            queryFilter.addFilter("created<=",endTime);
        }
        if(StringUtil.isNull(minNum)){
            queryFilter.addFilter("transactionCount>=",minNum);
        }
        if(StringUtil.isNull(maxNum)){
            queryFilter.addFilter("transactionCount<=",maxNum);
        }

        if(StringUtil.isNull(coinCode)){
            queryFilter.addFilter("coinCode=",coinCode);
        }
       // queryFilter.addFilter("isShow=",1);
        queryFilter.setOrderby("created desc");
        queryFilter.setPage(Integer.parseInt(page));
        queryFilter.setPageSize(Integer.parseInt(pageSize));
        return icoTransactionRecordService.findPageBySql(hashMap);
       // Page<IcoTransactionRecord> orderPage= icoTransactionRecordService.findPage(queryFilter);
      /*  Page<IcoTransactionRecord> orderPage= icoTransactionRecor dService.findPage(queryFilter);
        if(orderPage.size()>0){
            List<RemoteIcoTransactionRecord> list=ObjectUtil.beanList(orderPage.getResult(),RemoteIcoTransactionRecord.class);
            FrontPage frontPage = new FrontPage(list, orderPage.getTotal(), orderPage.getPages(), orderPage.getPageSize());
            return frontPage;
        }
        return new FrontPage(null, 0, 1, 10);*/
    }

    /**
     * ๆ็่ดฆๅๆ็ป
     * @param hashMap
     * @return
     */
    @Override
    public JsonResult getTransactionDetail(HashMap<String, String> hashMap) {
        String transactionId=hashMap.get("transactionId");//ๆตๆฐดId
        String customerId=hashMap.get("customerId");//็จๆทId
        IcoTransactionRecord icoTransactionRecord=null;
        if(transactionId.contains("_CB")){//ๅๅธ
            String [] strings=transactionId.split("_");
            hashMap.put("transactionId",strings[0]);
            icoTransactionRecord= icoTransactionRecordService.getCBTransaction(hashMap);
        }else{
             icoTransactionRecord= icoTransactionRecordService.get(new QueryFilter(IcoTransactionRecord.class)
                     .addFilter("id=",transactionId).addFilter("customerId=",customerId));
        }
        if(icoTransactionRecord==null){
            return new JsonResult().setSuccess(false).setMsg("liushuizhangdanbucunzai");//ๆตๆฐด่ดฆๅไธๅญๅจ
        }
        RemoteIcoTransactionRecord remote=ObjectUtil.bean2bean(icoTransactionRecord,RemoteIcoTransactionRecord.class);
        Long foreignKey=icoTransactionRecord.getForeignKey();//Long.valueOf(hashMap.get("foreignKey"));//ไธๅกๆตๆฐดId
        Integer type=icoTransactionRecord.getType();//Integer.parseInt(hashMap.get("type"));//ๆตๆฐด็ฑปๅ
        Object data=icoBillDetailsService.getBillDetails(type,foreignKey);
        remote.setObj(ObjectUtil.bean2bean(data,Map.class));
        return new JsonResult().setSuccess(true).setMsg("success").setObj(remote);
    }

    /**
     * ๆ็ๆจๅนฟๆ็ป๏ผๅข้่ตไบง็ญ๏ผ
     * @param hashMap
     * @return
     */
    @Override
    public JsonResult getMyRecommendationDetails(HashMap<String, String> hashMap) {
        String customerId=hashMap.get("customerId");
        RemoteRecommendationDetails recommendationDetails=new RemoteRecommendationDetails();
        JsonResult jsonResult= getIcoAccountInfo(hashMap);
        int keepDigit=0;//ไฟ็ไฝๆฐ
        if(jsonResult.getSuccess()){
            RemoteIcoAccount remoteIcoAccount=ObjectUtil.bean2bean(jsonResult.getObj(),RemoteIcoAccount.class);
            recommendationDetails.setColdMoney(remoteIcoAccount.getColdMoney());
            recommendationDetails.setHotMoney(remoteIcoAccount.getHotMoney());
            recommendationDetails.setTotalMoney(remoteIcoAccount.getTotalMoney());
            keepDigit=remoteIcoAccount.getKeepDigit();
            recommendationDetails.setKeepDigit(keepDigit);
        }
        int teamMember= appCommendUserService.countOneLevel(customerId);//ไธ็บงๆๆไธบๅข้ไบบๆฐ
        recommendationDetails.setTeamMember(teamMember);//ๅข้ไบบๆฐ
        String recommendedLock=icoAccountService.recommendedLockSum(Long.valueOf(customerId));
        if(recommendedLock!=null){
            recommendationDetails.setRecommendedLock(BigDecimalUtil.
                    bigDecimalScaleDigitToString(new BigDecimal(recommendedLock),keepDigit));//ๆจ่้ไป
        }
        String  recommendedAward= icoAwardRecordService.recommendedLockSum(Long.valueOf(customerId));
        if(recommendedAward!=null){
            recommendationDetails.setRecommendedAward(BigDecimalUtil.
                    bigDecimalScaleDigitToString(new BigDecimal(recommendedAward),keepDigit));
        }
        return new JsonResult().setSuccess(true).setObj(recommendationDetails);
    }

    /**
     * ้ไปๆไฝ
     * @param hashMap
     * @return
     */
    @Override
    public JsonResult toLockDepot(HashMap<String, String> hashMap) {
         String cu=hashMap.get("customerId");
         Long customerId=Long.valueOf(cu);//็จๆทId
         Integer lockDay=Integer.valueOf(hashMap.get("lockDay"));//้ไปๅคฉๆฐ
         BigDecimal number=new BigDecimal(hashMap.get("number"));//้ไปๆฐ้
         boolean isIco=Boolean.valueOf(hashMap.get("isIco"));
         JsonResult ruleResult=getPlatformRule(RulesConfig.RulesCoinKey);//
         if(!ruleResult.getSuccess()){
              return new JsonResult().setSuccess(false).setMsg("ๅนณๅฐ่งๅ่ทๅ้่ฏฏ");
         }
         IcoRulesConfig rulesConfig=ObjectUtil.bean2bean(ruleResult.getObj(),IcoRulesConfig.class);
         String coinCode=rulesConfig.getCoinCode();//ๅนณๅฐๅธ็ง
         return  toLockStorage(customerId,lockDay,number,coinCode,isIco);
    }
    //่ฟฝๅ?้ไปๆไฝ
    @Override
    public JsonResult appendLockDepot(HashMap<String, String> hashMap) {
        String cu=hashMap.get("customerId");
        Long customerId=Long.valueOf(cu);
        Integer addLockDay=Integer.valueOf(hashMap.get("addLockDay"));//่ฟฝๅ?้ไปๅคฉๆฐ
        Long lockId=Long.valueOf(hashMap.get("lockId"));//้ไปId
        QueryFilter queryFilter=new QueryFilter(IcoLockRecord.class);
        queryFilter.addFilter("id=",lockId);
        queryFilter.addFilter("state=","0");//(0.ๅพ้ๆพ1.ๅทฒ้ๆพ)
        queryFilter.addFilter("customerId=",customerId);
        IcoLockRecord icoLockRecord=icoLockRecordService.get(queryFilter);
        if(icoLockRecord==null){
            return  new JsonResult().setSuccess(false).setMsg("dingdanyibeishifang");//่ฎขๅๅทฒ่ขซ้ๆพ
        }
        BigDecimal number=icoLockRecord.getNumber();//้ไปๆฐ้
        Date releaseDate=icoLockRecord.getReleaseDate();//้ๆพๆถ้ด
        boolean f=icoLockRecordService.appendLockRecord(icoLockRecord,addLockDay);
        if(!f){
            return  new JsonResult().setSuccess(false).setMsg("appendError");//่ฟฝๅ?ๅคฑ่ดฅ
        }
        //่ฟฝๅ?้ไปๅ็ป้ช
        Integer hour=addLockDay.intValue()*24;
        BigDecimal exPerience=number.multiply(new BigDecimal(hour));//่ฟฝๅ?ๅ้็็ป้ช
        releaseDate=hry.util.date.DateUtil.addDaysToDate(releaseDate,addLockDay);//ๆฐ็้ๆพๆถ้ด
        IcoLockAppendRecord appendRecord=new IcoLockAppendRecord();//่ฟฝๅ?่ฎฐๅฝ
        appendRecord.setAppendDay(addLockDay);//่ฟฝๅ?ๅคฉๆฐ
        appendRecord.setCoinCode(icoLockRecord.getCoinCode());//ๅธ็ง
        appendRecord.setLockId(lockId);//้ไปId
        appendRecord.setNumber(number);//ๅ้ไปๆฐ้
        appendRecord.setReleaseDate(releaseDate);//้ๆพๆถ้ด
        appendRecord.setExperience(exPerience.longValue());//่ฟฝๅ?่ทๅพ็็ป้ช
        appendRecordService.save(appendRecord);
        remoteManageService.clearingExperience(customerId,"0101",exPerience.longValue(),number,"่ฟฝๅ?้ไปๅคฉๆฐๅฅๅฑ");
        return new JsonResult().setSuccess(true).setMsg("success");
    }

    /**
     * ้ๆพๆไฝ
     * @param hashMap
     * @return
     */
    @Override
    public JsonResult releaseOperation(HashMap<String, String> hashMap) {
        Long customerId=Long.valueOf(hashMap.get("customerId"));//็จๆทId
        Long lockId=Long.valueOf(hashMap.get("lockId"));//้ไปId
        QueryFilter queryFilter=new QueryFilter(IcoLockRecord.class);
        queryFilter.addFilter("id=",lockId);
        queryFilter.addFilter("state=","0");//(0.ๅพ้ๆพ1.ๅทฒ้ๆพ)
        queryFilter.addFilter("customerId=",customerId);
        IcoLockRecord icoLockRecord=icoLockRecordService.get(queryFilter);
        if(icoLockRecord==null){
            return  new JsonResult().setSuccess(false).setMsg("dingdanyibeishifang");//่ฎขๅๅทฒ่ขซ้ๆพ
        }
         int type= icoLockRecord.getType();
        if(type==1){
            return  new JsonResult().setSuccess(false).setMsg("icobukeshifang");//Ico้ถๆฎตไธๅฏๆๅจ้ๆพ
        }
        RemoteReleaseDeduction releaseDeduction= getReleaseDeductionInfo(icoLockRecord);
        Long releaseDeduct=releaseDeduction.getReleaseDeduct();
        icoLockRecord.setReleaseDeductType(1);//1.็ป้ชๆฃ้ค2.itxๆฃ้ค
        icoLockRecord.setReleaseDeduct(BigDecimal.valueOf(releaseDeduct));//็ป้ชๅผ
        icoLockRecord.setReleaseType(1);//1ๆๅ้ๆพ
        // ๆๅ้ๆพๆฃ้คๅธ  ้ไปๅธๆฐ็ๆฏ็  /่ทๅคฉๆฐ่ฎก็ฎ
        BigDecimal subtractNum=releaseDeduction.getSubtractNum();//้ๆพๆฃ้ค็ๅธไธชๆฐ
        BigDecimal number = icoLockRecord.getNumber();//้ไปๆฐ้
        BigDecimal actualReleaseNum=number.subtract(subtractNum);//้ไปๆฐ้-ๆฃ้คๅธๆฐ้=ๅฎ้้ๆพๆฐ้
//        icoLockRecord.setNumber(actualReleaseNum);
        icoLockRecord.setActualReleaseNum(actualReleaseNum);//ๅฎ้้ๆพๆฐ้
        icoLockRecord.setReleaseDeductNum(subtractNum);//้ๆพๆฃๅธๆฐ้
        JsonResult releaseResult= release(icoLockRecord);
        if(releaseResult.getSuccess()){
            //้ๆพๆฃ้ค็ป้ช
            RemoteResult result=remoteManageService.clearingExperience(Long.valueOf(customerId),"0202",releaseDeduct,number,"้ๆพๆฃ้ค");
            if(!result.getSuccess()){
                logger.error("้ๆพๆฃ้ค็ป้ชๅผๅธธ");
                //return new JsonResult().setSuccess(false).setObj(result.getObj());
            }
        }
        return releaseResult;
    }

    //ๅฎๆถๅจ้ๆพไปปๅก
    public JsonResult releaseMQ(String msgText){
        IcoLockRecord lockRecord=JSON.parseObject(msgText,IcoLockRecord.class);
        return  release(lockRecord);
    }
    /**
     * ้ๆพไปปๅก
     * @param icoLockRecord
     * @return
     */
    private JsonResult release(IcoLockRecord icoLockRecord) {
     //   System.out.println("้ๆพๆไฝ+ใ"+JSON.toJSONString(icoLockRecord));
        Long customerId = icoLockRecord.getCustomerId();
        String coinCode = icoLockRecord.getCoinCode();//้ไปๅธ็ง
        BigDecimal number = icoLockRecord.getNumber();//้ไปๆฐ้
        BigDecimal releaseDeductNum=icoLockRecord.getReleaseDeductNum();//้ๆพๆฃๅธๆฐ้
        BigDecimal actualReleaseNum=icoLockRecord.getActualReleaseNum();//ๅฎ้้ๆพๆฐ้
        if(releaseDeductNum==null){//้ๆพๆฃ้คไธบ0
            releaseDeductNum=new BigDecimal(0);
            icoLockRecord.setReleaseDeductNum(releaseDeductNum);
        }if(actualReleaseNum==null){//ไธบ็ฉบๅฎ้้ๆพไธบ้ไป้
            actualReleaseNum=number;
            icoLockRecord.setActualReleaseNum(actualReleaseNum);
        }
        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(String.valueOf(customerId));
       // System.out.println("userRedis+ใ"+userRedis);
        Long accountId = userRedis.getDmAccountId(coinCode);
       // System.out.println("accountId+ใ"+accountId);
        // ่ทๅ่ต้่ดฆๆท๏ผๅคๆญ่ต้่ดฆๆทไฝ้ข
        ExDigitalmoneyAccountRedis exAccount = UserRedisUtils.getAccount(userRedis.getDmAccountId(coinCode).toString(), coinCode);
        List<Accountadd> listLock = new ArrayList<>();
       // System.out.println("exAccount+ใ"+exAccount);
       // System.out.println("number+ใ"+number);
        List<Accountadd> accountadds = ToAccountUtil.unblockedAssets(accountId, number);//้ๆพ ๅป็ป่ฝฌๅฏ็จ
        listLock.addAll(accountadds);
        exAccount.setHotMoney(exAccount.getHotMoney().add(number));
        exAccount.setColdMoney(exAccount.getColdMoney().subtract(number));

        if(!(releaseDeductNum.compareTo(BigDecimal.ZERO)==0)){
            //้ๆพๆฃ้คๅธ
            Accountadd accountaddss = ToAccountUtil.expenditureHotAssets(accountId, releaseDeductNum);//ๆฃ้ค
            listLock.add(accountaddss);
            exAccount.setHotMoney(exAccount.getHotMoney().subtract(releaseDeductNum));
        }
        String orderNumber = String.valueOf(System.currentTimeMillis());
        IcoTransactionRecord transactionRecord = new IcoTransactionRecord();
        transactionRecord.setCustomerId(customerId);
        transactionRecord.setProjectNumber(orderNumber);//ๆตๆฐดๅท
        transactionRecord.setCoinCode(coinCode);
        transactionRecord.setColdMoney(exAccount.getColdMoney());
        transactionRecord.setHotMoney(exAccount.getHotMoney());
        transactionRecord.setType(12);// 11.้ไป12.้ๆพ13้ไปๆฃ้ค21่ฝฌ่ดฆ31ๅ็บข32ๆจ่ๅฅๅฑ 41.ๅๅธ42.ๆๅธ51.ไนฐๅฅ52.ๅๅบ
        transactionRecord.setState(202);//201.ๆฏๅบ202.ๆถๅฅ
        transactionRecord.setTransactionCount(actualReleaseNum);
        transactionRecord.setRemark("้ๆพๅญๅจ่ฝฌๆต้");
        transactionRecord.setIsShow(1);
        transactionRecord.setForeignKey(icoLockRecord.getId());
        IcoAccount icoAccount=icoAccountService.get(new QueryFilter(IcoAccount.class).addFilter("customerId=",customerId));
        boolean f = icoLockRecordService.releaseLockRecord(icoLockRecord);
         if (f) {
             boolean account= icoAccountService.updateByAccountId( icoAccount.getId(),icoAccount.getVersion(), number.multiply(new BigDecimal(-1)));
             if(!account){
                logger.error("้ๆพๅผๅธธ");
                 throw new RuntimeException();
             }
             icoTransactionRecordService.save(transactionRecord);//ไบคๆๆตๆฐด
             messageProducer.toAccount(JSON.toJSONString(listLock));
            return new JsonResult().setSuccess(true).setMsg("shifangchenggong");//้ๆพๆๅ
        }
        return  new JsonResult().setSuccess(false).setMsg("shifangshibai");//้ๆพๆๅ
    }

    /**
     * ้ๆพๆฃ้ค็ป้ช
     * ไพๅฆ้ไบ100ๅคฉ๏ผๅจ็ฌฌ9ๅคฉ็ๆถๅ่งฃไปๅฐฑๆฃ้ค91%็ๅธ๏ผ
     * ๅช็ป็จๆท9%ใๅนถไธๅ่กๅๆพ็็ป้ชๅผไนๆฏๆ็ธๅๆฏไพๆฃ้คใ
     * ็นๅป้ๆพๅๅผนๅบๆ็คบ้ๆพ้ๆฃ้ค็ๅธๅ็ป้ชๅผ๏ผๅผนๅบ็กฎๅฎๅๅๆถ็็ชๅฃ๏ผ
     * ๅฟ้กปๅ่ฎกๆถ่ฟ10็งๅๆ่ฝ็นๅป็กฎๅฎใ
     * ๅฆๆๅธ็่ฎก็ฎ็ปๆๆๅฐๆฐไฝ๏ผไฟ็4ไฝๅฐๆฐ๏ผ
     * ๅฆๆ็ป้ชๅผๆๅฐๆฐไฝ็ดๆฅ่ๅป
     * @return
     */
    @Override
    public JsonResult getReleaseDeductionInfo(HashMap<String,String> hashMap) {
        String customerId=hashMap.get("customerId");
        String lockId=hashMap.get("lockId");
        QueryFilter queryFilter=new QueryFilter(IcoLockRecord.class);
        queryFilter.addFilter("id=",lockId);
        queryFilter.addFilter("customerId=",customerId);
        queryFilter.addFilter("state=","0");//(0.ๅพ้ๆพ1.ๅทฒ้ๆพ)
        IcoLockRecord icoLockRecord=icoLockRecordService.get(queryFilter);
        if(icoLockRecord==null){
            return  new JsonResult().setSuccess(false).setMsg("dingdanyibeishifang");//่ฎขๅๅทฒ่ขซ้ๆพ
        }
        int type= icoLockRecord.getType();
        if(type==1){
            return  new JsonResult().setSuccess(false).setMsg("icobukeshifang");//Ico้ถๆฎตไธๅฏๆๅจ้ๆพ
        }
        // ่ฐ็จ่ทๅ็จๆทๅฝๅ็ป้ชๅผๆปๆฐ
        JsonResult levelResult= seeCustomerLevelAccount(Long.valueOf(customerId));
        if(!levelResult.getSuccess()){
            return levelResult;
        }
        IcoCustomerLevel level=ObjectUtil.bean2bean( levelResult.getObj(),IcoCustomerLevel.class);
        Long userExperience=level.getExperience();//็จๆทๅฝๅ็ป้ชๅผ
        RemoteReleaseDeduction releaseDeduction =getReleaseDeductionInfo(icoLockRecord);
        Long deductionExp=releaseDeduction.getReleaseDeduct(); //้ๆพๆฃ้ค็ป้ช
        if(deductionExp>userExperience){
            return new JsonResult().setSuccess(false).setMsg("jingyanbuzubukecaozuo").setObj(releaseDeduction);//็ป้ชไธ่ถณไธๅฏๆไฝ
        }
        return new JsonResult().setSuccess(true).setMsg("success").setObj(releaseDeduction);
    }

    //ๆฐ่ทๅ้ๆพๆฃ้ค็ๅธๅๆฃ้ค็็ป้ช
    private  RemoteReleaseDeduction getReleaseDeductionInfo(IcoLockRecord icoLockRecord){
        RemoteReleaseDeduction releaseDeduction =new RemoteReleaseDeduction();
        //่ทๅ้ๆพๆฃ้คitxๆฐ้
        BigDecimal subtractNum=icoLockRecord.getNumber();
        Long releaseDeduct=0l;//้ๆพๆฃ้ค็ป้ช
        Date now =new Date();//ๅฝๅๆถ้ด
        Date releaseDate=icoLockRecord.getReleaseDate();//้ๆพๆถ้ด
        Integer lockDay= icoLockRecord.getLockDay();//้ไปๅคฉๆฐ
        try {
            Integer day=DateUtil.daysBetween(now,releaseDate);
            if(day.intValue()==icoLockRecord.getLockDay().intValue()){
                releaseDeduction.setSubtractNum(subtractNum);//ๆฃ้ค็ๆฐ้
                releaseDeduct=new BigDecimal(24*day).multiply(subtractNum).longValue();
                releaseDeduction.setReleaseDeduct(releaseDeduct);
                releaseDeduction.setReleaseDeductType(1);//้ๆพๆฃ้ค็ฑปๅ๏ผ1.็ป้ชๆฃ้ค2.itxๆฃ้ค๏ผ
                return releaseDeduction;
            }
            BigDecimal ratio=new BigDecimal(day).divide(new BigDecimal(lockDay),2,BigDecimal.ROUND_DOWN);
            releaseDeduct= new BigDecimal(24*day).multiply(icoLockRecord.getNumber())
                    .setScale(0,BigDecimal.ROUND_DOWN).longValue();
            subtractNum=BigDecimalUtil.bigDecimalScaleDigit(subtractNum.multiply(ratio),4);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        releaseDeduction.setSubtractNum(subtractNum);//ๆฃ้ค็ๆฐ้
        releaseDeduction.setReleaseDeduct(releaseDeduct);
        releaseDeduction.setReleaseDeductType(1);//้ๆพๆฃ้ค็ฑปๅ๏ผ1.็ป้ชๆฃ้ค2.itxๆฃ้ค๏ผ
        return releaseDeduction;
    }
    /**
     *  ๆต้่ฝฌๅญๅจ๏ผ้ไป๏ผ
     */
    @Override
    public JsonResult toLockStorage(Long customerId , Integer lockDay, BigDecimal number,String coinCode,boolean isIco){
        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(String.valueOf(customerId));
        // ่ทๅ่ต้่ดฆๆท๏ผๅคๆญ่ต้่ดฆๆทไฝ้ข
        ExDigitalmoneyAccountRedis exAccount = UserRedisUtils.getAccount(userRedis.getDmAccountId(coinCode).toString(),coinCode);
        if(exAccount.getHotMoney().compareTo(number)==-1){
            return new JsonResult().setSuccess(false).setMsg("zijinbuzu");//่ต้ไธ่ถณ
        }
        JsonResult jsonResult=getLockDeductionInfo(customerId);//่ทๅพ้ไปๆฃ้ค็ป้ชๆitxๅธ
        if(!jsonResult.getSuccess()){
            return  jsonResult;
        }
        RemoteLockDeduction remoteLockDeduction=ObjectUtil.bean2bean(jsonResult.getObj(),RemoteLockDeduction.class);
        Integer lockDeductType=remoteLockDeduction.getLockDeductType();//ๆฃ้ค็ฑปๅ1.็ป้ชๆฃ้ค2.itxๆฃ้ค
        BigDecimal lockDeduct=remoteLockDeduction.getLockDeduct();//ๆฃ้คๆฐ้
        List<Accountadd>listLock=new ArrayList<>();
        Long accountId = userRedis.getDmAccountId(exAccount.getCoinCode());
        List<Accountadd> accountadds=ToAccountUtil.frozenAssets(accountId,number);
        listLock.addAll(accountadds);
        exAccount.setHotMoney(exAccount.getHotMoney().subtract(number));
        exAccount.setColdMoney(exAccount.getColdMoney().add(number));
        String orderNumber=String.valueOf(System.currentTimeMillis());
       IcoTransactionRecord transactionRecord2=new IcoTransactionRecord();
        transactionRecord2.setCustomerId(customerId);
        transactionRecord2.setProjectNumber(orderNumber);//ๆตๆฐดๅท
        transactionRecord2.setCoinCode(exAccount.getCoinCode());
        transactionRecord2.setColdMoney(exAccount.getColdMoney());
        transactionRecord2.setHotMoney(exAccount.getHotMoney());
        transactionRecord2.setType(11);// 11.้ไป12.้ๆพ13้ไปๆฃ้ค21่ฝฌ่ดฆ31ๅ็บข32ๆจ่ๅฅๅฑ 41.ๅๅธ42.ๆๅธ51.ไนฐๅฅ52.ๅๅบ
        transactionRecord2.setState(201);//201.ๆฏๅบ202.ๆถๅฅ
        transactionRecord2.setTransactionCount(number);
        transactionRecord2.setIsShow(1);
        transactionRecord2.setRemark("ๆต้่ฝฌๅฅๅญๅจ");
        IcoLockRecord icoLockRecord=new IcoLockRecord();
        icoLockRecord.setCustomerId(customerId);
        icoLockRecord.setCoinCode(coinCode);
        icoLockRecord.setNumber(number);//้ไป้้ข
        icoLockRecord.setLockDay(lockDay);//้ไปๅคฉๆฐ
        icoLockRecord.setState(0);
        icoLockRecord.setReleaseDate(hry.util.date.DateUtil.addDaysToDate(new Date(),lockDay));//้ๆพๆฅๆ
        //TODO ็จไบๆต่ฏๅไธบๆนไธบๅ้
          // icoLockRecord.setReleaseDate(hry.util.date.DateUtil.addMinToDate(new Date(),lockDay));//้ๆพๆฅๆ

        icoLockRecord.setLockDeduct(lockDeduct);//ๆฃ้คๆฐ้
        icoLockRecord.setLockDeductType(lockDeductType);//ๆฃ้ค็ฑปๅ
        if(isIco){
            icoLockRecord.setType(1);//(1.ico้ถๆฎต0.้ico)
        }
        IcoAccount icoAccount=icoAccountService.get(new QueryFilter(IcoAccount.class).addFilter("customerId=",customerId));

        icoLockRecord.setCurrentLockSum(icoAccount.getStorageMoney());
        if(lockDeductType.intValue()==2){//ๆ?ๅธ
            if(exAccount.getHotMoney().compareTo(lockDeduct)==-1){
                return new JsonResult().setSuccess(false).setMsg("้ไปๆฃ้คไธ่ถณ");
            }
            exAccount.setHotMoney(exAccount.getHotMoney().subtract(lockDeduct));
        }else if(lockDeductType.intValue()==1){//ๆฃ็ป้ช
            //่ฐ็จๆฃ้ค่ดฆๆท็ป้ช
            /**
             * ็ป็จๆทๆทปๅ?ๆๅๅฐ็ป้ชๅผ
             * @param customerId  ็จๆทid
             * @param account_type  ไบคๆ็ฑปๅ๏ผ0101 ้ไปๅฅๅฑ๏ผ0102 ๆณจๅ่ต?้๏ผ0201 ้ไปๆฃ้ค๏ผ0202 ้ๆพๆฃ้ค0203ๆๆซๆฃ้ค๏ผ
             * @param experience  ็ป้ชๅผ๏ผๆ?ๆญฃ่ด๏ผ
             * @param money ๆๅธๆฐ๏ผไบคๆ็ฑปๅๅฑไบ 0102ๆถ ไผ?null๏ผ
             * @param upgradeNote  ๅคๆณจ
             * @return
             */
            RemoteResult result=remoteManageService.clearingExperience(Long.valueOf(customerId),"0201",lockDeduct.longValue(),number,"้ไปๆฃ้ค");
            if(!result.getSuccess()){
                return new JsonResult().setSuccess(false).setObj(result.getObj());
            }
        }
        //2019ๅนด3ๆ19ๆฅ ๆนไธบ้ไปๅๆถๅ็ป้ช
        Integer hour=lockDay.intValue()*24;
        BigDecimal exPerience=number.multiply(new BigDecimal(hour));
        RemoteResult rewardResult=remoteManageService.clearingExperience(Long.valueOf(customerId),"0101",exPerience.longValue(),number,"้ไปๆๅๅฅๅฑ");
        if(!rewardResult.getSuccess()){
            return new JsonResult().setSuccess(false).setObj(rewardResult.getObj());
        }
        boolean f=icoAccountService.updateByAccountId(icoAccount.getId(),icoAccount.getVersion(),number);
        if(!f){
            return new JsonResult().setSuccess(false).setMsg("suocangshibai");//้ไปๅคฑ่ดฅ
        }
        icoLockRecordService.save(icoLockRecord);//้ไป่ฎฐๅฝ

        if(lockDeductType.intValue()==2){//ๆ?ๅธ
            IcoTransactionRecord transactionRecord3=new IcoTransactionRecord();
            transactionRecord3.setCustomerId(customerId);
            transactionRecord3.setProjectNumber(String.valueOf(System.currentTimeMillis()));//ๆตๆฐดๅท
            transactionRecord3.setCoinCode(exAccount.getCoinCode());
            transactionRecord3.setColdMoney(exAccount.getColdMoney());
            transactionRecord3.setHotMoney(exAccount.getHotMoney());
            transactionRecord3.setType(13);// 11.้ไป12.้ๆพ13.้ไปๆฃๅธ21่ฝฌ่ดฆ31ๅ็บข32ๆจ่ๅฅๅฑ 41.ๅๅธ42.ๆๅธ51.ไนฐๅฅ52.ๅๅบ
            transactionRecord3.setState(201);//201.ๆฏๅบ202.ๆถๅฅ
            transactionRecord3.setTransactionCount(lockDeduct);
            transactionRecord3.setRemark("้ไปๆฃ้คๅธ");
            transactionRecord3.setForeignKey(icoLockRecord.getId());
            icoTransactionRecordService.save(transactionRecord3);//ไบคๆๆตๆฐด
            Accountadd accountDeduct=ToAccountUtil.expenditureHotAssets(accountId,lockDeduct);
            listLock.add(accountDeduct);//ๆฃ้คitx
        }
        transactionRecord2.setForeignKey(icoLockRecord.getId());
        icoTransactionRecordService.save(transactionRecord2);//ไบคๆๆตๆฐด
        //TODO ๆต่ฏไฝฟ็จ ๅฎ้ไธๅกๆๅฐไธบ1ๅคฉไธ้่ฆๅฅ้
        // iDelayedOrder.orderDelay(icoLockRecord);//้ๆถ้ๅ

        messageProducer.toAccount(JSON.toJSONString(listLock));

        RemoteMessage message=new RemoteMessage();
        message.setMsgKey(MessageType.Message_Lock_Remind.getKey());//ๆถๆฏ็ฑปๅ ๆจกๆฟKEY//
        message.setMsgType("1,2");// 1.็ซๅไฟก๏ผ2.็ญไฟก,
        message.setUserId(customerId.toString());
        messageProducer.toMessageWarn(JSON.toJSONString(message));//ๆถๆฏๆ้
        return new JsonResult().setSuccess(true).setObj("success");
    }


    /**
     * ่ทๅ้ไปๆ็ป
     * @param hashMap
     * @return
     */
    @Override
    public FrontPage getLockDetailed(HashMap<String, String> hashMap) {
        String customerId=hashMap.get("customerId");
        String state=hashMap.get("state");//็ถๆ(0.ๅพ้ๆพ1.ๅทฒ้ๆพ)
        String page=hashMap.get("page")==null?"0":hashMap.get("page");
        String pageSize=hashMap.get("pageSize")==null?"10":hashMap.get("pageSize");

        String starTime=hashMap.get("starTime");//ๅผๅงๆถ้ด
        String endTime=hashMap.get("endTime");//็ปๆๆถ้ด
        if(Integer.parseInt(pageSize)>100){
            pageSize="10";
        }
        QueryFilter queryFilter=new QueryFilter(IcoLockRecord.class);
        queryFilter.addFilter("customerId=",customerId);
        if(StringUtil.isNull(state)){
            queryFilter.addFilter("state=",state);
        }
        if(StringUtil.isNull(starTime)){
            queryFilter.addFilter("created>=",starTime);
        }
        if(StringUtil.isNull(endTime)){
            queryFilter.addFilter("created<=",endTime);
        }
        queryFilter.setOrderby("created desc");
        queryFilter.setPage(Integer.parseInt(page));
        queryFilter.setPageSize(Integer.parseInt(pageSize));
        Page<IcoLockRecord> orderPage= icoLockRecordService.findPage(queryFilter);
        if(orderPage.size()>0){
            List<RemoteIcoLockRecord> list=ObjectUtil.beanList(orderPage.getResult(),RemoteIcoLockRecord.class);
           // if("0".equals(state)){//ๅพ้ๆพ่ทๅ ๆฃ้ค็็ป้ช
                if(list!=null&&list.size()>0){
                    //้ico้ถๆฎต่ทๅๅธธ่งๆฃ้ค็ป้ชๅผ
                    JsonResult jsonResult= getPlatformRule(RulesConfig.RulesCommonKey);
                    BigDecimal releaseExperienceDeduct=null;
                    if(jsonResult.getSuccess()){
                        IcoRulesConfig rulesConfig = ObjectUtil.bean2bean(jsonResult.getObj(), IcoRulesConfig.class);
                        releaseExperienceDeduct=new BigDecimal(rulesConfig.getReleaseExperienceDeduct());
                    }
                    for (RemoteIcoLockRecord lockRecord:list ) {
                        if(lockRecord.getState()==0&&lockRecord.getType()==0){
                             lockRecord.setReleaseDeductType(1);//ๆฃ้ค็ฑปๅ
                             lockRecord.setReleaseDeduct(releaseExperienceDeduct);//้ๆพๆฃ้ค็็ป้ช
                        }
                    }
                }
           // }
            FrontPage frontPage = new FrontPage(list, orderPage.getTotal(), orderPage.getPages(), orderPage.getPageSize());
            return frontPage;
        }
        return new FrontPage(null, 0, 1, 10);
    }
    /**
     * ้ไปๆฃ้คไฟกๆฏ(ไผๅๆฃ็ป้ช๏ผ็ป้ชไธ่ถณๆฃๅธ)
     * @param
     * @return
     */
    @Override
    public JsonResult getLockDeductionInfo(Long customerId) {
        //String customerId=hashMap.get("customerId");//็จๆทId
        RemoteLockDeduction remoteLockDeduction=new RemoteLockDeduction();
        JsonResult isIco=isCheckICOTime();
        Long deductionItx=null; //ๆฃ้คๅธ้
        Long deductionExp=null; //ๆฃ้ค็ป้ช
        Integer lockDeductType=1; //ๆฃ้ค็ฑปๅ๏ผไผๅๆฃ้ค็ป้ช๏ผ
        // ่ฐ็จ่ทๅ็จๆทๅฝๅ็ป้ชๅผๆปๆฐ
        JsonResult levelResult= seeCustomerLevelAccount(customerId);
        if(!levelResult.getSuccess()){
            return levelResult;
        }
        IcoCustomerLevel level=ObjectUtil.bean2bean( levelResult.getObj(),IcoCustomerLevel.class);
        Long userExperience=level.getExperience();//็จๆทๅฝๅ็ป้ชๅผ
        if(isIco.getSuccess()){
            //ico้ถๆฎต่ทๅicoๆฃ้ค็ป้ชๅผ
            JsonResult jsonResult=getPlatformRule(RulesConfig.RulesICOKey);
            IcoRulesConfig rulesConfig=ObjectUtil.bean2bean(jsonResult.getObj(),IcoRulesConfig.class);
            deductionItx= Long.valueOf(rulesConfig.getIcoLockItxDeduct());
            deductionExp= Long.valueOf(rulesConfig.getIcoLockExperienceDeduct());
        }else{
            //้ico้ถๆฎต่ทๅๅธธ่งๆฃ้ค็ป้ชๅผ
            JsonResult jsonResult= getPlatformRule(RulesConfig.RulesCommonKey);
            IcoRulesConfig rulesConfig=ObjectUtil.bean2bean(jsonResult.getObj(),IcoRulesConfig.class);
            deductionItx= Long.valueOf(rulesConfig.getItxDeduct());
            deductionExp= Long.valueOf(rulesConfig.getLockExperienceDeduct());
        }
        if(userExperience>=deductionExp){//ๅฝๅ็ป้ช่ถณๅคๆฃ้ค
            remoteLockDeduction.setLockDeduct(new BigDecimal(deductionExp));
        }else{
            lockDeductType=2;//ๆฃ้คๅธitx
            remoteLockDeduction.setLockDeduct(new BigDecimal(deductionItx));
        }
        remoteLockDeduction.setLockDeductType(lockDeductType);
        return new JsonResult().setSuccess(true).setObj(remoteLockDeduction).setMsg("success");
    }

    /**
     * ่ทๅicoๅธ่ดฆๆทไฟกๆฏ
     * @param hashMap
     * @return
     */
    @Override
    public JsonResult getIcoAccountInfo(HashMap<String, String> hashMap) {
        String customerId=hashMap.get("customerId");//็จๆทId
        RemoteIcoAccount result=new RemoteIcoAccount();
        // ๆฅredis็ผๅญ
        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(customerId.toString());
        IcoAccount icoAccount= icoAccountService.get(new QueryFilter(IcoAccount.class).addFilter("customerId=",customerId));
        String coinCode=icoAccount.getCoinCode();
        ExDigitalmoneyAccountRedis exaccount = UserRedisUtils.getAccount(userRedis.getDmAccountId(coinCode).toString(), coinCode);
        //ไฟ็็ๅฐๆฐไฝ
        int keepDigit=coinCodeKeepDigitService.getCoinCodeKeepDigit(coinCode);
        result.setKeepDigit(keepDigit);
        result.setCoinCode(exaccount.getCoinCode());
        result.setHotMoney( BigDecimalUtil.bigDecimalScaleDigit(exaccount.getHotMoney(),keepDigit));//ๅฏ็จ
        result.setColdMoney( BigDecimalUtil.bigDecimalScaleDigit(exaccount.getColdMoney(),keepDigit));//ๅป็ป
        result.setStorageMoney(BigDecimalUtil.bigDecimalScaleDigit(icoAccount.getStorageMoney(),keepDigit));
        result.setTotalMoney(BigDecimalUtil.bigDecimalScaleDigit(exaccount.getHotMoney().add(exaccount.getColdMoney()),keepDigit));//ๆปๆฐ
        //่ช่บซ้ไปๆฐ/ๆๆ็จๆท็ๆป้ไปๆฐ)*40%*(100%+็ญ็บงๅ?ๆ็พๅๆฏ)=้ไปๆฏ
       JsonResult levelAccount= seeCustomerLevelAccount(Long.valueOf(customerId));
       if(levelAccount.getSuccess()){
           RemoteIcoCustomerLevel remoteIcoCustomerLevel = ObjectUtil.bean2bean(levelAccount.getObj(), RemoteIcoCustomerLevel.class);

           BigDecimal additionRatio= (new BigDecimal(remoteIcoCustomerLevel.getAdditionRatio()).add(new BigDecimal(100)))
                   .divide(new BigDecimal(100),8,BigDecimal.ROUND_DOWN);//็ญ็บงๅ?ๆๆฏ็
           //่ช่บซ้ไปๅ?ๆฏ 6ไฝๅฐๆฐ
           String atio=icoAccountService.getMemberLockAtio(Long.valueOf(customerId));
           //RATIO 40%  100%+็ญ็บงๅ?ๆ็พๅๆฏ
           BigDecimal accountAtio=new BigDecimal(atio).multiply(additionRatio).multiply(new BigDecimal(RATIO));
           result.setAccountAtio(accountAtio.setScale(6,BigDecimal.ROUND_DOWN));//่ช่บซ้ไปๆฏ็
       }
        return new JsonResult().setSuccess(true).setObj(result);
    }

    /**
     * ่ฝฌ่ดฆๆฅๅฃ
     * @param hashMap
     * @return
     */
    @Override
    public JsonResult transferAccounts(HashMap<String, String> hashMap) {
        String customerId=hashMap.get("customerId");//็จๆทId
        String coinCode=hashMap.get("coinCode");//่ฝฌ่ดฆ็ๅธ็ง
        BigDecimal number=new BigDecimal(hashMap.get("number"));//่ฝฌ่ดฆ็ๆฐ้
        String toPublickKey=hashMap.get("toPublickKey");//ๆถๅฅๆน็ๅธ็งๅฐๅ
        if(toPublickKey==null||"".equals(toPublickKey)){
            return new JsonResult().setSuccess(false).setMsg("shourufangdizhicuowu");//ๆถๅฅๆน็ๅฐๅ้่ฏฏ
        }
        // ๆฅredis็ผๅญ
        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(customerId);
        ExDigitalmoneyAccountRedis exaccount = UserRedisUtils.getAccount(userRedis.getDmAccountId(coinCode).toString(), coinCode);
         if(exaccount==null){
               return new JsonResult().setSuccess(false).setMsg("bizhanghubucunzai");//ๅธ็ง่ดฆๆทไธๅญๅจ
          }
        if(exaccount.getHotMoney().compareTo(number)==-1){//่ต้ไธ่ถณ
            return new JsonResult().setSuccess(false).setMsg("zijinbuzu");//่ต้ไธ่ถณ
        }
        exaccount.setHotMoney(exaccount.getHotMoney().subtract(number));
        ExDigitalmoneyAccount exDigitalmoneyAccount=exDigitalmoneyAccountService.get(new QueryFilter(ExDigitalmoneyAccount.class).addFilter("publicKey=",toPublickKey).addFilter("coinCode=",coinCode));
        if(exDigitalmoneyAccount==null){
            return new JsonResult().setSuccess(false).setMsg("shourufangdizhicuowu");//ๆถๅฅๆน็ๅฐๅ้่ฏฏ
        }
        String toCustomerId=exDigitalmoneyAccount.getCustomerId().toString();
        if(toCustomerId.equals(customerId)){
            return new JsonResult().setSuccess(false).setMsg("bunengxiangzijizhuanzhang");//ไธ่ฝๅ่ชๅทฑ่ฝฌ่ดฆ
        }
        List<Accountadd>listLock=new ArrayList<>();

        UserRedis toUserRedis = redisUtil.get(toCustomerId);
        ExDigitalmoneyAccountRedis toExaccount = UserRedisUtils.getAccount(toUserRedis.getDmAccountId(coinCode).toString(), coinCode);
        toExaccount.setHotMoney(toExaccount.getHotMoney().add(number));
        Long accountId = userRedis.getDmAccountId(coinCode);
        Long toAccountId = toUserRedis.getDmAccountId(coinCode);//ๆถๅฅๆน่ดฆๆทID
        Accountadd accountadd=ToAccountUtil.expenditureHotAssets(accountId,number);//ๆฏๅบ
        Accountadd toAccountadd=ToAccountUtil.ncomeAssets(toAccountId,number);//ๆถๅฅ
        listLock.add(accountadd);
        listLock.add(toAccountadd);

        String serialNumber=String.valueOf(System.currentTimeMillis());
        IcoTransactionRecord transactionRecord=new IcoTransactionRecord();
        transactionRecord.setCustomerId(Long.valueOf(customerId));
        transactionRecord.setProjectNumber(serialNumber);//ๆตๆฐดๅท
        transactionRecord.setCoinCode(exaccount.getCoinCode());
        transactionRecord.setColdMoney(exaccount.getColdMoney());
        transactionRecord.setHotMoney(exaccount.getHotMoney());
        transactionRecord.setType(21);// 11.้ไป12.้ๆพ13.้ไปๆฃๅธ21่ฝฌ่ดฆๅฅ22่ฝฌ่ดฆๅบ31ๅ็บข32ๆจ่ๅฅๅฑ 41.ๅๅธ42.ๆๅธ51.ไนฐๅฅ52.ๅๅบ
        transactionRecord.setState(201);//201.ๆฏๅบ202.ๆถๅฅ
        transactionRecord.setTransactionCount(number);
        transactionRecord.setIsShow(1);
        transactionRecord.setRemark("่ฝฌ่ดฆๆฏๅบ");

        IcoTransactionRecord transactionRecord2=new IcoTransactionRecord();
        transactionRecord2.setCustomerId(Long.valueOf(toCustomerId));
        transactionRecord2.setProjectNumber(serialNumber);//ๆตๆฐดๅท
        transactionRecord2.setCoinCode(toExaccount.getCoinCode());
        transactionRecord2.setColdMoney(toExaccount.getColdMoney());
        transactionRecord2.setHotMoney(toExaccount.getHotMoney());
        transactionRecord2.setType(22);// 11.้ไป12.้ๆพ13.้ไปๆฃๅธ21่ฝฌ่ดฆๅฅ22่ฝฌ่ดฆๅบ31ๅ็บข32ๆจ่ๅฅๅฑ 41.ๅๅธ42.ๆๅธ51.ไนฐๅฅ52.ๅๅบ
        transactionRecord2.setState(202);//201.ๆฏๅบ202.ๆถๅฅ
        transactionRecord2.setTransactionCount(number);
        transactionRecord2.setIsShow(1);
        transactionRecord2.setRemark("่ฝฌ่ดฆๆถๅฅ");
        IcoTransferAccounts transferAccounts=new IcoTransferAccounts();
        transferAccounts.setSerialNumber(serialNumber);
        transferAccounts.setCoinCode(coinCode);
        transferAccounts.setCustomerId(Long.valueOf(customerId));
        transferAccounts.setToCustomerId(Long.valueOf(toCustomerId));
        transferAccounts.setMoney(number);
        transferAccounts.setRemark("่ฝฌ่ดฆ่ฎฐๅฝ");
        icoTransferAccountsService.save(transferAccounts);//่ฝฌ่ดฆ่ฎฐๅฝ่กจ
        transactionRecord.setForeignKey(transferAccounts.getId());
        transactionRecord2.setForeignKey(transferAccounts.getId());
        icoTransactionRecordService.save(transactionRecord);//ไบคๆๆตๆฐด
        icoTransactionRecordService.save(transactionRecord2);//ไบคๆๆตๆฐด

        messageProducer.toAccount(JSON.toJSONString(listLock));
        RemoteMessage message=new RemoteMessage();
        message.setMsgKey(MessageType.Message_RollOut_Reminder.getKey());//ๆถๆฏ็ฑปๅ ๆจกๆฟKEY//
        message.setMsgType("1,2");// 1.็ซๅไฟก๏ผ2.็ญไฟก,
        message.setUserId(customerId);
        messageProducer.toMessageWarn(JSON.toJSONString(message));//ๆถๆฏๆ้
        message.setMsgKey(MessageType.Message_RoolIn_Reminder.getKey());//ๆถๆฏ็ฑปๅ ๆจกๆฟKEY//
        message.setMsgType("1,2");// 1.็ซๅไฟก๏ผ2.็ญไฟก,
        message.setUserId(toCustomerId);
        messageProducer.toMessageWarn(JSON.toJSONString(message));//ๆถๆฏๆ้

        return new JsonResult().setSuccess(true).setMsg("success");
    }


    @Override
    public  JsonResult addCustomerLevelAccount(Long customerId){
        try{
            IcoCustomerLevel icoCustomerLevel = new IcoCustomerLevel();
            RemoteResult remoteResult = remoteManageService.countLevel(0L);
            if (remoteResult.getSuccess()){
                IcoUpgradeConfig icoUpgradeConfig =(IcoUpgradeConfig) remoteResult.getObj();
                icoCustomerLevel.setLevel_id(icoUpgradeConfig.getId());
                icoCustomerLevel.setGradeName(icoUpgradeConfig.getGradeName());
                icoCustomerLevel.setSort(icoUpgradeConfig.getSort());
            }
            icoCustomerLevel.setCustomer_id(customerId);
            icoCustomerLevel.setExperience(0L);
            icoCustomerLevelService.save(icoCustomerLevel);
            return new JsonResult().setSuccess(true).setObj(icoCustomerLevel).setMsg("success");
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult().setSuccess(false).setMsg("tianjiadengjizhanghushibai");//ๆทปๅ?็ญ็บง่ดฆๆทๅคฑ่ดฅ
        }
    }
    /**
     * ๆฅ็็จๆทๆจ่็ญ็บง
     * @return
     */
    public  JsonResult seeCustomerLevelAccount(Long customerId){
        try{
            QueryFilter queryFilter = new QueryFilter(IcoCustomerLevel.class);
            queryFilter.addFilter("customer_id=",customerId);
            IcoCustomerLevel icoCustomerLevel = icoCustomerLevelService.get(queryFilter);
            //ไธไธช็จๆทๅชๆไธไธช่ดฆๆท็ญ็บง
            RemoteIcoCustomerLevel remoteIcoCustomerLevel = ObjectUtil.bean2bean(icoCustomerLevel, RemoteIcoCustomerLevel.class);
            Long levelId= icoCustomerLevel.getLevel_id();
            IcoUpgradeConfig upgradeConfig= upgradeConfigService.get(levelId);
            if(upgradeConfig!=null){ //็ญ็บงๅ?ๆๆฏ็
                remoteIcoCustomerLevel.setAdditionRatio(upgradeConfig.getAdditionRatio());
            }
            return new JsonResult().setSuccess(true).setObj(remoteIcoCustomerLevel).setMsg("success");
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult().setSuccess(false).setMsg("xitongyichang");//็ณป็ปๅผๅธธ

        }
    }


    @Override
    public  FrontPage queryExperiencesDetail(HashMap<String, String> map){

        //----------------------ๆฅ่ฏขๅผๅง------------------------------
        String date=map.get("date");//ๆถ้ด
        if(StringUtil.isNull(date)){
            String startTime=date+"-01";
            map.put("startTime",startTime);
           try{
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               Date dt = sdf.parse(startTime);
               Calendar rightNow = Calendar.getInstance();
               rightNow.setTime(dt);
               rightNow.add(Calendar.MONTH, 1);
               Date dt1 = rightNow.getTime();
               String endTime = sdf.format(dt1);
               map.put("endTime",endTime);
           }catch (Exception e){
               e.printStackTrace();
           }

        }
        FrontPage frontPage = icoExperienceService.queryExperiencesDetail(map);
        return frontPage;

    }


    /**
     * ่ทๅๅ็บข่ฎฐๅฝ
     * @param hashMap
     * @return
     */
    @Override
    public FrontPage queryDividendRecord(HashMap<String, String> hashMap){
        String startTime=(String)hashMap.get("startTime");//ๅผๅงๆถ้ด
        String endTime=(String)hashMap.get("endTime");//็ปๆๆถ้ด
        String coinCode=(String)hashMap.get("coinCode");//็ปๆๆถ้ด
        if(StringUtil.isNull(coinCode)){
            hashMap.put("coinCode","%"+coinCode+"%");
        }
        if(startTime==null||endTime==null){//้ป่ฎคๆฅ่ฏขๅฝๅคฉ็
            String now= DateUtil.getNowDate();
            hashMap.put("startTime",now);
            hashMap.put("endTime",now);
        }
        FrontPage frontPage = icoDividendRecordService.queryDividendRecord(hashMap);
        return frontPage;
    }



    /**
     * ่ทๅ็จๆท้ขๆต็ญ็บงๅๅฝๅ็ญ็บง
     * @param map
     * @return
     */
    @Override
    public JsonResult predictUserLevel(Map<String, Object> map){
        try{
            Long customerId = Long.valueOf(map.get("customerId").toString());
            //่ฟๅๅผ
            HashMap<String, Object> hashMap = new HashMap<>();
            //่ทๅๆจ่ไบบไฟกๆฏ
            AppCustomer appCustomer = appCustomerService.get(customerId);
            QueryFilter queryFilterAppCommendUser = new QueryFilter(AppCommendUser.class);
            queryFilterAppCommendUser.addFilter("uid=",customerId);
            List<AppCommendUser> appCommendUsers = appCommendUserService.find(queryFilterAppCommendUser);
            String pname="";//ๆฒกๆฅๅฐไธ็บงไฟกๆฏ
            if(null != appCommendUsers && appCommendUsers.size() > 0 ){
                QueryFilter queryFilterAppPersonInfo = new QueryFilter(AppPersonInfo.class);
                queryFilterAppPersonInfo.addFilter("customerId=",appCommendUsers.get(0).getPid());
                List<AppPersonInfo> appPersonInfos = appPersonInfoService.find(queryFilterAppPersonInfo);
                if(null != appPersonInfos && appPersonInfos.size() > 0 ){
                    AppPersonInfo appPersonInfo = appPersonInfos.get(0);
                    if(null != appPersonInfo.getMobilePhone() && !appPersonInfo.getMobilePhone().equals("") ){
                        pname=appPersonInfo.getMobilePhone();
                    }else {
                        pname=appPersonInfo.getEmail();
                    }
                }
            }
            //่ทๅ็จๆทๅฝๅ็ญ็บงๅ็ป้ช
            JsonResult jsonResult = this.seeCustomerLevelAccount(customerId);
            RemoteIcoCustomerLevel remoteIcoCustomerLevel = (RemoteIcoCustomerLevel)jsonResult.getObj();
            QueryFilter queryFilterIcoAccount = new QueryFilter(IcoAccount.class);
            queryFilterIcoAccount.addFilter("customerId=",customerId);
            IcoAccount icoAccount = icoAccountService.get(queryFilterIcoAccount);
            //ๅฝๅ็ป้ช
            Long nowExperience = remoteIcoCustomerLevel.getExperience();
            hashMap.put("created",appCustomer.getCreated());//ๆณจๅๆถ้ด
            hashMap.put("modified",icoAccount.getModified());//ๆดๆฐๆถ้ด
            hashMap.put("nowGradeName",remoteIcoCustomerLevel.getGradeName());
            hashMap.put("pname",pname);//ไธ็บง
            hashMap.put("nowExperience",nowExperience);//ๅฝๅ็ป้ช
            return new JsonResult().setSuccess(true).setObj(hashMap).setMsg("success");

        }catch (Exception e){
            return new JsonResult().setSuccess(false).setMsg("xitongyichang");//็ณป็ปๅผๅธธ

        }
    }


    //่ฎก็ฎไฟฉๆถ้ด็ๅฐๆถๆฐๅทฎ
    private static int differHourNum(long time1,long time2){
//        long dif=(time2-time1)/(60*60*1000);
        long dif=((time2/1000)-(time1/1000))/(60*60);
        return ((Long) dif).intValue();
    }


 /*   public static void main(String[] args) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//่ฎพ็ฝฎๆฅๆๆ?ผๅผ
        String datec = df.format(1552291200000L);// new Date()ไธบ่ทๅๅฝๅ็ณป็ปๆถ้ด๏ผไนๅฏไฝฟ็จๅฝๅๆถ้ดๆณ
        System.out.println(datec);
        //        System.out.println(new Date(1552290314000l));
        String dater = df.format(1552463114000L);// new Date()ไธบ่ทๅๅฝๅ็ณป็ปๆถ้ด๏ผไนๅฏไฝฟ็จๅฝๅๆถ้ดๆณ
        System.out.println(dater);

//        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");//่ฎพ็ฝฎๆฅๆๆ?ผๅผ
//
//        String date1 = df1.format(1551686884167L);// new Date()ไธบ่ทๅๅฝๅ็ณป็ปๆถ้ด๏ผไนๅฏไฝฟ็จๅฝๅๆถ้ดๆณ
//        int datec = Integer.valueOf("0000");
//        System.out.println("11111:  "+date1);
//        System.out.println(datec);
        int hour=differHourNum(1552291200000L,1552463114000L);//่ฎก็ฎไธๆฌก็ธๅทฎๅฐๆถๆฐ
        System.out.println("hour:  "+hour);
        SimpleDateFormat dfymd = new SimpleDateFormat("yyyy-MM-dd");//่ฎพ็ฝฎๆฅๆๆ?ผๅผ
        String formatc = dfymd.format(1552291200000L);
        String formatr = dfymd.format(1552463114000L);
        //้ๅฝๅคฉๅฐๆใๆดๆฐๆถ้ด็ๅ็งๆฏ้ๆพๆถ้ด็ๅ็งๅฐใๅฐๆถๆฐๅ1
        System.out.println(formatc);
        System.out.println(formatr);
        System.out.println(!formatc.equals(formatr));
//่ทๅๅ็ง

        if (!formatc.equals(formatr)){
            SimpleDateFormat dfms = new SimpleDateFormat("mmss");//่ฎพ็ฝฎๆฅๆๆ?ผๅผ
            int datecs = Integer.valueOf(dfms.format(1552291200000L));
            //ๅฆๆๆดๆฐๆถ้ด็ๅ็งๆฏ้ๆพๆถ้ด็ๅ็งๆดๆฉ๏ผๅ้่ฆๅๅ1ไธชๅฐๆถ
            int daters = Integer.valueOf(dfms.format(1552463114000L));
            System.out.println("datecs:  "+datecs+"        daters: "+daters);

            System.out.println("T");
        }else{
            System.out.println("F");
        }

    }*/
}
