<template>
  <Header />

  <div class="profile-container">
    <h2 class="profile-title">个人中心</h2>

    <div class="profile-content">
      <div class="user-info-card">
        <div class="avatar-container">
          <el-image
              :src="user?.avatar || '@/assets/images/default-avatar.png'"
              fit="cover"
              style="width: 120px; height: 120px; border-radius: 50%;"
          />
        </div>

        <div class="user-info">
          <div class="info-item">
            <span class="label">用户名：</span>
            <span class="value">{{ user?.username || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">昵称：</span>
            <span class="value">{{ user?.nickname || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">手机号：</span>
            <span class="value">{{ user?.phone || '未绑定' }}</span>
          </div>
          <div class="info-item">
            <span class="label">邮箱：</span>
            <span class="value">{{ user?.email || '未绑定' }}</span>
          </div>
          <div class="info-item">
            <span class="label">用户类型：</span>
            <span class="value">{{ formatUserType(user?.type) }}</span>
          </div>
        </div>

        <el-button
            type="primary"
            icon="EditPen"
            class="edit-btn"
            @click="handleEditProfile"
        >
          修改个人信息
        </el-button>
      </div>

      <!-- 申请成为商家按钮（在卡片下方） -->
      <div class="apply-merchant-btn-container">
        <el-button
            type="success"
            icon="Shop"
            @click="showApplyDialog = true"
            :disabled="user?.type === 'merchant'"
        >
          申请成为商家
        </el-button>
      </div>
    </div>
  </div>

  <!-- 修改个人信息弹框 -->
  <el-dialog v-model="isDialogVisible" :title="dialogTitle" width="800">
    <el-form :model="form" :rules="rules" ref="formRef" :label-width="85">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" autocomplete="off" disabled />
      </el-form-item>
      <!-- 密码字段修复：把 el-form-item__error 改成 div 标签 + 样式类 -->
      <el-form-item label="密码" prop="password">
        <el-input
            v-model="form.password"
            autocomplete="off"
            type="password"
            placeholder="不修改请留空，修改请输入新明文密码"
        />
        <!-- 修复：用 div 标签承载说明文字，添加样式类 -->
        <div class="el-form-item__error custom-hint">
          密码长度至少6位，留空则保持原密码不变
        </div>
      </el-form-item>
      <el-form-item label="头像">
        <el-row style="width: 100%" :gutter="40">
          <el-col :span="20">
            <el-input v-model="form.avatar" readonly disabled autocomplete="off" />
          </el-col>
          <el-col :span="4">
            <el-upload
                action="http://localhost:8080/file/upload"
                :show-file-list="false"
                :on-success="avaterUploadSuccess"
                :headers="uploadHeaders"
            >
              <el-button type="primary">上传头像</el-button>
            </el-upload>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" autocomplete="off" />
      </el-form-item>
      <el-form-item label="性别" prop="sex">
        <el-radio-group v-model="form.sex">
          <el-radio value="0">未知</el-radio>
          <el-radio value="1">男</el-radio>
          <el-radio value="2">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="phone" prop="phone">
        <el-input v-model="form.phone" autocomplete="off" />
      </el-form-item>
      <el-form-item label="email">
        <el-input v-model="form.email" autocomplete="off" />
      </el-form-item>
      <el-form-item label="用户类型">
        <el-select v-model="form.type" placeholder="" disabled>
          <el-option label="管理员" value="admin" />
          <el-option label="普通用户" value="user" />
          <el-option label="商家" value="merchant" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="isDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 申请成为商家提示框 -->
  <el-dialog
      v-model="showApplyDialog"
      title="申请成为商家"
      width="500px"
      :closable="true"
      :append-to-body="true"
  >
    <div class="apply-dialog-content">
      <div class="apply-icon">
        <el-icon class="icon"><InfoFilled /></el-icon>
      </div>
      <div class="apply-message">
        <h4>申请须知</h4>
        <p>1. 成为商家后可发布商品、管理订单等操作</p>
        <p>2. 请准备好相关资质证明（如营业执照等）</p>
        <p>3. 申请提交后，管理员将在1-3个工作日内审核</p>
        <p>4. 审核通过后，您的用户类型将自动变更为「商家」</p>
      </div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="showApplyDialog = false">取消</el-button>
        <el-button type="primary" @click="submitApply">确认申请</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import Header from "./Header.vue";
import { getUser, setUser, removeUser } from "@/utils/user.js";
import { ElMessage } from 'element-plus';
import { updateuser, getuser } from '@/api/sysuser.js';
import { useRouter } from "vue-router";
// 导入所需图标（仅保留存在的）
import { EditPen, Shop, InfoFilled } from '@element-plus/icons-vue';
// 新增：导入申请商家接口（需要先创建 api/apply.js）
import { submitMerchantApply } from '@/api/apply.js';

const router = useRouter();
const user = ref(null);
const isDialogVisible = ref(false);
const dialogTitle = ref('修改个人信息');
const form = ref({});
const formRef = ref(null);
const showApplyDialog = ref(false);

// 密码规则：非必填，仅填写时校验长度
const rules = ref({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ min: 6, message: '密码必须6位以上', trigger: 'blur', required: false }], // 非必填
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
});

const uploadHeaders = computed(() => {
  return {
    'Authorization': user.value?.token || ''
  };
});

onMounted(() => {
  user.value = getUser();
  if (!user.value) {
    ElMessage.warning("请先登录");
    router.push('/login');
  }
});

const formatUserType = (type) => {
  const typeMap = {
    admin: '管理员',
    user: '普通用户',
    merchant: '商家'
  };
  return typeMap[type] || '未知用户';
};

// 点击修改按钮，查询用户信息回显
const handleEditProfile = () => {
  if (!user.value?.userId || !user.value?.token) return;

  getuser(user.value.userId, {
    headers: { 'Authorization': user.value.token }
  }).then(res => {
    if (res?.code === 200 && res.data) {
      // 密码字段置空，不回显加密乱码
      form.value = {
        ...res.data,
        password: ""
      };
      isDialogVisible.value = true;
    } else {
      ElMessage.error("获取个人信息失败");
    }
  }).catch(err => {
    ElMessage.error("Token无效，请重新登录");
    removeUser();
    user.value = null;
    router.push('/login');
  });
};

// 头像上传成功回调
const avaterUploadSuccess = (res) => {
  if (res.code === 200) {
    form.value.avatar = res.data;
    ElMessage.success("头像上传成功");
  } else {
    ElMessage.error("头像上传失败");
  }
};

// 提交修改表单
const submitForm = () => {
  formRef.value.validate(valid => {
    if (valid) {
      const submitData = { ...form.value };
      // 密码留空则删除字段，不更新密码
      if (!submitData.password) {
        delete submitData.password;
      }

      updateuser(submitData, {
        headers: { 'Authorization': user.value.token }
      }).then(res => {
        if (res.code === 200) {
          ElMessage.success("修改成功");
          // 更新本地存储，密码保持原加密值
          const updatedUser = {
            ...submitData,
            token: user.value.token,
            password: user.value.password
          };
          setUser(updatedUser);
          user.value = updatedUser;
          isDialogVisible.value = false;
        } else {
          ElMessage.error("修改失败：" + (res.msg || '未知错误'));
        }
      }).catch(() => {
        ElMessage.error("修改失败，请重试");
      });
    }
  });
};

// 提交商家申请（修改：调用后端接口，不再只是弹提示）
const submitApply = () => {
  // 校验用户是否登录（防止未登录操作）
  if (!user.value || !user.value.userId) {
    ElMessage.warning("请先登录");
    showApplyDialog.value = false;
    router.push('/login');
    return;
  }

  // 调用后端申请接口，传递当前用户ID
  submitMerchantApply(user.value.userId)
      .then(res => {
        if (res.code === 200) {
          ElMessage.success(res.msg || "申请提交成功！请等待管理员审核");
          showApplyDialog.value = false; // 关闭弹框
        } else {
          ElMessage.error(res.msg || "申请提交失败");
        }
      })
      .catch(err => {
        ElMessage.error("网络错误，申请失败，请重试");
        console.error("申请失败原因：", err);
      });
};
</script>

<style scoped>
.profile-container {
  width: 100%;
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
  box-sizing: border-box;
}

.profile-title {
  font-size: 24px;
  font-weight: 500;
  color: #333;
  margin-bottom: 30px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.profile-content {
  background-color: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.user-info-card {
  display: flex;
  align-items: center;
  gap: 40px;
  position: relative;
  padding-bottom: 40px;
  margin-bottom: 20px; /* 给下方按钮留间距 */
}

.avatar-container {
  border: 2px solid #f5f5f5;
  border-radius: 50%;
  overflow: hidden;
}

.user-info {
  flex: 1;
}

.info-item {
  margin-bottom: 16px;
  font-size: 16px;
}

.info-item .label {
  color: #666;
  margin-right: 10px;
  display: inline-block;
  width: 80px;
}

.info-item .value {
  color: #333;
  font-weight: 400;
}

.edit-btn {
  position: absolute;
  right: 0;
  bottom: 0;
}

/* 申请成为商家按钮样式 */
.apply-merchant-btn-container {
  display: flex;
  justify-content: flex-start; /* 靠左显示，可改为 center 居中 */
  margin-top: 10px;
}

/* 申请提示框内容样式 */
.apply-dialog-content {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 10px 0;
}

.apply-icon .icon {
  color: #409EFF;
  font-size: 24px;
  margin-top: 4px;
}

.apply-message h4 {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 16px;
}

.apply-message p {
  margin: 6px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

/* 修复：密码说明文字样式（避免和 Element Plus 错误提示冲突） */
.custom-hint {
  color: #999 !important; /* 灰色提示色，不是红色错误色 */
  font-size: 12px !important;
  margin-top: 4px !important;
  height: auto !important; /* 取消固定高度，适应文字 */
  line-height: 1.4 !important;
}

/* 密码输入框提示样式优化 */
.el-input__placeholder {
  color: #999 !important;
  font-size: 12px !important;
}

@media (max-width: 768px) {
  .user-info-card {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .info-item .label {
    display: block;
    margin: 0 auto 5px;
  }

  .edit-btn {
    position: relative;
    margin-top: 20px;
    right: auto;
    bottom: auto;
  }

  /* 移动端申请按钮居中 */
  .apply-merchant-btn-container {
    justify-content: center;
  }
}
</style>