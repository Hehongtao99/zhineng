<template> 
  <div class="app-container">
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>类型列表</span>
      <el-button size="small" class="btn-add" @click="handleAdd()" style="margin-left: 20px" type="success"><i class="el-icon-plus">创建分类</i></el-button>
      <el-button size="small"  @click="getList()" style="margin-left: 20px" type="primary"><i class="el-icon-search">查询</i></el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="adminTable"
                :data="list"
                style="width: 100%;"
                v-loading="listLoading" stripe>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="类型名称" width="200" align="center" prop="name">
        </el-table-column>
        <el-table-column label="类型描述" align="center">
          <template slot-scope="scope">{{scope.row.description}}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini"
                       type="warning"
                       icon="el-icon-edit"
                       @click="handleUpdate(scope.$index, scope.row)">
              编辑
            </el-button>
            <el-button size="mini"
                       type="danger"
                       icon="el-icon-delete"
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
      :title="isEdit?'编辑分类':'创建分类'"
      :visible.sync="dialogVisible"
      width="40%">
      <el-form :model="admin"
              ref="adminForm"
              label-width="150px"
              size="small"
              :rules="adminRules">
        <el-form-item label="名称：" prop="name">
          <el-input v-model="admin.name"></el-input>
        </el-form-item>
        <el-form-item label="描述：" prop="description">
          <el-input type="textarea" :rows="5" v-model="admin.description"></el-input>
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
  import {fetchCategoryList,createCategory,updateCategory,deleteCategory} from '@/api/api';

  const defaultListQuery = {
    pageNum: 1,
    pageSize: 10,
  };
  const defaultAdmin = {
    id: null,
    name: null,
    description: null,
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
          name: [
            { required: true, message: '请输入分类名称', trigger: 'blur' }
          ],
          description: [
            { required: true, message: '请输入分类描述', trigger: 'blur' }
          ],
        }
      }
    },
    created() {
      this.getList();
    },
    methods: {
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
        this.$confirm('是否要删除该分类?删除后将无法撤销', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deleteCategory(row.id).then(response => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            this.getList();
          });
        });
      },
      handleUpdate(index, row) {
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
                updateCategory(this.admin).then(response => {
                  this.$message({
                    message: '修改成功！',
                    type: 'success'
                  });
                  this.dialogVisible = false;
                  this.getList();
                })
              } else {
                createCategory(this.admin).then(response => {
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
        fetchCategoryList(this.listQuery).then(response => {
          this.listLoading = false;
          this.list = response.data.list;
          this.total = response.data.total;
        });
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
  width: 300px;
  height: 178px;
  display: block;
}
</style>
