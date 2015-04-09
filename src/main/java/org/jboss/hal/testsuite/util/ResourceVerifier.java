/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.hal.testsuite.util;

import java.io.IOException;
import java.util.Map;

import org.jboss.hal.testsuite.cli.CliClient;
import org.junit.Assert;

/**
 * Created by pjelinek on Apr 7, 2015
 */
public class ResourceVerifier {

    private String dmrPath;
    private CliClient cliClient;

    public ResourceVerifier(String dmrPath, CliClient cliClient){
        if (cliClient == null) {
            throw new IllegalArgumentException("Management client not set.");
        }
        this.cliClient = cliClient;
        setDmrPath(dmrPath);
    }

    /**
     * Verifies that resource on given path exists in model
     *
     * @param dmrPath dmr address of the resource
     * @param expected <code>true</code> if resource is expected to exists, false otherwise
     */
    public void verifyResource(String dmrPath, boolean expected) {
        boolean exists = cliClient.executeForSuccess(dmrPath + ":read-resource()");
        if (expected) {
            Assert.assertTrue("Resource " + dmrPath + " should exist", exists);
        } else {
            Assert.assertFalse("Resource " + dmrPath + " should not exist", exists);
        }
    }

    /**
     * Verifies that resource on set path path exists in model
     *
     * @param expected <code>true</code> if resource is expected to exists, false otherwise
     */
    public void verifyResource(boolean expected) {
        if (dmrPath == null) {
            throw new IllegalStateException("DMR path not set");
        }
        verifyResource(dmrPath, expected);
    }

    /**
     * Verifies the value of attribute in model.
     *
     * @param name name of the attribute. If the name is camelCase it will be converted to camel-case.
     * @param expectedValue expected value
     */
    public void verifyAttribute(String name, String expectedValue) throws IOException {
        if (dmrPath == null) {
            throw new IllegalStateException("DMR path not set");
        }

        String dmrName = camelToDash(name);
        String actualValue = cliClient.readAttribute(dmrPath, dmrName);

        Assert.assertEquals("Attribute value is different in model.", expectedValue, actualValue);
    }

    /**
     * Verify the value of attributes against model
     *
     * @param pairs Key-Value map of attribute names and values. If name is camelCase it will be coverted to camel-case
     */
    public void verifyAttributes(Map<String, String> pairs) throws IOException {
        for (Map.Entry<String, String> p : pairs.entrySet()) {
            verifyAttribute(p.getKey(), p.getValue());
        }
    }

    public void setDmrPath(String dmrPath) {
        this.dmrPath = dmrPath;
    }

    /**
     * Converts a camelCaseString to camel-case-string
     *
     * @param input
     * @return
     */
    private static String camelToDash(String input) {
        return input.replaceAll("\\B([A-Z])", "-$1" ).toLowerCase();
    }

}