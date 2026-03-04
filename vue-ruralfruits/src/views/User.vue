<template>
  <div>
    <div class="box">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="用户名" >
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="昵称" >
          <el-input v-model="queryParams.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select
              v-model="queryParams.type"
              placeholder="请输入用户类型"
              clearable
              style="width: 200px">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
            <el-option label="商家" value="merchant" />
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
      <el-button type="success" icon="EditPen" @click="handleEdit">修改</el-button>
      <el-button type="info" icon="Remove" @click="handleDelete">批量删除</el-button>
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
      <el-table :data="data.tableData" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection"></el-table-column>
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="scope">
            <el-image :src="scope.row.avatar" style="width: 50px; height: 50px;"></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="编号" width="180" />
        <el-table-column prop="username" label="用户名" width="180" />
        <el-table-column prop="nickname" label="用户昵称" width="180" />
        <el-table-column prop="type" label="用户类型" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button
                size="small"
                icon="EditPen" @click="handleEdit(scope.row)">编辑</el-button>
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
          layout="sizes, prev, pager, next,  total"
          :page-sizes="[5, 10, 20, 50]"
          :total="data.total"
          v-model:page-size="queryParams.pageSize"
          v-model:current-page="queryParams.pageNum"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
      />
    </div>


    <el-dialog v-model="visible" :title="title" width="800">
      <el-form :model="form" :rules="rules"  ref="form1" :label-width="85">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="密码" prop="password" >
          <el-input v-model="form.password" autocomplete="off" />
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
              >
                <el-button type="primary">上传头像</el-button>
              </el-upload>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="昵称" >
          <el-input v-model="form.nickname" autocomplete="off" />
        </el-form-item>
        <el-form-item label="性别" >
          <el-radio-group v-model="form.sex">
            <el-radio value="0">未知</el-radio>
            <el-radio value="1">男</el-radio>
            <el-radio value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="phone" >
          <el-input v-model="form.phone" autocomplete="off" />
        </el-form-item>
        <el-form-item label="email" >
          <el-input v-model="form.email" autocomplete="off" />
        </el-form-item>

        <el-form-item label="用户类型" >
          <el-select v-model="form.type" placeholder="">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
            <el-option label="商家" value="merchant" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="visible= false" >取消</el-button>
          <el-button type="primary" @click="submitForm">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>



  </div>
</template>
<script setup>
import { reactive,ref } from 'vue'
import {download} from '@/utils/request.js';
import {ElMessage, ElMessageBox} from "element-plus";
import {listUser,deleteuser,adduser,getuser,updateuser} from '@/api/sysuser.js'


const ids=ref([]);//选中数据的编号
const visible=ref(false);
const title=ref('添加用户');//显示对话框标题
const form=ref({});//表单数据
const form1=ref();
const uploadUrl=ref(import.meta.env.VITE_API_BASE_URL+'/sysuser/import');//导入文件地址

//校验规则
// 表单验证规则
const rules = reactive( {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码必须6位以上', trigger: 'blur' }
  ]
})

const formInline = reactive({
  user: '',
  region: ''
});

// 核心修改：将 queryParams 从 ref 改为 reactive
const queryParams = reactive({
  pageNum: 1,
  pageSize: 5
})

const data=reactive({
  total:0,
  tableData:[],//用户列表
})

const handleSizeChange=(val)=>{
  queryParams.value.pageNum=1;
  getUserList();
}

const handleCurrentChange=()=>{
  getUserList();
}

//条件查询
const search=()=>{
  queryParams.pageNum=1;
  getUserList();
}

//重置
const reset01=()=>{
  queryParams.username='';
  queryParams.nickname='';
  queryParams.type='';
  queryParams.pageNum=1;
  getUserList();
}

//删除单个用户
const handleDelete = (row) => {
  const id = row.userId ||ids.value;
  if(ids.value.length==0){
    ElMessage.error("请选择要删除的数据")
    return;
  }
  ElMessageBox.confirm("是否确认删除编号是 '" + id + "' 的数据吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    deleteuser(id).then(res => {
      ElMessage({
        type: 'success',
        message: '删除成功!'
      })
      getUserList();
    });
  });
};

//批量删除
const handleSelectionChange=selections=>{
  // console.log(selections);
  ids.value=[]
  selections.map(item=>ids.value.push(item.userId))
  console.log(ids.value);
}

//添加
const handleAdd=()=>{
  visible.value=true;
  title.value="添加用户";
  resetForm();
}

//清空表单
const resetForm=()=>{
  form.value={
    username:"",
    nickname:"",
    password:"",
    sex: '',
    type:'',
    phone:'',
    email:''

  };
}

//编辑
// 编辑功能：同时支持「行内单个编辑」和「顶部选择1条编辑」
const handleEdit = (row) => {
  let targetId; // 最终要编辑的用户ID

  // 场景1：行内单个编辑（点击表格行内的「编辑」按钮）
  if (row && row.userId) {
    targetId = row.userId; // 直接取当前行的用户ID
  }

  // 场景2：顶部批量选择后编辑（点击顶部「修改」按钮）
  else {
    // 校验1：是否勾选了数据
    if (ids.value.length === 0) {
      ElMessage.error("请选择要修改的用户");
      return;
    }
    // 校验2：只能勾选1条（编辑不能批量操作）
    if (ids.value.length > 1) {
      ElMessage.error("最多只能选择1条数据进行编辑");
      return;
    }
    // 取勾选的第一条数据的ID
    targetId = ids.value[0];
  }

  // 校验：确保有有效的用户ID（避免空ID调用接口）
  if (!targetId) {
    ElMessage.error("用户ID无效，请重试");
    return;
  }

  // 调用接口查询用户详情，回显到表单
  getuser(targetId)
      .then((res) => {
        // 接口返回正常，但数据为空（用户不存在/已删除）
        if (!res.data) {
          ElMessage.error("该用户不存在或已被删除");
          return;
        }
        // 回显数据到表单（注意：密码字段接口可能不返回，避免表单显示undefined）
        form.value = {
          ...res.data,
          password: res.data.password || "", // 密码兜底为空字符串
        };
        title.value = "修改用户";
        visible.value = true; // 打开对话框
      })
      .catch((err) => {
        // 接口请求失败（网络错误、后端异常等）
        ElMessage.error("查询用户信息失败，请稍后重试");
        console.error("编辑用户查询失败：", err);
      });
};

//导出
const  handleExport=()=>{
  download("/sysuser/export",queryParams)
}

//批量导入
const  handleImportSuccess=(res)=>{
  ElMessage.success("导入成功，共导入"+res.data+"条数据");
  getUserList()
}

//提交表单
const submitForm=()=> {
  form1.value.validate(valid => {
    if (valid) {
      //根据form中的userId是否有值来判断执行添加还是修改操作，有值:edit,否则：add
      if (!form.value.userId){
        adduser(form.value).then(res => {
          ElMessage.success("添加用户成功");
          visible.value = false;
          getUserList();
        })
      }else {
        updateuser(form.value).then(res=>{
          ElMessage.success("修改用户成功");
          visible.value = false;
          getUserList();})
      }

    }
  });
}

const avaterUploadSuccess=(res)=>{
  form.value.avatar=res.data;

}




const getUserList=()=>{
  listUser(queryParams).then(res=> {
    if(res.code==200){
      data.tableData=res.data.records;
      data.total=res.data.total
    }else{
      ElMessage.error('接口调用异常');
    }
    console.log(res);
  });
}

getUserList();
//
// deleteuser(id).then(res=>{
//   console.log(res)
// });
</script>
