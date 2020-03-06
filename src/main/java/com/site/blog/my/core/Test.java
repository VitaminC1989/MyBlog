/**  
 * @Title:  Test.java   
 * @Package com.site.blog.my.core   
 * @Description:    描述文件作用    
 * @date:   2020年3月4日 下午7:33:23   
 * @version V1.0 
 * @Copyright: 
 */
package com.site.blog.my.core;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**   
 * @ClassName:  Test   
 * @Description:TODO
 * @date:   2020年3月4日 下午7:33:23      
 * @Copyright:  
 */
public class Test {
	public static void main(String[] args) {
		String resultString = "123456";
		String charsetname = "UTF-8";

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bs = md.digest(resultString.getBytes(charsetname));
			System.out.println(bs);
			String str = new String(bs, "UTF-8");
			System.out.println(str);

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//		if (charsetname == null || "".equals(charsetname))
		//			resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
		//		else
		//			resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));

	}
}
