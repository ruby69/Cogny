<template>
  <div class="card card-table p-2">
    <div class="card-header">
      <span class="font-weight-bold mx-0"> 사용자 초대 이력 </span>
    </div>
    <div class="card-body">
      <b-container class="mw-100 dataTables_wrapper container-fluid dt-bootstrap4 p-0">
        <b-row class="mb-3">
          <b-col cols="12" class="text-right">
            <button class="btn btn-rounded btn-space btn-primary mr-4" @click="newInvitation()">
              <i class="icon mdi material-icons">person_add</i> 사용자 초대
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
              <b-form-input type="text" class="form-control" placeholder="사용자 초대 이력 검색" 
                            v-model="invitationsPage.query" 
                            @keyup.enter.native="search()"
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
              <b-form-select v-model="invitationsPage.scale" :options="scaleOptions" @input="search()"/>
            </div>
            <span class="d-inline-flex">개</span>
          </b-col>
        </b-row>
        <b-row class="be-datatable-body mt-4 mx-1">
          <b-col>
            <b-table striped hover :fields="invitationsFields" :items="invitationsPage.contents" class="border-top border-bottom">
              <template slot="partnerSlot" slot-scope="row">
                <div> {{ row.item.partner ? row.item.partner.companyName : '-'}}</div>
              </template>
              <template slot="signUpStatusSlot" slot-scope="row">
                <div> {{ row.item.signUpDate ? '가입완료' : '가입대기'}}</div>
              </template>
              <template slot="manager" slot-scope="row">
                <div v-show="!row.item.signUpDate">
                  <!-- <button type="button" class="btn btn-secondary" @click="showInfo(row.item)" v-b-tooltip.hover title="OBD 단말기 정보수정"><i class="icon mdi mdi-edit"/></button> -->
                  <button type="button" class="btn btn-secondary" @click="onDelete(row.item)" v-b-tooltip.hover title="삭제"><i class="icon mdi mdi-delete"/></button>
                </div>
              </template>
            </b-table>
          </b-col>
        </b-row>
        <b-row class="be-datatable-footer mx-4">
          <b-col cols="5">
            <div class="dataTables_info" id="table1_info" role="status" aria-live="polite"> {{ invitationsPage.page }} / {{ invitationsPage.totalPages }} 페이지, 총 : {{ invitationsPage.total }}개</div>
          </b-col>
          <b-col cols="7">
            <div class="dataTables_paginate paging_simple_numbers" id="table1_paginate">
              <b-pagination align="right" size="md" 
                            :total-rows="invitationsPage.total" 
                            v-model="invitationsPage.page" 
                            :per-page="invitationsPage.scale" 
                            @input="movePage()" />
            </div>
          </b-col>
        </b-row>
      </b-container>
    </div>
    <div>
      <b-modal ref="deleteYnModal" @ok="onDeleteOk" @hide="onHideDelete">
        <div class="text-center font-enlarge">
          사용자 '<span class="text-danger font-weight-bold">{{ invitationToBeDeleted ? invitationToBeDeleted.name : null }}</span>' <br/> 초대를 취소하시겠습니까? 
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
  name: 'InvitationList',
  components: {
    PopupPartnerList
  }, 

  computed: mapGetters({ 
    invitationsPage: 'getInvitationsPage'
  }),

  data: function() {
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
      invitationsFields: [
        { key: 'userInvitationNo', label: '번호', class: 'text-center width-5'},
        { key: 'name', label: '이름', class: 'text-center width-10'},
        { key: 'hpNo', label: '핸드폰', formatter: 'formatTel', class: 'text-center width-15'},
        { key: 'partnerSlot', label: '제휴사', class: 'text-center width-10'},
        { key: 'roleName', label: '구분', class: 'text-center width-5'},
        { key: 'invitationCode', label: '초대코드', class: 'text-center width-5'},
        { key: 'regUser.name', label: '초대인', class: 'text-center width-10'},
        { key: 'regDate', label: '초대일', formatter: 'formatDate', class: 'text-center width-10'},
        { key: 'signUpDate', label: '가입일', formatter: 'formatDate', class: 'text-center width-10'},
        { key: 'signUpStatusSlot', label: '상태', class: 'text-center width-10'},
        { key: 'manager', label: '초대취소', class: 'text-center width-10'},
      ],
      invitationToBeDeleted: null
    }
  },

  methods: {
    search() {
      this.params.query = this.invitationsPage.query
      this.params.scale = this.invitationsPage.scale
      this.params.page = 1
      this.$store.dispatch('loadInvitationsPage', this.params)
    },
    movePage() {
      this.params.page = this.invitationsPage.page
      this.$store.dispatch('loadInvitationsPage', this.params)
    },
    newInvitation() {
      this.$router.push({ name: 'invitationNew' })
    },
    showInfo(userInvitation) {
      this.$router.push({ name: 'invitationDetail', params: { userInvitation : userInvitation } })
    },
    onDelete(userInvitation) {
      this.invitationToBeDeleted = userInvitation
      this.$refs.deleteYnModal.show()
    },
    onDeleteOk() {
      this.$store.dispatch('deleteInvitation', this.invitationToBeDeleted.userInvitationNo)
    },
    onHideDelete() {
      this.invitationToBeDeleted = null
    }, 
    onSelectAllPartner() {
      this.params.partnerNo = null
      this.companyName = null
      this.$store.dispatch('loadInvitationsPage', this.params)
    },
    formatTel: function(tel) {
      return cf.formatTel(tel)
    },    
    formatDate: function(date) {
      return cf.formatDate(date)
    }    
  },

  created() {
    console.log('created invitation/List')
    this.$store.dispatch('loadInvitationsPage', this.params)

    let that = this
    eventBus.$on('onSelectPopupPartner', function(partner){
      that.params.partnerNo = partner.partnerNo
      that.companyName = partner.companyName
      that.showPopupPartner = false;
      that.$store.dispatch('loadInvitationsPage', that.params)
    })

    eventBus.$on('onClosePopupPartnerList', function() {
      that.showPopupPartner = false
    })
  },
  mounted() {
    this.params = {
      page: this.invitationsPage.page,
      scale: this.invitationsPage.scale,
      query: this.invitationsPage.query
    }
  }   
}
</script>
<style>
  .width-10 { width: 10% }
  .width-15 { width: 15% }
  .width-25 { width: 25% }
  .partner-input .popover {
    max-width: 600px;
}</style>

