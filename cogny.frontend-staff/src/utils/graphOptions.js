const  selectSensorOptions = [
  { value: 'odometer', text: 'odo미터', hasRange: false, decimalPlace: 1, min: 0, max: 0, colorSet: ['']},
  { value: 'batteryVolt', text: '배터리 전압', hasRange: true, decimalPlace: 2, min: 10, max: 15, colorSet: ['#F44336']},
  { value: 'ignitionVolt', text: '이그니션 전압(IG1)', hasRange: true, decimalPlace: 2, min: 10, max: 16, colorSet: ['#673AB7']},
  { value: 'engineRpm', text: '엔진회전수', hasRange: true, decimalPlace: 0, min: 0, max: 5100, colorSet: ['#03A9F4']},
  { value: 'mapPa', text: '흡기압 센서(전압)', hasRange: true, decimalPlace: 2, min: 0, max: 4.5, colorSet: ['#4CAF50']},
  { value: 'coolantTemp', text: '냉각수온센서', hasRange: true, decimalPlace: 0, min: -48, max: 110, colorSet: ['#FF5722']},
  { value: 'oxyVoltS1', text: '산소센서전압(B1/S1)', hasRange: true, decimalPlace: 2, min: 0, max: 1.2, colorSet: ['#E91E63']},
  { value: 'oxyVoltS1Lin', text: '산소센서전압(B1/S1)-linear', hasRange: true, decimalPlace: 2, min: 0, max: 2.3, colorSet: ['#3F51B5']},
  { value: 'oxyVoltS2', text: '산소센서전압(B1/S2)', hasRange: true, decimalPlace: 2, min: 0, max: 1.2, colorSet: ['#00BCD4']},
  { value: 'vehicleSpd', text: '차량속도', hasRange: true, decimalPlace: 0, min: 0, max: 200, colorSet: ['#8BC34A']},
  { value: 'injectionTimeC1', text: '연료분사 시간-실린더 1', hasRange: true, decimalPlace: 2, min: 0, max: 20, colorSet: ['#FFC107']},
  { value: 'injectionTimeC2', text: '연료분사 시간-실린더 2', hasRange: true, decimalPlace: 2, min: 0, max: 20, colorSet: ['#795548']},
  { value: 'injectionTimeC3', text: '연료분사 시간-실린더 3', hasRange: true, decimalPlace: 2, min: 0, max: 20, colorSet: ['#9C27B0']},
  { value: 'injectionTimeC4', text: '연료분사 시간-실린더 4', hasRange: true, decimalPlace: 2, min: 0, max: 20, colorSet: ['#2196F3']},
  { value: 'ignitionSwitch', text: '이그니션 스위치', hasRange: true, decimalPlace: 0, min: -0.1, max: 1.1, colorSet: ['#009688']},
  { value: 'brakeLampSwitch', text: '브레이크등 스위치', hasRange: true, decimalPlace: 0, min: -0.4, max: 4.4, colorSet: ['#CDDC39']},
  { value: 'afRatioCtrlActivation', text: '공연비 제어 활성화', hasRange: true, decimalPlace: 0, min: -0.8, max: 8.8, colorSet: ['#FF9800']},
  { value: 'acCompressorOn', text: '에어컨컴프레서 ON', hasRange: true, decimalPlace: 0, min: -0.4, max: 4.4, colorSet: ['#607D8B']},
  { value: 'cmpCkpSync', text: 'CMP/CKP 동기화', hasRange: true, decimalPlace: 0, min: -0.8, max: 8.8, colorSet: ['#D32F2F']},
  { value: 'coolingFanRelayHs', text: '쿨링팬 릴레이(고속)', hasRange: false, decimalPlace: 0, min: 0, max: 0, colorSet: ['#512DA8']},
  { value: 'knockSensing', text: '노크 감지(B1/S1)', hasRange: true, decimalPlace: 0, min: -0.1, max: 1.1, colorSet: ['#0288D1']},
  { value: 'batterySensorCurrent', text: '배터리센서 전류(AMS)', hasRange: false, decimalPlace: 2, min: 0, max: 0, colorSet: ['#388E3C']},
  { value: 'batterySensorVolt', text: '배터리센서 전압(AMS)', hasRange: true, decimalPlace: 2, min: 6, max: 15, colorSet: ['#FBC02D']},
  { value: 'alternatorTargetVolt', text: '발전기 목표전압(듀티)', hasRange: true, decimalPlace: 2, min: 30, max: 80, colorSet: ['#E64A19']},
  { value: 'timerIgOn', text: '타이머(IG ON 이후)', hasRange: false, decimalPlace: 0, min: 0, max: 0, colorSet: ['#C2185B']},
  { value: 'torqueCvtTubSpd', text: '토크컨버터터빈스피드', hasRange: true, decimalPlace: 0, min: 0, max: 5000, colorSet: ['#303F9F']},
  { value: 'acPaVolt', text: '에어컨 압력 센서 전압', hasRange: true, decimalPlace: 2, min: 1.2, max: 3.2, colorSet: ['#0097A7']},
  { value: 'requiredAfRatio', text: '요구 공연비', hasRange: true, decimalPlace: 2, min: 0.8, max: 1.1, colorSet: ['#689F38']},
  { value: 'actualAfRatio', text: '실제 공연비', hasRange: true, decimalPlace: 2, min: 0, max: 16, colorSet: ['#FFA000']},
  { value: 'igTimingC1', text: '점화시기-실린더 1', hasRange: true, decimalPlace: 0, min: -100, max: 100, colorSet: ['#5D4037']},
  { value: 'igFailureCntC1', text: '실화 발생 횟수-CYL 1', hasRange: false, decimalPlace: 0, min: 0, max: 0, colorSet: ['#7B1FA2']},
  { value: 'igFailureCntC2', text: '실화 발생 횟수-CYL 2', hasRange: false, decimalPlace: 0, min: 0, max: 0, colorSet: ['#1976D2']},
  { value: 'igFailureCntC3', text: '실화 발생 횟수-CYL 3', hasRange: false, decimalPlace: 0, min: 0, max: 0, colorSet: ['#00796B']},
  { value: 'igFailureCntC4', text: '실화 발생 횟수-CYL 4', hasRange: false, decimalPlace: 0, min: 0, max: 0, colorSet: ['#AFB42B']},
  { value: 'igFailureCntCatalystC1', text: '실화발생횟수(촉매)CYL 1', hasRange: false, decimalPlace: 0, min: 0, max: 0, colorSet: ['#F57C00']},
  { value: 'igFailureCntCatalystC2', text: '실화발생횟수(촉매)CYL 2', hasRange: false, decimalPlace: 0, min: 0, max: 0, colorSet: ['#455A64']},
  { value: 'igFailureCntCatalystC3', text: '실화발생횟수(촉매)CYL 3', hasRange: false, decimalPlace: 0, min: 0, max: 0, colorSet: ['#E57373']},
  { value: 'igFailureCntCatalystC4', text: '실화발생횟수(촉매)CYL 4', hasRange: false, decimalPlace: 0, min: 0, max: 0, colorSet: ['#9575CD']},
  { value: 'tpsVolt1', text: '스로틀포지션센서-1전압', hasRange: true, decimalPlace: 2, min: 0, max: 5, colorSet: ['#4FC3F7']},
  { value: 'tpsVolt2', text: '스로틀포지션센서-2전압', hasRange: true, decimalPlace: 2, min: 0, max: 5, colorSet: ['#81C784']},
  { value: 'accelPedalVolt1', text: '엑셀 페달 센서 1 전압', hasRange: true, decimalPlace: 2, min: 0, max: 5, colorSet: ['#FF8A65']},
  { value: 'accelPedalVolt2', text: '엑셀 페달 센서 2 전압', hasRange: true, decimalPlace: 2, min: 0, max: 0.1, colorSet: ['#F06292']},
  { value: 'etcMotorDuty', text: 'ETC 모터 듀티', hasRange: true, decimalPlace: 2, min: -100, max: 100, colorSet: ['#7986CB']},
  { value: 'lpgFuelRailPa', text: 'LPG 연료 레일압력', hasRange: true, decimalPlace: 0, min: 550, max: 1200, colorSet: ['#4DD0E1']},
  { value: 'lpgFuelRailPaVolt', text: 'LPG 연료 레일압력-전압', hasRange: true, decimalPlace: 2, min: 1.5, max: 4, colorSet: ['#AED581']},
  { value: 'fuelPumpCtrl', text: '연료펌프제어-PWM신호', hasRange: true, decimalPlace: 2, min: 0, max: 100, colorSet: ['#FFD54F']},
  { value: 'fuelPumpFault', text: '연료펌프 고장진단', hasRange: true, decimalPlace: 2, min: 4.5, max: 9, colorSet: ['#A1887F']},
  { value: 'butaneRatio', text: '연료내 부탄함량비율 계산', hasRange: true, decimalPlace: 2, min: 60, max: 100, colorSet: ['#BA68C8']},
  { value: 'tirePressure1', text: '타이어 압력1 (TPMS)', hasRange: true, decimalPlace: 0, min: 15, max: 50, colorSet: ['#64B5F6']},
  { value: 'tirePressure2', text: '타이어 압력2 (TPMS)', hasRange: true, decimalPlace: 0, min: 15, max: 50, colorSet: ['#4DB6AC']},
  { value: 'tirePressure3', text: '타이어 압력3 (TPMS)', hasRange: true, decimalPlace: 0, min: 15, max: 50, colorSet: ['#FFB74D']},
  { value: 'tirePressure4', text: '타이어 압력4 (TPMS)', hasRange: true, decimalPlace: 0, min: 15, max: 50, colorSet: ['#90A4AE']},
  { value: 'manifoldAirTemp', text: '흡기온도', hasRange: true, decimalPlace: 2, min: -50, max: 150, colorSet: ['#F44336']},
  { value: 'engineOilTemp', text: '엔진오일 온도', hasRange: true, decimalPlace: 2, min: -50, max: 200, colorSet: ['#673AB7']},
]

const graphsColorSet = [
  ['#E57373', '#9575CD', '#4FC3F7', '#81C784', '#FF8A65', '#F06292', '#7986CB', '#4DD0E1', '#AED581', '#FFD54F', '#A1887F', '#BA68C8', '#64B5F6', '#4DB6AC', '#FFB74D', '#90A4AE'],  
  ['#F44336'],	['#673AB7'],	['#03A9F4'],	['#4CAF50'],	['#FF5722'],	['#E91E63'],	['#3F51B5'],	['#00BCD4'],	['#8BC34A'],	['#FFC107'],	['#795548'],	['#9C27B0'],	['#2196F3'],	['#009688'],	['#CDDC39'],	['#FF9800'],	['#607D8B'],
  ['#D32F2F'],	['#512DA8'],	['#0288D1'],	['#388E3C'],	['#FBC02D'],	['#E64A19'],	['#C2185B'],	['#303F9F'],	['#0097A7'],	['#689F38'],	['#FFA000'],	['#5D4037'],	['#7B1FA2'],	['#1976D2'],	['#00796B'],	['#AFB42B'],	['#F57C00'],	['#455A64'],
  ['#E57373'],	['#9575CD'],	['#4FC3F7'],	['#81C784'],	['#FF8A65'],	['#F06292'],	['#7986CB'],	['#4DD0E1'],	['#AED581'],	['#FFD54F'],	['#A1887F'],	['#BA68C8'],	['#64B5F6'],	['#4DB6AC'],	['#FFB74D'],	['#90A4AE'],
]

const getValueRange = function(sensorName) {
  let valueRange = null
  selectSensorOptions.forEach(function(selectSensorOption) {
    if(sensorName === selectSensorOption.value && selectSensorOption.hasRange) {
      valueRange = [selectSensorOption.min, selectSensorOption.max]
    }
  })
  return valueRange
}

const getColorSet = function(sensorName) {
  let colorSet = []
  selectSensorOptions.forEach(function(selectSensorOption) {
    if(sensorName === selectSensorOption.value) {
      colorSet = selectSensorOption.colorSet
    }
  })
  return colorSet
}

const getSensorField = function(sensorName) {
  let sensorField = null
  selectSensorOptions.forEach(function(selectSensorOption) {
    if(sensorName === selectSensorOption.value) {
      sensorField = selectSensorOption
    }
  })
  return sensorField
}

export default{
  selectSensorOptions,
  graphsColorSet,
  getValueRange,
  getColorSet,
  getSensorField
}