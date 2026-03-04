package com.example.ruralfruits.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.util.StringUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ruralfruits.common.Result;
import com.example.ruralfruits.entity.Products;
import com.example.ruralfruits.entity.SysUser;
import com.example.ruralfruits.service.ProductsService;
import com.example.ruralfruits.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductsController {
    @Autowired
    private ProductsService productsService;
    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/recommendList")
    public Result recommendList() {
        return Result.success(productsService.selectRecommend(4));
    }

    // 分页查询（支持全部查询 + 个人筛选）
    @GetMapping("/list")
    public Result list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestHeader(value = "token", required = false) String token, // token非必填
            @RequestParam(required = false) String column,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "false") Boolean isAdmin // 新增：是否为管理员（默认不是）
    ) {
        try {
            QueryWrapper<Products> queryWrapper = new QueryWrapper<>();
            // 基础查询条件（名称、分类）
            queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
            queryWrapper.eq(StringUtils.isNotBlank(category), "category", category);

            // 排序逻辑
            boolean isAsc = "asc".equalsIgnoreCase(sort) || StrUtil.isBlank(sort);
            queryWrapper.orderBy(StrUtil.isNotBlank(column), isAsc, column);

            // 核心逻辑：区分管理员和普通用户
            if (!isAdmin) {
                // 普通用户（MyProducts.vue）：必须登录，只查自己的商品
                if (StringUtils.isBlank(token)) {
                    log.warn("无登录Token，不返回任何商品");
                    Map<String, Object> emptyMap = new HashMap<>();
                    emptyMap.put("total", 0);
                    emptyMap.put("list", Collections.emptyList());
                    return Result.success(emptyMap);
                }
                try {
                    DecodedJWT decodedJWT = JWT.decode(token);
                    List<String> audienceList = decodedJWT.getAudience();
                    if (!audienceList.isEmpty() && StringUtils.isNumeric(audienceList.get(0))) {
                        Long userId = Long.parseLong(audienceList.get(0));
                        SysUser sysUser = sysUserService.getById(userId);
                        if (sysUser != null) {
                            queryWrapper.eq("user_id", userId); // 按当前用户筛选
                            log.info("普通用户ID（Long）：{}，只查询该用户的商品", userId);
                        }
                    }
                } catch (Exception e) {
                    log.error("JWT解析失败，返回空列表：", e);
                    return Result.success(Collections.emptyList());
                }
            } else {
                // 管理员（Products.vue）：无需token，查询所有商品
                log.info("管理员查询全部商品（无需登录）");
            }

            // 分页查询
            Page<Products> page = Page.of(pageNum, pageSize);
            productsService.page(page, queryWrapper);

            // 构造返回结果
            Map<String, Object> map = new HashMap<>();
            map.put("total", page.getTotal());
            map.put("list", page.getRecords() != null ? page.getRecords() : Collections.emptyList());
            return Result.success(map);
        } catch (Exception e) {
            log.error("查询商品列表异常：", e);
            return Result.error("查询商品列表失败：" + e.getMessage());
        }
    }

    // 添加商品（普通用户绑定自己的ID，管理员可指定）
    @PostMapping("/add")
    public Result add(
            @RequestBody Products products,
            @RequestHeader(value = "token", required = false) String token,
            @RequestParam(defaultValue = "false") Boolean isAdmin
    ) {
        try {
            if (!isAdmin) {
                // 普通用户：强制绑定自己的ID
                if (StringUtils.isBlank(token)) {
                    return Result.error("请先登录！");
                }
                Long userId = null;
                try {
                    DecodedJWT decodedJWT = JWT.decode(token);
                    List<String> audienceList = decodedJWT.getAudience();
                    if (!audienceList.isEmpty() && StringUtils.isNumeric(audienceList.get(0))) {
                        userId = Long.parseLong(audienceList.get(0));
                    }
                } catch (Exception e) {
                    log.error("JWT解析失败：", e);
                    return Result.error("登录信息异常，请重新登录！");
                }
                if (userId == null) {
                    return Result.error("用户ID获取失败，请重新登录！");
                }
                products.setUserId(userId); // 强制绑定
                log.info("普通用户添加商品，绑定用户ID：{}", userId);
            } else {
                // 管理员：可手动指定userId（可选）
                if (products.getUserId() == null) {
                    return Result.error("管理员添加商品需指定userId！");
                }
            }

            // 字段默认值处理
            if (Objects.isNull(products.getStock()) || products.getStock() == 0L) {
                products.setStock(0L);
            }
            if (StrUtil.isEmpty(products.getUnit())) {
                products.setUnit("");
            }
            if (StrUtil.isEmpty(products.getProducer())) {
                products.setProducer("");
            }

            boolean success = productsService.save(products);
            return success ? Result.success("添加成功") : Result.error("添加失败，请检查字段是否符合要求");
        } catch (Exception e) {
            log.error("添加商品异常：", e);
            return Result.error("添加商品失败：" + e.getMessage());
        }
    }

    // 修改商品（普通用户只能改自己的，管理员可改所有）
    @PutMapping
    public Result update(
            @RequestBody Products products,
            @RequestHeader(value = "token", required = false) String token,
            @RequestParam(defaultValue = "false") Boolean isAdmin
    ) {
        try {
            if (products.getId() == null) {
                return Result.error("商品ID不能为空");
            }

            // 普通用户：校验归属
            if (!isAdmin) {
                if (StringUtils.isBlank(token)) {
                    return Result.error("请先登录！");
                }
                Long currentUserId = null;
                try {
                    DecodedJWT decodedJWT = JWT.decode(token);
                    List<String> audienceList = decodedJWT.getAudience();
                    if (!audienceList.isEmpty() && StringUtils.isNumeric(audienceList.get(0))) {
                        currentUserId = Long.parseLong(audienceList.get(0));
                    }
                } catch (Exception e) {
                    log.error("JWT解析失败：", e);
                    return Result.error("登录信息异常，请重新登录！");
                }
                if (currentUserId == null) {
                    return Result.error("用户ID获取失败，请重新登录！");
                }

                // 校验归属
                Products existingProduct = productsService.getById(products.getId());
                if (existingProduct == null) {
                    return Result.error("修改失败，商品不存在！");
                }
                if (!currentUserId.equals(existingProduct.getUserId())) {
                    return Result.error("无权修改他人商品！");
                }
                products.setUserId(currentUserId); // 强制绑定
            }

            if (products.getUpdateTime() == null) {
                products.setUpdateTime(new Date());
            }

            boolean success = productsService.updateById(products);
            return success ? Result.success("修改成功") : Result.error("修改失败，请检查字段是否符合要求");
        } catch (Exception e) {
            log.error("修改商品异常：", e);
            return Result.error("修改商品失败：" + e.getMessage());
        }
    }

    // 查询单个商品（普通用户只能看自己的，管理员可看所有）
    @GetMapping("/{id}")
    public Result detail(
            @PathVariable long id,
            @RequestHeader(value = "token", required = false) String token,
            @RequestParam(defaultValue = "false") Boolean isAdmin
    ) {
        try {
            Products product = productsService.getById(id);
            if (product == null) {
                return Result.error("商品不存在！");
            }

            // 普通用户：校验归属
            if (!isAdmin) {
                if (StringUtils.isBlank(token)) {
                    return Result.error("请先登录！");
                }
                Long currentUserId = null;
                try {
                    DecodedJWT decodedJWT = JWT.decode(token);
                    List<String> audienceList = decodedJWT.getAudience();
                    if (!audienceList.isEmpty() && StringUtils.isNumeric(audienceList.get(0))) {
                        currentUserId = Long.parseLong(audienceList.get(0));
                    }
                } catch (Exception e) {
                    log.error("JWT解析失败：", e);
                    return Result.error("登录信息异常，请重新登录！");
                }
                if (currentUserId == null) {
                    return Result.error("用户ID获取失败，请重新登录！");
                }
                if (!currentUserId.equals(product.getUserId())) {
                    return Result.error("无权查看他人商品！");
                }
            }

            return Result.success(product);
        } catch (Exception e) {
            log.error("查询商品详情异常：", e);
            return Result.error("查询商品详情失败：" + e.getMessage());
        }
    }

    // 删除商品（普通用户只能删自己的，管理员可删所有）
    @PostMapping("/{ids}")
    public Result delete(
            @PathVariable String[] ids,
            @RequestHeader(value = "token", required = false) String token,
            @RequestParam(defaultValue = "false") Boolean isAdmin
    ) {
        try {
            // 普通用户：校验归属
            if (!isAdmin) {
                if (StringUtils.isBlank(token)) {
                    return Result.error("请先登录！");
                }
                Long currentUserId = null;
                try {
                    DecodedJWT decodedJWT = JWT.decode(token);
                    List<String> audienceList = decodedJWT.getAudience();
                    if (!audienceList.isEmpty() && StringUtils.isNumeric(audienceList.get(0))) {
                        currentUserId = Long.parseLong(audienceList.get(0));
                    }
                } catch (Exception e) {
                    log.error("JWT解析失败：", e);
                    return Result.error("登录信息异常，请重新登录！");
                }
                if (currentUserId == null) {
                    return Result.error("用户ID获取失败，请重新登录！");
                }

                // 校验所有商品归属
                for (String idStr : ids) {
                    Long id = Long.parseLong(idStr);
                    Products product = productsService.getById(id);
                    if (product == null) {
                        return Result.error("删除失败，商品ID=" + id + "不存在！");
                    }
                    if (!currentUserId.equals(product.getUserId())) {
                        return Result.error("删除失败，无权删除他人商品（ID=" + id + "）！");
                    }
                }
            }

            productsService.removeByIds(Arrays.asList(ids));
            return Result.success("删除成功");
        } catch (NumberFormatException e) {
            log.error("商品ID格式错误：", e);
            return Result.error("删除失败，商品ID格式错误！");
        } catch (Exception e) {
            log.error("删除商品异常：", e);
            return Result.error("删除商品失败：" + e.getMessage());
        }
    }
}