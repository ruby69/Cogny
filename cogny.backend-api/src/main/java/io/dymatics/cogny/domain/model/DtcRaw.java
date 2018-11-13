package io.dymatics.cogny.domain.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(of = { "dtcRawNo", "vehicleNo", "obdDeviceNo", "code" })
@JsonInclude(Include.NON_NULL)
public class DtcRaw implements Serializable {
    private static final long serialVersionUID = -2249175126254573457L;

    private static final Map<String, String> DTC_CATEGORY = new HashMap<>();

    private Long dtcRawNo;
    private Long vehicleNo;
    private Long obdDeviceNo;
    private Long driveHistoryNo;
    private long dtcSeq;
    private Date dtcIssuedTime;
    private Date dtcUpdatedTime;
    private String dtcCode;
    private String dtcState;
    private Date regDate;

    private String code;
    private String state;

    public void setCode(String code) {
        this.code = code;
        dtcCode = DTC_CATEGORY.get(code.substring(0, 1)) + code.substring(1);
    }

    public void setState(String state) {
        this.state = state;
        String stateBit = toStateBit(state);
        dtcState = state + ":" + stateBit + ":" + messageBy(stateBit);
    }

    private String messageBy(String stateBit) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<stateBit.length(); i++) {
            if (stateBit.charAt(i) == '1') {
                sb.append(STATE[i]).append(",");
            }
        }
        return sb.toString();
    }

    private static final String[] STATE = {
            "warningIndicatorRequested",
            "testNotCompletedThisOperationCycle",
            "testFailedSinceLastClear",
            "testNotCompletedSinceLastClear",
            "confirmedDTC",
            "pendingDTC",
            "testFailedThisOperationCycle",
            "testFailed"
    };

    private String toStateBit(String state) {
        return String.format("%8s", Integer.toBinaryString(Integer.parseInt(state, 16))).replace(' ', '0');
    }

    static {
        DTC_CATEGORY.put("0", "P0");
        DTC_CATEGORY.put("1", "P1");
        DTC_CATEGORY.put("2", "P2");
        DTC_CATEGORY.put("3", "P3");
        DTC_CATEGORY.put("4", "C0");
        DTC_CATEGORY.put("5", "C1");
        DTC_CATEGORY.put("6", "C2");
        DTC_CATEGORY.put("7", "C3");
        DTC_CATEGORY.put("8", "B0");
        DTC_CATEGORY.put("9", "B1");
        DTC_CATEGORY.put("A", "B2");
        DTC_CATEGORY.put("B", "B3");
        DTC_CATEGORY.put("C", "U0");
        DTC_CATEGORY.put("D", "U1");
        DTC_CATEGORY.put("E", "U2");
        DTC_CATEGORY.put("F", "U3");
    }

/*
 * hex : 0340 -> P0340
 * hex : 0270 -> P0270
 * hex : 5208 -> C1208
 *
 *    0         3    4    0
 * 0000      0011 0100 0000
 * ----      ---------------
 * category  dtcCode
 *
 * 0 - 00 00 - => P0
 * 1 - 00 01 - => P1
 * 2 - 00 10 - => P2
 * 3 - 00 11 - => P3
 *     -- --
 *      P 0~3 : P(powertrain)
 *
 * 4 - 01 00 - => C0
 * 5 - 01 01 - => C1
 * 6 - 01 10 - => C2
 * 7 - 01 11 - => C3
 *     -- --
 *      C 0~3 : C(chassis)
 *
 * 8 - 10 00 - => B0
 * 9 - 10 01 - => B1
 * A - 10 10 - => B2
 * B - 10 11 - => B3
 *     -- --
 *      B 0~3 : B(body)
 *
 * C - 11 00 - => U0
 * D - 11 01 - => U1
 * E - 11 10 - => U2
 * F - 11 11 - => U3
 *     -- --
 *      U 0~3 : U(network)
 *
 *
 *
 *
 * Bit0
 *  - "testFailed"
 *  - 결함이 여전히 존재(활성/주입/발생)하는지에 대한 정보를 제공한다.
 *    결함이 여전히 존재(발생 혹은 활성)하면 1이고, 그렇지 않으면 0이다. 예 : "단락 회로" 결함이 여전히 존재한다.(활성상태 이다. or 발생하고 있다. or 존재한다.)
 *  - This bit provides the information about the fault (Error) is still active (injected) or not.
 *    If Fault is still injected or Active, then the value is 1 otherwise the value is 0.
 *
 * Bit1
 *  - "testFailedThisOperationCycle"
 *  - 결함이 현재 오퍼레이션 사이클 중에 발생했는지를 나타낸다.(결함이 현재 발생 결함인지, 과거 발생 결함인지 나타낸다.)
 *    현재 발생 결함이면 1, 그렇지 않으면 0이다.
 *  - This bit indicates whether the fault is occurred anytime during the current operation cycle.
 *    If Fault has occurred in the current operation cycle, then the value is 1 otherwise the value is 0.
 *
 * Bit2
 *  - "pendingDTC"
 *  - 결함이 현재 오퍼레이션 사이클 중에 발생했는지를 나타낸다.(결함이 현재 발생 결함인지, 과거 발생 결함인지 나타낸다.)
 *    "testFailedThisOperationCycle"과의 차이점은
 *    "testFailedThisOperationCycle"은 현재 오퍼레이션 사이클이 끝날때 지워지고,(적어도 결함이 여전이 존재하는지는 신경써야 한다)
 *    "pendingDTC"는 다음 오퍼레이션 사이클에서 결함이 없어졌을때(모니터 루틴 결과가 PASS를 보였을때) 지워진다.
 *    따라서, 현재 발생 결함이면 1, 과거 발생 했으나 현재 발생하지 않는 결함이면 0이다.
 *  - This bit indicates whether the fault is occurred anytime during the current operation cycle.
 *    The only difference between "testFailedThisOperationCycle" and "pendingDTC" is "testFailedThisOperationCycle" is cleared at the end of current operation cycle (least bothering whether the fault is still active or inactive) and "pendingDTC" is cleared only when in next operation cycle the monitor routine is run and the result shows pass(fault is not present).
 *    So if Fault is still injected or Active in the current operation cycle, then the value is 1 otherwise if the Fault was active in previous operation cycle and is inactive (i.e monitor routine is run and fault is inactive) in the current operation cycle, then the value is 0.
 *
 * Bit3
 *  - "confirmedDTC"
 *  - (특정 모니터 루틴에서)결함이 지속적으로 발생했고, 현재 오퍼레이션 사이클에서 충분히 성숙되어(오래되어) "확실한 결함이라 할 수 있다" 라고 알린다.
 *    결함이 발생하고 성숙(오래)되었으면 1, 그렇지 않으면 0이다.
 *  - This bit informs that fault is continuously active for specific monitor routines and is matured enough in the current operation cycle so that it can be said Confirmed.
 *    If fault is active and matured, then the value is 1 otherwise the value is 0.
 *
 * Bit4
 *  - "testNotCompletedSinceLastClear"
 *  - (DTC 소거 완료 후) 현재 오퍼레이션 사이클 에서 모니터 루틴이 작동하고 있지 않음을 알린다.
 *    현재 오퍼레이션 사이클에서 작동하지 않은 이유는, 특정 핀이 비활성 상태 일 수 있기 때문이다. (예 : 주차 또는 최대 절전 모드)
 *    오퍼레이션 사이클에서 모니터 루틴이 완료되지 않았으면 1, 아니면 0이다.
 *  - This bit informs that monitor routine is not run in the current operation cycle(once after Clearing the DTC is done).
 *    The reason for not running in the current operation cycle can be because particular pin is inactive in the operation cycle(Ex: Parked or hibernate vehicle mode).
 *    If the monitor routine is not completed this operation cycle, then the value is 1 otherwise the value is 0.
 *
 * Bit5
 *  - "testFailedSinceLastClear"
 *  - DTC 소거 이후 최소 한 번, 오퍼레이션 사이클들 중에서 테스트가 실패했음을 알린다.(최소 한 번 Bit0이 설정된다.)
 *    DTC 소거 이후 결함이 발생하면 1, 그렇지 않으면 0이다.
 *  - This bit informs monitor routine has reported that test has failed (at least once Bit0 is set) in any operation cycle at least once after clearing the DTC action is performed.
 *    If the fault has occurred after clear DTC is performed, then the value is 1 otherwise the value is 0.
 *
 * Bit6
 *  - "testNotCompletedThisOperationCycle"
 *  - 현재 오퍼레이션 사이클 에서 모니터 루틴이 작동하고 있지 않음을 알린다.
 *    오퍼레이션 사이클에서 핀이 활성되지 않았거나, 테스터가 요청했을때 모니터 루틴이 작동 중이 아니었기 때문이다.
 *    오퍼레이션 사이클에서 모니터 루틴이 작동하고 있지 않으면 1, 아니면 0이다.
 *  - This bit informs that the monitor routine is still not run during this current operation cycle.
 *    This is because the pin is not active for this operation cycle or when the request is sent from the tester, the monitor routine is not run.
 *    If the monitor routine is not run this operation cycle, then the value is 1 otherwise the value is 0.
 *
 * Bit7
 *  - "warningIndicatorRequested"
 *  - 결함 발생시 사용자나 운전자의 주의를 끌기 위해 사용한다.
 *    결함이 발생하고, 특정 결함에 대한 모니터가 필요한 경우 1, 아니면 0이다.
 *  - This bit is used to bring into the attention of the user or driver when the fault occurs.
 *    If fault occurs and any monitor is required for specific fault, then the value is 1 otherwise the value is 0.
 *
 * ex #1] hex : 03 40 01
 *  - dtcCode : 0340 -> P0340
 *  - status
 *    01  =>  0 0 0 0 0 0 0 1
 *            - - - - - - - -
 *        bit 7 6 5 4 3 2 1 0
 *      # bit0 : testFailed : 결함이 존재한다.
 *
 *
 * ex #2] 03 40 61
 *  - dtcCode : 0340 -> P0340
 *  - status
 *    61  =>  0 1 1 0 0 0 0 1
 *            - - - - - - - -
 *        bit 7 6 5 4 3 2 1 0
 *      # bit0 : testFailed : 결함이 존재한다.
 *      # bit5 : testFailedSinceLastClear: DTC 소거 이후 결함이 발생했다.
 *      # bit6 : testNotCompletedThisOperationCycle : 오퍼레이션 사이클에서 모니터 루틴이 작동하고 있지 않는다.
 *
 * ex #3] 02 70 04
 *  - dtcCode : 0270 -> P0270
 *  - status
 *    04  =>  0 0 0 0 0 1 0 0
 *            - - - - - - - -
 *        bit 7 6 5 4 3 2 1 0
 *      # bit3 : pendingDTC : 현재 발생 결함이다.
 */
}
