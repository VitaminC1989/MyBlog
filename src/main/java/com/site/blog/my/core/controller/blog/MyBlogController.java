package com.site.blog.my.core.controller.blog;

import com.site.blog.my.core.service.BlogService;
import com.site.blog.my.core.service.TagService;
import com.site.blog.my.core.util.PageResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MyBlogController {

	@Resource
	private BlogService blogService;
	@Resource
	private TagService tagService;

	/**
	 * 首页(取第一页数据)
	 *
	 * @return
	 */
	@GetMapping({ "/", "/index", "index.html" })
	public String index(HttpServletRequest request) {
		return this.page(request, 1);
	}

	/**
	 * 首页 分页数据
	 *
	 * @return
	 */
	@GetMapping({ "/page/{pageNum}" })
	public String page(HttpServletRequest request, @PathVariable("pageNum") int pageNum) {
		PageResult blogPageResult = blogService.getBlogsForIndexPage(pageNum);
		if (blogPageResult == null) {
			return "error/error_404";
		}
		// 传回【首页】博客列表
		request.setAttribute("blogPageResult", blogPageResult);
		//  传回【侧栏】最多点击和最新发布的博客
		request.setAttribute("newBlogs", blogService.getBlogListForIndexPage(1));
		request.setAttribute("hotBlogs", blogService.getBlogListForIndexPage(0));
		//	传回【侧栏】热门标签
		request.setAttribute("hotTags", tagService.getBlogTagCountForIndex());
		request.setAttribute("pageName", "首页");
		return "blog/index";
	}

}
