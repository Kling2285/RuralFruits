<template>
  <Header active-name="/products"></Header>
  <div class="container">
    <div class="title">
      农产品列表
    </div>
    <div class="categories">
      <el-link type="primary" @click="searchProduct('')">全部</el-link>
      <el-link type="primary" v-for="item in categoryLlist" :key="item.id" @click="searchProduct(item.name)">{{item.name}}</el-link>
    </div>
    <el-row style="margin-top: 10px;":gutter="15">
      <!-- 加v-if防止数据没加载时渲染报错 -->
      <el-col :span="6" v-for="item in productList" :key="item.id" style="margin-bottom: 10px;" v-if="productList.length">
        <el-card shadow="hover">
          <router-link :to="`/details/${item.id}`">
            <el-image
                :src="item.image_url || ''"
                fit="fill"
                style="width: 100%;height: 200px;"
            />
          </router-link>
          <template #footer>
            <div style="margin: 5px 0px; font-size: 16px;">{{ item.name || '未知商品' }}</div>
            <el-text line-clamp="2">
              {{ item.description || '暂无简介' }}
            </el-text>
          </template>
        </el-card>
      </el-col>
      <!-- 无数据时显示提示 -->
      <div v-else style="text-align: center; width: 100%; padding: 50px 0;">
        暂无农产品数据
      </div>
    </el-row>
    <el-pagination background layout="prev,pager,next" v-model:page-size="queryParams.pageSize"
                   v-model:current-page="queryParams.pageNum" :total="total"
                   @current-change="loadProducts"/>
  </div>
</template>
<script setup>
import {ref,onMounted} from "vue";
import Header from "./Header.vue";
import {listAllCategories}from '@/api/categories.js'
import {listProducts} from "@/api/products.js";
import {ElMessage} from "element-plus"; // 引入提示组件

//查询参数：添加 isAdmin=true（关键！告诉后端查全部）
const queryParams=ref({
  pageNum:1,
  pageSize:8,
  category:'',
  isAdmin: true // 无需登录查全部的核心参数
});

const total=ref(0);//农产品条数
const categoryLlist=ref([]);//农产品类别列表
const productList=ref([]);//农产品列表

const loadProducts=()=>{
  // 查询前清空数据，避免旧数据残留
  productList.value=[];
  // 传递含 isAdmin=true 的参数，查全部商品
  listProducts(queryParams.value).then(res=>{
    // 兼容后端不同返回格式
    const data = res.data || res;
    productList.value = data.list || data || [];
    total.value = data.total || productList.value.length;
  }).catch(err=>{
    // 捕获错误并提示
    ElMessage.error('加载商品失败：' + (err.message || '网络异常'));
    productList.value=[];
    total.value=0;
  })
}
const searchProduct=category =>{
  queryParams.value.category=category;
  queryParams.value.pageNum=1; // 切换分类时重置到第1页
  loadProducts();
}

onMounted(()=>{
  //查询所有的农产品类别（加catch捕获错误）
  listAllCategories().then(res=>{
    categoryLlist.value=res.data || [];
  }).catch(err=>{
    ElMessage.error('加载分类失败：' + (err.message || '网络异常'));
  })

  loadProducts();
})
</script>

<style scoped>
.categories a{
  margin-right: 10px;;
}
.title{
  font-size: 18px;
  font-weight: bold;
  margin: 10px 0;
}
</style>