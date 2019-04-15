package com.zhfile.dao;

import com.zhfile.model.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileInfoDao extends JpaRepository<FileInfo, String> {

}
