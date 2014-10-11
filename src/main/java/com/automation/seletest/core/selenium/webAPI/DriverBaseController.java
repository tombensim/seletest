/*
This file is part of the Seletest by Papadakis Giannis <gpapadakis84@gmail.com>.

Copyright (c) 2014, Papadakis Giannis <gpapadakis84@gmail.com>
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.automation.seletest.core.selenium.webAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.beans.factory.annotation.Autowired;

import com.automation.seletest.core.selenium.threads.SessionContext;
import com.automation.seletest.core.selenium.webAPI.interfaces.MainController;
import com.automation.seletest.core.services.actions.WaitFor;
import com.automation.seletest.core.services.factories.StrategyFactory;

/**
 * @author Giannis Papadakis (mailTo:gpapadakis84@gmail.com)
 * @param <T>
 *
 */
@SuppressWarnings("unchecked")
public abstract class DriverBaseController<T> implements MainController<DriverBaseController<T>>{

    @Autowired
    StrategyFactory<?> factoryStrategy;

    /**
     * Gets the WebDriver instance
     * @return WebDriver instance
     */
    public T webDriver(){
        return (T) SessionContext.getSession().getWebDriver();
    }

    /**
     * Gets the selenium instance
     * @return
     */
    public T selenium(){
        return (T) SessionContext.getSession().getSelenium();
    }

    /**
     * WaitFor Controller
     * @return WaitFor
     */
    public WaitFor waitController() {
        return factoryStrategy.getWaitStrategy(SessionContext.getSession().getWaitStrategy());
    }

    /* (non-Javadoc)
     * @see com.automation.seletest.core.selenium.webAPI.interfaces.MainController#downloadFile(java.lang.String, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("resource")
    @Override
    public String downloadFile(String url, String filenamePrefix, String fileExtension) throws MalformedURLException, IOException, InterruptedException {
        URLConnection request = null;
        request = new URL(url).openConnection();
        request.setRequestProperty("Cookie", "PHPSESSID="+getCookieNamed("PHPSESSID").getValue());
        InputStream in = request.getInputStream();
        File downloadedFile = File.createTempFile(filenamePrefix, fileExtension);
        FileOutputStream out = new FileOutputStream(downloadedFile);
        byte[] buffer = new byte[1024];
        int len = in.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
        in.close();
        out.close();
        return downloadedFile.getAbsolutePath();
    }


}
