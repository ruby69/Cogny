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
            <label for="inputCompanyName" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">회사명</label>
            <div class="col-12 col-sm-8 col-lg-6 pt-2">
              <span>{{ companyName }}</span>
            </div>
          </div>
          <div class="form-group row">
            <label for="inputEmail" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">이메일*</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-input-group>
                <b-form-input type="text" class="form-control" id="inputEmail" placeholder="이메일을 입력하세요." 
                              v-model.trim="form.email" maxlength="45"
                              @input="$v.form.email.$touch()" 
                              :class="{ 'is-invalid': $v.form.email.$error }"
                              aria-describedby="inputEmailInvalidLiveFeedback"
                              />
                <b-form-invalid-feedback id="inputEmailInvalidLiveFeedback">
                  {{ invalidEmailMessage }}
                </b-form-invalid-feedback>
              </b-input-group>
            </div>
          </div>
          <div class="form-group row">
            <label for="inputName" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">이름*</label>
              <div class="col-12 col-sm-8 col-lg-6">
              <b-form-input type="text" class="form-control" id="inputName" placeholder="관리자 이름을 입력하세요." 
                            v-model.trim="form.name" maxlength="45" 
                            @input="$v.form.name.$touch()" 
                            v-bind:class="{ 'is-invalid': $v.form.name.$error }"
                            aria-describedby="inputNameLiveFeedback"
                            />
              <b-form-invalid-feedback id="inputNameLiveFeedback">
                이름을 입력하세요.
              </b-form-invalid-feedback>
            </div>
          </div>
          <div class="form-group row">
            <label class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">전화</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-container>
                <b-row align-v="center">
                  <b-col cols="2" class="px-0">
                    <b-form-input type="text" id="inputTelFirst" 
                                  v-model.trim="form.telFirst" maxlength="3" 
                                  @input="$v.form.telFirst.$touch()" 
                                  :class="{ 'is-invalid': $v.form.telFirst.$error }"
                                  />
                  </b-col>
                  <b-col cols="1" class="px-0 text-center">
                    <span>-</span>
                  </b-col>
                  <b-col cols="4" class="px-0">
                    <b-form-input type="text"
                                  v-model.trim="form.telSecond" maxlength="4" 
                                  @input="$v.form.telSecond.$touch()" 
                                  v-bind:class="{ 'is-invalid': $v.form.telSecond.$error }"
                                  />
                  </b-col>
                  <b-col cols="1" class="px-0 text-center">
                    <span>-</span>                    
                  </b-col>
                  <b-col cols="4" class="px-0">
                    <b-form-input type="text"
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
            <label class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">핸드폰</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-container>
                <b-row align-v="center">
                  <b-col cols="2" class="px-0">
                    <b-form-input type="text" 
                            v-model.trim="form.hpNoFirst" maxlength="3" 
                            @input="$v.form.hpNoFirst.$touch()" 
                            :class="{ 'is-invalid': $v.form.hpNoFirst.$error }"
                            />
                  </b-col>
                  <b-col cols="1" class="px-0 text-center">
                    <span>-</span>
                  </b-col>
                  <b-col cols="4" class="px-0">
                    <b-form-input type="text"
                            v-model.trim="form.hpNoSecond" maxlength="4" 
                            @input="$v.form.hpNoSecond.$touch()" 
                            v-bind:class="{ 'is-invalid': $v.form.hpNoSecond.$error }"
                            />
                  </b-col>
                  <b-col cols="1" class="px-0 text-center">
                    <span>-</span>                    
                  </b-col>
                  <b-col cols="4" class="px-0">
                    <b-form-input type="text"
                              v-model.trim="form.hpNoThird" maxlength="4" 
                              @input="$v.form.hpNoThird.$touch()" 
                              v-bind:class="{ 'is-invalid': $v.form.hpNoThird.$error }"
                              />
                  </b-col>
                </b-row>
              </b-container>
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
import { helpers, email, minLength, maxLength, sameAs, numeric, required } from 'vuelidate/lib/validators'
import { mapGetters } from 'vuex'
import cf from '../../utils/customFilters'

// vuelidator 사용자 설정 함수
const name = helpers.regex('name', /^[a-zA-Z0-9가-힣\s]+$/)
const phoneFirst = helpers.regex('phoneFirst', /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})$/)

export default {
  name: 'currentUserDetail',
  mixins: [validationMixin],
  computed: mapGetters({
    currentUser: 'getCurrentUser',
  }),
  props: {
    pageDetailTitle: null,
    pageDetailDescription: null,
    pageContents: null,
    redirectRouter: null
  },
  data: function() {
    return {
      form: {
        userNo: null,
        email: null,
        name: null,
        partnerNo: null,
        hpNo: null,
        hpNoFirst: null,
        hpNoSecond: null,
        hpNoThird: null,
        tel: null,
        telFirst: null,
        telSecond: null,
        telThird: null,
      },
      invalidEmailMessage: "이메일을 입력하세요.",
      companyName: null,
    }
  },
  methods: {
    onSubmit: function() {
      this.$v.form.$touch()
      if (!this.$v.form.$invalid) {
        let that = this
        this.form.hpNo = cf.mergeTel(this.form.hpNoFirst,  this.form.hpNoSecond, this.form.hpNoThird)
        this.form.tel = cf.mergeTel(this.form.telFirst, this.form.telSecond, this.form.telThird)
        this.$store.dispatch('saveStaff', {
          data: this.form,
          callback: currentUser => {
            that.$router.push({ name: that.redirectRouter })
          }
        })
      }      
    },
    onCancel: function() {
      this.$router.push({ name: this.redirectRouter })
    }
  },
  created() {
    this.form.partnerNo = this.currentUser.partnerNo
    this.companyName = this.currentUser.partner ? this.currentUser.partner.companyName : null

    console.log('created currentUser/currentUserDetail')
    let that = this
    if (this.pageContents != undefined && this.pageContents != null) {
      this.companyName = this.pageContents.partner ? this.pageContents.partner.companyName : null
      this.form = {
        userNo: this.pageContents.userNo,
        email: this.pageContents.email,
        name: this.pageContents.name, 
        partnerNo: this.pageContents.partnerNo, 
        hpNo: this.pageContents.hpNo, 
        hpNoFirst: cf.convertTel(this.pageContents.hpNo)[0],
        hpNoSecond: cf.convertTel(this.pageContents.hpNo)[1],
        hpNoThird: cf.convertTel(this.pageContents.hpNo)[2],
        tel: this.pageContents.tel,
        telFirst: cf.convertTel(this.pageContents.tel)[0],
        telSecond: cf.convertTel(this.pageContents.tel)[1],
        telThird: cf.convertTel(this.pageContents.tel)[2],
      }
    }
  },
  validations: {
    form: {
      name: {
        required,
        name,
        minLength: minLength(2),
        maxLength: maxLength(20),
      },
      email: {
        required,
        email,
        maxLength: maxLength(45)
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
      hpNoFirst: {
        numeric,
        phoneFirst,
        minLength: minLength(2),
        maxLength: maxLength(3)
      },
      hpNoSecond: {
        numeric,
        minLength: minLength(3),
        maxLength: maxLength(4)
      },
      hpNoThird: {
        numeric,
        minLength: minLength(4),
        maxLength: maxLength(4)
      },
    }
  }
}
</script>
<style>
  .tel-first { width: 25% }
  .tel-second { width: 35% }
  .tel-third { width: 35% }  
</style>