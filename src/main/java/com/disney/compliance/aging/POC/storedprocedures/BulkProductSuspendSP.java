package com.disney.compliance.aging.POC.storedprocedures;


import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import com.disney.compliance.aging.POC.storedprocedures.rowmappers.DeactivationRowMapper;


public class BulkProductSuspendSP extends StoredProcedure {
	
	public BulkProductSuspendSP(DataSource dataSource) {
		//super(dataSource, "dbo.spAgingFindProductsUserIsInvolvedWithTMA");
		super(dataSource, "dbo.spAdminBulkSuspendProducts");
		
		RowMapper rowMapper = new DeactivationRowMapper();
		
		declareParameter(new SqlReturnResultSet("RESULT_LIST", rowMapper));
		
		SqlParameter productIdListParam = new SqlParameter("pProductIdList", Types.VARCHAR);
		SqlParameter commentsParam = new SqlParameter("comments", Types.VARCHAR);
		SqlParameter userIdParam = new SqlParameter("userId", Types.INTEGER);
		
		
		SqlParameter[] paramArray = {productIdListParam,
				commentsParam,
				userIdParam};
		setParameters(paramArray);
		compile();
	}
	
	 public Map execute(String productIdList, String comments, Integer userId) {
	
		return super.execute(productIdList, 
	   		 comments,
	   		 userId);
	 }
	 
}
