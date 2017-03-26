package com.sanislo.lostandfound.model;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Thing {
	@Expose
	private List<String> descriptionPhotos;

	@Expose
	private String userAvatar;

	@Expose
	private String description;

	@Expose
	private String photo;

	@Expose
	private String type;

	@Expose
	private String userName;

	@Expose
	private String title;

	@Expose
	private int commentCount;

	@Expose
	private Location location;

	@Expose
	private int id;

	@Expose
	private String category;

	@Expose
	private String userUID;

	@Expose
	private long timestamp;

	public void setDescriptionPhotos(List<String> descriptionPhotos){
		this.descriptionPhotos = descriptionPhotos;
	}

	public List<String> getDescriptionPhotos(){
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

	public void setTimestamp(long timestamp){
		this.timestamp = timestamp;
	}

	public long getTimestamp(){
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