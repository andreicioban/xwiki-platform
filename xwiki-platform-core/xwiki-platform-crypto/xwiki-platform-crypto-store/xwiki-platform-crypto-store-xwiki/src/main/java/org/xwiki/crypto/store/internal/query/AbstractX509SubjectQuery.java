/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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
package org.xwiki.crypto.store.internal.query;

import java.util.List;

import org.xwiki.crypto.BinaryStringEncoder;
import org.xwiki.crypto.pkix.params.PrincipalIndentifier;
import org.xwiki.crypto.store.CertificateStoreException;
import org.xwiki.crypto.store.internal.X509CertificateStore;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;

/**
 * Abstract class to build queries on CertificateClass in a certificate store based on subject.
 *
 * @version $Id$
 * @since 6.0
 */
public abstract class AbstractX509SubjectQuery extends AbstractX509StoreQuery
{
    private static final String SUBJECT = "subject";

    private static final String WHERE_STATEMENT =
        " and obj." + X509CertificateStore.CERTIFICATECLASS_PROP_SUBJECT + "=:" + SUBJECT;

    /**
     * Create a query.
     *
     * @param store the reference of a document or a space where the certificate should be stored.
     * @param select the select statement specific to the query.
     * @param from the additional from statement specific to the query.
     * @param where the additional where statement specific to the query.
     * @param encoder a string encoder/decoder used to convert byte arrays to/from String.
     * @param queryManager the query manager used to build queries.
     * @throws CertificateStoreException on error creating required queries.
     */
    public AbstractX509SubjectQuery(EntityReference store, String select, String from, String where,
        BinaryStringEncoder encoder, QueryManager queryManager) throws CertificateStoreException
    {
        super(store, select, from, WHERE_STATEMENT + where, encoder, queryManager);
    }

    /**
     * Execute the query.
     *
     * @param subject the subject name
     * @return the query result.
     * @throws QueryException on query error.
     */
    protected <T> List<T> execute(PrincipalIndentifier subject) throws QueryException
    {
        return getQuery().bindValue(SUBJECT, subject.getName()).execute();
    }

}
