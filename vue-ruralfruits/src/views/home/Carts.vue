<template>
  <Header active-name="/details"></Header>
  <div class="container">
    <div class="title">
      我的购物车
      <!-- 余额显示（前端本地维护，初始500） -->
      <div class="balance" style="margin-left: auto;">
        我的余额：<span style="color: #e53935; font-weight: bold;">¥{{ userBalance.toFixed(2) }}</span>
      </div>
    </div>
    <el-table :data="cartList">
      <el-table-column label="商品名称" prop="name"></el-table-column>
      <el-table-column label="图片" prop="image">
        <template #default="scope">
          <el-image :src="scope.row.image" style="width: 50px; height: 50px;" />
        </template>
      </el-table-column>
      <el-table-column label="价格" prop="price"></el-table-column>
      <el-table-column label="单位" prop="unit"></el-table-column>
      <el-table-column label="数量" prop="count"></el-table-column>
      <el-table-column label="操作" prop="image">
        <template #default="scope">
          <el-button type="danger" @click="removeCartItem(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top: 15px" v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
                   background layout="prev,pager,next" :total="total" @current-change="loadCartList"/>
    <div class="order" style="background-color: #eeeeee; display: flex; align-items: center; padding: 15px 0px; justify-content: flex-end;margin-top: 15px;">
      <div style="margin-right: 20px;">共 {{ totalMoney}}元</div>
      <el-button type="danger" icon="Plus" @click="toAddOrder">确认下单</el-button>
    </div>
    <el-dialog title="添加收货人信息" v-model="linkman_visible" width="800">
      <el-form :model="linkForm" label-width="auto" :rules="linkRules" ref="linkFormRef">
        <el-form-item label="收货人" prop="linkUser">
          <el-input v-model="linkForm.linkUser" />
        </el-form-item>
        <el-form-item label="联系方式" prop="linkPhone">
          <el-input v-model="linkForm.linkPhone" type="number" />
        </el-form-item>
        <el-form-item label="收货地址" prop="tem">
          <el-cascader
              style="width: 100%;"
              v-model="linkForm.tem"
              :options="regionData"
              placeholder="请选择地区"
              clearable
              @change="handleChange"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="linkForm.address" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="success" @click="handleSubmit">提交订单</el-button>
        <el-button type="danger" @click="linkman_visible=false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import Header from "@/views/home/Header.vue";
import { ref, computed, onMounted } from 'vue'
import { listCarts, deleteCart } from "@/api/cart.js";
import { addOrders } from "@/api/order.js";
// 移除余额相关接口导入（不需要后端了）
import { ElMessage, ElMessageBox } from "element-plus";
import { regionData, codeToText } from "element-china-area-data";

const linkForm = ref({});
const linkman_visible = ref(false);
const cartList = ref([]);
const total = ref(0);
const queryParams = ref({ pageNum: 1, pageSize: 10 });
const linkFormRef = ref(null);
// 余额写死初始值500（非响应式也可以，这里用ref方便显示）
const userBalance = ref(500.00);
// 简化：不需要用户ID（如果提交订单后端必须要，就写死一个，比如1）
const currentUserId = 1; // 写死用户ID，避免后端报错

// 表单校验规则
const linkRules = ref({
  linkUser: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  linkPhone: [{ required: true, message: '请输入联系方式', trigger: 'blur' }],
  tem: [{ required: true, message: '请选择收货地区', trigger: 'change' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
});

// 订单总金额计算
const totalMoney = computed(() => {
  let money = 0;
  cartList.value.forEach(item => {
    const price = parseFloat(item.price) || 0;
    const count = item.count || 0;
    money += price * count;
  });
  return money.toFixed(2);
});

// 移除加载余额的接口调用
const loadUserBalance = () => {
  console.log('初始余额：¥', userBalance.value);
};

// 提交订单（移除后端扣减余额接口，只前端本地递减）
const handleSubmit = async () => {
  // 1. 表单校验
  try {
    await linkFormRef.value.validate();
  } catch (err) {
    return;
  }

  // 2. 校验订单金额
  const orderAmount = parseFloat(totalMoney.value);
  if (orderAmount <= 0) {
    ElMessage.error('订单金额不能为0');
    return;
  }

  // 3. 校验余额（前端本地判断）
  if (userBalance.value < orderAmount) {
    ElMessage.error(`余额不足！当前余额：¥${userBalance.value.toFixed(2)}，订单金额：¥${orderAmount.toFixed(2)}`);
    return;
  }

  // 4. 二次确认
  ElMessageBox.confirm(
      `确认下单？`,
      '下单确认',
      { type: 'info' }
  ).then(async () => {
    try {
      // 5. 前端本地扣减余额（不需要调用后端接口）
      userBalance.value -= orderAmount;
      console.log('扣减后余额：¥', userBalance.value);

      // 6. 提交订单到后端（只创建订单，不涉及余额操作）
      const orderData = cartList.value.map(item => ({
        ...item,
        linkUser: linkForm.value.linkUser,
        linkPhone: linkForm.value.linkPhone,
        linkAddress: linkForm.value.linkAddress + linkForm.value.address,
        productId: item.goodsId,
        userId: currentUserId, // 写死用户ID
        orderId: `ORDER_${Date.now()}_${Math.floor(Math.random() * 1000)}`
      }));
      console.log('提交订单参数：', orderData);

      const res = await addOrders(orderData);
      console.log('提交订单接口返回：', res);
      if (res.code === 200) {
        ElMessage.success(`下单成功！`);
        linkman_visible.value = false;
        loadCartList(); // 刷新购物车
      } else {
        // 订单创建失败，恢复余额
        userBalance.value += orderAmount;
        ElMessage.error("下单失败：" + (res.message || res.msg || '未知错误'));
      }
    } catch (err) {
      // 异常情况，恢复余额
      userBalance.value += orderAmount;
      console.error('下单异常：', err);
      ElMessage.error('下单失败，请重试');
    }
  }).catch(() => {
    ElMessage.info('已取消下单');
  });
};

// 原有逻辑不变
const toAddOrder = () => {
  if (cartList.value.length === 0) {
    ElMessage.warning('购物车为空，无法下单');
    return;
  }
  linkman_visible.value = true;
};

const removeCartItem = (row) => {
  let id = row.id;
  deleteCart(id).then(res => {
    ElMessage.success("移除成功！");
    loadCartList();
  }).catch(err => {
    ElMessage.error("移除失败：" + (err.msg || '网络错误'));
  });
};

const handleChange = (value) => {
  linkForm.value.linkAddress = value.map((code) => codeToText[code]).join("");
};

const loadCartList = () => {
  listCarts(queryParams.value).then(res => {
    cartList.value = res.data.list || [];
    total.value = res.data.total || 0;
  }).catch(err => {
    ElMessage.error('获取购物车失败：' + (err.msg || '网络错误'));
  });
};

// 页面初始化（加载购物车+显示初始余额）
onMounted(() => {
  loadCartList();
  loadUserBalance();
});
</script>
<style scoped>
.title {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  margin: 20px 0;
}
.balance {
  font-size: 16px;
}
</style>