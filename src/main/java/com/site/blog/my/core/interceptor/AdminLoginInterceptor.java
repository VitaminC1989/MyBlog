package com.site.blog.my.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 后台系统身份验证拦截器
 */
@Component
public class AdminLoginInterceptor implements HandlerInterceptor {
	// 在业务处理器处理请求之前被调用。
	// 预处理，可以进行编码、安全控制、权限校验等处理；
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		String uri = request.getRequestURI();
		// 请求路径以admin开头  且  Session中不包含属性loginUser时  拦截
		if (uri.startsWith("/admin") && null == request.getSession().getAttribute("loginUser")) {
			request.getSession().setAttribute("errorMsg", "请登陆");
			response.sendRedirect(request.getContextPath() + "/admin/login");
			return false;
		} else {
			// 移除登陆之前可能添加在Session中的 key = "errorMsg"
			request.getSession().removeAttribute("errorMsg");
			return true;
		}
	}

	// 在业务处理器处理请求执行完成后，生成视图之前执行
	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
	        ModelAndView modelAndView) throws Exception {
	}

	// 在 DispatcherServlet 完全处理完请求后被调用
	// 可用于清理资源等，返回处理（已经渲染了页面）；
	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
	        Object o, Exception e) throws Exception {
	}

}
