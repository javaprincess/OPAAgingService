package aging.POC.storedprocedures;


import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.util.LinkedCaseInsensitiveMap;


public class BulkUserDeactivateSP extends StoredProcedure {
	
	public BulkUserDeactivateSP(DataSource dataSource) {
		super(dataSource, "dbo.spAgingBulkDeativateUsersTMA");
		SqlParameter userIdListParam = new SqlParameter("userIdList", Types.NVARCHAR);
		SqlParameter[] paramArray = {userIdListParam};
		setParameters(paramArray);
		compile();
	}
	
	 public Map<String, Object> execute(ArrayList<LinkedCaseInsensitiveMap<String>> userIdList) {

		 System.out.println(userIdList.toString().replace("[", "").replace("]", ""));
	     return super.execute(userIdList.toString().replace("[", "").replace("]",""));
	 }
	 
}
