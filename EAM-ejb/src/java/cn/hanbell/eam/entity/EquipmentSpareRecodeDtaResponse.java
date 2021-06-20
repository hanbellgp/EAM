/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.entity;

import com.lightshell.comm.SuperEntity;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author C2090
 */
@XmlRootElement
public class EquipmentSpareRecodeDtaResponse extends SuperEntity {

    @Column(name = "equipmentSpareRecodeDtaList")
    private List<EquipmentSpareRecodeDta> equipmentSpareRecodeDtaList;
    @Column(name = "spareInfo")
    private EquipmentSpare spareInfo;
    @Column(name = "qty")
    private BigDecimal qty;
    @Column(name = "totalPrice")
    private BigDecimal totalPrice;

    public EquipmentSpareRecodeDtaResponse(EquipmentSpare equipmentSpare, List<EquipmentSpareRecodeDta> eqpSpareRecodeDtas, BigDecimal qty, BigDecimal totalPrice) {
        this.spareInfo = equipmentSpare;
        this.equipmentSpareRecodeDtaList = eqpSpareRecodeDtas;
        this.qty = qty;
        this.totalPrice = totalPrice;
    }
    
    public List<EquipmentSpareRecodeDta> getEquipmentSpareRecodeDtaList()
    {
        return equipmentSpareRecodeDtaList;
    }
    
    public void setEquipmentSpareRecodeDtaList(List<EquipmentSpareRecodeDta> eqpSpareRecodeDtas)
    {
        this.equipmentSpareRecodeDtaList = eqpSpareRecodeDtas;
    }
    
        
    public EquipmentSpare getSpareInfo()
    {
        return spareInfo;
    }
    
    public void setSpareInfo(EquipmentSpare spareInfo)
    {
        this.spareInfo = spareInfo;
    }
    
        
    public BigDecimal getQty()
    {
        return qty;
    }
    
    public void setQty(BigDecimal qty)
    {
        this.qty = qty;
    }
    
    public BigDecimal getTotalPrice()
    {
        return totalPrice;
    }
    
    private void setTotalPrice(BigDecimal totalPrice)
    {
        this.totalPrice = totalPrice;
    }

}
