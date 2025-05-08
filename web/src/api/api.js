import request from '@/utils/request'

// 资源分类
export function fetchCategoryList(params) {
  return request({
    url: '/web/category/list',
    method: 'get',
    params: params,
  })
}

export function createCategory(data) {
  return request({
    url: '/web/category/create',
    method: 'post',
    data: data,
  })
}

export function updateCategory(data) {
  return request({
    url: '/web/category/update',
    method: 'post',
    data: data
  })
}

export function deleteCategory(id) {
  return request({
    url: '/web/category/delete/' + id,
    method: 'post',
  })
}

export function fetchAllCategoryList() {
  return request({
    url: '/web/category/listAll',
    method: 'get',
  })
}

// 首页数据
export function homeData() {
  return request({
    url: '/web/home',
    method: 'get',
  })
}


// 用户
export function fetchUserList(params) {
  return request({
    url: '/web/user/list',
    method: 'get',
    params: params,
  })
}

export function createUser(data) {
  return request({
    url: '/web/user/create',
    method: 'post',
    data: data,
  })
}

export function updateUser(data) {
  return request({
    url: '/web/user/update',
    method: 'post',
    data: data
  })
}

export function deleteUser(id) {
  return request({
    url: '/web/user/delete/' + id,
    method: 'post',
  })
}

// 权重
export function fetchPriorityList(params) {
  return request({
    url: '/web/priority/list',
    method: 'get',
    params: params,
  })
}

export function createPriority(data) {
  return request({
    url: '/web/priority/create',
    method: 'post',
    data: data,
  })
}

export function updatePriority(data) {
  return request({
    url: '/web/priority/update',
    method: 'post',
    data: data
  })
}

export function deletePriority(id) {
  return request({
    url: '/web/priority/delete/' + id,
    method: 'post',
  })
}

export function fetchAllPriorityList(params) {
  return request({
    url: '/web/priority/listAll',
    method: 'get',
    params: params,
  })
}


// 任务
export function fetchTaskList(params) {
  return request({
    url: '/web/task/list',
    method: 'get',
    params: params,
  })
}

export function createTask(data) {
  return request({
    url: '/web/task/create',
    method: 'post',
    data: data,
  })
}

export function updateTask(data) {
  return request({
    url: '/web/task/update',
    method: 'post',
    data: data
  })
}

export function updateTaskStatus(id, status) {
  return request({
    url: '/web/task/updateStatus',
    method: 'post',
    params: { id, status }
  })
}

export function setTaskReminder(id, reminderTime) {
  return request({
    url: '/web/task/setReminder',
    method: 'post',
    params: { id, reminderTime }
  })
}

export function deleteTask(id) {
  return request({
    url: '/web/task/delete/' + id,
    method: 'post',
  })
}

export function fetchAllTaskListByStatus(params) {
  return request({
    url: '/web/task/listAllByStatus',
    method: 'get',
    params: params,
  })
}

export function fetchAllTaskList() {
  return request({
    url: '/web/task/listAll',
    method: 'get',
  })
}

export function fetchTaskInfo(id) {
  return request({
    url: '/web/task/info/' + id,
    method: 'get',
  })
}

// 安排
export function fetchScheduleList(params) {
  return request({
    url: '/web/schedule/list',
    method: 'get',
    params: params,
  })
}

// 日程智能排序
export function fetchScheduleSmartSort(params) {
  return request({
    url: '/web/schedule/smartSort',
    method: 'get',
    params: params,
  })
}

export function fetchUserScheduleList(params) {
  return request({
    url: '/web/schedule/user/list',
    method: 'get',
    params: params,
  })
}

// 用户日程智能排序
export function fetchUserScheduleSmartSort(params) {
  return request({
    url: '/web/schedule/user/smartSort',
    method: 'get',
    params: params,
  })
}

export function fetchAllUsersSchedulesByWeight(params) {
  return request({
    url: '/web/schedule/allUsers',
    method: 'get',
    params: params,
  })
}

export function fetchMonthScheduleList(yearMonth) {
  return request({
    url: `/web/schedule/month/${yearMonth}`,
    method: 'get',
  })
}

export function fetchUserMonthScheduleList(yearMonth) {
  return request({
    url: `/web/schedule/month/user/${yearMonth}`,
    method: 'get',
  })
}

export function createSchedule(data) {
  return request({
    url: '/web/schedule/create',
    method: 'post',
    data: data,
  })
}

export function updateSchedule(data) {
  return request({
    url: '/web/schedule/update',
    method: 'post',
    data: data
  })
}

export function updateScheduleTime(id, startTime, endTime) {
  return request({
    url: '/web/schedule/updateTime',
    method: 'post',
    params: { id, startTime, endTime }
  })
}

export function deleteSchedule(id) {
  return request({
    url: '/web/schedule/delete/' + id,
    method: 'post',
  })
}

export function fetchConflictScheduleList(params) {
  return request({
    url: '/web/schedule/conflict/list/' + params.id,
    method: 'get',
    params: params,
  })
}

// 通知
export function fetchNotificationList(params) {
  return request({
    url: '/web/notification/list',
    method: 'get',
    params: params,
  })
}

export function fetchUserNotificationList(params) {
  return request({
    url: '/web/notification/user/list',
    method: 'get',
    params: params,
  })
}

export function createNotification(data) {
  return request({
    url: '/web/notification/create',
    method: 'post',
    data: data,
  })
}

export function updateNotification(data) {
  return request({
    url: '/web/notification/update',
    method: 'post',
    data: data
  })
}

export function deleteNotification(id) {
  return request({
    url: '/web/notification/delete/' + id,
    method: 'post',
  })
}

export function markNotificationAsRead(id) {
  return request({
    url: '/web/notification/read/' + id,
    method: 'post',
  })
}

export function markAllNotificationsAsRead() {
  return request({
    url: '/web/notification/readAll',
    method: 'post',
  })
}

// 报表
export function trendPie() {
  return request({
    url: '/web/chart/trendPie',
    method: 'get',
  })
}

export function trend(params) {
  return request({
    url: '/web/chart/trend',
    method: 'get',
    params: params,
  })
}

// 用户完成任务统计报表
export function userTrend(params) {
  return request({
    url: '/web/chart/userTrend',
    method: 'get',
    params: params,
  })
}

// 会议室
export function fetchMeetingRoomCount() {
  return request({
    url: '/web/meetingRoom/count',
    method: 'get',
  })
}

export function fetchAvailableMeetingRoomCount() {
  return request({
    url: '/web/meetingRoom/available',
    method: 'get',
  })
}

export function autoArrangeTasksByDate(date) {
  return request({
    url: '/web/task/autoArrangeByDate',
    method: 'post',
    params: { date }
  })
}

export function getRemainingTimeForDate(date) {
  return request({
    url: '/web/task/getRemainingTimeForDate',
    method: 'get',
    params: { date }
  })
}

export function getRemainingTimeForUserAndDate(date, userId) {
  return request({
    url: '/web/task/getRemainingTimeForUserAndDate',
    method: 'get',
    params: { date, userId }
  })
}

/**
 * 调整日程顺序
 * @param {Object} params
 * @param {number} params.scheduleId - 被拖动的日程ID
 * @param {string} params.targetTime - 目标时间
 * @param {string} params.date - 日期
 */
export function adjustScheduleOrder(params) {
  return request({
    url: '/web/schedule/adjustOrder',
    method: 'post',
    params: params
  });
}

export function checkTimeConflict(data) {
  return request({
    url: '/web/task/checkTimeConflict',
    method: 'post',
    data: data
  })
}
