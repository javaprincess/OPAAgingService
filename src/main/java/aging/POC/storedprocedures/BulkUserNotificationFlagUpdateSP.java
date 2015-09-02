package aging.POC.storedprocedures;


import java.sql.Types;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;


public class BulkUserNotificationFlagUpdateSP extends StoredProcedure {
	
	public BulkUserNotificationFlagUpdateSP(DataSource dataSource) {
		super(dataSource, "dbo.spAgingBulkUserNotificationFlagUpdateTMA");
		SqlParameter userIdListParam = new SqlParameter("userIdList", Types.NVARCHAR);
		SqlParameter newNotificationFlagValueParam = new SqlParameter("newNotificationFlagValue", Types.INTEGER);
		SqlParameter[] paramArray = {userIdListParam, newNotificationFlagValueParam};
		setParameters(paramArray);
		compile();
	}
	
	 public Map<String, Object> execute(List<String> notificationList, Integer newNotificationFlagValue) {

		 System.out.println(notificationList.toString().replace("[", "").replace("]", ""));
	     return super.execute(notificationList.toString().replace("[", "").replace("]",""), newNotificationFlagValue);
	 }
	 
}
