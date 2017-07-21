package com.spring.validator.constraints;

import com.mysql.jdbc.StringUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataValidator {

    /**
     * 检查商户号是否存在
     *
     * @param merchantNo 商户号
     * @return true 商户号存在，false 商户号不存在
     */
    public boolean validateMerchantNo(String merchantNo) {
        boolean ret = false;
        if (!StringUtils.isEmptyOrWhitespaceOnly(merchantNo) ) {
            ret = true;
        }
        return ret;
    }


    /**
     * 检查代办员编号与网点编号是否匹配
     *
     * @param operNo 代办员编号
     * @param placeNo 网点编号
     * @return true 办员编号与网点编号匹配，false 办员编号与网点编号不匹配
     */
    public boolean ifOperNoMatchWithPlaceNo(String operNo, String placeNo) {
        boolean ret = false;
        try {
            if(!StringUtils.isNullOrEmpty(operNo) && !StringUtils.isNullOrEmpty(placeNo) ){
                ret = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            ret = false;
        }
        return  ret;
    }
}
