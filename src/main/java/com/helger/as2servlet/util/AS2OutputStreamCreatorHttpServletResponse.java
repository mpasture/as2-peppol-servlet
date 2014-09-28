/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.as2servlet.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.mail.Header;
import javax.mail.internet.InternetHeaders;
import javax.servlet.http.HttpServletResponse;

import com.helger.as2lib.util.http.IAS2HttpResponseHandler;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.streams.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.string.StringHelper;

/**
 * An implementation of {@link IAS2HttpResponseHandler} that works upon a
 * {@link HttpServletResponse}.
 *
 * @author Philip Helger
 */
public final class AS2OutputStreamCreatorHttpServletResponse implements IAS2HttpResponseHandler
{
  private final HttpServletResponse m_aHttpResponse;

  public AS2OutputStreamCreatorHttpServletResponse (@Nonnull final HttpServletResponse aHttpResponse)
  {
    m_aHttpResponse = ValueEnforcer.notNull (aHttpResponse, "HttpResponse");
  }

  public void sendHttpResponse (@Nonnegative final int nHttpResponseCode,
                                @Nonnull final InternetHeaders aHeaders,
                                @Nonnull final NonBlockingByteArrayOutputStream aData) throws IOException
  {
    // Set status code
    m_aHttpResponse.setStatus (nHttpResponseCode);

    // Add headers
    final Enumeration <?> aHeaderEnum = aHeaders.getAllHeaders ();
    while (aHeaderEnum.hasMoreElements ())
    {
      final Header aHeader = (Header) aHeaderEnum.nextElement ();
      // HTTPResponse cannot deal with newlines in header values and this
      // happens e.g. for Content-Type!
      m_aHttpResponse.addHeader (aHeader.getName (),
                                 new String (StringHelper.replaceMultiple (aHeader.getValue (),
                                                                           new char [] { '\r', '\n', '\t' },
                                                                           ' ')));
    }

    // Write response body
    final OutputStream aOS = StreamUtils.getBuffered (m_aHttpResponse.getOutputStream ());
    aData.writeTo (aOS);

    // Don't close the OutputStream - just flush it.
    aOS.flush ();
  }
}
