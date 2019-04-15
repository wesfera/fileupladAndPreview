package com.zhfile.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 文件信息维护
 */
@Entity
@Table(name="t_file_info")
@Data
public class FileInfo extends BaseEntity {

    //文件名称
    private String name;

    //菜单编码
    private String modelCode;

    //文件路径
    private String url;

    private String orgCode;

}
