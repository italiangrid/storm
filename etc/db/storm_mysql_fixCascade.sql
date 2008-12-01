use storm_db;

alter table request_ExtraInfo drop foreign key FK_request_Ex_2570;
alter table request_RetentionPolicyInfo drop foreign key FK_request_Re_5291;
alter table request_ClientNetworks drop foreign key FK_request_Cl_4686;
alter table request_TransferProtocols drop foreign key FK_request_Tr_6848;
alter table request_ExtraInfo drop foreign key FK_request_Ex_9422;
alter table request_Get drop foreign key FK_request_Ge_3811;
alter table request_Put drop foreign key FK_request_Pu_4665;
alter table request_Copy drop foreign key FK_request_Co_6810;
alter table request_VOMSAttributes drop foreign key FK_request_VO_5290;
alter table request_ExtraInfo drop foreign key FK_request_Ex_9425;
alter table request_ExtraInfo drop foreign key FK_request_Ex_8646;
alter table status_Get drop foreign key FK_status_Get_4853;
alter table status_Put drop foreign key FK_status_Put_3223;
alter table status_Copy drop foreign key FK_status_Cop_447;
alter table request_BoL drop foreign key FK_request_Bo_8346;
alter table status_BoL drop foreign key FK_status_BoL_1747;

alter table request_ExtraInfo drop index FK_request_Ex_2570;
alter table request_RetentionPolicyInfo drop index FK_request_Re_5291;
alter table request_ClientNetworks drop index FK_request_Cl_4686;
alter table request_TransferProtocols drop index FK_request_Tr_6848;
alter table request_ExtraInfo drop index FK_request_Ex_9422;
alter table request_Get drop index FK_request_Ge_3811;
alter table request_Put drop index FK_request_Pu_4665;
alter table request_Copy drop index FK_request_Co_6810;
alter table request_VOMSAttributes drop index FK_request_VO_5290;
alter table request_ExtraInfo drop index FK_request_Ex_9425;
alter table request_ExtraInfo drop index FK_request_Ex_8646;
alter table status_Get drop index FK_status_Get_4853;
alter table status_Put drop index FK_status_Put_3223;
alter table status_Copy drop index FK_status_Cop_447;
alter table request_BoL drop index FK_request_Bo_8346;
alter table status_BoL drop index FK_status_BoL_1747;



ALTER TABLE request_ExtraInfo add index FK_request_Ex_2570 (request_queueID), add constraint FK_request_Ex_2570 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;

ALTER TABLE request_RetentionPolicyInfo add index FK_request_Re_5291 (request_queueID), add constraint FK_request_Re_5291 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;

ALTER TABLE request_ClientNetworks add index FK_request_Cl_4686 (request_queueID), add constraint FK_request_Cl_4686 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;

ALTER TABLE request_TransferProtocols add index FK_request_Tr_6848 (request_queueID), add constraint FK_request_Tr_6848 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;

ALTER TABLE request_ExtraInfo add index FK_request_Ex_9422 (status_GetID), add constraint FK_request_Ex_9422 foreign key (status_GetID) references status_Get (ID) ON DELETE CASCADE;

ALTER TABLE request_Get add index FK_request_Ge_3811 (request_queueID), add constraint FK_request_Ge_3811 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;

ALTER TABLE request_Put add index FK_request_Pu_4665 (request_queueID), add constraint FK_request_Pu_4665 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;

ALTER TABLE request_Copy add index FK_request_Co_6810 (request_queueID), add constraint FK_request_Co_6810 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;

ALTER TABLE request_VOMSAttributes add index FK_request_VO_5290 (request_queueID), add constraint FK_request_VO_5290 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;

ALTER TABLE request_ExtraInfo add index FK_request_Ex_9425 (request_queueID2), add constraint FK_request_Ex_9425 foreign key (request_queueID2) references request_queue (ID) ON DELETE CASCADE;

ALTER TABLE request_ExtraInfo add index FK_request_Ex_8646 (status_PutID), add constraint FK_request_Ex_8646 foreign key (status_PutID) references status_Put (ID) ON DELETE CASCADE;

ALTER TABLE status_Get add index FK_status_Get_4853 (request_GetID), add constraint FK_status_Get_4853 foreign key (request_GetID) references request_Get (ID) ON DELETE CASCADE;

ALTER TABLE status_Put add index FK_status_Put_3223 (request_PutID), add constraint FK_status_Put_3223 foreign key (request_PutID) references request_Put (ID) ON DELETE CASCADE;

ALTER TABLE status_Copy add index FK_status_Cop_447 (request_CopyID), add constraint FK_status_Cop_447 foreign key (request_CopyID) references request_Copy (ID) ON DELETE CASCADE;


ALTER TABLE request_BoL add index FK_request_Bo_8346 (request_queueID), add constraint FK_request_Bo_8346 foreign key (request_queueID) references request_queue (ID) ON DELETE CASCADE;

ALTER TABLE status_BoL add index FK_status_BoL_1747 (request_BoLID), add constraint FK_status_BoL_1747 foreign key (request_BoLID) references request_BoL (ID) ON DELETE CASCADE;



