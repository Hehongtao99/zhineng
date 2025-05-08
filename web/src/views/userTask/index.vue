<template> 
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="120px">
          <el-form-item label="分类：">
            <el-select filterable clearable v-model="listQuery.categoryId" @change="getList" placeholder="全部">
              <el-option v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="关键字：">
            <el-input v-model="listQuery.searchKey" placeholder="请输入关键字" clearable></el-input>
          </el-form-item>
          <el-button
            style="margin-left: 15px"
            icon="el-icon-refresh"
            @click="handleResetSearch()"
            size="small">
            重置
          </el-button>
          <el-button
            type="primary"
            icon="el-icon-search"
            @click="handleSearchList()"
            size="small">
            查询搜索
          </el-button>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <div>
          <i class="el-icon-tickets"></i>
          <span>任务列表</span><el-tag type="info" style="margin-left: 10px;" class="el-icon-info">已按优先级从高到低排列</el-tag>
          <div class="meeting-room-info" style="display: inline-block; margin-left: 15px;">
            <el-tooltip content="会议室总数/可用数量" placement="top">
              <el-tag type="warning" effect="plain">
                <i class="el-icon-office-building"></i> 会议室: {{meetingRoomTotal}}/{{meetingRoomAvailable}}
              </el-tag>
            </el-tooltip>
          </div>
        </div>
        <el-button size="small" type="success" @click="handleAdd()" class="create-task-btn"><i class="el-icon-plus"></i> 创建任务</el-button>
      </div>
    </el-card>
    <div class="table-container">
      <el-table ref="adminTable"
                :data="list"
                style="width: 100%;"
                v-loading="listLoading" stripe>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column width="160" label="任务编码" align="center">
          <template slot-scope="scope">{{scope.row.code}}</template>
        </el-table-column>
        <el-table-column width="120" label="标题" align="center">
          <template slot-scope="scope">{{scope.row.title}}</template>
        </el-table-column>
        <el-table-column width="120" label="类型" align="center">
          <template slot-scope="scope">{{scope.row.categoryName}}</template>
        </el-table-column>
        <el-table-column width="120" label="重要性" align="center">
          <template slot-scope="scope">{{scope.row.importanceName || '一般'}}</template>
        </el-table-column>
        <el-table-column width="120" label="紧急性" align="center">
          <template slot-scope="scope">{{scope.row.exigencyName || '一般'}}</template>
        </el-table-column>
        <el-table-column width="120" label="权重分值" align="center">
          <template slot-scope="scope"><el-tag type="danger">{{scope.row.priorityScore || 0}}</el-tag></template>
        </el-table-column>
        <el-table-column label="状态" width="160" align="center">
          <template slot-scope="scope">{{scope.row.status | formatStatus}}</template>
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
        <el-table-column label="预估时间" width="160" align="center">
          <template slot-scope="scope">{{scope.row.timeSpend}} 分钟</template>
        </el-table-column>
        <el-table-column width="300" label="任务描述" align="center">
          <template slot-scope="scope">{{scope.row.description}}</template>
        </el-table-column>
        <el-table-column label="截止时间" width="160" align="center">
          <template slot-scope="scope">{{scope.row.deadline | formatDateTime}}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="160" align="center">
          <template slot-scope="scope">{{scope.row.createTime | formatDateTime}}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini"
              type="warning"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)">编辑
            </el-button>
            <el-button size="mini"
              icon="el-icon-delete"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
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
      :title="isEdit?'编辑任务':'创建任务'"
      :visible.sync="dialogVisible"
      width="40%">
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
          <el-input-number v-model="admin.timeSpend" style="width: 150px" :min="1" @change="checkTaskEndTime" :disabled="isEdit"></el-input-number> 分钟
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
  </div>
</template>
<script>
  import {fetchTaskList,createTask,updateTask,deleteTask,fetchAllCategoryList, fetchAllPriorityList,fetchMeetingRoomCount,fetchAvailableMeetingRoomCount, checkTimeConflict, getRemainingTimeForDate, getRemainingTimeForUserAndDate} from '@/api/api';
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
        this.$refs.adminForm.validate(valid => {
          if (valid) {
            // 检查会议室是否可用
            if (this.admin.needMeetingRoom && !this.isEdit) {
              // 对于新建任务，需要检查会议室是否还有剩余
              if (this.meetingRoomAvailable <= 0) {
                this.$message({
                  message: '当前没有可用的会议室，请调整需求或等待其他任务释放会议室',
                  type: 'warning'
                });
                return;
              }
            }
            
            // 先检查时间冲突
            checkTimeConflict(this.admin).then(response => {
              if (response.data && response.data.length > 0) {
                // 有冲突，显示冲突信息
                let conflictMessage = '存在以下时间冲突：\n';
                response.data.forEach(conflict => {
                  if (conflict.title === '工作时间超出范围') {
                    conflictMessage = conflict.description;
                  } else {
                    const startTime = this.$moment ? this.$moment(conflict.scheduleStartTime).format('HH:mm') : 
                      conflict.scheduleStartTime.split(' ')[1].substring(0, 5);
                    const endTime = this.$moment ? this.$moment(conflict.scheduleEndTime).format('HH:mm') : 
                      conflict.scheduleEndTime.split(' ')[1].substring(0, 5);
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
          this.admin.userData = JSON.stringify(this.admin.userIds);
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
        // 重置冲突状态
        this.timeConflicts = [];
        this.hasTimeConflict = false;
        this.showTimeWarning = false;
      },
      handleDelete(index, row) {
        this.$confirm('是否要删除该任务?删除后将无法撤销', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deleteTask(row.id).then(response => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.getList();
          });
        });
      },
      handleUpdate(row) {
        this.isEdit = true;
        try {
          row.userIds = JSON.parse(row.userData);
        } catch (error) {
          row.userIds = [];
        }
        this.admin = Object.assign({},row);
        this.dialogVisible = true;
        // 重置冲突状态
        this.timeConflicts = [];
        this.hasTimeConflict = false;
        this.showTimeWarning = false;
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
      // 添加时间检查方法
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
        // 如果有选择日期，则获取该日期剩余可安排时间
        if (date) {
          this.fetchRemainingTimeForDate(date);
        } else {
          this.remainingTimeForDate = null;
          this.exceedRemainingTimeWarning = false;
        }
        
        // 执行时间检查
        this.checkTaskEndTime();
      },
      
      fetchRemainingTimeForDate(date) {
        // 如果有选择的用户，则获取特定用户的剩余时间
        if (this.admin.userIds && this.admin.userIds.length > 0) {
          // 确保日期格式正确
          const formattedDate = date ? date.toString() : '';
          
          // 清空用户剩余时间对象
          this.userRemainingTimes = {};
          
          // 获取最小的剩余时间，用于检查是否超出限制
          let minRemainingTime = Number.MAX_SAFE_INTEGER;
          
          // 临时存储所有Promise
          const promises = [];
          
          // 获取每个用户的剩余时间
          this.admin.userIds.forEach(userId => {
            const promise = getRemainingTimeForUserAndDate(formattedDate, userId)
              .then(response => {
                // 保存用户剩余时间
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
        // 当用户选择变化时，如果有开始日期，则重新获取剩余时间
        if (this.admin.startTime) {
          this.fetchRemainingTimeForDate(this.admin.startTime);
        } else {
          this.userRemainingTimes = {};
        }
        
        // 执行时间检查
        this.checkTaskEndTime();
      },
      
      getUserName(userId) {
        // 根据用户ID获取用户名称
        const user = this.userList.find(u => u.id === userId);
        return user ? user.nickName : `用户${userId}`;
      },
    }
  }
</script>
<style>
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

.meeting-room-warning {
  margin-left: 10px;
  color: #E6A23C;
  font-size: 13px;
}

.create-task-btn {
  font-weight: bold;
  font-size: 14px;
  padding: 8px 16px;
  border-width: 2px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: all 0.3s;
}

.create-task-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

.operate-container {
  margin-bottom: 15px;
}

.table-container {
  margin-bottom: 20px;
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

.time-warning {
  margin-left: 10px;
  color: #E6A23C;
  font-size: 13px;
}

.time-conflicts {
  margin-top: 10px;
  padding: 10px;
  background-color: #f0f0f0;
  border-radius: 4px;
}

.conflict-header {
  font-weight: bold;
  margin-bottom: 10px;
}

.conflict-item {
  margin-bottom: 5px;
}

.user-remaining-times {
  margin-top: 10px;
  padding: 10px;
  background-color: #f0f0f0;
  border-radius: 4px;
}

.user-time-header {
  font-weight: bold;
  margin-bottom: 10px;
}

.user-time-item {
  margin-bottom: 5px;
}

.remaining-time-info {
  margin-left: 10px;
  color: #606266;
  font-size: 13px;
}
</style>
