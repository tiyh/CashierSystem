#drop table if exists student;
create table order (
  id integer primary key not null,
  numbers varchar(14) not null,
  time varchar(16)
);
#1.公司银行表
CREATE TABLE OPP_ORG_BANK ( 
    ORG_CODE    VARCHAR(30) NOT NULL,
    BANK_ID     VARCHAR(30) NOT NULL,
    BANK_NAME   VARCHAR(30),
    BANK_TYPE   CHARACTER(1),
    SEQ         DECIMAL(22,0),
    IS_MRB      CHARACTER(1),
    NOTE        VARCHAR(40),
    BANK_ICO    VARCHAR(30) DEFAULT '',
    PAY_TYPE    CHARACTER(2),
    RATE        CHARACTER(1),
    ORG_NAME    VARCHAR(50),
    PRIMARY KEY(ORG_CODE,BANK_ID)
);

#2.交易日志
CREATE TABLE OPP_PAY_LOG ( 
    LOG_ID          VARCHAR(30) NOT NULL,
    ORG_CODE        VARCHAR(30) NOT NULL,
    PAY_SEQ         VARCHAR(30),
    BANK_ID         VARCHAR(30),
    BANK_NAME       VARCHAR(30),
    MERCHANT_ID     VARCHAR(30),
    BRANCH_ID       VARCHAR(16),
    POS_ID          VARCHAR(32),
    BATCH_NO        VARCHAR(30),
    VOUCHER_NO      VARCHAR(60),
    PAY_DATE        CHARACTER(8),
    PAY_TIME        CHARACTER(8),
    PAY_TYPE        CHARACTER(2),
    PAY_FLAG        CHARACTER(1),
    CUST_CODE       VARCHAR(30),
    CO_NUM          VARCHAR(30) NOT NULL,
    AMOUNT          DECIMAL(18,6),
    NOTE            VARCHAR(128),
    USER_TYPE       CHARACTER(2) DEFAULT '01',
    PAY_CO_NUM      VARCHAR(32) NOT NULL,
    PRIMARY KEY(ORG_CODE,LOG_ID)
);


#3.交互日志
CREATE TABLE opp_PAY_EXCHANGE_LOG ( 
    ORG_CODE        VARCHAR(30) NOT NULL,
    LOG_ID          VARCHAR(30) NOT NULL,
    SYS_FROM        VARCHAR(30),
    SYS_TO          VARCHAR(30),
    EXCHANGE_DATE   VARCHAR(8),
    EXCHANGE_TIME   VARCHAR(8),
    EXCHANGE_TYPE   VARCHAR(30),
    CUST_CODE       VARCHAR(30),
    CO_NUM          VARCHAR(30) NOT NULL,
    AMOUNT          DECIMAL(18,6),
    NOTE            VARCHAR(5000),
    PRIMARY KEY(ORG_CODE,LOG_ID)
);


#4.网上支付银行表
#--易付银行表
CREATE TABLE OPP_ORG_EPAY ( 
    ORG_CODE                VARCHAR(30) NOT NULL,
    BANK_ID                 VARCHAR(30) NOT NULL,
    BANK_NAME               VARCHAR(30),
    MERCHANT_ID             VARCHAR(30) NOT NULL,
    ACQUIRING_BANK          VARCHAR(30),
    KEY_FIELD               VARCHAR(200),
    PAY_URL                 VARCHAR(120),
    QUERY_URL               VARCHAR(120),
    REGISTER_URL            VARCHAR(120),
    NEW_EPAY_ORDER_URL      VARCHAR(120),
    PHONE_MSG_URL           VARCHAR(120),
    QUERY_BIND_CARD_URL     VARCHAR(120),
    QUERY_IS_BIND_URL       VARCHAR(120),
    REFUND_URL              VARCHAR(120),
    REFUND_QUERY_URL        VARCHAR(120),
    BIND_PAY_URL            VARCHAR(120),
    UNBIND_URL              VARCHAR(120),
    BIND_SMS_URL            VARCHAR(120),
    VERIFY_SMS_URL          VARCHAR(120),
    VERSIONS                VARCHAR(12),
    PHONE_MSG_VALID_TIME    CHARACTER(2),
    PRIVATE_KEY_STORE_PATH  VARCHAR(120),
    PUBLIC_KEY_STORE_PATH   VARCHAR(120),
    KEY_PASSWORD            VARCHAR(20),
    FTP_QUERY_URL           VARCHAR(120),
    FTP_DOWNLOAD_URL        VARCHAR(120),
    FTP_PORT                VARCHAR(10),
    FTP_USERNAME            VARCHAR(30),
    FTP_PASSWORD            VARCHAR(30),
    FTP_REMOTEPATH          VARCHAR(120),
    FTP_LOCALPATH           VARCHAR(120),
    NOTE                    VARCHAR(100),
    PRIMARY KEY(ORG_CODE,BANK_ID,MERCHANT_ID)
)
