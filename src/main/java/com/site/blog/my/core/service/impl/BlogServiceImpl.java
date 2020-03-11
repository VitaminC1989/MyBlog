package com.site.blog.my.core.service.impl;

import com.site.blog.my.core.config.Constants;
import com.site.blog.my.core.controller.vo.BlogListVO;
import com.site.blog.my.core.controller.vo.SimpleBlogListVO;
import com.site.blog.my.core.dao.BlogCategoryMapper;
import com.site.blog.my.core.dao.BlogMapper;
import com.site.blog.my.core.dao.BlogTagMapper;
import com.site.blog.my.core.dao.BlogTagRelationMapper;
import com.site.blog.my.core.entity.Blog;
import com.site.blog.my.core.entity.BlogCategory;
import com.site.blog.my.core.entity.BlogTag;
import com.site.blog.my.core.entity.BlogTagRelation;
import com.site.blog.my.core.service.BlogService;
import com.site.blog.my.core.util.PageQueryUtil;
import com.site.blog.my.core.util.PageResult;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService {

	@Autowired
	private BlogMapper blogMapper;
	@Autowired
	private BlogCategoryMapper categoryMapper;
	@Autowired
	private BlogTagMapper tagMapper;
	@Autowired
	private BlogTagRelationMapper blogTagRelationMapper;

	/**
	* @Title: saveBlog
	* @Description: 保存博客
	* @param blog
	* @return  String
	* @override: @see com.site.blog.my.core.service.BlogService#saveBlog(com.site.blog.my.core.entity.Blog)   
	*/
	@Override
	@Transactional
	public String saveBlog(Blog blog) {
		BlogCategory blogCategory = categoryMapper.selectByPrimaryKey(blog.getBlogCategoryId());

		if (blogCategory == null) {// 默认分类
			blog.setBlogCategoryId(0);
			blog.setBlogCategoryName("默认分类");
		} else {
			//设置博客分类名称
			blog.setBlogCategoryName(blogCategory.getCategoryName());
			//分类的排序值加1
			blogCategory.setCategoryRank(blogCategory.getCategoryRank() + 1);
		}
		//处理标签数据
		String[] tags = blog.getBlogTags().split(",");
		if (tags.length > 6) {
			return "标签数量限制为6";
		}
		//保存文章
		if (blogMapper.insertSelective(blog) > 0) {
			//新增的tag对象
			List<BlogTag> tagListForInsert = new ArrayList<>();
			//所有的tag对象，用于建立关系数据
			List<BlogTag> allTagsList = new ArrayList<>();
			for (int i = 0; i < tags.length; i++) {
				BlogTag tag = tagMapper.selectByTagName(tags[i]);
				if (tag == null) {
					//未定义的标签存储在集合 tagListForInsert 中
					//未定义就新增
					BlogTag tempTag = new BlogTag();
					tempTag.setTagName(tags[i]);
					tagListForInsert.add(tempTag);
				} else {
					//已定义的标签存储在集合 allTagsList 中
					allTagsList.add(tag);
				}
			}
			//新增标签数据并修改分类排序值
			if (!CollectionUtils.isEmpty(tagListForInsert)) {
				tagMapper.batchInsertBlogTag(tagListForInsert);
			}
			categoryMapper.updateByPrimaryKeySelective(blogCategory);
			//标签关系数据的集合
			List<BlogTagRelation> blogTagRelations = new ArrayList<>();
			//新增关系数据
			allTagsList.addAll(tagListForInsert);
			for (BlogTag tag : allTagsList) {
				BlogTagRelation blogTagRelation = new BlogTagRelation();
				blogTagRelation.setBlogId(blog.getBlogId());
				blogTagRelation.setTagId(tag.getTagId());
				blogTagRelations.add(blogTagRelation);
			}
			if (blogTagRelationMapper.batchInsert(blogTagRelations) > 0) {
				return "success";
			}
		}
		return "保存失败";
	}

	@Override
	public Blog getBlogById(Long blogId) {
		return blogMapper.selectByPrimaryKey(blogId);
	}

	/**
	* @Title: updateBlog
	* @Description: 更新博客
	* @param   blog
	* @return  String
	* @override: @see com.site.blog.my.core.service.BlogService#updateBlog(com.site.blog.my.core.entity.Blog)   
	*/
	@Override
	@Transactional
	public String updateBlog(Blog blog) {
		Blog blogForUpdate = blogMapper.selectByPrimaryKey(blog.getBlogId());
		if (blogForUpdate == null) {
			return "数据不存在";
		}
		blogForUpdate.setBlogTitle(blog.getBlogTitle());
		blogForUpdate.setBlogSubUrl(blog.getBlogSubUrl());
		blogForUpdate.setBlogContent(blog.getBlogContent());
		blogForUpdate.setBlogCoverImage(blog.getBlogCoverImage());
		blogForUpdate.setBlogStatus(blog.getBlogStatus());
		blogForUpdate.setEnableComment(blog.getEnableComment());
		BlogCategory blogCategory = categoryMapper.selectByPrimaryKey(blog.getBlogCategoryId());
		if (blogCategory == null) {
			blogForUpdate.setBlogCategoryId(0);
			blogForUpdate.setBlogCategoryName("默认分类");
		} else {
			//设置博客分类名称
			blogForUpdate.setBlogCategoryName(blogCategory.getCategoryName());
			blogForUpdate.setBlogCategoryId(blogCategory.getCategoryId());
			//分类的排序值加1
			blogCategory.setCategoryRank(blogCategory.getCategoryRank() + 1);
		}
		//处理标签数据
		String[] tags = blog.getBlogTags().split(",");
		if (tags.length > 6) {
			return "标签数量限制为6";
		}
		blogForUpdate.setBlogTags(blog.getBlogTags());
		//新增的tag对象
		List<BlogTag> tagListForInsert = new ArrayList<>();
		//所有的tag对象，用于建立关系数据
		List<BlogTag> allTagsList = new ArrayList<>();
		for (int i = 0; i < tags.length; i++) {
			BlogTag tag = tagMapper.selectByTagName(tags[i]);
			if (tag == null) {
				//不存在就新增
				BlogTag tempTag = new BlogTag();
				tempTag.setTagName(tags[i]);
				tagListForInsert.add(tempTag);
			} else {
				allTagsList.add(tag);
			}
		}
		//新增标签数据不为空->新增标签数据
		if (!CollectionUtils.isEmpty(tagListForInsert)) {
			tagMapper.batchInsertBlogTag(tagListForInsert);
		}
		List<BlogTagRelation> blogTagRelations = new ArrayList<>();
		//新增关系数据
		allTagsList.addAll(tagListForInsert);
		for (BlogTag tag : allTagsList) {
			BlogTagRelation blogTagRelation = new BlogTagRelation();
			blogTagRelation.setBlogId(blog.getBlogId());
			blogTagRelation.setTagId(tag.getTagId());
			blogTagRelations.add(blogTagRelation);
		}
		//修改blog信息->修改分类排序值->删除原关系数据->保存新的关系数据
		categoryMapper.updateByPrimaryKeySelective(blogCategory);
		blogTagRelationMapper.deleteByBlogId(blog.getBlogId());
		blogTagRelationMapper.batchInsert(blogTagRelations);
		if (blogMapper.updateByPrimaryKeySelective(blogForUpdate) > 0) {
			return "success";
		}
		return "修改失败";
	}

	/**
	* @Title: getBlogsPage
	* @Description: 得到博客分页结果
	* @param: 		pageUtil
	* @return:		PageResult
	* @override: @see com.site.blog.my.core.service.BlogService#getBlogsPage(com.site.blog.my.core.util.PageQueryUtil)   
	*/
	@Override
	public PageResult getBlogsPage(PageQueryUtil pageUtil) {
		List<Blog> blogList = blogMapper.findBlogList(pageUtil);
		int total = blogMapper.getTotalBlogs(pageUtil);
		PageResult pageResult = new PageResult(blogList, total, pageUtil.getLimit(), pageUtil.getPage());
		return pageResult;
	}

	/**
	* @Title: deleteBatch
	* @Description: 批量删除
	* @param:		ids
	* @return:		Boolean
	* @override: @see com.site.blog.my.core.service.BlogService#deleteBatch(java.lang.Integer[])   
	*/
	@Override
	public Boolean deleteBatch(Integer[] ids) {
		return blogMapper.deleteBatch(ids) > 0;
	}

	/**
	* @Title: getBlogsForIndexPage
	* @Description: 获取某一页（首页）博客BlogListVO值对象
	* @param page   页数
	* @return
	* @override: @see com.site.blog.my.core.service.BlogService#getBlogsForIndexPage(int)   
	*/
	@Override
	public PageResult getBlogsForIndexPage(int page) {
		Map params = new HashMap();
		params.put("page", page);
		//每页8条
		params.put("limit", 8);
		params.put("blogStatus", 1);//过滤发布状态下的数据
		PageQueryUtil pageUtil = new PageQueryUtil(params);
		List<Blog> blogList = blogMapper.findBlogList(pageUtil);
		List<BlogListVO> blogListVOS = getBlogListVOsByBlogs(blogList);
		int total = blogMapper.getTotalBlogs(pageUtil);
		PageResult pageResult = new PageResult(blogListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
		return pageResult;
	}

	/**
	* @Title: getBlogListVOsByBlogs
	* @Description: 将PO对象 blog 转换为 VO对象 BlogListVO
	* @param blogList
	* @return
	* List<BlogListVO>
	* @throws
	*/
	private List<BlogListVO> getBlogListVOsByBlogs(List<Blog> blogList) {
		List<BlogListVO> blogListVOS = new ArrayList<>();
		if (!CollectionUtils.isEmpty(blogList)) {
			List<Integer> categoryIds = blogList.stream().map(Blog::getBlogCategoryId).collect(Collectors.toList());
			Map<Integer, String> blogCategoryMap = new HashMap<>();
			if (!CollectionUtils.isEmpty(categoryIds)) {
				List<BlogCategory> blogCategories = categoryMapper.selectByCategoryIds(categoryIds);
				if (!CollectionUtils.isEmpty(blogCategories)) {
					blogCategoryMap = blogCategories.stream().collect(Collectors.toMap(BlogCategory::getCategoryId,
					        BlogCategory::getCategoryIcon, (key1, key2) -> key2));
				}
			}
			for (Blog blog : blogList) {
				BlogListVO blogListVO = new BlogListVO();
				// 将PO对象 blog 的特定信息复制进 VO对象 blogListVO
				BeanUtils.copyProperties(blog, blogListVO);
				if (blogCategoryMap.containsKey(blog.getBlogCategoryId())) {
					blogListVO.setBlogCategoryIcon(blogCategoryMap.get(blog.getBlogCategoryId()));
				} else {
					blogListVO.setBlogCategoryId(0);
					blogListVO.setBlogCategoryName("默认分类");
					blogListVO.setBlogCategoryIcon("/admin/dist/img/category/1.png");
				}
				blogListVOS.add(blogListVO);
			}
		}
		return blogListVOS;
	}

	@Override
	public List<SimpleBlogListVO> getBlogListForIndexPage(int type) {
		List<SimpleBlogListVO> simpleBlogListVOS = new ArrayList<>();
		/**
		 * type:0  最热的文章
		 * type:1  最新的文章
		 * SIDEBAR_LIMIT是侧栏显示 最多点击和最新发布 博客展示栏的记录数  默认为5
		 */

		List<Blog> blogs = blogMapper.findBlogListByType(type, Constants.SIDEBAR_LIMIT);
		if (!CollectionUtils.isEmpty(blogs)) {
			for (Blog blog : blogs) {
				SimpleBlogListVO simpleBlogListVO = new SimpleBlogListVO();
				BeanUtils.copyProperties(blog, simpleBlogListVO);
				simpleBlogListVOS.add(simpleBlogListVO);
			}
		}
		return simpleBlogListVOS;
	}

}
