<template>
  <div>
    <b-container :class="{'non-max-width': isMaximized}">
        <b-row class="pb-3">
          <b-col cols="6">
            <div ref="driveDatePicker" >
              <datepicker v-model="startDate" 
                          :disabledDates="disabledDates" 
                          :language="language" 
                          :format="datePickerFormat"
                          @input="onSelectDriveStartDate"
                          ></datepicker>
            </div>            
          </b-col>
          <b-col cols="5">
            <b-form-select v-model="selectedDriveHistoryNo" :options="driveStartTimeOptions" @input="onChangeDriveStartTime()"/>
            <b-form-checkbox value="test">테스트</b-form-checkbox>
          </b-col>          
          <b-col cols="1">
          </b-col>
        </b-row>
        <b-row class="border-bottom pb-3" align-v="end">
          <b-col cols="11">
            <div class="border">
              <b-form-group class="pt-4 pl-2 pr-2">
                <b-form-checkbox-group v-model="selectedSensors" name="selectedSensor">
                  <b-container>
                    <b-row class="pt-2">
                      <b-col cols="6" sm="4" md="3"
                            v-for="selectedSensorOption in selectSensorOptions"
                            :key="selectedSensorOption.value"
                            >
                        <!-- <label class="custom-control custom-checkbox custom-control-inline">
                          <input type="checkbox" name="selectedSensor" class="custom-control-input" :value="selectedSensorOption.value"><span class="custom-control-label">{{ selectedSensorOption.text }}</span>
                        </label> -->
                            <b-form-checkbox :value="selectedSensorOption.value">{{ selectedSensorOption.text }}</b-form-checkbox>
                      </b-col>
                    </b-row>
                  </b-container>
                </b-form-checkbox-group>
              </b-form-group>              
            </div>
          </b-col>
          <b-col cols="1">
            <b-button variant="primary" size="lg" @click="onSearchDriveHistory()">조회</b-button>
          </b-col>
        </b-row>        
        <b-row>
          <b-col cols="12 mt-3">
            <div>
            </div>
            <div>
              <table class="table table-sm table-hover table-bordered table-striped">
                <colgroup>
                  <col width="10%"/>
                  <col width="15%"/>
                  <col width="10%"/>
                  <col width="15%"/>
                  <col width="10%"/>
                  <col width="15%"/>
                  <col width="10%"/>
                  <col width="15%"/>
                </colgroup>
                <tbody>
                  <tr>
                    <th class="text-center">운행일자</th>
                    <td class="text-center" colspan="3">2018-04-14 13:00 ~ 2018-01-01 13:00</td>
                    <th class="text-center">운행지역</th>
                    <td class="text-center" colspan="3">서울시 논현동 ~ 서울시 서초동</td>
                  </tr>
                  <tr>
                    <th class="text-center">점검결과</th>
                    <td class="text-center" colspan="7">기계계통, 연료계통...</td>
                  </tr>
                  <tr>
                    <th class="text-center">운전자</th>
                    <td class="text-center">홍길동</td>
                    <th class="text-center">운행시간</th>
                    <td class="text-center">1시간 12분</td>
                    <th class="text-center">운행거리</th>
                    <td class="text-center" colspan="3">12 km</td>
                  </tr>
                </tbody>
              </table>  
            </div>
          </b-col>
        </b-row>
        <b-row align-h="center mt-3">
          <b-col>
            <div ref="sensorGraphGroup" class="graph-group-div"></div>
          </b-col>
        </b-row>
    </b-container>
  </div>
</template>
<script>
// import { validationMixin } from 'vuelidate'
// import { minLength, numeric, required } from 'vuelidate/lib/validators'
import { mapGetters } from 'vuex'
import Datepicker from 'vuejs-datepicker'
// import {ko} from 'vuejs-datepicker/dist/locale'
import { eventBus } from '../../common/EventBus.vue'
import graphOptions from './graphOptions'
import cf from '../../../utils/customFilters'
// import Dygraph from 'dygraphs'

export default {
  name: 'SensorGraph',

  components: {
    Datepicker,
  },

  props: {
    vehicleNo: null,
    isMaximized: false
  },

  data () {
    return {
      isFirstSearch: true,
      startDate: null,
      datePickerFormat: 'yyyy-MM-dd',
      // language: ko,
      disabledDates: {
        to: null,
        from: null,
        dates: []
      },
      selectedDriveHistoryNo: null,
      driveStartTimeOptions: [],
      selectSensorOptions: graphOptions.selectSensorOptions,
      selectedSensors: [
        "engineRpm", 
        "vehicleSpd"
      ],
      graphsColorSet: graphOptions.graphsColorSet
    }
  },

  computed: mapGetters({
    driveMgtPage: 'getDriveMgtPage',
  }),

  methods: {
    onSelectDriveStartDate: function() {
      let that = this
      this.$store.dispatch('loadDriveHistotyIndexes', {
        params: {
          vehicleNo: that.vehicleNo,
          startDate: cf.formatDate(that.startDate)
        },
        callback: () => {
          that.callbackOnSelectDriveStartDate()
        }
      })
    },

    callbackOnSelectDriveStartDate: function() {
      let seletionOptions = [
        { value: null, text: '주행 시작 시간을 선택하세요.' },
      ]
      let that = this
      let maxDriveHistoryNo = null
      this.driveMgtPage.driveHistoryIndexes.forEach(function(driveHistoryIndex) {
        if(maxDriveHistoryNo == null || maxDriveHistoryNo < driveHistoryIndex.driveHistoryNo) {
          maxDriveHistoryNo = driveHistoryIndex.driveHistoryNo
        }
        seletionOptions.push({
          value: driveHistoryIndex.driveHistoryNo,
          text: driveHistoryIndex.startDate + " " + driveHistoryIndex.startTime
        })
      })
      this.selectedDriveHistoryNo = maxDriveHistoryNo
      this.driveStartTimeOptions = seletionOptions
    },

    onSearchDriveHistory: function() {
      let that = this
      this.$store.dispatch('loadSensingRaw', {
        params: {
          vehicleNo: that.vehicleNo,
          driveHistoryNo: that.selectedDriveHistoryNo,
          fieldList: that.selectedSensors
        },
        callback: () => {
          that.drawSensorGraph()
        }
      })
    },
    onChangeDriveStartTime: function() {
      if (!this.isFirstSearch) return
      this.isFirstSearch = false
      this.onSearchDriveHistory()
    },
    drawSensorGraph: function() {
      let graphs = []
      this.$refs.sensorGraphGroup.innerHTML = ""
      for(let i = 0; i < this.driveMgtPage.sensingRawGraphs.length; i++) {
      // for(let i = 0; i < 1; i++) {        
        let divNode = document.createElement("div")
        divNode.setAttribute("id", "graphDiv-" + i)
        divNode.classList.add("graph-div")
        divNode.classList.add("mt-3")
        divNode.classList.add("mb-3")
        this.$refs.sensorGraphGroup.appendChild(divNode)


        google.charts.load('current', {'packages':['corechart']});
        google.charts.setOnLoadCallback(drawChart)
        let that = this

        function drawChart() {

          // Create the data table.
          var data = new google.visualization.DataTable();
          data.addColumn('datetime', 'Time');
          data.addColumn('number', that.driveMgtPage.sensingRawGraphs[i].fields[1]);
          data.addRows(that.driveMgtPage.sensingRawGraphs[i].data);

          // Set chart options
          var options = {
                        title:that.driveMgtPage.sensingRawGraphs[i].fields[1],
                        titleTextStyle: {
                          color: that.graphsColorSet[i]
                        },
                        height:150,
                        lineWidth: 1,
                        colors: that.graphsColorSet[i],
                        explorer: { 
                          actions: ['dragToZoom', 'rightClickToReset'],
                          axis: 'horizontal'
                          },
                        legend:{
                          position: 'none'
                        }
                        // hAxis: { 
                        //   ticks: [new Date(2014,3,15), 100] 
                        //   }
                        }

          // Instantiate and draw our chart, passing in some options.
          var chart = new google.visualization.LineChart(divNode)
          google.visualization.events.addListener(chart,'select',function() {
            graphs.forEach(function(element) {
            // parent.window.iframe_charts.forEach(function(element) {   
              element.setSelection(chart.getSelection()) 
              });
          })
          chart.draw(data, options)
        }

        //   new Dygraph(
        //     divNode,
        //     this.driveMgtPage.sensingRawGraphs[i].data,
        //     {
        //       labels: this.driveMgtPage.sensingRawGraphs[i].fields,
        //       colors: this.graphsColorSet[i],
        //       showRangeSelector: true,
        //       showInRangeSelector: true,
        //       rangeSelectorHeight: 30,
        //       // errorBars: true,
        //       legend: "always",
        //       labelsSeparateLines: true,
        //       connectSeparatedPoints: false,
        //     }          
        //   )
        // )
      }

      // if(graphs.length > 1) {
      //   let sync = Dygraph.synchronize(graphs,{
      //     selection: true,
      //     zoom: true,
      //     range: false
      //   })
      // } 
    }
  },

  created() {
    console.log('created SensorGraph')
    let that = this
    this.$store.dispatch('loadDriveHistoryStartDates', {
      vehicleNo: this.vehicleNo, 
      callback: () => {
        that.startDate = that.driveMgtPage.defaultStartDate
        that.disabledDates = {
          to: that.driveMgtPage.disabledMaxDate,
          from: that.driveMgtPage.disabledMinDate,
          dates: that.driveMgtPage.disabledDates
        }
        that.onSelectDriveStartDate()
      }
    })

    eventBus.$on('onMaximizeGraph', function(){
      that.onSearchDriveHistory()
    })
    
    eventBus.$on('onRestoreGraph', function(){
      that.onSearchDriveHistory()
    })
  },

  mounted() {
    this.$refs.driveDatePicker.getElementsByTagName('input')[0].classList.add('form-control')
  },
  // validations: {

  // }
}
</script> 
<style>
.graph-group-div {
  width: 100%;
}

.graph-div {
 height: 150px; 
 width: 100%;
}

.non-max-width {
  max-width: none;
}
</style>


