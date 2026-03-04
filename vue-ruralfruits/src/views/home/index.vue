<template>
  <Header active-name="/index"></Header>
  <div class="container">
    <!-- 轮播图（修复：空数据兜底 + 图片加载优化） -->
    <el-carousel
        :interval="4000"
        type="card"
        height="350px"
        style="margin-top: 20px; border-radius: 8px; overflow: hidden;"
    >
      <el-carousel-item
          v-for="(item, index) in bannerList"
          :key="item?.id || index"
      >
        <el-image
            :src="item?.bannerImg || ''"
            fit="cover"
            style="width: 100%; height: 100%;"
            fallback="@/assets/images/default-banner.png"
        >
          <template #loading>
            <div class="image-loading">加载中...</div>
          </template>
        </el-image>
      </el-carousel-item>
      <!-- 轮播图空数据兜底 -->
      <el-carousel-item v-if="bannerList.length === 0" :key="0">
        <div style="width: 100%; height: 350px; background: #f5f5f5; display: flex; align-items: center; justify-content: center; color: #999;">
          暂无轮播图数据
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 通知列表 -->
    <div class="notice-container">
      <h3>通知列表</h3>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading">加载中...</div>

      <!-- 空数据状态 -->
      <div v-else-if="allNotices.length === 0" class="empty">暂无通知数据</div>

      <!-- 固定显示最新2条通知 -->
      <template v-else>
        <!-- 最新2条通知 -->
        <div
            class="notice-item"
            v-for="notice in latestTwoNotices"
            :key="notice?.id"
        >
          <div class="notice-text">
            {{ formatTime(notice?.time) }}：{{ notice?.title }}
          </div>
          <!-- 弹窗：用notice.id绑定独立实例 -->
          <el-popover
              placement="right"
              width="500"
              trigger="click"
              :append-to-body="true"
              popper-class="detail-popover"
              :ref="(el) => popoverRefs[notice?.id] = el"
          >
            <div class="notice-content">
              <h4 class="content-title">
                通知详情
                <!-- 修复：type="link" → type="text" + 自定义link-btn样式 -->
                <el-button type="text" size="small" @click="closePopover(notice?.id)" class="link-btn">关闭</el-button>
              </h4>
              <div class="content-body">
                {{ notice?.content || "暂无详细通知内容" }}
              </div>
            </div>
            <template #reference>
              <span class="detail-btn">详情</span>
            </template>
          </el-popover>
        </div>

        <!-- 显示全部按钮 -->
        <div v-if="allNotices.length > 2" class="show-all-btn-container">
          <el-button
              type="text"
              @click="showAll = true"
              :disabled="loading"
              class="link-btn"
          >
            显示过往通知
          </el-button>
        </div>

        <!-- 全部通知列表 -->
        <div v-if="showAll" class="all-notices-container">
          <!-- 收起按钮 -->
          <div class="collapse-btn-container">
            <el-button type="text" @click="showAll = false" class="link-btn">收起</el-button>
          </div>
          <!-- 剩余通知 -->
          <div
              class="notice-item"
              v-for="notice in remainingNotices"
              :key="notice?.id"
          >
            <div class="notice-text">
              {{ formatTime(notice?.time) }}：{{ notice?.title }}
            </div>
            <el-popover
                placement="right"
                width="500"
                trigger="click"
                :append-to-body="true"
                popper-class="detail-popover"
                :ref="(el) => popoverRefs[notice?.id] = el"
            >
              <div class="notice-content">
                <h4 class="content-title">
                  通知详情
                  <el-button type="text" size="small" @click="closePopover(notice?.id)" class="link-btn">关闭</el-button>
                </h4>
                <div class="content-body">
                  {{ notice?.content || "暂无详细通知内容" }}
                </div>
              </div>
              <template #reference>
                <span class="detail-btn">详情</span>
              </template>
            </el-popover>
          </div>
        </div>
      </template>
    </div>

    <!-- 最新上架的农产品 -->
    <div class="product-section">
      <div class="title">
        <h2>最新上架的农产品</h2>
        <el-link type="primary">更多农产品</el-link>
      </div>
      <el-row :gutter="15" style="margin-top: 10px;">
        <el-col :span="6" v-for="item in productList" :key="item.id" style="margin-bottom: 10px;">
          <el-card shadow="hover">
            <el-image
                :src="item.image_url"
                fit="cover"
                style="width: 100%; height: 200px;"
            />
            <template #footer>
              <div style="margin: 5px 0; font-size: 16px; font-weight: 500;">{{ item.name }}</div>
              <el-text line-clamp="2" style="color: #666; font-size: 14px;">
                {{ item.description || "暂无商品描述" }}
              </el-text>
            </template>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 农产品推荐 -->
    <div class="product-section">
      <div class="title">
        <h2>农产品推荐</h2>
      </div>
      <el-row :gutter="15" style="margin-top: 10px;">
        <el-col :span="6" v-for="item in (recommendList || [])" :key="item.id" style="margin-bottom: 10px;">
          <el-card shadow="hover">
            <el-image
                :src="item.image_url"
                fit="cover"
                style="width: 100%; height: 200px;"
            />
            <template #footer>
              <div style="margin: 5px 0; font-size: 16px; font-weight: 500;">{{ item.name }}</div>
              <el-text line-clamp="2" style="color: #666; font-size: 14px;">
                {{ item.description || "暂无商品描述" }}
              </el-text>
            </template>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>

  <!-- 详情弹窗（统一复用） -->
  <el-dialog
      title="通知详情"
      :model-value="detailVisible"
      :width="500"
      @close="detailVisible = false"
  >
    <div class="dialog-content">
      {{ currentNotice?.content || "暂无详细通知内容" }}
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import Header from "./Header.vue";
import { listAllBanners } from '@/api/banner.js';
import { listProducts, listRecommendProducts } from "@/api/products.js";
import { ElButton, ElDialog, ElPopover, ElMessage } from "element-plus";
import {listNotice} from "@/api/notice.js";

// 轮播图、商品相关变量
const bannerList = ref([]);
const productList = ref([]);       // 最新上架的农产品集合
const recommendList = ref([]);     // 推荐商品集合（初始化为空数组）

// 通知相关核心变量
const allNotices = ref([]);        // 存储所有通知数据
const loading = ref(false);        // 通知加载状态
const showAll = ref(false);        // 是否显示全部通知
const detailVisible = ref(false);  // 详情弹窗显示状态
const currentNotice = ref(null);   // 当前选中的通知
const popoverRefs = ref({});       // 存储所有弹窗实例（键：notice.id）

// 计算属性：最新2条通知
const latestTwoNotices = computed(() => {
  return allNotices.value.slice(0, 2);
});

// 计算属性：剩余通知
const remainingNotices = computed(() => {
  return allNotices.value.slice(2);
});

// 页面加载时初始化数据
onMounted(() => {
  fetchBannerList();
  fetchProductList();
  fetchRecommendList();
  fetchAllNotices();
});

// 1. 获取轮播图数据
const fetchBannerList = async () => {
  try {
    const res = await listAllBanners();
    bannerList.value = res.data || [];
  } catch (error) {
    console.error("获取轮播图失败：", error);
    ElMessage.error("轮播图加载失败");
  }
};

// 2. 获取最新上架农产品（只加了 isAdmin: true 这一行）
const fetchProductList = async () => {
  try {
    const res = await listProducts({
      pageNum: 1,
      pageSize: 8,
      column: "create_time",
      sort: "desc",
      isAdmin: true  // 核心修改：告诉后端查全部商品，无需登录
    });
    productList.value = res.data?.list || [];
  } catch (error) {
    console.error("获取最新农产品失败：", error);
    ElMessage.error("最新农产品加载失败");
  }
};

// 3. 获取推荐商品
const fetchRecommendList = async () => {
  try {
    const res = await listRecommendProducts();
    recommendList.value = res.data || [];
  } catch (error) {
    console.error("获取推荐商品失败：", error);
    ElMessage.error("推荐商品加载失败");
  }
};

// 4. 获取所有通知数据
const fetchAllNotices = async () => {
  loading.value = true;
  try {
    const queryParams = {
      column: "time",
      sort: "desc",
      open: 1,
      pageSize: 9999 // 一次性获取所有数据
    };

    const res = await listNotice(queryParams);
    if (res?.code === 200) {
      allNotices.value = res.data?.records || [];
    } else {
      ElMessage.error(res?.msg || "获取通知失败");
      allNotices.value = [];
    }
  } catch (error) {
    console.error("请求通知接口异常：", error);
    ElMessage.error("网络错误，请重试");
    allNotices.value = [];
  } finally {
    loading.value = false;
  }
};

// 格式化时间（yyyy-MM-dd HH:mm:ss → yyyy-MM-dd）
const formatTime = (time) => {
  if (!time) return "";
  return time.split(" ")[0];
};

// 打开详情弹窗
const openDetail = (notice) => {
  currentNotice.value = notice;
  detailVisible.value = true;
};

// 关闭指定通知的弹窗
const closePopover = (noticeId) => {
  const currentPopover = popoverRefs.value[noticeId];
  if (currentPopover) {
    currentPopover.hide();
  }
};
</script>

<style scoped>
/* 轮播图样式 */
.el-carousel__item {
  height: 100% !important;
}
.el-carousel__container {
  height: 100% !important;
}
.image-loading {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  color: #666;
}

/* 通用容器样式 */
.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  box-sizing: border-box;
}

/* 通知列表容器 */
.notice-container {
  margin: 30px 0;
  padding: 20px;
  background-color: #fafafa;
  border-radius: 8px;
}

/* 商品区域样式 */
.product-section {
  margin: 30px 0;
}

/* 标题样式 */
.title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-left: 4px solid #409EFF;
  margin-bottom: 15px;
}
.title h2 {
  font-weight: 500;
  margin: 0;
  padding-left: 10px;
  font-size: 18px;
}

/* 通知项样式 */
.notice-item {
  margin: 8px 0;
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-radius: 6px;
  transition: background-color 0.2s ease;
}
.notice-item:hover {
  background-color: #f8fafc;
}

/* 通知文本样式 */
.notice-text {
  flex: 1;
  margin-right: 16px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 14px;
  color: #333;
}

/* 详情按钮样式 */
.detail-btn {
  color: #1989fa;
  cursor: pointer;
  font-size: 14px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}
.detail-btn:hover {
  background-color: #e8f4f8;
}

/* 核心修复：模拟link按钮样式（替换type="link"） */
.link-btn {
  color: #1989fa !important; /* 链接蓝色 */
  padding: 4px 8px !important;
  font-size: 14px !important;
  background: transparent !important;
  border: none !important;
}
.link-btn:hover {
  color: #0e75d3 !important; /* hover加深 */
  text-decoration: underline !important; /* 下划线（可选） */
  background: transparent !important;
}

/* 加载状态样式 */
.loading {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 14px;
}

/* 空数据状态样式 */
.empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  background-color: #fff;
  border-radius: 8px;
  font-size: 14px;
}

/* 按钮容器样式 */
.show-all-btn-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  padding: 8px 0;
}
.collapse-btn-container {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
  padding: 8px 0;
}

/* 详情弹窗样式 */
.detail-popover .el-popover__content {
  padding: 0 !important;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.notice-content {
  padding: 16px;
}
.content-title {
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  color: #333;
}
.content-body {
  line-height: 1.8;
  font-size: 14px;
  color: #666;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
}

/* 对话框内容样式 */
.dialog-content {
  line-height: 1.8;
  font-size: 14px;
  color: #666;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 400px;
  overflow-y: auto;
  padding: 10px 0;
}

/* 卡片样式优化 */
.el-card {
  border-radius: 8px;
  overflow: hidden;
}
.el-card__footer {
  padding: 12px;
  background-color: #fff;
}
</style>