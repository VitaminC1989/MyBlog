package com.site.blog.my.core.controller.admin;

import com.site.blog.my.core.util.MyBlogUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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

	@GetMapping("/blogs/edit")
	public String edit(HttpServletRequest request) {
		request.setAttribute("path", "edit");
		return "admin/edit";
	}

	@PostMapping("/blogs/md/uploadfile")
	public void uploadFileByEditormd(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam(name = "editormd-image-file", required = true) MultipartFile file)
	        throws IOException, URISyntaxException {
		//上传文件的默认url前缀，根据部署设置自行修改
		//String FILE_UPLOAD_DIC = "/home/project/upload/";
		String FILE_UPLOAD_DIC = "C:/MyJava/project/spring/My-Blog/src/main/resources/upload/";
		String fileName = file.getOriginalFilename();
		String suffixName = fileName.substring(fileName.lastIndexOf("."));
		//生成文件名称通用方法
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Random r = new Random();
		StringBuilder tempName = new StringBuilder();
		tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
		String newFileName = tempName.toString();
		// 创建文件  
		// 路径FILE_UPLOAD_DIC + newFileName 是上传图片文件存储的绝对路径
		File destFile = new File(FILE_UPLOAD_DIC + newFileName);
		/* URI语法：
		/* protocol://userInfo@host:port/path?query#fragment
		/* 协议://用户信息@主机名:端口/路径?查询#片段
		/* fileUrl = protocol://userInfo@host:port/upload/图片文件名字
		/* fileUrl 是 上传图片文件的URL地址（不一定是物理路径）
		/**/

		// fileUrl是配置的虚拟路径，用来映射文件的真实绝对路径
		String fileUrl = MyBlogUtils.getHost(new URI(request.getRequestURL() + "")) + "/upload/" + newFileName;
		File fileDirectory = new File(FILE_UPLOAD_DIC);
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
