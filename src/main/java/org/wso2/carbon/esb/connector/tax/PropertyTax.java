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
import java.math.RoundingMode;

/**
 * PropertyTax connector calculates property tax by adding 5% to the income tax amount.
 */
public class PropertyTax extends AbstractConnector {

    private static final Log log = LogFactory.getLog(PropertyTax.class);

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
            
            // Calculate property tax (income tax + 5%)
            BigDecimal propertyTaxRate = new BigDecimal("0.05");
            BigDecimal propertyTax = incomeTax.add(incomeTax.multiply(propertyTaxRate))
                                              .setScale(2, RoundingMode.HALF_UP);

            System.out.println(propertyTax);
            // Set the result in the specified variable
            messageContext.setVariable(variableName, propertyTax.toString());

            if (log.isDebugEnabled()) {
                log.debug("Property tax calculated: " + propertyTax + " for income tax: " + incomeTax);
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format in incomeTax: " + e.getMessage());
            throw new ConnectException("Invalid number format in incomeTax: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error calculating property tax: " + e.getMessage());
            throw new ConnectException("Error calculating property tax: " + e.getMessage());
        }
    }
}