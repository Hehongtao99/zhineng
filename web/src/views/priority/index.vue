<template> 
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
        <div>
          <i class="el-icon-search"></i>
          <span>筛选搜索</span>
        </div>
        <div style="margin-top: 15px">
          <el-form :inline="true" :model="listQuery" size="small" label-width="120px">
            <el-form-item label="类型：">
              <el-select filterable clearable v-model="listQuery.type" @change="getList" placeholder="全部">
                <el-option
                label="重要性"
                :value="1">
                </el-option>
                <el-option
                label="紧急性"
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
              size="small">
              查询搜索
            </el-button>
          </el-form>
        </div>
      </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>权重列表</span>
      <el-button size="small" class="btn-add" @click="handleAdd()" style="margin-left: 20px" type="success"><i class="el-icon-plus">创建权重</i></el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="adminTable"
                :data="list"
                style="width: 100%;"
                v-loading="listLoading" stripe>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{scope.row.id}}</template>
        </el-table-column>
        <el-table-column label="权重类型" width="140" align="center">
          <template slot-scope="scope">{{scope.row.type == 1 ? '重要性' : '紧急性'}}</template>
        </el-table-column>
        <el-table-column label="权重名称" width="200" align="center" prop="name">
        </el-table-column>
        <el-table-column label="权重分数" width="100" align="center">
          <template slot-scope="scope"><el-tag type="info">{{scope.row.score}} 分</el-tag></template>
        </el-table-column>
        <el-table-column label="权重占比" width="100" align="center">
          <template slot-scope="scope"><el-tag type="info">{{scope.row.weight}}</el-tag></template>
        </el-table-column>
        <el-table-column label="权重描述" align="center">
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
      :title="isEdit?'编辑权重':'创建权重'"
      :visible.sync="dialogVisible"
      width="40%">
      <el-form :model="admin"
              ref="adminForm"
              label-width="100px"
              size="small"
              :rules="adminRules">
        <el-form-item label="类型：" prop="type">
          <el-select v-model="admin.type" placeholder="选择类型" @change="handleSelectType" :disabled="isEdit">
            <el-option
            label="重要性"
            :value="1">
            </el-option>
            <el-option
            label="紧急性"
            :value="2">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="名称：" prop="name">
          <el-input v-model="admin.name"></el-input>
        </el-form-item>
        <el-form-item label="类型：" prop="categoryId" v-if="this.admin.type==1">
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
        <el-form-item label="时间范围最小值：" prop="minHours" v-if="this.admin.type==2">
          <el-input-number v-model="admin.minHours" :min="0"></el-input-number> 小时
        </el-form-item>
        <el-form-item label="时间范围最大值：" prop="maxHours" v-if="this.admin.type==2">
          <el-input-number v-model="admin.maxHours" :min="0"></el-input-number> 小时
        </el-form-item>
        <el-form-item label="分数：" prop="score">
          <el-input-number v-model="admin.score" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="占比：" prop="weight">
          <el-input-number v-model="admin.weight" :min="0.01" :max="1" :precision="2" :step="0.01"></el-input-number>
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
  import {fetchPriorityList,createPriority,updatePriority,deletePriority,fetchAllCategoryList} from '@/api/api';

  const defaultListQuery = {
    pageNum: 1,
    pageSize: 10,
    searchKey: null,
    type: null,
  };
  const defaultAdmin = {
    id: null,
    name: null,
    description: null,
    type: null,
    score: null,
    weight: null,
    categoryId: null,
    minHours: null,
    maxHours: null,
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
            { required: true, message: '请输入权重名称', trigger: 'blur' }
          ],
          type: [
            { required: true, message: '请选择权重类型', trigger: 'change' }
          ],
          description: [
            { required: true, message: '请输入权重描述', trigger: 'blur' }
          ],
        },
        categoryList: [],
      }
    },
    created() {
      this.getList();
      this.getCategoryList();
    },
    methods: {
      getCategoryList() {
        fetchAllCategoryList().then(res => {
          this.categoryList = res.data
        })
      },
      handleSelectType() {
        this.admin.categoryId = null;
        this.admin.minHours = null;
        this.admin.maxHours = null;
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
      handleAdd() {
        this.dialogVisible = true;
        this.isEdit = false;
        this.admin = Object.assign({}, defaultAdmin);
      },
      handleDelete(index, row) {
        this.$confirm('是否要删除该权重?删除后将无法撤销', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deletePriority(row.id).then(response => {
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
                updatePriority(this.admin).then(response => {
                  this.$message({
                    message: '修改成功！',
                    type: 'success'
                  });
                  this.dialogVisible = false;
                  this.getList();
                })
              } else {
                createPriority(this.admin).then(response => {
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
        fetchPriorityList(this.listQuery).then(response => {
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
