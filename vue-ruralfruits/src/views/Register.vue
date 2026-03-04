<template>
  <div class="main">
    <div class="login-box">
      <h2 style="text-align: center" >注册</h2>
      <el-form  :model="form" :rules="rules" label-width="auto"  ref="loginForm" >
        <el-form-item prop="username" label="用户名">
          <el-input v-model="form.username" autofocus prefix-icon="User" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item prop="password" label="密码">
          <el-input v-model="form.password" type="password" show-password prefix-icon="Lock" placeholder="请输入用户密码"></el-input>
        </el-form-item>
        <el-form-item prop="confirmPwd" label="确认密码">
          <el-input v-model="form.confirmPwd" type="password" show-password prefix-icon="Lock" autocomplete="off"placeholder="请输入确认密码"></el-input>
        </el-form-item>
        <el-form-item prop="nickname" label="昵称">
          <el-input v-model="form.nickname" prefix-icon="Mail" autocomplete="off" placeholder="请输入昵称"/>
        </el-form-item>
        <el-form-item prop="phone" label="手机号">
          <el-input v-model="form.phone" prefix-icon="Phone" autocomplete="off" placeholder="请输入手机号"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleRegister">立即注册</el-button>
        </el-form-item>
      </el-form>

      <div style="text-align: right">
        已有账号?<router-link to="/login">去登录</router-link>
      </div>

    </div>
  </div>
</template>

<script setup>
import {reactive ,ref} from 'vue';
import {register} from "@/api/login.js";
import {ElMessage} from "element-plus";
import {useRouter} from "vue-router";

const form=reactive({});//表单数据
const loginForm=ref();//表单引用
const router=useRouter();

//定义验证规则
const validatePass2=(rule,value,callback)=>{
  if (value!==form.password){
    callback(new Error("两次密码输入不一致！"))
  }else {
    callback()
  }
}

//表单校验规则
const rules=reactive({
  username:[
    {required:true,message:'请填写登录名',trigger:'blur'}
  ],
  password:[
    {required:true,message:'请填写密码',trigger:'blur'}
  ],
  confirmPwd:[
    {required:true,message:'请输入确认密码',trigger:'blur'},
    {validator:validatePass2,trigger: "blur"}
  ]

});

//处理用户注册
const handleRegister=()=>{
loginForm.value.validate(valid=>{
  if (valid){
    register(form).then(res=>{
      ElMessage.success("注册成功！")
      router.push('/login');
    })
  }
});

}
</script>

<style scoped>
.main{
  width: 100%;
  height: calc(100vh);
  background: url("@/assets/img/background1.jpg") no-repeat center center;
  background-size: cover;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-box{
  width: 500px;
  background-color: white;
  border-radius: 5px;
  padding: 15px;
}
</style>