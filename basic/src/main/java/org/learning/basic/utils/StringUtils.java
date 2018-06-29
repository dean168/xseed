package org.learning.basic.utils;

import java.util.Locale;

public abstract class StringUtils extends org.apache.commons.lang3.StringUtils {

	/**
	 * 表达式语言开始标记符号
	 */
	public static final String EVAL_PREFIX = "${";
	/**
	 * 表达式语言结束标记符号
	 */
	public static final String EVAL_SUFFIX = "}";

	public static int ints(String text) {
		if (StringUtils.isNotEmpty(text)) {
			StringBuilder textToUse = new StringBuilder(text.length());
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				if (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '.') {
					textToUse.append(c);
				}
			}
			if (textToUse.length() == 0) {
				return 0;
			}
			return Float.valueOf(textToUse.toString()).intValue();
		}
		return 0;
	}

	public static String lines(String text) {
		text = StringUtils.replace(text, " * ", "\n* ");
		text = StringUtils.replace(text, " • ", "\n• ");
		text = StringUtils.replace(text, " ＊ ", "\n＊ ");
		text = StringUtils.replace(text, " ＊　", "\n＊　");
		text = StringUtils.replace(text, " \uF0D8 ", "\n	");
		for (int i = 2; i < 20; i++) {
			text = StringUtils.replace(text, " " + i + "、", "\n" + i + "、");
			text = StringUtils.replace(text, " " + i + ".", "\n" + i + ".");
			text = StringUtils.replace(text, " " + i + "：", "\n" + i + "：");
			text = StringUtils.replace(text, " " + i + ":", "\n" + i + ":");
		}
		return text;
	}

	/**
	 * 取指定长度的前面字符串
	 * @param src 字符串
	 * @param byteLength 安字节算, 中文算两个字节
	 * @param tail tail
	 * @return 处理后的字符串
	 */
	public static String ellipsis(String src, int byteLength, String tail) {
		if (src == null) {
			return src;
		}

		StringBuffer s = new StringBuffer();
		for (int i = 0; i < src.length() && byteLength > 0; i++) {
			char c = src.charAt(i);
			s.append(c);
			byteLength -= String.valueOf(c).getBytes().length;
		}
		if (tail != null && byteLength <= 0) {
			byteLength = tail.getBytes().length;
			for (int i = s.length() - 1; i >= 0 && byteLength > 0; i--) {
				char c = s.charAt(i);
				s.deleteCharAt(i);
				byteLength -= String.valueOf(c).getBytes().length;
			}
			return s.append(tail).toString();
		} else {
			return s.toString();
		}
	}

	/**
	 * 只显示字符串的前面几个字符方法
	 * @param src 字符串
	 * @param byteLength 需要保留的长度
	 * @param tail 跟在后面的字符串
	 * @return 处理后的字符串
	 */
	public static String[] ellipse(String src[], int byteLength, String tail) {
		String[] returnSrc = new String[src.length];
		for (int i = 0; i < src.length; i++) {
			returnSrc[i] = ellipsis(src[i], byteLength, tail);
		}
		return returnSrc;
	}

	/**
	 * 字符串替换, 替换某一种符号内的内容, 比如替换 "hello ${your.name}" 后为 "hello tester"
	 * @param text 要替换的字符串
	 * @param prefix 括号符号
	 * @param suffix 括号符号
	 * @param process 替换过程
	 * @return 替换过的字符串
	 */
	public static String replace(String text, String prefix, String suffix, IStringReplaceProcess process) {

		if (isEmpty(text)) {
			return text;
		}

		StringBuffer src = new StringBuffer(text);
		int prefixIndex = 0, suffixIndex = -1;
		while ((prefixIndex = src.indexOf(prefix, prefixIndex)) != -1
				&& (suffixIndex = findMatchesSuffixIndex(src, prefixIndex, prefix, suffix)) != -1) {
			// 查找匹配的
			int start = prefixIndex + prefix.length();
			String code = src.substring(start, suffixIndex);
			// 递归内部的替换
			code = replace(code, prefix, suffix, process);
			// 替换
			String msg = process.doReplace(code, src, start, suffixIndex);
			if (prefixIndex < src.length()) {
				src.delete(prefixIndex, suffixIndex + suffix.length());
			}
			// 加上替换的内容
			if (isNotEmpty(msg) && prefixIndex <= src.length()) {
				src.insert(prefixIndex, msg);
				prefixIndex += msg.length();
			}
		}

		return src.toString();
	}

	/**
	 * 查找匹配的
	 * @param text 要替换的字符串
	 * @param formIndex 开始索引
	 * @param prefix 前缀
	 * @param suffix 后缀
	 * @return 字符的索引
	 */
	private static int findMatchesSuffixIndex(StringBuffer text, int formIndex, String prefix, String suffix) {

		int suffixIndex = text.indexOf(suffix, formIndex + prefix.length());
		int firstIndex = suffixIndex;

		while (suffixIndex != -1) {
			String code = text.substring(formIndex, suffixIndex + suffix.length());
			if (countMatches(code, prefix) != countMatches(code, suffix)) {
				suffixIndex = text.indexOf(suffix, suffixIndex + suffix.length());
			} else {
				break;
			}
		}
		return suffixIndex == -1 ? firstIndex : suffixIndex;
	}

	/**
	 * 字符串替换, 替换某一种符号内的内容, 比如替换 "hello ${your.name}" 后为 "hello tester"
	 * @param text 值
	 * @param process 处理实现
	 * @return 处理结果
	 * @see #replace(String, String, String, IStringReplaceProcess)
	 */
	public static String replaceEval(String text, IStringReplaceProcess process) {
		return replace(text, EVAL_PREFIX, EVAL_SUFFIX, process);
	}

	/**
	 * 格式化路径
	 * @param path 路径
	 * @return 格式化后的路径
	 */
	public static String cleanPath(String path) {
		return org.springframework.util.StringUtils.cleanPath(path);
	}

	/**
	 * 解析 locale
	 * @param locale locale
	 * @return locale 对象
	 */
	public static Locale parseLocaleString(String locale) {
		return org.springframework.util.StringUtils.parseLocaleString(locale);
	}

	/**
	 * 字符串变成数组
	 * @param str 字符串
	 * @param delimiters 间隔
	 * @return 字符串数组
	 */
	public static String[] tokenizeToStringArray(String str, String delimiters) {
		return org.springframework.util.StringUtils.tokenizeToStringArray(str, delimiters);
	}
}