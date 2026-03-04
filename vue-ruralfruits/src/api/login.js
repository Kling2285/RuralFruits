import request from '@/utils/request.js'

//用户登录
export function login(data){
    return request.post('/login',data);
}

//用户注册
export function register(data){
    return request.post('/register',data);
}