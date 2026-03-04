<template>
  <Header active-name="/details"></Header>
  <div class="container">
    <div class="title">
      农产品详情
    </div>
    <!-- 加载状态 -->
    <div v-if="loading" style="text-align: center; padding: 100px 0; font-size: 18px; color: #666;">
      加载中...
    </div>
    <!-- 商品不存在提示 -->
    <div v-else-if="!product.id" style="text-align: center; padding: 100px 0; font-size: 18px; color: #999;">
      暂无该商品数据
    </div>
    <!-- 商品详情（数据存在才渲染） -->
    <div v-else class="details">
      <el-image class="image" :src="product.image_url || ''"/>
      <div class="info">
        <h3>{{product.name || '未知商品'}}</h3>
        <div class="item">
          <label>价格：</label>
          <span>￥{{product.price || 0}}/{{product.unit || '件'}}</span>
        </div>
        <div class="item">
          <label>产地：</label>
          <span>{{product.producer || '暂无信息'}}</span>
        </div>
        <div class="item">
          <label>库存：</label>
          <span>{{product.stock || 0}}</span>
        </div>
        <div class="item" style="color: #cccccc">
          {{product.description || '暂无商品描述'}}
        </div >
        <div class="item" style="margin-top: 25px">
          <el-input-number v-model="cartForm.count" :min="1" :max="product.stock || 10" style="margin-right: 15px"/>
          <el-button type="danger" icon="ShoppingCart" @click="addToCart">加入购物车</el-button>
        </div>
      </div>
    </div>
    <el-tabs v-model="activeName" style="margin-top: 20px;" v-if="product.id">
      <el-tab-pane label="商品详情" name="details">
        <div v-html="product.content || '<p>暂无详细介绍</p>'"></div>
      </el-tab-pane>
      <el-tab-pane label="商品评价" name="comment">
        <el-form :model="form" label-width="auto" >
          <el-form-item >
            <el-input v-model="form.desc" type="textarea" placeholder="请填写您的评价"/>
          </el-form-item>
        </el-form>
        <div class="comments">
          <div class="comment" v-for=" item in commentList"  :key="item.id">
            <div class="user">
              {{ item.createUser }} 于 {{ dayjs(item.createTime).format('YYYY-MM-DD HH:mm:ss') }}发布评论
            </div>
            <div style="color: saddlebrown">
              {{item.comment}}
            </div>
          </div>
          <!-- 无评论提示 -->
          <div v-if="commentList.length === 0" style="text-align: center; padding: 20px; color: #999;">
            暂无评论，快来抢沙发～
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script setup>
import {useRoute}from 'vue-router'
import {ref,onMounted} from "vue";
import Header from "./Header.vue"
import {getProducts} from "@/api/products.js";
import {listComment} from'@/api/comment.js'
import {addCart} from'@/api/cart.js'
import dayjs from 'dayjs';
import {ElMessage}from'element-plus'
import {getUser} from "@/utils/user.js";

const route=useRoute();
const productId=route.params.id; // 提取商品ID
const product=ref({});//农产品信息
const activeName=ref('details')
const form=ref({});
const commentList=ref([]);//评论列表
const cartForm=ref({
  count:1
});//购物车表单数据
const loading=ref(true);// 加载状态

//加入购物车
const addToCart=()=> {
  //判断用户是登录
  let user = getUser();
  if (user == null) {
    ElMessage.error("登陆后才可以加入购物车");
    return;
  }
  // 防止商品数据为空时提交
  if (!product.value.id) {
    ElMessage.error("商品信息未加载完成");
    return;
  }
  addCart(cartForm.value).then(res=>{
    ElMessage.success("加入购物车成功")
  }).catch(err=>{
    ElMessage.error("加入购物车失败：" + (err.message || '网络异常'));
  });
}
defineExpose({ dayjs });

onMounted(()=>{
  //查询农产品详情信息（关键：加 isAdmin=true，无需登录查商品）
  getProducts(productId, true).then(res=>{
    product.value=res.data || {};
    // 初始化购物车表单（判断用户是否登录）
    let user=getUser();
    if (user) {
      cartForm.value.userId=user.userId;
      cartForm.value.createUser=user.nickname;
    }
    cartForm.value.name=product.value.name || '';
    cartForm.value.type=product.value.category || '';
    cartForm.value.price=product.value.price || 0;
    cartForm.value.unit=product.value.unit || '件';
    cartForm.value.goodsId=product.value.id || '';
    cartForm.value.image=product.value.image_url || '';
  }).catch(err=>{
    console.error("获取商品详情失败：", err);
    ElMessage.error("商品详情加载失败");
    product.value={};
  }).finally(()=>{
    loading.value=false; // 加载完成
  })

  //查询评论信息
  listComment(productId).then(res=>{
    commentList.value=res.data || [];
  }).catch(err=>{
    console.error("获取评论失败：", err);
    ElMessage.error("评论加载失败");
    commentList.value=[];
  })
})
</script>

<style scoped>
.details{
  display: flex;
}
.details .image{
  width: 350px;
  height: 270px;
  margin-right: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
}
div.item{
  line-height: 35px;
}
div.item label{
  font-weight: bold;
}
div.comment{
  background-color: #eeeeee;
  margin-bottom: 5px;
  padding: 10px;
  border-radius: 8px;
}
div.user{
  margin-bottom: 10px;
  color: #666;
}
</style>