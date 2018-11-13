package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(of = { "sensingRawNo", "vehicleNo", "obdDeviceNo", "driveHistoryNo", "sensingSeq" })
@JsonInclude(Include.NON_NULL)
public class SensingRaw implements Serializable {
    private static final long serialVersionUID = -3409085350736382442L;

    private Long sensingRawNo;
    private Long vehicleNo;
    private Long obdDeviceNo;
    private Long driveHistoryNo;

    private long sensingSeq;
    private Date sensingTime;
    private String time;

    private Integer odometer;
    private Integer batteryVolt;
    private Integer ignitionVolt;
    private Integer engineRpm;
    private Integer mapPa;
    private Integer coolantTemp;
    private Integer oxyVoltS2;
    private Integer oxyVoltS1;
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
    private Integer oxyVoltS1Lin;

    private Date regDate;
    private Date updDate;

    private static final FastDateFormat DF = FastDateFormat.getInstance("yy-MM-dd HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());

    public SensingRaw(Long vehicleNo, Long obdDeviceNo, Long driveHistoryNo, String values) {
        this.vehicleNo = vehicleNo;
        this.obdDeviceNo = obdDeviceNo;
        this.driveHistoryNo = driveHistoryNo;

        String[] temp = StringUtils.commaDelimitedListToStringArray(values);
        sensingSeq = Integer.parseInt(temp[0]);
        time = temp[1];

        try {
            sensingTime = DF.parse(time);
        } catch (ParseException e) {
            sensingTime = new Date();
        }

        batteryVolt = convert(temp[2]);
        ignitionVolt = convert(temp[3]);
        engineRpm = convert(temp[4]);
        mapPa = convert(temp[5]);
        coolantTemp = convert(temp[6]);
        oxyVoltS2 = convert(temp[7]);
        oxyVoltS1 = convert(temp[8]);
        vehicleSpd = convert(temp[9]);

        injectionTimeC1 = convert(temp[10]);
        injectionTimeC2 = convert(temp[11]);
        injectionTimeC3 = convert(temp[12]);
        injectionTimeC4 = convert(temp[13]);
        ignitionSwitch = convert(temp[14]);
        brakeLampSwitch = convert(temp[15]);
        afRatioCtrlActivation = convert(temp[16]);
        acCompressorOn = convert(temp[17]);
        cmpCkpSync = convert(temp[18]);
        coolingFanRelayHs = convert(temp[19]);

        knockSensing = convert(temp[20]);
        batterySensorCurrent = convert(temp[21]);
        batterySensorVolt = convert(temp[22]);
        alternatorTargetVolt = convert(temp[23]);
        timerIgOn = convert(temp[24]);
        torqueCvtTubSpd = convert(temp[25]);
        acPaVolt = convert(temp[26]);
        requiredAfRatio = convert(temp[27]);
        actualAfRatio = convert(temp[28]);

        igTimingC1 = convert(temp[29]);
        igFailureCntC1 = convert(temp[30]);
        igFailureCntC2 = convert(temp[31]);
        igFailureCntC3 = convert(temp[32]);
        igFailureCntC4 = convert(temp[33]);
        igFailureCntCatalystC1 = convert(temp[34]);
        igFailureCntCatalystC2 = convert(temp[35]);
        igFailureCntCatalystC3 = convert(temp[36]);
        igFailureCntCatalystC4 = convert(temp[37]);

        tpsVolt1 = convert(temp[38]);
        tpsVolt2 = convert(temp[39]);
        accelPedalVolt1 = convert(temp[40]);
        accelPedalVolt2 = convert(temp[41]);
        etcMotorDuty = convert(temp[42]);
        lpgFuelRailPa = convert(temp[43]);
        lpgFuelRailPaVolt = convert(temp[44]);
        fuelPumpCtrl = convert(temp[45]);
        fuelPumpFault = convert(temp[46]);
        butaneRatio = convert(temp[47]);

        tirePressure1 = convert(temp[48]);
        tirePressure2 = convert(temp[49]);
        tirePressure3 = convert(temp[50]);
        tirePressure4 = convert(temp[51]);
        manifoldAirTemp = convert(temp[52]);
        engineOilTemp = convert(temp[53]);
        odometer = convert(temp[54]);
        if (temp.length > 55) {
            oxyVoltS1Lin = convert(temp[55]);
        }
    }

    private Integer convert(String value) {
        return value == null || "null".equals(value) ? null : Integer.parseInt(value);
    }

    public String toValues() {
        StringBuilder sb = new StringBuilder();
        sb.append(sensingSeq).append(",");
        sb.append(odometer == null ? "" : odometer).append(",");
        sb.append(batteryVolt == null ? "" : batteryVolt).append(",");
        sb.append(ignitionVolt == null ? "" : ignitionVolt).append(",");
        sb.append(engineRpm == null ? "" : engineRpm).append(",");
        sb.append(mapPa == null ? "" : mapPa).append(",");
        sb.append(coolantTemp == null ? "" : coolantTemp).append(",");
        sb.append(oxyVoltS1 == null ? "" : oxyVoltS1).append(",");
        sb.append(oxyVoltS1Lin == null ? "" : oxyVoltS1Lin).append(",");
        sb.append(oxyVoltS2 == null ? "" : oxyVoltS2).append(",");
        sb.append(vehicleSpd == null ? "" : vehicleSpd).append(",");
        sb.append(injectionTimeC1 == null ? "" : injectionTimeC1).append(",");
        sb.append(injectionTimeC2 == null ? "" : injectionTimeC2).append(",");
        sb.append(injectionTimeC3 == null ? "" : injectionTimeC3).append(",");
        sb.append(injectionTimeC4 == null ? "" : injectionTimeC4).append(",");
        sb.append(ignitionSwitch == null ? "" : ignitionSwitch).append(",");
        sb.append(brakeLampSwitch == null ? "" : brakeLampSwitch).append(",");
        sb.append(afRatioCtrlActivation == null ? "" : afRatioCtrlActivation).append(",");
        sb.append(acCompressorOn == null ? "" : acCompressorOn).append(",");
        sb.append(cmpCkpSync == null ? "" : cmpCkpSync).append(",");
        sb.append(coolingFanRelayHs == null ? "" : coolingFanRelayHs).append(",");
        sb.append(knockSensing == null ? "" : knockSensing).append(",");
        sb.append(batterySensorCurrent == null ? "" : batterySensorCurrent).append(",");
        sb.append(batterySensorVolt == null ? "" : batterySensorVolt).append(",");
        sb.append(alternatorTargetVolt == null ? "" : alternatorTargetVolt).append(",");
        sb.append(timerIgOn == null ? "" : timerIgOn).append(",");
        sb.append(torqueCvtTubSpd == null ? "" : torqueCvtTubSpd).append(",");
        sb.append(acPaVolt == null ? "" : acPaVolt).append(",");
        sb.append(requiredAfRatio == null ? "" : requiredAfRatio).append(",");
        sb.append(actualAfRatio == null ? "" : actualAfRatio).append(",");
        sb.append(igTimingC1 == null ? "" : igTimingC1).append(",");
        sb.append(igFailureCntC1 == null ? "" : igFailureCntC1).append(",");
        sb.append(igFailureCntC2 == null ? "" : igFailureCntC2).append(",");
        sb.append(igFailureCntC3 == null ? "" : igFailureCntC3).append(",");
        sb.append(igFailureCntC4 == null ? "" : igFailureCntC4).append(",");
        sb.append(igFailureCntCatalystC1 == null ? "" : igFailureCntCatalystC1).append(",");
        sb.append(igFailureCntCatalystC2 == null ? "" : igFailureCntCatalystC2).append(",");
        sb.append(igFailureCntCatalystC3 == null ? "" : igFailureCntCatalystC3).append(",");
        sb.append(igFailureCntCatalystC4 == null ? "" : igFailureCntCatalystC4).append(",");
        sb.append(tpsVolt1 == null ? "" : tpsVolt1).append(",");
        sb.append(tpsVolt2 == null ? "" : tpsVolt2).append(",");
        sb.append(accelPedalVolt1 == null ? "" : accelPedalVolt1).append(",");
        sb.append(accelPedalVolt2 == null ? "" : accelPedalVolt2).append(",");
        sb.append(etcMotorDuty == null ? "" : etcMotorDuty).append(",");
        sb.append(lpgFuelRailPa == null ? "" : lpgFuelRailPa).append(",");
        sb.append(lpgFuelRailPaVolt == null ? "" : lpgFuelRailPaVolt).append(",");
        sb.append(fuelPumpCtrl == null ? "" : fuelPumpCtrl).append(",");
        sb.append(fuelPumpFault == null ? "" : fuelPumpFault).append(",");
        sb.append(butaneRatio == null ? "" : butaneRatio).append(",");
        sb.append(tirePressure1 == null ? "" : tirePressure1).append(",");
        sb.append(tirePressure2 == null ? "" : tirePressure2).append(",");
        sb.append(tirePressure3 == null ? "" : tirePressure3).append(",");
        sb.append(tirePressure4 == null ? "" : tirePressure4).append(",");
        sb.append(manifoldAirTemp == null ? "" : manifoldAirTemp).append(",");
        sb.append(engineOilTemp == null ? "" : engineOilTemp);

        return sb.toString();
    }

    public static final String KEY_NAME = "name";
    public static final String KEY_OBD_DEVICE_NO = "obd_device_no";
    public static final String KEY_DRIVE_HISTORY_NO = "drive_history_no";
    public static final String KEY_SENSING_SEQ = "sensing_seq";
    public static final String KEY_SENSING_TIME = "sensing_time";
    public static final String KEY_REG_DATE = "reg_date";
    public static final String KEY_VALUES = "values";
}
