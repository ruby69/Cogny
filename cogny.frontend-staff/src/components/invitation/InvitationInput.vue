<template>
  <div class="col-md-12">
    <div class="card card-border-color card-border-color-primary">
      <div class="card-header card-header-divider font-weight-bold">
        {{ pageDetailTitle }}
        <span class="card-subtitle">{{ pageDetailDescription }}</span>
      </div>
      <div class="card-body">
        <div class="form-group row" v-if="!showSelectPartnerInput">
          <label for="inputCompanyName" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">회사명</label>
          <div class="col-12 col-sm-8 col-lg-6 pt-2">
            <span>{{ companyName }}</span>
          </div>
        </div>
        <div class="form-group row partner-input" id="popupPartnerContainer" v-else>
          <label for="inputCompanyName" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">제휴사*</label>
          <div class="col-12 col-sm-8 col-lg-6">
            <b-input-group>
              <b-form-input v-model.trim="companyName" readonly></b-form-input>
              <b-btn id="popoverSelectPartner" variant="primary">제휴사 선택</b-btn>
              <b-popover container="popupPartnerContainer" :show.sync="showPopupPartner" :placement="'bottomleft'" target="popoverSelectPartner">
                <PopupPartnerList/>
              </b-popover>
            </b-input-group>
          </div>
        </div>
        <div class="form-group row">
          <label for="radioUserRole" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">사용자 구분</label>
          <div class="col-12 col-sm-8 col-lg-6">
            <b-form-radio-group v-model="form.role" :options="roleOptions" id="radioUserRole"
                          class="form-check mt-2"
                          />
          </div>
        </div>         
        <div class="form-group row">
          <label class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">초대할 사용자</label>
          <div class="col-12 col-sm-8 col-lg-6">
            <b-input-group>
              <b-form-input v-model.trim="inputInvitationList" @keyup.enter.native="onAddButton()" placeholder="초대할 사용자 정보를 입력하세요."></b-form-input>
              <b-btn variant="primary" @click="onAddButton()">추가</b-btn>
            </b-input-group>
            <b-form-text>사용자 정보는 '이름, 핸드폰; 이름, 핸드폰; ...'의 형식으로 입력하세요. (예: 홍길동, 010-1234-5678; 김철수, 010-9876-5432;)
            </b-form-text>
          </div>
        </div>
        <div class="form-group row">
          <label class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold"></label>
          <div class="col-12 col-sm-8 col-lg-6">
            <b-container v-show="showGuideMsg" class=" mt-2 new-invitation-list p-0">
              <b-row align-v="center" align-h="center" class="new-invitation-cell text-center">
                <b-col>초대할 사용자를 추가하세요.</b-col>
              </b-row>
            </b-container>
            <b-container v-show="!showGuideMsg">
              <b-row class="new-invitation-cell">
                  <b-col>
                    <b-table hover :fields="invitationFields" :items="form.userInvitationList" class="border-top border-bottom">
                      <template slot="nameSlot" slot-scope="row">
                        <div :class="{ 'text-danger': !row.item.validation.isValidName }" 
                              v-b-tooltip.hover :title="row.item.validation.isValidName ? '' : row.item.validation.nameValidationMsg">
                          {{ row.item.name }}
                        </div>
                      </template>
                      <template slot="hpNoSlot" slot-scope="row">
                        <div :class="{ 'text-danger': !row.item.validation.isValidHpNo }"
                              v-b-tooltip.hover :title="row.item.validation.isValidHpNo? '' : row.item.validation.hpNoValidationMsg">
                          {{ formatTel(row.item.hpNo) }}
                        </div>
                      </template>
                      <template slot="manageSlot" slot-scope="row">
                        <button type="button" class="close float-none" aria-label="Close" @click="onRemove(form.userInvitationList.indexOf(row.item))">
                          <span aria-hidden="true">&times;</span>
                        </button>
                      </template>
                    </b-table>
                  </b-col>
              </b-row>
            </b-container>
              <div class="mt-2">'<span class="text-primary">사용자 초대</span>'를 클릭하시면 사용자의 전화번호로 <span class="text-danger">초대 문자가 발송</span>됩니다.</div>
          </div>
        </div>
        <div class="form-group row text-right">
          <div class="col col-sm-10 col-lg-9 offset-sm-1 offset-lg-0">
            <button type="button" class="btn btn-space btn-primary" :disabled="!isSubmittable()" @click="onSubmit()">사용자 초대</button>
            <button type="button" class="btn btn-space btn-secondary" @click="onCancel()">취소</button>
          </div>
        </div>
      </div>
    </div>
    <div>
      <b-modal ref="alertModal" :ok-only="modalProp.isOkOnly" @ok="onModalOk" @hide="onHideModal">
        <div class="text-center font-enlarge text-bold">
          {{ modalProp.message }} 
        </div>
      </b-modal>
    </div>    
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { eventBus } from '../common/EventBus.vue'
import PopupPartnerList from '../common/popup/PopupPartnerList.vue'
import cf from '../../utils/customFilters'

export default {
  name: 'InvitationInput',

  components: {
    PopupPartnerList
  },

  props: {
    pageDetailTitle : null,
    pageDetailDescription : null
  },

  computed: mapGetters({
    currentUser: 'getCurrentUser'
  }),

  data: function() {
    return {
      showSelectPartnerInput: true,
      companyName: null,
      showPopupPartner: false,
      form: {
        /**
         * 데이터 샘플
         * userInvitationList: [
         *  { name: '홍길동', hpNo: '01012345678', validation: {isValidName: true, nameValidationMsg: '', isValidHpNo: true, hpNoValidationMsg: ''}},
         *  { name: '김철수', hpNo: '01054321234', validation: {isValidName: false, nameValidationMsg: '잘못된 형식의 이름입니다.', isValidHpNo: true, hpNoValidationMsg: ''}},
         * ]
         */
        userInvitationList: [],
        partnerNo: null,
        role: "DRIVER"
      },
      roleOptions: [
        { text: "운전자", value: "DRIVER"},
        { text: "정비담당자", value: "PARTNER_MECHANIC"}
      ],
      inputInvitationList: null,
      showGuideMsg: true,

      invitationFields: [
        { key: 'nameSlot', label: '이름', class: 'text-center'},
        { key: 'hpNoSlot', label: '핸드폰', class: 'text-center'},
        { key: 'manageSlot', label: '삭제', class: 'text-center'},
      ],
      modalProp: {
        isOkOnly: false,
        eventType: null,
        message: null,
      },
      isSubmitted: false,
    }
  },

  methods: {
    onAddButton: function() {
      if(this.inputInvitationList == null || this.inputInvitationList == "") {
        this.modalProp  = {
          isOkOnly: true,
          message: "초대할 사용자 정보를 입력하세요. ",
        }
        this.$refs.alertModal.show()   
        return
      }
      // 입력받은 초대 사용자 번호
      let newUserInvitations = this.inputInvitationList.split(";")
      newUserInvitations = newUserInvitations.filter(function(userInvitation) {
        return (userInvitation.trim() !== (undefined || null || ''))
      })

      if(newUserInvitations.length == undefined || newUserInvitations.length == null || newUserInvitations.length < 1) {
        this.modalProp  = {
          isOkOnly: true,
          message: "초대할 사용자 정보를 형식에 맞게 입력하세요 : " + this.inputInvitationList,
        }
        this.$refs.alertModal.show()   
        return
      }

      // 입력된 사용자 초대 정보의 유효성 확인
      let that = this
      newUserInvitations.forEach(function(newInvitation) {
        if(newInvitation == null) return
        let invitationInfo = newInvitation.split(",")
        if(invitationInfo.length == undefined || invitationInfo.length == null || invitationInfo.length != 2) {
          that.modalProp  = {
            isOkOnly: true,
            message: "초대할 사용자 정보를 형식에 맞게 입력하세요 : " + newInvitation,
          }
          that.$refs.alertModal.show()          
          return
        }
        that.form.userInvitationList.push({
          name: invitationInfo[0].trim(),
          hpNo: invitationInfo[1].trim().replace(/[^0-9]/g,""),
          validation: that.checkInvitationValidation(invitationInfo)
        })
      })
      if(this.form.userInvitationList.length > 0) {
        this.showGuideMsg = false
        this.inputInvitationList = null
      }
      this.isSubmittable()
    },
    checkInvitationValidation: function(invitationInfo){
      let rtn = {
        isValidName: true,
        nameValidationMsg: '',
        isValidHpNo: true,
        hpNoValidationMsg: ''
      }
      if(invitationInfo[0] == undefined || invitationInfo[0] == null || cf.isValidName(invitationInfo[0]) == false) {
        rtn.isValidName = false
        rtn.nameValidationMsg = '사용자 이름은 한글,영문,숫자로 입력하세요.'
      }

      if(invitationInfo[1] == undefined || invitationInfo[1] == null || cf.isValidHpNo(invitationInfo[1]) == false) {
        rtn.isValidHpNo = false
        rtn.hpNoValidationMsg = '잘못된 형식의 전화번호 입니다.'
      }
      return rtn
    },
    onRemove : function(invitationIndex) {
      this.form.userInvitationList.splice(invitationIndex, 1)
      if(this.form.userInvitationList.length === 0) this.showGuideMsg = true
    },    
    isSubmittable: function() {
      let rtn = true
      if(this.isSubmitted || this.form.userInvitationList.length <= 0 || !this.form.partnerNo) {
        rtn = false
        return rtn
      }
      this.form.userInvitationList.forEach(function(userInvitation) {
        if(userInvitation.validation.isValidName == false || userInvitation.validation.isValidHpNo == false) {
          rtn = false
        }
      })
      return rtn
    },
    onSubmit: function() {
      if(!this.isSubmittable()) return
      this.modalProp  = {
        isOkOnly: false,
        message: "입력하신 사용자를 초대하시겠습니까?",
        eventType: "CONFIRM_SUBMIT"
      }
      this.$refs.alertModal.show()
    },
    onSubmitConfirmed() {
      let that = this
      this.isSubmitted = true
      this.$store.dispatch('saveInvitationList', {
        data: this.form,
        callback: invitationsForm => {
          that.modalProp.isOkOnly = true
          that.modalProp.message = "사용자에게 초대 문자를 발송하였습니다."
          that.modalProp.eventType = "FINISH_INVITE"
          that.$refs.alertModal.show()
        }
      })
    }, 
    onModalOk() {
      switch(this.modalProp.eventType) {
        case 'FINISH_INVITE':
          this.$router.push({ name: 'invitationList' })
          break
        case 'CONFIRM_SUBMIT':
          this.onSubmitConfirmed()
          break
      }
    },
    onHideModal() {
      this.modalProp = {
        isOkOnly: false,
        eventType: null,
        message: null,
      }
    },    
    onCancel: function(){
      this.$router.push({ name: 'invitationList' })
    },
    formatTel: function(tel) {
      return cf.formatTel(tel)
    }
  },

  created() {
    console.log('created invitation/input')

    let that = this
    eventBus.$on('onSelectPopupPartner', function(partner){
      that.form.partnerNo = partner.partnerNo
      that.companyName = partner.companyName
      that.showPopupPartner = false;
    })
    eventBus.$on('onClosePopupPartnerList', function() {
      that.showPopupPartner = false
    })    
  }
}
</script>

<style>
.new-invitation-list {
  border: 1px solid #d5d8de;
}
.new-invitation-cell {
  height: 300px;
  overflow-y: auto;  
}
li.is-invalid {
  border-color: #dc3545;
}
span.is-invalid {
  color: #dc3545;
}
.partner-input .popover {
  max-width: 600px;
}
</style>