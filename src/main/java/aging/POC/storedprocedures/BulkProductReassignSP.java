package aging.POC.storedprocedures;

import java.sql.Types;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import aging.POC.storedprocedures.rowmappers.DeactivationRowMapper;

public class BulkProductReassignSP extends StoredProcedure {
	
	public BulkProductReassignSP(DataSource dataSource) {
		super(dataSource, "dbo.spAgingReassignAssociateProductIdListTMA");
		
		RowMapper rowMapper = new DeactivationRowMapper();
		
		declareParameter(new SqlReturnResultSet("RESULT_LIST", rowMapper));
		
		SqlParameter oldUserIdParam = new SqlParameter("iOldUserId", Types.INTEGER);
		SqlParameter newUserIdParam = new SqlParameter("iNewUserId", Types.INTEGER);
		SqlParameter productIdParam = new SqlParameter("iProductId", Types.INTEGER);
		SqlParameter commentsParam = new SqlParameter("iComments", Types.VARCHAR);
		
		SqlParameter[] paramArray = {oldUserIdParam,
				newUserIdParam,
				productIdParam,
				commentsParam};
				
		setParameters(paramArray);
		compile();
	}
	
	 public Map execute(Integer oldUserId, 
			 Integer newUserId,
			 Integer productId,
			 String comment) {
		 
	
		return super.execute(oldUserId,
				newUserId,
				productId,
				comment);
	 }

}
