package com.site.blog.my.core.service;

import java.util.List;

import com.site.blog.my.core.controller.vo.SimpleBlogListVO;
import com.site.blog.my.core.entity.Blog;
import com.site.blog.my.core.util.PageQueryUtil;
import com.site.blog.my.core.util.PageResult;

public interface BlogService {

	String saveBlog(Blog blog);

	/**
	   *    根据id获取博客详情
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

	/**
	* 首页侧边栏数据列表
	* type:
	* 0-点击最多 
	* 1-最新发布
	*
	* @param type
	* @return
	*/
	List<SimpleBlogListVO> getBlogListForIndexPage(int type);

	/**
	 * 获取首页文章列表
	 *
	 * @param page
	 * @return
	 */
	PageResult getBlogsForIndexPage(int page);

	/**
	 * 根据搜索关键字获取首页文章列表
	 *
	 * @param page
	 * @return
	 */
	PageResult getBlogsPageBySearch(String keyword, int page);

	/**
	* 根据分类获取文章列表
	*
	* @param categoryId
	* @param page
	* @return
	*/
	PageResult getBlogsPageByCategory(String categoryId, int page);

	/**
	 * 根据标签获取文章列表
	 *
	 * @param tagName
	 * @param page
	 * @return
	 */
	PageResult getBlogsPageByTag(String tagName, int page);
}
