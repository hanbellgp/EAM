/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentSpareRecodeBean;
import cn.hanbell.eam.ejb.EquipmentSpareRecodeDtaBean;
import cn.hanbell.eam.entity.EquipmentSpareRecode;
import cn.hanbell.eam.entity.EquipmentSpareRecodeDta;
import cn.hanbell.eam.lazy.EquipmentSpareRecodeModel;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.ejb.DepartmentBean;
import cn.hanbell.eap.entity.Department;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.LinearAxis;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentSpareConsumeManagedBean")
@SessionScoped
public class EquipmentSpareConsumeManagedBean extends FormMultiBean<EquipmentSpareRecode, EquipmentSpareRecodeDta> {

    @EJB
    private EquipmentSpareRecodeBean equipmentSpareRecodeBean;

    @EJB
    private EquipmentSpareRecodeDtaBean equipmentSpareRecodeDtaBean;
    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    private DepartmentBean departmentBean;
    private String queryUserno;
    private String queryDeptno;
    private BarChartModel barModel;
    private BarChartModel lineModel;
    private List<Object[]> list;
    private List<Department> departmentList;
    private String[] dept;

    public EquipmentSpareConsumeManagedBean() {
        super(EquipmentSpareRecode.class, EquipmentSpareRecodeDta.class);
    }

    @Override
    public void init() {
        superEJB = equipmentSpareRecodeBean;
        model = new EquipmentSpareRecodeModel(equipmentSpareRecodeBean, userManagedBean);
        detailEJB = equipmentSpareRecodeDtaBean;
        this.model.getFilterFields().put("status", "N");
        this.model.getFilterFields().put("accepttype", "10");
        queryState = "N";
        this.model.getSortFields().put("formid", "ASC");
        queryDateBegin = equipmentRepairBean.getMonthDay(1);//获取当前月第一天
        queryDateEnd = equipmentRepairBean.getMonthDay(0);//获取当前月最后一天
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        queryUserno = "MONTH";
        queryDeptno = "枫泾厂";
        openOptions = new HashMap<>();
        openOptions.put("company", userManagedBean.getCompany());
        departmentList = departmentBean.findByFilters(openOptions);
        list = equipmentSpareRecodeDtaBean.getSpareConsumeQty(simpleDateFormat.format(queryDateBegin), simpleDateFormat.format(queryDateEnd), queryDeptno, queryUserno,"");
        createLineModels();
        createLineModels2();
        super.init();
    }

    @Override
    public void query() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String deptSql = "";
        if (dept.length > 0) {
            for (String sqlCompanyID : dept) {
                deptSql +=  " '" + sqlCompanyID + "', ";
            }
            deptSql = deptSql.substring(0,deptSql.length()-2);
        }
        //获取备件消耗数量
        list = equipmentSpareRecodeDtaBean.getSpareConsumeQty(simpleDateFormat.format(queryDateBegin), simpleDateFormat.format(queryDateEnd), queryDeptno, queryUserno,deptSql);
        createLineModels();
        createLineModels2();
    }

    private void createLineModels() {
        barModel = initCategoryModel();
        barModel.getAxis(AxisType.X);
        Axis yAxis = barModel.getAxis(AxisType.Y);
        barModel.setTitle("备件消耗数量统计表--按" + queryUserno + "统计-(个)");
        yAxis.setMin(0);
        Axis y2Axis = new LinearAxis("次数");
        y2Axis.setMin(0);
        barModel.getAxes().put(AxisType.Y2, y2Axis);
        barModel.setShowPointLabels(true);
    }

    private BarChartModel initCategoryModel() {
        barModel = new BarChartModel();
        ChartSeries maintenanceTime = new ChartSeries();
        for (Object[] obj : list) {
            maintenanceTime.set(obj[0].toString(), Double.parseDouble(obj[1].toString()));
        }
        barModel.addSeries(maintenanceTime);
        return barModel;
    }

    private void createLineModels2() {
        lineModel = initCategoryModel2();
        lineModel.getAxis(AxisType.X);
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        lineModel.setTitle("备件消耗金额统计表--按" + queryUserno + "统计-(元)");
        yAxis.setMin(0);
        lineModel.setShowPointLabels(true);
    }

    private BarChartModel initCategoryModel2() {
        lineModel = new BarChartModel();
        LineChartSeries totalTime = new LineChartSeries();
        for (Object[] obj : list) {
            totalTime.set(obj[0].toString(), Double.parseDouble(obj[2].toString()));
        }
        lineModel.addSeries(totalTime);
        return lineModel;
    }

    public String getQueryUserno() {
        return queryUserno;
    }

    public void setQueryUserno(String queryUserno) {
        this.queryUserno = queryUserno;
    }

    public String getQueryDeptno() {
        return queryDeptno;
    }

    public void setQueryDeptno(String queryDeptno) {
        this.queryDeptno = queryDeptno;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public void setBarModel(BarChartModel barModel) {
        this.barModel = barModel;
    }

    public BarChartModel getLineModel() {
        return lineModel;
    }

    public void setLineModel(BarChartModel lineModel) {
        this.lineModel = lineModel;
    }

    public List<Department> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<Department> departmentList) {
        this.departmentList = departmentList;
    }

    public String[] getDept() {
        return dept;
    }

    public void setDept(String[] dept) {
        this.dept = dept;
    }

}
