<template>
  <div>
    <div class="box">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="订单编号" >
          <el-input v-model="queryParams.params.orderId" placeholder="请输入订单编号" clearable />
        </el-form-item>
        <el-form-item label="商品名称" >
          <el-input v-model="queryParams.params.name" placeholder="请输入商品名称" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select
              v-model="queryParams.status"
              placeholder="请选择订单状态"
              clearable
              style="width: 200px">
            <el-option label="全部" value="-1" />
            <el-option label="待付款" value="待付款" />
            <el-option label="已付款" value="已付款" />
            <el-option label="已发货" value="已发货" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
              v-model="queryParams.params.begin"
              type="date"
              placeholder="开始日期"
              clearable
              style="width: 180px"
          />
          <span style="margin: 0 8px">至</span>
          <el-date-picker
              v-model="queryParams.params.end"
              type="date"
              placeholder="结束日期"
              clearable
              style="width: 180px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="search">查询</el-button>
          <el-button type="warning" icon="Promotion" @click="reset01">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="box" style="display: flex;">
      <el-button type="primary" icon="Plus" @click="handleAdd">添加订单</el-button>
      <el-button type="success" icon="EditPen" @click="handleEdit">修改订单</el-button>
      <el-button type="info" icon="Remove" @click="handleDeleteBatch">批量删除</el-button>
      <el-button type="warning" icon="Download" @click="handleExport">批量导出</el-button>

      <el-upload
          class="upload-demo"
          style="margin-left: 10px;"
          :show-file-list="false"
          :on-success="handleImportSuccess"
          :action="uploadUrl"
      >
        <el-button type="danger" icon="Upload">批量导入</el-button>
      </el-upload>
    </div>

    <div class="box">
      <el-table :data="orderList" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection"></el-table-column>
        <el-table-column prop="id" label="订单ID" width="100" />
        <el-table-column prop="orderId" label="订单编号" width="180" />
        <el-table-column prop="linkUser" label="联系人" width="120" />
        <el-table-column prop="linkPhone" label="联系电话" width="150" />
        <el-table-column prop="createTime" label="创建时间" width="200">
          <!-- 格式化时间显示 -->
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button
                size="small"
                icon="EditPen" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
                size="small"
                type="danger"
                icon="Remove"
                @click="handleDeleteSingle(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="box">
      <el-pagination
          background
          layout="sizes, prev, pager, next, total"
          :page-sizes="[5, 10, 20, 50]"
          :total="total"
          v-model:page-size="queryParams.pageSize"
          v-model:current-page="queryParams.pageNum"
          @current-change="loadOrderList"
          @size-change="handleSizeChange"
      />
    </div>

    <!-- 订单表单对话框 -->
    <el-dialog v-model="visible" :title="title" width="800">
      <el-form :model="form" :rules="rules" ref="formRef" :label-width="85">
        <el-form-item label="订单编号" prop="orderId">
          <el-input v-model="form.orderId" autocomplete="off" :disabled="form.id ? true : false" />
        </el-form-item>
        <el-form-item label="商品ID" prop="productId"  >
          <el-input v-model="form.productId" type="number" autocomplete="off" :disabled="form.id ? true : false"/>
        </el-form-item>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" autocomplete="off"  :disabled="form.id ? true : false" />
        </el-form-item>
        <el-form-item label="购买数量" prop="count">
          <el-input v-model="form.count" type="number" min="1" autocomplete="off" />
        </el-form-item>
        <el-form-item label="商品单价" prop="price">
          <el-input v-model="form.price" type="number" min="0.01" step="0.01" autocomplete="off" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="如：个、斤、箱" autocomplete="off" />
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
            <el-option label="待付款" value="0" />
            <el-option label="已付款" value="1" />
            <el-option label="已发货" value="2" />
            <el-option label="已完成" value="3" />
            <el-option label="已取消" value="4" />
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
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { download } from '@/utils/request.js'
import { ElMessage, ElMessageBox } from "element-plus"
// 导入订单相关接口
import {
  listAllOrders,
  addSingleOrder,
  updateOrder,
  deleteOrder,
  deleteBatchOrders,
  getOrderDetail
} from '@/api/order.js'

const ids = ref([]) // 选中数据的订单ID
const visible = ref(false)
const title = ref('添加订单') // 对话框标题
const form = ref({}) // 表单数据
const formRef = ref() // 表单引用
const uploadUrl = ref(import.meta.env.VITE_API_BASE_URL + '/order/import') // 导入文件地址

// 表单验证规则
const rules = reactive({
  orderId: [
    { required: true, message: '请输入订单编号', trigger: 'blur' }
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

// 查询参数：完全对齐 order.vue 的格式（嵌套 params 对象）
const queryParams = reactive({
  pageNum: 1,
  pageSize: 5,
  status: '-1', // 默认查询全部
  params: { // 嵌套参数，和 order.vue 一致
    orderId: '',
    name: '',
    begin: '',
    end: ''
  }
})

// 表格数据：对齐 order.vue 的变量名
const orderList = ref([])
const total = ref(0)

// 格式化时间（ISO格式转本地时间）
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 每页条数改变：对齐 order.vue 的逻辑
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  loadOrderList()
}

// 条件查询：对齐 order.vue 的逻辑
const search = () => {
  queryParams.pageNum = 1
  loadOrderList()
}

// 重置查询条件：对齐 order.vue 的格式
const reset01 = () => {
  queryParams.pageNum = 1
  queryParams.status = '-1'
  queryParams.params = {
    orderId: '',
    name: '',
    begin: '',
    end: ''
  }
  loadOrderList()
}

// 单条删除订单
const handleDeleteSingle = (row) => {
  const id = row.id
  ElMessageBox.confirm(
      `是否确认删除订单ID为 '${id}' 的数据？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(() => {
    deleteOrder(id).then(res => {
      if (res.code === 200) {
        ElMessage.success('删除成功！')
        loadOrderList()
      } else {
        ElMessage.error(res.msg || '删除失败！')
      }
    }).catch(err => {
      ElMessage.error('删除异常，请重试！')
      console.error(err)
    })
  })
}

// 批量删除订单
const handleDeleteBatch = () => {
  if (ids.value.length === 0) {
    ElMessage.error('请选择要删除的订单！')
    return
  }
  ElMessageBox.confirm(
      `是否确认删除选中的 ${ids.value.length} 条订单数据？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(() => {
    deleteBatchOrders(ids.value).then(res => {
      if (res.code === 200) {
        ElMessage.success('批量删除成功！')
        ids.value = [] // 清空选中状态
        loadOrderList()
      } else {
        ElMessage.error(res.msg || '批量删除失败！')
      }
    }).catch(err => {
      ElMessage.error('批量删除异常，请重试！')
      console.error(err)
    })
  })
}

// 选中数据变化（批量操作）
const handleSelectionChange = (selections) => {
  ids.value = []
  selections.map(item => ids.value.push(item.id))
}

// 打开添加订单对话框
const handleAdd = () => {
  visible.value = true
  title.value = '添加订单'
  resetForm()
}

// 清空表单
const resetForm = () => {
  form.value = {
    orderId: '',
    productId: '',
    name: '',
    image: '',
    count: 1,
    price: 0.01,
    unit: '',
    linkUser: '',
    linkPhone: '',
    linkAddress: '',
    status: '待付款'
  }
  // 重置表单校验状态
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 打开编辑订单对话框
const handleEdit = (row) => {
  let targetId
  // 行内编辑
  if (row && row.id) {
    targetId = row.id
  }
  // 顶部批量选择后编辑
  else {
    if (ids.value.length === 0) {
      ElMessage.error('请选择要修改的订单！')
      return
    }
    if (ids.value.length > 1) {
      ElMessage.error('最多只能选择1条订单进行编辑！')
      return
    }
    targetId = ids.value[0]
  }

  // 查询订单详情并回显
  getOrderDetail(targetId).then(res => {
    if (res.code === 200 && res.data) {
      form.value = { ...res.data }
      title.value = '修改订单'
      visible.value = true
    } else {
      ElMessage.error(res.msg || '该订单不存在或已被删除！')
    }
  }).catch(err => {
    ElMessage.error('查询订单详情失败，请重试！')
    console.error(err)
  })
}

// 导出订单数据
const handleExport = () => {
  // 格式化导出的日期参数（对齐嵌套 params 格式）
  const exportParams = {
    pageNum: queryParams.pageNum,
    pageSize: queryParams.pageSize,
    status: queryParams.status,
    params: {
      ...queryParams.params,
      begin: queryParams.params.begin ? new Date(queryParams.params.begin).toISOString().split('T')[0] : '',
      end: queryParams.params.end ? new Date(queryParams.params.end).toISOString().split('T')[0] : ''
    }
  }
  download("/order/export", exportParams)
}

// 批量导入订单成功回调
const handleImportSuccess = (res) => {
  if (res.code === 200) {
    ElMessage.success(`导入成功，共导入 ${res.data} 条数据！`)
    loadOrderList()
  } else {
    ElMessage.error(res.msg || '导入失败！')
  }
}

// 提交表单（添加/修改订单）
const submitForm = () => {
  formRef.value.validate(valid => {
    if (valid) {
      // 添加订单（无id）
      if (!form.value.id) {
        addSingleOrder(form.value).then(res => {
          if (res.code === 200) {
            ElMessage.success('添加订单成功！')
            visible.value = false
            loadOrderList()
          } else {
            ElMessage.error(res.msg || '添加订单失败！')
          }
        }).catch(err => {
          ElMessage.error('添加订单异常，请重试！')
          console.error(err)
        })
      }
      // 修改订单（有id）
      else {
        updateOrder(form.value).then(res => {
          if (res.code === 200) {
            ElMessage.success('修改订单成功！')
            visible.value = false
            loadOrderList()
          } else {
            ElMessage.error(res.msg || '修改订单失败！')
          }
        }).catch(err => {
          ElMessage.error('修改订单异常，请重试！')
          console.error(err)
        })
      }
    }
  })
}

// 商品图片上传成功回调
const imageUploadSuccess = (res) => {
  if (res.code === 200) {
    form.value.image = res.data
  } else {
    ElMessage.error(res.msg || '图片上传失败！')
  }
}

// 获取订单列表：完全对齐 order.vue 的逻辑（参数格式、接口调用）
const loadOrderList = () => {
  // 直接传递和 order.vue 完全一致的参数格式
  listAllOrders(queryParams).then(res => {
    console.log('订单列表接口响应：', res)
    if (res.code === 200) {
      orderList.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '查询订单列表失败！')
      orderList.value = []
      total.value = 0
    }
  }).catch(err => {
    ElMessage.error('接口调用异常，请重试！')
    console.error('订单列表查询异常：', err)
    orderList.value = []
    total.value = 0
  })
}

// 页面加载时获取订单列表
loadOrderList()
</script>

<style scoped>
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
</style>