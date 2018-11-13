<template>
    <div class="col-md-12">
    <div class="card card-border-color card-border-color-primary">
      <div class="card-header card-header-divider font-weight-bold">
        {{ pageDetailTitle }}
        <span class="card-subtitle">{{ pageDetailDescription }}</span>
      </div>
      <div class="card-body">
        <div class="form-group row" v-if="!showSelectPartner">
          <label for="inputCompanyName" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">회사명</label>
          <div class="col-12 col-sm-8 col-lg-6 pt-2">
            <span>{{ companyName }}</span>
          </div>
        </div>
        <div class="form-group row vehicle-input" id="popupPartnerContainer" v-else>
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
          <label class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">OBD 단말기 고유번호</label>
          <div class="col-12 col-sm-8 col-lg-6">
            <b-input-group>
              <b-form-input v-model.trim="inputObdSerials" @keyup.enter.native="onAddButton()" placeholder="등록할 OBD 단말기의 고유번호를 입력하세요."></b-form-input>
              <b-btn variant="primary" @click="onAddButton()">추가</b-btn>
            </b-input-group>
            <b-form-text>두 개이상의 OBD 단말기 번호는 ','로 구분하여 입력하세요. (예: 12345,5667,5566)
            </b-form-text>
          </div>
        </div>
        <div class="form-group row">              
          <label class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold"></label>
          <div class="col-12 col-sm-8 col-lg-6">
            <b-container class=" mt-2 new-obd-list p-0">
                <b-row v-show="showGuideMsg" align-v="center" align-h="center" class="new-obd-cell text-center">
                  <b-col>등록할 OBD 단말기를 추가하세요.</b-col>
                </b-row>
                <b-row v-show="!showGuideMsg"  class="new-obd-cell">
                    <b-col>
                      <ul class="list-group">
                        <li v-for="selectedObdDevice in selectedObdDeviceList" 
                            :key="selectedObdDeviceList.indexOf(selectedObdDevice)" 
                            class="list-group-item d-flex justify-content-between align-items-center mb-1" 
                            :class="{'is-invalid': selectedObdDevice.classIsDup}">
                              {{ selectedObdDevice.obdSerial }}
                          <button type="button" class="close" aria-label="Close" @click="onRemove(selectedObdDeviceList.indexOf(selectedObdDevice))">
                            <span aria-hidden="true" :class="{'is-invalid': selectedObdDevice.classIsDup}">&times;</span>
                          </button>
                        </li>
                      </ul>
                    </b-col>
                </b-row>
            </b-container>
            <div v-show="hasDupObdOnDb()" class="text-danger mt-1">
              이미 등록되어 있는 OBD 단말기가 입력되었습니다.
            </div>
          </div>
        </div>
        <div class="form-group row text-right">
          <div class="col col-sm-10 col-lg-9 offset-sm-1 offset-lg-0">
            <button type="button" class="btn btn-space btn-primary" :disabled="!isSubmittable()" @click="onSubmit()">저장</button>
            <button type="button" class="btn btn-space btn-secondary" @click="onCancel()">취소</button>
          </div>
        </div>
      </div>
    </div>
  </div>        
</template>
<script >
import { mapGetters } from 'vuex'
import { eventBus } from '../common/EventBus.vue'
import PopupPartnerList from '../common/popup/PopupPartnerList.vue'

export default {
  name: 'ObdInput',
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
      showPopupPartner: false,
      inputObdSerials: null,
      form: {
        obdDeviceList: [],
        partnerNo: null
      },
      companyName: null,
      showSelectPartner: true,
      /** 
       * UI에서 입력된 OBD 단말기 번호 관리 변수
       * 예> 
       * selectedObdDeviceList: [{
       *   obdSerial: '1234',
       *   classIsDup: null
       * }],
       **/
      selectedObdDeviceList: [],

      isSubmitted: false,
      showGuideMsg: true,
      onSubmitModalShow: false,
    }
  },
  
  methods: {
    onAddButton : function() {
      if(this.inputObdSerials == "") return

      // 입력받은 obd 단말기 번호
      let newObdSerials = this.inputObdSerials.split(",")
      newObdSerials = newObdSerials.filter(function(obdSerial) {
        return (obdSerial !== (undefined || null || ''))
      })

      // 입력된 obd 단말 번호가 중복인지 확인
      let that = this
      this.form.obdDeviceList = []
      newObdSerials.forEach(function (newObdSerial) {
        that.form.obdDeviceList.push({obdSerial: newObdSerial.trim()})
      })

      // 서버의 obd 단말 번호가 중복인지 확인
      this.$store.dispatch('checkObdIsDup', {
        data: this.form,
        callback: dupedOnDbObds => {
          newObdSerials.forEach(function(newObdSerial) {
            // UI에서 입력받은 기존 목록에 중복이 없는 경우
            if(!that.checkIsDupObdSerial(newObdSerial, that.selectedObdDeviceList)) {
              that.selectedObdDeviceList.push({
                obdSerial: newObdSerial, 
                // 서버에 중복된 OBD 단말기 번호가 있는 경우
                classIsDup : that.checkObdIsDupOnDb(newObdSerial, dupedOnDbObds)
                })
            }
            // 화면 렌더링
            if(that.selectedObdDeviceList.length > 0) {
              that.showGuideMsg = false
              that.inputObdSerials = null
            }
          })
        }
      })
    },

    onRemove : function(obdDeviceIndex) {
      this.selectedObdDeviceList.splice(obdDeviceIndex, 1)
      if(this.selectedObdDeviceList.length === 0) this.showGuideMsg = true
    },
    checkIsDupObdSerial: function(newObdSerial, obdDeviceList) {
      let rtn = false
      obdDeviceList.forEach(function (obdDevice) {
        if (newObdSerial == obdDevice.obdSerial) rtn = true
      })
      return rtn
    },
    checkObdIsDupOnDb: function(newObdSerial, dupedOnDbObds) {
      let rtn = false
      if (dupedOnDbObds != null && dupedOnDbObds.length > 0) {
        dupedOnDbObds.forEach(function(dupedOnDbObd) {
          if (newObdSerial == dupedOnDbObd.obdSerial) rtn = true
        })
      }
      return rtn
    },
    hasDupObdOnDb: function () {
      let rtn = false
      this.selectedObdDeviceList.forEach(function(selectedObdDevice) {
        if (selectedObdDevice.classIsDup == true) rtn = true
      })
      return rtn
    },
    isSubmittable: function() {
      if(this.isSubmitted || this.selectedObdDeviceList.length <= 0 || this.hasDupObdOnDb() || !this.form.partnerNo) {
        return false
      } else {
        return true
      }
    },
    onSubmit: function() {
      if(!this.isSubmittable()) return

      let that = this
      this.isSubmitted = true
      this.form.obdDeviceList = []
      this.selectedObdDeviceList.forEach(function(selectedObdDevice) {
        that.form.obdDeviceList.push({obdSerial: selectedObdDevice.obdSerial.trim()})
      })
      this.$store.dispatch('saveObdList', {
        data: this.form,
        callback: obds => {
          that.obds = obds
          this.$router.push({ name: 'obdList' })
        }
      })
    },
    onCancel: function(){
      this.$router.push({ name: 'obdList' })
    }
  },
  created() {
    if(this.currentUser.role == 'ADMIN') {
      this.showSelectPartner = true
    } else if (this.currentUser.role == 'PARTNER_MECHANIC' && this.currentUser.partner ) {
      this.showSelectPartner = false
      this.form.partnerNo = this.currentUser.partnerNo
      this.companyName = this.currentUser.partner.companyName
    } else {
      console.error("error on OpdInput during created()")
    }

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
.new-obd-list {
  border: 1px solid #d5d8de;
}
.new-obd-cell {
  height: 300px;
  overflow-y: auto;  
}
li.is-invalid {
  border-color: #dc3545;
}
span.is-invalid {
  color: #dc3545;
}
.vehicle-input .popover {
  max-width: 600px;
}
</style>



