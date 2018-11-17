package com.bigkoo.pickerviewdemo.bean;

import com.contrarywind.interfaces.IPickerViewData;

/**
 * @author ling_cx
 * @version 1.0
 * @Description
 * @date 2018/11/16 0016.
 * @Copyright: 2018 www.kind.com.cn Inc. All rights reserved.
 */
public class DrugClassModel implements IPickerViewData {

    /**
     * Dictid : 44628d18-6846-4f9c-97ba-9a3eac8f0a14
     * Dictname : 抗微生物药
     * Dictparentid : null
     * Dictkey : 1
     */

    private String Dictid;
    private String Dictname;
    private String Dictparentid;
    private String Dictkey;

    public String getDictid() {
        return Dictid;
    }

    public void setDictid(String Dictid) {
        this.Dictid = Dictid;
    }

    public String getDictname() {
        return Dictname;
    }

    public void setDictname(String Dictname) {
        this.Dictname = Dictname;
    }

    public String getDictparentid() {
        return Dictparentid;
    }

    public void setDictparentid(String Dictparentid) {
        this.Dictparentid = Dictparentid;
    }

    public String getDictkey() {
        return Dictkey;
    }

    public void setDictkey(String Dictkey) {
        this.Dictkey = Dictkey;
    }

    @Override
    public String getPickerViewText() {
        return Dictname;
    }
}
