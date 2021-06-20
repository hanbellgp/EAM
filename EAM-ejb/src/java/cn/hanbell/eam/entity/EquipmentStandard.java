/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.SuperEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "equipmentstandard")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentStandard.findAll", query = "SELECT e FROM EquipmentStandard e"),
    @NamedQuery(name = "EquipmentStandard.findById", query = "SELECT e FROM EquipmentStandard e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentStandard.findByCompany", query = "SELECT e FROM EquipmentStandard e WHERE e.company = :company"),
    @NamedQuery(name = "EquipmentStandard.findByAssetno", query = "SELECT e FROM EquipmentStandard e WHERE e.assetno = :assetno AND e.status=:status"),
    @NamedQuery(name = "EquipmentStandard.findByAssetdesc", query = "SELECT e FROM EquipmentStandard e WHERE e.assetdesc = :assetdesc"),
    @NamedQuery(name = "EquipmentStandard.findByItemno", query = "SELECT e FROM EquipmentStandard e WHERE e.itemno = :itemno"),
    @NamedQuery(name = "EquipmentStandard.findByDeptno", query = "SELECT e FROM EquipmentStandard e WHERE e.deptno = :deptno"),
    @NamedQuery(name = "EquipmentStandard.findByDeptname", query = "SELECT e FROM EquipmentStandard e WHERE e.deptname = :deptname"),
    @NamedQuery(name = "EquipmentStandard.findByStandardlevel", query = "SELECT e FROM EquipmentStandard e WHERE e.standardlevel = :standardlevel"),
    @NamedQuery(name = "EquipmentStandard.findBySort", query = "SELECT e FROM EquipmentStandard e WHERE e.sort = :sort"),
    @NamedQuery(name = "EquipmentStandard.findByStandardtype", query = "SELECT e FROM EquipmentStandard e WHERE e.standardtype = :standardtype"),
    @NamedQuery(name = "EquipmentStandard.findByRespondept", query = "SELECT e FROM EquipmentStandard e WHERE e.respondept = :respondept"),
    @NamedQuery(name = "EquipmentStandard.findByCheckarea", query = "SELECT e FROM EquipmentStandard e WHERE e.checkarea = :checkarea"),
    @NamedQuery(name = "EquipmentStandard.findByCheckcontent", query = "SELECT e FROM EquipmentStandard e WHERE e.checkcontent = :checkcontent"),
    @NamedQuery(name = "EquipmentStandard.findByJudgestandard", query = "SELECT e FROM EquipmentStandard e WHERE e.judgestandard = :judgestandard"),
    @NamedQuery(name = "EquipmentStandard.findByMethod", query = "SELECT e FROM EquipmentStandard e WHERE e.method = :method"),
    @NamedQuery(name = "EquipmentStandard.findByMethodname", query = "SELECT e FROM EquipmentStandard e WHERE e.methodname = :methodname"),
    @NamedQuery(name = "EquipmentStandard.findByFrequency", query = "SELECT e FROM EquipmentStandard e WHERE e.frequency = :frequency"),
    @NamedQuery(name = "EquipmentStandard.findByFrequencyunit", query = "SELECT e FROM EquipmentStandard e WHERE e.frequencyunit = :frequencyunit"),
    @NamedQuery(name = "EquipmentStandard.findByDowntime", query = "SELECT e FROM EquipmentStandard e WHERE e.downtime = :downtime"),
    @NamedQuery(name = "EquipmentStandard.findByManpower", query = "SELECT e FROM EquipmentStandard e WHERE e.manpower = :manpower"),
    @NamedQuery(name = "EquipmentStandard.findByManhour", query = "SELECT e FROM EquipmentStandard e WHERE e.manhour = :manhour"),
    @NamedQuery(name = "EquipmentStandard.findByManhourunit", query = "SELECT e FROM EquipmentStandard e WHERE e.manhourunit = :manhourunit"),
    @NamedQuery(name = "EquipmentStandard.findByResulttype", query = "SELECT e FROM EquipmentStandard e WHERE e.resulttype = :resulttype"),
    @NamedQuery(name = "EquipmentStandard.findByAreaimage", query = "SELECT e FROM EquipmentStandard e WHERE e.areaimage = :areaimage"),
    @NamedQuery(name = "EquipmentStandard.findByLasttime", query = "SELECT e FROM EquipmentStandard e WHERE e.lasttime = :lasttime"),
    @NamedQuery(name = "EquipmentStandard.findByNexttime", query = "SELECT e FROM EquipmentStandard e WHERE e.nexttime = :nexttime"),
    @NamedQuery(name = "EquipmentStandard.findByRemark", query = "SELECT e FROM EquipmentStandard e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentStandard.findByStatus", query = "SELECT e FROM EquipmentStandard e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentStandard.findByCreator", query = "SELECT e FROM EquipmentStandard e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentStandard.findByCredate", query = "SELECT e FROM EquipmentStandard e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentStandard.findByOptuser", query = "SELECT e FROM EquipmentStandard e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentStandard.findByOptdate", query = "SELECT e FROM EquipmentStandard e WHERE e.optdate = :optdate"),
    @NamedQuery(name = "EquipmentStandard.findByCfmuser", query = "SELECT e FROM EquipmentStandard e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentStandard.findByCfmdate", query = "SELECT e FROM EquipmentStandard e WHERE e.cfmdate = :cfmdate")})
public class EquipmentStandard extends SuperEntity {

    @Size(max = 2)
    @Column(name = "company")
    private String company;
    @Size(max = 45)
    @Column(name = "assetno")
    private String assetno;
    @Size(max = 45)
    @Column(name = "assetdesc")
    private String assetdesc;
    @Size(max = 45)
    @Column(name = "itemno")
    private String itemno;
    @Size(max = 45)
    @Column(name = "deptno")
    private String deptno;
    @Size(max = 45)
    @Column(name = "deptname")
    private String deptname;
    @Size(max = 10)
    @Column(name = "standardlevel")
    private String standardlevel;
    @Column(name = "sort")
    private Integer sort;
    @Size(max = 10)
    @Column(name = "standardtype")
    private String standardtype;
    @Size(max = 10)
    @Column(name = "respondept")
    private String respondept;
    @Size(max = 100)
    @Column(name = "checkarea")
    private String checkarea;
    @Size(max = 100)
    @Column(name = "checkcontent")
    private String checkcontent;
    @Size(max = 100)
    @Column(name = "judgestandard")
    private String judgestandard;
    @Size(max = 100)
    @Column(name = "method")
    private String method;
    @Size(max = 100)
    @Column(name = "methodname")
    private String methodname;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "frequency")
    private Integer frequency;
    @Size(max = 2)
    @Column(name = "frequencyunit")
    private String frequencyunit;
    @Column(name = "downtime")
    private Float downtime;
    @Column(name = "manpower")
    private Float manpower;
    @Column(name = "manhour")
    private Float manhour;
    @Size(max = 2)
    @Column(name = "manhourunit")
    private String manhourunit;
    @Size(max = 10)
    @Column(name = "resulttype")
    private String resulttype;
    @Size(max = 100)
    @Column(name = "areaimage")
    private String areaimage;
    @Column(name = "lasttime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lasttime;
    @Column(name = "nexttime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nexttime;
    @Size(max = 200)
    @Column(name = "remark")
    private String remark;

    public EquipmentStandard() {
    }

    public EquipmentStandard(Integer id) {
        this.id = id;
    }

    public EquipmentStandard(Integer id, String status) {
        this.id = id;
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAssetno() {
        return assetno;
    }

    public void setAssetno(String assetno) {
        this.assetno = assetno;
    }

    public String getAssetdesc() {
        return assetdesc;
    }

    public void setAssetdesc(String assetdesc) {
        this.assetdesc = assetdesc;
    }

    public String getItemno() {
        return itemno;
    }

    public void setItemno(String itemno) {
        this.itemno = itemno;
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

    public String getStandardlevel() {
        return standardlevel;
    }

    public void setStandardlevel(String standardlevel) {
        this.standardlevel = standardlevel;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getStandardtype() {
        return standardtype;
    }

    public void setStandardtype(String standardtype) {
        this.standardtype = standardtype;
    }

    public String getRespondept() {
        return respondept;
    }

    public void setRespondept(String respondept) {
        this.respondept = respondept;
    }

    public String getCheckarea() {
        return checkarea;
    }

    public void setCheckarea(String checkarea) {
        this.checkarea = checkarea;
    }

    public String getCheckcontent() {
        return checkcontent;
    }

    public void setCheckcontent(String checkcontent) {
        this.checkcontent = checkcontent;
    }

    public String getJudgestandard() {
        return judgestandard;
    }

    public void setJudgestandard(String judgestandard) {
        this.judgestandard = judgestandard;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethodname() {
        return methodname;
    }

    public void setMethodname(String methodname) {
        this.methodname = methodname;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public String getFrequencyunit() {
        return frequencyunit;
    }

    public void setFrequencyunit(String frequencyunit) {
        this.frequencyunit = frequencyunit;
    }

    public Float getDowntime() {
        return downtime;
    }

    public void setDowntime(Float downtime) {
        this.downtime = downtime;
    }

    public Float getManpower() {
        return manpower;
    }

    public void setManpower(Float manpower) {
        this.manpower = manpower;
    }

    public Float getManhour() {
        return manhour;
    }

    public void setManhour(Float manhour) {
        this.manhour = manhour;
    }

    public String getManhourunit() {
        return manhourunit;
    }

    public void setManhourunit(String manhourunit) {
        this.manhourunit = manhourunit;
    }

    public String getResulttype() {
        return resulttype;
    }

    public void setResulttype(String resulttype) {
        this.resulttype = resulttype;
    }

    public String getAreaimage() {
        return areaimage;
    }

    public void setAreaimage(String areaimage) {
        this.areaimage = areaimage;
    }

    public Date getLasttime() {
        return lasttime;
    }

    public void setLasttime(Date lasttime) {
        this.lasttime = lasttime;
    }

    public Date getNexttime() {
        return nexttime;
    }

    public void setNexttime(Date nexttime) {
        this.nexttime = nexttime;
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
        if (!(object instanceof EquipmentStandard)) {
            return false;
        }
        EquipmentStandard other = (EquipmentStandard) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentStandard[ id=" + id + " ]";
    }

}
