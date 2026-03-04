<template>
  <div>
    <el-row :gutter="10">
      <el-col :span="6" >
        <el-card style="max-width: 480%">
          <template #header>
            <div class="card-header">
              <span>今日营业额</span>
            </div>
          </template>
          <p>￥{{ daily_data.total.toFixed(2) }}</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="max-width: 480%">
          <template #header>
            <div class="card-header">
              <span>今日订单数</span>
            </div>
          </template>
          <p>{{ daily_data.order_count }}</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="max-width: 480%">
          <template #header>
            <div class="card-header">
              <span>今日新增用户</span>
            </div>
          </template>
          <p>{{ daily_data.user_count }}位</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="max-width: 480%">
          <template #header>
            <div class="card-header">
              <span>今日新增农产品数量</span>
            </div>
          </template>
          <p>{{ daily_data.product_count }}个</p>
        </el-card>
      </el-col>
    </el-row>

    <el-col>
      <el-row>
        <div id="month_order"></div>
      </el-row>
    </el-col>
    <el-row>
      <el-col :span="10">
        <div id="categories"></div>
      </el-col>
      <el-col :span="14">
        <div id="month_user"></div>
      </el-col>
    </el-row>
  </div>
</template>
<script setup>
import {ref,onMounted} from 'vue'
import {selectDailyData,selectMonthOrder,selectCategories} from "@/api/dashboard.js"
import * as echarts from 'echarts';

const  daily_data=ref({
  total:1000,
  order_count:2,
  user_count:34,
  product_count:33
});


//当月营业额的数据
const month_order=ref({
  x_data:[],
  y_data:[],
});


//当月营业额的数据
const month_user=ref({
  x_data:[],
  y_data:[],
});

const categories=ref([

])

// 初始化 echarts 图表（修正语法和参数问题）
const initCharts = (id,options) => {
  const chartDom = document.getElementById(id);
  if (!chartDom) {
    console.error(`未找到 id 为 ${id} 的元素，图表初始化失败`);
    return null;
  }
  const myChart = echarts.init(chartDom);
  // 3. 监听窗口 resize，让图表自适应
  window.addEventListener('resize', () => {
    myChart.resize();
  });
  options && myChart.setOption(options);
  return myChart;
};

const option = {
  title: {
    show: true,
    text: '近一个月的营业额'
  },
  xAxis: {
    type: 'category',
    name: '订单日期',
    nameLocation: 'center',
    data: month_order.value.x_data,
  },
  yAxis: {
    type: 'value',
    name: '营业额/元',
    nameLocation: 'center'
  },
  series: [
    {
      data: month_order.value.y_data,
      type: 'bar'
    }
  ]
};

const option2 = {
  title: {
    text: '农产品分类统计',
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: 'Access From',
      type: 'pie',
      radius: '50%',
      data: categories.value
    }
  ]
};

const option3 = {
  title: {
    show: true,
    text: '近一个月新增用户数量'
  },
  xAxis: {
    type: 'category',
    name: '注册日期',
    nameLocation: 'center',
    data: month_order.value.x_data,
  },
  yAxis: {
    type: 'value',
    name: '数量',
    nameLocation: 'center'
  },
  series: [
    {
      data: month_order.value.y_data,
      type: 'line'
    }
  ]
};

onMounted(()=>{
  selectDailyData().then(res=>{
    daily_data.value=res.data;
  });


  //检索当月营业额
  selectMonthOrder().then(res=>{
    res.data.map(item=>{
      month_order.value.x_data.push(item.order_date);
      month_order.value.y_data.push(item.m)
    })
      initCharts("month_order",option)
  });

  //渲染饼图
  selectCategories().then(res=>{
    res.data.map(item=>{
      categories.value.push(item);
    });
    initCharts("categories",option2);
  })

  selectMonthOrder().then(res=>{
    res.data.map(item=>{
      month_user.value.x_data.push(item.order_date);
      month_user.value.y_data.push(item.m)
    })
    initCharts("month_user",option3)
  });
})
</script>
<style scoped>
#month_order,#categories,#month_user {
  width: 100%; /* 占满父容器宽度 */
  height: 340px; /* 固定高度 */
  background-color: white;
  display: block; /* 避免 inline 导致宽高失效 */
  box-sizing: border-box; /* 防止 padding/margin 影响宽高 */
}

</style>
