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
import java.util.Date;

/**   
 * @ClassName:  Test   
 * @Description:TODO
 * @date:   2020年3月4日 下午7:33:23      
 * @Copyright:  
 */
public class Test {
	public static void main(String[] args) {
		Integer i1 = new Integer(1);
		Integer i2 = new Integer(12);
		System.out.println(i1.SIZE);
		Byte b = i1.byteValue();
		System.out.println(b.SIZE);
		System.out.println(i2.byteValue());

	}
}
