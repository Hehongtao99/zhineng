<template>
  <div class="app-container">
    <!-- 饼图部分 -->
    <div class="statistics-layout">
      <div class="layout-title"><i class="el-icon-pie-chart"></i> 我的任务类型统计</div>
      <el-row>
        <el-col :span="20">
          <div style="padding: 10px;border-left:1px solid #DCDFE6">
            <div style="margin-top: 10px;">
              <ve-pie
                :data="pieData"
                :legend-visible="true"
                :loading="pieLoading"
                :data-empty="pieDataEmpty"
                :settings="pieSettings"></ve-pie>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 折线图部分 -->
    <div class="statistics-layout">
      <div class="layout-title"><i class="el-icon-s-data"></i> 我的已完成任务统计</div>
      <el-row>
        <el-col :span="20">
          <div style="padding: 10px;border-left:1px solid #DCDFE6">
            <el-date-picker
              style="float: right;z-index: 1"
              size="small"
              v-model="dateRange"
              type="daterange"
              align="right"
              unlink-panels
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="handleDateChange"
              :picker-options="pickerOptions">
            </el-date-picker>
            <div style="margin-top: 40px;">
              <ve-line
                :data="chartData"
                :settings="chartSettings"
                :loading="loading"
                :data-empty="dataEmpty"
                height="400px"></ve-line>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { userTrend, trendPie } from '@/api/api';
import { formatDate } from '@/utils/date';
import 'echarts/lib/component/title'; // 引入标题组件

export default {
  name: 'UserReport',
  data() {
    this.chartSettings = {
      labelMap: {
        'countPending': '待办任务数',
        'countLeft': '逾期任务数',
        'countRight': '已完成任务数'
      },
      yAxisName: ['任务数量'],
      area: true, // 显示面积图
      itemStyle: {
          borderWidth: 2
      },
      lineStyle: {
          width: 2
      }
    };
    this.pieSettings = {
      dataType: 'normal',
      selectedMode: 'multiple',
      itemStyle: {
        borderColor: '#fff',
        borderWidth: 1
      },
      label: {
        alignTo: 'edge',
        formatter: '{name|{b}}\n{count|{c}个任务}',
        minMargin: 5,
        edgeDistance: 10,
        lineHeight: 15,
        rich: {
          time: {
            fontSize: 10,
            color: '#999'
          }
        }
      },
      labelLine: {
        length: 40,
        length2: 150,
        maxSurfaceAngle: 80
      },
      dimension: 'name',
      metrics: 'count',
    };
    return {
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
      dateRange: '',
      loading: false,
      dataEmpty: false,
      chartData: {
        columns: ['date', 'countPending', 'countLeft', 'countRight'],
        rows: []
      },
      // 饼图数据
      pieLoading: false,
      pieDataEmpty: false,
      pieData: {
        columns: ['name', 'count'],
        rows: []
      }
    }
  },
  created() {
    this.initDateRange();
    this.fetchUserTrendData();
    this.fetchPieData();
  },
  methods: {
    initDateRange() {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30); // 默认显示最近一个月
      this.dateRange = [start, end];
    },
    handleDateChange() {
      this.fetchUserTrendData();
    },
    fetchUserTrendData() {
      if (!this.dateRange || this.dateRange.length !== 2) {
        return;
      }
      this.loading = true;
      this.dataEmpty = false;
      const startDate = formatDate(this.dateRange[0], 'yyyy-MM-dd');
      const endDate = formatDate(this.dateRange[1], 'yyyy-MM-dd');
      
      userTrend({ startDate, endDate }).then(response => {
        console.log("用户完成任务统计 API 响应:", response); 
        if (response && Array.isArray(response.data)) { 
          if (response.data.length > 0) {
            this.chartData.rows = response.data.map(item => ({
                date: item.date,
                countPending: item.countPending || 0,
                countLeft: item.countLeft || 0, // 逾期
                countRight: item.countRight || 0 // 已完成
            }));
            this.dataEmpty = false;
          } else {
            this.chartData.rows = [];
            this.dataEmpty = true;
          }
        } else {
          console.error("API 返回的数据格式不正确或 data 不是数组:", response);
          this.chartData.rows = [];
          this.dataEmpty = true;
        }
        this.loading = false;
      }).catch(error => {
        console.error("获取用户完成任务统计失败:", error);
        this.chartData.rows = [];
        this.dataEmpty = true;
        this.loading = false;
        this.$message.error('加载数据失败');
      });
    },
    fetchPieData() {
      this.pieLoading = true;
      this.pieDataEmpty = false;
      trendPie().then(response => {
        if (response.data && response.data.length > 0) {
          this.pieData.rows = response.data;
          this.pieDataEmpty = false;
        } else {
          this.pieData.rows = [];
          this.pieDataEmpty = true;
        }
        this.pieLoading = false;
      }).catch(error => {
        console.error("获取饼图数据失败:", error);
        this.pieData.rows = [];
        this.pieDataEmpty = true;
        this.pieLoading = false;
        this.$message.error('加载饼图数据失败');
      });
    }
  }
}
</script>

<style scoped>
.app-container {
  margin-top: 20px;
  margin-left: 60px;
  margin-right: 60px;
}

.layout-title {
  color: #606266;
  padding: 15px 20px;
  background: #F2F6FC;
  font-weight: bold;
}

.statistics-layout {
  margin-top: 20px;
  border: 1px solid #DCDFE6;
  margin-bottom: 30px;
}
</style> 