package com.example.ruralfruits.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.example.ruralfruits.common.Result;
import com.example.ruralfruits.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${files.upload.path}")
    private String uploadPath;
    @Value("${files.download.path}")
    private String downloadPath;

    @PostMapping("/upload")
    public Result uploadFile(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws Exception {
        String ext = "jpg";

        // 创建文件存储目录
        File dir = new File(uploadPath);
        if (dir.exists() == false) {
            dir.mkdirs();
        }

        // 生成唯一标识（文件名）
        String filename = UUID.randomUUID().toString();
        // 保存文件
        File dest = new File(uploadPath + filename + "." + ext);
        file.transferTo(dest);

        String url= StrUtil.removeSuffix(request.getRequestURL(),"/upload") +"/api/"+filename+"."+ext;
        return Result.success(url);
    }

    @PostMapping("/wang/upload")
    public Map<String, Object> uploadWangEditorFile(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IOException {
        // 获取文件的后缀名（类型）
        String ext = FileUtil.extName(file.getOriginalFilename());

        // 创建文件存储目录
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成唯一标识码（文件名）
        String uuid = UUID.randomUUID().toString();
        // 保存文件
        File dest = new File(uploadPath + uuid + "." + ext);
        file.transferTo(dest);

        String url = StrUtil.removeSuffix(request.getRequestURL().toString(), "/wang/upload") + "/" + dest.getName();
        Map<String, Object> map = new HashMap<>();
        map.put("errno", 0);
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        map.put("data", data);
        return map;
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws Exception {
        // 获取文件路径
        Path path = Paths.get(downloadPath).resolve(filename).normalize();
        Resource resource = new UrlResource(path.toUri());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + URLEncoder.encode(resource.getFilename(), "utf-8") + "\"")
                    .body(resource);
        } else {
            throw new CustomException(404,"文件不存在！");
        }
    }
}