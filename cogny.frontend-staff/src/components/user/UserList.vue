<template>
  <div class="card card-table p-2">
    <div class="card-header">
      <span class="font-weight-bold mx-0"> 사용자 목록 </span>
    </div>
    <div class="card-body">
      <b-container class="mw-100 dataTables_wrapper container-fluid dt-bootstrap4">
        <b-row class="mb-3">
          <b-col cols="12" class="text-right">
            <button class="btn btn-rounded btn-space btn-primary mr-4" @click="newUser">
              <i class="icon mdi material-icons">group</i> 사용자 등록
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
              <b-form-input type="text" class="form-control" placeholder="사용자 검색" 
                            v-model="usersPage.query" 
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
              <b-form-select v-model="usersPage.scale" :options="scaleOptions" @input="search"/>
            </div>
            <span class="d-inline-flex">개</span>
          </b-col>
        </b-row>
        <b-row class="be-datatable-body mt-4 mx-1">
          <b-col>
            <b-table striped hover :fields="userFields" :items="usersPage.contents" class="border-top border-bottom">
              <template slot="partnerCompanySlot" slot-scope="row">
                <div>
                  {{ row.item.partner && row.item.partner.companyName ? row.item.partner.companyName : "-" }}
                </div>
              </template>
              <template slot="manager" slot-scope="row">
                <div>
                  <button type="button" class="btn btn-secondary" @click="showInfo(row.item)" v-b-tooltip.hover title="사용자정보수정"><i class="icon mdi mdi-edit"/></button>
                  <button type="button" class="btn btn-secondary" @click="onDelete(row.item)" v-b-tooltip.hover title="삭제"><i class="icon mdi mdi-delete"/></button>
                </div>
              </template>
            </b-table>
          </b-col>
        </b-row>
        <b-row class="be-datatable-footer mx-4">
          <b-col cols="5">
              <div class="dataTables_info" id="table1_info" role="status" aria-live="polite"> {{ usersPage.page }} / {{ usersPage.totalPages }} 페이지, 총 제휴사 : {{ usersPage.total }}개</div>
          </b-col>
          <b-col cols="7">
            <div class="dataTables_paginate paging_simple_numbers" id="table1_paginate">
              <b-pagination align="right" size="md" 
                            :total-rows="usersPage.total" 
                            v-model="usersPage.page" 
                            :per-page="usersPage.scale" 
                            @input="movePage" />
            </div>
          </b-col>
        </b-row>
      </b-container>
    </div>
    <div>
      <b-modal ref="deleteYnModal" @ok="onDeleteOk" @hide="onHideDelete">
        <div class="text-center font-enlarge">
          사용자 '<span class="text-danger font-weight-bold">{{ userToBeDeleted ? userToBeDeleted.name : null }}</span>'의 <br/> 정보를 삭제하시겠습니까? 
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
  name: 'UserList',
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
      userFields: [
        { key: 'userNo', label: '번호', class: 'text-center width-5'},
        { key: 'partnerCompanySlot', label: '제휴사명', class: 'text-center width-10'},
        { key: 'email', label: '이메일', class: 'text-center width-10'},
        { key: 'name', label: '사용자명', class: 'text-center width-10'},
        { key: 'tel', label: '전화', formatter: 'formatTel', class: 'text-center width-10'},
        { key: 'hpNo', label: '핸드폰', formatter: 'formatTel', class: 'text-center width-10'},
        { key: 'userStatusName', label: '상태', class: 'text-center width-10'},
        { key: 'roleName', label: '권한', class: 'text-center width-5'},
        { key: 'regDate', label: '등록일', formatter: 'formatDate', class: 'text-center width-10'},
        { key: 'updDate', label: '수정일', formatter: 'formatDate', class: 'text-center width-10'},
        { key: 'manager', label: '수정/삭제', class: 'text-center width-10'},
      ],
      userToBeDeleted: null
    }
  },

  computed: mapGetters({
    usersPage: 'getUserPage'
  }),

  methods: {
    search() {
      this.params.query = this.usersPage.query
      this.params.scale = this.usersPage.scale
      this.params.page = 1
      this.$store.dispatch('loadUsersPage', this.params)
    },
    movePage() {
      this.params.page = this.usersPage.page
      this.$store.dispatch('loadUsersPage', this.params)
    },
    newUser() {
      this.$router.push({ path: '/user/new' })
    },
    showInfo(user) {
      this.$router.push({ name: 'userDetail', params: { user : user } })
    },
    onDelete(user) {
      this.userToBeDeleted = user
      this.$refs.deleteYnModal.show()
    },
    onDeleteOk() {
      this.$store.dispatch('deleteUser', this.userToBeDeleted.userNo)
    },
    onHideDelete() {
      this.userToBeDeleted = null
    },
    onSelectAllPartner() {
      this.params.partnerNo = null
      this.companyName = null
      this.$store.dispatch('loadUsersPage', this.params)
    },

    keydownHandler(event) {
      if (event.which === 13) {
        this.search()
      }
    },
    formatTel: function(tel) {
      return cf.formatTel(tel)
    },
    formatDate: function(value) {
      return cf.formatDate(value)
    }
  },

  created() {
    console.log('created user/User')
    this.$store.dispatch('loadUsersPage', this.params)

    let that = this
    eventBus.$on('onSelectPopupPartner', function(partner){
      that.params.partnerNo = partner.partnerNo
      that.companyName = partner.companyName
      that.showPopupPartner = false;
      that.$store.dispatch('loadUsersPage', that.params)
    })

    eventBus.$on('onClosePopupPartnerList', function() {
      that.showPopupPartner = false
    })
  },

  mounted() {
    this.params = {
      page: this.usersPage.page,
      scale: this.usersPage.scale,
      query: this.usersPage.query,
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
