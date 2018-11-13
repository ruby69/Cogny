<template>
    <b-row>
      <b-col md="3" v-show="!isMaximized"> 
        <b-card class="card card-border-color card-border-color-primary">
          <b-card-body>
            <div class="icon-container">
              <div class="icon">
                <span class="mdi mdi-car-taxi"></span>
              </div>              
            </div>
          </b-card-body>
          <h4 class=" font-weight-bold">{{ pageContents.licenseNo }}</h4>
          <b-card-body class="mt-2 p-0">
            <table class="table table-sm table-hover table-bordered table-striped">
              <colgroup>
                <col width="20%"/>
                <col width="30%"/>
                <col width="20%"/>
                <col width="30%"/>
              </colgroup>
              <tbody>
                <tr>
                  <th class="text-center">제조사</th>
                  <td class="text-center">{{ pageContents.model.modelGroup.manufacturer.name }}</td>
                  <th class="text-center">차종</th>
                  <td class="text-center">{{ pageContents.model.name }}</td>
                </tr>
                <tr>
                  <th class="text-center">연식</th>
                  <td class="text-center">{{ pageContents.modelYear + '/' + pageContents.modelMonth }}</td>
                  <th class="text-center">유종</th>
                  <td class="text-center">{{ pageContents.fuel.name }}</td>
                </tr>
                <tr>
                  <th class="text-center">주행거리</th>
                  <td class="text-center">{{ formatNumber(parseInt(pageContents.currentMileage / 10)) }} km</td>
                  <th class="text-center">설치 OBD</th>
                  <td class="text-center">{{ pageContents.obdDevice ? pageContents.obdDevice.obdSerial : "-" }}</td>
                </tr>
              </tbody>
            </table>  
          </b-card-body>
        </b-card>
      </b-col>
      <b-col :md="detailCardMd"> 
        <b-card class="card card-border-color card-border-color-primary">
          <div class="card-header">
            <div class="tools">
              <span v-b-tooltip.hover title="확대" 
                    v-show="!isMaximized" 
                    @click="toMaximize()"
                    class="icon mdi mdi-window-maximize" style="color: #bababa;"></span>
              <span v-b-tooltip.hover title="축소" 
                    v-show="isMaximized" 
                    @click="toRestore()"
                    class="icon mdi mdi-window-restore" style="color: #bababa;"></span>
            </div>
          </div>
          <b-tabs card>
            <!-- <b-tab title="운행이력">
              <b-card-img bottom src="https://lorempixel.com/600/200/food/6/" />
              <b-card-footer>Picture 3 footer</b-card-footer>
            </b-tab> -->
            <b-tab title="진단이력" @click="onClickDtcTab" class="mx-0" v-if="hasAuthSesingRaw">
              <VehicleDtcList :vehicle="pageContents" :isMaximized="isMaximized" v-if="showDtcGraph"/>
            </b-tab>
            <b-tab title="센서데이터" @click="onClickSensorTab" class="mx-0" v-if="hasAuthSesingRaw" active>
              <!-- <router-view name="sensorGraph"/> -->
              <SensorGraph :vehicle="pageContents" :isMaximized="isMaximized" v-if="showSensorGraph"/>
            </b-tab>
            <!-- <b-tab title="정비이력" @click="onClickRepairTab" class="mx-0"> -->
              <!-- <router-view name="repair"/> -->
              <!-- <component :is="currentView" :vehicle="pageContents" :repairItem="repairItem" :isMaximized="isMaximized" v-if="showRepair"/>
            </b-tab> -->
          </b-tabs>
        </b-card>
      </b-col>
    </b-row>
</template>

<script>
import SensorGraph from './tabs/SensorGraph.vue'
import VehicleDtcList from './tabs/VehicleDtcList.vue'
import RepairDetail from './tabs/RepairDetail.vue'
import RepairList from './tabs/RepairList.vue'
import { mapGetters } from 'vuex'
import { eventBus } from '../common/EventBus.vue'
import cf from '../../utils/customFilters'

export default {
  name: 'VehicleMgt',
  computed: mapGetters({
    currentUser: 'getCurrentUser'
  }),
  components: {
    SensorGraph,
    VehicleDtcList,
    RepairList,
    RepairDetail
  },

  props: {
    pageContents : null
  },  

  data () {
    return {
      detailCardMd: 9,
      showDtcGraph: false,
      showSensorGraph: false,
      showRepair: false,
      currentView: 'RepairList',
      repairItem: null,
      hasAuthSesingRaw: false,
      isMaximized: false
    }
  },
  methods: {
    formatNumber: function(value) {
      return cf.formatNumber(value) ? cf.formatNumber(value) : "-"
    },
    toMaximize: function() {
      this.isMaximized = true,
      this.detailCardMd = 12
      eventBus.$emit('onMaximizeGraph')
    },
    toRestore: function() {
      this.isMaximized = false
      this.detailCardMd = 9
      eventBus.$emit('onRestoreGraph')
    },
    onClickDtcTab: function() {
      this.showDtcGraph = true
      this.showSensorGraph = false
      this.showRepair = false
    },
    onClickSensorTab: function() {
      this.showDtcGraph = false
      this.showSensorGraph = true
      this.showRepair = false
      // this.$router.push({ name: 'vehicleSensorGraph', params: { vehicle: this.pageContents, isMaximized: this.isMaximized }})
    },
    onClickRepairTab: function() {
      this.showDtcGraph = false
      this.showSensorGraph = false
      this.showRepair = true
      // this.$router.push({ name: 'vehicleRepairList', params: { vehicle: this.pageContents, isMaximized: this.isMaximized }})      
      
    },
  },
  created() {
    console.log('created vehicle/VehicleMgt')
    let that = this
    if(this.currentUser.role == 'ADMIN') {
      this.hasAuthSesingRaw = true
    }

    eventBus.$on('onNewRepair', function(){
      that.repairItem = {
        repairComponentList: []
      }
      that.currentView = 'RepairDetail'
    })

    eventBus.$on('onCloseRepairDetail', function(){
      that.repairItem = null
      that.currentView = 'RepairList'
    })
    eventBus.$on('onRepairInfo', function(repair){
      that.repairItem = repair
      that.currentView = 'RepairDetail'
    })
  }
}
</script>