import request from '@/utils/request.js';


export function submitMerchantApply(userId) {
    return request.post('/apply/submit', { userId });
}

/**
 * 分页查询申请列表
 * @param {Object} params - 查询参数（pageNum, pageSize, auditStatus, startTime, endTime, userId）
 * @returns {Promise}
 */
export function listApply(params) {
    return request({
        url: '/apply/page',
        method: 'get',
        params
    });
}

/**
 * 删除申请记录（单个/批量）
 * @param {Number|Array} id - 申请ID（单个ID或ID数组）
 * @returns {Promise}
 */
export function deleteApply(id) {
    return request({
        url: `/apply/${id}`,
        method: 'delete'
    });
}

/**
 * 审核申请（通过/驳回）
 * @param {Object} data - 审核数据（id, auditorId, auditStatus, auditRemark）
 * @returns {Promise}
 */
export function auditApply(data) {
    return request({
        url: '/apply/update',
        method: 'put',
        data
    });
}

/**
 * 导出申请记录
 * @param {Object} params - 导出参数（与查询参数一致）
 * @returns {Promise}
 */
export function exportApply(params) {
    return request({
        url: '/apply/export',
        method: 'get',
        params,
        responseType: 'blob'
    });
}