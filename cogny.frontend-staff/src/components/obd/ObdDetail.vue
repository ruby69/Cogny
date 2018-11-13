<template>
  <div class="col-md-12">
    <div class="card card-border-color card-border-color-primary">
      <div class="card-header card-header-divider font-weight-bold">
        {{ pageDetailTitle }}
        <span class="card-subtitle">{{ pageDetailDescription }}</span>
      </div>
      <div class="card-body">
        <form>
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
            <label for="inputObsSerial" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">OBD 단말기 번호</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-input type="text" class="form-control" id="inputObsSerial" placeholder="OBD 단말기 번호를 입력하세요." 
                            v-model.trim="form.obdSerial" maxlength="45" 
                            @input="$v.form.obdSerial.$touch()"
                            v-bind:class="{ 'is-invalid': $v.form.obdSerial.$error }"
                            aria-describedby="inputObsSerialLiveFeedback"/>
              <b-form-invalid-feedback id="inputObsSerialLiveFeedback">
                OBD 단말기 번호를 입력하세요.
              </b-form-invalid-feedback>
            </div>
          </div>
          <div class="form-group row vehicle-input" id="userObdDeviceVehicleContainer">
            <label for="selectVehicle" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">설치 차량</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-input-group>
                <b-form-input v-model.trim="licenseNo" placeholder="OBD 단말기를 설치할 차량을 선택하세요." readonly></b-form-input>
                <b-btn id="popoverSelectVehicle"
                    variant="primary"
                    v-b-tooltip.hover>차량 선택</b-btn>
                <b-btn
                    class="ml-1"
                    variant="primary"
                    @click="onRemoveVehicle()"
                    v-b-tooltip.hover title="차량에서 OBD 단말기를 제거합니다.">OBD 단말기 제거</b-btn>
                <b-popover container="userObdDeviceVehicleContainer" :show.sync="showObdVehiclePopup" :placement="'bottomleft'" target="popoverSelectVehicle" class="list-popover">
                  <PopupVehicleList :partnerNo="pageContents.partnerNo"/>
                </b-popover>
              </b-input-group>
            </div>
          </div>
          <div class="form-group row text-right">
            <div class="col col-sm-10 col-lg-9 offset-sm-1 offset-lg-0">
              <button type="button" class="btn btn-space btn-primary" @click="onSubmit()">확인</button>
              <button type="button" class="btn btn-space btn-secondary" @click="onCancel()">취소</button>
            </div>
          </div>  
        </form>
      </div>
    </div>
  </div>  
</template>
<script>
import { validationMixin } from 'vuelidate'
import { maxLength, required } from 'vuelidate/lib/validators'
import { mapGetters } from 'vuex'
import { eventBus } from '../common/EventBus.vue'
import PopupPartnerList from '../common/popup/PopupPartnerList.vue'
import PopupVehicleList from '../common/popup/PopupVehicleList.vue'

export default {
  name: 'ObdDetail',
  components: {
    PopupPartnerList,
    PopupVehicleList
  },
  mixins: [validationMixin],

  props: {
    pageDetailTitle : null,
    pageDetailDescription : null,
    pageContents : null
  },
  computed: mapGetters({
    currentUser: 'getCurrentUser'
  }),
  data: function() {
    return {
      showObdVehiclePopup: false,
      showPopupPartner: false,
      companyName: null,
      showSelectPartner: false,
      
      form: {
        obdDeviceVehicleNo: null,
        obdDeviceNo: null,
        vehicleNo: null,
        obdSerial: null,
        partnerNo: null,
        perspective: 'OBD'
      },
      licenseNo: null
    }
  },

  methods: {
    onRemoveVehicle: function() {
      this.form.vehicleNo = null
      this.licenseNo = null
    },

    onSubmit: function() {
      if (!this.$v.form.$invalid) {
        let that = this
        this.$store.dispatch('saveLinkVehicle', {
          data: this.form,
          callback: obd => {
            that.obd = obd
            this.$router.push({ name: 'obdList' })
          }
        })
      }
    }, 

    onCancel: function(){
      this.$router.push({ name: 'obdList' })
    },
  },

  created() {
    if(this.currentUser.role == 'ADMIN') {
      this.showSelectPartner = true
      if(this.pageContents.partner) {
        this.form.partnerNo = this.pageContents.partner.partnerNo
        this.companyName = this.pageContents.partner.companyName
      }      
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
    eventBus.$on('onSelectPopupVehicles', function(vehicle){
      that.form.vehicleNo = vehicle.vehicleNo
      that.licenseNo = vehicle.licenseNo
      that.showObdVehiclePopup = false
    })

    eventBus.$on('onClosePopupVehicleList', function() {
      that.showObdVehiclePopup = false
    })

    if (this.pageContents) {
      this.form.obdSerial = this.pageContents.obdSerial
      this.form.obdDeviceNo = this.pageContents.obdDeviceNo
      if(this.pageContents.vehicle) {
        this.form.obdDeviceVehicleNo = this.pageContents.vehicle.obdDeviceVehicleNo
        this.form.vehicleNo = this.pageContents.vehicle.vehicleNo
        this.licenseNo = this.pageContents.vehicle.licenseNo
      }
    }
    console.log('created obd/obdDetail')
  },

  validations: {
    form: {
      obdSerial: {
        required,
        maxLength: maxLength(45)
      },
    }
  }  
}
</script>
<style>
.vehicle-input .popover {
  max-width: 600px;
}
</style>
