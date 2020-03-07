package com.site.blog.my.core.dao;

import com.site.blog.my.core.entity.Blog;
import com.site.blog.my.core.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BlogMapper {
	int deleteByPrimaryKey(Long blogId);

	int insert(Blog record);

	int insertSelective(Blog record);

	Blog selectByPrimaryKey(Long blogId);

	int updateByPrimaryKeySelective(Blog record);

	int updateByPrimaryKeyWithBLOBs(Blog record);

	int updateByPrimaryKey(Blog record);
}