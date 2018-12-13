/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.FormDetailEntity;
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
@Table(name = "assetdistributedetail")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetDistributeDetail.findAll", query = "SELECT a FROM AssetDistributeDetail a"),
    @NamedQuery(name = "AssetDistributeDetail.findById", query = "SELECT a FROM AssetDistributeDetail a WHERE a.id = :id"),
    @NamedQuery(name = "AssetDistributeDetail.findByPId", query = "SELECT a FROM AssetDistributeDetail a WHERE a.pid = :pid ORDER BY a.seq")})
public class AssetDistributeDetail extends FormDetailEntity {

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
    @NotNull
    @Column(name = "price")
    private BigDecimal price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "amts")
    private BigDecimal amts;
    @Basic(optional = false)
    @Size(max = 10)
    @Column(name = "unit")
    private String unit;

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
    @JoinColumn(name = "warehouseno2", referencedColumnName = "warehouseno")
    @ManyToOne
    private Warehouse warehouse2;

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

    public AssetDistributeDetail() {
        this.price = BigDecimal.ZERO;
        this.amts = BigDecimal.ZERO;
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

    /**
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * @return the amts
     */
    public BigDecimal getAmts() {
        return amts;
    }

    /**
     * @param amts the amts to set
     */
    public void setAmts(BigDecimal amts) {
        this.amts = amts;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public AssetPosition getPosition1() {
        return position1;
    }

    public void setPosition1(AssetPosition position1) {
        this.position1 = position1;
    }

    public AssetPosition getPosition2() {
        return position2;
    }

    public void setPosition2(AssetPosition position2) {
        this.position2 = position2;
    }

    public AssetPosition getPosition3() {
        return position3;
    }

    public void setPosition3(AssetPosition position3) {
        this.position3 = position3;
    }

    public AssetPosition getPosition4() {
        return position4;
    }

    public void setPosition4(AssetPosition position4) {
        this.position4 = position4;
    }

    public AssetPosition getPosition5() {
        return position5;
    }

    public void setPosition5(AssetPosition position5) {
        this.position5 = position5;
    }

    public AssetPosition getPosition6() {
        return position6;
    }

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

    public void setUsername(String username) {
        this.username = username;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    /**
     * @return the warehouse2
     */
    public Warehouse getWarehouse2() {
        return warehouse2;
    }

    /**
     * @param warehouse2 the warehouse2 to set
     */
    public void setWarehouse2(Warehouse warehouse2) {
        this.warehouse2 = warehouse2;
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
        if (!(object instanceof AssetDistributeDetail)) {
            return false;
        }
        AssetDistributeDetail other = (AssetDistributeDetail) object;
        if (this.id != null && other.id != null) {
            return this.id.equals(other.id);
        }
        if (this.pid != null && other.pid != null) {
            return (this.pid.equals(other.pid) && (this.seq == other.seq));
        }
        return this.seq == other.seq;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetDistributeDetail[ id=" + id + " ]";
    }

}
