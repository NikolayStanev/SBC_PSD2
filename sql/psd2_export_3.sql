create sequence UBXPSD2.AUDIT_LOG_SEQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 224
increment by 1
cache 20;


create sequence UBXPSD2.CONSENT_SEQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 61
increment by 1
cache 20;


create sequence UBXPSD2.CREDIT_TRANS_SEQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 21
increment by 1
cache 20;


create sequence UBXPSD2.PSD2_STATISTICS_SEQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 21
increment by 1
cache 20;

create sequence UBXPSD2.ACCOUNT_SEQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 41
increment by 1
cache 20;

create table UBXPSD2.CONSENT
(
  id                         NUMBER default "UBXPSD2"."CONSENT_SEQ"."NEXTVAL" not null,
  x_request_id               VARCHAR2(255) not null,
  psu_id                     VARCHAR2(255),
  recurring_indicator        CHAR(1) not null,
  valid_until                TIMESTAMP(6) not null,
  frequency_per_day          NUMBER not null,
  combined_service_indicator CHAR(1),
  consent_id                 VARCHAR2(255) not null,
  consent_status             VARCHAR2(20) not null,
  creation_date              TIMESTAMP(6) default SYSTIMESTAMP not null,
  tpp_id                     VARCHAR2(255) not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on column UBXPSD2.CONSENT.recurring_indicator
  is 'Y, N';
comment on column UBXPSD2.CONSENT.frequency_per_day
  is '-1 - unlimited';
comment on column UBXPSD2.CONSENT.combined_service_indicator
  is 'Y, N';
comment on column UBXPSD2.CONSENT.tpp_id
  is '<test>';
create unique index UBXPSD2.IND_CONSENT_CONSENT_ID on UBXPSD2.CONSENT (CONSENT_ID)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table UBXPSD2.CONSENT
  add constraint PK_CONSENT primary key (ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );


create table UBXPSD2.ACCOUNT
(
  id           NUMBER default "UBXPSD2"."ACCOUNT_SEQ"."NEXTVAL" not null,
  iban         VARCHAR2(100) not null,
  account_id   VARCHAR2(100) not null,
  transactions CHAR(1) not null,
  balances     CHAR(1) not null,
  consent_id   NUMBER not null,
  currency     VARCHAR2(100) not null,
  try          NUMBER default 0,
  cur_date     DATE default SYSDATE,
  account_info CHAR(1) not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on column UBXPSD2.ACCOUNT.transactions
  is 'Y, N';
comment on column UBXPSD2.ACCOUNT.balances
  is 'Y, N';
comment on column UBXPSD2.ACCOUNT.account_info
  is 'Y,N';
create index UBXPSD2.IND_ACC_CONSENT_ID on UBXPSD2.ACCOUNT (CONSENT_ID)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
create unique index UBXPSD2.UNQ_ACC_ACC_ID on UBXPSD2.ACCOUNT (ACCOUNT_ID)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table UBXPSD2.ACCOUNT
  add constraint PK_ACCOUNT primary key (ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table UBXPSD2.ACCOUNT
  add constraint FK_ACC_CONSENT foreign key (CONSENT_ID)
  references UBXPSD2.CONSENT (ID) on delete cascade;


create table UBXPSD2.AUDIT_LOG
(
  id                NUMBER default "UBXPSD2"."AUDIT_LOG_SEQ"."NEXTVAL" not null,
  op                NVARCHAR2(2000) not null,
  op_timestamp      TIMESTAMP(6) default SYSTIMESTAMP not null,
  request           CLOB,
  response          CLOB,
  ip                NVARCHAR2(50) not null,
  psu_id            NVARCHAR2(255),
  x_request_id      NVARCHAR2(255),
  op_type           NVARCHAR2(10),
  response_time     TIMESTAMP(6),
  op_time_millis    NUMBER,
  is_internal_error CHAR(1) not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on column UBXPSD2.AUDIT_LOG.op_type
  is 'PSP_PI, PSP_AI, PSP_IC';
comment on column UBXPSD2.AUDIT_LOG.is_internal_error
  is 'Y, N';


create table UBXPSD2.CREDIT_TRANSFER
(
  id                 NUMBER default "UBXPSD2"."CREDIT_TRANS_SEQ"."NEXTVAL" not null,
  x_request_id       NVARCHAR2(255) not null,
  ip                 NVARCHAR2(20) not null,
  psu_ip_address     NVARCHAR2(255),
  debit_iban         NVARCHAR2(50) not null,
  currency           NVARCHAR2(3) not null,
  amount             NUMBER(15,4) not null,
  credit_iban        NVARCHAR2(50) not null,
  creditor_name      NVARCHAR2(255) not null,
  transaction_status NVARCHAR2(20) not null,
  payment_id         NVARCHAR2(255) not null,
  log_date           TIMESTAMP(6) default CURRENT_TIMESTAMP not null,
  ext_ref_id         NVARCHAR2(255),
  psu_id             NVARCHAR2(255),
  payment_type       NVARCHAR2(255),
  remitance_ref      NVARCHAR2(255),
  consent_id         NVARCHAR2(255),
  tpp_id             NVARCHAR2(255) not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table UBXPSD2.CREDIT_TRANSFER
  add constraint PK_CREDIT_TRANSFER primary key (ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table UBXPSD2.CREDIT_TRANSFER
  add constraint UNQ_CT_XREQID unique (X_REQUEST_ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table UBXPSD2.CREDIT_TRANSFER
  add constraint UNQ_PAYMENT_ID unique (PAYMENT_ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );


create table UBXPSD2.PSD2_STATISTICS
(
  id                          NUMBER default "UBXPSD2"."PSD2_STATISTICS_SEQ"."NEXTVAL" not null,
  record_date                 DATE not null,
  percent_working             NUMBER,
  percent_not_working         NUMBER,
  all_requests                NUMBER,
  all_error_requests          NUMBER,
  as_requests                 NUMBER,
  as_error_requests           NUMBER,
  pi_requests                 NUMBER,
  pi_error_requests           NUMBER,
  ic_requests                 NUMBER,
  ic_error_requests           NUMBER,
  average_time_per_request    NUMBER,
  as_average_time_per_request NUMBER,
  pi_average_time_per_request NUMBER,
  ic_average_time_per_request NUMBER
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
create index UBXPSD2.PSD2_STATISTICS_DATE_I on UBXPSD2.PSD2_STATISTICS (RECORD_DATE)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table UBXPSD2.PSD2_STATISTICS
  add constraint PSD2_STATISTICS_ID_PK primary key (ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );








create or replace package ubxpsd2.psd2 is

  -- Author  : PAVEL.BONEV
  -- Created : 23.7.2019 ?. 17:15:34
  -- Purpose : 
  
  PROCEDURE insertBGNTrans(
     p_id      OUT INT,
     p_x_request_id    VARCHAR2,
     p_ip     VARCHAR2,
     p_psu_ip_address  VARCHAR2,
     p_debit_iban   VARCHAR2,
     p_currency   VARCHAR2,
     p_amount   DECIMAL,
     p_credit_iban VARCHAR2,
     p_creditor_name VARCHAR2,
     p_transaction_status VARCHAR2,
     p_payment_id VARCHAR2,
     p_psu_id VARCHAR2,
     p_payment_type VARCHAR2,
     p_remitance_ref VARCHAR2,
     p_consent_id VARCHAR2,
     p_tpp_id VARCHAR2);


  PROCEDURE updateBGNTrans(
    p_id      INT,
    p_transaction_status VARCHAR2,
    p_ext_ref_id VARCHAR2);
     
  
  PROCEDURE getBGNTransByPmntID(
     p_payment_id VARCHAR2,
     p_tpp_id VARCHAR2,
     o_cur OUT sys_refcursor);
     
     
     
  PROCEDURE insertConsent(
     p_id      OUT INT,
     p_x_request_id    VARCHAR2,
     p_psu_id VARCHAR2,
     p_recurring_indicator    VARCHAR2,
     p_valid_until  DATE,
     p_frequency_per_day   NUMBER,
     p_combined_service_indicator   VARCHAR2,
     p_consent_id   VARCHAR2,
     p_consent_status VARCHAR2,
     p_tpp_id VARCHAR2);
     
     
  PROCEDURE updateConsentStatus(
     p_id      INT,
     p_consent_status VARCHAR2);
     
  PROCEDURE getConsentByConsentID (
     p_consent_id VARCHAR2,
     p_tpp_id VARCHAR2,
     o_cur OUT sys_refcursor);
     
  PROCEDURE getConsentByID (
     p_id VARCHAR2,
     o_cur OUT sys_refcursor);
     
     
  PROCEDURE insertAccount(
     p_id      OUT INT,
     p_iban    VARCHAR2,
     p_account_id VARCHAR2,
     p_transactions CHAR,
     p_balances  CHAR,
     p_consent_id   VARCHAR2,
     p_currency   VARCHAR2,
     p_try  NUMBER,
     p_current_date DATE,
     p_account_info CHAR);
     
   PROCEDURE getAccountsByConsentID (
     p_consent_id VARCHAR2,
     o_cur OUT sys_refcursor);  
     
   PROCEDURE getAccountByAccID (
     p_account_id VARCHAR2,
     o_cur OUT sys_refcursor);
     
   PROCEDURE getAccount(
     p_account_id VARCHAR2,
     p_consent_id int,
     o_cur OUT sys_refcursor);
     
   PROCEDURE getAccountByIBAN(
     p_iban VARCHAR2,
     p_consent_id int,
     o_cur OUT sys_refcursor);
     
   PROCEDURE updateAccount (
     p_id number,
     p_try number,
     p_current_date date);
     
   PROCEDURE createActivity(
     p_id      OUT INT,
     p_op    VARCHAR2,
     p_request  CLOB,
     p_response   CLOB,
     p_ip   VARCHAR2,
     p_psu_id VARCHAR2,
     p_x_request_id VARCHAR2,
     p_op_type VARCHAR2);
     
     PROCEDURE updateActivity (
     p_id number,
     p_response varchar2,
     p_is_internal_error char);
     
     PROCEDURE getActivity (
      p_op_timestamp timestamp,
      o_cur OUT sys_refcursor);
      
      PROCEDURE createRecord(
       p_id      OUT INT,
       p_record_date    date,
       p_percent_working  number,
       p_percent_not_working   number,
       p_all_requests   number,
       p_all_error_requests number,
       p_as_requests number,
       p_as_error_requests number,
       p_pi_requests number,
       p_pi_error_requests number,
       p_ic_requests number,
       p_ic_error_request number,
       p_average_time_per_request number,
       p_as_average_time_per_request number,
       p_pi_average_time_per_request number,
       p_ic_average_time_per_request number
       );
       
      PROCEDURE deleteConsent (
        p_consent_id          VARCHAR2,
        p_tpp_id              VARCHAR2
        );
     
end psd2;
/


create or replace package body ubxpsd2.psd2 is

  -- =============================================
  -- Author:    Pavel Bonev
  -- Create date: 11.05.2007
  -- Description:  Inserts a new client
  -- =============================================
  PROCEDURE insertBGNTrans(
     p_id      OUT INT,
     p_x_request_id    VARCHAR2,
     p_ip     VARCHAR2,
     p_psu_ip_address  VARCHAR2,
     p_debit_iban   VARCHAR2,
     p_currency   VARCHAR2,
     p_amount   DECIMAL,
     p_credit_iban VARCHAR2,
     p_creditor_name VARCHAR2,
     p_transaction_status VARCHAR2,
     p_payment_id VARCHAR2,
     p_psu_id VARCHAR2,
     p_payment_type VARCHAR2,
     p_remitance_ref VARCHAR2,
     p_consent_id VARCHAR2,
     p_tpp_id VARCHAR2)
  IS
    flagDuplicated int;
  BEGIN
    
    SELECT count(*)
    INTO flagDuplicated
    FROM CREDIT_TRANSFER
    WHERE payment_id = p_payment_id;
    
    IF flagDuplicated != 0 
    THEN
      RAISE_APPLICATION_ERROR (-20260, 'Payment request with this paymentID is already created!');
    END IF;
    
    SELECT count(*)
    INTO flagDuplicated
    FROM CREDIT_TRANSFER
    WHERE x_request_id = p_x_request_id;
    
    IF flagDuplicated != 0 
    THEN
      RAISE_APPLICATION_ERROR (-20261, 'Payment request with this X_REQUEST_ID is already created!');
    END IF;


    INSERT INTO CREDIT_TRANSFER
    (
        X_REQUEST_ID,
        IP,
        PSU_IP_ADDRESS,
        DEBIT_IBAN,
        CURRENCY,
        AMOUNT,
        CREDIT_IBAN,
        CREDITOR_NAME,
        TRANSACTION_STATUS,
        PAYMENT_ID,
        PSU_ID,
        PAYMENT_TYPE, 
        REMITANCE_REF,
        CONSENT_ID,
        TPP_ID
    )
    VALUES
    (
        p_x_request_id,
        p_ip,
        p_psu_ip_address,
        p_debit_iban,
        p_currency,
        p_amount,
        p_credit_iban,
        p_creditor_name,
        p_transaction_status,
        p_payment_id,
        p_psu_id,
        p_payment_type,
        p_remitance_ref,
        p_consent_id,
        p_tpp_id
    )
    RETURNING id 
    INTO p_id;
    
   
  END;


  PROCEDURE updateBGNTrans(
    p_id      INT,
    p_transaction_status VARCHAR2,
    p_ext_ref_id VARCHAR2)
  IS

    BEGIN

      UPDATE CREDIT_TRANSFER
      SET transaction_status = p_transaction_status,
        ext_ref_id = p_ext_ref_id
      WHERE id = p_id;

    END;
  
  
  PROCEDURE getBGNTransByPmntID(
     p_payment_id VARCHAR2,
     p_tpp_id VARCHAR2,
     o_cur OUT sys_refcursor)
  IS
    
  BEGIN
    OPEN o_cur FOR
      SELECT id, x_request_id, ip, psu_ip_address, debit_iban, currency, amount, credit_iban, 
             creditor_name, transaction_status, payment_id, ext_ref_id, log_date, psu_id, 
             payment_type, remitance_ref, consent_id
      FROM	credit_transfer
      WHERE payment_id = p_payment_id and 
            tpp_id = p_tpp_id;
        
    RETURN;
  END;
  
  ------------------------ Consents
  
  PROCEDURE insertConsent(
     p_id      OUT INT,
     p_x_request_id    VARCHAR2,
     p_psu_id VARCHAR2,
     p_recurring_indicator    VARCHAR2,
     p_valid_until  DATE,
     p_frequency_per_day   NUMBER,
     p_combined_service_indicator   VARCHAR2,
     p_consent_id   VARCHAR2,
     p_consent_status VARCHAR2,
     p_tpp_id VARCHAR2
     )
  IS
    flagDuplicated int;
  BEGIN
    
    SELECT count(*)
    INTO flagDuplicated
    FROM CONSENT
    WHERE consent_id = p_consent_id;
    
    IF flagDuplicated != 0 
    THEN
      RAISE_APPLICATION_ERROR (-20270, 'Consent with this consentID is already created!');
    END IF;
    
    SELECT count(*)
    INTO flagDuplicated
    FROM CONSENT
    WHERE x_request_id = p_x_request_id;
    
    IF flagDuplicated != 0 
    THEN
      RAISE_APPLICATION_ERROR (-20271, 'Consent with this X_REQUEST_ID is already created!');
    END IF;


    INSERT INTO CONSENT
    (
        x_request_id,
        PSU_ID,
        recurring_indicator,
        valid_until,
        frequency_per_day,
        combined_service_indicator,
        consent_id,
        consent_status,
        tpp_id
    )
    VALUES
    (
        p_x_request_id,
        p_psu_id,
        p_recurring_indicator,
        p_valid_until,
        p_frequency_per_day,
        p_combined_service_indicator,
        p_consent_id,
        p_consent_status,
        p_tpp_id
    )
    RETURNING id 
    INTO p_id;
    
   
  END;
  
  PROCEDURE updateConsentStatus(
     p_id      INT,
     p_consent_status VARCHAR2)
  IS

  BEGIN

    UPDATE CONSENT
    SET consent_status = p_consent_status
    WHERE id = p_id;    
   
  END;
  
  
  PROCEDURE getConsentByConsentID (
     p_consent_id VARCHAR2,
     p_tpp_id VARCHAR2,
     o_cur OUT sys_refcursor)
  IS
    
  BEGIN
    OPEN o_cur FOR
      SELECT id, 
        x_request_id,
        psu_id,
        recurring_indicator,
        valid_until,
        frequency_per_day,
        combined_service_indicator,
        consent_id,
        consent_status,
        creation_date,
        tpp_id
      FROM	consent
      WHERE consent_id = p_consent_id and 
            tpp_id = p_tpp_id;
         
    RETURN;
  END;
  
  
  PROCEDURE getConsentByID (
     p_id VARCHAR2,
     o_cur OUT sys_refcursor)
  IS
    
  BEGIN
    OPEN o_cur FOR
      SELECT id, 
        x_request_id,
        psu_id,
        recurring_indicator,
        valid_until,
        frequency_per_day,
        combined_service_indicator,
        consent_id,
        consent_status,
        creation_date
      FROM	consent
      WHERE id = p_id;
        
    RETURN;
  END;


  -- consent accounts

  PROCEDURE insertAccount(
     p_id      OUT INT,
     p_iban    VARCHAR2,
     p_account_id VARCHAR2,
     p_transactions CHAR,
     p_balances  CHAR,
     p_consent_id   VARCHAR2,
     p_currency   VARCHAR2,
     p_try  NUMBER,
     p_current_date DATE,
     p_account_info CHAR
     )
  IS
    flagDuplicated int;
  BEGIN
    
    SELECT count(*)
    INTO flagDuplicated
    FROM ACCOUNT
    WHERE account_id = p_account_id;
    
    IF flagDuplicated != 0 
    THEN
      RAISE_APPLICATION_ERROR (-20280, 'Account with this accountID is already created!');
    END IF;
    
   
    INSERT INTO ACCOUNT
    (
       iban,
       account_id,
       transactions,
       balances,
       consent_id,
       currency,
       try,
       cur_date,
       account_info
    )
    VALUES
    (
       p_iban,
       p_account_id,
       p_transactions,
       p_balances,
       p_consent_id,
       p_currency,
       p_try,
       p_current_date,
       p_account_info
    )
    RETURNING id 
    INTO p_id;
    
   
  END;
  
  
  PROCEDURE getAccountsByConsentID (
     p_consent_id VARCHAR2,
     o_cur OUT sys_refcursor)
  IS
    
  BEGIN
    OPEN o_cur FOR
      SELECT id, 
       iban,
       account_id,
       transactions,
       balances,
       consent_id,
       currency,
       try,
       cur_date,
       account_info
      FROM	account
      WHERE consent_id = p_consent_id;
        
    RETURN;
  END;
  
  
  PROCEDURE getAccountByAccID (
     p_account_id VARCHAR2,
     o_cur OUT sys_refcursor)
  IS
    
  BEGIN
    OPEN o_cur FOR
      SELECT id, 
       iban,
       account_id,
       transactions,
       balances,
       consent_id,
       currency,
       try,
       cur_date,
       account_info
      FROM	account
      WHERE account_id = p_account_id;
        
    RETURN;
  END;
  
  PROCEDURE getAccount(
     p_account_id VARCHAR2,
     p_consent_id int,
     o_cur OUT sys_refcursor)
  IS
    
  BEGIN
    OPEN o_cur FOR
      SELECT id, 
       iban,
       account_id,
       transactions,
       balances,
       consent_id,
       currency,
       try,
       cur_date,
       account_info
      FROM	account
      WHERE account_id = p_account_id and 
            consent_id = p_consent_id;
        
    RETURN;
  END;
    
  PROCEDURE getAccountByIBAN(
     p_iban VARCHAR2,
     p_consent_id int,
     o_cur OUT sys_refcursor)
  IS
    
  BEGIN
    OPEN o_cur FOR
      SELECT id, 
       iban,
       account_id,
       transactions,
       balances,
       consent_id,
       currency,
       try,
       cur_date,
       account_info
      FROM	account
      WHERE iban = p_iban and 
            consent_id = p_consent_id;
        
    RETURN;
  END;
  
  
  PROCEDURE updateAccount (
     p_id number,
     p_try number,
     p_current_date date)
  IS
    
  BEGIN
    UPDATE ACCOUNT
    SET try = p_try,
        cur_date = p_current_date
    WHERE id = p_id;    
        
    RETURN;
  END;
  
  -- ACTIVITIES --
  
  
  PROCEDURE createActivity(
     p_id      OUT INT,
     p_op    VARCHAR2,
     p_request  CLOB,
     p_response   CLOB,
     p_ip   VARCHAR2,
     p_psu_id VARCHAR2,
     p_x_request_id VARCHAR2,
     p_op_type VARCHAR2)
   IS
     
   BEGIN
    INSERT INTO AUDIT_LOG
    (
        OP,
        REQUEST,
        RESPONSE,
        IP,
        PSU_ID,
        X_REQUEST_ID,
        OP_TYPE,
        IS_INTERNAL_ERROR
    )
    VALUES
    (
     p_op,
     p_request,
     p_response,
     p_ip,
     p_psu_id,
     p_x_request_id,
     p_op_type,
     'N'
    )
    RETURNING id 
    INTO p_id;
    
   
  END;
  
  PROCEDURE updateActivity (
     p_id number,
     p_response varchar2,
     p_is_internal_error char)
  IS
    -- v_op_time_millis int;
    v_response_time timestamp;
  BEGIN
    v_response_time := SYSTIMESTAMP;
    -- v_op_time_millis := extract(second from v_response_time - doj) * 1000;
    
    UPDATE AUDIT_LOG
    SET response = p_response,
        response_time = v_response_time,
        is_internal_error = p_is_internal_error,
        op_time_millis = extract(second from v_response_time - op_timestamp) * 1000
    WHERE id = p_id;    
        
    RETURN;
  END;
  
  PROCEDURE getActivity (
    p_op_timestamp timestamp,
    o_cur OUT sys_refcursor)
    IS 
    BEGIN
      OPEN o_cur FOR
      SELECT id,
      op,
      op_timestamp,
      op_type,
      response_time,
      op_time_millis,
      is_internal_error
      FROM audit_log
      WHERE to_char(cast(op_timestamp as date),'DD-MM-YYYY') = to_char(cast(p_op_timestamp as date),'DD-MM-YYYY') and
            op_type != 'OP_UNKNOWN'
      ORDER BY op_timestamp ASC;     
      RETURN;
    END;
    
    PROCEDURE createRecord(
     p_id OUT INT,
     p_record_date    date,
     p_percent_working  number,
     p_percent_not_working   number,
     p_all_requests   number,
     p_all_error_requests number,
     p_as_requests number,
     p_as_error_requests number,
     p_pi_requests number,
     p_pi_error_requests number,
     p_ic_requests number,
     p_ic_error_request number,
     p_average_time_per_request number,
     p_as_average_time_per_request number,
     p_pi_average_time_per_request number,
     p_ic_average_time_per_request number
     )
   IS
     
   BEGIN
    INSERT INTO PSD2_STATISTICS
    (
     record_date,
     percent_working,
     percent_not_working,
     all_requests,
     all_error_requests,
     as_requests,
     as_error_requests,
     pi_requests,
     pi_error_requests,
     ic_requests,
     ic_error_requests,
     average_time_per_request,
     as_average_time_per_request,
     pi_average_time_per_request,
     ic_average_time_per_request
    )
    VALUES
    (
     p_record_date,
     p_percent_working,
     p_percent_not_working,
     p_all_requests,
     p_all_error_requests,
     p_as_requests,
     p_as_error_requests,
     p_pi_requests,
     p_pi_error_requests,
     p_ic_requests,
     p_ic_error_request,
     p_average_time_per_request,
     p_as_average_time_per_request,
     p_pi_average_time_per_request,
     p_ic_average_time_per_request
    )
    RETURNING id 
    INTO p_id;
  END;
  
  PROCEDURE deleteConsent (
    p_consent_id          VARCHAR2,
    p_tpp_id              VARCHAR2
    )
    IS
    
    BEGIN
      DELETE FROM CONSENT
      WHERE consent_id = p_consent_id and
            tpp_id = p_tpp_id;
    END;
  
begin
  null;
end psd2;
/
