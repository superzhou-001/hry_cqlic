/**
 * Copyright:   北京互融时代软件有限公司
 * @author:      Wu Shuiming
 * @version:      V1.0 
 * @Date:        2016年3月24日 下午2:04:29
 */
package hry.trade.entrust;


import hry.trade.entrust.ExEntrustSimple;
import hry.trade.entrust.model.ExEntrust;

import java.util.List;
import java.util.Map;

/**
 * <p> TODO</p>
 * @author:         Gao Mimi 
 * @Date :          2016年4月12日 下午4:45:50 
 */
public interface ExOrderInfoServiceNoTr {
	/**
	 * 定时计算我放手续费和佣金
	 */
	public void  timingCulAtferMoney();
	
	/**
	 * 派发佣金
	 */
	public void payCommissions();
	
	/**
	 * 东方城自动回购
	 */
	public void buyBackOrder();
}
