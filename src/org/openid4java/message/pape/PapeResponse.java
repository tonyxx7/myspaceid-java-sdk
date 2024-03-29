/*
 * Copyright 2006-2008 Sxip Identity Corporation
 */

package org.openid4java.message.pape;

import org.openid4java.message.ParameterList;
import org.openid4java.message.MessageException;
import org.openid4java.message.Parameter;
import org.openid4java.util.InternetDateFormat;
import org.openid4java.OpenIDException;

import java.util.*;
import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements the extension for OpenID Provider Authentication Policy responses.
 *
 * @author Marius Scurtescu, Johnny Bufu
 */
public class PapeResponse extends PapeMessage
{
    private static Log _log = LogFactory.getLog(PapeResponse.class);
    private static final boolean DEBUG = _log.isDebugEnabled();

    protected final static List PAPE_FIELDS = Arrays.asList( new String[] {
            "auth_policies", "auth_time", "nist_auth_level"
    });

    private static InternetDateFormat _dateFormat = new InternetDateFormat();

    /**
     * Constructs a Pape Response with an empty parameter list.
     */
    protected PapeResponse()
    {
        set("auth_policies", "none");

        if (DEBUG) _log.debug("Created empty PAPE response.");
    }

    /**
     * Constructs a Pape Response with an empty parameter list.
     */
    public static PapeResponse createPapeResponse()
    {
        return new PapeResponse();
    }

    /**
     * Constructs a Pape Response from a parameter list.
     * <p>
     * The parameter list can be extracted from a received message with the
     * getExtensionParams method of the Message class, and MUST NOT contain
     * the "openid.<extension_alias>." prefix.
     */
    protected PapeResponse(ParameterList params)
    {
        _parameters = params;
    }

    public static PapeResponse createPapeResponse(ParameterList params)
            throws MessageException
    {
        PapeResponse resp = new PapeResponse(params);

        resp.validate();

        if (DEBUG)
            _log.debug("Created PAPE response from parameter list:\n" + params);

        return resp;
    }

    /**
     * Gets the auth_policies parameter value.
     */
    public String getAuthPolicies()
    {
        return getParameterValue("auth_policies");
    }

    /**
     * Sets a new value for the auth_policies parameter.
     *
     * The previous value of the parameter will be owerwritten.
     *
     * @param policyUris    Space separated list of authentication policy
     *                      URIs to be set.
     * @see #addAuthPolicy(String)
     */
    public void setAuthPolicies(String policyUris)
    {
        // todo: enforce that policyUri is a valid URI?

        set("auth_policies", policyUris);
    }

    /**
     * Adds an authentication policy URI to the auth_policies
     * parameter.
     *
     * @param policyUri     The authentication policy URI to be set.
     * @see #setAuthPolicies(String)
     */
    public void addAuthPolicy(String policyUri)
    {
        // todo: check that policyUri isn't already in the list?

        String policies = getAuthPolicies();

        if (policies == null || "none".equals(policies)) // should never be null
            setAuthPolicies(policyUri);
        else
            setAuthPolicies(policies + " " + policyUri);
    }

    /**
     * Gets a list with the auth_policies. An empty list is
     * returned if no authentication policies exist.
     */
    public List getAuthPoliciesList()
    {
        String policies = getParameterValue("auth_policies");

        if (policies == null || "none".equals(policies)) // should never be null
            return new ArrayList();
        else
            return Arrays.asList(policies.split(" "));
    }

    /**
     * Sets the auth_time parameter.
     *
     * @param timestamp The most recent timestamp when the End User has
     *                  actively authenticated to the OP in a manner
     *                  fitting the asserted policies.
     */
    public void setAuthTime(Date timestamp)
    {
        set("auth_time", _dateFormat.format(timestamp));
    }

    /**
     * Gets the timestamp when the End User has most recentnly authenticated
     * to the OpenID Provider in a manner fitting the asserted policies.
     *
     * @return          The verbatim value of the auth_time parameter.
     *                  Null is returned if the parameter is not present
     *                  in the PapeResponse.
     *
     * @see #getAuthDate()
     */
    public String getAuthTime()
    {
        return getParameterValue("auth_time");
    }

    /**
     * Gets the timestamp when the End User has most recentnly authenticated
     * to the OpenID Provider in a manner fitting the asserted policies.
     *
     * @return          The value of the auth_time parameter parsed into
     *                  a java.util.Date. Null is returned if the parameter
     *                  is not present in the PapeResponse, or if the
     *                  parameter value is invalid.
     *
     * @see #getAuthTime()
     */
    public Date getAuthDate()
    {
        String authTime = getParameterValue("auth_time");

        if (authTime != null)
        {
            try
            {
                return _dateFormat.parse(authTime);
            }
            catch (ParseException e)
            {
                _log.warn("Invalid auth_time: " + authTime + 
                          "; returning null.");
            }
        }

        return null;
    }

    /**
     * Gets the value of the nist_auth_level parameter.
     * <p>
     * NIST levels are integers between 1 and 4 inclusive. Level 0 is
     * used to signify that the OP recognizes the parameter and the
     * user authentication did not meet the requirements of Level 1.
     *
     * @return          The NIST level, or -1 if the parameter is not set.
     */
    public int getNistAuthLevel()
    {
        String level = getParameterValue("nist_auth_level");

        if ( level != null && level.length() > 0 )
            return Integer.parseInt(level);
        else
            return -1;
    }

    public void setNistAuthLevel(int level) throws MessageException
    {
        if (level < 0 || level > 4)
            throw new MessageException("Invalid NIST level: " + level);

        set("nist_auth_level", Integer.toString(level));
    }

    /**
     * Checks the validity of the extension.
     * <p>
     * Used when constructing a extension from a parameter list.
     *
     * @throws MessageException if the PapeResponse is not valid.
     */
    private void validate() throws MessageException
    {
        if (! _parameters.hasParameter("auth_policies"))
        {
            throw new MessageException(
                "auth_policies is required in a PAPE response.",
                OpenIDException.PAPE_ERROR);
        }

        String authTime = getAuthTime();
        if (authTime != null)
        {
            try
            {
                _dateFormat.parse(authTime);
            }
            catch (ParseException e)
            {
                throw new MessageException(
                    "Invalid auth_time in PAPE response: " + authTime,
                    OpenIDException.PAPE_ERROR, e);
            }
        }

        Iterator it = _parameters.getParameters().iterator();
        while (it.hasNext())
        {
            String paramName = ((Parameter) it.next()).getKey();

            if (! PAPE_FIELDS.contains(paramName))
            {
                throw new MessageException(
                    "Invalid parameter name in PAPE response: " + paramName,
                    OpenIDException.PAPE_ERROR);
            }
        }
    }
}
