<template>
  <div>
    <b-container class="px-0">
      <b-row align-v="center" class="mb-3">
        <b-col cols="12" class="text-right">
          <button class="btn btn-rounded btn-space btn-primary mb-0 mr-0" @click="newRepair">
            <i class="icon mdi mdi-wrench"></i> 차량 정비이력등록
          </button>            
        </b-col>
      </b-row>
      <b-row class="mb-3">
        <b-col cols="6">
              <div class="input-group input-search pl-0">
              <b-form-input type="text" placeholder="차량 정비 이력 검색"
                            v-model.trim="repairPage.query"
                            @keydown.native="keydownHandler"
                            />
              <span class="input-group-btn">
                <button class="btn btn-sm btn-secondary" @click="search()">
                  <i class="icon mdi mdi-search"></i>
                </button>
              </span>   
              </div>
        </b-col>

        <b-col cols="6" class="text-right">
          <div class="pr-0">
              <span class="d-inline-flex">페이지당 </span>
              <div class="d-inline-flex">
                <b-form-select v-model="repairPage.scale" :options="scaleOptions" @input="search"/>
              </div>
              <span class="d-inline-flex">개</span>
          </div>

        </b-col>
      </b-row>
      <b-row>
        <b-col cols="12">
          <b-table hover :fields="repairFields" :items="repairPage.contents" class="border-top border-bottom">
            <template slot="manager" slot-scope="row">
              <div :class="{ 'd-none': !row.item.repairNo }">
                <button type="button" class="btn btn-secondary" @click="showInfo(row.item)" v-b-tooltip.hover title="정비 이력 수정"><i class="icon mdi mdi-edit"/></button>
                <button type="button" class="btn btn-secondary" @click="onDelete(row.item)" v-b-tooltip.hover title="삭제"><i class="icon mdi mdi-delete"/></button>
              </div>
            </template>
          </b-table>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols="5">
          <div class="dataTables_info" id="table1_info" role="status"> {{ repairPage.page }} / {{ repairPage.totalPages }} 페이지, 총 : {{ repairPage.total }}건</div>
        </b-col>
        <b-col cols="7">
          <div class="dataTables_paginate paging_simple_numbers" id="table1_paginate">
            <b-pagination align="right" size="md" 
                          :total-rows="repairPage.total" 
                          v-model="repairPage.page" 
                          :per-page="repairPage.scale" 
                          @input="movePage" />
          </div>        
        </b-col>
      </b-row>
    </b-container>
    <div>
      <b-modal ref="deleteYnModal" @ok="onDeleteOk" @hide="onHideDelete">
        <div class="text-center font-enlarge">
          '<span class="text-danger font-weight-bold">{{ toBeDeleted ? formatDate(toBeDeleted.repairDate) : null }}</span>'의 <br/>정비 이력을 삭제하시겠습니까? 
        </div>
      </b-modal>
    </div>
  </div>  
</template>

<script>
import { mapGetters } from 'vuex'
import { eventBus } from '../../common/EventBus.vue'
import cf from '../../../utils/customFilters'

export default {
  name: 'RepairList',

  props: {
    vehicle: null,
    isMaximized: false
  },

  computed: mapGetters({
    repairPage: 'getRepairPage'
  }),  

  data: function() {
    return {
      params: {
        vehicleNo: null,
        query: null
      },
      scaleOptions: [
        { value: 5, text: 5 },
        { value: 15, text: 15 },
        { value: 30, text: 30 },
        { value: 50, text: 50 }
      ],      
      repairFields: [
        { key: 'repairDate', label: '정비일자', formatter: 'formatDate', class: 'text-center width-10'},
        { key: 'odometer', label: '주행거리', formatter: 'formatDistance', class: 'text-center width-10'},
        { key: 'repairComponent.componentCate1.name', label: '구분', class: 'text-center width-10'},
        { key: 'repairComponent.componentCate2.name', label: '상세구분', class: 'text-center width-10'},
        { key: 'repairComponent.component.name', label: '부품', class: 'text-center width-10'},
        { key: 'repairComponent.category.text', label: '정비 구분', class: 'text-center width-10'},
        { key: 'repairComponent.cost.value', label: '정비비용', formatter: 'formatMoney', class: 'text-center width-10'},
        { key: 'repairComponent.memo.value', label: '비고', class: 'text-center width-10'},
        { key: 'memo', label: '메모', class: 'text-center width-10'},
        { key: 'manager', label: '수정/삭제', class: 'text-center width-10'},
      ],
      toBeDeleted: null
    }
  },

  methods: {
    search: function() {
      this.params.query = this.repairPage.query
      this.params.scale = this.repairPage.scale
      this.params.page = 1
      this.$store.dispatch('loadRepairPage', this.params)
    },
    movePage: function() {
      this.params.page = this.repairPage.page
      this.$store.dispatch('loadRepairPage', this.params)
    },
    newRepair: function() {
      eventBus.$emit('onNewRepair')
    },
    showInfo: function(repair) {
      eventBus.$emit('onRepairInfo', repair)
    }, 
    onDelete: function(repair) {
      this.toBeDeleted = repair
      this.$refs.deleteYnModal.show() 
    },
    onDeleteOk() {
      let that = this
      this.$store.dispatch('deleteRepair', {
        repairNo: this.toBeDeleted.repairNo,
        callback: response => {
          that.$store.dispatch('loadRepairPage', that.params)
        }
      })
    },
    onHideDelete() {
      this.toBeDeleted = null
    },    
    formatDate: function(date) {
      return date ? cf.formatDate(date) : null
    },
    formatMoney: function(value) {
      if(value) {
        let rtn = cf.formatNumber(value)
        return rtn ? "\\ " + rtn : null
      } else {
        return null
      }
    },
    formatDistance: function(value) {
      if(value) {
        let rtn = cf.formatNumber(value)
        return rtn ? rtn + " km" : null
      } else {
        return null
      }
    }
  },

  created() {
    console.log('created vehicle/tabs/RepairList')
    this.params.scale = 5
    this.params.vehicleNo = this.vehicle.vehicleNo
    this.$store.dispatch('loadRepairPage', this.params)
  },
}
</script>
<style>
.table-bg-gray {
  background-color: #f5f5f5;
}
.width-10 { 
  width: 10% 
}
</style>