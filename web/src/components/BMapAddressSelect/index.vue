<template>
    <el-dialog title="位置选择" :visible.sync="openMap" width="900px" append-to-body>
      <el-form label-width="80px">
        <el-row>
          <el-col :span="10">
            <el-form-item label="搜索地址">
              <el-input size="mini" type="text" id="searchAddres" v-model="searchAddresKeywords" placeholder="请输入地点">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="14">
            <el-form-item label="当前地址">
              <el-input placeholder="请输入内容" v-model="addressInfo.address">
                <template slot="prepend">{{addressInfo.province}}{{addressInfo.city}}{{addressInfo.district}}</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div id="baidu-map-container" style="width: 100%; height: 400px;"></div>
   
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmSelect">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </template>
   
  <script>
  export default {
    data() {
      return {
        searchAddresKeywords: "",
        addressInfo: {
          // 地址信息
          longitude: "", // 经度
          latitude: "", // 纬度
          province: "", // 省
          city: "", // 市
          district: "", // 区
          address: "", // 详细地址
        },
        openMap: false,
      };
    },
    methods: {
      // 初始化百度地图
      initBaiduMap() {
        let that = this;
        this.$nextTick(function () {
          /** 初始化地图Start */
          var map = new BMap.Map("baidu-map-container"); // 创建地图实例
          var point = new BMap.Point(116.404, 39.915); // 设置中心点坐标
          map.centerAndZoom(point, 13); // 地图初始化，同时设置地图展示级别
          map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
          /** 初始化地图End */
   
          /** 点击地图创建坐标事件Start */
          // 添加地图点击事件
          map.addEventListener("click", function (e) {
            var clickpt = e.point; // 点击的坐标
            map.clearOverlays(); // 移除地图上的标注
            var marker = new BMap.Marker(clickpt); // 创建标注
            map.addOverlay(marker); // 将标注添加到地图中
            // 逆地址解析
            that.geocAddress(clickpt);
          });
          /** 点击地图创建坐标事件End */
   
          /** 搜索地址Start */
          // 建立一个自动完成的对象
          var ac = new BMap.Autocomplete({
            input: "searchAddres",
            location: map,
          });
          // 鼠标点击下拉列表后的事件
          ac.addEventListener("onconfirm", function (e) {
            map.clearOverlays(); // 移除地图上的标注
            var local = new BMap.LocalSearch(map, {
              //智能搜索
              onSearchComplete: function (results) {
                let poi = results.getPoi(0); //获取第一个智能搜索的结果
                var searchpt = poi.point; // 获取坐标
                map.centerAndZoom(searchpt, 16);
                map.addOverlay(new BMap.Marker(searchpt)); //添加标注
                that.geocAddress(searchpt); // 逆地址解析
              },
            });
            // 搜索词
            var searchValue = e.item.value;
            local.search(
              searchValue.province +
                searchValue.city +
                searchValue.district +
                searchValue.street +
                searchValue.business
            );
          });
          /** 搜索地址End */
        });
      },
   
      /** 逆向解析地址 point */
      geocAddress(point) {
        let that = this;
        var geoc = new BMap.Geocoder();
        geoc.getLocation(point, function (geocInfo) {
          // 设置基本信息
          var addressInfo = geocInfo.addressComponents;
          that.addressInfo.longitude = point.lng;
          that.addressInfo.latitude = point.lat;
          that.addressInfo.province = addressInfo.province;
          that.addressInfo.city = addressInfo.city;
          that.addressInfo.district = addressInfo.district;
          let address = addressInfo.street + addressInfo.streetNumber;
          if (geocInfo.surroundingPois.length > 0) {
            address = address + geocInfo.surroundingPois[0].title;
          }
          that.addressInfo.address = address;
        });
      },
   
      /** 打开地图选择 */
      show() {
        this.openMap = true;
        this.initBaiduMap();
      },
   
      /**
       * 确认选择
       */
      confirmSelect() {
          this.$emit("confirmMapAddress", this.addressInfo);
          this.openMap = false;
      },
   
      /**
       * 取消选择
       */
      cancel() {
          this.openMap = false;
      }
    },
  };
  </script>
   
  <style lang="scss">
  // 防止地图自动完成的对象被遮挡
  .tangram-suggestion {
    z-index: 9999;
  }
  </style>