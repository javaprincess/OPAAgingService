package aging.POC.storedprocedures.rowmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

public class DeactivationRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		DeactivationMessage deactivationMessage = new DeactivationMessage();
		
		
        Integer id = new Integer(StringUtils.trimAllWhitespace(rs.getString(2))); //location of the productId in the resultSet
        String status =  new String(StringUtils.trimAllWhitespace(rs.getString(3))); //location of the status message in the resultSet
        String message = new String(StringUtils.trimAllWhitespace(rs.getString(4))); //location of the detailed message in the resultSet
        
        
        deactivationMessage.setProductId(id);
        deactivationMessage.setStatus(status);
        deactivationMessage.setMessage(message);

        return deactivationMessage;
	}

}
