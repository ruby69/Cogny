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
            <label for="inputLicenseNo" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">차량번호*</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-input type="text" class="form-control" id="inputLicenseNo" placeholder="차량번호를 입력하세요." 
                            v-model.trim="form.licenseNo" maxlength="9" 
                            @input="$v.form.licenseNo.$touch()" 
                            v-bind:class="{ 'is-invalid': $v.form.licenseNo.$error }"
                            aria-describedby="inputLicenseNoLiveFeedback"
                            />
              <b-form-invalid-feedback id="inputLicenseNoLiveFeedback">
                7-9자리로 입력하세요.
              </b-form-invalid-feedback>
            </div>
          </div>
          <div class="form-group row">
            <label for="selectManufacturer" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">제조사</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-select v-model="selectedManufacturerNo" id="selectManufacturer" @change="onSelectManufacturer">
                <option :value="null">제조사선택</option>
                <option :value="null" disabled>------------------</option>
                <option v-for="manufacturer in manufacturers" :key="manufacturer.manufacturerNo" :value="manufacturer.manufacturerNo">{{ manufacturer.name }}</option>
              </b-form-select>
            </div>
          </div>
          <div class="form-group row">
            <label for="selectModelGroup" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">차종</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-select v-model="selectedModelGroupNo" id="selectModelGroup" @change="onSelectModelGroup">
                <option :value="null">차종선택</option>
                <option :value="null" disabled>------------------</option>
                <option v-for="modelGroup in modelGroups" :key="modelGroup.modelGroupNo" :value="modelGroup.modelGroupNo">{{ modelGroup.name }}</option>
              </b-form-select>
            </div>
          </div>
          <div class="form-group row">
            <label for="selectModel" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">모델</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-select v-model="form.modelNo" id="selectModel" @change="onSelectModel">
                <option :value="null">모델선택</option>
                <option :value="null" disabled>------------------</option>
                <option v-for="model in models" :key="model.modelNo" :value="model.modelNo">{{ model.name }}</option>
              </b-form-select>
            </div>
          </div>
          <div class="form-group row">
            <label for="selectFuel" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">유종</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-select v-model="form.fuelNo" id="selectFuel">
                <option :value="null" >유종선택</option>
                <option :value="null" disabled>------------------</option>
                <option v-for="fuel in fuels" :key="fuel.fuelNo" :value="fuel.fuelNo">{{ fuel.name }}</option>
              </b-form-select>
            </div>
          </div>
          <div class="form-group row">
            <label for="selectModelYear" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">제조년도</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-select v-model="form.modelYear" id="selectModelYear">
                <option :value="null">제조년도</option>
                <option :value="null" disabled>------------------</option>
                <option v-for="modelYear in modelSalesYear" :key="modelYear.value" :value="modelYear.value">{{ modelYear.text }}</option>
              </b-form-select>
            </div>
          </div>
          <div class="form-group row">
            <label for="selectModelMonth" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">제조월</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-select v-model="form.modelMonth" id="selectModelMonth">
                <option :value="null">제조월</option>
                <option :value="null" disabled>------------------</option>
                <option v-for="n in 12" :key="n" :value="n">{{ n }}월</option>
              </b-form-select>
            </div>
          </div>
          <div class="form-group row">
            <label for="inputMemo" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">메모</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <textarea id="inputMemo" class="form-control" v-model="form.memo"></textarea>
            </div>
          </div>          
          <div class="form-group row text-right">
            <div class="col col-sm-10 col-lg-9 offset-sm-1 offset-lg-0">
              <button type="button" class="btn btn-space btn-primary" @click="onSubmit">확인</button>
              <button type="button" class="btn btn-space btn-secondary" @click="onCancel">취소</button>
            </div>
          </div> 
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { validationMixin } from 'vuelidate'
import { minLength, maxLength, required } from 'vuelidate/lib/validators'
import { mapGetters } from 'vuex'
import { eventBus } from '../common/EventBus.vue'
import PopupPartnerList from '../common/popup/PopupPartnerList.vue'

export default {
  name: 'VehicleDetail',
  mixins: [validationMixin],

  computed: mapGetters({
    modelGroups: 'getModelGroups',
    models: 'getModels',
    modelSalesYear: 'getModelSalesYear',
    fuels: 'getFuels',
    manufacturers: 'getManufacturers',
    currentUser: 'getCurrentUser',
  }),
  components: {
    PopupPartnerList
  },
  props: {
    pageDetailTitle : null,
    pageDetailDescription : null,
    pageContents : null
  },

  data: function() {
    return {
      vehicle: {},
      selectedManufacturerNo: null,
      selectedModelGroupNo: null,
      showSelectPartner: false,
      showPopupPartner: false,

      form: {
        partnerNo: null,
        vehicleNo: null,
        licenseNo: null,
        modelNo: null,
        modelYear: null,
        modelMonth: null,
        fuelNo: null,
        memo: null
      },
      companyName: null
    }
  },

  methods: {
    onSelectManufacturer: function(selectedManufacturerNo) {
      let that = this
      this.$store.dispatch('loadModelGroupsByManufacturer', {
        manufacturerNo: selectedManufacturerNo,
        callback: () => {
          that.selectedModelGroupNo = null
          that.form.modelNo = null
          that.form.modelYear = null
          that.form.modelMonth = null
          that.form.fuelNo = null
        }
      })
    },
    onSelectModelGroup: function(selectedModelGroupNo) {
      let form = this.form
      this.$store.dispatch('loadModelsByModelGroup', {
        modelGroupNo: selectedModelGroupNo,
        callback: () => {
          form.modelNo = null
          form.modelYear = null
          form.modelMonth = null
          form.fuelNo = null
        }
      })
    },
    onSelectModel: function(selectedModelNo) {
      let form = this.form
      this.$store.dispatch('loadModelSalesYear', {
        modelNo: selectedModelNo,
        callback: () => {
          form.modelMonth = null
        }
      })
    },
    onSubmit: function() {
      if (!this.$v.form.$invalid) {
        let that = this
        this.$store.dispatch('saveVehicle', {
          data: this.form,
          callback: vehicle => {
            that.vehicle = vehicle
            that.$router.push({ name: 'vehicleList' })
          }
        })
      }
    }, 
    onCancel: function(){
      this.$router.push({ name: 'vehicleList' })
    },
    callbackForLoadModelGroupsByManufacturer: function() {
      let that = this
      this.selectedManufacturerNo = this.pageContents.model.modelGroup.manufacturer.manufacturerNo
      this.$store.dispatch('loadModelsByModelGroup', {
        modelGroupNo: that.pageContents.model.modelGroup.modelGroupNo,
        callback: () => {
          that.callbackForLoadModelsByModelGroup()
        }
      })
    },
    callbackForLoadModelsByModelGroup: function() {
      let that = this
      this.selectedModelGroupNo = this.pageContents.model.modelGroup.modelGroupNo
      this.$store.dispatch('loadModelSalesYear', {
        modelNo: that.pageContents.model.modelNo,
        callback: () => {
          that.callbackForLoadModelSalesYear()
        }
      })
    },
    callbackForLoadModelSalesYear: function () {
      this.form = {
        partnerNo: this.pageContents.partnerNo,
        vehicleNo: this.pageContents.vehicleNo,
        licenseNo: this.pageContents.licenseNo,
        modelNo: this.pageContents.model.modelNo,
        modelYear: this.pageContents.modelYear,
        modelMonth: this.pageContents.modelMonth,
        fuelNo: this.pageContents.fuel.fuelNo,
        memo: this.pageContents.memo,
      }
    },
  },
  created() {
    if(this.currentUser.role == 'ADMIN') {      
      this.showSelectPartner = true
    } else if (this.currentUser.role == 'PARTNER_MECHANIC'  && this.currentUser.partner ) {
      this.showSelectPartner = false
      this.form.partnerNo = this.currentUser.partnerNo
      this.companyName = this.currentUser.partner.companyName
    } else {
      console.error("error on VehicleDetail during created()")
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

    if(!this.fuels) this.$store.dispatch('loadFuels')
    if(!this.manufacturers) this.$store.dispatch('loadManufacturers')

    if (this.pageContents != undefined && this.pageContents != null) {
      this.companyName = this.pageContents.partner.companyName
      let that = this
      this.$store.dispatch('loadModelGroupsByManufacturer', {
        manufacturerNo: that.pageContents.model.modelGroup.manufacturer.manufacturerNo,
        callback : () => {
          that.callbackForLoadModelGroupsByManufacturer()
        }
      })
    }
    console.log('created vehicle/vehicleDetail')
  },
  validations: {
    form: {
      partnerNo: {
        required
      },
      licenseNo: {
        required,
        minLength: minLength(7),
        maxLength: maxLength(9)
      },
      modelNo: {
        required
      },
      modelYear: {
        required
      },
      fuelNo: {
        required
      }
    }
  }
}
</script>
<style>
.partner-input .popover {
  max-width: 600px;
}
</style>