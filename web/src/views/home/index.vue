<template>
  <el-main style="background: #f0f0f4">
    <el-row class="home">
      <!-- 管理员 -->
      <el-col :span="24" :gutter="12">
        <div class="header">
          <el-card
            shadow="hover"
            style="background: #9bc53b; height: 140px"
            :body-style="{ display: 'flex', padding: 0 }"
          >
            <i class="icon" :class="'el-icon-user'"></i>
            <div class="detail">
              <p class="num">{{ liveUser.number }}</p>
              <p class="text">{{ liveUser.title }}</p>
            </div>
          </el-card>
          <el-card
            shadow="hover"
            style="background: #55a6e8; height: 140px"
            :body-style="{ display: 'flex', padding: 0 }"
          >
            <i class="icon" :class="'el-icon-folder-checked'"></i>
            <div class="detail">
              <p class="num">{{ hall.number }}</p>
              <p class="text">{{ hall.title }}</p>
            </div>
          </el-card>
          <el-card
            shadow="hover"
            style="background: #9db0ff; height: 140px"
            :body-style="{ display: 'flex', padding: 0 }"
          >
            <i class="icon" :class="'el-icon-document-checked'"></i>
            <div class="detail">
              <p class="num">{{ reserves.number }}</p>
              <p class="text">{{ reserves.title }}</p>
            </div>
          </el-card>
          <el-card
            shadow="hover"
            style="background: #3ed4af; height: 140px"
            :body-style="{ display: 'flex', padding: 0 }"
          >
            <i class="icon" :class="'el-icon-date'"></i>
            <div class="detail">
              <p class="num">{{ studys.number }}</p>
              <p class="text">{{ studys.title }}</p>
            </div>
          </el-card>
        </div>
      </el-col>

    </el-row>
    <div style="border-radius: 20px;margin-top: 20px;">
      <el-card>
        <h3>欢迎来到智能企业办公日程规划系统</h3>
        <el-calendar v-model="value">
          <template #dateCell="{ data }">
            <div class="calendar-day" @click="gotoSchedule">
              <div class="day-number">{{ data.day.split('-')[2] }}</div>
              <div class="schedule-list" v-if="monthSchedule[data.day] && monthSchedule[data.day].length > 0">
                <div
                  v-for="(item, index) in monthSchedule[data.day]"
                  :key="item.id"
                  class="schedule-item-container"
                  @click="(event) => gotoSchedule(event, item)"
                >
                  <!-- Preview Bar (Animated Height/Opacity) -->
                  <div
                    class="schedule-item-preview"
                    :style="{backgroundColor: getColorfulItemColor(index)}"
                  >
                    <span class="schedule-time">{{ formatTime(item.startTime) }}</span>
                    <span class="schedule-title">{{ item.taskName }}</span>
                  </div>
                  <!-- Detail Box (Animated Max-Height/Opacity) -->
                  <div
                    class="schedule-item-detail"
                    :style="{backgroundColor: getColorfulItemColor(index)}"
                  >
                    <div class="detail-content-wrapper">
                      <p class="detail-title">{{ item.taskName }}</p>
                      <p><strong>开始:</strong> {{ formatFullTime(item.startTime) }}</p>
                      <p><strong>结束:</strong> {{ formatFullTime(item.endTime) }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </el-calendar>
      </el-card>
    </div>

  </el-main>
</template>

<script>
import { homeData, fetchUserMonthScheduleList } from '@/api/api';
export default {
  data() {
    return {
      messageList: [],
      data: {},
      isAdmin: false,
      reserve: {
        count: 100,
        other: 20,
        finished: 80,
        percentage: 0,
      },

      study: {
        count: 100,
        other: 60,
        finished: 40,
        percentage: 0,
      },

      liveUser: {
        title: "用户",
        number: "0",
      },
      hall: {
        title: "资源",
        number: "0",
      },
      reserves: {
        title: "任务",
        number: "0",
      },
      studys: {
        title: "安排",
        number: "0",
      },

      value: new Date(),
      monthSchedule: {},
      // Brighter, more varied color palette
      colorfulColors: [
        '#E53935', // Red
        '#D81B60', // Pink
        '#8E24AA', // Purple
        '#5E35B1', // Deep Purple
        '#3949AB', // Indigo
        '#1E88E5', // Blue
        '#039BE5', // Light Blue
        '#00ACC1', // Cyan
        '#00897B', // Teal
        '#43A047', // Green
        '#7CB342', // Light Green
        '#C0CA33', // Lime
        '#FDD835', // Yellow
        '#FFB300', // Amber
        '#FB8C00', // Orange
        '#F4511E', // Deep Orange
        '#6D4C41', // Brown
        '#757575'  // Grey
      ]
    };
  },

  created() {
    this.getHome();
    this.getMonthSchedule();
  },
  
  watch: {
    value: {
      handler(newVal) {
        this.getMonthSchedule();
      },
      deep: true
    }
  },

  methods: {
    getHome() {
      homeData().then(response => {
        const data = response.data;

        this.liveUser.number = data.userCount;
        this.hall.number = data.resourceCount;
        this.reserves.number = data.taskCount;
        this.studys.number = data.scheduleCount;
      });
    },
    
    getMonthSchedule() {
      const year = this.value.getFullYear();
      const month = this.value.getMonth() + 1;
      const yearMonth = `${year}-${month < 10 ? '0' + month : month}`;
      
      fetchUserMonthScheduleList(yearMonth).then(response => {
        this.monthSchedule = response.data;
      });
    },
    
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      return `${date.getHours()}:${date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()}`;
    },

    formatFullTime(time) {
      if (!time) return '';
      const date = new Date(time);
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    },
    
    getColorfulItemColor(index) {
      return this.colorfulColors[index % this.colorfulColors.length];
    },
    
    gotoSchedule(event, item) {
      // 阻止事件冒泡
      if (event) {
        event.stopPropagation();
      }
      
      // 获取路由列表，检查哪些路由是可访问的
      const routers = this.$router.options.routes.concat(this.$store.getters.addRouters);
      
      // 先尝试查找是否有schedule路由权限
      const hasScheduleRoute = routers.some(route => {
        if (route.children) {
          return route.children.some(child => child.name === 'schedule');
        }
        return false;
      });
      
      // 再尝试查找是否有userSchedule路由权限
      const hasUserScheduleRoute = routers.some(route => {
        if (route.children) {
          return route.children.some(child => child.name === 'userSchedule');
        }
        return false;
      });
      
      // 根据路由权限决定跳转路径
      if (hasScheduleRoute) {
        this.$router.push({ path: '/schedule' });
      } else if (hasUserScheduleRoute) {
        this.$router.push({ path: '/userSchedule' });
      } else {
        // 如果都没有权限，默认尝试跳转到userSchedule，同时提示用户
        this.$message.warning('您可能没有查看日程的权限');
        this.$router.push({ path: '/userSchedule' }).catch(() => {
          this.$router.push({ path: '/' });
        });
      }
    },
    
    handleDetail(id) {
      this.$router.push({ path: `${this.isAdmin?'notice-detail':'noticeDetail'}`, query: { id}});
    }
  },
};
</script>

<style>
.header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
.header .el-card {
  width: 24%;
  border-radius: 20px;
}
.detail {
  margin-left: auto;
  margin-top: 25px;
  text-align: right;
  margin-right: 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  justify-items: center;
}
.detail .num {
  font-size: 40px;
  font-weight: bold;
  color: #fff;
  margin: 0px;
}
.detail .text {
  font-size: 20px;
  color: #fff;
  margin-top: 5px;
}
.icon {
  font-size: 80px;
  width: 80px;
  height: 80px;
  text-align: center;
  line-height: 80px;
  margin-top: 25px;
  margin-left: 20px;
  color: #fff;
}

/* 日历样式 */
.calendar-day {
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative; /* Needed for absolute positioning context */
  overflow: hidden; /* Hide overflow during animation */
  cursor: pointer; /* 添加手型光标 */
  transition: background-color 0.3s;
}

.calendar-day:hover {
  background-color: #f0f8ff; /* 添加悬停背景色 */
}

.day-number {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}
.schedule-list {
  overflow-y: auto;
  flex: 1;
  max-height: 120px; /* Or adjust as needed */
  /* Styling for scrollbar (optional) */
  &::-webkit-scrollbar {
      width: 4px;
  }
  &::-webkit-scrollbar-thumb {
      background: #ccc;
      border-radius: 2px;
  }
}

/* Schedule Item Container */
.schedule-item-container {
  position: relative; /* Context for potential future absolute elements if needed */
  width: 100%;
  margin-bottom: 4px;
  cursor: pointer;
  border-radius: 4px;
  background-color: #f8f8f8; /* Optional: Add a light background */
  overflow: hidden; /* Clip content during animation */
}

/* Preview Bar */
.schedule-item-preview {
  /* position: relative; <-- No longer needed */
  width: 100%;
  height: 22px; /* Initial height */
  padding: 3px 6px;
  color: #fff;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: flex;
  align-items: center;
  border-radius: 4px; /* Radius needed here too */
  box-sizing: border-box;
  opacity: 1;
  transition: opacity 0.2s ease-out, height 0.3s ease-out, padding 0.3s ease-out; /* Animate height/padding */
  z-index: 2;
}

.schedule-item-container:hover .schedule-item-preview {
  opacity: 0;
  height: 0;
  padding-top: 0;
  padding-bottom: 0;
  pointer-events: none;
}

.schedule-time {
  font-weight: bold;
  margin-right: 5px;
}

.schedule-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Detail Box */
.schedule-item-detail {
  /* position: absolute; <-- No longer needed */
  width: 100%;
  box-sizing: border-box;
  color: #fff;
  font-size: 13px;
  max-height: 0; /* Initially collapsed */
  opacity: 0;
  overflow: hidden; /* Hide content when collapsed */
  transition: max-height 0.35s ease-out, opacity 0.25s ease-in 0.1s; /* Animate max-height and opacity */
  border-radius: 4px; /* Match container */
  z-index: 1;
}

.schedule-item-container:hover .schedule-item-detail {
  max-height: 150px; /* Expand to this height (adjust if needed) */
  opacity: 1;
}

/* Wrapper for detail content padding */
.detail-content-wrapper {
    padding: 8px 12px;
}

.schedule-item-detail p {
  margin: 4px 0;
  line-height: 1.4;
  white-space: normal;
}

.schedule-item-detail .detail-title {
  font-weight: bold;
  margin-bottom: 6px;
}

/* 日历默认样式覆盖 */
.el-calendar-table .el-calendar-day {
  height: 150px;
  padding: 6px;
  position: relative;
}
.el-calendar-table td {
  border: 1px solid #ebeef5;
  vertical-align: top;
}
.el-calendar__header {
  padding: 12px 20px;
  border-bottom: 1px solid #ebeef5;
}

/* Hide any potential residual popover styles */
.el-popover.el-popper {
  display: none;
}
</style>
