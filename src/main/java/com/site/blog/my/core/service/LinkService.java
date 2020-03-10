package com.site.blog.my.core.service;

import com.site.blog.my.core.entity.BlogLink;
import com.site.blog.my.core.util.PageQueryUtil;
import com.site.blog.my.core.util.PageResult;

import java.util.List;
import java.util.Map;

public interface LinkService {
	/**
	 * 查询友链的分页数据
	 *
	 * @param pageUtil
	 * @return
	 */
	PageResult getBlogLinkPage(PageQueryUtil pageUtil);

	Boolean saveLink(BlogLink link);

	BlogLink selectById(Integer id);

	Boolean updateLink(BlogLink tempLink);

	Boolean deleteBatch(Integer[] ids);
}
