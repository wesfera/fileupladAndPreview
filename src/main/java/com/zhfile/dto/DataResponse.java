package com.zhfile.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataResponse<T> extends Response {

    @ApiModelProperty("数据")
    private T data;

    private DataResponse(T data, String message) {
        this.data = data;
        super.message = message;
    }

    public static <T> DataResponse<T> ok(T data, String... message) {
        return new DataResponse<>(data, StringUtils.join(message));
    }

}
