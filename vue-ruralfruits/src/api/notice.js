import request from "@/utils/request.js"; // 统一使用 request 实例（和项目原有风格一致）

/**
 * 分页查询通知列表（对应后端 /notice/list）
 * @param {Object} queryParams - 查询参数
 * @param {string} [queryParams.title] - 通知标题（模糊查询）
 * @param {number} [queryParams.open] - 是否启用（1=启用，0=禁用）
 * @param {string} [queryParams.startTime] - 开始时间（时间区间筛选）
 * @param {string} [queryParams.endTime] - 结束时间（时间区间筛选）
 * @param {number} [queryParams.pageNum=1] - 页码（默认第1页）
 * @param {number} [queryParams.pageSize=5] - 每页条数（默认5条）
 * @param {string} [queryParams.column] - 排序字段（如 time、id、title）
 * @param {string} [queryParams.sort] - 排序方向（asc/desc）
 * @returns {Promise} - 请求结果
 */
export function listNotice(queryParams) {
    return request.get("/notice/list", {
        params: queryParams,
    });
}

/**
 * 查询单个通知详情（对应后端 /notice/{id}）
 * @param {number} id - 通知ID
 * @returns {Promise} - 请求结果
 */
export function getNotice(id) {
    return request.get(`/notice/${id}`);
}

/**
 * 新增通知（对应后端 /notice/add）
 * @param {Object} data - 通知数据
 * @param {string} data.title - 通知标题（必填）
 * @param {string} data.content - 通知内容（必填）
 * @param {string} data.time - 发布时间（必填）
 * @param {number} [data.open=1] - 是否启用（默认1=启用）
 * @returns {Promise} - 请求结果
 */
export function addNotice(data) {
    console.log("前端传给后端的通知参数：", data);
    return request.post("/notice/add", data);
}

/**
 * 修改通知（对应后端 /notice/update）
 * @param {Object} data - 通知数据
 * @param {number} data.id - 通知ID（必填）
 * @param {string} data.title - 通知标题（必填）
 * @param {string} data.content - 通知内容（必填）
 * @param {string} data.time - 发布时间（必填）
 * @param {number} data.open - 是否启用（1=启用，0=禁用）
 * @returns {Promise} - 请求结果
 */
export function updateNotice(data) {
    return request.put("/notice/update", data);
}

/**
 * 批量/单个删除通知（对应后端 /notice/{ids}）
 * @param {number|number[]} ids - 通知ID（单个ID或ID数组，如 1 或 [1,2,3]）
 * @returns {Promise} - 请求结果
 */
export function deleteNotice(ids) {
    // 处理单个ID场景：转为数组后拼接字符串
    const idStr = Array.isArray(ids) ? ids.join(",") : ids;
    return request.delete(`/notice/${idStr}`);
}