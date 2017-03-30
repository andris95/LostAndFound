package com.sanislo.lostandfound.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Thing implements Parcelable {
	public static final String TYPE_LOST = "lost";
	public static final String TYPE_FOUND = "found";

	@SerializedName("descriptionPhotos")
	@Expose
	private List<String> descriptionPhotos;

	@SerializedName("description")
	@Expose
	private String description;

	@SerializedName("photo")
	@Expose
	private String photo;

	@SerializedName("location")
	@Expose
	private Location location;

	@SerializedName("id")
	@Expose
	private int id;

	@SerializedName("category")
	@Expose
	private String category;

	@SerializedName("title")
	@Expose
	private String title;

	@SerializedName("userName")
	@Expose
	private String userName;

	@SerializedName("userAvatar")
	@Expose
	private String userAvatar;

	@SerializedName("type")
	@Expose
	private String type;

	@SerializedName("commentCount")
	@Expose
	private int commentCount;

	@SerializedName("timestamp")
	@Expose
	private long timestamp;

	@SerializedName("userUID")
	@Expose
	private String userUID;

	public Thing() {

	}

	public void setDescriptionPhotos(List<String> descriptionPhotos){
		this.descriptionPhotos = descriptionPhotos;
	}

	public List<String> getDescriptionPhotos(){
		return descriptionPhotos;
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

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getUserName(){
		return userName;
	}

	public void setCommentCount(int commentCount){
		this.commentCount = commentCount;
	}

	public int getCommentCount(){
		return commentCount;
	}

	public void setTimestamp(long timestamp){
		this.timestamp = timestamp;
	}

	public long getTimestamp(){
		return timestamp;
	}

	public void setUserUID(String userUID){
		this.userUID = userUID;
	}

	public String getUserUID(){
		return userUID;
	}

	public String getUserAvatar() {
		return userAvatar;
	}

	public void setUserAvatar(String userAvatar) {
		this.userAvatar = userAvatar;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Thing{" +
				"descriptionPhotos=" + descriptionPhotos +
				", description='" + description + '\'' +
				", photo='" + photo + '\'' +
				", location=" + location +
				", id=" + id +
				", category='" + category + '\'' +
				", title='" + title + '\'' +
				", userName='" + userName + '\'' +
				", userAvatar='" + userAvatar + '\'' +
				", type='" + type + '\'' +
				", commentCount=" + commentCount +
				", timestamp=" + timestamp +
				", userUID='" + userUID + '\'' +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(descriptionPhotos);
		dest.writeString(photo);
		//write location
		dest.writeParcelable(location, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
		dest.writeInt(id);
		dest.writeString(category);
		dest.writeString(title);
		dest.writeString(userName);
		dest.writeString(userAvatar);
		dest.writeString(type);
		dest.writeInt(commentCount);
		dest.writeString(userUID);
		dest.writeLong(timestamp);
	}

	public static final Parcelable.Creator<Thing> CREATOR = new Parcelable.Creator<Thing>() {
		// распаковываем объект из Parcel
		public Thing createFromParcel(Parcel in) {
			return new Thing(in);
		}

		public Thing[] newArray(int size) {
			return new Thing[size];
		}
	};

	// конструктор, считывающий данные из Parcel
	private Thing(Parcel parcel) {
		descriptionPhotos = parcel.readArrayList(null);
		photo = parcel.readString();
		location = parcel.readParcelable(Thing.class.getClassLoader());
		id = parcel.readInt();
		category = parcel.readString();
		title = parcel.readString();
		userName = parcel.readString();
		userAvatar = parcel.readString();
		type = parcel.readString();
		commentCount = parcel.readInt();
		userUID = parcel.readString();
		timestamp = parcel.readLong();
	}
}