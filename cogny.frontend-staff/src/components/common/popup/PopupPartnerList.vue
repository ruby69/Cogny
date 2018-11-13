<template>
  <div class="col-sm-12">
    <div class="card card-table non-shadow">
      <div class="card-header">
        <b-input-group size="sm">
          <b-form-input type="text" 
                        v-model.trim="partnersPage.query" 
                        @keydown.native="keydownHandler" 
                        placeholder="제휴사를 검색하세요."/>
          <b-input-group-append>
            <b-btn variant="primary" @click="search">제휴사 검색</b-btn>
          </b-input-group-append>   
        </b-input-group>                
      </div>
      <div class="card-body detail-list-card-body">
        <table class="table">
          <thead>
            <tr>
              <th class="text-center" scope="col">회사명</th>
              <th class="text-center" scope="col">담당자</th>
              <th class="text-center" scope="col">계약상태</th>
              <th class="text-center" scope="col">선택</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="partner in partnersPage.contents" :key="partner.partnerNo">
              <td class="align-middle text-center">{{ partner.companyName }}</td>
              <td class="align-middle text-center">{{ partner.personInCharge }}</td>
              <td class="align-middle text-center">{{ partner.contractStatusName }}</td>
              <td class="align-middle text-center">
                <b-btn variant="secondary" :size="'sm'" @click="onSelect(partner)">선택</b-btn>
              </td>
            </tr>
          </tbody>
        </table>
        <b-pagination align="center" size="sm" 
                    :total-rows="partnersPage.total" 
                    v-model="partnersPage.page" 
                    :per-page="partnersPage.scale" 
                    @input="movePage" />
      </div>
      <div class="text-right">
        <b-btn variant="secondary" :size="'sm'" @click="onClose()">취소</b-btn> 
      </div>      
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { eventBus } from '../EventBus.vue'
export default {
  name: 'PopupPartnerList',

  computed: mapGetters({
    partnersPage: 'getPartnersPage'
  }),
  data: function() {
    return {
      params: {
        page: 1,
        scale: 15,
        contractStatus: 'CONTRACTED',
        query: null,
      },
    }
  },

  methods: {
    search: function(){
      this.params.query = this.partnersPage.query
      this.params.scale = this.partnersPage.scale
      this.params.page = 1      
      this.$store.dispatch('loadPartnersPage', this.params)
    },
    onSelect: function(partner){
      eventBus.$emit('onSelectPopupPartner', partner)
    },
    movePage: function(){
      this.params.page = this.partnersPage.page
      this.$store.dispatch('loadPartnersPage', this.params)
    },
    onClose: function() {
      eventBus.$emit('onClosePopupPartnerList')
    },    
    keydownHandler(event) {
      if (event.which === 13) {
        this.search()
      }
    },
    isContracted(partner) {
      if(partner != null && partner.contractStatus != null && partner.contractStatus == 'CONTRACTED') {
        return true
      } else {
        return false
      }
    }
  },

  created() {
    this.params.scale = this.partnersPage.scale
    this.$store.dispatch('loadPartnersPage', this.params)
  },
}
</script>
<style>
.detail-list-card-body {
  min-width: 500px;
  min-height: 300px;
}
</style>
