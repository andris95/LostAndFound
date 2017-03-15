package com.sanislo.lostandfound.model.api;

import com.google.gson.annotations.Expose;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Thing{

	@SerializedName("descriptionPhotos")
	@Expose
	private DescriptionPhotos descriptionPhotos;

	@SerializedName("userAvatar")
	@Expose
	private String userAvatar;

	@SerializedName("description")
	@Expose
	private String description;

	@SerializedName("photo")
	@Expose
	private String photo;

	@SerializedName("type")
	@Expose
	private String type;

	@SerializedName("userName")
	@Expose
	private String userName;

	@SerializedName("title")
	@Expose
	private String title;

	@SerializedName("commentCount")
	@Expose
	private int commentCount;

	@SerializedName("location")
	@Expose
	private Location location;

	@SerializedName("id")
	@Expose
	private int id;

	@SerializedName("category")
	@Expose
	private String category;

	@SerializedName("userUID")
	@Expose
	private String userUID;

	@SerializedName("timestamp")
	@Expose
	private int timestamp;

	public void setDescriptionPhotos(DescriptionPhotos descriptionPhotos){
		this.descriptionPhotos = descriptionPhotos;
	}

	public DescriptionPhotos getDescriptionPhotos(){
		return descriptionPhotos;
	}

	public void setUserAvatar(String userAvatar){
		this.userAvatar = userAvatar;
	}

	public String getUserAvatar(){
		return userAvatar;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setPhoto(String photo){
		this.photo = photo;
	}

	public String getPhoto(){
		return photo;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getUserName(){
		return userName;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setCommentCount(int commentCount){
		this.commentCount = commentCount;
	}

	public int getCommentCount(){
		return commentCount;
	}

	public void setLocation(Location location){
		this.location = location;
	}

	public Location getLocation(){
		return location;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setCategory(String category){
		this.category = category;
	}

	public String getCategory(){
		return category;
	}

	public void setUserUID(String userUID){
		this.userUID = userUID;
	}

	public String getUserUID(){
		return userUID;
	}

	public void setTimestamp(int timestamp){
		this.timestamp = timestamp;
	}

	public int getTimestamp(){
		return timestamp;
	}

	@Override
 	public String toString(){
		return 
			"Thing{" + 
			"descriptionPhotos = '" + descriptionPhotos + '\'' + 
			",userAvatar = '" + userAvatar + '\'' + 
			",description = '" + description + '\'' + 
			",photo = '" + photo + '\'' + 
			",type = '" + type + '\'' + 
			",userName = '" + userName + '\'' + 
			",title = '" + title + '\'' + 
			",commentCount = '" + commentCount + '\'' + 
			",location = '" + location + '\'' + 
			",id = '" + id + '\'' + 
			",category = '" + category + '\'' + 
			",userUID = '" + userUID + '\'' + 
			",timestamp = '" + timestamp + '\'' + 
			"}";
		}
}