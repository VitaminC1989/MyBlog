package com.site.blog.my.core.controller.admin;

import com.site.blog.my.core.entity.AdminUser;
import com.site.blog.my.core.service.AdminUserService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Resource
	private AdminUserService adminUserService;

	@GetMapping({ "/login" })
	public String login() {
		return "admin/login";
	}

	@PostMapping(value = "/login")
	public String login(@RequestParam("userName") String userName, @RequestParam("password") String password,
	        @RequestParam("verifyCode") String verifyCode, HttpSession session) {
		if (StringUtils.isEmpty(verifyCode)) {
			session.setAttribute("errorMsg", "验证码不能为空");
			return "admin/login";
		}
		if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
			session.setAttribute("errorMsg", "用户名或密码不能为空");
			return "admin/login";
		}
		String kaptchaCode = session.getAttribute("verifyCode") + "";
		if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
			session.setAttribute("errorMsg", "验证码错误");
			return "admin/login";
		}
		AdminUser adminUser = adminUserService.login(userName, password);
		// 用户名密码验证成功
		if (adminUser != null) {
			// 在Session中添加用户昵称和ID信息
			session.setAttribute("loginUser", adminUser.getNickName());
			session.setAttribute("loginUserId", adminUser.getAdminUserId());
			//session过期时间设置为7200秒 即两小时
			session.setMaxInactiveInterval(60 * 60 * 2);
			return "redirect:/admin/index";
		} else {
			session.setAttribute("errorMsg", "登陆失败");
			return "admin/login";
		}
	}

	@GetMapping({ "", "/", "/index", "/index.html" })
	public String index() {
		return "admin/index";
	}

	@GetMapping("/profile")
	public String profile(HttpServletRequest request) {
		Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
		AdminUser adminUser = adminUserService.getUserDetailById(loginUserId);
		if (adminUser == null) {
			return "admin/login";
		}
		// 前端 侧栏页面sidebar.html通过path的值来修改侧栏的样式（选中的功能模块底色变蓝）以显示当前被选中的功能模块（修改密码、安全退出等） 
		// th:class="${path}=='categories'?'nav-link active':'nav-link'"
		// 当前端请求  url=admin/profile  
		// 后端赋值path字段   前端就可以更新path的值了
		
		//以后如果需要在系统中新增一个模块，就可以对应的增加一个导航栏按钮在 sidebar.html 文件中，并在后端的控制器方法中赋值对应的 path 字段即可，比如博客管理、配置管理等之后的功能模块。
		request.setAttribute("path", "profile");
		request.setAttribute("loginUserName", adminUser.getLoginUserName());
		request.setAttribute("nickName", adminUser.getNickName());
		return "admin/profile";
	}

	@PostMapping("/profile/password")
	@ResponseBody
	public String passwordUpdate(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
	        @RequestParam("newPassword") String newPassword) {

		if (StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)) {
			return "参数不能为空";
		}
		Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
		if (adminUserService.updatePassword(loginUserId, originalPassword, newPassword)) {
			//修改成功后清空session中的数据，拦截器 AdminLoginInterceptor 会控制前端控制跳转至登录页
			request.getSession().removeAttribute("loginUserId");
			request.getSession().removeAttribute("loginUser");
			request.getSession().removeAttribute("errorMsg");
			return "success";
		} else {
			return "修改失败";
		}
	}

	@PostMapping("/profile/name")
	@ResponseBody
	public String nameUpdate(HttpServletRequest request, @RequestParam("loginUserName") String loginUserName,
	        @RequestParam("nickName") String nickName) {
		if (StringUtils.isEmpty(loginUserName) || StringUtils.isEmpty(nickName)) {
			return "参数不能为空";
		}
		Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
		if (adminUserService.updateName(loginUserId, loginUserName, nickName)) {
			// 更新Session中存储的用户昵称
			request.getSession().setAttribute("loginUser", nickName);
			return "success";
		} else {
			return "修改失败";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		request.getSession().removeAttribute("loginUserId");
		request.getSession().removeAttribute("loginUser");
		request.getSession().removeAttribute("errorMsg");
		return "admin/login";
	}

}
