import request from "@/utils/request.js"

//查询所有类别
export function listAllCategories(){
    return request.get('/categories/all');
}