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
@Table(name = "equipmentworktime")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EquipmentWorkTime.findAll", query = "SELECT e FROM EquipmentWorkTime e"),
    @NamedQuery(name = "EquipmentWorkTime.findById", query = "SELECT e FROM EquipmentWorkTime e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentWorkTime.findByCompany", query = "SELECT e FROM EquipmentWorkTime e WHERE e.company = :company"),
    @NamedQuery(name = "EquipmentWorkTime.findByFormdate", query = "SELECT e FROM EquipmentWorkTime e WHERE e.formdate = :formdate"),
    @NamedQuery(name = "EquipmentWorkTime.findByDept", query = "SELECT e FROM EquipmentWorkTime e WHERE e.dept = :dept"),
    @NamedQuery(name = "EquipmentWorkTime.findByDeptname", query = "SELECT e FROM EquipmentWorkTime e WHERE e.deptname = :deptname"),
    @NamedQuery(name = "EquipmentWorkTime.findByWorktime", query = "SELECT e FROM EquipmentWorkTime e WHERE e.worktime = :worktime"),
    @NamedQuery(name = "EquipmentWorkTime.findByOvertime", query = "SELECT e FROM EquipmentWorkTime e WHERE e.overtime = :overtime"),
    @NamedQuery(name = "EquipmentWorkTime.findByStatus", query = "SELECT e FROM EquipmentWorkTime e WHERE e.status = :status"),
    @NamedQuery(name = "EquipmentWorkTime.findByRemark", query = "SELECT e FROM EquipmentWorkTime e WHERE e.remark = :remark"),
    @NamedQuery(name = "EquipmentWorkTime.findByCreator", query = "SELECT e FROM EquipmentWorkTime e WHERE e.creator = :creator"),
    @NamedQuery(name = "EquipmentWorkTime.findByCredate", query = "SELECT e FROM EquipmentWorkTime e WHERE e.credate = :credate"),
    @NamedQuery(name = "EquipmentWorkTime.findByOptuser", query = "SELECT e FROM EquipmentWorkTime e WHERE e.optuser = :optuser"),
    @NamedQuery(name = "EquipmentWorkTime.findByCfmuser", query = "SELECT e FROM EquipmentWorkTime e WHERE e.cfmuser = :cfmuser"),
    @NamedQuery(name = "EquipmentWorkTime.findByCfmdate", query = "SELECT e FROM EquipmentWorkTime e WHERE e.cfmdate = :cfmdate")})
public class EquipmentWorkTime extends SuperEntity {

    @Size(max = 2)
    @Column(name = "company")
    private String company;
    @Column(name = "formdate")
    @Temporal(TemporalType.DATE)
    private Date formdate;
    @Size(max = 20)
    @Column(name = "dept")
    private String dept;
    @Size(max = 20)
    @Column(name = "deptname")
    private String deptname;
    @Size(max = 10)
    @Column(name = "workingsystem")
    private String workingsystem;
    @Column(name = "worktime")
    private Integer worktime;
    @Column(name = "overtime")
    private Integer overtime;

    @Size(max = 30)
    @Column(name = "remark")
    private String remark;

    public EquipmentWorkTime() {
    }

    public EquipmentWorkTime(Integer id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Date getFormdate() {
        return formdate;
    }

    public void setFormdate(Date formdate) {
        this.formdate = formdate;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    public String getWorkingsystem() {
        return workingsystem;
    }

    public void setWorkingsystem(String workingsystem) {
        this.workingsystem = workingsystem;
    }

    public Integer getWorktime() {
        return worktime;
    }

    public void setWorktime(Integer worktime) {
        this.worktime = worktime;
    }

    public Integer getOvertime() {
        return overtime;
    }

    public void setOvertime(Integer overtime) {
        this.overtime = overtime;
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
        if (!(object instanceof EquipmentWorkTime)) {
            return false;
        }
        EquipmentWorkTime other = (EquipmentWorkTime) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "cn.hanbell.eam.entity.EquipmentWorkTime[ id=" + id + " ]";
    }

}
