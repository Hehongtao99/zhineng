import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '../views/layout/Layout'

/**
 * hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
 *                                if not set alwaysShow, only more than one route under the children
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noredirect           if `redirect:noredirect` will no redirct in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
  }
 **/
export const constantRouterMap = [
  {path: '/login', component: () => import('@/views/login/index'), hidden: true},
  {path: '/404', component: () => import('@/views/404'), hidden: true},
  {
    path: '',
    component: Layout,
    redirect: '/home',
    children: [{
      path: 'home',
      name: 'home',
      component: () => import('@/views/home/index'),
      meta: {title: '首页', icon: 'home'}
    }]
  }
]

export const asyncRouterMap = [
  {
    path:'/ums',
    component: Layout,
    redirect: '/ums/admin',
    name: 'ums',
    meta: {title: '权限', icon: 'ums'},
    children: [
      {
        path: 'admin',
        name: 'admin',
        component: () => import('@/views/ums/admin/index'),
        meta: {title: '用户列表', icon: 'ums-admin'}
      },
      // {
      //   path: 'role',
      //   name: 'role',
      //   component: () => import('@/views/ums/role/index'),
      //   meta: {title: '角色列表', icon: 'ums-role'}
      // },
      // {
      //   path: 'allocMenu',
      //   name: 'allocMenu',
      //   component: () => import('@/views/ums/role/allocMenu'),
      //   meta: {title: '分配菜单'},
      //   hidden: true
      // },
      // {
      //   path: 'allocResource',
      //   name: 'allocResource',
      //   component: () => import('@/views/ums/role/allocResource'),
      //   meta: {title: '分配资源'},
      //   hidden: true
      // },
      // {
      //   path: 'menu',
      //   name: 'menu',
      //   component: () => import('@/views/ums/menu/index'),
      //   meta: {title: '菜单列表', icon: 'ums-menu'}
      // },
      // {
      //   path: 'addMenu',
      //   name: 'addMenu',
      //   component: () => import('@/views/ums/menu/add'),
      //   meta: {title: '添加菜单'},
      //   hidden: true
      // },
      // {
      //   path: 'updateMenu',
      //   name: 'updateMenu',
      //   component: () => import('@/views/ums/menu/update'),
      //   meta: {title: '修改菜单'},
      //   hidden: true
      // },
      // {
      //   path: 'resource',
      //   name: 'resource',
      //   component: () => import('@/views/ums/resource/index'),
      //   meta: {title: '资源列表', icon: 'ums-resource'}
      // },
      // {
      //   path: 'resourceCategory',
      //   name: 'resourceCategory',
      //   component: () => import('@/views/ums/resource/categoryList'),
      //   meta: {title: '资源分类'},
      //   hidden: true
      // }
    ]
  },
  {
    path:'',
    component: Layout,
    redirect: '/category',
    name: 'category',
    meta: {title: '任务类型', icon: ''},
    children: [
      {
        path: 'category',
        name: 'category',
        component: () => import('@/views/category/index'),
        meta: {title: '任务类型', icon: ''}
      },
    ]
  },
  {
    path:'',
    component: Layout,
    redirect: '/priority',
    name: 'priority',
    meta: {title: '权重管理', icon: ''},
    children: [
      {
        path: 'priority',
        name: 'priority',
        component: () => import('@/views/priority/index'),
        meta: {title: '权重管理', icon: ''}
      },
    ]
  },
  {
    path:'',
    component: Layout,
    redirect: '/task',
    name: 'task',
    meta: {title: '任务管理', icon: ''},
    children: [
      {
        path: 'task',
        name: 'task',
        component: () => import('@/views/task/index'),
        meta: {title: '任务管理', icon: ''}
      },
    ]
  },
  {
    path:'',
    component: Layout,
    redirect: '/schedule',
    name: 'schedule',
    meta: {title: '日程安排', icon: ''},
    children: [
      {
        path: 'schedule',
        name: 'schedule',
        component: () => import('@/views/schedule/index'),
        meta: {title: '日程安排', icon: ''}
      },
    ]
  },
  {
    path:'',
    component: Layout,
    redirect: '/notification',
    name: 'notification',
    meta: {title: '通知管理', icon: ''},
    children: [
      {
        path: 'notification',
        name: 'notification',
        component: () => import('@/views/notification/index'),
        meta: {title: '通知管理', icon: ''}
      },
    ]
  },
  {
    path:'',
    component: Layout,
    redirect: '/userSchedule',
    name: 'userSchedule',
    meta: {title: '日程安排', icon: ''},
    children: [
      {
        path: 'userSchedule',
        name: 'userSchedule',
        component: () => import('@/views/userSchedule/index'),
        meta: {title: '日程安排', icon: ''}
      },
    ]
  },
  {
    path:'',
    component: Layout,
    redirect: '/userNotification',
    name: 'userNotification',
    meta: {title: '我的通知', icon: ''},
    children: [
      {
        path: 'userNotification',
        name: 'userNotification',
        component: () => import('@/views/userNotification/index'),
        meta: {title: '我的通知', icon: ''}
      },
    ]
  },
  {
    path:'',
    component: Layout,
    redirect: '/chart',
    name: 'chart',
    meta: {title: '统计报表', icon: ''},
    children: [
      {
        path: 'chart',
        name: 'chart',
        component: () => import('@/views/chart/index'),
        meta: {title: '统计报表', icon: ''}
      },
    ]
  },
  {
    path:'',
    component: Layout,
    redirect: '/userInfo',
    name: 'userInfo',
    meta: {title: '用户信息', icon: ''},
    children: [
      {
        path: 'userInfo',
        name: 'userInfo',
        component: () => import('@/views/userInfo/index'),
        meta: {title: '用户信息', icon: ''}
      },
    ]
  },
  {
    path:'',
    component: Layout,
    redirect: '/userTask',
    name: 'userTask',
    meta: {title: '任务管理', icon: ''},
    children: [
      {
        path: 'userTask',
        name: 'userTask',
        component: () => import('@/views/userTask/index'),
        meta: {title: '任务管理', icon: ''}
      },
    ]
  },
  {
    path:'',
    component: Layout,
    redirect: '/userReport',
    name: 'userReport',
    meta: {title: '我的报表', icon: 'chart'},
    children: [
      {
        path: 'userReport',
        name: 'userReport',
        component: () => import('@/views/userReport/index'),
        meta: {title: '我的报表', icon: 'chart'}
      },
    ]
  },
  {path: '*', redirect: '/404', hidden: true}
]

export default new Router({
  // mode: 'history', //后端支持可开
  scrollBehavior: () => ({y: 0}),
  routes: constantRouterMap
})
