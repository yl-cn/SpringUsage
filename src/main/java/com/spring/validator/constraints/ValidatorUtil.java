package com.spring.validator.constraints;

import lombok.experimental.UtilityClass;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.util.Arrays;

@UtilityClass
public class ValidatorUtil {

    //Usage:
    //@ScriptAssert(lang = "javascript", script = "com.gznb.member.validator.ValidatorUtil.isEqualSum(_this.balanceAfterTrade,_this.balanceBeforeTrade,_this.transferenceFee)", message="卡系统交易后金额应该等于：交易前金额 + 圈存金额")
    public static boolean isEqualSum(Object total, Object... amounts) {
        boolean result = false;
        try {

            if(null != total && !ArrayUtils.isEmpty(amounts)) {
                BigDecimal totalAmount = (BigDecimal) ConvertUtils.convert(total, BigDecimal.class);
                BigDecimal sumAmount = Arrays.stream(amounts)
                        .map(amount -> (BigDecimal)ConvertUtils.convert(amount, BigDecimal.class))
                        .reduce(new BigDecimal(0), BigDecimal::add);

                if(totalAmount.equals(sumAmount)){
                    result = true;
                }

            }

        } catch (Exception e) {
            //
        }

        return result;
    }
}
