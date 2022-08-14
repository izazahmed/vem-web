package com.enerallies.vem.dao.schedule;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.enerallies.vem.beans.Schedule;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.schedule.AddScheduleDetails;
import com.enerallies.vem.exceptions.VEMAppException;

@Repository
public interface ScheduleDAO {
	
		public Response getScheduleList(GetUserResponse userDetailss) throws VEMAppException;
		public void setDataSource(DataSource dataSource);
		public void setDataSourceRead(DataSource dataSourceRead);
		public Response getFilterData(Schedule schedule,GetUserResponse userDetailss) throws VEMAppException;
		public Response getFilterSearch(Schedule schedule,GetUserResponse userDetailss) throws VEMAppException;
		public Response getCustomerList(GetUserResponse userDetailss,Schedule schedule) throws VEMAppException;
		public Response applySchedule(GetUserResponse userDetails,Schedule schedule) throws VEMAppException;
	
		public int addSchedule(AddScheduleDetails addScheduleDetails, int userId) throws VEMAppException;
		public int validateDuplicateSchedule(AddScheduleDetails addScheduleDetails) throws VEMAppException;
		public Response getScheduleDetails(AddScheduleDetails addScheduleDetails, GetUserResponse userDetails) throws VEMAppException;
		public Response updateSchedule(AddScheduleDetails addScheduleDetails, int userId) throws VEMAppException;
		public Response deleteSchedule(AddScheduleDetails addScheduleDetails, int userId) throws VEMAppException;
		public Response applyScheduleValidate(GetUserResponse userDetails,Schedule schedule) throws VEMAppException;
		public int addDeviceSchedule(AddScheduleDetails addScheduleDetails, int userId, boolean isCustom) throws VEMAppException;
		
		public int validateScheduleIsMappedToForecast(int scheduleId) throws VEMAppException;
		public int validateScheduleIsMappedToDevice(int scheduleId) throws VEMAppException;
		public void addCustomSchedule(String iotRespose, int deviceId);

}
