<template>
  <div class="col-sm-12">
    <div class="card card-table non-shadow">
      <div class="card-header">
        <b-input-group size="sm">
          <b-form-input v-model.trim="vehiclesPage.query" 
                        @keyup.enter.native="search()" 
                        placeholder="설치차량을 검색하세요."/>
          <b-input-group-append>
            <b-btn variant="primary" @click="search()">설치 차량 검색</b-btn>
          </b-input-group-append>   
        </b-input-group>                
      </div>
      <div class="card-body obd-list-card-body">
        <b-table striped hover :fields="vehicleFields" :items="vehiclesPage.contents">
          <template slot="yearMonthSlot" slot-scope="row">
            <div>
              {{ row.item.modelYear + '/' + row.item.modelMonth }}
            </div>
          </template>
          <template slot="obdDeviceSlot" slot-scope="row">
            <div>
              <span v-if="row.item.obdDevice"><span class="badge">{{ row.item.obdDevice.obdSerial }}</span><br/>{{ formatDate(row.item.obdDevice.obdInstallDate) }}</span>
              <i v-else class="material-icons">phonelink_setup</i>
            </div>
          </template>
          <template slot="selectSlot" slot-scope="row">
            <div>
              <b-btn variant="secondary" :size="'sm'" @click="onSelect(row.item)">선택</b-btn> 
            </div>
          </template>
        </b-table>
        <b-pagination align="center" size="sm" 
                    :total-rows="vehiclesPage.total" 
                    v-model="vehiclesPage.page" 
                    :per-page="vehiclesPage.scale" 
                    @input="movePage" />
      </div>
      <div class="text-right">
        <b-btn variant="secondary" :size="'sm'" @click="onClose()">취소</b-btn> 
      </div>
    </div>
  </div>
</template>
  
<script>
import { mapGetters } from 'vuex'
import { eventBus } from '../EventBus.vue'
import cf from '../../../utils/customFilters'

export default {
  name: 'PopupVehicleList',

  computed: mapGetters({
    vehiclesPage: 'getVehiclesPage'
  }),
  props: {
    partnerNo: null
  },
  data: function() {
    return {
      params: {
        vehicleStatus: 'FREE',
        partnerNo: null
      },
      vehicleFields: [
        { key: 'licenseNo', label: '차량번호', class: 'text-center' },
        { key: 'model.name', label: '차종', class: 'text-center' },
        { key: 'yearMonthSlot', label: '연식', class: 'text-center' },
        { key: 'obdDeviceSlot', label: '설치여부', class: 'text-center' },
        { key: 'selectSlot', label: '선택', class: 'text-center' },
      ]
    }
  },

  methods: {
    search: function(){
      this.params.query = this.vehiclesPage.query
      this.params.partnerNo = this.vehiclesPage.partnerNo
      this.params.scale = this.vehiclesPage.scale
      this.params.page = 1      
      this.$store.dispatch('loadVehiclesPage', this.params)
    },
    movePage: function(){
      this.params.page = this.vehiclesPage.page
      this.$store.dispatch('loadVehiclesPage', this.params)
    },
    onSelect: function(vehicle){
      eventBus.$emit('onSelectPopupVehicles', vehicle)
    },
    onClose: function() {
      eventBus.$emit('onClosePopupVehicleList')
    },
    formatDate: function(date) {
      return cf.formatDate(date)
    }
  },

  created() {
    this.params.scale = 5
    this.params.partnerNo = this.partnerNo
    this.$store.dispatch('loadVehiclesPage', this.params)
  },
}
</script>
<style>
.obd-list-card-body {
  min-width: 500px;
  min-height: 300px;
}
.non-shadow {
  -webkit-box-shadow: 0 0 0 0 rgba(0, 0, 0, 0);
  box-shadow: 0 0 0 0 rgba(0, 0, 0, 0);
}
</style>