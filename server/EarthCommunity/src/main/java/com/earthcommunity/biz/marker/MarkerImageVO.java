package com.earthcommunity.biz.marker;

public class MarkerImageVO {
	private int seq;
	private int id;
	private String file_address;
	
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFile_address() {
		return file_address;
	}
	public void setFile_address(String file_address) {
		this.file_address = file_address;
	}
	@Override
	public String toString() {
		return "MarkerImageVO [seq=" + seq + ", id=" + id + ", file_address=" + file_address + "]";
	}

	
}
