/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.SuperEntity;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author C0160
 */
@Entity
@Table(name = "assetinventory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetInventory.findAll", query = "SELECT a FROM AssetInventory a"),
    @NamedQuery(name = "AssetInventory.findAssetInventory", query = "SELECT a FROM AssetInventory a WHERE a.company = :company AND a.assetItem.itemno = :itemno AND a.brand = :brand AND a.batch = :batch AND a.sn = :sn AND a.warehouse.warehouseno = :warehouseno "),
    @NamedQuery(name = "AssetInventory.findAssetInventories", query = "SELECT a FROM AssetInventory a WHERE a.company = :company AND a.assetItem.itemno = :itemno AND a.brand = :brand AND a.batch = :batch AND a.sn = :sn ORDER BY a.assetItem.itemno "),
    @NamedQuery(name = "AssetInventory.findById", query = "SELECT a FROM AssetInventory a WHERE a.id = :id"),
    @NamedQuery(name = "AssetInventory.findByCompany", query = "SELECT a FROM AssetInventory a WHERE a.company = :company"),
    @NamedQuery(name = "AssetInventory.findByItemno", query = "SELECT a FROM AssetInventory a WHERE a.assetItem.itemno = :itemno"),
    @NamedQuery(name = "AssetInventory.findByWarehouseno", query = "SELECT a FROM AssetInventory a WHERE a.warehouse.warehouseno = :warehouseno"),
    @NamedQuery(name = "AssetInventory.findByStatus", query = "SELECT a FROM AssetInventory a WHERE a.status = :status")})
public class AssetInventory extends SuperEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;

    @JoinColumn(name = "itemno", referencedColumnName = "itemno")
    @ManyToOne(optional = false)
    private AssetItem assetItem;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "brand")
    private String brand;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "batch")
    private String batch;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "sn")
    private String sn;

    @JoinColumn(name = "warehouseno", referencedColumnName = "warehouseno")
    @ManyToOne(optional = false)
    private Warehouse warehouse;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "qty")
    private BigDecimal qty;
    @Basic(optional = false)
    @NotNull
    @Column(name = "preqty")
    private BigDecimal preqty;
    @Size(max = 45)
    @Column(name = "location")
    private String location;
    @Size(max = 45)
    @Column(name = "objectkind")
    private String objectkind;
    @Size(max = 45)
    @Column(name = "objectid")
    private String objectid;
    @Column(name = "indate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date indate;
    @Column(name = "outdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date outdate;

    public AssetInventory() {
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public AssetItem getAssetItem() {
        return assetItem;
    }

    public void setAssetItem(AssetItem assetItem) {
        this.assetItem = assetItem;
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

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getPreqty() {
        return preqty;
    }

    public void setPreqty(BigDecimal preqty) {
        this.preqty = preqty;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getObjectkind() {
        return objectkind;
    }

    public void setObjectkind(String objectkind) {
        this.objectkind = objectkind;
    }

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

    public Date getIndate() {
        return indate;
    }

    public void setIndate(Date indate) {
        this.indate = indate;
    }

    public Date getOutdate() {
        return outdate;
    }

    public void setOutdate(Date outdate) {
        this.outdate = outdate;
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
        if (!(object instanceof AssetInventory)) {
            return false;
        }
        AssetInventory other = (AssetInventory) object;
        return this.assetItem.getItemno().equals(other.assetItem.getItemno())
                && this.brand.equals(other.brand)
                && this.batch.equals(other.batch) && this.sn.equals(other.sn)
                && this.warehouse.getWarehouseno().equals(other.warehouse.getWarehouseno());
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetInventory[ id=" + id + " ]";
    }

}
