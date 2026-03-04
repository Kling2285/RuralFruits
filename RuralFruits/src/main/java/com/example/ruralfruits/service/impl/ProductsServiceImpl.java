package com.example.ruralfruits.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruralfruits.entity.Products;
import com.example.ruralfruits.entity.SysUser;
import com.example.ruralfruits.mapper.ProductsMapper;
import com.example.ruralfruits.service.ProductsService;
import com.example.ruralfruits.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductsServiceImpl extends ServiceImpl<ProductsMapper, Products> implements ProductsService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public List<Products> list(IPage<Products> page, Wrapper<Products> queryWrapper) {

        IPage<Products> productsPage = super.page(page, queryWrapper);
        List<Products> productList = productsPage.getRecords();


        Set<Long> userIds = productList.stream()
                .map(Products::getUserId) // 商品表的 user_id（Long 类型）
                .filter(Objects::nonNull) // 过滤 null 的 user_id
                .collect(Collectors.toSet());

        // 3. 关键修正：判断 userIds 是否为空，非空才查用户表（避免 IN () 语法错误）
        Map<Long, SysUser> userMap;
        if (!CollectionUtils.isEmpty(userIds)) { // 判空：只有 userIds 非空才查用户表
            List<SysUser> sysUsers = sysUserService.listByIds(userIds);
            // 4. 把用户列表转成 Map（key：user_id，value：SysUser），方便后续关联
            userMap = sysUsers.stream()
                    .filter(user -> user.getUserId() != null) // 过滤用户表中 null 的 user_id
                    .collect(Collectors.toMap(
                            user -> user.getUserId().longValue(), // 假设用户表 user_id 是 Integer，转成 Long 对齐
                            user -> user,
                            (k1, k2) -> k1 // 防止重复 user_id 冲突（取第一个）
                    ));
        } else {
            userMap = new HashMap<>();
        }

        // 5. 商品关联用户：给每个商品设置对应的 SysUser
        productList.forEach(product -> {
            Long userId = product.getUserId();
            product.setSysUser(userMap.get(userId)); // 空 user_id 或无对应用户，会设为 null（不报错）
        });

        return productList;
    }

    @Override
    public List<Map<String,Object>> selectCategoriesCount(){
        return baseMapper.selectCategoriesCount();
    }

    @Override
    public List<Products> selectRecommend(int count) {
        QueryWrapper<Products> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("rand()"); // 假设数据库支持 rand() 排序（如 MySQL）

        // 使用 MyBatis-Plus 分页，Page.of(当前页, 每页条数)
        IPage<Products> page = Page.of(1, count);
        IPage<Products> productsPage = baseMapper.selectPage(page, queryWrapper);
        return productsPage.getRecords();
    }


}