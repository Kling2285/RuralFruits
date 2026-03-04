import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: 'http://localhost:8080', // 固定基础地址（也可保留环境变量，二选一）
    timeout: 30000, // 请求超时时间
    headers: {
        'Content-Type': 'application/json;charset=utf-8' // 统一请求头格式
    }
})

// request 拦截器：携带 Token（已正确实现，无需修改）
request.interceptors.request.use(
    config => {
        // 从 sessionStorage 获取登录用户信息（和你的登录逻辑一致）
        let user = sessionStorage.getItem("login_user");
        if (user) {
            user = JSON.parse(user);
            config.headers['token'] = user.token; // 后端从 header 获取 token
        }
        return config;
    },
    error => {
        ElMessage.error('请求发送失败：' + error.message);
        return Promise.reject(error);
    }
)

// response 拦截器：统一处理响应（新增错误提示，避免静默失败）
request.interceptors.response.use(
    response => {
        let res = response.data;
        // 判断是否为 json 格式（兼容后端可能返回的字符串格式）
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res;
        }
        // 新增：后端返回非 200 时，自动提示错误（避免前端漏处理）
        if (res && res.code !== 200) {
            ElMessage.error(res.msg || '操作失败！');
        }
        return res;
    },
    error => {
        // 优化错误提示，更精准
        if (error.response) {
            const status = error.response.status;
            if (status === 401) {
                ElMessage.error("未登录或 Token 已过期，请重新登录！");
                // 可选：跳转到登录页（需导入路由）
                // router.push('/login');
            } else if (status === 403) {
                ElMessage.error("暂无权限操作！");
            } else if (status === 404) {
                ElMessage.error("请求的接口不存在！");
            } else if (status === 500) {
                ElMessage.error("服务器内部错误，请联系管理员！");
            } else {
                ElMessage.error(`请求失败（状态码：${status}）：${error.message}`);
            }
        } else {
            ElMessage.error('网络异常，请检查网络连接！');
        }
        return Promise.reject(error);
    }
)

// 批量导出的通用方法（优化 Token 携带方式，和请求拦截器一致）
export function download(url, params) {
    const base_url = 'http://localhost:8080'; // 和 baseURL 保持一致
    let searchParam = new URLSearchParams();
    // 拼接查询参数
    for (let key in params) {
        if (params[key] !== '' && params[key] !== undefined) {
            searchParam.append(key, params[key]);
        }
    }
    // 携带 Token（从 sessionStorage 获取，和请求拦截器逻辑一致）
    let user = sessionStorage.getItem("login_user");
    if (user) {
        user = JSON.parse(user);
        searchParam.append("token", user.token);
    }
    // 跳转下载
    window.location = base_url + url + "?" + searchParam.toString();
}

export default request