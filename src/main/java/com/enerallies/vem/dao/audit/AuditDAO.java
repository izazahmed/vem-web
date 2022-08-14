package com.enerallies.vem.dao.audit;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.enerallies.vem.beans.audit.AuditRequest;


/**
 * File Name : AuditDAO 
 * AuditDAO  is used to serve all the database level operations related to audit.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        1-11-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 1-11-2016		Arun		    File Created.
 *
 */
@Repository
public interface AuditDAO {

	
	/**
	  * insertAuditLog to do operations related to audit log.
	  * @param auditRequest
	 */
	public void insertAuditLog(AuditRequest auditRequest);
	
	 /**
	  * setDataSource to be used to initialize database resources i.e. connection.
	  * @param dataSource
	 */
	 public void setDataSource(DataSource dataSource);
	
}
