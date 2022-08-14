package com.enerallies.vem.beans.site;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AddSiteMoreInfoRequest {
	
	private Integer siteId;
	private String siteName;
	private String siteAddLine1;
	private String siteAddLine2;
}
