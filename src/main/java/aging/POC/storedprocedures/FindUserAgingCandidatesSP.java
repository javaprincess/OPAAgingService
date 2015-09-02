package aging.POC.storedprocedures;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.object.StoredProcedure;

public class FindUserAgingCandidatesSP extends StoredProcedure {
	
	public FindUserAgingCandidatesSP(DataSource dataSource) {
		super(dataSource, "dbo.spAgingFindUserAgingCandidatesTMA");
		compile();
	}
	
	 public Map<String, Object> execute() {
	        return super.execute(new LinkedHashMap<String, Object>());
	 }
	 
}
