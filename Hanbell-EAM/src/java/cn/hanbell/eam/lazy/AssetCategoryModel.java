/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.lazy;

import cn.hanbell.eam.entity.AssetCategory;
import com.lightshell.comm.BaseLazyModel;
import com.lightshell.comm.SuperEJB;

/**
 *
 * @author C1368
 */
public class AssetCategoryModel extends BaseLazyModel<AssetCategory> {

    public AssetCategoryModel(SuperEJB superEJB) {
        this.superEJB = superEJB;
        this.sortFields.put("category", "ASC");
    }

}
