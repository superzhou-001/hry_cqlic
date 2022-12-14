package hry.admin.exchange.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import hry.util.excel.ExcelReaderUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import hry.admin.exchange.model.BuildRecord;
import hry.admin.exchange.model.DateUtil;
import hry.admin.exchange.model.LastKLinePayload;
import hry.admin.exchange.model.ObjectUtil;
import hry.admin.exchange.service.BuildRecordService;
import hry.bean.JsonResult;
import hry.redis.common.utils.RedisService;
import hry.util.klinedata.KlineDataUtil;
import hry.util.klinedata.TransactionOrder;
import hry.util.sys.ContextUtil;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.util.StringUtil;

@Controller
public class KLineHistoryController {

    private static final Logger log = Logger.getLogger(KLineHistoryController.class);

    public static void main(String[] args) throws ParseException{
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long l = sim.parse("2018-05-01 05:59").getTime() + 1000 * 60;
        System.out.println(sim.format(l));
    }

	@RequestMapping("/kLineUpdateExcel")
	@ResponseBody
	public JsonResult kLineUpdateExcel(HttpServletRequest request) throws ParseException{
		JsonResult jsonResult = new JsonResult();

        String tradeCoinCode = request.getParameter("tradeCoinCode"); // ?????????
        String priceCoinCode = request.getParameter("priceCoinCode"); // ?????????
        String start = request.getParameter("start"); // ???
        String high = request.getParameter("high"); // ???
        String low = request.getParameter("low"); // ???
        String end = request.getParameter("end"); // ???
        String amount = request.getParameter("amount"); // ??????
        String time = request.getParameter("time"); // ??????

        if(StringUtil.isEmpty(start)){
            return new JsonResult().setSuccess(false).setMsg("?????????????????????");
        }
        if(StringUtil.isEmpty(high)){
            return new JsonResult().setSuccess(false).setMsg("?????????????????????");
        }
        if(StringUtil.isEmpty(low)){
            return new JsonResult().setSuccess(false).setMsg("?????????????????????");
        }
        if(StringUtil.isEmpty(end)){
            return new JsonResult().setSuccess(false).setMsg("?????????????????????");
        }
        if(StringUtil.isEmpty(amount)){
            return new JsonResult().setSuccess(false).setMsg("??????????????????");
        }
        if(StringUtil.isEmpty(time)){
            return new JsonResult().setSuccess(false).setMsg("??????????????????");
        }

        TransactionOrder t = new TransactionOrder();
        t.setStartPrice(new BigDecimal(start));
        t.setMaxPrice(new BigDecimal(high));
        t.setMinPrice(new BigDecimal(low));
        t.setEndPrice(new BigDecimal(end));
        t.setTransactionCount(new BigDecimal(amount));
        t.setTransactionTime(time);
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long l = sim.parse(time).getTime() + 1000 * 60;
        t.setTransactionEndTime(sim.format(l));
        SimpleDateFormat simf = new SimpleDateFormat("yyyyMMddHHmm");
        t.setId(simf.parse(time).getTime()/1000 + "");

        List<TransactionOrder> list = new ArrayList<TransactionOrder>();
        list.add(t);

        boolean flag = ExcelReaderUtil.queue.offer(1);
        if(flag){
            JsonResult common = common(list.get(0).getTransactionTime(), list.get(0).getTransactionTime(), tradeCoinCode, priceCoinCode, list, sim);
            return common;
        }else{
            jsonResult.setSuccess(false);
            jsonResult.setMsg("??????????????????????????????????????????????????????????????????");
            return jsonResult;
        }


        /*String filePath = request.getParameter("filePath");
        try {
            //?????????????????????????????????
            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat simf = new SimpleDateFormat("yyyyMMddHHmm");
            SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");

            boolean flag = ExcelReaderUtil.queue.offer(1);
            if(flag){
                //new Thread(() -> {
                    List<TransactionOrder> list = new ArrayList<TransactionOrder>(100000);
                    Map<String, Object> map = new HashMap<String, Object>();
                    //map.put("startTime", startTime);
                    //map.put("endTime", endTime);
                    map.put("tradeCoinCode", tradeCoinCode);
                    map.put("priceCoinCode", priceCoinCode);
                    map.put("list", list);
                    ExcelReaderUtil.tl.set(map);

                    long sTime = System.currentTimeMillis();   //??????????????????

                    ExcelReaderUtil.kLineExcel(file);

                    long eTime = System.currentTimeMillis(); //??????????????????
                    log.info("excel??????K??????????????????" + (eTime - sTime) / 1000 + "s");
                //}).start();
                jsonResult.setSuccess(true);
                jsonResult.setMsg("???????????????");
            }else{
                jsonResult.setSuccess(false);
                jsonResult.setMsg("??????????????????????????????????????????????????????????????????");
            }
        } catch (Exception e) {
            ExcelReaderUtil.queue.poll();
            e.printStackTrace();
            jsonResult.setMsg("?????????????????????");
        } finally {
            log.info("?????? ??? size:" + ExcelReaderUtil.queue.size());
        }
		return jsonResult;*/
	}

	/**
	 * ?????????????????????
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/klineSectionSave")
	@ResponseBody
	public JsonResult klineSectionSave(HttpServletRequest request) throws ParseException{
		
		/*SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat simf = new SimpleDateFormat("yyyyMMddHHmm");
		SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");
		
		RedisService redisService = (RedisService) ContextUtil.getBean("redisService");
		String key1 = "ETH_USDT:klinedata:TransactionOrder_ETH_USDT_1";
		String str1 = redisService.get(key1);
		JSONArray p1 = JSONArray.parseArray(str1);
		List<TransactionOrder> list1 = ObjectUtil.beanList(p1, TransactionOrder.class);
		Line15(list1, "ETH", "USDT", redisService, sim, sdf, simf, simple, 5);
		
		return new JsonResult().setSuccess(true);*/
		
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String tradeCoinCode = request.getParameter("tradeCoinCode"); // ?????????
		String priceCoinCode = request.getParameter("priceCoinCode"); // ?????????

        try {
            //?????????????????????????????????
            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Date begin = sim.parse(startTime);
            Date end = sim.parse(endTime);
            long between = (end.getTime()-begin.getTime())/1000;//??????1000?????????????????????
            long min = between / 60;
            if(min > 1440){
                return new JsonResult().setSuccess(false).setMsg("?????????????????????,????????????????????????1???");
            }

            if(sim.parse(startTime).getTime() > sim.parse(endTime).getTime()){
                return new JsonResult().setSuccess(false).setMsg("?????????????????????");
            }

            //?????????????????????
            KLineHistoryCallable k = new KLineHistoryCallable(startTime, endTime, tradeCoinCode, priceCoinCode);
            JsonResult jsonResult = k.call();
            if(jsonResult.getSuccess()){
                //???????????????
                JSONArray parseArrayThird = JSONArray.parseArray(jsonResult.getObj().toString());
                //JSONArray parseArrayThird = JSONArray.parseArray(jsonObject.get("data").toString());
                List<TransactionOrder> listThird = ObjectUtil.beanList(parseArrayThird, TransactionOrder.class);
                if(listThird.size() > 0){
                    common(startTime, endTime, tradeCoinCode, priceCoinCode, listThird, sim);
                    return new JsonResult().setSuccess(true).setMsg("?????????");
                }else{
                    return new JsonResult().setSuccess(false).setMsg("????????????????????????,??????????????????");
                }
            }
            return new JsonResult().setSuccess(false).setMsg(jsonResult.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonResult().setSuccess(false).setMsg("????????????,??????????????????");
        }

    }

	public static JsonResult common(String startTime, String endTime, String tradeCoinCode, String priceCoinCode, List<TransactionOrder> listThird, SimpleDateFormat sim) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simf = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");

        RedisService redisService = (RedisService) ContextUtil.getBean("redisService");

        /*String webContentByGet = getWebContentByGet("http://172.16.80.3:8007/open/api/hryr/dc/getTestHistoryPairQuotes", "utf-8", 20000);
        JSONObject jsonObject = JSONObject.parseObject(webContentByGet);*/


        /*String key1 = "ETH_USDT:klinedata:TransactionOrder_ETH_USDT_1";
        String str1 = redisService.get(key1);
        JSONArray p1 = JSONArray.parseArray(str1);
        List<TransactionOrder> listThird = ObjectUtil.beanList(p1, TransactionOrder.class);
        JsonResult jsonResult = new JsonResult();
        jsonResult.setSuccess(true);*/
        long startTimeK = System.currentTimeMillis(); // ????????????
        //????????????
        Date pullDate = new Date();

        //??????????????????1??????K?????????
        String key1min = tradeCoinCode + "_" + priceCoinCode + ":" + "klinedata:TransactionOrder_" + tradeCoinCode + "_" + priceCoinCode + "_1";
        String str = redisService.get(key1min);

        if(!StringUtils.isEmpty(str)){

        }else{
            //????????????????????????K?????????,1?????????????????????????????????
            redisService.save(key1min, JSONArray.toJSONString(listThird));
        }
        //redis???????????????1??????K?????????
        JSONArray parseArrayNow = JSONArray.parseArray(str);
        List<TransactionOrder> listNow = ObjectUtil.beanList(parseArrayNow, TransactionOrder.class);
        //redis???1??????K????????????????????????
        TransactionOrder one = listNow.get(listNow.size()-1);
        //redis???1??????K???????????????????????????
        TransactionOrder last = listNow.get(0);

        //???list
        List<TransactionOrder> listAll = new ArrayList<TransactionOrder>();

        //??????????????????????????????
        //listThird =  ObjectUtil.beanList(jsonResult.getObj().toString(), TransactionOrder.class);

        LastKLinePayload l1 = null;
        LastKLinePayload l5 = null;
        LastKLinePayload l15 = null;
        LastKLinePayload l30 = null;
        LastKLinePayload l60 = null;
        LastKLinePayload lday = null;
        LastKLinePayload lweek = null;

        Map<String, LastKLinePayload> map = null;

        if(sim.parse(endTime).getTime() < sim.parse(one.getTransactionTime()).getTime()){
            //??????????????????end ?????? redis??????????????????????????????,???????????????

            //??????1??????K???
            listAll.addAll(listNow);
            listAll.addAll(listThird);
            redisService.save(key1min, JSONArray.toJSONString(listAll));
            //KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listAll);

            map = backData(l1, l5, l15, l30, l60, lday, lweek, tradeCoinCode, priceCoinCode, redisService, listAll, sim, sdf, simf, simple, startTime, endTime);

            //LSL
            KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listThird);
            backDataL(tradeCoinCode, priceCoinCode, redisService, listThird, sim, sdf, simf, simple, startTime, endTime);

        }else if(sim.parse(startTime).getTime() < sim.parse(one.getTransactionTime()).getTime() &&
                (sim.parse(endTime).getTime() > sim.parse(one.getTransactionTime()).getTime() &&
                        sim.parse(endTime).getTime() < sim.parse(last.getTransactionEndTime()).getTime())){
            //??????????????????redis????????????????????????????????????????????????redis???????????????????????????????????????????????????????????????

            //??????????????????????????????????????????
            List<TransactionOrder> list31 = new ArrayList<TransactionOrder>();
            List<TransactionOrder> list32 = new ArrayList<TransactionOrder>(); // ????????????,???????????????????????????

            for(int q=0; q<listThird.size(); q++){
                if(sim.parse(listThird.get(q).getTransactionTime()).getTime() < sim.parse(one.getTransactionTime()).getTime()){
                    list31.add(listThird.get(q));
                }else{
                    list32.add(listThird.get(q));
                }
            }

            //redis???????????????
            List<TransactionOrder> listR1 = new ArrayList<TransactionOrder>();
            List<TransactionOrder> listR2 = new ArrayList<TransactionOrder>(); // ????????????,??????????????????????????????
            for(int w=0; w<listNow.size(); w++){
                if(sim.parse(endTime).getTime() >=  sim.parse(listNow.get(w).getTransactionTime()).getTime()){
                    listR1.add(listNow.get(w));
                }else{
                    listR2.add(listNow.get(w));
                }
            }

            //??????1??????K???
            listAll.addAll(listR1);
            listAll.addAll(list32);
            listAll.addAll(list31);
            redisService.save(key1min, JSONArray.toJSONString(listAll));
            //KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listAll);

            map = backData(l1, l5, l15, l30, l60, lday, lweek, tradeCoinCode, priceCoinCode, redisService, listAll, sim, sdf, simf, simple, startTime, endTime);

            //LSL
            KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listThird);
            backDataL(tradeCoinCode, priceCoinCode, redisService, listThird, sim, sdf, simf, simple, startTime, endTime);

        }else if(sim.parse(startTime).getTime() >= sim.parse(listNow.get(listNow.size()-1).getTransactionTime()).getTime()
                && sim.parse(endTime).getTime() <= sim.parse(listNow.get(0).getTransactionTime()).getTime()){
            //????????????????????????redis??????????????????????????????????????????????????????redis?????????????????????????????????

            if(sim.parse(startTime).getTime() == sim.parse(listNow.get(listNow.size()-1).getTransactionTime()).getTime()){
                //?????????????????? ????????? redis????????????????????????????????????  ??????????????????????????? ?????????
                //redis???????????????
                List<TransactionOrder> listR1 = new ArrayList<TransactionOrder>(); // ????????????,?????????????????????
                List<TransactionOrder> listR2 = new ArrayList<TransactionOrder>();

                for(int t=0; t<listNow.size(); t++){
                    if(sim.parse(listNow.get(t).getTransactionTime()).getTime() <= sim.parse(endTime).getTime()){
                        listR1.add(listNow.get(t));
                    }else{
                        listR2.add(listNow.get(t));
                    }
                }

                //??????1??????K???
                listAll.addAll(listR2);
                listAll.addAll(listThird);
                redisService.save(key1min, JSONArray.toJSONString(listAll));
                //KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listAll);

                map = backData(l1, l5, l15, l30, l60, lday, lweek, tradeCoinCode, priceCoinCode, redisService, listAll, sim, sdf, simf, simple, startTime, endTime);

                //LSL
                KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listThird);
                backDataL(tradeCoinCode, priceCoinCode, redisService, listThird, sim, sdf, simf, simple, startTime, endTime);

            }else if(sim.parse(startTime).getTime() > sim.parse(listNow.get(listNow.size()-1).getTransactionTime()).getTime() &&
                    sim.parse(endTime).getTime() < sim.parse(listNow.get(0).getTransactionTime()).getTime()){
                //???????????? ?????? redis??????????????????????????????  ???????????? ?????? redis?????????????????????????????????
                //redis???????????????
                List<TransactionOrder> listR1 = new ArrayList<TransactionOrder>();
                List<TransactionOrder> listR2 = new ArrayList<TransactionOrder>(); // ????????????,?????????????????????
                List<TransactionOrder> listR3 = new ArrayList<TransactionOrder>();

                for(int t=0; t<listNow.size(); t++){
                    if(sim.parse(listNow.get(t).getTransactionTime()).getTime() < sim.parse(startTime).getTime()){
                        listR1.add(listNow.get(t));
                    }else if(sim.parse(listNow.get(t).getTransactionTime()).getTime() >= sim.parse(startTime).getTime() &&
                            sim.parse(listNow.get(t).getTransactionTime()).getTime() <= sim.parse(endTime).getTime()){
                        listR2.add(listNow.get(t));
                    }else if(sim.parse(listNow.get(t).getTransactionTime()).getTime() > sim.parse(endTime).getTime()){
                        listR3.add(listNow.get(t));
                    }
                }

                //??????1??????K???
                listAll.addAll(listR3);
                listAll.addAll(listThird);
                listAll.addAll(listR1);
                redisService.save(key1min, JSONArray.toJSONString(listAll));
                //KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listAll);

                map = backData(l1, l5, l15, l30, l60, lday, lweek, tradeCoinCode, priceCoinCode, redisService, listAll, sim, sdf, simf, simple, startTime, endTime);

                //LSL
                KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listThird);
                backDataL(tradeCoinCode, priceCoinCode, redisService, listThird, sim, sdf, simf, simple, startTime, endTime);

            }else if(sim.parse(endTime).getTime() == sim.parse(listNow.get(0).getTransactionTime()).getTime()){
                // ???????????? ???????????? redis?????????????????????????????????
                List<TransactionOrder> listR1 = new ArrayList<TransactionOrder>(); // ????????????,?????????????????????
                List<TransactionOrder> listR2 = new ArrayList<TransactionOrder>();

                for(int t=0; t<listNow.size(); t++){
                    if(sim.parse(listNow.get(t).getTransactionTime()).getTime() >= sim.parse(startTime).getTime()){
                        listR1.add(listNow.get(t));
                    }else{
                        listR2.add(listNow.get(t));
                    }
                }

                //??????1??????K???
                listAll.addAll(listThird);
                listAll.addAll(listR2);
                redisService.save(key1min, JSONArray.toJSONString(listAll));
                //KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listAll);

                map = backData(l1, l5, l15, l30, l60, lday, lweek, tradeCoinCode, priceCoinCode, redisService, listAll, sim, sdf, simf, simple, startTime, endTime);

                //LSL
                KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listThird);
                backDataL(tradeCoinCode, priceCoinCode, redisService, listThird, sim, sdf, simf, simple, startTime, endTime);
            }
        }else if(sim.parse(startTime).getTime() > sim.parse(listNow.get(listNow.size()-1).getTransactionTime()).getTime() &&
                sim.parse(endTime).getTime() >= sim.parse(listNow.get(0).getTransactionTime()).getTime()){
            // ???????????? ?????? redis???????????????(?????????????????????redis??????????????????) ???????????? ???????????? redis?????????????????????????????????

            if(sim.parse(startTime).getTime() >= sim.parse(listNow.get(listNow.size()-1).getTransactionTime()).getTime() &&
                    sim.parse(startTime).getTime() <= sim.parse(listNow.get(0).getTransactionTime()).getTime()){
                //???????????????redis???????????? ??? ??????redis?????????????????????????????????

                //redis???????????????
                List<TransactionOrder> listR1 = new ArrayList<TransactionOrder>();
                List<TransactionOrder> listR2 = new ArrayList<TransactionOrder>();

                for(int t=0; t<listNow.size(); t++){
                    if(sim.parse(startTime).getTime() <= sim.parse(listNow.get(t).getTransactionTime()).getTime()){
                        listR2.add(listNow.get(t)); // ????????????listThird????????????
                    }else{
                        listR1.add(listNow.get(t));
                    }
                }

                //??????1??????K???
                listAll.addAll(listThird);
                listAll.addAll(listR1);
                redisService.save(key1min, JSONArray.toJSONString(listAll));
                //KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listAll);

                map = backData(l1, l5, l15, l30, l60, lday, lweek, tradeCoinCode, priceCoinCode, redisService, listAll, sim, sdf, simf, simple, startTime, endTime);

                //LSL
                KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listThird);
                backDataL(tradeCoinCode, priceCoinCode, redisService, listThird, sim, sdf, simf, simple, startTime, endTime);

            }else if(sim.parse(startTime).getTime() >= sim.parse(listNow.get(0).getTransactionTime()).getTime()){
                // ???????????? ???????????? redis?????????????????????????????????  ????????????
                //??????1??????K???
                listAll.addAll(listThird);
                listAll.addAll(listNow);
                redisService.save(key1min, JSONArray.toJSONString(listAll));
                //KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listAll);

                map = backData(l1, l5, l15, l30, l60, lday, lweek, tradeCoinCode, priceCoinCode, redisService, listAll, sim, sdf, simf, simple, startTime, endTime);

                //LSL
                KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listThird);
                backDataL(tradeCoinCode, priceCoinCode, redisService, listThird, sim, sdf, simf, simple, startTime, endTime);

            }
        }else if(sim.parse(startTime).getTime() <= sim.parse(listNow.get(0).getTransactionTime()).getTime() &&
                sim.parse(endTime).getTime() >= sim.parse(listNow.get(listNow.size()-1).getTransactionTime()).getTime()){
            //????????????????????????redis??????????????????????????????????????????????????????redis??????????????????  ????????????
            listAll.addAll(listThird);
            redisService.save(key1min, JSONArray.toJSONString(listAll));
            //KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listAll);

            map = backData(l1, l5, l15, l30, l60, lday, lweek, tradeCoinCode, priceCoinCode, redisService, listAll, sim, sdf, simf, simple, startTime, endTime);

            //LSL
            KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1min", listThird);
            backDataL(tradeCoinCode, priceCoinCode, redisService, listThird, sim, sdf, simf, simple, startTime, endTime);
        }

        l1 = map.get("1min");
        l5 = map.get("5min");
        l15 = map.get("15min");
        l30 = map.get("30min");
        l60 = map.get("60min");
        lday = map.get("1day");
        lweek = map.get("1week");

        BuildRecord br = new BuildRecord();
        br.setStartTime(sim.parse(startTime));
        br.setEndTime(sim.parse(endTime));
        br.setPullTime(pullDate);
        br.setTradeCoinCode(tradeCoinCode);
        br.setPriceCoinCode(priceCoinCode);
        br.setM1(listThird.size());
        br.setM5(l5.getSize());
        br.setM15(l15.getSize());
        br.setM30(l30.getSize());
        br.setM60(l60.getSize());
        br.setDay1(lday.getSize());
        br.setWeek1(lweek.getSize());
        BuildRecordService buildRecordService = (BuildRecordService) ContextUtil.getBean("buildRecordService");
        buildRecordService.save(br);

        long finishedTimeK = System.currentTimeMillis(); // ??????????????????
        System.out.println("----------------K???????????????,?????????"+ (finishedTimeK - startTimeK) / 1000 + "m----------------");

        ExcelReaderUtil.queue.poll();

        log.info("?????? ??? size:" + ExcelReaderUtil.queue.size());
        return new JsonResult().setSuccess(true).setMsg("????????????");
    }
	
	/**
	 * ???????????? 
	 * @return
	 */
	public static BigDecimal[] getCode(int keepDecimalForCurrency, BigDecimal[] array){
	    BigDecimal maxIndex = array[0];//??????????????????????????????????????????
	    BigDecimal minIndex = array[0];//??????????????????????????????????????????
        //??????????????????
        for (int i = 0; i < array.length; i++) {
            if(maxIndex.compareTo(array[i]) < 0){
                maxIndex = array[i];
            }
            if(minIndex.compareTo(array[i]) > 0){
                minIndex = array[i];
            }
        }
        BigDecimal[] mm = new BigDecimal[]{maxIndex.setScale(keepDecimalForCurrency, BigDecimal.ROUND_HALF_UP), minIndex.setScale(keepDecimalForCurrency, BigDecimal.ROUND_HALF_UP)};
        return mm;
	}
	
	/**
	 * ?????????????????????
	 * @param tradeCoinCode ?????????
	 * @param priceCoinCode ?????????
	 * @return
	 */
	public static int getCoinToCoinKeep(String tradeCoinCode, String priceCoinCode) {
		RedisService redisService = (RedisService) ContextUtil.getBean("redisService");
		
		int  keepDecimalForCurrency = 4;
		String str = redisService.get("cn:coinInfoList");
		if(!StringUtils.isEmpty(str)){
			JSONArray array = JSON.parseArray(str);
			if(array!=null){
				for(int i =0 ; i < array.size() ;i++){
					JSONObject jsonObject = array.getJSONObject(i);
					if(tradeCoinCode.equals(jsonObject.getString("coinCode")) && priceCoinCode.equals(jsonObject.getString("fixPriceCoinCode"))){
						keepDecimalForCurrency = jsonObject.getInteger("keepDecimalForCurrency");
						break;
					}
				}
			}
		}
		return keepDecimalForCurrency;
	}

	/**
	 * 
	 * @param list5
	 * @param tradeCoinCode
	 * @param priceCoinCode
	 * @param redisService
	 * @param sim
	 * @param sdf
	 * @param simf
	 * @param simple
	 * @param scale
	 * @return
	 * @throws ParseException
	 */
	public static LastKLinePayload line(List<TransactionOrder> list5, String tradeCoinCode, String priceCoinCode, RedisService redisService,
			SimpleDateFormat sim, SimpleDateFormat sdf, SimpleDateFormat simf, SimpleDateFormat simple, int scale, String startTime, String endTime) throws ParseException{
		TransactionOrder tOne = list5.get(list5.size()-1); // 5??????K?????????????????????
		TransactionOrder tLast = list5.get(0); // 5??????K????????????????????????
		
		List<TransactionOrder> listAll = new ArrayList<TransactionOrder>();
		
		//??????15??????????????????
    	int minute = Integer.valueOf(tLast.getTransactionTime().split(":")[1]); // 18:12 ?????????12
    	int minuteNew = minute - minute % scale + scale; // ??????10=12-12%2
    	//?????????????????????????????????????????????  ??????
    	String tLastTime = "";
    	if(minuteNew != 0){
    		if(minuteNew < 10){
    			tLastTime = tLast.getTransactionTime().split(":")[0] + ":0" + minuteNew;
    		}else if(minuteNew == 60){
    			tLastTime = tLast.getTransactionTime().split(":")[0] + ":00";
    			tLastTime = sim.format((sim.parse(tLast.getTransactionTime()).getTime() / 1000 + scale * 60) * 1000);
    		}else{
    			tLastTime = tLast.getTransactionTime().split(":")[0] + ":" + minuteNew;
    		}
    	}else{
    		tLastTime = tLast.getTransactionTime().split(":")[0] + ":00";
    	}
		
		int count = list5.size();
		while(count > 0){
			List<TransactionOrder> listNew = new ArrayList<TransactionOrder>();
			TransactionOrder t = new TransactionOrder();
	    	Date lastOne = sim.parse(tLastTime); // ????????????????????????????????????
	    	String startT = sim.format((lastOne.getTime() / 1000 - scale * 60) * 1000); // ???????????????????????????
	    	t.setTransactionTime(startT);//????????????  beginOne??????????????????????????????
			t.setTransactionEndTime(tLastTime);//????????????  beginOne??????????????????????????????
			
			//??????????????????????????????
			for(int i=0; i<list5.size(); i++){
				if(sim.parse(startT).getTime() <= sim.parse(list5.get(i).getTransactionTime()).getTime() &&
	    				sim.parse(list5.get(i).getTransactionTime()).getTime() < sim.parse(tLastTime).getTime()){
	    			listNew.add(list5.get(i));
	    		}
			}
			if(listNew.size() > 0){
				t.setStartPrice(listNew.get(listNew.size() - 1 < 0? 0 : listNew.size() - 1).getStartPrice());
		    	t.setEndPrice(listNew.get(0).getEndPrice());
		    	
		    	BigDecimal[] mmmax = new BigDecimal[listNew.size()]; // ???????????????
		    	BigDecimal[] mmmin = new BigDecimal[listNew.size()]; // ???????????????
		    	BigDecimal countall = new BigDecimal("0"); //?????????????????????
		    	for(int m=0; m<listNew.size(); m++){
		    		mmmax[m] = listNew.get(m).getMaxPrice();
		    		mmmin[m] = listNew.get(m).getMinPrice();
		    		countall = countall.add(listNew.get(m).getTransactionCount());
		    	}
		    	mmmax = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmmax);
		    	mmmin = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmmin);
		    	
		    	t.setTransactionCount(countall);
		    	t.setMaxPrice(mmmax[0]);
		    	t.setMinPrice(mmmin[1]);
		    	t.setId(simf.format(sdf.parse(tOne.getTransactionEndTime())));
		    	listAll.add(t);
		    	
		    	tLastTime = startT;
				startT = sim.format((sim.parse(startT).getTime() / 1000 - scale * 60) * 1000);
				
				count = count - listNew.size();
			}else{
				//?????????????????? ?????????????????????
				tLastTime = startT;
				startT = sim.format((sim.parse(startT).getTime() / 1000 - scale * 60) * 1000);
			}
		}
		redisService.save(tradeCoinCode + "_" + priceCoinCode + ":klinedata:TransactionOrder_" + tradeCoinCode + "_" + priceCoinCode + "_" + scale, JSONArray.toJSONString(listAll));
		//KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, scale + "m", listAll);
		
		//??????scale????????????????????????
		LastKLinePayload l = new LastKLinePayload();
		l.setCount(new BigDecimal(0));
		l.setDayTotalDealAmount(new BigDecimal(0));
		l.setEndTime(listAll.get(0).getTransactionEndTime());
		l.setStartTime(listAll.get(0).getTransactionTime());
		l.setId(sdf.parse(listAll.get(0).getTransactionEndTime()).getTime() / 1000);
		l.setTime(sdf.parse(listAll.get(0).getTransactionEndTime()).getTime() / 1000);
		l.setPeriod(scale + "min");
		l.setPriceOpen(listAll.get(listAll.size()-1).getStartPrice());
		l.setPriceLast(listAll.get(0).getEndPrice());
		
		int p = 0;
		//?????????????????????
		for(int t=0; t<listAll.size(); t++){
			if(sim.parse(startTime).getTime() <= sim.parse(listAll.get(t).getTransactionTime()).getTime() && 
					sim.parse(listAll.get(t).getTransactionTime()).getTime() <= sim.parse(endTime).getTime()){
				p++;
			}
		}
		l.setSize(p + 1);
		
		BigDecimal[] mmMax15 = new BigDecimal[listAll.size()]; // ???????????????
    	BigDecimal[] mmMin15 = new BigDecimal[listAll.size()]; // ???????????????
    	BigDecimal countAll15 = new BigDecimal("0"); //?????????????????????
    	for(int m=0; m<listAll.size(); m++){
    		mmMax15[m] = listAll.get(m).getMaxPrice();
    		mmMin15[m] = listAll.get(m).getMinPrice();
    		countAll15 = countAll15.add(listAll.get(m).getTransactionCount());
    	}
    	mmMax15 = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMax15);
    	mmMin15 = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMin15);
		
    	l.setPriceHigh(mmMax15[0]);
    	l.setPriceLow(mmMin15[1]);
		l.setAmount(countAll15);
		return l;
	} 
	
	/**
	 * ????????????
	 * @param list
	 * @param tradeCoinCode
	 * @param priceCoinCode
	 * @param redisService
	 * @param scale
	 * @param sim
	 * @param sdf
	 * @param simf
	 * @throws ParseException
	 */
	public static LastKLinePayload lineDay(List<TransactionOrder> list, String tradeCoinCode, String priceCoinCode, RedisService redisService,
			SimpleDateFormat sim, SimpleDateFormat sdf, SimpleDateFormat simple, String startTime, String endTime) throws ParseException{
		List<TransactionOrder> listAll = new ArrayList<TransactionOrder>();
		
		//?????????1???????????? ??????????????????????????????????????????  0????????????
		int daysBetween = DateUtil.daysBetween(list.get(list.size() - 1).getTransactionTime(), list.get(0).getTransactionTime());
		daysBetween += 1;
		
		//redis???????????????????????????
		String oneDay = sdf.format(sdf.parse(list.get(list.size() - 1).getTransactionTime())) + " 00:00";
		//????????????
		String nextDay = sdf.format((sdf.parse(oneDay).getTime() / 1000 + 24 * 60 * 60) * 1000) + " 00:00";
		
		for(int q=0; q<daysBetween; q++){
			TransactionOrder t = new TransactionOrder();
			
			List<TransactionOrder> listDay = new ArrayList<TransactionOrder>();
			
			for(int i=0; i<list.size(); i++){
				if(sim.parse(list.get(i).getTransactionTime()).getTime() >= sim.parse(oneDay).getTime() && 
						sim.parse(list.get(i).getTransactionTime()).getTime() < sim.parse(nextDay).getTime()){
					//???????????????????????????
					
					listDay.add(list.get(i));
				}
			}
			if(listDay.size() > 0){
				t.setTransactionTime(oneDay);
				t.setTransactionEndTime(nextDay);
				t.setId(simple.format(sim.parse(oneDay))+"0000");
				t.setStartPrice(listDay.get(listDay.size() - 1 < 0? 0 : listDay.size() - 1).getStartPrice());
				t.setEndPrice(listDay.get(0).getStartPrice());
				
				BigDecimal[] mmMax = new BigDecimal[listDay.size()]; // ???????????????
		    	BigDecimal[] mmMin = new BigDecimal[listDay.size()]; // ???????????????
		    	BigDecimal countAll = new BigDecimal("0"); //?????????????????????
		    	for(int m=0; m<listDay.size(); m++){
		    		mmMax[m] = listDay.get(m).getMaxPrice();
		    		mmMin[m] = listDay.get(m).getMinPrice();
		    		countAll = countAll.add(listDay.get(m).getTransactionCount());
		    	}
		    	mmMax = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMax);
		    	mmMin = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMin);
		    	
		    	t.setTransactionCount(countAll);
		    	t.setMaxPrice(mmMax[0]);
		    	t.setMinPrice(mmMin[1]);
				
				listAll.add(t);
				
				//?????????????????????????????????????????????
				oneDay = nextDay;
				//????????????+1
				nextDay = sdf.format((sdf.parse(oneDay).getTime() / 1000 + 24 * 60 * 60) * 1000) + " 00:00";
			}else{
				//?????????????????????????????????????????????
				oneDay = nextDay;
				//????????????+1
				nextDay = sdf.format((sdf.parse(oneDay).getTime() / 1000 + 24 * 60 * 60) * 1000) + " 00:00";
			}
		}
		//????????????
		Collections.reverse(listAll);
		redisService.save(tradeCoinCode + "_" + priceCoinCode + ":klinedata:TransactionOrder_" + tradeCoinCode + "_" + priceCoinCode + "_1440", JSONArray.toJSONString(listAll));
		//KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1day", listAll);
		
		//????????????
		LastKLinePayload l = new LastKLinePayload();
		l.setCount(new BigDecimal(0));
		l.setDayTotalDealAmount(new BigDecimal(0));
		l.setEndTime(listAll.get(0).getTransactionEndTime());
		l.setStartTime(listAll.get(0).getTransactionTime());
		l.setId(simple.parse(listAll.get(0).getTransactionEndTime()).getTime() / 1000);
		l.setTime(sdf.parse(listAll.get(0).getTransactionEndTime()).getTime() / 1000);
		l.setPeriod("1day");
		l.setPriceOpen(listAll.get(listAll.size()-1).getStartPrice());
		l.setPriceLast(listAll.get(0).getEndPrice());
		
		BigDecimal[] mmMax = new BigDecimal[listAll.size()]; // ???????????????
    	BigDecimal[] mmMin = new BigDecimal[listAll.size()]; // ???????????????
    	BigDecimal countAll = new BigDecimal("0"); //?????????????????????
    	for(int m=0; m<listAll.size(); m++){
    		mmMax[m] = listAll.get(m).getMaxPrice();
    		mmMin[m] = listAll.get(m).getMinPrice();
    		countAll = countAll.add(listAll.get(m).getTransactionCount());
    	}
    	mmMax = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMax);
    	mmMin = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMin);
		
    	l.setPriceHigh(mmMax[0]);
    	l.setPriceLow(mmMin[1]);
		l.setAmount(countAll);
		
		int p = 0;
		//?????????????????????
		for(int t=0; t<listAll.size(); t++){
			if(sim.parse(startTime).getTime() <= sim.parse(listAll.get(t).getTransactionTime()).getTime() && 
					sim.parse(listAll.get(t).getTransactionTime()).getTime() <= sim.parse(endTime).getTime()){
				p++;
			}
		}
		l.setSize(p + 1);
		return l;
	}
	
	/**
	 * ????????????
	 * @param tradeCoinCode
	 * @param priceCoinCode
	 * @param redisService
	 * @param sim
	 * @param sdf
	 * @param simf
	 * @throws ParseException
	 */
	public static LastKLinePayload lineWeek(String tradeCoinCode, String priceCoinCode, RedisService redisService,
			SimpleDateFormat sim, SimpleDateFormat sdf, SimpleDateFormat simf, SimpleDateFormat simple, String startTime, String endTime) throws ParseException{
		List<TransactionOrder> listAll = new ArrayList<TransactionOrder>();
		
		//??????Key
		String key = tradeCoinCode + "_" + priceCoinCode + ":klinedata:TransactionOrder_" + tradeCoinCode + "_" + priceCoinCode + "_1440";
		String str = redisService.get(key);
		if(!StringUtils.isEmpty(str)){
			//redis?????????????????????K?????????
			JSONArray parseArrayNow = JSONArray.parseArray(str);
			List<TransactionOrder> listNow = ObjectUtil.beanList(parseArrayNow, TransactionOrder.class);
			
			//??????redis???????????????????????????????????????????????????????????????????????????
			long from = sdf.parse(listNow.get(listNow.size()-1).getTransactionTime()).getTime();
	        long to = sdf.parse(listNow.get(0).getTransactionTime()).getTime();
	        int days = (int) ((to - from) / (1000*3600*24));
	        
	        //????????????
	        int week = days / 7 + 1;
	        
	        //redis??????????????????????????????????????????
			String oneDay = sim.format(DateUtil.getFirstDayOfWeek(sdf.parse(listNow.get(listNow.size() - 1).getTransactionTime())));
			//????????????
			String nextDay = sim.format((DateUtil.getLastDayOfWeek(sdf.parse(listNow.get(listNow.size() - 1).getTransactionTime())).getTime() / 1000 + 24 * 60 * 60) * 1000);
			
			/*Date d = new Date();
			long n = (sdf.parse(listNow.get(0).getTransactionTime()).getTime() / 1000 + 24 * 60 * 60) * 1000;
			d.setTime(n);
			String nextDay = sim.format((DateUtil.getLastDayOfWeek(d).getTime() / 1000 + 24 * 60 * 60) * 1000);*/
			
	        for(int q=0; q<week; q++){
	        	TransactionOrder t = new TransactionOrder();
				
				List<TransactionOrder> listWeek = new ArrayList<TransactionOrder>();
				
				for(int i=0; i<listNow.size(); i++){
					if(sim.parse(listNow.get(i).getTransactionTime()).getTime() >= sim.parse(oneDay).getTime() && 
							sim.parse(listNow.get(i).getTransactionTime()).getTime() < sim.parse(nextDay).getTime()){
						//?????????????????????
						listWeek.add(listNow.get(i)); // ????????????
					}
				}
				if(listWeek.size() > 0){
					t.setTransactionTime(oneDay);
					t.setTransactionEndTime(nextDay);
					t.setId(simple.format(sim.parse(oneDay))+"0000");
					t.setStartPrice(listWeek.get(listWeek.size() - 1 < 0? 0 : listWeek.size() - 1).getStartPrice());
					t.setEndPrice(listWeek.get(0).getStartPrice());
					
					BigDecimal[] mmMax = new BigDecimal[listWeek.size()]; // ???????????????
			    	BigDecimal[] mmMin = new BigDecimal[listWeek.size()]; // ???????????????
			    	BigDecimal countAll = new BigDecimal("0"); //?????????????????????
			    	for(int m=0; m<listWeek.size(); m++){
			    		mmMax[m] = listWeek.get(m).getMaxPrice();
			    		mmMin[m] = listWeek.get(m).getMinPrice();
			    		countAll = countAll.add(listWeek.get(m).getTransactionCount());
			    	}
			    	mmMax = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMax);
			    	mmMin = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMin);
			    	
			    	t.setTransactionCount(countAll);
			    	t.setMaxPrice(mmMax[0]);
			    	t.setMinPrice(mmMin[1]);
					
					listAll.add(t);
					
					//?????????????????????????????????????????????
					oneDay = nextDay;
					//??????????????? + 7
					/*d.setTime((sdf.parse(listNow.get(0).getTransactionTime()).getTime() / 1000 + 24 * 60 * 60 * 7) * 1000);
					nextDay = sim.format((DateUtil.getLastDayOfWeek(d).getTime() / 1000 + 24 * 60 * 60) * 1000);*/
					nextDay = sim.format((DateUtil.getLastDayOfWeek(sdf.parse(nextDay)).getTime() / 1000 + 24 * 60 * 60) * 1000);
				}else{
					//?????????????????????????????????????????????
					oneDay = nextDay;
					//??????????????? + 7
					/*d.setTime((sdf.parse(listNow.get(0).getTransactionTime()).getTime() / 1000 + 24 * 60 * 60 * 7) * 1000);
					nextDay = sim.format((DateUtil.getLastDayOfWeek(d).getTime() / 1000 + 24 * 60 * 60) * 1000);*/
					nextDay = sim.format((DateUtil.getLastDayOfWeek(sdf.parse(nextDay)).getTime() / 1000 + 24 * 60 * 60) * 1000);
				}
	        }
	        redisService.save(tradeCoinCode + "_" + priceCoinCode + ":klinedata:TransactionOrder_" + tradeCoinCode + "_" + priceCoinCode + "_10080", JSONArray.toJSONString(listAll));
	        //KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1week", listAll);
	        
	        //????????????
			LastKLinePayload l = new LastKLinePayload();
			l.setCount(new BigDecimal(0));
			l.setDayTotalDealAmount(new BigDecimal(0));
			l.setEndTime(listAll.get(0).getTransactionEndTime());
			l.setStartTime(listAll.get(0).getTransactionTime());
			l.setId(sdf.parse(listAll.get(0).getTransactionEndTime()).getTime() / 1000);
			l.setTime(sdf.parse(listAll.get(0).getTransactionEndTime()).getTime() / 1000);
			l.setPeriod("1week");
			l.setPriceOpen(listAll.get(listAll.size()-1).getStartPrice());
			l.setPriceLast(listAll.get(0).getEndPrice());
			
			BigDecimal[] mmMax = new BigDecimal[listAll.size()]; // ???????????????
	    	BigDecimal[] mmMin = new BigDecimal[listAll.size()]; // ???????????????
	    	BigDecimal countAll = new BigDecimal("0"); //?????????????????????
	    	for(int m=0; m<listAll.size(); m++){
	    		mmMax[m] = listAll.get(m).getMaxPrice();
	    		mmMin[m] = listAll.get(m).getMinPrice();
	    		countAll = countAll.add(listAll.get(m).getTransactionCount());
	    	}
	    	mmMax = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMax);
	    	mmMin = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMin);
			
	    	l.setPriceHigh(mmMax[0]);
	    	l.setPriceLow(mmMin[1]);
			l.setAmount(countAll);

			int p = 0;
			//?????????????????????
			for(int t=0; t<listAll.size(); t++){
				if(sim.parse(startTime).getTime() <= sim.parse(listAll.get(t).getTransactionTime()).getTime() && 
						sim.parse(listAll.get(t).getTransactionTime()).getTime() <= sim.parse(endTime).getTime()){
					p++;
				}
			}
			l.setSize(p + 1);
			return l;
		}
		return null;
	}
	
	public static Map<String, LastKLinePayload> backData(LastKLinePayload l1, LastKLinePayload l5, LastKLinePayload l15, LastKLinePayload l30 , LastKLinePayload l60,
			LastKLinePayload lday, LastKLinePayload lweek, String tradeCoinCode, String priceCoinCode, RedisService redisService, List<TransactionOrder> listAll,
			SimpleDateFormat sim, SimpleDateFormat sdf, SimpleDateFormat simf, SimpleDateFormat simple, String startTime, String endTime) throws ParseException{
		
		//??????1????????????????????????
		l1 = new LastKLinePayload();
		l1.setCount(new BigDecimal(0));
		l1.setDayTotalDealAmount(new BigDecimal(0));
		l1.setEndTime(listAll.get(0).getTransactionEndTime());
		l1.setStartTime(listAll.get(0).getTransactionTime());
		l1.setId(sdf.parse(listAll.get(listAll.size()-1).getTransactionEndTime()).getTime() / 1000);
		l1.setTime(sdf.parse(listAll.get(listAll.size()-1).getTransactionEndTime()).getTime() / 1000);
		l1.setPeriod("1min");
		l1.setPriceOpen(listAll.get(listAll.size()-1).getStartPrice());
		l1.setPriceLast(listAll.get(0).getEndPrice());
		l1.setVolume(new BigDecimal(0));
		
		BigDecimal[] mmMax = new BigDecimal[listAll.size()]; // ???????????????
    	BigDecimal[] mmMin = new BigDecimal[listAll.size()]; // ???????????????
    	BigDecimal countAll = new BigDecimal("0"); //?????????????????????
    	for(int m=0; m<listAll.size(); m++){
    		mmMax[m] = listAll.get(m).getMaxPrice();
    		mmMin[m] = listAll.get(m).getMinPrice();
    		countAll = countAll.add(listAll.get(m).getTransactionCount());
    	}
    	mmMax = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMax);
    	mmMin = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMin);
		
    	l1.setPriceHigh(mmMax[0]);
    	l1.setPriceLow(mmMin[1]);
		l1.setAmount(countAll);
		l1.setSize(listAll.size());
		
		//??????5??????K???
		String key1 = tradeCoinCode + "_" + priceCoinCode + ":" + "klinedata:TransactionOrder_" + tradeCoinCode + "_" + priceCoinCode + "_1";
		String str1 = redisService.get(key1);
		JSONArray p1 = JSONArray.parseArray(str1);
		List<TransactionOrder> list1 = ObjectUtil.beanList(p1, TransactionOrder.class);
		l5 = line(list1, tradeCoinCode, priceCoinCode, redisService, sim, sdf, simf, simple, 5, startTime, endTime);
		
		//??????15??????K???
		String key5 = tradeCoinCode + "_" + priceCoinCode + ":" + "klinedata:TransactionOrder_" + tradeCoinCode + "_" + priceCoinCode + "_5";
		String str5 = redisService.get(key5);
		JSONArray p5 = JSONArray.parseArray(str5);
		List<TransactionOrder> list5 = ObjectUtil.beanList(p5, TransactionOrder.class);
		l15 = line(list5, tradeCoinCode, priceCoinCode, redisService, sim, sdf, simf, simple, 15, startTime, endTime);
		
		//??????30??????K???
		String key15 = tradeCoinCode + "_" + priceCoinCode + ":" + "klinedata:TransactionOrder_" + tradeCoinCode + "_" + priceCoinCode + "_15";
		String str15 = redisService.get(key15);
		JSONArray p15 = JSONArray.parseArray(str15);
		List<TransactionOrder> list15 = ObjectUtil.beanList(p15, TransactionOrder.class);
		l30 = line(list15, tradeCoinCode, priceCoinCode, redisService, sim, sdf, simf, simple, 30, startTime, endTime);
		
		//??????60??????K???
		String key30 = tradeCoinCode + "_" + priceCoinCode + ":" + "klinedata:TransactionOrder_" + tradeCoinCode + "_" + priceCoinCode + "_30";
		String str30 = redisService.get(key30);
		JSONArray p30 = JSONArray.parseArray(str30);
		List<TransactionOrder> list30 = ObjectUtil.beanList(p30, TransactionOrder.class);
		l60 = line(list30, tradeCoinCode, priceCoinCode, redisService, sim, sdf, simf, simple, 60, startTime, endTime);
		
		//????????????
		String key60 = tradeCoinCode + "_" + priceCoinCode + ":" + "klinedata:TransactionOrder_" + tradeCoinCode + "_" + priceCoinCode + "_60";
		String str60 = redisService.get(key60);
		JSONArray p60 = JSONArray.parseArray(str60);
		List<TransactionOrder> list60 = ObjectUtil.beanList(p60, TransactionOrder.class);
		lday = lineDay(list60, tradeCoinCode, priceCoinCode, redisService, sim, sdf, simple, startTime, endTime);
		
		//????????????
		lweek = lineWeek(tradeCoinCode, priceCoinCode, redisService, sim, sdf, simf, simple, startTime, endTime);
		
		//??????redis???BTC_USDT:PeriodLastKLineList_backups??????
		Map<String, LastKLinePayload> map = new HashMap<String, LastKLinePayload>(7);
		map.put("1min", l1);
		map.put("5min", l5);
		map.put("15min", l15);
		map.put("30min", l30);
		map.put("60min", l60);
		map.put("1day", lday);
		map.put("1week", lweek);
		String keyl = tradeCoinCode + "_" + priceCoinCode + ":PeriodLastKLineList_backups";
		redisService.save(keyl, JSONArray.toJSONString(map));
		
		//??????redis???BTC_ETH:PeriodLastKLineList
		List<LastKLinePayload> listNew = new ArrayList<LastKLinePayload>();
		listNew.add(l1);
		listNew.add(l5);
		listNew.add(l15);
		listNew.add(l30);
		listNew.add(l60);
		listNew.add(lday);
		listNew.add(lweek);
		String key2 = tradeCoinCode + "_" + priceCoinCode + ":PeriodLastKLineList";
		redisService.save(key2, JSONArray.toJSONString(listNew));
		
		return map;
	}
	
	/**
	 * ????????????????????? 5 - 60??????
	 * @param list5
	 * @param tradeCoinCode
	 * @param priceCoinCode
	 * @param redisService
	 * @param sim
	 * @param sdf
	 * @param simf
	 * @param simple
	 * @param scale
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	public static List<TransactionOrder> lineL(List<TransactionOrder> listThird, String tradeCoinCode, String priceCoinCode, RedisService redisService,
			SimpleDateFormat sim, SimpleDateFormat sdf, SimpleDateFormat simf, SimpleDateFormat simple, int scale, String startTime, String endTime) throws ParseException{

		TransactionOrder tOne = listThird.get(listThird.size()-1); // 5??????K?????????????????????
		TransactionOrder tLast = listThird.get(0); // 5??????K????????????????????????
		
		List<TransactionOrder> listAll = new ArrayList<TransactionOrder>();
		
		//??????15??????????????????
    	int minute = Integer.valueOf(tLast.getTransactionTime().split(":")[1]); // 18:12 ?????????12
    	int minuteNew = minute - minute % scale + scale; // ??????10=12-12%2
    	//?????????????????????????????????????????????  ??????
    	String tLastTime = "";
    	if(minuteNew != 0){
    		if(minuteNew < 10){
    			tLastTime = tLast.getTransactionTime().split(":")[0] + ":0" + minuteNew;
    		}else if(minuteNew == 60){
    			tLastTime = tLast.getTransactionTime().split(":")[0] + ":00";
    			tLastTime = sim.format((sim.parse(tLast.getTransactionTime()).getTime() / 1000 + scale * 60) * 1000);
    		}else{
    			tLastTime = tLast.getTransactionTime().split(":")[0] + ":" + minuteNew;
    		}
    	}else{
    		tLastTime = tLast.getTransactionTime().split(":")[0] + ":00";
    	}
		
		int count = listThird.size();
		while(count > 0){
			List<TransactionOrder> listNew = new ArrayList<TransactionOrder>();
			TransactionOrder t = new TransactionOrder();
	    	Date lastOne = sim.parse(tLastTime); // ????????????????????????????????????
	    	String startT = sim.format((lastOne.getTime() / 1000 - scale * 60) * 1000); // ???????????????????????????
	    	t.setTransactionTime(startT);//????????????  beginOne??????????????????????????????
			t.setTransactionEndTime(tLastTime);//????????????  beginOne??????????????????????????????
			
			//??????????????????????????????
			for(int i=0; i<listThird.size(); i++){
				if(sim.parse(startT).getTime() <= sim.parse(listThird.get(i).getTransactionTime()).getTime() &&
	    				sim.parse(listThird.get(i).getTransactionTime()).getTime() < sim.parse(tLastTime).getTime()){
	    			listNew.add(listThird.get(i));
	    		}
			}
			if(listNew.size() > 0){
				t.setStartPrice(listNew.get(listNew.size() - 1 < 0? 0 : listNew.size() - 1).getStartPrice());
		    	t.setEndPrice(listNew.get(0).getEndPrice());
		    	
		    	BigDecimal[] mmmax = new BigDecimal[listNew.size()]; // ???????????????
		    	BigDecimal[] mmmin = new BigDecimal[listNew.size()]; // ???????????????
		    	BigDecimal countall = new BigDecimal("0"); //?????????????????????
		    	for(int m=0; m<listNew.size(); m++){
		    		mmmax[m] = listNew.get(m).getMaxPrice();
		    		mmmin[m] = listNew.get(m).getMinPrice();
		    		countall = countall.add(listNew.get(m).getTransactionCount());
		    	}
		    	mmmax = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmmax);
		    	mmmin = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmmin);
		    	
		    	t.setTransactionCount(countall);
		    	t.setMaxPrice(mmmax[0]);
		    	t.setMinPrice(mmmin[1]);
		    	t.setId(simf.format(sdf.parse(tOne.getTransactionEndTime())));
		    	listAll.add(t);
		    	
		    	tLastTime = startT;
				startT = sim.format((sim.parse(startT).getTime() / 1000 - scale * 60) * 1000);
				
				count = count - listNew.size();
			}else{
				//?????????????????? ?????????????????????
				tLastTime = startT;
				startT = sim.format((sim.parse(startT).getTime() / 1000 - scale * 60) * 1000);
			}
		}
		KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, scale + "min", listAll);
		return listAll;
	}
	
	/**
	 * ????????????????????? 1????????????
	 * @param list
	 * @param tradeCoinCode
	 * @param priceCoinCode
	 * @param redisService
	 * @param sim
	 * @param sdf
	 * @param simple
	 * @param startTime
	 * @param endTime
	 * @throws ParseException
	 */
	public static List<TransactionOrder> lineDayL(List<TransactionOrder> listThird, String tradeCoinCode, String priceCoinCode, RedisService redisService,
			SimpleDateFormat sim, SimpleDateFormat sdf, SimpleDateFormat simple, String startTime, String endTime) throws ParseException{
		List<TransactionOrder> listAll = new ArrayList<TransactionOrder>();
		
		//?????????1???????????? ??????????????????????????????????????????  0????????????
		int daysBetween = DateUtil.daysBetween(listThird.get(listThird.size() - 1).getTransactionTime(), listThird.get(0).getTransactionTime());
		daysBetween += 1;
		
		//redis???????????????????????????
		String oneDay = sdf.format(sdf.parse(listThird.get(listThird.size() - 1).getTransactionTime())) + " 00:00";
		//????????????
		String nextDay = sdf.format((sdf.parse(oneDay).getTime() / 1000 + 24 * 60 * 60) * 1000) + " 00:00";
		
		for(int q=0; q<daysBetween; q++){
			TransactionOrder t = new TransactionOrder();
			
			List<TransactionOrder> listDay = new ArrayList<TransactionOrder>();
			
			for(int i=0; i<listThird.size(); i++){
				if(sim.parse(listThird.get(i).getTransactionTime()).getTime() >= sim.parse(oneDay).getTime() && 
						sim.parse(listThird.get(i).getTransactionTime()).getTime() < sim.parse(nextDay).getTime()){
					//???????????????????????????
					
					listDay.add(listThird.get(i));
				}
			}
			if(listDay.size() > 0){
				t.setTransactionTime(oneDay);
				t.setTransactionEndTime(nextDay);
				t.setId(simple.format(sim.parse(oneDay))+"0000");
				t.setStartPrice(listDay.get(listDay.size() - 1 < 0? 0 : listDay.size() - 1).getStartPrice());
				t.setEndPrice(listDay.get(0).getStartPrice());
				
				BigDecimal[] mmMax = new BigDecimal[listDay.size()]; // ???????????????
		    	BigDecimal[] mmMin = new BigDecimal[listDay.size()]; // ???????????????
		    	BigDecimal countAll = new BigDecimal("0"); //?????????????????????
		    	for(int m=0; m<listDay.size(); m++){
		    		mmMax[m] = listDay.get(m).getMaxPrice();
		    		mmMin[m] = listDay.get(m).getMinPrice();
		    		countAll = countAll.add(listDay.get(m).getTransactionCount());
		    	}
		    	mmMax = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMax);
		    	mmMin = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMin);
		    	
		    	t.setTransactionCount(countAll);
		    	t.setMaxPrice(mmMax[0]);
		    	t.setMinPrice(mmMin[1]);
				
				listAll.add(t);
				
				//?????????????????????????????????????????????
				oneDay = nextDay;
				//????????????+1
				nextDay = sdf.format((sdf.parse(oneDay).getTime() / 1000 + 24 * 60 * 60) * 1000) + " 00:00";
			}else{
				//?????????????????????????????????????????????
				oneDay = nextDay;
				//????????????+1
				nextDay = sdf.format((sdf.parse(oneDay).getTime() / 1000 + 24 * 60 * 60) * 1000) + " 00:00";
			}
		}
		//????????????
		Collections.reverse(listAll);
		KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1day", listAll);
		return listAll;
	}
	
	/**
	 * ?????????????????????
	 * @param tradeCoinCode
	 * @param priceCoinCode
	 * @param redisService
	 * @param sim
	 * @param sdf
	 * @param simf
	 * @throws ParseException
	 */
	public static void lineWeekL(List<TransactionOrder> listThird, String tradeCoinCode, String priceCoinCode, RedisService redisService,
			SimpleDateFormat sim, SimpleDateFormat sdf, SimpleDateFormat simf, SimpleDateFormat simple, String startTime, String endTime) throws ParseException{
		List<TransactionOrder> listAll = new ArrayList<TransactionOrder>();
		
		//??????redis???????????????????????????????????????????????????????????????????????????
		long from = sdf.parse(listThird.get(listThird.size()-1).getTransactionTime()).getTime();
        long to = sdf.parse(listThird.get(0).getTransactionTime()).getTime();
        int days = (int) ((to - from) / (1000*3600*24));
        
        //????????????
        int week = days / 7 + 1;
        
        //redis??????????????????????????????????????????
		String oneDay = sim.format(DateUtil.getFirstDayOfWeek(sdf.parse(listThird.get(listThird.size() - 1).getTransactionTime())));
		//????????????
		String nextDay = sim.format((DateUtil.getLastDayOfWeek(sdf.parse(listThird.get(listThird.size() - 1).getTransactionTime())).getTime() / 1000 + 24 * 60 * 60) * 1000);
		
		/*Date d = new Date();
		long n = (sdf.parse(listNow.get(0).getTransactionTime()).getTime() / 1000 + 24 * 60 * 60) * 1000;
		d.setTime(n);
		String nextDay = sim.format((DateUtil.getLastDayOfWeek(d).getTime() / 1000 + 24 * 60 * 60) * 1000);*/
		
        for(int q=0; q<week; q++){
        	TransactionOrder t = new TransactionOrder();
			
			List<TransactionOrder> listWeek = new ArrayList<TransactionOrder>();
			
			for(int i=0; i<listThird.size(); i++){
				if(sim.parse(listThird.get(i).getTransactionTime()).getTime() >= sim.parse(oneDay).getTime() && 
						sim.parse(listThird.get(i).getTransactionTime()).getTime() < sim.parse(nextDay).getTime()){
					//?????????????????????
					listWeek.add(listThird.get(i)); // ????????????
				}
			}
			if(listWeek.size() > 0){
				t.setTransactionTime(oneDay);
				t.setTransactionEndTime(nextDay);
				t.setId(simple.format(sim.parse(oneDay))+"0000");
				t.setStartPrice(listWeek.get(listWeek.size() - 1 < 0? 0 : listWeek.size() - 1).getStartPrice());
				t.setEndPrice(listWeek.get(0).getStartPrice());
				
				BigDecimal[] mmMax = new BigDecimal[listWeek.size()]; // ???????????????
		    	BigDecimal[] mmMin = new BigDecimal[listWeek.size()]; // ???????????????
		    	BigDecimal countAll = new BigDecimal("0"); //?????????????????????
		    	for(int m=0; m<listWeek.size(); m++){
		    		mmMax[m] = listWeek.get(m).getMaxPrice();
		    		mmMin[m] = listWeek.get(m).getMinPrice();
		    		countAll = countAll.add(listWeek.get(m).getTransactionCount());
		    	}
		    	mmMax = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMax);
		    	mmMin = getCode(getCoinToCoinKeep(tradeCoinCode, priceCoinCode), mmMin);
		    	
		    	t.setTransactionCount(countAll);
		    	t.setMaxPrice(mmMax[0]);
		    	t.setMinPrice(mmMin[1]);
				
				listAll.add(t);
				
				//?????????????????????????????????????????????
				oneDay = nextDay;
				//??????????????? + 7
				/*d.setTime((sdf.parse(listNow.get(0).getTransactionTime()).getTime() / 1000 + 24 * 60 * 60 * 7) * 1000);
				nextDay = sim.format((DateUtil.getLastDayOfWeek(d).getTime() / 1000 + 24 * 60 * 60) * 1000);*/
				nextDay = sim.format((DateUtil.getLastDayOfWeek(sdf.parse(nextDay)).getTime() / 1000 + 24 * 60 * 60) * 1000);
			}else{
				//?????????????????????????????????????????????
				oneDay = nextDay;
				//??????????????? + 7
				/*d.setTime((sdf.parse(listNow.get(0).getTransactionTime()).getTime() / 1000 + 24 * 60 * 60 * 7) * 1000);
				nextDay = sim.format((DateUtil.getLastDayOfWeek(d).getTime() / 1000 + 24 * 60 * 60) * 1000);*/
				nextDay = sim.format((DateUtil.getLastDayOfWeek(sdf.parse(nextDay)).getTime() / 1000 + 24 * 60 * 60) * 1000);
			}
        }
        KlineDataUtil.insert(tradeCoinCode + "_" + priceCoinCode, "1week", listAll);
	}
	
	public static void backDataL(String tradeCoinCode, String priceCoinCode, RedisService redisService, List<TransactionOrder> listThird,
			SimpleDateFormat sim, SimpleDateFormat sdf, SimpleDateFormat simf, SimpleDateFormat simple, String startTime, String endTime) throws ParseException{
		
		//??????5??????K???
		List<TransactionOrder> lineL5 = lineL(listThird, tradeCoinCode, priceCoinCode, redisService, sim, sdf, simf, simple, 5, startTime, endTime);
		
		//??????15??????K???
		List<TransactionOrder> lineL15 = lineL(lineL5, tradeCoinCode, priceCoinCode, redisService, sim, sdf, simf, simple, 15, startTime, endTime);
		
		//??????30??????K???
		List<TransactionOrder> lineL30 = lineL(lineL15, tradeCoinCode, priceCoinCode, redisService, sim, sdf, simf, simple, 30, startTime, endTime);
		
		//??????60??????K???
		List<TransactionOrder> lineL60 = lineL(lineL30, tradeCoinCode, priceCoinCode, redisService, sim, sdf, simf, simple, 60, startTime, endTime);
		
		//????????????
		List<TransactionOrder> lineDay = lineDayL(lineL60, tradeCoinCode, priceCoinCode, redisService, sim, sdf, simple, startTime, endTime);
		
		//????????????
		lineWeekL(lineDay, tradeCoinCode, priceCoinCode, redisService, sim, sdf, simf, simple, startTime, endTime);
	}

	public static String getWebContentByGet(String urlString, final String charset, int timeout) throws IOException {
		if (urlString == null || urlString.length() == 0) {
			return null;
		}
		urlString = (urlString.startsWith("http://") || urlString.startsWith("https://")) ? urlString
				: ("http://" + urlString).intern();
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		// ?????????????????????????????????????????????
		conn.setRequestProperty("User-Agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		// ?????????text/html????????????????????????????????????,pdf,*/*???????????????tomcat/conf/web??????????????????
		conn.setRequestProperty("Accept", "*/*");
		conn.setConnectTimeout(timeout);
		try {
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		InputStream input = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, charset));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\r\n");
		}
		if (reader != null) {
			reader.close();
		}
		if (conn != null) {
			conn.disconnect();
		}
		return sb.toString();

	}
}
