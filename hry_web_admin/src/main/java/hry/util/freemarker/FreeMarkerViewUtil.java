package hry.util.freemarker;

import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import hry.admin.HryCache;
import hry.redis.common.utils.RedisService;
import hry.util.HttpServletRequestUtils;
import hry.util.SpringUtil;
import hry.util.sys.ContextUtil;
import org.apache.log4j.Logger;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Locale;
import java.util.Map;

public class FreeMarkerViewUtil extends FreeMarkerView {
	public static final Logger log = Logger.getLogger(FreeMarkerViewUtil.class);
	private static final String CONTEXT_PATH = "ctx"; 
	private static final String CACHE_APPCONFIG = "cache_appconfig";

	 @Override
    protected void exposeHelpers(Map<String, Object> model,
        HttpServletRequest request) throws Exception {
        //model.put(CONTEXT_PATH, request.getAttribute(CONTEXT_PATH));
		 //System.out.println(ContextUtil.getBasePath(request));
        model.put(CONTEXT_PATH, ContextUtil.getAppPath(request));
        model.put(CACHE_APPCONFIG, HryCache.cache_appconfig);
        super.exposeHelpers(model, request);
    }
	
	@Override
	protected void doRender(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Expose model to JSP tags (as request attributes).
		exposeModelAsRequestAttributes(model, request);
		// Expose all standard FreeMarker hash models.
		SimpleHash fmModel = buildTemplateModel(model, request, response);

		if (logger.isDebugEnabled()) {
			logger.debug("Rendering FreeMarker 模版 [" + getUrl() + "] in FreeMarkerView '" + getBeanName() + "'");
		}
		// Grab the locale-specific version of the template.
		Locale locale = RequestContextUtils.getLocale(request);

		/*
		 * 默认生成静态文件,除非在编写ModelAndView时指定CREATE_HTML = false, 这样对静态文件生成的粒度控制更细一点
		 * 例如：ModelAndView mav = new ModelAndView("search");
		 * mav.addObject("CREATE_HTML", false);
		 * 
		 * 修改:默认不生成静态html,设置CREATE_HTML = true是时生成html。2017/02/06
		 */
		if (Boolean.TRUE.equals(model.get("CREATE_HTML"))) {
			log.info("生成 FreeMarker 模版 [" + getUrl() + "]  '" + getBeanName() + "'");
			createHTML(getTemplate(locale), fmModel, request, response);
		} else {
			processTemplate(getTemplate(locale), fmModel, response);
		}
	}

	public void createHTML(Template template, SimpleHash model, HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateException, ServletException {
		// 站点根目录的绝对路径
		String basePath = request.getSession().getServletContext().getRealPath("/");
		String requestHTML = this.getRequestHTML(request);
		// 静态页面绝对路径
		String htmlPath = basePath + requestHTML;
		

		String requestUrl = request.getRequestURL().toString();
		requestUrl += HttpServletRequestUtils.getParamsMd5(request);
		String md5DigestAsHex = DigestUtils.md5DigestAsHex(requestUrl.getBytes());
		
		RedisService redisService = SpringUtil.getBean("redisService");
		
		log.info("生成静态页面绝对路径===========>>:" + htmlPath);
		redisService.save("static_html:"+md5DigestAsHex, requestHTML, 300);
		
		
		File htmlFile = new File(htmlPath);
		if (!htmlFile.getParentFile().exists()) {
			htmlFile.getParentFile().mkdirs();
		}
		if (!htmlFile.exists()) {
			htmlFile.createNewFile();
		}
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlFile), "UTF-8"));
		// 处理模版
		template.process(model, out);
		out.flush();
		out.close();
		/* 将请求转发到生成的htm文件 */
		request.getRequestDispatcher(requestHTML).forward(request, response);
	}

	/**
	 * 计算要生成的静态文件相对路径 因为大家在调试的时候一般在Tomcat的webapps下面新建站点目录的，
	 * 但在实际应用时直接布署到ROOT目录里面,这里要保证路径的一致性。
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return /目录/*.htm
	 */
	private String getRequestHTML(HttpServletRequest request) {
		// web应用名称,部署在ROOT目录时为空
		String contextPath = request.getContextPath();
		// web应用/目录/文件.do
		String requestURI = request.getRequestURI();
		// basePath里面已经有了web应用名称，所以直接把它replace掉，以免重复
		requestURI = requestURI.replaceFirst(contextPath, "");
		// 补充默认页
		if ("/".equals(requestURI)) {
			requestURI += "index.do";
		}
		// 将.do改为.html,稍后将请求转发到此html文件
		requestURI = requestURI +HttpServletRequestUtils.getParamsMd5(request) +".html";
		return requestURI;
	}
}