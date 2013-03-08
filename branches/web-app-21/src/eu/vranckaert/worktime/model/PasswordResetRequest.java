package eu.vranckaert.worktime.model;

import java.util.Date;

import com.google.code.twig.annotation.Entity;
import com.google.code.twig.annotation.Id;

import eu.vranckaert.worktime.security.utils.KeyGenerator;

@Entity(kind="passwordResetRequest")
public class PasswordResetRequest {
	@Id private String key;
	private Date requestDate;
	private String email;
	private boolean used;
	
	public PasswordResetRequest() {}
	
	public PasswordResetRequest(User user) {
		key = KeyGenerator.getNewKey();
		requestDate = new Date();
		this.email = user.getEmail();
		this.used = false;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public Date getRequestDate() {
		return requestDate;
	}
	
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean isUsed() {
		return used;
	}
	
	public void setUsed(boolean used) {
		this.used = used;
	}
}
