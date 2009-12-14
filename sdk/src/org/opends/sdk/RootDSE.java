/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE
 * or https://OpenDS.dev.java.net/OpenDS.LICENSE.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE.  If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2009 Sun Microsystems, Inc.
 */

package org.opends.sdk;



import java.util.Collection;
import java.util.Collections;

import org.opends.sdk.requests.Requests;
import org.opends.sdk.requests.SearchRequest;
import org.opends.sdk.responses.SearchResultEntry;
import org.opends.sdk.schema.CoreSchema;

import com.sun.opends.sdk.util.*;



/**
 * The root DSE is a DSA-specific Entry (DSE) and not part of any naming
 * context (or any subtree), and which is uniquely identified by the
 * empty DN.
 * <p>
 * A Directory Server uses the root DSE to provide information about
 * itself using the following set of attributes:
 * <ul>
 * <li>{@code altServer}: alternative Directory Servers
 * <li>{@code namingContexts}: naming contexts
 * <li>{@code supportedControl}: recognized LDAP controls
 * <li>{@code supportedExtension}: recognized LDAP extended operations
 * <li>{@code supportedFeatures}: recognized LDAP features
 * <li>{@code supportedLDAPVersion}: LDAP versions supported
 * <li>{@code supportedSASLMechanisms}: recognized SASL authentication
 * mechanisms
 * <li>{@code supportedAuthPasswordSchemes}: recognized authentication
 * password schemes
 * <li>{@code subschemaSubentry}: the name of the subschema subentry
 * holding the schema controlling the Root DSE
 * <li>{@code vendorName}: the name of the Directory Server implementer
 * <li>{@code vendorVersion}: the version of the Directory Server
 * implementation.
 * </ul>
 * The values provided for these attributes may depend on session-
 * specific and other factors. For example, a server supporting the SASL
 * EXTERNAL mechanism might only list "EXTERNAL" when the client's
 * identity has been established by a lower level.
 * <p>
 * The root DSE may also include a {@code subschemaSubentry} attribute.
 * If it does, the attribute refers to the subschema (sub)entry holding
 * the schema controlling the root DSE. Clients SHOULD NOT assume that
 * this subschema (sub)entry controls other entries held by the server.
 *
 * @see <a href="http://tools.ietf.org/html/rfc4512">RFC 4512 -
 *      Lightweight Directory Access Protocol (LDAP): Directory
 *      Information Models </a>
 * @see <a href="http://tools.ietf.org/html/rfc3045">RFC 3045 - Storing
 *      Vendor Information in the LDAP Root DSE </a>
 * @see <a href="http://tools.ietf.org/html/rfc3112">RFC 3112 - LDAP
 *      Authentication Password Schema </a>
 */
public final class RootDSE
{
  private static final AttributeDescription ATTR_ALT_SERVER = AttributeDescription
      .create(CoreSchema.getAltServerAttributeType());

  private static final AttributeDescription ATTR_NAMING_CONTEXTS = AttributeDescription
      .create(CoreSchema.getNamingContextsAttributeType());

  private static final AttributeDescription ATTR_SUPPORTED_CONTROL = AttributeDescription
      .create(CoreSchema.getSupportedControlAttributeType());

  private static final AttributeDescription ATTR_SUPPORTED_EXTENSION = AttributeDescription
      .create(CoreSchema.getSupportedExtensionAttributeType());

  private static final AttributeDescription ATTR_SUPPORTED_FEATURE = AttributeDescription
      .create(CoreSchema.getSupportedFeaturesAttributeType());

  private static final AttributeDescription ATTR_SUPPORTED_LDAP_VERSION = AttributeDescription
      .create(CoreSchema.getSupportedLDAPVersionAttributeType());

  private static final AttributeDescription ATTR_SUPPORTED_SASL_MECHANISMS = AttributeDescription
      .create(CoreSchema.getSupportedSASLMechanismsAttributeType());

  private static final AttributeDescription ATTR_SUPPORTED_AUTH_PASSWORD_SCHEMES = AttributeDescription
      .create(CoreSchema.getSupportedAuthPasswordSchemesAttributeType());

  private static final AttributeDescription ATTR_SUBSCHEMA_SUBENTRY = AttributeDescription
      .create(CoreSchema.getSubschemaSubentryAttributeType());

  private static final AttributeDescription ATTR_VENDOR_NAME = AttributeDescription
      .create(CoreSchema.getVendorNameAttributeType());

  private static final AttributeDescription ATTR_VENDOR_VERSION = AttributeDescription
      .create(CoreSchema.getVendorNameAttributeType());

  private static final SearchRequest SEARCH_REQUEST = Requests
      .newSearchRequest(DN.rootDN(), SearchScope.BASE_OBJECT, Filter
          .getObjectClassPresentFilter(), ATTR_ALT_SERVER.toString(),
          ATTR_NAMING_CONTEXTS.toString(), ATTR_SUPPORTED_CONTROL
              .toString(), ATTR_SUPPORTED_EXTENSION.toString(),
          ATTR_SUPPORTED_FEATURE.toString(),
          ATTR_SUPPORTED_LDAP_VERSION.toString(),
          ATTR_SUPPORTED_SASL_MECHANISMS.toString(), ATTR_VENDOR_NAME
              .toString(), ATTR_VENDOR_VERSION.toString(),
          ATTR_SUPPORTED_AUTH_PASSWORD_SCHEMES.toString(),
          ATTR_SUBSCHEMA_SUBENTRY.toString(), "*");



  /**
   * Reads the Root DSE from the Directory Server using the provided
   * connection.
   * <p>
   * If the Root DSE is not returned by the Directory Server then the
   * request will fail with an {@link EntryNotFoundException}. More
   * specifically, the returned future will never return {@code null}.
   *
   * @param connection
   *          A connection to the Directory Server whose Root DSE is to
   *          be read.
   * @param handler
   *          A result handler which can be used to asynchronously
   *          process the operation result when it is received, may be
   *          {@code null}.
   * @return A future representing the result of the operation.
   * @throws UnsupportedOperationException
   *           If the connection does not support search operations.
   * @throws IllegalStateException
   *           If the connection has already been closed, i.e. if
   *           {@code isClosed() == true}.
   * @throws NullPointerException
   *           If the {@code connection} was {@code null}.
   */
  public static ResultFuture<RootDSE> readRootDSE(
      AsynchronousConnection connection,
      ResultHandler<RootDSE> handler)
      throws UnsupportedOperationException, IllegalStateException,
      NullPointerException
  {
    final ResultTransformer<SearchResultEntry, RootDSE> future =
      new ResultTransformer<SearchResultEntry, RootDSE>(handler)
    {

      protected RootDSE transformResult(SearchResultEntry result)
          throws ErrorResultException
      {
        return new RootDSE(result);
      }

    };

    ResultFuture<SearchResultEntry> innerFuture = connection
        .searchSingleEntry(SEARCH_REQUEST, future);
    future.setResultFuture(innerFuture);
    return future;
  }



  /**
   * Reads the Root DSE from the Directory Server using the provided
   * connection.
   * <p>
   * If the Root DSE is not returned by the Directory Server then the
   * request will fail with an {@link EntryNotFoundException}. More
   * specifically, this method will never return {@code null}.
   *
   * @param connection
   *          A connection to the Directory Server whose Root DSE is to
   *          be read.
   * @return The Directory Server's Root DSE.
   * @throws ErrorResultException
   *           If the result code indicates that the request failed for
   *           some reason.
   * @throws InterruptedException
   *           If the current thread was interrupted while waiting.
   * @throws UnsupportedOperationException
   *           If the connection does not support search operations.
   * @throws IllegalStateException
   *           If the connection has already been closed, i.e. if
   *           {@code isClosed() == true}.
   * @throws NullPointerException
   *           If the {@code connection} was {@code null}.
   */
  public static RootDSE readRootDSE(Connection connection)
      throws ErrorResultException, InterruptedException,
      UnsupportedOperationException, IllegalStateException,
      NullPointerException
  {
    final Entry entry = connection.searchSingleEntry(SEARCH_REQUEST);
    return new RootDSE(entry);
  }



  private final Entry entry;



  /**
   * Creates a new Root DSE instance backed by the provided entry.
   * Modifications made to {@code entry} will be reflected in the
   * returned Root DSE. The returned Root DSE instance is unmodifiable
   * and attempts to use modify any of the returned collections will
   * result in a {@code UnsupportedOperationException}.
   *
   * @param entry
   *          The Root DSE entry.
   * @throws NullPointerException
   *           If {@code entry} was {@code null} .
   */
  public RootDSE(Entry entry) throws NullPointerException
  {
    Validator.ensureNotNull(entry);
    this.entry = entry;
  }



  /**
   * Returns an unmodifiable list of URIs referring to alternative
   * Directory Servers that may be contacted when the Directory Server
   * becomes unavailable.
   * <p>
   * URIs for Directory Servers implementing the LDAP protocol are
   * written according to RFC 4516. Other kinds of URIs may be provided.
   * <p>
   * If the Directory Server does not know of any other Directory
   * Servers that could be used, the returned list will be empty.
   *
   * @return An unmodifiable list of URIs referring to alternative
   *         Directory Servers, which may be empty.
   * @see <a href="http://tools.ietf.org/html/rfc4516">RFC 4516 -
   *      Lightweight Directory Access Protocol (LDAP): Uniform Resource
   *      Locator </a>
   */
  public Collection<String> getAlternativeServers()
  {
    return getMultiValuedAttribute(ATTR_ALT_SERVER, Functions
        .valueToString());
  }



  /**
   * Returns an unmodifiable list of DNs identifying the context
   * prefixes of the naming contexts that the Directory Server masters
   * or shadows (in part or in whole).
   * <p>
   * If the Directory Server does not master or shadow any naming
   * contexts, the returned list will be empty.
   *
   * @return An unmodifiable list of DNs identifying the context
   *         prefixes of the naming contexts, which may be empty.
   */
  public Collection<String> getNamingContexts()
  {
    return getMultiValuedAttribute(
        ATTR_SUPPORTED_AUTH_PASSWORD_SCHEMES, Functions.valueToString());
  }



  /**
   * Returns a string which represents the DN of the subschema subentry
   * holding the schema controlling the Root DSE.
   * <p>
   * Clients SHOULD NOT assume that this subschema (sub)entry controls
   * other entries held by the Directory Server.
   *
   * @return The DN of the subschema subentry holding the schema
   *         controlling the Root DSE, or {@code null} if the DN is not
   *         provided.
   */
  public String getSubschemaSubentry()
  {
    return getSingleValuedAttribute(ATTR_SUBSCHEMA_SUBENTRY);
  }



  /**
   * Returns an unmodifiable list of supported authentication password
   * schemes which the Directory Server supports.
   * <p>
   * If the Directory Server does not support any authentication
   * password schemes, the returned list will be empty.
   *
   * @return An unmodifiable list of supported authentication password
   *         schemes, which may be empty.
   * @see <a href="http://tools.ietf.org/html/rfc3112">RFC 3112 - LDAP
   *      Authentication Password Schema </a>
   */
  public Collection<String> getSupportedAuthenticationPasswordSchemes()
  {
    return getMultiValuedAttribute(
        ATTR_SUPPORTED_AUTH_PASSWORD_SCHEMES, Functions.valueToString());
  }



  /**
   * Returns an unmodifiable list of object identifiers identifying the
   * request controls that the Directory Server supports.
   * <p>
   * If the Directory Server does not support any request controls, the
   * returned list will be empty. Object identifiers identifying
   * response controls may not be listed.
   *
   * @return An unmodifiable list of object identifiers identifying the
   *         request controls, which may be empty.
   */
  public Collection<String> getSupportedControls()
  {
    return getMultiValuedAttribute(ATTR_SUPPORTED_CONTROL, Functions
        .valueToString());
  }



  /**
   * Returns an unmodifiable list of object identifiers identifying the
   * extended operations that the Directory Server supports.
   * <p>
   * If the Directory Server does not support any extended operations,
   * the returned list will be empty.
   * <p>
   * An extended operation generally consists of an extended request and
   * an extended response but may also include other protocol data units
   * (such as intermediate responses). The object identifier assigned to
   * the extended request is used to identify the extended operation.
   * Other object identifiers used in the extended operation may not be
   * listed as values of this attribute.
   *
   * @return An unmodifiable list of object identifiers identifying the
   *         extended operations, which may be empty.
   */
  public Collection<String> getSupportedExtendedOperations()
  {
    return getMultiValuedAttribute(ATTR_SUPPORTED_EXTENSION, Functions
        .valueToString());
  }



  /**
   * Returns an unmodifiable list of object identifiers identifying
   * elective features that the Directory Server supports.
   * <p>
   * If the server does not support any discoverable elective features,
   * the returned list will be empty.
   *
   * @return An unmodifiable list of object identifiers identifying the
   *         elective features, which may be empty.
   */
  public Collection<String> getSupportedFeatures()
  {
    return getMultiValuedAttribute(ATTR_SUPPORTED_FEATURE, Functions
        .valueToString());
  }



  /**
   * Returns an unmodifiable list of the versions of LDAP that the
   * Directory Server supports.
   *
   * @return An unmodifiable list of the versions.
   */
  public Collection<Integer> getSupportedLDAPVersions()
  {
    return getMultiValuedAttribute(ATTR_SUPPORTED_LDAP_VERSION,
        Functions.valueToInteger());
  }



  /**
   * Returns an unmodifiable list of the SASL mechanisms that the
   * Directory Server recognizes and/or supports.
   * <p>
   * The contents of the returned list may depend on the current session
   * state and may be empty if the Directory Server does not support any
   * SASL mechanisms.
   *
   * @return An unmodifiable list of the SASL mechanisms, which may be
   *         empty.
   * @see <a href="http://tools.ietf.org/html/rfc4513">RFC 4513 -
   *      Lightweight Directory Access Protocol (LDAP): Authentication
   *      Methods and Security Mechanisms </a>
   * @see <a href="http://tools.ietf.org/html/rfc4422">RFC 4422 - Simple
   *      Authentication and Security Layer (SASL) </a>
   */
  public Collection<String> getSupportedSASLMechanisms()
  {
    return getMultiValuedAttribute(ATTR_SUPPORTED_SASL_MECHANISMS,
        Functions.valueToString());
  }



  /**
   * Returns a string which represents the name of the Directory Server
   * implementer.
   *
   * @return The name of the Directory Server implementer, or {@code
   *         null} if the vendor name is not provided.
   * @see <a href="http://tools.ietf.org/html/rfc3045">RFC 3045 -
   *      Storing Vendor Information in the LDAP Root DSE </a>
   */
  public String getVendorName()
  {
    return getSingleValuedAttribute(ATTR_VENDOR_NAME);
  }



  /**
   * Returns a string which represents the version of the Directory
   * Server implementation.
   * <p>
   * Note that this value is typically a release value comprised of a
   * string and/or a string of numbers used by the developer of the LDAP
   * server product. The returned string will be unique between two
   * versions of the Directory Server, but there are no other syntactic
   * restrictions on the value or the way it is formatted.
   *
   * @return The version of the Directory Server implementation, or
   *         {@code null} if the vendor version is not provided.
   * @see <a href="http://tools.ietf.org/html/rfc3045">RFC 3045 -
   *      Storing Vendor Information in the LDAP Root DSE </a>
   */
  public String getVendorVersion()
  {
    return getSingleValuedAttribute(ATTR_VENDOR_VERSION);
  }



  private <N> Collection<N> getMultiValuedAttribute(
      AttributeDescription attributeDescription,
      Function<ByteString, N, Void> function)
  {
    // The returned collection is unmodifiable because we may need to
    // return an empty collection if the attribute does not exist in the
    // underlying entry. If a value is then added to the returned empty
    // collection it would require that an attribute is created in the
    // underlying entry in order to maintain consistency.
    final Attribute attr = entry.getAttribute(attributeDescription);
    if (attr != null)
    {
      return Collections.unmodifiableCollection(Collections2.transform(
          attr, function, Functions.objectToByteString()));
    }
    else
    {
      return Collections.emptySet();
    }
  }



  private String getSingleValuedAttribute(
      AttributeDescription attributeDescription)
  {
    final Attribute attr = entry.getAttribute(attributeDescription);
    if (attr == null || attr.isEmpty())
    {
      return null;
    }
    else
    {
      return attr.firstValueAsString();
    }
  }

}