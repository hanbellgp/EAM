/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.FormEntity;
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
@Table(name = "assetcard")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetCard.getRowCount", query = "SELECT COUNT(a) FROM AssetCard a"),
    @NamedQuery(name = "AssetCard.findAll", query = "SELECT a FROM AssetCard a"),
    @NamedQuery(name = "AssetCard.findById", query = "SELECT a FROM AssetCard a WHERE a.id = :id"),
    @NamedQuery(name = "AssetCard.findByCompany", query = "SELECT a FROM AssetCard a WHERE a.company = :company"),
    @NamedQuery(name = "AssetCard.findByCompanyAndFormid", query = "SELECT a FROM AssetCard a WHERE  a.company = :company AND a.formid = :formid"),
    @NamedQuery(name = "AssetCard.findByFormid", query = "SELECT a FROM AssetCard a WHERE a.formid = :formid"),
    @NamedQuery(name = "AssetCard.findByFilters", query = "SELECT a FROM AssetCard a WHERE a.company = :company AND a.formid = :formid AND a.assetItem.itemno = :itemno AND a.deptno = :deptno AND a.userno = :userno "),
    @NamedQuery(name = "AssetCard.findByFiltersAndNotUsed", query = "SELECT a FROM AssetCard a WHERE a.srcformid = :srcformid AND a.srcseq = :srcseq AND a.used=false "),
    @NamedQuery(name = "AssetCard.findByFiltersAndUsed", query = "SELECT a FROM AssetCard a WHERE a.relformid = :relformid AND a.relseq = :relseq AND a.used=true AND a.scrap=false"),
    @NamedQuery(name = "AssetCard.findByFiltersAndScrapped", query = "SELECT a FROM AssetCard a WHERE a.relformid = :relformid AND a.relseq = :relseq AND a.used=true AND a.scrap=true"),
    @NamedQuery(name = "AssetCard.findByItemno", query = "SELECT a FROM AssetCard a WHERE a.assetItem.itemno = :itemno"),
    @NamedQuery(name = "AssetCard.findByItemnoAndNotUsed", query = "SELECT a FROM AssetCard a WHERE a.assetItem.itemno = :itemno AND a.used=false "),
    @NamedQuery(name = "AssetCard.findByDeptno", query = "SELECT a FROM AssetCard a WHERE a.deptno = :deptno"),
    @NamedQuery(name = "AssetCard.findByUserno", query = "SELECT a FROM AssetCard a WHERE a.userno = :userno"),
    @NamedQuery(name = "AssetCard.findBySrcformid", query = "SELECT a FROM AssetCard a WHERE a.srcformid = :srcformid "),
    @NamedQuery(name = "AssetCard.findByRelformidAndNeedDelete", query = "SELECT a FROM AssetCard a WHERE a.relformid = :relformid AND a.status='X' "),
    @NamedQuery(name = "AssetCard.findByWarehouseno", query = "SELECT a FROM AssetCard a WHERE a.warehouse.warehouseno = :warehouseno")})
public class AssetCard extends FormEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "assetDesc")
    private String assetDesc;
    @Size(max = 45)
    @Column(name = "assetSpec")
    private String assetSpec;
    @Size(max = 45)
    @Column(name = "assetDesc2")
    private String assetDesc2;
    @Size(max = 45)
    @Column(name = "assetSpec2")
    private String assetSpec2;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "unit")
    private String unit;
    @Size(max = 100)
    @Column(name = "img")
    private String img;
    @Size(max = 45)
    @Column(name = "code")
    private String code;

    @JoinColumn(name = "itemno", referencedColumnName = "itemno")
    @ManyToOne(optional = false)
    private AssetItem assetItem;

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

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "qty")
    private BigDecimal qty;
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
    @Column(name = "amts")
    private BigDecimal amts;
    @Basic(optional = false)
    @NotNull
    @Column(name = "buyDate")
    @Temporal(TemporalType.DATE)
    private Date buyDate;
    @Column(name = "upkeep")
    private BigDecimal upkeep;
    @Column(name = "upkeepDate")
    @Temporal(TemporalType.DATE)
    private Date upkeepDate;
    @Column(name = "surplusRate")
    private BigDecimal surplusRate;
    @Column(name = "surplusValue")
    private BigDecimal surplusValue;
    @Basic(optional = false)
    @NotNull
    @Column(name = "used")
    private boolean used;
    @Column(name = "usingDate")
    @Temporal(TemporalType.DATE)
    private Date usingDate;
    @Column(name = "pause")
    private Boolean pause;
    @Column(name = "pauseDate")
    @Temporal(TemporalType.DATE)
    private Date pauseDate;
    @Column(name = "scrap")
    private Boolean scrap;
    @Column(name = "scrapDate")
    @Temporal(TemporalType.DATE)
    private Date scrapDate;
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
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;

    public AssetCard() {
        this.amts = BigDecimal.ZERO;
        this.used = false;
        this.pause = false;
        this.scrap = false;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAssetDesc() {
        return assetDesc;
    }

    public void setAssetDesc(String assetDesc) {
        this.assetDesc = assetDesc;
    }

    public String getAssetSpec() {
        return assetSpec;
    }

    public void setAssetSpec(String assetSpec) {
        this.assetSpec = assetSpec;
    }

    public String getAssetDesc2() {
        return assetDesc2;
    }

    public void setAssetDesc2(String assetDesc2) {
        this.assetDesc2 = assetDesc2;
    }

    public String getAssetSpec2() {
        return assetSpec2;
    }

    public void setAssetSpec2(String assetSpec2) {
        this.assetSpec2 = assetSpec2;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AssetItem getAssetItem() {
        return assetItem;
    }

    public void setAssetItem(AssetItem assetItem) {
        this.assetItem = assetItem;
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

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
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

    public BigDecimal getAmts() {
        return amts;
    }

    public void setAmts(BigDecimal amts) {
        this.amts = amts;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    public BigDecimal getUpkeep() {
        return upkeep;
    }

    public void setUpkeep(BigDecimal upkeep) {
        this.upkeep = upkeep;
    }

    public Date getUpkeepDate() {
        return upkeepDate;
    }

    public void setUpkeepDate(Date upkeepDate) {
        this.upkeepDate = upkeepDate;
    }

    public BigDecimal getSurplusRate() {
        return surplusRate;
    }

    public void setSurplusRate(BigDecimal surplusRate) {
        this.surplusRate = surplusRate;
    }

    public BigDecimal getSurplusValue() {
        return surplusValue;
    }

    public void setSurplusValue(BigDecimal surplusValue) {
        this.surplusValue = surplusValue;
    }

    public boolean getUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Date getUsingDate() {
        return usingDate;
    }

    public void setUsingDate(Date usingDate) {
        this.usingDate = usingDate;
    }

    public Boolean getPause() {
        return pause;
    }

    public void setPause(Boolean pause) {
        this.pause = pause;
    }

    public Date getPauseDate() {
        return pauseDate;
    }

    public void setPauseDate(Date pauseDate) {
        this.pauseDate = pauseDate;
    }

    public Boolean getScrap() {
        return scrap;
    }

    public void setScrap(Boolean scrap) {
        this.scrap = scrap;
    }

    public Date getScrapDate() {
        return scrapDate;
    }

    public void setScrapDate(Date scrapDate) {
        this.scrapDate = scrapDate;
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
        if (!(object instanceof AssetCard)) {
            return false;
        }
        AssetCard other = (AssetCard) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetCard[ id=" + id + " ]";
    }

}
