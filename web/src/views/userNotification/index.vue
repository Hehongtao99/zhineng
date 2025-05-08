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
        <span>我的通知</span>
      </div>
      <div class="operate-actions">
        <el-button 
          size="small" 
          class="btn-refresh" 
          @click="getList()" 
          type="primary" 
          icon="el-icon-refresh" 
          plain>刷新</el-button>
        <el-button 
          v-if="hasUnread" 
          size="small" 
          class="btn-read-all" 
          @click="handleReadAll()" 
          type="success" 
          icon="el-icon-check" 
          plain>全部标为已读</el-button>
      </div>
    </el-card>
    
    <!-- 现代化的通知展示区域 -->
    <div class="notification-container" v-loading="listLoading">
      <div v-if="!list || list.length === 0" class="empty-state">
        <el-empty description="暂无通知"></el-empty>
      </div>
      
      <div v-else class="notification-list">
        <transition-group name="notification-fade" tag="div">
          <div 
            v-for="notification in list" 
            :key="notification.id" 
            class="notification-item"
            :class="{'unread': notification.status === 1}"
            @click="handleView(notification.id)">
            
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
              
              <div class="notification-actions">
                <el-button 
                  v-if="notification.status === 1" 
                  type="text" 
                  size="mini" 
                  @click.stop="handleMarkAsRead(notification.id)">
                  <i class="el-icon-check"></i> 标记已读
                </el-button>
                <el-button 
                  type="text" 
                  size="mini" 
                  class="delete-btn" 
                  @click.stop="handleDelete(null, notification)">
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
    
    <!-- 通知详情对话框 -->
    <el-dialog
      title="通知详情"
      :visible.sync="detailDialogVisible"
      width="40%">
      <div v-if="currentNotification" class="notification-detail">
        <div class="detail-header">
          <el-tag :type="currentNotification.type === 1 ? 'primary' : 'success'">
            {{ currentNotification.type === 1 ? '任务通知' : '系统通知' }}
          </el-tag>
          <span class="detail-time">{{ formatDateTime(currentNotification.createTime) }}</span>
        </div>
        
        <div class="detail-content">
          <h3 class="detail-title">
            {{ currentNotification.type === 1 ? `[任务] ${currentNotification.taskName || '未指定任务'}` : '[系统] 通知' }}
          </h3>
          <p class="detail-message">{{ currentNotification.message }}</p>
        </div>
        
        <div class="detail-footer">
          <el-tag size="small" :type="currentNotification.status === 1 ? 'danger' : 'info'">
            {{ currentNotification.status === 1 ? '未读' : '已读' }}
          </el-tag>
        </div>
      </div>
      
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button 
          v-if="currentNotification && currentNotification.status === 1" 
          type="primary" 
          @click="handleMarkAsReadAndClose(currentNotification.id)">
          标记为已读并关闭
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import {fetchUserNotificationList, deleteNotification, markNotificationAsRead, markAllNotificationsAsRead} from '@/api/api';
  import {formatDate} from '@/utils/date';

  const defaultListQuery = {
    pageNum: 1,
    pageSize: 10,
    searchKey: null,
    status: null,
  };
  
  export default {
    name: 'userNotificationList',
    data() {
      return {
        listQuery: Object.assign({}, defaultListQuery),
        list: null,
        total: null,
        listLoading: false,
        detailDialogVisible: false,
        currentNotification: null
      }
    },
    computed: {
      hasUnread() {
        if (!this.list) return false;
        return this.list.some(item => item.status === 1);
      }
    },
    created() {
      this.getList();
    },
    methods: {
      formatDateTime(time) {
        if (time == null || time === '') {
          return 'N/A';
        }
        let date = new Date(time);
        return formatDate(date, 'yyyy-MM-dd HH:mm');
      },
      
      // 标记单个通知为已读
      handleMarkAsRead(id) {
        this.listLoading = true;
        markNotificationAsRead(id).then(response => {
          this.$message({
            message: '已标记为已读',
            type: 'success'
          });
          this.getList();
        }).catch(() => {
          this.listLoading = false;
        });
      },
      
      // 查看通知详情并标记为已读
      handleView(id) {
        const notification = this.list.find(item => item.id === id);
        if (!notification) return;
        
        // 设置当前通知并显示详情对话框
        this.currentNotification = notification;
        this.detailDialogVisible = true;
        
        // 如果是未读通知，标记为已读
        if (notification.status === 1) {
          this.handleMarkAsRead(id);
        }
      },
      
      // 标记为已读并关闭对话框
      handleMarkAsReadAndClose(id) {
        this.handleMarkAsRead(id);
        this.detailDialogVisible = false;
      },
      
      // 全部标记为已读功能
      handleReadAll() {
        this.$confirm('是否要将所有未读通知标记为已读?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        }).then(() => {
          this.listLoading = true;
          
          markAllNotificationsAsRead().then(response => {
            this.$message({
              message: '所有通知已标记为已读',
              type: 'success'
            });
            this.getList();
          }).catch(() => {
            this.listLoading = false;
            this.$message.error('操作失败，请稍后重试');
          });
        }).catch(() => {});
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
      
      getList() {
        this.listLoading = true;
        fetchUserNotificationList(this.listQuery).then(response => {
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

.notification-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.notification-actions .el-button {
  margin-left: 16px;
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

/* 通知详情对话框样式 */
.notification-detail {
  padding: 10px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.detail-time {
  color: #909399;
  font-size: 14px;
}

.detail-content {
  margin-bottom: 20px;
}

.detail-title {
  font-size: 18px;
  color: #303133;
  margin-bottom: 10px;
}

.detail-message {
  font-size: 15px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-line;
}

.detail-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}
</style>
