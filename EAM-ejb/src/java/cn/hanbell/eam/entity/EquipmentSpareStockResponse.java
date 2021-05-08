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
public class EquipmentSpareStockResponse extends SuperEntity {

    @Column(name = "equipmentSpareStockList")
    private List<EquipmentSpareStock> equipmentSpareStockList;
    @Column(name = "spareInfo")
    private EquipmentSpare spareInfo;
    @Column(name = "qty")
    private BigDecimal qty;

    public EquipmentSpareStockResponse(EquipmentSpare equipmentSpare, List<EquipmentSpareStock> eqpSpareStocks, BigDecimal qty) {
        this.spareInfo = equipmentSpare;
        this.equipmentSpareStockList = eqpSpareStocks;
        this.qty = qty;
    }
    
    public List<EquipmentSpareStock> getEquipmentSpareStockList()
    {
        return equipmentSpareStockList;
    }
    
    public void setEquipmentSpareStockList(List<EquipmentSpareStock> equipmentSpareStockList)
    {
        this.equipmentSpareStockList = equipmentSpareStockList;
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

}
