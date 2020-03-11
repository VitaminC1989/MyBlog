/**  
 * @Title:  markdown.java   
 * @Package com.site.blog.my.core   
 * @Description:    描述文件作用    
 * @date:   2020年3月11日 下午3:11:22   
 * @version V1.0 
 * @Copyright: 
 */
package com.site.blog.my.core;

/**   
 * @ClassName:  markdown   
 * @Description:TODO
 * @date:   2020年3月11日 下午3:11:22      
 * @Copyright:  
 */
public class markdown {
public final static String MD = "---\r\n" + 
		"tags:\r\n" + 
		"  - java\r\n" + 
		"  - 中级\r\n" + 
		"flag: blue\r\n" + 
		"---\r\n" + 
		"\r\n" + 
		"# I/O\r\n" + 
		"\r\n" + 
		"## 字节流\r\n" + 
		"\r\n" + 
		"### flush()方法\r\n" + 
		"\r\n" + 
		"立即把数据写入到硬盘，而不是等缓存满了才写出去。 这时候就需要用到flush\r\n" + 
		"\r\n" + 
		"```java\r\n" + 
		"FileOutput fos = new FileOutputStream(file);\r\n" + 
		"fos.write(data);\r\n" + 
		"// 关闭输出流时 使用flush()方法\r\n" + 
		"// fluch() 将缓冲区数据强行输出 确保所有数据全部写入文件\r\n" + 
		"fos.flush();\r\n" + 
		"fos.close();\r\n" + 
		"```\r\n" + 
		"\r\n" + 
		"## 文件编码\r\n" + 
		"\r\n" + 
		"FileInputStream：    文件输入字节流\r\n" + 
		"FileReader:              文件输入字符流， 不可设置编码，默认使用操作系统的编码\r\n" + 
		"InputStreamReader:  可设置编码 \r\n" + 
		"![IO]($resource/IO.png)\r\n" + 
		"\r\n" + 
		"```java\r\n" + 
		"InputStreamReader isr = \r\n" + 
		"new InputStreamReader(new FileInputStream(f),Charset.forName(\"UTF-8\"))\r\n" + 
		"```\r\n" + 
		"\r\n" + 
		"【注意】\r\n" + 
		"如果用记事本根据UTF-8编码保存汉字就会在最前面生成一段标示符，这个标示符用于表示该文件是使用UTF-8编码的。\r\n" + 
		"\r\n" + 
		"## 数据流\r\n" + 
		"\r\n" + 
		"**格式化顺序读写**\r\n" + 
		"使用数据流的writeUTF()和readUTF() 可以进行数据的格式化顺序读写\r\n" + 
		"通过DataOutputStream 向文件**顺序写出** 布尔值，整数和字符串。 \r\n" + 
		"然后再通过DataInputStream **顺序读入**这些数据。\r\n" + 
		"【注意】\r\n" + 
		"用DataInputStream 读取的文件必须是由DataOutputStream 写出的，否则会出现EOFException，因为DataOutputStream 在写出的时候会做一些特殊标记，只有DataInputStream 才能成功的读取。\r\n" + 
		"\r\n" + 
		"## 对象流\r\n" + 
		"\r\n" + 
		"【ObjectStream】\r\n" + 
		"对象流：可以直接把一个对象以流的形式传输给其他的介质，比如硬盘。\r\n" + 
		"一个对象以流的形式进行传输，叫做**序列化**。\r\n" + 
		" 该对象所对应的类，必须是实现**Serializable**接口\r\n" + 
		"\r\n" + 
		"## System.in\r\n" + 
		"\r\n" + 
		"            // Scanner对象连续读取整数和字符串时要注意\r\n" + 
		"    		Scanner s = new Scanner(System.in);\r\n" + 
		"    		// Scanner对象读取整数时只读取整数  不读取换行符\r\n" + 
		"    		int i = s.nextInt();\r\n" + 
		"    		// 在读取下一个字符串前，用.nextLine()先读取掉换行符\r\n" + 
		"    		s.nextLine();\r\n" + 
		"    		String str = s.nextLine();\r\n" + 
		"    	}\r\n" + 
		"\r\n" + 
		"# 集合框架\r\n" + 
		"\r\n" + 
		"## ArrayList\r\n" + 
		"\r\n" + 
		"数组的局限：存储空间在声明时就确定了，不能动态扩展和移除\r\n" + 
		"ArrayList因此被引入，ArrayList可以动态扩展和移除存储空间\r\n" + 
		"\r\n" + 
		"## 泛型 Generic\r\n" + 
		"\r\n" + 
		"不指定泛型的容器，可以存放任何类型的元素\r\n" + 
		"指定了泛型的容器，只能存放指定类型的元素以及其子类\r\n" + 
		"\r\n" + 
		"## 遍历\r\n" + 
		"\r\n" + 
		"1. for循环\r\n" + 
		"2. 迭代器\r\n" + 
		"3. foreach\r\n" + 
		"\r\n" + 
		"【注意】：\r\n" + 
		"不能够在使用Iterator和增强for循环遍历数据的同时，删除数据，否则会抛出ConcurrentModificationException\r\n" + 
		"解决办法1：准备一个临时容器，专门用来保存需要删除的对象。 然后再删掉\r\n" + 
		"解决办法2：用迭代器遍历时使用Iterator.remove()方法\r\n" + 
		"\r\n" + 
		"# 泛型\r\n" + 
		"[localhost://C:\\Users\\17412\\Documents\\Yu Writer Libraries\\Default\\Java中级.resource\\多线程.xmind](localhost://C:%5CUsers%5C17412%5CDocuments%5CYu%20Writer%20Libraries%5CDefault%5CJava%E4%B8%AD%E7%BA%A7.resource%5C%E5%A4%9A%E7%BA%BF%E7%A8%8B.xmind)\r\n" + 
		"## 通配符\r\n" + 
		"\r\n" + 
		"1. ? extends ：只取不存（只输出不输入）\r\n" + 
		"2. ? super ：   只存不取（只输入不输出）(实际上可以通过强制类型转换取出，但是有风险)\r\n" + 
		"3. ? ：            只取不存（只输出不输入）\r\n" + 
		"4. 总结：小容器->大容器\r\n" + 
		"   ![泛型通配符]($resource/%E6%B3%9B%E5%9E%8B%E9%80%9A%E9%85%8D%E7%AC%A6.jpg)\r\n" + 
		"\r\n" + 
		"## 泛型转型\r\n" + 
		"\r\n" + 
		"父类泛型和子类泛型不能互相转换\r\n" + 
		"\r\n" + 
		"# 多线程\r\n" + 
		"![多线程 2]($resource/%E5%A4%9A%E7%BA%BF%E7%A8%8B%202.jpg)\r\n" + 
		"\r\n" + 
		""; 
}
