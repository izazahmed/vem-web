package com.enerallies.vem.beans.pdfreport;

import java.net.URL;
import java.util.Date;

public class PDFThreadRequest {

	 URL url;
	 Date date;
	 String folderName;
	 
	 public URL getUrl() {
	  return url;
	 }
	 public void setUrl(URL url) {
	  this.url = url;
	 }
	 public Date getDate() {
	  return date;
	 }
	 public void setDate(Date date) {
	  this.date = date;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
}
