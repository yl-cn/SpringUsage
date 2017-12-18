package com.spring.data;

import com.spring.util.GsonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebServiceResult {

    private String code;

    private String message;

    private Map<String, Object> data;


    public String toJson() {
        return GsonUtil.toJson(this, false);
    }
}
