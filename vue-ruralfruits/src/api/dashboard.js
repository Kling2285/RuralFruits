import request from "@/utils/request.js";

// 查询当日统计数据（删除 :void 注解）
export function selectDailyData() {
    return request.get("/dashboard/daily"); // 返回 Promise，供前端异步调用
}


//查询当月营业额
export function selectMonthOrder(){
    return request.get("dashboard/month_order")
}

//查询产品分类统计
export function selectCategories(){
    return request.get("dashboard/categories")
}