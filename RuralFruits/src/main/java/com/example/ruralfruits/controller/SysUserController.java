package com.example.ruralfruits.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ruralfruits.common.Result;
import com.example.ruralfruits.entity.SysUser;
import com.example.ruralfruits.service.SysUserService;
import com.example.ruralfruits.utils.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/sysuser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/list")
    public Result list() {
        List<SysUser> list = sysUserService.list();
        return Result.success(list);
    }

    @GetMapping("/list02")
    public Result list02(SysUser sysUser) {
        int pageNum = sysUser.getPageNum() == null ? 1 : sysUser.getPageNum(); // 默认第 1 页
        int pageSize = sysUser.getPageSize() == null ? 10 : sysUser.getPageSize(); // 默认每页 10 条

        Page<SysUser> page = Page.of(pageNum, pageSize);

        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(sysUser.getUsername()), "username", sysUser.getUsername())
                .like(StringUtils.hasText(sysUser.getNickname()), "nickname", sysUser.getNickname())
                .eq(StringUtils.hasText(sysUser.getType()), "type", sysUser.getType());

        IPage<SysUser> userPage = sysUserService.page(page, queryWrapper);
        return Result.success(userPage);
    }

    @PostMapping
    public Result save(@RequestBody SysUser sysUser) {
        // 新增用户：MD5 加密密码（原有逻辑不变）
        sysUser.setPassword(SecureUtil.md5(sysUser.getPassword()));
        sysUserService.save(sysUser);
        return Result.success();
    }

    // 原有根据 ID 查询用户（用于列表/管理页）
    @RequestMapping("/{id}")
    public Result findOne(@PathVariable("id") Integer id) {
        SysUser sysUser = sysUserService.getById(id);
        return Result.success(sysUser);
    }

    // 新增：专门用于个人中心的用户查询（回显时清空密码，避免加密乱码）
    @GetMapping("/profile/{userId}")
    public Result getUserProfile(@PathVariable("userId") Integer userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 关键：清空密码字段，前端回显时密码框为空
        user.setPassword("");
        return Result.success(user);
    }

    // 原有通用更新接口（用于管理页，保持不变）
    @PutMapping
    public Result update(@RequestBody SysUser sysUser) {
        sysUserService.updateById(sysUser);
        return Result.success();
    }

    // 新增：个人中心修改个人信息接口（含密码 MD5 加密处理）
    @PutMapping("/profile/update")
    public Result updateUserProfile(@RequestBody SysUser sysUser) {
        // 1. 校验用户 ID
        if (sysUser.getUserId() == null) {
            return Result.error("用户 ID 不能为空");
        }

        // 2. 查询数据库中原有用户
        SysUser dbUser = sysUserService.getById(sysUser.getUserId());
        if (dbUser == null) {
            return Result.error("用户不存在");
        }

        // 3. 密码处理：前端传递了密码才加密更新（和新增用户保持 MD5 一致）
        if (StringUtils.hasText(sysUser.getPassword())) {
            dbUser.setPassword(SecureUtil.md5(sysUser.getPassword())); // MD5 加密明文密码
        }

        // 4. 更新其他字段（按你的 SysUser 实体类字段补充，确保和前端弹框字段一致）
        if (StringUtils.hasText(sysUser.getNickname())) {
            dbUser.setNickname(sysUser.getNickname());
        }
        if (StringUtils.hasText(sysUser.getPhone())) {
            dbUser.setPhone(sysUser.getPhone());
        }
        if (StringUtils.hasText(sysUser.getEmail())) {
            dbUser.setEmail(sysUser.getEmail());
        }
        if (sysUser.getSex() != null) { // 性别是数字类型（0/1/2）
            dbUser.setSex(sysUser.getSex());
        }
        if (StringUtils.hasText(sysUser.getAvatar())) {
            dbUser.setAvatar(sysUser.getAvatar());
        }
        // 注意：用户类型（type）不允许修改，不添加更新逻辑

        // 5. 执行更新
        sysUserService.updateById(dbUser);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Integer[] id) {
        sysUserService.removeBatchByIds(Arrays.asList(id));
        return Result.success();
    }

    @GetMapping("/export")
    public void exportData(HttpServletResponse response, SysUser sysUser) throws Exception {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(sysUser.getUsername()), "username", sysUser.getUsername())
                .like(StringUtils.hasText(sysUser.getNickname()), "nickname", sysUser.getNickname())
                .eq(StringUtils.hasText(sysUser.getType()), "type", sysUser.getType());
        // 查询所有的用户信息
        List<SysUser> list = sysUserService.list(queryWrapper);
        // 调用Hutool的工具方法导出数据为excel
        ExcelUtils.export(response, list, "用户列表");
    }

    @PostMapping("/import")
    public Result importData(@RequestParam("file") MultipartFile file) throws Exception {
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<SysUser> list = reader.readAll(SysUser.class);
        int count = 0;
        for (SysUser sysUser : list) {
            // 导入用户时也加密密码（保持一致）
            sysUser.setPassword(SecureUtil.md5(sysUser.getPassword()));
            sysUserService.save(sysUser);
            count++;
        }
        return Result.success(count);
    }

    @GetMapping("/getById/{userId}")
    public Result getUserById(@PathVariable Integer userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 仅返回必要字段，避免敏感信息泄露
        return Result.success(Map.of("username", user.getUsername()));
    }

    // 查询用户余额（仅修复空指针，不改动返回格式）
    @GetMapping("/balance/{userId}")
    public Result getUserBalance(@PathVariable Integer userId) {
        if (Objects.isNull(userId)) {
            return Result.error("用户ID不能为空");
        }
        SysUser user = sysUserService.getById(userId);
        if (Objects.isNull(user)) {
            return Result.error("用户不存在");
        }
        // 修复：避免 restMoney 为 null 时返回 null
        BigDecimal restMoney = user.getRestMoney() == null ? BigDecimal.ZERO : user.getRestMoney();
        return Result.success(Map.of("restMoney", restMoney));
    }

    // 扣减用户余额（仅修复空指针，不改动其他逻辑和返回格式）
    @PostMapping("/balance/deduct")
    @Transactional // 事务保证：扣减余额和下单操作原子性
    public Result deductUserBalance(@RequestBody Map<String, Object> params) {
        // 修复：参数获取时避免空指针（先判断是否存在，再转换）
        Integer userId = params.get("userId") instanceof Integer ? (Integer) params.get("userId") : null;
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 金额参数校验（避免非数字类型）
        Object amountObj = params.get("amount");
        if (amountObj == null) {
            return Result.error("扣减金额不能为空");
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountObj.toString());
        } catch (Exception e) {
            return Result.error("扣减金额格式错误，请输入数字");
        }

        // 1. 参数校验
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("扣减金额必须大于0");
        }

        // 2. 查询用户
        SysUser user = sysUserService.getById(userId);
        if (Objects.isNull(user)) {
            return Result.error("用户不存在");
        }

        // 3. 校验余额是否充足（默认余额为0）
        BigDecimal restMoney = user.getRestMoney() == null ? BigDecimal.ZERO : user.getRestMoney();
        if (restMoney.compareTo(amount) < 0) {
            return Result.error("余额不足");
        }

        // 4. 扣减余额
        user.setRestMoney(restMoney.subtract(amount));
        boolean success = sysUserService.updateById(user);
        return success ? Result.success() : Result.error("余额扣减失败");
    }
}