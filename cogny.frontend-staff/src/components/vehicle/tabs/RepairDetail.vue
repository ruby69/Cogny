<template>
  <form>
    <b-container class="border p-2">
      <b-row class="m-2">
        <b-col cols="3">
          <div class="form-group row">
            <i class="material-icons">access_time</i>
            <label for="inputRepairDate" class="col-12 col-sm-3 col-form-label p-0 px-2">정비일자</label>
            <div class="col-11 pl-0 pr-4" ref="repairDatePicker">
              <datepicker v-model="form.repairDate" 
                          :language="language" 
                          :format="datePickerFormat"
                          :disabledDates="disabledDates" 
                          >
              </datepicker>
            </div>
          </div>
        </b-col>
        <b-col cols="3">
          <div class="form-group row">
            <i class="material-icons">sort</i>
            <label for="inputOdometer" class="col-3 col-sm-3 col-form-label p-0 px-2">주행거리</label>
            <div class="col-10 px-0 pr-2">
              <input id="inputOdometer" type="text" 
                     class="form-control form-control-sm"
                     v-model="odometerFormatted" 
                     :class="{ 'is-invalid': $v.form.odometer.$error }"
                     @input="onInputOdometer()">
            </div>
            <div class="col-2 px-0 text-left"><label for="inputOdometer" class="col-3 col-sm-3 col-form-label text-sm-left px-0">km</label></div>
          </div>
        </b-col>
        <b-col cols="6">
          <div class="form-group row">
            <i class="material-icons">create</i>
            <label for="inputRepairMemo" class="col-2 col-form-label p-0 px-2">메모</label>
            <div class="col-12 pr-0">
              <textarea id="inputRepairMemo" class="form-control" v-model="form.memo"></textarea>
            </div>
          </div>
        </b-col>
      </b-row>
      <b-row class="border m-2 mt-6 pt-4">
        <b-col cols="4">
          <div class="form-group row">
            <label for="inputComponentCate1" class="col-sm-3 col-form-label text-sm-right">정비영역</label>
            <div class="col-sm-9">
              <b-form-select id="inputComponentCate1" class="select-sm" 
                             style="height: 37px;font-size: 1rem;padding: 4px 12px;"
                             :class="{ 'is-invalid': $v.selectedRepairComponent.componentCate1.$error }"
                             v-model="selectedRepairComponent.componentCate1" 
                             @input="onSelectComponentCate1()">
                <option :value="null">정비영역 선택</option>
                <option :value="null" disabled>------------------</option>
                <option v-for="componentCate1Item in repairDetail.componentCate1List" 
                        :key="componentCate1Item.componentCate1No" 
                        :value="componentCate1Item">
                  {{ componentCate1Item.name }}
                </option>
              </b-form-select>
            </div>
          </div>
        </b-col>
        <b-col cols="4">
          <div class="form-group row">
            <label for="inputComponentCate2" class="col-sm-3 col-form-label text-sm-right">부품계통</label>
            <div class="col-sm-9">
              <b-form-select id="inputComponentCate2" class="select-sm" 
                             style="height: 37px;font-size: 1rem;padding: 4px 12px;"
                             @input="onSelectComponentCate2()" 
                             v-model="selectedRepairComponent.componentCate2" 
                             :class="{ 'is-invalid': $v.selectedRepairComponent.componentCate2.$error }">
                <option :value="null">부품계통 선택</option>
                <option :value="null" disabled>------------------</option>
                <option v-for="componentCate2Item in repairDetail.componentCate2List" 
                        :key="componentCate2Item.componentCate2No" 
                        :value="componentCate2Item">
                  {{ componentCate2Item.name }}
                </option>
              </b-form-select>
            </div>
          </div>
        </b-col>        
        <b-col cols="4">
          <div class="form-group row">
            <label for="inputComponent" class="col-sm-3 col-form-label text-sm-right">부품</label>
            <div class="col-sm-9">
              <b-form-select id="inputComponent" class="select-sm" 
                             v-model="selectedRepairComponent.component" 
                             :class="{ 'is-invalid': $v.selectedRepairComponent.component.$error }" 
                             style="height: 37px;font-size: 1rem;padding: 4px 12px;">
                <option :value="null">부품 선택</option>
                <option :value="null" disabled>------------------</option>
                <option v-for="componentItem in repairDetail.componentList" 
                        :key="componentItem.componentCateNo" 
                        :value="componentItem">
                  {{ componentItem.name }}
                </option>
              </b-form-select>
            </div>
          </div>
        </b-col>
        <b-col cols="4">
          <div class="form-group row">
            <label for="inputRepairCategory" class="col-sm-3 col-form-label text-sm-right">정비내역</label>
            <div class="col-sm-9">
              <b-form-select id="inputRepairCategory" class="select-sm" style="height: 37px;font-size: 1rem;padding: 4px 12px;"
                             v-model="selectedRepairComponent.category" 
                             :class="{ 'is-invalid': $v.selectedRepairComponent.category.$error }">
                <option :value="null">정비내역 선택</option>
                <option :value="null" disabled>------------------</option>
                <option v-for="categoryItem in repairDetail.repairCategoryList" 
                        :key="categoryItem.value" 
                        :value="categoryItem">
                  {{ categoryItem.text }}
                </option>
              </b-form-select>
            </div>
          </div>
        </b-col>        
        <b-col cols="4">
          <div class="form-group row">
            <label for="inputRepairCost" class="col-sm-3 col-form-label text-sm-right">비용</label>
            <div class="col-sm-9">
              <input id="inputRepairCost" type="text" 
                     class="form-control form-control-sm" 
                     v-model="selectedRepairComponent.cost.formatted" 
                     :class="{ 'is-invalid': $v.selectedRepairComponent.cost.value.$error }"
                     @input="onInputCost()">
            </div>
          </div>
        </b-col>
        <b-col cols="4">
          <div class="form-group row">
            <label for="inputRepairComponentMemo" class="col-sm-3 col-form-label text-sm-right">부품정비 메모</label>
            <div class="col-sm-9">
              <textarea id="inputRepairComponentMemo" class="form-control" 
                        v-model="selectedRepairComponent.memo.value" 
                        @input="onInputMemo()">
              </textarea>
            </div>
          </div>
        </b-col>
        <b-col cols="12">
          <b-row class="text-center my-2">
            <b-col cols="12">
              <button type="button" class="btn btn-space btn-primary" @click="onAddRepairComponent()">
                <i class="icon icon-right mdi mdi-long-arrow-down"></i> 
                {{ addRepairBtnText }}
              </button>
            </b-col>
          </b-row>
        </b-col>
        <b-col cols="12">
          <b-table class="mb-0 border-top" striped hover :items="repairItem.repairComponentList" :fields="repairFields">
            <template slot="manager" slot-scope="data">
              <button type="button" class="btn btn-secondary" @click="onEditRepairComponent(data.index)"><i class="icon mdi mdi-edit"/></button>
              <button type="button" class="btn btn-secondary" @click="onDeleteRepairComponent(data.index)"><i class="icon mdi mdi-delete"/></button>
            </template>
          </b-table>
          <div class="text-center py-5" :class="{ 'd-none': !showRepairComponentGuideMsg, 'text-danger': $v.repairComponentCount.$error }">
            입력된 정비내역이 없습니다.
          </div>
        </b-col>
      </b-row>
    </b-container>
    <b-container class="mt-3 px-0">
      <b-row class="text-right">
        <b-col cols="12">
          <button type="button" class="btn btn-space btn-primary btn-lg" @click="onSubmit()">정비 내역 저장</button>
          <button type="button" class="btn btn-space btn-secondary btn-lg mr-0" @click="onCancel()">취소</button>
        </b-col>
      </b-row>
    </b-container>
  </form>
</template>
<script>
import { mapGetters } from 'vuex'
import Datepicker from 'vuejs-datepicker'
import { ko } from 'vuejs-datepicker/dist/locale'
import { validationMixin } from 'vuelidate'
import { eventBus } from '../../common/EventBus.vue'
import { required, integer, numeric, minValue, maxValue } from 'vuelidate/lib/validators'
import cf from '../../../utils/customFilters'

export default {
  name: 'RepairDetail',
  components: {
    Datepicker,
  },
  mixins: [validationMixin],
  props: {
    vehicle: null,
    repairItem: null,
    isMaximized: false
  },
  data () {
    return {
      language: ko,
      
      datePickerFormat: 'yyyy-MM-dd',
      
      disabledDates: {
        from: new Date(),
      },
      odometerFormatted: null,
      selectedRepairComponent: {
        componentCate1: null,
        componentCate2: null,
        component: null,
        category: null,
        cost: {
          formatted: null,
          value: null
        },
        memo: {
          formatted: null,
          value: null
        },
      },
      repairComponent4Edit: null,
      deletedRepairComponentList: [],
      showRepairComponentGuideMsg: true,
      addRepairBtnText : "부품정비내역 추가",
      repairFields: [
        { key: 'componentCate1.name', label: '정비영역', class: 'text-center' },
        { key: 'componentCate2.name', label: '부품계통', class: 'text-center' },
        { key: 'component.name', label: '부품', class: 'text-center' },
        { key: 'category.text', label: '정비내역', class: 'text-center' },
        { key: 'cost.formatted', label: '비용', class: 'text-center' },
        { key: 'memo.formatted', label: '부품정비 메모', class: 'text-center' },
        { key: 'manager', label: '수정/삭제', class: 'text-center' }
      ],
      repairComponentCount: 0,
      form: {
        repairNo: null,
        repairDate: new Date(),
        odometer: null,
        vehicleNo: this.vehicle.vehicleNo,
        memo: null,
        repairComponentList: []
      }
    }
  },
  computed: {
    ...mapGetters({
      repairDetail: 'getRepairDetail'
    }),
  },
  methods: {
    onInputOdometer: function() {
      console.log("this.odometerFormatted : " + this.odometerFormatted)
      if(!this.odometerFormatted || this.odometerFormatted == null) {
        this.odometerFormatted = null
        this.form.odometer = null
        return
      }
      this.odometerFormatted = cf.formatNumber(this.odometerFormatted)
      this.form.odometer = this.odometerFormatted.replace(/,/gi,"") * 1      
      this.$v.form.odometer.$touch()
    },
    onSelectComponentCate1: function() {
      if(!this.selectedRepairComponent.componentCate1) return
      // 기존 리스트 초기화
      this.repairDetail.componentCate2List = []
      this.repairDetail.componentList = []
      this.selectedRepairComponent.componentCate2 = null
      this.selectedRepairComponent.component = null
      // parameter 설정
      let params = {
        componentCate1No: this.selectedRepairComponent.componentCate1.componentCate1No
      }
      let that = this
      this.$store.dispatch('loadComponentCate2', {
        params: params,
        callback: () => {
          if(that.repairComponent4Edit) {
            that.selectedRepairComponent.componentCate2 = that.repairComponent4Edit.componentCate2
          }
        }
      })
    },
    onSelectComponentCate2: function() {
      if(!this.selectedRepairComponent.componentCate2) return
      // 기존 리스트 초기화
      this.repairDetail.componentList = []
      this.selectedRepairComponent.component = null
      // parameter 설정   
      let params = {
        manufacturerNo: this.vehicle.model.modelGroup.manufacturer.manufacturerNo,
        componentCate1No: this.selectedRepairComponent.componentCate1.componentCate1No,
        componentCate2No: this.selectedRepairComponent.componentCate2.componentCate2No
      }
      let that = this
      this.$store.dispatch('loadComponentList', {
        params: params,
        callback: () => {
          if(that.repairComponent4Edit) {
            that.selectedRepairComponent.component = that.repairComponent4Edit.component
          }
        }
      })
    },
    onInputCost: function() {
      if(!this.selectedRepairComponent.cost) {
        this.selectedRepairComponent.cost = {
          value: null,
          formatted: null
        }
        return
      }

      this.selectedRepairComponent.cost.formatted = this.selectedRepairComponent.cost.formatted.replace("\\ ", "").length == 0 ? "" : "\\ " + cf.formatNumber(this.selectedRepairComponent.cost.formatted.replace("\\ ", ""))
      this.selectedRepairComponent.cost.value = this.selectedRepairComponent.cost.formatted.replace("\\ ", "").replace(/,/gi,"") * 1
      this.$v.selectedRepairComponent.cost.value.$touch()
    },
    onInputMemo: function() {
      if(!this.selectedRepairComponent.memo) {
        this.selectedRepairComponent.memo = {
          value: null,
          formatted: null
        }
        return
      }
      this.selectedRepairComponent.memo.formatted = cf.shortenText(this.selectedRepairComponent.memo.value)
    },
    onAddRepairComponent: function() {
      if(this.$v.selectedRepairComponent.$invalid) {
        this.$v.selectedRepairComponent.$touch()
        return
      }

      if(this.isInputComponentRepair) {
        this.isInputComponentRepair = false
        this.addRepairBtnText = "부품정비내역 추가"
      } else {
        this.repairItem.repairComponentList.push(this.selectedRepairComponent)
      }
      this.initializaRepairComponent()
    },
    onEditRepairComponent: function(index) {
      this.isInputComponentRepair = true
      this.addRepairBtnText = "부품정비내역 수정 완료"
      this.selectedRepairComponent = this.repairItem.repairComponentList[index]
      this.repairComponent4Edit = {
          componentCate2 : this.repairItem.repairComponentList[index].componentCate2,
          component: this.repairItem.repairComponentList[index].component,
        }
      this.selectedRepairComponent.componentCate1 = this.repairItem.repairComponentList[index].componentCate1
      this.selectedRepairComponent.category = this.repairItem.repairComponentList[index].category
      this.selectedRepairComponent.cost = this.repairItem.repairComponentList[index].cost
      this.selectedRepairComponent.memo = this.repairItem.repairComponentList[index].memo
    },
    onDeleteRepairComponent: function(index) {
      if(this.repairItem.repairComponentList[index].repairComponentNo){
        this.deletedRepairComponentList.push(this.repairItem.repairComponentList[index])
      }
      this.repairItem.repairComponentList.splice(index, 1)
      this.initializaRepairComponent()
    },
    onSubmit: function() {
      if(this.$v.form.$invalid || this.$v.repairComponentCount.$invalid) {
        this.$v.form.$touch()
        this.$v.repairComponentCount.$touch()
        return
      }
      this.form.repairComponentList = []

      let that = this
      this.repairItem.repairComponentList.forEach(function(repairComponent) {
        that.form.repairComponentList.push({
          repairComponentNo: repairComponent.repairComponentNo,
          componentCateNo: repairComponent.component.componentCateNo,
          category: repairComponent.category.value,
          cost: repairComponent.cost.value,
          memo: repairComponent.memo.value,
        })
      })
      this.deletedRepairComponentList.forEach(function(repairComponent) {
        that.form.repairComponentList.push({
          repairComponentNo: repairComponent.repairComponentNo,
          componentCateNo: repairComponent.component.componentCateNo,
          category: repairComponent.category.value,
          cost: repairComponent.cost.value,
          memo: repairComponent.memo.value,
          enabled: false
        })
      })

      this.$store.dispatch('saveRepair', {
        data: this.form,
        callback: repair => {
          eventBus.$emit('onCloseRepairDetail')
        }
      })

    }, 
    onCancel: function() {
      eventBus.$emit('onCloseRepairDetail')
    },
    initializaRepairComponent: function() {
      this.selectedRepairComponent = {
        componentCate1: this.selectedRepairComponent.componentCate1,
        componentCate2: this.selectedRepairComponent.componentCate2,
        component: null,
        category: null,
        cost: {
          formatted: null,
          value: null
        },
        memo: {
          formatted: null,
          value: null
        },        
      }
      this.repairComponentCount = this.repairItem.repairComponentList.length
      this.$v.repairComponentCount.$reset()
      this.showRepairComponentGuideMsg = this.repairItem.repairComponentList.length > 0 ? false: true
      this.$v.selectedRepairComponent.$reset()
      this.repairComponent4Edit = null
    }
  },
  created() {
    console.log('created RepairDetail')
    this.$store.dispatch('loadComponentCate1')
    this.$store.dispatch('loadRepairEnums')

    if(this.repairItem.repairNo != undefined && this.repairItem.repairNo != null) {
      this.form.repairNo = this.repairItem.repairNo
      this.form.repairDate = this.repairItem.repairDate
      this.odometerFormatted = this.repairItem.odometer
      this.onInputOdometer()
      this.form.memo = this.repairItem.memo
      this.initializaRepairComponent()
    } else {
      this.odometerFormatted = parseInt(this.vehicle.currentMileage / 10)
      this.onInputOdometer()
    }
  },
  mounted() {
    this.$refs.repairDatePicker.getElementsByTagName('input')[0].classList.add('form-control')
    this.$refs.repairDatePicker.getElementsByTagName('input')[0].classList.add('form-control-sm')
    this.$refs.repairDatePicker.getElementsByTagName('input')[0].setAttribute("id", "inputRepairDate")
  },
  validations: {
    selectedRepairComponent: {
      componentCate1: {
        required
      },
      componentCate2: {
        required
      },
      component: {
        required
      },
      category: {
        required
      },
      cost: {
        value: {
          integer,
          maxvalue: maxValue(2147483647)
        }
      },
    },
    form: {
      repairDate: {
        required
      },
      odometer: {
        required,
        integer,
        maxValue: maxValue(16777215)
      }
    },
    repairComponentCount: {
      integer,
      minValue: minValue(1)
    }    
  },
}
</script>
<style>
.select-sm {
  height: 37px;
  font-size: 1rem;
  padding: 4px 12px;
}
</style>