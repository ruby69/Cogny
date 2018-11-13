<template>
  <b-container>
    <b-row>
      <b-col cols="12">
        <b-table striped hover :fields="dtcFields" :items="dtcPage.contents">
        </b-table>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="5">
        <div class="dataTables_info" id="table1_info" role="status"> {{ dtcPage.page }} / {{ dtcPage.totalPages }} 페이지, 총 : {{ dtcPage.total }}건</div>
      </b-col>
      <b-col cols="7">
        <div class="dataTables_paginate paging_simple_numbers" id="table1_paginate">
          <b-pagination align="right" size="md" 
                        :total-rows="dtcPage.total" 
                        v-model="dtcPage.page" 
                        :per-page="dtcPage.scale" 
                        @input="movePage" />
        </div>
      </b-col>
    </b-row>
  </b-container>
</template>
<script>
import { mapGetters } from 'vuex'
import cf from '../../../utils/customFilters'

export default {
  name: 'VehicleDtcList',
  props: {
    vehicle: undefined,
    isMaximized: false
  },
  computed: mapGetters({
    dtcPage: 'getDtcPage'
  }), 
  data: function() {
    return {
      params: {
        page: undefined,
        scale: undefined,
        vehicleNo: undefined,
        query: undefined
      },
      scaleOptions: [
        { value: 5, text: 5 },
        { value: 15, text: 15 },
        { value: 30, text: 30 },
        { value: 50, text: 50 }
      ],
      dtcFields: [
        { key: 'driveHistoryNo', label: '번호', class: 'text-center'},
        { key: 'userName', label: '운전자', class: 'text-center'},
        { key: 'dtcRaw.dtcCode', label: 'DTC', class: 'text-center'},
        { key: 'dtcRaw.dtcState', formatter: 'formatDtcState', label: '상태', class: 'text-center'},
        { key: 'dtcRaw.desc', label: '설명', class: 'text-center'},
        { key: 'dtcRaw.dtcIssuedTime', formatter: 'formatTime', label: 'DTC시작시각', class: 'text-center'},
        { key: 'dtcRaw.dtcUpdatedTime', formatter: 'formatTime', label: 'DTC종료시각', class: 'text-center'},
        { key: 'driveStartDateTime', label: '운행시작시간', class: 'text-center'},
        { key: 'driveEndDateTime', label: '운행종료시간', class: 'text-center'},
      ]
    }
  },
  methods: {
    movePage: function() {
      this.params.page = this.dtcPage.page
      this.params.vehicleNo = this.vehicle.vehicleNo
      this.$store.dispatch('loadDtcPage', this.params)
    },
    formatDtcState: function(dtcState) {
      if(!dtcState) return ''
      let bit5 = dtcState.substr(5, 1)
      let bit6 = dtcState.substr(4, 1)
      if(bit5 == "1" || bit6 == "1") {
        return "현재발생"
      } else {
        return "과거발생"
      }
    },    
    formatDateTime: function(date) {
      return cf.formatDateTime(date)
    },
    formatTime: function(date) {
      return cf.formatTime(date)
    },
  },
  created() {
    console.log('created vehicle/tabs/VehicleDtcList')
    this.params.vehicleNo = this.vehicle.vehicleNo    
    this.$store.dispatch('loadDtcPage', this.params)
  },
  mounted() {
    this.params = {
      page: this.dtcPage.page,
      scale: this.dtcPage.scale,
      query: this.dtcPage.query,
      vehicleNo: this.vehicle.vehicleNo,
    }
  },
}
</script>
<style>
.width-10 { width: 10% }
.width-20 { width: 20% }
</style>