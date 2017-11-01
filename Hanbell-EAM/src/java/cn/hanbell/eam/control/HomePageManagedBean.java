/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.ejb.AssetCategoryBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetCategory;
import cn.hanbell.eam.web.SuperQueryBean;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "homePageManagedBean")
@SessionScoped
public class HomePageManagedBean extends SuperQueryBean<AssetCard> {

    @EJB
    private AssetCategoryBean assetCategoryBean;
    @EJB
    private AssetCardBean assetCardBean;

    private BarChartModel chartModel;

    public HomePageManagedBean() {
        super(AssetCard.class);
    }

    @PostConstruct
    public void construct() {
        initChartModel();
    }

    private void initChartModel() {

        List<AssetCategory> categoryList = assetCategoryBean.findAsset();
        BarChartModel cm = new BarChartModel();
        ChartSeries cs;
        for (AssetCategory c : categoryList) {
            cs = new ChartSeries();
            cs.setLabel(c.getName());
            cs.set("未领用", assetCardBean.getQtyByCategory(userManagedBean.getCompany(), c.getCategory(), 1, 0, 0));
            cs.set("已领用", assetCardBean.getQtyByCategory(userManagedBean.getCompany(), c.getCategory(), 0, 0, 0));
            cs.set("闲置仓", assetCardBean.getQtyByCategory(userManagedBean.getCompany(), c.getCategory(), 0, 1, 0));
            cs.set("待处置", assetCardBean.getQtyByCategory(userManagedBean.getCompany(), c.getCategory(), 0, 0, 1));
            cm.addSeries(cs);
        }
        setChartModel(cm);

        getChartModel().setTitle("资产分布图");
        getChartModel().setLegendPosition("ne");

        Axis xAxis = getChartModel().getAxis(AxisType.X);
        xAxis.setLabel("类别");

        Axis yAxis = getChartModel().getAxis(AxisType.Y);
        yAxis.setLabel("数量");

    }

    /**
     * @return the chartModel
     */
    public BarChartModel getChartModel() {
        return chartModel;
    }

    /**
     * @param chartModel the chartModel to set
     */
    public void setChartModel(BarChartModel chartModel) {
        this.chartModel = chartModel;
    }

}
