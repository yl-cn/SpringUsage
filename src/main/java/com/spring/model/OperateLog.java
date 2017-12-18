package com.spring.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Table(name = "operate_log")
@Data
public class OperateLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 日志id
     */
    @Column(name = "log_id")
    @NotNull
    @Size(min = 0, max = 20 , message = "长度必须在{min}和{max}之间")
    private String logId;

    /**
     * 日志标题
     */
    @Size(min = 0, max = 500 , message = "长度必须在{min}和{max}之间")
    private String operation;

    /**
     * 操作人
     */
    @Column(name = "operator_name")
    @NotNull
    @Size(min = 0, max = 50 , message = "长度必须在{min}和{max}之间")
    private String operatorName;

    @Column(name = "operator_id")
    @NotNull
    @Size(min = 0, max = 20 , message = "长度必须在{min}和{max}之间")
    private String operatorId;

    /**
     * 操作时间
     */
    @Column(name = "operate_time")
    @NotNull
    private Date operateTime;

    /**
     * 时长（毫秒）
     */
    @Max(value=2147483647,message="最大值不能高于{value}")
    @Min(value=-2147483648,message="最小值不能低于{value}")
    private Integer duration;

    /**
     * IP
     */
    @Size(min = 0, max = 255 , message = "长度必须在{min}和{max}之间")
    private String ip;

    /**
     * 操作接口(类名+方法名)
     */
    @Column(name = "operate_method")
    @Size(min = 0, max = 255 , message = "长度必须在{min}和{max}之间")
    private String operateMethod;

    /**
     * 操作参数
     */
    @Column(name = "operate_param")
    @Size(min = 0, max = 1000 , message = "长度必须在{min}和{max}之间")
    private String operateParam;

    /**
     * 是否成功: S-成功 F-失败
     */
    @Size(min = 0, max = 1 , message = "长度必须在{min}和{max}之间")
    private String result;

    /**
     * 操作信息(成功或失败的概要信息)
     */
    @Size(min = 0, max = 255 , message = "长度必须在{min}和{max}之间")
    private String message;
}