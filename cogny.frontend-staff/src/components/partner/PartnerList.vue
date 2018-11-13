<template>
  <div class="card card-table p-2">
    <div class="card-header">
      <span class="font-weight-bold mx-0"> 제휴사 목록 </span>
    </div>
    <div class="card-body">
      <b-container class="mw-100 dataTables_wrapper container-fluid dt-bootstrap4">
        <b-row class="mb-3">
          <b-col cols="12" class="text-right">
            <button class="btn btn-rounded btn-space btn-primary mr-4" @click="newPartner">
              <i class="icon mdi material-icons">location_city</i> 제휴사 등록
            </button>            
          </b-col>
        </b-row>
        <b-row>
          <b-col cols="6">
            <div class="input-group input-search mx-4">
              <b-form-input type="text" class="form-control" placeholder="제휴사 검색" 
                            v-model="partnersPage.query" 
                            @keyup.enter.native="search()"
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
              <b-form-select v-model="partnersPage.scale" :options="scaleOptions" @input="search"/>
            </div>
            <span class="d-inline-flex">개</span>
          </b-col>
        </b-row>
        <b-row class="be-datatable-body mt-4 mx-1">
          <b-col>
            <b-table striped hover :fields="partnerFields" :items="partnersPage.contents" class="border-top border-bottom">
              <template slot="manager" slot-scope="row">
                <button type="button" class="btn btn-secondary" @click="showInfo(row.item)" v-b-tooltip.hover title="제휴사 정보수정"><i class="icon mdi mdi-edit"/></button>
                <button type="button" class="btn btn-secondary" @click="onDelete(row.item)" v-b-tooltip.hover title="삭제"><i class="icon mdi mdi-delete"/></button>                  
              </template>
            </b-table>
          </b-col>
        </b-row>
        <b-row class="be-datatable-footer mx-4">
          <b-col cols="5">
              <div class="dataTables_info" id="table1_info" role="status" aria-live="polite"> {{ partnersPage.page }} / {{ partnersPage.totalPages }} 페이지, 총 제휴사 : {{ partnersPage.total }}개</div>
          </b-col>
          <b-col cols="7">
            <div class="dataTables_paginate paging_simple_numbers" id="table1_paginate">
              <b-pagination align="right" size="md" 
                            :total-rows="partnersPage.total" 
                            v-model="partnersPage.page" 
                            :per-page="partnersPage.scale" 
                            @input="movePage" />
            </div>
          </b-col>
        </b-row>
      </b-container>
    </div>
    <div>
      <b-modal ref="deleteYnModal" @ok="onDeleteOk" @hide="onHideDelete">
        <div class="text-center font-enlarge">
          제휴사 '<span class="text-danger font-weight-bold">{{ partnerToBeDeleted ? partnerToBeDeleted.companyName : null }}</span>'의 <br/>정보를 삭제하시겠습니까? 
        </div>
      </b-modal>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import cf from '../../utils/customFilters'

export default {
  name: 'PartnerList',

  data: function(){
    return {
      params: {},
      scaleOptions: [
        { value: 15, text: 15 },
        { value: 30, text: 30 },
        { value: 50, text: 50 },
        { value: 100, text: 100 }
      ],
      partnerFields: [
        { key: 'partnerNo', label: '번호', class: 'text-center width-5'},
        { key: 'companyName', label: '회사명', class: 'text-center width-10'},
        { key: 'partnerTypeName', label: '구분', class: 'text-center width-10'},
        { key: 'tel', label: '전화번호', formatter: 'formatTel', class: 'text-center width-10'},
        { key: 'addrDetail', label: '주소', class: 'text-center width-15'},
        // { key: 'TODO', label: '계약기간', class: 'text-center'},
        { key: 'personInCharge', label: '담당자', class: 'text-center width-10'},
        { key: 'contractStatusName', label: '계약상태', class: 'text-center width-10'},
        { key: 'regDate', label: '등록일', formatter: 'formatDate', class: 'text-center width-10'},
        { key: 'updDate', label: '수정일', formatter: 'formatDate', class: 'text-center width-10'},
        { key: 'manager', label: '수정/삭제', class: 'text-center width-10'},
      ],
      partnerToBeDeleted: null
    }
  },

  computed: mapGetters({
    partnersPage: 'getPartnersPage'
  }),

  methods: {
    search() {
      this.params.query = this.partnersPage.query
      this.params.scale = this.partnersPage.scale
      this.params.page = 1
      this.$store.dispatch('loadPartnersPage', this.params)
    },
    movePage() {
      this.params.page = this.partnersPage.page
      this.$store.dispatch('loadPartnersPage', this.params)
    },
    newPartner() {
      this.$router.push({ path: '/partner/new' })
    },
    showInfo(partner) {
      this.$router.push({ name: 'partnerDetail', params: { partner : partner } })
    },
    onDelete(partner) {
      this.partnerToBeDeleted = partner
      this.$refs.deleteYnModal.show()
    },
    onDeleteOk() {
      this.$store.dispatch('deletePartner', this.partnerToBeDeleted.partnerNo)
    },
    onHideDelete() {
      this.partnerToBeDeleted = null
    },
    formatTel: function(tel) {
      return cf.formatTel(tel)
    },
    formatDate: function(date){
      return cf.formatDate(date)
    }
  }, 

  created() {
    console.log('created partner/PartnerList')
    this.$store.dispatch('loadPartnersPage', this.params)
  },

  mounted() {
    this.params = {
      page: this.partnersPage.page,
      scale: this.partnersPage.scale,
      query: this.partnersPage.query
    }
  }
}
</script>
<style>
  .width-5 { width: 5% }
  .width-10 { width: 10% }
  .width-15 { width: 15% }
</style>
