package models.dto;

public class ModifiedUserDto {
	protected String firstName;
	protected String lastName;
	protected String username;
	protected String password;
	protected String email;
	protected String qualification;
	protected String specialization;
	protected String employeeId;
	protected String aadhaarNumber;
	protected String panNumber;
	protected String passportNumber;
	protected String phoneNumber;
	protected String organizationName;
	protected String organizationUnitName;
	protected String departmentName;
	protected String roleName;
	protected String gender;
	protected String accessInfo;
	protected String universalAccessInfo;
	
	public ModifiedUserDto() {
		super();
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPasswordHash(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getAadhaarNumber() {
		return aadhaarNumber;
	}

	public void setAadhaarNumber(String aadhaarNumber) {
		this.aadhaarNumber = aadhaarNumber;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationUnitName() {
		return organizationUnitName;
	}

	public void setOrganizationUnitName(String organizationUnitName) {
		this.organizationUnitName = organizationUnitName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAccessInfo() {
		return accessInfo;
	}

	public void setAccessInfo(String accessInfo) {
		this.accessInfo = accessInfo;
	}

	public String getUniversalAccessInfo() {
		return universalAccessInfo;
	}

	public void setUniversalAccessInfo(String universalAccessInfo) {
		this.universalAccessInfo = universalAccessInfo;
	}


	@Override
	public String toString() {
		return "UserDto [firstName=" + firstName + ", lastName=" + lastName + ", username=" + username + ", password="
				+ password + ", email=" + email + ", qualification=" + qualification + ", specialization="
				+ specialization + ", employeeId=" + employeeId + ", aadhaarNumber=" + aadhaarNumber + ", panNumber="
				+ panNumber + ", passportNumber=" + passportNumber + ", phoneNumber=" + phoneNumber
				+ ", organizationName=" + organizationName + ", organizationUnitName=" + organizationUnitName
				+ ", departmentName=" + departmentName + ", roleName=" + roleName + ", gender=" + gender
				+ ", accessInfo=" + accessInfo + ", universalAccessInfo=" + universalAccessInfo + "]";
	}

	

}
