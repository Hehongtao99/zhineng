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
          <el-form-item label="选择用户：">
            <el-select v-model="listQuery.userId" placeholder="请选择用户" clearable @change="handleUserChange">
              <el-option label="全部" value=""></el-option>
              <el-option v-for="item in userList" :key="item.id" :label="item.nickName || item.username" :value="item.id"></el-option>
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
        <span>日程安排</span>
      </div>
      <div class="operate-actions">
      </div>
    </el-card>

    <!-- 优雅的日程时间轴 -->
    <el-card shadow="hover" class="scheduler-container" v-loading="listLoading">
      <div v-if="Object.keys(groupedSchedules).length === 0" class="empty-state">
        <el-empty description="暂无日程数据">
        </el-empty>
      </div>
      <div v-else-if="!userGroupedData" class="scheduler-wrapper">
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
                <div class="time-marker" :style="{backgroundColor: getTimeSlotColors(formatTime(schedule.startTime)).dotColor}"></div>
              </div>

              <div class="event-connector">
                <div class="connector-dot" :class="{'conflict-dot': schedule.hasConflict}" :style="{backgroundColor: getTimeSlotColors(formatTime(schedule.startTime)).dotColor}"></div>
                <div class="connector-line"></div>
              </div>

              <div class="event-card"
                :class="{
                  'conflict-card': schedule.hasConflict,
                  'overdue-card': isOverdue(schedule),
                  'completed-card': schedule.status === 4
                }"
                :style="{
                  backgroundColor: !isOverdue(schedule) ? getTimeSlotColors(formatTime(schedule.startTime)).backgroundColor : '#f5f7fa',
                  borderLeftColor: schedule.hasConflict ? '#F56C6C' : (isOverdue(schedule) ? '#dcdfe6' : getTimeSlotColors(formatTime(schedule.startTime)).borderColor)
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

                  <el-tooltip content="编辑日程" placement="top" effect="light">
                    <el-button type="text" class="action-btn edit-btn" @click="handleUpdate(date, schedule)">
                      <i class="el-icon-edit"></i>
                    </el-button>
                  </el-tooltip>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 用户分组视图（仅在选择"全部"时显示） -->
      <div v-else class="scheduler-wrapper">
        <div v-for="(schedules, userKey) in groupedSchedules" :key="userKey" class="day-group">
          <div class="day-header">
            <div class="day-left">
              <div class="day-number">
                <i class="el-icon-s-order"></i>
              </div>
            </div>
            <div class="day-date">
              <div class="date-tag">{{ userKey }}</div>
            </div>
          </div>

          <div class="timeline-container">
            <div class="timeline-track"></div>

            <div v-for="(schedule, index) in schedules" :key="schedule.id" 
              class="timeline-event">
              <div class="event-time">
                <span>{{ index + 1 }}</span>
                <div class="time-marker" :style="{backgroundColor: '#409EFF'}"></div>
              </div>

              <div class="event-connector">
                <div class="connector-dot" :style="{backgroundColor: '#409EFF'}"></div>
                <div class="connector-line"></div>
              </div>

              <div class="event-card"
                :style="{
                  backgroundColor: '#f0f9eb',
                  borderLeftColor: '#67c23a'
                }"
                @click="viewInfo(schedule)">

                <div class="card-time-range" style="color: #909399">
                  <i class="el-icon-user"></i>
                  <span>{{ schedule.nickname || schedule.username }}</span>
                </div>

                <div class="card-title" style="color: #303133">
                  {{ schedule.taskName }}
                </div>

                <div class="card-actions">
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

    <!-- 对话框部分保持不变 -->
    <el-dialog
      :title="isEdit?'编辑日程安排':'创建日程安排'"
      :visible.sync="dialogVisible"
      width="40%">
      <el-form :model="admin"
              ref="adminForm"
              label-width="100px"
              size="small"
              :rules="adminRules">
        <el-form-item label="任务：" prop="taskId">
          <el-select v-if="isEdit" disabled v-model="admin.taskId" filterable placeholder="请选择任务" style="width: 250px">
            <el-option
              v-for="(item, index) in taskList"
              :label="`${item.title}`"
              :key="index"
              :value="item.id">
            </el-option>
          </el-select>
          <el-select v-else v-model="admin.taskId" filterable placeholder="请选择任务" style="width: 250px">
            <el-option
              v-for="(item, index) in taskList"
              :label="`${item.title}`"
              :key="index"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="执行时间：" prop="startTime">
          <el-date-picker
            style="width: 250px"
            v-model="admin.startTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="pickerOptions"
            placeholder="选择执行时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间：" prop="endTime">
          <el-date-picker
            style="width: 250px"
            v-model="admin.endTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="pickerOptions"
            placeholder="选择结束时间">
          </el-date-picker>
          <el-tag size="mini" type="info">如不选择，系统将根据任务预估时间自动计算</el-tag>
        </el-form-item>
        <el-form-item label="提醒时间：" prop="reminderTime">
          <el-date-picker
            style="width: 250px"
            v-model="admin.reminderTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="pickerOptions"
            placeholder="选择提醒时间">
          </el-date-picker>
          <el-tag size="mini" type="info">任务将在此时间点发送提醒</el-tag>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleDialogConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>

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
          <div class="conflict-type-tag" style="margin-bottom: 10px;">
            <el-tag v-if="item.conflictType === 'deadline'" type="danger">截止时间冲突</el-tag>
            <el-tag v-else-if="item.conflictType === 'schedule'" type="warning">日程时间冲突</el-tag>
            <el-tag v-else type="info">其他冲突</el-tag>
          </div>
          <h3>冲突任务{{ index + 1 }}</h3>
          <div>
            <p><strong>任务标题：</strong>{{ item.title }}</p>
            <p><strong>任务编码：</strong>{{ item.code }}</p>
            <p><strong>任务描述：</strong>{{ item.description || '无' }}</p>
            <p><strong>截止时间：</strong>{{ item.deadline }}</p>
            <p v-if="item.conflictType === 'deadline'"><strong>冲突原因：</strong>日程结束时间({{ formatDateTime(item.scheduleEndTime) }})晚于任务截止时间({{ formatDateTime(item.deadline) }})</p>
            <p v-else-if="item.conflictType === 'schedule'"><strong>冲突原因：</strong>日程时间({{ formatDateTime(item.scheduleStartTime) }} - {{ formatDateTime(item.scheduleEndTime) }})与其他日程重叠</p>
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

    <!-- 任务详情对话框 -->
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
          <el-descriptions-item label="状态" :span="2">
            <el-tag :type="getStatusTagType(taskDetail.status)">{{ taskDetail.status | formatStatus }}</el-tag>
            <el-button type="text" @click="showStatusChangeDialog">变更状态</el-button>
          </el-descriptions-item>
          <el-descriptions-item label="提醒时间" :span="2">
            <span v-if="taskDetail.reminderTime">{{ taskDetail.reminderTime | formatDateTime }}</span>
            <span v-else>未设置</span>
            <el-button type="text" @click="showReminderDialog">{{ taskDetail.reminderTime ? '修改提醒' : '设置提醒' }}</el-button>
          </el-descriptions-item>
          <el-descriptions-item label="任务描述" :span="2">{{ taskDetail.description }}</el-descriptions-item>
        </el-descriptions>

        <div class="task-actions" style="margin-top: 20px; text-align: right;">
          
          <el-button @click="taskDetailVisible = false">关闭</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 状态变更对话框 -->
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

    <!-- 提醒时间设置对话框 -->
    <el-dialog
      title="设置提醒时间"
      :visible.sync="reminderDialogVisible"
      width="30%">
      <el-form label-width="80px">
        <el-form-item label="提醒时间">
          <el-date-picker
            v-model="reminderTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择提醒时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="reminderDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSetReminder">确 定</el-button>
        <el-button v-if="taskDetail.reminderTime" type="danger" @click="handleCancelReminder">取消提醒</el-button>
      </span>
    </el-dialog>

    <!-- 修改执行时间对话框 -->
    <el-dialog
      title="修改执行时间"
      :visible.sync="scheduleTimeDialogVisible"
      width="30%">
      <el-form label-width="80px">
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="scheduleTimeForm.startTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="scheduleTimeForm.endTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择结束时间">
          </el-date-picker>
          <div><el-tag size="mini">不填写将根据任务预估时长自动计算</el-tag></div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="scheduleTimeDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleScheduleTimeUpdate">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
  import {fetchScheduleList, fetchScheduleSmartSort, createSchedule,updateSchedule,deleteSchedule,fetchAllTaskList,fetchAllTaskListByStatus,fetchTaskInfo,fetchConflictScheduleList,updateTaskStatus,setTaskReminder, adjustScheduleOrder, fetchUserList, fetchAllUsersSchedulesByWeight} from '@/api/api';
  import JsBarcode from 'jsbarcode';
  import html2canvas from 'html2canvas';
  import {formatDate} from '@/utils/date';

  const defaultListQuery = {
    pageNum: 1,
    pageSize: 10,
    searchKey: null,
    status: null,
    type: null,
    userId: ""
  };
  const defaultAdmin = {
    id: null,
    taskId: null,
    startTime: null,
    endTime: null,
    reminderTime: null,
  };
  export default {
    name: 'adminList',
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
        userList: [], // 用户列表

        // 任务详情相关
        taskDetailVisible: false,
        taskDetail: {},
        currentScheduleId: null,

        // 状态变更相关
        statusChangeVisible: false,
        statusForm: {
          status: 1
        },

        // 提醒时间相关
        reminderDialogVisible: false,
        reminderTime: '',

        // 修改日程时间相关
        scheduleTimeDialogVisible: false,
        scheduleTimeForm: {
          startTime: '',
          endTime: ''
        },

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

        // 增强时间段颜色配置
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
        },
        userGroupedData: null
      }
    },
    computed: {
      // 按日期分组日程安排
      groupedSchedules() {
        // 如果有用户分组数据，直接返回
        if (this.userGroupedData) {
          return this.userGroupedData;
        }
        
        if (!this.list) return {};

        // 创建基于日期的分组
        const groups = {};
        this.list.forEach(schedule => {
          const date = this.getDateStr(schedule.startTime);
          if (!date) return; // 跳过没有有效日期的项
          
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
        return formatDate(date, 'yyyy-MM-dd HH:mm:ss');
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
      this.getUserList(); // 获取用户列表
      this.handleUserChange(""); // 默认加载全部用户日程
    },
    methods: {
      // 格式化日期时间
      formatDateTime(time) {
        if (time == null || time === '') {
          return 'N/A';
        }
        let date = new Date(time);
        return formatDate(date, 'yyyy-MM-dd HH:mm:ss');
      },
      
      // 修改：判断日程是否已过结束时间
      isOverdue(schedule) {
        // 如果status已经是5(已逾期)则直接返回true
        if (schedule.status === 5) {
          return true;
        }
        // 检查 endTime 是否存在，是否早于当前时间，并且任务状态不是已完成 (4)
        return schedule.endTime && new Date(schedule.endTime) < new Date() && schedule.status !== 4;
      },

      // 智能排序方法
      handleSmartSort() {
        this.listLoading = true;
        const params = {
          pageNum: this.listQuery.pageNum,
          pageSize: this.listQuery.pageSize,
          searchKey: this.listQuery.searchKey,
          userId: this.listQuery.userId
        };

        fetchScheduleSmartSort(params).then(response => {
          this.list = response.data.list;
          this.total = response.data.total;
          this.listLoading = false;
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
      // 格式化日期为YYYY-MM-DD格式
      getDateStr(dateStr) {
        if (!dateStr) return ''; // 添加null检查
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
        await this.getTaskInfo(row.taskId);
        this.infoDialogVisible = true;
      },
      async viewConflict(row) {
        const res = await fetchConflictScheduleList({id:row.id,scheduleIds: row.conflictScheduleIds || ''});
        this.conflictList = res.data;
        this.conflictDialogVisible = true;
      },
      onDialogOpened() {
        // 确保弹窗打开后才生成条码
        JsBarcode(this.$refs.barcodePreview, this.currentCode, {
          format: 'CODE128',
          width: 2,
          height: 100,
          displayValue: true,
        });
      },
      exportBarcode() {
        const barcodeElement = this.$refs.barcodePreview;

        if (!barcodeElement) {
          console.error('条码元素未找到');
          return;
        }

        console.log('捕获条码元素:', barcodeElement);

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
          this.getList();
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
      async handleAdd() {
        this.$message({
          message: '创建日程功能已被禁用',
          type: 'warning'
        });
      },
      handleDelete(index, row) {
        this.$confirm('是否要删除该日程安排?删除后将无法撤销', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deleteSchedule(row.id).then(response => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.getList();
          });
        });
      },
      async handleUpdate(index, row) {
        await this.getAllTaskList();
        this.dialogVisible = true;
        this.isEdit = true;
        this.admin = Object.assign({},row);
      },
      handleDialogConfirm() {
        this.$refs.adminForm.validate(valid => {
          if (valid) {
            this.$confirm('是否要确认?', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              if (this.isEdit) {
                updateSchedule(this.admin).then(response => {
                  this.$message({
                    message: '修改成功！',
                    type: 'success'
                  });
                  this.dialogVisible = false;
                  this.getList();
                })
              } else {
                createSchedule(this.admin).then(response => {
                  this.$message({
                    message: '创建成功！',
                    type: 'success'
                  });
                  this.dialogVisible = false;
                  this.getList();
                })
              }
            })
          }
        });
      },
      getList() {
        // 如果是空用户ID，表示查看全部用户，跳过此方法
        if (this.listQuery.userId === "") {
          return;
        }
        
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
        
        if (this.listQuery.userId !== null && this.listQuery.userId !== '') {
          params.userId = this.listQuery.userId;
        }

        fetchScheduleList(params).then(response => {
          this.listLoading = false;
          this.list = response.data.list;
          this.total = response.data.total;
        });
      },
      // 获取日程项的颜色 - 使用更精细的时间段划分
      getTimeSlotColors(timeStr) {
        if (!timeStr) return this.timeColors.morning;

        const hour = parseInt(timeStr.split(':')[0], 10);

        if (hour >= 5 && hour < 8) {
          return this.timeColors.earlyMorning; // 早晨 5:00-7:59
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

      // 获取每周的第几天，用于变化颜色
      getDayOfWeek(dateStr) {
        const date = new Date(dateStr);
        return date.getDay(); // 0-6，表示周日到周六
      },
      // 获取日期中的天数
      getDayNumber(dateStr) {
        const date = new Date(dateStr);
        return date.getDate();
      },

      // 获取日期的星期表示
      getDayWeekday(dateStr) {
        const date = new Date(dateStr);
        const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
        return weekdays[date.getDay()];
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

      // 获取状态标签类型
      getStatusTagType(status) {
        switch (status) {
          case 1: return 'info';    // 待办
          case 4: return 'success'; // 已完成
          case 5: return 'danger';  // 已逾期
          default: return 'info';
        }
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

      // 显示提醒设置对话框
      showReminderDialog() {
        this.reminderTime = this.taskDetail.reminderTime;
        this.reminderDialogVisible = true;
      },

      // 设置提醒时间
      handleSetReminder() {
        this.listLoading = true;
        setTaskReminder(this.taskDetail.id, this.reminderTime).then(() => {
          this.reminderDialogVisible = false;
          this.taskDetail.reminderTime = this.reminderTime;
          this.$message.success('提醒时间设置成功');
          this.listLoading = false;
        }).catch(() => {
          this.listLoading = false;
        });
      },

      // 取消提醒
      handleCancelReminder() {
        this.listLoading = true;
        setTaskReminder(this.taskDetail.id, null).then(() => {
          this.reminderDialogVisible = false;
          this.taskDetail.reminderTime = null;
          this.$message.success('已取消提醒');
          this.listLoading = false;
        }).catch(() => {
          this.listLoading = false;
        });
      },

      // 显示修改执行时间对话框
      handleUpdateScheduleTime() {
        // 获取当前日程安排的时间
        const schedule = this.list.find(item => item.id === this.currentScheduleId);
        if (schedule) {
          this.scheduleTimeForm.startTime = schedule.startTime;
          this.scheduleTimeForm.endTime = schedule.endTime;
          this.scheduleTimeDialogVisible = true;
        }
      },

      // 更新日程时间
      handleScheduleTimeUpdate() {
        this.listLoading = true;
        const scheduleData = {
          id: this.currentScheduleId,
          startTime: this.scheduleTimeForm.startTime,
          endTime: this.scheduleTimeForm.endTime
        };

        // 调用正确的更新日程 API 方法
        updateSchedule(scheduleData).then(() => {
          this.$message.success('执行时间更新成功');
          this.scheduleTimeDialogVisible = false; // 关闭对话框
          this.getList(); // 刷新列表
          this.listLoading = false;
        }).catch(error => {
          console.error('更新执行时间失败:', error);
          this.$message.error('执行时间更新失败，请稍后重试');
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

      // 放下元素
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
      
      // 获取用户列表
      async getUserList() {
        try {
          const response = await fetchUserList({ pageSize: 100, pageNum: 1 });
          this.userList = response.data.list || [];
        } catch (error) {
          console.error('获取用户列表失败', error);
          this.$message.error('获取用户列表失败');
        }
      },
      
      // 处理用户选择变更
      handleUserChange(val) {
        // 全部用户视图
        if (val === "") {
          // 获取所有用户的日程并按权重排序
          fetchAllUsersSchedulesByWeight({
            pageSize: this.listQuery.pageSize,
            pageNum: this.listQuery.pageNum,
            searchKey: this.listQuery.searchKey,
            status: this.listQuery.status
          }).then(response => {
            this.listLoading = false;
            this.list = response.data.list;
            this.total = response.data.total;
            // 不要直接修改计算属性
            // this.groupedSchedules = this.groupUserSchedules();
            // 使用临时变量储存用户分组数据
            this.userGroupedData = this.groupUserSchedules();
          });
        } else {
          // 单一用户视图
          this.listQuery.pageNum = 1;
          this.userGroupedData = null; // 清除用户分组数据
          this.getList();
        }
      },
      
      // 用于所有用户日程分组的特殊方法
      groupUserSchedules() {
        // 不再按用户分组，而是将所有日程按权重排序放在一起显示
        let group = {};
        
        // 创建单一分组
        const groupName = "所有用户日程";
        group[groupName] = [...this.list]; // 复制列表
        
        // 根据优先级排序（已从后端排序，这里保持不变）
        
        return group;
      }
    }
  }
</script>
<style>
/* 通用样式 */
.app-container {
  padding: 20px;
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

/* 任务信息样式 */
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

/* 顶部卡片样式优化 */
.filter-container, .operate-container {
  margin-bottom: 20px;
  border-radius: 8px;
  border: none;
}

.filter-header, .operate-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 15px;
}

.filter-header i, .operate-header i {
  margin-right: 8px;
  font-size: 18px;
  color: #409EFF;
}

.filter-content {
  margin-top: 15px;
}

.operate-actions {
  display: flex;
  justify-content: flex-end;
}

.search-btn {
  background: #409EFF;
  border-color: #409EFF;
  border-radius: 4px;
}

.btn-add {
  border-radius: 4px;
}

/* 新的日程时间轴样式 */
.scheduler-container {
  margin-bottom: 20px;
  border-radius: 8px;
  border: none;
  padding: 0;
  overflow: hidden;
}

.scheduler-wrapper {
  padding: 20px;
}

.day-group {
  margin-bottom: 30px;
}

.day-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.day-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  min-width: 60px;
  padding: 8px;
  background-color: #fafafa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.06);
}

.day-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.day-weekday {
  font-size: 14px;
  color: #606266;
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

/* 空状态样式 */
.empty-state {
  padding: 40px 0;
  display: flex;
  justify-content: center;
  align-items: center;
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

.task-detail {
  /* 任务详情样式 */
  margin-bottom: 20px;
}

/* 新增逾期卡片样式 */
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

.timeline-event.dragging {
  opacity: 0.5;
  cursor: move;
}

.timeline-event.drag-over {
  border: 2px dashed #409EFF;
}
</style>
