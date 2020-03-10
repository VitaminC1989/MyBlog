package com.site.blog.my.core.service;

import com.site.blog.my.core.entity.Blog;
import com.site.blog.my.core.util.PageQueryUtil;
import com.site.blog.my.core.util.PageResult;

public interface BlogService {

	String saveBlog(Blog blog);

	/**
	 * 根据id获取博客详情
	 *
	 * @param blogId
	 * @return
	 */
	Blog getBlogById(Long blogId);

	/**
	 * 后台修改
	 *
	 * @param blog
	 * @return
	 */
	String updateBlog(Blog blog);

	PageResult getBlogsPage(PageQueryUtil pageUtil);

	Boolean deleteBatch(Integer[] ids);

}
