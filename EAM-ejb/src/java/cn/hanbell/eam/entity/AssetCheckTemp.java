/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;
import com.lightshell.comm.SuperEntity;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
/**
 *
 * @author C2231
 */
@Entity
@Table(name = "assetchecktemp")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssetCheckTemp.findAll", query = "SELECT a FROM AssetCheckTemp a"),
    @NamedQuery(name = "AssetCheckTemp.findById", query = "SELECT a FROM AssetCheckTemp a WHERE a.id = :id"),
    @NamedQuery(name = "AssetCheckTemp.findByCompany", query = "SELECT e FROM AssetCheckTemp e WHERE e.company = :company"),
    @NamedQuery(name = "AssetCheckTemp.findByFormid", query = "SELECT e FROM AssetCheckTemp e WHERE e.formid = :formid"),
    @NamedQuery(name = "AssetCheckTemp.findByItemno", query = "SELECT e FROM AssetCheckTemp e WHERE e.itemno = :itemno"),
    @NamedQuery(name = "AssetCheckTemp.findByAssetno", query = "SELECT e FROM AssetCheckTemp e WHERE e.assetno = :assetno"),
    @NamedQuery(name = "AssetCheckTemp.findByAssetDesc", query = "SELECT e FROM AssetCheckTemp e WHERE e.assetDesc = :assetDesc"),
    @NamedQuery(name = "AssetCheckTemp.findByAssetSpec", query = "SELECT e FROM AssetCheckTemp e WHERE e.assetSpec = :assetSpec"),
    @NamedQuery(name = "AssetCheckTemp.findByQty", query = "SELECT e FROM AssetCheckTemp e WHERE e.qty = :qty"),
    @NamedQuery(name = "AssetCheckTemp.findByUserno", query = "SELECT a FROM AssetCheckTemp a WHERE a.userno = :userno"),
    @NamedQuery(name = "AssetCheckTemp.findByUsername", query = "SELECT e FROM AssetCheckTemp e WHERE e.username = :username"),
    @NamedQuery(name = "AssetCheckTemp.findByDeptno", query = "SELECT a FROM AssetCheckTemp a WHERE a.deptno = :deptno"),
    @NamedQuery(name = "AssetCheckTemp.findByDeptname", query = "SELECT e FROM AssetCheckTemp e WHERE e.deptname = :deptname"),
    @NamedQuery(name = "AssetCheckTemp.findByAddress", query = "SELECT a FROM AssetCheckTemp a WHERE a.address = :address"),
    @NamedQuery(name = "AssetCheckTemp.findByItimes", query = "SELECT e FROM AssetCheckTemp e WHERE e.itimes = :itimes"),
    @NamedQuery(name = "AssetCheckTemp.findByIqty", query = "SELECT a FROM AssetCheckTemp a WHERE a.iqty = :iqty"),
    @NamedQuery(name = "AssetCheckTemp.findByIaddress", query = "SELECT a FROM AssetCheckTemp a WHERE a.iaddress = :iaddress"),
    @NamedQuery(name = "AssetCheckTemp.findByRemark", query = "SELECT a FROM AssetCheckTemp a WHERE a.remark = :remark"),
    @NamedQuery(name = "AssetCheckTemp.findByStatus", query = "SELECT a FROM AssetCheckTemp a WHERE a.status = :status"),
    @NamedQuery(name = "AssetCheckTemp.findByCreator", query = "SELECT a FROM AssetCheckTemp a WHERE a.creator = :creator"),
    @NamedQuery(name = "AssetCheckTemp.findByCredate", query = "SELECT e FROM AssetCheckTemp e WHERE e.credate = :credate"),
    @NamedQuery(name = "AssetCheckTemp.findByOptuser", query = "SELECT a FROM AssetCheckTemp a WHERE a.optuser = :optuser"),
    @NamedQuery(name = "AssetCheckTemp.findByOptdate", query = "SELECT a FROM AssetCheckTemp a WHERE a.optdate = :optdate"),
    @NamedQuery(name = "AssetCheckTemp.findByCfmuser", query = "SELECT a FROM AssetCheckTemp a WHERE a.cfmuser = :cfmuser"),
    @NamedQuery(name = "AssetCheckTemp.findByCfmdate", query = "SELECT a FROM AssetCheckTemp a WHERE a.cfmdate = :cfmdate")})
public class AssetCheckTemp extends SuperEntity {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "company")
    private String company;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "formid")
    private String formid;
    @Size(max = 45)
    @Column(name = "itemno")
    private String itemno;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "assetno")
    private String assetno;
    @Size(max = 45)
    @Column(name = "assetDesc")
    private String assetDesc;
    @Size(max = 45)
    @Column(name = "assetSpec")
    private String assetSpec;
    @Basic(optional = false)
    @NotNull
    @Column(name = "qty")
    private int qty;
    @Size(max = 20)
    @Column(name = "userno")
    private String userno;
    @Size(max = 45)
    @Column(name = "username")
    private String username;
    @Size(max = 100)
    @Column(name = "deptno")
    private String deptno;
    @Size(max = 13)
    @Column(name = "deptname")
    private String deptname;
    @Size(max = 45)
    @Column(name = "address")
    private String address;
    @Basic(optional = false)
    @NotNull
    @Column(name = "itimes")
    private int itimes;
    @Basic(optional = false)
    @NotNull
    @Column(name = "iqty")
    private int iqty;
    @Size(max = 45)
    @Column(name = "iaddress")
    private String iaddress;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;
 

    public AssetCheckTemp() {
    }

    public AssetCheckTemp(Integer id) {
        this.id = id;
    }

    public AssetCheckTemp(Integer id, String company, String formid, String assetno, int qty, int itimes, int iqty, String status) {
        this.id = id;
        this.company = company;
        this.formid = formid;
        this.assetno = assetno;
        this.qty = qty;
        this.itimes = itimes;
        this.iqty = iqty;
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }

    public String getItemno() {
        return itemno;
    }

    public void setItemno(String itemno) {
        this.itemno = itemno;
    }

    public String getAssetno() {
        return assetno;
    }

    public void setAssetno(String assetno) {
        this.assetno = assetno;
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

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getItimes() {
        return itimes;
    }

    public void setItimes(int itimes) {
        this.itimes = itimes;
    }

    public int getIqty() {
        return iqty;
    }

    public void setIqty(int iqty) {
        this.iqty = iqty;
    }

    public String getIaddress() {
        return iaddress;
    }

    public void setIaddress(String iaddress) {
        this.iaddress = iaddress;
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
        if (!(object instanceof AssetCheckTemp)) {
            return false;
        }
        AssetCheckTemp other = (AssetCheckTemp) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.AssetCheckTemp[ id=" + id + " ]";
    }

}
