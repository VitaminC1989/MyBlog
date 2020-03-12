package com.site.blog.my.core.controller.blog;

import com.site.blog.my.core.controller.vo.BlogDetailVO;
import com.site.blog.my.core.entity.Blog;
import com.site.blog.my.core.entity.BlogComment;
import com.site.blog.my.core.service.BlogService;
import com.site.blog.my.core.service.CommentService;
import com.site.blog.my.core.service.TagService;
import com.site.blog.my.core.util.MyBlogUtils;
import com.site.blog.my.core.util.PageResult;
import com.site.blog.my.core.util.PatternUtil;
import com.site.blog.my.core.util.Result;
import com.site.blog.my.core.util.ResultGenerator;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class MyBlogController {

	@Resource
	private BlogService blogService;
	@Resource
	private TagService tagService;
	@Resource
	private CommentService commentService;

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

	/**
	 * 搜索列表页
	 *
	 * @return
	 */
	@GetMapping({ "/search/{keyword}" })
	public String search(HttpServletRequest request, @PathVariable("keyword") String keyword) {
		return search(request, keyword, 1);
	}

	/**
	 * 搜索列表页
	 *
	 * @return
	 */
	@GetMapping({ "/search/{keyword}/{page}" })
	public String search(HttpServletRequest request, @PathVariable("keyword") String keyword,
	        @PathVariable("page") Integer page) {
		PageResult blogPageResult = blogService.getBlogsPageBySearch(keyword, page);
		request.setAttribute("blogPageResult", blogPageResult);
		request.setAttribute("pageName", "搜索");
		request.setAttribute("pageUrl", "search");
		request.setAttribute("keyword", keyword);
		return "blog/list";
	}

	/**
	 * 分类列表页
	 *
	 * @return
	 */
	@GetMapping({ "/category/{categoryName}" })
	public String category(HttpServletRequest request, @PathVariable("categoryName") String categoryName) {
		return category(request, categoryName, 1);
	}

	/**
	 * 分类列表页
	 *
	 * @return
	 */
	@GetMapping({ "/category/{categoryName}/{page}" })
	public String category(HttpServletRequest request, @PathVariable("categoryName") String categoryName,
	        @PathVariable("page") Integer page) {
		PageResult blogPageResult = blogService.getBlogsPageByCategory(categoryName, page);
		request.setAttribute("blogPageResult", blogPageResult);
		request.setAttribute("pageName", "分类");
		request.setAttribute("pageUrl", "category");
		request.setAttribute("keyword", categoryName);
		return "blog/list";
	}

	/**
	 * 标签列表页
	 *
	 * @return
	 */
	@GetMapping({ "/tag/{tagName}" })
	public String tag(HttpServletRequest request, @PathVariable("tagName") String tagName) {
		return tag(request, tagName, 1);
	}

	/**
	 * 标签列表页
	 *
	 * @return
	 */
	@GetMapping({ "/tag/{tagName}/{page}" })
	public String tag(HttpServletRequest request, @PathVariable("tagName") String tagName,
	        @PathVariable("page") Integer page) {
		PageResult blogPageResult = blogService.getBlogsPageByTag(tagName, page);
		request.setAttribute("blogPageResult", blogPageResult);
		request.setAttribute("pageName", "标签");
		request.setAttribute("pageUrl", "tag");
		request.setAttribute("keyword", tagName);
		return "blog/list";
	}

	/**
	* @Title: detail
	* @Description: 返回文章详情和评论的分页数据
	* @param request	 http请求
	* @param blogId		   博客id
	* @param commentPage 分页数据
	* @return				
	* String
	* @throws
	*/
	@GetMapping("/blog/{blogId}")
	public String detail(HttpServletRequest request, @PathVariable("blogId") Long blogId,
	        @RequestParam(value = "commentPage", required = false, defaultValue = "1") Integer commentPage) {
		BlogDetailVO blogDetailVO = blogService.getBlogDetail(blogId);
		if (blogDetailVO != null) {
			request.setAttribute("blogDetailVO", blogDetailVO);
			request.setAttribute("commentPageResult",
			        commentService.getCommentPageByBlogIdAndPageNum(blogId, commentPage));
		}
		request.setAttribute("pageName", "详情");
		return "blog/detail";
	}

	/**
	* @Title: comment
	* @Description: 添加评论
	* @param request
	* @param session
	* @param blogId
	* @param verifyCode
	* @param commentator
	* @param email
	* @param websiteUrl
	* @param commentBody
	* @return
	* Result
	* @throws
	*/
	@PostMapping(value = "/blog/comment")
	@ResponseBody
	public Result comment(HttpServletRequest request, HttpSession session, @RequestParam Long blogId,
	        @RequestParam String verifyCode, @RequestParam String commentator, @RequestParam String email,
	        @RequestParam String websiteUrl, @RequestParam String commentBody) {
		if (StringUtils.isEmpty(verifyCode)) {
			return ResultGenerator.genFailResult("验证码不能为空");
		}
		String kaptchaCode = session.getAttribute("verifyCode") + "";
		if (StringUtils.isEmpty(kaptchaCode)) {
			return ResultGenerator.genFailResult("非法请求");
		}
		if (!verifyCode.equals(kaptchaCode)) {
			return ResultGenerator.genFailResult("验证码错误");
		}
		String ref = request.getHeader("Referer");
		if (StringUtils.isEmpty(ref)) {
			return ResultGenerator.genFailResult("非法请求");
		}
		if (null == blogId || blogId < 0) {
			return ResultGenerator.genFailResult("非法请求");
		}
		if (StringUtils.isEmpty(commentator)) {
			return ResultGenerator.genFailResult("请输入称呼");
		}
		if (StringUtils.isEmpty(email)) {
			return ResultGenerator.genFailResult("请输入邮箱地址");
		}
		if (!PatternUtil.isEmail(email)) {
			return ResultGenerator.genFailResult("请输入正确的邮箱地址");
		}
		if (StringUtils.isEmpty(commentBody)) {
			return ResultGenerator.genFailResult("请输入评论内容");
		}
		if (commentBody.trim().length() > 200) {
			return ResultGenerator.genFailResult("评论内容过长");
		}
		BlogComment comment = new BlogComment();
		comment.setBlogId(blogId);
		comment.setCommentator(MyBlogUtils.cleanString(commentator));
		comment.setEmail(email);
		// 设置更新时间为时间戳形式
		// update_time 默认值 为0000:00-00 00:00:00  查询数据库时不能转换为 java.sql.Timestap 类型【 会报错】
		comment.setReplyCreateTime(new Date());
		if (PatternUtil.isURL(websiteUrl)) {
			comment.setWebsiteUrl(websiteUrl);
		}
		comment.setCommentBody(MyBlogUtils.cleanString(commentBody));
		return ResultGenerator.genSuccessResult(commentService.addComment(comment));
	}
}
