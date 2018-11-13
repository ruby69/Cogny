<template>
  <div class="col-md-12">
    <div class="card card-border-color card-border-color-primary">
      <div class="card-header card-header-divider font-weight-bold">
        {{ pageDetailTitle }}
        <span class="card-subtitle">{{ pageDetailDescription }}</span>
      </div>
      <div class="card-body">
        <form>
          <div class="form-group row">
            <label for="inputCompanyName" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">제휴사명*</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-input type="text" class="form-control" id="inputCompanyName" placeholder="제휴사명을 입력하세요." 
                            v-model.trim="form.companyName" maxlength="45" 
                            @input="$v.form.companyName.$touch()" 
                            v-bind:class="{ 'is-invalid': $v.form.companyName.$error }"
                            aria-describedby="inputCompanyNameLiveFeedback"
                            />
              <b-form-invalid-feedback id="inputCompanyNameLiveFeedback">
                제휴사명을 입력하세요.
              </b-form-invalid-feedback>
            </div>
          </div>
          <div class="form-group row">
            <label for="selectPartnerType" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">제휴사 종류*</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-select v-model="form.partnerType" id="selectPartnerType">
                <option :value="null">제휴사 종류를 선택하세요.</option>
                <option v-for="partnerTypeOption in partnerTypeOptions" :key="partnerTypeOption.value" :value="partnerTypeOption.value">{{ partnerTypeOption.text }}</option>
              </b-form-select>
            </div>
          </div>      
          <div class="form-group row">
            <label for="inputTel" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">전화번호</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-container>
                <b-row align-v="center">
                  <b-col cols="2" class="px-0">
                    <b-form-input type="text" class="tel-first" id="inputTelFirst" 
                                  v-model.trim="form.telFirst" maxlength="4" 
                                  @input="$v.form.telFirst.$touch()" 
                                  v-bind:class="{ 'is-invalid': $v.form.telFirst.$error }"
                                  />
                  </b-col>
                  <b-col cols="1" class="px-0 text-center">
                    <span>-</span>
                  </b-col>
                  <b-col cols="4" class="px-0">
                    <b-form-input type="text" class="tel-second" id="inputTelSecond"
                                  v-model.trim="form.telSecond" maxlength="4" 
                                  @input="$v.form.telSecond.$touch()" 
                                  v-bind:class="{ 'is-invalid': $v.form.telSecond.$error }"
                                  />
                  </b-col>
                  <b-col cols="1" class="px-0 text-center">
                    <span>-</span>                    
                  </b-col>
                  <b-col cols="4" class="px-0">
                    <b-form-input type="text" class="tel-third" id="inputTelThird"
                                  v-model.trim="form.telThird" maxlength="4" 
                                  @input="$v.form.telThird.$touch()" 
                                  v-bind:class="{ 'is-invalid': $v.form.telThird.$error }"
                                  />
                  </b-col>
                </b-row>
              </b-container>
            </div>
          </div>
          <div class="form-group row">
            <label for="inputAddrDetail" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">주소</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-input type="text" class="form-control" id="inputAddrDetail" placeholder="주소를 입력하세요." 
                            v-model.trim="form.addrDetail" maxlength="200" 
                            @input="$v.form.addrDetail.$touch()" 
                            v-bind:class="{ 'is-invalid': $v.form.addrDetail.$error }"
                            aria-describedby="inputAddrDetailLiveFeedback"
                            />
              <b-form-invalid-feedback id="inputAddrDetailLiveFeedback">
                주소를 입력하세요.
              </b-form-invalid-feedback>
            </div>
          </div>
          <div class="form-group row">
            <label for="inputPersonInCharge" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">담당자</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-input type="text" class="form-control" id="inputPersonInCharge" placeholder="담당자를 입력하세요." 
                            v-model.trim="form.personInCharge" maxlength="50" 
                            @input="$v.form.personInCharge.$touch()" 
                            v-bind:class="{ 'is-invalid': $v.form.personInCharge.$error }"
                            />
            </div>
          </div>
          <div class="form-group row">
            <label for="radioContractStatus" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">제휴상태</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-radio-group v-model="form.contractStatus" :options="contractStatusOptions" id="radioContractStatus"
                            class="form-check mt-2"
                            @input="$v.form.contractStatus.$touch()" 
                            v-bind:class="{ 'is-invalid': $v.form.contractStatus.$error }"
                            />
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
import { numeric, minLength, maxLength, required, helpers } from 'vuelidate/lib/validators'
import cf from '../../utils/customFilters'

// vuelidator 사용자 설정 함수
const phoneFirst = helpers.regex('phoneFirst', /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})$/)

export default {
  name: 'PartnerDetail',
  mixins: [validationMixin],

  props: {
    pageDetailTitle : null,
    pageDetailDescription : null,
    pageContents : null
  },

  data: function() {
    return {
      classCustomSelect: false,
      form: {
        partnerNo: null,
        companyName: null,
        partnerType: null,
        tel: null,
        telFirst: null,
        telSecond: null,
        telThird: null,
        postcodeNo: null,
        addrPostCode: null,
        addrDetail: null,
        personInCharge: null,
        contractStatus: null
      },
      partnerTypeOptions: [],
      contractStatusOptions: []
    }
  },

  methods: {
    onSubmit: function() {
      if (!this.$v.form.$invalid) {
        let that = this
        this.form.tel = this.form.telFirst + this.form.telSecond + this.form.telThird
        this.$store.dispatch('savePartner', {
          data: this.form,
          callback: partner => {
            that.partner = partner
            this.$router.push({ path: '/partner' })
          }
        })
      }
    },
    onCancel: function(){
      this.$router.push({ path: '/partner' })
    },
  },
  created() {
    if (this.pageContents != undefined && this.pageContents != null) {
      this.form = {
        partnerNo: this.pageContents.partnerNo,
        companyName: this.pageContents.companyName,
        partnerType: this.pageContents.partnerType,
        tel: this.pageContents.tel,
        telFirst: cf.convertTel(this.pageContents.tel)[0],
        telSecond: cf.convertTel(this.pageContents.tel)[1],
        telThird: cf.convertTel(this.pageContents.tel)[2],
        postcodeNo: this.pageContents.postcodeNo,
        addrPostCode: this.pageContents.addrPostCode,
        addrDetail: this.pageContents.addrDetail,
        personInCharge: this.pageContents.personInCharge,
        contractStatus: this.pageContents.contractStatus        
      }
    }
    console.log('created partner/partnerDetail')
  },

  mounted() {
    let that = this
    this.$store.dispatch('loadPartnerEnums', {
      callback: partnerEnums => {
        that.partnerTypeOptions = partnerEnums.partnerTypeEnums
        that.contractStatusOptions = partnerEnums.partnerStatusEnums
      }
    })
  },

  validations: {
    form: {
      companyName: {
        required,
        maxLength: maxLength(45)
      },
      partnerType: {
        required
      },
      telFirst: {
        numeric,
        phoneFirst,
        minLength: minLength(2),
        maxLength: maxLength(3)
      },
      telSecond: {
        numeric,
        minLength: minLength(3),
        maxLength: maxLength(4)
      },
      telThird: {
        numeric,
        minLength: minLength(4),
        maxLength: maxLength(4)
      },
      addrDetail: {
        maxLength: maxLength(200)
      },
      personInCharge: {
        maxLength: maxLength(50)
      },
      contractStatus:{
        required
      }
    }
  }  
}
</script>
<style>
  .width-5 { width: 5% }
  .width-10 { width: 10% }
  .width-25 { width: 25% }
  .width-30 { width: 30% }
</style>