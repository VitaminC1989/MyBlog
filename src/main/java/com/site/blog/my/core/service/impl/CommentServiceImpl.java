package com.site.blog.my.core.service.impl;

import com.site.blog.my.core.dao.BlogCommentMapper;
import com.site.blog.my.core.entity.BlogComment;
import com.site.blog.my.core.service.CommentService;
import com.site.blog.my.core.util.PageQueryUtil;
import com.site.blog.my.core.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
	@Autowired
	private BlogCommentMapper blogCommentMapper;

	/**
	* @Title: addComment
	* @Description: 添加评论
	* @param blogComment
	* @return
	* @override: @see com.site.blog.my.core.service.CommentService#addComment(com.site.blog.my.core.entity.BlogComment)   
	*/
	@Override
	public Boolean addComment(BlogComment blogComment) {
		return blogCommentMapper.insertSelective(blogComment) > 0;
	}

	/**
	* @Title: getCommentsPage
	* @Description: 获取评论分页数据
	* @param pageUtil
	* @return
	* @override: @see com.site.blog.my.core.service.CommentService#getCommentsPage(com.site.blog.my.core.util.PageQueryUtil)   
	*/
	@Override
	public PageResult getCommentsPage(PageQueryUtil pageUtil) {
		List<BlogComment> comments = blogCommentMapper.findBlogCommentList(pageUtil);
		int total = blogCommentMapper.getTotalBlogComments(pageUtil);
		PageResult pageResult = new PageResult(comments, total, pageUtil.getLimit(), pageUtil.getPage());
		return pageResult;
	}

	/**
	* @Title: getTotalComments
	* @Description: 获取评论总数
	* @return
	* @override: @see com.site.blog.my.core.service.CommentService#getTotalComments()   
	*/
	@Override
	public int getTotalComments() {
		return blogCommentMapper.getTotalBlogComments(null);
	}

	/**
	* @Title: checkDone
	* @Description: 批量审核评论
	* @param ids
	* @return
	* @override: @see com.site.blog.my.core.service.CommentService#checkDone(java.lang.Integer[])   
	*/
	@Override
	public Boolean checkDone(Integer[] ids) {
		return blogCommentMapper.checkDone(ids) > 0;
	}

	/**
	* @Title: deleteBatch
	* @Description: 批量删除评论
	* @param ids
	* @return
	* @override: @see com.site.blog.my.core.service.CommentService#deleteBatch(java.lang.Integer[])   
	*/
	@Override
	public Boolean deleteBatch(Integer[] ids) {
		return blogCommentMapper.deleteBatch(ids) > 0;
	}

	/**
	* @Title: reply
	* @Description: 回复评论
	* @param commentId
	* @param replyBody
	* @return
	* @override: @see com.site.blog.my.core.service.CommentService#reply(java.lang.Long, java.lang.String)   
	*/
	@Override
	public Boolean reply(Long commentId, String replyBody) {
		BlogComment blogComment = blogCommentMapper.selectByPrimaryKey(commentId);
		//blogComment不为空且状态为已审核，则继续后续操作
		if (blogComment != null && blogComment.getCommentStatus().intValue() == 1) {
			blogComment.setReplyBody(replyBody);
			blogComment.setReplyCreateTime(new Date());
			return blogCommentMapper.updateByPrimaryKeySelective(blogComment) > 0;
		}
		return false;
	}

	/**
	* @Title: getCommentPageByBlogIdAndPageNum
	* @Description: 通过博客id和分页数据获取评论
	* @param blogId
	* @param page
	* @return
	* @override: @see com.site.blog.my.core.service.CommentService#getCommentPageByBlogIdAndPageNum(java.lang.Long, int)   
	*/
	@Override
	public PageResult getCommentPageByBlogIdAndPageNum(Long blogId, int page) {
		if (page < 1) {
			return null;
		}
		Map params = new HashMap();
		params.put("page", page);
		//每页8条
		params.put("limit", 8);
		params.put("blogId", blogId);
		params.put("commentStatus", 1);//过滤审核通过的数据
		PageQueryUtil pageUtil = new PageQueryUtil(params);
		List<BlogComment> comments = blogCommentMapper.findBlogCommentList(pageUtil);
		if (!CollectionUtils.isEmpty(comments)) {
			int total = blogCommentMapper.getTotalBlogComments(pageUtil);
			PageResult pageResult = new PageResult(comments, total, pageUtil.getLimit(), pageUtil.getPage());
			return pageResult;
		}
		return null;
	}
}
