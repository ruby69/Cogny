package io.dymatics.cogny.support.obd.handler;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.dymatics.cogny.Constants;
import io.dymatics.cogny.On;
import io.dymatics.cogny.event.obd.ObdAction;
import io.dymatics.cogny.service.CognyBean;
import io.dymatics.cogny.support.obd.Protocol;
import io.dymatics.cogny.support.obd.message.SensorMessage;

@EBean(scope = EBean.Scope.Singleton)
public class LiveHandler extends Handler<SensorMessage> {
    @Bean CognyBean cognyBean;

    private boolean readyLock;
    private boolean finLock;

    @Override
    public void initialize() {
        readyLock = false;
        finLock = false;
    }

    @Override
    protected void doHandle(List<SensorMessage> messages) {
        for (SensorMessage message : messages) {
            Integer rpm = message.getEngineRpm();
            if (validRpm(rpm)) {
                if (rpm > 0 && !cognyBean.isDriving() && !readyLock) {
                    readyLock = true;
                    cognyBean.readyDrive(message.getDate(), message.getOdometer(), new On<Void>().addCompleteListener(result -> readyLock = false));
                }

                if (rpm > 0 && cognyBean.isDriving()) {
                    cognyBean.updateEndMileage(message.getDate(), message.getOdometer());
                    cognyBean.recordSensingLog(message.getValueString());

                    if (message.getDtcCount() > 0) {
                        EventBus.getDefault().post(new ObdAction(Protocol.Cmd.Write.DTC));
                    }
                }

                if (rpm < 1 && cognyBean.isDriving() && !finLock) {
                    finLock = true;
                    cognyBean.finDrive(message.getDate(), new On<Void>().addCompleteListener(result -> finLock = false));
                }
            }

            if (message.getVehicleSpd() > 20) {
                cognyBean.checkFota();
            }
        }
    }

    private boolean validRpm(Integer rpm) {
        return rpm != null && rpm > -1 && rpm < 30000;
    }


//    @Override
//    protected void doHandle(List<SensorMessage> messages) {
//        for (SensorMessage message : messages) {
//            Integer rpm = message.getEngineRpm();
//            if (validRpm(rpm)) {
//                if (rpm > 0 && !cognyBean.isDriving() && !readyLock) {
//                    readyLock = true;
//                    cognyBean.readyDrive(message.getDate(), message.getOdometer(), new On<Void>().addCompleteListener(result -> readyLock = false));
//                }
//
//                if (rpm > 0 && cognyBean.isDriving()) {
//                    cognyBean.updateEndMileage(message.getDate(), message.getOdometer());
//                    cognyBean.recordSensingLog(message.getValueString());
//
//                    genDtcRaws();
//                }
//
//                if (rpm < 1 && cognyBean.isDriving() && !finLock) {
//                    finLock = true;
//                    cognyBean.finDrive(message.getDate(), new On<Void>().addCompleteListener(result -> finLock = false));
//                }
//            }
//
//            if (message.getVehicleSpd() > 20) {
//                cognyBean.checkFota();
//            }
//        }
//    }
//
//    private long count = 0;
//    private void genDtcRaws() {
//        count++;
//        if (count % 2 == 0) {
//            cognyBean.recordDtcRaws(genDtcCode());
//        }
//    }
//
//    private int codeCount;
//    private int genCount;
//    private int sampleIndex;
//    private List<String> genDtcCode() {
//        String timeStr = Constants.FORMAT_DATE_YMD_OBD.format(new Date());
//        List<String> codes = new ArrayList<>();
//        List<String> list = SAMPLE.get(sampleIndex);
//        for (String str : list) {
//            codes.add(String.format("%s,%s,%s", codeCount++, timeStr, str));
//        }
//
//        genCount++;
//        if (genCount == 10) {
//            sampleIndex++;
//            genCount = 0;
//        }
//        if (sampleIndex == SAMPLE.size()) {
//            sampleIndex = 0;
//        }
//
//        return codes;
//    }
//
//    private static final List<List<String>> SAMPLE = new ArrayList<>();
//    static {
//        SAMPLE.add(Arrays.asList("0340,01"));
//        SAMPLE.add(Arrays.asList("0340,04","0270,64"));
//        SAMPLE.add(Arrays.asList("0270,64"));
//        SAMPLE.add(Collections.emptyList());
//        SAMPLE.add(Collections.emptyList());
////        SAMPLE.add(Arrays.asList("0340,01","0270,64","0032,20"));
////        SAMPLE.add(Arrays.asList("0340,14"));
////        SAMPLE.add(Arrays.asList("5283,14","5261,61"));
////        SAMPLE.add(Arrays.asList("5283,64","5261,61"));
////        SAMPLE.add(Arrays.asList("5283,64","5261,61","5623,41"));
////        SAMPLE.add(Arrays.asList("5261,11","5623,20"));
////        SAMPLE.add(Arrays.asList("5261,11"));
////        SAMPLE.add(Arrays.asList("0626,54"));
//    }
}
