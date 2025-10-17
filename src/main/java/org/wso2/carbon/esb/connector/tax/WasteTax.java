/*
 * Copyright (c) 2025, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.wso2.carbon.esb.connector.tax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import java.math.BigDecimal;

/**
 * WasteTax connector calculates waste tax based on income tax amount.
 * If income tax > 4000, waste tax is free (0). Otherwise, it's a fixed tax of 100.
 */
public class WasteTax extends AbstractConnector {

    private static final Log log = LogFactory.getLog(WasteTax.class);
    private static final BigDecimal INCOME_TAX_THRESHOLD = new BigDecimal("4000");
    private static final BigDecimal FIXED_WASTE_TAX = new BigDecimal("100");

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        try {
            // Get the incomeTax parameter directly as string
            String incomeTaxValue = (String) getParameter(messageContext, "incomeTax");
            String variableName = (String) getParameter(messageContext, "targetVariable");

            if (incomeTaxValue == null || incomeTaxValue.isEmpty()) {
                throw new ConnectException("incomeTax parameter is required");
            }

            if (variableName == null || variableName.isEmpty()) {
                throw new ConnectException("targetVariable parameter is required");
            }

            // Convert to BigDecimal for precise calculations
            BigDecimal incomeTax = new BigDecimal(incomeTaxValue);
            
            // Calculate waste tax based on income tax threshold
            BigDecimal wasteTax;
            if (incomeTax.compareTo(INCOME_TAX_THRESHOLD) > 0) {
                // Income tax > 4000, waste tax is free
                wasteTax = BigDecimal.ZERO;
            } else {
                // Income tax <= 4000, fixed waste tax of 100
                wasteTax = FIXED_WASTE_TAX;
            }

            // Set the result in the specified variable
            messageContext.setProperty(variableName, wasteTax.toString());

            if (log.isDebugEnabled()) {
                log.debug("Waste tax calculated: " + wasteTax + " for income tax: " + incomeTax);
            }

        } catch (NumberFormatException e) {
            throw new ConnectException("Invalid number format in incomeTax: " + e.getMessage());
        } catch (Exception e) {
            throw new ConnectException("Error calculating waste tax: " + e.getMessage());
        }
    }
}