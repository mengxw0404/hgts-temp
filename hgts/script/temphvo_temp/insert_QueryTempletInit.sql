INSERT INTO pub_query_templet (ts, ID, MODEL_CODE, MODEL_NAME, NODE_CODE, PK_CORP, METACLASS, LAYER ) VALUES ('2019-11-04 17:06:12', '0001ZZ10000000003RLS', '40H10099', 'temp', '40H10099', '@@@@', '8c6f202d-ba90-415f-85fa-f53d1a53dc4c', 0 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '集团', 5, 0, 1, 'pk_group', '0001ZZ10000000003RLT', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=', '等于@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, VALUE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '组织（包含全局）(所有)', 5, 1, 1, 'pk_org', '0001ZZ10000000003RLU', 'Y', 'N', 'Y', 'N', 'N', 'Y', 'N', 'N', 'Y', 'N', 'N', '=@', '等于@', 0, '@@@@', '0001ZZ10000000003RLS', 2, '#mainorg#', 'Y', 'Y', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '业务单元版本', 5, 2, 1, 'pk_org_v', '0001ZZ10000000003RLV', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=', '等于@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 3, 1, 'pk_id', '0001ZZ10000000003RLW', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 4, 1, 'code', '0001ZZ10000000003RLX', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 5, 1, 'name', '0001ZZ10000000003RLY', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 6, 1, 'billno', '0001ZZ10000000003RLZ', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, VALUE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 3, 7, 0, 'billdate', '0001ZZ10000000003RM0', 'Y', 'N', 'Y', 'N', 'N', 'Y', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000003RLS', 2, '#day(0)#,#day(0)#', 'Y', 'Y', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 8, 1, 'pkorg', '0001ZZ10000000003RM1', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 9, 1, 'busitype', '0001ZZ10000000003RM2', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 10, 1, 'billmaker', '0001ZZ10000000003RM3', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 11, 1, 'approver', '0001ZZ10000000003RM4', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', 'IM,2ed33012-890c-4e5f-82a0-40ef0eeb4b45', 6, 12, 1, 'approvestatus', '0001ZZ10000000003RM5', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@<>@', '等于@不等于@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 13, 1, 'approvenote', '0001ZZ10000000003RM6', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 8, 14, 1, 'approvedate', '0001ZZ10000000003RM7', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'Y', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 15, 1, 'transtype', '0001ZZ10000000003RM8', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 16, 1, 'billtype', '0001ZZ10000000003RM9', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 17, 1, 'transtypepk', '0001ZZ10000000003RMA', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 18, 1, 'def1', '0001ZZ10000000003RMB', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 19, 1, 'def2', '0001ZZ10000000003RMC', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 20, 1, 'def3', '0001ZZ10000000003RMD', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 21, 1, 'def4', '0001ZZ10000000003RME', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 22, 1, 'def5', '0001ZZ10000000003RMF', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 23, 1, 'def6', '0001ZZ10000000003RMG', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 24, 1, 'def7', '0001ZZ10000000003RMH', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 25, 1, 'def8', '0001ZZ10000000003RMI', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 26, 1, 'def9', '0001ZZ10000000003RMJ', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 27, 1, 'def10', '0001ZZ10000000003RMK', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 28, 1, 'def11', '0001ZZ10000000003RML', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 29, 1, 'def12', '0001ZZ10000000003RMM', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 30, 1, 'def13', '0001ZZ10000000003RMN', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 31, 1, 'def14', '0001ZZ10000000003RMO', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 32, 1, 'def15', '0001ZZ10000000003RMP', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 33, 1, 'def16', '0001ZZ10000000003RMQ', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 34, 1, 'def17', '0001ZZ10000000003RMR', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 35, 1, 'def18', '0001ZZ10000000003RMS', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 36, 1, 'def19', '0001ZZ10000000003RMT', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 37, 1, 'def20', '0001ZZ10000000003RMU', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '用户', 5, 38, 1, 'creator', '0001ZZ10000000003RMV', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=', '等于@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 8, 39, 1, 'creationtime', '0001ZZ10000000003RMW', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'Y', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '用户', 5, 40, 1, 'modifier', '0001ZZ10000000003RMX', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=', '等于@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 8, 41, 1, 'modifiedtime', '0001ZZ10000000003RMY', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'Y', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 1, 42, 1, 'emendenum', '0001ZZ10000000003RMZ', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 43, 1, 'billversionpk', '0001ZZ10000000003RN0', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 8, 44, 1, 'maketime', '0001ZZ10000000003RN1', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'Y', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 8, 45, 1, 'lastmaketime', '0001ZZ10000000003RN2', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'Y', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2019-11-04 17:06:12', '-99', 0, 46, 1, 'id_tempb', '0001ZZ10000000003RN3', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000003RLS', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_systemplate_base (ts, nodekey, funnode, layer, moduleid, templateid, pk_systemplate, devorg, pk_industry, tempstyle, pk_country, dr ) VALUES ('2019-11-04 17:06:12', 'qt', '40H10099', 0, '40H1', '0001ZZ10000000003RLS', '0001ZZ10000000003RN4', 'yonyouBQ', '~', 1, '~', 0 );