CREATE TABLE IF NOT EXISTS m_calendar
(
   seq_id INT (11) NOT NULL,
   client_id INT (11) NOT NULL,
   employee_id INT (11),
   year_month DATE NOT NULL,
   monthly_holidays INT (2),
   monthly_prescribed_days INT (2),
   comment VARCHAR (100),
   delete_flg boolean,
   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   created_user VARCHAR (16),
   updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   updated_user VARCHAR (16)
);
CREATE TABLE IF NOT EXISTS m_client
(
   `client_id` INT (11) NOT NULL,
   `client_name` VARCHAR (50) NOT NULL,
   `open_time` TIME NOT NULL,
   `close_time` TIME NOT NULL,
   `working_time` DECIMAL
   (
      5,
      2
   )
   NULL DEFAULT NULL,
   `rest1_start` TIME NOT NULL,
   `rest1_end` TIME NOT NULL,
   `rest2_start` TIME NULL DEFAULT NULL,
   `rest2_end` TIME NULL DEFAULT NULL,
   `rest3_start` TIME NULL DEFAULT NULL,
   `rest3_end` TIME NULL DEFAULT NULL,
   `rest4_start` TIME NULL DEFAULT NULL,
   `rest4_end` TIME NULL DEFAULT NULL,
   `rest5_start` TIME NULL DEFAULT NULL,
   `rest5_end` TIME NULL DEFAULT NULL,
   `rest6_start` TIME NULL DEFAULT NULL,
   `rest6_end` TIME NULL DEFAULT NULL,
   `adjust_rest_time_start` TIME NULL DEFAULT NULL,
   `adjust_rest_time_end` TIME NULL DEFAULT NULL,
   `comment` VARCHAR (100) NULL DEFAULT NULL,
   `delete_flg` boolean NULL DEFAULT NULL,
   `created_at` DATETIME NULL DEFAULT NULL,
   `created_user` VARCHAR (16) NULL DEFAULT NULL,
   `updated_at` DATETIME NULL DEFAULT NULL,
   `updated_user` VARCHAR (16) NULL DEFAULT NULL
);
CREATE TABLE IF NOT EXISTS m_employee
(
   `employee_id` INT (11) NOT NULL,
   `employee_name` VARCHAR (16) NOT NULL,
   `client_id` INT (11) NOT NULL,
   `hourly_wage` BIT (1) NULL DEFAULT NULL,
   `paid_holiday_std` DATE NULL DEFAULT NULL,
   `delete_flg` boolean NULL DEFAULT NULL,
   `created_at` DATETIME NULL DEFAULT NULL,
   `created_user` VARCHAR (16) NULL DEFAULT NULL,
   `updated_at` DATETIME NULL DEFAULT NULL,
   `updated_user` VARCHAR (16) NULL DEFAULT NULL
);
CREATE TABLE IF NOT EXISTS m_user
(
   `seq_id` INT (11) NOT NULL,
   `user_id` varchar (16) NOT NULL,
   `user_name` varchar (16) NOT NULL,
   `password` varchar (64) NOT NULL,
   `auth_id` int (11) NOT NULL,
   `mail_address` varchar (254) NOT NULL,
   `delete_flg` bit (1) NULL DEFAULT NULL,
   `created_at` datetime NULL DEFAULT NULL,
   `created_user` varchar (16) NULL DEFAULT NULL,
   `updated_at` datetime NULL DEFAULT NULL,
   `updated_user` varchar (16) NULL DEFAULT NULL
);

CREATE TABLE `m_employee_paid_vacation` (
  `seq_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'シーケンスID',
  `employee_id` int(4) NOT NULL COMMENT '社員ID',
  `yearA` date DEFAULT NULL COMMENT '年度',
  `remaind_this_year` decimal(10,0) DEFAULT NULL COMMENT '有給保有数-当年度分',
  `remaind_last_year` decimal(10,0) DEFAULT NULL COMMENT '有給保有数-前年度分',
  `delete_flg` bit(1) DEFAULT NULL COMMENT '削除フラグ',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'レコード作成日付',
  `created_user` varchar(11) DEFAULT NULL COMMENT 'レコード作成ユーザID',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'レコード最終日付',
  `updated_user` varchar(11) DEFAULT NULL COMMENT 'レコード最終更新ユーザID',
  PRIMARY KEY (`seq_id`),
  KEY `fk_employee_id` (`employee_id`),
  CONSTRAINT `fk_employee_id` FOREIGN KEY (`employee_id`) REFERENCES `m_employee` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='有給休暇マスタ';

CREATE TABLE `t_work_leave_application` (
  `seq_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'シーケンスID',
  `employee_id` int(4) NOT NULL COMMENT '社員ID',
  `holiday_date` date DEFAULT NULL COMMENT '取得日',
  `application_date` date DEFAULT NULL COMMENT '申請日',
  `application_class` int(11) DEFAULT NULL COMMENT '申請種別',
  `day_off_date` date DEFAULT NULL COMMENT '休出日',
  `day_off_time` decimal(10,0) DEFAULT NULL COMMENT '休出時間',
  `start_end_time` time DEFAULT NULL COMMENT '出退勤時刻',
  `reason` varchar(1000) DEFAULT NULL COMMENT '理由',
  `remarks` varchar(1000) DEFAULT NULL COMMENT '備考',
  `delete_flg` bit(1) DEFAULT NULL COMMENT '削除フラグ',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'レコード作成日付',
  `created_user` varchar(16) DEFAULT NULL COMMENT 'レコード作成ユーザーID',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'レコード最終日付',
  `updated_user` varchar(16) DEFAULT NULL COMMENT 'レコード最終更新ユーザーID',
  PRIMARY KEY (`seq_id`),
  KEY `fk2_employee_id` (`employee_id`),
  CONSTRAINT `fk2_employee_id` FOREIGN KEY (`employee_id`) REFERENCES `m_employee` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='休暇申請';

CREATE TABLE `m_calendar_detail` (
  `seq_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'シーケンスID',
  `parent_seq_id` int(11) NOT NULL COMMENT '親シーケンスID',
  `date` date NOT NULL COMMENT '日付',
  `holiday_flg` bit(1) DEFAULT NULL COMMENT '休日フラグ',
  `comment` varchar(100) DEFAULT NULL COMMENT 'コメント',
  `delete_flg` bit(1) DEFAULT NULL COMMENT '削除フラグ',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'レコード作成日付',
  `created_user` varchar(16) DEFAULT NULL COMMENT 'レコード作成ユーザID',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'レコード最終更新日付',
  `updated_user` varchar(16) DEFAULT NULL COMMENT 'レコード最終更新ユーザID',
  PRIMARY KEY (`seq_id`),
  KEY `parent_seq_id` (`parent_seq_id`),
  CONSTRAINT `m_calendar_detail_ibfk_1` FOREIGN KEY (`parent_seq_id`) REFERENCES `m_calendar` (`seq_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='カレンダーマスタ詳細';

CREATE TABLE `m_authority` (
  `auth_id` int(11) NOT NULL ,
  `auth_status` varchar(10) NOT NULL
);