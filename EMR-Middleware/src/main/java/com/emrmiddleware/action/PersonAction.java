package com.emrmiddleware.action;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emrmiddleware.api.APIClient;
import com.emrmiddleware.api.RestAPI;
import com.emrmiddleware.api.dto.AddressAPIDTO;
import com.emrmiddleware.api.dto.NameAPIDTO;
import com.emrmiddleware.api.dto.PersonAPIDTO;
import com.emrmiddleware.dao.PersonDAO;
import com.emrmiddleware.dto.PatientDTO;
import com.emrmiddleware.dto.PersonDTO;
import com.emrmiddleware.exception.DAOException;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.emrmiddleware.exception.ActionException;

public class PersonAction {

	private final Logger logger = LoggerFactory.getLogger(PersonAction.class);
	//RestAPI restapiintf = APIClient.getClient().create(RestAPI.class);
	APIClient apiclient = new APIClient();
	RestAPI restapiintf = apiclient.getClient().create(RestAPI.class);

	public ArrayList<PersonDTO> setPersons(ArrayList<PersonAPIDTO> personList) throws DAOException, ActionException {
		ArrayList<PersonDTO> persons = new ArrayList<PersonDTO>();
		PersonDTO persondto ;
		PersonAPIDTO personforerror = new PersonAPIDTO();
		boolean isPersonSet=true;
		Gson gson = new Gson();
		int i=0;
		try {
			for ( PersonAPIDTO person : personList) {
				// processPerson(person);
				personforerror=person;
				logger.info("counter : "+i++);
				if (isPersonExists(person.getUuid())) {
					isPersonSet=editPersonOpenMRS(person);
				} else {
					isPersonSet=addPersonOpenMRS(person);
				}
				persondto = new PersonDTO();
				persondto.setUuid(person.getUuid());
				persondto.setSyncd(isPersonSet);
				persons.add(persondto);
			}
		} catch (Exception e) {
			 logger.error("Error occurred for json string : "+gson.toJson(personforerror));
			 logger.error(e.getMessage(),e);
			//throw new ActionException(e.getMessage(), e);
		}
		return persons;

	}

	private boolean isPersonExists(String personuuid) throws DAOException {
		boolean isPersonExists = false;
		PersonDAO persondao = new PersonDAO();
		PersonDTO persondto = persondao.getPerson(personuuid);
		if (persondto != null) {
			isPersonExists = true;
		}
		return isPersonExists;

	}

	/*
	 * private PersonAPIDTO getPersonFromPatient(PatientDTO patientdto){
	 * PersonAPIDTO persondto = new PersonAPIDTO(); NameAPIDTO namedto = new
	 * NameAPIDTO(); AddressAPIDTO addressdto = new AddressAPIDTO();
	 * persondto.setUuid(patientdto.getUuid());
	 * namedto.setGivenName(patientdto.getFirstname());
	 * namedto.setMiddleName(patientdto.getMiddlename());
	 * namedto.setFamilyName(patientdto.getLastname());
	 * persondto.addName(namedto);
	 * addressdto.setAddress1(patientdto.getAddress1());
	 * addressdto.setAddress2(patientdto.getAddress2());
	 * addressdto.setCityVillage(patientdto.getCityvillage());
	 * addressdto.setCountry(patientdto.getCountry());
	 * addressdto.setPostalCode(patientdto.getPostalcode());
	 * addressdto.setStateProvince(patientdto.getStateprovince());
	 * persondto.addAddresses(addressdto); persondto.setBirthdate("1996-02-06");
	 * persondto.setGender(patientdto.getGender());
	 * 
	 * return persondto;
	 * 
	 * }
	 */
	private boolean addPersonOpenMRS(PersonAPIDTO persondto) {
		Gson gson = new Gson();
		String val = "";
		logger.info("patient value : " + gson.toJson(persondto));
		
		try {
			Call<ResponseBody> callperson = restapiintf.addPerson(persondto);
			Response<ResponseBody> response = callperson.execute();
			if (response.isSuccessful()) {
				val = response.body().string();
			} else {
				val = response.errorBody().string();
				logger.error("REST failed : " + val);
				return false;
			}
			logger.info("Response is : " + val);
		} catch (IOException | NullPointerException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			return false;
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	private boolean editPersonOpenMRS(PersonAPIDTO persondto) {
		Gson gson = new Gson();
		String val = "";
		logger.info("edit patient value : " + gson.toJson(persondto));
		
		try {
			Call<ResponseBody> callperson = restapiintf.editPerson(persondto.getUuid(), persondto);
			Response<ResponseBody> response = callperson.execute();
			if (response.isSuccessful()) {
				val = response.body().string();
			} else {
				val = response.errorBody().string();
				logger.error("REST failed : " + val);
				return false;
			}
			logger.info("Response for edit is : " + val);
		} catch (IOException | NullPointerException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			return false;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

}
