package com.example.ruralfruits;

import com.example.ruralfruits.entity.SysUser;
import com.example.ruralfruits.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class RuralFruitsApplicationTests {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Test
    void testFindAll() {
        List<SysUser> list = sysUserMapper.selectList(null);
        list.forEach(item -> {
            System.out.println(item.getUsername()+" \t"+item.getUsername());
        });
    }

    @Test
    void testFindById() {
        SysUser user = sysUserMapper.selectById(1);
        System.out.println(user.getUsername()+" \t"+user.getUsername());
    }

    @Test
    public void testInsertMerchant() {
        SysUser merchantUser = new SysUser();
        merchantUser.setUsername("merchant_insert_002"); // 唯一商家账号
        merchantUser.setPassword("merchant1234");
        merchantUser.setNickname("商家");
        merchantUser.setPhone("13800138006");
        merchantUser.setEmail("merchant_insert_001@ruralfruits.com");
        merchantUser.setSex(0); // 性别：0-未知
        merchantUser.setStatus(0);
        merchantUser.setType("merchant"); // 自定义用户类型：商家
        int result = sysUserMapper.insert(merchantUser);
        System.out.println(result);
    }

}
