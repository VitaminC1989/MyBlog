package com.site.blog.my.core.controller.blog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//首页
@Controller
public class MyBlogController {
	@GetMapping({ "/", "/index", "index.html" })
	public String index() {
		return "blog/index";
	}
}
