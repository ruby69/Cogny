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
            <label for="inputCompanyName" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">제휴사</label>
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
            <label for="inputEmail" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">사용자 이메일*</label>
              <div class="col-12 col-sm-8 col-lg-6">
                <b-input-group>
                  <b-form-input type="text" class="form-control" id="inputEmail" placeholder="이메일을 입력하세요." 
                                v-model.trim="form.email" maxlength="45" 
                                @input="onInputEmail()" 
                                :class="{ 'is-invalid': $v.form.email.$error }"
                                aria-describedby="inputEmailInvalidLiveFeedback"
                                :readonly="isModifyUser"
                                />
                  <b-btn variant="primary" @click="checkDupEmail()">중복확인</b-btn>
                  <b-form-invalid-feedback id="inputEmailInvalidLiveFeedback">
                    {{ invalidEmailMessage }}
                  </b-form-invalid-feedback>
                  <div class="valid-message" :class="{'invalid-email': $v.form.email.$error, 'valid-email': !$v.form.email.$error}">
                    {{ validEmailMessage }}
                  </div>
                </b-input-group>
            </div>
          </div>
          <div class="form-group row" v-if="!isModifyUser">
            <label for="inputPassword" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">비밀번호*</label>
              <div class="col-12 col-sm-8 col-lg-6">
              <b-form-input type="password" class="form-control" id="inputPassword" placeholder="비밀번호를 입력하세요." 
                            v-model.trim="form.password" maxlength="45" 
                            @input="$v.form.password.$touch()" 
                            :class="{ 'is-invalid': $v.form.password.$error }"
                            aria-describedby="inputPasswordLiveFeedback"
                            />
              <b-form-invalid-feedback id="inputPasswordLiveFeedback">
                숫자, 영문, 특수문자(!,@,#,$,%,^,&,*,?,_,~,-) 6~20단어로 입력하세요.
              </b-form-invalid-feedback>
            </div>
          </div>
          <div class="form-group row" v-if="!isModifyUser">
            <label for="inputPasswordRe" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">비밀번호 확인*</label>
              <div class="col-12 col-sm-8 col-lg-6">
              <b-form-input type="password" id="inputPasswordRe" placeholder="비밀번호를 한번 더 입력하여 확인하세요." 
                            v-model.trim="form.passwordRe" maxlength="45" 
                            @input="$v.form.passwordRe.$touch()" 
                            :class="{ 'is-invalid': $v.form.passwordRe.$error }"
                            aria-describedby="inputPasswordReLiveFeedback"
                            />
              <b-form-invalid-feedback id="inputPasswordReLiveFeedback">
                비밀번호를 한번 더 입력하세요.
              </b-form-invalid-feedback>
            </div>
          </div>
          <div class="form-group row">
            <label for="inputUserName" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">사용자 이름*</label>
              <div class="col-12 col-sm-8 col-lg-6">
              <b-form-input type="text" class="form-control" id="inputUserName" placeholder="사용자 이름을 입력하세요." 
                            v-model.trim="form.name" maxlength="45" 
                            @input="$v.form.name.$touch()" 
                            v-bind:class="{ 'is-invalid': $v.form.name.$error }"
                            aria-describedby="inputUserNameLiveFeedback"
                            />
              <b-form-invalid-feedback id="inputUserNameLiveFeedback">
                사용자 이름을 입력하세요.
              </b-form-invalid-feedback>
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
          <div class="form-group row mt-2" v-show="isModifyUser">
            <label for="selectUserStatus" class="col-12 col-sm-3 col-form-label text-sm-right font-weight-bold">가입상태*</label>
            <div class="col-12 col-sm-8 col-lg-6">
              <b-form-select v-model="form.userStatus" id="selectUserStatus" :class="{ 'is-invalid': $v.form.userStatus.$error }" :disabled="!isModifyUser">
                <option :value="null">가입 상태를 선택하세요.</option>
                <option v-for="userStatusOption in userStatusOptions" :key="userStatusOption.value" :value="userStatusOption.value">{{ userStatusOption.text }}</option>
              </b-form-select>
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
import { alphaNum, email, sameAs, numeric, minLength, maxLength, required, helpers } from 'vuelidate/lib/validators'
import { mapGetters } from 'vuex'
import { eventBus } from '../common/EventBus.vue'
import PopupPartnerList from '../common/popup/PopupPartnerList.vue'
import cf from '../../utils/customFilters'

// vuelidator 사용자 설정 함수
const alphaNumSpecial = helpers.regex('alphaNumSpecial', /([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~,-])|([!,@,#,$,%,^,&,*,?,_,~,-].*[a-zA-Z0-9])/)
const name = helpers.regex('name', /^[a-zA-Z0-9가-힣\s]+$/)
const phoneFirst = helpers.regex('phoneFirst', /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})$/)
const passwdRequired = function(value) {
  return helpers.req(value) || this.isModifyUser
}
const isNotEmailDuplicated = function(value) {
  return !helpers.req(value) || this.isNotEmailDuplicatedBool
  }
const checkedEmailDuplicated = function(value) {
  return !helpers.req(value) || this.checkedEmailDuplicatedBool
  }

export default {
  name: 'UserDetail',
  mixins: [validationMixin],
  computed: mapGetters({
    currentUser: 'getCurrentUser'
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
      showSelectPartner: false,
      showPopupPartner: false,
      form: {
        userNo: null, 
        email: null, 
        password: null,
        passwordRe: null,
        name: null, 
        partnerNo: null, 
        signProvider: "COGNY",
        hpNo: null, 
        hpNoFirst: null,
        hpNoSecond: null,
        hpNoThrid: null,
        userStatus: null, 
        role: null
      },
      isModifyUser: false,
      isInputNewUser: false,
      isNotEmailDuplicatedBool: false,
      checkedEmailDuplicatedBool: false,
      invalidEmailMessage: "이메일을 입력하고 중복을 확인하세요.",
      validEmailMessage: "",
      companyName: null,
      userStatusOptions: [],

      roleOptions: []
    }
  },

  methods: {
    onInputEmail: function() {
      this.isNotEmailDuplicatedBool = false
      this.checkedEmailDuplicatedBool = false
      this.invalidEmailInputForm()
    },
    invalidEmailInputForm: function() {
      this.$v.form.email.$touch()
      if(!this.$v.form.email.email || !this.$v.form.email.required || !this.$v.form.email.maxLength) {
        // 정상 이메일이 아닌 경우
        this.invalidEmailMessage = "이메일을 입력하고 중복을 확인하세요."
        this.validEmailMessage = ""
      } else if(this.$v.form.email.email && this.$v.form.email.required && this.$v.form.email.maxLength 
                && !this.$v.form.email.checkedEmailDuplicated ) {
        // 정상 이메일 입력 및 중복확인 이전
        this.invalidEmailMessage = "이메일 중복확인을 하세요."
        this.validEmailMessage = ""
      } else if(this.$v.form.email.email && this.$v.form.email.required && this.$v.form.email.maxLength
                && this.$v.form.email.checkedEmailDuplicated && !this.$v.form.email.isNotEmailDuplicated ) {
        // 정상 이메일 입력 및 중복된 이메일인 경우
        this.invalidEmailMessage = "이미 가입된 이메일입니다."
        this.validEmailMessage = ""
      } else {
        // 정상 이메일 입력 및 중복되지 않은 이메일인경우
        this.invalidEmailMessage = ""
        this.validEmailMessage = "사용가능한 이메일입니다."
      }
    },
    checkDupEmail: function() {
      if (this.$v.form.email.email && this.$v.form.email.required && this.$v.form.email.maxLength) {
        // 정상 이메일인 경우만 실행
        let that = this
        this.$store.dispatch('checkEmailDup', {
          params: {
            email: that.form.email
          },
          callback: data => {
            that.checkedEmailDuplicatedBool = true
            if(data.status == 1000){
              that.isNotEmailDuplicatedBool = true
              that.invalidEmailInputForm()
            } else {
              that.isNotEmailDuplicatedBool = false
              that.invalidEmailInputForm()
            }
          }
        })
      } else {
        // 정상 이메일이 아닌 경우
        this.invalidEmailMessage = "비정상적인 이메일 형식입니다."
        this.validEmailMessage = ""
      }
    },
    onSubmit: function() {
      this.$v.form.$touch()
      if (!this.$v.form.$invalid) {
        let that = this
        this.form.hpNo = cf.mergeTel(this.form.hpNoFirst, this.form.hpNoSecond, this.form.hpNoThird)  // TODO
        this.$store.dispatch('saveUser', {
          data: this.form,
          callback: user => {
            that.user = user
            this.$router.push({ name: 'userList' })
          }
        })
      }
    },
    onCancel: function(){
      this.$router.push({ name: 'userList' })
    },
  },

  created() {
    if(this.currentUser.role == 'ADMIN') {
      this.showSelectPartner = true
    } else if (this.currentUser.role == 'PARTNER_MECHANIC' && this.currentUser.partner ) {
      this.showSelectPartner = false
      this.form.partnerNo = this.currentUser.partnerNo
      this.companyName = this.currentUser.partner.companyName
    } else {
      console.error("error on UserDetail during created()")
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

    if (this.pageContents != undefined && this.pageContents != null) {
      this.isModifyUser = true
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
        userStatus: this.pageContents.userStatus, 
        role: this.pageContents.role
      }
    }
    console.log('created user/userDetail')
  },

  mounted() {
    let that = this
    this.$store.dispatch('loadUserEnums', {
      callback: userEnums => {
        that.userStatusOptions = userEnums.userStatusEnums
        that.roleOptions = userEnums.roleEnums
        if(!that.isModifyUser) {
          that.form.userStatus = 'MEMBER'
        }
      }
    })
    if (this.pageContents != undefined && this.pageContents != null) {
      this.isNotEmailDuplicatedBool = true
      this.checkedEmailDuplicatedBool = true
    }
  },

  validations: {
    form: {
      email: {
        required,
        email,
        isNotEmailDuplicated,
        checkedEmailDuplicated,
        maxLength: maxLength(45)
      },
      password: {
        passwdRequired,
        alphaNumSpecial,
        minLength: minLength(6),
        maxLength: maxLength(20)
      },
      passwordRe: {
        passwdRequired,
        alphaNumSpecial,
        minLength: minLength(6),
        maxLength: maxLength(20),        
        sameAsPassword: sameAs('password')
      },
      name: {
        required,
        name,
        minLength: minLength(2),
        maxLength: maxLength(20),        
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
      userStatus: {
        required
      },
    }
  }
}
</script>
<style>
.valid-message {
  width: 100%;
  margin-top: 0.25rem;
  font-size: 65%;
  color: #34a853;
}
.invalid-email {
  display: none;
}
.valid-email {
  display: block;
}
.tel-first { width: 25% }
.tel-second { width: 35% }
.tel-third { width: 35% }
.partner-input .popover {
  max-width: 600px;
}
</style>
