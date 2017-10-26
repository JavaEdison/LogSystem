package com.cignacmb.member.mis.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用常用字符串、对象、集合工具类
 * @author j1mei
 * 2016年9月8日 下午3:03:34
 */
@SuppressWarnings("rawtypes")
public class CommonUtil {

    /**
     * 判断字符串是否为空
     * @param str 验证字符串
     * @return Boolean
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:05:26
     */
    public static boolean isBlank(String str) {
        if (null == str || "".equals(str) || str.length() == 0
                || "null".equals(str) || "".equals(str.replaceAll(" ", ""))){
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否不为空
     * @param str 验证字符串
     * @return Boolean
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:05:26
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 判断字符串是否为空
     * @param str 验证字符串
     * @return Boolean
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:05:26
     */
    public static boolean isEmpty(String str) {
        if (null == str || "".equals(str)){
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否不为空
     * @param str 验证字符串
     * @return Boolean
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:05:26
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断对象是否为空
     * @param obj 验证的对象
     * @return Boolean
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:07:00
     */
    public static boolean isNull(Object obj) {
        return (null == obj || "".equals(obj)) ? true : false;
    }

    /**
     * 判断对象是否不为空
     * @param obj 验证的对象
     * @return Boolean
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:07:00
     */
    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    /**
     * 判断集合对象是否为空
     * @param list 验证的list对象
     * @return Boolean
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:07:49
     */
    public static boolean isNullList(List list) {
        return null == list || list.size() == 0;
    }

    /**
     * 判断集合对象是否不为空
     * @param list 验证的list对象
     * @return Boolean
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:07:49
     */
    public static boolean isNotNullList(List list) {
        return !isNullList(list);
    }
    
    public static String regetObject(String string) {
        return isBlank(string)?"":string;
    }

    /**
     * 获取32位大写UUID序号
     * @return 32位长度大写字符串
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:08:56
     */
    public static String newUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").trim();
    }

    /**
     * 获取指定长度的随机字符串
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String random(int length) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 获取指定位数的随机数字串
     * @param length 指定的位数
     * @return 指定位数的随机数字串
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:23:44
     */
    public static String newRandomCode(int length) {
        String mill = Long.toString(System.currentTimeMillis());
        return mill.substring(mill.length() - length);
    }

    /**
     * 计算周岁
     * @param s 起始日期
     * @param c 截止日期
     * @return 周岁
     * @creator MeiGaoBang
     * @createdate 2015-5-15 下午8:24:23
     */
    public static int calYeay(final Date s, final Date c) {
        if (s.getTime() > c.getTime()){
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(s);
        int i = 0;
        while (true) {
            if (cal.getTime().getTime() <= c.getTime()) {
                cal.setTime(s);
                i++;
                cal.add(Calendar.YEAR, i);
            } else {
                break;
            }
        }
        return i - 1;
    }

    /**
     * 获取发送的信息内容
     * @param param param 参数集合
     * @param msgTemp msgTemp 信息模板
     * @return 返回信息模板数据
     */
    public static String getSendMsg(Map<String, String> param, String msgTemp) {
        String msg = "";
        if (param != null) {
            Iterator<String> keys = param.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next().trim();
                msg = msgTemp.replace("#" + key, param.get(key));
            }
        }
        return msg;
    }

    /**
     * 检查字符串是否是double型数
     * @param str 传递的字符串
     * @return true
     */
    public static boolean checkDouble(String str) {
        String regex = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
        return match(str, regex);
    }

    /**
     * 检验是否是整数
     * @param str 传递的字符串
     * @return true or false
     */
    public static boolean checkInteger(String str) {
        String regex = "[0-9]*";
        return match(str, regex);
    }

    /**
     * 检查是否是数字
     * @param str 外部传递的字符串
     * @return true　or false
     */
    public static boolean isNumeric(String str) {
        return match(str, "^(-?\\d+)(\\.\\d+)?$");
    }

   /**
    * 检验是否是短型日期
    * @param str 传递的字符串
    * @return true or false
    */
    public final static boolean checkShortDate(String str) {
        String datePattern1 = "\\d{4}-\\d{2}-\\d{2}";
        String datePattern2 = "^((\\d{2}(([02468][048])|([13579][26]))"
                + "[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|"
                + "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?"
                + "((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?("
                + "(((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?"
                + "((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
        if (match(str, datePattern1)) {
            return match(str, datePattern2);
        }
        return false;
    }

    /**
     * 检验是否是长型日期
     * @param str 传递的字符串
     * @return true or false
     */
    public final static boolean checkLongDate(String str) {
        String datePattern1 = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
        return match(str, datePattern1);
    }

    /**
     * 通用验证表达式
     * @param str 要验证的字符串
     * @param reg 正则表达式
     * @return true or false
     */
    private final static boolean match(String str, String reg) {
        if (isEmpty(str)){
            return false;
        }
        return Pattern.compile(reg).matcher(str).matches();
    }

    /**
     * 获取本机IP地址
     * @return IP
     */
    public static String getLocalIP() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces
                        .nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (null != ip && ip instanceof Inet4Address) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断传入的参数是否是数组的元素
     * @param param 传入参数
     * @param value 数组
     * @return true or false
     */
    public static boolean isInArray(String param, String[] value) {
        if (param == null){
            return false;
        }
        if (value == null){
            return false;   
        }
        for (int i = 0; i < value.length; i++) {
            if (param.equals(value[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断数组是否为空
     * @param objs 数组
     * @return true or false
     */
    public final static boolean isNull(Object[] objs) {
        if (objs == null || objs.length == 0){
            return true;   
        }
        return false;
    }
    
    /**
     * 判断Interger类型是否为空
     * @param integer 传递的整数
     * @return true or false
     */
    public final static boolean isNull(Integer integer) {
        if (integer == null || integer == 0){
            return true;
        }
        return false;
    }
    
    /**
     * 判断集合是否为空
     * @param collection 外部传递的集合
     * @return true or false
     */
    public final static boolean isNull(Collection<Object> collection) {
        if (collection == null || collection.size() == 0){
            return true;
        }
        return false;
    }
    
    /**
     * 判断Map是否为空
     * @param map 待验证到MAP
     * @return true or false
     */
    public final static boolean isNull(Map<Object,Object> map) {
        if (map == null || map.size() == 0){
            return true;
        }
        return false;
    }

    /**
     * 判断long类型是否为空
     * @param longs 待验证的longs
     * @return true or false
     */
    public final static boolean isNull(Long longs) {
        if (longs == null || longs == 0){
            return true;
        }
        return false;
    }

    /**
     * 匹配URL地址
     * @param str 带匹配的地址
     * @return true or false
     */
    public final static boolean isUrl(String str) {
        return match(str, "^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$");
    }

    /**
     * 匹配密码，以字母开头，长度在6-12之间，只能包含字符、数字和下划线。
     * 
     * @param str 待验证的密码
     * @return true or false
     */
    public final static boolean isPwd(String str) {
        return match(str, "^[a-zA-Z]\\w{6,12}$");
    }


    /**
     * 验证字符，只能包含中文、英文、数字、下划线等字符。
     * @param str 待验证的字符
     * @return true or false
     */
    public final static boolean stringCheck(String str) {
        return match(str, "^[a-zA-Z0-9\u4e00-\u9fa5-_]+$");
    }

    /**
     * 匹配Email地址
     * @param str 待验证的email地址
     * @return true or false
     */
    public final static boolean isEmail(String str) {
        return match(str, "[a-zA-Z_]{1,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}");
    }

    /**
     * 匹配非负整数（正整数+0）
     * @param str 
     * @return
     */
    public final static boolean isInteger(String str) {
        return match(str, "^[+]?\\d+$");
    }

    /**
     * 只能输入数字
     * @param str
     * @return
     */
    public final static boolean isDigits(String str) {
        return match(str, "^[0-9]*$");
    }

    /**
     * 匹配正浮点数
     * @param str
     * @return
     */
    public final static boolean isFloat(String str) {
        return match(str, "^[-\\+]?\\d+(\\.\\d+)?$");
    }

    /**
     * 联系电话(手机/电话皆可)验证
     * 
     * @param text
     * @return
     */
    public final static boolean isTel(String text) {
        if (isMobile(text) || isPhone(text))
            return true;
        return false;
    }

    /**
     * 电话号码验证
     * @param text
     * @return
     */
    public final static boolean isPhone(String text) {
        return match(text, "^(\\d{3,4}-?)?\\d{7,9}$");
    }

    /**
     * 手机号码验证
     * @param text
     * @return
     */
    public final static boolean isMobile(String text) {
        if (text.length() != 11)
            return false;
        return match(text,
                "^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(17[0-9]{1})|(14[0-9]{1}))+\\d{8})$");
    }

    /**
     * 身份证号码验证
     * @param text
     * @return
     */
    public final static boolean isIdCardNo(String text) {
        return match(text, "^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$");
    }

    /**
     * 邮政编码验证
     * @param text
     * @return
     */
    public final static boolean isZipCode(String text) {
        return match(text, "^[0-9]{6}$");
    }

    /**
     * 判断整数num是否等于0
     * @param num
     * @return
     */
    public final static boolean isIntEqZero(int num) {
        return num == 0;
    }

    /**
     * 判断整数num是否大于0
     * @param num
     * @return
     */
    public final static boolean isIntGtZero(int num) {
        return num > 0;
    }

    /**
     * 判断整数num是否大于或等于0
     * @param num
     * @return
     */
    public final static boolean isIntGteZero(int num) {
        return num >= 0;
    }

    /**
     * 判断浮点数num是否等于0
     * @param num
     * @return
     */
    public final static boolean isFloatEqZero(float num) {
        return num == 0f;
    }

    /**
     * 判断浮点数num是否大于0
     * @param num 浮点数
     * @return
     */
    public final static boolean isFloatGtZero(float num) {
        return num > 0f;
    }

    /**
     * 判断浮点数num是否大于或等于0
     * @param num  浮点数
     * @return
     */
    public final static boolean isFloatGteZero(float num) {
        return num >= 0f;
    }

    /**
     * 判断是否为合法字符(a-zA-Z0-9-_)
     * @param text
     * @return
     */
    public final static boolean isRightfulString(String text) {
        return match(text, "^[A-Za-z0-9_-]+$");
    }

    /**
     * 判断英文字符(a-zA-Z)
     * @param text
     * @return
     */
    public final static boolean isEnglish(String text) {
        return match(text, "^[A-Za-z]+$");
    }

    /**
     * 判断中文字符(包括汉字和符号)
     * @param text
     * @return
     */
    public final static boolean isChineseChar(String text) {
        return match(text, "^[\u0391-\uFFE5]+$");
    }

    /**
     * 匹配汉字
     * @param text
     * @return
     */
    public final static boolean isChinese(String text) {
        return match(text, "^[\u4e00-\u9fa5]+$");
    }

    /**
     * 是否包含中英文特殊字符，除英文"-_"字符外
     * @param str
     * @return
     */
    public static boolean isContainsSpecialChar(String text) {
        if (isBlank(text)){
            return false;
        }
        String[] chars = { "[", "`", "~", "!", "@", "#", "$", "%", "^", "&",
                "*", "(", ")", "+", "=", "|", "{", "}", "'", ":", ";", "'",
                ",", "[", "]", ".", "<", ">", "/", "?", "~", "！", "@", "#",
                "￥", "%", "…", "&", "*", "（", "）", "—", "+", "|", "{", "}",
                "【", "】", "‘", "；", "：", "”", "“", "’", "。", "，", "、", "？", "]" };
        for (String ch : chars) {
            if (text.contains(ch)){
                return true;
            }
        }
        return false;
    }

    /**
     * 过滤中英文特殊字符，除英文"-_"字符外
     * @param text
     * @return
     */
    public static String stringFilter(String text) {
        String regExpr = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regExpr);
        Matcher m = p.matcher(text);
        return m.replaceAll("").trim();
    }

    /**
     * java正则表达式匹配真实姓名(2~7个中文或者3~10个英文)
     * @param name
     * @return
     */
    public static boolean checkName(String name) {
        String regx = "(([\u4E00-\u9FA5]{2,7})|([a-zA-Z]{3,10}))";
        return Pattern.matches(regx, name);
    }

    /**
     * 过滤html代码
     * @param inputString  含html标签的字符串
     * @return
     */
    public static String htmlFilter(String inputString) {
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;
        java.util.regex.Pattern p_ba;
        java.util.regex.Matcher m_ba;

        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            // }
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            // }
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            String patternStr = "\\s+";

            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签

            p_ba = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            m_ba = p_ba.matcher(htmlStr);
            htmlStr = m_ba.replaceAll(""); // 过滤空格

            textStr = htmlStr;

        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }
        return textStr;// 返回文本字符串
    }

    public static String read(String filePath, String charset) throws Exception {
        File newFile = new File(filePath);
        if (newFile.exists()) {
            FileInputStream fileInputStream = new FileInputStream(newFile);
            InputStreamReader reader = new InputStreamReader(fileInputStream,
                    charset);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while (null != (line = bufferedReader.readLine())) {
                builder.append(line);
            }
            bufferedReader.close();
            return new String(builder);
        } else {
            System.out.println("文件不存在");
            return "";
        }
    }

    public static void writer(String filePath, String content, String charset)
            throws FileNotFoundException, IOException {
        File newFile = new File(filePath);
        FileOutputStream fileOutputStream = new FileOutputStream(newFile);
        OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream,
                charset);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.write(content);
        bufferedWriter.flush();
        bufferedWriter.close();
    }
    /**
     * 返回两个map中key相等的vaule不相等的kv
     * @param map1
     * @param map2
     * @return
     */
    public static String eaqualsChar(Map<String,Object> map1,Map<String,Object> map2,List<String> showLines){
    	StringBuffer sb=new StringBuffer();
    	int flag=0;
    	if(map1==null||map2==null)return sb.toString();
    	for(String s:map1.keySet()){
    		if(map1.get(s) instanceof Map){
    			sb.append(eaqualsChar((Map)map1.get(s), (Map)map2.get(s),showLines));
    		}else{
    			Map<String,Object> bMap=map2;
                Map<String,Object> aMap=map1;
        		if(bMap!=null&&aMap!=null){
        				if(aMap.containsKey(s)){
        					if(s.toLowerCase().contains("date")||s.toLowerCase().contains("icon"))continue;
        						String av=aMap.get(s)!=null?aMap.get(s).toString():"无";
        						String bv=bMap.get(s)!=null?bMap.get(s).toString():"无";
        						if(!(showLines==null||showLines.size()<=0)){
        	            			if(showLines.contains(s)
        	    							||showLines.contains(s.substring(s.indexOf(".")+1))){
        	    						sb.append(bMap.get(s)!=null?s+":"+bMap.get(s).toString()+" ":"");
        	    						flag=-1;
        	    						//continue;
        	            			}
        	                	}else if(!s.equals("serialVersionUID")&&flag==0&&s.toLowerCase().contains("name")){
        							sb.append("name:"+bv+" ");
        							flag=-1;
        							//continue;
        						}
        						if(!(av.equals(bv))){
        							//处理密码
        							if(s.toLowerCase().equals("password")
        									||s.toLowerCase().substring(s.indexOf(".")+1).equals("password")){
        								av="******";
        								bv="******";
        							}
        							if(!bv.equals("")&&!av.equals("")){
        								sb.append(s).append("(")
        								.append(bv).append("->").append(av).append(") ");
        							}
            			}
            		}
                		
        	}
    		}
    	}
    	return sb.toString();
    }
    
    /**
     * 递归遍历map不为null的属性
     * @param map1
     * @param map2
     * @return
     */
    public static String getMapValueNotNull(Map<String,Object> map1,String[] showLines){
    	StringBuffer sb=new StringBuffer();
    	List<String> list=Arrays.asList(showLines);
    	if(map1==null)return sb.toString();
    	for(String s:map1.keySet()){
    		if(map1.get(s) instanceof Map){
    			sb.append(getMapValueNotNull((Map)map1.get(s),showLines));
    		}else{
                Map<String,Object> aMap=map1;
        		if(aMap!=null){
        			if(!(showLines==null||showLines[0].trim().equals(""))){
        			if(list.contains(s)
							||list.contains(s.substring(s.indexOf(".")+1))){
						sb.append(aMap.get(s)!=null?"  "+s+":"+aMap.get(s).toString():"");
        			}
            		}else{
            			if(s.toLowerCase().contains("date")||s.toLowerCase().contains("icon"))continue;
    					if(s.toLowerCase().contains("password")){
    						sb.append(aMap.get(s)!=null?" "+s+":"+"******":"");
    						continue;
    					}
    						sb.append(aMap.get(s)!=null?"  "+s+":"+aMap.get(s).toString():"");
            		}
        		}
        	}
    		}
    	return sb.toString();
    }
}
