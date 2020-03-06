package com.site.blog.my.core.service.impl;

import com.site.blog.my.core.dao.BlogCategoryMapper;
import com.site.blog.my.core.entity.BlogCategory;
import com.site.blog.my.core.service.CategoryService;
import com.site.blog.my.core.util.PageQueryUtil;
import com.site.blog.my.core.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private BlogCategoryMapper blogCategoryMapper;

	@Override
	public PageResult getBlogCategoryPage(PageQueryUtil pageUtil) {
		List<BlogCategory> categoryList = blogCategoryMapper.findCategoryList(pageUtil);
		int total = blogCategoryMapper.getTotalCategories(pageUtil);
		PageResult pageResult = new PageResult(categoryList, total, pageUtil.getLimit(), pageUtil.getPage());
		return pageResult;
	}

	@Override
	public int getTotalCategories() {
		return blogCategoryMapper.getTotalCategories(null);
	}

	@Override
	public Boolean saveCategory(String categoryName, String categoryIcon) {
		BlogCategory temp = blogCategoryMapper.selectByCategoryName(categoryName);
		// 不存在重名分类则新建并插入
		if (temp == null) {
			BlogCategory blogCategory = new BlogCategory();
			blogCategory.setCategoryName(categoryName);
			blogCategory.setCategoryIcon(categoryIcon);
			return blogCategoryMapper.insertSelective(blogCategory) > 0;
		}
		return false;
	}

	@Override
	@Transactional
	public Boolean updateCategory(Integer categoryId, String categoryName, String categoryIcon) {
		BlogCategory blogCategory = blogCategoryMapper.selectByPrimaryKey(categoryId);
		if (blogCategory != null) {
			blogCategory.setCategoryIcon(categoryIcon);
			blogCategory.setCategoryName(categoryName);
			return blogCategoryMapper.updateByPrimaryKeySelective(blogCategory) > 0;
		}
		return false;
	}

	@Override
	@Transactional
	public Boolean deleteBatch(Integer[] ids) {
		if (ids.length < 1) {
			return false;
		}
		//删除分类数据
		return blogCategoryMapper.deleteBatch(ids) > 0;
	}

	@Override
	public List<BlogCategory> getAllCategories() {
		return blogCategoryMapper.findCategoryList(null);
	}

	@Override
	public BlogCategory selectById(Integer id) {
		return blogCategoryMapper.selectByPrimaryKey(id);
	}

}
