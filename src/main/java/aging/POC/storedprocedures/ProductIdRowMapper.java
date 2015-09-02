package aging.POC.storedprocedures;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

public class ProductIdRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		 	ProductId productId = new ProductId();
	
	        Integer id = new Integer(StringUtils.trimAllWhitespace(rs.getString(1)));
	        productId.setProductId(id);

	        return productId;
	}

}
