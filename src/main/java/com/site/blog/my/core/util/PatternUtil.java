package com.site.blog.my.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 *
 */
public class PatternUtil {

	/**
	 * 验证只包含中英文和数字的字符串
	 *
	 * @param keyword
	 * @return
	 */
	public static Boolean validKeyword(String keyword) {
		String regex = "^[a-zA-Z0-9\u4E00-\u9FA5]+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(keyword);
		return match.matches();
	}
}
