/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCategoryBean;
import cn.hanbell.eam.entity.AssetCategory;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.AssetCategoryModel;
import cn.hanbell.eam.web.SuperSingleBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author C1368
 */
@ManagedBean(name = "assetCategoryManagedBean")
@SessionScoped
public class AssetCategoryManagedBean extends SuperSingleBean<AssetCategory> {

    @EJB
    private AssetCategoryBean assetCategoryBean;

    private TreeNode rootNode;
    private TreeNode selectedNode;
    private List<AssetCategory> rootCategory;

    private String category;
    private List<String> paramHascost = null;

    public AssetCategoryManagedBean() {
        super(AssetCategory.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setNoauto(false);
        newEntity.setNochange(false);
    }

    @Override
    protected boolean doAfterDelete() throws Exception {
        initTree();
        return super.doAfterDelete();
    }

    @Override
    protected boolean doAfterPersist() throws Exception {
        initTree();
        return super.doAfterPersist();
    }

    @Override
    protected boolean doAfterUpdate() throws Exception {
        initTree();
        return super.doAfterUpdate();
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            AssetCategory e = (AssetCategory) event.getObject();
            currentEntity.setParentCategory(e);
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            AssetCategory e = (AssetCategory) event.getObject();
            newEntity.setParentCategory(e);
        }
    }

    public void handleDialogReturnWarehouseWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Warehouse e = (Warehouse) event.getObject();
            currentEntity.setWarehouse(e);
        }
    }

    public void handleDialogReturnWarehouseWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            Warehouse e = (Warehouse) event.getObject();
            newEntity.setWarehouse(e);
        }
    }

    public void handleDialogReturnWarehouse2WhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Warehouse e = (Warehouse) event.getObject();
            currentEntity.setWarehouse2(e);
        }
    }

    public void handleDialogReturnWarehouse2WhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            Warehouse e = (Warehouse) event.getObject();
            newEntity.setWarehouse2(e);
        }
    }

    @Override
    public void init() {
        openParams = new HashMap<>();
        superEJB = assetCategoryBean;
        model = new AssetCategoryModel(assetCategoryBean);
        super.init();
        //initTree();
    }

    private void initTree() {
        rootNode = new DefaultTreeNode(new AssetCategory("Root"), null);
        rootNode.setExpanded(true);
        rootCategory = assetCategoryBean.findRoot();
        if (rootCategory != null && !rootCategory.isEmpty()) {
            for (AssetCategory p : rootCategory) {
                TreeNode n = new DefaultTreeNode(p, rootNode);
                n.setExpanded(true);
                initTree(p, n);
            }
        }
    }

    private void initTree(AssetCategory category, TreeNode node) {
        List<AssetCategory> categorys = assetCategoryBean.findByPId(category.getId());
        if (categorys != null && !categorys.isEmpty()) {
            for (AssetCategory p : categorys) {
                TreeNode n = new DefaultTreeNode(p, node);
                n.setExpanded(true);
                initTree(p, n);
            }
        }
    }

    @Override
    public void openDialog(String view) {
        switch (view) {
            case "warehouseSelect":
                openParams.clear();
                if (paramHascost == null) {
                    paramHascost = new ArrayList<>();
                } else {
                    paramHascost.clear();
                }
                paramHascost.add("1");
                openParams.put("hascost", paramHascost);
                super.openDialog("warehouseSelect", openParams);
                break;
            case "warehouse2Select":
                openParams.clear();
                if (paramHascost == null) {
                    paramHascost = new ArrayList<>();
                } else {
                    paramHascost.clear();
                }
                paramHascost.add("0");
                openParams.put("hascost", paramHascost);
                super.openDialog("warehouseSelect", openParams);
                break;
            default:
                super.openDialog(view);
        }
    }

    @Override
    public void query() {
        if (this.model != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("category", queryFormId);
            }
            if (queryName != null && !"".equals(this.queryName)) {
                this.model.getFilterFields().put("name", queryName);
            }
            if (queryState != null && !"ALL".equals(queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }
        }
    }

    /**
     * @return the rootNode
     */
    public TreeNode getRootNode() {
        return rootNode;
    }

    /**
     * @param rootNode the rootNode to set
     */
    public void setRootNode(TreeNode rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * @return the selectedNode
     */
    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    /**
     * @param selectedNode the selectedNode to set
     */
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
        if (selectedNode != null) {
            currentEntity = (AssetCategory) selectedNode.getData();
            setToolBar();
        }
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

}
