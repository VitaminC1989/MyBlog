package com.site.blog.my.core.util;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.util.StringUtils;

import java.util.Arrays;

public class MarkDownUtil {

	/**
	* @Title: mdToHtml
	* @Description:  解析markdown文档   markdown——>HTML
	* 将 markdown 格式的 content 内容字段转换为带有 html 标签的页面，因为在后台管理系统中操作时使用的是 Editor.md 编辑器，
	* 存储到数据库中的字段也是 markdown 格式的字段，在页面中显示的话需要进行转换
	* @param markdownString
	* @return
	* String
	* @throws
	*/
	public static String mdToHtml(String markdownString) {
		if (StringUtils.isEmpty(markdownString)) {
			return "";
		}
		java.util.List<Extension> extensions = Arrays.asList(TablesExtension.create());
		Parser parser = Parser.builder().extensions(extensions).build();
		Node document = parser.parse(markdownString);
		HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
		String content = renderer.render(document);
		return content;
	}
}
