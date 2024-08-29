/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.CompanyGrantBean;
import cn.hanbell.eam.entity.CompanyGrant;
import cn.hanbell.eap.ejb.CompanyBean;
import cn.hanbell.eap.ejb.SystemUserBean;
import cn.hanbell.eap.entity.Company;
import cn.hanbell.eap.entity.SystemGrantPrg;
import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.BaseLib;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUtils;
import org.json.JSONObject;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "userManagedBean")
@SessionScoped
public class UserManagedBean implements Serializable {

    @EJB
    private CompanyBean companyBean;

    @EJB
    private SystemUserBean systemUserBean;

    @EJB
    private CompanyGrantBean companyGrantBean;

    private Company currentCompany;
    private SystemUser currentUser;

    private String company;
    private String userid;
    private String mobile;
    private String email;
    private String pwd;
    private String newpwd;
    private String secpwd;
    private boolean status;
    private String token;
    private List<SystemGrantPrg> systemGrantPrgList;
    private List<Company> companyList;

    public UserManagedBean() {
        status = false;
    }

    @PostConstruct
    public void construct() {
        companyList = companyBean.findBySystemName("EAM");
//        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//        token = request.getQueryString();
//        if (token != null && !"".equals(token)) {
//            try {
//                company = "C";
//                String checkLogin = sendPostLogin("http://edw.hanbell.com.cn/platfm.webapi/v1/user/get-info", token);
//                if (checkLogin.equals("home")) {
//                    FacesContext.getCurrentInstance().getExternalContext().redirect("home.xhtml");
//                } else {
//                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "您无权免密登入,请联系管理员"));
//                }
//
//            } catch (IOException ex) {
//                Logger.getLogger(UserManagedBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        }

    }

    public boolean checkUser() {
        return true;
    }

    public SystemUser findById(int id) {
        return systemUserBean.findById(id);
    }

    /**
     * 获取当前访问URL （含协议、域名、端口号[忽略80端口]、项目名）
     *
     * @param url
     * @param param
     * @return: String
     */
//新增一个接口
    public String sendPostLogin(String url, String param) throws IOException {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        token = request.getQueryString();
  
        if (token==null || token.equals("")) {
            return "";
        }
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        url =url+"?"+token;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("accept", "*/*");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        } //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        String userNo = "";

        JSONObject jsonObject = new JSONObject(result);
        int resultTemp = jsonObject.getInt("code");//获取成功状态
        if (resultTemp == 1) {//成功则获取员工id
            JSONObject data = jsonObject.getJSONObject("data");
            userid = data.getString("UserNo");
            SystemUser u = null;
            u = systemUserBean.findByUserId(userid);
            company = "C";
            if (u != null) {
                if ("Admin".equals(u.getUserid())) {
                    currentCompany = companyBean.findByCompany(company);
                    if (currentCompany == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "请维护公司信息"));
                    }
                } else {
                    //此处加入公司授权检查
                    CompanyGrant cg = companyGrantBean.findByCompanyAndUserid(company, userid);
                    if (cg == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "您无权访问此公司别,请联系管理员"));
                        status = false;
                        return "";
                    }
                    currentCompany = companyBean.findByCompany(company);
                    if (currentCompany == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "请维护公司信息"));
                        status = false;
                        return "";
                    }
                }
                currentUser = u;
                status = true;
                mobile = u.getUserid();
                updateLoginTime();

            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "您无权免密登入,请联系管理员"));
            status = false;
            return "login";
        }
        token = "";
         FacesContext.getCurrentInstance().getExternalContext().redirect("home.xhtml");
        return "home";
    }

    public String login() {
        if (getUserid().length() == 0 || getPwd().length() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "请输入用户名和密码"));
            return "";
        }
        secpwd = BaseLib.securityMD5(getPwd());
        try {
            SystemUser u = null;
            if ("Admin".equals(userid)) {
                u = systemUserBean.findByUserIdAndPwd(userid, secpwd);
                if (u == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "用户名或密码错误"));
                    status = false;
                    return "";
                }
            } else if (cn.hanbell.util.BaseLib.ADAuth("172.16.10.190:389", userid + "@hanbell.com.cn", pwd)) {
                u = systemUserBean.findByUserId(getUserid());
            }
            if (u != null) {
                if ("Admin".equals(u.getUserid())) {
                    currentCompany = companyBean.findByCompany(company);
                    if (currentCompany == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "请维护公司信息"));
                    }
                } else {
                    //此处加入公司授权检查
                    CompanyGrant cg = companyGrantBean.findByCompanyAndUserid(company, userid);
                    if (cg == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "您无权访问此公司别"));
                        status = false;
                        return "";
                    }
                    currentCompany = companyBean.findByCompany(company);
                    if (currentCompany == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "请维护公司信息"));
                        status = false;
                        return "";
                    }
                }
                currentUser = u;
                status = true;
                mobile = u.getUserid();
                updateLoginTime();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "用户名或密码不正确！"));
            status = false;
            return "login";
        }
        return "home";
    }

    public String logout() {
        if (status) {
            currentUser = null;
            status = false;
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return "login";
        } else {
            return "home";
        }
    }

    public void update() {
        if (currentUser != null) {
            if (mobile != null && !mobile.equals("") && !mobile.equals(currentUser.getUserid())) {
                currentUser.setUserid(mobile);
            }
            if (email != null && !email.equals("") && !email.equals(currentUser.getEmail())) {
                currentUser.setEmail(email);
            }
            try {
                systemUserBean.update(currentUser);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "更新成功"));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "更新失败！"));
            }
        }
    }

    public void updateLoginTime() {
        if (currentUser != null) {
            currentUser.setLastlogin(currentUser.getNewestlogin());
            currentUser.setNewestlogin(BaseLib.getDate());
            update();
        }
    }

    public void updatePwd() {
        secpwd = BaseLib.securityMD5(getPwd());
        SystemUser u = systemUserBean.findByUserIdAndPwd(getUserid(), getSecpwd());
        if (u != null) {
            secpwd = BaseLib.securityMD5(newpwd);
            currentUser.setPassword(secpwd);
            update();
            pwd = "";
            newpwd = "";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "更新密码成功"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "原密码错误"));
        }
    }

    /**
     * @return the currentCompany
     */
    public Company getCurrentCompany() {
        return currentCompany;
    }

    /**
     * @return the currentUser
     */
    public SystemUser getCurrentUser() {
        return currentUser;
    }

    public boolean getStatus() {
        return status;
    }

    /**
     * @return the company
     */
    public String getCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * @return the userid
     */
    public String getUserid() {
        return userid;
    }

    /**
     * @param userid the userid to set
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * @return the pwd
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * @param pwd the pwd to set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * @return the newpwd
     */
    public String getNewpwd() {
        return newpwd;
    }

    /**
     * @param newpwd the newpwd to set
     */
    public void setNewpwd(String newpwd) {
        this.newpwd = newpwd;
    }

    /**
     * @return the secpwd
     */
    public String getSecpwd() {
        return secpwd;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the systemGrantPrgList
     */
    public List<SystemGrantPrg> getSystemGrantPrgList() {
        return systemGrantPrgList;
    }

    /**
     * @param systemGrantPrgList the systemGrantPrgList to set
     */
    public void setSystemGrantPrgList(List<SystemGrantPrg> systemGrantPrgList) {
        this.systemGrantPrgList = systemGrantPrgList;
    }

    /**
     * @return the companyList
     */
    public List<Company> getCompanyList() {
        return companyList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
