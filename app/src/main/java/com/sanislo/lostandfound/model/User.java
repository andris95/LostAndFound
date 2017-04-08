package com.sanislo.lostandfound.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class User{

	@SerializedName("uid")
	@Expose
	private String uid;

	@SerializedName("firstName")
	@Expose
	private String firstName;

	@SerializedName("lastName")
	@Expose
	private String lastName;

	@SerializedName("country")
	@Expose
	private String country;

	@SerializedName("emailAddress")
	@Expose
	private String emailAddress;

	@SerializedName("phoneNumber")
	@Expose
	private String phoneNumber;

	@SerializedName("gender")
	@Expose
	private int gender;

	@SerializedName("city")
	@Expose
	private String city;

	@SerializedName("avatarURL")
	@Expose
	private String avatarURL;

	@SerializedName("fullName")
	@Expose
	private String fullName;

	@SerializedName("id")
	@Expose
	private int id;

	@SerializedName("birthDate")
	@Expose
	private int birthDate;

	@SerializedName("contacts")
	@Expose
	private List<String> contacts;

	public void setUid(String uid){
		this.uid = uid;
	}

	public String getUid(){
		return uid;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setEmailAddress(String emailAddress){
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress(){
		return emailAddress;
	}

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public void setGender(int gender){
		this.gender = gender;
	}

	public int getGender(){
		return gender;
	}

	public void setCity(String city){
		this.city = city;
	}

	public String getCity(){
		return city;
	}

	public void setAvatarURL(String avatarURL){
		this.avatarURL = avatarURL;
	}

	public String getAvatarURL(){
		return avatarURL;
	}

	public void setFullName(String fullName){
		this.fullName = fullName;
	}

	public String getFullName(){
		return fullName;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setBirthDate(int birthDate){
		this.birthDate = birthDate;
	}

	public int getBirthDate(){
		return birthDate;
	}

	public List<String> getContacts() {
		return contacts;
	}

	public void setContacts(List<String> contacts) {
		this.contacts = contacts;
	}

	@Override
 	public String toString(){
		return 
			"User{" + 
			"uid = '" + uid + '\'' + 
			",firstName = '" + firstName + '\'' + 
			",lastName = '" + lastName + '\'' + 
			",country = '" + country + '\'' + 
			",emailAddress = '" + emailAddress + '\'' + 
			",phoneNumber = '" + phoneNumber + '\'' + 
			",gender = '" + gender + '\'' + 
			",city = '" + city + '\'' + 
			",avatarURL = '" + avatarURL + '\'' + 
			",fullName = '" + fullName + '\'' + 
			",id = '" + id + '\'' + 
			",birthDate = '" + birthDate + '\'' + 
			"}";
		}
}