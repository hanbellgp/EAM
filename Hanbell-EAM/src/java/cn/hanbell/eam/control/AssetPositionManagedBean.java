/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetPositionBean;
import cn.hanbell.eam.entity.AssetPosition;
import cn.hanbell.eam.lazy.AssetPositionModel;
import cn.hanbell.eam.web.SuperSingleBean;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetPositionManagedBean")
@SessionScoped
public class AssetPositionManagedBean extends SuperSingleBean<AssetPosition> {

    @EJB
    private AssetPositionBean assetPositionBean;

    private TreeNode rootNode;
    private TreeNode selectedNode;
    private List<AssetPosition> rootPositions;

    public AssetPositionManagedBean() {
        super(AssetPosition.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCompany(userManagedBean.getCompany());
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
            AssetPosition e = (AssetPosition) event.getObject();
            currentEntity.setParentPosition(e);
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            AssetPosition e = (AssetPosition) event.getObject();
            newEntity.setParentPosition(e);
        }
    }

    @Override
    public void init() {
        this.superEJB = assetPositionBean;
        this.model = new AssetPositionModel(assetPositionBean, userManagedBean);
        super.init();
        initTree();
    }

    private void initTree() {
        rootNode = new DefaultTreeNode(new AssetPosition(userManagedBean.getCompany(), "Root"), null);
        rootNode.setExpanded(true);
        rootPositions = assetPositionBean.findRootByCompany(userManagedBean.getCompany());
        if (rootPositions != null && !rootPositions.isEmpty()) {
            for (AssetPosition p : rootPositions) {
                TreeNode n = new DefaultTreeNode(p, rootNode);
                n.setExpanded(true);
                initTree(p, n);
            }
        }
    }

    private void initTree(AssetPosition position, TreeNode node) {
        List<AssetPosition> positions = assetPositionBean.findByPId(position.getId());
        if (positions != null && !positions.isEmpty()) {
            for (AssetPosition p : positions) {
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
            currentEntity = (AssetPosition) selectedNode.getData();
        }
    }

}
