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
 * SewerageFee connector calculates sewerage fee which is a fixed value of 50 for everyone.
 */
public class SewerageFee extends AbstractConnector {

    private static final Log log = LogFactory.getLog(SewerageFee.class);
    private static final BigDecimal FIXED_SEWERAGE_FEE = new BigDecimal("50");

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {
        try {
            // Get the incomeTax parameter directly as string (for consistency, even though not used for calculation)
            String incomeTaxValue = (String) getParameter(messageContext, "incomeTax");
            String variableName = (String) getParameter(messageContext, "targetVariable");

            if (incomeTaxValue == null || incomeTaxValue.isEmpty()) {
                throw new ConnectException("incomeTax parameter is required");
            }

            if (variableName == null || variableName.isEmpty()) {
                throw new ConnectException("targetVariable parameter is required");
            }

            // Sewerage fee is fixed for everyone
            BigDecimal sewerageFee = FIXED_SEWERAGE_FEE;

            // Set the result in the specified variable
            messageContext.setProperty(variableName, sewerageFee.toString());

            if (log.isDebugEnabled()) {
                log.debug("Sewerage fee set: " + sewerageFee);
            }

        } catch (Exception e) {
            throw new ConnectException("Error setting sewerage fee: " + e.getMessage());
        }
    }
}