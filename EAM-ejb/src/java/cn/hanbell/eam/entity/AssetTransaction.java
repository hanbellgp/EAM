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
@Table(name = "assettransaction")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetTransaction.findAll", query = "SELECT a FROM AssetTransaction a"),
    @NamedQuery(name = "AssetTransaction.findById", query = "SELECT a FROM AssetTransaction a WHERE a.id = :id"),
    @NamedQuery(name = "AssetTransaction.findByCompany", query = "SELECT a FROM AssetTransaction a WHERE a.company = :company"),
    @NamedQuery(name = "AssetTransaction.findByTrtype", query = "SELECT a FROM AssetTransaction a WHERE a.trtype = :trtype"),
    @NamedQuery(name = "AssetTransaction.findByFormid", query = "SELECT a FROM AssetTransaction a WHERE a.formid = :formid ORDER BY a.seq"),
    @NamedQuery(name = "AssetTransaction.findByFormidAndSeq", query = "SELECT a FROM AssetTransaction a WHERE a.formid = :formid AND a.seq = :seq"),
    @NamedQuery(name = "AssetTransaction.findByStatus", query = "SELECT a FROM AssetTransaction a WHERE a.status = :status")})
public class AssetTransaction extends SuperEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;

    @JoinColumn(name = "trtype", referencedColumnName = "trtype")
    @ManyToOne(optional = false)
    private TransactionType trtype;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "formid")
    private String formid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "formdate")
    @Temporal(TemporalType.DATE)
    private Date formdate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "seq")
    private int seq;
    @Size(max = 20)
    @Column(name = "requireDeptno")
    private String requireDeptno;
    @Size(max = 45)
    @Column(name = "requireDeptname")
    private String requireDeptname;
    @Size(max = 20)
    @Column(name = "requireUserno")
    private String requireUserno;
    @Size(max = 45)
    @Column(name = "requireUsername")
    private String requireUsername;

    @JoinColumn(name = "assetid", referencedColumnName = "id")
    @ManyToOne
    private AssetCard assetCard;

    @Size(max = 20)
    @Column(name = "assetno")
    private String assetno;

    @JoinColumn(name = "itemno", referencedColumnName = "itemno")
    @ManyToOne(optional = false)
    private AssetItem assetItem;

    @Size(max = 20)
    @Column(name = "brand")
    private String brand;
    @Size(max = 20)
    @Column(name = "batch")
    private String batch;
    @Size(max = 20)
    @Column(name = "sn")
    private String sn;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "qty")
    private BigDecimal qty;
    @Size(max = 10)
    @Column(name = "unit")
    private String unit;
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;

    @JoinColumn(name = "warehouseno", referencedColumnName = "warehouseno")
    @ManyToOne(optional = false)
    private Warehouse warehouse;
    @Basic(optional = false)
    @NotNull
    @Column(name = "iocode")
    private Integer iocode;
    @Size(max = 100)
    @Column(name = "srcapi")
    private String srcapi;
    @Size(max = 20)
    @Column(name = "srcformid")
    private String srcformid;
    @Column(name = "srcseq")
    private Integer srcseq;
    @Size(max = 100)
    @Column(name = "relapi")
    private String relapi;
    @Size(max = 20)
    @Column(name = "relformid")
    private String relformid;
    @Column(name = "relseq")
    private Integer relseq;
    @Size(max = 100)
    @Column(name = "remark")
    private String remark;

    public AssetTransaction() {
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public TransactionType getTrtype() {
        return trtype;
    }

    public void setTrtype(TransactionType trtype) {
        this.trtype = trtype;
    }

    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }

    public Date getFormdate() {
        return formdate;
    }

    public void setFormdate(Date formdate) {
        this.formdate = formdate;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getRequireDeptno() {
        return requireDeptno;
    }

    public void setRequireDeptno(String requireDeptno) {
        this.requireDeptno = requireDeptno;
    }

    public String getRequireDeptname() {
        return requireDeptname;
    }

    public void setRequireDeptname(String requireDeptname) {
        this.requireDeptname = requireDeptname;
    }

    public String getRequireUserno() {
        return requireUserno;
    }

    public void setRequireUserno(String requireUserno) {
        this.requireUserno = requireUserno;
    }

    public String getRequireUsername() {
        return requireUsername;
    }

    public void setRequireUsername(String requireUsername) {
        this.requireUsername = requireUsername;
    }

    public AssetCard getAssetCard() {
        return assetCard;
    }

    public void setAssetCard(AssetCard assetCard) {
        this.assetCard = assetCard;
    }

    public String getAssetno() {
        return assetno;
    }

    public void setAssetno(String assetno) {
        this.assetno = assetno;
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

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public String getSrcapi() {
        return srcapi;
    }

    public void setSrcapi(String srcapi) {
        this.srcapi = srcapi;
    }

    public String getSrcformid() {
        return srcformid;
    }

    public void setSrcformid(String srcformid) {
        this.srcformid = srcformid;
    }

    public Integer getSrcseq() {
        return srcseq;
    }

    public void setSrcseq(Integer srcseq) {
        this.srcseq = srcseq;
    }

    public String getRelapi() {
        return relapi;
    }

    public void setRelapi(String relapi) {
        this.relapi = relapi;
    }

    public String getRelformid() {
        return relformid;
    }

    public void setRelformid(String relformid) {
        this.relformid = relformid;
    }

    public Integer getRelseq() {
        return relseq;
    }

    public void setRelseq(Integer relseq) {
        this.relseq = relseq;
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
        if (!(object instanceof AssetTransaction)) {
            return false;
        }
        AssetTransaction other = (AssetTransaction) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetTransaction[ id=" + id + " ]";
    }

    /**
     * @return the iocode
     */
    public Integer getIocode() {
        return iocode;
    }

    /**
     * @param iocode the iocode to set
     */
    public void setIocode(Integer iocode) {
        this.iocode = iocode;
    }

}
