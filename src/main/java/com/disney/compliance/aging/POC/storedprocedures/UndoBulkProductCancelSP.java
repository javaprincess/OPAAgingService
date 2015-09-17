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
import com.disney.compliance.aging.POC.storedprocedures.rowmappers.ReactivationRowMapper;

public class UndoBulkProductCancelSP extends StoredProcedure {
	
	public UndoBulkProductCancelSP(DataSource dataSource) {
		super(dataSource, "dbo.spUndoBulkAdminCancelProduct");
		
		RowMapper rowMapper = new ReactivationRowMapper();
		
		declareParameter(new SqlReturnResultSet("RESULT_LIST", rowMapper));
		
		SqlParameter productIdListParam = new SqlParameter("pProductIdList", Types.VARCHAR);
		SqlParameter userIdParam = new SqlParameter("userId", Types.INTEGER);
		SqlParameter commentsParam = new SqlParameter("comments", Types.VARCHAR);
		
		SqlParameter[] paramArray = {productIdListParam,
				commentsParam,
				userIdParam};
				
		setParameters(paramArray);
		compile();
	}
	
	 public Map execute(String productIdList, 
			 String comment,
			 Integer userId) {
		 
	
		return super.execute(productIdList,
				comment,
				userId);
	 }

}
