package com.cloume.commons.rest.request;

import java.util.Map;

/**
 * 用于简化@RequestBody参数
 * @author Gang
 * @deprecated 不能被@ResponseBody识别成map
 */
public interface RestRequestBody extends Map<String, Object> {

}
