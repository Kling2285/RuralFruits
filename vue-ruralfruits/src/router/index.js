import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path:'/index',
      name:'index00',
      component: () => import('../views/home/index.vue'),
    },
    {
      path: '/login',
      name: 'login',
      // 去掉显式类型声明，直接简写
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      // 去掉显式类型声明，直接简写
      component: () => import('../views/Register.vue')
    },
    {
      path: '/Orders',
      name: 'Orders',
      component: () => import('../views/home/Orders.vue')
    },
    {
      path: '/carts',
      name: 'carts',
      component: () => import('../views/home/Carts.vue')
    },
    {
      path: '/person',
      name: 'person',
      component: () => import('../views/home/Person.vue')
    },
    {
      path:'/',
      name:'index',
      redirect:'/login'
    },
    {
      path: '/about',
      name: 'about',
      // 去掉显式类型声明，直接简写
      component: () => import('../views/About.vue')
    },
    {
      path: '/details/:id',
      name: 'details',
      component: () => import('../views/home/ProductDetails.vue')
    },
    {
      path: '/notfound',
      name: 'notfound',
      // 去掉显式类型声明，直接简写
      component: () => import('../views/404.vue')
    },


    {
      path: '/products',
      name: 'products_index',
      component: () => import('../views/home/productList.vue')
    },

    {
      path: '/search',
      name: 'search',
      component: () => import('../views/home/Search.vue')
    },
    {path:'/manager',
    name:'manager',
      component: () => import('../views/Manager.vue'),
    children:[
        {
      path: 'home',
      name: 'home',
      component: Home,
    },
      {
        path:'user',
        name:'user',
        component: () => import('../views/User.vue'),
      },
      {
        path:'products',
        name:'products',
        component: () => import('../views/Products.vue'),
      },
      {
        path:'notice',
        name:'notice',
        component: () => import('../views/Notice.vue'),
      },
      {
        path:'ordermanager',
        name:'ordermanager',
        component: () => import('../views/OrderManager.vue'),
      },
      {
        path:'apply',
        name:'apply',
        component: () => import('../views/Apply.vue'),
      },
      {
        path:'myproducts',
        name:'myproducts',
        component: () => import('../views/MyProducts.vue'),
      },
      {
        path: '/:pathMath(.*)',
        redirect: 'notfound'
      },
    ]
    }
  ],
})
//定义白名单
const whiteList= ['/login', '/register', '/notfound','/index','/products','/search'];
//路由前置守卫
router.beforeEach((to, from, next ) => {

  //取出登录的标识
  let user = sessionStorage.getItem("login_user");
  if (!user) { //如果没有标识
    //判断访问的是不是白名单路由
    if (whiteList.indexOf(to.path) !== -1||to.name == 'details') {
      next();
    } else {
      next('/login');
    }
  } else {
    next();
  }
})

export default router
