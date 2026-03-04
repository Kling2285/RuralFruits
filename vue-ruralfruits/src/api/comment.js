import request from '@/utils/request.js'

export function listComment(pid){
    return request.get('/comment/all',{
        params:{
            pid:pid
        }
    })
}