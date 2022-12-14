
package hry.manage.remote;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

import hry.account.fund.model.*;
import hry.account.fund.service.*;
import hry.account.remote.RemoteAppAccountService;
import hry.bean.JsonResult;
import hry.bus.BusApiCode;
import hry.bus.BusRequestUtil;
import hry.bus.model.BusCustomerTO;
import hry.bus.model.BusJsonResult;
import hry.bus.model.BusUpdateCustomerTO;
import hry.bus.service.BusInterface;
import hry.core.constant.CodeConstant;
import hry.core.constant.StringConstant;
import hry.core.shiro.PasswordHelper;
import hry.core.sms.JuheSendUtils;
import hry.customer.agents.model.AppAgentsCustromer;
import hry.customer.agents.service.AppAgentsCustromerService;
import hry.customer.agents.service.CustomerAsAgentsService;
import hry.customer.businessman.model.C2cTransaction;
import hry.customer.businessman.service.C2cCoinService;
import hry.customer.businessman.service.C2cTransactionService;
import hry.customer.commend.dao.AppCommendTradeDao;
import hry.customer.commend.model.AppCommendMoney;
import hry.customer.commend.model.AppCommendRank;
import hry.customer.commend.model.AppCommendTrade;
import hry.customer.commend.model.AppCommendUser;
import hry.customer.commend.service.AppCommendMoneyService;
import hry.customer.commend.service.AppCommendRankService;
import hry.customer.commend.service.AppCommendTradeService;
import hry.customer.commend.service.AppCommendUserService;
import hry.customer.person.dao.AppPersonInfoDao;
import hry.customer.person.model.AppPersonInfo;
import hry.customer.person.service.AppPersonInfoService;
import hry.customer.remote.RemoteAppAgentsService;
import hry.customer.user.dao.AppCustomerDao;
import hry.customer.user.model.AppCustomer;
import hry.customer.user.service.AppCustomerService;
import hry.customer.user.service.AppTeamLevelService;
import hry.exchange.account.model.ExDigitalmoneyAccount;
import hry.exchange.account.service.ExDigitalmoneyAccountService;
import hry.exchange.lend.model.ExDmLend;
import hry.exchange.lend.service.ExDmLendService;
import hry.exchange.lend.service.ExDmPingService;
import hry.exchange.lend.service.LendCoinAccountService;
import hry.exchange.product.model.ExCointoCoin;
import hry.exchange.product.model.ExProduct;
import hry.exchange.product.model.ProductCommon;
import hry.exchange.product.service.ExCointoCoinService;
import hry.exchange.product.service.ExProductService;
import hry.exchange.product.service.ProductCommonService;
import hry.exchange.purse.CoinInterfaceUtil;
import hry.exchange.remote.account.RemoteExProductService;
import hry.exchange.tradingArea.model.ExTradingArea;
import hry.exchange.tradingArea.service.ExTradingAreaService;
import hry.exchange.transaction.model.ExDmTransaction;
import hry.exchange.transaction.service.ExDmTransactionService;
import hry.front.redis.model.UserRedis;
import hry.ico.model.*;
import hry.ico.remote.RemoteIcoService;
import hry.ico.remote.model.IcoRulesConfig;
import hry.ico.remote.model.RulesConfig;
import hry.ico.service.*;
import hry.klg.level.service.KlgCustomerLevelService;
import hry.klg.level.service.KlgLevelConfigService;
import hry.klg.level.service.KlgTeamlevelService;
import hry.lend.constant.DealConstant;
import hry.lend.constant.ExchangeLendKey;
import hry.lend.model.pojo.ExLendAccount;
import hry.licqb.record.model.BoundRecord;
import hry.licqb.record.service.BoundRecordService;
import hry.manage.remote.model.*;
import hry.manage.remote.model.base.FrontPage;
import hry.manage.remote.model.c2c.C2cOrder;
import hry.manage.remote.model.commend.CommendMoney;
import hry.manage.remote.model.commend.CommendUser;
import hry.mq.producer.MessageAccountUtil;
import hry.mq.producer.service.MessageProducer;
import hry.redis.common.dao.RedisUtil;
import hry.redis.common.utils.RedisService;
import hry.trade.entrust.dao.ExEntrustDao;
import hry.trade.entrust.model.ExEntrust;
import hry.trade.entrust.model.ExOrderInfo;
import hry.trade.entrust.service.ExEntrustService;
import hry.trade.entrust.service.ExOrderInfoService;
import hry.trade.redis.model.*;
import hry.util.QueryFilter;
import hry.util.SpringUtil;
import hry.util.StringUtil;
import hry.util.UUIDUtil;
import hry.util.date.DateUtil;
import hry.util.idgenerate.IdGenerate;
import hry.util.idgenerate.NumConstant;
import hry.util.properties.PropertiesUtils;
import hry.util.serialize.Mapper;
import hry.util.serialize.ObjectUtil;
import hry.util.sys.ContextUtil;
import hry.web.dictionary.service.AppDicService;
import hry.web.log.model.LoginSafe;
import hry.web.log.service.LoginSafeService;
import hry.web.mail.service.MailService;
import hry.web.message.service.AppMessageService;
import hry.web.remote.RemoteAppConfigService;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import util.HryRealNameUtil;
import util.MathUtil;
import util.UserRedisUtils;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class RemoteManageServiceImpl implements RemoteManageService {
    private static Logger logger = Logger.getLogger(RemoteManageServiceImpl.class);
    private static String coinCode=null;
    @Resource
    private AppCustomerService appCustomerService;
    @Resource
    private MailService mailService;
    @Resource
    private AppPersonInfoService appPersonInfoService;
    @Resource
    private ExOrderInfoService exOrderInfoService;
    @Resource
    private ExEntrustService exEntrustService;
    @Resource
    private ExEntrustDao exEntrustDao;
    @Resource
    private AppCommendTradeDao appCommendTradeDao;
    @Resource(name = "customerAsAgentsService")
    private CustomerAsAgentsService customerAsAgentsService;
    @Resource
    private AppAccountService appAccountService;
    @Resource
    private AppPersonInfoDao appPersonInfoDao;
    @Resource
    private AppOurAccountService appOurAccountService;
    @Resource
    private ExProductService exProductService;
    @Resource
    private ExCointoCoinService exCointoCoinService;
    @Resource
    private ExDigitalmoneyAccountService exDigitalmoneyAccountService;
    @Resource
    private ProductCommonService productCommonService;
    @Resource
    private AppTransactionService appTransactionService;
    @Resource
    private AppCustomerDao appCustomerDao;
    @Resource
    private AppDicService appDicService;
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private AppAgentsCustromerService appAgentsCustromerService;
    @Resource
    private RedisService redisService;
    @Resource
    private C2cTransactionService c2cTransactionService;
    @Resource
    private ExDmPingService exDmPingService;
    @Resource
    private AppCommendTradeService appCommendTradeService;
    @Resource
    private AppCommendRankService appCommendRankService;
    @Resource
    private AppCommendUserService appCommendUserService;
    @Resource
    private ExDmTransactionService exDmTransactionService;
    @Resource
    private AppCommendMoneyService appCommendMoneyService;
    @Resource
    private LoginSafeService loginSafeService;
    @Resource
    private LendCoinAccountService lendCoinAccountService;
    @Resource
    private ExDmLendService exDmLendService;
    @Resource
    private RemoteAppConfigService remoteAppConfigService;
    @Resource
    private WhiteListService whiteListService;
    @Resource
    private AppMessageService appMessageService;
    @Resource
    private AppBankCardService appBankCardService;
    @Resource
    private ExTradingAreaService exTradingAreaService;
    @Resource
    private IcoUpgradeConfigService icoUpgradeConfigService;
    @Resource
    private C2cCoinService c2cCoinService;
    @Resource
    private RemoteIcoService remoteIcoService;

    @Resource
    private KlgCustomerLevelService klgCustomerLevelService;
    @Resource
    private KlgLevelConfigService klgLevelConfigService;
    @Resource
    private KlgTeamlevelService klgTeamlevelService;

    @Resource
    private AppTeamLevelService appTeamLevelService;

    @Resource
    private BoundRecordService boundRecordService;

    @Override
    public void cancelAllExEntrust(EntrustTrade entrustTrade) {
        exEntrustService.cancelAllExEntrust(entrustTrade);
    }

    @Override
    public RemoteResult createC2cOrder(C2cOrder c2cOrder) {

        //????????????1??????2??? ????????????
        Integer transactionType = c2cOrder.getTransactionType();
        JsonResult jr = null;
        String mes = "";
        if (transactionType.intValue() == 2) {
            //?????????????????????????????????????????????
            Long customerId = c2cOrder.getCustomerId();
            RemoteResult canTakeMoney = canTakeMoney(customerId.toString());
            if (canTakeMoney.getSuccess()) {
                jr = c2cTransactionService.createC2cOrder(c2cOrder);
            } else {
                mes = "??????????????????????????????????????????";
                // mes = canTakeMoney.getMsg();
            }
        } else {
            jr = c2cTransactionService.createC2cOrder(c2cOrder);
        }
        if (jr != null) {
            if (jr.getSuccess()) {
                return new RemoteResult().setSuccess(true).setObj(jr.getObj());
            } else {
                return new RemoteResult().setMsg(jr.getMsg());
            }
        }
        return new RemoteResult().setMsg(mes);
    }


    @Override
    public Integer getKeepDecimalForCoin(String CoinCode) {
        ProductCommon productCommon = productCommonService.getProductCommon(CoinCode);
        return productCommon.getKeepDecimalForCoin();
    }

    @Override
    public String getC2cOrderDetail(String transactionNum) {
        return c2cTransactionService.getC2cOrderDetail(transactionNum);
    }

    @Override
    public FrontPage findAllEntrust(Map<String, String> params) {

        return exEntrustService.findPage(params);
    }

    @Override
    public RemoteResult appgetAccountInfo(String coinCode, String fixCode) {
        JSONObject result = new JSONObject();
        List<ExCointoCoin> find = exCointoCoinService.find(new QueryFilter(ExCointoCoin.class).addFilter("state=", 1).addFilter("coinCode=", coinCode).addFilter("fixPriceCoinCode=", fixCode));
        if (find.size() > 0) {
            ExCointoCoin e = find.get(0);
            result.put("buyFeeRate", e.getBuyFeeRate());
            result.put("sellFeeRate", e.getSellFeeRate());
            result.put("keepDecimalForNumber", e.getKeepDecimalCoinCode());
            result.put("keepDecimalForPrice", e.getKeepDecimalFixPrice());
        }

        ArrayList<Coin> list = new ArrayList<Coin>();
        for (ExCointoCoin ex : find) {
            Coin coin = new Coin();
            coin.setCoinCode(ex.getCoinCode());
            coin.setFixPriceCoinCode(ex.getFixPriceCoinCode());
            list.add(coin);
        }

        return new RemoteResult().setSuccess(true).setObj(result);
    }

    @Override
    public List<C2cOrder> c2c10Order(Long customerId, String coinCode) {

        QueryFilter filter = new QueryFilter(C2cTransaction.class);
        filter.addFilter("coinCode=", coinCode);
        filter.addFilter("customerId=", customerId);
        filter.setOrderby("id desc");
        filter.setPage(1);
        filter.setPageSize(10);
        Page<C2cTransaction> findPage = c2cTransactionService.findPage(filter);
        List<C2cTransaction> list = findPage.getResult();

        ArrayList<C2cOrder> arrayList = new ArrayList<C2cOrder>();
        if (list != null && list.size() > 0) {
            for (C2cTransaction c2cTransaction : list) {
                C2cOrder c2cOrder = new C2cOrder();
                c2cOrder.setTransactionTime(c2cTransaction.getCreated());
                c2cOrder.setCreateTime(new SimpleDateFormat("YYYY:MM:dd HH:mm:ss").format(c2cTransaction.getCreated()));
                c2cOrder.setCoinCode(c2cTransaction.getCoinCode());
                c2cOrder.setTransactionNum(c2cTransaction.getTransactionNum());
                c2cOrder.setTransactionPrice(c2cTransaction.getTransactionPrice());
                c2cOrder.setTransactionCount(c2cTransaction.getTransactionCount());
                c2cOrder.setTransactionMoney(c2cTransaction.getTransactionMoney());
                c2cOrder.setTransactionType(c2cTransaction.getTransactionType());
                c2cOrder.setStatus(c2cTransaction.getStatus());
                c2cOrder.setStatus2(c2cTransaction.getStatus2());
                arrayList.add(c2cOrder);
            }
        }

        return arrayList;

    }

    @Override
    public BigDecimal getC2cCoinPrice(String coinNmae, String priceType) {
        return c2cCoinService.getCoinPrice(coinNmae,priceType);
    }

    @Override
    public RemoteResult createPublicKey(Long exdmaccountId) {

        RemoteResult remoteResult = new RemoteResult();
        remoteResult.setMsg("createerror");

        ExDigitalmoneyAccount exDigitalmoneyAccount = exDigitalmoneyAccountService.get(exdmaccountId);
        if (!StringUtils.isEmpty(exDigitalmoneyAccount.getPublicKey())) {
            return remoteResult.setMsg("qianbaodizhicunzai");// ?????????????????????!
        }
        try {
            String ss = CoinInterfaceUtil.create(exDigitalmoneyAccount.getUserName(), exDigitalmoneyAccount.getAccountNum().toLowerCase(), exDigitalmoneyAccount.getCoinCode());
            JSONObject parseObject = null;
            if (!StringUtils.isEmpty(ss)) {
                parseObject = JSONObject.parseObject(ss);
            }
            String address = "";
            if (parseObject != null) {
                address = parseObject.get("address").toString();
            }

            if ("true".equals(PropertiesUtils.APP.getProperty("app.blockbus"))) {
                address = "blockbus" + UUIDUtil.getUUID().substring(0,7);
            }

            if (!StringUtils.isEmpty(address)) {

                //-------?????????????????????-----------??????
                String busCustomerNumber = "";
                if ("true".equals(PropertiesUtils.APP.getProperty("app.blockbus"))) {

                    AppCustomer appCustomer = appCustomerService.get(exDigitalmoneyAccount.getCustomerId());

                    BusRequestUtil.putCoinAddress(appCustomer.getBusNumber(), exDigitalmoneyAccount.getCoinCode(), address);
                }
                //-------?????????????????????-----------??????

                remoteResult.setSuccess(true);
                remoteResult.setObj(address);
                remoteResult.setMsg("success");
                exDigitalmoneyAccount.setPublicKey(address);
                exDigitalmoneyAccountService.update(exDigitalmoneyAccount);
                logger.error("???????????????" + exDigitalmoneyAccount.getUserName() + ",????????????" + address);


            } else {
                remoteResult.setMsg("createerror");// ????????????
                logger.error("??????" + exDigitalmoneyAccount.getCoinCode() + "????????????");
            }

        } catch (Exception e) {
            logger.error("??????????????????????????????");
            e.printStackTrace();
        }

        return remoteResult;
    }

    @Override
    public RemoteResult getPublicKey(Long exdmaccountId) {

        RemoteResult remoteResult = new RemoteResult();

        ExDigitalmoneyAccount exDigitalmoneyAccount = exDigitalmoneyAccountService.get(exdmaccountId);
        if (exDigitalmoneyAccount != null) {
            return remoteResult.setSuccess(true).setObj(exDigitalmoneyAccount.getPublicKey());
        }

        return remoteResult;
    }

    @Override
    public RemoteResult getPublicKey(Long exdmaccountId, String cusId) {

        RemoteResult remoteResult = new RemoteResult();

        ExDigitalmoneyAccount exDigitalmoneyAccount = exDigitalmoneyAccountService.get(exdmaccountId);
        if (!exDigitalmoneyAccount.getCustomerId().toString().equals(cusId)) {
            return remoteResult.setSuccess(false).setMsg("???????????????????????????");
        }
        if (exDigitalmoneyAccount != null) {
            return remoteResult.setSuccess(true).setObj(exDigitalmoneyAccount.getPublicKey());
        }

        return remoteResult;
    }


    @Override
    public RemoteResult login(String username, String password, String uuid) {

        QueryFilter f = new QueryFilter(AppCustomer.class);
        username = username.toLowerCase();
        f.addFilter("userName=", username);
        // ?????????????????????
        AppCustomer appCustomer = appCustomerService.get(f);
        // ????????????????????????
        if (appCustomer != null) {
            PasswordHelper passwordHelper = new PasswordHelper();
            String encryString = passwordHelper.encryString(password, appCustomer.getSalt());
            // ????????????
            if (!appCustomer.getPassWord().equals(encryString)) {
                return new RemoteResult().setSuccess(false).setMsg("mimacuowu");
            }
            // ?????????????????????
            if (appCustomer.getIsDelete().equals(Integer.valueOf(1))) {
                return new RemoteResult().setSuccess(false).setMsg("user_forbidden");
            }
            // ????????????????????????
            if (appCustomer.getHasEmail() == null || appCustomer.getHasEmail().equals(Integer.valueOf(0))) {
                return new RemoteResult().setSuccess(false).setMsg("zhanghaoweijihuo");
            }
            // ??????appPersonInfo
            QueryFilter filter = new QueryFilter(AppPersonInfo.class);
            filter.addFilter("customerId=", appCustomer.getId());
            AppPersonInfo apppersonInfo = appPersonInfoService.get(filter);

            User user = new User();
            user.setUsername(appCustomer.getUserName());
            user.setUserCode(appCustomer.getUserCode());
            user.setAccountPassWord(appCustomer.getAccountPassWord());
            user.setIsReal(appCustomer.getIsReal() == null ? 0 : appCustomer.getIsReal().intValue());
            user.setIsDelete(appCustomer.getIsDelete());
            user.setIsChange(appCustomer.getIsChange());
            user.setCustomerId(appCustomer.getId());
            // ??????
            user.setCountry(apppersonInfo.getCountry());
            // ?????????
            user.setMobile(apppersonInfo.getMobilePhone());

            user.setSurname(apppersonInfo.getSurname());
            user.setTruename(apppersonInfo.getTrueName());
            user.setIsLock(appCustomer.getIsLock());
            user.setCustomerType(apppersonInfo.getCustomerType());
            user.setSalt(appCustomer.getSalt());
            user.setGoogleKey(appCustomer.getGoogleKey());
            user.setGoogleState(appCustomer.getGoogleState());
            user.setMessIp(appCustomer.getMessIp());
            user.setPassDate(appCustomer.getPassDate());
            user.setPhoneState(appCustomer.getPhoneState());
            user.setStates(appCustomer.getStates());
            user.setPassword(appCustomer.getPassWord());
            user.setUuid(uuid);

            // ????????????????????????
            String commend = getCnfigValue("commend_switch");
            if (commend != null && !"".equals(commend)) {
                user.setCommend(Integer.valueOf(commend));
            }

            appCustomerService.update(appCustomer);

            String cardCurrency = getCnfigValue("card_Currency");
            // ????????????????????????
            String uncardCurrency = getCnfigValue("uncard_Currency");
            if (cardCurrency != null && uncardCurrency != null) {
                user.setCardCurrency(cardCurrency);
                user.setUncardCurrency(uncardCurrency);
            }
            return new RemoteResult().setSuccess(true).setObj(user).setMsg("login_success");
        }
        return new RemoteResult().setSuccess(false).setMsg("login_nameorpwd_erro");

    }

    @Override
    public RemoteResult login(String username, String password, String country, String uuid) {


        AppCustomer appCustomer = null;

        //????????????,???????????????
        String loginType = "email";
        if (username.contains("@")) {// ??????????????????????????????
            QueryFilter f = new QueryFilter(AppPersonInfo.class);
            f.addFilter("email=", username);
            AppPersonInfo appPersonInfo = appPersonInfoService.get(f);
            if (appPersonInfo != null) {
                appCustomer = appCustomerService.get(appPersonInfo.getCustomerId());
            }
        } else {// ??????????????????????????????
            //????????????
            loginType = "mobile";
            QueryFilter f = new QueryFilter(AppPersonInfo.class);
            f.addFilter("mobilePhone=", username);
            f.addFilter("country=", country);
            AppPersonInfo appPersonInfo = appPersonInfoService.get(f);
            if (appPersonInfo != null) {
                appCustomer = appCustomerService.get(appPersonInfo.getCustomerId());
            }

        }

        System.out.println("appCustomer ????????????"+appCustomer);
        // ????????????????????????
        if (appCustomer != null) {
            PasswordHelper passwordHelper = new PasswordHelper();
            String encryString = passwordHelper.encryString(password, appCustomer.getSalt());
            // ????????????
            if (!appCustomer.getPassWord().equals(encryString)) {
                return new RemoteResult().setSuccess(false).setMsg("login_nameorpwd_erro");
            }
            // ?????????????????????
            if (appCustomer.getIsDelete().equals(Integer.valueOf(1))) {
                return new RemoteResult().setSuccess(false).setMsg("user_forbidden").setCode("1001");
            }
            // ??????appPersonInfo
            QueryFilter filter = new QueryFilter(AppPersonInfo.class);
            filter.addFilter("customerId=", appCustomer.getId());
            AppPersonInfo apppersonInfo = appPersonInfoService.get(filter);


            //???????????????????????????????????????
            if ("email".equals(loginType)) {
                // ????????????????????????
                if (appCustomer.getHasEmail() == null || appCustomer.getHasEmail().equals(Integer.valueOf(0))) {
                    return new RemoteResult().setSuccess(false).setMsg("zhanghaoweijihuo").setObj(apppersonInfo);
                }
            }


            User user = new User();
            //??????????????????????????????,????????????????????????????????????(?????????????????????)
            user.setNickName(username);

            user.setUsername(appCustomer.getUserName());
            user.setUserCode(appCustomer.getUserCode());
            user.setAccountPassWord(appCustomer.getAccountPassWord());
            user.setIsReal(appCustomer.getIsReal() == null ? 0 : appCustomer.getIsReal().intValue());
            user.setIsDelete(appCustomer.getIsDelete());
            user.setIsChange(appCustomer.getIsChange());
            user.setCustomerId(appCustomer.getId());
            // ??????
            user.setCountry(apppersonInfo.getCountry());
            // ?????????
            user.setMobile(apppersonInfo.getMobilePhone());
            // ??????????????????????????????????????????
            user.setIsOpenCoinFee(apppersonInfo.getIsOpenCoinFee());
            user.setSurname(apppersonInfo.getSurname());
            user.setTruename(apppersonInfo.getTrueName());
            user.setIsLock(appCustomer.getIsLock());
            user.setCustomerType(apppersonInfo.getCustomerType());
            user.setSalt(appCustomer.getSalt());
            user.setGoogleKey(appCustomer.getGoogleKey());
            user.setGoogleState(appCustomer.getGoogleState());
            user.setMessIp(appCustomer.getMessIp());
            user.setPassDate(appCustomer.getPassDate());
            user.setPhoneState(appCustomer.getPhoneState());
            user.setStates(appCustomer.getStates());
            user.setPassword(appCustomer.getPassWord());
            user.setUuid(uuid);
            //????????????
            user.setEmail(apppersonInfo.getEmail());
            // ??????????????????
            user.setHasEmail(appCustomer.getHasEmail());

            user.setSafeLoginType(appCustomer.getSafeLoginType());
            user.setSafeTixianType(appCustomer.getSafeTixianType());
            user.setSafeTradeType(appCustomer.getSafeTradeType());

            user.setIsBlacklist(appCustomer.getIsBlacklist());
            // ????????????????????????
            String commend = getCnfigValue("commend_switch");
            if (commend != null && !"".equals(commend)) {
                user.setCommend(Integer.valueOf(commend));
            }
            appCustomer.setUuid(uuid);
            appCustomerService.update(appCustomer);

            String cardCurrency = getCnfigValue("card_Currency");
            // ????????????????????????
            String uncardCurrency = getCnfigValue("uncard_Currency");
            if (cardCurrency != null && uncardCurrency != null) {
                user.setCardCurrency(cardCurrency);
                user.setUncardCurrency(uncardCurrency);
            }
            // otc??????
            user.setNickNameOtc(appCustomer.getNickNameOtc());
            return new RemoteResult().setSuccess(true).setObj(user).setMsg("login_success");
        } else {//??????????????????
            System.out.println("??????????????????");
            if ("true".equals(PropertiesUtils.APP.getProperty("app.blockbus"))) {
                //-------?????????????????????-----------??????
                System.out.println("-------?????????????????????-----------??????");
                BusJsonResult<BusCustomerTO> busResult = null;
                if ("email".equals(loginType)) {
                    busResult = BusRequestUtil.login4Email(username, password);
                } else {
                    busResult = BusRequestUtil.login4Mobile(country, username, password);
                }
                System.out.println("busResult==============="+busResult);
                if (null == busResult) {
                    //????????????
                    return new RemoteResult().setMsg("login_nameorpwd_erro");
                }
                if (!busResult.getSuccess()) {
                    return new RemoteResult().setMsg("login_nameorpwd_erro");
                }
                //????????????id
                BusCustomerTO busCustomerTO = busResult.getObj();
                System.out.println("BusCustomerTO==="+busCustomerTO);
                if (busCustomerTO == null) {
                    return new RemoteResult().setMsg("login_nameorpwd_erro");
                } else {

                    BusInterface busInterface = SpringUtil.getBean("busInterface");
                    User user = busInterface.login(username, busCustomerTO);
                    return new RemoteResult().setSuccess(true).setObj(user).setMsg("login_success");
                }
                //--------?????????????????????-----------??????
            }
        }
        return new RemoteResult().setSuccess(false).setMsg("login_nameorpwd_erro");

    }

    @Override
    public RemoteResult logout(User user) {
        QueryFilter f = new QueryFilter(AppCustomer.class);
        f.addFilter("userName=", user.getUsername());
        AppCustomer appCustomer = appCustomerService.get(f);
        if (appCustomer != null) {
            appCustomerService.update(appCustomer);
            return new RemoteResult().setSuccess(true);
        }
        return new RemoteResult().setSuccess(false);
    }

    @Override
    public BigDecimal getOldMoney(Long customerId, String created) {
        BigDecimal oldMoney = BigDecimal.valueOf(0);
        QueryFilter f = new QueryFilter(AppTransaction.class);
        f.addFilter("customerId=", customerId);
        // f.addFilter("status=", 1);
        f.addFilter("status=", 2);
        f.addFilter("created>", created + " 00:00:00");
        List<AppTransaction> appTransaction = appTransactionService.find(f);

        QueryFilter f1 = new QueryFilter(AppTransaction.class);
        f1.addFilter("customerId=", customerId);
        f1.addFilter("status=", 1);
        // f1.addFilter("status=", 2);
        f1.addFilter("created>", created + " 00:00:00");
        List<AppTransaction> app = appTransactionService.find(f1);
        if (appTransaction != null) {
            for (AppTransaction a : appTransaction) {
                oldMoney = oldMoney.add(a.getTransactionMoney());
            }

        }
        if (app != null) {
            for (AppTransaction a : app) {
                oldMoney = oldMoney.add(a.getTransactionMoney());
            }

        }
        return oldMoney;
    }

    public static String getCnfigValue(String type) {
        RemoteAppConfigService remoteAppConfigService = (RemoteAppConfigService) ContextUtil.getBean("remoteAppConfigService");
        return remoteAppConfigService.getValueByKey(type);
    }

    @Override
    public RemoteResult regist(String email, String password, String referralCode, String language) {

        try {
            Boolean lock = redisService.lock(email + "regist");
            if (lock) {
                // ??????????????????
                email = email.toLowerCase();
                // ?????????????????????????????????
                QueryFilter filter = new QueryFilter(AppPersonInfo.class);
                filter.addFilter("email=", email);
                AppPersonInfo _appPersonInfo = appPersonInfoService.get(filter);

                if (_appPersonInfo != null) {
                    return new RemoteResult().setMsg("user_reg");
                }

                //????????????
                String salt = UUIDUtil.getUUID();
                //????????????
                PasswordHelper passwordHelper = new PasswordHelper();
                String encryPassword = passwordHelper.encryString(password, salt);

                //-------?????????????????????-----------??????
                String busCustomerNumber = "";
                if ("true".equals(PropertiesUtils.APP.getProperty("app.blockbus"))) {
                    BusJsonResult busResult = BusRequestUtil.regist4Email(email, encryPassword, salt);
                    if (null == busResult) {
                        //????????????
                        return new RemoteResult().setMsg("error");
                    }
                    if (!busResult.getSuccess()) {
                        //????????????
                        if (BusApiCode.code_1001.equals(busResult.getCode())) {
                            return new RemoteResult().setMsg("user_reg");
                        }
                        return new RemoteResult().setMsg(busResult.getMsg());
                    }
                    //????????????id
                    busCustomerNumber = (String) busResult.getObj();
                }
                //--------?????????????????????-----------??????

                AppCustomer customer = new AppCustomer();
                //??????????????????
                customer.setBusNumber(busCustomerNumber);

                String uuid = UUIDUtil.getUUID();
                // ?????????????????????ID
                customer.setUserName(uuid);
                // ??????????????????ID
                customer.setUserCode(uuid);
                // ????????????
                customer.setPassWord(password);
                // ??????????????????????????????0
                customer.setGoogleState(0);
                // ??????????????????????????????0
                customer.setPhoneState(0);
                // ??????????????????
                customer.setStates(0);
                // ????????????
                customer.setIsReal(0);
                //????????????????????????
                customer.setHasEmail(1);
                // ???????????????
                customer.setReferralCode(MathUtil.generateShortUuid());
                // ???????????????
                if (referralCode != null) {

                    if (referralCode.startsWith("AGENT") && referralCode.length() > 10) { //?????????????????????
                        customer.setAgentCommendCode(referralCode);
                    } else {
                        customer.setCommendCode(referralCode);
                    }

                }
                // ????????????
                customer.setSalt(salt);
                // ????????????
                customer.setPassWord(encryPassword);
                // ??????appCustomer
                customer.setCommonLanguage(language);
                appCustomerService.save(customer);


                if ("true".equals(PropertiesUtils.APP.getProperty("app.blockbus"))) {
                    BusUpdateCustomerTO busUpdateCustomerTO = new BusUpdateCustomerTO();
                    busUpdateCustomerTO.setBusCustomerNumber(busCustomerNumber);
                    busUpdateCustomerTO.setCustomerLocalNumber(customer.getId().toString());
                    busUpdateCustomerTO.setReferralCode(customer.getReferralCode());//??????????????????
                    busUpdateCustomerTO.setInviteCode(referralCode);//?????????????????????
                    //????????????????????????
                    BusRequestUtil.updateCustomer(busUpdateCustomerTO);
                }

                // ???????????????AppPersonInfo
                AppPersonInfo appPersonInfo = new AppPersonInfo();
                // ??????????????????
                appPersonInfo.setCountry("+86");
                // ??????????????????
                appPersonInfo.setCustomerSource(0);
                // ??????customerId
                appPersonInfo.setCustomerId(customer.getId());
                // ??????????????????
                appPersonInfo.setCustomerType(1);
                // ?????????????????????
                appPersonInfo.setEmailCode(UUIDUtil.getUUID());
                // ????????????
                appPersonInfo.setEmail(email);
                appPersonInfoService.save(appPersonInfo);

                // ?????????????????????
                RemoteAppAccountService remoteAppAccountService = (RemoteAppAccountService) ContextUtil.getBean("remoteAppAccountService");
                remoteAppAccountService.openAccount(customer, appPersonInfo, language, ContextUtil.getWebsite());

                // ?????????????????????
                RemoteExProductService remoteExProductService = (RemoteExProductService) ContextUtil.getBean("remoteExProductService");
                remoteExProductService.openDmAccount(customer, appPersonInfo, null, ContextUtil.getWebsite(), language);
                // ?????????
                appCommendUserService.saveObj(customer);
                //?????????????????????
                appTeamLevelService.saveTeamLevel(customer);

                // ????????????

                //??????????????????

                appMessageService.sysSendMsg(customer, MsgTypeEnum.REGIST);

                redisService.unLock(email + "regist");

                return new RemoteResult().setSuccess(true).setObj(appPersonInfo.getEmailCode());


            } else {
                return new RemoteResult().setSuccess(false).setMsg("qingqiupinf");//????????????
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RemoteResult().setSuccess(false).setMsg("error");

    }
    /**
     *
     *????????????????????? ??????????????????
     */
    private  void addUserLevel(AppCustomer customer){
        klgCustomerLevelService.addUserLevel(customer);
        klgTeamlevelService.addUser(customer);//?????????????????????
    }


    /**
     * ?????? 2017-10-31 09:57:43 -- denghf
     */
    private void giveCoin(Long id, String language) {
        // ?????????????????????????????????
        ExProductService exProductService = (ExProductService) ContextUtil.getBean("exProductService");
        ExDigitalmoneyAccountService exDigitalmoneyAccountService = (ExDigitalmoneyAccountService) ContextUtil.getBean("exDigitalmoneyAccountService");
        QueryFilter filter = new QueryFilter(ExProduct.class);
        filter.addFilter("issueState=", Integer.valueOf(1));
        filter.setOrderby("isRecommend DESC");
        List<ExProduct> list = exProductService.find(filter);

        for (ExProduct exProduct : list) {
            QueryFilter filterAppAccount = new QueryFilter(AppAccount.class);
            filterAppAccount.addFilter("customerId=", id);
            filterAppAccount.addFilter("coinCode=", exProduct.getCoinCode());
            ExDigitalmoneyAccount _digitalmoneyAccount = exDigitalmoneyAccountService.get(filterAppAccount);
            if (_digitalmoneyAccount != null) {
                if (exProduct.getGiveCoin() != null && exProduct.getGiveCoin().compareTo(new BigDecimal(0)) > 0) {
                    if (_digitalmoneyAccount.getHotMoney() != null) {
                        _digitalmoneyAccount.setHotMoney(_digitalmoneyAccount.getHotMoney().add(exProduct.getGiveCoin()));// ????????????
                    } else {
                        _digitalmoneyAccount.setHotMoney(exProduct.getGiveCoin());
                    }
                    exDigitalmoneyAccountService.update(_digitalmoneyAccount);

                    // ????????????
                    exDigitalmoneyAccountService.saveRecord(_digitalmoneyAccount, 1);
                }
            }
        }
    }

    /**
     * ????????????
     *
     * @param id ?????????
     */
    private void commendCoin(Long id) {
        // ?????????????????????????????????
        ExProductService exProductService = (ExProductService) ContextUtil.getBean("exProductService");
        ExDigitalmoneyAccountService exDigitalmoneyAccountService = (ExDigitalmoneyAccountService) ContextUtil.getBean("exDigitalmoneyAccountService");
        QueryFilter filter = new QueryFilter(ExProduct.class);
        filter.addFilter("issueState=", Integer.valueOf(1));
        filter.setOrderby("isRecommend DESC");
        List<ExProduct> list = exProductService.find(filter);

        for (ExProduct exProduct : list) {
            QueryFilter filterAppAccount = new QueryFilter(AppAccount.class);
            filterAppAccount.addFilter("customerId=", id);
            filterAppAccount.addFilter("coinCode=", exProduct.getCoinCode());
            ExDigitalmoneyAccount _digitalmoneyAccount = exDigitalmoneyAccountService.get(filterAppAccount);
            if (_digitalmoneyAccount != null) {
                if (exProduct.getCommendCoin() != null && exProduct.getCommendCoin().compareTo(new BigDecimal(0)) > 0) {

                    // ????????????
                    ExDmTransaction exDmTransaction = new ExDmTransaction();
                    exDmTransaction.setAccountId(_digitalmoneyAccount.getId());
                    exDmTransaction.setCoinCode(_digitalmoneyAccount.getCoinCode());
                    exDmTransaction.setCreated(new Date());
                    exDmTransaction.setCustomerId(_digitalmoneyAccount.getCustomerId());
                    exDmTransaction.setCustomerName(_digitalmoneyAccount.getUserName());
                    exDmTransaction.setTime(DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    exDmTransaction.setTimereceived(DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    exDmTransaction.setBlocktime(DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    exDmTransaction.setFee(new BigDecimal(0));
                    exDmTransaction.setTransactionMoney(exProduct.getCommendCoin());
                    exDmTransaction.setStatus(2);
                    exDmTransaction.setTransactionNum(IdGenerate.transactionNum(NumConstant.Ex_Dm_Transaction));
                    // ??????
                    exDmTransaction.setTransactionType(1);
                    exDmTransaction.setUserId(_digitalmoneyAccount.getCustomerId());
                    exDmTransaction.setWebsite(_digitalmoneyAccount.getWebsite());
                    exDmTransaction.setCurrencyType(_digitalmoneyAccount.getCurrencyType());
                    exDmTransaction.setRemark("?????????" + exProduct.getCommendCoin() + "??????!");
                    exDmTransactionService.save(exDmTransaction);
                    exDmTransaction.setOptType(8);
                    // ????????????
                    MessageAccountUtil.addCoin(_digitalmoneyAccount.getId(), exProduct.getCommendCoin(), exDmTransaction.getTransactionNum());

                }
            }
        }
    }

    @Override
    public FrontPage findTrades(Map<String, String> params) {
        return exOrderInfoService.findFrontPage(params);
    }

    @Override
    public FrontPage findEntrust(Map<String, String> params) {

        return exEntrustService.findFrontPage(params);
    }


    private String getIsOpen(String key) {
        //
        String value = "1";
        if (null != key && !"".equals(key)) {
            String data = "";
            data = redisService.get(StringConstant.CONFIG_CACHE + ":all");
            if (null != data && !"".equals(data)) {
                JSONObject obj = JSON.parseObject(data);
                if (obj.containsKey(key)) {
                    value = obj.get(key).toString();
                }


            }
        }
        return value;
    }

    @Override
    public RemoteResult realname(String userName, String trueName, String surName, String country, String cardType, String cardId) {

        AppCustomer appCustomer = appCustomerService.get(new QueryFilter(AppCustomer.class).addFilter("userName=", userName));

        if (appCustomer != null && appCustomer.getIsReal() == 0) {
            AppPersonInfo appPersonInfo = appPersonInfoService.get(new QueryFilter(AppPersonInfo.class).addFilter("customerId=", appCustomer.getId()));
            // ???X?????????X
            appPersonInfo.setCardId(cardId.replace("x", "X"));
            appPersonInfo.setTrueName(trueName);
            appPersonInfo.setSurname(surName);
            appPersonInfo.setCountry(country);
            appPersonInfo.setCardType(Integer.valueOf(cardType));

            // ???????????????????????????
            AppPersonInfo appPersonInfo2 = appPersonInfoDao.getAppPersonInfoByCardId(appPersonInfo.getCardId());
            if (appPersonInfo2 != null) {
                return new RemoteResult().setMsg("card_id_chongfu");
            }

            try {
                // ??????????????????????????????
                String checkIdentityIsOpen = getIsOpen("checkIdentityInterface");

                if ("0".equals(checkIdentityIsOpen)) {
                    RemoteAppConfigService remoteAppConfigService = (RemoteAppConfigService) ContextUtil.getBean("remoteAppConfigService");
                    String juhe_cardOpen = remoteAppConfigService.getValueByKey("juhe_cardOpen");
                    String juhe_cardKey = remoteAppConfigService.getValueByKey("juhe_cardKey");
                    String juhe_cardUrl = remoteAppConfigService.getValueByKey("juhe_cardUrl");
                    // ????????????????????????,????????????????????? ?????????0????????????
                    if ("0".equals(juhe_cardOpen)) {
                        boolean checkCard = JuheSendUtils.auth(appPersonInfo.getTrueName(), appPersonInfo.getCardId(), juhe_cardUrl, juhe_cardKey);
                        if (!checkCard) {
                            return new RemoteResult().setMsg("failrealname");
                        }
                    }
                }

                // ?????????????????????
                // RemoteAppAccountService remoteAppAccountService =
                // (RemoteAppAccountService)
                // ContextUtil.getBean("remoteAppAccountService");
                // remoteAppAccountService.openAccount(appCustomer,appPersonInfo,ContextUtil.getCurrencyType(),ContextUtil.getWebsite());

                // ?????????????????????
                /*
                 * RemoteExProductService remoteExProductService = (RemoteExProductService)
                 * ContextUtil.getBean("remoteExProductService");
                 * remoteExProductService.openDmAccount(appCustomer,
                 * appPersonInfo,null,ContextUtil.getWebsite(),ContextUtil. getCurrencyType());
                 */

                // ???????????????
                AppAgentsCustromer appAgentsCustromer = new AppAgentsCustromer();
                appAgentsCustromer.setAddress("??????");
                appAgentsCustromer.setAgentName(appPersonInfo.getTrueName());
                appAgentsCustromer.setSurname(appPersonInfo.getSurname());
                appAgentsCustromer.setCustomerName(appPersonInfo.getMobilePhone());
                appAgentsCustromer.setCustomerId(appPersonInfo.getCustomerId());
                appAgentsCustromer.setPapersType("0");
                appAgentsCustromer.setPapersNo(appPersonInfo.getCardId());
                appAgentsCustromer.setRecommendCode(appPersonInfo.getMobilePhone());
                appAgentsCustromer.setStates(2);
                RemoteAppAgentsService remoteAppAgentsService = (RemoteAppAgentsService) ContextUtil.getBean("remoteAppAgentsService");
                JsonResult result = remoteAppAgentsService.saveAgents(appAgentsCustromer);

                appCustomer.setIsReal(1);
                appCustomerService.update(appCustomer);
                appPersonInfoService.update(appPersonInfo);

                return new RemoteResult().setSuccess(true);
            } catch (Exception e) {
                return new RemoteResult().setMsg("?????????????????????");
            }
        }
        return new RemoteResult();

    }

    @Override
    public RemoteResult getPersonInfo(String userCode) {

        AppCustomer appCustomer = appCustomerService.get(new QueryFilter(AppCustomer.class).addFilter("userCode=", userCode));
        if (appCustomer != null) {
            AppPersonInfo appPersonInfo = appPersonInfoService.get(new QueryFilter(AppPersonInfo.class).addFilter("customerId=", appCustomer.getId()));
            UserInfo info = new UserInfo();
            info.setTrueName(appPersonInfo.getTrueName());
            info.setSurname(appPersonInfo.getSurname());
            info.setCountry(appPersonInfo.getCountry());
            info.setCardType(appPersonInfo.getCardType().toString());
            info.setCardId(appPersonInfo.getCardId());
            String papersType = appPersonInfo.getPapersType();

            if ("1".equals(papersType)) {
                info.setPapersType("?????????");
            } else if ("2".equals(papersType)) {
                info.setPapersType("??????");

            }
            info.setType(papersType);
            return new RemoteResult().setSuccess(true).setObj(info);
        }
        return new RemoteResult();
    }

    @Override
    public RemoteResult setpw(String username, String oldPassWord, String newPassWord) {

        AppCustomer appCustomer = appCustomerService.get(new QueryFilter(AppCustomer.class).addFilter("username=", username));
        // ???????????????
        String passWord = appCustomer.getPassWord();

        PasswordHelper passwordHelper = new PasswordHelper();

        String encryString = passwordHelper.encryString(oldPassWord, appCustomer.getSalt());
        if (!passWord.equals(encryString)) {
            return new RemoteResult().setMsg("yuanshimimabuzhengq");
        }

        appCustomer.setPassDate(new Date());
        String encryString2 = passwordHelper.encryString(newPassWord, appCustomer.getSalt());
        // ?????????????????????
        appCustomer.setPassWord(passwordHelper.encryString(newPassWord, appCustomer.getSalt()));
        appCustomerService.update(appCustomer);

        // ???????????????
        appMessageService.sysSendMsg(appCustomer, MsgTypeEnum.MODIFYPWD);

        return new RemoteResult().setSuccess(true).setObj(encryString2);

    }

    @Override
    public RemoteResult setapw(Long customerId, String accountPassWord) {

        AppCustomer appCustomer = appCustomerService.get(customerId);

        PasswordHelper passwordHelper = new PasswordHelper();

        String apw = passwordHelper.encryString(accountPassWord, appCustomer.getSalt());
        if (apw.equals(appCustomer.getPassWord())) {
            return new RemoteResult().setMsg("jiaoyimimabunengyudenglumimaxiangtong");//???????????????????????????????????????
        }

        // ?????????????????????
        appCustomer.setAccountPassWord(apw);
        appCustomerService.update(appCustomer);


        return new RemoteResult().setSuccess(true).setObj(appCustomer.getAccountPassWord());

    }

    @Override
    public RemoteResult resetapw(Long customerId, String passWord, String accountPassWord) {

        AppCustomer appCustomer = appCustomerService.get(customerId);

        PasswordHelper passwordHelper = new PasswordHelper();

        String apw = passwordHelper.encryString(accountPassWord, appCustomer.getSalt());
        if (apw.equals(appCustomer.getPassWord())) {
            return new RemoteResult().setMsg("jiaoyimimabunengyudenglumimaxiangtong");
        }

        // ?????????????????????
        appCustomer.setAccountPassWord(apw);
        appCustomerService.update(appCustomer);

        return new RemoteResult().setSuccess(true).setObj(appCustomer.getAccountPassWord());

    }

    @Override
    public RemoteResult getAccountInfo(String coinCode, Long customerId) {

        String[] split = coinCode.split("_");
        // ??????????????????
        String code = split[0];
        // ??????????????????
        String priceCode = split[1];

        // ????????????
        TradeAccount tradeAccount = new TradeAccount();
        RedisService redisService = (RedisService) ContextUtil.getBean("redisService");

        // ??????????????????redis??????
        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(customerId.toString());
        if (userRedis == null || StringUtils.isEmpty(userRedis.getDmAccountId(code))) {
            return new RemoteResult();
        }
        ExDigitalmoneyAccountRedis digitAccount = UserRedisUtils.getAccount(userRedis.getDmAccountId(code).toString(), code);

        // product??????redis????????????
        ExProduct product = null;
        String productJson = redisService.getMap("HRY:EXCHANGE:PRODUCT", code);
        if (null != productJson) {
            product = JSON.parseObject(productJson, ExProduct.class);
        } else {
            RemoteExProductService rmoteExProductService = (RemoteExProductService) ContextUtil.getBean("remoteExProductService");
            product = rmoteExProductService.findByCoinCode(code, "");
        }

        ProductCommonService productCommonService = (ProductCommonService) ContextUtil.getBean("productCommonService");
        Coin productCommon = productCommonService.getProductCommon(code, priceCode);

        String currencyType = ContextUtil.getCurrencyType();
        String Website = ContextUtil.getWebsite();
        String header = exEntrustService.getHeader(null, null, code, priceCode);
        String currentExchangPrices = redisService.get(header + ":" + ExchangeDataCacheRedis.CurrentExchangPrice);
        BigDecimal currentExchangPrice = new BigDecimal("0.00");
        if (null != currentExchangPrices) {
            currentExchangPrice = new BigDecimal(currentExchangPrices);
            // ???????????????
            tradeAccount.setCanSellMoney(digitAccount.getHotMoney().multiply(currentExchangPrice).setScale(2, BigDecimal.ROUND_DOWN));

        }
        BigDecimal buyOnePrice = new BigDecimal("0.00");
        String buyOnePrices = redisService.get(header + ":" + ExchangeDataCacheRedis.BuyOnePrice);
        if (null != buyOnePrices) {
            buyOnePrice = new BigDecimal(buyOnePrices);
        }
        String sellOnePrices = redisService.get(header + ":" + ExchangeDataCacheRedis.SellOnePrice);
        BigDecimal sellOnePrice = new BigDecimal("0.00");
        if (null != sellOnePrices) {
            sellOnePrice = new BigDecimal(sellOnePrices);
        }

        String language_code = remoteAppConfigService.getFinanceByKey("language_code");

        // ?????????????????????????????????????????????
        if (priceCode.equalsIgnoreCase(language_code)) {
            // AppAccount appAccount = appAccountService.get(new
            // QueryFilter(AppAccount.class).addFilter("userName=", username));
            AppAccountRedis appAccount = UserRedisUtils.getAccount(userRedis.getAccountId().toString());

            tradeAccount.setRmb(appAccount != null ? appAccount.getHotMoney() : BigDecimal.ZERO);
            tradeAccount.setColdrmb(appAccount != null ? appAccount.getColdMoney() : BigDecimal.ZERO);
            if (currentExchangPrice.compareTo(new BigDecimal(0)) > 0) {
                // ??????????????????
                tradeAccount.setCanBuyCoin(appAccount.getHotMoney().divide(currentExchangPrice, 10, BigDecimal.ROUND_DOWN));
            }
            // ?????????
            Integer keepDecimalForRmb = productCommon.getKeepDecimalForCurrency();
            if (null == keepDecimalForRmb)
                keepDecimalForRmb = 4;
            tradeAccount.setRmb(tradeAccount.getRmb().setScale(keepDecimalForRmb, BigDecimal.ROUND_DOWN));
            tradeAccount.setColdrmb(tradeAccount.getColdrmb().setScale(keepDecimalForRmb, BigDecimal.ROUND_DOWN));
        } else {// ??????????????????????????????
            // ExDigitalmoneyAccount appAccount =
            // exDigitalmoneyAccountService.get(new
            // QueryFilter(ExDigitalmoneyAccount.class).addFilter("userName=",
            // username).addFilter("coinCode=", priceCode));
            ExDigitalmoneyAccountRedis appAccount = UserRedisUtils.getAccount(userRedis.getDmAccountId(priceCode).toString(), priceCode);

            tradeAccount.setRmb(appAccount != null ? (appAccount.getHotMoney().setScale(productCommon.getKeepDecimalForCoin(), BigDecimal.ROUND_DOWN)) : BigDecimal.ZERO);
            tradeAccount.setColdrmb(appAccount != null ? appAccount.getColdMoney().setScale(productCommon.getKeepDecimalForCoin(), BigDecimal.ROUND_DOWN) : BigDecimal.ZERO);
            if (currentExchangPrice.compareTo(new BigDecimal(0)) > 0) {
                tradeAccount.setCanBuyCoin(appAccount.getHotMoney().divide(currentExchangPrice, productCommon.getKeepDecimalForCoin(), BigDecimal.ROUND_DOWN));
            }
        }

        // ???????????????????????????
        tradeAccount.setBuyMinMoney(productCommon.getBuyMinMoney() == null ? new BigDecimal(0.00) : productCommon.getBuyMinMoney());
        // ???????????????????????????
        tradeAccount.setSellMinCoin(productCommon.getOneTimeOrderNumMin() == null ? new BigDecimal(0.00) : productCommon.getOneTimeOrderNumMin());
        // ???????????????????????????
        tradeAccount.setOneTimeOrderNum(productCommon.getOneTimeOrderNum());

        // ????????????????????????????????????
        tradeAccount.setCoinHotMoney(digitAccount != null ? digitAccount.getHotMoney().setScale(productCommon.getKeepDecimalForCoin(), BigDecimal.ROUND_DOWN) : BigDecimal.ZERO);
        tradeAccount.setCurrentCoinColdMoney(digitAccount.getColdMoney().setScale(productCommon.getKeepDecimalForCoin(), BigDecimal.ROUND_DOWN));

        tradeAccount.setKeepDecimalForCoin(null != productCommon.getKeepDecimalForCoin() ? new BigDecimal(productCommon.getKeepDecimalForCoin()) : new BigDecimal(4));
        tradeAccount.setKeepDecimalForCurrency(null != productCommon.getKeepDecimalForCurrency() ? new BigDecimal(productCommon.getKeepDecimalForCurrency()) : new BigDecimal(2));
        tradeAccount.setCurrentExchangPrice(currentExchangPrice);
        tradeAccount.setBuyOnePrice(buyOnePrice);
        tradeAccount.setSellOnePrice(sellOnePrice);
        tradeAccount.setBuyFeeRate(productCommon.getBuyFeeRate() == null ? new BigDecimal(0.00) : productCommon.getBuyFeeRate().setScale(4, BigDecimal.ROUND_DOWN));
        tradeAccount.setSellFeeRate(productCommon.getSellFeeRate() == null ? new BigDecimal(0.00) : productCommon.getSellFeeRate().setScale(4, BigDecimal.ROUND_DOWN));
        // tradeAccount.setCirculation(productCommon.getCirculation()==null?new
        // BigDecimal(0.00):productCommon.getCirculation().setScale(4,BigDecimal.ROUND_DOWN));
        return new RemoteResult().setSuccess(true).setObj(JSON.toJSONString(tradeAccount));
    }

    @Override
    public String[] addLendEntrust(EntrustTrade exEntrust) {
        String[] rt = new String[2];
        // ?????????????????????????????????????????????
        Long customerId = exEntrust.getCustomerId();
        RemoteResult canTakeMoney = canTakeMoney(customerId.toString());
        if (canTakeMoney.getSuccess() == false) {
            rt[0] = CodeConstant.CODE_FAILED;
            rt[1] = "zhijinzhanghuweifushubuyunxujiaoyi";
            return rt;
        }

        // ??????redis??????
        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(exEntrust.getCustomerId().toString());

        String code = exEntrust.getCoinCode();// ?????????
        String priceCode = exEntrust.getFixPriceCoinCode(); // ?????????
        // ???????????????????????????
        String config = redisService.get("configCache:all");
        JSONObject parseObject = JSONObject.parseObject(config);
        String languageCode = (String) parseObject.get("language_code");// ????????????

        // ExEntrust exEntruste=JSON.parseObject(JSON.toJSONString(exEntrust),
        // ExEntrust.class);
        EntrustTrade et = new EntrustTrade();
        et.setCoinCode(exEntrust.getCoinCode());
        et.setFixPriceCoinCode(exEntrust.getFixPriceCoinCode());
        if ("cny".equalsIgnoreCase(exEntrust.getFixPriceCoinCode()) || "usd".equalsIgnoreCase(exEntrust.getFixPriceCoinCode()) || languageCode.equalsIgnoreCase(priceCode)) {
            et.setFixPriceType(0);
        } else {
            et.setFixPriceType(1);
        }
        et.setCustomerId(exEntrust.getCustomerId());
        et.setEntrustPrice(exEntrust.getEntrustPrice());
        et.setEntrustCount(exEntrust.getEntrustCount());
        et.setEntrustSum(exEntrust.getEntrustSum());
        et.setType(exEntrust.getType());
        et.setSource(exEntrust.getSource());
        et.setEntrustWay(exEntrust.getEntrustWay());
        et.setUserName(exEntrust.getUserName());
        et.setTrueName(exEntrust.getTrueName());
        et.setSurName(exEntrust.getSurName());
        et.setIsOpenCoinFee(exEntrust.getIsOpenCoinFee());
        et.setIsType(exEntrust.getIsType());
        if (et.getFixPriceType().equals(0)) { // ????????????
            et.setAccountId(userRedis.getAccountId());
        } else {
            et.setAccountId(userRedis.getDmAccountId(exEntrust.getFixPriceCoinCode()));
        }
        et.setCoinAccountId(userRedis.getDmAccountId(exEntrust.getCoinCode()));
        // ?????????????????????
        exEntrustService.saveExEntrustTrade(et);

        if (et.getIsType() == 1) {
            JedisPool jedisPool = (JedisPool) ContextUtil.getBean("jedisPool");
            try (Jedis jedis = jedisPool.getResource()) {
                String coinCode = et.getCoinCode() + "_" + et.getFixPriceCoinCode();
                String accountStr = jedis.hget(ExchangeLendKey.getLendAccountKey(coinCode), et.getCustomerId().toString());
                if (!StringUtils.isEmpty(accountStr)) {
                    ExLendAccount account = JSON.parseObject(accountStr, ExLendAccount.class);
                    if (exEntrust.getType().equals(DealConstant.BUY_TYPE)) {
                        // ??????
                        if (account.getPriMoney().compareTo(et.getEntrustSum()) < 0) {
                            rt[0] = CodeConstant.CODE_FAILED;
                            rt[1] = priceCode + "??????";
                            return rt;
                        }
                    } else {
                        // ??????
                        if (account.getTradeMoney().compareTo(et.getEntrustCount()) < 0) {
                            rt[0] = CodeConstant.CODE_FAILED;
                            rt[1] = code + "??????";
                            return rt;
                        }
                    }
                } else {
                    rt[0] = CodeConstant.CODE_FAILED;
                    rt[1] = priceCode + "??????";
                    return rt;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (exEntrust.getType() == 1) {// ????????? ???????????????
                if ("cny".equalsIgnoreCase(priceCode) || "usd".equalsIgnoreCase(priceCode) || languageCode.equalsIgnoreCase(priceCode)) {
                    AppAccountRedis accountRedis = UserRedisUtils.getAccount(userRedis.getAccountId().toString());
                    if (accountRedis.getHotMoney().compareTo(et.getEntrustSum()) < 0) {
                        rt[0] = CodeConstant.CODE_FAILED;
                        rt[1] = priceCode + "??????";
                        return rt;
                    }
                } else {
                    ExDigitalmoneyAccountRedis ear = UserRedisUtils.getAccount(userRedis.getDmAccountId(priceCode).toString(), priceCode);
                    if (ear == null) {
                        UserRedis userRedis1 = new UserRedis();
                        userRedis1.setId(exEntrust.getCustomerId().toString());
                        Map<String, Long> map = this.findAllAccountId(exEntrust.getCustomerId().toString());
                        userRedis1.setAccountId(map.get("accountId") == null ? null : map.get("accountId"));
                        // ?????????????????????
                        map.remove("accountId");
                        userRedis1.setDmAccountId(map);
                        redisUtil.put(userRedis1, userRedis1.getId());
                    } else {
                        if (ear.getHotMoney().compareTo(et.getEntrustSum()) < 0) {
                            rt[0] = CodeConstant.CODE_FAILED;
                            rt[1] = priceCode + "??????";
                            return rt;
                        }
                    }
                }
            }

            if (et.getType() == 2) {// ????????? ???????????????
                ExDigitalmoneyAccountRedis ear = UserRedisUtils.getAccount(userRedis.getDmAccountId(code).toString(), code);
                if (ear.getHotMoney().compareTo(et.getEntrustCount()) < 0) {
                    rt[0] = CodeConstant.CODE_FAILED;
                    rt[1] = code + "??????";
                    return rt;
                }
            }

            if (et.getEntrustPrice().compareTo(new BigDecimal("0")) == 0 && et.getEntrustCount().compareTo(new BigDecimal("0")) == 0 && et.getEntrustSum().compareTo(new BigDecimal("0")) == 0) {
                rt[0] = CodeConstant.CODE_FAILED;
                rt[1] = code + "??????";
                return rt;
            }
            if (et.getEntrustWay() == 1 && et.getEntrustCount().compareTo(new BigDecimal("0")) == 0 && et.getEntrustSum().compareTo(new BigDecimal("0")) == 0) {
                rt[0] = CodeConstant.CODE_FAILED;
                rt[1] = code + "??????";
                return rt;
            }
        }
        // ?????????
        String str = JSON.toJSONString(et);
        logger.error(str);
        // ????????????
        messageProducer.toTrade(str);
        String[] arr = {CodeConstant.CODE_SUCCESS};
        return arr;
    }

    /**
     * ??????????????????c2c??????
     * @return
     */
    @Override
    public long getTransactionNumOnTodayOfSell () {
        QueryFilter qf = new QueryFilter(C2cTransaction.class);
        qf.addFilter("transactionType=", 2);
        qf.addFilter("DATE_FORMAT(created,'%Y-%m-%d')=",DateUtil.dateToString(new Date(), "yyyy-MM-dd"));
        return c2cTransactionService.count(qf);
    }

    @Override
    public String[] addEntrust(EntrustTrade exEntrust) {
        String[] rt = new String[2];
        // ?????????????????????????????????????????????
        Long customerId = exEntrust.getCustomerId();
        RemoteResult canTakeMoney = canTakeMoney(customerId.toString());
        if (canTakeMoney.getSuccess() == false) {
            rt[0] = CodeConstant.CODE_FAILED;
            rt[1] = "zhijinzhanghuweifushubuyunxujiaoyi";
            return rt;
        }

        // ??????redis??????
        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(exEntrust.getCustomerId().toString());

        String code = exEntrust.getCoinCode();// ?????????
        String priceCode = exEntrust.getFixPriceCoinCode(); // ?????????
        // ???????????????????????????
        String config = redisService.get("configCache:all");
        JSONObject parseObject = JSONObject.parseObject(config);
        String languageCode = (String) parseObject.get("language_code");// ????????????

        // ExEntrust exEntruste=JSON.parseObject(JSON.toJSONString(exEntrust),
        // ExEntrust.class);
        String[] check = exEntrustService.saveExEntrustCheck(exEntrust);
        if (check != null && CodeConstant.CODE_SUCCESS.equals(check[0])) {
            EntrustTrade et = new EntrustTrade();
            et.setCoinCode(exEntrust.getCoinCode());
            et.setFixPriceCoinCode(exEntrust.getFixPriceCoinCode());
            //if ("cny".equalsIgnoreCase(exEntrust.getFixPriceCoinCode()) || "usd".equalsIgnoreCase(exEntrust.getFixPriceCoinCode()) || languageCode.equalsIgnoreCase(priceCode)) {
            if ("cny".equalsIgnoreCase(exEntrust.getFixPriceCoinCode()) || "usd".equalsIgnoreCase(exEntrust.getFixPriceCoinCode())) {
                et.setFixPriceType(0);
            } else {
                et.setFixPriceType(1);
            }
            et.setCustomerId(exEntrust.getCustomerId());
            et.setEntrustPrice(exEntrust.getEntrustPrice());
            et.setEntrustCount(exEntrust.getEntrustCount());
            et.setEntrustSum(exEntrust.getEntrustSum());
            et.setType(exEntrust.getType());
            et.setSource(exEntrust.getSource());
            et.setEntrustWay(exEntrust.getEntrustWay());
            et.setUserName(exEntrust.getUserName());
            et.setTrueName(exEntrust.getTrueName());
            et.setSurName(exEntrust.getSurName());
            et.setIsOpenCoinFee(exEntrust.getIsOpenCoinFee());
            et.setIsType(exEntrust.getIsType());
            if (et.getFixPriceType().equals(0)) { // ????????????
                et.setAccountId(userRedis.getAccountId());
            } else {
                et.setAccountId(userRedis.getDmAccountId(exEntrust.getFixPriceCoinCode()));
            }
            et.setCoinAccountId(userRedis.getDmAccountId(exEntrust.getCoinCode()));
            // ?????????????????????
            exEntrustService.saveExEntrustTrade(et);

            if (et.getIsType() == 1) {
                JedisPool jedisPool = (JedisPool) ContextUtil.getBean("jedisPool");
                try (Jedis jedis = jedisPool.getResource()) {
                    String coinCode = et.getCoinCode() + "_" + et.getFixPriceCoinCode();
                    String accountStr = jedis.hget(ExchangeLendKey.getLendAccountKey(coinCode), et.getCustomerId().toString());
                    if (!StringUtils.isEmpty(accountStr)) {
                        ExLendAccount account = JSON.parseObject(accountStr, ExLendAccount.class);
                        if (exEntrust.getType().equals(DealConstant.BUY_TYPE)) {
                            // ??????
                            if (account.getPriMoney().compareTo(et.getEntrustSum()) < 0) {
                                rt[0] = CodeConstant.CODE_FAILED;
                                rt[1] = priceCode + "??????";
                                return rt;
                            }
                        } else {
                            // ??????
                            if (account.getTradeMoney().compareTo(et.getEntrustCount()) < 0) {
                                rt[0] = CodeConstant.CODE_FAILED;
                                rt[1] = code + "??????";
                                return rt;
                            }
                        }
                    } else {
                        rt[0] = CodeConstant.CODE_FAILED;
                        rt[1] = priceCode + "??????";
                        return rt;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (exEntrust.getType() == 1) {// ????????? ???????????????
                    //if ("cny".equalsIgnoreCase(priceCode) || "usd".equalsIgnoreCase(priceCode) || languageCode.equalsIgnoreCase(priceCode)) {
                    if ("cny".equalsIgnoreCase(priceCode) || "usd".equalsIgnoreCase(priceCode)) {
                        AppAccountRedis accountRedis = UserRedisUtils.getAccount(userRedis.getAccountId().toString());
                        if (accountRedis.getHotMoney().compareTo(et.getEntrustSum()) < 0) {
                            rt[0] = CodeConstant.CODE_FAILED;
                            rt[1] = priceCode + "??????";
                            return rt;
                        }
                    } else {
                        ExDigitalmoneyAccountRedis ear = UserRedisUtils.getAccount(userRedis.getDmAccountId(priceCode).toString(), priceCode);
                        if (ear == null) {
                            UserRedis userRedis1 = new UserRedis();
                            userRedis1.setId(exEntrust.getCustomerId().toString());
                            Map<String, Long> map = this.findAllAccountId(exEntrust.getCustomerId().toString());
                            userRedis1.setAccountId(map.get("accountId") == null ? null : map.get("accountId"));
                            // ?????????????????????
                            map.remove("accountId");
                            userRedis1.setDmAccountId(map);
                            redisUtil.put(userRedis1, userRedis1.getId());
                        } else {
                            if (ear.getHotMoney().compareTo(et.getEntrustSum()) < 0) {
                                rt[0] = CodeConstant.CODE_FAILED;
                                rt[1] = priceCode + "??????";
                                return rt;
                            }
                        }
                    }
                }

                if (et.getType() == 2) {// ????????? ???????????????
                    ExDigitalmoneyAccountRedis ear = UserRedisUtils.getAccount(userRedis.getDmAccountId(code).toString(), code);
                    if (ear.getHotMoney().compareTo(et.getEntrustCount()) < 0) {
                        rt[0] = CodeConstant.CODE_FAILED;
                        rt[1] = code + "??????";
                        return rt;
                    }
                }

                if (et.getEntrustPrice().compareTo(new BigDecimal("0")) == 0 && et.getEntrustCount().compareTo(new BigDecimal("0")) == 0 && et.getEntrustSum().compareTo(new BigDecimal("0")) == 0) {
                    rt[0] = CodeConstant.CODE_FAILED;
                    rt[1] = code + "??????";
                    return rt;
                }
                if (et.getEntrustWay() == 1 && et.getEntrustCount().compareTo(new BigDecimal("0")) == 0 && et.getEntrustSum().compareTo(new BigDecimal("0")) == 0) {
                    rt[0] = CodeConstant.CODE_FAILED;
                    rt[1] = code + "??????";
                    return rt;
                }
            }
            // ?????????
            String str = JSON.toJSONString(et);
            logger.error(str);
            // ????????????
            messageProducer.toTrade(str);
            String[] arr = {CodeConstant.CODE_SUCCESS};
            return arr;
        } else {
            return check;
        }

    }

    public hry.manage.remote.model.AppCustomer selectCustomerByName(String username) {
        QueryFilter f = new QueryFilter(AppCustomer.class);
        f.addFilter("userName=", username);
        AppCustomer appCustomer = appCustomerService.get(f);
        if (appCustomer != null) {
            hry.manage.remote.model.AppCustomer cust = new hry.manage.remote.model.AppCustomer();
            cust.setId(appCustomer.getId());
            cust.setUserName(appCustomer.getUserName());
            return cust;
        }
        return null;
    }

    @Override
    public AppAccountManage getByCustomerIdAndType(Long customerId, String currencyType, String website) {

        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(customerId.toString());
        AppAccountRedis account = UserRedisUtils.getAccount(userRedis.getAccountId().toString());

        AppAccountManage appAccountManage = common(account);

        return appAccountManage;
    }

    @Override
    public AppAccountManage getAppAccountManage(Long customerId) {
        AppAccountManage appAccountManage = new AppAccountManage();
        AppAccount account = appAccountService.get(new QueryFilter(AppAccount.class).addFilter("customerId=", customerId));
        if (account != null) {
            appAccountManage.setId(account.getId());
            appAccountManage.setUserName(account.getUserName());
            appAccountManage.setHotMoney(account.getHotMoney());
        }
        return appAccountManage;
    }

    /**
     * ???????????? ???????????? denghf
     *
     * @param appAccount
     * @return
     */
    public AppAccountManage common(AppAccountRedis appAccount) {
        AppAccountManage appAccountManage = new AppAccountManage();
        appAccountManage.setId(appAccount.getId());
        appAccountManage.setUserName(appAccount.getUserName());
        appAccountManage.setHotMoney(appAccount.getHotMoney());
        return appAccountManage;
    }

    /**
     * ???????????? ???????????? denghf
     *
     * @param appPersonInfo
     * @return
     */
    public AppPersonInfoManage common(AppPersonInfo appPersonInfo) {
        AppPersonInfoManage appPersonInfoManage = new AppPersonInfoManage();
        appPersonInfoManage.setTrueName(appPersonInfo.getTrueName());
        appPersonInfoManage.setSurName(appPersonInfo.getSurname());
        return appPersonInfoManage;
    }

    @Override
    public boolean isAgentExist(String agentLevel, String provinceId, String cityId, String countyId) {
        return appPersonInfoService.isAgentExist(agentLevel, provinceId, cityId, countyId);
    }

    /**
     * ????????????
     */
    @Override
    public AppOurAccountManage getOurAccount(String website, String currencyType, String accountType) {
        QueryFilter filter = new QueryFilter(AppAccountRecord.class);
        //filter.addFilter("website=", website); //
        filter.addFilter("currencyType=", currencyType);
        filter.addFilter("isShow=", "1");
        filter.addFilter("accountType=", accountType);
        AppOurAccount ourAccount = appOurAccountService.get(filter);
        AppOurAccountManage appOurAccountManage = common(ourAccount);
        return appOurAccountManage;
    }

    public AppOurAccountManage common(AppOurAccount ourAccount) {
        AppOurAccountManage appOurAccountManage = new AppOurAccountManage();
        appOurAccountManage.setAccountMoney(ourAccount.getAccountMoney());
        appOurAccountManage.setAccountName(ourAccount.getAccountName());
        appOurAccountManage.setAccountNumber(ourAccount.getAccountNumber());
        appOurAccountManage.setAccountType(ourAccount.getAccountType());
        appOurAccountManage.setBankAddress(ourAccount.getBankAddress());
        appOurAccountManage.setBankLogo(ourAccount.getBankLogo());
        appOurAccountManage.setBankName(ourAccount.getBankName());
        appOurAccountManage.setCoinTotalMoney(ourAccount.getCoinTotalMoney());
        appOurAccountManage.setCreated(ourAccount.getCreated());
        appOurAccountManage.setCurrencyType(ourAccount.getCurrencyType());
        appOurAccountManage.setDicBankName(ourAccount.getDicBankName());
        appOurAccountManage.setId(ourAccount.getId());
        appOurAccountManage.setIsShow(ourAccount.getIsShow());
        appOurAccountManage.setModified(ourAccount.getModified());
        appOurAccountManage.setOpenAccountType(ourAccount.getOpenAccountType());
        appOurAccountManage.setOpenTime(ourAccount.getOpenTime());
        appOurAccountManage.setRemark(ourAccount.getRemark());
        appOurAccountManage.setRetainsMoney(ourAccount.getRetainsMoney());
        appOurAccountManage.setWebsite(ourAccount.getWebsite());
        appOurAccountManage.setWithdrawMoney(ourAccount.getWithdrawMoney());
        return appOurAccountManage;
    }

    @Override
    public String[] addEntrustCheck(EntrustTrade exEntrust) {
        return exEntrustService.saveExEntrustCheck(exEntrust);
    }

    @Override
    public RemoteResult finaCoins() {

        List<ExCointoCoin> find = exCointoCoinService.find(new QueryFilter(ExCointoCoin.class).addFilter("state=", 1));

        ArrayList<Coin> list = new ArrayList<Coin>();
        for (ExCointoCoin ex : find) {
            Coin coin = new Coin();
            coin.setCoinCode(ex.getCoinCode());
            coin.setFixPriceCoinCode(ex.getFixPriceCoinCode());
            list.add(coin);
        }

        return new RemoteResult().setSuccess(true).setObj(list);
    }

    @Override
    public String[] cancelExEntrust(EntrustTrade entrustTrade) {
        String[] rt = new String[2];
        rt = exDmPingService.checkPing(entrustTrade.getCustomerId());
        if (rt[0].equals(CodeConstant.CODE_FAILED)) {
        	rt[1]="";
            return rt;
        }
        return exEntrustService.cancelExEntrust(entrustTrade, "????????????");
    }

    public static double div(double v1, double v2) {
        return div(v1, v2);
    }

    /**
     * ?????????????????????BTC?????????????????????BTC?????? ?????????????????????NULL
     *
     * @param userRedis
     * @param code
     * @return
     */
    public BigDecimal computeBTC(UserRedis userRedis, String code) {

        // ???????????????
        BigDecimal btcSum = new BigDecimal(0);

        String string = redisService.get("cn:coinInfoList");
        List<Coin> coins = JSONArray.parseArray(string, Coin.class);

        Map<String, Long> dmMap = userRedis.getDmAccountId();
        ExDigitalmoneyAccountRedis ear = UserRedisUtils.getAccount(dmMap.get(code).toString(), code);
        boolean flag = false;
        for (Coin c : coins) {
            if (ear.getCoinCode().equals(c.getCoinCode())) {
                if ("BTC".equals(c.getFixPriceCoinCode())) {
                    // ???????????????
                    String changePrice = redisService.get(code + "_BTC:currentExchangPrice");
                    btcSum = new BigDecimal(changePrice);
                    /*
                     * if(!StringUtils.isEmpty(changePrice)){ flag = true; BigDecimal coinBtc =
                     * (ear.getHotMoney().add(ear.getColdMoney())).multiply(new
                     * BigDecimal(changePrice)); btcSum = btcSum.add(coinBtc); }
                     */
                    break;
                }

            }
        }

        if (!StringUtils.isEmpty(btcSum)) {
            return btcSum;
        }
        return null;
    }

    /**
     * ?????????BTC???????????????????????????ETH???????????????
     *
     * @param userRedis
     * @return BigDecimal[0] btc ,BigDecimal[1] eth
     */
    public BigDecimal[] computeBTCETH(UserRedis userRedis) {

        String string = redisService.get("cn:coinInfoList");
        List<Coin> coins = JSONArray.parseArray(string, Coin.class);

        // ???????????????
        BigDecimal btcSum = new BigDecimal(0);

        // ???????????????
        Map<String, Long> dmMap = userRedis.getDmAccountId();
        if (dmMap != null) {
            Set<String> keySet = dmMap.keySet();
            Iterator<String> it = keySet.iterator();
            while (it.hasNext()) {
                String code = it.next();
                if ("BTC".equals(code)) {// ??????????????????????????????????????????
                    ExDigitalmoneyAccountRedis ear = UserRedisUtils.getAccount(dmMap.get(code).toString(), code);
                    if (ear != null) {
                        btcSum = btcSum.add(ear.getHotMoney()).add(ear.getColdMoney());
                    }
                } else {// ??????????????????????????????????????????
                    ExDigitalmoneyAccountRedis ear = UserRedisUtils.getAccount(dmMap.get(code).toString(), code);
                    if (ear != null) {
                        for (Coin c : coins) {
                            if (ear.getCoinCode().equals(c.getCoinCode())) {
                                if ("BTC".equals(c.getFixPriceCoinCode())) {
                                    // ???????????????
                                    String changePrice = redisService.get(code + "_BTC:currentExchangPrice");
                                    if (!StringUtils.isEmpty(changePrice)) {
                                        BigDecimal coinBtc = (ear.getHotMoney().add(ear.getColdMoney())).multiply(new BigDecimal(changePrice));
                                        btcSum = btcSum.add(coinBtc);
                                    }
                                    break;
                                }

                            }
                        }
                    }
                }
            }
        }

        BigDecimal[] arr = new BigDecimal[2];
        arr[0] = btcSum;

        return arr;
    }

    @Override
    public BigDecimal[] getSum(Long customerId) {
        String crmb = remoteAppConfigService.getFinanceByKey("coinCodeForRmb"); //?????????????????????ustdt
        // ?????????
        BigDecimal coinNetAsset = lendCoinAccountService.getCoinNetAsset(customerId, crmb, crmb, "cny", "cn");
        // ?????????
        BigDecimal rMBLendMoneyed = lendCoinAccountService.getCodeLendMoneySum(customerId, crmb, crmb, "cny", "cn");
        // ?????????
        BigDecimal sum = coinNetAsset.add(rMBLendMoneyed);
        BigDecimal[] arrsum = new BigDecimal[5];
        arrsum[0] = coinNetAsset;
        arrsum[1] = rMBLendMoneyed;
        arrsum[2] = sum;
        BigDecimal per = new BigDecimal("100");
        if (sum.compareTo(new BigDecimal("0")) > 0) {
            per = coinNetAsset.multiply(new BigDecimal("100")).divide(sum, 2, BigDecimal.ROUND_DOWN);
        }
        arrsum[3] = per;
        arrsum[4] = new BigDecimal("100").subtract(per);
        return arrsum;
    }

    @Override
    public MyAccountTO myAccount(Long customerId) {

        // ???redis??????
        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(customerId.toString());

        BigDecimal[] computeBTCETH = computeBTCETH(userRedis);

        // ???????????????
        BigDecimal btcSum = computeBTCETH[0];

        // ????????????
        BigDecimal $money = new BigDecimal(0);

        // B??????????????????
        BigDecimal bprice = new BigDecimal(1);
        String bweb_price = redisService.get("bitstamp_btc_price");
        if (!StringUtils.isEmpty(bweb_price)) {
            bprice = new BigDecimal(bweb_price);
        }

        // B???ETH??????
        BigDecimal eprice = new BigDecimal(1);
        String eweb_price = redisService.get("bitstamp_eth_price");
        if (!StringUtils.isEmpty(eweb_price)) {
            bprice = new BigDecimal(eweb_price);
        }

        $money = btcSum.multiply(bprice);

        // ??????????????????
        MyAccountTO myAccountTO = new MyAccountTO();
        Integer keepDecimalForRmb = exProductService.getkeepDecimalForRmb();
        myAccountTO.setSumMoney($money.setScale(keepDecimalForRmb, BigDecimal.ROUND_HALF_DOWN));
        myAccountTO.setSumBtcMoney(btcSum.setScale(8, BigDecimal.ROUND_HALF_DOWN));

        AppAccount account = appAccountService.get(new QueryFilter(AppAccount.class).addFilter("customerId=", customerId));
        if (account != null) {
            myAccountTO.setHotMoney(account.getHotMoney().setScale(2, BigDecimal.ROUND_HALF_DOWN));
            myAccountTO.setColdMoney(account.getColdMoney().setScale(2, BigDecimal.ROUND_HALF_DOWN));
        }

        return myAccountTO;

    }

    @Override
    public List<CoinAccount> findCoinAccount(Long customerId) {

        ArrayList<CoinAccount> list = new ArrayList<CoinAccount>();

        // ???redis??????
        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(customerId.toString());
        if (userRedis == null || "".equals(userRedis)) {
            return list;
        }

        // ?????????????????????????????????id
        Map<String, Long> map = userRedis.getDmAccountId();
        Set<String> keySet = map.keySet();
        // ??????????????????
        List<ExProduct> plist = exProductService.findByIssueState(2, "");
        List<String> lists = new ArrayList<>();
        if (plist != null && plist.size() > 0) {
            for (ExProduct ex : plist) {
                lists.add(ex.getCoinCode());
            }
        }
        for (String key : keySet) {
            // ???????????????
            ExDigitalmoneyAccountRedis exaccount = UserRedisUtils.getAccount(userRedis.getDmAccountId(key).toString(), key);
            if (exaccount != null) {
                if (lists != null && lists.size() > 0) {
                    if (!lists.contains(exaccount.getCoinCode())) {
                        ProductCommon productCommon = productCommonService.getProductCommon(exaccount.getCoinCode());
                        if (productCommon != null && null != productCommon.getKeepDecimalForCoin()) {
                            CoinAccount coinAccount = new CoinAccount();
                            coinAccount.setCoinCode(exaccount.getCoinCode());
                            coinAccount.setColdMoney(exaccount.getColdMoney().setScale(8, BigDecimal.ROUND_HALF_DOWN));
                            coinAccount.setHotMoney(exaccount.getHotMoney().setScale(8, BigDecimal.ROUND_HALF_DOWN));
                            coinAccount.setOpenTibi(productCommon.getOpenTibi());
                            if (null != productCommon && null != productCommon.getKeepDecimalForCoin()) {
                                coinAccount.setKeepDecimalForCoin(productCommon.getKeepDecimalForCoin());
                                coinAccount.getColdMoney().setScale(productCommon.getKeepDecimalForCoin(), BigDecimal.ROUND_HALF_DOWN);
                                coinAccount.getHotMoney().setScale(productCommon.getKeepDecimalForCoin(), BigDecimal.ROUND_HALF_DOWN);
                                coinAccount.setName(productCommon.getName());

                            }
                            list.add(coinAccount);
                        }
                    }
                } else {
                    ProductCommon productCommon = productCommonService.getProductCommon(exaccount.getCoinCode());
                    if (productCommon != null && null != productCommon.getKeepDecimalForCoin()) {
                        CoinAccount coinAccount = new CoinAccount();
                        coinAccount.setCoinCode(exaccount.getCoinCode());
                        coinAccount.setColdMoney(exaccount.getColdMoney().setScale(8, BigDecimal.ROUND_HALF_DOWN));
                        coinAccount.setHotMoney(exaccount.getHotMoney().setScale(8, BigDecimal.ROUND_HALF_DOWN));
                        coinAccount.setOpenTibi(productCommon.getOpenTibi());
                        if (null != productCommon && null != productCommon.getKeepDecimalForCoin()) {
                            coinAccount.setKeepDecimalForCoin(productCommon.getKeepDecimalForCoin());
                            coinAccount.getColdMoney().setScale(productCommon.getKeepDecimalForCoin(), BigDecimal.ROUND_HALF_DOWN);
                            coinAccount.getHotMoney().setScale(productCommon.getKeepDecimalForCoin(), BigDecimal.ROUND_HALF_DOWN);
                            coinAccount.setName(productCommon.getName());

                        }
                        list.add(coinAccount);
                    }
                }
            }
        }

        // ???redis???????????????
        if (list != null && list.size() > 0) {
            RedisService redisService = (RedisService) ContextUtil.getBean("redisService");
            String string = redisService.get("cn:productinfoListall");
            List<Coin> coins = JSONArray.parseArray(string, Coin.class);
            if (coins != null && coins.size() > 0) {
                for (Coin coin : coins) {
                    for (CoinAccount coinAccount : list) {
                        if (coin.getCoinCode().equals(coinAccount.getCoinCode())) {
                            coinAccount.setPicturePath(coin.getPicturePath());
                        }
                    }
                }
            }
        }

        return list;
    }

    @Override
    public RemoteResult selectPhone(String telphone) {
        QueryFilter f = new QueryFilter(AppPersonInfo.class);
        f.addFilter("mobilePhone=", telphone);// ?????????
        AppPersonInfo _appPersonInfo = appPersonInfoService.get(f);
        if (_appPersonInfo != null) {
            return new RemoteResult().setSuccess(true);
        } else {
            return new RemoteResult().setSuccess(false);
        }
    }

    @Override
    public RemoteResult updateFeature(String customerId, String feature) {
        AppCustomer appCustomer=new AppCustomer();
        appCustomer.setId(Long.parseLong(customerId));
        if(StringUtil.isNull(feature)){
            appCustomer.setFeature(feature);
        }
        appCustomerService.update(appCustomer);
        return new RemoteResult().setSuccess(true);
    }

    @Override
    public RemoteResult selectAgent(String username) {
        QueryFilter qf = new QueryFilter(AppCustomer.class);
        qf.addFilter("referralCode=", username);
        // qf.addFilter("states=", 2);
        List<AppCustomer> find = appCustomerService.find(qf);
        if (find.size() > 0) {
            return new RemoteResult().setSuccess(true);
        } else {
            return new RemoteResult().setSuccess(false);
        }
    }

    @Override
    public RemoteResult updatepwd(String pwd, String tel) {
        QueryFilter qf = new QueryFilter(AppCustomer.class);
        qf.addFilter("userName=", tel);
        AppCustomer appcustomer = appCustomerService.get(qf);

        PasswordHelper passwordHelper = new PasswordHelper();
        String encryString = passwordHelper.encryString(pwd, appcustomer.getSalt());
        appcustomer.setPassWord(encryString);
        appCustomerService.update(appcustomer);
        return new RemoteResult().setSuccess(true);
    }


    /**
     * ??????User
     *
     * @param appCustomer
     * @param apppersonInfo
     * @return
     */
    private User createUser(AppCustomer appCustomer, AppPersonInfo apppersonInfo) {
        User user = new User();
        user.setBusNumber(appCustomer.getBusNumber());//????????????
        user.setCountry(apppersonInfo.getCountry());
        user.setEmail(apppersonInfo.getEmail());
        user.setMobile(apppersonInfo.getMobilePhone());
        user.setCardcode(apppersonInfo.getCardId());
        user.setUsername(appCustomer.getUserName());
        user.setUserCode(appCustomer.getUserCode());
        user.setAccountPassWord(appCustomer.getAccountPassWord());
        user.setIsReal(appCustomer.getIsReal() == null ? 0 : appCustomer.getIsReal().intValue());
        user.setIsDelete(appCustomer.getIsDelete());
        user.setIsChange(appCustomer.getIsChange());
        user.setCustomerId(appCustomer.getId());
        user.setTruename(apppersonInfo.getTrueName());
        user.setSurname(apppersonInfo.getSurname());
        user.setIsLock(appCustomer.getIsLock());
        user.setCustomerType(apppersonInfo.getCustomerType());
        user.setSalt(appCustomer.getSalt());
        //google Key???
        user.setGoogleKey(appCustomer.getGoogleKey());
        user.setGoogleState(appCustomer.getGoogleState());
        user.setMessIp(appCustomer.getMessIp());
        user.setPassDate(appCustomer.getPassDate());
        user.setPhoneState(appCustomer.getPhoneState());
        user.setStates(appCustomer.getStates());
        user.setPassword(appCustomer.getPassWord());
        user.setIsOpenCoinFee(apppersonInfo.getIsOpenCoinFee());
        return user;
    }


    @Override
    public User selectMobilePhoneOrEmail(String value) {
        QueryFilter qp = new QueryFilter(AppPersonInfo.class);
        //???????????????@????????????????????????
        if (!value.contains("@")) {
            qp.addFilter("mobilePhone=", value);
        } else {
            qp.addFilter("email=", value);
        }

        AppPersonInfo PersonInfo = appPersonInfoService.get(qp);

        QueryFilter qc = new QueryFilter(AppCustomer.class);
        qc.addFilter("id=", PersonInfo.getCustomerId());
        AppCustomer appCustomer = appCustomerService.get(qc);
        User user = new User();
        if (appCustomer != null) {
            user = createUser(appCustomer, PersonInfo);
        }
        return user;
    }

    /**
     * ??????????????????
     *
     * @param customerId
     * @return
     */
    @Override
    public String[] checkLend(Long customerId) {
        QueryFilter filter = new QueryFilter(ExDmLend.class);
        filter.addFilter("status!=", 3);
        filter.addFilter("customerId=", customerId);
        List<ExDmLend> exDmLends = exDmLendService.find(filter);
        String[] result = new String[2];
        if (null != exDmLends && exDmLends.size() > 0) {
            result[0] = CodeConstant.CODE_FAILED;
            result[1] = "goingPing";
            return result;
        } else {
            result[0] = CodeConstant.CODE_SUCCESS;
            return result;
        }
    }

    @Override
    public User selectByTel(String tel) {
        QueryFilter qf = new QueryFilter(AppCustomer.class);
        qf.addFilter("userName=", tel);
        AppCustomer appCustomer = appCustomerService.get(qf);
        User user = new User();
        if (appCustomer != null) {
            QueryFilter qfw = new QueryFilter(AppPersonInfo.class);
            qfw.addFilter("customerId=", appCustomer.getId());
            AppPersonInfo apppersonInfo = appPersonInfoService.get(qfw);
            user = createUser(appCustomer, apppersonInfo);
        }
        return user;
    }

    @Override
    public RemoteResult appresetapw(String username, String accountPassWord) {

        AppCustomer appCustomer = appCustomerService.get(new QueryFilter(AppCustomer.class).addFilter("username=", username));

        PasswordHelper passwordHelper = new PasswordHelper();

        // ?????????????????????
        appCustomer.setAccountPassWord(passwordHelper.encryString(accountPassWord, appCustomer.getSalt()));
        appCustomerService.update(appCustomer);

        return new RemoteResult().setSuccess(true);

    }

    @Override
    public CoinAccount getAppaccount(Long id) {
        AppAccount account = appAccountService.get(new QueryFilter(AppAccount.class).addFilter("customerId=", id));
        CoinAccount c = new CoinAccount();
        if (account != null) {
            c.setHotMoney(account.getHotMoney());
            c.setColdMoney(account.getColdMoney());
            c.setAccountNum(account.getAccountNum());
        }
        return c;
    }

    // ??????????????????????????????
    /*
     * public AppCustomer getAppMoblie(String mobile){ AppCustomer account =
     * appCustomerService.get(new
     * QueryFilter(AppCustomer.class).addFilter("username=", mobile)); return
     * account; }
     */

    @Override
    public RemoteResult testAppCustomer(String username) {
        AppCustomer appCustomer = appCustomerDao.getAppCustomerSingleByUserName(username);
        if (appCustomer != null) {
            return new RemoteResult().setSuccess(true);
        } else {
            return new RemoteResult().setSuccess(false);
        }

    }

    @Override
    public RemoteResult sendgoogle(String userCode, String savedSecret) {
        // TODO Auto-generated method stub
        AppCustomer appCustomer = appCustomerService.get(new QueryFilter(AppCustomer.class).addFilter("userCode=", userCode));
        // ?????????key
        if (savedSecret != null && !savedSecret.equals("")) {
            logger.error("savedSecret not null");
            appCustomer.setGoogleState(1);
            appCustomer.setIsProving(1);
            appCustomer.setGoogleKey(savedSecret);
            appCustomerService.update(appCustomer);
            return new RemoteResult().setSuccess(true);
        } else {
            return new RemoteResult().setSuccess(false).setCode("sendgoogleerror");
        }
    }

    @Override
    public RemoteResult jcgoogle(String mobile, String savedSecret) {
        // TODO Auto-generated method stub
        AppCustomer appCustomer = appCustomerService.get(new QueryFilter(AppCustomer.class).addFilter("username=", mobile));
        // ?????????key
        if (appCustomer != null) {
            appCustomer.setGoogleState(0);
            appCustomer.setIsProving(0);
            appCustomer.setGoogleDate(new Date());
            appCustomer.setPassDate(new Date());
            appCustomer.setGoogleKey(null);
            appCustomer.setSafeLoginType(1);
            appCustomer.setSafeTixianType(1);
            appCustomer.setSafeTradeType(1);
            appCustomerService.update(appCustomer);
            return new RemoteResult().setSuccess(true);
        } else {
            return new RemoteResult().setSuccess(false).setMsg("user_no_exist");
        }

    }

    // ???ip???????????????
    @Override
    public RemoteResult savaIp(String mobile, String messIp) {
        AppCustomer appCustomer = appCustomerService.get(new QueryFilter(AppCustomer.class).addFilter("userName=", mobile));
        // ?????????key
        appCustomer.setMessIp(messIp);
        appCustomerService.update(appCustomer);
        return new RemoteResult().setSuccess(true);
    }

    @Override
    public RemoteResult activation(String code) {

        QueryFilter filter = new QueryFilter(AppPersonInfo.class);
        filter.addFilter("emailCode=", code);
        AppPersonInfo appPersonInfo = appPersonInfoService.get(filter);
        if (appPersonInfo != null) {
            AppCustomer appCustomer = appCustomerService.get(appPersonInfo.getCustomerId());
            appCustomer.setHasEmail(1);
            appCustomerService.update(appCustomer);
            return new RemoteResult().setSuccess(true);
        }
        return new RemoteResult();

    }

    @Override
    public Map<String, Long> findAllAccountId(String id) {
        Map<String, Long> map = new HashMap<String, Long>();

        AppAccount appAccount = appAccountService.get(new QueryFilter(AppAccount.class).addFilter("customerId=", Long.valueOf(id)));
        if (appAccount != null) {

            RedisUtil<AppAccountRedis> redisUtil = new RedisUtil<AppAccountRedis>(AppAccountRedis.class);
            AppAccountRedis appAccountRedis = redisUtil.get(appAccount.getId().toString());
            // ??????redis??????accout???????????????
            if (appAccountRedis == null || appAccountRedis.getHotMoney() == null || appAccountRedis.getHotMoney().compareTo(new BigDecimal(0)) == 0) {
                AppAccountRedis ar = new AppAccountRedis();
                ar.setColdMoney(appAccount.getColdMoney());
                ar.setCustomerId(appAccount.getCustomerId());
                ar.setHotMoney(appAccount.getHotMoney());
                ar.setId(appAccount.getId());
                ar.setUserName(appAccount.getUserName());
                redisUtil.put(ar, ar.getId().toString());
            }
            map.put("accountId", appAccount.getId());
        }

        List<ExDigitalmoneyAccount> list = exDigitalmoneyAccountService.find(new QueryFilter(ExDigitalmoneyAccount.class).addFilter("customerId=", Long.valueOf(id)));
        if (list != null && list.size() > 0) {
            for (ExDigitalmoneyAccount exDigitalmoneyAccount : list) {

                RedisUtil<ExDigitalmoneyAccountRedis> redisUtil = new RedisUtil<ExDigitalmoneyAccountRedis>(ExDigitalmoneyAccountRedis.class);
                ExDigitalmoneyAccountRedis exDigitalmoneyAccountRedis = redisUtil.get(exDigitalmoneyAccount.getId().toString());
                // ??????redis??????accout???????????????
                if (exDigitalmoneyAccountRedis == null || exDigitalmoneyAccountRedis.getHotMoney() == null || exDigitalmoneyAccountRedis.getHotMoney().compareTo(new BigDecimal(0)) == 0) {
                    ExDigitalmoneyAccountRedis exar = new ExDigitalmoneyAccountRedis();
                    exar.setCoinCode(exDigitalmoneyAccount.getCoinCode());
                    exar.setColdMoney(exDigitalmoneyAccount.getColdMoney());
                    exar.setHotMoney(exDigitalmoneyAccount.getHotMoney());
                    exar.setCustomerId(exDigitalmoneyAccount.getCustomerId());
                    exar.setId(exDigitalmoneyAccount.getId());
                    redisUtil.put(exar, exDigitalmoneyAccount.getId().toString());
                }
                map.put(exDigitalmoneyAccount.getCoinCode(), exDigitalmoneyAccount.getId());
            }
        }

        return map;
    }

    @Override
    public RemoteResult setPhone(String mobile, String CustomerId) {
        AppCustomer appCustomer = appCustomerService.get(new QueryFilter(AppCustomer.class).addFilter("id=", CustomerId));
        // ??????????????????
        appCustomer.setPhoneState(1);
        appCustomerService.update(appCustomer);

        AppPersonInfo appPersonInfo = appPersonInfoService.get(new QueryFilter(AppPersonInfo.class).addFilter("customerId=", CustomerId));

        // ??????????????????
        appPersonInfoService.update(appPersonInfo);


        return new RemoteResult().setSuccess(true);
    }

    @Override
    public RemoteResult setPhone(String country, String mobile, Long customerId) {

        RemoteResult remoteResult = new RemoteResult();

        QueryFilter f = new QueryFilter(AppPersonInfo.class);
        f.addFilter("mobilePhone=", mobile);
        f.addFilter("country=", country);
        AppPersonInfo appPersonInfo = appPersonInfoService.get(f);
        if (appPersonInfo != null) {
            return remoteResult.setSuccess(false);
        } else {
            AppCustomer appCustomer = appCustomerService.get(customerId);
            // ???????????????????????????1
            appCustomer.setPhoneState(1);
            appCustomerService.update(appCustomer);

            // ??????appPersonInfo
            QueryFilter filter = new QueryFilter(AppPersonInfo.class);
            filter.addFilter("customerId=", customerId);
            AppPersonInfo info = appPersonInfoService.get(filter);
            // ?????????
            String oAccountNum = info.getMobilePhone();
            info.setCountry(country);
            info.setMobilePhone(mobile);
            appPersonInfoService.update(info);

            /******????????????????????????*******/
            BoundRecord record = new BoundRecord();
            record.setCustomerId(info.getCustomerId());
            record.setAccountNumType(1); // 1 ????????? 2 ??????
            record.setOAccountNum(oAccountNum);
            record.setNAccountNum(mobile);
            boundRecordService.save(record);

            //????????????
            //??????????????????????????????
            if ("true".equals(PropertiesUtils.APP.getProperty("app.blockbus"))) {
                String busNumber = appCustomer.getBusNumber();
                if(!StringUtils.isEmpty(busNumber)){
                    BusUpdateCustomerTO busUpdateCustomerTO = new BusUpdateCustomerTO();
                    busUpdateCustomerTO.setBusCustomerNumber(busNumber);
                    busUpdateCustomerTO.setMobile(mobile);
                    busUpdateCustomerTO.setCountry(country);
                    //????????????????????????
                    BusRequestUtil.updateCustomer(busUpdateCustomerTO);
                }
            }

            return remoteResult.setSuccess(true);
        }


    }

    @Override
    public List<String> getTredeArea() {
        QueryFilter qf = new QueryFilter(ExTradingArea.class);
        qf.addFilter("struts=", 1);
        qf.setOrderby("sort");
        List<ExTradingArea> list = exTradingAreaService.find(qf);
        List<String> returnList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ExTradingArea eta : list) {
                returnList.add(eta.getTradingArea());
            }
        }
        return returnList;
    }

    @Override
    public Boolean openChongbi(String coinCode){
        ExProduct exProduct = exProductService.get(new QueryFilter(ExProduct.class).addFilter("coinCode=",coinCode).addFilter("openChongbi=","1"));
        if(exProduct==null){
            return false;
        }
        return true;
    }

    @Override
    public RemoteResult identityVerification(String userName, String trueName, String sex, String suranme, String country, String cardType, String cardId, String[] pathImg, String type, String language) {

        AppCustomer appCustomer = appCustomerService.get(new QueryFilter(AppCustomer.class).addFilter("userName=", userName));

        if (appCustomer != null) {
            AppPersonInfo appPersonInfo = appPersonInfoService.getByCustomerId(appCustomer.getId());
            // ???????????????????????????
            AppPersonInfo appPersonInfo2 = appPersonInfoDao.getAppPersonInfoByCardId(cardId);
            if (appPersonInfo2 != null) {
                return new RemoteResult().setSuccess(false).setMsg("card_id_chongfu");
            }
            // ???X?????????X
            appPersonInfo.setCardId(cardId.replace("x", "X"));
            if (sex != null && sex.equals("???")) {
                appPersonInfo.setSex(0);
            } else if (sex != null && sex.equals("???")) {
                appPersonInfo.setSex(1);
            }

            appPersonInfo.setPapersType(type);
            appPersonInfo.setSurname(suranme);
            appPersonInfo.setTrueName(trueName);
            appPersonInfo.setCardType(Integer.valueOf(cardType));
            appPersonInfo.setPersonBank(pathImg[0]);
            appPersonInfo.setPersonCard(pathImg[1]);
            appPersonInfo.setFrontpersonCard(pathImg[2]);
            //appPersonInfoService.update(appPersonInfo);

            try {
                RemoteAppConfigService remoteAppConfigService = (RemoteAppConfigService) ContextUtil.getBean("remoteAppConfigService");
                String juhe_cardOpen = remoteAppConfigService.getValueByKey("juhe_cardOpen");
                String juhe_cardKey = remoteAppConfigService.getValueByKey("juhe_cardKey");
                String juhe_cardUrl = remoteAppConfigService.getValueByKey("juhe_cardUrl");
                // ????????????????????????,????????????????????????????????????????????????????????????
                if ("0".equals(cardType) && "0".equals(juhe_cardOpen)) {
						/*boolean checkCard = JuheSendUtils.auth(appPersonInfo.getTrueName(), appPersonInfo.getCardId(), juhe_cardUrl, juhe_cardKey);
						if (!checkCard) {
							return new RemoteResult().setMsg("failrealname");
						}*/
                    JsonResult jsonResult = HryRealNameUtil.juheConfigTest(appPersonInfo.getSurname() + appPersonInfo.getTrueName(), cardId);
                    if (!jsonResult.getSuccess()) {
                        return new RemoteResult().setMsg("failrealname");
                    }
                }

                /**
                 * ???ocr??? ????????????????????????
                 * ???????????????????????????????????????  ?????????????????????
                 */
                if(appPersonInfo.getCardType()==0){
                	appCustomer.setStates(2);
                    appCustomer.setIsReal(1);
                    //??????????????????????????????
                    if ("true".equals(PropertiesUtils.APP.getProperty("app.blockbus"))) {
                    	AppCustomer appc = appCustomerService.get(appCustomer.getId());
                    	String busNumber = "";
                    	if(appc!=null){
                    		busNumber = appc.getBusNumber();
                    	}
                        if(!StringUtils.isEmpty(busNumber)){
                            System.out.println("??????????????????=========");
                            BusUpdateCustomerTO busUpdateCustomerTO = new BusUpdateCustomerTO();
                            busUpdateCustomerTO.setBusCustomerNumber(busNumber);
                            busUpdateCustomerTO.setCustomerLocalNumber(appCustomer.getId().toString());
                            busUpdateCustomerTO.setIsReal(1);
                            busUpdateCustomerTO.setTrueName(appPersonInfo.getTrueName());
                            busUpdateCustomerTO.setSurName(appPersonInfo.getSurname());
                            busUpdateCustomerTO.setCardid(appPersonInfo.getCardId());
                            busUpdateCustomerTO.setCountry(appPersonInfo.getCountry());
                            //????????????????????????
                            BusJsonResult<BusUpdateCustomerTO> result= BusRequestUtil.updateCustomer(busUpdateCustomerTO);
                            System.out.println("????????????????????????========"+ JSON.toJSONString(result));
                        }
                    }
                }else{
                	appCustomer.setStates(1);
                }
                
                appCustomerService.update(appCustomer);
                appPersonInfoService.update(appPersonInfo);
                
                return new RemoteResult().setSuccess(true);
            } catch (Exception e) {
                return new RemoteResult().setMsg("card_id_chongfu");
            }
        }
        return new RemoteResult().setSuccess(false).setMsg("yishixianhuoshenhezhong");

    }

    @Override
    public RemoteResult offPhone(String mobile, String username) {
        AppCustomer appCustomer = appCustomerService.get(new QueryFilter(AppCustomer.class).addFilter("username=", username));
        // ?????????key
        // ??????????????????
        appCustomer.setPhoneState(0);
        appCustomer.setOpenPhone(1);
        appCustomer.setPassDate(new Date());
        appCustomerService.updateNull(appCustomer);
        return new RemoteResult().setSuccess(true);
    }

    @Override
    public RemoteResult setVailPassWord(String username, String oldPassWord) {

        AppCustomer appCustomer = appCustomerService.get(new QueryFilter(AppCustomer.class).addFilter("username=", username));
        if (appCustomer != null) {
            // ???????????????
            String passWord = appCustomer.getPassWord();

            PasswordHelper passwordHelper = new PasswordHelper();

            String encryString = passwordHelper.encryString(oldPassWord, appCustomer.getSalt());

            if (!passWord.equals(encryString)) {
                return new RemoteResult().setSuccess(false).setMsg("yuanshimimabuzhengq");
            }
            return new RemoteResult().setSuccess(true);

        } else {
            return new RemoteResult().setSuccess(false).setMsg("user_no_exist");
        }


    }


    @Override
    public RemoteResult regphone(String mobile) {
        // AppCustomer appCustomer = appCustomerService.get(new
        // QueryFilter(AppCustomer.class).addFilter("phone=", mobile));
        List<AppCustomer> appCustomer = appCustomerService.find(new QueryFilter(AppCustomer.class).addFilter("phone=", mobile));
        // appCustomerService.getByPhone(mobile)
        if (appCustomer.size() >= 1) {
            return new RemoteResult().setSuccess(false).setMsg("card_id_chongfu");
        } else {
            return new RemoteResult().setSuccess(true);

        }
    }

    @Override
    public void cancelCustAllExEntrust(EntrustTrade entrustTrade) {
        Boolean isPinging = exDmPingService.isPinging(entrustTrade.getCustomerId(), null, null, null);
        String[] rt = new String[2];
        if (isPinging) {

            rt[0] = CodeConstant.CODE_FAILED;
            rt[1] = "??????????????????????????????";
        } else {
            exEntrustService.cancelCustAllExEntrust(entrustTrade);
        }

    }

    /**
     * ????????????
     */
    @Override
    public RemoteResult updatepwdemail(String passwd, String email) {
        try {
            QueryFilter qf = new QueryFilter(AppPersonInfo.class);
            qf.addFilter("email=", email);
            AppPersonInfo appPersonInfo = appPersonInfoService.get(qf);
            if (appPersonInfo != null) {
                appPersonInfo.setEmailCode("");
                appPersonInfoService.update(appPersonInfo);
                AppCustomer appCustomer = appCustomerService.get(appPersonInfo.getCustomerId());
                PasswordHelper passwordHelper = new PasswordHelper();
                String encryString = passwordHelper.encryString(passwd, appCustomer.getSalt());
                appCustomer.setPassWord(encryString);
                appCustomerService.update(appCustomer);
                return new RemoteResult().setSuccess(true);
            }

        } catch (Exception e) {
            new RemoteResult().setSuccess(false);
        }
        return new RemoteResult().setSuccess(false);
    }

    @Override
    public RemoteResult forget(String email, String code) {
        // ???????????????AppPersonInfo
        QueryFilter qf = new QueryFilter(AppPersonInfo.class);
        qf.addFilter("email=", email);
        AppPersonInfo appPersonInfo = appPersonInfoService.get(qf);
        if (appPersonInfo == null) {
            return new RemoteResult().setSuccess(false);
        }
        appPersonInfo.setEmailCode(code);
        appPersonInfoService.update(appPersonInfo);
        return new RemoteResult().setSuccess(true).setObj(appPersonInfo.getEmailCode());
    }

    @Override
    public RemoteResult emailvail(String email, String code) {
        QueryFilter filter = new QueryFilter(AppPersonInfo.class);
        filter.addFilter("emailCode=", code);
        filter.addFilter("email=", email);
        AppPersonInfo appPersonInfo = appPersonInfoService.get(filter);
        if (appPersonInfo != null) {
            return new RemoteResult().setSuccess(true);
        } else {
            return new RemoteResult().setSuccess(false);
        }
    }

    @Override
    public BigDecimal myBtcCount(Long customerId, String code) {

        // TODO Auto-generated method stub
        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(customerId.toString());

        BigDecimal computeBTC = computeBTC(userRedis, code);
        return computeBTC;
    }

    @Override
    public List<Map<String, List<EntrustTrade>>> findExEntrustBycust(Long customerId) {
        QueryFilter queryFilter = new QueryFilter(ExCointoCoin.class);
        queryFilter.addFilter("state=", 1);
        List<ExCointoCoin> list = exCointoCoinService.find(queryFilter);
        List<Map<String, List<EntrustTrade>>> listml = new ArrayList<Map<String, List<EntrustTrade>>>();
        Map<String, List<EntrustTrade>> entrustedmap = new HashMap<String, List<EntrustTrade>>();
        Map<String, List<EntrustTrade>> entrustingmap = new HashMap<String, List<EntrustTrade>>();
        for (ExCointoCoin ec : list) {
            Map<String, Object> maping = new HashMap<String, Object>();
            maping.put("customerId", customerId);
            maping.put("coinCode", ec.getCoinCode());
            maping.put("fixPriceCoinCode", ec.getFixPriceCoinCode());
            maping.put("counting", EntrustByUser.ingMAXsize);
            maping.put("isType", 0);
            List<ExEntrust> listing = exEntrustDao.getExIngBycustomerId(maping);
            String listings = Mapper.objectToJson(listing);
            List<EntrustTrade> entrustTradinglist = JSON.parseArray(listings, EntrustTrade.class);
            entrustingmap.put(ec.getCoinCode() + "_" + ec.getFixPriceCoinCode(), entrustTradinglist);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("customerId", customerId);
            map.put("coinCode", ec.getCoinCode());
            map.put("fixPriceCoinCode", ec.getFixPriceCoinCode());
            map.put("counted", EntrustByUser.edMAXsize);
            map.put("isType", 0);
            List<ExEntrust> listied = exEntrustDao.getExEdBycustomerId(map);
            String listeds = Mapper.objectToJson(listied);
            List<EntrustTrade> entrustTradedlist = JSON.parseArray(listeds, EntrustTrade.class);
            System.out.println("?????????????????????:"+entrustTradedlist.size());
            entrustedmap.put(ec.getCoinCode() + "_" + ec.getFixPriceCoinCode(), entrustTradedlist);

        }
        listml.add(entrustedmap);
        listml.add(entrustingmap);
        return listml;
    }

    @Override
    public RemoteResult selectCommend(String username, String property) {
        // TODO Auto-generated method stub
        RemoteResult RemoteResult = new RemoteResult();
        QueryFilter qf = new QueryFilter(AppCustomer.class);
        qf.addFilter("userName=", username);
        AppCustomer appcustomer = appCustomerService.get(qf);
        commendCode ac = new commendCode();
        ac.setCommendCode(appcustomer.getReferralCode());
        // ac.setCommendLink(property + "/regcode/" + appcustomer.getReferralCode());
       // ac.setCommendLink(property + "/reg?referrCode=" + appcustomer.getReferralCode());
       // ac.setCommendLink(property + "?extensionCode=" + appcustomer.getReferralCode());
        ac.setCommendLink(property + "#/?extensionCode=" + appcustomer.getReferralCode());
        ac.setUserName(appcustomer.getUserName());

//		int commendCount = appCustomerService.commendCount(appcustomer.getReferralCode());
        // appAgentsCustromerService.findAgentsForMoneyToList(appcustomer.getUserName());
//		ac.setCommendCount(commendCount);
        // ac.setCommendAmount(agentsForMoney.getSurplusMoney());
        // ac.setCommendNoAmount(agentsForMoney.getDeawalMoney());

        return RemoteResult.setSuccess(true).setObj(ac);
    }

    /*
     * @Override public List<commendCode> selectCommendfind(String username) {
     *
     * List<commendCode> list = new ArrayList<commendCode>(); List<AgentsForMoney>
     * lists = appAgentsCustromerService.findAgentsForMoneyToListOne(username);
     * for(AgentsForMoney agentsForMoney :lists){ commendCode commendCode=new
     * commendCode(); // map.put("key1",agentsForMoney.getDeawalMoney());
     * commendCode.setDeawalMoney(agentsForMoney.getDeawalMoney());
     * commendCode.setFixPriceCoinCode(agentsForMoney.getFixPriceCoinCode());
     * commendCode.setSurplusMoney(agentsForMoney.getSurplusMoney());
     * list.add(commendCode); } return list;
     *
     * }
     */

    @Override
    public List<CommendMoney> selectCommendfind(Long customerId) {

        QueryFilter appCommendMoneyQueryFilter = new QueryFilter(AppCommendMoney.class);
        appCommendMoneyQueryFilter.addFilter("custromerId=", customerId);
        List<AppCommendMoney> appCommendMoneylist = appCommendMoneyService.find(appCommendMoneyQueryFilter);
        List<CommendMoney> list = new ArrayList<CommendMoney>();
        for (AppCommendMoney cm : appCommendMoneylist) {

            CommendMoney commendMoney = new CommendMoney();
            commendMoney.setCoinCode(cm.getCoinCode());
            commendMoney.setCommendLater(cm.getCommendLater());
            commendMoney.setCommendOne(cm.getCommendOne());
            commendMoney.setCommendTwo(cm.getCommendTwo());
            commendMoney.setCommendThree(cm.getCommendThree());
            commendMoney.setCustromerId(cm.getCustromerId());
            commendMoney.setCustromerName(cm.getCustromerName());
            commendMoney.setLastPaidTime(cm.getLastPaidTime());
            commendMoney.setMoneyNum(cm.getMoneyNum());
            commendMoney.setPaidMoney(cm.getPaidMoney());
            commendMoney.setUserCode(cm.getUserCode());
            list.add(commendMoney);
        }

        return list;

    }
    
    
    

    int lent = 1;
    // ??????????????????sgh
    List<AppCommendUser> deptVosList2 = new ArrayList<AppCommendUser>();

    public List<AppCommendUser> findNumber(Long uid, String username, int num) {
        num++;
        AppCommendUser deptVo2 = new AppCommendUser();
        List<AppCommendUser> find2 = null;
        String coinCode = null;
        if (num != 1) {
            QueryFilter AppCommendUser = new QueryFilter(AppCommendUser.class);
            AppCommendUser.addFilter("pid=", uid);
            find2 = appCommendUserService.find(AppCommendUser);
        } else if (num == 1) {
            deptVosList2.clear();
            QueryFilter AppCommendUser = new QueryFilter(AppCommendUser.class);
            AppCommendUser.addFilter("uid=", uid);
            find2 = appCommendUserService.find(AppCommendUser);
        }

        BigDecimal moneyNum = new BigDecimal("0");
        if (find2.size() > 0) {
            for (AppCommendUser deptVo : find2) {
                deptVo2 = new AppCommendUser();
                BigDecimal moneyNumone = new BigDecimal("0");
                // ??????????????????USDT
                List<AppCommendTrade> selectCommendTrade = appCommendTradeService.selectCommendTrade(deptVo.getUsername());
                for (AppCommendTrade appCommendTrade : selectCommendTrade) {
//					coinCode = appCommendTrade.getFixPriceCoinCode() + "_" + "USDT";
//					String price = newTransactionPrice(coinCode);
//					logger.error(coinCode + "?????????" + price);
//					BigDecimal money = appCommendTrade.getRewardmoney().multiply(new BigDecimal(price));
//					moneyNumone = moneyNum.add(new BigDecimal(money.toString()));
                }
                // ????????????
                int count = 0;
                if (deptVo.getUsername() != null && !"".equals(deptVo.getUsername())) {
                    count = exOrderInfoService.selectTransactionCount(deptVo.getUsername());
                    logger.error("????????????" + count);
                }
                // ???????????????????????????USDT
                List<ExOrderInfo> selectTransaction = exOrderInfoService.selectTransaction(deptVo.getUsername());
                if (selectTransaction.size() > 0) {
                    for (ExOrderInfo exOrderInfo : selectTransaction) {
                        coinCode = exOrderInfo.getCoinCode() + "_" + exOrderInfo.getFixPriceCoinCode();
                        String price = newTransactionPrice(coinCode);
                        BigDecimal money = exOrderInfo.getTransactionSum().multiply(new BigDecimal(price));
                        moneyNum = moneyNum.add(new BigDecimal(money.toString()));
                    }
                }
                deptVo2.setComLeven(num);
                deptVo2.setMoneyNum(moneyNum);
                deptVo2.setUsername(deptVo.getUsername());
                deptVo2.setCommendMoney(moneyNumone);
                deptVo2.setExorderCount(count);
                deptVo2.setCoinCode(coinCode);
                deptVo2.setUid(deptVo.getUid());
                deptVosList2.add(deptVo2);
                findNumber(deptVo2.getUid(), username, num);
            }
        }
        return deptVosList2;

    }

    @Override
    public List<commendCode> selectCommendRanking() {
        List<commendCode> deptVosList = new ArrayList<commendCode>();
        List<AppCommendRank> findAll = appCommendRankService.findAll();
        RedisService redisService = (RedisService) ContextUtil.getBean("redisService");
        String config = redisService.get("configCache:all");
        JSONObject parseObject = JSONObject.parseObject(config);
        Integer RankingNum = Integer.valueOf(parseObject.get("RankingNum").toString());
        if (RankingNum > findAll.size()) {
            RankingNum = findAll.size();
        }
        for (int i = 0; i < RankingNum; i++) {
            commendCode commendCode = new commendCode();
            commendCode.setName(findAll.get(i).getUserName());
            commendCode.setFixPriceCoinCode(findAll.get(i).getFixMoney() + "" + findAll.get(i).getFixCoin());
            deptVosList.add(commendCode);
        }
        return deptVosList;

    }

    public String newTransactionPrice(String coinCode) {

        RedisService redisService = (RedisService) ContextUtil.getBean("redisService");
        String price = redisService.get(coinCode + ":currentExchangPrice");
        if (price == null || "".equals(price)) {
            price = "1";
        }

        return price;

    }

    @Override
    public List<C2cOrder> c2cNewBuyOrder() {

        QueryFilter filter = new QueryFilter(C2cTransaction.class);
        filter.addFilter("transactionType=", 1);
        filter.addFilter("status=", 2);
        filter.setOrderby("id desc");
        filter.setPage(1);
        filter.setPageSize(10);
        Page<C2cTransaction> findPage = c2cTransactionService.findPage(filter);
        List<C2cTransaction> list = findPage.getResult();

        ArrayList<C2cOrder> arrayList = new ArrayList<C2cOrder>();
        if (list != null && list.size() > 0) {
            for (C2cTransaction c2cTransaction : list) {
                Long customerId = c2cTransaction.getCustomerId();
                QueryFilter afc = new QueryFilter(AppPersonInfo.class);
                afc.addFilter("customerId=", customerId);
                AppPersonInfo appPersonInfo = appPersonInfoService.get(afc);
                if (appPersonInfo != null) {
                    String name = StringUtils.isEmpty(appPersonInfo.getEmail()) ? appPersonInfo.getMobilePhone() : appPersonInfo.getEmail();
                    C2cOrder c2cOrder = new C2cOrder();
                    c2cOrder.setTransactionTime(c2cTransaction.getCreated());
                    c2cOrder.setCreateTime(new SimpleDateFormat("YYYY:MM:DD HH:mm:ss").format(c2cTransaction.getCreated()));
                    c2cOrder.setCoinCode(c2cTransaction.getCoinCode());
                    c2cOrder.setTransactionNum(c2cTransaction.getTransactionNum());
                    c2cOrder.setTransactionPrice(c2cTransaction.getTransactionPrice());
                    c2cOrder.setTransactionCount(c2cTransaction.getTransactionCount());
                    c2cOrder.setTransactionMoney(c2cTransaction.getTransactionMoney());
                    c2cOrder.setTransactionType(c2cTransaction.getTransactionType());
                    c2cOrder.setStatus(c2cTransaction.getStatus());
                    c2cOrder.setUserName(name.substring(0, 2) + "****" + name.substring(name.length() - 3, name.length()));
                    arrayList.add(c2cOrder);
                }
            }
        }

        return arrayList;

    }

    @Override
    public Map<String, Object> selectRechargeCoin(String username, String coinCode) {
        Map<String, Object> map = new HashMap<String, Object>();

        QueryFilter qf = new QueryFilter(AppCustomer.class);
        qf.addFilter("userName=", username);
        AppCustomer appCustomer = appCustomerService.get(qf);
        if (appCustomer != null) {
            map.put("customerId", appCustomer.getId());

            QueryFilter qf1 = new QueryFilter(ExDigitalmoneyAccount.class);
            qf1.addFilter("coinCode=", coinCode);
            qf1.addFilter("customerId=", appCustomer.getId());
            ExDigitalmoneyAccount exDigitalmoneyAccount = exDigitalmoneyAccountService.get(qf1);
            if (exDigitalmoneyAccount != null) {
                map.put("accountId", exDigitalmoneyAccount.getId());
            }
        }
        return map;
    }

    @Override
    public List<C2cOrder> c2cNewSellOrder() {

        QueryFilter filter = new QueryFilter(C2cTransaction.class);
        filter.addFilter("transactionType=", 2);
        filter.addFilter("status=", 2);
        filter.setOrderby("id desc");
        filter.setPage(1);
        filter.setPageSize(10);
        Page<C2cTransaction> findPage = c2cTransactionService.findPage(filter);
        List<C2cTransaction> list = findPage.getResult();

        ArrayList<C2cOrder> arrayList = new ArrayList<C2cOrder>();
        if (list != null && list.size() > 0) {
            for (C2cTransaction c2cTransaction : list) {
                Long customerId = c2cTransaction.getCustomerId();
                QueryFilter afc = new QueryFilter(AppPersonInfo.class);
                afc.addFilter("customerId=", customerId);
                AppPersonInfo appPersonInfo = appPersonInfoService.get(afc);
                String name = StringUtils.isEmpty(appPersonInfo.getEmail()) ? appPersonInfo.getMobilePhone() : appPersonInfo.getEmail();
                C2cOrder c2cOrder = new C2cOrder();
                c2cOrder.setTransactionTime(c2cTransaction.getCreated());
                c2cOrder.setCreateTime(new SimpleDateFormat("YYYY:MM:DD HH:mm:ss").format(c2cTransaction.getCreated()));
                c2cOrder.setCoinCode(c2cTransaction.getCoinCode());
                c2cOrder.setTransactionNum(c2cTransaction.getTransactionNum());
                c2cOrder.setTransactionPrice(c2cTransaction.getTransactionPrice());
                c2cOrder.setTransactionCount(c2cTransaction.getTransactionCount());
                c2cOrder.setTransactionMoney(c2cTransaction.getTransactionMoney());
                c2cOrder.setTransactionType(c2cTransaction.getTransactionType());
                c2cOrder.setStatus(c2cTransaction.getStatus());
                c2cOrder.setUserName(name.substring(0, 2) + "****" + name.substring(name.length() - 3, name.length()));
                arrayList.add(c2cOrder);
            }
        }

        return arrayList;

    }

    @Override
    public User selectByCustomerId(Long customerId) {
        QueryFilter qf = new QueryFilter(AppCustomer.class);
        qf.addFilter("id=", customerId);
        AppCustomer appCustomer = appCustomerService.get(qf);
        User user = new User();
        if (appCustomer != null) {
            QueryFilter qfw = new QueryFilter(AppPersonInfo.class);
            qfw.addFilter("customerId=", appCustomer.getId());
            AppPersonInfo apppersonInfo = appPersonInfoService.get(qfw);

            // ?????????
            user.setMobile(apppersonInfo.getMobilePhone());
            // ??????
            user.setCountry(apppersonInfo.getCountry());
            // ????????????
            user.setAccountPassWord(appCustomer.getAccountPassWord());
            // ?????????
            user.setUsername(appCustomer.getUserName());
            user.setCardcode(apppersonInfo.getCardId());
            user.setUserCode(appCustomer.getUserCode());

            user.setIsReal(appCustomer.getIsReal() == null ? 0 : appCustomer.getIsReal().intValue());
            user.setIsDelete(appCustomer.getIsDelete());
            user.setIsChange(appCustomer.getIsChange());
            user.setCustomerId(appCustomer.getId());

            user.setTruename(apppersonInfo.getTrueName());
            user.setSurname(apppersonInfo.getSurname());
            user.setIsLock(appCustomer.getIsLock());
            user.setCustomerType(apppersonInfo.getCustomerType());
            user.setSalt(appCustomer.getSalt());
            user.setGoogleKey(appCustomer.getGoogleKey());
            // google??????
            user.setGoogleState(appCustomer.getGoogleState());
            // ????????????
            user.setPhoneState(appCustomer.getPhoneState());
            user.setIsOpenCoinFee(apppersonInfo.getIsOpenCoinFee());
            user.setMessIp(appCustomer.getMessIp());
            user.setPassDate(appCustomer.getPassDate());

            user.setStates(appCustomer.getStates());
            user.setPassword(appCustomer.getPassWord());
            user.setSafeTixianType(appCustomer.getSafeTixianType());
            user.setEmail(apppersonInfo.getEmail());
        }
        return user;
    }

    @Override
    public boolean setc2cTransactionStatus2(String transactionNum, int status2, String remark) {
        QueryFilter filter = new QueryFilter(C2cTransaction.class);
        filter.addFilter("transactionNum=", transactionNum);
        C2cTransaction c2cTransaction = c2cTransactionService.get(filter);

        // ???????????????????????????
        if (c2cTransaction != null) {

            if (c2cTransaction.getStatus() != 2) {
                c2cTransaction.setStatus2(status2);

                if (!StringUtils.isEmpty(remark)) {
                    c2cTransaction.setRemark(remark);
                }

                // ???????????????????????????????????????,????????????????????????
                if (status2 == 3 || status2 == 4) {
                    c2cTransaction.setStatus(3);
                    ;

                    // ???????????????????????????,??????????????????
                    if (status2 == 4 && c2cTransaction.getTransactionType() == 2) {
                        c2cTransactionService.close(c2cTransaction.getId());
                    }

                }
                c2cTransactionService.update(c2cTransaction);
            }
            return true;

        }
        return false;
    }

    @Override
    public boolean setc2cTransactionStatus(String transactionNum, int status2, String remark) {
        QueryFilter filter = new QueryFilter(C2cTransaction.class);
        filter.addFilter("transactionNum=", transactionNum);
        filter.addFilter("status2=", 2);
        C2cTransaction c2cTransaction = c2cTransactionService.get(filter);

        // ???????????????????????????
        if (c2cTransaction != null) {

            if (c2cTransaction.getStatus() != 2) {
                c2cTransaction.setStatus(status2);

                if (!StringUtils.isEmpty(remark)) {
                    c2cTransaction.setRemark(remark);
                }

                // ???????????????????????????????????????,????????????????????????
                if (status2 == 3 || status2 == 4) {
                    c2cTransaction.setStatus(3);
                    ;

                    // ???????????????????????????,??????????????????
                    if (status2 == 4 && c2cTransaction.getTransactionType() == 2) {
                        c2cTransactionService.close(c2cTransaction.getId());
                    }

                }
                c2cTransactionService.update(c2cTransaction);
            }
            return true;

        }
        return false;
    }

    @Override
    public String[] checkPing(Long customerId) {
        return exDmPingService.checkPing(customerId);
    }

    @Override
    public FrontPage c2clist(Map<String, String> params) {
        return c2cTransactionService.c2clist(params);
    }

    @Override
    public boolean insertMail(Mail mail) {
        hry.web.mail.model.Mail mail1 = ObjectUtil.bean2bean(mail, hry.web.mail.model.Mail.class);
        mailService.save(mail1);
        return false;
    }

    @Override
    public List<String> findOpenTibi() {
        QueryFilter filter = new QueryFilter(ExProduct.class);
        filter.addFilter("openTibi=", "1");
        List<ExProduct> list = exProductService.find(filter);

        List<String> coinList = new ArrayList<String>();
        if (list != null) {
            for (ExProduct exProduct : list) {
                coinList.add(exProduct.getCoinCode());
            }
            return coinList;
        }
        return Collections.emptyList();

    }

    @Override
    @Transactional
    public RemoteResult registMobile(String mobile, String password, String referralCode, String country, String language) {

        try {

            if (redisService.lock(mobile + "registMobile")) {
                // ?????????????????????????????????
                QueryFilter f = new QueryFilter(AppPersonInfo.class);
                f.addFilter("mobilePhone=", mobile);// ?????????
                f.addFilter("country=", country);// ??????
                AppPersonInfo _appPersonInfo = appPersonInfoService.get(f);
                if (_appPersonInfo != null) {
                    return new RemoteResult().setMsg("user_reg");
                }


                //????????????
                String salt = UUIDUtil.getUUID();
                //????????????
                PasswordHelper passwordHelper = new PasswordHelper();
                String encryPassword = passwordHelper.encryString(password, salt);

                //-------?????????????????????-----------??????
                String busCustomerNumber = "";
                if ("true".equals(PropertiesUtils.APP.getProperty("app.blockbus"))) {
                    BusJsonResult busResult = BusRequestUtil.regist4Mobile(country, mobile, encryPassword, salt);
                    if (null == busResult) {
                        //????????????
                        return new RemoteResult().setMsg("error");
                    }
                    if (!busResult.getSuccess()) {
                        //????????????
                        if (BusApiCode.code_1001.equals(busResult.getCode())) {
                            return new RemoteResult().setMsg("user_reg");
                        }
                        return new RemoteResult().setMsg(busResult.getMsg());
                    }
                    //????????????id
                    busCustomerNumber = (String) busResult.getObj();
                }
                //--------?????????????????????-----------??????


                AppCustomer customer = new AppCustomer();
                //??????????????????
                customer.setBusNumber(busCustomerNumber);

                String uuid = UUIDUtil.getUUID();
                // ?????????????????????ID
                customer.setUserName(uuid);
                // ??????????????????ID
                customer.setUserCode(uuid);
                // ??????????????????0
                customer.setGoogleState(0);
                // ????????????????????????1
                customer.setPhoneState(1);
                // ?????????????????????
                customer.setHasEmail(0);
                // ???????????????
                customer.setStates(0);
                // ???????????????
                customer.setIsReal(0);
                // ??????????????????id
                customer.setUserCode(UUIDUtil.getUUID());
                //??????????????????
                customer.setCommonLanguage(language);
                // ????????????????????????
                customer.setReferralCode(MathUtil.generateShortUuid());

                // ????????????????????????
                if (referralCode != null) {
                    if (referralCode.startsWith("AGENT") && referralCode.length() > 10) { //?????????????????????
                        customer.setAgentCommendCode(referralCode);
                    } else {
                        customer.setCommendCode(referralCode);
                    }

                }
                // ????????????
                customer.setSalt(salt);
                // ????????????
                customer.setPassWord(encryPassword);
                // ??????appCustomer
                appCustomerService.save(customer);

                if ("true".equals(PropertiesUtils.APP.getProperty("app.blockbus"))) {
                    BusUpdateCustomerTO busUpdateCustomerTO = new BusUpdateCustomerTO();
                    busUpdateCustomerTO.setBusCustomerNumber(busCustomerNumber);
                    busUpdateCustomerTO.setCustomerLocalNumber(customer.getId().toString());
                    busUpdateCustomerTO.setReferralCode(customer.getReferralCode());//??????????????????
                    busUpdateCustomerTO.setInviteCode(referralCode);//?????????????????????
                    //????????????????????????
                    BusRequestUtil.updateCustomer(busUpdateCustomerTO);
                }


                // ???????????????AppPersonInfo
                AppPersonInfo appPersonInfo = new AppPersonInfo();
                // ????????????
                appPersonInfo.setCountry(country);
                // ??????????????????
                appPersonInfo.setCustomerSource(0);
                // ???????????????
                appPersonInfo.setMobilePhone(mobile);
                // ??????customerId
                appPersonInfo.setCustomerId(customer.getId());
                // ??????????????????
                appPersonInfo.setCustomerType(1);
                // ??????appPersonInfo
                appPersonInfoService.save(appPersonInfo);

                // ?????????????????????
                RemoteAppAccountService remoteAppAccountService = (RemoteAppAccountService) ContextUtil.getBean("remoteAppAccountService");
                remoteAppAccountService.openAccount(customer, appPersonInfo, language, ContextUtil.getWebsite());

                // ?????????????????????
                RemoteExProductService remoteExProductService = (RemoteExProductService) ContextUtil.getBean("remoteExProductService");
                remoteExProductService.openDmAccount(customer, appPersonInfo, null, ContextUtil.getWebsite(), language);

                // ???????????????
                appCommendUserService.saveObj(customer);
                addUserLevel(customer);                               //??????????????????
                appMessageService.sysSendMsg(customer, MsgTypeEnum.REGIST);
                // ????????????
                redisService.unLock(mobile + "registMobile");

                return new RemoteResult().setSuccess(true);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RemoteResult().setSuccess(false).setMsg("error");

    }

    @Override
    public RemoteResult canTakeMoney(String customerId) {
        RemoteResult jsonResult = new RemoteResult();
        // ??????redis??????
        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(customerId);

        // ?????????????????????????????????????????????
        AppAccountRedis accountRedis = UserRedisUtils.getAccount(userRedis.getAccountId().toString());
        if (null != accountRedis) {
            if (accountRedis.getHotMoney().compareTo(new BigDecimal("0")) < 0 || accountRedis.getColdMoney().compareTo(new BigDecimal("0")) < 0) {
                jsonResult.setSuccess(false);
                jsonResult.setMsg(accountRedis.getId() + ":??????" + accountRedis.getHotMoney() + "-??????" + accountRedis.getColdMoney() + "????????????????????????");
                return jsonResult;
            }
        }
        // ???????????????
        Map<String, Long> dmAccountId = userRedis.getDmAccountId();
        for (Map.Entry<String, Long> entry : dmAccountId.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                ExDigitalmoneyAccountRedis ear = UserRedisUtils.getAccount(entry.getValue().toString(), entry.getKey());
                ProductCommon productCommon = productCommonService.getProductCommon(ear.getCoinCode());
                if (productCommon != null && null != productCommon.getKeepDecimalForCoin()) {
                    if (ear.getHotMoney().setScale(productCommon.getKeepDecimalForCoin(), BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal("0")) < 0 || ear.getColdMoney().setScale(productCommon.getKeepDecimalForCoin(), BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal("0")) < 0) {
                        jsonResult.setSuccess(false);
                        jsonResult.setMsg(ear.getId() + ":" + ear.getCoinCode() + "-??????" + ear.getHotMoney() + "-??????" + ear.getColdMoney() + "????????????");
                        return jsonResult;
                    }
                } else {
                    if (ear.getHotMoney().compareTo(new BigDecimal("0")) < 0 || ear.getColdMoney().compareTo(new BigDecimal("0")) < 0) {
                        jsonResult.setSuccess(false);
                        jsonResult.setMsg(ear.getId() + ":" + ear.getCoinCode() + "-??????" + ear.getHotMoney() + "-??????" + ear.getColdMoney() + "????????????");
                        return jsonResult;
                    }
                }
            }
        }

        return jsonResult.setSuccess(true);
    }


    @Override
    public boolean hasMobile(String country, String mobile, String ip) {
        try {
            QueryFilter filter = new QueryFilter(AppPersonInfo.class);
            filter.addFilter("country=", country);
            filter.addFilter("mobilePhone=", mobile);
            AppPersonInfo appPersonInfo = appPersonInfoService.get(filter);
            if (appPersonInfo != null) {
                // ????????????????????????????????????????????????????????????????????????
                //addWhiteList(appPersonInfo.getCustomerId(), ip);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User getUserByUserCode(String userCode) {

        QueryFilter qf = new QueryFilter(AppCustomer.class);
        qf.addFilter("userCode=", userCode);
        AppCustomer appCustomer = appCustomerService.get(qf);
        User user = new User();
        if (appCustomer != null) {
            QueryFilter qfw = new QueryFilter(AppPersonInfo.class);
            qfw.addFilter("customerId=", appCustomer.getId());
            AppPersonInfo apppersonInfo = appPersonInfoService.get(qfw);
            user = createUser(appCustomer, apppersonInfo);
        }
        return user;

    }

    @Override
    public User getUserInfoByUsername(String username) {
        User user = new User();
        if (!StringUtils.isEmpty(username)) {
            QueryFilter qf = new QueryFilter(AppCustomer.class);
            qf.addFilter("userName=", username);
            AppCustomer appCustomer = appCustomerService.get(qf);
            if (appCustomer != null) {
                QueryFilter qfw = new QueryFilter(AppPersonInfo.class);
                qfw.addFilter("customerId=", appCustomer.getId());
                AppPersonInfo apppersonInfo = appPersonInfoService.get(qfw);
                user = createUser(appCustomer, apppersonInfo);
            }
        }
        return user;
    }

    @Override
    public boolean hasEmail(String email) {
        try {
            QueryFilter filter = new QueryFilter(AppPersonInfo.class);
            filter.addFilter("email=", email);
            AppPersonInfo appPersonInfo = appPersonInfoService.get(filter);

            if (appPersonInfo != null) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;

    }

    @Override
    public boolean isActive(String email) {
        try {
            QueryFilter filter = new QueryFilter(AppPersonInfo.class);
            filter.addFilter("email=", email);
            AppPersonInfo appPersonInfo = appPersonInfoService.get(filter);
            if (appPersonInfo != null) {
                AppCustomer appCustomer = appCustomerService.get(appPersonInfo.getCustomerId());
                if (appCustomer.getHasEmail().intValue() == 1) {
                    return true;
                }
            }


        } catch (Exception e) {
        }
        return false;

    }

    @Override
    public RemoteResult setEmail(String email, Long customerId, String loginPwd) {

        RemoteResult remoteResult = new RemoteResult();

        QueryFilter f = new QueryFilter(AppPersonInfo.class);
        f.addFilter("email=", email);
        AppPersonInfo appPersonInfo = appPersonInfoService.get(f);
        if (appPersonInfo != null) {
            return remoteResult.setSuccess(false).setMsg("gaiyouxiangyijingbeishiyong");
        } else {
            PasswordHelper passwordHelper = new PasswordHelper();
            AppCustomer appCustomer = appCustomerService.get(customerId);
            if (!appCustomer.getPassWord().equals(passwordHelper.encryString(loginPwd, appCustomer.getSalt()))) {
                return remoteResult.setSuccess(false).setMsg("mimacuowu");
            }

            // ??????appPersonInfo
            QueryFilter filter = new QueryFilter(AppPersonInfo.class);
            filter.addFilter("customerId=", customerId);
            AppPersonInfo info = appPersonInfoService.get(filter);
            // ?????????
            String oAccountNum = info.getEmail();
            info.setEmail(email);
            appPersonInfoService.update(info);

            //????????????????????????
            appCustomer.setHasEmail(1);
            appCustomerService.update(appCustomer);

            /******????????????????????????*******/
            BoundRecord record = new BoundRecord();
            record.setCustomerId(info.getCustomerId());
            record.setAccountNumType(2); // 1 ????????? 2 ??????
            record.setOAccountNum(oAccountNum);
            record.setNAccountNum(email);
            boundRecordService.save(record);

            //????????????
            //??????????????????????????????
            if ("true".equals(PropertiesUtils.APP.getProperty("app.blockbus"))) {
                String busNumber = appCustomer.getBusNumber();
                if(!StringUtils.isEmpty(busNumber)){
                    BusUpdateCustomerTO busUpdateCustomerTO = new BusUpdateCustomerTO();
                    busUpdateCustomerTO.setBusCustomerNumber(busNumber);
                    busUpdateCustomerTO.setEmail(email);
                    //????????????????????????
                    BusRequestUtil.updateCustomer(busUpdateCustomerTO);
                }
            }

            return remoteResult.setSuccess(true);
        }

    }

    @Override
    public RemoteResult updatepwdMobile(String country, String mobile, String newPassWord) {

        try {
            QueryFilter qf = new QueryFilter(AppPersonInfo.class);
            qf.addFilter("country=", country);
            qf.addFilter("mobilePhone=", mobile);
            AppPersonInfo appPersonInfo = appPersonInfoService.get(qf);
            if (appPersonInfo != null) {
                AppCustomer appCustomer = appCustomerService.get(appPersonInfo.getCustomerId());
                PasswordHelper passwordHelper = new PasswordHelper();
                String encryString = passwordHelper.encryString(newPassWord, appCustomer.getSalt());
                appCustomer.setPassWord(encryString);
                appCustomerService.update(appCustomer);
                return new RemoteResult().setSuccess(true);
            }

        } catch (Exception e) {
            new RemoteResult().setSuccess(false);
        }
        return new RemoteResult().setSuccess(false);
    }

    @Override
    public List<CommendUser> culCommendCount(Map<String, String> map) {
        List<CommendUser> listcu = new ArrayList<CommendUser>();
        List<AppCommendUser> list = appCommendUserService.culCommendCount(map);
        for (AppCommendUser acu : list) {
            CommendUser commendUser = new CommendUser();
            commendUser.setLaterNumber(acu.getLaterNumber());
            commendUser.setOneNumber(acu.getOneNumber());
            commendUser.setTwoNumber(acu.getTwoNumber());
            commendUser.setThreeNumber(acu.getThreeNumber());
            commendUser.setUsername(acu.getUsername());
            listcu.add(commendUser);
        }
        return listcu;
    }

    @Override
    public User getCustomerById(Long id) {
        AppCustomer app = appCustomerService.get(id);
        if (app != null) {
            User u = new User();
            u.setGoogleState(app.getGoogleState());
            u.setPhoneState(app.getPhoneState());
            return u;
        }
        return null;
    }

    @Override
    public RemoteResult insertLoginSafe(Long id, String ip, String browserName, String userName) {
        try {
            //AppCustomer app = appCustomerService.get(id);
            AppPersonInfo appPersonInfo = appPersonInfoService.get(new QueryFilter(AppPersonInfo.class).addFilter("customerId=", id));
            if (appPersonInfo != null) {
                LoginSafe l1 = new LoginSafe();
                l1.setCustomerId(id);
                l1.setUserName(userName);
                l1.setContent(ip);
                l1.setType(0);
                loginSafeService.save(l1);

                LoginSafe l2 = new LoginSafe();
                l2.setCustomerId(id);
                l2.setUserName(userName);
                l2.setContent(browserName);
                l2.setType(1);
                loginSafeService.save(l2);

                return new RemoteResult().setSuccess(true);
            }
            return new RemoteResult().setSuccess(false);
        } catch (Exception e) {
            return new RemoteResult().setSuccess(false);
        }
    }

    @Override
    public RemoteResult setSecurity(String type, Integer valueOf, Long customerId) {

        AppCustomer appCustomer = appCustomerService.get(customerId);
        if ("login".equals(type)) {//??????????????????
            appCustomer.setSafeLoginType(valueOf);
        }
        if ("tixian".equals(type)) {
            appCustomer.setSafeTixianType(valueOf);
        }
        if ("trade".equals(type)) {
            appCustomer.setSafeTradeType(valueOf);
        }

        appCustomerService.update(appCustomer);

        return new RemoteResult().setSuccess(true);
    }

    @Override
    public FrontPage findConmmendForntPageBySql(Map<String, String> params) throws Exception {
        return appCommendUserService.findConmmendForntPageBySql(params);
    }

    @Override
    public BigDecimal getHotMoney(String customerId, String coinCode) {
        // ??????redis??????
        RedisUtil<UserRedis> redisUtil = new RedisUtil<UserRedis>(UserRedis.class);
        UserRedis userRedis = redisUtil.get(customerId);

        // ???????????????
        Map<String, Long> dmAccountId = userRedis.getDmAccountId();
        for (Map.Entry<String, Long> entry : dmAccountId.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null && entry.getKey().equals(coinCode)) {
                ExDigitalmoneyAccountRedis ear = UserRedisUtils.getAccount(entry.getValue().toString(), entry.getKey());
                return ear.getHotMoney();
            }
        }

        return new BigDecimal("0");
    }

    @Override
    public void addWhiteList(Long id, String ip) {
        AppCustomer appCustomer = appCustomerService.get(id);
        AppPersonInfo appPersonInfo = appPersonInfoService.get(new QueryFilter(AppPersonInfo.class).addFilter("customerId=", id));

        WhiteList whiteList = new WhiteList();
        whiteList.setUserId(appPersonInfo.getCustomerId());
        whiteList.setUserName(appCustomer.getUserName());
        whiteList.setTel(appPersonInfo.getMobilePhone());
        whiteList.setEmail(appPersonInfo.getEmail());
        String userName = appPersonInfo.getSurname() != null ? appPersonInfo.getSurname() + (appPersonInfo.getTrueName() != null ? appPersonInfo.getTrueName() : "") : "";
        whiteList.setTrueName(userName);
        whiteList.setIp(ip);
        whiteList.setLoginNum(0L);
        whiteList.setType(2);
        whiteList.setCreated(new Date());
        whiteList.setLoginLast(new Date());
        whiteListService.save(whiteList);

        appCustomer.setIsBlacklist(2);
        appCustomerService.update(appCustomer);
    }

    @Override
    public void updateWriteList(Long id) {
        AppCustomer appCustomer = appCustomerService.get(id);
        WhiteList whiteList = whiteListService.get(new QueryFilter(WhiteList.class).addFilter("userId=", appCustomer.getId()));
        if (whiteList != null && appCustomer.getIsBlacklist() == 2) {//????????????????????????IsBlacklist???2??????????????????????????????????????????
            whiteList.setLoginNum(whiteList.getLoginNum() + 1);
            whiteListService.update(whiteList);
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param userId
     * @param ip
     * @return true:???????????????false:???????????????
     */
    @Override
    public boolean isFirstLoginByComplexPwd(Long userId, String ip) {
        QueryFilter qf = new QueryFilter(WhiteList.class);
        qf.addFilter("userId=", userId);
        qf.addFilter("ip=", ip);
        List<WhiteList> whiteLists = whiteListService.find(qf);
        if (whiteLists != null && whiteLists.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public RemoteResult swapEmail(Long id, String email, String loginPwd) {
        AppPersonInfo appPersonInfo = appPersonInfoService.get(new QueryFilter(AppPersonInfo.class).addFilter("email=", email));
        if (appPersonInfo != null) {
            return new RemoteResult().setSuccess(false).setMsg("gaiyouxiangyijingbeishiyong");
        }
        AppCustomer appCustomer = appCustomerService.get(id);
        PasswordHelper passwordHelper = new PasswordHelper();
        if (!appCustomer.getPassWord().equals(passwordHelper.encryString(loginPwd, appCustomer.getSalt()))) {
            return new RemoteResult().setSuccess(false).setMsg("mimacuowu");
        }
        return new RemoteResult().setSuccess(true);
    }

    @Override
    public String getEmailCode(String email) {

        AppPersonInfo appPersonInfo = appPersonInfoService.get(new QueryFilter(AppPersonInfo.class).addFilter("email=", email));
        if (appPersonInfo != null) {
            return appPersonInfo.getEmailCode();
        }
        return "";
    }

    /**
     * ???????????????????????????
     *
     * @param password
     * @param phone
     * @return
     */
    @Override
    public RemoteResult updatepwdphone(String password, String phone) {
        String encryString = null;
        try {
            QueryFilter qp = new QueryFilter(AppPersonInfo.class);
            qp.addFilter("mobilePhone=", phone);
            AppPersonInfo PersonInfo = appPersonInfoService.get(qp);

            QueryFilter qc = new QueryFilter(AppCustomer.class);
            qc.addFilter("id=", PersonInfo.getCustomerId());
            AppCustomer appcustomer = appCustomerService.get(qc);

            PasswordHelper passwordHelper = new PasswordHelper();
            encryString = passwordHelper.encryString(password, appcustomer.getSalt());
            appcustomer.setPassWord(encryString);
            appCustomerService.update(appcustomer);
        } catch (Exception e) {
            return new RemoteResult().setSuccess(false);
        }
        return new RemoteResult().setSuccess(true).setObj(encryString);
    }

    @Override
    public RemoteResult registApi(String data) {
        JSONObject obj = JSON.parseObject(data);
        // ??????
        String email = obj.getString("email");
        // ??????
        String password = obj.getString("password");
        // ????????????
        String paypassowrd = obj.getString("paypassowrd");
        // ????????????
        String truename = obj.getString("truename");

        // ?????????
        String mobile = obj.getString("mobile");
        // ?????????
        String idcard = obj.getString("idcard");
        // ????????????,0??????,1??????
        int isReal = obj.getIntValue("idcardauth");
        // ????????????
        int status = obj.getIntValue("status");

        // ?????????
        String referalCode = obj.getString("referalCode");
        // ?????????
        String commendCode = obj.getString("commendCode");
        // ?????????
        String language = obj.getString("language_code");

        // ????????????
        JSONArray coins = obj.getJSONArray("coins");


        if (StringUtils.isEmpty(email) && StringUtils.isEmpty(mobile)) {
            return new RemoteResult().setMsg("????????????????????????????????????");
        }


        AppPersonInfo _appPersonInfo = null;
        if (!StringUtils.isEmpty(mobile)) {
            QueryFilter f = new QueryFilter(AppPersonInfo.class);
            f.addFilter("mobilePhone=", mobile);// ?????????
            f.addFilter("country=", "+86");// ??????
            _appPersonInfo = appPersonInfoService.get(f);
            if (_appPersonInfo != null) {
                return new RemoteResult().setMsg("???????????????");
            }
        }
        if (!StringUtils.isEmpty(email)) {
            // ??????????????????
            email = email.toLowerCase();
            // ?????????????????????????????????
            QueryFilter filter = new QueryFilter(AppPersonInfo.class);
            filter.addFilter("email=", email);
            _appPersonInfo = appPersonInfoService.get(filter);
            if (_appPersonInfo != null) {
                return new RemoteResult().setMsg("???????????????");
            }
        }

        AppCustomer customer = new AppCustomer();
        String uuid = UUIDUtil.getUUID();
        // ?????????????????????ID
        customer.setUserName(uuid);
        // ??????????????????ID
        customer.setUserCode(uuid);
        // ????????????
        customer.setPassWord(password);
        // ??????????????????0
        customer.setGoogleState(0);
        // ????????????????????????1
        if (!StringUtils.isEmpty(mobile)) {
            customer.setPhoneState(1);
        } else {
            customer.setPhoneState(0);
        }
        // ????????????
        if (!StringUtils.isEmpty(email)) {
            customer.setHasEmail(1);
        } else {
            customer.setHasEmail(0);
        }
        // ????????????
        if (isReal == 1) {
            customer.setStates(2);
            customer.setIsReal(1);
        } else {
            // ???????????????
            customer.setStates(0);
            // ???????????????
            customer.setIsReal(0);
        }
        // ????????????
        if (!StringUtils.isEmpty(paypassowrd)) {
            customer.setAccountPassWord(paypassowrd);
        }
        // ??????????????????
        customer.setIsDelete(0);
        // ???????????????
        customer.setReferralCode(referalCode);
        // ???????????????
        if (!StringUtils.isEmpty(commendCode)) {
            customer.setCommendCode(commendCode);
        }
        // ????????????
        customer.setSalt(UUIDUtil.getUUID());
        PasswordHelper passwordHelper = new PasswordHelper();
        // ????????????
        customer.setPassWord(passwordHelper.encryString(password, customer.getSalt()));
        // ??????appCustomer
        appCustomerService.save(customer);

        // ???????????????AppPersonInfo
        AppPersonInfo appPersonInfo = new AppPersonInfo();
        // ??????????????????
        appPersonInfo.setCountry("+86");
        // ??????????????????
        appPersonInfo.setCustomerSource(0);
        // ??????customerId
        appPersonInfo.setCustomerId(customer.getId());
        // ??????????????????
        appPersonInfo.setCustomerType(1);
        // ?????????????????????
        appPersonInfo.setEmailCode(UUIDUtil.getUUID());
        // ????????????
        appPersonInfo.setEmail(email);
        // ???????????????
        appPersonInfo.setMobilePhone(mobile);

        // ?????????
        if (!StringUtils.isEmpty(truename)) {
            appPersonInfo.setSurname(truename.substring(0, 1));
            appPersonInfo.setTrueName(truename.substring(1));
        }

        // ???????????????
        if (!StringUtils.isEmpty(idcard)) {
            appPersonInfo.setCardId(idcard);
        }

        appPersonInfoService.save(appPersonInfo);


        // ?????????????????????
        RemoteAppAccountService remoteAppAccountService = (RemoteAppAccountService) ContextUtil.getBean("remoteAppAccountService");
        remoteAppAccountService.openAccount(customer, appPersonInfo, language, ContextUtil.getWebsite());

        // ?????????????????????
        RemoteExProductService remoteExProductService = (RemoteExProductService) ContextUtil.getBean("remoteExProductService");
        remoteExProductService.openDmAccount(customer, appPersonInfo, null, ContextUtil.getWebsite(), language);
        // ?????????
        appCommendUserService.saveObj(customer);
        // ????????????
        return new RemoteResult().setSuccess(true);

    }

    /**
     * ????????????????????????id??????????????????????????????????????????????????????????????????
     *
     * @param customerId
     * @return
     */
    @Override
    public JsonResult updateIsOpenTransFeeState(Long customerId, String isOpen) {
        JsonResult jsonResult = null;
        try {
            // ??????????????????
            QueryFilter qf = new QueryFilter(AppPersonInfo.class);
            qf.addFilter("customerId=", customerId);
            AppPersonInfo appPersonInfo = appPersonInfoService.get(qf);

            jsonResult = new JsonResult();
            if ("1".equals(isOpen)) {
                // 1??????redis??????????????????????????????????????????
                String config = redisService.get("configCache:all");
                if (!StringUtils.isEmpty(config)) {
                    String coinCode = ""; // ??????????????????????????????
                    String openLimit = ""; // ??????????????????????????????
                    JSONObject parseObject = JSONObject.parseObject(config);
                    if (parseObject != null) {
                        coinCode = parseObject.getString("platCoin");
                        openLimit = parseObject.getString("openLimit");
                    }
                    if (!StringUtils.isEmpty(coinCode) && !StringUtils.isEmpty(openLimit)) {
                        // 2????????????????????????id??????????????????????????????????????????????????????
                        String cId = appPersonInfo.getCustomerId() != null ? appPersonInfo.getCustomerId().toString() : "";
                        if (!StringUtils.isEmpty(cId)) {
                            // ?????????????????????
                            BigDecimal hotMoney = getHotMoney(cId, coinCode);
                            if (hotMoney.compareTo(new BigDecimal(openLimit)) == -1) {
                                return jsonResult.setSuccess(false).setMsg("???????????????????????????????????????");
                            } else {
                                // 3???????????????????????????
                                appPersonInfo.setIsOpenCoinFee(new Integer(isOpen));
                                appPersonInfoService.update(appPersonInfo);
                                return jsonResult.setSuccess(true);
                            }
                        }
                    }
                }
                return jsonResult.setSuccess(false).setMsg("????????????????????????");
            } else {
                appPersonInfo.setIsOpenCoinFee(new Integer(isOpen));
                appPersonInfoService.update(appPersonInfo);
                return jsonResult.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return jsonResult.setSuccess(false);
        }
    }

    /**
     * ????????????????????????id??????????????????
     *
     * @param customerId
     * @return
     */
    @Override
    public JsonResult setCommonLanage(Long customerId, String language) {
        JsonResult jsonResult = new JsonResult();
        try {
            // ??????????????????

            AppCustomer appCustomer = appCustomerService.get(customerId);
            if (appCustomer == null) {
                jsonResult.setMsg("???????????????");
                jsonResult.setSuccess(false);
                return jsonResult;
            }
            List<String> list = appDicService.findValByPkey(language);
            System.out.println(Arrays.toString(list.toArray()));
			/*if(!list.contains(language)){
				jsonResult.setMsg("???????????????");
				jsonResult.setSuccess(false);
				return jsonResult;
			}*/

            appCustomer.setCommonLanguage(language);
            appCustomerService.update(appCustomer);
            jsonResult.setSuccess(true);

        } catch (Exception e) {
            e.printStackTrace();
            return jsonResult.setSuccess(false);
        }
        return jsonResult;
    }

    @Override
    public FrontPage getPersonalAssetById(Map<String, String> params) {
        FrontPage frontPage = appBankCardService.findPageBySql1(params);
        List<AppBankCard> list = frontPage.getRows();
        List<AppBankCardManage> beanList = ObjectUtil.beanList(list, AppBankCardManage.class);
        frontPage.setRows(beanList);
        return frontPage;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    @Override
    public Boolean isAccountPassword(Long userId, String accountPassword) {
        QueryFilter filter = new QueryFilter(AppCustomer.class);
        filter.addFilter("id=", userId);
        AppCustomer customer = appCustomerService.get(filter);

        if (customer != null) {
            PasswordHelper passwordHelper = new PasswordHelper();
            String apw = passwordHelper.encryString(accountPassword, customer.getSalt());
            if (apw.equals(customer.getAccountPassWord())) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        PasswordHelper passwordHelper = new PasswordHelper();
        String apw = passwordHelper.encryString("7d8646a76a0e660a3e7dcb0a3dbc5557", "9def3d1d7a8242139fce4522473d6b21");
        System.out.println(apw);
    }

    @Override
    public RemoteResult getById(String id) {
        QueryFilter filter = new QueryFilter(AppCustomer.class);
        filter.addFilter("id=", id);
        AppCustomer customer = appCustomerService.get(filter);

        AppPersonInfo appPersonInfo = appPersonInfoService.get(new QueryFilter(AppPersonInfo.class).addFilter("customerId=", id));
        if (customer != null) {
            User bean2bean = ObjectUtil.bean2bean(customer, User.class);
            bean2bean.setAccountPassWord(customer.getAccountPassWord());
            bean2bean.setSurname(appPersonInfo.getSurname());
            bean2bean.setTruename(appPersonInfo.getTrueName());
            bean2bean.setCardcode(appPersonInfo.getCardId());
            bean2bean.setMobile(appPersonInfo.getMobilePhone());
            //bean2bean.setFirstIP(appPersonInfo.getFirstIP());
            bean2bean.setAreaCode(appPersonInfo.getCountry());
            bean2bean.setEmail(appPersonInfo.getEmail());
            return new RemoteResult().setSuccess(true).setObj(bean2bean);
        }
        return new RemoteResult().setSuccess(false);
    }

	@Override
	public List<Map<String, Object>> countCommendMoney(Map<String, Object> map) {
		List<Map<String, Object>> countCommendMoney = appCommendTradeDao.countCommendMoney(map);
		return countCommendMoney;
	}


    @Override
    public RemoteResult clearingExperience(Long customerId ,String account_type ,Long experience, BigDecimal money,String upgradeNote){
       /* //????????????
        QueryFilter qf = new QueryFilter(IcoCustomerLevel.class);
        System.out.println(customerId);
        qf.addFilter("customer_id=", customerId);
        //System.out.println("??????5++++" +customerId);get
        IcoCustomerLevel icoCustomerLevel = icoCustomerLevelService.get(qf);
        //System.out.println("??????6++++" +icoCustomerLevel);
        //????????????
        //????????????????????????
        String tpye="1";
        Long experience2 = icoCustomerLevel.getExperience();
       // System.out.println("??????7++++" +experience2);
        //???????????????0101 ???????????????0102 ???????????????0201 ???????????????0202 ????????????0203???????????????
        if(account_type.equals("0201")||account_type.equals("0202")||account_type.equals("0203")){
            tpye="2";
            experience2=experience2-experience;

            Long deductExperience = icoCustomerLevel.getDeductExperience();
            if(null == deductExperience){
                icoCustomerLevel.setDeductExperience(experience);
            }else{
                icoCustomerLevel.setDeductExperience(deductExperience+experience);
            }

        }else{
            experience2=experience2+experience;

            Long awardExperience = icoCustomerLevel.getAwardExperience();
            if(null == awardExperience){
                icoCustomerLevel.setAwardExperience(experience);
            }else{
                icoCustomerLevel.setAwardExperience(awardExperience+experience);
            }
        }
        RemoteResult countLevel=this.countLevel(experience2);
        if(!countLevel.getSuccess()){
            return  new RemoteResult().setSuccess(false).setMsg("jisuandengjicuowu");//??????????????????
        }

        IcoExperience icoExperience = new IcoExperience();
        //??????????????????
        if(null == countLevel.getObj() || countLevel.getObj().equals("")){
            //?????????????????????????????????
            icoExperience.setOldLevel("0");
            icoExperience.setNewLevel("");
            //???????????????????????????
            icoCustomerLevel.setExperience(experience2);

        }else{
            //????????????????????????
            IcoUpgradeConfig icoUpgradeConfig =(IcoUpgradeConfig)countLevel.getObj();
            //??????????????????
            icoExperience.setOldLevel_id(icoCustomerLevel.getLevel_id());
            icoExperience.setOldLevel(icoCustomerLevel.getGradeName());
            //?????????
            icoExperience.setNewLevel_id(icoUpgradeConfig.getId());
            icoExperience.setNewLevel(icoUpgradeConfig.getGradeName());
            //??????????????????
            if(icoUpgradeConfig.getId()!=icoCustomerLevel.getLevel_id()){
                //??????????????????
                IcoUpgradeRecord icoUpgradeRecord = new IcoUpgradeRecord();
                icoUpgradeRecord.setCustomer_id(customerId);
                icoUpgradeRecord.setOldLevel_id(icoCustomerLevel.getLevel_id());
                icoUpgradeRecord.setOldLevel(icoCustomerLevel.getGradeName());
                icoUpgradeRecord.setNewLevel_id(icoUpgradeConfig.getId());
                icoUpgradeRecord.setNewLevel(icoUpgradeConfig.getGradeName());
                icoUpgradeRecord.setUpgradeNote(upgradeNote);
                if(icoCustomerLevel.getLevel_id()==null || "".equals(icoCustomerLevel.getLevel_id().toString()) || icoCustomerLevel.getLevel_id()<icoUpgradeConfig.getId()){
                    icoUpgradeRecord.setType("1");
                }else {
                    icoUpgradeRecord.setType("2");
                }
                icoUpgradeRecordService.save(icoUpgradeRecord);
            }
            //???????????????????????????
            icoCustomerLevel.setExperience(experience2);
            icoCustomerLevel.setLevel_id(icoUpgradeConfig.getId());
            icoCustomerLevel.setGradeName(icoUpgradeConfig.getGradeName());
        }
        //??????????????????
        icoCustomerLevelService.update(icoCustomerLevel); */

        //????????????
        // ??????????????????
        /*QueryFilter qfs = new QueryFilter(AppPersonInfo.class);
        qf.addFilter("customerId=", customerId);
        AppPersonInfo appPersonInfo = appPersonInfoService.get(qfs);

        icoExperience.setCustomer_id(customerId);
        icoExperience.setCustomer_email(appPersonInfo.getEmail());
        icoExperience.setCustomer_mobile(appPersonInfo.getMobilePhone());
        icoExperience.setType(tpye);
        icoExperience.setAccount_type(account_type);
        if(!account_type.equals("0102")){
            icoExperience.setCoinNum(money);//????????????
        }
        icoExperience.setExperienceNum(String.valueOf(new Date().getTime()));//?????????
        icoExperience.setExperience(experience);
        icoExperience.setUpgradeNote(upgradeNote);
        icoExperienceService.save(icoExperience);*/
        return  new RemoteResult().setSuccess(true).setObj(upgradeNote);
    }

    @Override
    public RemoteResult countLevel(Long experience ){
        QueryFilter queryFilter = new QueryFilter(IcoUpgradeConfig.class);
        queryFilter.setOrderby("conditionPara desc");
        List<IcoUpgradeConfig> all = icoUpgradeConfigService.find(queryFilter);
        if (null !=all && all.size()>0){
            for (int i=0;i<all.size();i++){
                if(experience>=Long.valueOf(all.get(i).getConditionPara())){
                    System.out.println("???????????????id???"+all.get(i).getId()+"    ????????????i:"+i+"      ???????????????"+all.get(i).getConditionPara());
                    return  new RemoteResult().setSuccess(true).setObj(all.get(i));
                }
            }
            return  new RemoteResult().setSuccess(false);
        }else {
            return  new RemoteResult().setSuccess(false).setMsg("????????????");
        }
    }


    @Override
    public RemoteResult registGetExperience(Long customerId){
        //System.out.println("??????3++++" +customerId);
        JsonResult platformRule = remoteIcoService.getPlatformRule(RulesConfig.RulesCommonKey);
        if (platformRule.getSuccess()){
            IcoRulesConfig icoRulesConfig = (IcoRulesConfig)platformRule.getObj();
            //System.out.println("??????4++++" +icoRulesConfig.getRegExperience());
            RemoteResult remoteResult = this.clearingExperience(customerId, "0102", Long.valueOf(icoRulesConfig.getRegExperience()), new BigDecimal("0"), "???????????????");
            return remoteResult;
        }
        return new RemoteResult().setSuccess(false);
    }

}
