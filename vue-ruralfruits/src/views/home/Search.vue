
<template>
  <Header active-name="/products"></Header>
  <div class="container">
    <div class="title">
      搜索结果
    </div>

    <el-row style="margin-top: 10px;":gutter="15">
      <el-col :span="6" v-for="item in productList" style="margin-bottom: 10px;">
        <el-card shadow="hover">
          <el-image
              :src="item.image_url"
              fit="fill"
              style="width: 100%;height: 200px;"
          />
          <template #footer>
            <div style="margin: 5px 0px; font-size: 16px;">{{ item.name }}</div>
            <el-text line-clamp="2">
              {{ item.description }}
            </el-text>
          </template>
        </el-card>
      </el-col>
    </el-row>
    <el-pagination background layout="prev,pager,next" v-model:page-size="queryParams.pageSize"
                   v-model:current-page="queryParams.pageNum" :total="total"
                   @current-change="loadProducts"/>
  </div>
</template>
<script setup>
import {ref,onMounted,watch} from "vue";
import Header from "./Header.vue";
import {listProducts} from "@/api/products.js";
import {useRoute} from "vue-router";
import router from "@/router/index.js";

//查询参数：查询条件，分页参数，排序参数
const queryParams=ref({
  pageNum:1,
  pageSize:8,
  name:''
});

const route=useRoute();
const total=ref(0);//农产品条数
const productList=ref([]);//农产品列表

const loadProducts=()=>{
  //查询农产品信息
  listProducts(queryParams.value).then(res=>{
    productList.value=res.data.list;
    total.value=res.data.total;
  })
}
const searchProduct=category =>{
  queryParams.value.category=category;
  loadProducts();
}

//监听router路由参数的变化
watch(
    () => route.query.words, // 直接监听 words 参数，不是整个 query
    (newWords) => { // 接收新关键词
      queryParams.value.name = decodeURIComponent(newWords || ''); // 解码
      queryParams.value.pageNum = 1; // 重置到第1页
      loadProducts();
    },
    { immediate: true }
)

onMounted(()=>{
//获取传递过来的words参数
  queryParams.value.name=route.query.words;
  loadProducts();
})

</script>

<style scoped>
div.title{
  font-size: 25px;
  font-weight: lighter;
  margin: 15px 0px;
  padding: 10px 0px;
  border-bottom: 1px solid #eeeeee;
}
.categories a{
  margin-right: 10px;;
}
</style>