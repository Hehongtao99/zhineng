<template>
    <div class="app-container">
      <el-card class="filter-container" shadow="hover">
        <div class="filter-header">
          <i class="el-icon-search"></i>
          <span>筛选搜索</span>
        </div>
        <div class="filter-content">
          <el-form :inline="true" :model="listQuery" size="small" label-width="120px">
            <el-form-item label="分类：">
              <el-select filterable clearable v-model="listQuery.categoryId" @change="getList" placeholder="全部" class="filter-select">
                <el-option v-for="item in categoryList"
                :key="item.id"
                :label="item.name"
                :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="关键字：">
              <el-input v-model="listQuery.searchKey" placeholder="请输入关键字" clearable class="filter-input"></el-input>
            </el-form-item>
            <div class="filter-buttons">
              <el-button
                icon="el-icon-refresh"
                @click="handleResetSearch()"
                size="small"
                plain>
                重置
              </el-button>
              <el-button
                type="primary"
                icon="el-icon-search"
                @click="handleSearchList()"
                size="small">
                查询搜索
              </el-button>
            </div>
          </el-form>
        </div>
      </el-card>
      <el-card class="operate-container" shadow="hover">
        <div class="operate-header">
          <div class="operate-title">
            <i class="el-icon-tickets"></i>
            <span>任务列表</span>
            <el-tag type="info" size="small" effect="plain" class="priority-tag"><i class="el-icon-info"></i> 已按优先级从高到低排列</el-tag>
          </div>
          <div class="meeting-room-info">
            <el-tooltip content="会议室总数/可用数量" placement="top">
              <el-tag type="warning" effect="plain">
                <i class="el-icon-office-building"></i> 会议室: {{meetingRoomTotal}}/{{meetingRoomAvailable}}
              </el-tag>
            </el-tooltip>
          </div>
          <div class="button-group">
          
            <el-button size="small" type="success" class="btn-add" @click="handleAdd()"><i class="el-icon-plus"></i> 创建任务</el-button>
          </div>
        </div>
      </el-card>
      <div class="table-container">
        <el-table 
          ref="adminTable"
          :data="list"
          style="width: 100%;"
          v-loading="listLoading" 
          stripe
          border
          highlight-current-row
          :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
          @row-click="handleRowClick"
          row-class-name="table-row">
          <el-table-column label="编号" width="80" align="center">
            <template slot-scope="scope">
              <span class="task-id">{{scope.row.id}}</span>
            </template>
          </el-table-column>
          <el-table-column width="160" label="任务编码" align="center">
            <template slot-scope="scope">
              <el-popover
                placement="top-start"
                title="任务编码"
                width="200"
                trigger="hover"
                :content="scope.row.code">
                <span class="code-text" slot="reference">{{scope.row.code}}</span>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column width="130" label="标题" align="center">
            <template slot-scope="scope">
              <el-popover
                placement="top"
                width="200"
                trigger="hover">
                <div class="popover-title">
                  <span class="popover-title-text">{{scope.row.title}}</span>
                </div>
                <div class="popover-content" v-if="scope.row.description">
                  <div class="popover-description">{{scope.row.description}}</div>
                </div>
                <span class="task-title" slot="reference">{{scope.row.title}}</span>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column width="120" label="类型" align="center">
            <template slot-scope="scope">
              <el-tag size="medium" effect="plain">{{scope.row.categoryName}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column width="120" label="重要性" align="center">
            <template slot-scope="scope">
              <el-tag type="warning" size="medium" effect="plain">{{scope.row.importanceName || '一般'}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column width="120" label="紧急性" align="center">
            <template slot-scope="scope">
              <el-tag type="danger" size="medium" effect="plain">{{scope.row.exigencyName || '一般'}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column width="120" label="权重分值" align="center">
            <template slot-scope="scope">
              <el-popover
                placement="top"
                width="200"
                trigger="hover">
                <div class="popover-title">
                  <span>权重计算</span>
                </div>
                <div class="popover-content">
                  <div class="popover-item">
                    <span class="popover-label">重要性:</span>
                    <span class="popover-value">{{scope.row.importanceName || '一般'}}</span>
                  </div>
                  <div class="popover-item">
                    <span class="popover-label">紧急性:</span>
                    <span class="popover-value">{{scope.row.exigencyName || '一般'}}</span>
                  </div>
                  <div class="popover-item">
                    <span class="popover-label">计算结果:</span>
                    <span class="popover-value priority-value">{{scope.row.priorityScore || 0}}</span>
                  </div>
                </div>
                <el-tag type="danger" effect="dark" class="priority-score" slot="reference">{{scope.row.priorityScore || 0}}</el-tag>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120" align="center">
            <template slot-scope="scope">
              <el-popover
                placement="top"
                width="200"
                trigger="hover">
                <div class="popover-content status-popover">
                  <div class="status-timeline">
                    <div class="timeline-item" :class="{'active': scope.row.status >= 1}">
                      <div class="timeline-node"></div>
                      <div class="timeline-content">待办</div>
                    </div>
                    <div class="timeline-item" :class="{'active': scope.row.status >= 4}">
                      <div class="timeline-node"></div>
                      <div class="timeline-content">已完成</div>
                    </div>
                  </div>
                  <div v-if="scope.row.status == 5" class="overdue-hint">
                    <i class="el-icon-warning"></i> 此任务已逾期
                  </div>
                </div>
                <el-tag 
                  :type="getStatusType(scope.row.status)" 
                  effect="light"
                  size="medium"
                  slot="reference">
                  {{scope.row.status | formatStatus}}
                </el-tag>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column label="会议室" width="100" align="center">
            <template slot-scope="scope">
              <el-tag 
                :type="scope.row.needMeetingRoom ? 'success' : 'info'" 
                effect="plain"
                size="medium">
                {{scope.row.needMeetingRoom ? '需要' : '不需要'}}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="预估时间" width="120" align="center">
            <template slot-scope="scope">
              <div class="time-info">
                <i class="el-icon-time"></i>
                <span>{{scope.row.timeSpend}} 分钟</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="开始时间" width="160" align="center">
            <template slot-scope="scope">
              <div class="create-time">
                <i class="el-icon-date"></i>
                <span>{{scope.row.startTime | formatDateTime}}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column width="200" label="任务描述" align="center">
            <template slot-scope="scope">
              <el-popover
                placement="top"
                width="300"
                trigger="hover">
                <div class="popover-title">
                  <span>任务描述</span>
                </div>
                <div class="popover-content">
                  <div class="popover-description">{{scope.row.description || '无描述'}}</div>
                </div>
                <div class="description-text" slot="reference">{{scope.row.description}}</div>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column label="截止时间" width="160" align="center">
            <template slot-scope="scope">
              <el-popover
                placement="top"
                width="200"
                trigger="hover">
                <div class="popover-title">
                  <span>截止时间</span>
                </div>
                <div class="popover-content">
                  <div class="deadline-info-detail">
                    <div class="popover-item">
                      <span class="popover-label">截止日期:</span>
                      <span class="popover-value">{{scope.row.deadline | formatDate}}</span>
                    </div>
                    <div class="popover-item">
                      <span class="popover-label">截止时间:</span>
                      <span class="popover-value">{{scope.row.deadline | formatTime}}</span>
                    </div>
                    <div class="popover-item" v-if="getRemainingTime(scope.row.deadline)">
                      <span class="popover-label">剩余时间:</span>
                      <span class="popover-value" :class="isDeadlineNear(scope.row.deadline) ? 'deadline-near' : ''">
                        {{getRemainingTime(scope.row.deadline)}}
                      </span>
                    </div>
                  </div>
                </div>
                <div class="deadline-info" :class="isDeadlineNear(scope.row.deadline) ? 'deadline-near' : ''" slot="reference">
                  <i class="el-icon-date"></i>
                  <span>{{scope.row.deadline | formatDateTime}}</span>
                </div>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="160" align="center">
            <template slot-scope="scope">
              <div class="create-time">
                <i class="el-icon-date"></i>
                <span>{{scope.row.createTime | formatDateTime}}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="280" align="center" fixed="right">
            <template slot-scope="scope">
              <div class="operate-buttons">
                <div class="btn-group">
                  <el-button size="mini"
                    type="primary"
                    icon="el-icon-edit"
                    plain
                    @click.stop="handleUpdate(scope.row)">编辑
                  </el-button>
                  <el-button size="mini"
                    icon="el-icon-delete"
                    type="danger"
                    plain
                    @click.stop="handleDelete(scope.$index, scope.row)">删除
                  </el-button>
                </div>
                <div class="btn-group">
                  <el-dropdown @command="(command) => handleCommand(command, scope.row)" trigger="click" @click.stop>
                    <el-button size="mini" type="info" plain>
                      更多<i class="el-icon-arrow-down el-icon--right"></i>
                    </el-button>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item v-if="scope.row.status == 1" command="complete">完成任务</el-dropdown-item>
                      <el-dropdown-item command="detail">查看详情</el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                  <el-popover
                    placement="left"
                    width="150"
                    trigger="hover">
                    <div class="popover-title">
                      <span>快捷操作</span>
                    </div>
                    <div class="popover-content">
                      <div class="action-buttons">
                        <el-button v-if="scope.row.status == 1" size="mini" type="success" @click.stop="updateTaskStatus(scope.row, 4)">完成任务</el-button>
                        <el-button size="mini" type="info" @click.stop="showTaskDetail(scope.row)">详情</el-button>
                      </div>
                    </div>
                    <el-button size="mini" type="warning" plain slot="reference">
                      快捷<i class="el-icon-s-operation el-icon--right"></i>
                    </el-button>
                  </el-popover>
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          layout="total, sizes, prev, pager, next, jumper"
          :current-page.sync="listQuery.pageNum"
          :page-size="listQuery.pageSize"
          :page-sizes="[10,15,20]"
          :total="total">
        </el-pagination>
      </div>

      <el-dialog
        :title="isEdit?'编辑任务':'创建任务'"
        :visible.sync="dialogVisible"
        width="40%"
        :close-on-click-modal="false"
        class="task-dialog">
        <el-form :model="admin"
                ref="adminForm"
                label-width="150px"
                size="small"
                :rules="rules">
          <el-form-item label="名称：" prop="title">
            <el-input v-model="admin.title" style="width: 250px" placeholder="请输入任务名称"></el-input>
          </el-form-item>
          <el-form-item label="类型：" prop="categoryId">
            <el-select style="width: 250px" v-model="admin.categoryId" placeholder="请选择任务类型">
              <el-option
                v-for="(item, index) in categoryList"
                :label="item.name"
                :key="index"
                :value="item.id">
                {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item>
          <!-- <el-form-item label="重要性：" prop="importanceId">
            <el-select style="width: 250px" v-model="admin.importanceId" placeholder="请选择重要性">
              <el-option
                v-for="(item, index) in importanceList"
                :label="item.name"
                :key="index"
                :value="item.id">
                {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="紧急性：" prop="exigencyId">
            <el-select style="width: 250px" v-model="admin.exigencyId" placeholder="请选择紧急性">
              <el-option
                v-for="(item, index) in exigencyList"
                :label="item.name"
                :key="index"
                :value="item.id">
                {{ item.name }}
              </el-option>
            </el-select>
          </el-form-item> -->
          <el-form-item label="参与人员：" prop="userIds">
            <el-select style="width: 250px" filterable multiple v-model="admin.userIds" placeholder="请选择参与人员" @change="handleUserChange">
              <el-option
                v-for="(item, index) in userList"
                :label="item.nickName"
                :key="index"
                :value="item.id">
                {{ item.nickName }}
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="预估时间：" prop="timeSpend">
            <el-input-number v-model="admin.timeSpend" @change="checkTaskEndTime" style="width: 150px" :min="1" :disabled="isEdit"></el-input-number> 分钟
            <span v-if="showTimeWarning" class="time-warning">
              <i class="el-icon-warning"></i> 预计结束时间将超过当天18:00
            </span>
            <div v-if="timeConflicts.length > 0" class="time-conflicts">
              <div class="conflict-header">
                <i class="el-icon-warning"></i>
                时间冲突
              </div>
              <div v-for="conflict in timeConflicts" :key="conflict.userId" class="conflict-item">
                <i class="el-icon-warning-outline"></i>
                <span>{{ conflict.userName }} 时间不足（剩余{{ conflict.remainingTime }}分钟，需要{{ conflict.neededTime }}分钟）</span>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="开始时间：" prop="startTime">
            <el-date-picker
              v-model="admin.startTime"
              type="date"
              value-format="yyyy-MM-dd"
              @change="handleStartDateChange"
              placeholder="选择开始日期">
            </el-date-picker>
            <!-- 用户剩余时间信息区域 -->
            <div v-if="Object.keys(userRemainingTimes).length > 0" class="user-remaining-times">
              <div class="user-time-header">各用户剩余时间：</div>
              <div v-for="(remainingTime, userId) in userRemainingTimes" :key="userId" class="user-time-item">
                <!-- 使用Number转换确保userId参数为数字 -->
                <span class="user-name">{{ getUserName(Number(userId)) }}:</span>
                <span class="remaining-time" :class="{'time-warning': remainingTime < admin.timeSpend}">
                  <i class="el-icon-time"></i> 剩余 {{ remainingTime }} 分钟
                </span>
              </div>
            </div>
            <!-- 旧的单一剩余时间显示 -->
            <span v-else-if="remainingTimeForDate !== null" class="remaining-time-info">
              <i class="el-icon-time"></i> 当天还可安排 <b>{{ remainingTimeForDate }}</b> 分钟
            </span>
          </el-form-item>
          <el-form-item label="截止时间：" prop="deadline">
            <el-date-picker
              v-model="admin.deadline"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="选择截止时间">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="是否需要会议室：">
            <el-switch
              v-model="admin.needMeetingRoom"
              active-color="#13ce66"
              inactive-color="#ff4949"
              active-text="需要"
              inactive-text="不需要"
              :disabled="meetingRoomAvailable <= 0 && !admin.needMeetingRoom">
            </el-switch>
            <span v-if="meetingRoomAvailable <= 0 && !admin.needMeetingRoom" class="meeting-room-warning">
              <i class="el-icon-warning"></i> 没有足够的会议室
            </span>
          </el-form-item>
          <el-form-item label="任务描述：" prop="description">
            <el-input v-model="admin.description" type="textarea" :rows="5" style="width: 250px"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false" size="small">取 消</el-button>
          <el-tooltip v-if="hasTimeConflict" content="存在用户时间冲突，无法创建任务" placement="top">
            <el-button type="primary" size="small" :disabled="hasTimeConflict">确 定</el-button>
          </el-tooltip>
          <el-button v-else type="primary" @click="handleDialogConfirm()" size="small">确 定</el-button>
        </span>
      </el-dialog>

      <el-dialog title="任务详情" :visible.sync="detailDialogVisible" width="50%" class="detail-dialog">
        <div v-if="currentTask" class="task-detail">
          <div class="detail-header">
            <h3>{{currentTask.title}}</h3>
            <el-tag :type="getStatusType(currentTask.status)" effect="dark">{{currentTask.status | formatStatus}}</el-tag>
          </div>
          <el-divider></el-divider>
          <div class="detail-content">
            <div class="detail-item">
              <span class="detail-label">任务编号:</span>
              <span class="detail-value">{{currentTask.code}}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">类型:</span>
              <span class="detail-value">{{currentTask.categoryName}}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">重要性:</span>
              <span class="detail-value">{{currentTask.importanceName || '一般'}}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">紧急性:</span>
              <span class="detail-value">{{currentTask.exigencyName || '一般'}}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">权重分值:</span>
              <span class="detail-value priority-value">{{currentTask.priorityScore || 0}}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">预估时间:</span>
              <span class="detail-value">{{currentTask.timeSpend}} 分钟</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">开始时间:</span>
              <span class="detail-value">
                {{currentTask.startTime | formatDate}}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">截止时间:</span>
              <span class="detail-value" :class="isDeadlineNear(currentTask.deadline) ? 'deadline-near' : ''">
                {{currentTask.deadline | formatDateTime}}
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">是否需要会议室:</span>
              <span class="detail-value">
                <el-tag :type="currentTask.needMeetingRoom ? 'success' : 'info'" effect="plain">
                  {{currentTask.needMeetingRoom ? '需要' : '不需要'}}
                </el-tag>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">创建时间:</span>
              <span class="detail-value">{{currentTask.createTime | formatDateTime}}</span>
            </div>
            <div class="detail-item detail-full">
              <span class="detail-label">任务描述:</span>
              <div class="detail-value description-area">{{currentTask.description}}</div>
            </div>
            <div class="detail-item detail-full" v-if="userResources.length > 0">
              <span class="detail-label">参与人员:</span>
              <div class="detail-value tag-group">
                <el-tag v-for="user in userResources" :key="'user-'+user.id" size="small" effect="plain" type="success" class="resource-tag">
                  {{user.nickName}}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关 闭</el-button>
          <el-button type="primary" @click="handleUpdate(currentTask)" v-if="currentTask">编 辑</el-button>
        </span>
      </el-dialog>

      <!-- 自动安排日程弹窗 -->
      <el-dialog
        title="自动安排日程"
        :visible.sync="autoArrangeDialogVisible"
        width="30%">
        <div class="auto-arrange-content">
          <p>系统将基于任务权重，从早上8点开始自动安排指定日期的所有任务日程。</p>
          <p>请选择要安排的日期：</p>
          <el-date-picker
            v-model="arrangeDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="选择日期">
          </el-date-picker>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="autoArrangeDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="confirmAutoArrange" :loading="autoArrangeLoading">确 定</el-button>
        </span>
      </el-dialog>
    </div>
  </template>
  <script>
    import {fetchTaskList,createTask,updateTask,deleteTask,fetchAllCategoryList, fetchAllPriorityList,fetchMeetingRoomCount,fetchAvailableMeetingRoomCount, autoArrangeTasksByDate, getRemainingTimeForDate, getRemainingTimeForUserAndDate, checkTimeConflict} from '@/api/api';
    import {fetchAllUserList} from '@/api/login';

    import {formatDate} from '@/utils/date';

    const defaultListQuery = {
      pageNum: 1,
      pageSize: 10,
      searchKey: null,
      categoryId: null,
    };
    const defaultAdmin = {
      id: null,
      title: null,
      code: null,
      categoryId: null,
      description: null,
      status: null,
      importanceId: null,
      exigencyId: null,
      userData: null,
      timeSpend: null,
      startTime: null,
      deadline: null,
      userIds: null,
      needMeetingRoom: false,
    };
    export default {
      name: 'TaskList',
      data() {
        return {
          listQuery: Object.assign({}, defaultListQuery),
          list: null,
          total: null,
          listLoading: false,
          dialogVisible: false,
          detailDialogVisible: false,
          currentTask: null,
          userResources: [],
          admin: Object.assign({}, defaultAdmin),
          isEdit: false,
          rules: {
            title: [
              { required: true, message: '请输入任务名称', trigger: 'blur' }
            ],
            categoryId: [
              { required: true, message: '请选择任务类型', trigger: 'change' }
            ],
            importanceId: [
              { required: true, message: '请选择重要性', trigger: 'change' }
            ],
            exigencyId: [
              { required: true, message: '请选择紧急性', trigger: 'change' }
            ],
            userIds: [
              { required: true, message: '请选择参与人员', trigger: 'change' }
            ],
            timeSpend: [
              { required: true, message: '请输入预估时间', trigger: 'blur' },
              { type: 'number', min: 1, message: '预估时间必须大于0', trigger: 'blur' }
            ],
            startTime: [
              { required: true, message: '请选择开始日期', trigger: 'change' }
            ],
            deadline: [
              { required: true, message: '请选择截止时间', trigger: 'change' }
            ],
            description: [
              { required: true, message: '请输入任务描述', trigger: 'blur' }
            ]
          },
          categoryList: [],

          importanceList: [],
          exigencyList: [],
          userList: [],
          meetingRoomTotal: 5,
          meetingRoomAvailable: 5,
          autoArrangeDialogVisible: false,
          arrangeDate: formatDate(new Date(), 'yyyy-MM-dd'),
          autoArrangeLoading: false,
          showTimeWarning: false,
          remainingTimeForDate: null,
          exceedRemainingTimeWarning: false,
          userRemainingTimes: {}, // 存储每个用户的剩余时间
          timeConflicts: [], // 存储时间冲突信息
          hasTimeConflict: false, // 是否存在时间冲突
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
        formatDate(time) {
          if (time == null || time === '') {
            return 'N/A';
          }
          let date = new Date(time);
          return formatDate(date, 'yyyy-MM-dd')
        },
        formatTime(time) {
          if (time == null || time === '') {
            return 'N/A';
          }
          let date = new Date(time);
          return formatDate(date, 'HH:mm:ss')
        },
        formatStatus(status) {
          // if (status == 0) {
          //   return '待审核';
          // }
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
        },
      },
      created() {
        this.getList();
        this.getCategoryList();
        this.getPriorityList();
        this.getUserList();
        this.getMeetingRoomInfo();
      },
      methods: {
        getMeetingRoomInfo() {
          // 如果后端API已经实现，可以取消注释使用实际API
          /*
          fetchMeetingRoomCount().then(response => {
            this.meetingRoomTotal = response.data;
            this.getAvailableMeetingRoomCount();
          });
          */
          // 默认设置为5个会议室，可以在这里从API获取
          this.meetingRoomTotal = 5;
          this.getAvailableMeetingRoomCount();
        },
        getAvailableMeetingRoomCount() {
          // 如果后端API已经实现，可以取消注释使用实际API
          /*
          fetchAvailableMeetingRoomCount().then(response => {
            this.meetingRoomAvailable = response.data;
          });
          */
          // 临时计算可用会议室数量
          if (this.list) {
            const usedRooms = this.list.filter(task => task.needMeetingRoom).length;
            this.meetingRoomAvailable = Math.max(0, this.meetingRoomTotal - usedRooms);
          } else {
            this.meetingRoomAvailable = this.meetingRoomTotal;
          }
        },
        getUserList() {
          fetchAllUserList().then(response => {
            this.userList = response.data;
          });
        },
        getPriorityList() {
          fetchAllPriorityList({type: 1}).then(response => {
            this.importanceList = response.data;
          });
          fetchAllPriorityList({type: 2}).then(response => {
            this.exigencyList = response.data;
          });
        },
        handleResetSearch() {
          this.listQuery = Object.assign({}, defaultListQuery);
          this.getList()
        },
        handleUpdateStatus(row) {
          this.$confirm('确认操作?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            updateTask(row).then(response => {
              this.$message({
                type: 'success',
                message: '操作成功!'
              });
              this.getList();
            });
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '取消操作'
            });
            this.getList();
          });
        },
        handleDialogConfirm() {
          this.$refs.adminForm.validate((valid) => {
            if (valid) {
              // 先检查时间冲突
              checkTimeConflict(this.admin).then(response => {
                if (response.data && response.data.length > 0) {
                  // 有冲突，显示冲突信息
                  let conflictMessage = '存在以下时间冲突：\n';
                  response.data.forEach(conflict => {
                    if (conflict.title === '工作时间超出范围') {
                      conflictMessage = conflict.description;
                    } else {
                      const startTime = this.$moment(conflict.scheduleStartTime).format('HH:mm');
                      const endTime = this.$moment(conflict.scheduleEndTime).format('HH:mm');
                      conflictMessage += `- ${conflict.title}（${startTime}-${endTime}）\n`;
                    }
                  });

                  this.$confirm(conflictMessage + '\n是否仍要继续创建？', '时间冲突提醒', {
                    confirmButtonText: '继续创建',
                    cancelButtonText: '取消',
                    type: 'warning'
                  }).then(() => {
                    this.submitTaskForm();
                  }).catch(() => {
                    // 用户取消，不执行任何操作
                  });
                } else {
                  // 没有冲突，直接提交
                  this.submitTaskForm();
                }
              });
            }
          });
        },
        
        submitTaskForm() {
          this.$confirm('是否要确认?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.admin.userData = window.JSON.stringify(this.admin.userIds);
            if (this.isEdit) {
              updateTask(this.admin).then(response => {
                this.$message({
                  message: '修改成功！',
                  type: 'success'
                });
                this.dialogVisible = false;
                this.getList();
              })
            } else {
              createTask(this.admin).then(response => {
                this.$message({
                  message: '创建成功！',
                  type: 'success'
                });
                this.dialogVisible = false;
                this.getList();
              })
            }
          })
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
        handleAdd() {
          this.dialogVisible = true;
          this.isEdit = false;
          this.admin = Object.assign({}, defaultAdmin);
        },
        handleDelete(index, row) {
          this.$confirm('是否要删除该任务?删除后将无法撤销', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            // 显示加载状态
            this.$message({
              message: '正在删除任务...',
              type: 'info',
              duration: 2000
            });
            
            deleteTask(row.id).then(response => {
              if (response.data === null && response.code === 200) {
                this.$message({
                  type: 'success',
                  message: '删除成功!'
                });
                this.getList();
              } else {
                this.$message({
                  type: 'error',
                  message: '删除失败: ' + (response.message || '未知错误')
                });
              }
            }).catch(error => {
              // 处理网络错误或服务器返回的错误
              let errorMessage = '删除失败';
              if (error.response && error.response.data) {
                errorMessage = errorMessage + ': ' + (error.response.data.message || '服务器错误');
              } else if (error.message) {
                errorMessage = errorMessage + ': ' + error.message;
              }
              
              this.$message({
                type: 'error',
                message: errorMessage,
                duration: 5000
              });
            });
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消删除'
            });
          });
        },
        handleUpdate(row) {
          this.isEdit = true;
          try {
            row.userIds = window.JSON.parse(row.userData);
          } catch (error) {
            row.userIds = [];
          }
          this.admin = Object.assign({},row);
          this.dialogVisible = true;
        },
        getList() {
          this.listLoading = true;
          fetchTaskList(this.listQuery).then(response => {
            this.listLoading = false;
            this.list = response.data.list;
            this.total = response.data.total;
            // 更新可用会议室数量
            this.getAvailableMeetingRoomCount();
          });
        },
        handleSuccess(response, file) {
          // 处理上传成功后的逻辑，获取后端返回的文件路径
          const filePath = response.data;
          this.admin.image = `${process.env.BASE_API}/${filePath}`;
          console.error(filePath);
        },
        getCategoryList() {
          fetchAllCategoryList().then(res => {
            this.categoryList = res.data
          })
        },
        handleVideo(file) {
          if (!file) {
            this.$message({
              type: 'warning',
              message: '没有可查看的任务'
            });
            return;
          }
          window.open(file, '_blank');
        },
        getStatusType(status) {
          if (status == 1) return 'info';
          if (status == 4) return 'success';
          if (status == 5) return 'danger';
          return 'info';
        },
        isDeadlineNear(deadline) {
          if (!deadline) return false;
          const now = new Date();
          const deadlineDate = new Date(deadline);
          // 计算时间差，如果小于48小时则为临近截止
          return (deadlineDate - now) < (48 * 60 * 60 * 1000) && (deadlineDate - now) > 0;
        },
        handleRowClick(row, column, event) {
          this.showTaskDetail(row);
        },
        
        showTaskDetail(task) {
          this.currentTask = Object.assign({}, task);
          this.detailDialogVisible = true;
          
          // 解析用户数据
          if (task.userData) {
            try {
              const userIds = window.JSON.parse(task.userData);
              this.userResources = this.userList.filter(user => userIds.includes(user.id));
            } catch (error) {
              this.userResources = [];
            }
          } else {
            this.userResources = [];
          }
        },
        
        handleCommand(command, row) {
          if (command === 'detail') {
            this.showTaskDetail(row);
          } else if (command === 'complete') {
            this.updateTaskStatus(row, 4);
          }
        },
        
        updateTaskStatus(row, status) {
          const task = Object.assign({}, row);
          task.status = status;
          
          this.$confirm(`确认将任务状态修改为"${this.$options.filters.formatStatus(status)}"?`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            // 如果状态是已完成(4)或待办(1)，自动释放会议室
            if ((status === 4 || status === 1) && task.needMeetingRoom) {
              task.needMeetingRoom = false;
            }
            
            updateTask(task).then(response => {
              this.$message({
                type: 'success',
                message: '状态更新成功!'
              });
              
              // 直接更新本地数据，不等待刷新
              const index = this.list.findIndex(item => item.id === task.id);
              if (index !== -1) {
                this.list[index].status = status;
                if ((status === 4 || status === 1) && this.list[index].needMeetingRoom) {
                  this.list[index].needMeetingRoom = false;
                }
              }
              
              // 立即刷新页面数据
              this.getList();
              // 更新可用会议室数量
              this.getMeetingRoomInfo();
            });
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '取消操作'
            });
          });
        },
        getRemainingTime(deadline) {
          if (!deadline) return null;
          
          const now = new Date();
          const deadlineDate = new Date(deadline);
          
          if (deadlineDate < now) {
            return '已逾期';
          }
          
          const diffMs = deadlineDate - now;
          const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
          const diffHours = Math.floor((diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
          const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
          
          if (diffDays > 0) {
            return `${diffDays}天${diffHours}小时`;
          } else if (diffHours > 0) {
            return `${diffHours}小时${diffMinutes}分钟`;
          } else {
            return `${diffMinutes}分钟`;
          }
        },
        handleAutoArrange() {
          this.arrangeDate = formatDate(new Date(), 'yyyy-MM-dd'); // 默认当天
          this.autoArrangeDialogVisible = true;
        },
        
        confirmAutoArrange() {
          if (!this.arrangeDate) {
            this.$message({
              message: '请选择要安排的日期',
              type: 'warning'
            });
            return;
          }
          
          this.autoArrangeLoading = true;
          autoArrangeTasksByDate(this.arrangeDate).then(response => {
            this.$message({
              message: '任务日程已自动安排完成',
              type: 'success'
            });
            this.autoArrangeDialogVisible = false;
            // 刷新任务列表
            this.getList();
          }).catch(error => {
            // 如果错误消息包含"超出了当日最大工作时间"关键字，使用warning类型
            var errorMsg = '';
            if (error.response && error.response.data && error.response.data.message) {
              errorMsg = error.response.data.message;
              if (errorMsg.indexOf('超出了当日最大工作时间') !== -1) {
                this.$message({
                  message: errorMsg,
                  type: 'warning',
                  duration: 5000  // 显示5秒
                });
                // 仍然关闭对话框并刷新列表以显示已安排的任务
                this.autoArrangeDialogVisible = false;
                this.getList();
                this.autoArrangeLoading = false;
                return;
              }
            } else if (error.message) {
              errorMsg = error.message;
            } else {
              errorMsg = '未知错误';
            }
            
            // 处理其他类型错误
            this.$message({
              message: '自动安排失败: ' + errorMsg,
              type: 'error'
            });
          }).finally(() => {
            this.autoArrangeLoading = false;
          });
        },
        checkTaskEndTime() {
          if (this.admin.timeSpend && this.admin.startTime && this.admin.userIds && this.admin.userIds.length > 0) {
            // 解析开始日期
            const startDate = new Date(this.admin.startTime);
            
            // 创建当天18点的时间点用于比较
            const endTimeLimit = new Date(this.admin.startTime);
            endTimeLimit.setHours(18, 0, 0, 0);
            
            // 计算任务开始时间（早上8点）
            const taskStartTime = new Date(this.admin.startTime);
            taskStartTime.setHours(8, 0, 0, 0);
            
            // 计算任务结束时间
            const taskEndTime = new Date(taskStartTime.getTime() + this.admin.timeSpend * 60 * 1000);
            
            // 检查任务结束时间是否超过了当天18点
            if (taskEndTime > endTimeLimit) {
              this.showTimeWarning = true;
            } else {
              this.showTimeWarning = false;
            }

            // 检查每个用户的剩余时间
            this.timeConflicts = [];
            this.hasTimeConflict = false;

            // 获取每个用户的剩余时间
            this.admin.userIds.forEach(userId => {
              getRemainingTimeForUserAndDate(this.admin.startTime, userId).then(response => {
                const remainingTime = response.data;
                if (remainingTime < this.admin.timeSpend) {
                  const user = this.userList.find(u => u.id === userId);
                  if (user) {
                    this.timeConflicts.push({
                      userId: userId,
                      userName: user.nickName,
                      remainingTime: remainingTime,
                      neededTime: this.admin.timeSpend
                    });
                    this.hasTimeConflict = true;
                  }
                }
              });
            });
          } else {
            this.showTimeWarning = false;
            this.timeConflicts = [];
            this.hasTimeConflict = false;
          }
        },
        handleStartDateChange(date) {
          // 先执行原来的检查结束时间逻辑
          this.checkTaskEndTime();
          
          // 如果有选择日期，则获取该日期剩余可安排时间
          if (date) {
            this.fetchRemainingTimeForDate(date);
          } else {
            this.remainingTimeForDate = null;
            this.exceedRemainingTimeWarning = false;
          }
        },
        fetchRemainingTimeForDate(date) {
          // 如果有选择的用户，则获取特定用户的剩余时间
          if (this.admin.userIds && this.admin.userIds.length > 0) {
            // 确保日期格式正确
            const formattedDate = date ? date.toString() : '';
            console.log('获取用户剩余时间，用户：', this.admin.userIds, '日期：', formattedDate);
            
            // 清空用户剩余时间对象
            this.userRemainingTimes = {};
            
            // 获取最小的剩余时间，用于检查是否超出限制
            let minRemainingTime = Number.MAX_SAFE_INTEGER;
            
            // 临时存储所有Promise
            const promises = [];
            
            // 获取每个用户的剩余时间
            this.admin.userIds.forEach(userId => {
              console.log('正在获取用户ID', userId, '的剩余时间');
              const promise = getRemainingTimeForUserAndDate(formattedDate, userId)
                .then(response => {
                  console.log('用户ID', userId, '的剩余时间:', response.data);
                  // 保存用户剩余时间，确保使用Vue的方式更新对象
                  this.$set(this.userRemainingTimes, userId.toString(), response.data);
                  
                  // 更新最小剩余时间
                  if (response.data < minRemainingTime) {
                    minRemainingTime = response.data;
                  }
                })
                .catch(error => {
                  console.error(`获取用户ID ${userId} 剩余时间失败`, error);
                  // 设置该用户剩余时间为未知
                  this.$set(this.userRemainingTimes, userId.toString(), 0);
                });
              
              promises.push(promise);
            });
            
            // 等待所有请求完成
            Promise.all(promises).then(() => {
              console.log('所有用户剩余时间获取完成:', this.userRemainingTimes);
              // 使用最小剩余时间进行限制检查
              this.remainingTimeForDate = minRemainingTime !== Number.MAX_SAFE_INTEGER ? minRemainingTime : null;
              this.checkRemainingTime();
            }).catch(error => {
              console.error('获取用户剩余时间失败', error);
              this.remainingTimeForDate = null;
              this.exceedRemainingTimeWarning = false;
            });
          } else {
            // 没有选择用户时使用原有逻辑
            getRemainingTimeForDate(date).then(response => {
              this.remainingTimeForDate = response.data;
              // 获取到数据后，检查当前预估时间是否超出
              this.checkRemainingTime();
            }).catch(error => {
              console.error('获取剩余时间失败', error);
              this.remainingTimeForDate = null;
              this.exceedRemainingTimeWarning = false;
            });
          }
        },
        checkRemainingTime() {
          if (this.remainingTimeForDate !== null && this.admin.timeSpend) {
            // 如果预估时间超过剩余可用时间，显示警告
            if (this.admin.timeSpend > this.remainingTimeForDate) {
              this.exceedRemainingTimeWarning = true;
            } else {
              this.exceedRemainingTimeWarning = false;
            }
          } else {
            this.exceedRemainingTimeWarning = false;
          }
        },
        handleUserChange() {
          console.log('用户选择变化：', this.admin.userIds);
          // 当用户选择变化时，如果有开始日期，则重新获取剩余时间
          if (this.admin.startTime) {
            console.log('开始日期存在，获取用户剩余时间');
            this.fetchRemainingTimeForDate(this.admin.startTime);
          } else {
            console.log('开始日期不存在，清空用户剩余时间');
            this.userRemainingTimes = {};
          }
        },
        getUserName(userId) {
          // 确保userId是数字类型
          userId = Number(userId);
          
          // 防止NaN
          if (isNaN(userId)) {
            return "未知用户";
          }
          
          // 根据用户ID获取用户名称
          const user = this.userList.find(u => u.id === userId);
          return user ? user.nickName : `用户${userId}`;
        },
      }
    }
  </script>
  <style>
  .filter-container {
    margin-bottom: 20px;
  }
  
  .filter-header {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 15px;
    color: #303133;
  }
  
  .filter-header i {
    margin-right: 8px;
    color: #409EFF;
  }
  
  .filter-content {
    padding: 10px 0;
  }
  
  .filter-select, .filter-input {
    width: 220px;
  }
  
  .filter-buttons {
    display: inline-flex;
    margin-left: 15px;
    gap: 10px;
  }
  
  .operate-container {
    margin-bottom: 20px;
  }
  
  .operate-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .meeting-room-info {
    margin-left: auto;
    margin-right: 15px;
  }
  
  .operate-title {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: bold;
    color: #303133;
  }
  
  .operate-title i {
    margin-right: 8px;
    color: #409EFF;
  }
  
  .priority-tag {
    margin-left: 10px;
    font-weight: normal;
  }
  
  .btn-add {
    margin-left: auto;
  }
  
  .table-container {
    margin-bottom: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    border-radius: 4px;
    overflow: hidden;
  }
  
  .table-row:hover {
    background-color: #f5f7fa !important;
  }
  
  .task-id {
    font-weight: bold;
    color: #606266;
  }
  
  .code-text {
    font-family: monospace;
    color: #606266;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: inline-block;
    max-width: 150px;
  }
  
  .task-title {
    font-weight: 500;
    color: #303133;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: inline-block;
    max-width: 110px;
  }
  
  .priority-score {
    font-weight: bold;
    font-size: 14px;
  }
  
  .time-info, .deadline-info, .create-time {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 5px;
  }
  
  .deadline-near {
    color: #f56c6c;
    font-weight: bold;
  }
  
  .description-text {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 180px;
  }
  
  .operate-buttons {
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 5px;
  }
  
  .btn-group {
    display: flex;
    justify-content: center;
    gap: 5px;
  }
  
  .pagination-container {
    text-align: center;
    margin-top: 20px;
    padding: 20px 0;
  }
  
  .task-dialog .el-dialog__body {
    padding: 20px 20px 0;
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
    width: 178px;
    height: 178px;
    display: block;
  }
  
  .table-row {
    cursor: pointer;
  }
  
  .detail-dialog .el-dialog__body {
    padding: 20px;
  }
  
  .task-detail {
    color: #606266;
  }
  
  .detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
  }
  
  .detail-header h3 {
    margin: 0;
    color: #303133;
    font-size: 18px;
  }
  
  .detail-content {
    display: flex;
    flex-wrap: wrap;
    margin-top: 20px;
  }
  
  .detail-item {
    width: 50%;
    margin-bottom: 15px;
    display: flex;
  }
  
  .detail-full {
    width: 100%;
  }
  
  .detail-label {
    width: 100px;
    color: #909399;
    text-align: right;
    padding-right: 15px;
    font-weight: 500;
  }
  
  .detail-value {
    flex: 1;
  }
  
  .priority-value {
    font-weight: bold;
    color: #f56c6c;
  }
  
  .description-area {
    background-color: #f8f8f8;
    padding: 10px;
    border-radius: 4px;
    min-height: 60px;
    white-space: pre-wrap;
  }
  
  .tag-group {
    display: flex;
    flex-wrap: wrap;
    gap: 5px;
  }
  
  .resource-tag {
    margin-right: 5px;
  }
  
  .popover-title {
    font-size: 14px;
    font-weight: bold;
    margin-bottom: 10px;
    padding-bottom: 10px;
    border-bottom: 1px solid #EBEEF5;
    color: #303133;
  }
  
  .popover-title-text {
    word-break: break-all;
  }
  
  .popover-content {
    font-size: 13px;
    color: #606266;
  }
  
  .popover-description {
    max-height: 200px;
    overflow-y: auto;
    word-break: break-all;
    white-space: pre-wrap;
    line-height: 1.5;
    background-color: #f8f8f8;
    padding: 8px;
    border-radius: 4px;
  }
  
  .popover-item {
    display: flex;
    margin-bottom: 8px;
    align-items: center;
  }
  
  .popover-label {
    width: 80px;
    color: #909399;
    font-weight: 500;
  }
  
  .popover-value {
    flex: 1;
  }
  
  .status-popover {
    padding: 5px 0;
  }
  
  .status-timeline {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }
  
  .timeline-item {
    display: flex;
    align-items: center;
    opacity: 0.5;
  }
  
  .timeline-item.active {
    opacity: 1;
  }
  
  .timeline-node {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background-color: #C0C4CC;
    margin-right: 8px;
  }
  
  .timeline-item.active .timeline-node {
    background-color: #67C23A;
  }
  
  .timeline-content {
    font-size: 13px;
  }
  
  .overdue-hint {
    margin-top: 10px;
    color: #F56C6C;
    font-size: 12px;
    display: flex;
    align-items: center;
    gap: 5px;
  }
  
  .deadline-info-detail {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  
  .action-buttons {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  
  .meeting-room-warning {
    margin-left: 10px;
    color: #E6A23C;
    font-size: 13px;
  }
  
  .button-group {
    display: flex;
    gap: 10px;
  }
  
  .btn-arrange {
    font-weight: 500;
  }
  
  .auto-arrange-content {
    padding: 10px 0;
    text-align: center;
  }
  
  .auto-arrange-content p {
    margin: 10px 0;
    color: #606266;
  }
  
  .auto-arrange-content .el-date-picker {
    width: 100%;
    margin-top: 15px;
  }
  
  .time-warning {
    color: #E6A23C;
    font-size: 12px;
    margin-left: 10px;
    padding: 2px 8px;
    background-color: #FEF6EB;
    border-radius: 4px;
    border-left: 2px solid #E6A23C;
    display: inline-block;
  }
  
  .remaining-time-info {
    margin-left: 10px;
    color: #409EFF;
    display: inline-flex;
    align-items: center;
  }
  
  .remaining-time-info i {
    margin-right: 5px;
  }
  
  .exceed-time-warning {
    margin-left: 10px;
    color: #F56C6C;
    font-weight: bold;
    display: inline-flex;
    align-items: center;
  }
  
  .exceed-time-warning i {
    margin-right: 5px;
  }
  
  .user-remaining-times {
    margin-left: 10px;
    margin-top: 5px;
    padding: 8px 10px;
    border-left: 3px solid #409EFF;
    background-color: #f0f9ff;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    max-width: 300px;
  }
  
  .user-time-header {
    font-weight: 500;
    color: #409EFF;
    margin-bottom: 8px;
    padding-bottom: 5px;
    border-bottom: 1px solid #e6f1fc;
  }
  
  .user-time-item {
    margin-bottom: 5px;
    display: flex;
    align-items: center;
    padding: 2px 0;
  }
  
  .user-time-item:last-child {
    margin-bottom: 0;
  }
  
  .user-name {
    min-width: 90px;
    font-weight: 500;
    color: #303133;
    margin-right: 8px;
  }
  
  .remaining-time {
    color: #409EFF;
    display: flex;
    align-items: center;
    font-weight: 500;
  }
  
  .remaining-time i {
    margin-right: 5px;
  }
  
  .remaining-time.time-warning {
    color: #E6A23C;
    background-color: #fef6e7;
    padding: 2px 8px;
    border-radius: 3px;
  }
  
  .el-button.is-disabled {
    opacity: 0.7;
    cursor: not-allowed;
    position: relative;
    background-color: #a0cfff !important;
    border-color: #a0cfff !important;
  }
  
  .el-tooltip {
    display: inline-block;
  }
  
  .time-conflicts {
    margin-top: 10px;
    background-color: #fff3f3;
    border-radius: 4px;
    border-left: 3px solid #f56c6c;
    overflow: hidden;
  }
  
  .conflict-header {
    background-color: #fde2e2;
    color: #f56c6c;
    padding: 8px;
    font-weight: bold;
    font-size: 14px;
    display: flex;
    align-items: center;
    border-bottom: 1px solid rgba(245, 108, 108, 0.2);
  }
  
  .conflict-header i {
    margin-right: 6px;
  }
  
  .conflict-item {
    display: flex;
    align-items: center;
    color: #f56c6c;
    margin: 8px;
    font-size: 13px;
    padding: 4px 0;
  }
  
  .conflict-item i {
    margin-right: 6px;
  }
  </style>
