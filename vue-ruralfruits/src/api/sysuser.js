import request from '@/utils/request.js'

// 删除单个用户
export function deleteuser(id) {
    return request.delete(`/sysuser/${id}`);
}

// 分页查询用户（管理页）
export function listUser(params) {
    return request({
        url: 'sysuser/list02',
        method: 'get',
        params: params
    })
}

// 添加用户
export function adduser(data) {
    return request.post('/sysuser', data);
}

// 根据编号查询单个数据（管理页用，原有逻辑不变）
export function getuserAdmin(id) {
    return request.get('/sysuser/' + id);
}

// 个人中心 - 查询用户详情（用于修改回显）
export function getuser(id) {
    return request.get('/sysuser/profile/' + id);
}

// 管理页 - 修改用户（原有逻辑不变）
export function updateuserAdmin(data) {
    return request.put('/sysuser', data)
}

// 个人中心 - 修改个人信息（含密码加密处理）
export function updateuser(data) {
    return request.put('/sysuser/profile/update', data)
}


export function getUserById(userId) {
    return request({
        url: `/sysuser/getById/${userId}`,
        method: 'get'
    })
}

export function getUserBalance(userId) {
    return request({
        url: `/sysuser/balance/${userId}`,
        method: 'get'
    });
}

// 新增：扣减用户余额
export function deductUserBalance(data) {
    return request({
        url: '/sysuser/balance/deduct',
        method: 'post',
        data: data
    });
}