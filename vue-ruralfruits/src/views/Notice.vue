<template>
  <div>
    <div class="box">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="通知标题" >
          <el-input v-model="queryParams.title" placeholder="请输入通知标题" clearable />
        </el-form-item>
        <el-form-item label="发布时间" >
          <el-date-picker
              v-model="queryParams.timeRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 300px;"
          />
        </el-form-item>
        <el-form-item label="启用状态">
          <el-select
              v-model="queryParams.open"
              placeholder="请选择状态"
              clearable
              style="width: 150px"
          >
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="search">查询</el-button>
          <el-button type="warning" icon="Promotion" @click="reset01">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="box" style="display: flex;">
      <el-button type="primary" icon="Plus" @click="handleAdd">添加通知</el-button>
      <el-button type="success" icon="EditPen" @click="handleEdit">修改</el-button>
      <el-button type="danger" icon="Remove" @click="handleDelete">批量删除</el-button>
      <el-button type="warning" icon="Download" @click="handleExport">批量导出</el-button>
    </div>
    <div class="box">
      <el-table :data="data.tableData" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection"></el-table-column>
        <el-table-column prop="id" label="通知ID" width="100" align="center" />
        <el-table-column prop="title" label="通知标题" :show-overflow-tooltip="true" />
        <el-table-column prop="time" label="发布时间" width="200" align="center" />
        <el-table-column prop="open" label="启用状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.open === 1 ? 'success' : 'danger'">
              {{ scope.row.open === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" >
          <template #default="scope">
            <el-button
                size="small"
                icon="EditPen"
                @click="handleEdit(scope.row)"
            >编辑</el-button>
            <el-button
                size="small"
                type="danger"
                icon="Remove"
                @click="handleDelete(scope.row)"
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
          :total="data.total"
          v-model:page-size="queryParams.pageSize"
          v-model:current-page="queryParams.pageNum"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
      />
    </div>

    <!-- 新增/编辑通知弹窗（已删除时间选择器，自动取当前时间） -->
    <el-dialog v-model="visible" :title="title" width="800px">
      <el-form :model="form" :rules="rules" ref="form1" label-width="85px">
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入通知标题" autocomplete="off" />
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input
              v-model="form.content"
              type="textarea"
              :rows="8"
              placeholder="请输入通知内容"
              autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="启用状态">
          <el-radio-group v-model="form.open">
            <el-radio value="1">启用（前台显示）</el-radio>
            <el-radio value="0">禁用（前台隐藏）</el-radio>
          </el-radio-group>
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
import { listNotice, deleteNotice, addNotice, getNotice, updateNotice } from '@/api/notice.js'

const ids = ref([]) // 选中的通知ID集合
const visible = ref(false) // 弹窗显示状态
const title = ref('添加通知') // 弹窗标题
const form = ref({}) // 表单数据
const form1 = ref() // 表单引用（用于校验）

// 表单验证规则（已移除time的必填校验）
const rules = reactive({
  title: [
    { required: true, message: '请输入通知标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入通知内容', trigger: 'blur' }
  ]
})

// 查询参数（适配通知的筛选字段）
const queryParams = reactive({
  pageNum: 1,
  pageSize: 5,
  title: '', // 通知标题筛选
  timeRange: [], // 发布时间区间筛选
  open: '' // 启用状态筛选
})

// 表格数据
const data = reactive({
  total: 0,
  tableData: [] // 通知列表数据
})

// 每页条数改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  getNoticeList()
}

// 当前页码改变
const handleCurrentChange = () => {
  getNoticeList()
}

// 条件查询
const search = () => {
  queryParams.pageNum = 1
  // 处理时间区间筛选（转成startTime和endTime传给后端）
  if (queryParams.timeRange.length === 2) {
    queryParams.startTime = queryParams.timeRange[0]
    queryParams.endTime = queryParams.timeRange[1]
  } else {
    queryParams.startTime = ''
    queryParams.endTime = ''
  }
  getNoticeList()
}

// 重置筛选条件
const reset01 = () => {
  queryParams.title = ''
  queryParams.timeRange = []
  queryParams.open = ''
  queryParams.startTime = ''
  queryParams.endTime = ''
  queryParams.pageNum = 1
  getNoticeList()
}

// 删除通知（支持单个/批量）
const handleDelete = (row) => {
  const id = row?.id || ids.value // 单个删除取行ID，批量删除取选中IDs
  if (ids.value.length === 0 && !row?.id) {
    ElMessage.error("请选择要删除的通知")
    return
  }
  ElMessageBox.confirm(
      `是否确认删除${row?.id ? '该' : '选中的'}通知？`,
      "提示",
      { confirmButtonText: "确定", cancelButtonText: "取消", type: "warning" }
  ).then(() => {
    deleteNotice(row?.id ? [row.id] : ids.value).then(res => {
      ElMessage.success('删除成功!')
      getNoticeList()
      ids.value = [] // 清空选中状态
    })
  })
}

// 表格选中事件（批量操作）
const handleSelectionChange = (selections) => {
  ids.value = []
  selections.map(item => ids.value.push(item.id))
}

// 格式化当前时间为 "YYYY-MM-DD HH:mm:ss" 格式
const formatCurrentTime = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  const second = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

// 新增通知（自动填充当前时间）
const handleAdd = () => {
  visible.value = true
  title.value = "添加通知"
  resetForm() // 清空表单并填充当前时间
}

// 清空表单（新增时自动填充当前时间，编辑时保留原时间）
const resetForm = () => {
  form.value = {
    title: "",
    content: "",
    time: formatCurrentTime(), // 自动填充当前时间
    open: 1 // 默认启用
  }
  // 重置表单校验状态
  if (form1.value) form1.value.clearValidate()
}

// 编辑通知（保留原发布时间，不修改）
const handleEdit = (row) => {
  let targetId
  // 行内编辑（有row数据）
  if (row && row.id) {
    targetId = row.id
  } else {
    // 批量选择编辑：必须选中1条
    if (ids.value.length === 0) {
      ElMessage.error("请选择要修改的通知")
      return
    }
    if (ids.value.length > 1) {
      ElMessage.error("最多只能选择1条通知进行编辑")
      return
    }
    targetId = ids.value[0]
  }

  // 调用接口查询通知详情（保留原时间）
  getNotice(targetId).then(res => {
    if (!res.data) {
      ElMessage.error("该通知不存在或已被删除")
      return
    }
    form.value = { ...res.data } // 回显原数据（包括原发布时间）
    title.value = "修改通知"
    visible.value = true
  }).catch(err => {
    ElMessage.error("查询通知失败，请稍后重试")
    console.error(err)
  })
}

// 导出通知数据
const handleExport = () => {
  download("/notice/export", queryParams)
}

// 提交表单（新增/编辑）
const submitForm = () => {
  form1.value.validate(valid => {
    if (valid) {
      // 新增时确保时间是当前时间（防止手动修改禁用的输入框）
      if (!form.value.id) {
        form.value.time = formatCurrentTime()
      }
      // 根据是否有id判断是新增还是编辑
      if (!form.value.id) {
        // 新增
        addNotice(form.value).then(res => {
          ElMessage.success("添加通知成功")
          visible.value = false
          getNoticeList()
        })
      } else {
        // 编辑（不修改发布时间）
        updateNotice(form.value).then(res => {
          ElMessage.success("修改通知成功")
          visible.value = false
          getNoticeList()
        })
      }
    }
  })
}

// 获取通知列表
const getNoticeList = () => {
  listNotice(queryParams).then(res => {
    if (res.code === 200) {
      data.tableData = res.data.records || res.data.list
      data.total = res.data.total
    } else {
      ElMessage.error('查询通知失败')
    }
  }).catch(err => {
    ElMessage.error('接口调用异常')
    console.error(err)
  })
}

// 页面加载时获取通知列表
getNoticeList()
</script>

<style scoped>
.box {
  margin-bottom: 16px;
}
.el-form-item {
  margin-right: 20px;
}
.dialog-footer {
  text-align: right;
}
</style>