package aging.POC.storedprocedures;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import aging.POC.storedprocedures.rowmappers.AgingCandidateRowMapper;
import aging.POC.storedprocedures.rowmappers.DeactivationRowMapper;

public class FindUserAgingCandidatesSP extends StoredProcedure {
	
	public FindUserAgingCandidatesSP(DataSource dataSource) {
		super(dataSource, "dbo.spAgingFindCandidatesTMA");
		
		RowMapper rowMapper = new AgingCandidateRowMapper();
		
		declareParameter(new SqlReturnResultSet("RESULT_LIST", rowMapper));
		compile();
	}
	
	 public Map<String, Object> execute() {
	        return super.execute(new LinkedHashMap<String, Object>());
	 }
	 
}
