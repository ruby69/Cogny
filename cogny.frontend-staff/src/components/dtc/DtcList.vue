<template>
  <div class="card card-table p-2">
    <div class="card-header">
      <span class="font-weight-bold mx-0"> DTC 목록 </span>
    </div>
    <div class="card-body mt-4">
      <b-container class="mw-100 dataTables_wrapper container-fluid dt-bootstrap4">
        <b-row>
          <b-col cols="6">
            <div class="input-group input-search mx-4">
              <b-form-input type="text" class="form-control" placeholder="DTC 검색" 
                            v-model="dtcPage.query" 
                            @keydown.native="keydownHandler"
                            />
              <span class="input-group-btn">
                <button class="btn btn-secondary" @click="search()">
                  <i class="icon mdi mdi-search"></i>
                </button>
              </span>
            </div>
          </b-col>
          <b-col cols="6" class="text-right pr-7">
            <span class="d-inline-flex">페이지당 </span>
            <div class="d-inline-flex">
              <b-form-select v-model="dtcPage.scale" :options="scaleOptions" @input="search"/>
            </div>
            <span class="d-inline-flex">개</span>
          </b-col>
        </b-row>
        <b-row class="be-datatable-body mt-4 mx-1">
          <b-col cols="12">
            <b-table striped hover :fields="dtcFields" :items="dtcPage.contents" class="border-top border-bottom">
            </b-table>
          </b-col>
        </b-row>
        <b-row class="be-datatable-footer mx-4">
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
    </div>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import cf from '../../utils/customFilters'

export default {
  name: 'DtcList',

  computed: mapGetters({
    dtcPage: 'getDtcPage',
  }),
  data: function() {
    return {
      params: {
        page: null,
        scale: null,
        query: null
      },
      scaleOptions: [
        { value: 15, text: 15 },
        { value: 30, text: 30 },
        { value: 50, text: 50 },
        { value: 100, text: 100 }        
      ],
      dtcFields: [
        { key: 'driveHistoryNo', label: '번호', class: 'text-center'},
        { key: 'companyName', label: '회사', class: 'text-center'},
        { key: 'licenseNo', label: '차량번호', class: 'text-center'},
        { key: 'modelName', label: '모델', class: 'text-center'},
        { key: 'obdSerial', label: 'OBD 단말기', class: 'text-center'},
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
    search() {
      this.params.query = this.dtcPage.query
      this.params.scale = this.dtcPage.scale
      this.params.page = 1
      this.$store.dispatch('loadDtcPage', this.params)
    },    
    movePage: function() {
      this.params.page = this.dtcPage.page
      this.$store.dispatch('loadDtcPage', this.params)
    },
    keydownHandler(event) {
      if (event.which === 13) {
        this.search()
      }
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
    console.log('created DtcList')
    this.$store.dispatch('loadDtcPage', this.param)
  },
  mounted() {
    this.params = {
      page: this.dtcPage.page,
      scale: this.dtcPage.scale,
      query: this.dtcPage.query
    }
  }  
}
</script>
