-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema cogny
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Table `manufacturer`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `manufacturer` ;

CREATE TABLE IF NOT EXISTS `manufacturer` (
  `manufacturer_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '제조사 고유키',
  `name` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NOT NULL COMMENT '제조사명',
  `country` VARCHAR(45) NULL COMMENT '국가',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`manufacturer_no`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '제조사 마스터';


-- -----------------------------------------------------
-- Table `component`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `component` ;

CREATE TABLE IF NOT EXISTS `component` (
  `component_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '차량부품 고유번호',
  `manufacturer_no` INT UNSIGNED NOT NULL COMMENT '제조사 번호',
  `name` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '차량부품명',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`component_no`),
  INDEX `fk_component_manufacturer1_idx` (`manufacturer_no` ASC),
  CONSTRAINT `fk_component_manufacturer1`
    FOREIGN KEY (`manufacturer_no`)
    REFERENCES `manufacturer` (`manufacturer_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '차량부품 마스터';


-- -----------------------------------------------------
-- Table `partner`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `partner` ;

CREATE TABLE IF NOT EXISTS `partner` (
  `partner_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '제휴사 번호(고유키)',
  `partner_code` VARCHAR(5) NOT NULL,
  `company_name` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL COMMENT '제휴사 명',
  `partner_type` ENUM('TAXI', 'RENTAL') NULL COMMENT '제휴사 구분',
  `tel` VARCHAR(20) NULL COMMENT '제휴사 전화번호',
  `postcode_no` INT UNSIGNED NULL COMMENT '우편번호 고유번호',
  `addr_post_code` VARCHAR(5) NULL COMMENT '제휴사 우편번호 주소',
  `addr_detail` VARCHAR(200) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL COMMENT '제휴사 상세주소',
  `person_in_charge` VARCHAR(50) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL COMMENT '담당자 이름',
  `contract_status` ENUM('STANDBY', 'CONTRACTED', 'NONCONTRACT') NOT NULL DEFAULT 'standby' COMMENT '계약상태(1:대기, 2:계약중, 3:계약만료)',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`partner_no`),
  UNIQUE INDEX `partner_code_UNIQUE` (`partner_code` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '제휴사';


-- -----------------------------------------------------
-- Table `obd_device`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `obd_device` ;

CREATE TABLE IF NOT EXISTS `obd_device` (
  `obd_device_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ODB 단말 고유키',
  `obd_serial` VARCHAR(45) NULL DEFAULT NULL COMMENT 'OBD 단말 ID',
  `partner_no` INT UNSIGNED NULL COMMENT '제휴사 코드(fk)',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `obd_serial_del` VARCHAR(45) NULL DEFAULT NULL COMMENT '삭제된 OBD 단말기의  ID',
  `reg_use_no` INT UNSIGNED NULL COMMENT '등록한 관리자 번호',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`obd_device_no`),
  UNIQUE INDEX `obd_device_id_UNIQUE` (`obd_serial` ASC),
  INDEX `fk_obd_device_partner1_idx` (`partner_no` ASC),
  CONSTRAINT `fk_obd_device_partner1`
    FOREIGN KEY (`partner_no`)
    REFERENCES `partner` (`partner_no`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'OBD 장비 마스터';


-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user` ;

CREATE TABLE IF NOT EXISTS `user` (
  `user_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '사용자 고유번호',
  `partner_no` INT UNSIGNED NULL COMMENT '제휴사코드',
  `uuid` VARCHAR(45) NOT NULL COMMENT 'firebase UUID',
  `sign_provider` ENUM('COGNY', 'GOOGLE', 'FACEBOOK', 'KAKAO') NOT NULL DEFAULT 'COGNY',
  `email` VARCHAR(100) NOT NULL COMMENT '이메일',
  `name` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '사용자명',
  `tel` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '전화번호',
  `hp_no` VARCHAR(16) NULL DEFAULT NULL COMMENT '핸드폰번호',
  `role` ENUM('ADMIN', 'PARTNER_MECHANIC', 'DRIVER', 'ADMIN_ANALYST') NOT NULL DEFAULT 'DRIVER' COMMENT '권한 - 1:ADMIN(관리자), 2:PARTNER_MECHANIC(고객사의 정비사), 3: 운전자(DRIVER), 4: 본사분석가(ADMIN_ANALYST)',
  `user_status` ENUM('MEMBER', 'QUIT') NOT NULL DEFAULT 'MEMBER' COMMENT '가입상태\nMEMBER:가입중\nQUIT:탈퇴',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`user_no`),
  UNIQUE INDEX `user_uuid_UNIQUE` (`uuid` ASC),
  FULLTEXT INDEX `email_idx` (`email` ASC),
  INDEX `partner_no_idx` (`partner_no` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '사용자 마스터';


-- -----------------------------------------------------
-- Table `fuel`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fuel` ;

CREATE TABLE IF NOT EXISTS `fuel` (
  `fuel_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '연료타입 고유키',
  `name` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '연료타입',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL COMMENT '수정일',
  `del_date` DATETIME NULL COMMENT '삭제일',
  PRIMARY KEY (`fuel_no`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '사용연료 마스터';


-- -----------------------------------------------------
-- Table `model_group`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `model_group` ;

CREATE TABLE IF NOT EXISTS `model_group` (
  `model_group_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '모델그룹번호',
  `manufacturer_no` INT UNSIGNED NOT NULL COMMENT '제조사 번호',
  `name` VARCHAR(45) NULL COMMENT '모델그룹명',
  `type` ENUM('SEDAN', 'SUV', 'VAN', 'BUS', 'TRUCK', 'ETC') NULL COMMENT '모델그룹 타입- 1(SEDAN:승용차), 2(SUV:SUV/RV), 3(VAN: 승합차), 4(BUS: 버스), 5(TRUCK: 트럭), 6(ETC: 기타)',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`model_group_no`),
  INDEX `fk_model_group_manufacturer1_idx` (`manufacturer_no` ASC),
  CONSTRAINT `fk_model_group_manufacturer1`
    FOREIGN KEY (`manufacturer_no`)
    REFERENCES `manufacturer` (`manufacturer_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '모델그룹 테이블';


-- -----------------------------------------------------
-- Table `model`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `model` ;

CREATE TABLE IF NOT EXISTS `model` (
  `model_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '차량모델고유키',
  `model_group_no` INT UNSIGNED NOT NULL COMMENT '모델그룹번호',
  `name` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '차량모델명',
  `begin_year` SMALLINT UNSIGNED NULL COMMENT '출시년도',
  `end_year` SMALLINT UNSIGNED NULL COMMENT '판매 마지막년도',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`model_no`),
  INDEX `fk_model_model_group1_idx` (`model_group_no` ASC),
  CONSTRAINT `fk_model_model_group1`
    FOREIGN KEY (`model_group_no`)
    REFERENCES `model_group` (`model_group_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '차량 모델 마스터';


-- -----------------------------------------------------
-- Table `vehicle`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `vehicle` ;

CREATE TABLE IF NOT EXISTS `vehicle` (
  `vehicle_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '차량고유키',
  `model_no` INT UNSIGNED NOT NULL COMMENT '모델고유번호',
  `fuel_no` INT UNSIGNED NOT NULL COMMENT '연료 타입 외래키',
  `license_no` VARCHAR(16) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '자동차등록번호',
  `model_year` SMALLINT(6) NULL DEFAULT NULL COMMENT '출시년도',
  `model_month` TINYINT(4) NULL DEFAULT NULL COMMENT '출시월',
  `partner_no` INT UNSIGNED NULL COMMENT '제휴사 코드(fk)',
  `memo` VARCHAR(1000) NULL DEFAULT NULL COMMENT '메모',
  `reg_user_no` INT UNSIGNED NULL COMMENT '등록한 관리자 번호',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`vehicle_no`),
  INDEX `fk_vehicle_model` (`model_no` ASC),
  INDEX `fk_vehicle_fuel1` (`fuel_no` ASC),
  INDEX `fk_vehicle_partner1_idx` (`partner_no` ASC),
  CONSTRAINT `fk_vehicle_fuel1`
    FOREIGN KEY (`fuel_no`)
    REFERENCES `fuel` (`fuel_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_vehicle_model`
    FOREIGN KEY (`model_no`)
    REFERENCES `model` (`model_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_vehicle_partner1`
    FOREIGN KEY (`partner_no`)
    REFERENCES `partner` (`partner_no`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '차량 마스터';


-- -----------------------------------------------------
-- Table `mobile_device`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mobile_device` ;

CREATE TABLE IF NOT EXISTS `mobile_device` (
  `mobile_device_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '모바일 장치 고유번호',
  `uuid` VARCHAR(64) NOT NULL COMMENT '모바일 장치 UUID',
  `os` ENUM('ANDROID', 'IOS', 'WIN') NOT NULL DEFAULT 'ANDROID' COMMENT 'android, ios, win mobile',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  PRIMARY KEY (`mobile_device_no`),
  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '모바일 장치';


-- -----------------------------------------------------
-- Table `user_mobile_device`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user_mobile_device` ;

CREATE TABLE IF NOT EXISTS `user_mobile_device` (
  `user_mobile_device_no` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_no` INT UNSIGNED NOT NULL,
  `mobile_device_no` INT UNSIGNED NOT NULL,
  `mobile_number` VARCHAR(20) NULL,
  `fcm_id` VARCHAR(20) NULL,
  `fcm_token` TEXT NULL,
  `push_agree` TINYINT(1) NULL DEFAULT 0,
  `push_agree_date` DATETIME NULL,
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_mobile_device_no`),
  INDEX `fk_user_mobile_device_user1_idx` (`user_no` ASC),
  INDEX `fk_user_mobile_device_mobile_device1_idx` (`mobile_device_no` ASC),
  CONSTRAINT `fk_user_mobile_device_user1`
    FOREIGN KEY (`user_no`)
    REFERENCES `user` (`user_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_user_mobile_device_mobile_device1`
    FOREIGN KEY (`mobile_device_no`)
    REFERENCES `mobile_device` (`mobile_device_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `drive_history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `drive_history` ;

CREATE TABLE IF NOT EXISTS `drive_history` (
  `drive_history_no` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '주행이력 고유키',
  `vehicle_no` INT UNSIGNED NOT NULL COMMENT '차량고유번호 외래키',
  `obd_device_no` INT UNSIGNED NOT NULL COMMENT 'ODB 단말 번호',
  `user_no` INT UNSIGNED NOT NULL COMMENT '사용자번호',
  `user_mobile_device_no` INT UNSIGNED NOT NULL,
  `start_date` DATE NOT NULL COMMENT '주행시작일 (Date type)',
  `start_time` TIME NOT NULL COMMENT '주행시작 시각(time type)',
  `start_latitude` DOUBLE NULL COMMENT '주행시작 지점의 위도',
  `start_longitude` DOUBLE NULL COMMENT '주행시작 지점의 경도',
  `start_address` VARCHAR(200) NULL COMMENT '주행시작 지점의 주소 (위도 경도 기준)',
  `start_mileage` INT UNSIGNED NULL COMMENT '주행시작 시점의 차량 총 주행거리(ODO, km)',
  `end_time` DATETIME NULL COMMENT '주행종료 시각',
  `end_latitude` DOUBLE NULL COMMENT '주행종료 지점의 위도',
  `end_longitude` DOUBLE NULL COMMENT '주행종료 지점의 경도',
  `end_address` VARCHAR(200) NULL COMMENT '주행종료 지점의 주소(위도 경도 기준)',
  `end_mileage` INT UNSIGNED NULL COMMENT '주행종료 시점의 차량 총 주행거리(ODO, km)',
  `drive_distance` INT UNSIGNED NULL COMMENT '주행 시작부터 종료까지 주행거리( end_mileage - start_mileage )',
  `drive_fuel_mileage` INT UNSIGNED NULL COMMENT '주행 시작부터 종료시점 동안의 연비',
  `drive_fuel_consumption` INT UNSIGNED NULL COMMENT '주행 시작부터 종료시점까지의 연료 소모량',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL,
  PRIMARY KEY (`drive_history_no`),
  INDEX `fk_drive_history_vehicle1` (`vehicle_no` ASC),
  INDEX `fk_drive_history_obd1` (`obd_device_no` ASC),
  INDEX `fk_drive_history_user1` (`user_no` ASC),
  INDEX `fk_drive_history_user_mobile_device1_idx` (`user_mobile_device_no` ASC),
  INDEX `idx_start_date` (`start_date` ASC),
  CONSTRAINT `fk_drive_history_obd1`
    FOREIGN KEY (`obd_device_no`)
    REFERENCES `obd_device` (`obd_device_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_drive_history_user1`
    FOREIGN KEY (`user_no`)
    REFERENCES `user` (`user_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_drive_history_vehicle1`
    FOREIGN KEY (`vehicle_no`)
    REFERENCES `vehicle` (`vehicle_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_drive_history_user_mobile_device1`
    FOREIGN KEY (`user_mobile_device_no`)
    REFERENCES `user_mobile_device` (`user_mobile_device_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '주행이력';


-- -----------------------------------------------------
-- Table `dtc`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dtc` ;

CREATE TABLE IF NOT EXISTS `dtc` (
  `dtc_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'DTC 고유번호(pk)',
  `manufacturer_no` INT UNSIGNED NOT NULL COMMENT '제조사 번호(fk)',
  `code` VARCHAR(25) NULL DEFAULT NULL COMMENT 'DTC 코드',
  `desc` VARCHAR(200) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT 'DTC 설명',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  PRIMARY KEY (`dtc_no`),
  INDEX `fk_dtc_manufacturer1_idx` (`manufacturer_no` ASC),
  CONSTRAINT `fk_dtc_manufacturer1`
    FOREIGN KEY (`manufacturer_no`)
    REFERENCES `manufacturer` (`manufacturer_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'DTC 마스터';


-- -----------------------------------------------------
-- Table `dtc_component`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dtc_component` ;

CREATE TABLE IF NOT EXISTS `dtc_component` (
  `dtc_component_no` INT UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT COMMENT '부품별 DTC 코드 고유번호',
  `component_no` INT UNSIGNED NOT NULL COMMENT '차량부품 외부키',
  `dtc_no` INT UNSIGNED NOT NULL COMMENT 'DTC 코드 외부키',
  `name` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '부품별 DTC 코드 이름',
  `desc` VARCHAR(100) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '부품별 DTC 코드 설명',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  PRIMARY KEY (`dtc_component_no`),
  UNIQUE INDEX `component_dtc_unique` (`component_no` ASC, `dtc_no` ASC),
  INDEX `fk_dtc_component_dtc1` (`dtc_no` ASC),
  CONSTRAINT `fk_dtc_component_component1`
    FOREIGN KEY (`component_no`)
    REFERENCES `component` (`component_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_dtc_component_dtc1`
    FOREIGN KEY (`dtc_no`)
    REFERENCES `dtc` (`dtc_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '부품별 DTC 코드';


-- -----------------------------------------------------
-- Table `dtc_raw`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dtc_raw` ;

CREATE TABLE IF NOT EXISTS `dtc_raw` (
  `dtc_raw_no` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '주행별 DTC 수집 데이터 고유번호',
  `vehicle_no` INT UNSIGNED NOT NULL COMMENT '차량고유번호',
  `obd_device_no` INT UNSIGNED NOT NULL COMMENT 'ODB 단말 고유번호',
  `drive_history_no` BIGINT UNSIGNED NOT NULL COMMENT '주행이력 고유번호',
  `dtc_seq` INT UNSIGNED NULL,
  `dtc_issued_time` DATETIME NOT NULL COMMENT 'DTC 발생시각',
  `dtc_updated_time` DATETIME NOT NULL COMMENT 'DTC 업데이트 시간 ',
  `dtc_code` VARCHAR(10) NOT NULL COMMENT 'DTC 코드',
  `dtc_state` VARCHAR(450) NOT NULL COMMENT 'DTC 상태 ',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  PRIMARY KEY (`dtc_raw_no`),
  INDEX `vehicle_no_idx` (`vehicle_no` ASC),
  INDEX `obd_device_no_idx` (`obd_device_no` ASC),
  INDEX `drive_history_no_idx` (`drive_history_no` ASC),
  INDEX `dtc_seq_idx` (`dtc_seq` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '주행별 DTC 수집 데이터';


-- -----------------------------------------------------
-- Table `obd_device_vehicle`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `obd_device_vehicle` ;

CREATE TABLE IF NOT EXISTS `obd_device_vehicle` (
  `obd_device_vehicle_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '차량 OBD 설치 기록',
  `obd_device_no` INT UNSIGNED NOT NULL COMMENT 'OBD 단말 외래키',
  `vehicle_no` INT UNSIGNED NOT NULL COMMENT '차량 고유번호 외래키',
  `install_date` DATE NULL DEFAULT NULL COMMENT '설치일',
  `uninstall_date` DATE NULL DEFAULT NULL COMMENT '제거일',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  PRIMARY KEY (`obd_device_vehicle_no`),
  INDEX `fk_obd_vehicle_vehicle1` (`vehicle_no` ASC),
  INDEX `fk_obd_vehicle_obd1` (`obd_device_no` ASC),
  CONSTRAINT `fk_obd_vehicle_obd1`
    FOREIGN KEY (`obd_device_no`)
    REFERENCES `obd_device` (`obd_device_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_obd_vehicle_vehicle1`
    FOREIGN KEY (`vehicle_no`)
    REFERENCES `vehicle` (`vehicle_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '차량별 OBD 설치 현황';


-- -----------------------------------------------------
-- Table `obd_sensor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `obd_sensor` ;

CREATE TABLE IF NOT EXISTS `obd_sensor` (
  `obd_sensor_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ODB 센서 고유번호',
  `code` VARCHAR(45) NULL DEFAULT NULL COMMENT 'OBD 코드',
  `name` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT 'OBD 명',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  PRIMARY KEY (`obd_sensor_no`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'ODB 센서 마스터';


-- -----------------------------------------------------
-- Table `postcode`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `postcode` ;

CREATE TABLE IF NOT EXISTS `postcode` (
  `postcode_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '우편번호 고유번호(고유키)',
  `postcode` VARCHAR(5) NOT NULL COMMENT '우편번호',
  `sido` VARCHAR(20) NULL COMMENT '시도',
  `sido_en` VARCHAR(40) NULL COMMENT '시도영문',
  `sigungu` VARCHAR(20) NULL COMMENT '시군구',
  `sigungu_en` VARCHAR(40) NULL COMMENT '시군구영문',
  `eupmyeon` VARCHAR(20) NULL COMMENT '읍면',
  `eupmyeon_en` VARCHAR(40) NULL COMMENT '읍면영문',
  `street_name_code` VARCHAR(12) NULL COMMENT '도로명코드',
  `street_name` VARCHAR(80) NULL COMMENT '도로명',
  `street_name_en` VARCHAR(80) NULL COMMENT '도로명영문',
  `underground_bl` TINYINT NULL COMMENT '지하여부',
  `building_main_num` INT UNSIGNED NULL COMMENT '건물번호본번',
  `building_sub_num` INT UNSIGNED NULL COMMENT '건물번호부번',
  `building_mgt_num` VARCHAR(25) NULL COMMENT '건물관리번호',
  `bulk_delivery_name` VARCHAR(40) NULL COMMENT '다량배달처명',
  `sigungu_bldg_name` VARCHAR(200) NULL COMMENT '시군구용건물명',
  `legal_dong_code` VARCHAR(10) NULL COMMENT '법정동코드',
  `legal_dong_name` VARCHAR(20) NULL COMMENT '법정동명',
  `li_name` VARCHAR(20) NULL COMMENT '리명',
  `adm_dong_name` VARCHAR(40) NULL COMMENT '행정동명',
  `san_bl` VARCHAR(1) NULL COMMENT '산여부',
  `lot_main_num` INT UNSIGNED NULL COMMENT '지번본번',
  `eupmyeondong_sn` VARCHAR(2) NULL COMMENT '읍면동일련번호',
  `lot_sub_num` INT UNSIGNED NULL COMMENT '지번부번',
  `old_postcode` VARCHAR(6) NULL COMMENT '구 우편번호',
  `postcode_sn` VARCHAR(3) NULL COMMENT '우편번호일련번호',
  `reg_date` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  PRIMARY KEY (`postcode_no`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = '우편번호마스터(출처:https://www.epost.go.kr/search/zipcode/areacdAddressDown.jsp)';


-- -----------------------------------------------------
-- Table `user_vehicle`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user_vehicle` ;

CREATE TABLE IF NOT EXISTS `user_vehicle` (
  `user_vehicle_no` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_no` INT UNSIGNED NOT NULL,
  `vehicle_no` INT UNSIGNED NOT NULL,
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_vehicle_no`),
  INDEX `fk_user_vehicle_user1_idx` (`user_no` ASC),
  INDEX `fk_user_vehicle_vehicle1_idx` (`vehicle_no` ASC),
  CONSTRAINT `fk_user_vehicle_user1`
    FOREIGN KEY (`user_no`)
    REFERENCES `user` (`user_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_user_vehicle_vehicle1`
    FOREIGN KEY (`vehicle_no`)
    REFERENCES `vehicle` (`vehicle_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `drive_trace`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `drive_trace` ;

CREATE TABLE IF NOT EXISTS `drive_trace` (
  `drive_trace_no` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `drive_history_no` BIGINT UNSIGNED NOT NULL,
  `latitude` DOUBLE NOT NULL,
  `longitude` DOUBLE NOT NULL,
  `date` DATETIME NOT NULL,
  PRIMARY KEY (`drive_trace_no`),
  INDEX `fk_drive_trace_drive_history1_idx` (`drive_history_no` ASC),
  CONSTRAINT `fk_drive_trace_drive_history1`
    FOREIGN KEY (`drive_history_no`)
    REFERENCES `drive_history` (`drive_history_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `analysis`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `analysis` ;

CREATE TABLE IF NOT EXISTS `analysis` (
  `analysis_no` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `vehicle_no` INT UNSIGNED NOT NULL,
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`analysis_no`),
  INDEX `fk_analysis_history_vehicle1_idx` (`vehicle_no` ASC),
  CONSTRAINT `fk_analysis_history_vehicle1`
    FOREIGN KEY (`vehicle_no`)
    REFERENCES `vehicle` (`vehicle_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `analysis_component`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `analysis_component` ;

CREATE TABLE IF NOT EXISTS `analysis_component` (
  `analysis_component_no` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `analysis_no` INT UNSIGNED NOT NULL,
  `component_no` INT UNSIGNED NOT NULL,
  `vehicle_no` INT UNSIGNED NOT NULL,
  `category` ENUM('REPLACE', 'SUPPLY', 'CHECK', 'FINE', 'ETC') NOT NULL DEFAULT 'ETC',
  `description` VARCHAR(400) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL,
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `upd_date` DATETIME NULL,
  PRIMARY KEY (`analysis_component_no`),
  INDEX `fk_analysis_component_analysis1_idx` (`analysis_no` ASC),
  INDEX `fk_analysis_component_component1_idx` (`component_no` ASC),
  INDEX `fk_analysis_component_vehicle1_idx` (`vehicle_no` ASC),
  CONSTRAINT `fk_analysis_component_analysis1`
    FOREIGN KEY (`analysis_no`)
    REFERENCES `analysis` (`analysis_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_analysis_component_component1`
    FOREIGN KEY (`component_no`)
    REFERENCES `component` (`component_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_analysis_component_vehicle1`
    FOREIGN KEY (`vehicle_no`)
    REFERENCES `vehicle` (`vehicle_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `repair`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `repair` ;

CREATE TABLE IF NOT EXISTS `repair` (
  `repair_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '수리이력고유번호',
  `vehicle_no` INT UNSIGNED NOT NULL COMMENT '차량번호',
  `user_no` INT UNSIGNED NOT NULL COMMENT '\'정비완료\'한 사용자 번호',
  `repair_shop_no` INT UNSIGNED NULL COMMENT '정비소번호',
  `odometer` MEDIUMINT UNSIGNED NULL COMMENT '총주행거리(odometer)',
  `repair_date` DATE NULL COMMENT '수리일자',
  `memo` VARCHAR(400) NULL COMMENT '정비 메모',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`repair_no`),
  INDEX `fk_repair_vehicle1_idx` (`vehicle_no` ASC),
  INDEX `repair_shop_no_idx` (`repair_shop_no` ASC),
  INDEX `fk_repair_user1_idx` (`user_no` ASC),
  CONSTRAINT `fk_repair_vehicle1`
    FOREIGN KEY (`vehicle_no`)
    REFERENCES `vehicle` (`vehicle_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_repair_user1`
    FOREIGN KEY (`user_no`)
    REFERENCES `user` (`user_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '차량 수리 이력';


-- -----------------------------------------------------
-- Table `component_cate1`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `component_cate1` ;

CREATE TABLE IF NOT EXISTS `component_cate1` (
  `component_cate1_no` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '부품 영역 키',
  `code` TINYINT UNSIGNED NULL COMMENT '부품 영역 코드',
  `name` VARCHAR(45) NULL COMMENT '부품 영역 이름',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`component_cate1_no`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = '부품 영역 테이블';


-- -----------------------------------------------------
-- Table `component_cate2`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `component_cate2` ;

CREATE TABLE IF NOT EXISTS `component_cate2` (
  `component_cate2_no` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `component_cate1_no` SMALLINT UNSIGNED NOT NULL,
  `code` SMALLINT UNSIGNED NULL,
  `name` VARCHAR(45) NULL,
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`component_cate2_no`),
  INDEX `fk_component_cate2_component_cate11_idx` (`component_cate1_no` ASC),
  CONSTRAINT `fk_component_cate2_component_cate11`
    FOREIGN KEY (`component_cate1_no`)
    REFERENCES `component_cate1` (`component_cate1_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '부품 계통 테이블';


-- -----------------------------------------------------
-- Table `component_cate`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `component_cate` ;

CREATE TABLE IF NOT EXISTS `component_cate` (
  `component_cate_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '부품별 카테고리 번호(pk)',
  `component_no` INT UNSIGNED NOT NULL COMMENT '부품번호',
  `component_cate1_no` SMALLINT UNSIGNED NOT NULL COMMENT '부품 영역 번호',
  `component_cate2_no` INT UNSIGNED NOT NULL COMMENT '부품 계통 번호',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`component_cate_no`),
  INDEX `fk_component_cate_component1_idx` (`component_no` ASC),
  INDEX `fk_component_cate_component_cate11_idx` (`component_cate1_no` ASC),
  INDEX `fk_component_cate_component_cate21_idx` (`component_cate2_no` ASC),
  UNIQUE INDEX `uk_component_cate` (`component_no` ASC, `component_cate1_no` ASC, `component_cate2_no` ASC),
  CONSTRAINT `fk_component_cate_component1`
    FOREIGN KEY (`component_no`)
    REFERENCES `component` (`component_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_component_cate_component_cate11`
    FOREIGN KEY (`component_cate1_no`)
    REFERENCES `component_cate1` (`component_cate1_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_component_cate_component_cate21`
    FOREIGN KEY (`component_cate2_no`)
    REFERENCES `component_cate2` (`component_cate2_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = '부품별 카테고리 매칭';


-- -----------------------------------------------------
-- Table `repair_component`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `repair_component` ;

CREATE TABLE IF NOT EXISTS `repair_component` (
  `repair_component_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '정비 부품 번호',
  `repair_no` INT UNSIGNED NOT NULL COMMENT '수리이력고유번호',
  `component_cate_no` INT UNSIGNED NOT NULL COMMENT '부품번호',
  `category` ENUM('SUPPLEMENT', 'DEMOUNT', 'REPLACE', 'DISASSEMBLE', 'CHECK', 'ALIGN', 'REPAIR', 'ETC') NULL DEFAULT NULL COMMENT '정비구분 - supplement(보충), demount(탈착), replace(교환), disassemble(분해), check(점검), align(조정), repair(수리), etc(기타)',
  `cost` INT NULL DEFAULT 0 COMMENT '수리비용',
  `memo` VARCHAR(400) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL COMMENT '부품 정비 메모',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`repair_component_no`),
  INDEX `fk_repair_parts_repair1_idx` (`repair_no` ASC),
  INDEX `fk_repair_component_component_cate_idx` (`component_cate_no` ASC),
  CONSTRAINT `fk_repair_parts_repair`
    FOREIGN KEY (`repair_no`)
    REFERENCES `repair` (`repair_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_repair_component_component_cate`
    FOREIGN KEY (`component_cate_no`)
    REFERENCES `component_cate` (`component_cate_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '차량 수리 부품이력';


-- -----------------------------------------------------
-- Table `repair_shop`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `repair_shop` ;

CREATE TABLE IF NOT EXISTS `repair_shop` (
  `repair_shop_no` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NOT NULL,
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`repair_shop_no`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `sensing_raw`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sensing_raw` ;

CREATE TABLE IF NOT EXISTS `sensing_raw` (
  `sensing_raw_no` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'OBD 초단위 원천 데이터 고유번호',
  `vehicle_no` INT UNSIGNED NOT NULL COMMENT '차량고유번호(외래키)',
  `obd_device_no` INT UNSIGNED NOT NULL COMMENT 'ODB 단말 고유번호(외래키)',
  `drive_history_no` BIGINT UNSIGNED NOT NULL COMMENT '주행이력 고유번호(외래키)',
  `sensing_seq` INT UNSIGNED NULL,
  `sensing_time` DATETIME NOT NULL COMMENT '데이터 수집 시간',
  `odometer` MEDIUMINT UNSIGNED NULL DEFAULT NULL COMMENT '총주행거리(odometer)',
  `battery_volt` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '배터리 전압(V)-BAT',
  `ignition_volt` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '이그니션전압(V)-IGV',
  `engine_rpm` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '엔진회전수(RPM)-RPM',
  `map_pa` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '흡기압(MAP)센서(kPa)-MAP',
  `coolant_temp` SMALLINT NULL DEFAULT NULL COMMENT '냉각수온센서(C)-COT',
  `oxy_volt_s1` SMALLINT NULL DEFAULT NULL COMMENT '산소센서전압(B1/S1)(V) Linear - OL1',
  `oxy_volt_s1_lin` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '산소센서전압(B1/S1)(V)-OV1',
  `oxy_volt_s2` SMALLINT NULL DEFAULT NULL COMMENT '산소센서전압(B1/S2)(V)-OV2',
  `vehicle_spd` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '차량속도(Km/h)-SPD',
  `injection_time_c1` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '연료분사 시간-실린더 1(mS)-IJ1',
  `injection_time_c2` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '연료분사 시간-실린더 2(mS)-IJ2',
  `injection_time_c3` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '연료분사 시간-실린더 3(mS)-IJ3',
  `injection_time_c4` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '연료분사 시간-실린더 4(mS)-IJ4',
  `ignition_switch` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '이그니션 스위치(ON:1 | OFF:0)-IGS',
  `brake_lamp_switch` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '브레이크등 스위치(ON:1 | OFF:0)-BLS',
  `af_ratio_ctrl_activation` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '공연비 제어 활성화(ON:1 | OFF:0)-AFC',
  `ac_compressor_on` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '에어컨컴프레서 ON(ON:1 | OFF:0)-ACO',
  `cmp_ckp_sync` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT 'CMP/CKP 동기화(ON:1 | OFF:0)-CCS',
  `cooling_fan_relay_hs` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '쿨링팬 릴레이(고속)(ON:1 | OFF:0)-CRH',
  `knock_sensing` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '노크 감지(B1/S1)(ON:1 | OFF:0)-KNS',
  `battery_sensor_current` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '배터리센서 전류(AMS)(A)-BSC',
  `battery_sensor_volt` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '배터리센서 전압(AMS)(V)-BSV',
  `alternator_target_volt` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '발전기 목표전압(듀티)(%)-ATV',
  `timer_ig_on` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '타이머(IG ON 이후)(Sec)-TIM',
  `torque_cvt_tub_spd` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '토크컨버터터빈스피드(RPM)-TTS',
  `ac_pa_volt` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '에어컨 압력 센서 전압(V)-APV',
  `required_af_ratio` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '요구 공연비-RAF',
  `actual_af_ratio` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '실제 공연비(-)-AAF',
  `ig_timing_c1` SMALLINT NULL DEFAULT NULL COMMENT '점화시기-실린더 1(CRK)-IG1',
  `ig_failure_cnt_c1` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '실화 발생 횟수-CYL 1-IF1',
  `ig_failure_cnt_c2` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '실화 발생 횟수-CYL 2-IF2',
  `ig_failure_cnt_c3` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '실화 발생 횟수-CYL 3-IF3',
  `ig_failure_cnt_c4` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '실화 발생 횟수-CYL 4-IF4',
  `ig_failure_cnt_catalyst_c1` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '실화발생횟수(촉매)CYL 1-IC1',
  `ig_failure_cnt_catalyst_c2` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '실화발생횟수(촉매)CYL 2-IC2',
  `ig_failure_cnt_catalyst_c3` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '실화발생횟수(촉매)CYL 3-IC3',
  `ig_failure_cnt_catalyst_c4` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '실화발생횟수(촉매)CYL 4-IC4',
  `tps_volt1` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '스로틀포지션센서-1전압(V)-TV1',
  `tps_volt2` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '스로틀포지션센서-2전압(V)-TV2',
  `accel_pedal_volt1` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '엑셀 페달 센서 1 전압(V)-AP1',
  `accel_pedal_volt2` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '엑셀 페달 센서 2 전압(V)-AP2',
  `etc_motor_duty` MEDIUMINT NULL DEFAULT NULL COMMENT 'ETC 모터 듀티(%)-EMD',
  `lpg_fuel_rail_pa` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT 'LPG 연료 레일압력(kPa)-LFP',
  `lpg_fuel_rail_pa_volt` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT 'LPG 연료 레일압력-전압(V)-LFV',
  `fuel_pump_ctrl` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '연료펌프제어-PWM신호(%)-FPC',
  `fuel_pump_fault` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '연료펌프 고장진단(Hz)-FPF',
  `butane_ratio` SMALLINT UNSIGNED NULL DEFAULT NULL COMMENT '연료내 부탄함량비율 계산(%)-BTR',
  `tire_pressure1` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '타이어 압력1 (TPMS)(kPa)-TP1',
  `tire_pressure2` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '타이어 압력2 (TPMS)(kPa)-TP2',
  `tire_pressure3` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '타이어 압력3 (TPMS)(kPa)-TP3',
  `tire_pressure4` TINYINT UNSIGNED NULL DEFAULT NULL COMMENT '타이어 압력4 (TPMS)(kPa)-TP4',
  `manifold_air_temp` MEDIUMINT NULL DEFAULT NULL COMMENT '흡기온도(℃)-MAT',
  `engine_oil_temp` MEDIUMINT NULL DEFAULT NULL COMMENT '엔진오일 온도(℃)-EOT',
  `analysis_yn` ENUM('Y', 'N') NOT NULL DEFAULT 'N' COMMENT '분석 수행 여부(1: Y, 2:N)',
  `reg_date` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL COMMENT '수정일',
  `analysis_date` DATETIME NULL COMMENT '분석 수행 일시',
  PRIMARY KEY (`sensing_raw_no`),
  INDEX `vehicle_no_idx` (`vehicle_no` ASC),
  INDEX `obd_device_no_idx` (`obd_device_no` ASC),
  INDEX `drive_history_no_idx` (`drive_history_no` ASC),
  INDEX `sensing_time_idx` (`sensing_time` ASC),
  INDEX `sensing_seq_idx` (`sensing_seq` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'OBD 초단위 원천 데이터';


-- -----------------------------------------------------
-- Table `fota`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fota` ;

CREATE TABLE IF NOT EXISTS `fota` (
  `fota_no` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `type` ENUM('F', 'T') NOT NULL DEFAULT 'F' COMMENT 'F:Firmware\nT:Table',
  `version` VARCHAR(10) NOT NULL,
  `enabled` TINYINT(1) NOT NULL DEFAULT 1,
  `url` VARCHAR(255) NOT NULL,
  `reg_date` TIMESTAMP NOT NULL,
  PRIMARY KEY (`fota_no`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `drive_history_memo`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `drive_history_memo` ;

CREATE TABLE IF NOT EXISTS `drive_history_memo` (
  `drive_history_memo_no` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '주행기록 메모 고유번호(PK)',
  `drive_history_no` BIGINT UNSIGNED NOT NULL COMMENT '주행기록 고유번호(FK)',
  `memo` VARCHAR(1000) NULL COMMENT '메모',
  `reg_user_no` INT UNSIGNED NULL COMMENT '메모 등록 사용자',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`drive_history_memo_no`),
  INDEX `fk_vehicle_memo_drive_history1_idx` (`drive_history_no` ASC),
  INDEX `fk_vehicle_memo_user1_idx` (`reg_user_no` ASC),
  UNIQUE INDEX `drive_history_no_UNIQUE` (`drive_history_no` ASC),
  CONSTRAINT `fk_vehicle_memo_drive_history1`
    FOREIGN KEY (`drive_history_no`)
    REFERENCES `drive_history` (`drive_history_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_vehicle_memo_user1`
    FOREIGN KEY (`reg_user_no`)
    REFERENCES `user` (`user_no`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = '주행이력 메모';


-- -----------------------------------------------------
-- Table `activity_log`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `activity_log` ;

CREATE TABLE IF NOT EXISTS `activity_log` (
  `activity_log_no` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_no` INT UNSIGNED NOT NULL,
  `activity_seq` INT UNSIGNED NOT NULL,
  `mobile_device_no` INT UNSIGNED NULL,
  `vehicle_no` INT UNSIGNED NULL,
  `obd_device_no` INT UNSIGNED NULL,
  `drive_history_no` BIGINT UNSIGNED NULL,
  `category` ENUM('APP', 'SIGN', 'JOB', 'BT', 'OBD', 'ETC') CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NOT NULL DEFAULT 'ETC' COMMENT 'APP : INSTALL / UNINSTALL / UPDATE / START / FINISH / SERVICE START / SERVICE STOP / SERVICE DESTROY\nSIGN : UP / IN / OUT / FAIL\nJOB : SENSING START,STOP / DETECTING START,STOP  / POSTING START,STOP\nBT : ON / OFF / SCAN / CONNECT / DISCONNECT\nOBD : CONNECT(comm-link) / DISCONNECT(comm-link) / INFO / FOTA START / FOTA FINISH / FOTA FAIL',
  `activity` VARCHAR(512) NULL,
  `activity_time` DATETIME NULL,
  `regdate` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`activity_log_no`),
  INDEX `user_no_idx` (`user_no` ASC),
  INDEX `category_idx` (`category` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `user_invitation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user_invitation` ;

CREATE TABLE IF NOT EXISTS `user_invitation` (
  `user_invitation_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '사용자 초대 고유번호(PK)',
  `invitation_code` VARCHAR(45) NULL COMMENT '초대 고유코드',
  `name` VARCHAR(45) NULL COMMENT '이름',
  `hp_no` VARCHAR(45) NULL COMMENT '핸드폰 번호',
  `user_no` INT UNSIGNED NULL COMMENT '가입한 사용자 번호',
  `partner_no` INT UNSIGNED NULL COMMENT '제휴사 번호',
  `role` ENUM('ADMIN', 'PARTNER_MECHANIC', 'DRIVER', 'ADMIN_ANALYST') NOT NULL DEFAULT 'DRIVER' COMMENT '권한 - 1:ADMIN(관리자), 2:PARTNER_MECHANIC(고객사의 정비사), 3: 운전자(DRIVER), 4: 본사분석가(ADMIN_ANALYST)',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `sms_provider` ENUM('DIRECTSEND') NULL COMMENT '문자발송업체',
  `sms_response` VARCHAR(45) NULL COMMENT '응답코드(200: 정상, 기타: 전송실패)',
  `sms_status` VARCHAR(45) NULL COMMENT '발송결과 상태코드',
  `reg_user_no` INT UNSIGNED NOT NULL COMMENT '초대한 사용자 번호',
  `reg_date` TIMESTAMP NOT NULL COMMENT '등록일시(초대일시)',
  `signup_date` DATETIME NULL COMMENT '가입일시',
  `del_date` DATETIME NULL COMMENT '삭제일시',
  PRIMARY KEY (`user_invitation_no`),
  UNIQUE INDEX `invitation_code_UNIQUE` (`invitation_code` ASC),
  INDEX `fk_user_invitation_partner1_idx` (`partner_no` ASC),
  INDEX `fk_user_invitation_user1_idx` (`user_no` ASC),
  UNIQUE INDEX `user_user_no_UNIQUE` (`user_no` ASC),
  INDEX `fk_user_invitation_user2_idx` (`reg_user_no` ASC),
  CONSTRAINT `fk_user_invitation_partner1`
    FOREIGN KEY (`partner_no`)
    REFERENCES `partner` (`partner_no`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_user_invitation_user1`
    FOREIGN KEY (`user_no`)
    REFERENCES `user` (`user_no`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_user_invitation_user2`
    FOREIGN KEY (`reg_user_no`)
    REFERENCES `user` (`user_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '신규 사용자 초대';


-- -----------------------------------------------------
-- Table `dtc_history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dtc_history` ;

CREATE TABLE IF NOT EXISTS `dtc_history` (
  `dtc_history_no` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '주행별 DTC 수집 데이터 고유번호',
  `vehicle_no` INT UNSIGNED NOT NULL COMMENT '차량고유번호',
  `obd_device_no` INT UNSIGNED NOT NULL COMMENT 'ODB 단말 고유번호',
  `drive_history_no` BIGINT UNSIGNED NOT NULL COMMENT 'DTC가 발생한 주행이력 고유번호',
  `dtc_issued_time` DATETIME NOT NULL COMMENT 'DTC 발생시각',
  `dtc_deleted_time` DATETIME NULL COMMENT 'DTC 삭제시각',
  `deleted_drive_history_no` BIGINT UNSIGNED NULL COMMENT 'DTC 삭제 감지한 주행이력 고유번호',
  `dtc_code` VARCHAR(10) NOT NULL COMMENT 'DTC 코드',
  `repair_no` INT UNSIGNED NULL COMMENT '점검번호 고유키',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
  `upd_date` DATETIME NULL COMMENT '수정일시',
  PRIMARY KEY (`dtc_history_no`),
  INDEX `vehicle_no_idx` (`vehicle_no` ASC),
  INDEX `obd_device_no_idx` (`obd_device_no` ASC),
  INDEX `drive_history_no_idx` (`drive_history_no` ASC),
  INDEX `fk_dtc_history_repair1_idx` (`repair_no` ASC),
  CONSTRAINT `fk_dtc_history_repair1`
    FOREIGN KEY (`repair_no`)
    REFERENCES `repair` (`repair_no`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = ' DTC history';


-- -----------------------------------------------------
-- Table `meta`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `meta` ;

CREATE TABLE IF NOT EXISTS `meta` (
  `meta_no` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `cogny_android` INT UNSIGNED NULL,
  `cogny_ios` INT UNSIGNED NULL,
  `report_android` INT UNSIGNED NULL,
  `report_ios` INT UNSIGNED NULL,
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`meta_no`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'cogny app meta info';


-- -----------------------------------------------------
-- Table `repair_msg`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `repair_msg` ;

CREATE TABLE IF NOT EXISTS `repair_msg` (
  `repair_msg_no` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `repair_no` INT UNSIGNED NULL COMMENT '정비완료한 repair_no',
  `vehicle_no` INT UNSIGNED NULL,
  `user_no` INT UNSIGNED NOT NULL COMMENT '메세지를 보낸 사용자 번호',
  `msg_type` ENUM('NORMAL', 'EMERGENCY', 'EMPTY') NOT NULL DEFAULT 'EMPTY' COMMENT '메시지 종류\nNORMAL: 일반\nEMERGENCY: 긴급\nEMPTY: 메시지없음',
  `call_type` ENUM('MSG', 'CALL') NOT NULL DEFAULT 'MSG' COMMENT '통화여부\nMSG: 메시지만 보내기\nCALL: 메시지 + 통화하기',
  `status` ENUM('VISIBLE', 'INVISIBLE', 'COMPLETE') NOT NULL DEFAULT 'VISIBLE' COMMENT '메시지 상태\nVISIBLE:노출중\nINVISIBLE: 노출안함\nCOMPLETE:정비완료',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`repair_msg_no`),
  INDEX `fk_repair_msg_repair1_idx` (`repair_no` ASC),
  INDEX `fk_repair_msg_user1_idx` (`user_no` ASC),
  INDEX `vehicle_no_idx` (`vehicle_no` ASC),
  CONSTRAINT `fk_repair_msg_repair1`
    FOREIGN KEY (`repair_no`)
    REFERENCES `repair` (`repair_no`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_repair_msg_user1`
    FOREIGN KEY (`user_no`)
    REFERENCES `user` (`user_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `repair_msg_dtc_history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `repair_msg_dtc_history` ;

CREATE TABLE IF NOT EXISTS `repair_msg_dtc_history` (
  `repair_msg_dtc_history_no` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `repair_msg_no` INT UNSIGNED NOT NULL COMMENT '안내 메시지 고유키 : 조합키',
  `dtc_history_no` BIGINT UNSIGNED NOT NULL COMMENT 'DTC 이력 고유키 : 조합키',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE INDEX `repair_msg_dtc_history_no_UNIQUE` (`repair_msg_dtc_history_no` ASC),
  INDEX `fk_repair_msg_dtc_history_repair_msg1_idx` (`repair_msg_no` ASC),
  INDEX `fk_repair_msg_dtc_history_dtc_history1_idx` (`dtc_history_no` ASC),
  PRIMARY KEY (`repair_msg_no`, `dtc_history_no`),
  CONSTRAINT `fk_repair_msg_dtc_history_repair_msg1`
    FOREIGN KEY (`repair_msg_no`)
    REFERENCES `repair_msg` (`repair_msg_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_repair_msg_dtc_history_dtc_history1`
    FOREIGN KEY (`dtc_history_no`)
    REFERENCES `dtc_history` (`dtc_history_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `diagnosis_cate_code`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `diagnosis_cate_code` ;

CREATE TABLE IF NOT EXISTS `diagnosis_cate_code` (
  `diagnosis_cate_code_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '카테고리 이름 고유번호',
  `cate_level` TINYINT UNSIGNED NOT NULL COMMENT '카테고리 분류 단계',
  `name` VARCHAR(45) NULL COMMENT '카테고리 이름',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`diagnosis_cate_code_no`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '카테고리 코드 마스터';


-- -----------------------------------------------------
-- Table `diagnosis_cate`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `diagnosis_cate` ;

CREATE TABLE IF NOT EXISTS `diagnosis_cate` (
  `diagnosis_cate_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '차량진단 분류 고유번호(PK)',
  `diagnosis_cate_code_no_1` INT UNSIGNED NOT NULL COMMENT '카테고리 대분류(fk)',
  `diagnosis_cate_code_no_2` INT UNSIGNED NULL COMMENT '카테고리 중분류(fk)',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`diagnosis_cate_no`),
  INDEX `fk_diagnosis_cate_diagnosis_cate_code1_idx` (`diagnosis_cate_code_no_1` ASC),
  INDEX `fk_diagnosis_cate_diagnosis_cate_code2_idx` (`diagnosis_cate_code_no_2` ASC),
  CONSTRAINT `fk_diagnosis_cate_diagnosis_cate_code1`
    FOREIGN KEY (`diagnosis_cate_code_no_1`)
    REFERENCES `diagnosis_cate_code` (`diagnosis_cate_code_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_diagnosis_cate_diagnosis_cate_code2`
    FOREIGN KEY (`diagnosis_cate_code_no_2`)
    REFERENCES `diagnosis_cate_code` (`diagnosis_cate_code_no`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '차량진단 분류';


-- -----------------------------------------------------
-- Table `diagnosis_item`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `diagnosis_item` ;

CREATE TABLE IF NOT EXISTS `diagnosis_item` (
  `diagnosis_item_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '진단항목 고유번호(pk)',
  `diagnosis_cate_no` INT UNSIGNED NOT NULL COMMENT '진단카테고리 고유번호(fk)',
  `diagnosis_code` CHAR(6) NOT NULL COMMENT '코그니 진단 코드(uq)',
  `name` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL COMMENT '진단항목 이름',
  `service_msg` VARCHAR(1000) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL COMMENT '안내 문구',
  `memo` VARCHAR(1000) CHARACTER SET 'utf8mb4' NULL COMMENT '메모',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  PRIMARY KEY (`diagnosis_item_no`),
  INDEX `fk_diagnosis_item_diagnosis_cate1_idx` (`diagnosis_cate_no` ASC),
  UNIQUE INDEX `code_UNIQUE` (`diagnosis_code` ASC),
  CONSTRAINT `fk_diagnosis_item_diagnosis_cate1`
    FOREIGN KEY (`diagnosis_cate_no`)
    REFERENCES `diagnosis_cate` (`diagnosis_cate_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = '진단항목';


-- -----------------------------------------------------
-- Table `diagnosis_history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `diagnosis_history` ;

CREATE TABLE IF NOT EXISTS `diagnosis_history` (
  `diagnosis_history_no` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '진단 수행 이력 고유번호(pk)',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '진단 시작 일시',
  `end_date` DATETIME NULL DEFAULT NULL COMMENT '진단 종료 일시',
  `recent_diagnosis_date` DATETIME NULL COMMENT '최근 진단 시도 일시',
  `diagnosis_type` ENUM('BATCH', 'MANUAL') NULL COMMENT '진단 수행 구분 - BATCH(배치수행), MANUAL(수동수행)',
  `drive_history_cnt` INT NULL COMMENT '분석한 운행이력 개수',
  PRIMARY KEY (`diagnosis_history_no`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '진단 수행 이력';


-- -----------------------------------------------------
-- Table `diagnosis`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `diagnosis` ;

CREATE TABLE IF NOT EXISTS `diagnosis` (
  `diagnosis_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '차량진단 고유번호(pk)',
  `diagnosis_history_no` BIGINT UNSIGNED NOT NULL COMMENT '진단 수행 시점의 진단 이력 번호',
  `vehicle_no` INT UNSIGNED NOT NULL COMMENT '차량번호(fk)',
  `drive_history_no` BIGINT UNSIGNED NOT NULL COMMENT '차량운행고유번호(fk)',
  `diagnosis_item_no` INT UNSIGNED NOT NULL COMMENT '진단항목번호(fk)',
  `diagnosis_result` ENUM('CAUTION', 'FATAL', 'NOT_AVAILABLE', 'NORMAL') NOT NULL DEFAULT 'CAUTION' COMMENT '진단결과 - CAUTION(주의), FATAL(경고), NOT_AVAILABLE(미지원차종), NORMAL(정상)',
  `diagnosis_msg` VARCHAR(64) NULL COMMENT '진단결과 안내 메시지',
  `repair_no` INT UNSIGNED NULL COMMENT '차량 수리 번호',
  `reg_date` TIMESTAMP NULL COMMENT '등록일시',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일시',
  PRIMARY KEY (`diagnosis_no`),
  INDEX `fk_diagnosis_vehicle1_idx` (`vehicle_no` ASC),
  INDEX `fk_diagnosis_drive_history1_idx` (`drive_history_no` ASC),
  INDEX `fk_diagnosis_repair1_idx` (`repair_no` ASC),
  INDEX `fk_diagnosis_diagnosis_history1_idx` (`diagnosis_history_no` ASC),
  UNIQUE INDEX `drive_history_no_diagnosis_item_no_UNIQUE` (`drive_history_no` ASC, `diagnosis_item_no` ASC),
  INDEX `fk_diagnosis_diagnosis_item1_idx` (`diagnosis_item_no` ASC),
  CONSTRAINT `fk_diagnosis_vehicle1`
    FOREIGN KEY (`vehicle_no`)
    REFERENCES `vehicle` (`vehicle_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_diagnosis_drive_history1`
    FOREIGN KEY (`drive_history_no`)
    REFERENCES `drive_history` (`drive_history_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_diagnosis_repair1`
    FOREIGN KEY (`repair_no`)
    REFERENCES `repair` (`repair_no`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_diagnosis_diagnosis_history1`
    FOREIGN KEY (`diagnosis_history_no`)
    REFERENCES `diagnosis_history` (`diagnosis_history_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_diagnosis_diagnosis_item1`
    FOREIGN KEY (`diagnosis_item_no`)
    REFERENCES `diagnosis_item` (`diagnosis_item_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '차량 진단';


-- -----------------------------------------------------
-- Table `repair_msg_diagnosis`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `repair_msg_diagnosis` ;

CREATE TABLE IF NOT EXISTS `repair_msg_diagnosis` (
  `repair_msg_diagnosis_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '안내메시지, 진단 관계',
  `repair_msg_no` INT UNSIGNED NOT NULL COMMENT '안내 메시지 고유번호(fk)',
  `diagnosis_no` INT UNSIGNED NOT NULL COMMENT '차량진단 고유번호(fk)',
  `reg_date` TIMESTAMP NULL COMMENT '등록일시',
  PRIMARY KEY (`repair_msg_diagnosis_no`),
  INDEX `fk_repair_msg_diagnosis_repair_msg1_idx` (`repair_msg_no` ASC),
  INDEX `fk_repair_msg_diagnosis_diagnosis1_idx` (`diagnosis_no` ASC),
  CONSTRAINT `fk_repair_msg_diagnosis_repair_msg1`
    FOREIGN KEY (`repair_msg_no`)
    REFERENCES `repair_msg` (`repair_msg_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_repair_msg_diagnosis_diagnosis1`
    FOREIGN KEY (`diagnosis_no`)
    REFERENCES `diagnosis` (`diagnosis_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci
COMMENT = '안내메시지 - 차량 진단 관계테이블';


-- -----------------------------------------------------
-- Table `diagnosis_drive_history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `diagnosis_drive_history` ;

CREATE TABLE IF NOT EXISTS `diagnosis_drive_history` (
  `drive_history_no` BIGINT UNSIGNED NOT NULL COMMENT '운행 이력 고유 번호(PK)',
  `last_diagnosis_history_no` BIGINT UNSIGNED NULL COMMENT '마지막 진단 수행 시점의 진단이력고유번호(fk)',
  `success_diagnosis_date` DATETIME NULL COMMENT '마지막 진단 수행 시점의 성공일시',
  `fail_diagnosis_date` DATETIME NULL COMMENT '마지막 진단 수행 시점의 실패일시',
  `final_diagnosis_date` DATETIME NULL COMMENT '운행 종료 후 마지막 버전의 진단 수행 일시',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `upd_date` DATETIME NULL DEFAULT NULL COMMENT '수정일',
  INDEX `fk_diagnosis_drive_history_drive_history1_idx` (`drive_history_no` ASC),
  PRIMARY KEY (`drive_history_no`),
  INDEX `fk_diagnosis_drive_history_diagnosis_history1_idx` (`last_diagnosis_history_no` ASC),
  CONSTRAINT `fk_diagnosis_drive_history_drive_history1`
    FOREIGN KEY (`drive_history_no`)
    REFERENCES `drive_history` (`drive_history_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_diagnosis_drive_history_diagnosis_history1`
    FOREIGN KEY (`last_diagnosis_history_no`)
    REFERENCES `diagnosis_history` (`diagnosis_history_no`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '진단 대상 운행이력';


-- -----------------------------------------------------
-- Table `diagnosis_criteria`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `diagnosis_criteria` ;

CREATE TABLE IF NOT EXISTS `diagnosis_criteria` (
  `diagnosis_criteria_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '진단 기준 고유번호(pk)',
  `sensor_type` ENUM('LPG_FUEL_RAIL_PA', 'INJECTION_TIME', 'OXY_VOLT_S2_DENSE', 'OXY_VOLT_S2_SPARSE', 'TIRE_PRESSURE', 'COOLANT_TEMP', 'BATTERY_VOLT_GNT') NOT NULL COMMENT '진단 기준 구분-LPG_FUEL_RAIL_PA(lpg연료레일압력),INJECTION_TIME(연료분사시간),OXY_VOLT_S2_DENSE(산소센서S2농후),OXY_VOLT_S2_SPARSE(산소센서S2희박),TIRE_PRESSURE(타이어압력),COOLANT_TEMP(냉각수온),BATTERY_VOLT_GNT(배터리전압)',
  `spd_cutoff` INT NULL COMMENT '차속 유효값',
  `spd_cnt_cutoff` INT NULL COMMENT '차속 유효값 기준',
  `caution_cutoff` INT NULL COMMENT '주의 진단 기준',
  `caution_cutoff_with_ac` INT NULL COMMENT '주의 진단 기준(ac on인 경우)',
  `caution_cnt_cutoff` INT NULL COMMENT '주의 판정 초과 횟수(초)',
  `fatal_cutoff` INT NULL COMMENT '경고 진단 기준',
  `fatal_cutoff_with_ac` INT NULL COMMENT '경고 진단 기준(ac on인 경우)',
  `fatal_cnt_cutoff` INT NULL COMMENT '경고 판정 초과 횟수(초)',
  `memo` VARCHAR(1000) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL COMMENT '메모',
  `reg_date` TIMESTAMP NULL COMMENT '등록일시',
  PRIMARY KEY (`diagnosis_criteria_no`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '진단 항목의 세부 진단 기준';


-- -----------------------------------------------------
-- Table `diagnosis_item_criteria`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `diagnosis_item_criteria` ;

CREATE TABLE IF NOT EXISTS `diagnosis_item_criteria` (
  `diagnosis_item_criteria_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '진단항목/기준 매칭 고유번호',
  `diagnosis_item_no` INT UNSIGNED NOT NULL COMMENT '진단항목 고유번호(pk)',
  `diagnosis_criteria_no` INT UNSIGNED NOT NULL COMMENT '진단기준 고유번호(pk)',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  INDEX `fk_table1_diagnosis_item1_idx` (`diagnosis_item_no` ASC),
  INDEX `fk_table1_diagnosis_criteria1_idx` (`diagnosis_criteria_no` ASC),
  PRIMARY KEY (`diagnosis_item_no`, `diagnosis_criteria_no`),
  UNIQUE INDEX `diagnosis_item_criteria_no_UNIQUE` (`diagnosis_item_criteria_no` ASC),
  CONSTRAINT `fk_table1_diagnosis_item1`
    FOREIGN KEY (`diagnosis_item_no`)
    REFERENCES `diagnosis_item` (`diagnosis_item_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_table1_diagnosis_criteria1`
    FOREIGN KEY (`diagnosis_criteria_no`)
    REFERENCES `diagnosis_criteria` (`diagnosis_criteria_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '진단 항목별 진단 센서 기준 매칭 테이블';


-- -----------------------------------------------------
-- Table `diagnosis_item_model`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `diagnosis_item_model` ;

CREATE TABLE IF NOT EXISTS `diagnosis_item_model` (
  `diagnosis_item_model_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '진단항목별 모델 매칭 고유번호',
  `model_no` INT UNSIGNED NOT NULL COMMENT '모델번호',
  `fuel_no` INT UNSIGNED NOT NULL COMMENT '연료번호',
  `diagnosis_item_no` INT UNSIGNED NOT NULL COMMENT '진단항목번호',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '사용여부(1:true, 0:false)',
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  `del_date` DATETIME NULL DEFAULT NULL COMMENT '삭제일',
  INDEX `fk_table1_model1_idx` (`model_no` ASC),
  INDEX `fk_table1_fuel1_idx` (`fuel_no` ASC),
  INDEX `fk_table1_diagnosis_item2_idx` (`diagnosis_item_no` ASC),
  PRIMARY KEY (`model_no`, `fuel_no`, `diagnosis_item_no`),
  UNIQUE INDEX `diagnosis_item_model_no_UNIQUE` (`diagnosis_item_model_no` ASC),
  CONSTRAINT `fk_table1_model1`
    FOREIGN KEY (`model_no`)
    REFERENCES `model` (`model_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_table1_fuel1`
    FOREIGN KEY (`fuel_no`)
    REFERENCES `fuel` (`fuel_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_table1_diagnosis_item2`
    FOREIGN KEY (`diagnosis_item_no`)
    REFERENCES `diagnosis_item` (`diagnosis_item_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '모델/연료타입 별 진단항목';


-- -----------------------------------------------------
-- Table `diagnosis_log`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `diagnosis_log` ;

CREATE TABLE IF NOT EXISTS `diagnosis_log` (
  `diagnosis_log_no` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '진단 결과 로그 번호',
  `diagnosis_history_no` BIGINT UNSIGNED NOT NULL COMMENT '진단이력번호(fk)',
  `vehicle_no` INT UNSIGNED NOT NULL COMMENT '차량번호(fk)',
  `drive_history_no` BIGINT UNSIGNED NOT NULL COMMENT '운행이력번호(fk)',
  `sensing_raw_size` MEDIUMINT NULL COMMENT 'sensing_raw 레코드 수',
  `stable_sec_before` SMALLINT NULL COMMENT '공회전 또는 AC OFF 이후 안정화 기준 시간',
  `stable_sec_after` SMALLINT NULL COMMENT '최대 데이터 delay 발생 가능 시각 (6초 = 5초  + 1초) ',
  `stable_idle_cnt` MEDIUMINT NULL COMMENT '유효 공회전 횟수(stable_sec_befor와 stable_sec_after 기준을 만족하는 공회전)',
  `finished_drive` TINYINT NULL COMMENT '운행종료 여부',
  `diagnosis_item_no` INT UNSIGNED NOT NULL COMMENT '진단항목번호(fk)',
  `diagnosis_code` CHAR(6) NULL COMMENT '코그니 진단 코드',
  `diagnosis_result` ENUM('NORMAL', 'CAUTION', 'FATAL', 'NOT_AVAILABLE', 'UNCERTAIN') NULL COMMENT '진단결과 -NORMAL(정상), CAUTION(주의), FATAL(경고), NOT_AVAILABLE(미지원차종), UNCERTAIN(진단데이터부족)',
  `diagnosis_criteria_no` INT UNSIGNED NULL COMMENT '진단기준번호(fk)',
  `sensor_type` ENUM('LPG_FUEL_RAIL_PA', 'INJECTION_TIME', 'OXY_VOLT_S2_DENSE', 'OXY_VOLT_S2_SPARSE', 'TIRE_PRESSURE', 'COOLANT_TEMP', 'BATTERY_VOLT_GNT') NULL COMMENT '진단 기준 구분-LPG_FUEL_RAIL_PA(lpg연료레일압력),INJECTION_TIME(연료분사시간),OXY_VOLT_S2_DENSE(산소센서S2농후),OXY_VOLT_S2_SPARSE(산소센서S2희박),TIRE_PRESSURE(타이어압력),COOLANT_TEMP(냉각수온),BATTERY_VOLT_GNT(배터리전압)',
  `valid_cnt` MEDIUMINT NULL COMMENT '진단 유효값 기준을 만족하는 회수',
  `spd_cutoff` MEDIUMINT NULL COMMENT '진단 유효값 기준 차량속도',
  `spd_cnt_cutoff` MEDIUMINT NULL COMMENT '진단 유효 차량 속도 개수 기준',
  `spd_cnt` MEDIUMINT NULL COMMENT '진단 유효 차량 속도 개수',
  `caution_cutoff` MEDIUMINT NULL COMMENT '주의 진단 기준',
  `caution_cutoff_with_ac` MEDIUMINT NULL COMMENT '주의 진단 기준(AC ON인 경우)',
  `caution_cnt` MEDIUMINT NULL COMMENT '주의 판정 초과 횟수(초)',
  `caution_cnt_cutoff` MEDIUMINT NULL,
  `fatal_cutoff` MEDIUMINT NULL COMMENT '경고 진단 기준',
  `fatal_cutoff_with_ac` MEDIUMINT NULL COMMENT '경고 진단 기준(AC ON인 경우)',
  `fatal_cnt` MEDIUMINT NULL COMMENT '경고 판정 초과 횟수(초)',
  `fatal_cnt_cutoff` MEDIUMINT NULL COMMENT '경고 판정 초과 횟수 기준',
  `reg_date` TIMESTAMP NULL COMMENT '등록일시',
  PRIMARY KEY (`diagnosis_log_no`),
  INDEX `fk_diagnosis_log_diagnosis_history1_idx` (`diagnosis_history_no` ASC),
  INDEX `fk_diagnosis_log_vehicle1_idx` (`vehicle_no` ASC),
  INDEX `fk_diagnosis_log_drive_history1_idx` (`drive_history_no` ASC),
  INDEX `fk_diagnosis_log_diagnosis_item1_idx` (`diagnosis_item_no` ASC),
  INDEX `fk_diagnosis_log_diagnosis_criteria1_idx` (`diagnosis_criteria_no` ASC),
  CONSTRAINT `fk_diagnosis_log_diagnosis_history1`
    FOREIGN KEY (`diagnosis_history_no`)
    REFERENCES `diagnosis_history` (`diagnosis_history_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_diagnosis_log_vehicle1`
    FOREIGN KEY (`vehicle_no`)
    REFERENCES `vehicle` (`vehicle_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_diagnosis_log_drive_history1`
    FOREIGN KEY (`drive_history_no`)
    REFERENCES `drive_history` (`drive_history_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_diagnosis_log_diagnosis_item1`
    FOREIGN KEY (`diagnosis_item_no`)
    REFERENCES `diagnosis_item` (`diagnosis_item_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_diagnosis_log_diagnosis_criteria1`
    FOREIGN KEY (`diagnosis_criteria_no`)
    REFERENCES `diagnosis_criteria` (`diagnosis_criteria_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COMMENT = '진단 결과 로그';


-- -----------------------------------------------------
-- Table `perform_history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `perform_history` ;

CREATE TABLE IF NOT EXISTS `perform_history` (
  `perform_history_no` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NULL,
  `body` TEXT NULL,
  `vehicle_no` INT UNSIGNED NOT NULL DEFAULT 0,
  `ref` ENUM('DTC', 'REP', 'REPM', 'DIAG') NOT NULL COMMENT 'DTC : dtc_history\nREP : repair\nREPM : repair_msg\nDIAG : diagnosis',
  `ref_no` BIGINT UNSIGNED NOT NULL DEFAULT 0,
  `date_idx` INT UNSIGNED NOT NULL DEFAULT 0,
  `issued_time` DATETIME NULL DEFAULT 발생시각(DTC, DIAG인 경우),
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`perform_history_no`),
  INDEX `vehicle_no` (`vehicle_no` ASC),
  INDEX `date_idx` (`date_idx` DESC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `drive_repair_log`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `drive_repair_log` ;

CREATE TABLE IF NOT EXISTS `drive_repair_log` (
  `drive_repair_log_no` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `vehicle_no` INT UNSIGNED NOT NULL,
  `user_no` INT NOT NULL,
  `drive_time` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '운행시간, 단위(초)',
  `drive_mileage` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '운행거리, 단위(*10 km)',
  `total_mileage` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '총 주행거리, 단위(*10 km)',
  `ref` ENUM('DRIVE', 'REP', 'REPM') NOT NULL DEFAULT 'DRIVE',
  `ref_no` BIGINT UNSIGNED NOT NULL,
  `date_idx` INT UNSIGNED NOT NULL DEFAULT 0,
  `reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `upd_date` DATETIME NULL,
  PRIMARY KEY (`drive_repair_log_no`),
  INDEX `vehicle_no` (`vehicle_no` ASC),
  INDEX `date_idx` (`date_idx` DESC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '운전자 차량점검이력 테이블';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
