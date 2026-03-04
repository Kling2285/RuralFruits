<template>
  <!-- 模板代码完全不变 -->
  <div class="main">
    <div class="login-box">
      <h2 style="text-align: center">登录</h2>

      <el-form :model="form" :rules="rules" label-width="auto" ref="loginForm">
        <el-form-item prop="username">
          <el-input
              v-model="form.username"
              autofocus
              prefix-icon="User"
              placeholder="请输入用户名"
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              v-model="form.password"
              type="password"
              @keyup.enter.native="handleLogin"
              show-password
              prefix-icon="Lock"
              placeholder="请输入用户密码"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>

      <div style="text-align: right; margin-top: 10px;">
        没有账号?<router-link to="/register">去注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { login } from "@/api/login.js";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import {storeUser}from '@/utils/user.js'

const form = reactive({
  username: '',
  password: ''
});
const loginForm = ref();
const router = useRouter();
const loading = ref(false);

const rules = reactive({
  username: [
    { required: true, message: '请填写登录名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请填写密码', trigger: 'blur' }
  ]
});

const handleLogin = () => {
  loginForm.value.validate(valid => {
    if (valid) {
      loading.value = true;
      login(form).then(res => {
        loading.value = false;
        if (res.code === 200) {
          const userInfo = res.data;
          storeUser(res.data);
          sessionStorage.setItem("token", userInfo.token);

          ElMessage.success("登录成功！");


          // 核心变更：添加用户类型判断跳转
          if (res.data.type === 'user') {
            router.push('/index');
          } else {
            router.push('/manager/home');
          }

        } else {
          ElMessage.error("登录失败：" + (res.message || '未知错误'));
        }
      }).catch(err => {
        loading.value = false;
        console.error("登录请求异常：", err);
        ElMessage.error("登录失败：网络错误或服务器异常");
      });
    }
  });
};
</script>

<style scoped>
.main {
  width: 100%;
  height: calc(100vh);
  background: url("@/assets/img/background1.jpg") no-repeat center center;
  background-size: cover;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-box {
  width: 400px;
  height: 320px;
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.el-loading-mask {
  z-index: 9999 !important;
}
</style>