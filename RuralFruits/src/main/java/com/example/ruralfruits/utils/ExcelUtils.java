package com.example.ruralfruits.utils;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.core.io.IoUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExcelUtils {
    /**
     * 导出Excel
     * @param response 响应对象
     * @param list 导出的数据集合
     * @param filename 导出的文件名
     */
    public static void export(HttpServletResponse response, List <?>list, String filename) {
        try {
            ExcelWriter writer = ExcelUtil.getWriter();
            writer.write(list, true);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8) + ".xlsx");
            ServletOutputStream os = response.getOutputStream();
            writer.flush(os, true);
            writer.close();
            IoUtil.close(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    }
