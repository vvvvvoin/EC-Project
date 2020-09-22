package com.earthcommunity.biz.marker;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MarkerDataVO implements Serializable {
	private int seq;
	private String subject;
	private String content;
	private String lat;
	private String lng;
	private String writer;
	private String address;
	private List<File> uploadFile;
	private String searchString;
	
	@JsonIgnore
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	@JsonIgnore
	public List<File> getUploadFile() {
		return uploadFile;
	}
	public void setUploadFile(List<File> uploadFile) {
		this.uploadFile = uploadFile;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return "MarkerDataVO [seq=" + seq + ", subject=" + subject + ", content=" + content + ", lat=" + lat + ", lng="
				+ lng + ", writer=" + writer + ", address=" + address + "]";
	}
	
}
