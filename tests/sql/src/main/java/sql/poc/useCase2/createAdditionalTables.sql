-- Customer table administration SQL

-------------------------------------------------------------------------------
-- Create doc table
CREATE TABLE CDSDBA.XML_DOC_3 (
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    STRUCTURE_ID_NBR DECIMAL(22) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    MSG_PAYLOAD_QTY DECIMAL(22) NOT NULL,
    MSG_PAYLOAD1_IMG BLOB(2000) NOT NULL,
    MSG_PAYLOAD2_IMG BLOB(2000),
    MSG_PAYLOAD_SIZE_NBR DECIMAL(22),
    MSG_PURGE_DT DATE,
    DELETED_FLG CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL,
    MSG_MAJOR_VERSION_NBR DECIMAL(22),
    MSG_MINOR_VERSION_NBR DECIMAL(22),
    OPT_LOCK_TOKEN_NBR DECIMAL(22) DEFAULT 1,
    PRESET_DICTIONARY_ID_NBR DECIMAL(22) DEFAULT 0 NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;
CREATE UNIQUE INDEX CDSDBA.XML_DOC_3_UK1    ON CDSDBA.XML_DOC_3(XML_DOC_ID_NBR,STRUCTURE_ID_NBR);
	
CREATE SYNONYM CDSUSR.XML_DOC_3 FOR CDSDBA.XML_DOC_3;
CREATE SYNONYM CDSUSRRO.XML_DOC_3 FOR CDSDBA.XML_DOC_3;
--GRANT DELETE ON CDSDBA.XML_DOC_3 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_DOC_3 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_DOC_3 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_DOC_3 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_DOC_3 TO CDSUSRRO;

-- Create change history doc table
CREATE TABLE CDSDBA.XML_HST_3 (
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    STRUCTURE_ID_NBR DECIMAL(22) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    MSG_PAYLOAD_QTY DECIMAL(22) NOT NULL,
    MSG_PAYLOAD1_IMG BLOB(2000) NOT NULL,
    MSG_PAYLOAD2_IMG BLOB(2000),
    MSG_PAYLOAD_SIZE_NBR DECIMAL(22),
    MSG_PURGE_DT DATE,
    DELETED_FLG CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL,
    MSG_MAJOR_VERSION_NBR DECIMAL(22),
    MSG_MINOR_VERSION_NBR DECIMAL(22),
    OPT_LOCK_TOKEN_NBR DECIMAL(22) DEFAULT 1,
    PRESET_DICTIONARY_ID_NBR DECIMAL(22) DEFAULT 0 NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;
CREATE UNIQUE INDEX CDSDBA.XML_HST_3_UK1
    ON CDSDBA.XML_HST_3(XML_DOC_ID_NBR,STRUCTURE_ID_NBR);

CREATE SYNONYM CDSUSR.XML_HST_3 FOR CDSDBA.XML_HST_3;
CREATE SYNONYM CDSUSRRO.XML_HST_3 FOR CDSDBA.XML_HST_3;
--GRANT DELETE ON CDSDBA.XML_HST_3 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_HST_3 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_HST_3 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_HST_3 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_HST_3 TO CDSUSRRO;


-------------------------------------------------------------------------------
-- Create index tables

-- /enterpriseProfile/accountNumber
CREATE TABLE CDSDBA.XML_IDX_20_1 (
    IDX_COL1 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_20_1_UK1
    ON CDSDBA.XML_IDX_20_1(IDX_COL1);
CREATE INDEX CDSDBA.XML_IDX_20_1_IX1
    ON CDSDBA.XML_IDX_20_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_20_1 FOR CDSDBA.XML_IDX_20_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_20_1 FOR CDSDBA.XML_IDX_20_1;
--GRANT DELETE ON CDSDBA.XML_IDX_20_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_20_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_20_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_20_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_20_1 TO CDSUSRRO;


-- /enterpriseAssociatedAccounts/accountNumber
CREATE TABLE CDSDBA.XML_IDX_21_1 (
    IDX_COL1 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_21_1_UK1
    ON CDSDBA.XML_IDX_21_1(IDX_COL1);
CREATE INDEX CDSDBA.XML_IDX_21_1_IX1
    ON CDSDBA.XML_IDX_21_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_21_1 FOR CDSDBA.XML_IDX_21_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_21_1 FOR CDSDBA.XML_IDX_21_1;
--GRANT DELETE ON CDSDBA.XML_IDX_21_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_21_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_21_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_21_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_21_1 TO CDSUSRRO;


-- /enterpriseAssociatedAccounts/associatedAccount/number
-- /enterpriseAssociatedAccounts/associatedAccount/opco
-- /enterpriseAssociatedAccounts/accountNumber
CREATE TABLE CDSDBA.XML_IDX_21_2 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 VARCHAR(4),
    IDX_COL3 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_21_2_UK1
    ON CDSDBA.XML_IDX_21_2(IDX_COL1,IDX_COL2,IDX_COL3);
CREATE INDEX CDSDBA.XML_IDX_21_2_IX1
    ON CDSDBA.XML_IDX_21_2(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_21_2 FOR CDSDBA.XML_IDX_21_2;
CREATE SYNONYM CDSUSRRO.XML_IDX_21_2 FOR CDSDBA.XML_IDX_21_2;
--GRANT DELETE ON CDSDBA.XML_IDX_21_2 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_21_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_21_2 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_21_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_21_2 TO CDSUSRRO;


-- /expressAccountProfile/accountNumber
-- /expressAccountProfile/profile/archiveReasonCode
CREATE TABLE CDSDBA.XML_IDX_23_1 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 VARCHAR(5),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_23_1_UK1
    ON CDSDBA.XML_IDX_23_1(IDX_COL1);
CREATE INDEX CDSDBA.XML_IDX_23_1_IX1
    ON CDSDBA.XML_IDX_23_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_23_1 FOR CDSDBA.XML_IDX_23_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_23_1 FOR CDSDBA.XML_IDX_23_1;
--GRANT DELETE ON CDSDBA.XML_IDX_23_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_23_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_23_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_23_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_23_1 TO CDSUSRRO;


-- /contact/person/lastName
-- /contact/person/firstName
-- /contact/person/middleName
-- /contact/companyName
-- /contact/attentionTo
-- /contact/address/streetLine
-- /contact/address/additionalLine1
-- /contact/address/additionalLine2
-- /contact/address/geoPoliticalSubdivision1
-- /contact/address/geoPoliticalSubdivision2
-- /contact/address/geoPoliticalSubdivision3
-- /contact/address/postalCode
-- /contact/address/countryCode
-- /contact/teleCom/teleComMethod
-- /contact/teleCom/numericCountryCode
-- /contact/teleCom/areaCode
-- /contact/teleCom/phoneNumber
CREATE TABLE CDSDBA.XML_IDX_24_1 (
    IDX_COL1 VARCHAR(30),
    IDX_COL2 VARCHAR(25),
    IDX_COL3 VARCHAR(25),
    IDX_COL4 VARCHAR(60),
    IDX_COL5 VARCHAR(25),
    IDX_COL6 VARCHAR(60),
    IDX_COL7 VARCHAR(60),
    IDX_COL8 VARCHAR(60),
    IDX_COL9 VARCHAR(60),
    IDX_COL10 VARCHAR(60),
    IDX_COL11 VARCHAR(60),
    IDX_COL12 VARCHAR(13),
    IDX_COL13 VARCHAR(2),
    IDX_COL14 VARCHAR(2),
    IDX_COL15 VARCHAR(5),
    IDX_COL16 VARCHAR(5),
    IDX_COL17 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_24_1_UK1
    ON CDSDBA.XML_IDX_24_1(IDX_COL1,IDX_COL4,IDX_COL14,IDX_COL16,IDX_COL17,XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_24_1_IX1
    ON CDSDBA.XML_IDX_24_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_24_1 FOR CDSDBA.XML_IDX_24_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_24_1 FOR CDSDBA.XML_IDX_24_1;
--GRANT DELETE ON CDSDBA.XML_IDX_24_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_24_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_24_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_24_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_24_1 TO CDSUSRRO;


-- /contact/teleCom/phoneNumber
-- /contact/teleCom/areaCode
-- /contact/teleCom/teleComMethod
-- /contact/teleCom/numericCountryCode
-- /contact/companyName
CREATE TABLE CDSDBA.XML_IDX_24_2 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 VARCHAR(5),
    IDX_COL3 VARCHAR(5),
    IDX_COL4 VARCHAR(5),
    IDX_COL5 VARCHAR(60),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_24_2_UK1
    ON CDSDBA.XML_IDX_24_2(IDX_COL1,IDX_COL2,IDX_COL3,IDX_COL4,XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_24_2_IX1
    ON CDSDBA.XML_IDX_24_2(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_24_2 FOR CDSDBA.XML_IDX_24_2;
CREATE SYNONYM CDSUSRRO.XML_IDX_24_2 FOR CDSDBA.XML_IDX_24_2;
--GRANT DELETE ON CDSDBA.XML_IDX_24_2 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_24_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_24_2 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_24_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_24_2 TO CDSUSRRO;


-- /contact/address/streetLine
-- /contact/address/additionalLine1
-- /contact/address/additionalLine2
-- /contact/address/geoPoliticalSubdivision1
-- /contact/address/geoPoliticalSubdivision2
-- /contact/address/geoPoliticalSubdivision3
-- /contact/address/postalCode
-- /contact/address/countryCode
CREATE TABLE CDSDBA.XML_IDX_24_3 (
    IDX_COL1 VARCHAR(60),
    IDX_COL2 VARCHAR(60),
    IDX_COL3 VARCHAR(60),
    IDX_COL4 VARCHAR(60),
    IDX_COL5 VARCHAR(60),
    IDX_COL6 VARCHAR(60),
    IDX_COL7 VARCHAR(13),
    IDX_COL8 VARCHAR(2),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_24_3_UK1
    ON CDSDBA.XML_IDX_24_3(XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_24_3_IX1
    ON CDSDBA.XML_IDX_24_3(IDX_COL1);

CREATE SYNONYM CDSUSR.XML_IDX_24_3 FOR CDSDBA.XML_IDX_24_3;
CREATE SYNONYM CDSUSRRO.XML_IDX_24_3 FOR CDSDBA.XML_IDX_24_3;
--GRANT DELETE ON CDSDBA.XML_IDX_24_3 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_24_3 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_24_3 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_24_3 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_24_3 TO CDSUSRRO;


-- /contact/companyName
-- /contact/attentionTo
-- /contact/address/streetLine
-- /contact/address/additionalLine1
-- /contact/address/additionalLine2
-- /contact/address/geoPoliticalSubdivision1
-- /contact/address/geoPoliticalSubdivision2
-- /contact/address/geoPoliticalSubdivision3
-- /contact/address/postalCode
-- /contact/address/countryCode
-- /contact/teleCom/teleComMethod
-- /contact/teleCom/numericCountryCode
-- /contact/teleCom/areaCode
-- /contact/teleCom/phoneNumber
CREATE TABLE CDSDBA.XML_IDX_24_4 (
    IDX_COL1 VARCHAR(60),
    IDX_COL2 VARCHAR(25),
    IDX_COL3 VARCHAR(60),
    IDX_COL4 VARCHAR(60),
    IDX_COL5 VARCHAR(60),
    IDX_COL6 VARCHAR(60),
    IDX_COL7 VARCHAR(60),
    IDX_COL8 VARCHAR(60),
    IDX_COL9 VARCHAR(13),
    IDX_COL10 VARCHAR(2),
    IDX_COL11 VARCHAR(2),
    IDX_COL12 VARCHAR(5),
    IDX_COL13 VARCHAR(5),
    IDX_COL14 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_24_4_UK1
    ON CDSDBA.XML_IDX_24_4(IDX_COL1,IDX_COL4,IDX_COL11,IDX_COL13,IDX_COL14,XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_24_4_IX1
    ON CDSDBA.XML_IDX_24_4(XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_24_4_IX2
    ON CDSDBA.XML_IDX_24_4(IDX_COL1,IDX_COL3,IDX_COL9);
CREATE INDEX CDSDBA.XML_IDX_24_4_IX3
    ON CDSDBA.XML_IDX_24_4(IDX_COL12,IDX_COL13,IDX_COL14);

CREATE SYNONYM CDSUSR.XML_IDX_24_4 FOR CDSDBA.XML_IDX_24_4;
CREATE SYNONYM CDSUSRRO.XML_IDX_24_4 FOR CDSDBA.XML_IDX_24_4;
--GRANT DELETE ON CDSDBA.XML_IDX_24_4 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_24_4 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_24_4 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_24_4 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_24_4 TO CDSUSRRO;


-- /accountContact/accountType/accountNumber
-- /accountContact/contactTypeCode
-- /accountContact/contactDocumentId
-- /accountContact/contactBusinessId
CREATE TABLE CDSDBA.XML_IDX_25_1 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 VARCHAR(5),
    IDX_COL3 DECIMAL(22),
    IDX_COL4 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_25_1_UK1
    ON CDSDBA.XML_IDX_25_1(IDX_COL1,IDX_COL2,IDX_COL4);
CREATE INDEX CDSDBA.XML_IDX_25_1_IX1
    ON CDSDBA.XML_IDX_25_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_25_1 FOR CDSDBA.XML_IDX_25_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_25_1 FOR CDSDBA.XML_IDX_25_1;
--GRANT DELETE ON CDSDBA.XML_IDX_25_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_25_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_25_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_25_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_25_1 TO CDSUSRRO;


-- /accountContact/accountType/accountId/number
-- /accountContact/accountType/accountId/opco
-- /accountContact/contactTypeCode
-- /accountContact/contactDocumentId
-- /accountContact/contactBusinessId
CREATE TABLE CDSDBA.XML_IDX_25_2 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 VARCHAR(4),
    IDX_COL3 VARCHAR(5),
    IDX_COL4 DECIMAL(22),
    IDX_COL5 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_25_2_UK1
    ON CDSDBA.XML_IDX_25_2(IDX_COL1,IDX_COL2,IDX_COL3,IDX_COL5);
CREATE INDEX CDSDBA.XML_IDX_25_2_IX1
    ON CDSDBA.XML_IDX_25_2(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_25_2 FOR CDSDBA.XML_IDX_25_2;
CREATE SYNONYM CDSUSRRO.XML_IDX_25_2 FOR CDSDBA.XML_IDX_25_2;
--GRANT DELETE ON CDSDBA.XML_IDX_25_2 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_25_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_25_2 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_25_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_25_2 TO CDSUSRRO;


-- /accountContact/contactDocumentId
-- /accountContact/contactTypeCode
-- /accountContact/accountType/accountId/opco
CREATE TABLE CDSDBA.XML_IDX_25_3 (
    IDX_COL1 DECIMAL(22),
    IDX_COL2 VARCHAR(5),
    IDX_COL3 VARCHAR(4),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_25_3_UK1
    ON CDSDBA.XML_IDX_25_3(IDX_COL1,IDX_COL2,IDX_COL3,XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_25_3_IX1
    ON CDSDBA.XML_IDX_25_3(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_25_3 FOR CDSDBA.XML_IDX_25_3;
CREATE SYNONYM CDSUSRRO.XML_IDX_25_3 FOR CDSDBA.XML_IDX_25_3;
--GRANT DELETE ON CDSDBA.XML_IDX_25_3 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_25_3 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_25_3 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_25_3 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_25_3 TO CDSUSRRO;


-- /comments/accountId/number
-- /comments/accountId/opco
-- /comments/comment/type
-- /comments/comment/commentDateTime
-- /comments/comment/commentId
CREATE TABLE CDSDBA.XML_IDX_26_1 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 VARCHAR(4),
    IDX_COL3 VARCHAR(3),
    IDX_COL4 DECIMAL(19),
    IDX_COL5 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_26_1_UK1
    ON CDSDBA.XML_IDX_26_1(IDX_COL1,IDX_COL2,IDX_COL3,IDX_COL4,IDX_COL5);
CREATE INDEX CDSDBA.XML_IDX_26_1_IX1
    ON CDSDBA.XML_IDX_26_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_26_1 FOR CDSDBA.XML_IDX_26_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_26_1 FOR CDSDBA.XML_IDX_26_1;
--GRANT DELETE ON CDSDBA.XML_IDX_26_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_26_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_26_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_26_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_26_1 TO CDSUSRRO;


-- 27_1
-- /groupId/groupIdDetail/number
-- /groupId/groupIdDetail/code
CREATE TABLE CDSDBA.XML_IDX_27_1 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 VARCHAR(10),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_27_1_UK1
    ON CDSDBA.XML_IDX_27_1(IDX_COL1,IDX_COL2);
CREATE INDEX CDSDBA.XML_IDX_27_1_IX1
    ON CDSDBA.XML_IDX_27_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_27_1 FOR CDSDBA.XML_IDX_27_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_27_1 FOR CDSDBA.XML_IDX_27_1;
--GRANT DELETE ON CDSDBA.XML_IDX_27_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_27_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_27_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_27_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_27_1 TO CDSUSRRO;

-- 27_2
-- /groupId/groupIdDetail/masterAccount
CREATE TABLE CDSDBA.XML_IDX_27_2 (
    IDX_COL1 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_27_2_UK1
    ON CDSDBA.XML_IDX_27_2(XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_27_2_IX1
    ON CDSDBA.XML_IDX_27_2(IDX_COL1);

CREATE SYNONYM CDSUSR.XML_IDX_27_2 FOR CDSDBA.XML_IDX_27_2;
CREATE SYNONYM CDSUSRRO.XML_IDX_27_2 FOR CDSDBA.XML_IDX_27_2;
--GRANT DELETE ON CDSDBA.XML_IDX_27_2 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_27_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_27_2 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_27_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_27_2 TO CDSUSRRO;


-- 30_1
-- /freightAccountProfile/accountNumber
-- /freightAccountProfile/profile/archiveReasonCode
CREATE TABLE CDSDBA.XML_IDX_30_1 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 VARCHAR(5),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_30_1_UK1
    ON CDSDBA.XML_IDX_30_1(IDX_COL1);
CREATE INDEX CDSDBA.XML_IDX_30_1_IX1
    ON CDSDBA.XML_IDX_30_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_30_1 FOR CDSDBA.XML_IDX_30_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_30_1 FOR CDSDBA.XML_IDX_30_1;
--GRANT DELETE ON CDSDBA.XML_IDX_30_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_30_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_30_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_30_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_30_1 TO CDSUSRRO;


-- 33_1
-- /expressAggregations/aggregations/edAggrCode
CREATE TABLE CDSDBA.XML_IDX_33_1 (
    IDX_COL1 VARCHAR(6),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_33_1_UK1
    ON CDSDBA.XML_IDX_33_1(XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_33_1_IX1
    ON CDSDBA.XML_IDX_33_1(IDX_COL1);

CREATE SYNONYM CDSUSR.XML_IDX_33_1 FOR CDSDBA.XML_IDX_33_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_33_1 FOR CDSDBA.XML_IDX_33_1;
--GRANT DELETE ON CDSDBA.XML_IDX_33_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_33_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_33_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_1 TO CDSUSRRO;



-- 33_2
-- /expressAggregations/aggregations/geoAcctNumber
CREATE TABLE CDSDBA.XML_IDX_33_2 (
    IDX_COL1 VARCHAR(9),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_33_2_UK1
    ON CDSDBA.XML_IDX_33_2(XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_33_2_IX1
    ON CDSDBA.XML_IDX_33_2(IDX_COL1);

CREATE SYNONYM CDSUSR.XML_IDX_33_2 FOR CDSDBA.XML_IDX_33_2;
CREATE SYNONYM CDSUSRRO.XML_IDX_33_2 FOR CDSDBA.XML_IDX_33_2;
--GRANT DELETE ON CDSDBA.XML_IDX_33_2 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_33_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_2 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_33_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_2 TO CDSUSRRO;



-- 33_3
-- /expressAggregations/aggregations/globalaccountNumber
-- /expressAggregations/aggregations/globalSubgroup
CREATE TABLE CDSDBA.XML_IDX_33_3 (
    IDX_COL1 VARCHAR(5),
    IDX_COL2 VARCHAR(5),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_33_3_UK1
    ON CDSDBA.XML_IDX_33_3(XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_33_3_IX1
    ON CDSDBA.XML_IDX_33_3(IDX_COL1,IDX_COL2);

CREATE SYNONYM CDSUSR.XML_IDX_33_3 FOR CDSDBA.XML_IDX_33_3;
CREATE SYNONYM CDSUSRRO.XML_IDX_33_3 FOR CDSDBA.XML_IDX_33_3;
--GRANT DELETE ON CDSDBA.XML_IDX_33_3 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_33_3 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_3 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_33_3 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_3 TO CDSUSRRO;



-- 33_4
-- /expressAggregations/aggregations/ediNumber
CREATE TABLE CDSDBA.XML_IDX_33_4 (
    IDX_COL1 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_33_4_UK1
    ON CDSDBA.XML_IDX_33_4(XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_33_4_IX1
    ON CDSDBA.XML_IDX_33_4(IDX_COL1);

CREATE SYNONYM CDSUSR.XML_IDX_33_4 FOR CDSDBA.XML_IDX_33_4;
CREATE SYNONYM CDSUSRRO.XML_IDX_33_4 FOR CDSDBA.XML_IDX_33_4;
--GRANT DELETE ON CDSDBA.XML_IDX_33_4 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_33_4 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_4 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_33_4 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_4 TO CDSUSRRO;



-- 33_5
-- /expressAggregations/aggregations/ssAccountNumber
CREATE TABLE CDSDBA.XML_IDX_33_5 (
    IDX_COL1 VARCHAR(9),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_33_5_UK1
    ON CDSDBA.XML_IDX_33_5(XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_33_5_IX1
    ON CDSDBA.XML_IDX_33_5(IDX_COL1);

CREATE SYNONYM CDSUSR.XML_IDX_33_5 FOR CDSDBA.XML_IDX_33_5;
CREATE SYNONYM CDSUSRRO.XML_IDX_33_5 FOR CDSDBA.XML_IDX_33_5;
--GRANT DELETE ON CDSDBA.XML_IDX_33_5 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_33_5 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_5 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_33_5 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_5 TO CDSUSRRO;



-- 33_6
-- /expressAggregations/aggregations/billToNumber
CREATE TABLE CDSDBA.XML_IDX_33_6 (
    IDX_COL1 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_33_6_UK1
    ON CDSDBA.XML_IDX_33_6(IDX_COL1,XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_33_6_IX1
    ON CDSDBA.XML_IDX_33_6(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_33_6 FOR CDSDBA.XML_IDX_33_6;
CREATE SYNONYM CDSUSRRO.XML_IDX_33_6 FOR CDSDBA.XML_IDX_33_6;
--GRANT DELETE ON CDSDBA.XML_IDX_33_6 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_33_6 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_6 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_33_6 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_6 TO CDSUSRRO;



-- 33_7
-- /expressAggregations/accountNumber
CREATE TABLE CDSDBA.XML_IDX_33_7 (
    IDX_COL1 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_33_7_UK1
    ON CDSDBA.XML_IDX_33_7(IDX_COL1);
CREATE INDEX CDSDBA.XML_IDX_33_7_IX1
    ON CDSDBA.XML_IDX_33_7(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_33_7 FOR CDSDBA.XML_IDX_33_7;
CREATE SYNONYM CDSUSRRO.XML_IDX_33_7 FOR CDSDBA.XML_IDX_33_7;
--GRANT DELETE ON CDSDBA.XML_IDX_33_7 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_33_7 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_7 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_33_7 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_33_7 TO CDSUSRRO;


-- 38_1
-- /expressCreditCard/accountNumber
CREATE TABLE CDSDBA.XML_IDX_38_1 (
    IDX_COL1 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_38_1_UK1
    ON CDSDBA.XML_IDX_38_1(IDX_COL1);
CREATE INDEX CDSDBA.XML_IDX_38_1_IX1
    ON CDSDBA.XML_IDX_38_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_38_1 FOR CDSDBA.XML_IDX_38_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_38_1 FOR CDSDBA.XML_IDX_38_1;
--GRANT DELETE ON CDSDBA.XML_IDX_38_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_38_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_38_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_38_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_38_1 TO CDSUSRRO;


-- 41_1
-- /expressElectronicPay/accountNumber
CREATE TABLE CDSDBA.XML_IDX_41_1 (
    IDX_COL1 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_41_1_UK1
    ON CDSDBA.XML_IDX_41_1(IDX_COL1);
CREATE INDEX CDSDBA.XML_IDX_41_1_IX1
    ON CDSDBA.XML_IDX_41_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_41_1 FOR CDSDBA.XML_IDX_41_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_41_1 FOR CDSDBA.XML_IDX_41_1;
--GRANT DELETE ON CDSDBA.XML_IDX_41_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_41_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_41_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_41_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_41_1 TO CDSUSRRO;


-- 47_1
-- /freightCreditCard/accountNumber
CREATE TABLE CDSDBA.XML_IDX_47_1 (
    IDX_COL1 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_47_1_UK1
    ON CDSDBA.XML_IDX_47_1(IDX_COL1);
CREATE INDEX CDSDBA.XML_IDX_47_1_IX1
    ON CDSDBA.XML_IDX_47_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_47_1 FOR CDSDBA.XML_IDX_47_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_47_1 FOR CDSDBA.XML_IDX_47_1;
--GRANT DELETE ON CDSDBA.XML_IDX_47_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_47_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_47_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_47_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_47_1 TO CDSUSRRO;


-- 50_1
-- /officeCreditCard/accountNumber
CREATE TABLE CDSDBA.XML_IDX_50_1 (
    IDX_COL1 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_50_1_UK1
    ON CDSDBA.XML_IDX_50_1(IDX_COL1);
CREATE INDEX CDSDBA.XML_IDX_50_1_IX1
    ON CDSDBA.XML_IDX_50_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_50_1 FOR CDSDBA.XML_IDX_50_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_50_1 FOR CDSDBA.XML_IDX_50_1;
--GRANT DELETE ON CDSDBA.XML_IDX_50_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_50_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_50_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_50_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_50_1 TO CDSUSRRO;


-- 57_1
-- /cargoOperationsProfile/accountNumber
-- /cargoOperationsProfile/profile/airportCode
-- /cargoOperationsProfile/profile/businessMode
-- /cargoOperationsProfile/profile/synonymName
CREATE TABLE CDSDBA.XML_IDX_57_1 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 VARCHAR(3),
    IDX_COL3 VARCHAR(2),
    IDX_COL4 VARCHAR(16),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_57_1_UK1
    ON CDSDBA.XML_IDX_57_1(IDX_COL1,IDX_COL2,IDX_COL3,IDX_COL4,XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_57_1_IX1
    ON CDSDBA.XML_IDX_57_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_57_1 FOR CDSDBA.XML_IDX_57_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_57_1 FOR CDSDBA.XML_IDX_57_1;
--GRANT DELETE ON CDSDBA.XML_IDX_57_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_57_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_57_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_57_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_57_1 TO CDSUSRRO;

-- 57_2 
-- /cargoOperationsProfile/profile/synonymName
CREATE TABLE CDSDBA.XML_IDX_57_2 (
    IDX_COL1 VARCHAR(16),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_57_2_UK1
    ON CDSDBA.XML_IDX_57_2(IDX_COL1,XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_57_2_IX1
    ON CDSDBA.XML_IDX_57_2(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_57_2 FOR CDSDBA.XML_IDX_57_2;
CREATE SYNONYM CDSUSRRO.XML_IDX_57_2 FOR CDSDBA.XML_IDX_57_2;
--GRANT DELETE ON CDSDBA.XML_IDX_57_2 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_57_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_57_2 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_57_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_57_2 TO CDSUSRRO;


-- 65_1
-- /nationalAccount/accountId/number
-- /nationalAccount/accountId/opco
-- /nationalAccount/nationalAccountDetail/membershipEffDateTime
-- /nationalAccount/nationalAccountDetail/membershipExpDateTime
-- /nationalAccount/nationalAccountDetail/nationalAccountCompanyCd
-- /nationalAccount/nationalAccountDetail/nationalAccountNbr
-- /nationalAccount/nationalAccountDetail/nationalPriorityCd
-- /nationalAccount/nationalAccountDetail/nationalSubgroupNbr
CREATE TABLE CDSDBA.XML_IDX_65_1 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 VARCHAR(4),
    IDX_COL3 DECIMAL(19),
    IDX_COL4 DECIMAL(19),
    IDX_COL5 VARCHAR(4),
    IDX_COL6 VARCHAR(20),
    IDX_COL7 CHAR(1),
    IDX_COL8 VARCHAR(3),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_65_1_UK1
    ON CDSDBA.XML_IDX_65_1(XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_65_1_IX1
    ON CDSDBA.XML_IDX_65_1(IDX_COL1);

CREATE SYNONYM CDSUSR.XML_IDX_65_1 FOR CDSDBA.XML_IDX_65_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_65_1 FOR CDSDBA.XML_IDX_65_1;
--GRANT DELETE ON CDSDBA.XML_IDX_65_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_65_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_65_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_65_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_65_1 TO CDSUSRRO;

-- 65_2
-- /nationalAccount/nationalAccountDetail/nationalAccountNbr
-- /nationalAccount/nationalAccountDetail/nationalAccountCompanyCd
-- /nationalAccount/nationalAccountDetail/nationalSubgroupNbr
-- /nationalAccount/nationalAccountDetail/membershipEffDateTime
-- /nationalAccount/nationalAccountDetail/membershipExpDateTime
CREATE TABLE CDSDBA.XML_IDX_65_2 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 VARCHAR(4),
    IDX_COL3 VARCHAR(3),
    IDX_COL4 DECIMAL(19),
    IDX_COL5 DECIMAL(19),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_65_2_UK1
    ON CDSDBA.XML_IDX_65_2(XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_65_2_IX1
    ON CDSDBA.XML_IDX_65_2(IDX_COL1);

CREATE SYNONYM CDSUSR.XML_IDX_65_2 FOR CDSDBA.XML_IDX_65_2;
CREATE SYNONYM CDSUSRRO.XML_IDX_65_2 FOR CDSDBA.XML_IDX_65_2;
--GRANT DELETE ON CDSDBA.XML_IDX_65_2 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_65_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_65_2 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_65_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_65_2 TO CDSUSRRO;


-- 78_1
-- /groupMembership/groupId/number
-- /groupMembership/groupId/code
-- /groupMembership/effectiveDateTime
-- /groupMembership/expirationDateTime
CREATE TABLE CDSDBA.XML_IDX_78_1 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 VARCHAR(10),
    IDX_COL3 DECIMAL(19),
    IDX_COL4 DECIMAL(19),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_78_1_UK1
    ON CDSDBA.XML_IDX_78_1(XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_78_1_IX1
    ON CDSDBA.XML_IDX_78_1(IDX_COL1);

CREATE SYNONYM CDSUSR.XML_IDX_78_1 FOR CDSDBA.XML_IDX_78_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_78_1 FOR CDSDBA.XML_IDX_78_1;
--GRANT DELETE ON CDSDBA.XML_IDX_78_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_78_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_78_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_78_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_78_1 TO CDSUSRRO;

-- 78_2
-- /groupMembership/accountId/number
-- /groupMembership/accountId/opco
-- /groupMembership/effectiveDateTime
-- /groupMembership/expirationDateTime
-- /groupMembership/groupId/number
-- /groupMembership/groupId/code
CREATE TABLE CDSDBA.XML_IDX_78_2 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 VARCHAR(4),
    IDX_COL3 DECIMAL(19),
    IDX_COL4 DECIMAL(19),
    IDX_COL5 VARCHAR(20),
    IDX_COL6 VARCHAR(10),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_78_2_UK1
    ON CDSDBA.XML_IDX_78_2(IDX_COL1,IDX_COL2,IDX_COL3,IDX_COL4,IDX_COL5,IDX_COL6);
CREATE INDEX CDSDBA.XML_IDX_78_2_IX1
    ON CDSDBA.XML_IDX_78_2(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_78_2 FOR CDSDBA.XML_IDX_78_2;
CREATE SYNONYM CDSUSRRO.XML_IDX_78_2 FOR CDSDBA.XML_IDX_78_2;
--GRANT DELETE ON CDSDBA.XML_IDX_78_2 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_78_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_78_2 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_78_2 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_78_2 TO CDSUSRRO;


-- 79_1
-- /expressApplyDiscountDetail/accountNumber
-- /expressApplyDiscountDetail/applyDiscount/effectiveDateTime
-- /expressApplyDiscountDetail/applyDiscount/expirationDateTime
CREATE TABLE CDSDBA.XML_IDX_79_1 (
    IDX_COL1 VARCHAR(20),
    IDX_COL2 DECIMAL(19),
    IDX_COL3 DECIMAL(19),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_79_1_UK1
    ON CDSDBA.XML_IDX_79_1(IDX_COL1,IDX_COL2,IDX_COL3);
CREATE INDEX CDSDBA.XML_IDX_79_1_IX1
    ON CDSDBA.XML_IDX_79_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_79_1 FOR CDSDBA.XML_IDX_79_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_79_1 FOR CDSDBA.XML_IDX_79_1;
--GRANT DELETE ON CDSDBA.XML_IDX_79_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_79_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_79_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_79_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_79_1 TO CDSUSRRO;


-- 87_1
-- /expressGFBOElectronicPay/accountNumber
CREATE TABLE CDSDBA.XML_IDX_87_1 (
    IDX_COL1 VARCHAR(20),
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_87_1_UK1
    ON CDSDBA.XML_IDX_87_1(IDX_COL1);
CREATE INDEX CDSDBA.XML_IDX_87_1_IX1
    ON CDSDBA.XML_IDX_87_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_87_1 FOR CDSDBA.XML_IDX_87_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_87_1 FOR CDSDBA.XML_IDX_87_1;
--GRANT DELETE ON CDSDBA.XML_IDX_87_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_87_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_87_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_87_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_87_1 TO CDSUSRRO;



-- Update notification index
CREATE TABLE CDSDBA.XML_IDX_98_1 (
    IDX_COL1 DECIMAL(19), -- time
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_98_1_UK1
    ON CDSDBA.XML_IDX_98_1(IDX_COL1,XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_98_1_IX1
    ON CDSDBA.XML_IDX_98_1(XML_DOC_ID_NBR);

CREATE SYNONYM CDSUSR.XML_IDX_98_1 FOR CDSDBA.XML_IDX_98_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_98_1 FOR CDSDBA.XML_IDX_98_1;
--GRANT DELETE ON CDSDBA.XML_IDX_98_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_98_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_98_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_98_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_98_1 TO CDSUSRRO;

-- History index
CREATE TABLE CDSDBA.XML_IDX_99_1 (
    IDX_COL1 DECIMAL(19),	-- time
    IDX_COL2 VARCHAR(50), -- name
    IDX_COL3 VARCHAR(20), -- key
    IDX_COL4 VARCHAR(20), -- operation
    IDX_COL5 DECIMAL(22),	-- sequence DECIMAL
    IDX_COL6 VARCHAR(30), -- custom search value
    XML_DOC_ID_NBR DECIMAL(19) NOT NULL,
    CREATE_MINT_CD CHAR(1) NOT NULL,
    LAST_UPDATE_SYSTEM_NM VARCHAR(30),
    LAST_UPDATE_TMSTP TIMESTAMP NOT NULL
)
PARTITION BY COLUMN (XML_DOC_ID_NBR)
REDUNDANCY 1;

CREATE UNIQUE INDEX CDSDBA.XML_IDX_99_1_UK1
    ON CDSDBA.XML_IDX_99_1(IDX_COL1,IDX_COL2,IDX_COL3,IDX_COL4,IDX_COL5,IDX_COL6);
CREATE INDEX CDSDBA.XML_IDX_99_1_IX1
    ON CDSDBA.XML_IDX_99_1(XML_DOC_ID_NBR);
CREATE INDEX CDSDBA.XML_IDX_99_1_IX2
    ON CDSDBA.XML_IDX_99_1(IDX_COL6,IDX_COL2,IDX_COL1);

CREATE SYNONYM CDSUSR.XML_IDX_99_1 FOR CDSDBA.XML_IDX_99_1;
CREATE SYNONYM CDSUSRRO.XML_IDX_99_1 FOR CDSDBA.XML_IDX_99_1;
--GRANT DELETE ON CDSDBA.XML_IDX_99_1 TO CDSUSR;
--GRANT INSERT ON CDSDBA.XML_IDX_99_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_99_1 TO CDSUSR;
--GRANT UPDATE ON CDSDBA.XML_IDX_99_1 TO CDSUSR;
--GRANT SELECT ON CDSDBA.XML_IDX_99_1 TO CDSUSRRO;



