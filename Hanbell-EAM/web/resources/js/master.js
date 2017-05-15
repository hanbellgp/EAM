/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {

    var btnConfirmDetail;
    btnConfirmDetail = document.getElementById("formOne:btnSaveDetail");
    if (btnConfirmDetail !== undefined && btnConfirmDetail !== null) {
        btnConfirmDetail.disabled = true;
        $(btnConfirmDetail).addClass('ui-state-disabled');
    }
    btnConfirmDetail = document.getElementById("formOne:tabView:btnSaveDetail");
    if (btnConfirmDetail !== undefined && btnConfirmDetail !== null) {
        btnConfirmDetail.disabled = true;
        $(btnConfirmDetail).addClass('ui-state-disabled');
    }
    var btnDeleteDetail;
    btnDeleteDetail = document.getElementById("formOne:btnDeleteDetail");
    if (btnDeleteDetail !== undefined && btnDeleteDetail !== null) {
        btnDeleteDetail.disabled = true;
        $(btnDeleteDetail).addClass('ui-state-disabled');
    }
    btnDeleteDetail = document.getElementById("formOne:tabView:btnDeleteDetail");
    if (btnDeleteDetail !== undefined && btnDeleteDetail !== null) {
        btnDeleteDetail.disabled = true;
        $(btnDeleteDetail).addClass('ui-state-disabled');
    }

});
