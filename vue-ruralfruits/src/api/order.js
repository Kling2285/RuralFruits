import request from '@/utils/request.js'

// 1. 批量新增订单（原有接口，不变）
export function addOrders(data) {
    return request.post("/order", data)
}

// 2. 单条新增订单（对应后端 /order/add 接口）
export function addSingleOrder(data) {
    return request.post("/order/add", data)
}

// 3. 修改订单（对应后端 /order/update 接口）
export function updateOrder(data) {
    return request.put("/order/update", data)
}

// 4. 单条删除订单（对应后端 /order/{id} 接口）
export function deleteOrder(id) {
    return request.delete(`/order/${id}`)
}

// 5. 批量删除订单（对应后端 /order/batch/{ids} 接口）
export function deleteBatchOrders(ids) {
    // ids 传入格式：数组 [1,2,3]，自动转为字符串 "1,2,3"
    return request.delete(`/order/batch/${ids.join(',')}`)
}

// 6. 查询订单详情（对应后端 /order/{id} 接口）
export function getOrderDetail(id) {
    return request.get(`/order/detail/${id}`);
}

// 7. 订单列表分页查询（原有接口，不变）
export function listOrders(Params) {
    return request.get("/order/list", {
        params: Params
    })
}

//查询全部
export function listAllOrders(Params) {
    return request.get("/order/listAll", {
        params: Params
    })
}