/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCategoryBean;
import cn.hanbell.eam.entity.AssetCategory;
import cn.hanbell.eam.lazy.AssetCategoryModel;
import cn.hanbell.eam.web.SuperSingleBean;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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

    public AssetCategoryManagedBean() {
        super(AssetCategory.class);
    }

    @Override
    public void create() {
        super.create();
        //newEntity.setCategory(userManagedBean.getCategory());
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
    public void init() {
        superEJB = assetCategoryBean;
        model = new AssetCategoryModel(assetCategoryBean);
        super.init();
        initTree();
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
