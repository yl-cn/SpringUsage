package com.spring.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@UtilityClass
public class HttpUtil {

    private static final String UNKNOWN = "unknown";

    public static Map<String, String> getHttpRequestParams(HttpServletRequest httpRequest) {
        Enumeration<String> enume = httpRequest.getParameterNames();
        Map<String, String> paramsMap = new HashMap<>();

        if (null != enume) {
/*

            while (enume.hasMoreElements()) {
                String element = enume.nextElement();
                if (null != element) {
                    String paramName = element;
                    String paramValue = httpRequest.getParameter(paramName);
                    paramsMap.put(paramName, paramValue);
                }
            }
*/

            Collections.list(enume).stream().forEach(p -> paramsMap.put(p, httpRequest.getParameter(p)));

            /*paramsMap = Collections.list(enume).stream()
                    .collect(Collector.of(HashMap::new, (m, p) -> m.put(p, httpRequest.getParameter(p)),(k,v)->v, Collector.Characteristics.IDENTITY_FINISH));*/
        }

        return paramsMap;
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
