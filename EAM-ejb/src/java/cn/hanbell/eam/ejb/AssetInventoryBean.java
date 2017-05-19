/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetInventory;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class AssetInventoryBean extends SuperEJBForEAM<AssetInventory> {

    public AssetInventoryBean() {
        super(AssetInventory.class);
    }

    public AssetInventory findAssetInventory(AssetInventory entity) {
        return findAssetInventory(entity.getCompany(), entity.getAssetItem().getItemno(), entity.getBrand(), entity.getBatch(), entity.getSn(), entity.getWarehouse().getWarehouseno());
    }

    public AssetInventory findAssetInventory(String company, String itemno, String brand, String batch, String sn, String warehousno) {
        if (brand == null) {
            brand = "";
        }
        if (batch == null) {
            batch = "";
        }
        if (sn == null) {
            sn = "";
        }
        Query query = this.getEntityManager().createNamedQuery("AssetInventory.findAssetInventory");
        query.setParameter("company", company);
        query.setParameter("itemno", itemno);
        query.setParameter("brand", brand);
        query.setParameter("batch", batch);
        query.setParameter("sn", sn);
        query.setParameter("warehouseno", warehousno);
        try {
            Object o = query.getSingleResult();
            return (AssetInventory) o;
        } catch (Exception e) {
            return null;
        }
    }

    public List<AssetInventory> findAssetInventories(AssetInventory entity) {
        return findAssetInventories(entity.getCompany(), entity.getAssetItem().getItemno(), entity.getBrand(), entity.getBatch(), entity.getSn());
    }

    public List<AssetInventory> findAssetInventories(String company, String itemno, String brand, String batch, String sn) {
        if (brand == null) {
            brand = "";
        }
        if (batch == null) {
            batch = "";
        }
        if (sn == null) {
            sn = "";
        }
        Query query = this.getEntityManager().createNamedQuery("AssetInventory.findAssetInventories");
        query.setParameter("company", company);
        query.setParameter("itemno", itemno);
        query.setParameter("brand", brand);
        query.setParameter("batch", batch);
        query.setParameter("sn", sn);
        return query.getResultList();
    }

    //增加数量
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void add(AssetInventory entity) throws RuntimeException {
        if (entity.getAssetItem().getInvtype()) {
            try {
                setDefaultValue(entity);
                AssetInventory e = findAssetInventory(entity);
                if (e == null) {
                    persist(entity);
                } else {
                    e.setPreqty(e.getPreqty().add(entity.getPreqty()));
                    e.setQty(e.getQty().add(entity.getQty()));
                    update(e);
                }
            } catch (RuntimeException ex) {
                throw new RuntimeException(ex.toString());
            }
        }
    }

    //增加数量
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void add(List<AssetInventory> entities) {
        for (AssetInventory e : entities) {
            add(e);
        }
    }

    //减少数量
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void subtract(AssetInventory entity) throws RuntimeException {
        if (entity.getAssetItem().getInvtype()) {
            try {
                setDefaultValue(entity);
                AssetInventory e = findAssetInventory(entity);
                if (e == null) {
                    throw new RuntimeException("找不到库存信息");
                } else {
                    e.setPreqty(e.getPreqty().subtract(entity.getPreqty()));
                    e.setQty(e.getQty().subtract(entity.getQty()));
                    update(e);
                }
            } catch (RuntimeException ex) {
                throw new RuntimeException(ex.toString());
            }
        }
    }

    //减少数量
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void subtract(List<AssetInventory> entities) {
        for (AssetInventory e : entities) {
            subtract(e);
        }
    }

    public void setDefaultValue(AssetInventory entity) {
        if (entity.getBrand() == null) {
            entity.setBrand("");
        }
        if (entity.getBatch() == null) {
            entity.setBatch("");
        }
        if (entity.getSn() == null) {
            entity.setSn("");
        }
    }

}
