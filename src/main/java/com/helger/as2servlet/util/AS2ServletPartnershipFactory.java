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

import javax.annotation.Nonnull;

import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2lib.partner.CPartnershipIDs;
import com.helger.as2lib.partner.Partnership;
import com.helger.as2lib.partner.SelfFillingXMLPartnershipFactory;

/**
 * A specialized {@link SelfFillingXMLPartnershipFactory} that automatically
 * stores partnerships to a file.
 *
 * @author Philip Helger
 */
public final class AS2ServletPartnershipFactory extends SelfFillingXMLPartnershipFactory
{
  @Override
  protected void onBeforeAddPartnership (@Nonnull final Partnership aPartnership) throws OpenAS2Exception
  {
    super.onBeforeAddPartnership (aPartnership);

    // Ensure a nice name
    if (Partnership.DEFAULT_NAME.equals (aPartnership.getName ()))
      aPartnership.setName (aPartnership.getSenderID (CPartnershipIDs.PID_AS2) +
                            "-" +
                            aPartnership.getReceiverID (CPartnershipIDs.PID_AS2));
  }

  @Override
  protected void markAsChanged () throws OpenAS2Exception
  {
    // Store everytime something changed
    storePartnership ();
  }
}
