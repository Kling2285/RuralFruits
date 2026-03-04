<template>
  <div>
    <div class="box">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="农产品名称" >
          <el-input v-model="queryParams.name" placeholder="请输入农产品名称" clearable />
        </el-form-item>
        <el-form-item label="产品类别">
          <el-select
              v-model="queryParams.category"
              placeholder="请选择产品类别"
              clearable
              style="width: 200px">
            <el-option v-for="item in categoriesList" :key="item.id" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="search">查询</el-button>
          <el-button type="warning" icon="Promotion" @click="reset01">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="box" style="display: flex;">
      <el-button type="primary" icon="Plus" @click="handleAdd">添加</el-button>
      <el-button type="warning" icon="Download" @click="handleExport">导出我的商品</el-button>
      <el-upload
          class="upload-demo"
          style="margin-left: 10px;"
          :show-file-list="false"
          :on-success="handleImportSuccess"
          :action="uploadUrl"
      >
        <el-button type="danger" icon="Upload">导入我的商品</el-button>
      </el-upload>
    </div>
    <div class="box">
      <el-table :data="data.tableData" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" ></el-table-column>
        <el-table-column prop="image_url" label="商品图片" width="80">
          <template #default="scope">
            <el-image :src="scope.row.image_url || ''" style="width: 50px; height: 50px;"></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="编号" width="90" />
        <el-table-column prop="name" label="商品名称" width="180" />
        <el-table-column prop="category" label="分类" width="90" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="stock" label="库存" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" icon="EditPen" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" icon="Remove" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="box">
      <el-pagination
          background
          layout="sizes, prev, pager, next,  total"
          :page-sizes="[5, 10, 20, 50]"
          :total="data.total"
          v-model:page-size="queryParams.pageSize"
          v-model:current-page="queryParams.pageNum"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
      />
    </div>

    <el-dialog v-model="visible" :title="title" width="900">
      <el-form :model="form" :rules="rules" ref="form1" :label-width="85">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="商品价格" >
          <el-input v-model="form.price" type="text" autocomplete="off" placeholder="请输入价格（如：8.99）" />
        </el-form-item>
        <el-form-item label="单位" >
          <el-input v-model="form.unit" type="text" autocomplete="off" placeholder="请输入单位（如：盒，斤）" />
        </el-form-item>
        <el-form-item label="农产品简介" >
          <el-input v-model="form.description" type="textarea" autocomplete="off" />
        </el-form-item>
        <el-form-item label="产品类别">
          <el-select
              v-model="form.category"
              placeholder="请选择产品类别"
              clearable
              style="width: 200px">
            <el-option v-for="item in categoriesList" :key="item.id" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品图片">
          <el-row style="width: 100%" :gutter="40">
            <el-col :span="20">
              <el-input v-model="form.image_url" readonly disabled autocomplete="off" />
            </el-col>
            <el-col :span="4">
              <el-upload
                  action="http://localhost:8080/file/upload"
                  :show-file-list="false"
                  :on-success="avaterUploadSuccess"
              >
                <el-button type="primary">上传图片</el-button>
              </el-upload>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="产地" >
          <el-input v-model="form.producer" type="text" autocomplete="off" />
        </el-form-item>
        <el-form-item label="库存量" >
          <el-input v-model="form.stock" type="number" autocomplete="off" />
        </el-form-item>
        <el-form-item label="详细介绍">
          <div style="border: 1px solid #ccc;width: 100%;">
            <Toolbar
                style="border-bottom: 1px solid #ccc"
                :editor="editorRef"
                :defaultConfig="toolbarConfig"
                :mode="mode"
            />
            <Editor
                style="height: 500px; overflow-y: hidden;"
                v-model="form.content"
                :defaultConfig="editorConfig"
                :mode="mode"
                @onCreated="handleCreated"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="visible= false" >取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import { reactive, ref, onMounted, onBeforeUnmount, shallowRef } from 'vue'
import { download } from '@/utils/request.js'
import { ElMessage, ElMessageBox } from "element-plus"
import { listProducts, deleteProducts, addProducts, getProducts, updateProducts } from '@/api/products.js'
import { listAllCategories } from '@/api/categories.js'
import { Editor, Toolbar } from "@wangeditor/editor-for-vue"

// ========== 登录校验 ==========
const loginUser = JSON.parse(sessionStorage.getItem("login_user"));
let currentUserId = null;
if (!loginUser || !loginUser.userId || isNaN(Number(loginUser.userId))) {
  ElMessage.error("请先登录或登录信息异常！");
  window.location.href = '/login';
} else {
  currentUserId = Number(loginUser.userId);
  console.log("当前登录用户ID：", currentUserId);
}

// ========== 基础变量 ==========
const ids = ref([]);
const visible = ref(false);
const title = ref('添加商品');
const form = ref({});
const form1 = ref();
const uploadUrl = ref(import.meta.env.VITE_API_BASE_URL + '/sysProducts/importMyProducts');
const categoriesList = ref([]);
const isAdmin = false; // 商家标记

// ========== 编辑器配置 ==========
const editorRef = shallowRef()
const mode = ref('default');
const toolbarConfig = {};
const editorConfig = {
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      server: import.meta.env.VITE_API_BASE_URL + '/file/wang/upload',
      fieldName: 'file',
      maxFileSize: 2 * 1024 * 1024,
      allowedFileTypes: ['image/jpg', 'image/jpeg', 'image/png', 'image/gif', 'image/bmp'],
      headers: {
        token: loginUser?.token || ''
      },
      onSuccess: (file, res) => console.log('图片上传成功', res),
      onFailed: (file, res) => ElMessage.error('图片上传失败：' + (res?.message || '未知错误')),
      onError: (file, err) => ElMessage.error('图片上传失败：网络异常')
    }
  }
};

// 组件销毁时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor) editor.destroy()
})

const handleCreated = (editor) => {
  editorRef.value = editor
}

// ========== 表单校验规则 ==========
const rules = reactive({
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入商品价格', trigger: 'blur' }]
})

// ========== 页面加载 ==========
onMounted(() => {
  listAllCategories().then(res => {
    categoriesList.value = res.data || [];
  }).catch(err => {
    ElMessage.error('加载分类失败：' + err.message);
  })
  if (currentUserId) {
    getProductsList();
  }
})

// ========== 查询参数（含isAdmin=false，关键传递给后端） ==========
const queryParams = reactive({
  pageNum: 1,
  pageSize: 5,
  name: '',
  category: '',
  isAdmin: isAdmin // 商家标识：传递给后端
})

const data = reactive({
  total: 0,
  tableData: [],
})

// ========== 分页/查询/重置 ==========
const handleSizeChange = (val) => {
  queryParams.pageSize = val;
  queryParams.pageNum = 1;
  getProductsList();
}

const handleCurrentChange = () => {
  getProductsList();
}

const search = () => {
  queryParams.pageNum = 1;
  getProductsList();
}

const reset01 = () => {
  queryParams.name = '';
  queryParams.category = '';
  queryParams.pageNum = 1;
  getProductsList();
}

// ========== 勾选事件 ==========
const handleSelectionChange = (selections) => {
  ids.value = selections.map(item => item.id);
}

// ========== 删除商品 ==========
const handleDelete = (row) => {
  ElMessageBox.confirm(
      `是否确认删除商品"${row.name}"？`,
      "删除确认",
      { type: "warning" }
  ).then(() => {
    deleteProducts(row.id.toString(), isAdmin).then(res => {
      if ((res.code && res.code === 200) || res.success) {
        ElMessage.success("删除成功！");
        getProductsList();
      } else {
        ElMessage.error(res.message || "删除失败！");
      }
    }).catch(err => {
      ElMessage.error("删除失败：" + err.message);
    });
  }).catch(() => {
    ElMessage.info("已取消删除");
  });
};

// ========== 编辑商品 ==========
const handleEdit = (row) => {
  getProducts(row.id).then((res) => {
    if (!res.data) {
      ElMessage.error("该商品不存在或已被删除");
      return;
    }
    // 校验归属
    if (res.data.userId !== currentUserId) {
      ElMessage.error("无权修改他人商品！");
      return;
    }
    form.value = {
      id: res.data.id,
      name: res.data.name,
      category: res.data.category,
      price: res.data.price,
      image_url: res.data.image_url || '',
      unit: res.data.unit || "",
      producer: res.data.producer || "",
      stock: res.data.stock || "",
      description: res.data.description || "",
      content: res.data.content || "",
      userId: res.data.userId
    };
    title.value = "修改商品";
    visible.value = true;
  }).catch((err) => {
    ElMessage.error("查询商品信息失败：" + err.message);
  });
};

// ========== 添加商品 ==========
const handleAdd = () => {
  resetForm();
  visible.value = true;
  title.value = "添加商品";
  form.value.userId = currentUserId; // 绑定当前用户ID
}

const resetForm = () => {
  form.value = { userId: currentUserId };
}

// ========== 提交表单 ==========
const submitForm = () => {
  form1.value.validate(valid => {
    if (valid) {
      form.value.userId = currentUserId; // 强制绑定
      const request = form.value.id ? updateProducts : addProducts;
      request(form.value, isAdmin).then(res => {
        if ((res.code && res.code === 200) || res.success) {
          ElMessage.success(form.value.id ? "修改商品成功" : "添加商品成功");
          visible.value = false;
          getProductsList();
        } else {
          ElMessage.error(res.message || "操作失败！");
        }
      }).catch(err => {
        ElMessage.error("操作失败：" + err.message);
      });
    }
  });
}

// ========== 图片上传 ==========
const avaterUploadSuccess = (res) => {
  if (res.data) {
    form.value.image_url = res.data;
    console.log("图片URL：", form.value.image_url);
  } else {
    ElMessage.error("图片上传失败：" + (res.message || "未知错误"));
  }
}

// ========== 导出商品 ==========
const handleExport = () => {
  download("/sysProducts/exportMyProducts", { ...queryParams, isAdmin: isAdmin });
}

// ========== 导入商品 ==========
const handleImportSuccess = (res) => {
  ElMessage.success("导入成功，共导入" + (res.data || 0) + "条商品数据");
  getProductsList();
}

// ========== 获取我的商品列表（关键：传递isAdmin=false） ==========
const getProductsList = () => {
  listProducts(queryParams).then(res => {
    const responseData = res.data || res;
    data.tableData = responseData.list || [];
    data.total = responseData.total || 0;
    console.log("商家-我的商品列表：", data.tableData);
  }).catch(err => {
    ElMessage.error('获取我的商品列表失败：' + err.message);
    data.tableData = [];
    data.total = 0;
  });
};
</script>

<style scoped>
.box{
  margin-bottom: 15px;
}
.categories a{
  margin-right: 10px;;
}
</style>