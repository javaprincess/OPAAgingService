package aging.POC.storedprocedures;


import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;


public class ProductsUserIsInvolvedWithSP extends StoredProcedure {
	
	public ProductsUserIsInvolvedWithSP(DataSource dataSource) {
		//super(dataSource, "dbo.spAgingFindProductsUserIsInvolvedWithTMA");
		super(dataSource, "dbo.spFindProducts_InAllLobs");
		
		RowMapper rowMapper = new ProductIdRowMapper();
		
		declareParameter(new SqlReturnResultSet("RESULT_LIST", rowMapper));
		
		SqlParameter productListParam = new SqlParameter("ForProductList", Types.VARCHAR);
		SqlParameter companyListParam = new SqlParameter("ForCompanyList", Types.VARCHAR);
		SqlParameter optionAttributesParam = new SqlParameter("ForOptionAttributes", Types.VARCHAR);
		SqlParameter textAttributesParam = new SqlParameter("ForTextAttributes", Types.NVARCHAR);
		SqlParameter productTypeListParam = new SqlParameter("ForProductTypeList", Types.VARCHAR);
		SqlParameter userListParam =  new SqlParameter("ForUserList", Types.VARCHAR);
		SqlParameter userIdParam = new SqlParameter("userId", Types.INTEGER);
		SqlParameter productStatusListParam = new SqlParameter("ForProductStatusList", Types.NVARCHAR);
		SqlParameter[] paramArray = {productListParam,
				companyListParam,
				productStatusListParam,
				userListParam,
				optionAttributesParam,
				textAttributesParam,
				productTypeListParam,
				userIdParam};
		setParameters(paramArray);
		compile();
	}
	
	 public Map execute(List<String> productStatusList, Integer userId) {
		 
	     return super.execute(null,
	    		 null,
	    		 productStatusList.toString().replace("[", "").replace("]",""), 
	    		 null,
	    		 null,
	    		 null,
	    		 null,
	    		 userId);
	 }
	 
}
