<template>
  <div class="be-left-sidebar">
    <div class="left-sidebar-wrapper">
      <a href="#" class="left-sidebar-toggle" :class="{'open' : isOpen}" @click="onSideLeftMenu">{{opener}}</a>
      
      <div class="left-sidebar-spacer" :class="{'open' : isOpen}">
        <div class="left-sidebar-scroll">
          <div class="left-sidebar-content">
            <ul class="sidebar-elements">
              <li v-for="(menu, index) in menuList" :key="index" :class="{'active' : active === menu.activeName || (menu.activeNameList && menu.activeNameList.indexOf(active) > -1), 'parent' : menu.isParentsMenu, 'open': currentParentMenuName === menu.parentMenuName }">
              <!-- <li v-for="(menu, index) in menuList" :key="index" :class="{'active' : active === menu.activeName, 'parent' : menu.isParentsMenu, 'open': currentParentMenuName === menu.parentMenuName }"> -->
                <a v-if="menu.isParentsMenu" @click="onClickParents(menu.parentMenuName)" href="#">
                  <i class="icon mdi material-icons">{{ menu.icon }}</i>
                  <span>{{ menu.menuText }}</span>                  
                </a>
                <ul v-if="menu.isParentsMenu" class="sub-menu">
                  <li v-for="(subMenu, subIndex) in menu.subMenuList" :key="subIndex" :class="{'active': active === subMenu.activeName}">
                    <router-link :to="{ name: subMenu.routerName }"><span> {{ subMenu.menuText }} </span></router-link>
                  </li>
                </ul>
                <router-link v-else :to="{ name: menu.routerName }">
                  <i class="icon mdi material-icons">{{ menu.icon }}</i>
                  <span>{{ menu.menuText }}</span>
                </router-link>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SidebarLeft',

  props: ['active'],

  computed: {
    opener: function() {
      return this.isOpen ? 'Opened' : 'Closed'
    }
  },

  data: function() {
    return {
      isOpen: false,
      currentParentMenuName: null,
      menuList: [
        // { routerName: 'dashboard', activeName: 'dashboard', icon: 'dashboard', menuText: 'Dashboard' },
        { isParentsMenu: false, routerName: 'partnerList', activeName: 'partner', icon: 'location_city', menuText: '제휴사 관리' },
        { isParentsMenu: true, 
          activeNameList: ['user', 'invitation'], 
          parentMenuName: 'users',
          icon: 'person', 
          menuText: '사용자 관리', 
          subMenuList: [
            { routerName: 'userList', activeName: 'user', icon: 'person', menuText: '사용자 조회' },
            { routerName: 'invitationList', activeName: 'invitation', icon: 'person_add', menuText: '사용자 초대' },        
        ]},
        // { isParentsMenu: false, routerName: 'userList', activeName: 'user', icon: 'person', menuText: '사용자 관리' },
        // { isParentsMenu: false, routerName: 'invitationList', activeName: 'invitation', icon: 'person_add', menuText: '사용자 초대' },
        // {isParentsMenu: true, 
        //   activeNameList: ['vehicle', 'obd'], 
        //   parentMenuName: 'tests',
        //   icon: 'person', 
        //   menuText: '테스트 관리', 
        //   subMenuList: [
        //     { isParentsMenu: false, routerName: 'vehicleList', activeName: 'vehicle', icon: 'directions_car', menuText: '차량 관리' },
        //     { isParentsMenu: false, routerName: 'obdList', activeName: 'obd', icon: 'phonelink_setup', menuText: 'OBD 관리' },
        //   ]
        // },     
        { isParentsMenu: false, routerName: 'vehicleList', activeName: 'vehicle', icon: 'directions_car', menuText: '차량 관리' },
        { isParentsMenu: false, routerName: 'obdList', activeName: 'obd', icon: 'phonelink_setup', menuText: 'OBD 관리' },
        { isParentsMenu: false, routerName: 'dtcList', activeName: 'dtc', icon: 'build', menuText: '차량 DTC 조회' },
      ],
    }
  },

  methods: {
    onSideLeftMenu: function() {
      this.isOpen = !this.isOpen
    },
    onClickParents: function(parentMenuName) {
      if(this.currentParentMenuName == parentMenuName) {
        this.currentParentMenuName = null
      } else {
        this.currentParentMenuName = parentMenuName
      }
      return false
    }
  },

  created() {
    console.log('created common/SidebarLeft')

    let that = this
    let newMenuList = []
    this.menuList.forEach(function(menu) {
      let authentication = that.$router.resolve({ name: menu.routerName }).route.meta

      // 권한의 정의되지 않은 경우
      if(authentication.requiresAuth == undefined 
        || !Array.isArray(authentication.requiredPermissions)
        || authentication.requiredPermissions.length <= 0) return

      // 인증이 필요없는 페이지의 경우
      if(authentication.requiresAuth == false ) {
        newMenuList.push(menu)
      } else if (authentication.requiresAuth == true
        && authentication.requiredPermissions != undefined 
        && authentication.requiredPermissions.length != undefined
        && authentication.requiredPermissions.length >= 0
        && authentication.requiredPermissions.indexOf(sessionStorage.role) >= 0) {
          // 인증이 필요하나 합당한 권한을 가진경우
          newMenuList.push(menu)
        } 
    })
    this.menuList = newMenuList

    // 하위메뉴 열기
    this.menuList.forEach(function(menu) {
      if(menu.isParentsMenu) {
        menu.subMenuList.forEach(function(subMenu) {
          if(subMenu.activeName == that.active) {
            that.currentParentMenuName = menu.parentMenuName
          }
        })
      }
    })
  }
}
</script>