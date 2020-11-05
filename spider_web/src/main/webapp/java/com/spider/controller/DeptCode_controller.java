package com.spider.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifeng.auto.we_provider.common.db.DynamicDataSourceHolder;
import com.spider.bean.DepcodeNew;
import com.spider.bean.TXwWebsite;
import com.spider.service.Deptcode_service;
import com.spider.utils.DataTablePageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;

/**
 * <p>
 * DeptCode_controller
 * </p>
 *
 * @author msi-
 * @package: com.spider.controller
 * @description: lar数据库，deptcode_new表维护
 * @date: Created in 2020-09-23 17:26
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: msi-
 */

@Controller

public class DeptCode_controller {

    @Autowired
    private Deptcode_service deptcode_service;

    //添加
    @RequestMapping("deptcode/insert")
    @ResponseBody
    public boolean deptcodeInsert(@RequestParam("deptName1")String deptName1,@RequestParam("deptName2")String deptName2){
        DynamicDataSourceHolder.clearCustomerType();//重点： 实际操作证明，切换的时候最好清空一下
        DynamicDataSourceHolder.setCustomerType(DynamicDataSourceHolder.DATA_SOURCE_LAR);
        //获取表中最大值
        int number = deptcode_service.getMaxNumber();
        DepcodeNew depcodeNew = new DepcodeNew();
        depcodeNew.setDepNumber((number+1)+"");
        depcodeNew.setDepName(deptName1);
        depcodeNew.setAlisName(deptName2);

        return deptcode_service.insert(depcodeNew);
    }

    //修改
    @RequestMapping("deptcode/update")
    @ResponseBody
    public boolean deptcodeUpdate(@RequestParam("number")String number,@RequestParam("name1")String name1,@RequestParam("name2")String name2){

        DepcodeNew depcodeNew = new DepcodeNew();
        depcodeNew.setDepNumber(number);
        depcodeNew.setDepName(name1);
        depcodeNew.setAlisName(name2);


        DynamicDataSourceHolder.clearCustomerType();//重点： 实际操作证明，切换的时候最好清空一下
        DynamicDataSourceHolder.setCustomerType(DynamicDataSourceHolder.DATA_SOURCE_LAR);
        return deptcode_service.update(depcodeNew);
    }

    //删除
    @RequestMapping("deptcode/delete")
    @ResponseBody
    public boolean deptcodeDelete(@RequestParam("ids")String ids){
        DynamicDataSourceHolder.clearCustomerType();//重点： 实际操作证明，切换的时候最好清空一下
        DynamicDataSourceHolder.setCustomerType(DynamicDataSourceHolder.DATA_SOURCE_LAR);
        return deptcode_service.deleteByNumber(ids);
    }

    //数据列表
    @RequestMapping("deptcode/index")

    public String deptcodeIndex(){

        return "deptcodeList";
    }

    //加载数据
    @RequestMapping(value="deptcodeList",method = RequestMethod.POST,produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public Object deptcodeList(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        DynamicDataSourceHolder.clearCustomerType();//重点： 实际操作证明，切换的时候最好清空一下
        DynamicDataSourceHolder.setCustomerType(DynamicDataSourceHolder.DATA_SOURCE_LAR);
        /**
         * 这几个参数后台直接取，前端不需要传!
         * 开启服务端分页后，datatables 会自动传！
         */
        Integer draw = Integer.parseInt(request.getParameter("draw"));//该参数取出来，不做任何操作，再传回去即可
        Integer start = Integer.parseInt(request.getParameter("start"));//配合求当前页，第一次传进来是 0，然后 10，20，30 ……
        Integer pageSize = Integer.parseInt(request.getParameter("length"));//【页大小】

        int total = deptcode_service.getDepcodeCount();// 总数量
        //自己处理分页逻辑
        List<DepcodeNew> beans = deptcode_service.getDepcodeList(start,pageSize);

        /**
         * 最重要的格式处理！
         * 工具类，处理成 datatable 规定的格式返回，才能正常显示！
         */
        DataTablePageUtil<DepcodeNew> pages = new DataTablePageUtil<>();
        pages.setRecordsTotal(total);
        pages.setRecordsFiltered(total);
        pages.setDraw(draw);
        pages.setData(beans);

        /**
         * 集合对象转成json数据返回
         */
        ObjectMapper MAPPER = new ObjectMapper();

        String jsonString = MAPPER.writeValueAsString(pages);
        Object stringToValue = JSONObject.stringToValue(jsonString);
        return stringToValue;
    }
}