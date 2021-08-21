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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
 * @author Administrator
 */
@Entity
@Table(name = "equipmentsparestock")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentSpareStock.findAll", query = "SELECT e FROM EquipmentSpareStock e"),
    @NamedQuery(name = "EquipmentSpareStock.findById", query = "SELECT e FROM EquipmentSpareStock e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentSpareStock.findByCompany", query = "SELECT e FROM EquipmentSpareStock e WHERE e.company = :company"),
    @NamedQuery(name = "EquipmentSpareStock.findByIntime", query = "SELECT e FROM EquipmentSpareStock e WHERE e.intime = :intime"),
    @NamedQuery(name = "EquipmentSpareStock.findBySparenum", query = "SELECT e FROM EquipmentSpareStock e WHERE e.sparenum.sparenum = :sparenum"),
    @NamedQuery(name = "EquipmentSpareStock.findBySparenumAndRemark", query = "SELECT e FROM EquipmentSpareStock e WHERE e.sparenum.sparenum = :sparenum And e.remark = :remark AND e.slocation = :slocation ORDER BY e.intime ASC"),
    @NamedQuery(name = "EquipmentSpareStock.findBySparenumAndSarea", query = "SELECT e FROM EquipmentSpareStock e WHERE e.sparenum.sparenum = :sparenum And e.sarea = :sarea  And e.qty !=:qty  ORDER BY e.intime ASC"),
    @NamedQuery(name = "EquipmentSpareStock.findBySparenumAndLocation", query = "SELECT e FROM EquipmentSpareStock e WHERE e.sparenum.sparenum = :sparenum And e.slocation = :slocation   ORDER BY e.intime ASC"),
    @NamedQuery(name = "EquipmentSpareStock.findByQty", query = "SELECT e FROM EquipmentSpareStock e WHERE e.qty = :qty"),
    @NamedQuery(name = "EquipmentSpareStock.findByUprice", query = "SELECT e FROM EquipmentSpareStock e WHERE e.uprice = :uprice"),
    @NamedQuery(name = "EquipmentSpareStock.findByBrand", query = "SELECT e FROM EquipmentSpareStock e WHERE e.brand = :brand"),
    @NamedQuery(name = "EquipmentSpareStock.findBySarea", query = "SELECT e FROM EquipmentSpareStock e WHERE e.sarea = :sarea"),
    @NamedQuery(name = "EquipmentSpareStock.findByWarehouseno", query = "SELECT e FROM EquipmentSpareStock e WHERE e.warehouseno = :warehouseno"),
    @NamedQuery(name = "EquipmentSpareStock.findBySlocation", query = "SELECT e FROM EquipmentSpareStock e WHERE e.slocation = :slocation"),
    @NamedQuery(name = "EquipmentSpareStock.findByRemark", query = "SELECT e FROM EquipmentSpareStock e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentSpareStock.findByStatus", query = "SELECT e FROM EquipmentSpareStock e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentSpareStock.findByCreator", query = "SELECT e FROM EquipmentSpareStock e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentSpareStock.findByCredate", query = "SELECT e FROM EquipmentSpareStock e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentSpareStock.findByOptuser", query = "SELECT e FROM EquipmentSpareStock e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentSpareStock.findByOptdate", query = "SELECT e FROM EquipmentSpareStock e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentSpareStock.findByCfmuser", query = "SELECT e FROM EquipmentSpareStock e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentSpareStock.findByCfmdate", query = "SELECT e FROM EquipmentSpareStock e WHERE e.cfmdate = :cfmdate")})
public class EquipmentSpareStock extends SuperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Column(name = "intime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date intime;
    @JoinColumn(name = "sparenum", referencedColumnName = "sparenum")
    @ManyToOne
    private EquipmentSpare sparenum;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "qty")
    private BigDecimal qty;
    @Column(name = "uprice")
    private BigDecimal uprice;
    @Size(max = 50)
    @Column(name = "brand")
    private String brand;
    @Size(max = 10)
    @Column(name = "sarea")
    private String sarea;
    @Size(max = 20)
    @Column(name = "warehouseno")
    private String warehouseno;
    @Size(max = 15)
    @Column(name = "slocation")
    private String slocation;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;

    public EquipmentSpareStock() {
    }

    public EquipmentSpareStock(Integer id) {
        this.id = id;
    }

    public EquipmentSpareStock(Integer id, String company, EquipmentSpare sparenum, String status) {
        this.id = id;
        this.company = company;
        this.sparenum = sparenum;
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Date getIntime() {
        return intime;
    }

    public void setIntime(Date intime) {
        this.intime = intime;
    }

    public EquipmentSpare getSparenum() {
        return sparenum;
    }

    public void setSparenum(EquipmentSpare sparenum) {
        this.sparenum = sparenum;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getUprice() {
        return uprice;
    }

    public void setUprice(BigDecimal uprice) {
        this.uprice = uprice;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSarea() {
        return sarea;
    }

    public void setSarea(String sarea) {
        this.sarea = sarea;
    }

    public String getWarehouseno() {
        return warehouseno;
    }

    public void setWarehouseno(String warehouseno) {
        this.warehouseno = warehouseno;
    }

    public String getSlocation() {
        return slocation;
    }

    public void setSlocation(String slocation) {
        this.slocation = slocation;
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
        if (!(object instanceof EquipmentSpareStock)) {
            return false;
        }
        EquipmentSpareStock other = (EquipmentSpareStock) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentSpareStock[ id=" + id + " ]";
    }

}
