<template>
  <div>
    <!-- 搜索区域：时间范围 + 状态筛选 + 查询/重置 -->
    <div class="box">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <!-- 申请时间范围查询 -->
        <el-form-item label="申请时间" style="width: 380px;">
          <el-date-picker
              v-model="queryParams.timeRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              clearable
          />
        </el-form-item>

        <!-- 审核状态筛选 -->
        <el-form-item label="审核状态">
          <el-select
              v-model="queryParams.auditStatus"
              placeholder="请选择审核状态"
              clearable
              style="width: 180px"
          >
            <el-option label="待审核" value="0" />
            <el-option label="审核通过" value="1" />
            <el-option label="审核驳回" value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="申请人ID">
          <el-input v-model="queryParams.userId" placeholder="请输入申请人ID" clearable style="width: 180px" />
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" icon="Search" @click="search">查询</el-button>
          <el-button type="warning" icon="Promotion" @click="reset01">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 批量操作按钮 -->
    <div class="box" style="display: flex; margin-bottom: 16px;">
      <el-button type="info" icon="Remove" @click="handleBatchDelete" >批量删除</el-button>
    </div>

    <!-- 申请列表表格 -->
    <div class="box">
      <el-table :data="data.tableData" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="申请编号" width="120" />
        <el-table-column prop="userId" label="申请人ID" width="120" />
        <el-table-column prop="applyTime" label="申请时间" width="200" />
        <el-table-column prop="auditStatus" label="审核状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.auditStatus)">
              {{ getStatusText(scope.row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template #default="scope">
            <!-- 仅待审核状态显示审核按钮 -->
            <el-button
                size="small"
                type="success"
                icon="Check"
                @click="handleAuditPass(scope.row)"
                v-if="scope.row.auditStatus === 0"
            >
              审核通过
            </el-button>
            <el-button
                size="small"
                type="danger"
                icon="Close"
                @click="handleAuditReject(scope.row)"
                v-if="scope.row.auditStatus === 0"
                style="margin-left: 8px;"
            >
              审核驳回
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页组件 -->
    <div class="box" style="margin-top: 16px; text-align: right;">
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

    <!-- 审核驳回原因弹框（可选） -->
    <el-dialog v-model="rejectDialogVisible" title="审核驳回" width="400px">
      <el-form :model="rejectForm" ref="rejectFormRef" :label-width="70">
        <el-form-item label="驳回原因" prop="reason" required>
          <el-input
              v-model="rejectForm.reason"
              type="textarea"
              :rows="3"
              placeholder="请输入驳回原因（必填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmReject">确认驳回</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { ElMessage, ElMessageBox, ElTag } from 'element-plus';
import { download } from '@/utils/request.js';
import { listApply, deleteApply, auditApply, exportApply } from '@/api/apply.js';
// 替换管理员查询接口为通用用户查询接口
import { getUserById } from '@/api/sysuser.js';

// 批量选择的申请ID
const ids = ref([]);
// 驳回弹框相关
const rejectDialogVisible = ref(false);
const rejectFormRef = ref(null);
const rejectForm = ref({ reason: '', currentApply: null });

// 查询参数（时间范围、状态、分页等）
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  auditStatus: '', // 审核状态：0-待审核，1-通过，2-驳回
  timeRange: [], // 申请时间范围：[开始日期, 结束日期]
  userId: '' // 申请人ID（可选查询条件）
});

// 表格数据
const data = reactive({
  total: 0,
  tableData: [], // 申请列表数据
  auditorMap: new Map() // 审核人ID->用户名映射（缓存）
});

// 审核状态文本映射
const getStatusText = (status) => {
  const statusMap = { 0: '待审核', 1: '审核通过', 2: '审核驳回' };
  return statusMap[status] || '未知状态';
};

// 审核状态标签样式映射
const getStatusTagType = (status) => {
  const typeMap = { 0: 'warning', 1: 'success', 2: 'danger' };
  return typeMap[status] || 'default';
};

// 格式化审核人ID（显示用户名，通用用户查询，不区分管理员）
const formatAuditorId = async (auditorId) => {
  // 严格校验 auditorId：必须是数字（非空、非 NaN、非字符串/对象）
  if (!auditorId || isNaN(Number(auditorId)) || typeof auditorId === 'object') {
    return '未审核';
  }
  const userId = Number(auditorId);
  // 缓存查询结果
  if (data.auditorMap.has(userId)) {
    return data.auditorMap.get(userId);
  }
  try {
    // 调用通用用户查询接口（替换原管理员接口）
    const res = await getUserById(userId);
    if (res.code === 200 && res.data) {
      const username = res.data.username || `用户(${userId})`;
      data.auditorMap.set(userId, username);
      return username;
    } else {
      return `用户(${userId})`;
    }
  } catch (err) {
    console.error(`查询用户 ${userId} 失败：`, err);
    return `用户(${userId})`;
  }
};

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val;
  getApplyList();
};

// 当前页改变
const handleCurrentChange = () => {
  getApplyList();
};

// 条件查询
const search = () => {
  queryParams.pageNum = 1;
  getApplyList();
};

// 重置查询条件
const reset01 = () => {
  queryParams.auditStatus = '';
  queryParams.timeRange = [];
  queryParams.userId = '';
  queryParams.pageNum = 1;
  getApplyList();
};

// 批量选择事件
const handleSelectionChange = (selections) => {
  ids.value = selections.map(item => item.id);
};

// 获取申请列表
const getApplyList = () => {
  // 构造接口请求参数（处理时间范围）
  const params = {
    ...queryParams,
    startTime: queryParams.timeRange[0] || '',
    endTime: queryParams.timeRange[1] || ''
  };
  // 删除多余的 timeRange 字段（后端不接收）
  delete params.timeRange;

  listApply(params).then(res => {
    if (res.code === 200) {
      data.tableData = res.data.records;
      data.total = res.data.total;
    } else {
      ElMessage.error('获取申请列表失败');
    }
  }).catch(err => {
    ElMessage.error('网络错误，获取申请列表失败');
    console.error(err);
  });
};

// 单个删除（补充原有缺失的单个删除逻辑）
const handleDelete = (row) => {
  const id = row.id;
  ElMessageBox.confirm(
      `是否确认删除申请编号为 ${id} 的记录？`,
      '提示',
      { type: 'warning' }
  ).then(() => {
    deleteApply(id).then(res => {
      if (res.code === 200) {
        ElMessage.success('删除成功！');
        getApplyList();
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    }).catch(err => {
      ElMessage.error('删除失败，请重试');
      console.error(err);
    });
  });
};

// 批量删除
const handleBatchDelete = () => {
  if (ids.value.length === 0) {
    ElMessage.error('请选择要删除的申请记录');
    return;
  }
  ElMessageBox.confirm(
      `是否确认删除选中的 ${ids.value.length} 条申请记录？`,
      '提示',
      { type: 'warning' }
  ).then(() => {
    // 批量删除时，将数组转为逗号分隔的字符串（适配后端接口）
    const idStr = ids.value.join(',');
    deleteApply(idStr).then(res => {
      if (res.code === 200) {
        ElMessage.success('批量删除成功！');
        ids.value = []; // 清空选择
        getApplyList();
      } else {
        ElMessage.error(res.msg || '批量删除失败');
      }
    }).catch(err => {
      ElMessage.error('批量删除失败，请重试');
      console.error(err);
    });
  });
};

// 审核通过
const handleAuditPass = (row) => {
  ElMessageBox.confirm(
      `是否确认通过申请编号为 ${row.id} 的申请？`,
      '审核确认',
      { type: 'success' }
  ).then(() => {
    // 获取当前登录用户ID（不区分管理员，仅校验登录状态）
    const userInfo = JSON.parse(localStorage.getItem('user')) || {};
    const auditorId = userInfo.userId;

    if (!auditorId) {
      ElMessage.error('请先登录账号'); // 去掉"管理员"字样
      return;
    }

    // 调用审核接口（审核状态1=通过）
    auditApply({
      id: row.id,
      auditorId: auditorId,
      auditStatus: 1
    }).then(res => {
      if (res.code === 200) {
        ElMessage.success('审核通过成功！');
        getApplyList();
      } else {
        ElMessage.error(res.msg || '审核失败');
      }
    }).catch(err => {
      ElMessage.error('审核操作失败，请重试');
      console.error(err);
    });
  });
};

// 审核驳回（打开弹框输入原因）
const handleAuditReject = (row) => {
  rejectForm.value = {
    reason: '',
    currentApply: row // 缓存当前申请记录
  };
  rejectDialogVisible.value = true;
};

// 确认驳回
const confirmReject = () => {
  rejectFormRef.value.validate(valid => {
    if (valid) {
      const { currentApply } = rejectForm.value;
      // 获取当前登录用户ID（不区分管理员）
      const userInfo = JSON.parse(localStorage.getItem('user')) || {};
      const auditorId = userInfo.userId;

      if (!auditorId) {
        ElMessage.error('请先登录账号'); // 去掉"管理员"字样
        return;
      }

      // 调用审核接口（审核状态2=驳回，携带驳回原因）
      auditApply({
        id: currentApply.id,
        auditorId: auditorId,
        auditStatus: 2,
        auditRemark: rejectForm.value.reason // 驳回原因存入审核备注
      }).then(res => {
        if (res.code === 200) {
          ElMessage.success('审核驳回成功！');
          rejectDialogVisible.value = false;
          getApplyList();
        } else {
          ElMessage.error(res.msg || '驳回失败');
        }
      }).catch(err => {
        ElMessage.error('驳回操作失败，请重试');
        console.error(err);
      });
    }
  });
};

// 批量导出（补充原有缺失的导出按钮绑定，若需显示可在页面添加按钮）
const handleExport = () => {
  // 构造导出参数（与查询参数一致）
  const exportParams = {
    ...queryParams,
    startTime: queryParams.timeRange[0] || '',
    endTime: queryParams.timeRange[1] || ''
  };
  delete exportParams.timeRange;

  download('/apply/export', exportParams, '申请记录导出.xlsx');
};

// 初始化加载申请列表
getApplyList();
</script>

<style scoped>
.box {
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
}

/* 搜索区域样式优化 */
.demo-form-inline {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  padding: 16px 0;
}

/* 标签样式优化 */
.el-tag {
  font-size: 12px;
  padding: 4px 8px;
}

/* 弹框样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .demo-form-inline {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>