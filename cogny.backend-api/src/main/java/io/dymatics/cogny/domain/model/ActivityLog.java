package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityLog implements Serializable {
    private static final long serialVersionUID = -6515558784208226965L;

    public enum Category {
        APP, SIGN, JOB, BT, OBD, ETC
    }

    private Long activityLogNo;
    private Long userNo;
    private Long activitySeq;
    private Long mobileDeviceNo;
    private Long vehicleNo;
    private Long obdDeviceNo;
    private Long driveHistoryNo;
    private Category category;
    private String activity;
    private Date activityTime;
    private Date regDate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<ActivityLog> getInstnaces(List<String> logs) {
        List<ActivityLog> instances = new ArrayList<>(logs.size());
        for (String log : logs) {
            try {
                String[] temp = objectMapper.readValue(log, String[].class);
                ActivityLog activityLog = new ActivityLog();
                activityLog.setUserNo(convert(temp[0]));
                activityLog.setActivitySeq(convert(temp[1]));
                activityLog.setMobileDeviceNo(convert(temp[2]));
                activityLog.setVehicleNo(convert(temp[3]));
                activityLog.setObdDeviceNo(convert(temp[4]));
                activityLog.setDriveHistoryNo(convert(temp[5]));
                activityLog.setCategory(Category.valueOf(temp[6]));
                activityLog.setActivityTime(new Date(convert(temp[8])));

                String[] values = StringUtils.commaDelimitedListToStringArray(temp[7]);
                String message = Code.valueOf(values[0]).convertMessage(values);
                activityLog.setActivity(message == null ? temp[7] : message);

                instances.add(activityLog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instances;
    }

    private static Long convert(String value) {
        return value == null || "null".equals(value) ? null : Long.parseLong(value);
    }


    public enum Code {
        APP10001("%s: 코그니 안드로이드 태스크 라이프사이클 (VersionCode:%s, %s)"),
        APP10002("%s: 코그니 백그라운드 서비스 라이프사이클 (VersionCode:%s, %s)"),
        APP10003("%s: 코그니 업데이트 완료 (VersionCode:%s)"),
        APP10004("%s: 안드로이드 부팅 완료 (VersionCode:%s)"),
        APP10005("%s: 안드로이드 셧다운 (VersionCode:%s)"),

        JOB10001("%s: OBD 장치 검색"),
        JOB10101("%s: OBD 장치 검출 시작"),
        JOB10102("%s: OBD 장치 검출 종료"),
        JOB10201("%s: OBD 센싱 시작"),
        JOB10202("%s: OBD 센싱 종료"),
        JOB10301("%s: 액티비티로그 수집 시작"),
        JOB10302("%s: 액티비티로그 수집 종료"),
        JOB10303("%s: 액티비티로그 전송 실패 (%s)"),
        JOB10401("%s: 센싱로그 전송루틴 시작"),
        JOB10402("%s: 센싱로그 전송루틴 종료"),
        JOB10403("%s: 센싱로그 전송 실패 (%s)"),

        OBD10001("%s: OBD 검출 (serial:%s, fwVersion:%s, tableVersion:%s)"),
        OBD10002("%s: OBD 정보 확인 (serial:%s)"),
        OBD10003("%s: 유효하지 않은 OBD 장치 연결 (serial:%s, fwVersion:%s)"),
        OBD10004("%s: OBD 정보확인 실패 (%s)"),
        OBD10005("%s: OBD 확인 완료 (obdDeviceNo:%s, serial:%s)"),
        OBD10101("%s: FOTA 바이너리 다운로드 완료 (no:%s, type:%s, version:%s)"),
        OBD10102("%s: FOTA 시작 (no:%s, type:%s, version:%s)"),
        OBD10103("%s: FOTA 완료 (no:%s, type:%s, version:%s)"),

        ETC10101("%s: OBD 매핑 차량 확인 (serial:%s)"),
        ETC10102("%s: OBD 매핑 차량 검색 실패"),
        ETC10103("%s: 차량 확인 완료 (vehicleNo:%s, licenseNo:%s)"),
        ETC10201("%s: 운행기록 시작요청 완료 (driveHistoryNo:%s)"),
        ETC10202("%s: 운행기록 시작요청 실패"),
        ETC10203("%s: 운행기록 업데이트 완료 (driveHistoryNo:%s)"),
        ETC10204("%s: 운행기록 업데이트 실패 (driveHistoryNo:%s)"),
        ETC10205("%s: 운행종료"),
        ETC10206("%s: 운행종료 강제실행"),
        ETC10301("%s: DTC 로그 전송 완료 (count:%s)"),
        ETC10302("%s: DTC 로그 전송 실패 (%s)"),

        BT10001("%s: BLE 스캔시작"),
        BT10002("%s: BLE 스캔중지"),
        BT10003("%s: BT 기능 상태변경 (state:%s)"),
        BLE10001("%s: BLE GATT 상태 변경 (%s, %s)"),
        BLE10002("%s: BLE 연결 요청 connect"),
        BLE10003("%s: BLE 기존 연결 있음"),
        BLE10004("%s: BLE GATT 연결 시도 (%s)"),
        BLE10005("%s: BLE GATT 연결 완료 connected (%s)"),
        BLE10006("%s: BLE GATT 연결해제 요청 disconnect"),
        BLE10007("%s: BLE GATT 연결해제 완료 disconnected (%s)"),
        BLE10008("%s: BLE 연결 해제 이벤트 발생"),
        BLE10009("%s: BLE GATT 연결종료 요청 close"),
        BLE10010("%s: BLE GATT 연결 검증"),
        BLE10011("%s: BLE GATT 재연결 (reconnect count:%s)");

        String message;
        Code(String message) {
            this.message = message;
        }

        public String convertMessage(Object[] arr) {
            try {
                return String.format(message, arr);
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

//    APP10001,   // 코그니 안드로이드 태스크 라이프사이클 (create or terminate)
//    APP10002,   // 코그니 백그라운드 서비스 라이프사이클 (create or start or destroy)
//
//    JOB10001,   // OBD 장치 검색
//    JOB10101,   // OBD 장치 검출 Job 시작
//    JOB10102,   // OBD 장치 검출 Job 종료
//    JOB10201,   // OBD 센싱 Job 시작
//    JOB10202,   // OBD 센싱 Job 종료
//    JOB10301,   // 액티비티로그 수집 시작
//    JOB10302,   // 액티비티로그 수집 종료
//    JOB10303,   // 액티비티로그 전송 실패 (message)
//    JOB10401,   // 센싱로그 전송 Job 시작
//    JOB10402,   // 센싱로그 전송 Job 종료
//    JOB10403,   // 센싱로그 전송 실패 (message)
//
//    OBD10001,   // OBD 검출 (serial, fwVersion, tableVersion)
//    OBD10002,   // OBD 정보 확인 (serial)
//    OBD10003,   // 유효하지 않은 OBD 장치 연결 (serial, fwVersion)
//    OBD10004,   // OBD 정보확인 실패 (fail message)
//    OBD10005,   // OBD 확인 완료 (obdDeviceNo, serial)
//    OBD10101,   // FOTA 바이너리 다운로드 완료 (no, type, version)
//    OBD10102,   // FOTA 시작 (no, type, version)
//    OBD10103,   // FOTA 완료 (no, type, version)
//
//    ETC10101,   // OBD 매핑 차량 확인 (serial)
//    ETC10102,   // OBD 매핑 차량 검색 실패
//    ETC10103,   // 차량 확인 완료 (vehicleNo, licenseNo)
//    ETC10201,   // 운행기록 시작요청 완료 (driveHistoryNo)
//    ETC10202,   // 운행기록 시작요청 실패
//    ETC10203,   // 운행기록 업데이트 완료 (driveHistoryNo)
//    ETC10204,   // 운행기록 업데이트 실패 (driveHistoryNo)
//    ETC10205,   // 운행종료
//    ETC10206,   // 운행종료 강제실행
//    ETC10301,   // DTC 로그 전송 완료 (count)
//    ETC10302,   // DTC 로그 전송 실패 (message)
//
//    BT10001,    // BLE 스캔시작
//    BT10002,    // BLE 스캔중지
//    BT10003,    // BT 기능 상태변경 (state)
//    BLE10001,   // BLE GATT 상태 변경 (status, newState)
//    BLE10002,   // BLE 연결 요청 connect
//    BLE10003,   // BLE 기존 연결 있음
//    BLE10004,   // BLE GATT 연결 시도 (address)
//    BLE10005,   // BLE GATT 연결 완료 connected (address)
//    BLE10006,   // BLE GATT 연결해제 요청 disconnect
//    BLE10007,   // BLE GATT 연결해제 완료 disconnected (address)
//    BLE10008,   // BLE 연결 해제 이벤트 발생 post
//    BLE10009,   // BLE GATT 연결종료 요청 close
//    BLE10010,   // BLE GATT 연결 검증
//    BLE10011,   // BLE GATT 재연결 (reconnect count)
}
