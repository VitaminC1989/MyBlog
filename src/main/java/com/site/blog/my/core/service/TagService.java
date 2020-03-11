package com.site.blog.my.core.service;

import java.util.List;

import com.site.blog.my.core.entity.BlogTagCount;
import com.site.blog.my.core.util.PageQueryUtil;
import com.site.blog.my.core.util.PageResult;

public interface TagService {

	/**
	 * 查询标签的分页数据
	 *
	 * @param pageUtil
	 * @return
	 */
	PageResult getBlogTagPage(PageQueryUtil pageUtil);

	Boolean saveTag(String tagName);

	Boolean deleteBatch(Integer[] ids);

	List<BlogTagCount> getBlogTagCountForIndex();
}
