package com.disney.compliance.aging.POC.storedprocedures.rowmappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

public class AgingCandidateRowMapper implements RowMapper {

	private Integer userId;
	private Integer notificationFlag;
	private Integer isPub;
	
	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		AgedUser agedUser =  new AgedUser();
		
        this.userId = new Integer(StringUtils.trimAllWhitespace(rs.getString(1))); //location of the userId in the resultSet
        this.notificationFlag =  new Integer(StringUtils.trimAllWhitespace(rs.getString(2))); //location of the notificationFlag in the resultSet
        this.isPub = new Integer(StringUtils.trimAllWhitespace(rs.getString(3))); //location of the isPub flag in the resultSet
        
        agedUser.setUserId(userId);
        agedUser.setNotificationFlag(notificationFlag);
        agedUser.setIsPub(isPub);
        
        return agedUser;
	}


}
