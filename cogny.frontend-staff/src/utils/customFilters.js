import moment from 'moment'

export default {
  convertTel: function(telNo) {
    if(telNo) {
      return telNo.replace(/(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})/,"$1-$2-$3").split('-')
    } else {
      return [telNo, '', '']
    }
  },
  formatNumber: function(value) {
    if (value) {
      let parts = value.toString().split(".")
      parts[0] = parts[0].replace(/,/gi,"").replace(/\B(?=(\d{3})+(?!\d))/g, ",")
      return parts.join(".").length == 0 ? null : parts.join(".")
    } else {
      return null
    }
  },
  formatTel: function(value) {
    if (value) {
      return value.replace(/(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})/,"$1-$2-$3")
    } else {
      return "-"
    }
  },
  mergeTel: function(tel1, tel2, tel3) {
    if(tel1 && tel2 && tel3) {
      return tel1.concat(tel2).concat(tel3)
    } else {
      return null
    }
  },
  formatDate: function(value) {
    if (value) {
      return moment(value).format('YYYY-MM-DD')
    } else {
      return '-'
    }
  },
  formatTime: function(value) {
    if (value) {
      return moment(value).format('HH:mm:ss')
    } else {
      return '-'
    }
  },  
  formatDateTime: function(value) {
    if (value) {
      return moment(value).format('YYYY-MM-DD HH:mm:ss')
    } else {
      return '-'
    }
  },
  isValidName: function(name) {
    let nameReg = /^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|\s|\*]+$/
    if(nameReg.test(name.trim())) {
      return true
    } else {
      return false
    }
  },  
  isValidHpNo: function(hpNo) {
    let regExp = /^01([0|1|6|7|8|9]?)-?([0-9]{3,4})-?([0-9]{4})$/;
    if(regExp.test(hpNo.trim().replace(/[^0-9]/g,""))){
      return true
    } else {
      return false
    }
  },
  getTimeDiff: function(startDate, endDate) {
    if(!startDate || !endDate || !(startDate instanceof Date) || !(endDate instanceof Date) ) return null
    let rtn = ""
    let startMoment = moment(startDate)
    let endMoment = moment(endDate)
    if(moment.duration(endMoment.diff(startMoment)).years() > 0) rtn += " " + moment.duration(endMoment.diff(startMoment)).years() + "년"
    if(moment.duration(endMoment.diff(startMoment)).months() > 0) rtn += " " + moment.duration(endMoment.diff(startMoment)).months() + "월"
    if(moment.duration(endMoment.diff(startMoment)).days() > 0) rtn += " " + moment.duration(endMoment.diff(startMoment)).days() + "일"
    if(moment.duration(endMoment.diff(startMoment)).hours() > 0) rtn += " " + moment.duration(endMoment.diff(startMoment)).hours() + "시간"
    if(moment.duration(endMoment.diff(startMoment)).minutes() > 0) rtn += " " + moment.duration(endMoment.diff(startMoment)).minutes() + "분"
    if(moment.duration(endMoment.diff(startMoment)).seconds() > 0) rtn += " " + moment.duration(endMoment.diff(startMoment)).seconds() + "초"
    return rtn
  },
  shortenText: function(value) {
    if(value && value.toString().length > 13) {
      return value.toString().substring(0, 10) + "..."
    } else {
      return value
    }
  }
}