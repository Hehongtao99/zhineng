<template> 
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <el-descriptions title="用户信息" :column="2" border>
            <!-- 使用 extra 插槽添加编辑按钮 -->
        <template #extra>
          <el-button icon="el-icon-edit" type="primary" @click="handleUpdate">编辑信息</el-button>
        </template>
        <el-descriptions-item label="ID">{{ userInfo.id }}</el-descriptions-item>
        <el-descriptions-item label="头像">
          <img :src="userInfo.icon" alt="用户头像" style="width: 50px; height: 50px; border-radius: 50%;" />
        </el-descriptions-item>
        <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ userInfo.nickName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ userInfo.gender }}</el-descriptions-item>
        <el-descriptions-item label="生日">{{ userInfo.birthday }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ userInfo.age }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ userInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ userInfo.address }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ userInfo.note }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ userInfo.createTime | formatDateTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
            {{ userInfo.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    <el-dialog
      title="编辑信息"
      :visible.sync="dialogVisible"
      width="40%">
      <el-form :model="admin"
              ref="adminForm"
              label-width="150px"
              size="small"
              :rules="adminRules">
        <el-form-item label="帐号：" prop="username">
          <el-input v-model="admin.username" disabled style="width: 250px"></el-input>
        </el-form-item>
        <el-form-item label="名字：" prop="nickName">
          <el-input v-model="admin.nickName" style="width: 250px"></el-input>
        </el-form-item>
        <el-form-item label="性别：" prop="sex">
          <el-select style="width: 250px" v-model="admin.gender" placeholder="请选择">
              <el-option
                value="男">
              </el-option>
              <el-option
                value="女">
              </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="地址：" prop="address">
          <el-input v-model="admin.address" style="width: 250px"></el-input>
        </el-form-item>
        <el-form-item label="部门职位：" prop="note">
          <el-input v-model="admin.note" type="textarea" :rows="5" style="width: 250px"></el-input>
        </el-form-item>
        <el-form-item label="头像：" prop="avatar">
          <el-upload
            class="avatar-uploader"
            accept="image/png, image/jpeg"
            action="http://localhost:8080/common/upload"
            :show-file-list="false"
            :on-success="handleSuccess">
            <img v-if="admin.icon" :src="admin.icon" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="生日：" prop="birthday">
          <el-date-picker
            v-model="admin.birthday"
            type="date"
            placeholder="选择生日">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="年龄：" prop="age">
          <el-input-number v-model="admin.age" controls-position="right" :min="18" :max="100"></el-input-number> 岁
        </el-form-item>
        <el-form-item label="手机号：" prop="phone">
          <el-input v-model="admin.phone" style="width: 250px"></el-input>
        </el-form-item>
        <el-form-item label="密码：" prop="password">
          <el-input v-model="admin.password" type="password" style="width: 250px"></el-input>
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
  import {getUserInfo, updateAdmin} from '@/api/login';
  import {formatDate} from '@/utils/date';

  const defaultAdmin = {
    id: null,
    username: null,
    password: null,
    nickName: null,
    avatar: null,
    age: null,
    phone: null,
    sex: null,
    enabled: null,
    createTime: null,
  };
  export default {
    name: 'adminList',
    data() {
      return {
        dialogVisible: false,
        admin: Object.assign({}, defaultAdmin),
        adminRules: {
          username: [
            { required: true, message: '请输入帐号', trigger: 'blur' }
          ],
          nickName: [
            { required: true, message: '请输入昵称', trigger: 'blur' }
          ],
          address: [
            { required: true, message: '请输入地址', trigger: 'blur' }
          ],
          note: [
            { required: true, message: '请输入部门职位', trigger: 'blur' }
          ],
          gender: [
            { required: true, message: '请选择性别', trigger: 'change' }
          ],
          icon: [
            { required: true, message: '请上传头像', trigger: 'change' }
          ],
          age: [
            { required: true, message: '请输入年龄', trigger: 'blur' }
          ],
          phone: [
            { required: true, message: '请输入手机号', trigger: 'blur' },
            { pattern: /^1[3456789]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '请输入密码', trigger: 'blur' },
            { min: 3, message: '密码至少为三位数', trigger: 'blur' }
          ]
        },

        userInfo: {},
      }
    },
    created() {
      this.fetchUserInfo();
    },
    filters: {
      formatDateTime(time) {
        if (time == null || time === '') {
          return 'N/A';
        }
        let date = new Date(time);
        return formatDate(date, 'yyyy-MM-dd HH:mm:ss')
      },
    },
    methods: {
      fetchUserInfo() {
        getUserInfo().then(response => {
          this.userInfo = response.data;
        }).catch(error => {
          console.error('Error fetching user info:', error);
        });
      },
      handleUpdate(index, row) {
        this.dialogVisible = true;
        this.admin = Object.assign({},this.userInfo);
      },
      handleDialogConfirm() {
        this.$refs.adminForm.validate(valid => {
          if (valid) {
            this.$confirm('是否要确认?', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              updateAdmin(this.admin.id, this.admin).then(response => {
                this.$message({
                  message: '修改成功！',
                  type: 'success'
                });
                this.fetchUserInfo();
                this.dialogVisible = false;
              })
            })
          }
        });
      },
      handleSuccess(response, file) {
        const filePath = response.data;
        this.admin.icon = `${process.env.BASE_API}/${filePath}`;
        console.error(filePath);
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
</style>
