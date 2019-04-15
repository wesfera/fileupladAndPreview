package com.zhfile.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("文件信息")
@Data
public class FileInfoDto {

    private String id;

    private String name;

}
