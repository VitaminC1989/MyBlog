package com.site.blog.my.core.service.impl;

import com.site.blog.my.core.dao.BlogCategoryMapper;
import com.site.blog.my.core.dao.BlogMapper;
import com.site.blog.my.core.dao.BlogTagMapper;
import com.site.blog.my.core.dao.BlogTagRelationMapper;
import com.site.blog.my.core.entity.Blog;
import com.site.blog.my.core.entity.BlogCategory;
import com.site.blog.my.core.entity.BlogTag;
import com.site.blog.my.core.entity.BlogTagRelation;
import com.site.blog.my.core.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
}
