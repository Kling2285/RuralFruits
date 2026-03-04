<template>
  <Header active-name="/orders"></Header>
  <div class="container">
    <div class="title">
      我的订单
    </div>
    <div class="box">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="开始时间">
          <el-date-picker
              v-model="queryParams.params.begin"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="开始时间"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
              v-model="queryParams.params.end"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="结束时间"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="loadOrderList">查询</el-button>
          <el-button type="warning" icon="Promotion" @click="reset2">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-tabs v-model="activeName" class="demo-tabs" @tab-change="handleStatusChange">
      <el-tab-pane label="全部" name="-1"></el-tab-pane>
      <el-tab-pane label="未发货" name="0"></el-tab-pane>
      <el-tab-pane label="已发货" name="1"></el-tab-pane>
      <el-tab-pane label="已收货" name="2"></el-tab-pane>
    </el-tabs>
    <el-table :data="orderList">
      <el-table-column label="订单编号" prop="orderId"></el-table-column>
      <el-table-column label="商品名称" prop="name"></el-table-column>
      <el-table-column label="总价" prop="price">
        <template #default="scope">
          ￥{{ (scope.row.price * scope.row.count).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column label="数量" prop="count"></el-table-column>
      <el-table-column label="收货人" prop="linkUser"></el-table-column>
      <el-table-column label="联系电话" prop="linkPhone"></el-table-column>
      <el-table-column label="订单日期" prop="createTime"></el-table-column>
      <el-table-column label="订单状态" prop="status">
        <template #default="scope">
          <el-tag type="info" v-if="scope.row.status == 0">未发货</el-tag>
          <el-tag type="warning" v-else-if="scope.row.status==1">已发货</el-tag>
          <el-tag type="success" v-else-if="scope.row.status==2">已收货</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <div style="display: flex; gap: 8px; justify-content: center;">
            <el-button size="small" type="primary" icon="EditPen" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" icon="Remove" @click="removeOrderItem(scope.row)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top: 15px" v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
                   background layout="prev,pager,next" :total="total" @current-change="loadOrderList"/>

  </div>

  <!-- 新增编辑订单对话框（复用订单管理页面的表单逻辑） -->
  <el-dialog v-model="visible" :title="title" width="800">
    <el-form :model="form" :rules="rules" ref="formRef" :label-width="85">
      <el-form-item label="订单编号" prop="orderId">
        <el-input v-model="form.orderId"  disabled />
      </el-form-item>
      <el-form-item label="商品名称" prop="name">
        <el-input v-model="form.name" autocomplete="off"  disabled/>
      </el-form-item>
      <el-form-item label="购买数量" prop="count">
        <el-input v-model="form.count" type="number" disabled autocomplete="off" />
      </el-form-item>
      <el-form-item label="联系人" prop="linkUser">
        <el-input v-model="form.linkUser" autocomplete="off" />
      </el-form-item>
      <el-form-item label="联系电话" prop="linkPhone">
        <el-input v-model="form.linkPhone" autocomplete="off" />
      </el-form-item>
      <el-form-item label="收货地址" prop="linkAddress">
        <el-input v-model="form.linkAddress" type="textarea" :rows="3" autocomplete="off" />
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-select v-model="form.status" placeholder="请选择订单状态">
          <el-option label="未发货" value="0" />
          <el-option label="已发货" value="1" />
          <el-option label="已收货" value="2" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import Header from "@/views/home/Header.vue";
import { ref } from 'vue'
import { listOrders, getOrderDetail, updateOrder } from '@/api/order.js'
import { ElMessage, ElMessageBox } from "element-plus"
import { deleteCart } from "@/api/cart.js";

// 原有变量保留
const linkForm = ref({});
const linkman_visible = ref(false);
const orderList = ref([]);
const total = ref(0);
const activeName = ref("-1");
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  params: {}
})

// 新增编辑功能相关变量
const visible = ref(false); // 对话框显示状态
const title = ref('编辑订单'); // 对话框标题
const form = ref({}); // 表单数据
const formRef = ref(); // 表单引用

// 新增表单验证规则（和订单管理页面一致）
const rules = ref({
  orderId: [
    { required: true, message: '订单编号不能为空', trigger: 'blur' }
  ],
  productId: [
    { required: true, message: '请输入商品ID', trigger: 'blur' },
    { type: 'number', min: 1, message: '商品ID必须大于0', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  count: [
    { required: true, message: '请输入购买数量', trigger: 'blur' },
    { type: 'number', min: 1, message: '购买数量必须大于0', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入商品单价', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '商品单价必须大于0', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '请输入单位', trigger: 'blur' }
  ],
  linkUser: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  linkPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ],
  linkAddress: [
    { required: true, message: '请输入收货地址', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择订单状态', trigger: 'change' }
  ]
})

// 原有方法保留
const reset2 = () => {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    params: {}
  }
  loadOrderList();
}

const handleStatusChange = (name) => {
  queryParams.value.status = name;
  loadOrderList();
}

const removeOrderItem = (row) => {
  let id = row.id;
  ElMessageBox.confirm(
      `是否确认删除订单ID为 '${id}' 的数据？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(() => {
    deleteCart(id).then(res => {
      if (res.code === 200) {
        ElMessage.success("移除成功！")
        loadOrderList();
      } else {
        ElMessage.error(res.msg || '删除失败！')
      }
    }).catch(err => {
      ElMessage.error('删除异常，请重试！')
      console.error(err)
    })
  })
}

const loadOrderList = () => {
  listOrders(queryParams.value).then(res => {
    orderList.value = res.data.list || [];
    total.value = res.data.total || 0;
  }).catch(err => {
    ElMessage.error('查询订单失败，请重试！')
    console.error(err)
  });
}

// 新增编辑相关方法
// 打开编辑对话框
const handleEdit = (row) => {
  const id = row.id;
  // 查询订单详情
  getOrderDetail(id).then(res => {
    if (res.code === 200 && res.data) {
      form.value = { ...res.data }; // 回显订单数据
      visible.value = true; // 显示对话框
    } else {
      ElMessage.error(res.msg || '查询订单详情失败！')
    }
  }).catch(err => {
    ElMessage.error('查询订单详情异常，请重试！')
    console.error(err)
  })
}

// 商品图片上传成功回调
const imageUploadSuccess = (res) => {
  if (res.code === 200) {
    form.value.image = res.data;
    ElMessage.success('图片上传成功！')
  } else {
    ElMessage.error(res.msg || '图片上传失败！')
  }
}

// 提交编辑表单
const submitForm = () => {
  formRef.value.validate(valid => {
    if (valid) {
      // 调用更新订单接口
      updateOrder(form.value).then(res => {
        if (res.code === 200) {
          ElMessage.success('编辑订单成功！')
          visible.value = false; // 关闭对话框
          loadOrderList(); // 刷新订单列表
        } else {
          ElMessage.error(res.msg || '编辑订单失败！')
        }
      }).catch(err => {
        ElMessage.error('编辑订单异常，请重试！')
        console.error(err)
      })
    }
  })
}

// 页面加载时获取订单列表
loadOrderList();
</script>

<style scoped>
/* 原有样式保留，新增对话框相关样式 */
.container {
  padding: 20px;
}
.title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
}
.box {
  margin-bottom: 16px;
}
.dialog-footer {
  text-align: right;
}
.el-text {
  display: inline-block;
  margin-top: 8px;
}
.el-button + .el-button {
  margin-left: 8px;
}
</style>