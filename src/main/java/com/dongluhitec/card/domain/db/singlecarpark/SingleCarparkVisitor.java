package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.DomainObject;

public class SingleCarparkVisitor extends DomainObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -146910856653100859L;
	public enum Property{
		plateNO,name,telephone,allIn,inCount,validTo,remark
	}
	private String plateNO;
	private String name;
	private String telephone;
	private Integer allIn;
	private Integer inCount;
	@Temporal(TemporalType.TIMESTAMP)
	private Date validTo;
	private String remark;
	
	public String getPlateNO() {
		return plateNO;
	}
	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		firePropertyChange("plateNO", null, null);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		firePropertyChange("name", null, null);
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
		firePropertyChange("telephone", null, null);
	}
	public Integer getAllIn() {
		return allIn;
	}
	public void setAllIn(Integer allIn) {
		this.allIn = allIn;
		firePropertyChange("allIn", null, null);
	}
	public Integer getInCount() {
		return inCount;
	}
	public void setInCount(Integer inCount) {
		this.inCount = inCount;
		firePropertyChange("inCount", null, null);
	}
	public Date getValidTo() {
		return validTo;
	}
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
		firePropertyChange("validTo", null, null);
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
		firePropertyChange("remark", null, null);
	}
	
}