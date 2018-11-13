package io.dymatics.cogny.support.obd.message;

import lombok.Getter;

public class SensorMessage extends SequentialMessage{
    @Getter private Integer batteryVolt;            // 2
    @Getter private Integer ignitionVolt;           // 2
    @Getter private Integer engineRpm;              // 2
    @Getter private Integer mapPa;                  // 2
    @Getter private Integer coolantTemp;            // 2(1:signal, 1:value)
    @Getter private Integer oxyVoltS1;              // 2(1:signal, 1:value)
    @Getter private Integer oxyVoltS2;              // 2(1:signal, 1:value)
    @Getter private Integer vehicleSpd;             // 1

    @Getter private Integer injectionTimeC1;        // 2
    @Getter private Integer injectionTimeC2;        // 2
    @Getter private Integer injectionTimeC3;        // 2
    @Getter private Integer injectionTimeC4;        // 2
    @Getter private Integer ignitionSwitch;         // 1
    @Getter private Integer brakeLampSwitch;        // 1
    @Getter private Integer afRatioCtrlActivation;  // 1
    @Getter private Integer acCompressorOn;         // 1
    @Getter private Integer cmpCkpSync;             // 1
    @Getter private Integer coolingFanRelayHs;      // 1

    @Getter private Integer knockSensing;           // 1
    @Getter private Integer batterySensorCurrent;   // 2
    @Getter private Integer batterySensorVolt;      // 2
    @Getter private Integer alternatorTargetVolt;   // 2
    @Getter private Integer timerIgOn;              // 2
    @Getter private Integer torqueCvtTubSpd ;       // 2
    @Getter private Integer acPaVolt;               // 2
    @Getter private Integer requiredAfRatio;        // 2
    @Getter private Integer actualAfRatio;          // 2

    @Getter private Integer igTimingC1;             // 2(1:signal, 1:value)
    @Getter private Integer igFailureCntC1;         // 2
    @Getter private Integer igFailureCntC2;         // 2
    @Getter private Integer igFailureCntC3;         // 2
    @Getter private Integer igFailureCntC4;         // 2
    @Getter private Integer igFailureCntCatalystC1; // 2
    @Getter private Integer igFailureCntCatalystC2; // 2
    @Getter private Integer igFailureCntCatalystC3; // 2
    @Getter private Integer igFailureCntCatalystC4; // 2

    @Getter private Integer tpsVolt1;               // 2
    @Getter private Integer tpsVolt2;               // 2
    @Getter private Integer accelPedalVolt1;        // 2
    @Getter private Integer accelPedalVolt2;        // 2
    @Getter private Integer etcMotorDuty;           // 3(1:signal, 2:value)
    @Getter private Integer lpgFuelRailPa;          // 2
    @Getter private Integer lpgFuelRailPaVolt;      // 2
    @Getter private Integer fuelPumpCtrl;           // 2
    @Getter private Integer fuelPumpFault;          // 2
    @Getter private Integer butaneRatio;            // 2

    @Getter private Integer tirePressure1;          // 1
    @Getter private Integer tirePressure2;          // 1
    @Getter private Integer tirePressure3;          // 1
    @Getter private Integer tirePressure4;          // 1
    @Getter private Integer manifoldAirTemp;        // 3(1:signal, 2:value)
    @Getter private Integer engineOilTemp;          // 3(1:signal, 2:value)

    @Getter private Integer odometer;               // 3
    @Getter private Integer emsDtcCount;
    @Getter private Integer absDtcCount;
    @Getter private Integer oxyVoltS1Lin;           // 2

    @Override
    protected void doPopulate() {
        parseSequence();

        batteryVolt = convert(bytes.get(OFFSET + 1), bytes.get(OFFSET + 2));                // 2
        ignitionVolt = convert(bytes.get(OFFSET + 3), bytes.get(OFFSET + 4));               // 2
        engineRpm = convert(bytes.get(OFFSET + 5), bytes.get(OFFSET + 6));                  // 2
        mapPa = convert(bytes.get(OFFSET + 7), bytes.get(OFFSET + 8));                      // 2
        coolantTemp = convertSigned(bytes.get(OFFSET + 9), bytes.get(OFFSET + 10));         // 2(1:signal, 1:value)
        oxyVoltS1 = convertSigned(bytes.get(OFFSET + 11), bytes.get(OFFSET + 12));          // 2(1:signal, 1:value)
        oxyVoltS2 = convertSigned(bytes.get(OFFSET + 13), bytes.get(OFFSET + 14));          // 2(1:signal, 1:value)
        vehicleSpd = convert(bytes.get(OFFSET + 15));                                       // 1

        injectionTimeC1 = convert(bytes.get(OFFSET + 16), bytes.get(OFFSET + 17));          // 2
        injectionTimeC2 = convert(bytes.get(OFFSET + 18), bytes.get(OFFSET + 19));          // 2
        injectionTimeC3 = convert(bytes.get(OFFSET + 20), bytes.get(OFFSET + 21));          // 2
        injectionTimeC4 = convert(bytes.get(OFFSET + 22), bytes.get(OFFSET + 23));          // 2
        ignitionSwitch = convert(bytes.get(OFFSET + 24));                                   // 1
        brakeLampSwitch = convert(bytes.get(OFFSET + 25));                                  // 1
        afRatioCtrlActivation = convert(bytes.get(OFFSET + 26));                            // 1
        acCompressorOn = convert(bytes.get(OFFSET + 27));                                   // 1
        cmpCkpSync = convert(bytes.get(OFFSET + 28));                                       // 1
        coolingFanRelayHs = convert(bytes.get(OFFSET + 29));                                // 1

        knockSensing = convert(bytes.get(OFFSET + 30));                                     // 1
        batterySensorCurrent = convert(bytes.get(OFFSET + 31), bytes.get(OFFSET + 32));     // 2
        batterySensorVolt = convert(bytes.get(OFFSET + 33), bytes.get(OFFSET + 34));        // 2
        alternatorTargetVolt = convert(bytes.get(OFFSET + 35), bytes.get(OFFSET + 36));     // 2
        timerIgOn = convert(bytes.get(OFFSET + 37), bytes.get(OFFSET + 38));                // 2
        torqueCvtTubSpd = convert(bytes.get(OFFSET + 39), bytes.get(OFFSET + 40));          // 2
        acPaVolt = convert(bytes.get(OFFSET + 41), bytes.get(OFFSET + 42));                 // 2
        requiredAfRatio = convert(bytes.get(OFFSET + 43), bytes.get(OFFSET + 44));          // 2
        actualAfRatio = convert(bytes.get(OFFSET + 45), bytes.get(OFFSET + 46));            // 2

        igTimingC1 = convertSigned(bytes.get(OFFSET + 47), bytes.get(OFFSET + 48));         // 2(1:signal, 1:value)
        igFailureCntC1 = convert(bytes.get(OFFSET + 49), bytes.get(OFFSET + 50));           // 2
        igFailureCntC2 = convert(bytes.get(OFFSET + 51), bytes.get(OFFSET + 52));           // 2
        igFailureCntC3 = convert(bytes.get(OFFSET + 53), bytes.get(OFFSET + 54));           // 2
        igFailureCntC4 = convert(bytes.get(OFFSET + 55), bytes.get(OFFSET + 56));           // 2
        igFailureCntCatalystC1 = convert(bytes.get(OFFSET + 57), bytes.get(OFFSET + 58));   // 2
        igFailureCntCatalystC2 = convert(bytes.get(OFFSET + 59), bytes.get(OFFSET + 60));   // 2
        igFailureCntCatalystC3 = convert(bytes.get(OFFSET + 61), bytes.get(OFFSET + 62));   // 2
        igFailureCntCatalystC4 = convert(bytes.get(OFFSET + 63), bytes.get(OFFSET + 64));   // 2

        tpsVolt1 = convert(bytes.get(OFFSET + 65), bytes.get(OFFSET + 66));                 // 2
        tpsVolt2 = convert(bytes.get(OFFSET + 67), bytes.get(OFFSET + 68));                 // 2
        accelPedalVolt1 = convert(bytes.get(OFFSET + 69), bytes.get(OFFSET + 70));          // 2
        accelPedalVolt2 = convert(bytes.get(OFFSET + 71), bytes.get(OFFSET + 72));          // 2
        etcMotorDuty = convertSigned(bytes.get(OFFSET + 73), bytes.get(OFFSET + 74), bytes.get(OFFSET + 75));  // 3(1:signal, 2:value)
        lpgFuelRailPa = convert(bytes.get(OFFSET + 76), bytes.get(OFFSET + 77));            // 2
        lpgFuelRailPaVolt = convert(bytes.get(OFFSET + 78), bytes.get(OFFSET + 79));        // 2
        fuelPumpCtrl = convert(bytes.get(OFFSET + 80), bytes.get(OFFSET + 81));             // 2
        fuelPumpFault = convert(bytes.get(OFFSET + 82), bytes.get(OFFSET + 83));            // 2
        butaneRatio = convert(bytes.get(OFFSET + 84), bytes.get(OFFSET + 85));              // 2

        tirePressure1 = convert(bytes.get(OFFSET + 86));                                    // 1
        tirePressure2 = convert(bytes.get(OFFSET + 87));                                    // 1
        tirePressure3 = convert(bytes.get(OFFSET + 88));                                    // 1
        tirePressure4 = convert(bytes.get(OFFSET + 89));                                    // 1
        manifoldAirTemp = convertSigned(bytes.get(OFFSET + 90), bytes.get(OFFSET + 91), bytes.get(OFFSET + 92)); // 3(1:signal, 2:value)
        engineOilTemp = convertSigned(bytes.get(OFFSET + 93), bytes.get(OFFSET + 94), bytes.get(OFFSET + 95));   // 3(1:signal, 2:value)

        odometer = convert(bytes.get(OFFSET + 96), bytes.get(OFFSET + 97), bytes.get(OFFSET + 98)); // 3
        emsDtcCount = convert(bytes.get(OFFSET + 99));
        absDtcCount = convert(bytes.get(OFFSET + 100));
        if (bytes.size() > 111) {
            oxyVoltS1Lin = convert(bytes.get(OFFSET + 101), bytes.get(OFFSET + 102));       // 2
        }
    }

    public int getDtcCount() {
        int count1 = emsDtcCount == null ? 0 : emsDtcCount;
        int count2 = absDtcCount == null ? 0 : absDtcCount;
        return count1 + count2;
    }

    @Override
    protected String[] getStringArray() {
        return new String[]{
                batteryVolt == null ? "null" : String.valueOf(batteryVolt),
                ignitionVolt == null ? "null" : String.valueOf(ignitionVolt),
                engineRpm == null ? "null" : String.valueOf(engineRpm),
                mapPa == null ? "null" : String.valueOf(mapPa),
                coolantTemp == null ? "null" : String.valueOf(coolantTemp),
                oxyVoltS2 == null ? "null" : String.valueOf(oxyVoltS2),
                oxyVoltS1 == null ? "null" : String.valueOf(oxyVoltS1),
                vehicleSpd == null ? "null" : String.valueOf(vehicleSpd),
                injectionTimeC1 == null ? "null" : String.valueOf(injectionTimeC1),
                injectionTimeC2 == null ? "null" : String.valueOf(injectionTimeC2),
                injectionTimeC3 == null ? "null" : String.valueOf(injectionTimeC3),
                injectionTimeC4 == null ? "null" : String.valueOf(injectionTimeC4),
                ignitionSwitch == null ? "null" : String.valueOf(ignitionSwitch),
                brakeLampSwitch == null ? "null" : String.valueOf(brakeLampSwitch),
                afRatioCtrlActivation == null ? "null" : String.valueOf(afRatioCtrlActivation),
                acCompressorOn == null ? "null" : String.valueOf(acCompressorOn),
                cmpCkpSync == null ? "null" : String.valueOf(cmpCkpSync),
                coolingFanRelayHs == null ? "null" : String.valueOf(coolingFanRelayHs),
                knockSensing == null ? "null" : String.valueOf(knockSensing),
                batterySensorCurrent == null ? "null" : String.valueOf(batterySensorCurrent),
                batterySensorVolt == null ? "null" : String.valueOf(batterySensorVolt),
                alternatorTargetVolt == null ? "null" : String.valueOf(alternatorTargetVolt),
                timerIgOn == null ? "null" : String.valueOf(timerIgOn),
                torqueCvtTubSpd == null ? "null" : String.valueOf(torqueCvtTubSpd),
                acPaVolt == null ? "null" : String.valueOf(acPaVolt),
                requiredAfRatio == null ? "null" : String.valueOf(requiredAfRatio),
                actualAfRatio == null ? "null" : String.valueOf(actualAfRatio),
                igTimingC1 == null ? "null" : String.valueOf(igTimingC1),
                igFailureCntC1 == null ? "null" : String.valueOf(igFailureCntC1),
                igFailureCntC2 == null ? "null" : String.valueOf(igFailureCntC2),
                igFailureCntC3 == null ? "null" : String.valueOf(igFailureCntC3),
                igFailureCntC4 == null ? "null" : String.valueOf(igFailureCntC4),
                igFailureCntCatalystC1 == null ? "null" : String.valueOf(igFailureCntCatalystC1),
                igFailureCntCatalystC2 == null ? "null" : String.valueOf(igFailureCntCatalystC2),
                igFailureCntCatalystC3 == null ? "null" : String.valueOf(igFailureCntCatalystC3),
                igFailureCntCatalystC4 == null ? "null" : String.valueOf(igFailureCntCatalystC4),
                tpsVolt1 == null ? "null" : String.valueOf(tpsVolt1),
                tpsVolt2 == null ? "null" : String.valueOf(tpsVolt2),
                accelPedalVolt1 == null ? "null" : String.valueOf(accelPedalVolt1),
                accelPedalVolt2 == null ? "null" : String.valueOf(accelPedalVolt2),
                etcMotorDuty == null ? "null" : String.valueOf(etcMotorDuty),
                lpgFuelRailPa == null ? "null" : String.valueOf(lpgFuelRailPa),
                lpgFuelRailPaVolt == null ? "null" : String.valueOf(lpgFuelRailPaVolt),
                fuelPumpCtrl == null ? "null" : String.valueOf(fuelPumpCtrl),
                fuelPumpFault == null ? "null" : String.valueOf(fuelPumpFault),
                butaneRatio == null ? "null" : String.valueOf(butaneRatio),
                tirePressure1 == null ? "null" : String.valueOf(tirePressure1),
                tirePressure2 == null ? "null" : String.valueOf(tirePressure2),
                tirePressure3 == null ? "null" : String.valueOf(tirePressure3),
                tirePressure4 == null ? "null" : String.valueOf(tirePressure4),
                manifoldAirTemp == null ? "null" : String.valueOf(manifoldAirTemp),
                engineOilTemp == null ? "null" : String.valueOf(engineOilTemp),
                odometer == null ? "null" : String.valueOf(odometer),
                oxyVoltS1Lin == null ? "null" : String.valueOf(oxyVoltS1Lin)
        };
    }
}
