<template>
  <div>
    <b-container :class="{'non-max-width': isMaximized}">
        <b-row class="border-bottom pb-3">
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
          </b-col>
          <b-col cols="1">
          </b-col>
        </b-row>
        <b-row class="border-bottom py-3" align-v="end">
          <b-col cols="11">
            <div>
              <table class="table table-sm table-bordered table-striped">
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
                    <th class="text-center">차량번호</th>
                    <td class="text-center">{{ vehicle.licenseNo }}</td>
                    <th class="text-center">차종</th>
                    <td class="text-center">{{ vehicle.model.name + "(" + vehicle.modelYear + ")" }} </td>
                    <th class="text-center">총 주행거리</th>
                    <td class="text-center"> {{ formatNumber(parseInt(vehicle.currentMileage / 10)) }} km</td>
                  </tr>
                  <tr>
                    <th class="text-center">운전자</th>
                    <td class="text-center">{{ driveHistoryDetail && driveHistoryDetail.user ? driveHistoryDetail.user.name : "-" }}</td>
                    <th class="text-center">운행시간</th>
                    <td class="text-center">{{ driveHistoryDetail ? driveHistoryDetail.driveDuration : "-" }} </td>
                    <th class="text-center">운행거리</th>
                    <td class="text-center"> {{ driveHistoryDetail ? driveHistoryDetail.driveDistance / 10 : "-" }} km</td>
                  </tr>
                  <tr>
                    <th class="text-center">운행일자</th>
                    <td class="text-center" colspan="5">{{ driveHistoryDetail ? driveHistoryDetail.startDate + " " + driveHistoryDetail.startTime: "-" }} ~ {{ driveHistoryDetail ? formatDateTime(driveHistoryDetail.endTime) : "-" }} </td>
                  <tr>
                    <th class="text-center">메모</th>
                    <td colspan="5">
                      <b-row align-v="end">
                        <b-col cols="10">
                          <textarea class="form-control" v-model="form.memo"></textarea>
                        </b-col>
                        <b-col cols="2">
                          <b-button variant="secondary" size="sm" @click="onSaveMemo()">메모저장</b-button>
                        </b-col>
                      </b-row>
                      <div>
                        <b-modal ref="alertModal" ok-only>
                        <!-- <b-modal ref="alertModal" centered ok-only>         -->
                          <p class="text-center font-weight-bold">{{ alertModalMessage }}</p>
                        </b-modal>
                      </div>
                    </td>
                  </tr>                  
                </tbody>
              </table>
            </div>            
            <div class="pl-2 border">
              <b-form-group class="pt-4 pr-2">
                <b-form-checkbox class="ml-3" v-model="isSelectAllSensor"> 모두 선택 </b-form-checkbox>
                <b-form-checkbox-group class="border-top" v-model="selectedSensors" name="selectedSensor">
                  <b-container :class="{'non-max-width': isMaximized}">
                    <b-row class="pt-2">
                      <b-col cols="6" sm="4" :md="optionsColsMd"
                            v-for="(selectSensorOption, index) in selectSensorOptions"
                            :key="selectSensorOption.value"
                            >
                        <b-form-checkbox v-show="expandSensorOptions || index < 4 ? true : false" :value="selectSensorOption.value">{{ selectSensorOption.text }}</b-form-checkbox>
                      </b-col>
                      <!-- <b-col cols="6" sm="4" v-show="!expandSensorOptions">
                        <div>
                          <i class="icon mdi material-icons">more_horiz</i>
                        </div>
                      </b-col>                       -->
                    </b-row>
                    <b-row>
                      <b-col cols="12">
                        <div class="text-center" v-show="!expandSensorOptions"><i class="icon mdi material-icons">more_horiz</i>
                        </div>
                      </b-col>
                    </b-row>
                    <b-row>
                      <b-col>
                        <b-button variant="secondary" size="sm" class="form-control form-control-sm" @click="onClickExpandSensors()"><i class="icon mdi material-icons">{{ expandSensorOptionsBtnIcon }}</i></b-button>
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
        <b-row class="border-bottom ">
          <b-col cols="12" class="mt-3">
            <div class="mx-3 pt-2">
              <b-form-checkbox v-model="isOnlyZeroSpeed" :disabled="!showOnlyZeroSpeedChkBox">공회전만 보기</b-form-checkbox>
            </div>
          </b-col>
        </b-row>
        <b-row align-h="center mt-3" v-show="showPagination">
          <b-col cols="12">
            <b-pagination align="center" size="md"
                          :total-rows="driveMgtPage.page.total"
                          v-model="driveMgtPage.page.page"
                          :per-page="driveMgtPage.page.scale"
                          @input="onChangePage()" />
          </b-col>
        </b-row>
        <b-row align-h="center mt-8" v-show="onLoading">
          <b-col cols="12" class="text-center height-900">
            <div class="lds-roller">
              <div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div>
            </div>
          </b-col>
        </b-row>
        <b-row align-h="center mt-3" v-show="showSensorGraph">
          <b-col cols="3"/>
          <b-col cols="6">
            <b-table ref="dtcListTable" :fields="dtcListFields" :items="driveMgtPage.dtcs" :thead-class="'d-none'" class="border-top border-bottom">
            </b-table>
          </b-col>
          <b-col cols="3"/>
        </b-row>        
        <b-row align-h="center mt-3">
          <b-col cols="11">
            <div ref="sensorGraphGroup" class="graph-group-div"></div>
          </b-col>
        </b-row>
    </b-container>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import Datepicker from 'vuejs-datepicker'
import { ko } from 'vuejs-datepicker/dist/locale'
import { eventBus } from '../../common/EventBus.vue'
import graphOptions from '../../../utils/graphOptions'
import Dygraph from 'dygraphs'
import cf from '../../../utils/customFilters'

export default {
  name: 'SensorGraph',

  components: {
    Datepicker,
  },

  props: {
    vehicle: null,
    isMaximized: false
  },

  computed: mapGetters({
    driveMgtPage: 'getDriveMgtPage',
  }),

  data () {
    return {
      alertModalMessage: null,
      optionsColsMd: 3,
      isFirstSearch: true,
      startDate: null,
      datePickerFormat: 'yyyy-MM-dd',
      language: ko,
      disabledDates: {
        to: null,
        from: null,
        dates: []
      },

      selectedDriveHistoryNo: null,
      driveStartTimeOptions: [],
      expandSensorOptions: true,
      expandSensorOptionsBtnIcon: "expand_less",
      selectSensorOptions: graphOptions.selectSensorOptions,
      driveHistoryDetail: null,
      selectedSensors: [
        "engineRpm", 
        "vehicleSpd"
      ],
      graphsColorSet: graphOptions.graphsColorSet,
      showOnlyZeroSpeedChkBox: true,
      isSelectAllSensor: false,
      isOnlyZeroSpeed: false,
      showSensorGraph: false,
      showPagination: false,
      onLoading: false,
      dtcListFields: [
        { key: 'dtcCode', label: 'DTC', class: 'text-center'},
        { key: 'desc', label: '설명', class: 'text-center'}
      ],
      form: {
        driveHistoryMemoNo: null,
        driveHistoryNo: null,
        memo: null
      }
    }
  },

  methods: {
    formatNumber: function(value) {
      return cf.formatNumber(value) ? cf.formatNumber(value) : "-"
    },    
    onClickExpandSensors: function() {
      if(this.expandSensorOptions){
        this.expandSensorOptions = false
        this.expandSensorOptionsBtnIcon = "expand_more"
      } else {
        this.expandSensorOptions = true
        this.expandSensorOptionsBtnIcon = "expand_less"
      }
    },
    onSelectDriveStartDate: function() {
      let that = this
      this.$store.dispatch('loadDriveHistotyIndexes', {
        params: {
          vehicleNo: that.vehicle.vehicleNo,
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

    onChangeDriveStartTime: function() {
      let that = this
      this.$store.dispatch('loadDriveHistoryDetail', {
        params: {
          vehicleNo: that.vehicle.vehicleNo,
          driveHistoryNo: that.selectedDriveHistoryNo,
        },
        callback: () => {
          that.driveHistoryDetail = that.driveMgtPage.driveHistoryDetail
          that.form.driveHistoryMemoNo = (that.driveMgtPage.driveHistoryDetail && that.driveMgtPage.driveHistoryDetail.driveHistoryMemo) ? that.driveMgtPage.driveHistoryDetail.driveHistoryMemo.driveHistoryMemoNo : null
          that.form.driveHistoryNo = that.driveMgtPage.driveHistoryDetail ? that.driveMgtPage.driveHistoryDetail.driveHistoryNo : null
          that.form.memo = (that.driveMgtPage.driveHistoryDetail && that.driveMgtPage.driveHistoryDetail.driveHistoryMemo) ? that.driveMgtPage.driveHistoryDetail.driveHistoryMemo.memo : null

          that.$refs.sensorGraphGroup.innerHTML = ""
          that.showSensorGraph = false
          that.showPagination = false
          that.onLoading = false          
        }
      })      
      if (this.isFirstSearch) {
        this.isFirstSearch = false
        this.onSearchDriveHistory()
      }
    },
    onChangePage : function() {
      this.showPagination = true
      this.loadSensingRaw()
    },
    onSearchDriveHistory: function() {
      this.showPagination = false
      this.driveMgtPage.page.page = 1
      this.loadSensingRaw()
    },
    loadSensingRaw: function() {
      if(this.onLoading) return
      this.$refs.sensorGraphGroup.innerHTML = ""
      this.showSensorGraph = false
      let that = this
      this.onLoading = true
      this.$store.dispatch('loadSensingRaw', {
        params: {
          vehicleNo: that.vehicle.vehicleNo,
          driveHistoryNo: that.selectedDriveHistoryNo,
          fieldList: that.selectedSensors,
          page: that.driveMgtPage.page
        },
        callback: () => {
          that.showSensorGraph = true
          that.showPagination = true
          that.onLoading = false
          that.drawSensorGraph()
        }
      })
    },

    onSaveMemo: function() {
      if(!this.form.memo || this.form.memo == '') {
        this.alertModalMessage = "메모를 입력하세요."
        this.$refs.alertModal.show()
        return
      }

      let that = this
      this.$store.dispatch('saveDriveHistoryMemo', {
        data: this.form,
        callback: driveHistoryMemo => {
          that.form.driveHistoryMemoNo = driveHistoryMemo.driveHistoryMemoNo
          that.alertModalMessage = "저장하였습니다."
          that.$refs.alertModal.show()
        }
      })
    },

    drawSensorGraph: function() {
      let graphs = []
      let graphData

      // 속도 데이터를 조회하는 경우
      if(this.driveMgtPage.sensingRawGraphsOnlySpeedZero.length > 0){
        this.showOnlyZeroSpeedChkBox = true
      } else {
        this.showOnlyZeroSpeedChkBox = false
      }

      if(this.isOnlyZeroSpeed) {
        graphData = this.driveMgtPage.sensingRawGraphsOnlySpeedZero
      } else {
        graphData = this.driveMgtPage.sensingRawGraphs
      }

      this.$refs.sensorGraphGroup.innerHTML = ""
      for(let i = 0; i < graphData.length; i++) {
        let divNode = document.createElement("div")
        divNode.setAttribute("id", "graphDiv-" + i)
        divNode.classList.add("graph-div")
        divNode.classList.add("mt-3")
        divNode.classList.add("mb-3")
        this.$refs.sensorGraphGroup.appendChild(divNode)
        graphs.push(
          new Dygraph(
            divNode,
            graphData[i].data,
            {
              labels: graphData[i].fields,
              colors: i == 0 ? this.graphsColorSet[i] : graphOptions.getColorSet(graphData[i].fieldNames[1]),
              showRangeSelector: i % 2 == 1 ? false: true,
              showInRangeSelector: true,
              rangeSelectorHeight: 30,
              logscale: i == 0 ? true : false,
              axes: i > 0 ? null: {
                y: {
                  axisLabelFormatter: function(y) {
                    return y <= 0 ? 0 : parseInt(Math.log(y) / Math.LN10) + " bit"
                  },
                  valueFormatter: function(y) {
                    return formatYvalue(y)
                  },
                  drawGrid: false
                }
              },
              legend: "always",
              labelsSeparateLines: true,
              valueRange: i == 0 ? [ 0.09, null ] : graphOptions.getValueRange(graphData[i].fieldNames[1]),
              connectSeparatedPoints: false,
            }
          )
        )
      }

      if(graphs.length > 1) {
        let sync = Dygraph.synchronize(graphs,{
          selection: true,
          zoom: true,
          range: false
        })
      } 
    },
    formatDateTime: function(value) {
      return cf.formatDateTime(value)
    }
  },

  watch: {
    isOnlyZeroSpeed(newChecked, oldChecked) {
      this.drawSensorGraph()
    },
    isSelectAllSensor(newChecked, oldChecked) {
      this.selectedSensors = []
      if(newChecked) {
        let that = this
        this.selectSensorOptions.forEach(function(sensorOption) {
          that.selectedSensors.push(sensorOption.value)
       }) 
      }
    },
    isMaximized(newVal, oldVal) {
      // 화면 최대화를 할 경우 조회 센서 옵션의 표시 개수 조정
      if(newVal){
        this.optionsColsMd = 2
      } else {
        this.optionsColsMd = 3
      }
    }
  },

  created() {
    console.log('created SensorGraph')
    let that = this
    this.$store.dispatch('loadDriveHistoryStartDates', {
      vehicleNo: this.vehicle.vehicleNo, 
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
}

let formatYlabel = function(value) {
  
}

let formatYvalue = function(value) {
  let strY = "" + value
  let headZero = ""
  if(strY.length > 8) return "" + value

  for(let i = 0; i < 8 - strY.length; i++){
    headZero += "0"
  }
  strY = headZero + strY
    return "  " + strY.substr(0, 4) + " " + strY.substr(4, 4)
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Dygraph.synchronize
var synchronize = function(/* dygraphs..., opts */) {
  if (arguments.length === 0) {
    throw 'Invalid invocation of Dygraph.synchronize(). Need >= 1 argument.';
  }

  var OPTIONS = ['selection', 'zoom', 'range'];
  var opts = {
    selection: true,
    zoom: true,
    range: true
  };
  var dygraphs = [];
  var prevCallbacks = [];

  var parseOpts = function(obj) {
    if (!(obj instanceof Object)) {
      throw 'Last argument must be either Dygraph or Object.';
    } else {
      for (var i = 0; i < OPTIONS.length; i++) {
        var optName = OPTIONS[i];
        if (obj.hasOwnProperty(optName)) opts[optName] = obj[optName];
      }
    }
  };

  if (arguments[0] instanceof Dygraph) {
    // Arguments are Dygraph objects.
    for (var i = 0; i < arguments.length; i++) {
      if (arguments[i] instanceof Dygraph) {
        dygraphs.push(arguments[i]);
      } else {
        break;
      }
    }
    if (i < arguments.length - 1) {
      throw 'Invalid invocation of Dygraph.synchronize(). ' +
            'All but the last argument must be Dygraph objects.';
    } else if (i == arguments.length - 1) {
      parseOpts(arguments[arguments.length - 1]);
    }
  } else if (arguments[0].length) {
    // Invoked w/ list of dygraphs, options
    for (var i = 0; i < arguments[0].length; i++) {
      dygraphs.push(arguments[0][i]);
    }
    if (arguments.length == 2) {
      parseOpts(arguments[1]);
    } else if (arguments.length > 2) {
      throw 'Invalid invocation of Dygraph.synchronize(). ' +
            'Expected two arguments: array and optional options argument.';
    }  // otherwise arguments.length == 1, which is fine.
  } else {
    throw 'Invalid invocation of Dygraph.synchronize(). ' +
          'First parameter must be either Dygraph or list of Dygraphs.';
  }

  if (dygraphs.length < 2) {
    throw 'Invalid invocation of Dygraph.synchronize(). ' +
          'Need two or more dygraphs to synchronize.';
  }

  var readycount = dygraphs.length;
  for (var i = 0; i < dygraphs.length; i++) {
    var g = dygraphs[i];
    g.ready( function() {
      if (--readycount == 0) {
        // store original callbacks
        var callBackTypes = ['drawCallback', 'highlightCallback', 'unhighlightCallback'];
        for (var j = 0; j < dygraphs.length; j++) {
          if (!prevCallbacks[j]) {
            prevCallbacks[j] = {};
          }
          for (var k = callBackTypes.length - 1; k >= 0; k--) {
            prevCallbacks[j][callBackTypes[k]] = dygraphs[j].getFunctionOption(callBackTypes[k]);
          }
        }

        // Listen for draw, highlight, unhighlight callbacks.
        if (opts.zoom) {
          attachZoomHandlers(dygraphs, opts, prevCallbacks);
        }

        if (opts.selection) {
          attachSelectionHandlers(dygraphs, prevCallbacks);
        }
      }
    });
  }

  return {
    detach: function() {
      for (var i = 0; i < dygraphs.length; i++) {
        var g = dygraphs[i];
        if (opts.zoom) {
          g.updateOptions({drawCallback: prevCallbacks[i].drawCallback});
        }
        if (opts.selection) {
          g.updateOptions({
            highlightCallback: prevCallbacks[i].highlightCallback,
            unhighlightCallback: prevCallbacks[i].unhighlightCallback
          });
        }
      }
      // release references & make subsequent calls throw.
      dygraphs = null;
      opts = null;
      prevCallbacks = null;
    }
  };
};

function arraysAreEqual(a, b) {
  if (!Array.isArray(a) || !Array.isArray(b)) return false;
  var i = a.length;
  if (i !== b.length) return false;
  while (i--) {
    if (a[i] !== b[i]) return false;
  }
  return true;
}

function attachZoomHandlers(gs, syncOpts, prevCallbacks) {
  var block = false;
  for (var i = 0; i < gs.length; i++) {
    var g = gs[i];
    g.updateOptions({
      drawCallback: function(me, initial) {
        if (block || initial) return;
        block = true;
        var opts = {
          dateWindow: me.xAxisRange()
        };
        if (syncOpts.range) opts.valueRange = me.yAxisRange();

        for (var j = 0; j < gs.length; j++) {
          if (gs[j] == me) {
            if (prevCallbacks[j] && prevCallbacks[j].drawCallback) {
              prevCallbacks[j].drawCallback.apply(this, arguments);
            }
            continue;
          }

          // Only redraw if there are new options
          if (arraysAreEqual(opts.dateWindow, gs[j].getOption('dateWindow')) && 
              arraysAreEqual(opts.valueRange, gs[j].getOption('valueRange'))) {
            continue;
          }

          gs[j].updateOptions(opts);
        }
        block = false;
      }
    }, true /* no need to redraw */);
  }
}

function attachSelectionHandlers(gs, prevCallbacks) {
  var block = false;
  for (var i = 0; i < gs.length; i++) {
    var g = gs[i];

    g.updateOptions({
      highlightCallback: function(event, x, points, row, seriesName) {
        if (block) return;
        block = true;
        var me = this;
        for (var i = 0; i < gs.length; i++) {
          if (me == gs[i]) {
            if (prevCallbacks[i] && prevCallbacks[i].highlightCallback) {
              prevCallbacks[i].highlightCallback.apply(this, arguments);
            }
            continue;
          }
          var idx = gs[i].getRowForX(x);
          if (idx !== null) {
            gs[i].setSelection(idx, seriesName);
          }
        }
        block = false;
      },
      unhighlightCallback: function(event) {
        if (block) return;
        block = true;
        var me = this;
        for (var i = 0; i < gs.length; i++) {
          if (me == gs[i]) {
            if (prevCallbacks[i] && prevCallbacks[i].unhighlightCallback) {
              prevCallbacks[i].unhighlightCallback.apply(this, arguments);
            }
            continue;
          }
          gs[i].clearSelection();
        }
        block = false;
      }
    }, true /* no need to redraw */);
  }
}
Dygraph.synchronize = synchronize;
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
</script> 
<style>
.graph-group-div {
  width: 100%;
}
.graph-div {
 height: 150px; 
 width: 100%;
}
.container.non-max-width {
  max-width: none;
}
.height-900 {
  height: 900px;
}
</style>


