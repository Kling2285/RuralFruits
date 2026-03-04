import request from "@/utils/request.js";

//查询所有的轮播图
export function listAllBanners(){
    return request.get('/banner/all')
}