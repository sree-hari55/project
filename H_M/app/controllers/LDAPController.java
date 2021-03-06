package controllers;

import play.mvc.*;
import models.*;
import models.entity.SurveyHeader;
import models.ldap.*;

import java.util.*;
import java.util.stream.Collectors;

import com.typesafe.config.Config;
import play.mvc.Controller;

import javax.inject.Inject;

import play.data.FormFactory;
import play.data.Form;
import play.data.DynamicForm;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import play.mvc.*;
import play.libs.ws.*;

import models.ldap.Organization;
/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class LDAPController extends Controller {

    private final Config config;
    LDAPServiceManager ldapServiceManager;
 

    @Inject
    FormFactory formFactory;

    @Inject
    public LDAPController(Config config) {
        this.config = config;
        ldapServiceManager = new LDAPServiceManager(this.config);
    }

    public Result createOrganization() {

        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null && loggedUserOrg.equals("")) {
            String response = "";
            Map<String, String[]> queryParameters = request().queryString();
            String orgName = queryParameters.get("orgName")[0];
            String orgSupportEmail = queryParameters.get("orgSupportEmail")[0];
            String orgSupportContactNumber = queryParameters.get("orgSupportContactNumber")[0];

            try {
                ldapServiceManager.createOrganization(orgName, orgSupportEmail, orgSupportContactNumber);
                response = "Organization creation successful.";
                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Organization creation failed.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }
        	        
    }

    public Result getOrganizationList() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            ArrayList<String> orgList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();
            String displayFlag = queryParameters.get("displayFlag")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(displayFlag.equals("page")) {
                    return ok(views.html.viewPages.viewOrganizationList.render(orgList));    
                } else {
                    String orgListString = String.join(",", orgList);
                    return ok(orgListString);    
                }            
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }      
    }

    public Result getTeams() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            ArrayList<String> orgList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();

            try {
                orgList = ldapServiceManager.getTeams();
                String orgListString = String.join("###", orgList);
                return ok(orgListString);           
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }      
    }

    public Result createOrganizationUnit() {

        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {

            String response = "";
            Map<String, String[]> queryParameters = request().queryString();
            String orgUnitName = queryParameters.get("orgUnitName")[0];
            String orgName = queryParameters.get("orgName")[0];
            String orgUnitLocation = queryParameters.get("orgUnitLocation")[0];
            String orgUnitLatitude = queryParameters.get("orgUnitLatitude")[0];
            String orgUnitLongitude = queryParameters.get("orgUnitLongitude")[0];
            String orgUnitPhone = queryParameters.get("orgUnitPhone")[0];

            try {
                ldapServiceManager.createOrganizationUnit(orgUnitName, orgName, orgUnitLocation, orgUnitLatitude, orgUnitLongitude, orgUnitPhone);
                response = "Organization Unit creation successful.";
                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Organization Unit creation failed.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }
    }

    public Result getOrganizationUnitList() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            ArrayList<String> orgUnitList = new ArrayList<String>();
            ArrayList<String> orgList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();
            String displayFlag = queryParameters.get("displayFlag")[0];
            String orgName = queryParameters.get("orgName")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        orgUnitList = ldapServiceManager.getOrganizationUnitList(orgList.get(0));    
                    } 
                } else {
                    orgUnitList = ldapServiceManager.getOrganizationUnitList(orgName);    
                }               

                if(displayFlag.equals("page")) {
                    return ok(views.html.viewPages.viewOrganizationUnitList.render(orgList, orgUnitList));    
                } else {
                    //orgUnitList = ldapServiceManager.getOrganizationUnitList(orgName); 
                    String orgUnitListString = String.join(",", orgUnitList);
                    return ok(orgUnitListString);
                }            
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }

                
    }

    public Result createDepartment() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            Map<String, String[]> queryParameters = request().queryString();
            String departmentName = queryParameters.get("departmentName")[0];
            String orgName = queryParameters.get("orgName")[0];
            String orgUnitName = queryParameters.get("orgUnitName")[0];

            try {
                ldapServiceManager.createDepartment(departmentName, orgUnitName, orgName);
                response = "Department creation successful.";
                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Department creation failed.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }
    }

    public Result getDepartmentList() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            ArrayList<String> orgUnitList = new ArrayList<String>();
            ArrayList<String> orgList = new ArrayList<String>();
            ArrayList<String> departmentList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();
            String displayFlag = queryParameters.get("displayFlag")[0];
            String orgName = queryParameters.get("orgName")[0];
            String orgUnitName = queryParameters.get("orgUnitName")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(!orgName.equals("") && !orgUnitName.equals("")) {
                    departmentList = ldapServiceManager.getDepartmentList(orgName, orgUnitName);
                } else {
                    String oName = orgList.get(0);
                    orgUnitList = ldapServiceManager.getOrganizationUnitList(oName);
                    departmentList = ldapServiceManager.getDepartmentList(oName, orgUnitList.get(0));   
                }

                if(displayFlag.equals("page")) {
                    return ok(views.html.viewPages.viewDepartmentList.render(orgList, orgUnitList, departmentList));    
                } else {
                    String departmentListString = String.join(",", departmentList);
                    return ok(departmentListString);
                }            
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }


    }

    public Result createRole() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            Map<String, String[]> queryParameters = request().queryString();
            String roleName = queryParameters.get("roleName")[0];
            String roleType = queryParameters.get("roleType")[0];
            String orgName = queryParameters.get("orgName")[0];
            String accessInfo = queryParameters.get("accessInfo")[0];
            String orgUnitName = queryParameters.get("orgUnitName")[0];
            String depName = queryParameters.get("depName")[0];

            try {
                ldapServiceManager.createRole(roleName, roleType, orgName, accessInfo, orgUnitName, depName);
                response = "Role creation successful.";
                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Role creation failed.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }
    }

    public Result getRoleList() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            
            ArrayList<String> orgList = new ArrayList<String>();
            ArrayList<String> roleList = new ArrayList<String>();
            ArrayList<Role> roles = new ArrayList<Role>();

            Map<String, String[]> queryParameters = request().queryString();
            String displayFlag = queryParameters.get("displayFlag")[0];
            String orgName = queryParameters.get("orgName")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        roleList = ldapServiceManager.getRoleList(orgList.get(0), "page");
                        roles = ldapServiceManager.getRoles(orgList.get(0));   
                    } 
                } else {
                    roleList = ldapServiceManager.getRoleList(orgName, "page");
                    roles = ldapServiceManager.getRoles(orgName);
                }               

                if(displayFlag.equals("page")) {
                    return ok(views.html.viewPages.viewRoleList.render(orgList, roleList, roles));    
                } else {
                    String roleListString = String.join(",", roleList);
                    return ok(roleListString);
                }           
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }               
    }

    public Result createUser() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            Map<String, String[]> queryParameters = request().queryString();

            String user_first_name = queryParameters.get("user_first_name")[0];
            String user_last_name = queryParameters.get("user_last_name")[0];
            String user_name = queryParameters.get("user_name")[0];
            String user_password = queryParameters.get("user_password")[0];
            String user_email = queryParameters.get("user_email")[0];
            String user_qualification = queryParameters.get("user_qualification")[0];
            String user_specialization = queryParameters.get("user_specialization")[0];
            String user_employee_id = queryParameters.get("user_employee_id")[0];
            String user_aadhaar = queryParameters.get("user_aadhaar")[0];
            String user_pan = queryParameters.get("user_pan")[0];
            String user_passport = queryParameters.get("user_passport")[0];
            String user_phone = queryParameters.get("user_phone")[0];
            
            String user_org = queryParameters.get("user_org")[0];
            String user_org_unit = queryParameters.get("user_org_unit")[0];
            String user_role = queryParameters.get("user_role")[0];
            String user_department = queryParameters.get("user_department")[0];

            String genderInfo = queryParameters.get("genderInfo")[0];
            String accessInfo = queryParameters.get("accessInfo")[0];
            String universalAccessInfo = queryParameters.get("universalAccessInfo")[0];        

            try {
                ldapServiceManager.createUser(user_first_name,user_last_name,user_name,user_password,user_email,user_qualification,user_specialization,user_employee_id,user_aadhaar,user_pan,user_passport,user_phone,user_org,user_org_unit,user_role,user_department,genderInfo,accessInfo,universalAccessInfo);
                response = "User creation successful.";
                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "User creation failed.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }                    
    }

    public Result getUserList() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            
            ArrayList<User> userList = new ArrayList<User>();
            ArrayList<String> orgList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();
            String displayFlag = queryParameters.get("displayFlag")[0];
            String orgName = queryParameters.get("orgName")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        userList = ldapServiceManager.getUsers(orgList.get(0));    
                    } 
                } else {
                    userList = ldapServiceManager.getUsers(orgName);
                }               

                if(displayFlag.equals("page")) {
                    return ok(views.html.viewPages.viewUserList.render(orgList, userList));    
                }     
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
            response = "Error occurred while reading the data.";
            return badRequest(response);
        } else {
            
            return unauthorized("Unauthorized");
        }
    }


    public Result createTabTemplate() {

        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            Map<String, String[]> queryParameters = request().queryString();
            String tabTemplateName = queryParameters.get("tabTemplateName")[0];
            String orgName = queryParameters.get("orgName")[0];

            try {
                ldapServiceManager.createTabtemplate(tabTemplateName, orgName);
                response = "Tab Template creation successful.";
                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Tab Template creation failed.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }       
    }

    public Result getTabTemplateList() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            
            ArrayList<String> orgList = new ArrayList<String>();
            ArrayList<TabTemplate> tabTemplateList = new ArrayList<TabTemplate>();

            Map<String, String[]> queryParameters = request().queryString();
            String displayFlag = queryParameters.get("displayFlag")[0];
            String orgName = queryParameters.get("orgName")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        tabTemplateList = ldapServiceManager.getTabTemplates(orgList.get(0));    
                    } 
                } else {
                    tabTemplateList = ldapServiceManager.getTabTemplates(orgName);    
                }               

                /*if(displayFlag.equals("page")) {
                    return ok(views.html.viewPages.viewRoleList.render(orgList, roleList));    
                } else {*/
                //} 

                ArrayList<String> templateNameList = new ArrayList<String>();
                for(TabTemplate tabTemplate: tabTemplateList) {
                    templateNameList.add(tabTemplate.getTemplateName());
                }
                String templateNameString = String.join(",", templateNameList);
                return ok(templateNameString);
                          
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }


    }

    
    public Result createTab() {

        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            
            String response = "";
            Map<String, String[]> queryParameters = request().queryString();
            String tab_name = queryParameters.get("tab_name")[0];
            String tab_header = queryParameters.get("tab_header")[0];
            String tab_purpose = queryParameters.get("tab_purpose")[0];
            String tab_template = queryParameters.get("tab_template")[0];
            String orgName = queryParameters.get("orgName")[0];
            String accessInfo = queryParameters.get("accessInfo")[0];
            String accessInfoDep = queryParameters.get("accessInfoDep")[0];
            String tab_org_unit = queryParameters.get("tab_org_unit")[0];
            String tab_department = queryParameters.get("tab_department")[0];
            String tab_role = queryParameters.get("tab_role")[0];

            try {
                ldapServiceManager.createTab(tab_name,tab_header,tab_purpose,tab_template,orgName,accessInfo,tab_org_unit,tab_department,tab_role,accessInfoDep);
                response = "Tab creation successful.";
                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Tab creation failed.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }
    }

    public Result getTabList() {

        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            
            ArrayList<Tab> tabList = new ArrayList<Tab>();
            ArrayList<String> orgList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();
            String displayFlag = queryParameters.get("displayFlag")[0];
            String orgName = queryParameters.get("orgName")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        tabList = ldapServiceManager.getTabs(orgList.get(0));    
                    } 
                } else {
                    tabList = ldapServiceManager.getTabs(orgName);
                }               

                if(displayFlag.equals("page")) {
                    return ok(views.html.viewPages.viewTabList.render(orgList, tabList));    
                }     
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
            response = "Error occurred while reading the data.";
            return badRequest(response);
        } else {
            
            return unauthorized("Unauthorized");
        }                  
    }

    
    public Result searchUsers() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            Map<String, String[]> queryParameters = request().queryString();
            String searchTerm = queryParameters.get("searchTerm")[0];
            String orgName = "";
            String listSize = "";
            try {
                orgName = queryParameters.get("orgName")[0];
            }
            catch(Exception exp) {
                //exp.printStackTrace();
                System.out.println("Disease category not found.");
                try {
                    orgName = ldapServiceManager.getOrganizationList(loggedUserOrg).get(0);
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
            try {
                listSize = queryParameters.get("listSize")[0];
                int listSiz = Integer.parseInt(listSize);
            }
            catch(Exception exp) {
                //exp.printStackTrace();
                System.out.println("Invalid number for list size / List size not found.");
                listSize = "5";
            }

            String resultString = "";
            try{
              resultString = ldapServiceManager.searchUsers(orgName, searchTerm, listSize);
            }
            catch (Exception e) {
              e.printStackTrace();
            }
            return ok(resultString);
        } else {
            
            return unauthorized("Unauthorized");
        }

    }

    public Result getUserRoles() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
        
            ArrayList<String> orgList = new ArrayList<String>();
            String roleS = "";

            Map<String, String[]> queryParameters = request().queryString();
            String displayFlag = queryParameters.get("displayFlag")[0];
            String orgName = queryParameters.get("orgName")[0];
            String userName = queryParameters.get("userName")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        roleS = ldapServiceManager.getUserRoleList(orgList.get(0), userName, displayFlag);    
                    } 
                } else {
                    roleS = ldapServiceManager.getUserRoleList(orgName, userName, displayFlag);
                }               

                //if(displayFlag.equals("list")) {
                return ok(roleS);
                //}
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }      
    }

    public Result addRoles4User() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            
            ArrayList<String> orgList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();
            String orgName = queryParameters.get("orgName")[0];
            String userName = queryParameters.get("userName")[0];
            String roles = queryParameters.get("roles")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        response = ldapServiceManager.addRoles4User(orgList.get(0), userName, roles);    
                    } 
                } else {
                    response = ldapServiceManager.addRoles4User(orgName, userName, roles);
                }

                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }                    
    }

    public Result removeRoles4User() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            
            ArrayList<String> orgList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();
            String orgName = queryParameters.get("orgName")[0];
            String userName = queryParameters.get("userName")[0];
            String roles = queryParameters.get("roles")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        response = ldapServiceManager.removeRoles4User(orgList.get(0), userName, roles);    
                    } 
                } else {
                    response = ldapServiceManager.removeRoles4User(orgName, userName, roles);
                }

                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }
    }

    public Result deleteUser() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        String response = "";
            
        ArrayList<String> orgList = new ArrayList<String>();

        Map<String, String[]> queryParameters = request().queryString();
        String userName = queryParameters.get("userName")[0];
        String orgName = "";

        try {
            orgName = queryParameters.get("orgName")[0];
        }catch(Exception exp) {
            orgName = "";
        }

        try {
            //orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
            if(orgName.equals("")) {
               response = ldapServiceManager.deleteUser(userName); 
            } else {
                response = ldapServiceManager.deleteUser(orgName, userName);
            }

            return ok(response);
        }
        catch(Exception exp) {
            exp.printStackTrace();
            response = "Error occurred while reading the data.";
            return badRequest(response);
        }

    }

    public Result getTabs() {

        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            
            ArrayList<Tab> tabList = new ArrayList<Tab>();
            ArrayList<String> orgList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();
            String purpose = queryParameters.get("purpose")[0];
            String orgName = queryParameters.get("orgName")[0];
            String orgUnit = queryParameters.get("orgUnit")[0];
            String includeDepFlag = queryParameters.get("includeDepFlag")[0];
            String department = queryParameters.get("department")[0];
            String role = queryParameters.get("role")[0];
            String has_all_ou_access = queryParameters.get("has_all_ou_access")[0];
            String has_all_dep_access = queryParameters.get("has_all_dep_access")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        orgName = orgList.get(0);
                    } 
                }

                if(purpose.equals("assigned")){
                    tabList = ldapServiceManager.getAssignedTabs(orgName, orgUnit, includeDepFlag, department, role);
                    return ok(views.html.viewPages.viewAssignedTabs.render(tabList));
                }
                else{
                    tabList = ldapServiceManager.getAvailableTabs(orgName, orgUnit, includeDepFlag, department, role, has_all_ou_access, has_all_dep_access);
                    return ok(views.html.viewPages.viewAvailableTabs.render(tabList));
                }

                
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
            /*response = "Error occurred while reading the data.";
            return badRequest(response);*/
        } else {
            
            return unauthorized("Unauthorized");
        }

    }
  
    
       // get Tabs based on the OU
    
    public Result getTabsBasedOnOU() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");
       
        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            
            ArrayList<String> tabList = new ArrayList<String>();
            ArrayList<String> orgList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();
         
            String orgName = queryParameters.get("orgName")[0];
            String orgUnit = queryParameters.get("orgUnit")[0];
            String has_all_ou_access = queryParameters.get("has_all_ou_access")[0];
            String displayFlag = queryParameters.get("displayFlag")[0];
            String tabType=queryParameters.get("tabType")[0];
            
            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        orgName = orgList.get(0);
                    } 
                }
                  tabList = ldapServiceManager.getAvailableTabsBasedOnOU(orgName, orgUnit, has_all_ou_access,tabType);
                  if(displayFlag.equals("page")) {
                      return ok(views.html.viewPages.viewTabsBasedOnOU.render(tabList));    
                  } else {
                      String orgtabListString = String.join(",", tabList);
                      return ok(orgtabListString);
                  } 
                   
            }catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }

    }
    
    public Result addRoles4Tab() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            
            ArrayList<String> orgList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();
            String orgName = queryParameters.get("orgName")[0];
            String roleName = queryParameters.get("roleName")[0];
            String tabs = queryParameters.get("tabs")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        response = ldapServiceManager.addRoles4Tab(orgList.get(0), roleName, tabs);    
                    } 
                } else {
                    response = ldapServiceManager.addRoles4Tab(orgName, roleName, tabs);
                }

                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }
    }

    public Result removeRoles4Tab() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            
            ArrayList<String> orgList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();
            String orgName = queryParameters.get("orgName")[0];
            String roleName = queryParameters.get("roleName")[0];
            String tabs = queryParameters.get("tabs")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        response = ldapServiceManager.removeRoles4Tab(orgList.get(0), roleName, tabs);    
                    } 
                } else {
                    response = ldapServiceManager.removeRoles4Tab(orgName, roleName, tabs);
                }

                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }
    }

    public Result deleteTab() {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            
            ArrayList<String> orgList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();
            String orgName = queryParameters.get("orgName")[0];
            String tabName = queryParameters.get("tabName")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        response = ldapServiceManager.deleteTab(orgList.get(0), tabName);    
                    } 
                } else {
                    response = ldapServiceManager.deleteTab(orgName, tabName);
                }

                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }           
    }

    public Result getAuthorizedTabs() {

        DynamicForm requestData = formFactory.form().bindFromRequest();
        String userName = "";
        String userId = "";
        String userOrganization = "";
        String userAuthToken = "";
        String responseString = "";

        String statusCode = "";

        try {
            userName = requestData.get("userName");
            userId = requestData.get("userId");
            userOrganization = requestData.get("userOrganization");
            userAuthToken = requestData.get("userAuthToken");
            
        } catch(Exception exception) {
            exception.printStackTrace();
            userName = "";
            userId = "";
            userOrganization = "";
            userAuthToken = "";
        }

        try {
            statusCode = ldapServiceManager.validateUser(userOrganization, userName, userId, userAuthToken);
        }
        catch(Exception exp) {
            exp.printStackTrace();
            statusCode = "400";
        }

        if(statusCode.contains("201")) {
            responseString = statusCode.split(":::")[1];
        } else {
            responseString = "Invalid Request";
        }

        return ok(responseString);
    }

    public Result getChannelMembers() {

        DynamicForm requestData = formFactory.form().bindFromRequest();
        String userName = "";
        String userId = "";
        String userOrganization = "";
        String userAuthToken = "";
        String responseString = "";
        String channelId = "";

        String statusCode = "";

        try {
            userName = requestData.get("userName");
            userId = requestData.get("userId");
            userOrganization = requestData.get("userOrganization");
            userAuthToken = requestData.get("userAuthToken");
            channelId = requestData.get("channelId");
            
        } catch(Exception exception) {
            exception.printStackTrace();
            userName = "";
            userId = "";
            userOrganization = "";
            userAuthToken = "";
            channelId = "";
        }

        try {
            statusCode = ldapServiceManager.getMembers(userOrganization, userName, userId, userAuthToken, channelId);
        }
        catch(Exception exp) {
            exp.printStackTrace();
            statusCode = "400";
        }

        if(statusCode.contains("201")) {
            try {
                responseString = statusCode.split(":::")[1];
            }
            catch(Exception exp) {
                responseString = "Invalid Request";
            }
        } else {
            responseString = "Invalid Request";
        }

        return ok(responseString);
    }
    
    
    // create news
    
    public Result createNews() {
    	    	 
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");
        
        DynamicForm requestData = formFactory.form().bindFromRequest();
        String content ="";
        String orgName = "";
        String orgUnitName = "";
        String tabName = "";

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null && loggedUserOrg.equals("")) {
        	 try {
        		 content=requestData.get("content");
            	 orgName=requestData.get("orgName");
            	 orgUnitName=requestData.get("orgUnitName");
            	 tabName=requestData.get("tabName");
                 
             } catch(Exception exception) {
                 exception.printStackTrace();
                 content="";
            	 orgName="";
            	 orgUnitName="";
            	 tabName="";
             }
        	        	
            String response = "";                               
            try { 
            	String resp=ldapServiceManager.createNews(content,orgName, orgUnitName, tabName);
				 response = "news creation successful.";
				 return ok(response);
			}catch(Exception exp) {
				exp.printStackTrace();
				response ="news creation failed."; 
				return badRequest(response);
			}
					
        } else {
            
            return unauthorized("Unauthorized");
        }
        	        
    }


    public Result getHisUrl(String orgName) {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";

            try {
                Organization org = ldapServiceManager.getOrganization(orgName);
                return ok(org.getOrganizationHisUrl());             
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }      
    }

    public Result updateHisUrl(final Http.Request request) {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        Map<String, String[]> queryParameters = request.body().asFormUrlEncoded();
        String orgName = queryParameters.get("orgName")[0];
        String hisUrl = queryParameters.get("hisURL")[0];

        System.out.println(orgName);
        System.out.println(hisUrl);

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";

            try {
                Organization org = ldapServiceManager.getOrganization(orgName);
                org.setOrganizationHisUrl(hisUrl);
                response = ldapServiceManager.updateOrganization(org);
                return ok(response);             
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Update Failed";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }      
    }


  //Validation Methods

    public Result validateOrganizationName() {

        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            
            String response = "";
            Map<String, String[]> queryParameters = request().queryString();
            String orgName = queryParameters.get("orgName")[0];

            try {
                if(ldapServiceManager.validateOrgName(orgName))
                    response = "true";
                else
                    response = "false";
                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "false";
                return badRequest(response);
            }
        } else {            
            return unauthorized("Unauthorized");
        }
    } 

    public Result validateOrgUnitName() {

        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            
            String response = "";
            Map<String, String[]> queryParameters = request().queryString();
            String orgName = queryParameters.get("orgName")[0];
            String orgUnitName = queryParameters.get("orgUnitName")[0];

            try {
                if(ldapServiceManager.validateOrgUnitName(orgName, orgUnitName))
                    response = "true";
                else
                    response = "false";
                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "false";
                return badRequest(response);
            }
        } else {            
            return unauthorized("Unauthorized");
        }
    }

    public Result validateDeptName() {

        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            
            String response = "";
            Map<String, String[]> queryParameters = request().queryString();
            String orgName = queryParameters.get("orgName")[0];
            String orgUnitName = queryParameters.get("orgUnitName")[0];
            String deptName = queryParameters.get("deptName")[0];

            try {
                if(ldapServiceManager.validateDepartmentName(orgName, orgUnitName, deptName))
                    response = "true";
                else
                    response = "false";
                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "false";
                return badRequest(response);
            }
        } else {            
            return unauthorized("Unauthorized");
        }
    }

    public Result validateRoleName() {

        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            
            String response = "";
            Map<String, String[]> queryParameters = request().queryString();
            String orgName = queryParameters.get("orgName")[0];
            String roleName = queryParameters.get("roleName")[0];

            try {
                if(ldapServiceManager.validateRoleName(orgName, roleName))
                    response = "true";
                else
                    response = "false";
                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "false";
                return badRequest(response);
            }
        } else {            
            return unauthorized("Unauthorized");
        }
    }

    public Result validateUsername() {

        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            
            String response = "";
            Map<String, String[]> queryParameters = request().queryString();
            String orgName = queryParameters.get("orgName")[0];
            String userName = queryParameters.get("userName")[0];

            try {
                if(ldapServiceManager.validateUserName(orgName, userName))
                    response = "true";
                else
                    response = "false";
                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "false";
                return badRequest(response);
            }
        } else {            
            return unauthorized("Unauthorized");
        }
    }

    public Result validateTabname() {

        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            
            String response = "";
            Map<String, String[]> queryParameters = request().queryString();
            String orgName = queryParameters.get("orgName")[0];
            String tabName = queryParameters.get("tabName")[0];

            try {
                if(ldapServiceManager.validateTabName(orgName, tabName))
                    response = "true";
                else
                    response = "false";
                return ok(response);
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "false";
                return badRequest(response);
            }
        } else {            
            return unauthorized("Unauthorized");
        }
    }  

 
}