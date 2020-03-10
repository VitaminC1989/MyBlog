package com.site.blog.my.core.controller.admin;

import com.site.blog.my.core.config.Constants;
import com.site.blog.my.core.entity.Blog;
import com.site.blog.my.core.service.BlogService;
import com.site.blog.my.core.service.CategoryService;
import com.site.blog.my.core.util.MyBlogUtils;
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
import java.util.Random;

@Controller
@RequestMapping("/admin")
public class BlogController {

	@Resource
	private CategoryService categoryService;
	@Resource
	private BlogService blogService;

	@GetMapping("/blogs/edit")
	public String edit(HttpServletRequest request) {
		// 更改侧栏“编辑文章” 选项样式为已选中，即蓝底
		request.setAttribute("path", "edit");
		// 在文章编辑页面显示分类信息
		request.setAttribute("categories", categoryService.getAllCategories());
		return "admin/edit";
	}

	// 存储文章
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
		//	blog.setUpdateTime(new Date());
		String saveBlogResult = blogService.saveBlog(blog);
		if ("success".equals(saveBlogResult)) {
			return ResultGenerator.genSuccessResult("添加成功");
		} else {
			return ResultGenerator.genFailResult(saveBlogResult);
		}
	}

	// 存储上传文件
	@PostMapping("/blogs/md/uploadfile")
	public void uploadFileByEditormd(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam(name = "editormd-image-file", required = true) MultipartFile file)
	        throws IOException, URISyntaxException {
		String fileName = file.getOriginalFilename();
		String suffixName = fileName.substring(fileName.lastIndexOf("."));
		//生成文件名称通用方法  日期 + 时间 + 随机数 + 后缀名
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Random r = new Random();
		StringBuilder tempName = new StringBuilder();
		tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
		String newFileName = tempName.toString();
		//创建文件
		//上传文件绝对路径      	C:/MyJava/project/spring/My-Blog/src/main/resources/upload/
		File destFile = new File(Constants.FILE_UPLOAD_DIC + newFileName);

		/* URI语法：
		/* protocol://userInfo@host:port/path?query#fragment
		/* 协议://用户信息@主机名:端口/路径?查询#片段
		/* fileUrl = protocol://userInfo@host:port/upload/图片文件名字
		/* fileUrl 是 上传图片文件的URL地址（不一定是物理路径）
		/* fileUrl是配置的虚拟路径，用来映射文件的真实绝对路径   路径映射在包 com.site.blog.my.core.config 类 MyBlogWebMvcConfigurer
		 */
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

}
