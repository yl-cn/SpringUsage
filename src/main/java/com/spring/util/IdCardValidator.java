package com.spring.util;

import com.spring.exception.InvalidIdCardException;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class IdCardValidator {

    /**
     * <p>
     * 判断18位身份证的合法性
     * </p>
     * 根据〖中华人民共和国国家标准GB11643-1999〗中有关公民身份号码的规定，公民身份号码是特征组合码，由十七位数字本体码和一位数字校验码组成。
     * 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
     * <p>
     * 顺序码: 表示在同一地址码所标识的区域范围内，对同年、同月、同 日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配 给女性。
     * </p>
     * <p>
     * 1.前1、2位数字表示：所在省份的代码； 2.第3、4位数字表示：所在城市的代码； 3.第5、6位数字表示：所在区县的代码；
     * 4.第7~14位数字表示：出生年、月、日； 5.第15、16位数字表示：所在地的派出所的代码；
     * 6.第17位数字表示性别：奇数表示男性，偶数表示女性；
     * 7.第18位数字是校检码：也有的说是个人信息码，一般是随计算机的随机产生，用来检验身份证的正确性。校检码可以是0~9的数字，有时也用x表示。
     * </p>
     * <p>
     * 第十八位数字(校验码)的计算方法为： 1.将前面的身份证号码17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4
     * 2 1 6 3 7 9 10 5 8 4 2
     * </p>
     * <p>
     * 2.将这17位数字和系数相乘的结果相加。
     * </p>
     * <p>
     * 3.用加出来和除以11，看余数是多少？
     * </p>
     * 4.余数只可能有0 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3
     * 2。
     * <p>
     * 5.通过上面得知如果余数是2，就会在身份证的第18位数字上出现罗马数字的Ⅹ。如果余数是10，身份证的最后一位号码就是2。
     * </p>
     */


    /**
     * 省，直辖市代码表： { 11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",
     * 21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",
     * 33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",
     * 42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",
     * 51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",
     * 63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"}
     */
    private static final Map<String, String> cityCodeMap = new HashMap<String, String>() {
        {
            this.put("11", "北京");this.put("12", "天津");this.put("13", "河北");
            this.put("14", "山西");this.put("15", "内蒙古");this.put("21", "辽宁");
            this.put("22", "吉林");this.put("23", "黑龙江");this.put("31", "上海");
            this.put("32", "江苏");this.put("33", "浙江");this.put("34", "安徽");
            this.put("35", "福建");this.put("36", "江西");this.put("37", "山东");
            this.put("41", "河南");this.put("42", "湖北");this.put("43", "湖南");
            this.put("44", "广东");this.put("45", "广西");this.put("46", "海南");
            this.put("50", "重庆");this.put("51", "四川");this.put("52", "贵州");
            this.put("53", "云南");this.put("54", "西藏");this.put("61", "陕西");
            this.put("62", "甘肃");this.put("63", "青海");this.put("64", "宁夏");
            this.put("65", "新疆");this.put("71", "台湾");this.put("81", "香港");
            this.put("82", "澳门");this.put("91", "国外");
        }
    };


    // 身份证号码中的出生日期的格式
    private static final String BIRTH_DATE_FORMAT = "yyyyMMdd";

    // 身份证的最小出生日期,1900年1月1日
    private static final Date MINIMAL_BIRTH_DATE = new Date(-2209017600000L);
    private static final int NEW_CARD_NUMBER_LENGTH = 18;
    private static final int OLD_CARD_NUMBER_LENGTH = 15;

    /**
     * 18位身份证中最后一位校验码
     */
    private final static char[] VERIFY_CODE = { '1', '0', 'X', '9', '8', '7',
            '6', '5', '4', '3', '2' };

    /**
     * 18位身份证中，各个数字的生成校验码时的权值
     */
    private final static int[] VERIFY_CODE_WEIGHT = { 7, 9, 10, 5, 8, 4, 2, 1,
            6, 3, 7, 9, 10, 5, 8, 4, 2 };


    /**
     * 判断身份证的合法性
     *
     * @param idCard 待验证的身份证
     * @return 是否为有效身份证
     */
    public static boolean validateIdCardStrictly(String idCard) {
        String cardNumber = idCard == null ? "" : idCard.trim().toUpperCase();


        //基本数字和位数验校
        if(!isIdCard(cardNumber)) {
            return false;
        }

        //15位转18位
        if(OLD_CARD_NUMBER_LENGTH == cardNumber.length()) {
            cardNumber = contertToNewCardNumber(cardNumber);
        }

        //是否全为数字串
        /*if (!isDigital(cardNumber.substring(0, 17))) {
            return false;
        }*/

        //判断是否为合法的省份
        //判断是否为合法生日
        //身份证号的第18位校验正确
        if(null == getProvinceNameByCode(cardNumber.substring(0, 2))
                || null == getBirthDate(cardNumber.substring(6, 14))
                || calculateVerifyCode(cardNumber) != cardNumber
                .charAt(NEW_CARD_NUMBER_LENGTH - 1)) {
            return false;
        }
        else {
            return true;
        }

    }

    public static IdCard convertToIdCard(String cardNumber) throws Exception {
        IdCard idCard = null;
        try {

            String idCardNumber = cardNumber == null ? "" : cardNumber.trim().toUpperCase();

            //基本数字和位数验校
            if(isIdCard(idCardNumber)) {

                String birthdayStr;

                //15位转18位
                if(OLD_CARD_NUMBER_LENGTH == idCardNumber.length()) {
                    birthdayStr = "19" + idCardNumber.substring(6, 12);
                }
                else {
                    birthdayStr = idCardNumber.substring(6, 14);

                    if(calculateVerifyCode(idCardNumber) != idCardNumber
                            .charAt(NEW_CARD_NUMBER_LENGTH - 1)) {
                        throw new InvalidIdCardException("身份证校验位错误");
                    }
                }

                String provinceName = getProvinceNameByCode(idCardNumber.substring(0,2));
                if(null == provinceName) {
                    throw new InvalidIdCardException("身份证省份/直辖市代码错误");
                }

                Date birthDate = DateUtils.parseDateStrictly(birthdayStr, BIRTH_DATE_FORMAT);
                Date today = new Date();
                if(birthDate.after(today) || birthDate.before(MINIMAL_BIRTH_DATE)) {
                    throw new InvalidIdCardException("身份证日期错误");
                }

                idCard = new IdCard();
                idCard.setProvince(provinceName);
                idCard.setBirthday(birthDate);
                idCard.setAge(getAge(birthDate));

                GregorianCalendar birth = new GregorianCalendar();
                birth.setTime(birthDate);
                idCard.setYear(birth.get(Calendar.YEAR));
                idCard.setMonth(birth.get(Calendar.MONTH) + 1);
                idCard.setDay(birth.get(Calendar.DAY_OF_MONTH));

                idCard.setGender(getGenderCode(idCardNumber) == 1 ? "男" : "女");

            }
            else {
                throw new InvalidIdCardException("身份证格式错误");
            }

        } catch (InvalidIdCardException e) {
            throw e;
        } catch (Exception e) {
            log.error("身份证解析错误：{}", e.getMessage(), e);
            throw new InvalidIdCardException("身份证解析错误");
        }

        return idCard;
    }

    private static int getAge(Date birthday) throws InvalidIdCardException{
        int age;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            Calendar birth = Calendar.getInstance();
            birth.setTime(birthday);

            //如果传入的时间，在当前时间的后面，返回0岁
            if (birth.after(now)) {
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                /*if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                    age += 1;
                }*/
            }
        } catch (Exception e) {
            throw new InvalidIdCardException("计算年龄错误");
        }

        return age;
    }


    /**
     * 15位和18位身份证号码的基本数字和位数验校
     *
     * @param idCard
     * @return
     */
    public static boolean isIdCard(String idCard) {
        /*return idCard == null || "".equals(idCard) ? false : Pattern.matches(
                "(^\\d{15}$)|(\\d{17}(?:\\d|x|X)$)", idCard);*/
        return (idCard != null && !"".equals(idCard)) && (is15Idcard(idCard) || (is18Idcard(idCard)));
    }

    /**
     * 15位身份证号码的基本数字和位数验校
     *
     * @param idcard
     * @return
     */
    public static boolean is15Idcard(String idcard) {
        return idcard == null || "".equals(idcard) ? false : Pattern.matches(
                "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$",
                idcard);
    }

    /**
     * 18位身份证号码的基本数字和位数验校
     *
     * @param idcard
     * @return
     */
    public static boolean is18Idcard(String idcard) {
        return Pattern
                .matches(
                        "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([\\d|x|X]{1})$",
                        idcard);
    }

    /**
     * 数字验证
     *
     * @param str
     * @return
     */
    private static boolean isDigital(String str) {

        return str == null || "".equals(str.trim()) ? false : str.matches("^[0-9]*$");
    }

/*    public boolean validate() {
        if (null == cacheValidateResult) {
            boolean result = true;
            // 身份证号不能为空
            result = result && (null != cardNumber);
            // 身份证号长度是18(新证)
            result = result && NEW_CARD_NUMBER_LENGTH == cardNumber.length();
            // 身份证号的前17位必须是阿拉伯数字
            for (int i = 0; result && i < NEW_CARD_NUMBER_LENGTH - 1; i++) {
                char ch = cardNumber.charAt(i);
                result = result && ch >= '0' && ch <= '9';
            }
            // 身份证号的第18位校验正确
            result = result
                    && (calculateVerifyCode(cardNumber) == cardNumber
                    .charAt(NEW_CARD_NUMBER_LENGTH - 1));
            // 出生日期不能晚于当前时间，并且不能早于1900年
            try {
                Date birthDate = this.getBirthDate();
                result = result && null != birthDate;
                result = result && birthDate.before(new Date());
                result = result && birthDate.after(MINIMAL_BIRTH_DATE);

                String birthdayPart = this.getBirthDayPart();
                String realBirthdayPart = this.createBirthDateParser().format(
                        birthDate);
                result = result && (birthdayPart.equals(realBirthdayPart));
            } catch (Exception e) {
                result = false;
            }
            // TODO 完整身份证号码的省市县区检验规则
            cacheValidateResult = Boolean.valueOf(result);
        }
        return cacheValidateResult;
    }*/

    private static String getAddressCode(String cardNumber) {
        return cardNumber.substring(0, 6);
    }

    private static Date getBirthDate(String birthday) {
        Date birthDate = null;
        if (null == birthday || "".equals(birthday.trim())) {
            return birthDate;
        }

        // 该身份证生出日期在当前日期之后时为假
        try {
            /**
             * 出生日期中的年、月、日必须正确,比如月份范围是[1,12],日期范围是[1,31]，
             * 还需要校验闰年、大月、小月的情况时，
             * 月份和日期相符合
             */
            //可以先用SimpleDateFormat 将日期字符转Date 再将Date转字符串 ，最后再对比，
            // 不匹配代表源日期字符串错误
            //下面的方法是日期严格验证，闰年月天数不符，抛出异常
            birthDate = DateUtils.parseDateStrictly(birthday, BIRTH_DATE_FORMAT);

            if (birthDate != null
                    && new Date().after(birthDate)
                    && MINIMAL_BIRTH_DATE.before(birthDate)) {
                //
            }
            else {
                birthDate = null;
            }
        } catch (ParseException e) {
            //时间格式不符 或 闰年大小月天数不符
            birthDate = null;
        }
        return birthDate;

    }


    /**
     * 根据省份编码，获取省份名称
     * @param provinceCode
     * @return
     */
    private static String getProvinceNameByCode(String provinceCode) {
        return cityCodeMap.get(provinceCode);
    }

    /**
     * 获取身份证最后一位，奇数为男性，偶数为女性
     *
     * @return
     */
    private static int getGenderCode(String cardNumber) {
        char genderCode = cardNumber.charAt(NEW_CARD_NUMBER_LENGTH == cardNumber.length() ? cardNumber.length() - 2 : cardNumber.length() - 1);
        return (((int) (genderCode - '0')) & 0x1);
    }

    private static String getBirthDayPart(String cardNumber) {
        return cardNumber.substring(6, 14);
    }

    /**
     * <li>校验码（第十八位数）：<br/>
     * <ul>
     * <li>十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0...16 ，先对前17位数字的权求和；
     * Ai:表示第i位置上的身份证号码数字值 Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4
     * 2；</li>
     * <li>计算模 Y = mod(S, 11)</li>
     * <li>通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7 8 9 10 校验码: 1 0 X 9 8 7 6 5 4 3 2</li>
     * </ul>
     *
     * @param cardNumber
     * @return
     */
    private static char calculateVerifyCode(CharSequence cardNumber) {
        int sum = 0;
        for (int i = 0; i < NEW_CARD_NUMBER_LENGTH - 1; i++) {
            char ch = cardNumber.charAt(i);
            sum += ((int) (ch - '0')) * VERIFY_CODE_WEIGHT[i];
        }
        return VERIFY_CODE[sum % 11];
    }

    /**
     * 把15位身份证号码转换到18位身份证号码<br>
     * 15位身份证号码与18位身份证号码的区别为：<br>
     * 1、15位身份证号码中，"出生年份"字段是2位，转换时需要补入"19"，表示20世纪<br>
     * 2、15位身份证无最后一位校验码。18位身份证中，校验码根据根据前17位生成
     *
     * @param oldCardNumber
     * @return
     */
    private static String contertToNewCardNumber(String oldCardNumber) {
        StringBuilder buf = new StringBuilder(NEW_CARD_NUMBER_LENGTH);
        buf.append(oldCardNumber.substring(0, 6));
        buf.append("19");
        buf.append(oldCardNumber.substring(6));
        buf.append(calculateVerifyCode(buf));
        return buf.toString();
    }

    @Data
    public static class IdCard {
        // 省份
        private String province;
        // 城市
        private String city;
        // 区县
        private String region;
        // 年份
        private int year;
        // 月份
        private int month;
        // 日期
        private int day;
        // 性别
        private String gender;
        // 出生日期
        private Date birthday;
        //年龄
        private int age;
    }

}
