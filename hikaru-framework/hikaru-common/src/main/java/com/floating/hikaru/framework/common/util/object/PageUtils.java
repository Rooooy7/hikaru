package com.floating.hikaru.framework.common.util.object;


import com.floating.hikaru.framework.common.pojo.PageParam;

/**
 * {@link PageParam} 工具类
 */
public class PageUtils {

    public static int getStart(PageParam pageParam) {
        return (pageParam.getPageNo() - 1) * pageParam.getPageSize();
    }

}
