package com.site.blog.my.core.util;

import java.net.URI;

/**
 * 生成上传图片的统一资源标识符
 */
public class MyBlogUtils {

	public static URI getHost(URI uri) {
		URI effectiveURI = null;
		try {
			effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
		} catch (Throwable var4) {
			effectiveURI = null;
		}
		return effectiveURI;
	}
}
