alter table request_ExtraInfo drop foreign key FK_request_Ex_2570;
alter table request_RetentionPolicyInfo drop foreign key FK_request_Re_5291;
alter table request_ClientNetworks drop foreign key FK_request_Cl_4686;
alter table request_TransferProtocols drop foreign key FK_request_Tr_6848;
alter table request_ExtraInfo drop foreign key FK_request_Ex_9422;
alter table request_Get drop foreign key FK_request_Ge_9630;
alter table request_Get drop foreign key FK_request_Ge_3811;
alter table request_Put drop foreign key FK_request_Pu_4665;
alter table request_Copy drop foreign key FK_request_Co_6810;
alter table request_Copy drop foreign key FK_request_Co_2630;
alter table request_VOMSAttributes drop foreign key FK_request_VO_5290;
alter table request_TransferProtocols drop foreign key FK_request_Tr_8127;
alter table request_RetentionPolicyInfo drop foreign key FK_request_Re_503;
alter table request_RetentionPolicyInfo drop foreign key FK_request_Re_2860;
alter table request_queue drop foreign key FK_request_qu_2651;
alter table request_queue drop foreign key FK_request_qu_4029;
alter table request_queue drop foreign key FK_request_qu_8833;
alter table request_queue drop foreign key FK_request_qu_8815;
alter table request_ExtraInfo drop foreign key FK_request_Ex_9425;
alter table request_ExtraInfo drop foreign key FK_request_Ex_8646;
alter table request_queue drop foreign key FK_request_qu_375;
alter table status_Get drop foreign key FK_status_Get_4853;
alter table status_Put drop foreign key FK_status_Put_3223;
alter table status_Copy drop foreign key FK_status_Cop_447;
alter table request_BoL drop foreign key FK_request_Bo_4166;
alter table request_BoL drop foreign key FK_request_Bo_8346;
alter table status_BoL drop foreign key FK_status_BoL_1747;
drop table if exists request_queue;
drop table if exists request_Get;
drop table if exists request_ExtraInfo;
drop table if exists request_RetentionPolicyInfo;
drop table if exists request_ClientNetworks;
drop table if exists request_TransferProtocols;
drop table if exists status_Get;
drop table if exists request_DirOption;
drop table if exists request_Put;
drop table if exists status_Put;
drop table if exists request_Copy;
drop table if exists status_Copy;
drop table if exists request_VOMSAttributes;
drop table if exists config_Protocols;
drop table if exists request_process;
drop table if exists config_RetentionPolicy;
drop table if exists config_AccessLatency;
drop table if exists config_FileStorageType;
drop table if exists config_AccessPattern;
drop table if exists config_ConnectionType;
drop table if exists config_Overwrite;
drop table if exists config_RequestType;
drop table if exists request_BoL;
drop table if exists status_BoL;












