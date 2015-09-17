package com.disney.compliance.aging.POC.storedprocedures;


import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import com.disney.compliance.aging.POC.storedprocedures.rowmappers.ProductIdRowMapper;


public class ProductsUserIsInvolvedWithSP extends StoredProcedure {
	
	public ProductsUserIsInvolvedWithSP(DataSource dataSource) {
		//super(dataSource, "dbo.spAgingFindProductsUserIsInvolvedWithTMA");
		super(dataSource, "dbo.spFindProducts2");
		
		RowMapper rowMapper = new ProductIdRowMapper();
		
		declareParameter(new SqlReturnResultSet("RESULT_LIST", rowMapper));
		
		SqlParameter productListParam = new SqlParameter("ForProductList", Types.VARCHAR);
		SqlParameter companyListParam = new SqlParameter("ForCompanyList", Types.VARCHAR);
		SqlParameter productStatusListParam = new SqlParameter("ForProductStatusList", Types.NVARCHAR);
		SqlParameter userListParam =  new SqlParameter("ForUserList", Types.VARCHAR);
		SqlParameter phaseListParam =  new SqlParameter("ForPhaseList", Types.VARCHAR);
		SqlParameter keywordsParam =  new SqlParameter("ForKeywords", Types.NVARCHAR);
		SqlParameter optionAttributesParam = new SqlParameter("ForOptionAttributes", Types.VARCHAR);
		SqlParameter dateAttributesParam =  new SqlParameter("ForDateAttributes", Types.VARCHAR);
		SqlParameter integerAttributesParam =  new SqlParameter("ForIntegerAttributes", Types.VARCHAR);
		SqlParameter numberAttributesParam =  new SqlParameter("ForNumberAttributes", Types.VARCHAR);
		SqlParameter textAttributesParam = new SqlParameter("ForTextAttributes", Types.NVARCHAR);
		SqlParameter productTypeListParam = new SqlParameter("ForProductTypeList", Types.VARCHAR);
		
		SqlParameter[] paramArray = {productListParam,
				companyListParam,
				productStatusListParam,
				userListParam,
				phaseListParam,
				keywordsParam,
				optionAttributesParam,
				dateAttributesParam,
				integerAttributesParam,
				numberAttributesParam,
				textAttributesParam,
				productTypeListParam};
		setParameters(paramArray);
		compile();
	}
	
	 public Map execute(List<String> productStatusList, long userId) {
		 
	     return super.execute(null,
	    		 null,
	    		 productStatusList.toString().replace("[", "").replace("]",""), 
	    		 userId,
	    		 null,
	    		 null,
	    		 null,
	    		 null,
	    		 null,
	    		 null,
	    		 null,
	    		 null);
	 }
	 
}
