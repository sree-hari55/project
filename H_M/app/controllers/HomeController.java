package controllers;

import play.mvc.*;
import models.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.mvc.Controller;

import javax.inject.Inject;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import java.util.*;
import javax.xml.bind.*;
import javax.xml.soap.*;
import java.io.*;

// import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
//import play.libs.Json;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import models.metamap.model.*;
import models.metamap.*;

/*import org.jsoup.Jsoup;  
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;  
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;*/

import play.libs.Files.TemporaryFile;
import play.mvc.Http;
import play.mvc.Result;

import java.nio.file.Paths;
import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.google.cloud.translate.*;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

	private final Config config;

	private final String titleMessage = "Homing Admin";

	@Inject
	private DBConnector dbConnector;

	@Inject
	public HomeController(Config config, DBConnector dbConnect) {
		this.config = config;
		this.dbConnector = dbConnect;
	}

	final Logger logger = LoggerFactory.getLogger(this.getClass());


	/**
	 * An action that renders an landing HTML page.
	 * The configuration in the <code>routes</code> file means that
	 * this method will be called when the application receives a
	 * <code>GET</code> request with a path of <code>/</code>.
	 */
	public Result index() {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");
		String loginFlag = ctx().session().get("loginFlag");


		/*   System.out.println("loggedUser: " + loggedUser);
        System.out.println("loggedUserRole: " + loggedUserRole);
        System.out.println("loggedUserOrg: " + loggedUserOrg);*/

		if(loggedUser != null) {          
			return ok(views.html.index.render(titleMessage, loggedUser, loginFlag));
		} else {
			return redirect("/login");
		}
	}

	public Result tools() {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {          
			return ok(views.html.tools.render(titleMessage, loggedUser, loggedUserRole, loggedUserOrg));
		} else {
			return redirect("/");
		}
	}

	public Result dashboard() {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		if(loggedUser != null && loggedUserRole != null) {
			return ok(views.html.dashboard.render(titleMessage, loggedUser));
		} else {
			return redirect("/");
		}
	}

	public Result teamStats(String teamId, String teamName) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");

		/*Map<String, String[]> queryParameters = request().queryString();
        String teamId = queryParameters.get("teamId")[0];
        String teamName = queryParameters.get("teamName")[0];*/

		MattermostServiceManager mmServiceManager = new MattermostServiceManager(config);
		LDAPServiceManager ldapServiceManager = new LDAPServiceManager(config);

		if(loggedUser != null && loggedUserRole != null) {
			models.client.Channel broadcastChannel = ldapServiceManager.getBroadcastChannel(teamName);
			String response = mmServiceManager.getTeamStats(teamId, broadcastChannel.getChannelId());
			response = response + "###" + ldapServiceManager.getStats4LDAP(teamName);
			return ok(response);
		} else {
			return redirect("/");
		}
	}

	public Result importOrganization(Http.Request request) {

		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");

		String resultStr = "false";

		Http.MultipartFormData<TemporaryFile> body = request().body().asMultipartFormData();
		Http.MultipartFormData.FilePart<TemporaryFile> picture = body.getFile("excelFile");
		Map<String, String[]> queryParameters = body.asFormUrlEncoded();
		//String orgname = queryParameters.get("orgname")[0];
		String hisURL = queryParameters.get("hisURL")[0];
		//logger.info("orgname: "+orgname);
		logger.info("hisURL: "+hisURL);

		if (picture != null) {

			String fileName = picture.getFilename();
			long fileSize = picture.getFileSize();
			String contentType = picture.getContentType();
			TemporaryFile file = picture.getRef();
			logger.info("TemporaryFile: "+fileName);

			String FILE_EXSTENSION = FilenameUtils.getExtension(fileName);
			String TIMESTAMP = String.valueOf(new Date().getTime());
			String uploadBasePath = ConfigFactory.load().getString("uploadBasePath");
			String filename = loggedUser + "_" + TIMESTAMP + "." + FILE_EXSTENSION;

			//String fileUploadPath = uploadBasePath + USERNAME + "/";
			String fileFullPath = uploadBasePath + filename;

			logger.info("fileFullPath: "+fileFullPath);



			file.moveFileTo(Paths.get(fileFullPath), true);

			File inputFile = new File(fileFullPath);

			LDAPServiceManager ldapServiceManager = new LDAPServiceManager(config);

			ldapServiceManager.processBulkUpload(FILE_EXSTENSION, inputFile, hisURL);

			return redirect("/tools");

			/*ApplicationModel apm = new ApplicationModel(config);
            resultStr = apm.processFileUpload(file, fileName, chatMessageJson);*/

		}

		/*if(resultStr.equalsIgnoreCase("true")) {
            return redirect("/tools");
        }
        else {
            return redirect("/tools");
        } */
		return redirect("/tools");

	}

	/*    public Result test() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null) {          
            return ok(views.html.test.render(titleMessage, loggedUser));
        } else {
            return redirect("/login");
        }
    }

    public Result convertJson() {
        Map<String, String[]> queryParameters = request().body().asFormUrlEncoded();
        String xmlString = queryParameters.get("xmlString")[0];

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = null;
            //NodeList nodeList = null;
            Elements elementList = null;

            document = Jsoup.parse(xmlString, "", Parser.xmlParser());
            //elementList = document.getElementsByTag("message");
            for (Element e : document.select("message")) {
                ArrayList<Element> elements = e.children();
                for(Element el: elements) {
                    String name = el.attr("name");
                    System.out.println(name);
                }

            }
        }
        catch(Exception exp) {
            exp.printStackTrace();
        }

        return ok(xmlString);
    }*/   

	public Result translate() {
		/*Translate translate = TranslateOptions.getDefaultInstance().getService();
        Translation translation = translate.translate("¡Hola Mundo!");
        System.out.printf("Translated Text:\n\t%s\n", translation.getTranslatedText());*/
		String sourceLang = "";
		String targetLang = "";
		String response = "";

		try {
			java.util.Optional<java.lang.String> sL = request().header("sourceLang");
			java.util.Optional<java.lang.String> tL = request().header("targetLang");

			sourceLang = sL.toString();
			targetLang = tL.toString();

			if (sL.isPresent()) {
				sourceLang = sL.get();
			}
			if (tL.isPresent()) {
				targetLang = tL.get();
			}

			if(sourceLang.isEmpty() || targetLang.isEmpty()) {
				throw new Exception();

			} else {
				System.out.println(sourceLang);
				System.out.println(targetLang);

				String uploadBasePath = ConfigFactory.load().getString("uploadBasePath");
				String filename = targetLang + "-IN.json";

				//String fileUploadPath = uploadBasePath + USERNAME + "/";
				String fileFullPath = uploadBasePath + "i18n/" + filename;

				logger.info("fileFullPath: "+fileFullPath);

				File targetFile = new File(fileFullPath);

				if(targetFile.exists()) {
					try {
						String fileContent = TranslateText.readFile(fileFullPath, StandardCharsets.UTF_8);
						ObjectMapper mapper = new ObjectMapper();
						JsonNode responseObj = mapper.readTree(fileContent);
						return ok(responseObj);
					}
					catch(Exception exp) {
						exp.printStackTrace();
					}
				}

				JsonNode jsonNode = request().body().asJson();
				System.out.println("Json Input: "+jsonNode);

				Iterator keys = jsonNode.fieldNames();

				ArrayList<String> keyList = new ArrayList<String>();
				ArrayList<String> valueList = new ArrayList<String>();

				while(keys.hasNext()) {
					String currentDynamicKey = (String)keys.next();
					String currentDynamicValue = jsonNode.get(currentDynamicKey).textValue();

					System.out.println(currentDynamicKey + ":" + currentDynamicValue);
					keyList.add(currentDynamicKey);
					valueList.add(currentDynamicValue);
				}

				System.out.println(keyList);
				System.out.println(valueList);

				TranslateText tr = new TranslateText();
				response = tr.simpleTranslate(keyList, valueList, sourceLang, targetLang);

				ObjectMapper mapper = new ObjectMapper();
				JsonNode responseObj = mapper.readTree(response);
				return ok(responseObj);

			}


		}
		catch(Exception exp) {
			exp.printStackTrace();
			response = "Missing Parameters.\nsourceLang(in header), targetLang(in header), JSON(as body) are required.";
		}


		return ok(response);
	}

	/**
	 * Get Metamap data using disease name
	 * @return return success. 
	 */
	// screen -d -m -L /home/srit/UMLS/public_mm/bin/mmserver16
	public Result getMetamapValues() {

		/*String[] tokenArray = tokenRequest.getToken().split(",");
        MetamapValues metamapValues = metamapService.getMetamapData(tokenArray);
        if(metamapValues != null){
            return new ResponseEntity<MetamapValues>(metamapValues,HttpStatus.OK);
        }else{
            return new ResponseEntity<MetamapValues>(HttpStatus.NO_CONTENT);
        }*/
		Http.Request request = request();
		Gson gson =  new Gson();

		//Http.Request request = request();
		JsonNode jsonNode = request.body().asJson();
		System.out.println("Json Input: "+jsonNode);

		/*String json = jsonNode.asText("");
        System.out.println("Json Input: "+json);

        Tokens tokenRequest = gson.fromJson(json, Tokens.class);*/

		String[] tokenArray = jsonNode.get("token").asText().split(",");

		MetamapService metamapService = new MetamapService();
		metamapService.setDataSource(dbConnector.getDataSource());
		MetamapValues metamapValues = metamapService.getMetamapData(tokenArray);

		System.out.println(gson.toJson(metamapValues));

		/*return surveyRepository
                .saveSurveyResult(surveyResult)
                .thenApplyAsync(results -> ok("survey results stored sucessfully"), ec.current());*/

		return ok(gson.toJson(metamapValues));

	}

}
