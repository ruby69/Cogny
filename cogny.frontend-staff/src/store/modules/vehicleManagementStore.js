import cogny from '../../api/vehicleManagemetApi'
import graphOptions from '../../utils/graphOptions'
import cf from '../../utils/customFilters'

const state = {
  driveMgtPage: {
    startDates: [],
    disabledMaxDate: null,
    disabledMinDate: null,
    defaultStartDate: null,
    disabledDates: [],

    driveHistoryIndexes: [],
    driveHistoryDetail: null,
    sensingRawGraphs: [{
      data: [],
      fields: []
    }],
    sensingRawGraphsOnlySpeedZero: [{
      data: [],
      fields: []
    }],
    dtcs: [],
    page: {
      hasNext:false,
      hasPrevious:false,
      page: 1,
      scale: 1800,
      total: 0,
      totalPages: 0,      
    }
  },
}

const getters = {
  getDriveMgtPage: state => state.driveMgtPage,
}

const actions = {
  // 차량 센서데이터 조회 탭의 actions(SensorGraph.vue)
  loadDriveHistoryStartDates({ commit }, options) {
    cogny.fetchDriveHistoryStartDates(driveHistoryStartDates => {
      commit('setDriveHistoryStartDates', driveHistoryStartDates)
      if(options.callback) options.callback()
    }, options.vehicleNo)
  },
  loadDriveHistotyIndexes({ commit }, options) {
    cogny.fetchDriveHistoryIndexes(driveHistoryIndexes => {
      commit('setDriveHistoryIndexes', driveHistoryIndexes)
      if(options.callback) options.callback()
    }, { params: options.params })
  },
  loadDriveHistoryDetail({ commit }, options) {
    cogny.fetchDriveHistory(driveHistoryDetail => {
      commit('setDriveHistoryDetail', driveHistoryDetail)
      if(options.callback) options.callback()
    }, { params: options.params })
  },
  loadSensingRaw({ commit }, options) {
    cogny.fetchSensingRaw(sensingRaw => {
      commit('setSensingRaw', sensingRaw)
      if(options.callback) options.callback()
    }, options.params)
  },
  saveDriveHistoryMemo({ commit }, options) {
    cogny.postDriveHistoryMemo(driveHistoryMemo => {
      commit('setDriveHistoryMemo', driveHistoryMemo)
      if(options.callback) options.callback(driveHistoryMemo)
    }, options.data)
  }
}

const mutations = {
  // 차량 센서데이터 조회 탭의 mutations(SensorGraph.vue)
  setDriveHistoryStartDates(state, driveHistoryStartDates) {
    let startDates = []
    driveHistoryStartDates.forEach(function(driveHistoryStartDate){
      try {
        startDates.push(new Date(driveHistoryStartDate.startDate))
      }
      catch(error){
        console.error("Error on convert start date : " + error)
      }
    })
    let disabledMaxDate = getDisabledMaxStartDate(startDates)
    let disabledMinDate = getDisabledMinStartDate(startDates)

    state.driveMgtPage.startDates = startDates
    state.driveMgtPage.disabledMaxDate = disabledMaxDate
    state.driveMgtPage.disabledMinDate = disabledMinDate ? disabledMinDate : new Date(),
    state.driveMgtPage.defaultStartDate = disabledMinDate ? disabledMinDate : null,
    state.driveMgtPage.disabledDates = getDisabledDates(startDates, disabledMaxDate, disabledMinDate)

  },
  setDriveHistoryIndexes(state, driveHistoryIndexes) {
    state.driveMgtPage.driveHistoryIndexes = driveHistoryIndexes
  },
  setDriveHistoryDetail(state, driveHistoryDetail) {
    let startDateTest = new Date(driveHistoryDetail.startDate + " " + driveHistoryDetail.startTime)
    let endDateTest = new Date(driveHistoryDetail.endTime)
    driveHistoryDetail.driveDuration = cf.getTimeDiff(startDateTest, endDateTest)

    state.driveMgtPage.driveHistoryDetail = driveHistoryDetail
  },
  setSensingRaw(state, sensingRaw) {
    /****
     * sensingRaw 데이터를 dygraph 입력 형식으로 데이터 전환
     * 
     * example>
     * sensingRaw = {
     *  fields: ["sensingRawNo", "sensingTime", "engineRpm", "vehicleSpd"],
     *  sensingData: [
     *    [27460, "2018-05-25T05:33:07.000+0000", 1223, 0], 
     *    [27461, "2018-05-25T05:33:08.000+0000", 1126, 0],
     *    ......,
     *  dtcRawList: [
     *    {
     *      driveHistoryNo: 104,
     *      dtcCode: "P0270",
     *      dtcIssuedTime: 1532080232000,
     *      dtcRawNo: 8,
     *      dtcSeq: 2,
     *      dtcState: "04:00000100:pendingDTC,",
     *      dtcUpdatedTime: 1532080457000,
     *      obdDeviceNo: 20,
     *      regDate: 1532080461000,
     *      vehicleNo: 20
     *    },
     *  ......,
     *  ]
     * },
     * 
     * 
     * state.driveMgtPage.sensingRawGraphs = [
     *  { data : [
     *      [ "2018-05-25T05:33:07.000+0000", 1223 ],
     *      [ "2018-05-25T05:33:08.000+0000", 1126 ],
     *      ......
     *    ],
     *    fields : ['시각', '엔진회전수'],
     *    fieldNames : ['time', 'engineRpm'],
     *  },
     *  { data : [
     *      [ "2018-05-25T05:33:07.000+0000", 0 ],
     *      [ "2018-05-25T05:33:08.000+0000", 0 ],
     *      ......
     *    ],
     *    fields : ['시각', '차량속도'],
     *    fieldNames : ['time', 'engineRpm'],
     *  },
     *  ......
     * ]
     *****/
    state.driveMgtPage.page = sensingRaw.page   // pagination 속성 
    state.driveMgtPage.sensingRawGraphs = []    // 모든 속도의 데이터 셋
    state.driveMgtPage.sensingRawGraphsOnlySpeedZero = []   // 속도가 0 인 경우의 데이터 셋
    state.driveMgtPage.dtcs = getDtcDescList(sensingRaw.dtcRawList)

    let i
    let vehicleSpdIdx = getVehicleSpdIdx(sensingRaw.fields)
    let onlyZeroSpeedIdx = 0

    // 1.1 dtcRawList의 fields 정의
    let dtcFields = getDtcFieldList(sensingRaw.dtcRawList)
    state.driveMgtPage.sensingRawGraphs[0] = {
      data: [],
      fields: dtcFields
    }
    state.driveMgtPage.sensingRawGraphsOnlySpeedZero[0] = {
      data: [],
      fields: dtcFields
    }
        
    // 1.2 dtcRawData의 정의
    let prevDtcCode = null
    let fieldsIdx = 0
    let dtcIdx = 0
    let dtcCount = sensingRaw.dtcRawList.length
    let sIdx = 0
    // sensingRaw 레코드 수
    let sCnt = sensingRaw.sensingData.length
    let startSIdx = 0
    let stateCode = null
    
    // 1.3 시각 설정
    for(sIdx = startSIdx; sIdx < sCnt; sIdx++) {
      state.driveMgtPage.sensingRawGraphs[0].data[sIdx] = [ new Date(sensingRaw.sensingData[sIdx][1]) ]
      if(vehicleSpdIdx && sensingRaw.sensingData[sIdx][vehicleSpdIdx] == 0) {
        state.driveMgtPage.sensingRawGraphsOnlySpeedZero[0].data[onlyZeroSpeedIdx] = [ onlyZeroSpeedIdx ]
        onlyZeroSpeedIdx += 1
      }
    }
    // 1.4 dtc 데이터 목록
    for(dtcIdx = 0; dtcIdx < dtcCount; dtcIdx++) {
      stateCode = parseInt(sensingRaw.dtcRawList[dtcIdx].dtcState.split(":")[1])
      if(prevDtcCode == null || prevDtcCode != sensingRaw.dtcRawList[dtcIdx].dtcCode) {
        startSIdx = 0
        onlyZeroSpeedIdx = 0
        fieldsIdx += 1
        prevDtcCode = sensingRaw.dtcRawList[dtcIdx].dtcCode
      } else if (prevDtcCode == sensingRaw.dtcRawList[dtcIdx].dtcCode && sensingRaw.sensingData[sCnt - 1][1] < sensingRaw.dtcRawList[dtcIdx].dtcIssuedTime) {
        // dtc가 조회 기간 중에 아직 발생하지 않은 경우 다음 dtc 조회 및 처리 단계로 넘어간다.
        continue
      }

      //1.5 dtc 데이터 입력
      for(sIdx = startSIdx; sIdx < sCnt; sIdx++) {
        // dtc 발생 시간 이전 데이터는 null 처리한다.
        if(sensingRaw.sensingData[sIdx][1] < sensingRaw.dtcRawList[dtcIdx].dtcIssuedTime) {
          state.driveMgtPage.sensingRawGraphs[0].data[sIdx][fieldsIdx] = null
          if(vehicleSpdIdx && sensingRaw.sensingData[sIdx][vehicleSpdIdx] == 0) {
            state.driveMgtPage.sensingRawGraphsOnlySpeedZero[0].data[onlyZeroSpeedIdx][fieldsIdx] = null
            onlyZeroSpeedIdx += 1
          }
        } else if(sensingRaw.sensingData[sIdx][1] <= sensingRaw.dtcRawList[dtcIdx].dtcUpdatedTime) {
          // dtc가 발생한 기간동안 dtc 코드의 state 값을 할당한다.
          state.driveMgtPage.sensingRawGraphs[0].data[sIdx][fieldsIdx] = stateCode
          if(vehicleSpdIdx && sensingRaw.sensingData[sIdx][vehicleSpdIdx] == 0) {
            state.driveMgtPage.sensingRawGraphsOnlySpeedZero[0].data[onlyZeroSpeedIdx][fieldsIdx] = stateCode
            onlyZeroSpeedIdx += 1
          }          
        } else {
          startSIdx = sIdx
          break
        }
      }
    }
    
    // state.driveMgtPage.sensingRawGraphs의 fields 정의
    for(i = 1; i < sensingRaw.fields.length - 1; i++) {
      state.driveMgtPage.sensingRawGraphs[i] = {
        data: [],
        fields: ['시각', graphOptions.getSensorField(sensingRaw.fields[i + 1]).text ],
        fieldNames: ['Time', graphOptions.getSensorField(sensingRaw.fields[i + 1]).value ]
      }
      // 속도가 0인경우의 필드명 정의
      if(vehicleSpdIdx){
        state.driveMgtPage.sensingRawGraphsOnlySpeedZero[i] = {
          data: [],
          fields: ['Time', graphOptions.getSensorField(sensingRaw.fields[i + 1]).text ],
          fieldNames: ['Time', graphOptions.getSensorField(sensingRaw.fields[i + 1]).value ]
        }
      }
    }

    // state.driveMgtPage.sensingRawGraphs의 data 정의
    onlyZeroSpeedIdx = 0
    sensingRaw.sensingData.forEach(function(sensing) {
      for(i = 1; i < sensingRaw.fields.length - 1; i++) {
        let row = [
          new Date(sensing[1]), 
          sensing[i + 1] / Math.pow(10, graphOptions.getSensorField(sensingRaw.fields[i + 1]).decimalPlace)
        ]
        state.driveMgtPage.sensingRawGraphs[i].data.push(row)
      }
      // 속도가 0인경우의 데이터셋 생성
      if(vehicleSpdIdx && sensing[vehicleSpdIdx] == 0) {
        for(i = 1; i < sensingRaw.fields.length - 1; i++) {
          let rowOnlyZero = [
            onlyZeroSpeedIdx, 
            sensing[i + 1] / Math.pow(10, graphOptions.getSensorField(sensingRaw.fields[i + 1]).decimalPlace)
          ]
          state.driveMgtPage.sensingRawGraphsOnlySpeedZero[i].data.push(rowOnlyZero)
        }
        onlyZeroSpeedIdx += 1
      }
    })
  },  
}

export default {
  state,
  getters,
  actions,
  mutations
}

function getDisabledMaxStartDate(startDates) {
  if(startDates.length == 0) return new Date()
  let minStartDate = null
  let disabledMaxDate = null
  try {
    startDates.forEach(function (startDate) {
      if (minStartDate == null || minStartDate > startDate) minStartDate = startDate
    })
    disabledMaxDate = new Date(minStartDate.getFullYear(), minStartDate.getMonth(), minStartDate.getDate())
  } catch (error) {
    console.error("Error on getDisabledMaxStartDate function : " + error)
    disabledMaxDate = new Date()
  }
  return disabledMaxDate
}

function getDisabledMinStartDate(startDates) {
  if(startDates.length == 0) return new Date()
  let maxStartDate = null
  let disabledMinDate = null
  try {
    startDates.forEach(function (startDate) {
      if (maxStartDate == null || maxStartDate < startDate) maxStartDate = startDate
    })
    disabledMinDate = new Date(maxStartDate.getFullYear(), maxStartDate.getMonth(), maxStartDate.getDate())
  } catch (error) {
    console.error("Error on getDisabledMinStartDate function : " + error)
  }
  return disabledMinDate
}

function getDisabledDates(startDates, minDate, maxDate) {
  if (startDates.length == 0) return []
  let currentDate = null
  let disabledDates = []
  let i = 0
  try{
    currentDate = new Date(minDate.getFullYear(), minDate.getMonth(), minDate.getDate())
    while (currentDate <= maxDate) {
      let disabledDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate())
      if(disabledDate.getFullYear() == startDates[i].getFullYear() 
        && disabledDate.getMonth() == startDates[i].getMonth() 
        && disabledDate.getDate() == startDates[i].getDate()) {
        i++
      } else {
        disabledDates.push(disabledDate)
      }
      currentDate.setDate(currentDate.getDate() + 1)
    }
  } catch(error) {
    console.error("Error on getDisabledDates function : " + error)
  }
  return disabledDates
}

function getVehicleSpdIdx(fields){
  let vehicleSpdIdx
  fields.forEach(function(field){
    if(field === 'vehicleSpd') {
      vehicleSpdIdx = fields.indexOf(field)
    }
  })
  return vehicleSpdIdx
}

function getDtcFieldList(dtcRawList) {
  let fields = ['시각']
  let prevDtcCode = null
  dtcRawList.forEach(function(dtcRaw) {
    if(prevDtcCode == null || prevDtcCode != dtcRaw.dtcCode) {
      fields.push(dtcRaw.dtcCode)
      prevDtcCode = dtcRaw.dtcCode
    }
  })
  return fields
}

function getDtcDescList(dtcRawList) {
  let dtcs = []
  let prevDtcCode = null
  dtcRawList.forEach(function(dtcRaw) {
    if(prevDtcCode == null || prevDtcCode != dtcRaw.dtcCode) {
      dtcs.push({dtcCode: dtcRaw.dtcCode, desc: dtcRaw.desc})
      prevDtcCode = dtcRaw.dtcCode
    }
  })
  return dtcs
}