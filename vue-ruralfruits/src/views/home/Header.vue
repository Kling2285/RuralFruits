<template>
  <header>
    <div class="logo">
      <img src="@/assets/img/logo.png" alt="">
      <h3>振兴助农平台</h3>
    </div>
    <div class="menu">
      <el-menu
          :default-active="currentActiveName"
          mode="horizontal"
          router
          style="flex: 1; border-bottom: none;"
          underline
          active-text-color="#409EFF"
      >
        <el-menu-item index="/index">首页</el-menu-item>
        <el-menu-item index="/products">农产品</el-menu-item>
        <el-menu-item index="/carts">我的购物车</el-menu-item>
        <el-menu-item index="/orders">我的订单</el-menu-item>
        <el-menu-item index="/person">个人中心</el-menu-item>
      </el-menu>
      <el-input
          v-model="keyword"
          placeholder="搜索农产品"
          style="height: 35px; width: 300px"
          @keyup.enter.native="searchKeyword"
      >
        <template #append>
          <el-button icon="Search" @click="searchKeyword"/>
        </template>
      </el-input>
    </div>
    <div class="user">
      <el-link v-if="!user" href="/login">登录</el-link>
      <el-dropdown v-else>
        <span class="el-dropdown-link">
          {{ user.nickname }}
          <el-icon class="el-icon--right">
            <arrow-down/>
          </el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleLogout">注销登陆</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup>
import { reactive, ref, onMounted } from "vue";
import { getUser, removeUser } from "@/utils/user.js";
import { ArrowDown } from "@element-plus/icons-vue";
import { ElMessage } from 'element-plus';
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();
const user = ref(null);
const keyword = ref('');
const currentActiveName = ref('/index');

onMounted(() => {
  user.value = getUser();
  currentActiveName.value = route.path;
});

const searchKeyword = () => {
  const key = keyword.value.trim();
  if (!key) return;
  router.push('/search?words=' + encodeURIComponent(key));
  keyword.value = '';
};

const handleLogout = () => {
  removeUser();
  user.value = null;
  ElMessage.success("注销成功");
  router.push("/index");
};
</script>

<style scoped>
header {
  display: flex;
  border-bottom: 1px solid #eeeeee;
  height: 60px;
  align-items: center;
}
div.logo {
  width: 240px;
  display: flex;
  align-items: center;
  gap: 10px;
}
div.logo img {
  width: 45px;
  height: 45px;
  object-fit: cover;
}
div.user {
  width: 200px;
  text-align: right;
  padding-right: 25px;
}
div.menu {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 20px;
}
.el-dropdown-link {
  outline: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}
.el-link {
  color: #409EFF;
  text-decoration: none;
}
.el-link:hover {
  color: #66b1ff;
}
</style>