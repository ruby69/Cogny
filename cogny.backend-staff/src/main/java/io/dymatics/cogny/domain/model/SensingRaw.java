package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@ToString
@JsonInclude(Include.NON_NULL)
public class SensingRaw implements Serializable {
    private static final long serialVersionUID = -7952108874911700909L;

    private Long sensingRawNo;
    private Long vehicleNo;
    private Long obdDeviceNo;
    private Long driveHistoryNo;
    private Date sensingTime;
    
    private Integer sensingSeq;
    private Integer odometer;
    private Integer batteryVolt;
    private Integer ignitionVolt;
    private Integer engineRpm;
    private Integer mapPa;
    private Integer coolantTemp;
    private Integer oxyVoltS1;
    private Integer oxyVoltS1Lin;
    private Integer oxyVoltS2;
    private Integer vehicleSpd;
    private Integer injectionTimeC1;
    private Integer injectionTimeC2;
    private Integer injectionTimeC3;
    private Integer injectionTimeC4;
    private Integer ignitionSwitch;
    private Integer brakeLampSwitch;
    private Integer afRatioCtrlActivation;
    private Integer acCompressorOn;
    private Integer cmpCkpSync;
    private Integer coolingFanRelayHs;
    private Integer knockSensing;
    private Integer batterySensorCurrent;
    private Integer batterySensorVolt;
    private Integer alternatorTargetVolt;
    private Integer timerIgOn;
    private Integer torqueCvtTubSpd;
    private Integer acPaVolt;
    private Integer requiredAfRatio;
    private Integer actualAfRatio;
    private Integer igTimingC1;
    private Integer igFailureCntC1;
    private Integer igFailureCntC2;
    private Integer igFailureCntC3;
    private Integer igFailureCntC4;
    private Integer igFailureCntCatalystC1;
    private Integer igFailureCntCatalystC2;
    private Integer igFailureCntCatalystC3;
    private Integer igFailureCntCatalystC4;
    private Integer tpsVolt1;
    private Integer tpsVolt2;
    private Integer accelPedalVolt1;
    private Integer accelPedalVolt2;
    private Integer etcMotorDuty;
    private Integer lpgFuelRailPa;
    private Integer lpgFuelRailPaVolt;
    private Integer fuelPumpCtrl;
    private Integer fuelPumpFault;
    private Integer butaneRatio;
    private Integer tirePressure1;
    private Integer tirePressure2;
    private Integer tirePressure3;
    private Integer tirePressure4;
    private Integer manifoldAirTemp;
    private Integer engineOilTemp;
    
    private Date regDate;
    
    private static final FastDateFormat KEY_STAMP= FastDateFormat.getInstance("yyyyMMddHHmmss", TimeZone.getDefault(), Locale.getDefault());
    
    public SensingRaw(Entity sensingRawEntity) {
        try {
//            log.info("key: " + sensingRawEntity.getKey().getName());
            // key 값 가져오기
            String keyName = sensingRawEntity.getKey().getName();
            String[] keyStrings = keyName.split("-");
            if(keyStrings.length != 4) {
                throw new Exception("invalid keyName in the sensing_raw entity :" + sensingRawEntity);
            }
            
            vehicleNo = StringUtils.isEmpty(keyStrings[0]) ? null : Long.valueOf(keyStrings[0]);
            driveHistoryNo = StringUtils.isEmpty(keyStrings[1]) ? null : Long.valueOf(keyStrings[1]);
            sensingTime = StringUtils.isEmpty(keyStrings[2]) ? null : KEY_STAMP.parse(keyStrings[2]);
            sensingSeq = StringUtils.isEmpty(keyStrings[3]) ? null : Integer.valueOf(keyStrings[3]);
            
            
            // sensor 값 가져오기
            Value<String> sensingValues = sensingRawEntity.getValue("values");
            String sensingRawEntityValues = sensingValues.get();
            
            String[] sensorStrings = sensingRawEntityValues.split(",");
            if(sensorStrings.length < 55) {
                throw new Exception("invalid values in the sensing_raw entity: " + sensingRawEntity);
            }
            sensingSeq              = this.parseInteger(sensorStrings[0]);
            odometer                = this.parseInteger(sensorStrings[1]);
            batteryVolt             = this.parseInteger(sensorStrings[2]);
            ignitionVolt            = this.parseInteger(sensorStrings[3]);
            engineRpm               = this.parseInteger(sensorStrings[4]);
            mapPa                   = this.parseInteger(sensorStrings[5]);
            coolantTemp             = this.parseInteger(sensorStrings[6]);
            oxyVoltS1               = this.parseInteger(sensorStrings[7]);
            oxyVoltS1Lin            = this.parseInteger(sensorStrings[8]);
            oxyVoltS2               = this.parseInteger(sensorStrings[9]);
            vehicleSpd              = this.parseInteger(sensorStrings[10]);
            injectionTimeC1         = this.parseInteger(sensorStrings[11]);
            injectionTimeC2         = this.parseInteger(sensorStrings[12]);
            injectionTimeC3         = this.parseInteger(sensorStrings[13]);
            injectionTimeC4         = this.parseInteger(sensorStrings[14]);
            ignitionSwitch          = this.parseInteger(sensorStrings[15]);
            brakeLampSwitch         = this.parseInteger(sensorStrings[16]);
            afRatioCtrlActivation   = this.parseInteger(sensorStrings[17]);
            acCompressorOn          = this.parseInteger(sensorStrings[18]);
            cmpCkpSync              = this.parseInteger(sensorStrings[19]);
            coolingFanRelayHs       = this.parseInteger(sensorStrings[20]);
            knockSensing            = this.parseInteger(sensorStrings[21]);
            batterySensorCurrent    = this.parseInteger(sensorStrings[22]);
            batterySensorVolt       = this.parseInteger(sensorStrings[23]);
            alternatorTargetVolt    = this.parseInteger(sensorStrings[24]);
            timerIgOn               = this.parseInteger(sensorStrings[25]);
            torqueCvtTubSpd         = this.parseInteger(sensorStrings[26]);
            acPaVolt                = this.parseInteger(sensorStrings[27]);
            requiredAfRatio         = this.parseInteger(sensorStrings[28]);
            actualAfRatio           = this.parseInteger(sensorStrings[29]);
            igTimingC1              = this.parseInteger(sensorStrings[30]);
            igFailureCntC1          = this.parseInteger(sensorStrings[31]);
            igFailureCntC2          = this.parseInteger(sensorStrings[32]);
            igFailureCntC3          = this.parseInteger(sensorStrings[33]);
            igFailureCntC4          = this.parseInteger(sensorStrings[34]);
            igFailureCntCatalystC1  = this.parseInteger(sensorStrings[35]);
            igFailureCntCatalystC2  = this.parseInteger(sensorStrings[36]);
            igFailureCntCatalystC3  = this.parseInteger(sensorStrings[37]);
            igFailureCntCatalystC4  = this.parseInteger(sensorStrings[38]);
            tpsVolt1                = this.parseInteger(sensorStrings[39]);
            tpsVolt2                = this.parseInteger(sensorStrings[40]);
            accelPedalVolt1         = this.parseInteger(sensorStrings[41]);
            accelPedalVolt2         = this.parseInteger(sensorStrings[42]);
            etcMotorDuty            = this.parseInteger(sensorStrings[43]);
            lpgFuelRailPa           = this.parseInteger(sensorStrings[44]);
            lpgFuelRailPaVolt       = this.parseInteger(sensorStrings[45]);
            fuelPumpCtrl            = this.parseInteger(sensorStrings[46]);
            fuelPumpFault           = this.parseInteger(sensorStrings[47]);
            butaneRatio             = this.parseInteger(sensorStrings[48]);
            tirePressure1           = this.parseInteger(sensorStrings[49]);
            tirePressure2           = this.parseInteger(sensorStrings[50]);
            tirePressure3           = this.parseInteger(sensorStrings[51]);
            tirePressure4           = this.parseInteger(sensorStrings[52]);
            manifoldAirTemp         = this.parseInteger(sensorStrings[53]);
            engineOilTemp           = this.parseInteger(sensorStrings[54]);
            
        } catch(Exception e) {
            log.error("error occured on parsing sensing_raw: " + e);
            return;
        }
    }
    
    private Integer parseInteger(String value) {
        return (StringUtils.isEmpty(value) || "null".equals(value)) ? null : Integer.valueOf(value);
        
    }

    @JsonIgnore
    public ArrayList<String> getValidColumns() throws IllegalArgumentException, IllegalAccessException {
        ArrayList<String> columns = new ArrayList<>();
        for (Field field : SensingRaw.class.getDeclaredFields()) {
            if (field.get(this) != null) {
                columns.add(field.getName());
            }
        }
        return columns;
    }
    
    @JsonIgnore
    public String toValues() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.parseString(this.sensingSeq)).append(",");
        sb.append(this.parseString(this.odometer)).append(",");
        sb.append(this.parseString(this.batteryVolt)).append(",");
        sb.append(this.parseString(this.ignitionVolt)).append(",");
        sb.append(this.parseString(this.engineRpm)).append(",");
        sb.append(this.parseString(this.mapPa)).append(",");
        sb.append(this.parseString(this.coolantTemp)).append(",");
        sb.append(this.parseString(this.oxyVoltS1)).append(",");
        sb.append(this.parseString(this.oxyVoltS1Lin)).append(",");
        sb.append(this.parseString(this.oxyVoltS2)).append(",");
        sb.append(this.parseString(this.vehicleSpd)).append(",");
        sb.append(this.parseString(this.injectionTimeC1)).append(",");
        sb.append(this.parseString(this.injectionTimeC2)).append(",");
        sb.append(this.parseString(this.injectionTimeC3)).append(",");
        sb.append(this.parseString(this.injectionTimeC4)).append(",");
        sb.append(this.parseString(this.ignitionSwitch)).append(",");
        sb.append(this.parseString(this.brakeLampSwitch)).append(",");
        sb.append(this.parseString(this.afRatioCtrlActivation)).append(",");
        sb.append(this.parseString(this.acCompressorOn)).append(",");
        sb.append(this.parseString(this.cmpCkpSync)).append(",");
        sb.append(this.parseString(this.coolingFanRelayHs)).append(",");
        sb.append(this.parseString(this.knockSensing)).append(",");
        sb.append(this.parseString(this.batterySensorCurrent)).append(",");
        sb.append(this.parseString(this.batterySensorVolt)).append(",");
        sb.append(this.parseString(this.alternatorTargetVolt)).append(",");
        sb.append(this.parseString(this.timerIgOn)).append(",");
        sb.append(this.parseString(this.torqueCvtTubSpd)).append(",");
        sb.append(this.parseString(this.acPaVolt)).append(",");
        sb.append(this.parseString(this.requiredAfRatio)).append(",");
        sb.append(this.parseString(this.actualAfRatio)).append(",");
        sb.append(this.parseString(this.igTimingC1)).append(",");
        sb.append(this.parseString(this.igFailureCntC1)).append(",");
        sb.append(this.parseString(this.igFailureCntC2)).append(",");
        sb.append(this.parseString(this.igFailureCntC3)).append(",");
        sb.append(this.parseString(this.igFailureCntC4)).append(",");
        sb.append(this.parseString(this.igFailureCntCatalystC1)).append(",");
        sb.append(this.parseString(this.igFailureCntCatalystC2)).append(",");
        sb.append(this.parseString(this.igFailureCntCatalystC3)).append(",");
        sb.append(this.parseString(this.igFailureCntCatalystC4)).append(",");
        sb.append(this.parseString(this.tpsVolt1)).append(",");
        sb.append(this.parseString(this.tpsVolt2)).append(",");
        sb.append(this.parseString(this.accelPedalVolt1)).append(",");
        sb.append(this.parseString(this.accelPedalVolt2)).append(",");
        sb.append(this.parseString(this.etcMotorDuty)).append(",");
        sb.append(this.parseString(this.lpgFuelRailPa)).append(",");
        sb.append(this.parseString(this.lpgFuelRailPaVolt)).append(",");
        sb.append(this.parseString(this.fuelPumpCtrl)).append(",");
        sb.append(this.parseString(this.fuelPumpFault)).append(",");
        sb.append(this.parseString(this.butaneRatio)).append(",");
        sb.append(this.parseString(this.tirePressure1)).append(",");
        sb.append(this.parseString(this.tirePressure2)).append(",");
        sb.append(this.parseString(this.tirePressure3)).append(",");
        sb.append(this.parseString(this.tirePressure4)).append(",");
        sb.append(this.parseString(this.manifoldAirTemp)).append(",");
        sb.append(this.parseString(this.engineOilTemp));
        return sb.toString();
    }
    private String parseString(Integer value) {
        return value == null ? "" : String.valueOf(value);
    }
    
    public static final String KEY_NAME = "name";
//    public static final String KEY_OBD_DEVICE_NO = "obd_device_no";
    public static final String KEY_DRIVE_HISTORY_NO = "drive_history_no";
    public static final String KEY_SENSING_SEQ = "sensing_seq";
    public static final String KEY_SENSING_TIME = "sensing_time";
    public static final String KEY_REG_DATE = "reg_date";
    public static final String KEY_VALUES = "values";

    @Data
    @NoArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class SendingRawDataSet implements Serializable {
        private static final long serialVersionUID = 2649798772567210394L;

        private ArrayList<String> fields;
        private ArrayList<ArrayList<Object>> sensingData;
        private List<DtcRaw> dtcRawList;
        private Page page;

        public SendingRawDataSet(ArrayList<String> fieldList, List<SensingRaw> sensingRawList, List<DtcRaw> dtcRawList, Page page) throws Exception {
            this.fields = fieldList;
            this.sensingData = new ArrayList<ArrayList<Object>>();
            if (sensingRawList.size() == 0)
                return;
            for (SensingRaw sensingRaw : sensingRawList) {
                ArrayList<Object> dataRow = new ArrayList<Object>();
                for (String field: fieldList) {
                    Field fi = SensingRaw.class.getDeclaredField(field);
                    fi.setAccessible(true);
                    dataRow.add(fi.get(sensingRaw));
                }
                this.sensingData.add(dataRow);
            }
            this.dtcRawList = dtcRawList;
            this.page = page;
        }
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(Include.NON_NULL)
    public static class Form implements Serializable {
        private static final long serialVersionUID = -747593586828859297L;

        private Long vehicleNo;
        private Long driveHistoryNo;
        private ArrayList<String> fieldList;
        private ArrayList<String> columnList;
        private Page page;
        
        public void addDefaultFields() {
            if(this.fieldList.indexOf("sensingRawNo") > -1 )this.fieldList.remove(this.fieldList.indexOf("sensingRawNo"));
            if(this.fieldList.indexOf("sensingTime") > -1) this.fieldList.remove(this.fieldList.indexOf("sensingTime"));
            this.fieldList.add(0, "sensingRawNo");
            this.fieldList.add(1, "sensingTime");
            
        }
        public void setColomnList() {
            this.columnList = new ArrayList<String>();
            for(String field: this.fieldList) {
                this.columnList.add(field.replaceAll("([A-Z])", "_$1").toLowerCase());
            }
        }
    }
}
