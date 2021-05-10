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
import java.text.SimpleDateFormat;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
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
    private String queryUserno;
    private String queryDeptno;
    private BarChartModel barModel;
    private List<Object[]> list;

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
        list = equipmentSpareRecodeDtaBean.getSpareConsumeQty(simpleDateFormat.format(queryDateBegin), simpleDateFormat.format(queryDateEnd), queryDeptno, queryUserno);
        createLineModels();
        super.init();
    }

    @Override
    public void query() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        //获取备件消耗数量
        list = equipmentSpareRecodeDtaBean.getSpareConsumeQty(simpleDateFormat.format(queryDateBegin), simpleDateFormat.format(queryDateEnd), queryDeptno, queryUserno);
        createLineModels();

    }

    private void createLineModels() {
        barModel = initCategoryModel();
        barModel.getAxis(AxisType.X);
        Axis yAxis = barModel.getAxis(AxisType.Y);
        barModel.setTitle("备件消耗统计表--按" + queryUserno + "统计");
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

}
