//获取用户信息
export function getUser(){
    let user=sessionStorage.getItem("login_user");
    if (user){
        return JSON.parse(user);
    }
    return null;
}

//存储用户信息
export function storeUser(user) {
    sessionStorage.setItem("login_user", JSON.stringify(user));
}

export function removeUser(){
    sessionStorage.removeItem("login_user");
}

export const setUser = (user) => {
    localStorage.setItem('user', JSON.stringify(user));
};