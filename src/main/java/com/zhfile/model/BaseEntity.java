package com.zhfile.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Data
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = -1357960499914980153L;

    @Id
    @GeneratedValue(generator = "uuid2", strategy = GenerationType.AUTO)
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    /**
     * 是否逻辑删除
     */
    private boolean del = Boolean.FALSE;

    /**
     * 创建时间
     */
    private Date createTime = new Date();

    /**
     * 保存人
     */
    private String saveUserId;

    /**
     * 保存人名字
     */
    private String saveUserName;

}
