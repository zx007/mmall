package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by zhaoxin on 2018/3/11.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
