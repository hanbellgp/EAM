/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.FormDetailEntity;
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
@Table(name = "assetapplydetail")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetApplyDetail.findAll", query = "SELECT a FROM AssetApplyDetail a"),
    @NamedQuery(name = "AssetApplyDetail.findById", query = "SELECT a FROM AssetApplyDetail a WHERE a.id = :id"),
    @NamedQuery(name = "AssetApplyDetail.findByPId", query = "SELECT a FROM AssetApplyDetail a WHERE a.pid = :pid"),
    @NamedQuery(name = "AssetApplyDetail.findByDistributed", query = "SELECT a FROM AssetApplyDetail a WHERE a.distributed = :distributed"),
    @NamedQuery(name = "AssetApplyDetail.findByPurchased", query = "SELECT a FROM AssetApplyDetail a WHERE a.purchased = :purchased"),
    @NamedQuery(name = "AssetApplyDetail.findByStatus", query = "SELECT a FROM AssetApplyDetail a WHERE a.status = :status")})
public class AssetApplyDetail extends FormDetailEntity {

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

    @JoinColumn(name = "itemno", referencedColumnName = "itemno")
    @ManyToOne(optional = false)
    private AssetItem assetItem;
    @Size(max = 45)
    @Column(name = "itemdesc")
    private String itemdesc;
    @Size(max = 45)
    @Column(name = "itemspec")
    private String itemspec;
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
    @Column(name = "disqty")
    private BigDecimal disqty;
    @Column(name = "purqty")
    private BigDecimal purqty;
    @Column(name = "accqty")
    private BigDecimal accqty;
    @Size(max = 10)
    @Column(name = "unit")
    private String unit;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "currency")
    private String currency;
    @Basic(optional = false)
    @NotNull
    @Column(name = "exchange")
    private BigDecimal exchange;
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "amts")
    private BigDecimal amts;
    @Size(max = 20)
    @Column(name = "budgetacc")
    private String budgetacc;
    @Size(max = 45)
    @Column(name = "budgetaccname")
    private String budgetaccname;
    @Size(max = 20)
    @Column(name = "dmark1")
    private String dmark1;
    @Size(max = 45)
    @Column(name = "dmark1desc")
    private String dmark1desc;
    @Size(max = 20)
    @Column(name = "dmark2")
    private String dmark2;
    @Size(max = 45)
    @Column(name = "dmark2desc")
    private String dmark2desc;
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
    @Column(name = "prapi")
    private String prapi;
    @Size(max = 45)
    @Column(name = "prformid")
    private String prformid;
    @Column(name = "prseq")
    private Integer prseq;
    @Column(name = "distributed")
    private Boolean distributed;
    @Column(name = "purchased")
    private Boolean purchased;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "status")
    private String status;
    @Size(max = 20)
    @Column(name = "creator")
    private String creator;
    @Column(name = "credate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date credate;
    @Size(max = 20)
    @Column(name = "optuser")
    private String optuser;
    @Column(name = "optdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date optdate;
    @Size(max = 20)
    @Column(name = "cfmuser")
    private String cfmuser;
    @Column(name = "cfmdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cfmdate;

    @Size(max = 100)
    @Column(name = "remark")
    private String remark;

    public AssetApplyDetail() {
        this.qty = BigDecimal.ONE;
        this.disqty = BigDecimal.ZERO;
        this.purqty = BigDecimal.ZERO;
        this.accqty = BigDecimal.ZERO;
        this.price = BigDecimal.ZERO;
        this.amts = BigDecimal.ZERO;
    }

    public AssetItem getAssetItem() {
        return assetItem;
    }

    public void setAssetItem(AssetItem assetItem) {
        this.assetItem = assetItem;
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

    public BigDecimal getDisqty() {
        return disqty;
    }

    public void setDisqty(BigDecimal disqty) {
        this.disqty = disqty;
    }

    public BigDecimal getPurqty() {
        return purqty;
    }

    public void setPurqty(BigDecimal purqty) {
        this.purqty = purqty;
    }

    /**
     * @return the accqty
     */
    public BigDecimal getAccqty() {
        return accqty;
    }

    /**
     * @param accqty the accqty to set
     */
    public void setAccqty(BigDecimal accqty) {
        this.accqty = accqty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getExchange() {
        return exchange;
    }

    public void setExchange(BigDecimal exchange) {
        this.exchange = exchange;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmts() {
        return amts;
    }

    public void setAmts(BigDecimal amts) {
        this.amts = amts;
    }

    public String getBudgetacc() {
        return budgetacc;
    }

    public void setBudgetacc(String budgetacc) {
        this.budgetacc = budgetacc;
    }

    public String getBudgetaccname() {
        return budgetaccname;
    }

    public void setBudgetaccname(String budgetaccname) {
        this.budgetaccname = budgetaccname;
    }

    public String getDmark1() {
        return dmark1;
    }

    public void setDmark1(String dmark1) {
        this.dmark1 = dmark1;
    }

    public String getDmark1desc() {
        return dmark1desc;
    }

    public void setDmark1desc(String dmark1desc) {
        this.dmark1desc = dmark1desc;
    }

    public String getDmark2() {
        return dmark2;
    }

    public void setDmark2(String dmark2) {
        this.dmark2 = dmark2;
    }

    public String getDmark2desc() {
        return dmark2desc;
    }

    public void setDmark2desc(String dmark2desc) {
        this.dmark2desc = dmark2desc;
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

    public String getPrapi() {
        return prapi;
    }

    public void setPrapi(String prapi) {
        this.prapi = prapi;
    }

    public String getPrformid() {
        return prformid;
    }

    public void setPrformid(String prformid) {
        this.prformid = prformid;
    }

    public Integer getPrseq() {
        return prseq;
    }

    public void setPrseq(Integer prseq) {
        this.prseq = prseq;
    }

    public Boolean getDistributed() {
        return distributed;
    }

    public void setDistributed(Boolean distributed) {
        this.distributed = distributed;
    }

    public Boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCredate() {
        return credate;
    }

    public void setCredate(Date credate) {
        this.credate = credate;
    }

    public String getOptuser() {
        return optuser;
    }

    public void setOptuser(String optuser) {
        this.optuser = optuser;
    }

    public Date getOptdate() {
        return optdate;
    }

    public void setOptdate(Date optdate) {
        this.optdate = optdate;
    }

    public String getCfmuser() {
        return cfmuser;
    }

    public void setCfmuser(String cfmuser) {
        this.cfmuser = cfmuser;
    }

    public Date getCfmdate() {
        return cfmdate;
    }

    public void setCfmdate(Date cfmdate) {
        this.cfmdate = cfmdate;
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
        if (!(object instanceof AssetApplyDetail)) {
            return false;
        }
        AssetApplyDetail other = (AssetApplyDetail) object;
        if (this.id != null && other.id != null) {
            return this.id.equals(other.id);
        }
        return this.seq == other.seq;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetApplyDetail[ id=" + id + " ]";
    }

    /**
     * @return the requireDeptno
     */
    public String getRequireDeptno() {
        return requireDeptno;
    }

    /**
     * @param requireDeptno the requireDeptno to set
     */
    public void setRequireDeptno(String requireDeptno) {
        this.requireDeptno = requireDeptno;
    }

    /**
     * @return the requireDeptname
     */
    public String getRequireDeptname() {
        return requireDeptname;
    }

    /**
     * @param requireDeptname the requireDeptname to set
     */
    public void setRequireDeptname(String requireDeptname) {
        this.requireDeptname = requireDeptname;
    }

    /**
     * @return the requireUserno
     */
    public String getRequireUserno() {
        return requireUserno;
    }

    /**
     * @param requireUserno the requireUserno to set
     */
    public void setRequireUserno(String requireUserno) {
        this.requireUserno = requireUserno;
    }

    /**
     * @return the requireUsername
     */
    public String getRequireUsername() {
        return requireUsername;
    }

    /**
     * @param requireUsername the requireUsername to set
     */
    public void setRequireUsername(String requireUsername) {
        this.requireUsername = requireUsername;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
