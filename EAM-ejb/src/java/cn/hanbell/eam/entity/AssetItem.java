/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.SuperEntity;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author C0160
 */
@Entity
@Table(name = "assetitem")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetItem.findAll", query = "SELECT a FROM AssetItem a"),
    @NamedQuery(name = "AssetItem.findById", query = "SELECT a FROM AssetItem a WHERE a.id = :id"),
    @NamedQuery(name = "AssetItem.findByCategoryId", query = "SELECT a FROM AssetItem a WHERE a.category.id = :categoryid"),
    @NamedQuery(name = "AssetItem.findByItemno", query = "SELECT a FROM AssetItem a WHERE a.itemno = :itemno"),
    @NamedQuery(name = "AssetItem.findByStatus", query = "SELECT a FROM AssetItem a WHERE a.status = :status")})
public class AssetItem extends SuperEntity {

    @JoinColumn(name = "categoryid", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private AssetCategory category;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "itemno")
    private String itemno;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "itemdesc")
    private String itemdesc;
    @Size(max = 100)
    @Column(name = "itemspec")
    private String itemspec;
    @Size(max = 10)
    @Column(name = "simplecode")
    private String simplecode;
    @Size(max = 2)
    @Column(name = "proptype")
    private String proptype;
    @Size(max = 2)
    @Column(name = "maketype")
    private String maketype;
    @Size(max = 8)
    @Column(name = "pptype")
    private String pptype;
    @Size(max = 10)
    @Column(name = "purkind")
    private String purkind;
    @Column(name = "qcpass")
    private Boolean qcpass;
    @Size(max = 45)
    @Column(name = "brand")
    private String brand;
    @Size(max = 45)
    @Column(name = "batch")
    private String batch;
    @Size(max = 45)
    @Column(name = "sn")
    private String sn;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "unittype")
    private String unittype;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "unit")
    private String unit;
    @Size(max = 10)
    @Column(name = "unit2")
    private String unit2;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "unitexch")
    private BigDecimal unitexch;
    @Size(max = 10)
    @Column(name = "unitsales")
    private String unitsales;
    @Size(max = 10)
    @Column(name = "unitpurchase")
    private String unitpurchase;
    @Column(name = "invtype")
    private Boolean invtype;
    @Size(max = 3)
    @Column(name = "bbstype")
    private String bbstype;
    @Column(name = "stdcost")
    private BigDecimal stdcost;
    @Column(name = "purprice")
    private BigDecimal purprice;
    @Column(name = "purmin")
    private BigDecimal purmin;
    @Column(name = "purmax")
    private BigDecimal purmax;
    @Column(name = "invmin")
    private BigDecimal invmin;
    @Column(name = "invmax")
    private BigDecimal invmax;
    @Size(max = 45)
    @Column(name = "barcode")
    private String barcode;
    @Size(max = 100)
    @Column(name = "img1")
    private String img1;
    @Size(max = 100)
    @Column(name = "img2")
    private String img2;
    @Size(max = 100)
    @Column(name = "remark")
    private String remark;

    public AssetItem() {
        this.invtype = true;
        this.bbstype = "000";
    }

    public AssetCategory getCategory() {
        return category;
    }

    public void setCategory(AssetCategory category) {
        this.category = category;
    }

    public String getItemno() {
        return itemno;
    }

    public void setItemno(String itemno) {
        this.itemno = itemno;
    }

    public String getItemdesc() {
        return itemdesc;
    }

    public void setItemdesc(String itemdesc) {
        this.itemdesc = itemdesc;
    }

    public String getItemspec() {
        return itemspec;
    }

    public void setItemspec(String itemspec) {
        this.itemspec = itemspec;
    }

    public String getSimplecode() {
        return simplecode;
    }

    public void setSimplecode(String simplecode) {
        this.simplecode = simplecode;
    }

    public String getProptype() {
        return proptype;
    }

    public void setProptype(String proptype) {
        this.proptype = proptype;
    }

    public String getMaketype() {
        return maketype;
    }

    public void setMaketype(String maketype) {
        this.maketype = maketype;
    }

    public String getPptype() {
        return pptype;
    }

    public void setPptype(String pptype) {
        this.pptype = pptype;
    }

    public String getPurkind() {
        return purkind;
    }

    public void setPurkind(String purkind) {
        this.purkind = purkind;
    }

    public Boolean getQcpass() {
        return qcpass;
    }

    public void setQcpass(Boolean qcpass) {
        this.qcpass = qcpass;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getUnittype() {
        return unittype;
    }

    public void setUnittype(String unittype) {
        this.unittype = unittype;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit2() {
        return unit2;
    }

    public void setUnit2(String unit2) {
        this.unit2 = unit2;
    }

    public BigDecimal getUnitexch() {
        return unitexch;
    }

    public void setUnitexch(BigDecimal unitexch) {
        this.unitexch = unitexch;
    }

    public String getUnitsales() {
        return unitsales;
    }

    public void setUnitsales(String unitsales) {
        this.unitsales = unitsales;
    }

    public String getUnitpurchase() {
        return unitpurchase;
    }

    public void setUnitpurchase(String unitpurchase) {
        this.unitpurchase = unitpurchase;
    }

    public Boolean getInvtype() {
        return invtype;
    }

    public void setInvtype(Boolean invtype) {
        this.invtype = invtype;
    }

    public String getBbstype() {
        return bbstype;
    }

    public void setBbstype(String bbstype) {
        this.bbstype = bbstype;
    }

    public BigDecimal getStdcost() {
        return stdcost;
    }

    public void setStdcost(BigDecimal stdcost) {
        this.stdcost = stdcost;
    }

    public BigDecimal getPurprice() {
        return purprice;
    }

    public void setPurprice(BigDecimal purprice) {
        this.purprice = purprice;
    }

    public BigDecimal getPurmin() {
        return purmin;
    }

    public void setPurmin(BigDecimal purmin) {
        this.purmin = purmin;
    }

    public BigDecimal getPurmax() {
        return purmax;
    }

    public void setPurmax(BigDecimal purmax) {
        this.purmax = purmax;
    }

    public BigDecimal getInvmin() {
        return invmin;
    }

    public void setInvmin(BigDecimal invmin) {
        this.invmin = invmin;
    }

    public BigDecimal getInvmax() {
        return invmax;
    }

    public void setInvmax(BigDecimal invmax) {
        this.invmax = invmax;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AssetItem)) {
            return false;
        }
        AssetItem other = (AssetItem) object;
        if (this.id != null && other.id != null) {
            return this.id.equals(other.id);
        }
        return this.itemno.equals(other.itemno);
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetItem[ id=" + id + " ]";
    }

}
