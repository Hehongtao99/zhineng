<template>
  <div class="app-container">
    <el-card class="filter-container" shadow="hover">
      <div class="filter-header">
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
      </div>
      <div class="filter-content">
        <el-form :inline="true" :model="listQuery" size="small" label-width="120px">
          <el-form-item label="关键字：">
            <el-input v-model="listQuery.searchKey" placeholder="请输入关键字" clearable></el-input>
          </el-form-item>
          <el-form-item label="任务状态：">
            <el-select v-model="listQuery.status" placeholder="请选择任务状态" clearable>
              <el-option label="全部" value=""></el-option>
              <el-option label="待办" :value="1"></el-option>
              <el-option label="已完成" :value="4"></el-option>
              <el-option label="已逾期" :value="5"></el-option>
            </el-select>
          </el-form-item>
          <el-button
            type="primary"
            icon="el-icon-search"
            @click="handleSearchList()"
            size="small"
            class="search-btn">
            查询搜索
          </el-button>
          <el-button
            type="info"
            icon="el-icon-refresh"
            @click="handleResetSearch()"
            size="small">
            重置
          </el-button>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="hover">
      <div class="operate-header">
        <i class="el-icon-date"></i>
        <span>我的日程安排</span>
      </div>
      <div class="operate-actions">
        <el-button size="small" class="btn-sort" @click="handleSmartSort()" type="success">
          <i class="el-icon-sort"></i>智能排序
        </el-button>
        <el-button size="small" class="btn-sort" @click="handleTimeSort()" type="info">
          <i class="el-icon-time"></i>时间排序
        </el-button>
      </div>
    </el-card>

    <!-- 日程时间轴 -->
    <el-card shadow="hover" class="scheduler-container" v-loading="listLoading">
      <div v-if="Object.keys(groupedSchedules).length === 0" class="empty-state">
        <el-empty description="暂无日程数据"></el-empty>
      </div>
      <div v-else class="scheduler-wrapper">
        <div v-for="(schedules, date) in groupedSchedules" :key="date" class="day-group">
          <div class="day-header">
            <div class="day-left">
              <div class="day-number">{{ getDayNumber(date) }}</div>
              <div class="day-weekday">{{ getDayWeekday(date) }}</div>
            </div>
            <div class="day-date">
              <div class="date-tag">{{ getDateTag(date) }}</div>
              <div class="date-full">{{ formatDayDate(date) }}</div>
            </div>
          </div>

          <div class="timeline-container">
            <div class="timeline-track"></div>

            <div v-for="schedule in schedules" :key="schedule.id" 
              class="timeline-event"
              draggable="true"
              @dragstart="handleDragStart($event, schedule)"
              @dragover.prevent="handleDragOver($event, schedule)"
              @drop="handleDrop($event, schedule)"
              @dragend="handleDragEnd">
              <div class="event-time">
                <span>{{ formatTime(schedule.startTime) }}</span>
                <div class="time-marker" :style="{backgroundColor: getTimeColors(formatTime(schedule.startTime)).dotColor}"></div>
              </div>

              <div class="event-connector">
                <div class="connector-dot" 
                  :class="{'conflict-dot': schedule.hasConflict}" 
                  :style="{backgroundColor: schedule._partType === 'afternoon' ? 
                    timeColors.afternoon.dotColor : 
                    getTimeColors(formatTime(schedule.startTime)).dotColor}">
                </div>
                <div class="connector-line"></div>
              </div>

              <div class="event-card"
                :class="{
                  'conflict-card': schedule.hasConflict,
                  'overdue-card': isOverdue(schedule),
                  'completed-card': schedule.status === 4
                }"
                :style="{
                  backgroundColor: !isOverdue(schedule) ? 
                    (schedule._partType === 'afternoon' ? timeColors.afternoon.backgroundColor : getTimeColors(formatTime(schedule.startTime)).backgroundColor) : 
                    '#f5f7fa',
                  borderLeftColor: schedule.hasConflict ? 
                    '#F56C6C' : 
                    (isOverdue(schedule) ? '#dcdfe6' : 
                      (schedule._partType === 'afternoon' ? timeColors.afternoon.borderColor : getTimeColors(formatTime(schedule.startTime)).borderColor))
                }"
                @click="handleClickTaskCard(schedule)">
                <el-tag
                  :type="getStatusTagType(schedule.status)"
                  size="mini"
                  effect="dark"
                  style="position: absolute; top: 10px; left: 10px; z-index: 3;"
                  v-if="schedule.status !== undefined && schedule.status !== null">
                  {{ schedule.status | formatStatus }}
                </el-tag>

                <div class="card-time-range" :style="{color: isOverdue(schedule) ? '#c0c4cc' : '#909399'}">
                  <i class="el-icon-time"></i>
                  <span>{{ formatTime(schedule.startTime) }} - {{ formatTime(schedule.endTime) }}</span>
                </div>

                <div class="card-title" :style="{color: isOverdue(schedule) ? '#a6a9ad' : '#303133', 'padding-left': '70px'}">
                  {{ schedule.taskName }}
                </div>

                <div class="card-actions">
                  <template v-if="schedule.hasConflict">
                    <el-tooltip content="查看冲突详情" placement="top" effect="light">
                      <el-button type="text" class="action-btn conflict-btn" @click="viewConflict(schedule)">
                        <i class="el-icon-warning"></i>
                      </el-button>
                    </el-tooltip>
                  </template>

                  <el-tooltip content="查看详情" placement="top" effect="light">
                    <el-button type="text" class="action-btn view-btn" @click="viewInfo(schedule)">
                      <i class="el-icon-view"></i>
                    </el-button>
                  </el-tooltip>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :current-page.sync="listQuery.pageNum"
        :page-size="listQuery.pageSize"
        :page-sizes="[10,15,20]"
        :total="total">
      </el-pagination>
    </div>

    <el-dialog
      title="任务详情"
      :visible.sync="barcodeDialogVisible"
      width="40%"
      @opened="onDialogOpened">
      <div class="task-info">
        <p><strong>标题：</strong>{{ taskInfo.title }}</p>
        <p><strong>描述：</strong>{{ taskInfo.description }}</p>
        <p><strong>耗时：</strong>{{ taskInfo.timeSpend }} 分钟</p>
        <p><strong>类别：</strong>{{ taskInfo.categoryName }}</p>
        <p><strong>重要性：</strong>{{ taskInfo.importanceName }}</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="barcodeDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="用户详情"
      :visible.sync="infoDialogVisible"
      width="60%">
      <h3>参与用户列表</h3>
      <el-table :data="taskInfo.userList || []" border style="margin-bottom: 20px;">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="150"></el-table-column>
        <el-table-column prop="nickName" label="昵称" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
        <el-table-column prop="phone" label="电话" width="150"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
      </el-table>

      <span slot="footer" class="dialog-footer">
        <el-button @click="infoDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="用户冲突详情"
      :visible.sync="conflictDialogVisible"
      width="60%">
      <div v-for="(item, index) in conflictList" :key="index">
        <el-card class="task-info-card" style="margin-bottom: 20px;">
          <h3>冲突任务{{ index + 1 }}</h3>
          <div>
            <p><strong>任务标题：</strong>{{ item.title }}</p>
            <p><strong>任务编码：</strong>{{ item.code }}</p>
            <p><strong>任务描述：</strong>{{ item.description || '无' }}</p>
            <p><strong>截止时间：</strong>{{ item.deadline }}</p>
          </div>
        </el-card>

        <h3>冲突参与用户列表</h3>
        <el-table :data="item.userList || []" border style="margin-bottom: 20px;">
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="username" label="用户名" width="150"></el-table-column>
          <el-table-column prop="nickName" label="昵称" width="120"></el-table-column>
          <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
          <el-table-column prop="phone" label="电话" width="150"></el-table-column>
          <el-table-column prop="address" label="地址"></el-table-column>
        </el-table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="conflictDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- 添加状态变更对话框 -->
    <el-dialog
      title="变更任务状态"
      :visible.sync="statusChangeVisible"
      width="30%">
      <el-form :model="statusForm" label-width="80px">
        <el-form-item label="当前状态">
          <el-tag :type="getStatusTagType(taskDetail.status)">{{ taskDetail.status | formatStatus }}</el-tag>
        </el-form-item>
        <el-form-item label="新状态">
          <el-select v-model="statusForm.status" placeholder="请选择新状态">
            <el-option :value="1" label="待办"></el-option>
            <el-option :value="4" label="已完成"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="statusChangeVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleStatusChange">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 添加任务详情对话框 -->
    <el-dialog
      title="任务详情"
      :visible.sync="taskDetailVisible"
      width="50%">
      <div class="task-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="任务标题">{{ taskDetail.title }}</el-descriptions-item>
          <el-descriptions-item label="任务编码">{{ taskDetail.code }}</el-descriptions-item>
          <el-descriptions-item label="类别">{{ taskDetail.categoryName }}</el-descriptions-item>
          <el-descriptions-item label="重要性">{{ taskDetail.importanceName }}</el-descriptions-item>
          <el-descriptions-item label="预估时间">{{ taskDetail.timeSpend }} 分钟</el-descriptions-item>
          <el-descriptions-item label="截止时间">{{ taskDetail.deadline | formatDateTime }}</el-descriptions-item>
          <el-descriptions-item label="执行时间" :span="2">
            {{ taskDetail.startTime | formatDateTime }}
            <template v-if="taskDetail.endTime && taskDetail.endTime !== ''">
              至 {{ taskDetail.endTime | formatDateTime }}
            </template>
          </el-descriptions-item>
          <el-descriptions-item label="提醒时间" :span="2">
            <template v-if="taskDetail.reminderTime">
              {{ taskDetail.reminderTime | formatDateTime }}
              <el-button type="text" @click="showReminderDialog">修改提醒</el-button>
            </template>
            <template v-else>
              未设置
              <el-button type="text" @click="showReminderDialog">设置提醒</el-button>
            </template>
          </el-descriptions-item>
          <el-descriptions-item label="状态" :span="2">
            <el-tag :type="getStatusTagType(taskDetail.status)">{{ taskDetail.status | formatStatus }}</el-tag>
            <el-button type="text" @click="showStatusChangeDialog">变更状态</el-button>
          </el-descriptions-item>
          <el-descriptions-item label="任务描述" :span="2">{{ taskDetail.description }}</el-descriptions-item>
        </el-descriptions>

        <div class="task-actions" style="margin-top: 20px; text-align: right;">
          <el-button @click="taskDetailVisible = false">关闭</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 添加提醒时间设置对话框 -->
    <el-dialog
      title="设置提醒时间"
      :visible.sync="reminderDialogVisible"
      width="30%">
      <el-form :model="reminderForm" label-width="100px">
        <el-form-item label="当前执行时间">
          <el-tag>{{ taskDetail.startTime | formatDateTime }}</el-tag>
        </el-form-item>
        <el-form-item label="提醒时间">
          <el-date-picker
            v-model="reminderForm.reminderTime"
            type="datetime"
            placeholder="选择提醒时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="reminderOptions">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="reminderDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSetReminder">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
  import {fetchUserScheduleList, fetchUserScheduleSmartSort, fetchMonthScheduleList, fetchUserMonthScheduleList, createSchedule, updateSchedule, deleteSchedule, fetchAllTaskList, fetchAllTaskListByStatus, fetchTaskInfo, fetchConflictScheduleList, updateTaskStatus, setTaskReminder, adjustScheduleOrder} from '@/api/api';
  import JsBarcode from 'jsbarcode';
  import html2canvas from 'html2canvas';
  import {formatDate} from '@/utils/date';

  const defaultListQuery = {
    pageNum: 1,
    pageSize: 10,
    searchKey: null,
    status: null,
    type: null,
  };
  const defaultAdmin = {
    id: null,
    taskId: null,
    startTime: null,
    endTime: null,
    reminderTime: null,
  };
  export default {
    name: 'userScheduleList',
    data() {
      return {
        listQuery: Object.assign({}, defaultListQuery),
        list: null,
        total: null,
        listLoading: false,
        dialogVisible: false,
        admin: Object.assign({}, defaultAdmin),
        isEdit: false,
        adminRules: {
          taskId: [
            { required: true, message: '请选择日程安排任务', trigger: 'change' }
          ],
          startTime: [
            { required: true, message: '请选择任务执行时间', trigger: 'change' }
          ],
        },
        barcodeDialogVisible: false,
        currentCode: '', // 当前选中的编码

        taskList: [],

        pickerOptions: {
          disabledDate(time) {
            // 禁用当前时间之前的时间
            return time.getTime() < Date.now() - 86400000; // 减去一天是为了避免时区问题
          }
        },

        taskInfo: {},
        infoDialogVisible: false,
        conflictDialogVisible: false,

        conflictList: [],

        // 状态变更相关
        statusChangeVisible: false,
        statusForm: {
          status: 1
        },

        // 提醒时间相关
        reminderDialogVisible: false,
        reminderForm: {
          reminderTime: ''
        },
        reminderOptions: {
          disabledDate(time) {
            // 允许选择当前日期之后的日期
            return time.getTime() < Date.now() - 8.64e7; // 禁用今天以前的日期
          }
        },

        // 修改日程时间相关
        scheduleTimeDialogVisible: false,
        scheduleTimeForm: {
          startTime: '',
          endTime: ''
        },

        // 任务详情相关
        taskDetailVisible: false,
        taskDetail: {},
        currentScheduleId: null,

        // 时间段颜色配置 - 使用更精细的时间段配色
        timeColors: {
          earlyMorning: {
            backgroundColor: '#E3F2FD',
            borderColor: '#90CAF9',
            dotColor: '#2196F3'
          },
          morning: {
            backgroundColor: '#E8F5E9',
            borderColor: '#A5D6A7',
            dotColor: '#4CAF50'
          },
          noon: {
            backgroundColor: '#FFF8E1',
            borderColor: '#FFE082',
            dotColor: '#FFC107'
          },
          afternoon: {
            backgroundColor: '#FBE9E7',
            borderColor: '#FFAB91',
            dotColor: '#FF5722'
          },
          evening: {
            backgroundColor: '#EDE7F6',
            borderColor: '#B39DDB',
            dotColor: '#673AB7'
          },
          night: {
            backgroundColor: '#E1F5FE',
            borderColor: '#81D4FA',
            dotColor: '#03A9F4'
          }
        },
        dragState: {
          draggingSchedule: null,
          dropTarget: null,
          originalPosition: null
        }
      }
    },
    computed: {
      // 按日期分组日程安排
      groupedSchedules() {
        if (!this.list) return {};

        // 创建基于日期的分组
        const groups = {};
        this.list.forEach(schedule => {
          const date = this.getDateStr(schedule.startTime);
          if (!groups[date]) {
            groups[date] = [];
          }
          groups[date].push(schedule);
        });

        // 按日期排序
        const sortedDates = Object.keys(groups).sort();
        const result = {};

        sortedDates.forEach(date => {
          // 按开始时间排序每一天的日程
          const sorted = groups[date].sort((a, b) => {
            return new Date(a.startTime) - new Date(b.startTime);
          });
          result[date] = sorted;
        });

        return result;
      }
    },
    filters: {
      formatDateTime(time) {
        if (time == null || time === '') {
          return 'N/A';
        }
        let date = new Date(time);
        return formatDate(date, 'yyyy-MM-dd HH:mm:ss')
      },
      formatStatus(status) {
        if (status == 1) {
          return '待办';
        }
        if (status == 4) {
          return '已完成';
        }
        if (status == 5) {
          return '已逾期';
        }
        return '未知';
      }
    },
    created() {
      this.getList();
    },
    methods: {
      // 获取日程项的状态标签类型
      getStatusTagType(status) {
        switch(status) {
          case 1: return 'info';    // 待办
          case 4: return 'success'; // 已完成
          case 5: return 'danger';  // 已逾期
          default: return 'info';
        }
      },

      // 智能排序方法
      handleSmartSort() {
        this.listLoading = true;

        // 获取当前年月，格式为YYYY-MM
        const now = new Date();
        const yearMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;

        // 使用智能排序接口获取排序后的用户日程
        fetchUserScheduleSmartSort(this.listQuery).then(response => {
          this.listLoading = false;

          // 将智能排序后的数据设置为当前列表
          this.list = response.data.list;
          this.total = response.data.total;

          this.$message({
            message: '已根据时间紧急性和任务重要性进行智能排序',
            type: 'success'
          });
        });
      },
      
      // 恢复时间排序方法
      handleTimeSort() {
        this.listLoading = true;
        this.getList();
        this.$message({
          message: '已恢复默认时间排序',
          type: 'success'
        });
      },

      // 判断日程是否已过结束时间
      isOverdue(schedule) {
        // 如果status已经是5(已逾期)则直接返回true
        if (schedule.status === 5) {
          return true;
        }
        // 检查 endTime 是否存在，是否早于当前时间，并且任务状态不是已完成 (4)
        return schedule.endTime && new Date(schedule.endTime) < new Date() && schedule.status !== 4;
      },

      // 获取日期标签(今天/明天等)
      getDateTag(dateStr) {
        const date = new Date(dateStr);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        const tomorrow = new Date(today);
        tomorrow.setDate(tomorrow.getDate() + 1);

        const yesterday = new Date(today);
        yesterday.setDate(yesterday.getDate() - 1);

        if (date.getTime() === today.getTime()) {
          return '今天';
        } else if (date.getTime() === tomorrow.getTime()) {
          return '明天';
        } else if (date.getTime() === yesterday.getTime()) {
          return '昨天';
        } else {
          // 返回星期几
          const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
          return weekdays[date.getDay()];
        }
      },

      // 获取日期中的日
      getDayNumber(dateStr) {
        return new Date(dateStr).getDate();
      },

      // 获取日期的星期
      getDayWeekday(dateStr) {
        const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
        return weekdays[new Date(dateStr).getDay()];
      },

      // 格式化日期为YYYY-MM-DD格式
      getDateStr(dateStr) {
        return dateStr.split(' ')[0];
      },

      // 格式化展示日期
      formatDayDate(dateStr) {
        const date = new Date(dateStr);
        return formatDate(date, 'yyyy年MM月dd日');
      },

      // 格式化时间为HH:MM格式
      formatTime(dateTimeStr) {
        if (!dateTimeStr) return '';
        const parts = dateTimeStr.split(' ');
        if (parts.length < 2 || !parts[1]) return '';
        return parts[1].substring(0, 5);
      },

      // 获取时间段的颜色配置
      getTimeColors(timeStr) {
        if (!timeStr) return this.timeColors.morning;

        const hour = parseInt(timeStr.split(':')[0]);

        if (hour >= 5 && hour < 8) {
          return this.timeColors.earlyMorning; // 清晨 5:00-7:59
        } else if (hour >= 8 && hour < 12) {
          return this.timeColors.morning; // 上午 8:00-11:59
        } else if (hour >= 12 && hour < 14) {
          return this.timeColors.noon; // 中午 12:00-13:59
        } else if (hour >= 14 && hour < 18) {
          return this.timeColors.afternoon; // 下午 14:00-17:59
        } else if (hour >= 18 && hour < 22) {
          return this.timeColors.evening; // 晚上 18:00-21:59
        } else {
          return this.timeColors.night; // 深夜 22:00-4:59
        }
      },

      async getTaskList(params) {
        const response = await fetchAllTaskListByStatus(params)
        this.taskList = response.data;
      },
      async getAllTaskList() {
        const response = await fetchAllTaskList()
        this.taskList = response.data;
      },
      async getTaskInfo(id) {
        const response = await fetchTaskInfo(id);
        this.taskInfo = response.data;
      },
      async viewBarcode(row) {
        this.currentCode = row.taskCode;
        await this.getTaskInfo(row.taskId);
        this.barcodeDialogVisible = true;
      },
      async viewInfo(row) {
        this.currentScheduleId = row.id;
        this.listLoading = true;
        fetchTaskInfo(row.taskId).then(response => {
          this.taskDetail = response.data;
          this.taskDetailVisible = true;
          this.listLoading = false;
        }).catch(() => {
          this.listLoading = false;
        });
      },
      async viewConflict(row) {
        const res = await fetchConflictScheduleList({id:row.id,scheduleIds: row.conflictScheduleIds || ''});
        this.conflictList = res.data;
        this.conflictDialogVisible = true;
      },
      onDialogOpened() {
        if (this.$refs.barcodePreview) {
          JsBarcode(this.$refs.barcodePreview, this.currentCode, {
            format: 'CODE128',
            width: 2,
            height: 100,
            displayValue: true,
          });
        }
      },
      exportBarcode() {
        const barcodeElement = this.$refs.barcodePreview;

        if (!barcodeElement) {
          console.error('条码元素未找到');
          return;
        }

        html2canvas(barcodeElement).then((canvas) => {
          if (!canvas) {
            console.error('未能生成 canvas');
            return;
          }

          const link = document.createElement('a');
          link.href = canvas.toDataURL('image/png');
          link.download = `${this.currentCode}.png`;
          link.click();
        }).catch((error) => {
          console.error('html2canvas 错误:', error);
        });
      },
      handleResetSearch() {
          this.listQuery = Object.assign({}, defaultListQuery);
          this.getList()
        },
      handleSearchList() {
        this.listQuery.pageNum = 1;
        this.getList();
      },
      handleSizeChange(val) {
        this.listQuery.pageNum = 1;
        this.listQuery.pageSize = val;
        this.getList();
      },
      handleCurrentChange(val) {
        this.listQuery.pageNum = val;
        this.getList();
      },
      getList() {
        this.listLoading = true;
        const params = {
          pageNum: this.listQuery.pageNum,
          pageSize: this.listQuery.pageSize
        };

        if (this.listQuery.searchKey) {
          params.searchKey = this.listQuery.searchKey;
        }
        
        if (this.listQuery.status !== null && this.listQuery.status !== '') {
          params.status = this.listQuery.status;
        }

        fetchUserScheduleList(params).then(response => {
          this.listLoading = false;
          // 处理跨越午休时间的日程，拆分成两部分
          const originalList = response.data.list;
          const processedList = this.processSchedules(originalList);
          this.list = processedList;
          this.total = response.data.total;
        });
      },
      // 处理跨越午休时间的日程
      processSchedules(schedules) {
        if (!schedules || schedules.length === 0) {
          return [];
        }

        const result = [];
        
        schedules.forEach(schedule => {
          // 克隆原始日程，避免直接修改
          const scheduleClone = JSON.parse(JSON.stringify(schedule));
          
          if (!scheduleClone.startTime || !scheduleClone.endTime) {
            // 如果没有开始或结束时间，直接添加
            result.push(scheduleClone);
            return;
          }
          
          const startDateTime = new Date(scheduleClone.startTime);
          const endDateTime = new Date(scheduleClone.endTime);
          
          // 构建当天的午休开始和结束时间
          const datePart = this.getDatePart(scheduleClone.startTime);
          const lunchBreakStart = new Date(datePart + ' 12:00:00');
          const lunchBreakEnd = new Date(datePart + ' 14:00:00');
          
          // 为日程添加时段标记，但不再分割
          if (startDateTime < lunchBreakStart) {
            // 如果开始时间在上午，标记为morning
            scheduleClone._partType = 'morning';
          } else if (startDateTime >= lunchBreakEnd) {
            // 如果开始时间在下午，标记为afternoon
            scheduleClone._partType = 'afternoon';
          } else {
            // 如果开始时间在午休时间内，也标记为afternoon
            scheduleClone._partType = 'afternoon';
          }
          
          result.push(scheduleClone);
        });
        
        // 按开始时间排序
        result.sort((a, b) => {
          return new Date(a.startTime) - new Date(b.startTime);
        });
        
        return result;
      },
      
      // 将Date对象格式化为yyyy-MM-dd HH:mm:ss格式
      formatDateTime(date) {
        if (!date) return '';
        // 使用模板字符串手动构建日期时间格式
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      },

      // 从日期时间字符串中提取日期部分 (YYYY-MM-DD)
      getDatePart(dateTimeStr) {
        if (!dateTimeStr) return '';
        return dateTimeStr.split(' ')[0];
      },

      // 查看任务详情
      handleClickTaskCard(schedule) {
        this.currentScheduleId = schedule.id;
        this.listLoading = true;
        fetchTaskInfo(schedule.taskId).then(response => {
          this.taskDetail = response.data;
          this.taskDetailVisible = true;
          this.listLoading = false;
        }).catch(() => {
          this.listLoading = false;
        });
      },
      
      // 显示状态变更对话框
      showStatusChangeDialog() {
        this.statusForm.status = this.taskDetail.status;
        this.statusChangeVisible = true;
      },
      
      // 处理状态变更
      handleStatusChange() {
        this.listLoading = true;
        updateTaskStatus(this.taskDetail.id, this.statusForm.status).then(() => {
          this.statusChangeVisible = false;
          this.taskDetail.status = this.statusForm.status;
          this.$message.success('状态更新成功');
          this.getList(); // 刷新列表
          this.listLoading = false;
        }).catch(() => {
          this.listLoading = false;
        });
      },

      // 开始拖动
      handleDragStart(event, schedule) {
        this.dragState.draggingSchedule = schedule;
        this.dragState.originalPosition = {
          startTime: schedule.startTime,
          endTime: schedule.endTime
        };
        
        // 设置拖动效果
        event.dataTransfer.effectAllowed = 'move';
        event.target.classList.add('dragging');
      },

      // 拖动经过其他元素
      handleDragOver(event, schedule) {
        if (this.dragState.draggingSchedule && this.dragState.draggingSchedule.id !== schedule.id) {
          event.preventDefault();
          event.dataTransfer.dropEffect = 'move';
        }
      },

      // 放下元素 - 更新拖拽功能，仅调整日程顺序，不修改执行时间
      async handleDrop(event, targetSchedule) {
        event.preventDefault();
        
        if (!this.dragState.draggingSchedule || this.dragState.draggingSchedule.id === targetSchedule.id) {
          return;
        }

        const draggingSchedule = this.dragState.draggingSchedule;
        const date = this.getDateStr(targetSchedule.startTime);
        
        try {
          // 调用后端接口调整日程顺序
          const response = await adjustScheduleOrder({
            scheduleId: draggingSchedule.id,
            targetTime: targetSchedule.startTime,
            date: date
          });

          if (response.data) {
            this.$message.success('日程调整成功');
            // 重新加载日程列表
            this.getList();
          } else {
            this.$message.error(response.message || '日程调整失败');
            // 恢复原始位置
            draggingSchedule.startTime = this.dragState.originalPosition.startTime;
            draggingSchedule.endTime = this.dragState.originalPosition.endTime;
          }
        } catch (error) {
          console.error('调整日程顺序失败:', error);
          this.$message.error('调整日程顺序失败，请重试');
          // 恢复原始位置
          draggingSchedule.startTime = this.dragState.originalPosition.startTime;
          draggingSchedule.endTime = this.dragState.originalPosition.endTime;
        }
      },

      // 拖动结束
      handleDragEnd(event) {
        event.target.classList.remove('dragging');
        this.dragState.draggingSchedule = null;
        this.dragState.originalPosition = null;
      },

      // 显示提醒设置对话框
      showReminderDialog() {
        this.reminderForm.reminderTime = this.taskDetail.reminderTime || '';
        this.reminderDialogVisible = true;
      },
      
      // 处理设置提醒
      handleSetReminder() {
        this.listLoading = true;
        setTaskReminder(this.taskDetail.id, this.reminderForm.reminderTime).then(() => {
          this.reminderDialogVisible = false;
          this.taskDetail.reminderTime = this.reminderForm.reminderTime;
          this.$message.success('提醒时间设置成功');
          this.getList(); // 刷新列表
          this.listLoading = false;
        }).catch(() => {
          this.listLoading = false;
        });
      }
    }
  }
</script>
<style>
/* 基础样式 */
.app-container {
  padding: 20px;
  background-color: #F5F7FA;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 300px;
  height: 178px;
  display: block;
}

#barcode-preview {
  width: 500px;
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.task-info {
  text-align: left;
  font-size: 14px;
  line-height: 1.6;
}

.task-info p {
  margin: 5px 0;
}

.task-info strong {
  color: #333;
}

/* 日程树样式 */
.filter-container, .operate-container, .scheduler-container {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  border: none;
}

.filter-header, .operate-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 15px;
  border-bottom: 1px solid #EBEEF5;
  padding-bottom: 15px;
}

.filter-header i, .operate-header i {
  margin-right: 8px;
  color: #409EFF;
  font-size: 18px;
}

.filter-content {
  margin-top: 15px;
}

.search-btn {
  background: #409EFF;
  border-color: #409EFF;
  border-radius: 4px;
  margin-left: 10px;
  padding: 8px 15px;
}

.scheduler-container {
  margin-bottom: 20px;
  min-height: 300px;
  padding: 0;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.scheduler-wrapper {
  padding: 20px;
}

.day-group {
  margin-bottom: 30px;
  border-bottom: 1px solid #EBEEF5;
  padding-bottom: 20px;
}

.day-group:last-child {
  margin-bottom: 0;
  border-bottom: none;
}

.day-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 20px;
  background-color: #F2F6FC;
  border-left: 4px solid #409EFF;
}

.day-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  min-width: 60px;
  padding: 8px;
  background: linear-gradient(135deg, #409EFF, #64B5F6);
  color: white;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.25);
}

.day-number {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
  line-height: 1;
}

.day-weekday {
  font-size: 14px;
  color: #fff;
  margin-top: 4px;
  font-weight: 500;
}

.day-date {
  display: flex;
  align-items: center;
}

.date-tag {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  background-color: #409EFF;
  padding: 5px 10px;
  border-radius: 4px;
  margin-right: 10px;
}

.date-full {
  font-size: 15px;
  color: #606266;
}

.timeline-container {
  position: relative;
  padding-left: 100px;
  margin-top: 20px;
  margin-left: 20px;
  margin-right: 20px;
}

.timeline-track {
  position: absolute;
  left: 70px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg,
    #e8eaec 0%,
    #2196F3 16%,
    #4CAF50 32%,
    #FFC107 48%,
    #FF5722 64%,
    #673AB7 80%,
    #03A9F4 100%);
  z-index: 1;
}

.timeline-event {
  position: relative;
  display: flex;
  margin-bottom: 25px;
  user-select: none; /* 防止拖动时选中文本 */
}

.timeline-event.dragging {
  opacity: 0.5;
  cursor: move;
}

.timeline-event.drag-over {
  border: 2px dashed #409EFF;
}

.event-time {
  position: absolute;
  left: -90px;
  top: 0;
  width: 60px;
  text-align: right;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  line-height: 24px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.time-marker {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-left: 8px;
}

.event-connector {
  position: absolute;
  left: -30px;
  top: 0;
  width: 30px;
  height: 24px;
  display: flex;
  align-items: center;
}

.connector-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  z-index: 2;
  box-shadow: 0 0 0 4px rgba(255, 255, 255, 0.6);
  transition: all 0.3s ease;
}

.conflict-dot {
  background-color: #F56C6C !important;
  box-shadow: 0 0 0 4px rgba(245, 108, 108, 0.2);
}

.connector-line {
  flex: 1;
  height: 2px;
  background-color: #dcdfe6;
}

.event-card {
  flex: 1;
  border-radius: 10px;
  padding: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border-left: 5px solid;
  transition: all 0.3s ease;
  cursor: pointer;
}

.event-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  filter: brightness(1.02);
}

.conflict-card {
  border-left-color: #F56C6C !important;
}

.overdue-card {
  opacity: 0.7;
  filter: grayscale(50%);
}

.overdue-card:hover {
  filter: grayscale(30%);
  opacity: 0.85;
}

/* 添加已完成任务的样式 */
.completed-card {
  opacity: 0.7;
  filter: grayscale(50%);
}

.completed-card:hover {
  filter: grayscale(30%);
  opacity: 0.85;
}

/* 确保冲突卡片样式优先级高于已完成和已逾期 */
.conflict-card {
  border-left-color: #F56C6C !important;
}

/* 添加额外样式以增强冲突状态的可见性，无论任务状态如何 */
.conflict-card::before {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 0 12px 12px 0;
  border-color: transparent #F56C6C transparent transparent;
}

.card-time-range {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.card-time-range i {
  margin-right: 5px;
  font-size: 14px;
}

.card-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 12px;
  line-height: 1.4;
  padding-left: 70px;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.action-btn {
  font-size: 16px;
  padding: 2px 5px;
  color: #909399;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: scale(1.1);
}

.conflict-btn {
  color: #F56C6C;
}

.view-btn:hover {
  color: #409EFF;
}

.edit-btn:hover {
  color: #E6A23C;
}

/* 分页样式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .timeline-container {
    padding-left: 70px;
  }

  .timeline-track {
    left: 50px;
  }

  .event-time {
    left: -65px;
    font-size: 12px;
  }

  .event-connector {
    left: -20px;
    width: 20px;
  }

  .card-title {
    font-size: 14px;
  }
}
</style>
