CREATE OR REPLACE 
FUNCTION OTR_INCOME_MANAGER_SALESERVICE_PARTATION_BY_NATURALDATE
-- *************************************************************************
-- SYSTEM:      电子商务系统
-- SUBSYS:      网上交易子系统
-- PROG:        OTR_INCOME_MANAGER_SALESERVICE_PARTATION_BY_NATURALDATE
-- AUTHOR:      wangyf@jjmmw.com
-- DESC:        收益提成，按自然日分区(为了提高速度，这里直接写入目的表，目标分区表需要提前简历。)
-- *************************************************************************
(
)
RETURNS trigger AS $$

DECLARE

  VC_RELNAME             VARCHAR;  -- 触发本触发器的表名
  VC_DEST_TABLE          VARCHAR;  -- 目标字表名称
  VC_SQL                 VARCHAR;  -- 动态SQL

BEGIN
  VC_DEST_TABLE := NULL;
  VC_RELNAME := LOWER(TG_RELNAME);

  BEGIN
    VC_DEST_TABLE := VC_RELNAME || '_' || substring(NEW.NATURALDATE,1,6);
    VC_SQL := 'INSERT INTO ' || VC_DEST_TABLE || ' SELECT $1.*';
    EXECUTE VC_SQL USING NEW;
    RETURN NULL;
  EXCEPTION
    WHEN OTHERS THEN
      RAISE NOTICE '[插入分区表失败]TABLE=%[%]%', VC_RELNAME, SQLSTATE, SQLERRM;
      RETURN NEW;
  END;

END;
$$ LANGUAGE plpgsql
