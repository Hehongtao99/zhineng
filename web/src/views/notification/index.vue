<template> 
  <div class="app-container">
    <el-card class="filter-container" shadow="hover">
      <div class="filter-header">
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
      </div>
      <div class="filter-content">
        <el-form :inline="true" :model="listQuery" size="small" label-width="120px">
          <el-form-item label="状态：">
            <el-select filterable clearable v-model="listQuery.status" @change="getList" placeholder="全部">
              <el-option
              label="未读"
              :value="1">
              </el-option>
              <el-option
              label="已读"
              :value="2">
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
            size="small"
            class="search-btn">
            查询搜索
          </el-button>
        </el-form>
      </div>
    </el-card>
    
    <el-card class="operate-container" shadow="hover">
      <div class="operate-header">
        <i class="el-icon-bell"></i>
        <span>系统通知</span>
      </div>
      <div class="operate-actions">
        <el-button size="small" class="btn-add" @click="handleAdd()" type="primary">
          <i class="el-icon-plus"></i>创建通知
        </el-button>
        <el-button 
          size="small" 
          class="btn-refresh" 
          @click="getList()" 
          type="info" 
          icon="el-icon-refresh" 
          plain>刷新</el-button>
      </div>
    </el-card>
    
    <!-- 现代化的通知展示区域 -->
    <div class="notification-container" v-loading="listLoading">
      <div v-if="!list || list.length === 0" class="empty-state">
        <el-empty description="暂无通知">
          <el-button type="primary" @click="handleAdd" size="small">创建通知</el-button>
        </el-empty>
      </div>
      
      <div v-else class="notification-list">
        <transition-group name="notification-fade" tag="div">
          <div 
            v-for="notification in list" 
            :key="notification.id" 
            class="notification-item"
            :class="{'unread': notification.status === 1}">
            
            <div class="notification-icon">
              <el-avatar :size="40" :class="notification.type === 1 ? 'task-avatar' : 'system-avatar'">
                <i :class="notification.type === 1 ? 'el-icon-date' : 'el-icon-message'"></i>
              </el-avatar>
              <div v-if="notification.status === 1" class="unread-indicator"></div>
            </div>
            
            <div class="notification-content">
              <div class="notification-header">
                <span class="notification-title">
                  {{ notification.type === 1 ? `[任务] ${notification.taskName || '未指定任务'}` : '[系统] 通知' }}
                </span>
                <span class="notification-time">{{ formatDateTime(notification.createTime) }}</span>
              </div>
              
              <div class="notification-message">{{ notification.message }}</div>
              
              <div class="notification-meta">
                <el-tag size="mini" type="info">接收用户: {{ notification.userName }}</el-tag>
              </div>
              
              <div class="notification-actions">
                <el-button 
                  type="text" 
                  size="mini" 
                  class="edit-btn" 
                  @click="handleUpdate(null, notification)">
                  <i class="el-icon-edit"></i> 编辑
                </el-button>
                <el-button 
                  type="text" 
                  size="mini" 
                  class="delete-btn" 
                  @click="handleDelete(null, notification)">
                  <i class="el-icon-delete"></i> 删除
                </el-button>
                <el-tag 
                  size="mini" 
                  :type="notification.status === 1 ? 'danger' : 'info'"
                  effect="plain">
                  {{ notification.status === 1 ? '未读' : '已读' }}
                </el-tag>
              </div>
            </div>
          </div>
        </transition-group>
      </div>
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
      :title="isEdit?'编辑通知':'创建通知'"
      :visible.sync="dialogVisible"
      width="40%">
      <el-form :model="admin"
              ref="adminForm"
              label-width="100px"
              size="small"
              :rules="adminRules">
        <el-form-item label="类型：" prop="type">
          <el-select v-model="admin.type" placeholder="选择通知类型">
            <el-option
            label="任务提醒"
            :value="1">
            </el-option>
            <el-option
            label="系统通知"
            :value="2">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="用户：" prop="userId">
          <el-select v-model="admin.userId" placeholder="选择接收用户" filterable>
            <el-option
              v-for="(item, index) in userList"
              :key="index"
              :label="item.username"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="admin.type === 1" label="任务：" prop="taskId">
          <el-select v-model="admin.taskId" placeholder="选择关联任务" filterable>
            <el-option
              v-for="(item, index) in taskList"
              :key="index"
              :label="item.title"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="通知内容：" prop="message">
          <el-input type="textarea" :rows="5" v-model="admin.message"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="handleDialogConfirm()" size="small">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import {fetchNotificationList, createNotification, updateNotification, deleteNotification, fetchAllTaskList, fetchUserList} from '@/api/api';
  import {formatDate} from '@/utils/date';

  const defaultListQuery = {
    pageNum: 1,
    pageSize: 10,
    searchKey: null,
    status: null,
  };
  const defaultAdmin = {
    id: null,
    userId: null,
    taskId: null,
    type: 2,
    message: null,
    status: 1
  };
  export default {
    name: 'notificationList',
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
          userId: [
            { required: true, message: '请选择接收用户', trigger: 'change' }
          ],
          type: [
            { required: true, message: '请选择通知类型', trigger: 'change' }
          ],
          message: [
            { required: true, message: '请输入通知内容', trigger: 'blur' }
          ],
          taskId: [
            { required: false, message: '请选择关联任务', trigger: 'change' }
          ]
        },
        taskList: [],
        userList: []
      }
    },
    created() {
      this.getList();
      this.getUserList();
      this.getTaskList();
    },
    methods: {
      formatDateTime(time) {
        if (time == null || time === '') {
          return 'N/A';
        }
        let date = new Date(time);
        return formatDate(date, 'yyyy-MM-dd HH:mm');
      },
      
      async getUserList() {
        try {
          const response = await fetchUserList({pageSize: 100});
          this.userList = response.data.list || [];
        } catch(error) {
          console.error('获取用户列表失败:', error);
        }
      },
      
      async getTaskList() {
        try {
          const response = await fetchAllTaskList();
          this.taskList = response.data || [];
        } catch(error) {
          console.error('获取任务列表失败:', error);
        }
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
      
      handleAdd() {
        this.dialogVisible = true;
        this.isEdit = false;
        this.admin = Object.assign({}, defaultAdmin);
      },
      
      handleDelete(index, row) {
        this.$confirm('是否要删除该通知?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deleteNotification(row.id).then(response => {
            this.$message({
              message: '删除成功!',
              type: 'success'
            });
            this.getList();
          });
        });
      },
      
      handleUpdate(index, row) {
        this.dialogVisible = true;
        this.isEdit = true;
        this.admin = Object.assign({}, row);
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
                updateNotification(this.admin).then(response => {
                  this.$message({
                    message: '修改成功！',
                    type: 'success'
                  });
                  this.dialogVisible = false;
                  this.getList();
                })
              } else {
                createNotification(this.admin).then(response => {
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
        this.listLoading = true;
        fetchNotificationList(this.listQuery).then(response => {
          this.listLoading = false;
          this.list = response.data.list;
          this.total = response.data.total;
        });
      }
    }
  }
</script>

<style>
.app-container {
  padding: 20px;
  background-color: #F5F7FA;
}

/* 头部卡片样式 */
.filter-container, .operate-container {
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

.operate-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.operate-actions .el-button {
  margin-left: 10px;
}

.search-btn {
  background: #409EFF;
  border-color: #409EFF;
  border-radius: 4px;
}

/* 通知列表样式 */
.notification-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  min-height: 200px;
  padding: 0;
  overflow: hidden;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.notification-list {
  padding: 0;
}

.notification-item {
  display: flex;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f2f5;
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
}

.notification-item:hover {
  background-color: #f9fafc;
}

.notification-item.unread {
  background-color: #f0f7ff;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-icon {
  position: relative;
  margin-right: 16px;
}

.notification-content {
  flex: 1;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.notification-title {
  font-weight: 500;
  color: #303133;
  font-size: 15px;
}

.notification-time {
  color: #909399;
  font-size: 13px;
}

.notification-message {
  color: #606266;
  margin-bottom: 12px;
  line-height: 1.5;
}

.notification-meta {
  margin-bottom: 10px;
}

.notification-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.notification-actions .el-button {
  margin-left: 16px;
}

.edit-btn {
  color: #E6A23C;
}

.delete-btn {
  color: #F56C6C;
}

.task-avatar {
  background-color: #409EFF;
  color: white;
}

.system-avatar {
  background-color: #67C23A;
  color: white;
}

.unread-indicator {
  position: absolute;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #F56C6C;
  top: 0;
  right: 0;
  border: 2px solid white;
}

/* 动画效果 */
.notification-fade-enter-active, .notification-fade-leave-active {
  transition: all 0.5s;
}

.notification-fade-enter, .notification-fade-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

/* 分页样式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
