package com.disney.compliance.aging.POC.storedprocedures;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;

import com.disney.compliance.aging.POC.storedprocedures.rowmappers.DeactivationRowMapper;

public class BulkProductCancelSP extends StoredProcedure {
	
	public BulkProductCancelSP(DataSource dataSource) {
		super(dataSource, "dbo.spBulkCancel");
		
		RowMapper rowMapper = new DeactivationRowMapper();
		
		declareParameter(new SqlReturnResultSet("RESULT_LIST", rowMapper));
		
		SqlParameter productIdListParam = new SqlParameter("pProductIdList", Types.VARCHAR);
		SqlParameter adminUserIdParam = new SqlParameter("userId", Types.INTEGER);
		SqlParameter userIdParam = new SqlParameter("inboxUserId", Types.INTEGER);
		SqlParameter commentsParam = new SqlParameter("comments", Types.VARCHAR);
		
		SqlParameter[] paramArray = {productIdListParam,
				adminUserIdParam,
				userIdParam,
				commentsParam};
				
		setParameters(paramArray);
		compile();
	}
	
	 public Map execute(String productIdList, 
			 Integer adminUserId,
			 Integer userId,
			 String comment) {
		 
	
		return super.execute(productIdList,
				adminUserId,
				userId,
				comment);
	 }

}
