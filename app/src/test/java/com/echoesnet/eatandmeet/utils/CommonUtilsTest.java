package com.echoesnet.eatandmeet.utils;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Copyright (C) 2018 科技发展有限公司
 * 完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2018/1/8 11:33
 * @description
 */
//@RunWith(MockitoJUnitRunner.class)
public class CommonUtilsTest
{
    @Test
    public void keep2Decimal() throws Exception
    {
        double original=1.5566;
        Assert.assertEquals(CommonUtils.keep2Decimal(original).toString(),"1.56");
    }
}