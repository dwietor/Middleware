package com.emrmiddleware.dao;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.emrmiddleware.conf.DBconfig;
import com.emrmiddleware.dmo.EncounterDMO;
import com.emrmiddleware.dmo.LocationDMO;
import com.emrmiddleware.dto.EncounterDTO;
import com.emrmiddleware.dto.LocationDTO;
import com.emrmiddleware.exception.DAOException;

public class LocationDAO {

	
	public ArrayList<LocationDTO> getLocations(Timestamp lastdatapulltime) throws DAOException {

		SqlSessionFactory sessionfactory = DBconfig.getSessionFactory();
		SqlSession session = sessionfactory.openSession();
		ArrayList<LocationDTO> locationlist = new ArrayList<LocationDTO>();
		try {

			LocationDMO locationdmo = session.getMapper(LocationDMO.class);
			locationlist = locationdmo.getLocations(lastdatapulltime);
			return locationlist;
		} catch (PersistenceException e) {
			throw new DAOException(e.getMessage(), e);
		} finally {
			session.close();
		}
	}
}
