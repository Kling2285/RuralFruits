import request from "@/utils/request.js"

// 商品列表查询
export function listProducts(queryParams) {
    return request.get('/products/list', {
        params: queryParams
    });
}

// 查询用户推荐的农产品
export function listRecommendProducts() {
    return request.get('/products/recommendList');
}

// 查询单个商品
export function getProducts(id, isAdmin = false) {
    return request.get(`/products/${id}`, {
        params: { isAdmin }
    });
}

// 添加商品
export function addProducts(data, isAdmin = false) {
    console.log("前端传给后端的商品参数：", data);
    return request.post('/products/add', data, {
        params: { isAdmin }
    });
}

// 修改商品
export function updateProducts(data, isAdmin = false) {
    return request.put('/products', data, {
        params: { isAdmin }
    });
}

// 删除商品
export function deleteProducts(ids, isAdmin = false) {
    return request.post(`/products/${ids}`, {}, {
        params: { isAdmin }
    });
}