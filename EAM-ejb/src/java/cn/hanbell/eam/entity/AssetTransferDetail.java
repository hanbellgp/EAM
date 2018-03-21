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
@Table(name = "assettransferdetail")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetTransferDetail.findAll", query = "SELECT a FROM AssetTransferDetail a"),
    @NamedQuery(name = "AssetTransferDetail.findById", query = "SELECT a FROM AssetTransferDetail a WHERE a.id = :id"),
    @NamedQuery(name = "AssetTransferDetail.findByPId", query = "SELECT a FROM AssetTransferDetail a WHERE a.pid = :pid"),
    @NamedQuery(name = "AssetTransferDetail.findByDeptno", query = "SELECT a FROM AssetTransferDetail a WHERE a.deptno = :deptno"),
    @NamedQuery(name = "AssetTransferDetail.findByDeptname", query = "SELECT a FROM AssetTransferDetail a WHERE a.deptname = :deptname"),
    @NamedQuery(name = "AssetTransferDetail.findByUserno", query = "SELECT a FROM AssetTransferDetail a WHERE a.userno = :userno"),
    @NamedQuery(name = "AssetTransferDetail.findByUsername", query = "SELECT a FROM AssetTransferDetail a WHERE a.username = :username")})
public class AssetTransferDetail extends FormDetailEntity {

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
    @Column(name = "buyPrice")
    private BigDecimal buyPrice;
    @Column(name = "buyAmts")
    private BigDecimal buyAmts;
    @Column(name = "buyDate")
    @Temporal(TemporalType.DATE)
    private Date buyDate;
    @Column(name = "usedMonth")
    private Integer usedMonth;
    @Column(name = "surplusValue")
    private BigDecimal surplusValue;
    @Column(name = "amts")
    private BigDecimal amts;
    @Basic(optional = false)
    @NotNull
    @Column(name = "used")
    private boolean used;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;

    @JoinColumn(name = "position1", referencedColumnName = "id")
    @ManyToOne
    private AssetPosition position1;
    @JoinColumn(name = "position2", referencedColumnName = "id")
    @ManyToOne
    private AssetPosition position2;
    @JoinColumn(name = "position3", referencedColumnName = "id")
    @ManyToOne
    private AssetPosition position3;
    @JoinColumn(name = "position4", referencedColumnName = "id")
    @ManyToOne
    private AssetPosition position4;
    @JoinColumn(name = "position5", referencedColumnName = "id")
    @ManyToOne
    private AssetPosition position5;
    @JoinColumn(name = "position6", referencedColumnName = "id")
    @ManyToOne
    private AssetPosition position6;

    @Size(max = 20)
    @Column(name = "deptno")
    private String deptno;
    @Size(max = 45)
    @Column(name = "deptname")
    private String deptname;
    @Size(max = 20)
    @Column(name = "userno")
    private String userno;
    @Size(max = 45)
    @Column(name = "username")
    private String username;

    @JoinColumn(name = "warehouseno", referencedColumnName = "warehouseno")
    @ManyToOne
    private Warehouse warehouse;

    @Size(max = 100)
    @Column(name = "remark")
    private String remark;
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

    public AssetTransferDetail() {
    }

    /**
     * @return the assetCard
     */
    public AssetCard getAssetCard() {
        return assetCard;
    }

    /**
     * @param assetCard the assetCard to set
     */
    public void setAssetCard(AssetCard assetCard) {
        this.assetCard = assetCard;
    }

    /**
     * @return the assetItem
     */
    public AssetItem getAssetItem() {
        return assetItem;
    }

    /**
     * @param assetItem the assetItem to set
     */
    public void setAssetItem(AssetItem assetItem) {
        this.assetItem = assetItem;
    }

    public String getAssetno() {
        return assetno;
    }

    public void setAssetno(String assetno) {
        this.assetno = assetno;
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

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public BigDecimal getBuyAmts() {
        return buyAmts;
    }

    public void setBuyAmts(BigDecimal buyAmts) {
        this.buyAmts = buyAmts;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    public Integer getUsedMonth() {
        return usedMonth;
    }

    public void setUsedMonth(Integer usedMonth) {
        this.usedMonth = usedMonth;
    }

    public BigDecimal getSurplusValue() {
        return surplusValue;
    }

    public void setSurplusValue(BigDecimal surplusValue) {
        this.surplusValue = surplusValue;
    }

    public BigDecimal getAmts() {
        return amts;
    }

    public void setAmts(BigDecimal amts) {
        this.amts = amts;
    }

    public boolean getUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * @return the position1
     */
    public AssetPosition getPosition1() {
        return position1;
    }

    /**
     * @param position1 the position1 to set
     */
    public void setPosition1(AssetPosition position1) {
        this.position1 = position1;
    }

    /**
     * @return the position2
     */
    public AssetPosition getPosition2() {
        return position2;
    }

    /**
     * @param position2 the position2 to set
     */
    public void setPosition2(AssetPosition position2) {
        this.position2 = position2;
    }

    /**
     * @return the position3
     */
    public AssetPosition getPosition3() {
        return position3;
    }

    /**
     * @param position3 the position3 to set
     */
    public void setPosition3(AssetPosition position3) {
        this.position3 = position3;
    }

    /**
     * @return the position4
     */
    public AssetPosition getPosition4() {
        return position4;
    }

    /**
     * @param position4 the position4 to set
     */
    public void setPosition4(AssetPosition position4) {
        this.position4 = position4;
    }

    /**
     * @return the position5
     */
    public AssetPosition getPosition5() {
        return position5;
    }

    /**
     * @param position5 the position5 to set
     */
    public void setPosition5(AssetPosition position5) {
        this.position5 = position5;
    }

    /**
     * @return the position6
     */
    public AssetPosition getPosition6() {
        return position6;
    }

    /**
     * @param position6 the position6 to set
     */
    public void setPosition6(AssetPosition position6) {
        this.position6 = position6;
    }

    public String getDeptno() {
        return deptno;
    }

    public void setDeptno(String deptno) {
        this.deptno = deptno;
    }

    public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public String getUsername() {
        return username;
    }

    /**
     * @return the warehouse
     */
    public Warehouse getWarehouse() {
        return warehouse;
    }

    /**
     * @param warehouse the warehouse to set
     */
    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AssetTransferDetail)) {
            return false;
        }
        AssetTransferDetail other = (AssetTransferDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetTransferDetail[ id=" + id + " ]";
    }

}
