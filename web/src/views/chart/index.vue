<template>
  <div class="app-container">
    <div class="statistics-layout">
      <div class="layout-title"><i class="el-icon-s-data"></i> 统计任务</div>
      <el-row>
        <el-col :span="20">
          <div style="padding: 10px;border-left:1px solid #DCDFE6">
            <el-date-picker
              style="float: right;z-index: 1"
              size="small"
              v-model="countBorrowDate"
              type="daterange"
              align="right"
              unlink-panels
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="handleBorrowDateChange"
              :picker-options="pickerOptions">
            </el-date-picker>
            <div style="margin-top: 10px;">
              <ve-line
                :data="borrowData"
                :legend-visible="true"
                :loading="loadingBorrow"
                :data-empty="borrowDataEmpty"
                :settings="borrowSettings"></ve-line>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>  
    <i class="el-icon-s-tools"></i> 设置最大显示数量：<el-input-number v-model="showNum" controls-position="right" @change="handleChange" :min="3" :max="99"></el-input-number>
    <el-tooltip  effect="dark" content="设置超过此数字时使用其他代替（此时数据会按照由大到小顺序显示）" placement="top-start">
      <i class="el-icon-warning"></i>
    </el-tooltip>
    <div class="statistics-layout">
      <div class="layout-title"><i class="el-icon-pie-chart"></i> 任务类型统计</div>
      <el-row>
        <el-col :span="20">
          <div style="padding: 10px;border-left:1px solid #DCDFE6">
            <div style="margin-top: 10px;">
              <ve-pie
                :data="classData"
                :legend-visible="true"
                :loading="loading"
                :data-empty="dataEmpty"
                :settings="trendPieSettings"></ve-pie>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

  </div>
</template>

<script>
  import { trendPie, trend} from '@/api/api';
  import {formatDate} from '@/utils/date';

  export default {
    name: 'Statistics',
    data() {
      return {
        pickerOptions: {
          shortcuts: [{
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              let start = new Date();
              start.setTime(end.getTime() - 1000 * 60 * 60 * 24 * 7)
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一月',
            onClick(picker) {
              const end = new Date();
              let start = new Date();
              start.setTime(end.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一年',
            onClick(picker) {
              const end = new Date();
              let start = new Date();
              start.setTime(end.getTime() - 3600 * 1000 * 24 * 30 * 12);
              picker.$emit('pick', [start, end]);
            }
          }]
        },
        showNum: 5,
        classData: {
          columns: ['name', 'count'],
          rows: []
        },
        loading: false,
        dataEmpty: false,
        trendPieSettings: {
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
          limitShowNum: 5,
          dimension: 'name',
          metrics: 'count',
        },
        data: {},
        countBorrowDate: '',
        loadingBorrow: true,
        borrowDataEmpty: false,
        borrowSettings: {
          xAxisType: 'time',
          area: true,
          axisSite: { right: ['countRight']},
          labelMap: {'countLeft': '逾期', 'countRight': '已完成'},
          yAxisName: ['逾期','已完成'],
          itemStyle: {
            borderWidth: 2
          },
          lineStyle: {
            width: 2
          }
        },
        borrowData: {
          columns: ['date', 'countLeft','countRight'],
          rows: []
        },
      }
    },
    created(){
      this.initCountDate();
      this.getTrendPie();
      this.getTrend();
    },
    methods:{
      initCountDate(){
        const end = new Date();
        let start = new Date();
        start.setTime(end.getTime() - 1000 * 60 * 60 * 24 * 30)
        this.countBorrowDate=[start,end];
      },
      handleChange(value) {
        this.trendPieSettings.limitShowNum = value;
        this.getTrendPie();
      },
      getTrendPie() {
        this.loading = true;
        this.dataEmpty = false;
        setTimeout(() => {
          trendPie().then(response => {
            if (response.data && response.data.length > 0) {
              this.classData.rows = response.data;
              this.dataEmpty = false;
            } else {
              this.dataEmpty = true;
              this.classData.rows = [];
            }
            this.loading = false;
          }).catch(error => {
            console.error("获取饼图数据失败:", error);
            this.dataEmpty = true;
            this.loading = false;
          });
        }, 100);
      },
      getTrend() {
        this.loadingBorrow = true;
        this.borrowDataEmpty = false;
        setTimeout(() => {
          const startDate = formatDate(this.countBorrowDate[0], 'yyyy-MM-dd');
          const endDate = formatDate(this.countBorrowDate[1], 'yyyy-MM-dd');
          trend({startDate, endDate}).then(response => {
            if (response.data && response.data.length > 0) {
              this.borrowData.rows = response.data;
              this.borrowDataEmpty = false;
            } else {
              this.borrowDataEmpty = true;
              this.borrowData.rows = [];
            }
            this.loadingBorrow = false;
          }).catch(error => {
            console.error("获取折线图数据失败:", error);
            this.borrowDataEmpty = true;
            this.loadingBorrow = false;
          });
        }, 100);
      },
      handleBorrowDateChange(){
        this.getTrend();
      },
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
  }
</style>
