package com.zhfile.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response implements Serializable {
    @ApiModelProperty("是否成功")
    protected Boolean success = true;
    @ApiModelProperty("返回消息")
    protected String message;

    public static final Response OK = new Response();

    public static Response msg(String message) {
        return new Response(true, message);
    }

    public static Response fail(String message) {
        return new Response(false, message);
    }

}