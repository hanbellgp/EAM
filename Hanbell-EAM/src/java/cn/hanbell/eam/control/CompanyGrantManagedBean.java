/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.CompanyGrantBean;
import cn.hanbell.eam.entity.CompanyGrant;
import cn.hanbell.eam.lazy.CompanyGrantModel;
import cn.hanbell.eam.web.SuperSingleBean;
import cn.hanbell.eap.ejb.CompanyBean;
import cn.hanbell.eap.entity.Company;
import cn.hanbell.eap.entity.SystemUser;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "companyGrantManagedBean")
@SessionScoped
public class CompanyGrantManagedBean extends SuperSingleBean<CompanyGrant> {

    @EJB
    private CompanyBean companyBean;

    @EJB
    private CompanyGrantBean companyGrantBean;

    private TreeNode rootNode;
    private TreeNode selectedNode;

    private List<Company> companyList;

    public CompanyGrantManagedBean() {
        super(CompanyGrant.class);
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
            SystemUser e = (SystemUser) event.getObject();
            currentEntity.setUserid(e.getUserid());
            currentEntity.setUsername(e.getUsername());
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        if (event.getObject() != null && newEntity != null) {
            SystemUser e = (SystemUser) event.getObject();
            newEntity.setUserid(e.getUserid());
            newEntity.setUsername(e.getUsername());
        }
    }

    @Override
    public void init() {
        this.superEJB = companyGrantBean;
        this.model = new CompanyGrantModel(companyGrantBean);
        super.init();
        companyList = companyBean.findAll();
        initTree();
    }

    private void initTree() {
        rootNode = new DefaultTreeNode(new CompanyGrant("root", "Root"), null);
        rootNode.setExpanded(true);
        if (companyList != null && !companyList.isEmpty()) {
            for (Company e : companyList) {
                TreeNode n = new DefaultTreeNode(new CompanyGrant(e.getCompany(), e.getName()), rootNode);
                n.setExpanded(true);
                initTree(e, n);
            }
        }
    }

    private void initTree(Company company, TreeNode node) {
        List<CompanyGrant> users = companyGrantBean.findByCompany(company.getCompany());
        if (users != null && !users.isEmpty()) {
            for (CompanyGrant e : users) {
                TreeNode n = new DefaultTreeNode(e, node);
                n.setExpanded(true);
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
            currentEntity = (CompanyGrant) selectedNode.getData();
            setToolBar();
        }
    }

    /**
     * @return the companyList
     */
    public List<Company> getCompanyList() {
        return companyList;
    }

    /**
     * @param companyList the companyList to set
     */
    public void setCompanyList(List<Company> companyList) {
        this.companyList = companyList;
    }

}
