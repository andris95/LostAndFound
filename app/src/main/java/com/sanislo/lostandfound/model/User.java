package com.sanislo.lostandfound.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class User implements Parcelable {

	public User() {
	}

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

	@Override
	public String toString() {
		return "FirebaseUser{" +
				"uid='" + uid + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", country='" + country + '\'' +
				", emailAddress='" + emailAddress + '\'' +
				", city='" + city + '\'' +
				", avatarURL='" + avatarURL + '\'' +
				", fullName='" + fullName + '\'' +
				", id=" + id +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(uid);
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeString(country);
		dest.writeString(emailAddress);
		dest.writeString(city);
		dest.writeString(avatarURL);
		dest.writeString(fullName);
		dest.writeInt(id);
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		// распаковываем объект из Parcel
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};

	// конструктор, считывающий данные из Parcel
	private User(Parcel parcel) {
		uid = parcel.readString();
		firstName = parcel.readString();
		lastName = parcel.readString();
		country = parcel.readString();
		emailAddress = parcel.readString();
		city = parcel.readString();
		avatarURL = parcel.readString();
		fullName = parcel.readString();
		id = parcel.readInt();
	}
}