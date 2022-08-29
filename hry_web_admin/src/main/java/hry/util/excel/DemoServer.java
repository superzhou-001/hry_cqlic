package hry.util.excel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DemoServer {
    static String str = "{\"status\":200,\"message\":\"成功\",\"data\":[{\"name\":\"板卡物理上下线\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"1\"},{\"name\":\"风扇物理上下线\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"2\"},{\"name\":\"电源物理上下线\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"3\"},{\"name\":\"设备下线\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"4\"},{\"name\":\"非核心设备升级\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"5\"},{\"name\":\"设备打补丁\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"6\"},{\"name\":\"设备打license\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"7\"},{\"name\":\"核心设备升级\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":3,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"8\"},{\"name\":\"巡检类操作\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"9\"},{\"name\":\"重大预案演习\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":3,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"10\"},{\"name\":\"一般预案演习\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"11\"},{\"name\":\"配合传输做端口级离线操作\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"12\"},{\"name\":\"配合传输做核心设备级离线操作\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":3,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"13\"},{\"name\":\"垃圾配置清理\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"14\"},{\"name\":\"安全防护相关配置添加\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"15\"},{\"name\":\"AAA配置修改\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"16\"},{\"name\":\"SNMP配置修改\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"17\"},{\"name\":\"NTP配置修改\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"18\"},{\"name\":\"BMP配置修改\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"19\"},{\"name\":\"FLOW配置修改\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"20\"},{\"name\":\"ERSPAN配置修改\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"21\"},{\"name\":\"GRPC配置修改\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"22\"},{\"name\":\"用户添加\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"23\"},{\"name\":\"接口预配置\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"24\"},{\"name\":\"接口描述配置\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"25\"},{\"name\":\"LINK-DELAY配置相关\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"26\"},{\"name\":\"BGP参数优化\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"27\"},{\"name\":\"ISIS参数优化\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"28\"},{\"name\":\"其他路由协议参数优化\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"29\"},{\"name\":\"BFD相关配置添加\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"30\"},{\"name\":\"用户添加\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"31\"},{\"name\":\"DCI扩容(人工)\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"32\"},{\"name\":\"DCI扩容(自动)\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"AUTO\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"listCtxFields\":[\"DEVICE_A\",\"DEVICE_B\",\"DEVICE_A_INTERFACE\",\"DEVICE_B_INTERFACE\",\"DEVICE_A_INTERFACE_IP\",\"DEVICE_B_INTERFACE_IP\",\"DEVICE_A_INTERFACE_DESC\",\"DEVICE_B_INTERFACE_DESC\"],\"flagCtxFields\":[\"GLOBAL_LINK_DELAY\"],\"plan\":[{\"planName\":\"DCI扩容-链路配置\",\"planSequence\":1,\"steps\":[{\"sequence\":1,\"execution\":\"26\",\"rollback\":\"\",\"executionPolicy\":\"PARALLEL\"}]},{\"planName\":\"DCI扩容-流量加载\",\"planSequence\":2,\"steps\":[{\"sequence\":1,\"execution\":\"DCI扩容变更-阶段2\",\"rollback\":\"\",\"executionPolicy\":\"PARALLEL\"}]}],\"id\":\"dci_expand_auto\"},{\"name\":\"DCN扩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"33\"},{\"name\":\"KBN扩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"34\"},{\"name\":\"云专线扩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"35\"},{\"name\":\"KIX扩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"36\"},{\"name\":\"KVR扩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"37\"},{\"name\":\"DPVS扩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"38\"},{\"name\":\"IDPVS扩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"39\"},{\"name\":\"SNAT扩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"40\"},{\"name\":\"KDNS扩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"41\"},{\"name\":\"KVG扩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"42\"},{\"name\":\"物理线路调整\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"43\"},{\"name\":\"DCI缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"44\"},{\"name\":\"DCN缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"45\"},{\"name\":\"KBN缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"46\"},{\"name\":\"云专线缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"47\"},{\"name\":\"KIX缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"48\"},{\"name\":\"KVR缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"49\"},{\"name\":\"DPVS缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"50\"},{\"name\":\"IDPVS缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"51\"},{\"name\":\"SNAT缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"52\"},{\"name\":\"KDNS缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"53\"},{\"name\":\"KVG缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"54\"},{\"name\":\"DCI架构迭代演进\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":3,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"55\"},{\"name\":\"KIX架构迭代演进\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":3,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"56\"},{\"name\":\"DCN架构迭代演进\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":3,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"57\"},{\"name\":\"KBN架构迭代演进\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":3,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"58\"},{\"name\":\"KMN架构迭代演进\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":3,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"59\"},{\"name\":\"外部运营商对接\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"60\"},{\"name\":\"新机房并网\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"61\"},{\"name\":\"新集群并网\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"62\"},{\"name\":\"新POD并网\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"63\"},{\"name\":\"新TOR并网\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"64\"},{\"name\":\"内网基础服务并网\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"65\"},{\"name\":\"外网基础服务并网\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"66\"},{\"name\":\"KIX接入设备并网\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"67\"},{\"name\":\"管理网设备并网\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"68\"},{\"name\":\"DCI核心设备横向扩容/缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":3,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"69\"},{\"name\":\"DCI接入设备横向扩容/缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"70\"},{\"name\":\"DCN核心设备横向扩容/缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":3,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"71\"},{\"name\":\"DCN汇聚设备横向扩容/缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":3,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"72\"},{\"name\":\"KIX核心设备横向扩容/缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":3,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"73\"},{\"name\":\"KIX接入设备横向扩容/缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"74\"},{\"name\":\"云网络并网\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"75\"},{\"name\":\"云网络设备横向扩容/缩容\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"76\"},{\"name\":\"黑白名单添加\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"理论无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"77\"},{\"name\":\"网段扩容相关\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"78\"},{\"name\":\"控制器并网相关\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"79\"},{\"name\":\"哈希策略调整\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"80\"},{\"name\":\"QOS相关参数调整\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"81\"},{\"name\":\"RDMA水线调优\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"82\"},{\"name\":\"故障板卡替换\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"83\"},{\"name\":\"故障引擎替换\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"84\"},{\"name\":\"故障交换网板替换\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"85\"},{\"name\":\"接入设备故障替换\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"86\"},{\"name\":\"汇聚设备故障替换\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"87\"},{\"name\":\"核心设备故障替换\",\"path\":\"网络,预配置\",\"flow\":\"change_main\",\"changeType\":\"network\",\"changeLevel\":3,\"flowLevel\":1,\"changeRisk\":\"理论无影响，异常情况下XXX\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"chenjiaji,yaochengcai\",\"id\":\"88\"},{\"name\":\"新系统建设\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"89\"},{\"name\":\"传输波道扩容\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"90\"},{\"name\":\"传输波道缩容\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"91\"},{\"name\":\"电路连通\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"92\"},{\"name\":\"无业务框/板卡/模块下线或更换\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"93\"},{\"name\":\"名称/IP批量更改\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"94\"},{\"name\":\"网管软件升级\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"95\"},{\"name\":\"网管进程重启\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"96\"},{\"name\":\"非工作路由光缆割接\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"97\"},{\"name\":\"工作路由光缆割接\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"98\"},{\"name\":\"保护倒换\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"99\"},{\"name\":\"POP点接头包的裸光熔接\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"100\"},{\"name\":\"业务机框/板卡更换\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"101\"},{\"name\":\"主机升级\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"102\"},{\"name\":\"性能调优\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"103\"},{\"name\":\"OLA站点设备整体操作/搬迁\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"104\"},{\"name\":\"非OLA站点设备整体搬迁\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"105\"},{\"name\":\"站点类型变化\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"106\"},{\"name\":\"电力改造\",\"path\":\"传输,预配置\",\"flow\":\"change_main\",\"changeType\":\"transmission\",\"changeLevel\":2,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"xiexuming\",\"id\":\"107\"},{\"name\":\"运营商变更\",\"path\":\"运营,预配置\",\"flow\":\"change_main\",\"changeType\":\"default\",\"changeLevel\":1,\"flowLevel\":1,\"changeRisk\":\"正常无影响\",\"changeRollback\":\"无\",\"changeMethod\":\"MANUAL\",\"affectFuncTool\":\"None\",\"auditUser\":\"user\",\"id\":\"108\"}],\"host\":\"172.29.14.18\",\"timestamp\":\"2021-09-23 14:28:56\"}";
    public static void main(String[] args) {
        JSONObject jsonObject = JSON.parseObject(str);
        // 取出data串
        String dataStr = jsonObject.getString("data");
        // 转为集合
        JSONArray dataArray = JSON.parseArray(dataStr);
        List<String> paths = new ArrayList<>();
        // 参数拼接
        for (int i = 0; i < dataArray.size(); i++) {
            String path = JSONObject.parseObject(dataArray.get(i).toString()).get("path").toString();
            String name = JSONObject.parseObject(dataArray.get(i).toString()).get("name").toString();
            paths.add(path + "," + name);
        }
        paths.add("网络,预配置2");
        paths.add("网络,预配置,板卡物理上下线,下线1,下线1-1,下线1-2,下线1-3");
        paths.add("网络,预配置,板卡物理上下线,下线1,下线1-2,下线1-2-1,下线1-2-3");
        paths.add("网络,预配置,板卡物理上下线,下线2,下线2-1");
//        paths.add("运输,预配置,板卡物理上下线,下线2");
//        paths.add("传输,预配置,新系统建设,办卡2");
//        paths.add("传输,预配置,新系统建设,办卡");
        System.out.println(paths.toString());
//        // 取出最大层级
//        int max = 0;
//        for (String str : paths) {
//            String[] strings = str.split(",");
//            if (strings.length > max) {
//                max = strings.length;
//            }
//        }
//        System.out.println(max);
//        Set<String> p1= new HashSet<>();
//        Set<String> p2= new HashSet<>();
//        Set<String> p3= new HashSet<>();
//        p1 = isEx(null, paths);
//        p2 = isEx("网络", paths);
//        p3 = isEx("运营,预配置", paths);

        List<Label> labelList = new ArrayList<>();
        for (String path : paths) {
            String[] sts = path.split(",");
            int num = sts.length;
            Label label = add(num, 0, path);
            labelList.add(label);
        }
        List<Label> labels = new ArrayList<>();
        // 所有树集合
        for (int i = 0; i < labelList.size(); i++) {
            hebing(labels, labelList.get(i));
        }
        System.out.println(JSON.toJSONString(labels));
        System.out.println("+++++++++");
    }

    private static void hebing(List<Label> labels,Label label2) {
        int flag = 0;
        for (Label label : labels) {
            // 如果说根节点不相同说明不是一个棵树
            if (label.getLabel().equals(label2.getLabel())) {
                flag ++;
                if (label.getChildren().size() > 0) {
                    hebing(label.getChildren(), label2.getChildren().get(0));
                } else {
                    // label2树深度超过当前树深度时添加
                    if (label2.getChildren().size() > 0) {
                        label.getChildren().add(label2.getChildren().get(0));
                    }
                }
            }
        }
        if (flag == 0) {
            labels.add(label2);
        }
    }

    // 先把每一个变成上下级
    private static Label isNext(String path) {
        Label label = new Label();
        String[] sts = path.split(",");
        for (int i = 0; i < sts.length; i ++) {
            if (i == 0) {
                label.setLabel(sts[i]);
            } else {
                addChildren(label, sts[i]);
            }
        }
        return label;
    }

    private static Label add(int num, int newNum, String path) {
        Label label = new Label();
        String[] sts = path.split(",");
        label.setLabel(sts[newNum]);
        newNum ++;
        List<Label> labels = new ArrayList<>();
        if (newNum < num) {
            labels.add(add(num, newNum, path));
            if (label.getChildren().size() == 0) {
                label.setChildren(labels);
            }
        }
        return label;
    }

    private static Set isEx(String name, List<String> paths) {
        Set<String> set = new HashSet<>();
        for (String path : paths) {
            String[] strings = path.split(",");
            if (name == null) {
                set.add(strings[0]);
            } else {
                String[] sv = name.split(",");
                if (strings.length > sv.length) {
                    String n = name + "," + strings[sv.length];
                    for (String s : paths) {
                        if (s.indexOf(n) != -1) {
                            set.add(strings[sv.length]);
                        }
                    }
                }
            }
        }
        return set;
    }

    private static void addChildren(Label label, String s) {
        Label lb = new Label();
        lb.setLabel(s);
        label.getChildren().add(lb);
    }
}