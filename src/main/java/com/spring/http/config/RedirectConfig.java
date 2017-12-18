package com.spring.http.config;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public class RedirectConfig {
    public static Map<String, RedirectInfo> redirectMap;
        static
        {
            redirectMap = new HashMap<String, RedirectInfo>( );
            redirectMap.put("rechargeDetail", new RedirectInfo("卡充值还款明细", "/国资一卡通报表/充值还款明细报表"));
            redirectMap.put("consumeDetail", new RedirectInfo("卡消费赊销退货明细", "/国资一卡通报表/消费赊销退货明细报表"));
            redirectMap.put("transferenceDetail", new RedirectInfo("圈存充值扣减明细", "/国资一卡通报表/会员电子账户充值扣减明细报表"));
            redirectMap.put("upCardDataSum", new RedirectInfo("上行卡结算数据汇总", "/上行店报表/上行卡结算数据汇总报表_时间段合计"));
            redirectMap.put("downCardDataSum", new RedirectInfo("下行卡结算数据汇总", "/国资一卡通报表/下行卡结算数据汇总报表_时间段内合计"));
            redirectMap.put("allCardDataSum", new RedirectInfo("所有结算数据汇总报表", "/国资一卡通报表/结算数据汇总报表_时间段合计"));
            redirectMap.put("ticketStatistics", new RedirectInfo("券统计报表", "/上行店报表/券统计报表"));
            redirectMap.put("ticketDetail", new RedirectInfo("券明细报表", "/上行店报表/券明细报表_券状态多选"));
        }

    @Data
    @AllArgsConstructor
    public static class RedirectInfo {
        private String description;
        private String redirectPath;
    }
}
