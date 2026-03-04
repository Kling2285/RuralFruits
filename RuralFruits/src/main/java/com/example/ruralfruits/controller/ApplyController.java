package com.example.ruralfruits.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ruralfruits.common.Result;
import com.example.ruralfruits.entity.Apply;
import com.example.ruralfruits.entity.SysUser;
import com.example.ruralfruits.service.ApplyService;
import com.example.ruralfruits.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.util.*;

@RestController
@RequestMapping("/apply")
public class ApplyController {
    @Autowired
    private ApplyService applyService;
    @Autowired
    private SysUserService sysUserService;


    @PostMapping("/submit")
    public Result submitApply(@RequestBody Apply apply) {
        if (Objects.isNull(apply.getUserId())) {
            return Result.error("申请失败：用户ID不能为空");
        }

        SysUser user = sysUserService.getById(apply.getUserId());
        if (Objects.isNull(user)) {
            return Result.error("申请失败：用户不存在");
        }

        LambdaQueryWrapper<Apply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Apply::getUserId, apply.getUserId())
                .in(Apply::getAuditStatus, 0, 1);
        if (applyService.count(queryWrapper) > 0) {
            return Result.error("申请失败：您已提交过申请，请勿重复提交");
        }

        apply.setAuditStatus(0);
        boolean success = applyService.save(apply);
        return success ? Result.success("申请提交成功！请等待管理员审核") : Result.error("申请提交失败");
    }

    // 2. 分页查询申请列表（前端 Apply.vue 核心接口，之前缺失）
    @GetMapping("/page")
    public Result getApplyPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer userId) {

        Page<Apply> page = Page.of(pageNum, pageSize);
        LambdaQueryWrapper<Apply> queryWrapper = new LambdaQueryWrapper<>();

        // 状态筛选
        if (Objects.nonNull(auditStatus)) {
            queryWrapper.eq(Apply::getAuditStatus, auditStatus);
        }
        // 申请人ID筛选
        if (Objects.nonNull(userId)) {
            queryWrapper.eq(Apply::getUserId, userId);
        }
        // 时间范围筛选（适配前端日期选择器）
        if (StringUtils.hasText(startTime)) {
            queryWrapper.ge(Apply::getApplyTime, startTime + " 00:00:00");
        }
        if (StringUtils.hasText(endTime)) {
            queryWrapper.le(Apply::getApplyTime, endTime + " 23:59:59");
        }
        // 按申请时间倒序
        queryWrapper.orderByDesc(Apply::getApplyTime);

        IPage<Apply> applyPage = applyService.page(page, queryWrapper);
        return Result.success(applyPage);
    }

    // 3. 删除申请（单个/批量，之前缺失）
    @DeleteMapping("/{id}")
    public Result deleteApply(@PathVariable("id") String id) {
        // 支持单个ID（1）或批量ID（1,2,3）
        List<String> idList = Arrays.asList(id.split(","));
        boolean success = applyService.removeByIds(idList);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PutMapping("/update")
    public Result auditApply(@RequestBody Apply apply) {
        // 1. 校验申请ID
        if (Objects.isNull(apply.getId())) {
            return Result.error("审核失败：申请ID不能为空");
        }

        // 2. 校验申请是否存在
        Apply oldApply = applyService.getById(apply.getId());
        if (Objects.isNull(oldApply)) {
            return Result.error("审核失败：申请记录不存在");
        }

        // 3. 校验审核人ID（仅校验非空，不判断是否是admin）
        if (Objects.isNull(apply.getAuditorId())) {
            return Result.error("审核失败：审核人ID不能为空");
        }

        // 4. 更新审核信息
        oldApply.setAuditorId(apply.getAuditorId());
        oldApply.setAuditStatus(apply.getAuditStatus());
        oldApply.setAuditTime(new Date());

        // 5. 审核通过：直接修改用户type为merchant（去掉管理员判断）
        if (apply.getAuditStatus() == 1) {
            SysUser user = sysUserService.getById(oldApply.getUserId());
            if (Objects.nonNull(user)) {
                user.setType("merchant");
                sysUserService.updateById(user);
            }
        }

        boolean success = applyService.updateById(oldApply);
        return success ? Result.success("审核完成") : Result.error("审核失败");
    }
}