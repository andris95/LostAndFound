package com.sanislo.lostandfound.model.api;

import com.google.gson.annotations.Expose;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class DescriptionPhotos{

	@SerializedName("0")
	@Expose
	private String jsonMember0;

	@SerializedName("1")
	@Expose
	private String jsonMember1;

	public void setJsonMember0(String jsonMember0){
		this.jsonMember0 = jsonMember0;
	}

	public String getJsonMember0(){
		return jsonMember0;
	}

	public void setJsonMember1(String jsonMember1){
		this.jsonMember1 = jsonMember1;
	}

	public String getJsonMember1(){
		return jsonMember1;
	}

	@Override
 	public String toString(){
		return 
			"DescriptionPhotos{" + 
			"0 = '" + jsonMember0 + '\'' + 
			",1 = '" + jsonMember1 + '\'' + 
			"}";
		}
}