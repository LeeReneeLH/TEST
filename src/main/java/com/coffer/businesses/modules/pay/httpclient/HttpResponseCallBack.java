package com.coffer.businesses.modules.pay.httpclient;

import java.io.IOException;
import java.io.InputStream;

/**
 * <b>功能说明:
 * </b>
 */
public interface HttpResponseCallBack {

    public void processResponse(InputStream responseBody) throws IOException;
}
