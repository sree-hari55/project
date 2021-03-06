package controllers;

import models.DBServiceManager;
import models.SurveyRepository;
import models.ReportLogRepository;
import models.dto.ResponseDto;
import models.dto.ReturnSurveyResultDto;
import models.dto.SurveyDto;
import models.dto.SurveyHeaderDto;
import models.dto.SurveyResultDto;
import models.entity.SurveyDetail;
import models.entity.SurveyHeader;
import models.entity.SurveyResult;
import models.entity.ReportLog;
import play.data.FormFactory;
import play.libs.Json;
import static play.libs.Json.toJson;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import play.mvc.Result;
import play.mvc.Http;
import views.html.viewPages.view;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.naming.NamingException;

import play.data.DynamicForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * This class is responsible for managing the  database of the application
 *
 */

public class DBController extends Controller {
	private final FormFactory formFactory;
	private final DBServiceManager dbServiceManager;
	private final HttpExecutionContext httpExecutionContext;
	private final SurveyRepository surveyRepository;
	private final ReportLogRepository reportLogRepository;
	private final Gson gson;
	private ObjectMapper objectMapper;

	ResponseDto responseDto=null;

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	public DBController(FormFactory formFactory, SurveyRepository surveyRepository, DBServiceManager dbServiceManager,
			HttpExecutionContext httpExecutionContext, ReportLogRepository reportLogRepository) {
		this.formFactory = formFactory;
		this.dbServiceManager = dbServiceManager;
		this.httpExecutionContext = httpExecutionContext;
		this.surveyRepository = surveyRepository;
		this.reportLogRepository = reportLogRepository;
		gson=new Gson();
		this.objectMapper= new ObjectMapper();
	}

	public Result createSurvey() {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");
		if (loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			SurveyHeader surveyHeader=null;
			String response = "";
			try {
				Http.Request request = request();
				JsonNode json = request.body().asJson();
				SurveyDto surveyDto=Json.fromJson(json,SurveyDto.class);
			    surveyHeader=dbServiceManager.createSurvey(surveyDto);
				response = "survey data stored successful.";
				responseDto=new  ResponseDto();
				responseDto.setStatusString(response);
				responseDto.setData(surveyHeader);
				return ok(toJson(responseDto));
			} catch (Exception exp) {
				exp.printStackTrace();
				response = "data creation failed.";
				return badRequest(toJson(response));
			}

		} else {

			return unauthorized("Unauthorized");
		}

	}

	public Result getSurveyList() throws InterruptedException, ExecutionException {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");
		
		if (loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";
			List<SurveyHeaderDto> surveyDtoList = null;
			try {
				surveyDtoList = dbServiceManager.getSurveyList();
				return ok(views.html.viewPages.viewSurveyHeaderList.render(surveyDtoList));
				//return ok(toJson(surveyDtoList));
			} catch (Exception e) {
				e.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
			 
		} else {

			return unauthorized("Unauthorized");
		}

	}

	public Result getSurveyDetailsList(String surveyName) throws InterruptedException, ExecutionException {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");
		if (loggedUser != null && loggedUserRole != null && loggedUserOrg != null && loggedUserOrg.equals("")) {

			String response = "";
			List<SurveyHeaderDto> surveyDtoList = new ArrayList<SurveyHeaderDto>();
			try {
				List<SurveyHeader> surveyHeaderList = dbServiceManager.getSurveyList(surveyName);

			
			  for(SurveyHeader surveyHeader:surveyHeaderList) {
					SurveyHeaderDto surveyDto=new SurveyHeaderDto();
					surveyDto.setId(surveyHeader.getId());
					surveyDto.setOrganizationName(surveyHeader.getOrganizationName());
					surveyDto.setOrganizationUnitName(surveyHeader.getOrganizationUnitName());
					surveyDto.setChannel(surveyHeader.getChannel());
					surveyDto.setStartDate(surveyHeader.getStartDate());
					surveyDto.setEndDate(surveyHeader.getEndDate());
					surveyDto.setSurveyName(surveyHeader.getSurveyName());
					surveyDtoList.add(surveyDto);
				}
				
				List<SurveyDetail> surveyDetails = surveyHeaderList.stream().flatMap(s -> s.getSurveyDetails().stream()).collect(Collectors.toList());
				
				
				return ok(views.html.viewPages.viewSurveyDetailsList.render(surveyDtoList,surveyDetails));
				//return ok(toJson(surveyHeaderList));
			}catch (Exception e) {
				e.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}			
		} else {
			return unauthorized("Unauthorized");
		}
	}

	public Result getSurveyDetailsJsonList(String surveyName) throws InterruptedException, ExecutionException {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");
		if (loggedUser != null && loggedUserRole != null && loggedUserOrg != null && loggedUserOrg.equals("")) {

			String response = "";
			List<SurveyHeaderDto> surveyDtoList = new ArrayList<SurveyHeaderDto>();
			try {
				List<SurveyHeader> surveyHeaderList = dbServiceManager.getSurveyList(surveyName);

			
			/*  for(SurveyHeader surveyHeader:surveyHeaderList) {
					SurveyHeaderDto surveyDto=new SurveyHeaderDto();
					surveyDto.setId(surveyHeader.getId());
					surveyDto.setOrganizationName(surveyHeader.getOrganizationName());
					surveyDto.setOrganizationUnitName(surveyHeader.getOrganizationUnitName());
					surveyDto.setChannel(surveyHeader.getChannel());
					surveyDto.setStartDate(surveyHeader.getStartDate());
					surveyDto.setEndDate(surveyHeader.getEndDate());
					surveyDto.setSurveyName(surveyHeader.getSurveyName());
					surveyDtoList.add(surveyDto);
				}
				
				List<SurveyDetail> surveyDetails = surveyHeaderList.stream().flatMap(s -> s.getSurveyDetails().stream()).collect(Collectors.toList());
				*/
				
				//return ok(views.html.viewPages.viewSurveyDetailsList.render(surveyDtoList,surveyDetails));
				return ok(toJson(surveyHeaderList));
			}catch (Exception e) {
				e.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}			
		} else {
			return unauthorized("Unauthorized");
		}
	}

	public Result updateSurvey() {

		String loggedUser =ctx().session().get("loggedUser"); 
		String loggedUserRole =ctx().session().get("loggedUserRole");
		String loggedUserOrg =ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null &&	loggedUserOrg.equals("")) { 
			
			String response = "";
			SurveyHeader surveyHeader=null;
			try {
				 Http.Request request = request();
			     JsonNode  json = request.body().asJson();
				SurveyDto surveyDto=Json.fromJson(json,SurveyDto.class);
				surveyHeader=dbServiceManager.updateSurvey(surveyDto);
				response = "survey data updated successful.";
				responseDto=new  ResponseDto();
				responseDto.setStatusString(response);
				responseDto.setData(surveyHeader);
				return ok(toJson(responseDto));
			}catch(Exception exp) { 
				exp.printStackTrace(); 
				response ="data creation failed.";
				return badRequest(toJson(response));
			}

		} else {

			return unauthorized("Unauthorized"); }

	}

	public Result getSurveyResultList(String channelId) {

		String loggedUser =ctx().session().get("loggedUser"); 
		String loggedUserRole =ctx().session().get("loggedUserRole");
		String loggedUserOrg =ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null &&	loggedUserOrg.equals("")) { 
			String response=null;
			Map<String, Map<List<String>,List<Long>>> mapQuestions=null;
			try {
				
				mapQuestions=dbServiceManager.getSurveyResultForRatingQuestions(channelId);
				
				//return 	ok(views.html.viewPages.viewSurveyResultList.render(mapQuestions));
				return ok(toJson(mapQuestions));
			}catch(Exception exp) { 
				exp.printStackTrace(); 
				response ="data creation failed.";
				return badRequest(toJson(response));
			}

		} else {
			return unauthorized("Unauthorized");
		}
	}

	public Result getSurveyResultsWithoutRatingQuestions(String channelId) {
		String loggedUser =ctx().session().get("loggedUser"); 
		String loggedUserRole =ctx().session().get("loggedUserRole");
		String loggedUserOrg =ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null &&	loggedUserOrg.equals("")) { 
			String response=null;
			List<ReturnSurveyResultDto> resultDtos=null;
			try {
				resultDtos=dbServiceManager.getSurveyResultsWithoutRatingQuestions(channelId);
				return ok(toJson(resultDtos));
			}catch(Exception exp) { 
				exp.printStackTrace(); 
				response ="data creation failed.";
				return badRequest(toJson(response));
			}

		} else {
			return unauthorized("Unauthorized");
		}

	}


	public Result getErrorReports() throws InterruptedException, ExecutionException {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		if (loggedUser != null && loggedUserRole != null) {
			String response = "";
			List<ReportLog> reportLogs = new ArrayList<ReportLog>();
			try {
				CompletionStage<List<ReportLog>> reports = reportLogRepository.getReportLogs();
				List<ReportLog> reportLogList = reports.toCompletableFuture().get();
				reportLogs = new ArrayList<>(reportLogList);
				logger.debug("Reports List {} " + reportLogs);
				//return ok(reportLogList.toString());
				return ok(views.html.viewPages.viewErrorLogs.render(loggedUser, reportLogs));
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println(e.getMessage());
				response = "Error occurred while reading the data.";
				response = e.getMessage();
				return badRequest(response);
			}
		} else {
			return unauthorized("Unauthorized");
		}

	}
}
