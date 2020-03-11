package com.site.blog.my.core.controller.admin;

import com.site.blog.my.core.config.Constants;
import com.site.blog.my.core.entity.Blog;
import com.site.blog.my.core.service.BlogService;
import com.site.blog.my.core.service.CategoryService;
import com.site.blog.my.core.util.MyBlogUtils;
import com.site.blog.my.core.util.PageQueryUtil;
import com.site.blog.my.core.util.Result;
import com.site.blog.my.core.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/admin")
public class BlogController {

	@Resource
	private CategoryService categoryService;
	@Resource
	private BlogService blogService;

	@GetMapping("/blogs")
	public String list(HttpServletRequest request) {
		request.setAttribute("path", "blogs");
		return "admin/blog";
	}

	/**
	* @Title: list
	* @Description: 返回博客文章分页数据
	* @param: 		params
	* @return: 		Result
	* @throws
	*/
	@GetMapping("/blogs/list")
	@ResponseBody
	public Result list(@RequestParam Map<String, Object> params) {
		if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
			return ResultGenerator.genFailResult("参数异常！");
		}
		PageQueryUtil pageUtil = new PageQueryUtil(params);
		return ResultGenerator.genSuccessResult(blogService.getBlogsPage(pageUtil));
	}

	@GetMapping("/blogs/edit")
	public String edit(HttpServletRequest request) {
		request.setAttribute("path", "edit");
		request.setAttribute("categories", categoryService.getAllCategories());
		return "admin/edit";
	}

	@GetMapping("/blogs/edit/{blogId}")
	public String edit(HttpServletRequest request, @PathVariable("blogId") Long blogId) {
		request.setAttribute("path", "edit");
		Blog blog = blogService.getBlogById(blogId);
		if (blog == null) {
			return "error/error_400";
		}
		request.setAttribute("blog", blog);
		request.setAttribute("categories", categoryService.getAllCategories());
		return "admin/edit";
	}

	/**
	* @Title: save
	* @Description: 保存博客
	* @param: @param blogTitle
	* @param: @param blogSubUrl
	* @param: @param blogCategoryId
	* @param: @param blogTags
	* @param: @param blogContent
	* @param: @param blogCoverImage
	* @param: @param blogStatus
	* @param: @param enableComment
	* @return: Result
	* @throws
	*/
	@PostMapping("/blogs/save")
	@ResponseBody
	public Result save(@RequestParam("blogTitle") String blogTitle,
	        @RequestParam(name = "blogSubUrl", required = false) String blogSubUrl,
	        @RequestParam("blogCategoryId") Integer blogCategoryId, @RequestParam("blogTags") String blogTags,
	        @RequestParam("blogContent") String blogContent, @RequestParam("blogCoverImage") String blogCoverImage,
	        @RequestParam("blogStatus") Byte blogStatus, @RequestParam("enableComment") Byte enableComment) {
		if (StringUtils.isEmpty(blogTitle)) {
			return ResultGenerator.genFailResult("请输入文章标题");
		}
		if (blogTitle.trim().length() > 150) {
			return ResultGenerator.genFailResult("标题过长");
		}
		if (StringUtils.isEmpty(blogTags)) {
			return ResultGenerator.genFailResult("请输入文章标签");
		}
		if (blogTags.trim().length() > 150) {
			return ResultGenerator.genFailResult("标签过长");
		}
		if (blogSubUrl.trim().length() > 150) {
			return ResultGenerator.genFailResult("路径过长");
		}
		if (StringUtils.isEmpty(blogContent)) {
			return ResultGenerator.genFailResult("请输入文章内容");
		}
		if (blogTags.trim().length() > 100000) {
			return ResultGenerator.genFailResult("文章内容过长");
		}
		if (StringUtils.isEmpty(blogCoverImage)) {
			return ResultGenerator.genFailResult("封面图不能为空");
		}
		Blog blog = new Blog();
		blog.setBlogTitle(blogTitle);
		blog.setBlogSubUrl(blogSubUrl);
		blog.setBlogCategoryId(blogCategoryId);
		blog.setBlogTags(blogTags);
		blog.setBlogContent(blogContent);
		blog.setBlogCoverImage(blogCoverImage);
		blog.setBlogStatus(blogStatus);
		blog.setEnableComment(enableComment);
		// 设置更新时间为时间戳形式
		// update_time 默认值 为0000:00-00 00:00:00  查询数据库时不能转换为 java.sql.Timestap 类型【 会报错】
		blog.setUpdateTime(new Date());
		String saveBlogResult = blogService.saveBlog(blog);
		if ("success".equals(saveBlogResult)) {
			return ResultGenerator.genSuccessResult("添加成功");
		} else {
			return ResultGenerator.genFailResult(saveBlogResult);
		}
	}

	/**
	* @Title: uploadFileByEditormd
	* @Description: 处理上传图片
	* @param request
	* @param response
	* @param file
	* @throws IOException
	* @throws URISyntaxException
	* void
	* @throws
	*/
	@PostMapping("/blogs/md/uploadfile")
	public void uploadFileByEditormd(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam(name = "editormd-image-file", required = true) MultipartFile file)
	        throws IOException, URISyntaxException {
		String fileName = file.getOriginalFilename();
		String suffixName = fileName.substring(fileName.lastIndexOf("."));
		//生成文件名称通用方法
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Random r = new Random();
		StringBuilder tempName = new StringBuilder();
		tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
		String newFileName = tempName.toString();
		//创建文件
		File destFile = new File(Constants.FILE_UPLOAD_DIC + newFileName);
		String fileUrl = MyBlogUtils.getHost(new URI(request.getRequestURL() + "")) + "/upload/" + newFileName;
		File fileDirectory = new File(Constants.FILE_UPLOAD_DIC);
		try {
			if (!fileDirectory.exists()) {
				if (!fileDirectory.mkdir()) {
					throw new IOException("文件夹创建失败,路径为：" + fileDirectory);
				}
			}
			file.transferTo(destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			request.setCharacterEncoding("utf-8");
			response.setHeader("Content-Type", "text/html");
			response.getWriter().write("{\"success\": 1, \"message\":\"success\",\"url\":\"" + fileUrl + "\"}");
		} catch (UnsupportedEncodingException e) {
			response.getWriter().write("{\"success\":0}");
		} catch (IOException e) {
			response.getWriter().write("{\"success\":0}");
		}
	}

	/**
	* @Title: update
	* @Description: 更新博客
	* @param blogId
	* @param blogTitle
	* @param blogSubUrl
	* @param blogCategoryId
	* @param blogTags
	* @param blogContent
	* @param blogCoverImage
	* @param blogStatus
	* @param enableComment
	* @return
	* Result
	* @throws
	*/
	@PostMapping("/blogs/update")
	@ResponseBody
	public Result update(@RequestParam("blogId") Long blogId, @RequestParam("blogTitle") String blogTitle,
	        @RequestParam(name = "blogSubUrl", required = false) String blogSubUrl,
	        @RequestParam("blogCategoryId") Integer blogCategoryId, @RequestParam("blogTags") String blogTags,
	        @RequestParam("blogContent") String blogContent, @RequestParam("blogCoverImage") String blogCoverImage,
	        @RequestParam("blogStatus") Byte blogStatus, @RequestParam("enableComment") Byte enableComment) {
		if (StringUtils.isEmpty(blogTitle)) {
			return ResultGenerator.genFailResult("请输入文章标题");
		}
		if (blogTitle.trim().length() > 150) {
			return ResultGenerator.genFailResult("标题过长");
		}
		if (StringUtils.isEmpty(blogTags)) {
			return ResultGenerator.genFailResult("请输入文章标签");
		}
		if (blogTags.trim().length() > 150) {
			return ResultGenerator.genFailResult("标签过长");
		}
		if (blogSubUrl.trim().length() > 150) {
			return ResultGenerator.genFailResult("路径过长");
		}
		if (StringUtils.isEmpty(blogContent)) {
			return ResultGenerator.genFailResult("请输入文章内容");
		}
		if (blogTags.trim().length() > 100000) {
			return ResultGenerator.genFailResult("文章内容过长");
		}
		if (StringUtils.isEmpty(blogCoverImage)) {
			return ResultGenerator.genFailResult("封面图不能为空");
		}
		Blog blog = new Blog();
		blog.setBlogId(blogId);
		blog.setBlogTitle(blogTitle);
		blog.setBlogSubUrl(blogSubUrl);
		blog.setBlogCategoryId(blogCategoryId);
		blog.setBlogTags(blogTags);
		blog.setBlogContent(blogContent);
		blog.setBlogCoverImage(blogCoverImage);
		blog.setBlogStatus(blogStatus);
		blog.setEnableComment(enableComment);
		blog.setUpdateTime(new Date());
		String updateBlogResult = blogService.updateBlog(blog);
		if ("success".equals(updateBlogResult)) {
			return ResultGenerator.genSuccessResult("修改成功");
		} else {
			return ResultGenerator.genFailResult(updateBlogResult);
		}
	}

	/**
	* @Title: delete
	* @Description: 批量删除博客  （软删除，tb_blog.is_deleted = 1) 
	* @param ids
	* @return
	* Result
	* @throws
	*/
	@PostMapping("/blogs/delete")
	@ResponseBody
	public Result delete(@RequestBody Integer[] ids) {
		if (ids.length < 1) {
			return ResultGenerator.genFailResult("参数异常！");
		}
		if (blogService.deleteBatch(ids)) {
			return ResultGenerator.genSuccessResult();
		} else {
			return ResultGenerator.genFailResult("删除失败");
		}
	}


}
