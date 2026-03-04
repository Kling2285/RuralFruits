<template>
  <div>
    <!--头部布局开始-->
    <header>
      <div class="logo">
        <img src="@/assets/logo.svg" alt="">
        乡村振兴助农平台
      </div>
      <div class="nav">
        首页 / 用户管理
      </div>
      <div class="user">
        <el-dropdown>
          <span class="el-dropdown-link">
            <img src="@/assets/img/avatar.jpg">
           {{user.nickname}}
            <el-icon class="el-icon--right">
              <ArrowDown />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人信息</el-dropdown-item>
              <el-dropdown-item>修改密码</el-dropdown-item>
              <el-dropdown-item @click="logout">注销登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    <!-- 头部布局结束 -->
    <div class="container">
      <!--左侧导航菜单开始-->
      <aside>
        <el-menu
            active-text-color="#ffd04b"
            background-color="#545c64"
            class="el-menu-vertical-demo"
            :default-active="user?.type === 'merchant' ? '/manager/myproducts' : '/manager/home'"
            text-color="#fff"
            router
            style="height: calc(100vh - 60px);"
        >
          <!-- 首页：非商家显示 -->
          <el-menu-item index="/manager/home" v-if="user?.type !== 'merchant'">
            <el-icon class="menu-icon"><HomeFilled/></el-icon>
            <span>首页</span>
          </el-menu-item>

          <!-- 系统管理：非商家显示 -->
          <el-sub-menu index="1" v-if="user?.type !== 'merchant'">
            <template #title>
              <el-icon><Location /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/manager/notice">
              <el-icon><Discount /></el-icon>
              <span>通知管理</span>
            </el-menu-item>
            <el-menu-item index="/manager/user">
              <el-icon><UserFilled /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/manager/ordermanager">
              <el-icon><Service /></el-icon>
              <span>订单管理</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- 平台管理：非商家显示 -->
          <el-sub-menu index="2" v-if="user?.type !== 'merchant'">
            <template #title>
              <el-icon><Location /></el-icon>
              <span>平台管理</span>
            </template>
            <el-menu-item index="/manager/apply">
              <el-icon><Discount /></el-icon>
              <span>申请管理</span>
            </el-menu-item>
            <el-menu-item index="/manager/products">
              <el-icon><UserFilled /></el-icon>
              <span>农产品管理</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- 店铺管理：默认显示（商家和管理员都能看） -->
          <el-sub-menu index="3">
            <template #title>
              <el-icon><Location /></el-icon>
              <span>店铺管理</span>
            </template>
            <el-menu-item index="/manager/myproducts">
              <el-icon><UserFilled /></el-icon>
              <span>农产品管理</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </aside>
      <!--左侧导航菜单结束-->
      <!--主显示区域开始-->
      <div class="main">
        <router-view/>
      </div>
      <!--主显示区域结束-->
    </div>
  </div>
</template>

<script setup>
// 脚本部分完全不变！
import {Discount, HomeFilled, Service, UserFilled, Location, ArrowDown} from "@element-plus/icons-vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {useRouter} from "vue-router";
import {ref} from "vue";

const user=ref({});//登录用户
const router=useRouter();
const logout=()=>{
  ElMessageBox.confirm('确定要注销登录吗？',"提示",{
    confirmButtonText:'确定',
    cancelButtonText:'取消',
    type:'warning'
  }).then(res=>{
    sessionStorage.removeItem("login_user");
    router.push('/login')
  });
}

user.value=JSON.parse(sessionStorage.getItem("login_user"));
// 脚本部分添加打印（测试完可删除）
console.log("当前用户数据：", user.value); // 打开浏览器F12→Console面板看输出
</script>

<style scoped>
/* 样式部分完全不变！ */
header{
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}
header div.logo{
  display: flex;
  align-items: center;
  background-color: #545c64;
  color: white;
  font-family: "微软雅黑";
  font-size: 18px;
  padding:5px 8px;
  margin-right: 25px;
  width: 239px;
  height: 60px;
}
header div.logo img{
  width: 40px;
  height: 40px;
}
header div.nav{
  flex: 1;
  border-bottom: 1px solid #eee;
  height: 100%;
  display: flex;
  align-items: center;
}
header div.user{
  padding-right: 15px;
  height: 100%;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
}
header div.user img{
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 6px;
}
header div.user .el-dropdown-link{
  display: flex;
  align-items: center;
  outline: none;
  cursor: pointer;
}
.container{display: flex;margin-left: 0}
.container aside{
  width: 240px;

}
.container div.main{
  flex: 1;
  padding: 10px;
  background-color: aliceblue;
}
</style>