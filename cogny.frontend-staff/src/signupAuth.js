import 'es6-promise/auto'
import Vue from 'vue'
import Vuex from 'vuex'
import BootstrapVue from 'bootstrap-vue'

import Auth from './components/signup/Auth.vue'

import store from './store/signup'

import './assets/css/app.css'
import './assets/css/cognyApp.css'
import './assets/lib/material-design-icons/css/material-design-iconic-font.min.css'

Vue.config.productionTip = false
Vue.use(BootstrapVue)
Vue.use(Vuex)

new Vue({
  render: h => h(Auth),
  store,
}).$mount('#app')
