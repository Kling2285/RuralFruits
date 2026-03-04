// 关键：默认导入，匹配 request.js 的 export default
import request from "@/utils/request.js";

export function addCart(data) {
    return request.post('/cart', data);
}

export function listCarts(queryParams) {
    return request.get(
        "/cart/list",
        { params: queryParams }
    )
}

export function deleteCart(ids){
    return request.delete('/cart/'+ids)
}