CREATE OR REPLACE 
FUNCTION F_FULL_INCOME_MANAGER_SALESERVICE_PARTATION
(
  PI_NATURALDATE          IN VARCHAR
)
  RETURNS VOID AS
$BODY$
DECLARE

  VC_SRC_TABLE           VARCHAR;          --源表
  VC_DATE                DATE;             --日期
  VC_MONPART_NAME        VARCHAR (6);      --月分区名：YYYYMM
  VC_DST_TABLE           VARCHAR (64);     --分区表名(目标表)
  VC_DATE_BEGIN          VARCHAR;          --开始日期
  VC_DATE_END            VARCHAR;          --结束日期
  VC_FIELD_CHECK         VARCHAR;          --日期分区字段名
  VC_SQL                 VARCHAR;          --执行的SQL语句

BEGIN

  VC_SRC_TABLE := 'STATISTICS_INCOME_MANAGER_SALESERVICE';
  VC_FIELD_CHECK  := 'NATURALDATE';
  VC_DATE := TO_DATE(PI_NATURALDATE, 'YYYYMMDD');

  VC_MONPART_NAME := NULL;
  VC_DST_TABLE := NULL;
  VC_SQL := NULL;
 
  -- ******************** 按月分区 ********************
  -- 分区月份，YYYYMM格式
  SELECT TO_CHAR(VC_DATE, 'YYYYMM') INTO VC_MONPART_NAME;
  VC_DATE_BEGIN := VC_MONPART_NAME || '01';
  VC_DATE_END := VC_MONPART_NAME || '31';

  -- 月表分区表名
  VC_DST_TABLE := VC_SRC_TABLE || '_' || VC_MONPART_NAME ;
  
  -- 创建分区表
  VC_SQL := 'CREATE TABLE IF NOT EXISTS ' || VC_DST_TABLE || '('
    || ' CHECK(' || VC_FIELD_CHECK || ' >= ''' || VC_DATE_BEGIN  || ''' AND ' || VC_FIELD_CHECK || ' <=''' || VC_DATE_END || ''')'
    || ' )'
    || ' INHERITS (' || VC_SRC_TABLE || ') tablespace tbs_data;';
  EXECUTE VC_SQL;

  VC_SQL := 'CREATE INDEX ' || VC_DST_TABLE || '_misc_idx ON ' || VC_DST_TABLE || ' USING btree (workdate, partnerno,partnernm,provider,accounttype) tablespace tbs_idx;';
  EXECUTE VC_SQL;
  VC_SQL := 'CREATE INDEX ' || VC_DST_TABLE || '_naturaldate_idx ON ' || VC_DST_TABLE || ' USING btree (naturaldate) tablespace tbs_idx;';
  EXECUTE VC_SQL;
  VC_SQL := 'CREATE INDEX ' || VC_DST_TABLE || '_workdate_idx ON ' || VC_DST_TABLE || ' USING btree (workdate) tablespace tbs_idx;';
  EXECUTE VC_SQL;

  RAISE NOTICE '******** % CREATE FINISH ********', VC_DST_TABLE;
  
  RETURN ;
END;
$BODY$ LANGUAGE PLPGSQL;

