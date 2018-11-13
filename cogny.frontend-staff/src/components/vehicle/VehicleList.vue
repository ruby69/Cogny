<template>
  <div class="card card-table p-2">
    <div class="card-header">
      <span class="font-weight-bold mx-0"> 차량 목록 </span>
    </div>
    <div class="card-body">
      <b-container class="mw-100 dataTables_wrapper container-fluid dt-bootstrap4">
        <b-row class="mb-3">
          <b-col cols="12" class="text-right">
              <button class="btn btn-rounded btn-space btn-primary mr-4" @click="newVehicle">
                <i class="icon mdi material-icons">directions_car</i> 차량 등록
              </button>
          </b-col>
        </b-row>
        <b-row class="px-4">
          <b-col cols="12" md="4" class="partner-input py-1" id="popupPartnerContainer">
              <b-input-group>
                <b-form-input v-model.trim="companyName" readonly></b-form-input>
                <b-btn id="popoverSelectPartner" variant="primary">제휴사 선택</b-btn>
                <b-btn
                    class="ml-1"
                    variant="primary"
                    @click="onSelectAllPartner()"
                    v-b-tooltip.hover title="전체 제휴사에서 보기">전체 보기</b-btn>
                <b-popover container="popupPartnerContainer" :show.sync="showPopupPartner" :placement="'bottomleft'" target="popoverSelectPartner">
                  <PopupPartnerList/>
                </b-popover>
              </b-input-group>
          </b-col>
          <b-col cols="12" md="5" class="py-1">
            <div class="input-group input-search">
              <b-form-input type="text" class="form-control" placeholder="차량 검색" 
                            v-model="vehiclesPage.query" 
                            @keydown.native="keydownHandler"
                            />
              <span class="input-group-btn">
                <button class="btn btn-secondary" @click="search()">
                  <i class="icon mdi mdi-search"></i>
                </button>
              </span>
            </div>
          </b-col>
          <b-col cols="12" md="3" class="text-right py-1">
            <span class="d-inline-flex">페이지당 </span>
            <div class="d-inline-flex">
              <b-form-select v-model="vehiclesPage.scale" :options="scaleOptions" @input="search"/>
            </div>
            <span class="d-inline-flex">개</span>
          </b-col>
        </b-row>
        <b-row class="be-datatable-body mt-4 mx-1">
          <b-col>
              <b-table striped hover :fields="vehicleFields" :items="vehiclesPage.contents" class="border-top border-bottom">
                <template slot="yearMonth" slot-scope="row">
                  <div>
                    {{ row.item.modelYear + '/' + row.item.modelMonth }}
                  </div>
                </template>
                <template slot="obdDeviceSlot" slot-scope="row">
                  <div>
                    <span v-if="row.item.obdDevice"><span class="badge">{{ row.item.obdDevice.obdSerial }}</span><br/>{{formatDate(row.item.obdDevice.obdInstallDate)}}</span>
                    <i v-else class="material-icons">phonelink_setup</i>
                  </div>
                </template>
                <template slot="driveSlot" slot-scope="row">
                  <div>
                    <button type="button" class="btn btn-secondary" @click="manageVehicle(row.item)" v-b-tooltip.hover title="차량진단정보"><i class="icon mdi mdi-car"/></button>
                  </div>
                </template>
                <template slot="managerSlot" slot-scope="row">
                  <div>
                    <button type="button" class="btn btn-secondary" @click="showInfo(row.item)" v-b-tooltip.hover title="차량정보수정"><i class="icon mdi mdi-edit"/></button>
                    <button type="button" class="btn btn-secondary" @click="onDelete(row.item)" v-b-tooltip.hover title="삭제"><i class="icon mdi mdi-delete"/></button>
                  </div>
                </template>
              </b-table>
          </b-col>
        </b-row>
        <b-row class="be-datatable-footer mx-4">
          <b-col cols="5">
              <div class="dataTables_info" id="table1_info" role="status" aria-live="polite"> {{ vehiclesPage.page }} / {{ vehiclesPage.totalPages }} 페이지, 총 제휴사 : {{ vehiclesPage.total }}개</div>
          </b-col>
          <b-col cols="7">
            <div class="dataTables_paginate paging_simple_numbers" id="table1_paginate">
              <b-pagination align="right" size="md" 
                            :total-rows="vehiclesPage.total" 
                            v-model="vehiclesPage.page" 
                            :per-page="vehiclesPage.scale" 
                            @input="movePage" />
            </div>
          </b-col>
        </b-row>
      </b-container>
    </div>
    <div>
      <b-modal ref="deleteYnModal" @ok="onDeleteOk" @hide="onHideDelete">
        <div class="text-center font-enlarge">
          차량 '<span class="text-danger font-weight-bold">{{ vehicleToBeDeleted ? vehicleToBeDeleted.licenseNo : null }}</span>'의 <br/>정보를 삭제하시겠습니까? 
        </div>
      </b-modal>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import cf from '../../utils/customFilters'
import { eventBus } from '../common/EventBus.vue'
import PopupPartnerList from '../common/popup/PopupPartnerList.vue'

export default {
  name: 'VehicleList',
  components: {
    PopupPartnerList
  }, 

  data: function(){
    return {
      companyName: null,
      showPopupPartner: false,
      params: {},      
      scaleOptions: [
        { value: 15, text: 15 },
        { value: 30, text: 30 },
        { value: 50, text: 50 },
        { value: 100, text: 100 }
      ],
      vehicleFields: [
        { key: 'vehicleNo', label: '번호', class: 'text-center width-5'},
        { key: 'partner.companyName', label: '회사명', class: 'text-center width-10'},
        { key: 'licenseNo', label: '차량번호', class: 'text-center width-10'},
        { key: 'model.modelGroup.manufacturer.name', label: '제조사', class: 'text-center width-10'},
        { key: 'model.name', label: '차종', class: 'text-center width-10'},
        { key: 'yearMonth', label: '연식', class: 'text-center width-5'},
        { key: 'fuel.name', label: '유종', class: 'text-center width-5'},
        { key: 'obdDeviceSlot', label: 'OBD 단말기/설치일', class: 'text-center width-10'},
        { key: 'memo', label: '메모', class: 'text-center width-10'},
        { key: 'regDate', label: '등록일', formatter: 'formatDate', class: 'text-center width-10'},
        { key: 'driveSlot', label: '운행 관리', class: 'text-center width-5'},        
        { key: 'managerSlot', label: '수정/삭제', class: 'text-center width-10'},
      ],
      vehicleToBeDeleted: null
    }
  },

  computed: mapGetters({
    vehiclesPage: 'getVehiclesPage',
    fuels: 'getFuels',
    manufacturers: 'getManufacturers'
  }),

  methods: {
    search() {
      this.params.query = this.vehiclesPage.query
      this.params.scale = this.vehiclesPage.scale
      this.params.page = 1
      this.$store.dispatch('loadVehiclesPage', this.params)
    },
    movePage() {
      this.params.page = this.vehiclesPage.page
      this.$store.dispatch('loadVehiclesPage', this.params)
    },
    showInfo(vehicle) {
      this.$router.push({ name: 'vehicleDetail', params: { vehicle: vehicle }})
    },
    newVehicle() {
      this.$router.push({ name: 'vehicleNew' })
    },
    manageVehicle(vehicle) {
      this.$router.push({ name: 'vehicleMgt', params: { vehicle: vehicle }})
    },
    onDelete(vehicle) {
      this.vehicleToBeDeleted = vehicle
      this.$refs.deleteYnModal.show()      
    },
    onDeleteOk() {
      this.$store.dispatch('deleteVehicle', this.vehicleToBeDeleted.vehicleNo)
    },
    onHideDelete() {
      this.vehicleToBeDeleted = null
    },
    onSelectAllPartner() {
      this.params.partnerNo = null
      this.companyName = null
      this.$store.dispatch('loadVehiclesPage', this.params)
    },

    keydownHandler(event) {
      if (event.which === 13) {
        this.search()
      }
    },
    formatDate: function(date) {
      return cf.formatDate(date)
    },
  },

  created() {
    console.log('created vehicle/VehicleList')
    this.$store.dispatch('loadVehiclesPage', this.params)

    let that = this
    eventBus.$on('onSelectPopupPartner', function(partner){
      that.params.partnerNo = partner.partnerNo
      that.companyName = partner.companyName
      that.showPopupPartner = false;
      that.$store.dispatch('loadVehiclesPage', that.params)
    })

    eventBus.$on('onClosePopupPartnerList', function() {
      that.showPopupPartner = false
    })

  },

  mounted() {
    this.params = {
      page: this.vehiclesPage.page,
      scale: this.vehiclesPage.scale,
      query: this.vehiclesPage.query
    }
  }
}
</script>
<style>
.width-5 { width: 5% }
.width-10 { width: 10% }
.width-15 { width: 15% }
.partner-input .popover {
  max-width: 600px;
}
</style>