package com.sanislo.lostandfound.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Thing implements Parcelable {
	public static final int TYPE_ANY = 0;
	public static final int TYPE_LOST = 1;
	public static final int TYPE_FOUND = 2;

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
	private int type;

	@SerializedName("commentCount")
	@Expose
	private int commentCount;

	@SerializedName("timestamp")
	@Expose
	private long timestamp;

	@SerializedName("userUID")
	@Expose
	private String userUID;

	@SerializedName("userContacts")
	@Expose
	private List<String> userContantcs;

	@SerializedName("returned")
	@Expose
	private boolean returned;

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<String> getUserContantcs() {
		return userContantcs;
	}

	public void setUserContantcs(List<String> userContantcs) {
		this.userContantcs = userContantcs;
	}

	public boolean isReturned() {
		return returned;
	}

	public void setReturned(boolean returned) {
		this.returned = returned;
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
		dest.writeString(description);
		dest.writeString(photo);
		//write location
		dest.writeParcelable(location, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
		dest.writeInt(id);
		dest.writeString(category);
		dest.writeString(title);
		dest.writeString(userName);
		dest.writeString(userAvatar);
		dest.writeInt(type);
		dest.writeInt(commentCount);
		dest.writeString(userUID);
		dest.writeLong(timestamp);
		dest.writeList(userContantcs);
		dest.writeByte((byte) (returned ? 1 : 0));
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
		description = parcel.readString();
		photo = parcel.readString();
		location = parcel.readParcelable(Thing.class.getClassLoader());
		id = parcel.readInt();
		category = parcel.readString();
		title = parcel.readString();
		userName = parcel.readString();
		userAvatar = parcel.readString();
		type = parcel.readInt();
		commentCount = parcel.readInt();
		userUID = parcel.readString();
		timestamp = parcel.readLong();
		userContantcs = parcel.readArrayList(null);
		returned = parcel.readByte() != 0;
	}
}