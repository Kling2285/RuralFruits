package com.example.ruralfruits.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ruralfruits.common.Result;
import com.example.ruralfruits.entity.Notice;
import com.example.ruralfruits.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/notice")
@Slf4j
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 分页查询公告（适配 Notice 实体，支持标题模糊查询、是否公开筛选、排序）
    @GetMapping("/list")
    public Result list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long open,     // 是否公开（1=公开，0=不公开）
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) String column,  // 排序字段（如 title、time、id）
            @RequestParam(required = false) String sort     // 排序方向（asc/desc）
    ) {
        try {
            // 构建 Notice 的查询条件
            QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
            queryWrapper.like(StringUtils.isNotBlank(title), "title", title);  // 标题模糊查询
            queryWrapper.eq(Objects.nonNull(open), "open", open);             // 是否公开精准筛选

            // 排序逻辑（字段不为空时才排序，默认升序）
            boolean isAsc = "asc".equalsIgnoreCase(sort) || StrUtil.isBlank(sort);
            queryWrapper.orderBy(StrUtil.isNotBlank(column), isAsc, column);

            // 分页查询（使用 NoticeService 和 Notice 分页对象）
            Page<Notice> page = Page.of(pageNum, pageSize);
            noticeService.page(page, queryWrapper);  // MyBatis-Plus 分页方法

            // 构造返回结果（兼容前端表格分页格式）
            Map<String, Object> map = new HashMap<>();
            map.put("total", page.getTotal());  // 总条数
            map.put("records", page.getRecords()); // 分页数据（前端用 records 接收，之前是 list，已适配）
            map.put("pages", page.getPages());  // 总页数（可选）
            return Result.success(map);
        } catch (Exception e) {
            log.error("查询公告列表异常：", e);
            return Result.error("查询公告列表失败：" + e.getMessage());
        }
    }

    // 新增公告（已删除 userId 相关逻辑，修复线程安全问题）
    @PostMapping("/add")
    public Result add(@RequestBody Notice notice) {
        try {
            // 校验必填字段（仅保留标题、内容）
            if (StrUtil.isEmpty(notice.getTitle())) {
                return Result.error("公告标题不能为空");
            }
            if (StrUtil.isEmpty(notice.getContent())) {
                return Result.error("公告内容不能为空");
            }

            // 默认启用（1=启用，兼容前端字符串类型参数）
            if (Objects.isNull(notice.getOpen())) {
                notice.setOpen(1L);
            }

            // 自动填充发布时间（线程安全的 DateTimeFormatter 替代 SimpleDateFormat）
            if (StrUtil.isEmpty(notice.getTime())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                notice.setTime(LocalDateTime.now().format(formatter));
            }

            // 保存公告（MyBatis-Plus 保存方法）
            boolean success = noticeService.save(notice);
            return success ? Result.success("公告添加成功") : Result.error("公告添加失败");
        } catch (Exception e) {
            log.error("添加公告异常：", e);
            return Result.error("添加公告失败：" + e.getMessage());
        }
    }

    // 修改公告（适配删除 userId 后的实体）
    @PutMapping("/update")
    public Result update(@RequestBody Notice notice) {
        try {
            // 校验必填参数
            if (Objects.isNull(notice.getId())) {
                return Result.error("公告ID不能为空");
            }
            if (StrUtil.isEmpty(notice.getTitle())) {
                return Result.error("公告标题不能为空");
            }
            if (StrUtil.isEmpty(notice.getContent())) {
                return Result.error("公告内容不能为空");
            }

            // 检查公告是否存在
            Notice existNotice = noticeService.getById(notice.getId());
            if (Objects.isNull(existNotice)) {
                return Result.error("修改失败：该公告不存在");
            }

            // 兼容前端 open 字段类型（若前端传字符串，自动转 Long）
            if (notice.getOpen() != null) {
                notice.setOpen(notice.getOpen()); // 若前端传字符串，可加：Long.parseLong(notice.getOpen().toString())
            } else {
                notice.setOpen(1L); // 修改时默认启用
            }

            // 保存修改（保留原发布时间，不允许修改）
            notice.setTime(existNotice.getTime());

            // 更新公告
            boolean success = noticeService.updateById(notice);
            return success ? Result.success("公告修改成功") : Result.error("公告修改失败");
        } catch (Exception e) {
            log.error("修改公告异常：", e);
            return Result.error("修改公告失败：" + e.getMessage());
        }
    }

    // 查询单个公告（根据ID）
    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id) {
        try {
            Notice notice = noticeService.getById(id);
            if (Objects.isNull(notice)) {
                return Result.error("查询失败：该公告不存在");
            }
            return Result.success(notice);
        } catch (Exception e) {
            log.error("查询公告详情异常：", e);
            return Result.error("查询公告详情失败：" + e.getMessage());
        }
    }

    // 批量删除公告（根据ID数组）
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable Long[] ids) {
        try {
            if (ids == null || ids.length == 0) {
                return Result.error("删除失败：请选择要删除的公告");
            }
            // 批量删除
            boolean success = noticeService.removeByIds(Arrays.asList(ids));
            return success ? Result.success("公告删除成功") : Result.error("公告删除失败");
        } catch (Exception e) {
            log.error("删除公告异常：", e);
            return Result.error("删除公告失败：" + e.getMessage());
        }
    }
}